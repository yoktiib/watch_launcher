package com.pomohouse.bff.network;

import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.ReadyFriendResult;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.bff.network.request.WatchInfoRequest;
import com.pomohouse.library.networks.ResultGenerator;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Admin on 8/26/16 AD.
 */
public interface BFFService {

    //Sender / Receive start BFF
    @POST("bff/requestStartReadyForBFF")
    Observable<ResultGenerator<ReadyFriendResult>> callStartReadyForBFF(@Body WatchInfoRequest param);

    //Sender / Receive Search
    @POST("bff/requestSearchFriend")
    Observable<FriendCollection> callSearchFriends(@Body WatchInfoRequest param);

    //Sender / Receive Fetch Invite
    @POST("bff/requestFetchInviteFriend")
    Observable<FetchFriendCollection> callFetchInviteFriend(@Body WatchInfoRequest param);

    //Sender invite result
    @POST("bff/requestInviteFriend")
    Observable<ResponseDao> callInviteFriend(@Body FriendRequest param);

    //Receive Confirm status result
    @POST("bff/requestConfirmInviteFriend")
    Observable<ResultGenerator<ResponseFriend>> callConfirmInviteFriend(@Body FriendRequest param);

    //Sender wait result
    @POST("bff/requestResultInviteFriend")
    Observable<ResultGenerator<ResponseFriend>> callAnswerInviteFriend(@Body FriendRequest param);

    //Sender / Receive end BFF
    @POST("bff/requestCancelReadyForBFF")
    Observable<ResponseDao> callCancelReadyForBFF(@Body WatchInfoRequest param);
}
