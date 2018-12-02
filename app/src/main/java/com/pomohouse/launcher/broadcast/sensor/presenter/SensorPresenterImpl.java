package com.pomohouse.launcher.broadcast.sensor.presenter;

import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.launcher.broadcast.sensor.interactor.ISensorInteractor;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.launcher.api.requests.ImeiRequest;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class SensorPresenterImpl extends BaseRetrofitPresenter implements ISensorPresenter {
    private ISensorInteractor interactor;

    public SensorPresenterImpl(ISensorInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void requestWearerStatus(WearerStatusRequest watchOnOffRequest) {
        if (watchOnOffRequest == null)
            return;
        if (interactor != null)
            interactor.callSenderWearerStatusService(watchOnOffRequest);
    }

    @Override
    public void requestFallService(ImeiRequest imeiRequest) {
        if (imeiRequest == null)
            return;
        if (interactor != null)
            interactor.callFallService(imeiRequest);
    }
}
