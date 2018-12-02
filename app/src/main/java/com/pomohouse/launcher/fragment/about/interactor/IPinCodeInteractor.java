package com.pomohouse.launcher.fragment.about.interactor;

import com.pomohouse.library.base.interfaces.interactor.IBaseRequestInteractor;
import com.pomohouse.launcher.api.requests.ImeiRequest;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface IPinCodeInteractor extends IBaseRequestInteractor{
    void callGetCode(ImeiRequest imeiRequest, OnPinCodeListener pinCodeListener);
    void callQRCode(ImeiRequest imeiRequest, OnQRCodeListener qrCodeListener);
}
