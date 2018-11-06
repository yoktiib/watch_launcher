package com.pomohouse.launcher.main.interactor.listener;

import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 10/12/2016 AD.
 */

public interface OnInitialDeviceListener extends IBaseInteractorListener {
    void onInitialDeviceFailure(MetaDataNetwork error);

    void onInitialDeviceSuccess(MetaDataNetwork metaData, DeviceInfoModel data);
}
