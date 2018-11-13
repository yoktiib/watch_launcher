package com.pomohouse.launcher.lock_screen;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pomohouse.launcher.R;
import com.pomohouse.launcher.broadcast.callback.ScreenOnListener;
import com.pomohouse.launcher.broadcast.callback.TimeTickChangedListener;
import com.pomohouse.launcher.broadcast.receivers.DeviceActionReceiver;
import com.pomohouse.launcher.broadcast.receivers.EventReceiver;
import com.pomohouse.launcher.fragment.contacts.IncomingCallReceiver;
import com.pomohouse.launcher.fragment.contacts.OutGoingCallReceiver;
import com.pomohouse.launcher.manager.notifications.INotificationManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefManager;
import com.pomohouse.launcher.manager.notifications.NotificationPrefModel;
import com.pomohouse.launcher.models.contacts.ContactCollection;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.launcher.utils.EventConstant;
import com.pomohouse.launcher.models.events.BatteryChargerEvent;
import com.pomohouse.launcher.models.events.NotificationMainIconDao;
import com.pomohouse.launcher.models.AlarmModel;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.launcher.models.LockScreenEnum;
import com.pomohouse.launcher.utils.SoundPoolManager;
import com.pomohouse.launcher.utils.VibrateManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import pl.droidsonroids.gif.GifDrawable;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.LOCK_SCREEN_INTENT;
import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UNLOCK_DEFAULT_TO_LAUNCHER_INTENT;

/**
 * Created by Admin on 11/14/2016 AD.
 */

public class LockScreenService extends Service {
    private static final int LOCK_SCREEN = 1;
    private static final int LOCK_SCREEN_WITH_IN_CLASS_MODE = 2;
    private static final int SOS = 3;
    private static final int ALARM = 4;
    private static final int UNLOCK_SCREEN = 5;
    private static final int UNLOCK_SCREEN_WITH_IN_CLASS_MODE = 6;
    private static final int BATTERY_CHARGE = 7;
    private static final int BATTERY_UN_CHARGE = 8;
    private LockScreenEnum lockScreenEnum = LockScreenEnum.NONE;
    private final IBinder mBinder = new LockScreenService.LockScreenBinder();
    public static boolean isLocked = false;
    //yangyu add
    public static boolean is24HourFormat = false;
    private WindowManager mWindowManager;
    private static Typeface custom_font;
    private static View mView;
    private FrameLayout contentBox;
    private INotificationManager notificationManager;
    private SoundPoolManager soundPoolManager;
    private VibrateManager vibrateManager;
    private int positionOfCall = 0;

    AppCompatTextView tvHour;
    AppCompatTextView tvAmPm;

    AppCompatTextView tvMinute;

    AppCompatTextView tvHour2;
    AppCompatTextView tvMinute2;

    AppCompatTextView tvDay;
    AppCompatTextView tvDate;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public static void startLockScreenService(final Context context) {
        context.startService(new Intent(LOCK_SCREEN_INTENT, null, context, LockScreenService.class));
    }

