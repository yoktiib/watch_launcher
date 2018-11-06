package com.pomohouse.bff.fragment.result;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pomohouse.bff.R;
import com.pomohouse.bff.base.BaseFragment;
import com.pomohouse.bff.dao.EventDataInfo;
import com.pomohouse.bff.dao.ResponseFriend;
import com.pomohouse.bff.dao.WearerInfo;
import com.pomohouse.bff.di.presenter.CancelFriendForeverPresenterModule;
import com.pomohouse.bff.di.presenter.ResultFriendPresenterModule;
import com.pomohouse.bff.fragment.ICancelView;
import com.pomohouse.bff.fragment.presenter.IClosePresenter;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by sirawit on 8/28/16 AD.
 */
public class ResultFriendFragment extends BaseFragment implements ICancelView {
    public static final int EVENT_BFF_CODE = 1141;
    public final static String RECEIVE_FROM_OTHER_APP = "com.pomohouse.service.RECEIVE_FROM_OTHER_APP";
    public final static String EVENT_STATUS_EXTRA = "EVENT_STATUS_EXTRA";
    public final static String EVENT_EXTRA = "EVENT_EXTRA";

    @Inject
    IClosePresenter presenter;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    ResponseFriend responseFriend;

    public static ResultFriendFragment newInstance(ResponseFriend responseFriend) {
        ResultFriendFragment fragment = new ResultFriendFragment();
        Bundle args = new Bundle();
        fragment.responseFriend = responseFriend;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WearerInfo wearerInfo = new WearerInfo();
        wearerInfo.setImei(WearerInfoUtils.getInstance().getImei());
        presenter.onInitial(wearerInfo);
        if (responseFriend != null) {
            String title;
            String desc;
            if (responseFriend.getF_status().equalsIgnoreCase("Y")) {
                title = getString(R.string.yuy_friend_title);
                desc = getString(R.string.yuy_friend_desc);
            } else {
                title = getString(R.string.sorry_friend_title);
                desc = getString(R.string.sorry_friend_desc);
            }
            tvTitle.setText(title);
            tvDesc.setText(desc);
        }
        sendBoardCastContact();
    }

    private void sendBoardCastContact() {
        EventDataInfo event = new EventDataInfo();
        event.setEventCode(EVENT_BFF_CODE);
        event.setEventDesc("success");
        final Intent intent = new Intent(RECEIVE_FROM_OTHER_APP, null);
        intent.putExtra(EVENT_STATUS_EXTRA, new MetaDataNetwork(0, "success"));
        intent.putExtra(EVENT_EXTRA, new Gson().toJson(event));
        if (getContext() != null)
            getContext().sendBroadcast(intent);
    }

    @OnClick(R.id.card_view)
    void onClickOK() {
        presenter.requestCancelRequestFriend();
        getActivity().finish();
    }

    @Override
    protected List<Object> injectModules() {
        return Arrays.asList(new ResultFriendPresenterModule(), new CancelFriendForeverPresenterModule(this));
    }

    @Override
    public void onDestroyView() {
        presenter.requestCancelRequestFriend();
        presenter.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void successCancel() {
        getActivity().finish();
    }

    @Override
    public void failureCancel() {
        getActivity().finish();
    }
}
