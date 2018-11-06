package com.pomohouse.bff.fragment.wait;

import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/29/16 AD.
 */
public interface IWaitingFriendView {
    void gotoResultFriend(ResponseFriend data);

    void failureRequestFriend(MetaDataNetwork error);

    void setName(String name);

    void setAvatar(String avatar);
}
