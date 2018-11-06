package com.pomohouse.launcher.broadcast.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, AalService.class);
		i.setAction(AalService.ACTION_SET_SILENT_ALARM);
		context.startService(i);
	}

}
