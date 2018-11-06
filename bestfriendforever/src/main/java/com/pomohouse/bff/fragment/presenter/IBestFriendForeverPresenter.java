package com.pomohouse.bff.fragment.presenter;

import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface IBestFriendForeverPresenter extends IBaseRequestStatePresenter {
    void requestReadyRequestFriend();

    //void requestSearchFriend();

    void stopRequestFriend();

    void openWaitingFriend(FriendItemModel friendDao);

    void requestAddFriend(FriendItemModel friendDao);

    void updateBTNameAddress(String macAddress);

    void resetBluetoothMacAddress();

    void requestRequestIntervalFriend();
}
