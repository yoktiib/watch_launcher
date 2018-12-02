package com.pomohouse.launcher.fragment.about.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.fragment.about.interactor.IPinCodeInteractor;
import com.pomohouse.launcher.fragment.about.interactor.OnPinCodeListener;
import com.pomohouse.launcher.fragment.about.interactor.OnQRCodeListener;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.fragment.about.IPinCodeView;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public class PinCodePresenterImpl extends BaseRetrofitPresenter implements IPinCodePresenter, OnPinCodeListener {
    private IPinCodeView view;
    private IPinCodeInteractor interactor;

    public PinCodePresenterImpl(IPinCodeView view, IPinCodeInteractor interactor) {
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
    public void requestGetCode() {
        view.disableGetCodeButton();
        ImeiRequest imeiRequest = new ImeiRequest(WearerInfoUtils.getInstance().getImei(AppContextor.getInstance().getContext()));
        interactor.callGetCode( imeiRequest,this);
    }

    @Override
    public void onPinCodeFailure(MetaDataNetwork error) {
        view.enableGetCodeButton();
    }

    @Override
    public void onPinCodeSuccess(MetaDataNetwork metaData, PinCodeModel readyModel) {
        view.enableGetCodeButton();
        view.onGetPinSuccess(readyModel);
    }
}
