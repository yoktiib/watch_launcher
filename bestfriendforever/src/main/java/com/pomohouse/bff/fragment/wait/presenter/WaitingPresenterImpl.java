package com.pomohouse.bff.fragment.wait.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.interactor.listener.OnResultInviteFriendListener;
import com.pomohouse.bff.fragment.wait.IWaitingFriendView;
import com.pomohouse.bff.network.Utils;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class WaitingPresenterImpl extends BaseRetrofitPresenter implements IWaitingFriendPresenter, OnResultInviteFriendListener {
    IWaitingFriendView view;
    IBestFriendForeverInteractor interactor;
    FriendRequest friendRequest;
    private AtomicLong lastTick = new AtomicLong(0L);
    private Subscription subscription;

    public WaitingPresenterImpl(IWaitingFriendView view, IBestFriendForeverInteractor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        if (param == null)
            return;
        if (param.length < 1)
            return;
        if (param[0] == null)
            return;
        if (param[0] instanceof FriendItemModel) {
            FriendItemModel friendDao = (FriendItemModel) param[0];
            friendRequest = new FriendRequest();
            friendRequest.setFriendImei(friendDao.getFriendImei());
            view.setName(friendDao.getName());
            view.setAvatar(friendDao.getAvatar());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void requestStatusAnswerFriend(String IMEI) {
        if (friendRequest == null)
            return;
        friendRequest.setImei(IMEI);
        if (subscription != null)
            subscription.unsubscribe();
        subscription = Observable.interval(Utils.getInstance().getTIME_LOOPER(), TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> lastTick.getAndIncrement())
                .subscribe(tick -> {
                            Timber.i("tick = " + tick);
                            interactor.callResultInviteFriend(this, friendRequest);
                        }
                );
    }

    @Override
    public void onResultInviteFailure(MetaDataNetwork error) {
        view.failureRequestFriend(error);
    }

    @Override
    public void onResultInviteSuccess(MetaDataNetwork metaData, ResponseFriend data) {
        view.gotoResultFriend(data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRequestResponseFriend();
    }


    @Override
    public void stopRequestResponseFriend() {
        if (subscription != null)
            subscription.unsubscribe();
    }
}
