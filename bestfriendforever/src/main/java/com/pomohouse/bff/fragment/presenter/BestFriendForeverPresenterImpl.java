package com.pomohouse.bff.fragment.presenter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.gson.JsonArray;
import com.pomohouse.bff.dao.FetchFriendCollection;
import com.pomohouse.bff.dao.FriendCollection;
import com.pomohouse.bff.dao.FriendItemModel;
import com.pomohouse.bff.dao.ReadyFriendResult;
import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.bff.fragment.IBestFriendForeverView;
import com.pomohouse.bff.fragment.interactor.IBestFriendForeverInteractor;
import com.pomohouse.bff.fragment.interactor.listener.OnFetchInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnInviteFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnSearchFriendListener;
import com.pomohouse.bff.fragment.interactor.listener.OnStartReadyFriendListener;
import com.pomohouse.bff.network.Utils;
import com.pomohouse.bff.network.request.FriendRequest;
import com.pomohouse.bff.network.request.WatchInfoRequest;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * Created by Admin on 8/26/16 AD.
 */
public class BestFriendForeverPresenterImpl extends BaseRetrofitPresenter implements IBestFriendForeverPresenter, OnStartReadyFriendListener, OnSearchFriendListener, OnFetchInviteFriendListener, OnInviteFriendListener {
    IBestFriendForeverView view;
    IBestFriendForeverInteractor interactor;
    private WatchInfoRequest watchInfoRequest;
    private AtomicLong lastTick = new AtomicLong(0L);
    private Subscription subscription;
    private FriendItemModel friendModel;
    private ArrayList<String> mDeviceList = new ArrayList<>();
    private boolean isRunning = false;

    public BestFriendForeverPresenterImpl(IBestFriendForeverView iBestFriendForeverView, IBestFriendForeverInteractor iBestFriendForeverInteractor) {
        this.view = iBestFriendForeverView;
        this.interactor = iBestFriendForeverInteractor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        if (param == null)
            return;
        if (param.length < 1)
            return;
        if (param[0] == null)
            return;
        if (param[0] instanceof WearerInfo) {
            WearerInfo wearerInfo = (WearerInfo) param[0];
            watchInfoRequest = new WatchInfoRequest();
            watchInfoRequest.setImei(WearerInfoUtils.getInstance().getImei());
            watchInfoRequest.setLat(wearerInfo.getLat());
            watchInfoRequest.setLng(wearerInfo.getLng());
            watchInfoRequest.setMyBTName(getBluetoothBTName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRequestFriend();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void requestReadyRequestFriend() {
        if (watchInfoRequest == null) {
            view.isNullObjectWearerInfo();
            return;
        }
        interactor.callStartReadyFriend(this, watchInfoRequest);
    }

    /**
     * get bluetooth adapter MAC address
     *
     * @return MAC address String
     */
    @SuppressLint("HardwareIds")
    private static String getBluetoothBTName() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Timber.e("device does not support bluetooth");
            return null;
        }
        return mBluetoothAdapter.getName();
    }

    /*
    @Override
    public void requestSearchFriend() {
        if (watchInfoRequest == null) {
            view.isNullObjectWearerInfo();
            return;
        }
*//*        jsonArr = new JsonArray();
        if (mDeviceList != null && mDeviceList.size() > 0)
            for (String macAddress : mDeviceList)
                jsonArr.add(macAddress);
        watchInfoRequest.setMacAddress(jsonArr.toString());
        interactor.callSearchFriends(this, watchInfoRequest);*//*

        //Timber.i("tick = " + tick);
    }*/

    @Override
    public void stopRequestFriend() {
        isRunning = false;
        if (subscription != null)
            subscription.unsubscribe();
    }

    @Override
    public void openWaitingFriend(FriendItemModel friendDao) {
        view.goToWaitingFriend(friendDao);
    }

    @Override
    public void requestAddFriend(FriendItemModel friendModel) {
        this.friendModel = friendModel;
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setImei(WearerInfoUtils.getInstance().getImei());
        friendRequest.setFriendImei(friendModel.getFriendImei());
        interactor.callInviteFriend(this, friendRequest);
    }

    @Override
    public void resetBluetoothMacAddress() {
        mDeviceList = new ArrayList<>();
    }

    @Override
    public void requestRequestIntervalFriend() {
        if (subscription != null)
            subscription.unsubscribe();
        isRunning = true;
        this.callBffData();
        subscription = Observable.interval(Utils.getInstance().getTIME_LOOPER(), TimeUnit.SECONDS, Schedulers.io())
                .map(tick -> lastTick.getAndIncrement())
                .subscribe(tick -> callBffData()
                );
    }

    private void callBffData() {
        if (!isRunning && subscription != null) {
            subscription.unsubscribe();
            return;
        }
        Timber.i("Event Timer");
        if (!isNetworkAvailable()) {
            view.failureInternetConnection();
            return;
        }
        interactor.callFetchInviteFriend(BestFriendForeverPresenterImpl.this, watchInfoRequest);
        JsonArray jsonArr = new JsonArray();
        if (mDeviceList != null && mDeviceList.size() > 0)
            for (String btName : mDeviceList)
                jsonArr.add(btName);
        if (watchInfoRequest == null)
            watchInfoRequest = new WatchInfoRequest();
        watchInfoRequest.setImei(WearerInfoUtils.getInstance().getImei());
        watchInfoRequest.setMyBTName(getBluetoothBTName());
        watchInfoRequest.setFriendBTName(jsonArr.toString());
        interactor.callSearchFriends(BestFriendForeverPresenterImpl.this, watchInfoRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) AppContextor.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void updateBTNameAddress(String BTName) {
        if (BTName != null) {
            if (mDeviceList != null) {
                boolean isAlreadyBTName = false;
                for (String bt : mDeviceList) {
                    if (bt.equalsIgnoreCase(BTName))
                        isAlreadyBTName = true;
                }
                if (!isAlreadyBTName)
                    mDeviceList.add(BTName);
            }
        }
    }

    @Override
    public void onStartReadyFriendSuccess(MetaDataNetwork metaData, ReadyFriendResult data) {
        view.successReadyAddNewFriend(metaData);
    }

    @Override
    public void onStartReadyFriendFailure(MetaDataNetwork metaData) {
        if (metaData.getResCode() == 2507)
            view.failureNotAdmin(metaData);
        else
            view.failureReadyAddNewFriend(metaData);
    }

    @Override
    public void onSearchFriendFailure(MetaDataNetwork error) {
        view.failureSearchFriend(error);
    }

    @Override
    public void onSearchFriendSuccess(MetaDataNetwork metaData, FriendCollection friendCollection) {
        view.updateFriendList(friendCollection);
    }

    /*@Override
    public void onCancelReadyFriendSuccess(MetaDataNetwork error, ResponseDao data) {
        view.successCancelRequestFriend(error);
    }

    @Override
    public void onCancelReadyFriendFailure(MetaDataNetwork error) {
        view.failureCancelRequestFriend(error);
    }*/

    @Override
    public void onFetchInviteFriendSuccess(MetaDataNetwork error, FetchFriendCollection fetchFriendCollection) {
        view.goToConfirmFriend(fetchFriendCollection);
    }

    @Override
    public void onFetchInviteFriendFailure(MetaDataNetwork error) {
        view.failureRequestAddFriend(error);
    }

    @Override
    public void onRequestInviteFriendFailure(MetaDataNetwork error) {
        view.failureAddFriend(error);
    }

    @Override
    public void onRequestInviteFriendSuccess(MetaDataNetwork metaData) {
        if (friendModel != null)
            view.goToWaitingFriend(friendModel);
    }
}
