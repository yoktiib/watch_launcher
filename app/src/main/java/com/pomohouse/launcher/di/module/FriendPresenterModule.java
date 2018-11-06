package com.pomohouse.launcher.di.module;

import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.friends.FriendFragment;
import com.pomohouse.launcher.fragment.friends.IFriendView;
import com.pomohouse.launcher.fragment.friends.interactor.IFriendInteractor;
import com.pomohouse.launcher.fragment.friends.presenter.FriendPresenterImpl;
import com.pomohouse.launcher.fragment.friends.presenter.IFriendPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/18/16 AD.
 */
@Module(
        injects = {FriendFragment.class},
        addsTo = ApplicationModule.class
)
public class FriendPresenterModule {
    IFriendView friendView;

    @Singleton
    @Provides
    IFriendPresenter provideFriendPresenter(IFriendView view, IFriendInteractor interactor) {
        return new FriendPresenterImpl(view, interactor);
    }

    public FriendPresenterModule(IFriendView friendView) {
        this.friendView = friendView;
    }

    @Singleton
    @Provides
    public IFriendView provideFriendView() {
        return friendView;
    }
}
