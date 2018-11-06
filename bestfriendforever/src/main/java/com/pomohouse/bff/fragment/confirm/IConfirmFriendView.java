package com.pomohouse.bff.fragment.confirm;

import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/29/16 AD.
 */
public interface IConfirmFriendView {
    void failureAddFriend(MetaDataNetwork error);

    void successAddFriend(ResponseFriend responseFriend);

    void setAvatar(String avatar);

    void setName(String name);
}
