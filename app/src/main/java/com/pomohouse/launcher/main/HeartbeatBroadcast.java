package com.pomohouse.launcher.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HeartbeatBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("Start", "LocationService");
        TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_UPDATE_HEARTBEAT, "{}");
        try {
            java.lang.Process process = Runtime.getRuntime().exec("system/bin/qmi_ap2cp_cmd");
            StringBuilder stringBuffer = new StringBuilder();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            process.waitFor();
            is.close();
            reader.close();
            process.destroy();
            Log.i(HeartbeatBroadcast.class.getName(), "" + stringBuffer.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        /*Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent);*/

    }
}
