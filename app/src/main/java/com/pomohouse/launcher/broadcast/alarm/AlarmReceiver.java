package com.pomohouse.launcher.broadcast.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.pomohouse.launcher.broadcast.BaseBroadcast;
import com.pomohouse.launcher.broadcast.callback.AlarmListener;
import com.pomohouse.launcher.models.AlarmModel;

/**
 * Created by Admin on 11/25/2016 AD.
 */

public class AlarmReceiver extends BaseBroadcast {
    private AlarmModel _alarmItem;
    private AlarmListener mAlarmListener;
    private static AlarmReceiver receiver;

    public static AlarmReceiver getInstance() {
        if (receiver == null)
            receiver = new AlarmReceiver();
        return receiver;
    }

    public void startAlarmReceiver(Context mContext) {
        if (mContext == null)
            return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SEND_EVENT_ALARM_INTENT);
        if (receiver != null)
            mContext.registerReceiver(receiver, filter);
    }

    public boolean stopAlarmService(Context mContext) {
        if (mContext == null)
            return false;
        if (receiver != null)
            mContext.unregisterReceiver(receiver);
        return true;
    }

    public void initAlarmListener(AlarmListener listener) {
        this.mAlarmListener = listener;
    }

    public void destroyAlarmListener() {
        this.mAlarmListener = null;
    }


    public AlarmModel getLastAlarm() {
        return _alarmItem;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(SEND_EVENT_ALARM_INTENT)) {
            _alarmItem = (AlarmModel) intent.getExtras().getSerializable(EXTRA_DATA);
            if (_alarmItem != null)
                mAlarmListener.onAlarmTimeListener(_alarmItem);
        }
    }
}
