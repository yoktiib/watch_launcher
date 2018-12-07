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

/*
package com.pomohouse.launcher.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
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
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;

import com.pomohouse.launcher.manager.fitness.FitnessPrefManagerImpl;
import com.pomohouse.launcher.manager.fitness.FitnessPrefModel;
import com.pomohouse.launcher.manager.fitness.IFitnessPrefManager;
import com.pomohouse.launcher.manager.settings.ISettingManager;
import com.pomohouse.launcher.manager.settings.SettingPrefManager;
import com.pomohouse.launcher.tcp.CMDCode;
import com.pomohouse.launcher.tcp.TCPSocketServiceProvider;
import com.pomohouse.library.manager.ActivityContextor;

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
    private static final String TAG = LocationBroadcast.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("LocationService", "Start");
        iSettingManager = new SettingPrefManager(context);
        iFitnessPrefManager = new FitnessPrefManagerImpl(context);
        initLocation(context);
        */
/*Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent);*//*

        //getLngAndLat(context);
        //  getModemCell(context);
    }

    public static void getModemCell(Context ctx) {
        //      通过MNC判断
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        */
/** 获取SIM卡的IMSI码
         * SIM卡唯一标识：IMSI 国际移动用户识别码（IMSI：International Mobile Subscriber Identification Number）是区别移动用户的标志，
         * 储存在SIM卡中，可用于区别移动用户的有效信息。IMSI由MCC、MNC、MSIN组成，其中MCC为移动国家号码，由3位数字组成，
         * 唯一地识别移动客户所属的国家，我国为460；MNC为网络id，由2位数字组成，
         * 用于识别移动客户所归属的移动网络，中国移动为00，中国联通为01,中国电信为03；MSIN为移动客户识别码，采用等长11位数字构成。
         * 唯一地识别国内GSM移动通信网中移动客户。所以要区分是移动还是联通，只需取得SIM卡中的MNC字段即可
         *//*

//        @SuppressLint("MissingPermission")
//        String imsi = telManager.getSubscriberId() + "getNetworkOperatorName=" + telManager.getNetworkOperatorName() + "\n";//直接获取移动运营商名称
//
//        Log.d(TAG, "---imsi---="+imsi);

        // 返回值MCC + MNC      460111140778615
        //移动联通
//        String operator = telManager.getNetworkOperator();
//        Log.d(TAG, "---operator---="+operator);    //46003
        //    int mcc = Integer.parseInt(operator.substring(0, 3));
        //    int mnc = Integer.parseInt(operator.substring(3,5));

        //电信4G
        int mcc = 0;
        int mnc = 0;
        int lac = 0;
        int cellId = 0;
        int cid = 0;

        //电信2G
        int sid = 0;
        int nid = 0;
        int bid = 0;

//        int Latitude = 0 ;
//        int Longitude = 0 ;
//        try {
//            @SuppressLint("MissingPermission")
//            CellLocation cel = telManager.getCellLocation();
//            int nPhoneType = telManager.getPhoneType();
//            //电信   CdmaCellLocation
//            if (nPhoneType == 2 && cel instanceof CdmaCellLocation) {
//                Log.d("电信", "-----------------》电信---2G基站");
//                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
//                sid = cdmaCellLocation.getSystemId();
//                nid = cdmaCellLocation.getNetworkId();
//                bid = cdmaCellLocation.getBaseStationId();
//                Latitude = cdmaCellLocation.getBaseStationLatitude();
//                Longitude = cdmaCellLocation.getBaseStationLongitude();
//
//            }
//            Log.e(TAG, "2G基站一次来了-sid =" + sid + "\t nid =" + nid + "\t bid =" + bid+ "\t Latitude =" + Latitude+ "\t Longitude =" + Longitude);
//        } catch (Exception e) {
//
//            Log.e(TAG,"---Modem2Utils-----e="+e);
//        }

        //Modem modem = new Modem();
        // 获取所有基站信息
        @SuppressLint("MissingPermission") List<CellInfo> infos = telManager.getAllCellInfo();
        if (infos != null) {
            Log.d(TAG, "附近基站个数是=" + infos.size());
            //   Log.d(TAG, "附近基站信息是=" + infos);
            //   ArrayList<> towers = new ArrayList<>(infos.size());
            for (CellInfo i : infos) { // 根据邻区总数进行循环

                if (i instanceof CellInfoLte) {
                    //        Log.d(TAG, "附近有效注册LTE基站信息是" + i.toString());
                    CellInfoLte cellInfoLte = (CellInfoLte) i;
                    CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                    CellSignalStrengthLte cellrsp = cellInfoLte.getCellSignalStrength();
                    int rsp = cellrsp.getDbm();

                    boolean isUse = cellInfoLte.isRegistered();

                    if (isUse) {
                        Log.d("", "有效注册LTE基站信息是" + cellIdentity.toString());
                        lac = cellIdentity.getTac();
                        cid = cellIdentity.getPci();
                        mcc = cellIdentity.getMcc();
                        mnc = cellIdentity.getMnc();

                        */
