package com.pomohouse.bff.di.presenter;

import com.pomohouse.bff.di.ApplicationModule;
import com.pomohouse.bff.fragment.result.ResultFriendFragment;

import dagger.Module;

/**
 * Created by Admin on 8/31/16 AD.
 */

@Module(
        addsTo = ApplicationModule.class, injects = ResultFriendFragment.class, includes = CancelFriendForeverPresenterModule.class
)
public class ResultFriendPresenterModule {

    public ResultFriendPresenterModule() {
    }

}


