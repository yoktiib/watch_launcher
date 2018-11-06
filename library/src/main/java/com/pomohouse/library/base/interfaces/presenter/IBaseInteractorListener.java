package com.pomohouse.library.base.interfaces.presenter;

import rx.Subscription;

/**
 * Created by Admin on 4/21/2016 AD.
 */
public interface IBaseInteractorListener extends IBaseLifeCyclePresenter{

    void subscriptionRetrofit(Subscription subscription);

    void unSubscriptionRetrofit();

}
