/*
Copyright 2016 Nextzy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.pomohouse.launcher.base;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.pomohouse.launcher.BuildConfig;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefModel;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.events.NotificationMainIconDao;
import com.pomohouse.launcher.models.events.TimezoneEvent;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.utils.SoundPoolManager;
import com.pomohouse.launcher.utils.VibrateManager;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.languages.LocalizationActivity;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.library.networks.MetaDataNetwork;

import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_APP_TIMEZONE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_VOLUME_CODE;

/**
 * Created by Akexorcist on 1/7/2016 AD.
 */
public abstract class BaseLauncherActivity extends LocalizationActivity {
    private ObjectGraph activityGraph;
    protected Context mContext;
    protected INotificationManager notificationManager;
    protected ISettingManager settingManager;
    protected SoundPoolManager soundPoolManager;
    protected VibrateManager vibrateManager;

    protected boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception ignore) {
            return false;
        }
    }

    protected boolean isMobileDataConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        } catch (Exception ignore) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.font_style)).setFontAttrId(R.attr.fontPath).build());
        WearerInfoUtils.getInstance().setLanguage(getLanguage());
        ActivityContextor.getInstance().init(this);
        ActivityContextor.getInstance().initActivity(this);
        WearerInfoUtils.getInstance().setPomoVersion(BuildConfig.VERSION_NAME);
        mContext = this;
        List<Object> modules = getModules();
        if (modules != null) {
            activityGraph = ((POMOWatchApplication) getApplication()).createScopedGraph(modules.toArray());
            activityGraph.inject(this);
        }
        vibrateManager = VibrateManager.getInstance(this);
        soundPoolManager = SoundPoolManager.getInstance(this);
        setAutoTimezone();

    }

    public void onSetNotificationManager(INotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void onSetSettingManager(ISettingManager settingManager) {
        this.settingManager = settingManager;
    }

    public void setUpTimeZone(String timeZone) {
        try {
            onSetupAutoTimeZone(false);
            Settings.Global.getInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone(timeZone);
            EventDataInfo eventDataInfo = new EventDataInfo();
            eventDataInfo.setEventCode(EVENT_APP_TIMEZONE_CODE);
            TimezoneEvent newTimeZone = new TimezoneEvent();
            newTimeZone.setTimezone(timeZone);
            eventDataInfo.setContent(new Gson().toJson(timeZone));
            this.onSendEventToBroadcast(eventDataInfo);
        } catch (Exception ignore) {
        }
    }

    public void onSendEventToBroadcast(EventDataInfo eventDataInfo) {
        final Intent intent = new Intent(SEND_EVENT_UPDATE_INTENT, null);
        intent.putExtra(EVENT_STATUS_EXTRA, new MetaDataNetwork(0, "success"));
        intent.putExtra(EVENT_EXTRA, eventDataInfo);
        sendBroadcast(intent);
    }

    public void setAutoTimezone() {
        onSetupAutoTimeZone(true);
    }

    public void onSetupAutoTimeZone(boolean stateOn) {
        /*Intent dataOnIntent = new Intent("com.pomohouse.waffle.REQUEST_AUTO_TIMEZONE");
        dataOnIntent.putExtra("status", stateOn ? "on" : "off");
        sendBroadcast(dataOnIntent);*/
        //Timber.e("Set Timezone = " + stateOn);
        Settings.Global.putInt(getContentResolver(), Settings.Global.AUTO_TIME_ZONE, stateOn ? 1 : 0);
    }

    public void onBrightnessChanged(int level) {
        float backLightValue;
        if (level == 1) backLightValue = 5;
        else if (level == 2) backLightValue = 50;
        else backLightValue = 150;
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = backLightValue;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityGraph = null;
    }

    protected abstract List<Object> getModules();

    protected void configurationChanged() {
        if (settingManager != null) {
            SettingPrefModel miniSettingPrefModel = settingManager.getSetting();
            if (miniSettingPrefModel != null) {
                modifyVolumeChanged(miniSettingPrefModel);
                modifyBrightnessChanged(miniSettingPrefModel.getBrightLevel());
                setTimeout(miniSettingPrefModel.getScreenOffTimer());
            }
        }
    }

    protected void enableSilentMode() {
        try {
            modifyVolumeChanged(settingManager.getSetting());
            NotificationMainIconDao notificationMainIconDao = new NotificationMainIconDao();
            notificationMainIconDao.setShow(true);
            EventDataInfo eventDataInfo = new EventDataInfo();
            eventDataInfo.setContent(new Gson().toJson(notificationMainIconDao));
            eventDataInfo.setEventCode(EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE);
            this.onSendEventToBroadcast(eventDataInfo);
            eventDataInfo.setEventCode(EVENT_NOTIFICATION_VOLUME_CODE);
            this.onSendEventToBroadcast(eventDataInfo);
        } catch (Exception ignore) {
            Timber.e("" + ignore.getMessage());
        }
    }

    protected void disableSilentMode() {
        try {
            modifyVolumeChanged(settingManager.getSetting());
            NotificationMainIconDao notificationMainIconDao = new NotificationMainIconDao();
            notificationMainIconDao.setShow(settingManager.getSetting().getVolumeLevel() == 0);
            EventDataInfo eventDataInfo = new EventDataInfo();
            eventDataInfo.setContent(new Gson().toJson(notificationMainIconDao));
            eventDataInfo.setEventCode(EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE);
            this.onSendEventToBroadcast(eventDataInfo);
            eventDataInfo.setEventCode(EVENT_NOTIFICATION_VOLUME_CODE);
            this.onSendEventToBroadcast(eventDataInfo);
        } catch (Exception ignore) {
            Timber.e("" + ignore.getMessage());
        }
    }

    public void onUpdateMuteNotification(boolean isMute) {
        if (notificationManager != null) {
            NotificationPrefModel notificationPrefModel = notificationManager.getNotification();
            if (notificationPrefModel != null) notificationPrefModel.setHaveMute(isMute);
            notificationManager.addNotification(notificationPrefModel);
        }
    }

    public void modifyBrightnessChanged(int level) {
        int backLightValue;
        if (level == 1) backLightValue = 2;
        else if (level == 2) backLightValue = 50;
        else backLightValue = 150;
        {
            ContentResolver cResolver = getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, backLightValue);
        }
        {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.screenBrightness = backLightValue;
            getWindow().setAttributes(layoutParams);
        }
    }

    /**
     * set screen off timeout
     *
     * @param screenOffTimeout int Millisecond
     */
    private void setTimeout(int screenOffTimeout) {
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, screenOffTimeout * 1000);
    }

    public void modifyVolumeChanged(SettingPrefModel settingPrefModel) {
        int level = settingPrefModel.getVolumeLevel();
        int volume = 15;
        if (level == 1) volume = 2;
        else if (level == 2) volume = 5;
        this.onUpdateMuteNotification(true);
        soundPoolManager = SoundPoolManager.getInstance(this);
        if (soundPoolManager == null) return;
        if (CombineObjectConstance.getInstance().isInClassTime()) {
            soundPoolManager.silentMode(this);
            return;
        }
        if (settingPrefModel.isSilentMode()) {
            soundPoolManager.silentMode(this);
        } else {
            if (level == 0) {
                soundPoolManager.vibrateOnly(this);
            } else {
                this.onUpdateMuteNotification(false);
                soundPoolManager.vibrateAndSound(this, volume);
            }
            if (CombineObjectConstance.getInstance().isAutoAnswer())
                soundPoolManager.ringingMode(this, volume);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }
}
