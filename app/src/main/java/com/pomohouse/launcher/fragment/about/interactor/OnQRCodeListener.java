package com.pomohouse.launcher.fragment.about.interactor;

import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface OnQRCodeListener extends IBaseInteractorListener{
    void onQRCodeFailure(MetaDataNetwork error);

    void onQRCodeSuccess(MetaDataNetwork metaData, QRCodeModel readyModel);
}
