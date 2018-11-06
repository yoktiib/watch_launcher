package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.bff.fragment.BestFriendForeverFragment;
import com.pomohouse.bff.fragment.IBestFriendForeverView;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.presenter.BestFriendForeverPresenterImpl;
import com.pomohouse.bff.fragment.presenter.IBestFriendForeverPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/26/16 AD.
 */

@Module(
        injects = BestFriendForeverFragment.class,
        /*includes = CancelFriendForeverPresenterModule.class,*/
        addsTo = ApplicationModule.class
)
public class BestFriendForeverPresenterModule {

    private IBestFriendForeverView view;

    public BestFriendForeverPresenterModule(IBestFriendForeverView view) {
        this.view = view;
    }

    @Singleton
    @Provides
    public IBestFriendForeverView provideView() {
        return view;
    }


    @Singleton
    @Provides
    public IBestFriendForeverPresenter providePresenter(IBestFriendForeverView bestFriendForeverView, IBestFriendForeverInteractor bestFriendForeverInteractor) {
        return new BestFriendForeverPresenterImpl(bestFriendForeverView, bestFriendForeverInteractor);
    }
}
