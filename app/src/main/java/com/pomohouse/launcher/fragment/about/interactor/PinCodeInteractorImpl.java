package com.pomohouse.launcher.fragment.about.interactor;

import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.requests.ImeiRequest;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public class PinCodeInteractorImpl extends BaseInteractor implements IPinCodeInteractor {
    private WatchService pairService;

    public PinCodeInteractorImpl() {
        pairService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }

    @Override
    public void callGetCode(ImeiRequest imeiRequest, OnPinCodeListener listener) {
        if (listener == null)
            return;
        Observable<ResultGenerator<PinCodeModel>> service = pairService.callPinCode(imeiRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        listener.subscriptionRetrofit(service.subscribe(new Observer<ResultGenerator<PinCodeModel>>() {
            @Override
            public void onCompleted() {
                listener.unSubscriptionRetrofit();
            }

            @Override
            public void onError(Throwable e) {
                listener.onPinCodeFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResultGenerator<PinCodeModel> readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0)
                    listener.onPinCodeSuccess(metaData, readyModel.getData());
                else
                    listener.onPinCodeFailure(metaData);
            }
        }));
    }
}
