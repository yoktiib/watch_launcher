package com.pomohouse.launcher.fragment.contacts.interactor;

import com.pomohouse.launcher.api.requests.AllowCallingRequest;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.library.base.interfaces.interactor.IBaseRequestInteractor;

/**
 * Created by Admin on 8/30/16 AD.
 */
public interface IContactInteractor extends IBaseRequestInteractor {
    void callContactService(OnContactListener listener, ImeiRequest imeiRequest);

    void callCheckAllowCalling(AllowCallingRequest callingRequest, OnCheckAllowCallingListener listener);
}
