package com.pomohouse.launcher.manager.settings;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.launcher.manager.DataManagerConstant;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class SettingPrefManager extends AbstractSharedPreferences implements ISettingManager {
    private final String MINI_SETTING = DataManagerConstant.KEY_MINI_SETTING;

    public SettingPrefManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addMiniSetting(SettingPrefModel miniSetting) {
        writeString(MINI_SETTING, new Gson().toJson(miniSetting));
    }

    @Override
    public SettingPrefModel getSetting() {
        SettingPrefModel miniSettingModel = new Gson().fromJson(readString(MINI_SETTING), SettingPrefModel.class);
        if (miniSettingModel == null)
            addMiniSetting(miniSettingModel = new SettingPrefModel());
        return miniSettingModel;
    }

    @Override
    public void removeMiniSetting() {
        removeKey(MINI_SETTING);
    }
}
