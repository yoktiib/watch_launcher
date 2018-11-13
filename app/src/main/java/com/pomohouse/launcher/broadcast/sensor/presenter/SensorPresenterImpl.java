package com.pomohouse.launcher.broadcast.sensor.presenter;

import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.launcher.api.requests.ImeiRequest;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class SensorPresenterImpl extends BaseRetrofitPresenter implements ISensorPresenter {

    public SensorPresenterImpl() {
    }

    @Override
    public void requestWearerStatus(WearerStatusRequest watchOnOffRequest) {
        if (watchOnOffRequest == null) return;
        TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_WEARER_STATUS, new Gson().toJson(watchOnOffRequest));
    }

    @Override
    public void requestFallService() {
        TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_FALL, "{}");
    }
}
