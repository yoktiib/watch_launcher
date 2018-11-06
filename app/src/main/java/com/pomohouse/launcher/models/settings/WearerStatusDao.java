package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class WearerStatusDao {


    /**
     * watchOff : N
     */
    @SerializedName("watchOff")
    @Expose
    private String watchOff;

    public String getWatchOff() {
        return watchOff;
    }

    public void setWatchOff(String watchOff) {
        this.watchOff = watchOff;
    }
}

