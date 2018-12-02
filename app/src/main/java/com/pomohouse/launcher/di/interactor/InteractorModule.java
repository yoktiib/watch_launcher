package com.pomohouse.launcher.di.interactor;

/**
 * Created by Admin on 8/18/16 AD.
 */

import com.pomohouse.launcher.activity.fitness.interactor.FitnessInteractorImpl;
import com.pomohouse.launcher.activity.fitness.interactor.IFitnessInteractor;
import com.pomohouse.launcher.main.interactor.ILauncherInteractor;
import com.pomohouse.launcher.main.interactor.LauncherInteractorImpl;
import com.pomohouse.launcher.fragment.about.interactor.IPinCodeInteractor;
import com.pomohouse.launcher.fragment.about.interactor.PinCodeInteractorImpl;
import com.pomohouse.launcher.fragment.contacts.interactor.ContactInteractorImpl;
import com.pomohouse.launcher.fragment.contacts.interactor.IContactInteractor;
import com.pomohouse.launcher.fragment.friends.interactor.FriendInteractorImpl;
import com.pomohouse.launcher.fragment.friends.interactor.IFriendInteractor;
import com.pomohouse.launcher.fragment.main.interactor.IMainFragmentInteractor;
import com.pomohouse.launcher.fragment.main.interactor.MainFragmentInteractorImpl;
import com.pomohouse.launcher.fragment.mini_setting.interactor.IMiniSettingInteractor;
import com.pomohouse.launcher.fragment.mini_setting.interactor.MiniSettingInteractorImpl;
import com.pomohouse.launcher.fragment.settings.interactor.ISettingInteractor;
import com.pomohouse.launcher.fragment.settings.interactor.SettingInteractorImpl;
import com.pomohouse.launcher.fragment.theme.interactor.IThemeFragmentInteractor;
import com.pomohouse.launcher.fragment.theme.interactor.ThemeFragmentInteractorImpl;
import com.pomohouse.launcher.broadcast.sensor.interactor.ISensorInteractor;
import com.pomohouse.launcher.broadcast.sensor.interactor.SensorInteractorImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
public class InteractorModule {

    @Provides
    @Singleton
    ILauncherInteractor provideMainInteractor() {
        return new LauncherInteractorImpl();
    }

    @Provides
    @Singleton
    IFriendInteractor provideFriendInteractor() {
        return new FriendInteractorImpl();
    }

    @Provides
    @Singleton
    IContactInteractor provideContactInteractor() {
        return new ContactInteractorImpl();
    }

    @Provides
    @Singleton
    IMiniSettingInteractor provideMiniSettingInteractor() {
        return new MiniSettingInteractorImpl();
    }

    @Provides
    @Singleton
    ISettingInteractor provideSettingInteractor() {
        return new SettingInteractorImpl();
    }

    @Provides
    @Singleton
    ISensorInteractor provideSensorInteractor() {
        return new SensorInteractorImpl();
    }

    @Provides
    @Singleton
    IFitnessInteractor provideFitnessInteractor() {
        return new FitnessInteractorImpl();
    }

    @Provides
    @Singleton
    IMainFragmentInteractor provideMainFragmentInteractor() {
        return new MainFragmentInteractorImpl();
    }
    @Provides
    @Singleton
    IThemeFragmentInteractor provideThemeFragmentInteractor() {
        return new ThemeFragmentInteractorImpl();
    }

    @Provides
    @Singleton
    IPinCodeInteractor providePinCodeInteractor() {
        return new PinCodeInteractorImpl();
    }
}