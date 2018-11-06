package com.pomohouse.launcher.models.locations;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 6/13/2017 AD.
 */

public class RefreshLocationDao {
    @SerializedName("endpoint")
    @Expose
    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
