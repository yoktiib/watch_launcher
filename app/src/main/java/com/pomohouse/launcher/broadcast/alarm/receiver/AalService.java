package com.pomohouse.launcher.broadcast.alarm.receiver;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.pomohouse.launcher.broadcast.BaseBroadcast;
import com.pomohouse.launcher.models.AlarmModel;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmDatabase;
import com.pomohouse.launcher.broadcast.alarm.model.AlarmItem;

import java.util.Calendar;


public class AalService extends Service {
    public final static String EXTRA_DATA = "EXTRA_DATA";

    public static final String ACTION_SET_ALARM = "set_alarm";
    public static final String ACTION_LAUNCH_ALARM = "launch_alarm";
    public static final String ACTION_FORCE_LAUNCH_ALARM = "force_launch";
    public static final String ACTION_STOP_ALARM = "stop_alarm";
    public static final String ACTION_SET_SILENT_ALARM = "set_silent_alarm";


    public static final String EXTRA_DO_NOT_DISABLE = "do_not_disable";
    public static final String PREF_FILE_NAME = "AutoAppLauncherPrefs";
    private static final String PREF_KEY_NEXT_ALARM_ID = "next_alarm_id";


    private static final int NOTIFY_ID = 1;
    private static final int NOTIFY_ID_ALARM_SET = 2;

    private NotificationManager mNotificationManager;
    private PowerManager.WakeLock mPartialWakeLock;
    private final Handler mHandler = new Handler();

    private AlarmDatabase mDbAdapter;
    private AlarmItem mCurrentAlarmItem;

