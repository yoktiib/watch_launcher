package com.pomohouse.bff;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.pomohouse.bff.base.LocationListener;
import com.pomohouse.bff.dao.LocationServiceData;


/**
 * Created by Admin on 9/8/16 AD.
 */
public class LocationBroadcast extends BroadcastReceiver {
    private final static String LOCATION_ACTION = "com.pomohouse.service.LOCATION_PROVIDER";
    private LocationListener mListener;
    private LocationServiceData _lastLocation;
    private Context mContext;
    private static LocationBroadcast mLocationBroadcast;

    public LocationBroadcast() {
    }

    public LocationBroadcast(Context mContext, LocationListener listener) {
        super();
        this.mListener = listener;
        this.mContext = mContext;
    }

    public LocationBroadcast(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public static LocationBroadcast getInstance(Context mContext, LocationListener listener) {
        if (mLocationBroadcast == null)
            mLocationBroadcast = new LocationBroadcast(mContext, listener);
        return mLocationBroadcast;
    }

    public static LocationBroadcast getInstance(Context mContext) {
        if (mLocationBroadcast == null)
            mLocationBroadcast = new LocationBroadcast(mContext);
        return mLocationBroadcast;
    }

    public LocationBroadcast startLocationService() {
        if (mContext == null)
            return null;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(LOCATION_ACTION);
        mContext.registerReceiver(this, filter);
        return mLocationBroadcast;
    }

    public void initLocationListener(LocationListener listener) {
        this.mListener = listener;
    }

    public void destroyLocationListener() {
        if (mContext == null)
            return;
        this.mListener = null;
        final IntentFilter filter = new IntentFilter();
        filter.addAction(LOCATION_ACTION);
        mContext.registerReceiver(this, filter);
    }


    public boolean stopLocationService() {
        if (mLocationBroadcast == null || mContext == null)
            return false;
        mContext.unregisterReceiver(mLocationBroadcast);
        return true;
    }

    public LocationServiceData getLastLocation() {
        return _lastLocation;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(LOCATION_ACTION)) {
            _lastLocation = (LocationServiceData) intent.getSerializableExtra("LOCATION_EXTRA");
            if (mListener != null)
                mListener.locationChanged(_lastLocation);
        }
    }
}
