package com.pomohouse.launcher.api.requests;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 6/13/2017 AD.
 */

public class RefreshLocationRequest extends LocationUpdateRequest {

    @SerializedName("endpoint")
    @Expose
    private String endpoint;

    public RefreshLocationRequest(Location currLocation, String endpoint) {
        super(currLocation);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
