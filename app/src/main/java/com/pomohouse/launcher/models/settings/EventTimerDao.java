package com.pomohouse.launcher.models.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 1/24/2017 AD.
 */

public class EventTimerDao {


    /**
     * eventTiming : 185
     */
    @SerializedName("eventTiming")
    @Expose
    private int eventTiming;

    public int getEventTiming() {
        return eventTiming;
    }

    public void setEventTiming(int eventTiming) {
        this.eventTiming = eventTiming;
    }
}
