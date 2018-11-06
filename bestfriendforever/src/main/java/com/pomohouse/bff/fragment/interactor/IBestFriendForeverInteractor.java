package com.pomohouse.bff.fragment.interactor;

import com.pomohouse.bff.fragment.interactor.listener.OnInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnConfirmFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnCancelReadyFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnFetchInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnResultInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnStartReadyFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnSearchFriendListener;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.bff.network.request.WatchInfoRequest;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface IBestFriendForeverInteractor {
    void callStartReadyFriend(OnStartReadyFriendListener mBffListener, WatchInfoRequest requestModel);

    void callSearchFriends(OnSearchFriendListener mSearchFriendListener, WatchInfoRequest requestModel);

    void callFetchInviteFriend(OnFetchInviteFriendListener mFetchRequestAddNewFriendListener, WatchInfoRequest requestModel);

    void callConfirmInviteFriend(OnConfirmFriendListener mAnswerNewFriendListener, FriendRequest friendRequest);

    void callCancelReadyFriend(OnCancelReadyFriendListener mCancelRequestFriendListener, WatchInfoRequest requestModel);

    void callInviteFriend(OnInviteFriendListener mAddNewFriendListener, FriendRequest friendRequest);

    void callResultInviteFriend(OnResultInviteFriendListener mGetResponseFriendListener, FriendRequest friendRequest);
}
