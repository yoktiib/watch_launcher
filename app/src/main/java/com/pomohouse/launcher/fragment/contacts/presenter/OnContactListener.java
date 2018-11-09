package com.pomohouse.launcher.fragment.contacts.presenter;

import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.library.base.interfaces.presenter.IBaseInteractorListener;
import com.pomohouse.library.networks.MetaDataNetwork;

/**
 * Created by Admin on 8/30/16 AD.
 */
public interface OnContactListener extends IBaseInteractorListener{
    void onContactFailure(MetaDataNetwork error);

    void onContactSuccess(MetaDataNetwork metaData, ContactCollection data);
}
