package com.pomohouse.bff.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class ResponseDao {

    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }
}
