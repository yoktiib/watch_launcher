package com.pomohouse.bff.di;

import com.pomohouse.bff.fragment.interactor.BestFriendForeverInteractorImpl;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 8/26/16 AD.
 */
@Module(library = true)
public class InteractorModule {
    @Provides
    @Singleton
    public IBestFriendForeverInteractor provideBestFriendForeverInteractor() {
        return new BestFriendForeverInteractorImpl();
    }/*

    @Provides
    @Singleton
    public IFriendInteractor provideFriendInteractor() {
        return new FriendInteractorImpl();
    }*/
}