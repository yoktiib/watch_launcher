package com.pomohouse.launcher.fragment.contacts.interactor;

import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;

/**
 * Created by Admin on 6/26/2017 AD.
 */

public interface OnCheckAllowCallingListener extends IBaseInteractorListener{
    void onCheckAllowCallingFailure(MetaDataNetwork error);

    void onCheckAllowCallingSuccess(MetaDataNetwork metaData, ResultGenerator<CallContact> readyModel);
}
