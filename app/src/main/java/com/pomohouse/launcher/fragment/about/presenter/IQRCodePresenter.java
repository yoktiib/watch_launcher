package com.pomohouse.launcher.fragment.about.presenter;

import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 1/30/2017 AD.
 */

public interface IQRCodePresenter extends IBaseRequestStatePresenter {
    void requestQRCode(String imei);
}
