package com.pomohouse.bff.network.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.RequestModel;

/**
 * Created by Admin on 8/26/16 AD.
 */
public class WatchInfoRequest extends RequestModel {

    /**
     * imei : string
     * lat : 0
     * lng : 0
     */

    private String imei;
    private String lat;
    private String lng;
    @SerializedName("friendBTName")
    @Expose
    private String friendBTName;
    @SerializedName("myBTName")
    @Expose
    private String myBTName;

    public String getFriendBTName() {
        return friendBTName;
    }

    public void setFriendBTName(String friendBTName) {
        this.friendBTName = friendBTName;
    }

    public String getMyBTName() {
        return myBTName;
    }

    public void setMyBTName(String myBTName) {
        this.myBTName = myBTName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
