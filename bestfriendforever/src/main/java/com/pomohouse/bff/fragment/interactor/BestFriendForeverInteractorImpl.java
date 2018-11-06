package com.pomohouse.bff.fragment.interactor;

import android.os.Process;

import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.ReadyFriendResult;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.fragment.interactor.listener.OnCancelReadyFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnConfirmFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnFetchInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnResultInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnSearchFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnStartReadyFriendListener;
import com.pomohouse.bff.network.BFFService;
import com.pomohouse.bff.network.ResponseDao;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.bff.network.request.WatchInfoRequest;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ServiceApiGenerator;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Admin on 8/26/16 AD.
 */
public class BestFriendForeverInteractorImpl extends BaseInteractor implements IBestFriendForeverInteractor {
    private BFFService service;

    public BestFriendForeverInteractorImpl() {
        service = ServiceApiGenerator.getInstance().createService(BFFService.class);
    }

    @Override
    public void callStartReadyFriend(OnStartReadyFriendListener mStartReadyListener, WatchInfoRequest requestModel) {
        if (mStartReadyListener == null)
            return;
        Observable<ResultGenerator<ReadyFriendResult>> callSafeZone = service.callStartReadyForBFF(requestModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mStartReadyListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<ResultGenerator<ReadyFriendResult>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mStartReadyListener.onStartReadyFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResultGenerator<ReadyFriendResult> readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0) {
                    mStartReadyListener.onStartReadyFriendSuccess(metaData, readyModel.getData());
                } else {
                    mStartReadyListener.onStartReadyFriendFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callSearchFriends(OnSearchFriendListener searchFriendListener, WatchInfoRequest requestModel) {
        if (searchFriendListener == null)
            return;
        Observable<FriendCollection> callSafeZone = service.callSearchFriends(requestModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        searchFriendListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<FriendCollection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                searchFriendListener.onSearchFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(FriendCollection friendCollection) {
                metaData = buildMetadata(friendCollection.getResCode(), friendCollection.getResDesc());
                if (metaData.getResCode() == 0) {
                    searchFriendListener.onSearchFriendSuccess(metaData, friendCollection);
                } else {
                    searchFriendListener.onSearchFriendFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callFetchInviteFriend(OnFetchInviteFriendListener mFetchInviteFriendListener, WatchInfoRequest requestModel) {
        if (mFetchInviteFriendListener == null)
            return;
        Observable<FetchFriendCollection> callSafeZone = service.callFetchInviteFriend(requestModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mFetchInviteFriendListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<FetchFriendCollection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mFetchInviteFriendListener.onFetchInviteFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(FetchFriendCollection fetchFriendCollection) {
                metaData = buildMetadata(fetchFriendCollection.getResCode(), fetchFriendCollection.getResDesc());
                if (metaData.getResCode() == 0) {
                    mFetchInviteFriendListener.onFetchInviteFriendSuccess(metaData, fetchFriendCollection);
                } else {
                    mFetchInviteFriendListener.onFetchInviteFriendFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callConfirmInviteFriend(OnConfirmFriendListener mConfirmFriendListener, FriendRequest friendRequest) {
        if (mConfirmFriendListener == null)
            return;
        Observable<ResultGenerator<ResponseFriend>> callSafeZone = service.callConfirmInviteFriend(friendRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mConfirmFriendListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<ResultGenerator<ResponseFriend>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mConfirmFriendListener.onConfirmFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResultGenerator<ResponseFriend> readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0) {
                    mConfirmFriendListener.onConfirmFriendSuccess(metaData, readyModel.getData());
                } else {
                    mConfirmFriendListener.onConfirmFriendFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callResultInviteFriend(OnResultInviteFriendListener mResultInviteListener, FriendRequest friendRequest) {
        if (mResultInviteListener == null)
            return;
        Observable<ResultGenerator<ResponseFriend>> callSafeZone = service.callAnswerInviteFriend(friendRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mResultInviteListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<ResultGenerator<ResponseFriend>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e("callAnswerInviteFriend : " + e.getMessage());
                mResultInviteListener.onResultInviteFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResultGenerator<ResponseFriend> responseFriend) {
                metaData = buildMetadata(responseFriend.getResCode(), responseFriend.getResDesc());
                if (responseFriend.getResCode() == 0) {
                    Timber.e("callAnswerInviteFriend onResultInviteSuccess");
                    mResultInviteListener.onResultInviteSuccess(metaData, responseFriend.getData());
                } else {
                    Timber.e("callAnswerInviteFriend onResultInviteFailure");
                    mResultInviteListener.onResultInviteFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callInviteFriend(OnInviteFriendListener mInviteListener, FriendRequest friendRequest) {
        if (mInviteListener == null)
            return;
        Observable<ResponseDao> callSafeZone = service.callInviteFriend(friendRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mInviteListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                mInviteListener.onRequestInviteFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResponseDao readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0) {
                    mInviteListener.onRequestInviteFriendSuccess(metaData);
                } else {
                    mInviteListener.onRequestInviteFriendFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callCancelReadyFriend(OnCancelReadyFriendListener mCancelReadyFriendListener, WatchInfoRequest requestModel) {
        if (mCancelReadyFriendListener == null)
            return;
        Observable<ResponseDao> callSafeZone = service.callCancelReadyForBFF(requestModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        mCancelReadyFriendListener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {
                Process.killProcess(Process.myPid());
            }

            @Override
            public void onError(Throwable e) {
                Process.killProcess(Process.myPid());
                mCancelReadyFriendListener.onCancelReadyFriendFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResponseDao responseDao) {
                metaData = buildMetadata(responseDao.getResCode(), responseDao.getResDesc());
                if (metaData.getResCode() == 0) {
                    mCancelReadyFriendListener.onCancelReadyFriendSuccess(metaData, responseDao);
                } else {
                    mCancelReadyFriendListener.onCancelReadyFriendFailure(metaData);
                }
                Process.killProcess(Process.myPid());
            }
        }));
    }
}
