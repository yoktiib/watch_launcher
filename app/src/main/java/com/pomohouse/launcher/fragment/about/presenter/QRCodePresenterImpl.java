package com.pomohouse.launcher.fragment.about.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.fragment.about.IPinCodeView;
import com.pomohouse.launcher.fragment.about.IQRCodeView;
import com.pomohouse.launcher.fragment.about.interactor.IPinCodeInteractor;
import com.pomohouse.launcher.fragment.about.interactor.OnPinCodeListener;
import com.pomohouse.launcher.fragment.about.interactor.OnQRCodeListener;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public class QRCodePresenterImpl extends BaseRetrofitPresenter implements IQRCodePresenter, OnQRCodeListener {
    private IQRCodeView view;
    private IPinCodeInteractor interactor;

    public QRCodePresenterImpl(IQRCodeView view, IPinCodeInteractor interactor) {
        super();
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void onQRCodeFailure(MetaDataNetwork error) {
        view.onFailureQRCode(error);
    }

    @Override
    public void onQRCodeSuccess(MetaDataNetwork metaData, QRCodeModel readyModel) {
        view.onSuccessQRCode(metaData,readyModel);
    }

    @Override
    public void requestQRCode(String imei) {
        interactor.callQRCode( new ImeiRequest(imei),this);
    }
}