/*modem.setMcc(mcc);
                        modem.setMnc(mnc);
                        modem.setLac(lac);
                        modem.setCi(cid);*//*


                        Log.d(TAG, "中国电信4G MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cid + "\t DBM = " + rsp);
                    }
                }

                if (i instanceof CellInfoCdma) {
                    Log.d(TAG, "现在是cdma基站---CellInfoCdma=" + i.toString());
                    //    CellIdentityCdma cellIdentityCdma = ((CellInfoCdma)i).getCellIdentity();
                    //     if(cellIdentityCdma==null)continue;
//                     sid = cellIdentityCdma.getSystemId();//cdma用sid,是系统识别码，每个地级市只有一个sid，是唯一的。
//                     nid = cellIdentityCdma.getNetworkId();//NID是网络识别码，由各本地网管理，也就是由地级分公司分配。每个地级市可能有1到3个nid。
//                     bid = cellIdentityCdma.getBasestationId();//cdma用bid,表示的是网络中的某一个小区，可以理解为基站。

                    @SuppressLint("MissingPermission") CellLocation cel = telManager.getCellLocation();
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
                    sid = cdmaCellLocation.getSystemId();
                    nid = cdmaCellLocation.getNetworkId();
                    bid = cdmaCellLocation.getBaseStationId();

                    Log.e(TAG, "2G-cdma基站2次来了-sid =" + sid + "\t nid =" + nid + "\t bid =" + bid);
                }

            }
        }
        //           ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        //  通过联网方式判断
//        @SuppressLint("MissingPermission")
//        NetworkInfo info2 = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        Log.i(TAG,
//                "getDetailedState=" + info2.getDetailedState() + "\n" +
//                        "getReason=" + info2.getReason() + "\n" +
//                        "getSubtype=" + info2.getSubtype() + "\n" +
//                        "getSubtypeName=" + info2.getSubtypeName() + "\n" +
//                        "getExtraInfo=" + info2.getExtraInfo() + "\n" +
//                        "getTypeName=" + info2.getTypeName() + "\n" +
//                        "getType=" + info2.getType()
//        );

        // bid最后一个参数可能获取到0，此2G基站信息无效
        // nid取值可以为0;   460-13835-00000-06880
        if (sid == 0 || bid == 0) {
            Log.e(TAG, "-获取到2g基站-参数任意一个为0-都设为无效2g基站-000--");
            sid = bid = 0;
        }

        //add by shipeixian begin
        */
