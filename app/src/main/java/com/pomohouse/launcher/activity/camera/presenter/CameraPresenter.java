package com.pomohouse.launcher.activity.camera.presenter;

import com.pomohouse.launcher.activity.camera.ICameraView;
import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ServiceApiGenerator;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CameraPresenter implements ICameraPresenter {
    ICameraView cameraView;
    WatchService service;

    public CameraPresenter(ICameraView cameraView) {
        this.cameraView = cameraView;
    }

    @Override
    public void onSendPictureToStore(OnUploadImageListener listener, ImageModelRequest img) {

        Map<String, RequestBody> map = new HashMap<>();
        map.put("imei", RequestBody.create(MediaType.parse("text/plain"), String.valueOf(img.getImei())));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), img.getImage());
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        service = ServiceApiGenerator.getInstance().createService(WatchService.class);
        Observable<ResultGenerator> addWatchService = service.callImageService(map, body).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());
        addWatchService.subscribe(new Observer<ResultGenerator>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                listener.onUploadImageFailure();

            }

            @Override
            public void onNext(ResultGenerator familyModel) {
                listener.onUploadImageSuccess();
            }
        });
    }
}
