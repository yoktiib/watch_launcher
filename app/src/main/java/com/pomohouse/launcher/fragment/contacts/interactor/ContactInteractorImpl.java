package com.pomohouse.launcher.fragment.contacts.interactor;

import com.pomohouse.launcher.api.requests.AllowCallingRequest;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.api.WatchService;
import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.library.base.BaseInteractor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResultGenerator;
import com.pomohouse.library.networks.ServiceApiGenerator;
import com.pomohouse.launcher.api.requests.ImeiRequest;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Admin on 8/30/16 AD.
 */
public class ContactInteractorImpl extends BaseInteractor implements IContactInteractor {
    private WatchService contactService;

    public ContactInteractorImpl() {
        contactService = ServiceApiGenerator.getInstance().createService(WatchService.class);
    }

    @Override
    public void callContactService(OnContactListener listener, ImeiRequest imeiRequest) {
        if (listener == null)
            return;
       /* Observable<ContactCollection> service = contactService.callReadyRequestFriend(imeiRequest)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        listener.subscriptionRetrofit(service.subscribe(new Observer<ContactCollection>() {
            @Override
            public void onCompleted() {
                listener.unSubscriptionRetrofit();
            }

            @Override
            public void onError(Throwable e) {
                 listener.onContactFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ContactCollection readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0)
                    listener.onContactSuccess(metaData, readyModel);
                else
                    listener.onContactFailure(metaData);
            }
        }));*/
    }

    @Override
    public void callCheckAllowCalling(AllowCallingRequest callingRequest, OnCheckAllowCallingListener listener) {
        if (listener == null)
            return;
        Observable<ResultGenerator<CallContact>> service = contactService.callCheckAllowCalling(callingRequest.getToContactId(),callingRequest.getFromContactId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        listener.subscriptionRetrofit(service.subscribe(new Observer<ResultGenerator<CallContact>>() {
            @Override
            public void onCompleted() {
                listener.unSubscriptionRetrofit();
            }

            @Override
            public void onError(Throwable e) {
                listener.onCheckAllowCallingFailure(MetaDataNetwork.getError(e));
            }

            @Override
            public void onNext(ResultGenerator<CallContact> readyModel) {
                metaData = buildMetadata(readyModel.getResCode(), readyModel.getResDesc());
                if (metaData.getResCode() == 0)
                    listener.onCheckAllowCallingSuccess(metaData, readyModel);
                else
                    listener.onCheckAllowCallingFailure(metaData);
            }
        }));
    }
}
