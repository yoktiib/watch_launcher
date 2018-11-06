package com.pomohouse.launcher.fragment.mini_setting.presenter;

import com.pomohouse.launcher.fragment.mini_setting.MiniSettingEnum;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

/**
 * Created by Admin on 8/18/16 AD.
 */
public interface IMiniSettingPresenter extends IBaseRequestStatePresenter {
    void addButtonClicked(MiniSettingEnum type) throws Exception;

    void reduceButtonClicked(MiniSettingEnum type) throws Exception;

    void levelSoundToText(int level) throws Exception;

    void levelBrightnessToText(int level) throws Exception;

    void onControlMenuSelected(MiniSettingEnum soundType, SettingPrefModel miniSetting) throws Exception;
}
