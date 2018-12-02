package com.pomohouse.launcher.activity.fitness.interactor;

import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.ResponseDao;
import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.requests.StepRequest;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 10/25/2016 AD.
 */

public class FitnessInteractorImpl extends BaseInteractor implements IFitnessInteractor {
    private WatchService watchService;

    public FitnessInteractorImpl() {
        watchService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }

    @Override
    public void callSendStep(StepRequest step) {
        Observable<ResponseDao> service = watchService.callUpdateStep(step)
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
