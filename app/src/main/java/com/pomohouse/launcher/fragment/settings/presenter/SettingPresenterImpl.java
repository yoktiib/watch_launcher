package com.pomohouse.launcher.fragment.settings.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.launcher.fragment.settings.ISettingView;
import com.pomohouse.library.base.BaseRetrofitPresenter;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class SettingPresenterImpl extends BaseRetrofitPresenter implements ISettingPresenter {
    ISettingView view;

    public SettingPresenterImpl(ISettingView settingView) {
        this.view = settingView;

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

}
