package com.pomohouse.launcher.main.interactor;

import com.pomohouse.launcher.api.requests.RefreshLocationRequest;
import com.pomohouse.launcher.main.interactor.listener.OnEventListener;
import com.pomohouse.launcher.main.interactor.listener.OnInitialDeviceListener;
import com.pomohouse.launcher.main.interactor.listener.OnSOSListener;
import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.launcher.models.EventDataListModel;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.InitDeviceRequest;
import com.pomohouse.launcher.api.requests.TimezoneUpdateRequest;
import com.pomohouse.launcher.api.requests.UpdateFirebaseRequest;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class LauncherInteractorImpl extends BaseInteractor implements ILauncherInteractor {

    private WatchService watchService;

    public LauncherInteractorImpl() {
        watchService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }

    @Override
    public void callInitialDevice(InitDeviceRequest initialDeviceRequest, OnInitialDeviceListener listener) {
        if (listener == null)
            return;
        Observable<ResultGenerator<DeviceInfoModel>> service = watchService.callInitialDevice(initialDeviceRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        listener.subscriptionRetrofit(service.subscribe(new Observer<ResultGenerator<DeviceInfoModel>>() {
            @Override
            public void onCompleted() {
                listener.unSubscriptionRetrofit();
            }

            @Override
            public void onError(Throwable e) {
                listener.onInitialDeviceFailure(MetaDataNetwork.getError(e));
                Timber.i("Failure : " + e.getMessage());
            }

            @Override
            public void onNext(ResultGenerator<DeviceInfoModel> readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0) {
                    listener.onInitialDeviceSuccess(metaData, readyModel.getData());
                } else {
                    listener.onInitialDeviceFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callShutdownDevice(ImeiRequest imeiRequest) {
        Observable<ResponseDao> service = watchService.callShutdownDevice(imeiRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        service.subscribe(new Observer<ResponseDao>() {
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
    public void callSOS(OnSOSListener listener, ImeiRequest imei) {
        Observable<ResponseDao> service = watchService.callSOS(imei)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        service.subscribe(new Observer<ResponseDao>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(ResponseDao readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                Timber.i("Data : " + metaData.getResDesc() + " = " + metaData.getResCode());
                if (metaData.getResCode() == 0) {
                    listener.onSOSSuccess(metaData, readyModel);
                } else {
                    Timber.i("Failure : ");
                    listener.onSOSFailure(metaData);
                }
            }
        });
    }

    @Override
    public void callUpdateFCMToken(UpdateFirebaseRequest requestParam) {
        Observable<ResponseDao> service = watchService.callUpdateFCMToken(requestParam)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        service.subscribe(new Observer<ResponseDao>() {
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
    public void callUpdateInfoAndGetEventService(OnEventListener listener, LocationUpdateRequest locationInfo) {
        Observable<EventDataListModel> callSafeZone = watchService.callUpdateInfoAndGetEventService(locationInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        listener.subscriptionRetrofit(callSafeZone.subscribe(new Observer<EventDataListModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                listener.onCallEventFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(EventDataListModel dataEvent) {
                metaData = buildMetadata(dataEvent.getResCode(), dataEvent.getResDesc());
                if (dataEvent.getResCode() == 0) {
                    Timber.e("Success onNext: " + dataEvent.getDataInfoList().size());
                    listener.onCallEventSuccess(metaData, dataEvent);
                } else {
                    Timber.e("onError onNext: ");
                    listener.onCallEventFailure(metaData);
                }
            }
        }));
    }

    @Override
    public void callSendNewLocationService(RefreshLocationRequest locationInfo) {
        Observable<ResponseDao> service = watchService.callUpdateLocation(locationInfo)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        service.subscribe(new Observer<ResponseDao>() {
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
    public void callTimezoneChanged(TimezoneUpdateRequest timeZoneParam) {
        Observable<ResponseDao> service = watchService.callUpdateTimezone(timeZoneParam)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        service.subscribe(new Observer<ResponseDao>() {
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
