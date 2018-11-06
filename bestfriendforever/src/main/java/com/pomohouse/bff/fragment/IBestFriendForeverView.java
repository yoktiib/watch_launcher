package com.pomohouse.bff.fragment;

import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface IBestFriendForeverView {

    void successReadyAddNewFriend(MetaDataNetwork metaData);

    void failureReadyAddNewFriend(MetaDataNetwork response);

    void isNullObjectWearerInfo();
/*

    void successCancelRequestFriend(MetaDataNetwork error);

    void failureCancelRequestFriend(MetaDataNetwork error);
*/

    void updateFriendList(FriendCollection friendCollection);

    void failureSearchFriend(MetaDataNetwork error);

    void goToWaitingFriend(FriendItemModel friendDao);

    void goToConfirmFriend(FetchFriendCollection friendDao);

    void failureAddFriend(MetaDataNetwork error);

    void failureRequestAddFriend(MetaDataNetwork error);

    void failureNotAdmin(MetaDataNetwork metaData);

    void failureInternetConnection();
}
