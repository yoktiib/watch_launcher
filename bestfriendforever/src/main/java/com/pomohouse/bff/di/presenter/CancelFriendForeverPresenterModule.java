package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.bff.fragment.ICancelView;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.presenter.ClosePresenterImpl;
import com.pomohouse.bff.fragment.presenter.IClosePresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/31/16 AD.
 */

@Module(
        addsTo = ApplicationModule.class, library = true
)
public class CancelFriendForeverPresenterModule {

    private ICancelView view;

    public CancelFriendForeverPresenterModule(ICancelView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public ICancelView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IClosePresenter providePresenter(ICancelView view, IBestFriendForeverInteractor bestFriendForeverInteractor) {
        return new ClosePresenterImpl(view, bestFriendForeverInteractor);
    }
}