/*modem.setSid(sid);
        modem.setNid(nid);
        modem.setBid(bid);*//*

        //add by shipeixian end
        //Log.e(TAG,"-获取到基站-modem---="+modem.toString());
        //return modem;
    }

    public void getLocation(Context ctx) {
        int mcc = 0;
        int mnc = 0;
        int lac = 0;
        int cellId = 0;
        int cid = 0;

        //电信2G
        int sid = 0;
        int nid = 0;
        int bid = 0;
        TelephonyManager telManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> infos = telManager.getAllCellInfo();
        if (infos != null) {
            Log.d(TAG, "附近基站个数是=" + infos.size());
            //   Log.d(TAG, "附近基站信息是=" + infos);
            //   ArrayList<> towers = new ArrayList<>(infos.size());
            for (CellInfo i : infos) { // 根据邻区总数进行循环

                if (i instanceof CellInfoLte) {
                    //        Log.d(TAG, "附近有效注册LTE基站信息是" + i.toString());
                    CellInfoLte cellInfoLte = (CellInfoLte) i;
                    CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                    CellSignalStrengthLte cellrsp = cellInfoLte.getCellSignalStrength();
                    int rsp = cellrsp.getDbm();

                    boolean isUse = cellInfoLte.isRegistered();

                    if (isUse) {
                        Log.d(TAG, "有效注册LTE基站信息是" + cellIdentity.toString());
                        lac = cellIdentity.getTac();
                        cid = cellIdentity.getPci();
                        mcc = cellIdentity.getMcc();
                        mnc = cellIdentity.getMnc();
*/
/*

                        modem.setMcc(mcc);
                        modem.setMnc(mnc);
                        modem.setLac(lac);
                        modem.setCi(cid);
*//*


                        Log.d(TAG, "中国电信4G MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cid + "\t DBM = " + rsp);
                    }
                }

                if (i instanceof CellInfoCdma) {
                    Log.d(TAG, "现在是cdma基站---CellInfoCdma=" + i.toString());
                    //    CellIdentityCdma cellIdentityCdma = ((CellInfoCdma)i).getCellIdentity();
                    //     if(cellIdentityCdma==null)continue;
//                     sid = cellIdentityCdma.getSystemId();//cdma用sid,是系统识别码，每个地级市只有一个sid，是唯一的。
//                     nid = cellIdentityCdma.getNetworkId();//NID是网络识别码，由各本地网管理，也就是由地级分公司分配。每个地级市可能有1到3个nid。
//                     bid = cellIdentityCdma.getBasestationId();//cdma用bid,表示的是网络中的某一个小区，可以理解为基站。

                    @SuppressLint("MissingPermission") CellLocation cel = telManager.getCellLocation();
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cel;
                    sid = cdmaCellLocation.getSystemId();
                    nid = cdmaCellLocation.getNetworkId();
                    bid = cdmaCellLocation.getBaseStationId();

                    Log.e(TAG, "2G-cdma基站2次来了-sid =" + sid + "\t nid =" + nid + "\t bid =" + bid);
                }

            }
        }
    }

    private String getLngAndLat(Context context) {
        double latitude = 0.0;
        double longitude = 0.0;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d("LocationService", "Location : " + latitude + "," + longitude);
            } else {
                Log.e("LocationService", "Can't Location");
                //return getLngAndLatWithNetwork();
            }
        } else {
            Log.e("LocationService", "GPS OFF");
        }
        return longitude + "," + latitude;
    }

    public static boolean isLocationUpdate = true;

    private void initLocation(Context context) {
        if (locationClient == null) locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();
    }


    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(true);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        //mOption.setInterval(3 * (60 * 1000));//可选，设置定位间隔。默认为2秒
        //mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    */
/**
     * 定位监听
     *//*

    AMapLocationListener locationListener = location -> {
        if (null != location) {
            Log.e(TAG, "Location :: " + location.getLatitude() + " : " + location.getLongitude());

            //   <PMHStart><147><K8><357450080116409><1.1><ILU><{"accuracy":25.0,"lat":19.039446,"lng":99.931521,"locationType":5,"power":45.0,"step":0}><234><PMHEnd>"
            if (location.getLatitude() == 0.0 || location.getLongitude() == 0.0) return;

            LocationUpdateRequest locationData = new LocationUpdateRequest();
            */
/*locationData.setAccuracy(25.0f);//location.getAccuracy());
            locationData.setLat(19.039446);//location.getLatitude());
            locationData.setLng(99.931521);//location.getLongitude());
            locationData.setLocationType(5);//location.getLocationType());*//*

            locationData.setAccuracy(location.getAccuracy());
            locationData.setLat(location.getLatitude());
            locationData.setLng(location.getLongitude());
            locationData.setLocationType(location.getLocationType());
            requestEventInterval(locationData);
            //if (locationClient != null) {
                */
/*locationClient.stopLocation();
                locationClient.disableBackgroundLocation(true);
                locationClient = null;*//*

            //}
            //     new Handler().postDelayed(this::initLocation, iSettingManager.getSetting().getPositionTiming() * 1000);
        }
    };

    */
/**
     * Start Of Event Function
     * Request API
     * Convert Data
     * Send Data To Broadcast
     * Insert To Database
     *
     * @param locationInfo
     *//*

    private long mLastStepTime = 0;
    private long mLastLocationTime = 0;
    private IFitnessPrefManager iFitnessPrefManager;

    private void requestEventInterval(LocationUpdateRequest locationInfo) {
        final long now = SystemClock.elapsedRealtime();// + (30 * 1000);
        if (iSettingManager == null) return;
        Timber.e((now - mLastLocationTime) + " : " + (iSettingManager.getSetting().getPositionTiming() - 60) * 1000);
        */
/*if (now - mLastLocationTime < ((iSettingManager.getSetting().getPositionTiming() - 60) * 1000) && mLastLocationTime != 0)
            return;*//*

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
*/
