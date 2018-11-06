package com.pomohouse.launcher.fragment.main.presenter;

import android.content.Context;
import android.content.Intent;

import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;
import com.pomohouse.launcher.models.EventDataInfo;

/**
 * Created by Admin on 8/19/16 AD.
 */
public interface IMainFragmentPresenter extends IBaseRequestStatePresenter {
    void onBatteryLevelInfo(Context mContext);

    void onBatteryLevelInfoAndResource(Context mContext);

    void updateEventReceiver(EventDataInfo lastEvent);

    void onDeviceStatusActionReceived(Intent intent);

    void onSignalChange(int signal);

    void onNoSimCardPlugin();

    //void checkEventUnreadAndVolume(EventDataInfo eventDataInfo);

    void ConnectivityChanged(ConnectivityChanged connectivityChanged);

    //void provideInClassModeManager(IInClassModePrefManager inClassModeManager);
}
