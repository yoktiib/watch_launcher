package com.pomohouse.launcher.fragment.main;

import android.content.Intent;

import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.pomohouse.launcher.models.EventDataInfo;

/**
 * Created by Admin on 8/19/16 AD.
 */
public interface IMainFragmentView {

    void onGroupChatEventReceived(EventDataInfo eventData);

    void onMessageEventReceived(EventDataInfo eventData);

    void onBatteryChanged(int icon, long level);

    void onBatteryChanged(long level);

    void powerConnected();

    void powerUnConnected();

    void batteryOkay(Intent intent);

    void batteryLow(Intent intent);

    void onSignalChanged(int drawable);

    void batteryFully();

    void onNotificationChanged(EventDataInfo eventData);

    void onTimeZoneChange(EventDataInfo eventData);

    void networkConnectionType(ConnectivityChanged typeOfConnection);
}
