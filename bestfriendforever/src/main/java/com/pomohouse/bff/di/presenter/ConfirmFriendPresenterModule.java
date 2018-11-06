package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.bff.fragment.confirm.ConfirmFriendFragment;
import com.pomohouse.bff.fragment.confirm.IConfirmFriendView;
import com.pomohouse.bff.fragment.confirm.presenter.ConfirmFriendPresenterImpl;
import com.pomohouse.bff.fragment.confirm.presenter.IConfirmFriendPresenter;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/26/16 AD.
 */

@Module(
        injects = ConfirmFriendFragment.class,
        addsTo = ApplicationModule.class
)
public class ConfirmFriendPresenterModule {

    private IConfirmFriendView view;

    public ConfirmFriendPresenterModule(IConfirmFriendView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IConfirmFriendView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IConfirmFriendPresenter providePresenter(IConfirmFriendView view, IBestFriendForeverInteractor interactor) {
        return new ConfirmFriendPresenterImpl(view, interactor);
    }
}
