package com.pomohouse.launcher;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import com.crashlytics.android.Crashlytics;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.library.manager.AppContextor;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Collections;
import java.util.List;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
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
        //  initializeService();
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

    /*private void initializeLanguage() {
        //     LanguageSetting.setLanguage(this, new Locale("th", LanguageSetting.getLocale(this).getCountry()));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/quicksand_bold.otf").setFontAttrId(R.attr.fontPath).build());
    }*/

    private void initializeInjector() {
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }
}