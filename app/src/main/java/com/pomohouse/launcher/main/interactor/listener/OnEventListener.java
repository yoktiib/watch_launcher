package com.pomohouse.launcher.main.interactor.listener;

import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.launcher.models.EventDataListModel;

/**
 * Created by Admin on 8/24/16 AD.
 */
public interface OnEventListener extends IBaseInteractorListener{
    void onCallEventFailure(MetaDataNetwork error);

    void onCallEventSuccess(MetaDataNetwork metaData, EventDataListModel data);
}
