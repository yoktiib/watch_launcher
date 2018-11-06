package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sirawit on 11/26/2016 AD.
 */

public class AlarmModel implements Serializable {

    private static final long serialVersionUID = -3736040333509897072L;
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
    @SerializedName("alarmId")
    @Expose
    private int alarmId;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("periodDate")
    @Expose
    private String periodDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("alarmType")
    @Expose
    private int alarmType;
    @SerializedName("soundId")
    @Expose
    private int soundId;
    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(String periodDate) {
        this.periodDate = periodDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
