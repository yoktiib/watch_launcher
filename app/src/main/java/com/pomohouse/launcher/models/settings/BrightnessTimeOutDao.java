package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 2/3/2017 AD.
 */

public class BrightnessTimeOutDao {
    @SerializedName("brightnessTimeOut")
    @Expose
    private int brightnessTimeOut;

    public int getBrightnessTimeOut() {
        return brightnessTimeOut;
    }

    public void setBrightnessTimeOut(int brightnessTimeOut) {
        this.brightnessTimeOut = brightnessTimeOut;
    }
}
