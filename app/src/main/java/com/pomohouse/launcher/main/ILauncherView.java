package com.pomohouse.launcher.main;

import android.content.Intent;
import android.location.Location;

import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;

import java.util.Locale;

/**
 * Created by Admin on 8/18/16 AD.
 */
public interface ILauncherView {
    void initializeServiceAndReceiver();

    void onInitialSuccess(MetaDataNetwork metaData, DeviceInfoModel data);

    void onInitialFailure(MetaDataNetwork error);

    void failureSOS(MetaDataNetwork error);

    void successSOS(MetaDataNetwork metaData, ResponseDao data);

    void onWakeScreen();

    void setUpInClassModeEnable();

    void setUpInClassModeDisable();

    void setUpTimeZone(String timeZone);

    void onEnableSilentMode();

    void enableAutoAnswer();

    void onUpdateWearerStatus();

    void onDisableSilentMode();

    void onChangeLanguage(Locale locale);

    void setAutoTimezone();

    void onResetFactoryEventReceived(EventDataInfo eventData);

    void onEventPairReceived(EventDataInfo eventData);

    void onBestFriendForeverReceived(EventDataInfo eventData);

    void onSyncContact(EventDataInfo eventData);

    void onSetUpBrightnessTimeOut(int timeOut);

    void disableAutoAnswer();

    void inClassModeSetUpEventReceived(EventDataInfo eventData);

    void onAlarmEventReceived();

    void openPinCodeView(String code);

    void onSendEventToBroadcast(EventDataInfo eventContent);

    void onSendFilterBroadcast(String filter);

    void sendIntentToBroadcast(Intent intent);

    void setUpConfigurationWhenLockScreen();

    void setUpConfiguration();

    void startLocation();

    void stopLocation();

    void setupSuccess();

    void sleepTimeSetUpEventReceived(EventDataInfo eventData);

    void requestGPSLocation();
    void startSensor();

    void stopSensor();

    void updateLocation(Location location);
}