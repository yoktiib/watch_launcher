package com.pomohouse.launcher.main;

import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataListModel;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;

public interface OnLauncherCallbackListener {

    void onInitialDeviceSuccess(MetaDataNetwork metaData, DeviceInfoModel data);
    void onInitialDeviceFailure(MetaDataNetwork error);

    void onCallEventSuccess(MetaDataNetwork metaData, EventDataListModel data);
    void onCallEventFailure(MetaDataNetwork metaDataNetwork);

    void onSOSSuccess(MetaDataNetwork metaData, ResponseDao data);
    void onSOSFailure(MetaDataNetwork error);

}
