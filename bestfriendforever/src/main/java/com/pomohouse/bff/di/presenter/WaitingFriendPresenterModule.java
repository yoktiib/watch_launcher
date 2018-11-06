package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.wait.IWaitingFriendView;
import com.pomohouse.bff.fragment.wait.WaitingFriendFragment;
import com.pomohouse.bff.fragment.wait.presenter.IWaitingFriendPresenter;
import com.pomohouse.bff.fragment.wait.presenter.WaitingPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/26/16 AD.
 */

@Module(
        injects = WaitingFriendFragment.class,
        addsTo = ApplicationModule.class
)
public class WaitingFriendPresenterModule {

    private IWaitingFriendView view;

    public WaitingFriendPresenterModule(IWaitingFriendView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IWaitingFriendView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IWaitingFriendPresenter providePresenter(IWaitingFriendView view, IBestFriendForeverInteractor interactor) {
        return new WaitingPresenterImpl(view, interactor);
    }
}
