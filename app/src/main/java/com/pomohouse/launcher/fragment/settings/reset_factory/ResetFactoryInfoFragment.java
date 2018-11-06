package com.pomohouse.launcher.fragment.settings.reset_factory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;

import java.util.List;

import butterknife.OnClick;

/**
 * Created by Admin on 2/3/2017 AD.
 */

public class ResetFactoryInfoFragment extends BaseFragment {

    public static ResetFactoryInfoFragment newInstance() {
        ResetFactoryInfoFragment fragment = new ResetFactoryInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected List<Object> injectModules() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_info_resetfactory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btnReset)
    void onClickReset() {
        showAlertDialogFragment(ResetFactoryConfirmFragment.newInstance(), ResetFactoryConfirmFragment.class.getName());
    }
}
