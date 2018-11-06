package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.bff.dao.ReadyFriendResult;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnStartReadyFriendListener extends IBaseInteractorListener {
    void onStartReadyFriendSuccess(MetaDataNetwork metaData, ReadyFriendResult data);

    void onStartReadyFriendFailure(MetaDataNetwork metaData);
/*
    void onSearchFriendFailure(MetaDataNetwork error);

    void onFetchInviteFriendFailure(MetaDataNetwork error);

    void onRequestInviteFriendFailure(MetaDataNetwork error);

    void onCancelReadyFriendFailure(MetaDataNetwork error);

    void onConfirmFriendFailure(MetaDataNetwork error);*/
}