    public static void stopLockScreenService(final Context context) {
        context.stopService(new Intent(LOCK_SCREEN_INTENT, null, context, LockScreenService.class));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LockScreenBinder extends Binder {
        public LockScreenService getService() {
            return LockScreenService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventReceiver.getInstance().initEventToLockScreenListener(this::onEventReceived);
        notificationManager = new NotificationPrefManager(this);
        soundPoolManager = SoundPoolManager.getInstance(this);
        vibrateManager = VibrateManager.getInstance(this);
    }

    private void onEventReceived(EventDataInfo eventDataInfo) {
        try {
            switch (eventDataInfo.getEventCode()) {
                case EventConstant.EventLocal.EVENT_LOCK_SCREEN_DEFAULT_CODE:
                    onRegisterDeviceListener();
                    lockScreenEnum = LockScreenEnum.LOCK_SCREEN_DEFAULT;
                    sendLockScreen();
                    break;
                case EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_DEFAULT_CODE:
                    onUnRegisterDeviceListener();
                    lockScreenEnum = LockScreenEnum.NONE;
                    sendUnLockScreen();
                    break;
                case EventConstant.EventLocal.EVENT_LOCK_SCREEN_IN_CLASS_CODE:
                    onRegisterDeviceListener();
                    lockScreenEnum = LockScreenEnum.LOCK_SCREEN_WITH_IN_CLASS_MODE;
                    CombineObjectConstance.getInstance().setSilentMode(true);
                    sendInClassModeLockScreen();
                    break;
                case EventConstant.EventLocal.EVENT_UNLOCK_SCREEN_IN_CLASS_CODE:
                    onUnRegisterDeviceListener();
                    lockScreenEnum = LockScreenEnum.NONE;
                    CombineObjectConstance.getInstance().setSilentMode(false);
                    sendUnLockScreenWithInClassMode();
                    break;
                case EventConstant.EventLocal.EVENT_SOS_CODE:
                    sendSOS();
                    break;
                case EventConstant.EventLocal.EVENT_ALARM_CODE:
                    AlarmModel alarmItem = new Gson().fromJson(eventDataInfo.getContent(), AlarmModel.class);
                    sendAlarm(alarmItem);
                    break;
                case EventConstant.EventLocal.EVENT_BATTERY_CHARGING_CODE:
                    BatteryChargerEvent batteryStatusDao = new Gson().fromJson(eventDataInfo.getContent(), BatteryChargerEvent.class);
                    if (batteryStatusDao.isCharger())
                        sendBatteryCharge();
                    else
                        sendBatteryUnCharge();
                    break;
                case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MESSAGE_CODE:
                    NotificationMainIconDao message = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                    if (message != null)
                        notificationMessage(message.isShow());
                    break;
                case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE:
                    NotificationMainIconDao mute = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                    if (mute != null)
                        notificationVolumeMute(mute.isShow());
                    break;
                case EventConstant.EventLocal.EVENT_LOCK_SCREEN_NOTIFICATION_CALL_CODE:
                    NotificationMainIconDao call = new Gson().fromJson(eventDataInfo.getContent(), NotificationMainIconDao.class);
                    if (call != null)
                        notificationCall(call.isShow());
                    break;
            }
        } catch (Exception ignore) {
        }
    }

    public void onRegisterDeviceListener() {
        DeviceActionReceiver.getInstance().setLockScreenTimeTickChangedListener(timeTickChangedListener);
        DeviceActionReceiver.getInstance().setLockScreenScreenOnChangedListener(screenOnListener);
    }

    public void onUnRegisterDeviceListener() {
        DeviceActionReceiver.getInstance().removeLockScreenTimeTickChangedListener();
        DeviceActionReceiver.getInstance().removeLockScreenScreenOnListener();
    }

    public void sendInClassModeLockScreen() {
        _handler.sendMessage(_handler.obtainMessage(LOCK_SCREEN_WITH_IN_CLASS_MODE));
    }

    public void sendUnLockScreenWithInClassMode() {
        _handler.sendMessage(_handler.obtainMessage(UNLOCK_SCREEN_WITH_IN_CLASS_MODE));
    }

    public void sendUnLockScreen() {
        _handler.sendMessage(_handler.obtainMessage(UNLOCK_SCREEN));
    }

    public void sendLockScreen() {
        _handler.sendMessage(_handler.obtainMessage(LOCK_SCREEN));
    }

    public void sendSOS() {
        _handler.sendMessage(_handler.obtainMessage(SOS));
    }

    public void sendAlarm(AlarmModel alarm) {
        _handler.sendMessage(_handler.obtainMessage(ALARM, alarm));
    }

    public void sendBatteryCharge() {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_CHARGE));
    }

    private void sendBatteryUnCharge() {
        _handler.sendMessage(_handler.obtainMessage(BATTERY_UN_CHARGE));
    }

