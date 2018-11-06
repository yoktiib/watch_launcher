package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

/**
 * Created by sirawit on 8/28/16 AD.
 */
public class ReadyFriendResult extends ResultModel {

    /**
     * id : 7
     * imei : 356879451687865
     * lat : 13.736717
     * lng : 100.523186
     */
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
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
