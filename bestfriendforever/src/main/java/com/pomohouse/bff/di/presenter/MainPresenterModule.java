package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.MainActivity;
import com.pomohouse.bff.di.ApplicationModule;

import dagger.Module;

/**
 * Created by Admin on 8/26/16 AD.
 */

@Module(
        injects = MainActivity.class,
        includes = CancelFriendForeverPresenterModule.class,
        addsTo = ApplicationModule.class
)
public class MainPresenterModule {
    public MainPresenterModule() {
    }
}