    private final Handler _handler = new Handler(msg -> {
        try {
            switch (msg.what) {
                case LOCK_SCREEN:
                    Timber.e("Lock CONTENT_LOCK_SCREEN");
                    if (!isLocked) {
                        isLocked = true;
                        lockScreenDefaultView();
                        onCreateWindowsManagerService();
                        updateTime();
                    }
                    break;
                case LOCK_SCREEN_WITH_IN_CLASS_MODE:
                    Timber.e("Lock - LOCK_SCREEN_WITH_IN_CLASS_MODE");
                    if (!isLocked) {
                        isLocked = true;
                        lockScreenInClassModeView();
                        onCreateWindowsManagerService();
                        updateTime();
                    } else {
                        if (mView != null) {
                            AppCompatImageView inClass = (AppCompatImageView) mView.findViewById(R.id.ivInClass);
                            if (inClass != null)
                                inClass.setVisibility(View.VISIBLE);
                            AppCompatImageView lock = (AppCompatImageView) mView.findViewById(R.id.lock);
                            if (lock != null)
                                lock.setVisibility(View.GONE);
                        }
                    }
                    break;
                case SOS:
                    OutGoingCallReceiver.isSOS = true;
                    positionOfCall = 0;
                    Timber.e("LockScreen - SOS");
                    sosView();
                    break;
                case ALARM:
                    Timber.e("Alarm");
                    if (msg.obj instanceof AlarmModel) {
                        AlarmModel alarmModel = (AlarmModel) msg.obj;
                        alarmView(alarmModel);
                    }
                    break;
                case UNLOCK_SCREEN_WITH_IN_CLASS_MODE:
                    Timber.e("Unlock - LOCK_SCREEN_WITH_IN_CLASS_MODE");
                    //onUnLockScreen();
                    if (mView != null) {
                        AppCompatImageView inClass = (AppCompatImageView) mView.findViewById(R.id.ivInClass);
                        if (inClass != null)
                            inClass.setVisibility(View.GONE);
                        AppCompatImageView lock = (AppCompatImageView) mView.findViewById(R.id.lock);
                        if (lock != null) {
                            mView.findViewById(R.id.lock).setVisibility(View.VISIBLE);
                            mView.findViewById(R.id.lock).setOnTouchListener((v, event) -> {
                                sendUnLockScreen();
                                return false;
                            });
                        }
                    }
                    break;
                case UNLOCK_SCREEN:
                    Timber.e("Unlock - onUnLockScreen");
                    onUnLockScreen();
                    break;
                case BATTERY_CHARGE:
                    Timber.e("Charge");
                    onBatteryCharge();
                    break;
                case BATTERY_UN_CHARGE:
                    Timber.e("Un Charge");
                    onBatteryUnCharge();
                    break;
            }
            return true;
        } catch (Exception ignore) {
            return true;
        }
    });

    public static boolean getLastCallDetails(Context context) {
        StringBuffer sb = new StringBuffer();
        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = context.getContentResolver().query(contacts, null, CallLog.Calls.TYPE + " = " + CallLog.Calls.OUTGOING_TYPE, null, android.provider.CallLog.Calls.DATE + " DESC limit 1");
        if (managedCursor != null) {
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            String callDuration = "";
            if (managedCursor.getCount() == 0)
                return true;
            while (managedCursor.moveToNext()) {
                // HashMap rowDataCall = new HashMap<String, String>();
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                String callDayTime = new Date(Long.valueOf(callDate)).toString();
                // long timestamp = convertDateToTimestamp(callDayTime);
                callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                sb.append("\nPhone Number:--- ").append(phNumber).append(" \nCall Type:--- ").append(dir).append(" \nCall Date:--- ").append(callDayTime).append(" \nCall duration in sec :--- ").append(callDuration);
                sb.append("\n----------------------------------");
            }
            if (callDuration.equalsIgnoreCase("0"))
                return true;
            managedCursor.close();
            System.out.println(sb);
        }
        return false;
    }

    ScreenOnListener screenOnListener = new ScreenOnListener() {
        @Override
        public void onScreenOn() {
            if (!isLocked)
                return;
            if (notificationManager == null)
                return;
            if (isWatchInCall() && !isAutoAnswerOn()) {
                sendUnLockScreen();
            } else {
                updateTime();
                updateNotification();
            }
        }
    };

