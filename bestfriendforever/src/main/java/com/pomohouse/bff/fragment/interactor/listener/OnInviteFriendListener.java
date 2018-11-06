package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnInviteFriendListener extends IBaseInteractorListener {
    void onRequestInviteFriendFailure(MetaDataNetwork error);

    void onRequestInviteFriendSuccess(MetaDataNetwork metaData);
}
