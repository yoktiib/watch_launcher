package com.pomohouse.launcher.main.interactor.listener;

import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 6/1/2017 AD.
 */

public interface OnFCMUpdateListener extends IBaseInteractorListener {
    void onFCMUpdateFailure(MetaDataNetwork error);

    void onFCMUpdateSuccess(MetaDataNetwork metaData);
}
