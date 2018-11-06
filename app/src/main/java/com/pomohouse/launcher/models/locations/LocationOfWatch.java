package com.pomohouse.launcher.models.locations;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 10/27/2016 AD.
 */

public class LocationOfWatch {

    /**
     * lat : 13.731416
     * lng : 100.560995
     */
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
