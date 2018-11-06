package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 6/1/2017 AD.
 */

public class UpdateFirebaseRequest extends ImeiRequest {

    /**
     * imei : string
     * fireBaseWatchToken : string
     */
    @SerializedName("fireBaseWatchToken")
    @Expose
    private String fireBaseWatchToken;

    public String getFireBaseWatchToken() {
        return fireBaseWatchToken;
    }

    public void setFireBaseWatchToken(String fireBaseWatchToken) {
        this.fireBaseWatchToken = fireBaseWatchToken;
    }
}
