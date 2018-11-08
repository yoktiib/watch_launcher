package com.pomohouse.launcher.main;

import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.library.networks.MetaDataNetwork;

public interface OnContactCallbackListener {

    void onContactListSuccess(MetaDataNetwork metaData, ContactCollection contactCollection);
    void onContactListFailure(MetaDataNetwork error);
}
