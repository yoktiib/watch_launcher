package com.pomohouse.launcher.fragment.mini_setting.presenter;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.fragment.mini_setting.IMiniSettingView;
import com.pomohouse.launcher.fragment.mini_setting.MiniSettingEnum;
import com.pomohouse.launcher.fragment.mini_setting.interactor.IMiniSettingInteractor;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.library.base.BaseRetrofitPresenter;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class MiniSettingPresenterImpl extends BaseRetrofitPresenter implements IMiniSettingPresenter {
    private IMiniSettingView view;
    private IMiniSettingInteractor interactor;
    private int brightness = 3;
    private int sound = 3;
    private Resources resources;

    public MiniSettingPresenterImpl(IMiniSettingView settingView, IMiniSettingInteractor interactor) {
        this.view = settingView;
        this.interactor = interactor;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        try {
            if (param == null)
                return;
            if (param.length < 1)
                return;
            if (param[0] == null)
                return;
            if (param[0] instanceof Activity) {
                Activity mContext = (Activity) param[0];
                resources = mContext.getResources();
            }
        } catch (Exception ignore) {

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void addButtonClicked(MiniSettingEnum type) {
        if (type == MiniSettingEnum.BRIGHTNESS) {
            if (brightness < 3)
                brightness++;
            view.onBrightnessChange(brightness, brightnessLevelConvertToDrawable(brightness));
        } else if (type == MiniSettingEnum.VOLUME) {
            if (sound < 3)
                sound++;
            view.onVolumeChange(sound, volumeLevelConvertToDrawable(sound));
        }
    }

    @Override
    public void reduceButtonClicked(MiniSettingEnum type) {
        if (type == MiniSettingEnum.BRIGHTNESS) {
            if (brightness > 1)
                brightness--;
            view.onBrightnessChange(brightness, brightnessLevelConvertToDrawable(brightness));
        } else if (type == MiniSettingEnum.VOLUME) {
            if (sound > 0)
                sound--;
            view.onVolumeChange(sound, volumeLevelConvertToDrawable(sound));
        }
    }

    @Override
    public void levelSoundToText(int level) {
        view.setLevelSound(calculateSoundText(level));
    }

    @Override
    public void levelBrightnessToText(int level) {
        view.setBrightnessLevel(String.valueOf(level));
    }

    @Override
    public void onControlMenuSelected(MiniSettingEnum menuType, SettingPrefModel miniSetting) {
        brightness = miniSetting.getBrightLevel();
        sound = miniSetting.getVolumeLevel();
        String typeControl = "";
        String levelStr = "";
        if (menuType == MiniSettingEnum.VOLUME) {
            typeControl = resources.getString(R.string.text_volume);
            if (miniSetting.isSilentMode())
                levelStr = calculateSoundText(-1);
            else
                levelStr = calculateSoundText(sound);
        } else if (menuType == MiniSettingEnum.BRIGHTNESS) {
            typeControl = resources.getString(R.string.text_brightness);
            levelStr = String.valueOf(brightness);
        }/* else {
            typeControl = resources.getString(R.string.text_setting);
            levelStr = "";
        }*/
        if (menuType == MiniSettingEnum.BRIGHTNESS) {
            view.menuItemSelected(menuType, typeControl, levelStr, brightnessLevelConvertToDrawable(brightness));
        } else if (menuType == MiniSettingEnum.VOLUME) {
            if (miniSetting.isSilentMode())
                view.menuItemSelected(menuType, typeControl, levelStr, R.drawable.setting_volume_mute);
            else
                view.menuItemSelected(menuType, typeControl, levelStr, volumeLevelConvertToDrawable(sound));
        }
    }

    private int volumeLevelConvertToDrawable(int level) {
        switch (level) {
            case 0:
                return R.drawable.setting_volume_mute;
            case 1:
                return R.drawable.setting_volume_low;
            case 2:
                return R.drawable.setting_volume_medium;
            case 3:
                return R.drawable.setting_volume_high;
            default:
                return R.drawable.setting_volume_mute;
        }
    }

    private int brightnessLevelConvertToDrawable(int level) {

        switch (level) {
            case 1:
                return R.drawable.setting_brightness_low;
            case 2:
                return R.drawable.setting_brightness_medium;
            case 3:
                return R.drawable.setting_brightness_high;
            default:
                return R.drawable.setting_brightness_low;
        }
    }

    private String calculateSoundText(int level) {
        if (level != -1)
            return String.valueOf(calculateVolumeText(level));
        else
            return resources.getString(R.string.text_volume_silent);
    }

    private String calculateVolumeText(int level) {
        if (level == 0)
            return resources.getString(R.string.text_volume_mute);
        else if (level == 1)
            return resources.getString(R.string.text_volume_low);
        else if (level == 2)
            return resources.getString(R.string.text_volume_medium);
        else
            return resources.getString(R.string.text_volume_high);
    }

}
