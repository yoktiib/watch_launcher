package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 6/26/2017 AD.
 */

public class AllowCallingRequest {

    @SerializedName("to")
    @Expose
    private String toContactId;
    @SerializedName("from")
    @Expose
    private String fromContactId;

    public String getToContactId() {
        return toContactId;
    }

    public void setToContactId(String toContactId) {
        this.toContactId = toContactId;
    }

    public String getFromContactId() {
        return fromContactId;
    }

    public void setFromContactId(String fromContactId) {
        this.fromContactId = fromContactId;
    }
}
