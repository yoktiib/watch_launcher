package com.pomohouse.launcher.broadcast.sensor.interactor;

import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.launcher.api.requests.ImeiRequest;

/**
 * Created by Admin on 9/5/16 AD.
 */
public interface ISensorInteractor {
    void callSenderWearerStatusService(WearerStatusRequest watchOnOffRequest);

    void callFallService(ImeiRequest imeiRequest);
}
