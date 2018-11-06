package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 9/5/16 AD.
 */

public class WearerStatusRequest extends ImeiRequest {

    @SerializedName("wearerStatus")
    @Expose
    private String wearerStatus;

    public String getWearerStatus() {
        return wearerStatus;
    }

    public void setWearerStatus(String wearerStatus) {
        this.wearerStatus = wearerStatus;
    }
}
