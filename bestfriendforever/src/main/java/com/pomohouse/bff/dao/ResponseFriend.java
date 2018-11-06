package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import org.parceler.Parcel;

/**
 * Created by Admin on 8/31/16 AD.
 */
@Parcel(Parcel.Serialization.BEAN)
public class ResponseFriend extends ResultModel {

    /**
     * imei : 356879451687865
     * friendImei : 356879451687866
     * f_status : r
     */
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("friendImei")
    @Expose
    private String friendImei;
    @SerializedName("f_status")
    @Expose
    private String f_status;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFriendImei() {
        return friendImei;
    }

    public void setFriendImei(String friendImei) {
        this.friendImei = friendImei;
    }

    public String getF_status() {
        return f_status;
    }

    public void setF_status(String f_status) {
        this.f_status = f_status;
    }

}
