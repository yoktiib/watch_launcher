package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

/**
 * Created by Admin on 10/12/2016 AD.
 */

public class DeviceSetUpDao extends ResultModel {

    /**
     * imei : 356879451687866
     * power : 100
     * autoAnswer : N
     * positionTiming : N
     * reportLocation : N
     * avoidUnknown : N
     * silentMode : N
     * timeZone : GMT+07:00
     * currentTime : 2016-10-12T10:41:33.000Z
     * watchStatus : 1
     * macAddress : 1234
     */
    @SerializedName("autoAnswer")
    @Expose
    private String autoAnswer = "N";
    @SerializedName("positionTiming")
    @Expose
    private int positionTiming;
    @SerializedName("silentMode")
    @Expose
    private String silentMode = "N";
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("autoTimezone")
    @Expose
    private String autoTimezone = "N";
    @SerializedName("currentTime")
    @Expose
    private String currentTime;
    @SerializedName("watchOff")
    @Expose
    private String wearerStatus = "N";
    @SerializedName("lang")
    @Expose
    private String lang = "en";
    @SerializedName("eventTiming")
    @Expose
    private int eventTiming;

    @SerializedName("brightnessTimeOut")
    @Expose
    private int brightnessTimeOut;
    @SerializedName("timeFormat")
    @Expose
    private int timeFormat;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(int timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getAutoAnswer() {
        return autoAnswer;
    }

    public int getBrightnessTimeOut() {
        return brightnessTimeOut;
    }

    public void setBrightnessTimeOut(int brightnessTimeOut) {
        this.brightnessTimeOut = brightnessTimeOut;
    }

    public void setAutoAnswer(String autoAnswer) {
        this.autoAnswer = autoAnswer;
    }


    public String getSilentMode() {
        return silentMode;
    }

    public void setSilentMode(String silentMode) {
        this.silentMode = silentMode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getAutoTimezone() {
        return autoTimezone;
    }

    public void setAutoTimezone(String autoTimezone) {
        this.autoTimezone = autoTimezone;
    }

    public String getWearerStatus() {
        return wearerStatus;
    }

    public void setWearerStatus(String wearerStatus) {
        this.wearerStatus = wearerStatus;
    }

    public int getPositionTiming() {
        return positionTiming;
    }

    public void setPositionTiming(int positionTiming) {
        this.positionTiming = positionTiming;
    }

    public int getEventTiming() {
        return eventTiming;
    }

    public void setEventTiming(int eventTiming) {
        this.eventTiming = eventTiming;
    }
}
