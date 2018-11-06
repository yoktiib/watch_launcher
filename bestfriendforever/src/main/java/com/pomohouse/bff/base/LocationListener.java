package com.pomohouse.bff.base;


import com.pomohouse.bff.dao.LocationServiceData;

/**
 * Created by Admin on 8/24/16 AD.
 */
public interface LocationListener {
    void locationChanged(LocationServiceData location);

    //void onLocationGPSChanged(LocationServiceData location);
}

