package com.pomohouse.launcher.main.presenter;

import android.app.Activity;
import android.content.Context;

import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.api.requests.UpdateFirebaseRequest;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.in_class_mode.IInClassModePrefManager;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.sleep_time.ISleepTimeManager;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.models.AlarmModel;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.base.interfaces.presenter.IBaseRequestStatePresenter;

import java.util.ArrayList;

/**
 * Created by Admin on 8/18/16 AD.
 */
public interface ILauncherPresenter extends IBaseRequestStatePresenter {
    void requestInitialDevice();

    void initDevice();

    void initContactListContentProvider(Activity acc);

    void initMessageContentProvider(Activity launcherActivity);

    void initContactCallsProvider(Activity acc);

    void requestShutdownDevice();

    void readThemeFromAsset();

    void requestSOS();

    void timeTickControl();

    void provideSettingManager(ISettingManager themeManager);

    void provideNotificationManager(INotificationManager notificationManager);

    void provideFitnessManager(IFitnessPrefManager fitnessPrefManager);

    void provideSleepTimeManager(ISleepTimeManager sleepTimePrefManager);

    void provideInClassModeManager(IInClassModePrefManager inClassModeManager);

    void provideEventManager(IEventPrefManager iEventPrefManager);

    void lockScreenDevice();

    void provideThemeManager(IThemePrefManager themeManager);

    void alarmTime(AlarmModel alarm);

    void batteryCharging();

    void batteryUnCharging();

    void eventReceiver(EventDataInfo eventDataInfo);

    void timeZoneChange();

    void updateScheduler(ArrayList<AlarmModel> alarmModelList);

    void updateFCMTokenManager(String fcmToken);

    void onStart();

    void onStop();

    void addNewWatchFaceToSetting(Context mContext, boolean open);

}
