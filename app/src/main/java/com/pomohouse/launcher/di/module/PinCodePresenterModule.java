package com.pomohouse.launcher.di.module;

import com.pomohouse.launcher.activity.getstarted.fragment.GetStartedPinCodeFragment;
import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.about.IPinCodeView;
import com.pomohouse.launcher.fragment.about.IQRCodeView;
import com.pomohouse.launcher.fragment.about.PinCodeFragment;
import com.pomohouse.launcher.fragment.about.QRCodeFragment;
import com.pomohouse.launcher.fragment.about.interactor.IPinCodeInteractor;
import com.pomohouse.launcher.fragment.about.presenter.IPinCodePresenter;
import com.pomohouse.launcher.fragment.about.presenter.IQRCodePresenter;
import com.pomohouse.launcher.fragment.about.presenter.PinCodePresenterImpl;
import com.pomohouse.launcher.fragment.about.presenter.QRCodePresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/18/16 AD.
 */
@Module(
        injects = {PinCodeFragment.class,GetStartedPinCodeFragment.class,QRCodeFragment.class},
        addsTo = ApplicationModule.class
)
public class PinCodePresenterModule {
    IPinCodeView view;
    IQRCodeView QRCodeView;

    @Singleton
    @Provides
    IPinCodePresenter providePinCodePresenter(IPinCodeView view, IPinCodeInteractor interactor) {
        return new PinCodePresenterImpl(view, interactor);
    }

    public PinCodePresenterModule(IPinCodeView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IPinCodeView providePinCode() {
        return view;
    }


    @Singleton
    @Provides
    IQRCodePresenter provideQRCodePresenter(IQRCodeView view, IPinCodeInteractor interactor) {
        return new QRCodePresenterImpl(view, interactor);
    }

    public PinCodePresenterModule(IQRCodeView view) {
        this.QRCodeView = view;
    }

    @Singleton
    @Provides
    public IQRCodeView provideQRCode() {
        return QRCodeView;
    }
}
