package com.pomohouse.launcher.di.module;

import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.settings.ISettingView;
import com.pomohouse.launcher.fragment.settings.SettingFragment;
import com.pomohouse.launcher.fragment.settings.interactor.ISettingInteractor;
import com.pomohouse.launcher.fragment.settings.presenter.ISettingPresenter;
import com.pomohouse.launcher.fragment.settings.presenter.SettingPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 9/8/16 AD.
 */
@Module(
        injects = SettingFragment.class,
        addsTo = ApplicationModule.class
)
public class SettingPresenterModule {

    private ISettingView view;

    public SettingPresenterModule(ISettingView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public ISettingView provideView() {
        return view;
    }

    @Singleton
    @Provides
    public ISettingPresenter providePresenter(ISettingView view, ISettingInteractor interactor) {
        return new SettingPresenterImpl(view, interactor);

    }
}
