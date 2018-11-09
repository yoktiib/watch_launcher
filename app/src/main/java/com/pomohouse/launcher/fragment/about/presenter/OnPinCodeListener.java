package com.pomohouse.launcher.fragment.about.presenter;

import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface OnPinCodeListener extends IBaseInteractorListener{
    void onPinCodeFailure(MetaDataNetwork error);

    void onPinCodeSuccess(MetaDataNetwork metaData, PinCodeModel readyModel);
}
