package com.pomohouse.launcher.fragment.mini_setting;

/**
 * Created by Admin on 8/18/16 AD.
 */
public interface IMiniSettingView {
    void onBrightnessChange(int level, int drawable);

    void onVolumeChange(int level, int drawable);

    void setLevelSound(String level);

    void setBrightnessLevel(String level);

    void menuItemSelected(MiniSettingEnum menuType, String typeControl, String levelStr, int drawable);
}
