package com.pomohouse.launcher.manager.settings;

/**
 * Created by Admin on 9/8/16 AD.
 */
public interface ISettingManager {

    void addMiniSetting(SettingPrefModel miniSetting);

    SettingPrefModel getSetting();

    void removeMiniSetting();
}
