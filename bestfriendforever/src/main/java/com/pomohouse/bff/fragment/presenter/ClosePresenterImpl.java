package com.pomohouse.bff.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.bff.fragment.ICancelView;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.interactor.listener.OnCancelReadyFriendListener;
import com.pomohouse.bff.network.ResponseDao;
import com.pomohouse.bff.network.request.WatchInfoRequest;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/31/16 AD.
 */
public class ClosePresenterImpl extends BaseRetrofitPresenter implements IClosePresenter, OnCancelReadyFriendListener {
    IBestFriendForeverInteractor interactor;
    ICancelView view;
    private WatchInfoRequest watchInfoRequest;

    public ClosePresenterImpl(ICancelView view, IBestFriendForeverInteractor interactor) {
        super();
        this.interactor = interactor;
        this.view = view;
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
        if (param[0] instanceof WearerInfo) {
            WearerInfo wearerInfo = (WearerInfo) param[0];
            watchInfoRequest = new WatchInfoRequest();
            watchInfoRequest.setImei(wearerInfo.getImei());
            watchInfoRequest.setLat(wearerInfo.getLat());
            watchInfoRequest.setLng(wearerInfo.getLng());
        }
    }

    @Override
    public void requestCancelRequestFriend() {
        if (watchInfoRequest == null)
            return;
        interactor.callCancelReadyFriend(this, watchInfoRequest);
    }

    @Override
    public void onCancelReadyFriendSuccess(MetaDataNetwork error, ResponseDao data) {
        view.successCancel();
    }

    @Override
    public void onCancelReadyFriendFailure(MetaDataNetwork error) {
        view.failureCancel();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }
}
