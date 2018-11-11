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
    private ObjectGraph objectGraph;
    public static Location mLocation;


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
        // if (BuildConfig.DEBUG)
        Timber.plant(new Timber.DebugTree());
        AppContextor.getInstance().initContext(getApplicationContext());
        this.initializeInjector();
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

}