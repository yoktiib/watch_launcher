package com.pomohouse.bff.dao;


import com.pomohouse.library.networks.ResultModel;

/**
 * Created by sirawit on 8/29/16 AD.
 */
public class CancelResult extends ResultModel {

    /**
     * id : 7
     * imei : 356879451687865
     * lat : 13.736717
     * lng : 100.523186
     */

    private int id;
    private String imei;
    private double lat;
    private double lng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

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
