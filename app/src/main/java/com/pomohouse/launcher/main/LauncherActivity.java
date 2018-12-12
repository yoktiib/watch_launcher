package com.pomohouse.launcher.main;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pomohouse.component.pager.HorizontalViewPager;
import com.pomohouse.launcher.POMOWatchApplication;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.activity.pair.EventAlertActivity;
import com.pomohouse.launcher.activity.pincode.PinCodeActivity;
import com.pomohouse.launcher.base.BaseLauncherActivity;
import com.pomohouse.launcher.broadcast.alarm.AlarmReceiver;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmDatabase;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmItem;
import com.pomohouse.launcher.broadcast.alarm.receiver.AalService;
import com.pomohouse.launcher.broadcast.callback.AlarmListener;
import com.pomohouse.launcher.broadcast.callback.TimeTickChangedListener;
import com.pomohouse.launcher.broadcast.receivers.DeviceActionReceiver;
import com.pomohouse.launcher.broadcast.receivers.EventReceiver;
import com.pomohouse.launcher.broadcast.sensor.SensorService;
import com.pomohouse.launcher.content_provider.POMOContract;
import com.pomohouse.launcher.di.module.LauncherPresenterModule;
import com.pomohouse.launcher.fragment.contacts.presenter.IContactPresenter;
import com.pomohouse.launcher.lock_screen.LockScreenService;
import com.pomohouse.launcher.main.presenter.ILauncherPresenter;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.in_class_mode.IInClassModePrefManager;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefModel;
import com.pomohouse.launcher.manager.sleep_time.ISleepTimeManager;
import com.pomohouse.launcher.manager.theme.IThemePrefManager;
import com.pomohouse.launcher.models.DeviceInfoModel;
import com.pomohouse.launcher.models.DeviceSetUpDao;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.settings.AutoAnswerDao;
import com.pomohouse.launcher.models.settings.InClassDao;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.OnTCPStatusListener;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.ActivityContextor;
import com.pomohouse.library.manager.AppContextor;
import com.pomohouse.library.networks.MetaDataNetwork;
import com.pomohouse.library.networks.ResponseDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;

import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_AUTO_ANSWER_CALL_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_KILL_APP;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_IN_CLASS_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;

/**
 * Manages start screen of the application.
 */
