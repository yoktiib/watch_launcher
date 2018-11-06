package com.pomohouse.launcher.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 3/2/2017 AD.
 */

public class NotificationMainIconDao {
    @SerializedName("isShow")
    @Expose
    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