    private boolean mIsPlayingBackup, mIsCounting;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AppAlarmTag");
        mPartialWakeLock.acquire();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDbAdapter = new AlarmDatabase(this);
        mDbAdapter.open();
        mIsPlayingBackup = false;
        mIsCounting = false;
    }

    @Override
    public void onDestroy() {
        setNextAlarm();
        try {
            mNotificationManager.cancel(NOTIFY_ID);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doAction(intent);

        try {
            mPartialWakeLock.release();
        } catch (Exception ignored) {
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void doAction(Intent intent) {
        if (intent == null)
            return;
        String action = intent.getAction();
        switch (action) {
            case ACTION_SET_ALARM:
                stopOrSet();
                break;
            case ACTION_SET_SILENT_ALARM:
                stopOrSet();
                break;
            case ACTION_LAUNCH_ALARM:
                sendAlarmIntent(intent.getBooleanExtra(EXTRA_DO_NOT_DISABLE, false));
                //actionLaunchAlarm();
                break;
            case ACTION_FORCE_LAUNCH_ALARM:
                actionForceLaunchAlarm();
                break;
            case ACTION_STOP_ALARM:
                actionStopAlarm();
                break;
        }
    }

    private void sendAlarmIntent(boolean doNotDisable) {
        if (mDbAdapter == null)
            return;
        mCurrentAlarmItem = mDbAdapter.getAlarmById(loadNextAlarmPref());
        if (!mCurrentAlarmItem.hasRepeat() && !doNotDisable) {
            mDbAdapter.setAlarmEnabled(mCurrentAlarmItem.getInt(AlarmItem.KEY_ROW_ID), false);
        }
        if (mCurrentAlarmItem.getBool(AlarmItem.KEY_DO_NOT_LAUNCH_ON_CALL) && isPhoneNotIdle()) {
            try {
                Thread.sleep(2000);
                stopOrSet();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            launchAlarm();
        }

        if (mCurrentAlarmItem != null) {
            /*AlarmModel alarm = new AlarmModel();
            alarm.setImei(mCurrentAlarmItem.getString(AlarmItem.KEY_IMEI));
            alarm.setName(mCurrentAlarmItem.getString(AlarmItem.KEY_NAME));
            alarm.setAlarmId(mCurrentAlarmItem.getInt(AlarmItem.KEY_ROW_ID));
            alarm.setAlarmType(mCurrentAlarmItem.getInt(AlarmItem.KEY_TYPE));
            //alarm.setPeriodDate(mCurrentAlarmItem.getInt(AlarmItem.KEY_HOUR));
            alarm.setSoundId(mCurrentAlarmItem.getInt(AlarmItem.KEY_SOUND));
            alarm.setTime(mCurrentAlarmItem.getInt(AlarmItem.KEY_HOUR) + ":" + mCurrentAlarmItem.getInt(AlarmItem.KEY_MINUTE));
            final Intent intent = new Intent(SEND_EVENT_ALARM_INTENT, null);
            intent.putExtra(EXTRA_DATA, alarm);
            sendBroadcast(intent);*/
            AlarmModel alarm = new AlarmModel();
            alarm.setImei(mCurrentAlarmItem.getString(AlarmItem.KEY_IMEI));
            alarm.setName(mCurrentAlarmItem.getString(AlarmItem.KEY_NAME));
            alarm.setAlarmId(mCurrentAlarmItem.getInt(AlarmItem.KEY_ROW_ID));
            alarm.setAlarmType(mCurrentAlarmItem.getInt(AlarmItem.KEY_TYPE));
            //alarm.setPeriodDate(mCurrentAlarmItem.getInt(AlarmItem.KEY_HOUR));
            alarm.setSoundId(mCurrentAlarmItem.getInt(AlarmItem.KEY_SOUND));
            int minute = mCurrentAlarmItem.getInt(AlarmItem.KEY_MINUTE);
            int hour = mCurrentAlarmItem.getInt(AlarmItem.KEY_HOUR);
            String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
            String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
            alarm.setTime(hourStr + ":" + minuteStr);
            final Intent intent = new Intent(BaseBroadcast.SEND_EVENT_ALARM_INTENT, null);
            intent.putExtra(EXTRA_DATA, alarm);
            sendBroadcast(intent);
        }
    }

    private void actionForceLaunchAlarm() {
        if (mCurrentAlarmItem.getBool(AlarmItem.KEY_DO_NOT_LAUNCH_ON_CALL) && isPhoneNotIdle()) {
            stopOrSet();
        } else {
            launchAlarm();
        }
    }

    private void actionStopAlarm() {
        mIsPlayingBackup = false;
        stopSelf();
    }

    private boolean isPhoneNotIdle() {
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        return tm.getCallState() != TelephonyManager.CALL_STATE_IDLE;
    }

    private void launchAlarm() {
        mIsCounting = true;
        mHandler.postDelayed(mSetTask, 2000);
    }

    private void stopOrSet() {
        if (isSafeToStopSelf()) {
            stopSelf();
        } else {
            setNextAlarm();
        }
    }

    private Runnable mSetTask = new Runnable() {
        @Override
        public void run() {
            mIsCounting = false;
            try {
                setNextAlarm();
            } catch (Exception e) {
                Intent i = new Intent(getBaseContext(), AalService.class);
                i.setAction(ACTION_SET_SILENT_ALARM);
                startService(i);
            }
        }
    };

    private boolean isSafeToStopSelf() {
        return !(mIsPlayingBackup || mIsCounting);
    }

    private void setNextAlarm() {
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(sender);

        Cursor c = mDbAdapter.fetchEnabledAlarms();
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                long nextAlarmTimeInMilis = 0;
                long nextAlarmId = 0;

                AlarmItem ai;
                while (!c.isAfterLast()) {
                    ai = new AlarmItem(AlarmItem.ALARM_DEFAULTS_LIST, c);
                    if (nextAlarmTimeInMilis == 0) {
                        nextAlarmTimeInMilis = ai.getNextAbsoluteTimeInMillis();
                        nextAlarmId = ai.getInt(AlarmItem.KEY_ROW_ID);
                    } else {
                        long atim = ai.getNextAbsoluteTimeInMillis();
                        Log.i("", atim + " : " + nextAlarmTimeInMilis);
                        if (atim > 0 && atim < nextAlarmTimeInMilis) {
                            nextAlarmTimeInMilis = atim;
                            nextAlarmId = ai.getInt(AlarmItem.KEY_ROW_ID);
                        }
                    }
                    c.moveToNext();
                }
                saveNextAlarmPref(nextAlarmId);
                if (nextAlarmId != 0) {
                    sender = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nextAlarmTimeInMilis, sender);
                    Calendar aC = Calendar.getInstance();
                    aC.setTimeInMillis(nextAlarmTimeInMilis);
                    //String timeString = aC.getTime().toString();
                    //if (mShowAlarmToast) {
                    //Toast.makeText(this, "Next alarm set for:\n\n" + timeString, Toast.LENGTH_LONG).show();
                    //}
                    //    showAlarmSetNotification(timeString);
                } else {
                    mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
                }
            } else {
                mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
            }
            c.close();
        } else {
            mNotificationManager.cancel(NOTIFY_ID_ALARM_SET);
        }
    }

    private void saveNextAlarmPref(long alarmId) {
        saveNextAlarmPref(getBaseContext(), alarmId);
    }

    public static void saveNextAlarmPref(Context c, long alarmId) {
        SharedPreferences.Editor spe = c.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
        spe.putLong(PREF_KEY_NEXT_ALARM_ID, alarmId);
        spe.apply();
    }

    private long loadNextAlarmPref() {
        SharedPreferences sp = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        return sp.getLong(PREF_KEY_NEXT_ALARM_ID, 0);
    }
}
