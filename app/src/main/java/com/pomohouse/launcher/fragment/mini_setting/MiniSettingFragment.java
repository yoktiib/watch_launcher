package com.pomohouse.launcher.fragment.mini_setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pomohouse.launcher.R;
import com.pomohouse.launcher.base.BaseFragment;
import com.pomohouse.launcher.di.module.MiniSettingPresenterModule;
import com.pomohouse.launcher.fragment.mini_setting.presenter.IMiniSettingPresenter;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefModel;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.utils.SoundPoolManager;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Fragment to manage the top page of the 5 pages application navigation (top, center, bottom, left, right).
 */
public class MiniSettingFragment extends BaseFragment implements IMiniSettingView {
    public static final String MENU_SELECTED = "MENU_SELECTED";
    @Inject
    IMiniSettingPresenter presenter;
    @Inject
    ISettingManager miniSettingManager;
    @Inject
    INotificationManager notificationManager;


    @BindView(R.id.ibSettingControl)
    AppCompatImageButton ibSettingControl;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvLevel)
    TextView tvLevel;
    @BindView(R.id.boxUpDown)
    RelativeLayout boxUpDown;
    public AudioManager audioManage;
    private MiniSettingEnum type = MiniSettingEnum.VOLUME;

    public static MiniSettingFragment newInstance() {
        MiniSettingFragment fragment = new MiniSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static MiniSettingFragment newInstance(int i) {
        MiniSettingFragment fragment = new MiniSettingFragment();
        Bundle args = new Bundle();
        args.putInt(MENU_SELECTED, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            presenter.onControlMenuSelected(type, miniSettingManager.getSetting());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            audioManage = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            presenter.onInitial(getContext());
            if (getArguments().getInt(MENU_SELECTED, 1) == 0) {
                type = MiniSettingEnum.VOLUME;
                presenter.onControlMenuSelected(MiniSettingEnum.VOLUME, miniSettingManager.getSetting());
            } else {
                type = MiniSettingEnum.BRIGHTNESS;
                presenter.onControlMenuSelected(MiniSettingEnum.BRIGHTNESS, miniSettingManager.getSetting());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<Object> injectModules() {
        return Collections.singletonList(new MiniSettingPresenterModule(this));
    }

    @OnClick(R.id.btnAdd)
    void onAddClick() {
        try {
            presenter.addButtonClicked(type);
        } catch (Exception ignore) {
        }
    }

    @OnClick(R.id.btnReduce)
    void onReduceClick() {
        try {
            presenter.reduceButtonClicked(type);
        } catch (Exception ignore) {
        }
    }

    @Override
    public void menuItemSelected(MiniSettingEnum position, String typeControl, String levelStr, int drawable) {
        type = position;
        boxUpDown.setVisibility(View.VISIBLE);
        ibSettingControl.setImageResource(drawable);
        tvTitle.setText(typeControl);
        tvLevel.setText(levelStr);
    }

    @Override
    public void onBrightnessChange(int level, int drawable) {
        try {
            presenter.levelBrightnessToText(level);
            ibSettingControl.setImageResource(drawable);
            modifyBrightnessChanged(level);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVolumeChange(int level, int drawable) {
        try {
            if (miniSettingManager.getSetting().isSilentMode()) {
                ibSettingControl.setImageResource(R.drawable.setting_volume_mute);
                return;
            }
            presenter.levelSoundToText(level);
            ibSettingControl.setImageResource(drawable);
            SettingPrefModel miniSettingPrefModel = miniSettingManager.getSetting();
            if (miniSettingPrefModel == null)
                miniSettingPrefModel = new SettingPrefModel();
            miniSettingPrefModel.setVolumeLevel(level);
            miniSettingManager.addMiniSetting(miniSettingPrefModel);
            modifyVolumeChanged(level);
            onUpdateMuteNotification(level == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onUpdateMuteNotification(boolean isMute) {
        if (notificationManager != null) {
            NotificationPrefModel notificationPrefModel = notificationManager.getNotification();
            if (notificationPrefModel != null)
                notificationPrefModel.setHaveMute(isMute);
            notificationManager.addNotification(notificationPrefModel);
        }
    }

    public void modifyBrightnessChanged(int level) {
        SettingPrefModel miniSettingPrefModel = miniSettingManager.getSetting();
        if (miniSettingPrefModel == null)
            miniSettingPrefModel = new SettingPrefModel();
        miniSettingPrefModel.setBrightLevel(level);
        miniSettingManager.addMiniSetting(miniSettingPrefModel);
        int backLightValue;
        if (level == 1)
            backLightValue = 5;
        else if (level == 2)
            backLightValue = 50;
        else
            backLightValue = 150;
        {
            ContentResolver cResolver = getContext().getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, backLightValue);
        }
        {
            WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
            layoutParams.screenBrightness = backLightValue;
            getActivity().getWindow().setAttributes(layoutParams);
        }
    }

    @SuppressLint("DefaultLocale")
    public void modifyVolumeChanged(int level) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        int volume = 15;
        if (level == 1)
            volume = 5;
        else if (level == 2)
            volume = 10;
        this.onUpdateMuteNotification(true);
        SoundPoolManager soundPoolManager = SoundPoolManager.getInstance(activity);
        if (soundPoolManager == null)
            return;
        if (CombineObjectConstance.getInstance().isInClassTime()) {
            soundPoolManager.silentMode(activity);
            return;
        }
        if (CombineObjectConstance.getInstance().isSilentMode()) {
            soundPoolManager.silentMode(activity);
        } else {
            if (level == 0) {
                soundPoolManager.vibrateOnly(activity);
            } else {
                this.onUpdateMuteNotification(false);
                soundPoolManager.vibrateAndSound(activity, volume);
            }
            if (CombineObjectConstance.getInstance().isAutoAnswer())
                soundPoolManager.ringingMode(activity, volume);
        }
    }

    @Override
    public void setLevelSound(String title) {
        tvLevel.setText(title);
    }

    @Override
    public void setBrightnessLevel(String level) {
        tvLevel.setText(level);
    }
}