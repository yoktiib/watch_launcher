package com.pomohouse.launcher.broadcast.sensor.interactor;

import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.ResponseDao;
import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.requests.ImeiRequest;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class SensorInteractorImpl extends BaseInteractor implements ISensorInteractor {
    private WatchService service;

    public SensorInteractorImpl() {
        service = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }

    @Override
    public void callSenderWearerStatusService(WearerStatusRequest watchOnOffRequest) {
        if (service == null)
            return;
        Observable<ResponseDao> callSafeZone = service.callSenderWearerStatusService(watchOnOffRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        callSafeZone.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResponseDao readyModel) {
            }
        });
    }

    @Override
    public void callFallService(ImeiRequest imeiRequest) {
        if (service == null)
            return;
        Observable<ResponseDao> callSafeZone = service.callFallStatusService(imeiRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        callSafeZone.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResponseDao readyModel) {
            }
        });
    }
}
