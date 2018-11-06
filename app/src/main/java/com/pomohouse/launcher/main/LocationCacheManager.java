package com.pomohouse.launcher.main;

import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by yangyu on 2018/5/25.
 */
public class LocationCacheManager {

    private static LocationCacheManager INSTANCE;
    private Context mContext;
    private WifiManager wifiManager;

    private final static int CACHE_LIST_CAPACITY = 5;
    private ArrayList<LocationCacheInfo> locationCacheInfoList = new ArrayList<>(CACHE_LIST_CAPACITY);

    public static LocationCacheManager getInstance(){
        if(INSTANCE == null){
            synchronized (LocationCacheManager.class){
                if(INSTANCE == null){
                    INSTANCE = new LocationCacheManager();
                }
            }
        }
        return  INSTANCE;
    }

    public void initContext(Context context){
        mContext = context;
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public List<ScanResult> getWifiScanResult(){
        return wifiManager.getScanResults();
    }


    /**
     * if have wifi scan result , will add wifi BSSID list to location extra
     * @param location
     */
    public void setCurLocationExtras(Location location){
        if(location == null){
            return;
        }
        Bundle extras = location.getExtras();
        if( extras == null){
            extras = new Bundle();

        }
        List<ScanResult> wifiList = getWifiScanResult();
        if(wifiList == null || wifiList.size() == 0){
            return;
        }
        String value = "";
        for(int i = 0 ; i < wifiList.size() ; i++) {
            if (i <= wifiList.size() - 2) {
                value = value + wifiList.get(0).BSSID + ",";
            } else {
                value = value + wifiList.get(0).BSSID;
            }
        }
        extras.putString("wifi_list", value);

        location.setExtras(extras);
    }


    private void addNewLocationToCache(Location location){
        if(locationCacheInfoList.size() == CACHE_LIST_CAPACITY){
            locationCacheInfoList.remove(0);
        }
        List<ScanResult> wifiScanResultList = wifiManager.getScanResults();
        LocationCacheInfo cacheInfo = new LocationCacheInfo();
        cacheInfo.setLocation(location);
        cacheInfo.setWifiScanResult(wifiScanResultList);
        cacheInfo.setTime(System.currentTimeMillis());
        locationCacheInfoList.add(cacheInfo);
    }







    public Location getLastCorrectLocation(Location latestLocationFromGoogle){
        addNewLocationToCache(latestLocationFromGoogle);
        compareCacheListAndReplaceWrongLocation();
        filterByTimeAndSpeed();

        if(locationCacheInfoList != null && locationCacheInfoList.size() > 0){
            return locationCacheInfoList.get(locationCacheInfoList.size() - 1).getLocation();
        }else{
            return  null;
        }

    }


    private void compareCacheListAndReplaceWrongLocation(){
       /* ArrayList<LocationCacheInfo> removeList = new ArrayList<>();
        ArrayList<LocationCacheInfo> wifiUseList = new ArrayList<>();
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 0){
            for(LocationCacheInfo info : locationCacheInfoList){
                if(info.getWifiScanResult() != null && info.getWifiScanResult().size() > 0){
                    wifiUseList.add(info);
                }
            }

        }
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 2){

        }*/

        filterByWifi();

    }


    private void filterByWifi(){
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 1){
            LocationCacheInfo lastInfo = locationCacheInfoList.get(locationCacheInfoList.size() - 1);
            if(lastInfo.getWifiScanResult() == null || lastInfo.getWifiScanResult().size() == 0){
                return;
            }
            int count = 0 ;
            LocationCacheInfo nearSameBssidItem = null;
            for(int i = locationCacheInfoList.size() - 2 ; i >= 0  ; i-- ){
                LocationCacheInfo temp = locationCacheInfoList.get(i);
                if(temp.getWifiScanResult() == null || temp.getWifiScanResult().size() == 0){
                    continue;
                }
                double gap = getDistance(new LatLng(lastInfo.getLocation()) , new LatLng(temp.getLocation()));
                if(haveSameWifiBssid(lastInfo.getWifiScanResult() , temp.getWifiScanResult() )){
                    if(nearSameBssidItem == null){
                        nearSameBssidItem = temp;
                    }
                    if(gap > 100){
                        Timber.e("filterByWifi , location have problem gap="+gap+",location="+new Gson().toJson(lastInfo.getLocation()));
                        count ++;
                    }
                }
            }

            if(count > 1){
//                locationCacheInfoList.remove(locationCacheInfoList.size() - 1);
//                locationCacheInfoList.add(nearSameBssidItem);
                lastInfo.getLocation().setLatitude(nearSameBssidItem.getLocation().getLatitude());
                lastInfo.getLocation().setLongitude(nearSameBssidItem.getLocation().getLongitude());


            }
        }
    }




