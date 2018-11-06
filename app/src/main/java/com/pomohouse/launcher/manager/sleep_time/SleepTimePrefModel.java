package com.pomohouse.launcher.manager.sleep_time;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.launcher.manager.TimeManager;

/**
 * Created by Admin on 9/8/16 AD.
 */
public class SleepTimePrefModel extends TimeManager {
    /**
     * begin : 09:15
     * end : 19:30
     * periodDate : Mon, Fri
     * imei : 356879451687865
     * inClassId : 3
     * active : N
     */
    @SerializedName("begin")
    @Expose
    private String begin = "23:00";
    @SerializedName("end")
    @Expose
    private String end = "06:30";
    @SerializedName("periodDate")
    @Expose
    private String periodDate = "Sun,Mon,Tue,Wed,Thu,Fri,Sat";
    @SerializedName("sleepId")
    @Expose
    private int sleepId;
    @SerializedName("status")
    @Expose
    private String status = "Y";

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public boolean isSleepTimeOn() {
        return status.equalsIgnoreCase("Y");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSleepId() {
        return sleepId;
    }

    public void setSleepId(int sleepId) {
        this.sleepId = sleepId;
    }

    public boolean isTodayHaveSleepTime() {
        return validateDays(periodDate);
    }

    public boolean isSleepTime() {
        return validateTimeManager(begin, end);
    }
}
