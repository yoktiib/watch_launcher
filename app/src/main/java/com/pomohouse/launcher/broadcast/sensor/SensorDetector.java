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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;

import com.pomohouse.launcher.broadcast.sensor.listener.FallSensorListener;
import com.pomohouse.launcher.broadcast.sensor.listener.LightSensorListener;
import com.pomohouse.launcher.broadcast.sensor.listener.TwistSensorListener;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Detects steps and notifies all listeners (that implement StepListener).
 *
 * @author Levente Bagi
 */
public class SensorDetector implements SensorEventListener {
    private SensorManager mSensorManager;
    //final static int SENSOR_TYPE_GESTURE1 = 80;
    private final static int SENSOR_TYPE_GESTURE2 = 81;
    //final static int SENSOR_TYPE_GESTURE3 = 82;
    private final static int SENSOR_TYPE_GESTURE4 = 83;

    private LightSensorListener mLightSensorListener;
    private FallSensorListener mFallSensorListener;
    private TwistSensorListener mTwistSensorListener;

    private Context context;

    void setLightSensorListener(LightSensorListener mLightSensorListener) {
        this.mLightSensorListener = mLightSensorListener;
    }

    void setFallSensorListener(FallSensorListener mFallSensorListener) {
        this.mFallSensorListener = mFallSensorListener;
    }

    void setTwistSensorListener(TwistSensorListener mTwistSensorListener) {
        this.mTwistSensorListener = mTwistSensorListener;
    }

    /*private AlarmManager mAlarmManager;
    private Intent mAlarmIntent;
    private PendingIntent mPI;
    private void startRtcRepeatAlarm(Context mContext){
        mAlarmManager = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = new Intent(this, SensorService.class);
        mPI = PendingIntent.getService(this,0 ,mAlarmIntent ,0 );
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+60*1000,
                20*60*1000 ,mPI );


    }*/

    private File mFile ;
    private FileOutputStream mOutStream;
    private final String mFileName = "proximity_sensor.txt";
    private String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private synchronized void  saveProximityValue(String flag , int value){
       /* try {
            if(mOutStream == null) {
                mOutStream = new FileOutputStream(mFile);
            }
            String str = getCurrentTimeString()+" "+flag+" proximity : "+value+"\n";
            mOutStream.write(str.getBytes());
            mOutStream.flush();

        }catch (Exception ex){

        }*/

    }
    private String getCurrentTimeString(){
        long utcTimeMillis  = System.currentTimeMillis();
        Date gpsUTCDate = new Date(utcTimeMillis);
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//当地时间格式
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate);
        return localTime;
    }

    SensorDetector(Context mContext) {
        context = mContext;
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        if (mSensorManager == null)
            return;
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
       /* mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(SENSOR_TYPE_GESTURE2),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(SENSOR_TYPE_GESTURE4),
                SensorManager.SENSOR_DELAY_NORMAL);*/
     /*  mFile = new File(mFilePath,mFileName);
        try {
            if(!mFile.exists()){

                mFile.createNewFile();

            }else{
                mFile.delete();
                mFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

       /* mAlarmManager= (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmIntent = new Intent("RTC_WAKE_UP_CPU");
        mPI = PendingIntent.getBroadcast(mContext, 0 , mAlarmIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT );

        IntentFilter filter = new IntentFilter("RTC_WAKE_UP_CPU");
         mContext.registerReceiver(rtcReceiver,filter);*/

       IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mContext.registerReceiver(mReceiver,filter);

    }
    private BroadcastReceiver rtcReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            saveProximityValue("broadcase" , lightValue);
            handler.removeCallbacks(run);
            handler.post(run);
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           refreshRegisterProximitySensor();
        }
    };

    private void refreshRegisterProximitySensor(){
        mSensorManager.unregisterListener(this);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
    }


    void unRegisterAllListener() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
