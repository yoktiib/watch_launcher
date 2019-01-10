package com.pomohouse.launcher.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.CellTower;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.launcher.api.requests.WifiAccessPoint;
import com.pomohouse.launcher.api.requests.WifiLoc;
import com.pomohouse.launcher.manager.event.EventPrefManagerImpl;
import com.pomohouse.launcher.manager.event.IEventPrefManager;
import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.FitnessPrefModel;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.manager.ActivityContextor;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private Context mContext;

    WifiManager wifiManager;
    WifiBroadcastReceiver wifiReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("Start", "LocationService");
        this.mContext = context;
        iSettingManager = new SettingPrefManager(context);
        iFitnessPrefManager = new FitnessPrefManagerImpl(context);
        iEventPrefManager = new EventPrefManagerImpl(context);
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
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                Log.e(TAG, "Location :: " + location.getLatitude() + " : " + location.getLongitude());
                //   <PMHStart><147><K8><357450080116409><1.1><ILU><{"accuracy":25.0,"lat":19.039446,"lng":99.931521,"locationType":5,"power":45.0,"step":0}><234><PMHEnd>"
                LocationUpdateRequest locationData = new LocationUpdateRequest();
                locationData.setAccuracy(location.getAccuracy());
                locationData.setLat(location.getLatitude());
                locationData.setLng(location.getLongitude());
                locationData.setWifiAccessPoint(new ArrayList<>());
                locationData.setLocationType(location.getLocationType());
                requestEventInterval(locationData);
                if (locationClient != null) {
                    locationClient.stopLocation();
                    locationClient.disableBackgroundLocation(true);
                    locationClient = null;
                }
            } else {
                //Instantiate broadcast receiver
                wifiReceiver = new WifiBroadcastReceiver();
                //Register the receiver
                if (mContext != null)
                    mContext.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

            }
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
    private IEventPrefManager iEventPrefManager;

    private void requestEventInterval(LocationUpdateRequest locationInfo) {
        if (locationInfo == null) return;

        final long now = SystemClock.elapsedRealtime();// + (30 * 1000);
        if (iSettingManager == null) return;
        Timber.e((now - mLastLocationTime) + " : " + (iSettingManager.getSetting().getPositionTiming() - 60) * 1000);
        /*if (now - mLastLocationTime < ((iSettingManager.getSetting().getPositionTiming() - 60) * 1000) && mLastLocationTime != 0)
            return;*/
        if (iEventPrefManager != null)
            locationInfo.setEventList(new Gson().toJson(iEventPrefManager.getEvent().getListEvent()));
        locationInfo.setCellTower(getModemCell(mContext));
        if (now - mLastStepTime < (iSettingManager.getSetting().getStepSyncTiming() * 1000)) {

            //wifiAccessPoints
            //cellTowers
            //eventlist
            Timber.e("ignoring STEP_PERIOD until period has elapsed");
            locationInfo.setPower(getPowerLevel());
            TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_EVENT_AND_LOCATION, new Gson().toJson(locationInfo));
            mLastLocationTime = now;

        } else {
            Timber.e("updateFitnessService");
            updateFitnessService();
            new Handler().postDelayed(() -> {
                Timber.e("Get STEP_PERIOD");
                locationInfo.setPower(getPowerLevel());
                locationInfo.setStep(iFitnessPrefManager.getFitness().getStepForSync());
                TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_EVENT_AND_LOCATION, new Gson().toJson(locationInfo));
                mLastStepTime = now;
                mLastLocationTime = now;
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

    //Define class to listen to broadcasts
    class WifiBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            ArrayList<WifiAccessPoint> wifiList = new ArrayList<>();
            Log.d(TAG, "onReceive()");
            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (ok) {
                Log.d(TAG, "scan OK");
                List<ScanResult> results = wifiManager.getScanResults();
                for (ScanResult wifi : results) {
                    WifiAccessPoint accessPoint = new WifiAccessPoint();
                    accessPoint.setMAC(wifi.BSSID);
                    accessPoint.setRssi(wifi.level);
                    wifiList.add(accessPoint);
                }
            } else Log.d(TAG, "scan not OK");
            LocationUpdateRequest locationData = new LocationUpdateRequest();
            locationData.setWifiAccessPoint(wifiList);
            requestEventInterval(locationData);
        }
    }

    public ArrayList<WifiAccessPoint> getMacAddress() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();

        ArrayList<WifiAccessPoint> wifiList = new ArrayList<>();
        for (ScanResult wifi : results) {
            WifiAccessPoint accessPoint = new WifiAccessPoint();
            accessPoint.setMAC(wifi.BSSID);
            accessPoint.setRssi(wifi.level);
            wifiList.add(accessPoint);
        }
        /*try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                WifiAccessPoint wifiAccessPoint = new WifiAccessPoint();
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return new ArrayList<>();
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                wifiAccessPoint.setMAC(res1.toString());
            }
        } catch (Exception ignored) {
        }*/
        return wifiList;
    }

    /*[{"radioType":"gsm","MCC":460,"MNC":0,"lac":9365,"cellid":5132,"Rxlev":204},{"radioType":"gsm","MCC":460,"MNC":0,"lac":9365,"cellid":3823,"Rxlev":188},{"radioType":"gsm","MCC":460,"MNC":0,"lac":9365,"cellid":3821,"Rxlev":187},{"radioType":"gsm","MCC":460,"MNC":0,"lac":9365,"cellid":3831,"Rxlev":179},{"radioType":"gsm","MCC":460,"MNC":0,"lac":9365,"cellid":5131,"Rxlev":171}],"wifiAccessPoints":[{"MAC":"f0:b4:29:d8:3d:47","rssi":-57},{"MAC":"d4:ee:07:2d:f9:ba","rssi":-58},{"MAC":"e4:6f:13:31:06:cc","rssi":-62},{"MAC":"a8:6b:ad:95:04:17","rssi":-65},{"MAC":"30:fc:68:9e:d8:59","rssi":-70}]}*/

    public static ArrayList<CellTower> getModemCell(Context ctx) {
        //      通过MNC判断

        ArrayList<CellTower> towers = new ArrayList<>();
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

// Type of the network
        int phoneTypeInt = telManager.getPhoneType();
        String phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : "";
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;
        int mcc = 0;
        int mnc = 0;
        int lac = 0;
        int cid = 0;
        /*
        //电信2G
        int sid = 0;
        int nid = 0;
        int bid = 0;

         int Latitude = 0;
        int Longitude = 0;
        try {
            @SuppressLint("MissingPermission") CellLocation cel = telManager.getCellLocation();
            int nPhoneType = telManager.getPhoneType();
            //电信   CdmaCellLocation
            if (nPhoneType == 2 && cel instanceof CdmaCellLocation) {
                Log.d("电信", "-----------------》电信---2G基站");
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
                sid = cdmaCellLocation.getSystemId();
                nid = cdmaCellLocation.getNetworkId();
                bid = cdmaCellLocation.getBaseStationId();
                Latitude = cdmaCellLocation.getBaseStationLatitude();
                Longitude = cdmaCellLocation.getBaseStationLongitude();
            }
            Timber.e("2G基站一次来了-sid =" + sid + "\t nid =" + nid + "\t bid =" + bid + "\t Latitude =" + Latitude + "\t Longitude =" + Longitude);
        } catch (Exception e) {
            Timber.e("---Modem2Utils-----e=" + e);
        }*/
        // 获取所有基站信息
        @SuppressLint("MissingPermission") List<CellInfo> infos = telManager.getAllCellInfo();
        if (infos != null) {
            Timber.e("附近基站个数是=" + infos.size());
            //   Log.d(TAG, "附近基站信息是=" + infos);

            for (CellInfo i : infos) { // 根据邻区总数进行循环
                CellTower cellTower = new CellTower();
                if (i instanceof CellInfoLte) {
                    //        Log.d(TAG, "附近有效注册LTE基站信息是" + i.toString());
                    CellInfoLte cellInfoLte = (CellInfoLte) i;

                    CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                    CellSignalStrengthLte cellrsp = cellInfoLte.getCellSignalStrength();
                    int rsp = cellrsp.getDbm();


                    boolean isUse = cellInfoLte.isRegistered();

                    if (isUse) {
                        Timber.e("Effective registration of LTE base station information is " + cellIdentity.toString());

                        Timber.e("中国电信4G MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cid + "\t DBM = " + rsp);
                    }
                    lac = cellIdentity.getTac();
                    cid = cellIdentity.getPci();
                    cellIdentity.getMobileNetworkOperator();
                    cellTower.setMCC(cellIdentity.getMccString());
                    cellTower.setMNC(cellIdentity.getMncString());
                    cellTower.setLac(lac);
                    cellTower.setCellid(cid);
                    cellTower.setRadioType(phoneType);
                    cellTower.setRxlev(rsp);
                    towers.add(cellTower);
                }
            }
        }
        return towers;
    }
}
