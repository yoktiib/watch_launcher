package com.pomohouse.launcher.broadcast.sensor.presenter;

import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.WearerStatusRequest;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface ISensorPresenter {

    void requestWearerStatus(WearerStatusRequest watchOnOffRequest);

    void requestFallService(ImeiRequest imeiRequest);
}
