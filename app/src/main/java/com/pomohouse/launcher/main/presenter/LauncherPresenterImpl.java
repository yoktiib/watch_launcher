package com.pomohouse.launcher.main.presenter;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.InitDeviceRequest;
import com.pomohouse.launcher.api.requests.TimezoneUpdateRequest;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmDatabase;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmItem;
import com.pomohouse.launcher.content_provider.POMOContentProvider;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.main.ILauncherView;
import com.pomohouse.launcher.main.OnLauncherCallbackListener;
import com.pomohouse.launcher.main.interactor.ILauncherInteractor;
import com.pomohouse.launcher.main.interactor.listener.OnEventListener;
import com.pomohouse.launcher.main.interactor.listener.OnInitialDeviceListener;
import com.pomohouse.launcher.main.interactor.listener.OnSOSListener;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.in_class_mode.IInClassModePrefManager;
import com.pomohouse.launcher.manager.in_class_mode.InClassModePrefModel;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefModel;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.launcher.manager.sleep_time.ISleepTimeManager;
import com.pomohouse.launcher.manager.sleep_time.SleepTimePrefModel;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.models.AlarmModel;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.EventDataListModel;
import com.pomohouse.launcher.models.LockScreenEnum;
import com.pomohouse.launcher.models.PinCodeModel;
import com.pomohouse.launcher.models.contacts.CallDao;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.models.contacts.ContactModel;
import com.pomohouse.launcher.models.events.BatteryChargerEvent;
import com.pomohouse.launcher.models.events.NotificationMainIconDao;
import com.pomohouse.launcher.models.settings.BrightnessTimeOutDao;
import com.pomohouse.launcher.models.settings.LanguageDao;
import com.pomohouse.launcher.models.settings.LocationTimerDao;
import com.pomohouse.launcher.models.settings.SilentDao;
import com.pomohouse.launcher.models.settings.TimeZoneDao;
import com.pomohouse.launcher.models.settings.WearerStatusDao;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.base.BaseRetrofitPresenter;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import timber.log.Timber;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_LOCK_DEFAULT_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_LOCK_IN_CLASS_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UNLOCK_IN_CLASS_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.utils.EventConstant.EventAlarmService.EVENT_ALARM_SET_UP_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventAlarmService.EVENT_IN_CLASS_MODE_SET_UP_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventAlarmService.EVENT_SLEEP_TIME_SET_UP_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_GROUP_CHAT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventChat.EVENT_MESSAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventContact.EVENT_BFF_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventContact.EVENT_SYNC_CONTACT;
import static com.pomohouse.launcher.utils.EventConstant.EventContact.EVENT_UPDATE_LOCAL_CONTACT;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_APP_TIMEZONE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_BRIGHTNESS_TIME_OUT_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_LANGUAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_LOCATION_REQUEST_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_RESET_FACTORY_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_RESTART_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_SHUTDOWN_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_SILENT_MODE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_TIMER_LOCATION_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_TIMEZONE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventDeviceInfo.EVENT_WEARER_STATUS_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_ALARM_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_BATTERY_CHARGING_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MESSAGE_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_REFRESH_LOCATION_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_SOS_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UPDATE_LOCATION_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventPair.EVENT_GET_PAIR_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventPair.EVENT_PAIR_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventPair.EVENT_UN_PAIR_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_CALL_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_NOTIFICATION_MESSAGE_CODE;

/**
 * Created by Admin on 8/18/16 AD.
 */
public class LauncherPresenterImpl extends BaseRetrofitPresenter implements ILauncherPresenter, OnInitialDeviceListener, OnLauncherCallbackListener, OnSOSListener, OnEventListener {
    private ILauncherView view;
    private ILauncherInteractor interactor;
    private IThemePrefManager themeManager;
    private LockScreenEnum lockBy = LockScreenEnum.NONE;
    private boolean isInClassModeEnable = false, isSleepMode = false;
    private IInClassModePrefManager inClassModeManager;
    private ISettingManager iSettingManager;
    private IFitnessPrefManager iFitnessPrefManager;
    private INotificationManager iNotificationManager;
    private IEventPrefManager iEventPrefManager;
    private ISleepTimeManager iSleepTimeManager;
    private InClassModePrefModel inClassModePrefModel;
    private SleepTimePrefModel sleepTimePrefModel;
    public static final String THEME_SETTING = "theme_open_close_setting";
    public static final String RILAKKUMA_THEME_STATUS = "rilakkuma_theme_status";

    /**
     * Start Of Event Variable
     */
    // Extras
    public final static String EVENT_STATUS_EXTRA = "EVENT_STATUS_EXTRA";
    public final static String EVENT_EXTRA = "EVENT_EXTRA";


