package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnConfirmFriendListener extends IBaseInteractorListener {
    void onConfirmFriendSuccess(MetaDataNetwork error, ResponseFriend responseFriend);

    void onConfirmFriendFailure(MetaDataNetwork error);
}
