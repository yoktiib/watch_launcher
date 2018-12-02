package com.pomohouse.launcher.main.interactor.listener;

import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;

/**
 * Created by Admin on 10/12/2016 AD.
 */

public interface OnSOSListener extends IBaseInteractorListener {
    void onSOSFailure(MetaDataNetwork error);

    void onSOSSuccess(MetaDataNetwork metaData, ResponseDao data);
}