    private void filterLocationByWifiForPhoneApp(){
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 1){
            int totalSize = locationCacheInfoList.size();
            List<LocationCacheInfo> removeList = new ArrayList<>();
            ArrayList<Integer> used_index = new ArrayList<>(totalSize);
            for(int i = 0 ; i < totalSize ; i++){
                boolean isUsed = false;
                for(int indx : used_index){
                    if(indx == i){
                        isUsed = true;
                        break;
                    }
                }
                if(isUsed){
                    continue;
                }
                LocationCacheInfo item = locationCacheInfoList.get(i);
                if(item.getWifiScanResult() == null || item.getWifiScanResult().size() == 0){
                    continue;
                }
                List<LocationCacheInfo> sameBssidList = new ArrayList<>();
                for(int j = i + 1 ; j < totalSize ; j++){
                    boolean haveSameBssid = haveSameWifiBssid(item.getWifiScanResult() , locationCacheInfoList.get(j).getWifiScanResult());
                    if(haveSameBssid){
                        used_index.add(j);
                        sameBssidList.add(locationCacheInfoList.get(j));
                    }
                }
                removeList.addAll(getShouldDeleteWifiPointsForPhoneApp(sameBssidList));
            }
            locationCacheInfoList.removeAll(removeList);




        }
    }

    private List<LocationCacheInfo> getShouldDeleteWifiPointsForPhoneApp(List<LocationCacheInfo> sameBssidList){
        if(sameBssidList == null || sameBssidList.size() <= 1){
            return null;
        }
        List<LocationCacheInfo> shouldRemoveList = new ArrayList<>();
        for(int i = 0 ; i < sameBssidList.size() ; i++){
            LocationCacheInfo item =  sameBssidList.get(i);
            int count = 0 ;
           for(int j = 0 ; j < sameBssidList.size(); j++){
               if(shouldRemoveList.contains(sameBssidList.get(j))){
                   continue;
               }
               double distance = getDistance(new LatLng(item.getLocation()) , new LatLng(sameBssidList.get(j).getLocation()));
               if(distance > 100){
                   count++;
               }
           }
           if(count > 1){
               shouldRemoveList.add(item);
           }

        }

        return shouldRemoveList;
    }


    private void filterLocationBySpeedForPhoneApp(){
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 1){
            List<LocationCacheInfo> removeList = new ArrayList<>();
            for(int i = 1; i < locationCacheInfoList.size()-1 ; i++){
                LocationCacheInfo item2 = locationCacheInfoList.get(i);
                LocationCacheInfo item1 = locationCacheInfoList.get(i-1);
                LocationCacheInfo item3 = locationCacheInfoList.get(i+1);
                if(!isSpeedOk(item2 , item1)&& !isSpeedOk(item2 , item3)){
                    removeList.add(item2);
                }

            }
        }

    }




    private void filterByTimeAndSpeed(){
        if(locationCacheInfoList != null && locationCacheInfoList.size() > 1){
            LocationCacheInfo lastLocationCache = locationCacheInfoList.get(locationCacheInfoList.size() - 1);
            if(lastLocationCache.getWifiScanResult() != null && lastLocationCache.getWifiScanResult().size() > 0){
                return;
            }
            LocationCacheInfo aheadCache = locationCacheInfoList.get(locationCacheInfoList.size() - 2);
            double distance = getDistance(new LatLng(lastLocationCache.getLocation()) , new LatLng(aheadCache.getLocation()));
            long timeGap = (lastLocationCache.getTime() - aheadCache.getTime())/1000; //seconds
            double speed = distance/timeGap;
            float accuracy1 = lastLocationCache.getLocation().getAccuracy();
            float accuracy2 = aheadCache.getLocation().getAccuracy();
            if(speed > 33){
                locationCacheInfoList.remove(locationCacheInfoList.size() - 1);
            }
            if( accuracy1 >  500){
                if(accuracy2 + accuracy1 < distance){

                }
            }

        }


    }


    private boolean isSpeedOk(LocationCacheInfo locationCacheInfo1 , LocationCacheInfo locationCacheInfo2){
        boolean result = true;
        double distance = getDistance(new LatLng(locationCacheInfo1.getLocation()) , new LatLng(locationCacheInfo2.getLocation()));
        long timeGap = Math.abs(locationCacheInfo1.getTime() - locationCacheInfo2.getTime())/1000; //seconds
        double speed = distance/timeGap;
        float accuracy1 = locationCacheInfo1.getLocation().getAccuracy();
        float accuracy2 = locationCacheInfo2.getLocation().getAccuracy();
        if(speed > 33){  //120KM/h
            result = false;
        }

        return result;
    }


    private boolean haveSameWifiBssid(List<ScanResult> wifiList1 , List<ScanResult> wifiList2){
        boolean result =false;
        if(wifiList1 == null || wifiList2 == null || wifiList1.size() == 0 || wifiList2.size() == 0){
            return result;
        }
        for(ScanResult item1 : wifiList1){
            for(ScanResult item2 : wifiList2){
                if(item1.BSSID.equals(item2.BSSID)){
                    result = true;
                    return result;
                }
            }
        }
        return result;
    }




    /**
     * 根据相邻三个点角度过滤
     *for phone app
     * @param datas
     */
    private void filterAngle(List<Location> datas) {
        List<Location> removePoints = new ArrayList<>();
        for (int i = 0; i < datas.size() - 2; i++) {
            Location firstLoc = datas.get(i);
            Location nexLocation = datas.get(i + 1);
            Location threeLocation = datas.get(i + 2);
            double gapAngle = angle(new LatLng(nexLocation), new LatLng(firstLoc), new LatLng(threeLocation));
            if (gapAngle < 45 || gapAngle > 315) {
                double gap = getDistance(new LatLng(firstLoc), new LatLng(nexLocation));
//                double gap2 = AMapUtils.calculateLineDistance(nexLocation.getLaLng(),threeLocation.getLaLng());
//                double bil = gap/(gap+gap2);
//                if (Math.abs(0.5-bil)<0.1)
//                {
                if (gap < 500 &&
                        !(nexLocation.getProvider().equals("wifi") || nexLocation.getProvider().equals("gps"))) {
                    i++;
                    removePoints.add(nexLocation);
                }
//                }
//                removePoints.add(threeLocation);
            }
        }
//        lineLocation.removeAll(removePoints);
//        locations.removeAll(removePoints);
        datas.removeAll(removePoints);
    }



    public static double angle(LatLng cen, LatLng first, LatLng second) {
        double ma_x = first.latitude - cen.latitude;
        double ma_y = first.longitude - cen.longitude;
        double mb_x = second.latitude - cen.latitude;
        double mb_y = second.longitude - cen.longitude;
        double v1 = (ma_x * mb_x) + (ma_y * mb_y);
        double ma_val = Math.sqrt(ma_x * ma_x + ma_y * ma_y);
        double mb_val = Math.sqrt(mb_x * mb_x + mb_y * mb_y);
        double cosM = v1 / (ma_val * mb_val);
        double angleAMB = Math.acos(cosM) * 180 / Math.PI;
        return angleAMB;
    }



    /**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 米 meter
     */
    public static double getDistance(LatLng start, LatLng end){
        if(start == null || end == null){
            return -1;
        }
        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;

        return d*1000;
    }

    private static class LatLng{
        double longitude;
        double latitude;
        public LatLng(double longitude, double latitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public LatLng(Location location){
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }
    }


}