//        context.unregisterReceiver(rtcReceiver);
        context.unregisterReceiver(mReceiver);
    }

    private boolean oldWatchStatus = false;
    private boolean isHandlerProcess;
    private Handler handler = new Handler();
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            saveProximityValue("run ",lightValue);
            //Timber.i("Watch Status ===== " + watchStatus + " : " + oldWatchStatus);
            if (mLightSensorListener != null) {
                isHandlerProcess = false;
                boolean watchStatus = lightValue > 0;
                if (watchStatus == oldWatchStatus)
                    return;
                if (watchStatus) {
                    mLightSensorListener.onWatchOff();
                    oldWatchStatus = true;
                } else {
                    mLightSensorListener.onWatchOn();
                    oldWatchStatus = false;
                }
            }
        }
    };

    private void sendWatchOnOffStatus(){

        if (mLightSensorListener != null) {
            isHandlerProcess = false;
            boolean watchStatus = lightValue > 0;
            if (watchStatus == oldWatchStatus)
                return;
            if (watchStatus) {
                mLightSensorListener.onWatchOff();
                oldWatchStatus = true;
            } else {
                mLightSensorListener.onWatchOn();
                oldWatchStatus = false;
            }
        }

    }

    private int lightValue = 0;
    private boolean isFallHandlerProcess;
    private Handler handlerFall = new Handler();
    private Runnable runFall = new Runnable() {
        @Override
        public void run() {
            Timber.i("Fall ===== " + isFallHandlerProcess);
            isFallHandlerProcess = false;
        }
    };

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        Timber.i("Sensor Type : " + sensor.getType());
        switch (sensor.getType()) {
            case Sensor.TYPE_PROXIMITY:
//                lightValue = (int) event.values[0];
                int value = (int) event.values[0];
                if(value != lightValue){
                    lightValue = value;
                }else{
                    break;
                }
                Timber.i("TYPE_PROXIMITY = " + lightValue);
                if (handler != null && !isHandlerProcess) {
                    isHandlerProcess = true;
                    handler.removeCallbacks(run);
                    handler.postDelayed(run, 5000);
//                  sendWatchOnOffStatus();
//                    saveProximityValue("event", lightValue);
//                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mPI );
                }
                break;
            case SENSOR_TYPE_GESTURE2:
                if (mTwistSensorListener != null) {
                    if ((int) event.values[0] == 1) {
                        mTwistSensorListener.onTwistUp();
                        //Timber.d("", "zhjp  Gest2 , EventID= Rotate or Twist HAND  ");
                        Timber.d("zhjp,SENSOR_TYPE_GESTURE2 light up screen"); // hands up ,light up screen
                    }
                }
                break;
            case SENSOR_TYPE_GESTURE4:
                if (mFallSensorListener != null) {
                    if ((int) event.values[0] == 1) {
                        if (handlerFall != null && !isFallHandlerProcess) {
                            isFallHandlerProcess = true;
                            mFallSensorListener.onFall();
                            handlerFall.removeCallbacks(runFall);
                            handlerFall.postDelayed(runFall, 60 * 1000 * 30);
                        }
                        Timber.d("", "zhjp  Gest4 , EventID= Free Fall");
                    }
                }
                break;
           /* case SENSOR_TYPE_GESTURE3:
                switch ((int) event.values[0]) {
                    case 1:
                        Toast.makeText(mContext, "Front TAP", Toast.LENGTH_SHORT).show();
                        Timber.d("", "zhjp  Gest3 , Front TAP ");
                        break;
                    case 2:
                        Toast.makeText(mContext, "Back TAP", Toast.LENGTH_SHORT).show();
                        Timber.d("", "zhjp  Gest3 , Back TAP");
                        break;
                }
                break;*/
         /* case SENSOR_TYPE_GESTURE1:
                if ((int) event.values[0] == 1) {
                    Timber.d("", "zhjp  Gest1 , EventID= Raise HAND");// may be here hands up ,light up screen
                    Toast.makeText(mContext, "Hand UP", Toast.LENGTH_SHORT).show();
                    // Log.d(TAG, "zhjp,SENSOR_TYPE_GESTURE1 light up screen");
                    // }
                }else{
                    Toast.makeText(mContext, "Hand Down", Toast.LENGTH_SHORT).show();
                }
                break;*/
           /* case SENSOR_TYPE_CONTEXT_POSTURE:
                switch ((int) event.values[0]) {
                    case 1:
                        Timber.d("", "zhjp  CP , Unknown");
                        break;
                    case 2:
                        Timber.d("", "zhjp  CP , In Pocket");
                        break;
                }
                break;
            case SENSOR_TYPE_CONTEXT_MOTION:
                switch ((int) event.values[0]) {
                    case 1:
                        Timber.d("", "zhjp   CM , Unknown ");
                        break;
                    case 2:
                        Timber.d("", "zhjp   CM , Stationary ");
                        break;
                    case 3:
                        Timber.d("", "zhjp   CM , Not on Person ");
                        break;
                    case 4:
                        Timber.d("", "zhjp   CM , Waking Steps=" + event.values[1]
                                + ", values0 = " + event.values[0]);
                        walkStep = (int) event.values[1];
                        break;
                    case 5:

                        Timber.d("", "zhjp  CM , Running Steps=" + event.values[1]
                                + ", values0 = " + event.values[0]);
                        runStep = (int) event.values[1];
                        break;
                    case 6:
                        Timber.d("", "zhjp   CM , Jogging");
                        break;
                }
                break;
            case SENSOR_TYPE_CONTEXT_TRANSPORT:
                switch ((int) event.values[0]) {
                    case 1:
                        Timber.d("", "zhjp   CT , In Vehicle ");
                        break;
                    case 2:
                        Timber.d("", "zhjp   CT , Off Vehicle");
                        break;
                }
                break;*/
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}