    public LauncherPresenterImpl(ILauncherView mainView, ILauncherInteractor iMainInteractor) {
        view = mainView;
        this.interactor = iMainInteractor;
        isInClassModeEnable = false;
    }

    @Override
    public void onInitial(Object... param) {
        super.onInitial(param);
        view.initializeServiceAndReceiver();
        this.setUpDevice();
        view.startLocation();
    }

    @Override
    public void onStart() {
        if (lockBy != LockScreenEnum.NONE) view.setUpConfigurationWhenLockScreen();
    }

    @Override
    public void provideSettingManager(ISettingManager iSettingManager) {
        this.iSettingManager = iSettingManager;
    }

    @Override
    public void provideNotificationManager(INotificationManager notificationManager) {
        this.iNotificationManager = notificationManager;
    }

    @Override
    public void provideFitnessManager(IFitnessPrefManager fitnessPrefManager) {
        this.iFitnessPrefManager = fitnessPrefManager;
    }

    @Override
    public void provideEventManager(IEventPrefManager eventPrefManager) {
        this.iEventPrefManager = eventPrefManager;
    }

    @Override
    public void provideThemeManager(IThemePrefManager _themeManager) {
        this.themeManager = _themeManager;
        this.readThemeFromAsset();
    }

    @Override
    public void provideInClassModeManager(IInClassModePrefManager _inClassModeManager) {
        this.inClassModeManager = _inClassModeManager;
        inClassModePrefModel = inClassModeManager.getInClassMode();
    }

    @Override
    public void provideSleepTimeManager(ISleepTimeManager sleepTimePrefManager) {
        this.iSleepTimeManager = sleepTimePrefManager;
        sleepTimePrefModel = iSleepTimeManager.getSleepTime();
    }

    @Override
    public void onStop() {
    }

