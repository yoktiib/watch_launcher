/*
 *
 *  *
 *  *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *  *
 *  *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  * you may not use this file except in compliance with the License.
 *  *  * You may obtain a copy of the License at
 *  *  *
 *  *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *
 *
 */

package com.pomohouse.launcher.di.module;

import android.app.Application;

import com.pomohouse.launcher.main.ILauncherView;
import com.pomohouse.launcher.main.LauncherActivity;
import com.pomohouse.launcher.main.presenter.ILauncherPresenter;
import com.pomohouse.launcher.main.presenter.LauncherPresenterImpl;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.contacts.presenter.ContactPresenterImpl;
import com.pomohouse.launcher.fragment.contacts.presenter.IContactPresenter;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.in_class_mode.IInClassModePrefManager;
import com.pomohouse.launcher.manager.in_class_mode.InClassModePrefManager;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.sleep_time.ISleepTimeManager;
import com.pomohouse.launcher.manager.sleep_time.SleepTimePrefManager;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {LauncherActivity.class},
        addsTo = ApplicationModule.class
)
public class LauncherPresenterModule {

    private ILauncherView view;

    public LauncherPresenterModule(ILauncherView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public ILauncherView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public ILauncherPresenter providePresenter(ILauncherView mainView) {
        return new LauncherPresenterImpl(mainView);
    }

    @Singleton
    @Provides
    IContactPresenter provideContactPresenter() {
        return new ContactPresenterImpl();
    }

    @Singleton
    @Provides
    public IThemePrefManager provideThemePrefManager(Application app) {
        return new ThemePrefManager(app);
    }

    @Singleton
    @Provides
    public IEventPrefManager provideEventPrefManager(Application app) {
        return new EventPrefManagerImpl(app);
    }

    @Singleton
    @Provides
    public IInClassModePrefManager provideInClassModeManager(Application app) {
        return new InClassModePrefManager(app);
    }

    @Singleton
    @Provides
    public IFitnessPrefManager provideFitnessManager(Application app) {
        return new FitnessPrefManagerImpl(app.getApplicationContext());
    }


    @Singleton
    @Provides
    public ISettingManager provideSettingPrefManager(Application app) {
        return new SettingPrefManager(app);
    }

    @Singleton
    @Provides
    public INotificationManager provideNotificationManager(Application app) {
        return new NotificationPrefManager(app);
    }

    @Singleton
    @Provides
    public ISleepTimeManager provideSleepTimeManager(Application app) {
        return new SleepTimePrefManager(app);
    }
}