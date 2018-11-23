package com.pomohouse.launcher.fragment.about.presenter;

import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface OnQRCodeListener{
    void onQRCodeFailure(MetaDataNetwork error);

    void onQRCodeSuccess(MetaDataNetwork metaData, QRCodeModel readyModel);
}
