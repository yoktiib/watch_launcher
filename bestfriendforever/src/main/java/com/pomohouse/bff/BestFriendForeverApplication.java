package com.pomohouse.bff;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.library.languages.LanguageSetting;
import com.pomohouse.library.manager.AppContextor;

import io.fabric.sdk.android.Fabric;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import dagger.ObjectGraph;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Admin on 8/26/16 AD.
 */
public class BestFriendForeverApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        AppContextor.getInstance().initContext(this);
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
        this.initializeLanguage();
        this.initializeInjector();
    }

    private List<Object> getModules() {
        return Collections.singletonList(new ApplicationModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    private void initializeLanguage() {
        LanguageSetting.setLanguage(this, new Locale("en", "US"));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/quicksand_bold.otf").setFontAttrId(R.attr.fontPath).build());
    }

    private void initializeInjector() {
        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);
    }
}
