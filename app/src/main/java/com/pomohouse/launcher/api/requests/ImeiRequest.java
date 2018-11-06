package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.RequestModel;

/**
 * Created by Admin on 5/3/2016 AD.
 */
public class ImeiRequest extends RequestModel {
    @SerializedName("imei")
    @Expose
    private String imei;

    public ImeiRequest() {
    }

    public ImeiRequest(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public ImeiRequest setImei(String imei) {
        this.imei = imei;
        return this;
    }
}
