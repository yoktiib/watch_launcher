package com.pomohouse.launcher.fragment.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.di.module.FriendPresenterModule;
import com.pomohouse.launcher.fragment.friends.presenter.IFriendPresenter;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class FriendFragment extends BaseFragment implements IFriendView {
    @Inject
    IFriendPresenter presenter;
    @BindView(R.id.ivMenuBff)
    ImageView ivMenuBff;

    @OnClick(R.id.ivBFF)
    void addFriend() {
        openApp("com.pomohouse.bff");
    }

    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bff, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new FriendPresenterModule(this));
    }
}