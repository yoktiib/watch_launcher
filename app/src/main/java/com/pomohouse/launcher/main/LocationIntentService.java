/*
package com.pomohouse.launcher.main;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.pomohouse.launcher.models.EventDataInfo;
import com.pomohouse.library.networks.MetaDataNetwork;

import timber.log.Timber;

import static com.pomohouse.launcher.broadcast.BaseBroadcast.SEND_EVENT_UPDATE_INTENT;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_EXTRA;
import static com.pomohouse.launcher.main.presenter.LauncherPresenterImpl.EVENT_STATUS_EXTRA;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_REFRESH_LOCATION_CODE;
import static com.pomohouse.launcher.utils.EventConstant.EventLocal.EVENT_UPDATE_LOCATION_CODE;

*/
/**
 * Created by Admin on 6/3/2017 AD.
 *//*


public class LocationIntentService extends IntentService {
    public static boolean isRefreshLocation = false;
    public static String locationRefreshEndpoint = "";

    public LocationIntentService() {
        super("LocationIntentService");
    }

    */
/**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     *//*

    public LocationIntentService(String name) {
        super(name);
    }

    */
/**
     * Called when a new location update is available.
     *//*

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (LocationResult.hasResult(intent)) {
                LocationResult locationResult = LocationResult.extractResult(intent);
                Location location = locationResult.getLastLocation();
                //yangyu add begin

                Timber.i("location location="+location);
                LocationCacheManager.getInstance().initContext(getApplicationContext());
                location = LocationCacheManager.getInstance().getLastCorrectLocation(location);
                Timber.i("location correct location="+location);
                LatLng mloc = new LatLng(location.getLongitude() , location.getLatitude());
                Timber.i("location getDistance="+getDistance(lastLocation ,mloc));
                lastLocation = mloc;

                */
/*LocationCacheManager.getInstance().setCurLocationExtras(location);
                Bundle extras = location.getExtras();
                Timber.i("location extras="+extras);
                if (extras != null && extras.containsKey("networkLocationType")) {
                    String type = extras.getString("networkLocationType");
                    if (type .equals("cell")) {
                        // Cell (2G/3G/LTE) is used.
                        Timber.i("location type=cell");
                    } else if (type .equals("wifi")) {
                        // Wi-Fi is used.
                        Timber.i("location type=wifi");
                    }
                }*//*

                //yangyu add end
                if (location != null) {
                    //Timber.e("Updated location: " + location.toString() + " : Provider " + location.getProvider());
                    EventDataInfo eventContent = new EventDataInfo();
                    if (LocationIntentService.isRefreshLocation)
                        eventContent.setEventCode(EVENT_REFRESH_LOCATION_CODE);
                    else
                        eventContent.setEventCode(EVENT_UPDATE_LOCATION_CODE);
                    eventContent.setContent(new Gson().toJson(location));
//                    Timber.i("location gson="+(new Gson().toJson(location)));
                    final Intent intentEvent = new Intent(SEND_EVENT_UPDATE_INTENT);
                    intentEvent.putExtra(EVENT_STATUS_EXTRA, new MetaDataNetwork(0, "success"));
                    intentEvent.putExtra(EVENT_EXTRA, eventContent);
                    sendBroadcast(intentEvent);
                }
            }
        } catch (Exception ignore) {
            Timber.i("location exception="+ignore);
            ignore.printStackTrace();
        }
    }


    private static LatLng lastLocation = null;
    */
/**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 米
     *//*

    public static double getDistance(LatLng start,LatLng end){
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
        public LatLng(double longitude,double latitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

}
*/