    private boolean isWatchInCall() {
        TelecomManager tm = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        return tm.isInCall();
    }

    private boolean isAutoAnswerOn() {
        return Settings.System.getInt(this.getContentResolver(), "AUTO_ANSWER_ON", 0) == 1;
    }

    public void updateNotification() {
        try {
            NotificationPrefModel notification = notificationManager.getNotification();
            if (notification != null) {
                if (notification.isHaveGroupChat() || notification.isHaveMessage())
                    notificationMessage(true);
                else
                    notificationMessage(false);
                if (notification.isHaveMissCall())
                    notificationCall(true);
                else
                    notificationCall(false);
                if (notification.isHaveMute() || notification.isHaveSilent())
                    notificationVolumeMute(true);
                else notificationVolumeMute(false);
            }
        } catch (Exception ignore) {
        }
    }

    TimeTickChangedListener timeTickChangedListener = () -> {
        if (isLocked)
            updateTime();
    };

    private void notificationVolumeMute(boolean isShow) {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView != null) {
                    if (isShow)
                        mView.findViewById(R.id.ivMuteNotification).setVisibility(View.VISIBLE);
                    else
                        mView.findViewById(R.id.ivMuteNotification).setVisibility(View.INVISIBLE);
                }
            }

        } catch (Exception ignore) {
        }
    }

    private void notificationMessage(boolean isShow) {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView != null) {
                    if (isShow)
                        mView.findViewById(R.id.ivMessageNotification).setVisibility(View.VISIBLE);
                    else
                        mView.findViewById(R.id.ivMessageNotification).setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception ignore) {
        }
    }

    private void notificationCall(boolean isShow) {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView != null) {
                    if (isShow)
                        mView.findViewById(R.id.ivCallNotification).setVisibility(View.VISIBLE);
                    else
                        mView.findViewById(R.id.ivCallNotification).setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception ignore) {
        }
    }

    protected String[] dayOfWeek, monthOfYear;

    public void updateTime() {
        try {
            int hours, minutes, dayNumber, day, month;
            this.mappingViewLockScreen();
            dayOfWeek = getResources().getStringArray(R.array.dayOfWeekNameArr);
            monthOfYear = getResources().getStringArray(R.array.monthOfYearNameArr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            dayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_WEEK);
            //int am_pm = calendar.get(Calendar.AM_PM);
            updateTime(hours, minutes);
            updateDate(dayNumber, monthOfYear[month], dayOfWeek[day > 0 ? day - 1 : day]);
        } catch (Exception ignore) {
        }
    }

    public void updateTime(int hour, int minute) {
        try {
            Timber.i(hour + " : " + minute);
            String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
            String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
            if (tvHour == null || tvHour2 == null || tvMinute == null || tvMinute2 == null)
                return;
            if (is24HourFormat) {
                tvAmPm.setVisibility(View.GONE);
            } else {
                tvAmPm.setVisibility(View.VISIBLE);
                tvAmPm.setText(hour >= 12 ? getResources().getString(R.string.text_pm) : getResources().getString(R.string.text_am));
                hour = hour < 13 ? hour : (hour - 12);
                hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
            }

            tvHour.setText(String.valueOf(hourStr.charAt(0)));
            tvHour2.setText(String.valueOf(hourStr.charAt(1)));
            tvMinute.setText(String.valueOf(minuteStr.charAt(0)));
            tvMinute2.setText(String.valueOf(minuteStr.charAt(1)));
        } catch (Exception ignore) {
        }
    }

    protected void updateDate(int day, String month, String dayName) {
        if (tvDay != null)
            tvDay.setText(dayName.toUpperCase());
        if (tvDate != null)
            tvDate.setText(day + " " + month);
        if (tvAmPm != null) {

            if (is24HourFormat) {
                tvAmPm.setVisibility(View.GONE);
            } else {
                tvAmPm.setVisibility(View.VISIBLE);
            }
        }
    }

    public void lockScreenDefaultView() {
        this.removeViewInWindowsManager();
        mView = View.inflate(getApplicationContext(), R.layout.lock_screen, null);
        mView.findViewById(R.id.ivInClass).setVisibility(View.GONE);
        mView.findViewById(R.id.ivCallNotification).setVisibility(View.GONE);
        mView.findViewById(R.id.ivMessageNotification).setVisibility(View.GONE);
        mView.findViewById(R.id.ivMuteNotification).setVisibility(View.GONE);
        mView.findViewById(R.id.lock).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.lock).setOnTouchListener((v, event) -> {
            sendUnLockScreen();
            return false;
        });
        this.updateNotification();
        this.mappingViewLockScreen();
    }

    public void lockScreenInClassModeView() {
        this.removeViewInWindowsManager();
        mView = View.inflate(getApplicationContext(), R.layout.lock_screen, null);
        mView.findViewById(R.id.ivInClass).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.lock).setVisibility(View.GONE);
        this.mappingViewLockScreen();
    }

    public void mappingViewLockScreen() {
        try {
            if (mView == null)
                return;
            custom_font = Typeface.createFromAsset(getAssets(), getString(R.string.font_style));
            tvHour = (AppCompatTextView) mView.findViewById(R.id.tvHour);
            tvHour2 = (AppCompatTextView) mView.findViewById(R.id.tvHour2);
            tvMinute = (AppCompatTextView) mView.findViewById(R.id.tvMinute);
            tvMinute2 = (AppCompatTextView) mView.findViewById(R.id.tvMinute2);
            tvDate = (AppCompatTextView) mView.findViewById(R.id.tvDate);
            tvDay = (AppCompatTextView) mView.findViewById(R.id.tvDay);
            tvAmPm = (AppCompatTextView) mView.findViewById(R.id.tvAmPm);
            if (is24HourFormat) {
                tvAmPm.setVisibility(View.GONE);
            } else {
                tvAmPm.setVisibility(View.VISIBLE);
            }
            if (tvHour == null || tvHour2 == null || tvMinute == null || tvMinute2 == null)
                return;
            tvHour.setTypeface(custom_font);
            tvHour2.setTypeface(custom_font);
            tvMinute.setTypeface(custom_font);
            tvMinute2.setTypeface(custom_font);
            tvAmPm.setTypeface(custom_font);
            tvDate.setTypeface(custom_font);
            tvDay.setTypeface(custom_font);
        } catch (Exception ignore) {

        }
    }

    private void onBatteryCharge() {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView == null)
                    return;
                contentBox = (FrameLayout) mView.findViewById(R.id.contentBox);
                contentBox.removeAllViews();
                contentBox.setVisibility(View.VISIBLE);
                View view = View.inflate(getApplicationContext(), R.layout.dialog_fragment_image_view, null);
                contentBox.addView(view);
                GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.view_charging);
                AppCompatImageView ivCharging = (AppCompatImageView) view.findViewById(R.id.ivView);
                ivCharging.setImageDrawable(gifFromResource);
                gifFromResource.addAnimationListener(loopNumber -> {
                    contentBox.removeAllViews();
                    contentBox.setVisibility(View.GONE);
                });
            } else {
                this.removeViewInWindowsManager();
                mView = View.inflate(getApplicationContext(), R.layout.dialog_fragment_image_view, null);
                GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.view_charging);
                AppCompatImageView ivCharging = (AppCompatImageView) mView.findViewById(R.id.ivView);
                ivCharging.setImageDrawable(gifFromResource);
                gifFromResource.addAnimationListener(loopNumber -> removeViewInWindowsManager());
                onCreateWindowsManagerService();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onBatteryUnCharge() {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (contentBox != null) {
                    contentBox.removeAllViews();
                    contentBox.setVisibility(View.GONE);
                }
            } else {
                removeViewInWindowsManager();
            }
        } catch (Exception ignore) {

        }
    }

    public void sosView() {
        try {
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView != null) {
                    contentBox = (FrameLayout) mView.findViewById(R.id.contentBox);
                    contentBox.removeAllViews();
                    contentBox.setVisibility(View.VISIBLE);
                    View view = View.inflate(getApplicationContext(), R.layout.dialog_fragment_image_view, null);
                    contentBox.addView(view);
                    onSOSAnimation((AppCompatImageView) view.findViewById(R.id.ivView), true);
                }
            } else {
                removeViewInWindowsManager();
                mView = View.inflate(getApplicationContext(), R.layout.dialog_fragment_image_view, null);
                onSOSAnimation((AppCompatImageView) mView.findViewById(R.id.ivView), false);
                onCreateWindowsManagerService();
            }
        } catch (Exception ignore) {
        }
    }

    public void onSOSAnimation(ImageView ivShow, boolean isLock) {
        try {
            GifDrawable gifFromResource = new GifDrawable(getResources(), R.drawable.sos_anim);
            ivShow.setVisibility(View.VISIBLE);
            ivShow.setImageDrawable(gifFromResource);
            gifFromResource.addAnimationListener(loopNumber -> {
                if (!isLock) removeViewInWindowsManager();
                else {
                    if (contentBox != null) {
                        contentBox.removeAllViews();
                        contentBox.setVisibility(View.GONE);
                    }
                }
                onSOSCalling();
            });
            if (vibrateManager != null)
                vibrateManager.sosVibration();

            OutGoingCallReceiver.sosListener = () -> {
                if (getLastCallDetails(getApplicationContext())) {
                    onSOSCalling();
                } else
                    OutGoingCallReceiver.isSOS = false;
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSOSCalling() {
        Timber.e("onSOSCalling : " + positionOfCall);
        ContactCollection contactCollection = CombineObjectConstance.getInstance().getContactEntity().getContactCollection();
        if (contactCollection == null || contactCollection.getContactModelList() == null || contactCollection.getContactModelList().size() == 0)
            return;
        if (contactCollection.getContactModelList().size() == 1)
            callSOS(contactCollection.getContactModelList().get(0).getPhone());
        else {
            if (positionOfCall == 1) {
                positionOfCall = 0;
                Timber.e("onSOSCalling : Call 1 " + positionOfCall);
                callSOS(contactCollection.getContactModelList().get(1).getPhone());
            } else {
                positionOfCall = 1;
                Timber.e("onSOSCalling : Call 0 " + positionOfCall);
                callSOS(contactCollection.getContactModelList().get(0).getPhone());
            }
        }
    }

    public void callSOS(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    boolean doubleBackToExitPressedOnce = false;
    int loopOfAlarm = 0;

    public void alarmView(AlarmModel alarmModel) {
        try {
            if (soundPoolManager != null)
                soundPoolManager.playAlarm();
            if (vibrateManager != null)
                vibrateManager.alarmVibration();
            loopOfAlarm = 0;
            doubleBackToExitPressedOnce = false;
            if (lockScreenEnum != LockScreenEnum.NONE) {
                if (mView != null) {
                    contentBox = (FrameLayout) mView.findViewById(R.id.contentBox);
                    contentBox.removeAllViews();
                    contentBox.setVisibility(View.VISIBLE);
                    View view = View.inflate(getApplicationContext(), R.layout.window_alarm, null);
                    contentBox.addView(view);
                    custom_font = Typeface.createFromAsset(getAssets(), getString(R.string.font_style));
                    AlarmTypeManager alarmTypeManager = new AlarmTypeManager();
                    TextView tvType = (TextView) view.findViewById(R.id.tvType);
                    TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
                    tvTime.setTypeface(custom_font);
                    tvType.setTypeface(custom_font);
                    AppCompatImageView ivAlarm = (AppCompatImageView) view.findViewById(R.id.ivAlarm);
                    tvType.setText(alarmTypeManager.getAlarmName(this, alarmModel.getAlarmType()));
                    tvTime.setText(alarmModel.getTime());
                    view.setOnClickListener(v -> onAlarmExit(contentBox, true));
                    try {
                        GifDrawable gifFromResource = new GifDrawable(getResources(), alarmTypeManager.getAlarmResource(alarmModel.getAlarmType()));
                        ivAlarm.setVisibility(View.VISIBLE);
                        ivAlarm.setImageDrawable(gifFromResource);
                        gifFromResource.addAnimationListener(loopNumber -> {
                            loopOfAlarm++;
                            if (loopOfAlarm <= 3) {
                                gifFromResource.reset();
                            } else
                                stopVibrateAndSound();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                this.removeViewInWindowsManager();
                mView = View.inflate(getApplicationContext(), R.layout.window_alarm, null);
                custom_font = Typeface.createFromAsset(getAssets(), getString(R.string.font_style));
                AlarmTypeManager alarmTypeManager = new AlarmTypeManager();
                TextView tvType = (TextView) mView.findViewById(R.id.tvType);
                TextView tvTime = (TextView) mView.findViewById(R.id.tvTime);
                tvTime.setTypeface(custom_font);
                tvType.setTypeface(custom_font);
                AppCompatImageView ivAlarm = (AppCompatImageView) mView.findViewById(R.id.ivAlarm);
                tvType.setText(alarmTypeManager.getAlarmName(this, alarmModel.getAlarmType()));
                tvTime.setText(alarmModel.getTime());
                mView.setOnClickListener(v -> onAlarmExit(null, false));
                try {
                    GifDrawable gifFromResource = new GifDrawable(getResources(), alarmTypeManager.getAlarmResource(alarmModel.getAlarmType()));
                    ivAlarm.setVisibility(View.VISIBLE);
                    ivAlarm.setImageDrawable(gifFromResource);
                    gifFromResource.addAnimationListener(loopNumber -> {
                        loopOfAlarm++;
                        if (loopOfAlarm <= 3) {
                            gifFromResource.reset();
                        } else
                            stopVibrateAndSound();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                onCreateWindowsManagerService();
            }
        } catch (Exception ignore) {
        }
    }

    public void stopVibrateAndSound() {
        if (soundPoolManager != null)
            soundPoolManager.stopAlarm();
        if (vibrateManager != null)
            vibrateManager.release();
    }

    public void onAlarmExit(FrameLayout box, boolean isLocked) {
        try {
            if (doubleBackToExitPressedOnce) {
                isStop = true;
                Timber.e("doubleBackToExitPressedOnce");
                if (vibrateManager != null)
                    vibrateManager.release();
                if (soundPoolManager != null)
                    soundPoolManager.stopAlarm();
                if (!isLocked) {
                    Timber.e("isLocked");
                    this.removeViewInWindowsManager();
                } else {
                    Timber.e("is Not Locked");
                    if (box != null) {
                        box.setVisibility(View.GONE);
                        box.removeAllViews();
                        return;
                    }
                }
            }
            this.doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } catch (Exception ignore) {
        }
    }

    boolean isStop = false;

    private void removeViewInWindowsManager() {
        try {
            if (mWindowManager == null)
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (mView != null)
                mWindowManager.removeView(mView);
            mView = null;
        } catch (IllegalArgumentException ignore) {
            mView = null;
        }
    }

    private void onCreateWindowsManagerService() {
        try {
            if (mView == null || mWindowManager == null)
                return;
            final WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            mView.setVisibility(View.VISIBLE);
            mWindowManager.addView(mView, mLayoutParams);
        } catch (Exception ignore) {

        }
    }

    private void onUnLockScreen() {
        try {
            isLocked = false;
            if (mWindowManager == null)
                mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (mView != null && mWindowManager != null) {
                Timber.e("Un Lock");
                mWindowManager.removeView(mView);
                mView = null;
                if (vibrateManager != null) {
                    vibrateManager.unlockScreen();
                }
            } else
                Timber.e("Not Un Lock");
            lockScreenEnum = LockScreenEnum.NONE;
            if (!isWatchInCall()) {  //yangyu add for fix bug
                sendBroadcast(new Intent(SEND_EVENT_UNLOCK_DEFAULT_TO_LAUNCHER_INTENT));
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}