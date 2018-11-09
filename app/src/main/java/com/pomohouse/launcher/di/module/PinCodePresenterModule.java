package com.pomohouse.launcher.di.module;

import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedPinCodeFragment;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.about.IPinCodeView;
import com.pomohouse.launcher.fragment.about.PinCodeFragment;
import com.pomohouse.launcher.fragment.about.presenter.IPinCodePresenter;
import com.pomohouse.launcher.fragment.about.presenter.PinCodePresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/18/16 AD.
 */
@Module(
        injects = {PinCodeFragment.class,GetStartedPinCodeFragment.class},
        addsTo = ApplicationModule.class
)
public class PinCodePresenterModule {
    IPinCodeView view;

    @Singleton
    @Provides
    IPinCodePresenter providePinCodePresenter(IPinCodeView view/*, IPinCodeInteractor interactor*/) {
        return new PinCodePresenterImpl(view);
    }

    public PinCodePresenterModule(IPinCodeView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IPinCodeView providePinCode() {
        return view;
    }
}