public class LauncherActivity extends BaseLauncherActivity implements ILauncherView/*, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {
    private static final int DEVICE_ACTION = 1;
    @BindView(R.id.viewpager)
    HorizontalViewPager viewPager;
    @Inject
    ILauncherPresenter presenter;
    @Inject
    IContactPresenter contactPresenter;
    @Inject
    INotificationManager notificationManager;
    @Inject
    IFitnessPrefManager fitnessPrefManager;
    @Inject
    IThemePrefManager themeManager;
    @Inject
    IInClassModePrefManager inClassModeManager;
    @Inject
    IEventPrefManager iEventPrefManager;
    @Inject
    ISettingManager settingManager;
    @Inject
    ISleepTimeManager sleepTimeManager;

    private LauncherHorizontalPagerAdapter pagerAdapter;
    private boolean isLocationStart = false, isAlarmTime = false;

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
        Timber.e("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
        Timber.e("onStop");
    }


    @Override
    public void startLocation() {
        //TODO Location Start
        Intent locationBroadcast = new Intent(getApplicationContext(), LocationBroadcast.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, locationBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        long startTime = System.currentTimeMillis(); //alarm starts immediately
        AlarmManager backupAlarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (backupAlarmMgr != null) {
            backupAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, startTime, settingManager.getSetting().getPositionTiming() * 1000, alarmIntent); // alarm will repeat after every 15 minutes
        }
    }


    PendingIntent alarmIntent;

    @Override
    public void stopLocation() {
        //TODO Location End
        if (alarmIntent != null) {
            AlarmManager backupAlarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            if (backupAlarmMgr != null) backupAlarmMgr.cancel(alarmIntent);
            alarmIntent = null;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void sleepTimeSetUpEventReceived(EventDataInfo eventData) {

    }

    @Override
    public void startSensor() {
        SensorService.startSensorService(this);
    }

    @Override
    public void stopSensor() {
        SensorService.stopSensorService(this);
    }

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    public void requestGPSLocation() {
        Timber.e("requestGPSLocation");
        //TODO Reuqest GPS
    }

    private final int MY_PERMISSIONS = 1010;
    String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_SETTINGS, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WAKE_LOCK, Manifest.permission.SET_TIME_ZONE};

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled() {
        for (String permission : PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {
        List<String> remainingPermissions = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), MY_PERMISSIONS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onSetNotificationManager(notificationManager);
        super.onSetSettingManager(settingManager);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        WearerInfoUtils.getInstance().initWearerInfoUtils(this);
        //startService(new Intent(this, TCPSocketServiceProvider.class));
        doBindService();

        presenter.provideThemeManager(themeManager);
        presenter.provideEventManager(iEventPrefManager);
        presenter.provideSettingManager(settingManager);
        presenter.provideInClassModeManager(inClassModeManager);
        presenter.provideFitnessManager(fitnessPrefManager);
        presenter.provideSleepTimeManager(sleepTimeManager);
        presenter.provideNotificationManager(notificationManager);

        presenter.initContactCallsProvider(this);
        presenter.initContactListContentProvider(this);
        presenter.initMessageContentProvider(this);


        viewPager.setAdapter(pagerAdapter = new LauncherHorizontalPagerAdapter(getSupportFragmentManager()));

        DeviceActionReceiver.getInstance().setLauncherTimeTickChangedListener(timeTickChangedListener);
        AlarmReceiver.getInstance().initAlarmListener(alarmListener);
        EventReceiver.getInstance().initEventLauncherListener(this::onEventReceived);

        presenter.onInitial(this);
        contactPresenter.onInitial(this);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (pagerAdapter.getContactFragment() != null)
                        pagerAdapter.getContactFragment().notifyDataChangeMissCall();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
            if (arePermissionsEnabled()) {
                presenter.requestInitialDevice();
            } else requestMultiplePermissions();

        } else {
            presenter.requestInitialDevice();
        }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS);
            } else {
                presenter.requestInitialDevice();
            }
        } else {
            presenter.requestInitialDevice();
        }*/
        presenter.initDevice();
        //Timber.e("GPS OPEN ? = " + isOPen(this));
        if (!isOPen(this)) openGPS(this);
    }

    public TCPSocketServiceProvider mBoundService;

    private void doBindService() {
        Intent intent = new Intent(this, TCPSocketServiceProvider.class);
        startService(intent);
        bindService(intent, socketConnection, Context.BIND_AUTO_CREATE);
    }

    protected ServiceConnection socketConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((TCPSocketServiceProvider.LocalBinder) service).getService();
            mBoundService.IsBendable(new OnTCPStatusListener() {
                @Override
                public void onConnected() {
                    TCPSocketServiceProvider.getInstance().sendMessage(CMDCode.CMD_REGISTER_SOCKET, "{}");
                }

                @Override
                public void onDisconnected() {
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;
        }
    };

    public static boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps /*|| network*/;
    }

    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