    @Override
    public void addNewWatchFaceToSetting(Context mContext, boolean open) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(THEME_SETTING, Activity.MODE_PRIVATE).edit();
        editor.putInt(RILAKKUMA_THEME_STATUS, open ? 1 : 0);
        editor.apply();
    }

    @Override
    public void requestInitialDevice() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) ActivityContextor.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            TimeZone tz = TimeZone.getDefault();
            String btName = "", btMacAddress = "";
            if (mBluetoothAdapter != null) {
                btMacAddress = mBluetoothAdapter.getAddress();
                btName = mBluetoothAdapter.getName();
            }
            InitDeviceRequest deviceRequest = new InitDeviceRequest();
            deviceRequest.setImei(WearerInfoUtils.getInstance().getImei(AppContextor.getInstance().getContext()));
            deviceRequest.setFirmwareVersion(Build.DISPLAY);
            if (iSettingManager != null && iSettingManager.getSetting() != null) {
                String token = iSettingManager.getSetting().getFCMToken();
                if (token != null && !token.isEmpty()) deviceRequest.setFireBaseWatchToken(token);
            }

            deviceRequest.setLauncherVersion(WearerInfoUtils.getInstance().getPomoVersion());
            deviceRequest.setMacAddress(btMacAddress);
            deviceRequest.setBTName(btName);
            if (telephonyManager != null) {
                deviceRequest.setSimOperator(telephonyManager.getSimOperator());
                deviceRequest.setSimOperatorName(telephonyManager.getSimOperatorName());
                if (ActivityCompat.checkSelfPermission(AppContextor.getInstance().getContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                    deviceRequest.setSimSerialNumber(telephonyManager.getSimSerialNumber());
            }
            if (tz != null) {
                Timber.i("Timezone :: " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezone id :: " + tz.getID());
                deviceRequest.setTimeZone(tz.getID());
            }
            interactor.callInitialDevice(deviceRequest, this);
        } catch (Exception ignore) {

        }
    }

    @Override
    public void onCallEventSuccess(MetaDataNetwork metaData, EventDataListModel data) {
        for (EventDataInfo event : data.getDataInfoList()) {
            sendEventIntent(event, metaData);
            insertEventContentProvider(event);
        }
        if (iEventPrefManager != null) iEventPrefManager.removeEvent();
    }

    @Override
    public void onCallEventFailure(MetaDataNetwork metaDataNetwork) {

    }

    private void sendEventIntent(final EventDataInfo eventData, MetaDataNetwork metaDataNetwork) {
        if (eventData != null) {
            final Intent intent = new Intent(SEND_EVENT_UPDATE_INTENT, null);
            intent.putExtra(EVENT_STATUS_EXTRA, metaDataNetwork);
            intent.putExtra(EVENT_EXTRA, eventData);
            view.onSendEventToBroadcast(eventData);
        }
    }

    private void insertEventContentProvider(EventDataInfo event) {
        if (AppContextor.getInstance().getContext() != null) {
            ContentValues values = new ContentValues();
            values.put(POMOContract.EventEntry.EVENT_ID, event.getEventId());
            values.put(POMOContract.EventEntry.EVENT_CODE, event.getEventCode());
            values.put(POMOContract.EventEntry.EVENT_TYPE, event.getEventType());
            values.put(POMOContract.EventEntry.SENDER, event.getSenderId());
            values.put(POMOContract.EventEntry.RECEIVE, event.getReceiveId());
            values.put(POMOContract.EventEntry.SENDER_INFO, event.getSenderInfo());
            values.put(POMOContract.EventEntry.RECEIVE_INFO, event.getReceiverInfo());
            values.put(POMOContract.EventEntry.CONTENT, event.getContent());
            values.put(POMOContract.EventEntry.STATUS, event.getStatus());
            values.put(POMOContract.EventEntry.TIME_STAMP, event.getTimeStamp());
            AppContextor.getInstance().getContext().getContentResolver().insert(POMOContract.EventEntry.CONTENT_URI, values);
        }
    }

    @Override
    public void timeTickControl() {
        this.setUpDevice();
    }

    private int timeTickCount = 0;

    private void setUpDevice() {
        this.validateInClassMode();
        this.validateSleepMode();
        /*if (!isSleepMode)
            view.startLocation();*/
        /*timeTickCount++;
        if (timeTickCount % 3 == 0) {
            timeTickCount = 0;

        }*/
    }

    /**
     * In Class Mode Functional
     * Start Validate
     * In Class Mode
     */

    private void validateSleepMode() {
        try {
            if (sleepTimePrefModel == null || sleepTimePrefModel.getBegin() == null || sleepTimePrefModel.getEnd() == null)
                return;
            Timber.e(sleepTimePrefModel.getBegin() + "-" + sleepTimePrefModel.getEnd() + " = " + sleepTimePrefModel.isSleepTime());
            if (!sleepTimePrefModel.isSleepTimeOn() || !sleepTimePrefModel.isTodayHaveSleepTime() || !sleepTimePrefModel.isSleepTime()) {
                this.checkSleepTimeIsOn();
                return;
            }
            if (!isSleepMode) this.onSleepModeEnable();
        } catch (Exception ignore) {
        }
    }

    private void checkSleepTimeIsOn() {
        if (isSleepMode) onSleepModeDisable();
    }

    private void onSleepModeDisable() {
        Timber.e("Sleep End");
        view.startLocation();
        isSleepMode = false;
    }

    private void onSleepModeEnable() {
        Timber.e("Sleep Start");
        isSleepMode = true;
        view.stopLocation();
    }

    /**
     * In Class Mode Functional
     * Start Validate
     * In Class Mode
     */
    private void validateInClassMode() {
        try {
            if (inClassModePrefModel == null || inClassModePrefModel.getBegin() == null || inClassModePrefModel.getEnd() == null)
                return;
            Timber.e(inClassModePrefModel.getBegin() + "-" + inClassModePrefModel.getEnd() + " = " + inClassModePrefModel.isInClassTime());
            if (!inClassModePrefModel.isInClassOn() || !inClassModePrefModel.isTodayHaveInClass() || !inClassModePrefModel.isInClassTime()) {
                this.checkInClassIsOn();
                return;
            }
            if (!isInClassModeEnable) this.onTimeInClassModeEnable();
        } catch (Exception ignore) {
        }
    }

    private void checkInClassIsOn() {
        if (isInClassModeEnable) onTimeInClassModeDisable();
    }

    private void onTimeInClassModeDisable() {
        Timber.e("InClass End");
        view.onWakeScreen();
        isInClassModeEnable = false;
        CombineObjectConstance.getInstance().setInClassTime(false);
        view.setUpInClassModeDisable();
        lockBy = LockScreenEnum.LOCK_SCREEN_DEFAULT;
        view.onSendFilterBroadcast(SEND_EVENT_UNLOCK_IN_CLASS_INTENT);
    }

    private void onTimeInClassModeEnable() {
        Timber.e("InClass Start");
        view.onWakeScreen();
        isInClassModeEnable = true;
        CombineObjectConstance.getInstance().setInClassTime(true);
        view.setUpInClassModeEnable();
        lockBy = LockScreenEnum.LOCK_SCREEN_WITH_IN_CLASS_MODE;
        view.onSendFilterBroadcast(SEND_EVENT_LOCK_IN_CLASS_INTENT);
    }

    @Override
    public void lockScreenDevice() {
        if (isInClassModeEnable) return;
        lockBy = LockScreenEnum.LOCK_SCREEN_DEFAULT;
        view.onSendFilterBroadcast(SEND_EVENT_LOCK_DEFAULT_INTENT);
    }

    @Override
    public void eventReceiver(EventDataInfo _eventData) {
        try {
            switch (_eventData.getEventCode()) {
                case EVENT_GET_PAIR_CODE:
                    PinCodeModel pinCodeModel = new Gson().fromJson(_eventData.getContent(), PinCodeModel.class);
                    if (pinCodeModel != null) view.openPinCodeView(pinCodeModel.getCode());
                    break;
                case EVENT_ALARM_SET_UP_CODE:
                    try {
                        this.setUpAlarm(_eventData);
                        view.onAlarmEventReceived();
                        view.setupSuccess();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                /**
                 * In Class Mode Event
                 */
                case EVENT_SLEEP_TIME_SET_UP_CODE:
                    this.setUpSleepTimeMode(_eventData);
                    view.sleepTimeSetUpEventReceived(_eventData);
                    view.setupSuccess();
                    break;
                case EVENT_IN_CLASS_MODE_SET_UP_CODE:
                    this.setUpInClassMode(_eventData);
                    view.inClassModeSetUpEventReceived(_eventData);
                    view.setupSuccess();
                    break;
                case EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE:
                    Timber.e("EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE");
                    lockBy = LockScreenEnum.NONE;
                    view.setUpConfiguration();
                    break;
                /**
                 Alarm
                 */
                case EVENT_PAIR_CODE:
                    onResetSyncContact();
                    view.onEventPairReceived(_eventData);
                    break;
                case EVENT_UN_PAIR_CODE:
                    onResetSyncContact();
                    view.onEventPairReceived(_eventData);
                    break;
                case EVENT_RESET_FACTORY_CODE:
                    onResetSyncContact();
                    view.onResetFactoryEventReceived(_eventData);
                    break;
                /*case EVENT_BFF_CODE:
                    onResetSyncContact();
                    view.onBestFriendForeverReceived(_eventData);
                    break;*/
                case EVENT_SYNC_CONTACT:
                    onResetSyncContact();
                    view.onSyncContact(_eventData);
                    break;
                case EVENT_RESTART_CODE:
                    addNewWatchFaceToSetting(ActivityContextor.getInstance().getContext(), true);
                    final Intent restart = new Intent("com.pomohouse.waffle.REQUEST_RESTART");
                    ActivityContextor.getInstance().getContext().sendBroadcast(restart);
                    break;
                case EVENT_SHUTDOWN_CODE:
                    final Intent shutdown = new Intent("com.pomohouse.waffle.REQUEST_SHUTDOWN");
                    ActivityContextor.getInstance().getContext().sendBroadcast(shutdown);
                    break;
                /*case EVENT_BRIGHTNESS_TIME_OUT_CODE:
                    BrightnessTimeOutDao brightnessTimeOut = new Gson().fromJson(_eventData.getContent(), BrightnessTimeOutDao.class);
                    if (brightnessTimeOut != null)
                        view.onSetUpBrightnessTimeOut(brightnessTimeOut.getBrightnessTimeOut());
                    view.setupSuccess();
                    break;*/
                case EVENT_LOCATION_REQUEST_CODE:
                    // TODO REQUEST LOCATION
                 /*   RefreshLocationDao refreshLocation = new Gson().fromJson(_eventData.getContent(), RefreshLocationDao.class);
                    if (refreshLocation != null) {
                        Timber.e("RefreshLocationDao : " + refreshLocation.getEndpoint());
                        LocationIntentService.locationRefreshEndpoint = refreshLocation.getEndpoint();
                        LocationIntentService.isRefreshLocation = true;
                    }*/
                    view.requestGPSLocation();
                    break;
          /*      case EVENT_LANGUAGE_CODE:
                    LanguageDao language = new Gson().fromJson(_eventData.getContent(), LanguageDao.class);
                    Log.d("LchrPreImpl", "EVENT_LANGUAGE_CODE _eventData.getContent() " + _eventData.getContent());
                    if (language != null)
                        view.onChangeLanguage(new Locale(language.getLanguage(), language.getCountry()));
                    view.setupSuccess();
                    break;*/
                /**
                 * Setup Event [ Position, WatchOff, Silent, TimeZone, AutoAnswer ]
                 */
                case EVENT_TIMER_LOCATION_CODE:
                    LocationTimerDao locationTimer = new Gson().fromJson(_eventData.getContent(), LocationTimerDao.class);
                    if (locationTimer != null) {
                        Timber.e(String.valueOf("Position Time : " + locationTimer.getPositionTiming()));
                        if (iSettingManager != null) {
                            SettingPrefModel setting = iSettingManager.getSetting();
                            if (setting != null) {
                                setting.setPositionTiming(locationTimer.getPositionTiming());
                                iSettingManager.addMiniSetting(setting);
                                view.stopLocation();
                                view.startLocation();
                            }
                        }
                    }
                    break;
                case EVENT_SILENT_MODE_CODE:
                    SilentDao silentDao = new Gson().fromJson(_eventData.getContent(), SilentDao.class);
                    if (silentDao != null && !silentDao.getSilentMode().isEmpty()) {
                        Timber.e(String.valueOf("Silent : " + silentDao.getSilentMode()));
                        if (silentDao.getSilentMode().equalsIgnoreCase("Y")) {
                            CombineObjectConstance.getInstance().setSilentMode(true);
                            view.onEnableSilentMode();
                        } else {
                            CombineObjectConstance.getInstance().setSilentMode(false);
                            view.onDisableSilentMode();
                        }
                    }
                    break;
             /*   case EVENT_TIMEZONE_CODE:
                    TimeZoneDao timeZoneDao = new Gson().fromJson(_eventData.getContent(), TimeZoneDao.class);
                    if (timeZoneDao != null && !timeZoneDao.getAutoTimezone().isEmpty() && !timeZoneDao.getTimeZone().isEmpty()) {
                        Timber.e(String.valueOf("Timezone : " + timeZoneDao.getAutoTimezone()));

                        SettingPrefModel settingPrefModel = iSettingManager.getSetting();
                        settingPrefModel.setTimeZone(timeZoneDao.getTimeZone());
                        settingPrefModel.setAutoTimezone(timeZoneDao.getAutoTimezone().equalsIgnoreCase("Y"));
                        iSettingManager.addMiniSetting(settingPrefModel);

                        if (timeZoneDao.getAutoTimezone().equalsIgnoreCase("Y"))
                            view.setAutoTimezone();
                        else view.setUpTimeZone(timeZoneDao.getTimeZone());
                    }
                    break;*/
            /*    case EVENT_AUTO_ANSWER_CODE:
                    AutoAnswerDao autoAnswer = new Gson().fromJson(_eventData.getContent(), AutoAnswerDao.class);
                    if (autoAnswer != null) {
                        if (autoAnswer.getAutoAnswer().equalsIgnoreCase("Y"))
                            view.enableAutoAnswer();
                        else
                            view.disableAutoAnswer();
                    }
                    break;*/
                /*case EVENT_WEARER_STATUS_CODE:
                    WearerStatusDao watchStatus = new Gson().fromJson(_eventData.getContent(), WearerStatusDao.class);
                    if (watchStatus != null) {
                        Timber.e("watchStatus : " + watchStatus.getWatchOff());
                        if (watchStatus.getWatchOff().equalsIgnoreCase("Y"))
                            CombineObjectConstance.getInstance().setWatchAlarm(true);
                        else CombineObjectConstance.getInstance().setWatchAlarm(false);
                        view.onUpdateWearerStatus();
                    }
                    break;*/
                case EVENT_REFRESH_LOCATION_CODE:
                    // TODO REQUEST NEW LOCATION
                    /*LocationIntentService.isRefreshLocation = false;
                    Location currLocation = new Gson().fromJson(_eventData.getContent(), Location.class);
                    if (currLocation != null) {
                        view.updateLocation(currLocation);
                        Timber.e("New Location : " + currLocation.getAccuracy());
                        RefreshLocationRequest locationUpdate = new RefreshLocationRequest(currLocation, LocationIntentService.locationRefreshEndpoint);
                        locationUpdate.setImei(WearerInfoUtils.getInstance().getImei());
                        locationUpdate.setPower(getPowerLevel());
                        interactor.callSendNewLocationService(locationUpdate);
                    }
                    LocationIntentService.locationRefreshEndpoint = "";*/
                    break;
                case EVENT_UPDATE_LOCATION_CODE:
                   /* LocationIntentService.isRefreshLocation = false;
                    LocationIntentService.locationRefreshEndpoint = "";
                    Location location = new Gson().fromJson(_eventData.getContent(), Location.class);
                    if (location != null) {
                        view.updateLocation(location);
                        Timber.e("Sync Location : " + location.getProvider());
                        LocationUpdateRequest locationUpdateRequest = new LocationUpdateRequest(location);
                        locationUpdateRequest.setImei(WearerInfoUtils.getInstance().getImei());
                        if (iEventPrefManager != null)
                            locationUpdateRequest.setEventList(new Gson().toJson(iEventPrefManager.getEvent().getListEvent()));
                        requestEventInterval(locationUpdateRequest);
                    }*/
                    break;
            }
        } catch (Exception ignore) {
        }
    }

    private void onResetSyncContact() {
        CombineObjectConstance.getInstance().getContactEntity().setContactSynced(false);
    }

    public static final String ACTION_REGISTER_TWILIO_TOKEN = "ACTION_REGISTER_TWILIO_TOKEN";

    @Override
    public void initDevice() {
        view.sendIntentToBroadcast(new Intent(ACTION_REGISTER_TWILIO_TOKEN));
        if (inClassModePrefModel != null)
            inClassModePrefModel = inClassModeManager.getInClassMode();
        if (sleepTimePrefModel != null) sleepTimePrefModel = iSleepTimeManager.getSleepTime();
        if (iSettingManager != null) {
            SettingPrefModel settingPrefModel = iSettingManager.getSetting();
            if (settingPrefModel == null) return;
            CombineObjectConstance.getInstance().setSilentMode(settingPrefModel.isSilentMode());
            CombineObjectConstance.getInstance().setAutoAnswer(settingPrefModel.isAutoAnswer());
            CombineObjectConstance.getInstance().setWatchAlarm(settingPrefModel.isWearerStatus());
            /**
             * Auto Timezone Setup
             */
            if (settingPrefModel.isAutoTimezone()) view.setAutoTimezone();
            else view.setUpTimeZone(settingPrefModel.getTimeZone());
            /**
             * Auto Answer
             */
            /*if (settingPrefModel.isAutoAnswer())
                view.enableAutoAnswer();
            else
                view.disableAutoAnswer();*/
            /**
             * Silent Mode
             */
            if (settingPrefModel.isSilentMode()) view.onEnableSilentMode();
            else view.onDisableSilentMode();
            /**
             * Screen Timer Off
             */
            view.onSetUpBrightnessTimeOut(settingPrefModel.getScreenOffTimer());
        }
    }

    @Override
    public void timeZoneChange() {
        try {
            TimeZone tz = TimeZone.getDefault();
            view.onAlarmEventReceived();
            if (tz != null) {
                TimezoneUpdateRequest timezoneUpdateRequest = new TimezoneUpdateRequest();
                timezoneUpdateRequest.setTimeZone(tz.getID());
                //timezoneUpdateRequest.setImei(WearerInfoUtils.getInstance().getImei());
                //TCPSocketServiceProvider.getInstance().sendMessageFromLauncher(this, CMDCode.CMD_TIME_ZONE, new Gson().toJson(timezoneUpdateRequest));
                interactor.callTimezoneChanged(timezoneUpdateRequest);
            }
            EventDataInfo eventContent = new EventDataInfo();
            eventContent.setEventCode(EVENT_APP_TIMEZONE_CODE);
            eventContent.setEventDesc("Timezone  Change");
            view.onSendEventToBroadcast(eventContent);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void batteryCharging() {
        try {
            EventDataInfo eventContent = new EventDataInfo();
            eventContent.setEventCode(EVENT_BATTERY_CHARGING_CODE);
            eventContent.setEventDesc("Battery Charge");
            BatteryChargerEvent batteryChargerEvent = new BatteryChargerEvent();
            batteryChargerEvent.setCharger(true);
            eventContent.setContent(new Gson().toJson(batteryChargerEvent));
            view.onSendEventToBroadcast(eventContent);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void batteryUnCharging() {
        try {
            EventDataInfo eventContent = new EventDataInfo();
            eventContent.setEventCode(EVENT_BATTERY_CHARGING_CODE);
            eventContent.setEventDesc("Battery Un Charge");
            BatteryChargerEvent batteryChargerEvent = new BatteryChargerEvent();
            batteryChargerEvent.setCharger(false);
            eventContent.setContent(new Gson().toJson(batteryChargerEvent));
            view.onSendEventToBroadcast(eventContent);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void requestSOS() {
        //TCPSocketServiceProvider.getInstance().sendMessageFromLauncher(this, CMDCode.CMD_LOCATION_UPDATE, "");
        interactor.callSOS(this, new ImeiRequest(WearerInfoUtils.getInstance().getImei(AppContextor.getInstance().getContext())));
        EventDataInfo eventContent = new EventDataInfo();
        eventContent.setEventCode(EVENT_SOS_CODE);
        view.onSendEventToBroadcast(eventContent);
    }

    @Override
    public void alarmTime(AlarmModel alarm) {
        if (isInClassModeEnable) return;
        try {
            view.onWakeScreen();
            EventDataInfo eventContent = new EventDataInfo();
            eventContent.setEventCode(EVENT_ALARM_CODE);
            eventContent.setContent(new Gson().toJson(alarm));
            view.onSendEventToBroadcast(eventContent);
        } catch (Exception ignored) {
        }
    }

    private void setUpAlarm(EventDataInfo _event) {
        if (_event.getContent() != null && !_event.getContent().isEmpty()) {
            ArrayList<AlarmModel> alarmList = new Gson().fromJson(_event.getContent(), new TypeToken<ArrayList<AlarmModel>>() {
            }.getType());
            updateScheduler(alarmList);
        }
    }

    @Override
    public void updateScheduler(ArrayList<AlarmModel> alarmList) {
        try {
            if (alarmList == null) return;
            AlarmDatabase mDbAdapter = new AlarmDatabase(ActivityContextor.getInstance().getContext());
            mDbAdapter.open();
            mDbAdapter.deleteAlarmByType(AlarmItem.TYPE_ALARM);
            mDbAdapter.open();
            for (AlarmModel item : alarmList)
                mDbAdapter.saveAlarm(new AlarmItem().setUpData(item));
            mDbAdapter.close();
        } catch (Exception ignore) {
        }
    }

    @Override
    public void initMessageContentProvider(Activity mActivity) {
        mActivity.getContentResolver().delete(POMOContract.EventEntry.CONTENT_URI, null, new String[]{});
        mActivity.getLoaderManager().initLoader(2, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(mActivity, POMOContract.EventEntry.CONTENT_URI, null, POMOContract.EventEntry.EVENT_CODE + " = ? or " + POMOContract.EventEntry.EVENT_CODE + " = ?", new String[]{String.valueOf(EVENT_MESSAGE_CODE), String.valueOf(EVENT_GROUP_CHAT_CODE)}, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
                if (c != null && iNotificationManager != null) {
                    NotificationPrefModel notification = iNotificationManager.getNotification();
                    if (notification != null) {
                        notification.setHaveGroupChat(c.getCount() > 0);
                        notification.setHaveMessage(c.getCount() > 0);
                        iNotificationManager.addNotification(notification);
                        NotificationMainIconDao notificationMainIconDao = new NotificationMainIconDao();
                        notificationMainIconDao.setShow(notification.isHaveGroupChat() || notification.isHaveMessage());
                        EventDataInfo eventDataInfo = new EventDataInfo();
                        eventDataInfo.setContent(new Gson().toJson(notificationMainIconDao));
                        eventDataInfo.setEventCode(EVENT_LOCK_SCREEN_NOTIFICATION_MESSAGE_CODE);
                        view.onSendEventToBroadcast(eventDataInfo);
                        eventDataInfo.setEventCode(EVENT_NOTIFICATION_MESSAGE_CODE);
                        view.onSendEventToBroadcast(eventDataInfo);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> arg0) {

            }
        });
    }

    private void setUpSleepTimeMode(EventDataInfo _event) {
        if (_event.getContent() != null && !_event.getContent().isEmpty()) {
            if (iSleepTimeManager == null) return;
            iSleepTimeManager.addSleepTime(new Gson().fromJson(_event.getContent(), SleepTimePrefModel.class));
            sleepTimePrefModel = iSleepTimeManager.getSleepTime();
            validateSleepMode();
            Timber.i("Success Setup Sleep Mode");
        }
    }

    private void setUpInClassMode(EventDataInfo _event) {
        if (_event.getContent() != null && !_event.getContent().isEmpty()) {
            if (inClassModeManager == null) return;
            inClassModeManager.addInClassMode(new Gson().fromJson(_event.getContent(), InClassModePrefModel.class));
            inClassModePrefModel = inClassModeManager.getInClassMode();
            validateInClassMode();
            Timber.i("Success Setup In Class Mode");
        }
    }

    /**
     * Contact Content Provider
     *
     * @param acc isContact all
     */
    @Override
    public void initContactListContentProvider(Activity acc) {
        try {
            acc.getLoaderManager().initLoader(POMOContentProvider.CONTACT, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(acc, POMOContract.ContactEntry.CONTENT_URI, null, null, null, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    if (loader.getId() == POMOContentProvider.CONTACT) {
                        cursor.moveToFirst();
                        Timber.e("Data Contact : Load Start");
                        StringBuilder res = new StringBuilder();
                        ContactCollection contactCollection = new ContactCollection();
                        ArrayList<ContactModel> contactModelArrayList = new ArrayList<>();
                        while (!cursor.isAfterLast()) {
                            ContactModel contactModel = new ContactModel().setUpData(cursor);
                            contactModelArrayList.add(contactModel);
                            res.append("Contact => ").append(contactModel.getContactType()).append(" : ").append(contactModel.getContactId()).append("\n");
                            cursor.moveToNext();
                        }
                        Timber.e(res.toString());
                        contactCollection.setContactModelList(contactModelArrayList);
                        CombineObjectConstance.getInstance().getContactEntity().setContactCollection(contactCollection);
                        Timber.e("Data Contact : Load End");
                        EventDataInfo eventContent = new EventDataInfo();
                        eventContent.setEventCode(EVENT_UPDATE_LOCAL_CONTACT);
                        eventContent.setEventDesc("Sync Contact");
                        view.onSendEventToBroadcast(eventContent);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> arg0) {

                }
            });
        } catch (Exception ignore) {

        }
    }

    @Override
    public void initContactCallsProvider(Activity acc) {
        try {
            acc.getContentResolver().delete(POMOContract.CallEntry.CONTENT_URI, null, new String[]{});
            acc.getLoaderManager().initLoader(POMOContentProvider.CALL, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(acc, POMOContract.CallEntry.CONTENT_URI, null, POMOContract.CallEntry.IS_READ + " = 0", null, POMOContract.CallEntry.DATE + " DESC");
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    Timber.e("initContactCallsProvider");
                    if (loader.getId() == POMOContentProvider.CALL) {
                        StringBuilder stringBuffer = new StringBuilder();
                        if (cursor != null) {
                            cursor.moveToFirst();
                            Timber.e("Loader => Calls");
                            int number = cursor.getColumnIndex(POMOContract.CallEntry.NUMBER);
                            int type = cursor.getColumnIndex(POMOContract.CallEntry.TYPE);
                            int date = cursor.getColumnIndex(POMOContract.CallEntry.DATE);
                            int duration = cursor.getColumnIndex(POMOContract.CallEntry.DURATION);
                            int isRead = cursor.getColumnIndex(POMOContract.CallEntry.IS_READ);
                            ArrayList<CallDao> lstCallDao = new ArrayList<>();
                            int missCalls = 0, inComingCalls = 0, outGoingCalls = 0;
                            while (!cursor.isAfterLast()) {
                                CallDao callDao = new CallDao();
                                String phNumber = cursor.getString(number);
                                String callType = cursor.getString(type);
                                String callDate = cursor.getString(date);
                                int callDuration = cursor.getInt(duration);
                                String dir = null;
                                int dirCode = Integer.parseInt(callType);
                                switch (dirCode) {
                                    case POMOContract.CallEntry.OUTGOING_TYPE:
                                        dir = "OUTGOING";
                                        outGoingCalls++;
                                        break;
                                    case POMOContract.CallEntry.INCOMING_TYPE:
                                        dir = "INCOMING";
                                        inComingCalls++;
                                        break;
                                    case POMOContract.CallEntry.MISSED_TYPE:
                                        dir = "MISSED";
                                        missCalls++;
                                        break;
                                }
                                stringBuffer.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(" \nCall duration in sec :--- ").append(callDuration);
                                stringBuffer.append("\n----------------------------------");
                                callDao.setNumber(phNumber);
                                callDao.setCallDate(callDate);
                                callDao.setDuration(callDuration);
                                callDao.setIsRead(isRead);
                                callDao.setType(dirCode);
                                lstCallDao.add(callDao);
                                cursor.moveToNext();
                            }
                            Timber.i(stringBuffer.toString());
                            CombineObjectConstance.getInstance().getCallEntity().setMissCalls(missCalls);
                            CombineObjectConstance.getInstance().getCallEntity().setOutGoingCalls(outGoingCalls);
                            CombineObjectConstance.getInstance().getCallEntity().setInComingCalls(inComingCalls);
                            CombineObjectConstance.getInstance().getCallEntity().setCallDao(lstCallDao);
                            CombineObjectConstance.getInstance().setHaveMissCall(missCalls > 0);
                            if (iNotificationManager == null) return;
                            NotificationPrefModel notification = iNotificationManager.getNotification();
                            notification.setHaveMissCall(missCalls > 0);
                            iNotificationManager.addNotification(notification);

                            NotificationMainIconDao notificationMainIconDao = new NotificationMainIconDao();
                            notificationMainIconDao.setShow(missCalls > 0);
                            EventDataInfo eventDataInfo = new EventDataInfo();
                            eventDataInfo.setContent(new Gson().toJson(notificationMainIconDao));
                            eventDataInfo.setEventCode(EVENT_NOTIFICATION_CALL_CODE);
                            view.onSendEventToBroadcast(eventDataInfo);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * get Theme from Assets
     * Theme List
     */
    @Override
    public void readThemeFromAsset() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = AppContextor.getInstance().getContext().getAssets().open("theme/theme.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            Timber.i("Load Start");
            Timber.i(buf.toString());
            Timber.i("Load End");
            themeManager.addDataTheme(buf.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    public void requestShutdownDevice() {
        TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_SHUTDOWN, "{}");
    }

    @Override
    public void onInitialDeviceFailure(MetaDataNetwork error) {
        view.onInitialFailure(error);
    }

    @Override
    public void onInitialDeviceSuccess(MetaDataNetwork metaData, DeviceInfoModel data) {
        view.onInitialSuccess(metaData, data);
    }

    @Override
    public void onSOSFailure(MetaDataNetwork error) {
        view.failureSOS(error);
    }

    @Override
    public void onSOSSuccess(MetaDataNetwork metaData, ResponseDao data) {
        view.successSOS(metaData, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
