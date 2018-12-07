package com.pomohouse.launcher.fragment.contacts.presenter;

import com.pomohouse.launcher.api.requests.AllowCallingRequest;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 8/30/16 AD.
 */
public interface IContactPresenter extends IBaseRequestStatePresenter {
    void requestContact();

    void onDeleteContact(ContactModel contact) throws Exception;

    void onUpdateContact(ContactModel contact);

    void onInsertContact(ContactModel contact);

    void onDeleteAllContact();

    void requestCheckAllowCalling(AllowCallingRequest callingRequest);

    void sendCalling(CallContact content);
}
