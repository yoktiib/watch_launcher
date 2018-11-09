package com.pomohouse.launcher.di.module;

import com.pomohouse.launcher.di.ApplicationModule;
import com.pomohouse.launcher.fragment.contacts.ContactFragment;
import com.pomohouse.launcher.fragment.contacts.presenter.ContactPresenterImpl;
import com.pomohouse.launcher.fragment.contacts.presenter.IContactPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Admin on 6/26/2017 AD.
 */
@Module(injects = {ContactFragment.class}, addsTo = ApplicationModule.class)
public class ContactPresenterModule {

    @Singleton
    @Provides
    IContactPresenter provideContactPresenter() {
        return new ContactPresenterImpl();
    }
}
