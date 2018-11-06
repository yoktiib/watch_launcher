package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.bff.network.ResponseDao;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnCancelReadyFriendListener extends IBaseInteractorListener {

    void onCancelReadyFriendSuccess(MetaDataNetwork error, ResponseDao data);
    void onCancelReadyFriendFailure(MetaDataNetwork error);
}
