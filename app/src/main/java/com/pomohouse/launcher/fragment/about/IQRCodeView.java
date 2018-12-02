package com.pomohouse.launcher.fragment.about;

import com.pomohouse.launcher.models.QRCodeModel;
import com.pomohouse.library.networks.MetaDataNetwork;

public interface IQRCodeView {
    void onFailureQRCode(MetaDataNetwork error);

    void onSuccessQRCode(MetaDataNetwork metaData, QRCodeModel readyModel);
}
