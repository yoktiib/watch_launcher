package com.pomohouse.bff.fragment.confirm.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.bff.dao.FetchFriend;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.fragment.confirm.IConfirmFriendView;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.interactor.listener.OnConfirmFriendListener;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class ConfirmFriendPresenterImpl extends BaseRetrofitPresenter implements IConfirmFriendPresenter, OnConfirmFriendListener {
    IConfirmFriendView view;
    IBestFriendForeverInteractor interactor;
    FriendRequest friendRequest;

    public ConfirmFriendPresenterImpl(IConfirmFriendView view, IBestFriendForeverInteractor interactor) {
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
        if (param[0] instanceof FetchFriend) {
            FetchFriend fetchFriend = (FetchFriend) param[0];
            friendRequest = new FriendRequest();
            friendRequest.setImei(fetchFriend.getImei());
            friendRequest.setFriendImei(fetchFriend.getFriendImei());
            view.setName(fetchFriend.getName());
            view.setAvatar(fetchFriend.getAvatar());
        }
    }

    @Override
    public void requestAcceptFriend() {
        if (friendRequest == null)
            return;
        friendRequest.setAllow("Y");
        interactor.callConfirmInviteFriend(this, friendRequest);
    }

    @Override
    public void requestCancelFriend() {
        if (friendRequest == null)
            return;
        friendRequest.setAllow("N");
        interactor.callConfirmInviteFriend(this, friendRequest);
    }

    @Override
    public void onConfirmFriendSuccess(MetaDataNetwork error, ResponseFriend responseFriend) {
        view.successAddFriend(responseFriend);
    }

    @Override
    public void onConfirmFriendFailure(MetaDataNetwork error) {
        view.failureAddFriend(error);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

}
