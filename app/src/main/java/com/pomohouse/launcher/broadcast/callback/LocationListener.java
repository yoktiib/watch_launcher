package com.pomohouse.launcher.broadcast.callback;

import com.pomohouse.launcher.models.locations.LocationServiceData;

/**
 * Created by Admin on 8/24/16 AD.
 */
public interface LocationListener {
    void locationChanged(LocationServiceData location);

    //void onLocationGPSChanged(LocationServiceData location);
}

