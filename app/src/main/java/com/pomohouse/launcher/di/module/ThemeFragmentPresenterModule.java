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

import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.theme.IThemeFragmentView;
import com.pomohouse.launcher.fragment.theme.ThemeAnalogFragment;
import com.pomohouse.launcher.fragment.theme.ThemeDigitalFragment;
import com.pomohouse.launcher.fragment.theme.presenter.IThemeFragmentPresenter;
import com.pomohouse.launcher.fragment.theme.presenter.ThemeFragmentPresenterImpl;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {ThemeDigitalFragment.class, ThemeAnalogFragment.class},
        addsTo = ApplicationModule.class
)
public class ThemeFragmentPresenterModule {

    private IThemeFragmentView view;

    public ThemeFragmentPresenterModule(IThemeFragmentView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IThemeFragmentView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IThemeFragmentPresenter providePresenter(IThemeFragmentView view) {
        return new ThemeFragmentPresenterImpl(view);
    }

    @Singleton
    @Provides
    public INotificationManager provideNotificationManager(Application app) {
        return new NotificationPrefManager(app);
    }
}