package com.pomohouse.launcher.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.FitnessPrefModel;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.manager.ActivityContextor;

import timber.log.Timber;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class LocationBroadcast extends BroadcastReceiver {
    public static final String REQUEST_LOCATION = "REQUEST_LOCATION";
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private SensorManager mSensorManager;
    private ISettingManager iSettingManager;
    private final String TAG = LocationBroadcast.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("Start", "LocationService");
        iSettingManager = new SettingPrefManager(context);
        iFitnessPrefManager = new FitnessPrefManagerImpl(context);
        initLocation(context);
        /*Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent);*/
    }

    //public static boolean isLocationUpdate = true;

    private void initLocation(Context context) {
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }


    private AMapLocationClientOption getDefaultOption() {

        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(20000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = location -> {
        if (null != location) {
            Log.e(TAG, "Location :: " + location.getLatitude() + " : " + location.getLongitude());
            //   <PMHStart><147><K8><357450080116409><1.1><ILU><{"accuracy":25.0,"lat":19.039446,"lng":99.931521,"locationType":5,"power":45.0,"step":0}><234><PMHEnd>"
            LocationUpdateRequest locationData = new LocationUpdateRequest();
            locationData.setAccuracy(location.getAccuracy());
            locationData.setLat( location.getLatitude());
            locationData.setLng(location.getLongitude());
            locationData.setLocationType(location.getLocationType());
            requestEventInterval(locationData);
            if (locationClient != null) {
                locationClient.stopLocation();
                locationClient.disableBackgroundLocation(true);
                locationClient = null;
            }
            //     new Handler().postDelayed(this::initLocation, iSettingManager.getSetting().getPositionTiming() * 1000);
        }
    };

    /**
     * Start Of Event Function
     * Request API
     * Convert Data
     * Send Data To Broadcast
     * Insert To Database
     *
     * @param locationInfo
     */
    private long mLastStepTime = 0;
    private long mLastLocationTime = 0;
    private IFitnessPrefManager iFitnessPrefManager;

    private void requestEventInterval(LocationUpdateRequest locationInfo) {
        final long now = SystemClock.elapsedRealtime();// + (30 * 1000);
        if (iSettingManager == null) return;
        Timber.e((now - mLastLocationTime) + " : " + (iSettingManager.getSetting().getPositionTiming() - 60) * 1000);
        /*if (now - mLastLocationTime < ((iSettingManager.getSetting().getPositionTiming() - 60) * 1000) && mLastLocationTime != 0)
            return;*/
        if (now - mLastStepTime < (iSettingManager.getSetting().getStepSyncTiming() * 1000)) {
            Timber.e("ignoring STEP_PERIOD until period has elapsed");
            if (locationInfo != null) {
                locationInfo.setPower(getPowerLevel());
                TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_LOCATION_UPDATE, new Gson().toJson(locationInfo));
                mLastLocationTime = now;
            }
        } else {
            Timber.e("updateFitnessService");
            updateFitnessService();
            new Handler().postDelayed(() -> {
                if (locationInfo != null) {
                    Timber.e("Get STEP_PERIOD");
                    locationInfo.setPower(getPowerLevel());
                    locationInfo.setStep(iFitnessPrefManager.getFitness().getStepForSync());
                    TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_LOCATION_UPDATE, new Gson().toJson(locationInfo));
                    mLastStepTime = now;
                    mLastLocationTime = now;
                }
            }, 5000);
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Sensor sensor = event.sensor;
            Timber.i("Sensor Type : " + sensor.getType());
            if (Sensor.TYPE_STEP_COUNTER == sensor.getType()) {
                int curr = (int) event.values[0];
                Timber.i("curr => " + curr);
                FitnessPrefModel fitnessPrefModel = iFitnessPrefManager.getFitness();
                fitnessPrefModel.calculateStepSync(curr, null);
                fitnessPrefModel.getSyncStep();
                iFitnessPrefManager.addFitness(fitnessPrefModel);
                if (mSensorManager != null) mSensorManager.unregisterListener(sensorEventListener);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void updateFitnessService() {
        mSensorManager = (SensorManager) ActivityContextor.getInstance().getContext().getSystemService(SENSOR_SERVICE);
        if (mSensorManager != null) {
            mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private int getPowerLevel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager bm = (BatteryManager) ActivityContextor.getInstance().getContext().getSystemService(BATTERY_SERVICE);
            if (bm != null) {
                return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }
        }
        return 0;
    }
}
