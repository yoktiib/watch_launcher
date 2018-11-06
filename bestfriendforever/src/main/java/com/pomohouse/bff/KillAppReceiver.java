package com.pomohouse.bff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;


/**
 * Created by Admin on 8/24/16 AD.
 */
public class KillAppReceiver extends BroadcastReceiver {
    public final static String SEND_EVENT_KILL_APP = "com.pomohouse.KILL_APP";
    private static KillAppReceiver receiver;

    public static KillAppReceiver getInstance() {
        if (receiver == null)
            receiver = new KillAppReceiver();
        return receiver;
    }

    public void startEventReceiver(Context mContext) {
        if (mContext == null)
            return;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SEND_EVENT_KILL_APP);
        if (receiver != null)
            mContext.registerReceiver(receiver, filter);
    }

    public boolean stopEventService(Context mContext) {
        if (mContext == null)
            return false;
        if (receiver != null)
            mContext.unregisterReceiver(receiver);
        return true;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals(SEND_EVENT_KILL_APP)) {
            Process.killProcess(Process.myPid());
        }
    }
}
