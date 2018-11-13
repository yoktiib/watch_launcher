package com.pomohouse.launcher.main;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.FitnessPrefModel;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.OnTCPStatusListener;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.WearerInfoUtils;
import com.pomohouse.library.manager.ActivityContextor;

import timber.log.Timber;

public class LocationService extends Service {
    private final IBinder myBinder = new LocationService.LocalBinder();
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private SensorManager mSensorManager;
    private final String TAG = LocationService.class.getName();

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "Start Location");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*if (isInstanceCreated()) {
            return START_STICKY_COMPATIBILITY;
        }*/
        super.onStartCommand(intent, flags, startId);
        initLocation();
        return START_STICKY;
    }

    private void initLocation() {
        iSettingManager = new SettingPrefManager(this);
        iFitnessPrefManager = new FitnessPrefManagerImpl(this);
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
        /*//启动后台定位
        locationClient.enableBackgroundLocation();*/
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(15000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(3 * (60 * 1000));//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
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
            Log.e(TAG,"Location :: "+location.getLatitude());
            LocationUpdateRequest locationData = new LocationUpdateRequest();
            locationData.setAccuracy(location.getAccuracy());
            locationData.setLat(location.getLatitude());
            locationData.setLng(location.getLongitude());
            locationData.setLocationType(location.getLocationType());
            requestEventInterval(locationData);
            //TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_LOCATION_UPDATE,"");
          /*  StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("地    址    : " + location.getDescription() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
           //     sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            sb.append("***定位质量报告***").append("\n");
            sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
            sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
            sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
            sb.append("****************").append("\n");
            //定位之后的回调时间
          //  sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

            //解析定位结果，
            String result = sb.toString();*/
            //tvResult.setText(result);
        } /*else {
            tvResult.setText("定位失败，loc is null");
        }*/
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
    private ISettingManager iSettingManager;
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
                TCPSocketServiceProvider.getInstance().sendLocation( CMDCode.CMD_LOCATION_UPDATE, new Gson().toJson(locationInfo));
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
            }, 3000);
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