/*

    public static void setDataEnabled(int slotIdx, boolean enable, Context context) throws Exception {
        try {
            int subid = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotIdx).getSubscriptionId();
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setDataEnabled = telephonyService.getClass().getDeclaredMethod("setDataEnabled", int.class, boolean.class);
            if (null != setDataEnabled) {
                setDataEnabled.invoke(telephonyService, subid, enable);
                LogUtil.LOGD(TAG, "setDataEnabled suc", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.LOGD(TAG, "setDataEnabled exception", false);
        }
    }

    public static boolean getDataEnabled(int slotIdx, Context context) throws Exception {
        boolean enabled = false;
        try {
            int subid = SubscriptionManager.from(context).getActiveSubscriptionInfoForSimSlotIndex(slotIdx).getSubscriptionId();
            TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getDataEnabled = telephonyService.getClass().getDeclaredMethod("getDataEnabled", int.class);
            if (null != getDataEnabled) {
                enabled = (Boolean) getDataEnabled.invoke(telephonyService, subid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enabled;
    }
*/

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAllAllowed = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*Log.d("", "Permission callback called-------");
        switch (requestCode) {
            case MY_PERMISSIONS: {
                Manifest.permission., Manifest.permission., Manifest.permission.WAKE_LOCK, Manifest.permission.SET_TIME_ZONE;
                Map<String, Integer> perms = new HashMap<>();
                for (String perm : permissions)
                    perms.put(perm, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(i) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }
                }
            }
        }*/
    }

    /**
     * Service Runtime
     * Broadcast Runtime Receiver
     */
    @Override
    public void initializeServiceAndReceiver() {
        /**
         * Broadcast Runtime Receiver
         */

        SensorService.startSensorService(AppContextor.getInstance().getContext());
        LockScreenService.startLockScreenService(AppContextor.getInstance().getContext());
        DeviceActionReceiver.getInstance().initDevicePowerActionListener(this::onDevicePowerActionReceived);

        EventReceiver.getInstance().startEventReceiver(AppContextor.getInstance().getContext());
        AlarmReceiver.getInstance().startAlarmReceiver(AppContextor.getInstance().getContext());
        DeviceActionReceiver.getInstance().startDeviceActionReceiver(AppContextor.getInstance().getContext());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        MultiDex.install(this);
    }


    @Override
    public void onInitialFailure(MetaDataNetwork error) {
    }

    @Override
    public void onInitialSuccess(MetaDataNetwork metaData, DeviceInfoModel data) {
        try {
            CombineObjectConstance.getInstance().setInitDevice(true);
            if (inClassModeManager != null && data.getInClassMode() != null)
                inClassModeManager.addInClassMode(data.getInClassMode());
            if (sleepTimeManager != null && data.getSleepMode() != null)
                sleepTimeManager.addSleepTime(data.getSleepMode());
            if (data.getAlarmModelList() != null)
                presenter.updateScheduler(data.getAlarmModelList());
            if (settingManager != null && data.getDeviceSetUp() != null) {
                DeviceSetUpDao deviceSetUp = data.getDeviceSetUp();
                SettingPrefModel setting = settingManager.getSetting();
                setting.setSilentMode(data.getDeviceSetUp().getSilentMode().equalsIgnoreCase("Y"));
                //setting.setTimeZone(deviceSetUp.getTimeZone());
                setting.setPositionTiming(deviceSetUp.getPositionTiming());
                //setting.setWearerStatus(deviceSetUp.getWearerStatus().equalsIgnoreCase("Y"));
                //setting.setAutoAnswer(deviceSetUp.getAutoAnswer().equalsIgnoreCase("Y"));
                //setting.setAutoTimezone(deviceSetUp.getAutoTimezone().equalsIgnoreCase("Y"));
                //setting.setScreenOffTimer(deviceSetUp.getBrightnessTimeOut());
                setting.setLang(deviceSetUp.getLang());
                settingManager.addMiniSetting(setting);
            }
            presenter.initDevice();
            super.configurationChanged();
        } catch (Exception ignore) {
        }
    }

    private void onEventReceived(EventDataInfo eventDataInfo) {
        presenter.eventReceiver(eventDataInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityContextor.getInstance().initActivity(this);
        ActivityContextor.getInstance().init(this);
        if (viewPager != null) viewPager.setCurrentItem(1);
        if (TCPSocketServiceProvider.getInstance() != null)
            TCPSocketServiceProvider.getInstance().checkSocketIsRunning();

//        startRtcRepeatAlarm( this);
        configurationChanged();
        sendBroadcast(new Intent(SEND_EVENT_KILL_APP));
    }

   /* private AlarmManager mAlarmManager;
    private Intent mAlarmIntent;
    private PendingIntent mPI;
    private void startRtcRepeatAlarm(Context mContext){
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = new Intent(this, SensorService.class);
        mPI = PendingIntent.getService(this,0 ,mAlarmIntent ,0 );
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60*1000,
                20*60*1000 ,mPI );


    }*/


    AlarmListener alarmListener = alarm -> new Handler().postDelayed(() -> {
        isAlarmTime = true;
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 20000);
        presenter.alarmTime(alarm);
    }, 1000);

    TimeTickChangedListener timeTickChangedListener = () -> {
        if (isNetworkAvailable()) {
            if (!CombineObjectConstance.getInstance().isInitDevice()) {
                Timber.e("Call InitDevice");
                presenter.requestInitialDevice();
            }
            if (!CombineObjectConstance.getInstance().getContactEntity().isContactSynced()) {
                Timber.e("Call Contact");
                contactPresenter.requestContact();
            }
        } else {
            if (settingManager != null && settingManager.getSetting().isMobileData() && !isMobileDataConnect()) {
                Intent dataOnIntent = new Intent("com.pomohouse.waffle.REQUEST_MOBILE_DATA");
                dataOnIntent.putExtra("status", "on");
                sendBroadcast(dataOnIntent);
            }
        }
        presenter.timeTickControl();
    };

    private void onDevicePowerActionReceived(Intent intent) {
        _handler.sendMessage(_handler.obtainMessage(DEVICE_ACTION, intent));
    }

    Handler _handler = new Handler(msg -> {
        switch (msg.what) {
            case DEVICE_ACTION:
                Intent intent = (Intent) msg.obj;
                if (intent == null || intent.getAction() == null) return true;
                switch (intent.getAction()) {
                    /*case Intent.ACTION_BOOT_COMPLETED:

                        contactPresenter.requestContact();
                        break;*/
                    case Intent.ACTION_SHUTDOWN:
                        presenter.requestShutdownDevice();
                        break;
                    case Intent.ACTION_SCREEN_OFF:
                        presenter.lockScreenDevice();
                        sendBroadcast(new Intent(SEND_EVENT_KILL_APP));
                        if (viewPager.getCurrentItem() != 1) viewPager.setCurrentItem(1);
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        presenter.batteryUnCharging();
                        break;
                    case Intent.ACTION_POWER_CONNECTED:
                        presenter.batteryCharging();
                        break;
                    case Intent.ACTION_TIMEZONE_CHANGED:
                        presenter.timeZoneChange();
                        break;
                }
                break;
        }
        return false;
    });

    @Override
    protected List<Object> getModules() {
        return Collections.singletonList(new LauncherPresenterModule(this));
    }

    boolean flag = false;
    boolean flag2 = false;

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent e) {
        e.startTracking();
        if (flag2) {
            flag = false;
        } else {
            flag = true;
            flag2 = false;
        }
        return true;
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        event.startTracking();
        flag = true;
        flag2 = false;
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        flag = false;
        flag2 = true;
        new Handler().postDelayed(() -> {
            if (flag2) presenter.requestSOS(/*WearerInfoUtils.getInstance().getImei()*/);
        }, 1500);
        return true;
    }

    @Override
    public void failureSOS(MetaDataNetwork error) {
    }

    @Override
    public void successSOS(MetaDataNetwork metaData, ResponseDao data) {
    }

    @Override
    public void onWakeScreen() {
        PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Alarm");
        screenLock.acquire();
        screenLock.release();
    }

    @Override
    public void setUpInClassModeEnable() {
        if (soundPoolManager != null) soundPoolManager.silentMode(this);
        Intent intent = new Intent(SEND_IN_CLASS_INTENT);
        intent.putExtra(EVENT_EXTRA, new Gson().toJson(new InClassDao().setInClass("Y")));
        sendBroadcast(intent);
        /**
         * Stop sound to silent
         */
        float backLightValue = 0.1f;
        {
            int brightnessInt = (int) (backLightValue * 255);
            if (brightnessInt < 1) brightnessInt = 1;
            ContentResolver cResolver = getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightnessInt);
        }
        {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.screenBrightness = backLightValue;
            getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void setUpInClassModeDisable() {
        Intent intent = new Intent(SEND_IN_CLASS_INTENT);
        intent.putExtra(EVENT_EXTRA, new Gson().toJson(new InClassDao().setInClass("N")));
        sendBroadcast(intent);
        modifyVolumeChanged(settingManager.getSetting());
    }

    @Override
    public void onResetFactoryEventReceived(EventDataInfo eventData) {
        if (inClassModeManager != null) inClassModeManager.removeInClassMode();
        {
            AlarmDatabase mDbAdapter = new AlarmDatabase(this);
            mDbAdapter.open();
            mDbAdapter.deleteAlarmByType(AlarmItem.TYPE_ALARM);
        }
        if (themeManager != null) themeManager.removeCurrentTheme();
        if (fitnessPrefManager != null) fitnessPrefManager.removeFitness();
        if (settingManager != null) settingManager.removeMiniSetting();
        if (contactPresenter != null) contactPresenter.onDeleteAllContact();

        /**
         * Send Broadcast to setup Sound after reset factory
         */
        if (settingManager != null)
            onBrightnessChanged(settingManager.getSetting().getBrightLevel());
    }

    @Override
    public void onEventPairReceived(EventDataInfo eventData) {
        contactPresenter.requestContact();
        startActivity(new Intent(this, EventAlertActivity.class).putExtra(EventAlertActivity.EVENT_EXTRA, eventData));
    }

    @Override
    public void onBestFriendForeverReceived(EventDataInfo eventData) {
        contactPresenter.requestContact();
    }

    @Override
    public void onSyncContact(EventDataInfo eventData) {
        contactPresenter.requestContact();
    }

    @Override
    public void onSetUpBrightnessTimeOut(int timeOut) {
        SettingPrefModel miniSetting = settingManager.getSetting();
        miniSetting.setScreenOffTimer(timeOut);
        settingManager.addMiniSetting(miniSetting);
        super.configurationChanged();
    }

    /**
     * Timezone : change time zone from parent app / and auto detector from network connection
     */
    @Override
    public void setUpTimeZone(String timeZone) {
        if (settingManager != null && settingManager.getSetting() != null) {
            SettingPrefModel settingPrefModel = settingManager.getSetting();
            settingPrefModel.setTimeZone(timeZone);
            settingManager.addMiniSetting(settingPrefModel);
            super.setUpTimeZone(settingManager.getSetting().getTimeZone());
        }
    }

    /**
     * Start Silent Mode : Set mute all sound in the watch
     */
    @Override
    public void onEnableSilentMode() {
        SettingPrefModel settingPrefModel = settingManager.getSetting();
        settingPrefModel.setSilentMode(true);
        settingManager.addMiniSetting(settingPrefModel);
        super.enableSilentMode();
    }

    @Override
    public void onDisableSilentMode() {
        SettingPrefModel settingPrefModel = settingManager.getSetting();
        settingPrefModel.setSilentMode(false);
        settingManager.addMiniSetting(settingPrefModel);
        super.disableSilentMode();
    }

    @Override
    public void onChangeLanguage(Locale locale) {
        setLanguage(locale.getLanguage());
        WearerInfoUtils.getInstance().setLanguage(locale.getLanguage());
        Log.d("Launcher", "onChangeLanguage " + WearerInfoUtils.getInstance().getLanguage());
    }

    /**
     * Auto answer : watch must answer call auto
     */
    @Override
    public void enableAutoAnswer() {
        CombineObjectConstance.getInstance().setAutoAnswer(true);
        Settings.System.putInt(this.getContentResolver(), "AUTO_ANSWER_ON", 1);
        Intent intent = new Intent(SEND_AUTO_ANSWER_CALL_INTENT);
        intent.putExtra(EVENT_EXTRA, new Gson().toJson(new AutoAnswerDao().setAutoAnswer("Y")));
        sendBroadcast(intent);
        modifyVolumeChanged(settingManager.getSetting());
    }


    @Override
    public void disableAutoAnswer() {
        CombineObjectConstance.getInstance().setAutoAnswer(false);
        Settings.System.putInt(this.getContentResolver(), "AUTO_ANSWER_ON", 0);
        Intent intent = new Intent(SEND_AUTO_ANSWER_CALL_INTENT);
        intent.putExtra(EVENT_EXTRA, new Gson().toJson(new AutoAnswerDao().setAutoAnswer("N")));
        sendBroadcast(intent);
        modifyVolumeChanged(settingManager.getSetting());
    }

    /**
     * Update Watch off alarm : modify mode when have event put/off
     */
    @Override
    public void onUpdateWearerStatus() {
    }

    @Override
    public void inClassModeSetUpEventReceived(EventDataInfo eventData) {
    }

    @Override
    public void setupSuccess() {
        Toast.makeText(this, getString(R.string.text_setup_success), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlarmEventReceived() {
        Timber.e("onAlarmEventReceived Change");
        Intent i = new Intent(this, AalService.class);
        i.setAction(AalService.ACTION_SET_SILENT_ALARM);
        startService(i);
    }

    @Override
    public void openPinCodeView(String code) {
        startActivity(new Intent(this, PinCodeActivity.class).putExtra(PinCodeActivity.EXTRA_PIN_CODE, code));
    }

    @Override
    public void onSendEventToBroadcast(EventDataInfo eventContent) {
        Timber.e("onSendEventToBroadcast");
        Timber.e("Content : " + eventContent.getContent());
        Timber.e("Event Code : " + eventContent.getEventCode());
        final Intent intentEvent = new Intent(SEND_EVENT_UPDATE_INTENT);
        intentEvent.putExtra(EVENT_STATUS_EXTRA, new MetaDataNetwork(0, "success"));
        intentEvent.putExtra(EVENT_EXTRA, eventContent);
        sendBroadcast(intentEvent);
    }

    @Override
    public void onSendFilterBroadcast(String filter) {
        if (filter != null) sendBroadcast(new Intent(filter));
    }

    @Override
    public void sendIntentToBroadcast(Intent intent) {
        if (intent != null) sendBroadcast(intent);
    }

    @Override
    public void setUpConfigurationWhenLockScreen() {
        Timber.e("setUpConfigurationWhenLockScreen");
        if (isAlarmTime)
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 20000);
        else {
            ContentResolver cResolver = getContentResolver();
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, 1);
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 2000);
        }
        isAlarmTime = false;
    }

    @Override
    public void updateLocation(Location location) {
        /*if (WearerInfoUtils.getInstance() == null || WearerInfoUtils.getInstance().getImei() == null || WearerInfoUtils.getInstance().getImei().isEmpty())
            return;*/
        POMOWatchApplication.mLocation = location;
        getContentResolver().delete(POMOContract.WearerEntry.CONTENT_URI, null, new String[]{});
        ContentValues values = new ContentValues();
        values.put(POMOContract.WearerEntry.IMEI, WearerInfoUtils.getInstance().getImei(this));
        values.put(POMOContract.WearerEntry.LONGITUDE, location.getLongitude());
        values.put(POMOContract.WearerEntry.LATITUDE, location.getLatitude());
        getContentResolver().insert(POMOContract.WearerEntry.CONTENT_URI, values);
    }

    @Override
    public void setUpConfiguration() {
        Timber.e("setUpConfiguration");
        super.configurationChanged();
    }
}
