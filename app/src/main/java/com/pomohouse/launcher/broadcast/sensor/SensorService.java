/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pomohouse.launcher.broadcast.sensor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.pomohouse.launcher.api.requests.ImeiRequest;
import com.pomohouse.launcher.api.requests.WearerStatusRequest;
import com.pomohouse.launcher.broadcast.BaseBroadcast;
import com.pomohouse.launcher.broadcast.sensor.listener.LightSensorListener;
import com.pomohouse.launcher.broadcast.sensor.presenter.ISensorPresenter;
import com.pomohouse.launcher.broadcast.sensor.presenter.SensorPresenterImpl;
import com.pomohouse.launcher.utils.CombineObjectConstance;
import com.pomohouse.library.WearerInfoUtils;

import timber.log.Timber;


/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link }
 * and {@link } classes show how to interact with the
 * service.
 * <p>
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class SensorService extends Service {
    private SensorDetector mSensorDetector;
    ISensorPresenter presenter;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    public static void stopSensorService(final Context context) {
        context.stopService(new Intent(BaseBroadcast.SEND_SENSOR_INTENT, null, context, SensorService.class));
    }

    public static void startSensorService(final Context context) {
        context.startService(new Intent(BaseBroadcast.SEND_SENSOR_INTENT, null, context, SensorService.class));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.e("Start Sensor");
        presenter = new SensorPresenterImpl();
        // Start detecting
        mSensorDetector = new SensorDetector(this);
        mSensorDetector.setLightSensorListener(new LightSensorListener() {
            @Override
            public void onWatchOff() {
                if (!CombineObjectConstance.getInstance().isWatchAlarm()) return;
                WearerStatusRequest wearerStatusRequest = new WearerStatusRequest();
               // wearerStatusRequest.setImei(WearerInfoUtils.getInstance().getImei());
                wearerStatusRequest.setWearerStatus("N");
                presenter.requestWearerStatus(wearerStatusRequest);
            }

            @Override
            public void onWatchOn() {
                if (!CombineObjectConstance.getInstance().isWatchAlarm()) return;
                WearerStatusRequest wearerStatusRequest = new WearerStatusRequest();
                //wearerStatusRequest.setImei(WearerInfoUtils.getInstance().getImei());
                wearerStatusRequest.setWearerStatus("Y");
                presenter.requestWearerStatus(wearerStatusRequest);
            }
        });
        mSensorDetector.setFallSensorListener(() -> presenter.requestFallService());
        mSensorDetector.setTwistSensorListener(() -> {
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "Loneworker - FULL WAKE LOCK");
            Timber.e("onResume powerManager: wake screen");
            if (!powerManager.isInteractive()) {
                fullWakeLock.acquire(3000);
            }
        });

        keepServiceForeground(SensorService.this);

    }

    private void keepServiceForeground(Service service){

            NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("SensorService")
                    .setContentText("")
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(false);
            Notification notification = builder.build();

            service.startForeground(88888,notification);
    }


    //wake lock
    private PowerManager.WakeLock fullWakeLock;
    private PowerManager powerManager;




    @Override
    public void onDestroy() {
        unregisterDetector();
        if (mSensorDetector != null)
            mSensorDetector.unRegisterAllListener();
        super.onDestroy();
        Timber.d("Service : Stop");
        stopForeground(true);
    }

    private void unregisterDetector() {
        if (mSensorDetector != null)
            mSensorDetector.unRegisterAllListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();
}

