package com.pomohouse.library.base;

import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;

import rx.Subscription;

/**
 * Created by Admin on 4/21/2016 AD.
 */
public abstract class BaseRetrofitPresenter implements IBaseInteractorListener {
    protected Subscription subscriptionRetrofit;

    @Override
    public void subscriptionRetrofit(Subscription subscription) {
        subscriptionRetrofit = subscription;
    }

    @Override
    public void unSubscriptionRetrofit() {
        if (subscriptionRetrofit != null)
            subscriptionRetrofit.unsubscribe();
    }


    @Override
    public void onInitial(Object... param) {
    }

    @Override
    public void onDestroy() {
        unSubscriptionRetrofit();
    }


}