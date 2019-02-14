package com.pomohouse.launcher.main;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;

import com.google.gson.Gson;
import com.pomohouse.launcher.api.requests.CellTower;
import com.pomohouse.launcher.api.requests.LocationUpdateRequest;
import com.pomohouse.launcher.api.requests.WifiAccessPoint;
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.content.Context.BATTERY_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class LocationBroadcast extends BroadcastReceiver {
    public static final String REQUEST_LOCATION = "REQUEST_LOCATION";

    private SensorManager mSensorManager;
    private ISettingManager iSettingManager;
    private final String TAG = LocationBroadcast.class.getName();
    private Context mContext;

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
        /*locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        locationClient.startLocation();*/
        //Instantiate broadcast receiver
        //LocationUpdateRequest locationData = new LocationUpdateRequest();
        //wifiReceiver = new WifiBroadcastReceiver();
        //Register the receiver
        /*if (mContext != null)
            mContext.getApplicationContext().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));*/
        getMacAddress();
        //requestEventInterval(locationData);
    }

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
        getCellInfo(mContext);
        locationInfo.setCellTower(getCellInfo(mContext));
        if (now - mLastStepTime < (iSettingManager.getSetting().getStepSyncTiming() * 1000)) {

            //wifiAccessPoints
            //cellTowers
            //eventlist
            Timber.e("ignoring STEP_PERIOD until period has elapsed");
            locationInfo.setPower(getPowerLevel());
            TCPSocketServiceProvider.getInstance().sendLocation(CMDCode.CMD_EVENT_AND_LOCATION, new Gson().toJson(locationInfo));
            mLastLocationTime = now;
            onK8ManageNet();
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
                onK8ManageNet();
            }, 5000);
        }
    }

    public void onK8ManageNet(){

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
            Log.i(LocationBroadcast.class.getName(), "" + stringBuffer.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
            /*boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (ok) {*/
            //Log.d(TAG, "scan OK");
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> results = wifiManager.getScanResults();
            LocationUpdateRequest locationData = new LocationUpdateRequest();
            for (ScanResult wifi : results) {
                WifiAccessPoint accessPoint = new WifiAccessPoint();
                accessPoint.setMAC(wifi.BSSID);
                accessPoint.setRssi(wifi.level);
                wifiList.add(accessPoint);
                /*}*/
                /*} else Log.d(TAG, "scan not OK");*/
            }
            locationData.setWifiAccessPoint(wifiList);
            requestEventInterval(locationData);
        }
    }

    public ArrayList<WifiAccessPoint> getMacAddress() {
        Log.d(TAG, "onReceive()");
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> results = wifiManager.getScanResults();

        ArrayList<WifiAccessPoint> wifiList = new ArrayList<>();
        LocationUpdateRequest locationData = new LocationUpdateRequest();
        for (ScanResult wifi : results) {
            WifiAccessPoint accessPoint = new WifiAccessPoint();
            accessPoint.setMAC(wifi.BSSID);
            accessPoint.setRssi(wifi.level);
            wifiList.add(accessPoint);
            /*}*/
            /*} else Log.d(TAG, "scan not OK");*/
        }
        locationData.setWifiAccessPoint(wifiList);
        requestEventInterval(locationData);
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

    public static ArrayList<CellTower> getCellInfo(Context ctx) {
        ArrayList<CellTower> towers = new ArrayList<>();
        try {
            TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);


// Type of the network
            int phoneTypeInt = tel.getPhoneType();
            String plmn = tel.getNetworkOperator();

            int mcc = Integer.parseInt(plmn.substring(0, 3));
            int mnc = Integer.parseInt(plmn.substring(3, plmn.length()));

            System.out.println("plmn[" + plmn + "] mcc[" + mcc + "] mnc[" + mnc + "]");
            String phoneType = null;
            phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
            phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_CDMA ? "cdma" : phoneType;

            //from Android M up must use getAllCellInfo
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                List<NeighboringCellInfo> neighCells = tel.getNeighboringCellInfo();
                for (int i = 0; i < neighCells.size(); i++) {
                    try {
                        NeighboringCellInfo thisCell = neighCells.get(i);
                        CellTower cellTower = new CellTower();
                        cellTower.setMCC(mcc);
                        cellTower.setMNC(mnc);
                        cellTower.setLac(thisCell.getLac());
                        cellTower.setCellid(thisCell.getCid());
                        cellTower.setRadioType(phoneType);
                        cellTower.setRxlev(thisCell.getRssi());
                        towers.add(cellTower);
                    /*JSONObject cellObj = new JSONObject();
                    cellObj.put("cellId", thisCell.getCid());
                    cellObj.put("lac", thisCell.getLac());
                    cellObj.put("rssi", thisCell.getRssi());
                    cellList.put(cellObj);*/
                    } catch (Exception ignored) {
                    }
                }
            } else {
                List<CellInfo> infos = tel.getAllCellInfo();
                for (int i = 0; i < infos.size(); ++i) {

                    try {
                        //JSONObject cellObj = new JSONObject();
                        CellInfo info = infos.get(i);
                        if (info instanceof CellInfoGsm) {
                            CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                            CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        /*cellObj.put("cellId", identityGsm.getCid());
                        cellObj.put("lac", identityGsm.getLac());
                        cellObj.put("dbm", gsm.getDbm());*/
                            Timber.e("Log Cell CellInfoGsm");
                            CellTower cellTower = new CellTower();
                            cellTower.setMCC(mcc);
                            cellTower.setMNC(mnc);
                            cellTower.setLac(identityGsm.getLac());
                            cellTower.setCellid(identityGsm.getCid());
                            cellTower.setRadioType(phoneType);
                            cellTower.setRxlev(gsm.getDbm());
                            towers.add(cellTower);
                        } else if (info instanceof CellInfoLte) {
                            Timber.e("Log Cell CellInfoLte");
                            CellTower cellTower = new CellTower();
                            CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                            CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                            cellTower.setMCC(mcc);
                            cellTower.setMNC(mnc);
                            cellTower.setLac(identityLte.getTac());
                            cellTower.setCellid(identityLte.getCi());
                            cellTower.setRadioType(phoneType);
                            cellTower.setRxlev(lte.getDbm());
                            towers.add(cellTower);
                        }

                    } catch (Exception ex) {

                    }
                }
            }
        } catch (Exception ex) {
            return towers;
        }
        return towers;
    }

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
                if (i instanceof CellInfoLte) Timber.e("CellInfoLte");

                if (i instanceof CellInfoGsm) Timber.e("CellInfoGsm");

                if (i instanceof CellInfoCdma) Timber.e("CellInfoCdma");

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
                    Timber.e("Lac " + cellIdentity.getTac());
                    Timber.e("Cid " + cellIdentity.getPci());
                    cellIdentity.getMobileNetworkOperator();
                    /*cellTower.setMCC(cellIdentity.getMccString());
                    cellTower.setMNC(cellIdentity.getMncString());*/
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
