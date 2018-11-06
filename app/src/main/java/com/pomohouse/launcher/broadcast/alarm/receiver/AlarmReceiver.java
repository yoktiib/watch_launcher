package com.pomohouse.launcher.broadcast.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent arg1) {
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "AppAlarmReceiver");
        wl.acquire(120000);
        Intent i = new Intent(ctx, AalService.class);
        i.setAction(AalService.ACTION_LAUNCH_ALARM);
        ctx.startService(i);
    }
}
