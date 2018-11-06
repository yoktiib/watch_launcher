package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnFetchInviteFriendListener extends IBaseInteractorListener {
    void onFetchInviteFriendSuccess(MetaDataNetwork error, FetchFriendCollection fetchFriendCollection);
    void onFetchInviteFriendFailure(MetaDataNetwork error);
}
