package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sirawit on 11/26/2016 AD.  add by  yang
 */

public class ThemeModel implements Serializable {


    /**
     * alarmId : 12
     * imei : 356879451687865
     * name : bedtime
     * time : 23:51
     * periodDate : Mon,Tue,Wed,Thu,Fri,Sat,Sun
     * status : Y
     * alarmType : 10
     * soundId : 2
     * timeStamp : 2016-11-24T06:45:38.000Z
     */
    @SerializedName("themeId")
    @Expose
    private int themeId;
    @SerializedName("themeCode")
    @Expose
    private String themeCode;
    @SerializedName("themeName")
    @Expose
    private String themeName;

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThemeCode() {
        return themeCode;
    }

    public void setThemeCode(String themeCode) {
        this.themeCode = themeCode;
    }

}
