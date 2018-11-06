package com.pomohouse.bff.fragment.interactor.listener;

import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface OnSearchFriendListener extends IBaseInteractorListener {

    void onSearchFriendFailure(MetaDataNetwork error);

    void onSearchFriendSuccess(MetaDataNetwork metaData, FriendCollection friendCollection);
}
