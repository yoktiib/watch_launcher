package com.pomohouse.launcher.di.module;

import android.app.Application;

import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.mini_setting.IMiniSettingView;
import com.pomohouse.launcher.fragment.mini_setting.MiniSettingFragment;
import com.pomohouse.launcher.fragment.mini_setting.interactor.IMiniSettingInteractor;
import com.pomohouse.launcher.fragment.mini_setting.presenter.IMiniSettingPresenter;
import com.pomohouse.launcher.fragment.mini_setting.presenter.MiniSettingPresenterImpl;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 9/8/16 AD.
 */
@Module(
        injects = MiniSettingFragment.class,
        addsTo = ApplicationModule.class
)
public class MiniSettingPresenterModule {

    private IMiniSettingView view;


    public MiniSettingPresenterModule(IMiniSettingView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IMiniSettingView provideView() {
        return view;
    }

    @Singleton
    @Provides
    public ISettingManager provideMiniSettingManager(Application app) {
        return new SettingPrefManager(app.getApplicationContext());
    }

    @Singleton
    @Provides
    public IMiniSettingPresenter providePresenter(IMiniSettingView view, IMiniSettingInteractor interactor) {
        return new MiniSettingPresenterImpl(view, interactor);

    }
    @Singleton
    @Provides
    public INotificationManager provideNotificationManager(Application app) {
        return new NotificationPrefManager(app);
    }
}
