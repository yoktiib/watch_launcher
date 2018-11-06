package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class SilentDao {
    @SerializedName("silentMode")
    @Expose
    private String silentMode;

    public String getSilentMode() {
        return silentMode;
    }

    public void setSilentMode(String silentMode) {
        this.silentMode = silentMode;
    }
}
