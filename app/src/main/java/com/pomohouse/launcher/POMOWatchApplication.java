package com.pomohouse.launcher;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import com.crashlytics.android.Crashlytics;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.manager.AppContextor;

import net.danlew.android.joda.JodaTimeAndroid;

import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.Disposable;
import moe.codeest.rxsocketclient.RxSocketClient;
import moe.codeest.rxsocketclient.SocketClient;
import moe.codeest.rxsocketclient.meta.SocketConfig;
import moe.codeest.rxsocketclient.meta.SocketOption;
import moe.codeest.rxsocketclient.meta.ThreadStrategy;
import timber.log.Timber;

/**
 * Created by Admin on 8/17/16 AD.
 */
public class POMOWatchApplication extends Application {
    public static TCPSocketServiceProvider mBoundService;
    private ObjectGraph objectGraph;
    public static Location mLocation;
    private static final String IP = "192.168.43.200";
    private static final int PORT = 4488;

    public static final boolean DEBUG = true;
    public static POMOWatchApplication application;

    public static String packageName;
    public static Resources resources;
    public static SocketClient mSocket;
    private static final byte[] HEART_BEAT = {1, 3, 4};
    private static final byte[] HEAD = {1, 2};
    private static final byte[] TAIL = {5, 7};


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
        // if (BuildConfig.DEBUG)
        Timber.plant(new Timber.DebugTree());
        AppContextor.getInstance().initContext(getApplicationContext());
        this.initializeInjector();
        createTCPSocket();
    }

    public SocketClient getSocket() {
        if (mSocket == null || !mSocket.isConnecting()) createTCPSocket();
        return mSocket;
    }

    public void createTCPSocket() {
        mSocket = RxSocketClient.create(new SocketConfig.Builder().setIp(IP).setPort(PORT).setCharset(Charset.forName("UTF-8")).setThreadStrategy(ThreadStrategy.ASYNC).setTimeout(30 * 1000).build()).option(new SocketOption.Builder()/*.setHeartBeat(HEART_BEAT, 60 * 1000).setHead(HEAD).setTail(TAIL)*/.build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private List<Object> getModules() {
        return Collections.singletonList(new ApplicationModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    private void initializeInjector() {
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }

    public void clearSocket() {
        if (mSocket != null && mSocket.isConnecting()) mSocket.disconnect();
        //createTCPSocket();
    }
}