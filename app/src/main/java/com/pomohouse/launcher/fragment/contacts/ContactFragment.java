package com.pomohouse.launcher.fragment.contacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pomohouse.component.pager.VerticalViewPager;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.api.requests.AllowCallingRequest;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.broadcast.receivers.EventReceiver;
import com.pomohouse.launcher.di.module.ContactPresenterModule;
import com.pomohouse.launcher.fragment.contacts.presenter.IContactPresenter;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.models.events.CallContact;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.WearerInfoUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Fragment to manage the left page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class ContactFragment extends BaseFragment {

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ContactFragment() {
        setRetainInstance(false);
    }

    @Inject
    IContactPresenter presenter;
    @BindView(R.id.fragment_contact_pager)
    VerticalViewPager mVerticalViewPager;
    @BindView(R.id.tvNoContact)
    TextView tvNoContact;

    ContactVerticalPagerAdapter mAdapter;
    ContactCollection contactCollection;
    ArrayList<ContactModel> contactModels;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.onInitial(getContext());
        EventReceiver.getInstance().initEventContactListener(this::onEventReceived);
        this.syncContact();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.getItemPosition(0);
        isCanCall = true;
    }

    public static final String ACTION_OUTGOING_CALL = "ACTION_OUTGOING_CALL";
    final Handler handler = new Handler();
    private boolean isCanCall = true;
    Runnable runnable = () -> isCanCall = true;
    OnContactItemClickListener contactItemClickListener = contactModel -> {
        try {
            Timber.e("callType : " + contactModel.getCallType() + " & ID : " + contactModel.getContactId() + " & Phone : " + contactModel.getPhone() + " isCall = " + isCanCall);
            if (isCanCall) {
                isCanCall = false;
                OutGoingCallReceiver.isSOS = false;
                if (contactModel.getCallType().equalsIgnoreCase("C")) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("tel:" + contactModel.getPhone()));
                    getActivity().startActivity(intent);
                } else if (contactModel.getCallType().equalsIgnoreCase("V")) {
                    Timber.e("family : " + (!contactModel.getContactType().equalsIgnoreCase("family")));
                    if (!contactModel.getContactType().equalsIgnoreCase("family")) {
                        AllowCallingRequest callingRequest = new AllowCallingRequest();
                        callingRequest.setFromContactId(WearerInfoUtils.newInstance(getContext()).getImei(getContext()));
                        callingRequest.setToContactId(contactModel.getContactId());
                        presenter.requestCheckAllowCalling(callingRequest);
                    } else {
                        CallContact callContact = new CallContact();
                        callContact.setToContactId(contactModel.getContactId());
                        callContact.setFromContactId(WearerInfoUtils.newInstance(getContext()).getImei(getContext()));
                        callContact.setIsAutoAnswer("N");
                        presenter.sendCalling(callContact);
                    }
                }
                if (runnable != null)
                    handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 5000);
            }
        } catch (Exception ignore) {
            Timber.e(ignore.getMessage());
        }
    };

   /* public void openApp(ContactModel contactModel) {
        Intent i = new Intent(getContext(), VoIPActivity.class);
        i.putExtra("ID", contactModel.getContactId());
        i.putExtra("TYPE", "CALL");
        i.putExtra("NAME", contactModel.getName());
        i.putExtra("AVATAR", contactModel.getAvatar());
        i.putExtra("AVATAR_TYPE", contactModel.getAvatarType());
        i.putExtra("PHONE", contactModel.getPhone());
        i.putExtra("LANG", LanguageSetting.getLanguage());
    }*/

    public void notifyDataChangeMissCall() {
        Timber.e("notifyDataChangeMissCall()");
        if (contactCollection.getContactModelList() == null || contactCollection.getContactModelList().size() == 0)
            return;
        if (CombineObjectConstance.getInstance().isHaveMissCall())
            CombineObjectConstance.getInstance().setClearContactNotifyDataChange(true);
        if (mAdapter != null && CombineObjectConstance.getInstance().isHaveMissCall()) {
            Timber.e(" == > notifyDataSetChanged 1");
            mAdapter.notifyDataSetChanged();
        } else {
            if (CombineObjectConstance.getInstance().isClearContactNotifyDataChange()) {
                Timber.e(" == > notifyDataSetChanged 2");
                mAdapter.notifyDataSetChanged();
                CombineObjectConstance.getInstance().setClearContactNotifyDataChange(false);
            }
        }
    }

    private void onEventReceived(EventDataInfo eventDataInfo) {
        this.syncContact();
    }

    public void syncContact() {
        contactCollection = CombineObjectConstance.getInstance().getContactEntity().getContactCollection();
        if (contactCollection.getContactModelList() == null || contactCollection.getContactModelList().size() == 0) {
            noContact();
        } else {
            Timber.e("syncContact : Have data");
            haveContact();
            contactModels = new ArrayList<>();
            if (contactCollection.getContactModelList().get(0).getCallType().equalsIgnoreCase("V")) {
                for (ContactModel contact : contactCollection.getContactModelList())
                    if (!contact.getContactType().equals("other"))
                        contactModels.add(contact);
            } else
                contactModels.addAll(contactCollection.getContactModelList());
            CombineObjectConstance.getInstance().getContactEntity().getContactCollection();
            mVerticalViewPager.setAdapter(mAdapter = new ContactVerticalPagerAdapter(getChildFragmentManager(), contactModels, contactItemClickListener));
        }
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new ContactPresenterModule());
    }

    public void haveContact() {
        mVerticalViewPager.setVisibility(View.VISIBLE);
        tvNoContact.setVisibility(View.GONE);
    }

    public void noContact() {
        mVerticalViewPager.setVisibility(View.GONE);
        tvNoContact.setVisibility(View.VISIBLE);
    }

    interface OnContactItemClickListener {
        void onItemClickListener(ContactModel menuModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventReceiver.getInstance().removeContactSyncListener();
    }
}