package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class LocationTimerDao {


    /**
     * positionTiming : 189
     */
    @SerializedName("positionTiming")
    @Expose
    private int positionTiming = 300;

    public int getPositionTiming() {
        return positionTiming;
    }

    public void setPositionTiming(int positionTiming) {
        this.positionTiming = positionTiming;
    }
}
