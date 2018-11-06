package com.pomohouse.launcher.fragment.settings.reset_factory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseDialogFragment;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;

import butterknife.OnClick;

/**
 * Created by Admin on 2/3/2017 AD.
 */

public class ResetFactoryConfirmFragment extends BaseDialogFragment {

    public static ResetFactoryConfirmFragment newInstance() {
        ResetFactoryConfirmFragment fragment = new ResetFactoryConfirmFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm_resetfactory, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null)
            window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btnConfirm)
    void onClickConfirm() {
        SettingPrefManager settingPrefManager = new SettingPrefManager(getContext());
        SettingPrefModel settingPrefModel = settingPrefManager.getSetting();
        if (settingPrefModel != null) {
            settingPrefModel.setFirstTime(true);
            settingPrefManager.addMiniSetting(settingPrefModel);
        }
        final Intent resetFactory = new Intent("com.pomohouse.waffle.REQUEST_RESET_FACTORY");
        getContext().sendBroadcast(resetFactory);
        // TODO Reset Factory Not Implement
    }

    @OnClick(R.id.btnCancel)
    void onClickCancel() {
        getActivity().finish();
    }
}
