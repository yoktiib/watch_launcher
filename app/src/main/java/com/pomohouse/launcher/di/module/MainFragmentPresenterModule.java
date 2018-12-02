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
import com.pomohouse.launcher.fragment.main.IMainFragmentView;
import com.pomohouse.launcher.fragment.main.MainFragment;
import com.pomohouse.launcher.fragment.main.interactor.IMainFragmentInteractor;
import com.pomohouse.launcher.fragment.main.presenter.IMainFragmentPresenter;
import com.pomohouse.launcher.fragment.main.presenter.MainFragmentPresenterImpl;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.manager.theme.ThemePrefManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = MainFragment.class,/*
        includes = {LocationPresenterModule.class},*/
        addsTo = ApplicationModule.class
)
public class MainFragmentPresenterModule {

    private IMainFragmentView view;

    public MainFragmentPresenterModule(IMainFragmentView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IThemePrefManager provideThemeManager(Application app) {
        return new ThemePrefManager(app.getApplicationContext());
    }

    @Singleton
    @Provides
    public ISettingManager provideMiniSettingManager(Application app) {
        return new SettingPrefManager(app.getApplicationContext());
    }

    @Singleton
    @Provides
    public IMainFragmentView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IMainFragmentPresenter providePresenter(IMainFragmentView mainView, IMainFragmentInteractor iMainInteractor) {
        return new MainFragmentPresenterImpl(mainView, iMainInteractor);
    }

}