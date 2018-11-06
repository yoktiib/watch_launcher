package com.pomohouse.launcher.main.interactor;

import com.pomohouse.launcher.api.requests.RefreshLocationRequest;
import com.pomohouse.launcher.main.interactor.listener.OnEventListener;
import com.pomohouse.launcher.main.interactor.listener.OnInitialDeviceListener;
import com.pomohouse.launcher.main.interactor.listener.OnSOSListener;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.library.base.interfaces.interactor.IBaseRequestInteractor;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.InitDeviceRequest;
import com.pomohouse.launcher.api.requests.TimezoneUpdateRequest;
import com.pomohouse.launcher.api.requests.UpdateFirebaseRequest;

/**
 * Created by Admin on 8/18/16 AD.
 */
public interface ILauncherInteractor extends IBaseRequestInteractor {
    void callInitialDevice(InitDeviceRequest initialDeviceRequest, OnInitialDeviceListener listener);

    void callShutdownDevice(ImeiRequest imei);

    void callSOS(OnSOSListener listener, ImeiRequest imei);

    void callUpdateFCMToken(UpdateFirebaseRequest requestParam);

    void callUpdateInfoAndGetEventService(OnEventListener listener, LocationUpdateRequest locationInfo);

    void callSendNewLocationService(RefreshLocationRequest locationInfo);

    void callTimezoneChanged(TimezoneUpdateRequest timeZoneParam);
}
