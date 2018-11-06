package com.pomohouse.launcher.manager.in_class_mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.launcher.manager.TimeManager;

/**
 * Created by Admin on 9/8/16 AD.
 */
public class InClassModePrefModel extends TimeManager {

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
    private String begin;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("periodDate")
    @Expose
    private String periodDate = "";

    @SerializedName("inClassId")
    @Expose
    private int inClassId;
    @SerializedName("status")
    @Expose
    private String status = "N";

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

    public int getInClassId() {
        return inClassId;
    }

    public void setInClassId(int inClassId) {
        this.inClassId = inClassId;
    }

    public String getStatus() {
        return status;
    }

    public boolean isInClassOn() {
        return status.equalsIgnoreCase("Y");
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInClassTime() {
        return validateTimeManager(begin, end);
    }

    public boolean isTodayHaveInClass() {
        return validateDays(periodDate);
    }


/**
 * eventId : 1130
 * from : 1
 * to : 356879451687865
 * eventCode : 112
 * eventDesc : App updated date and time on in class mode
 * content : {"begin":"09:15","end":"19:30","periodDate":"Mon, Fri","imei":"356879451687865","inClassId":3,"active":"N"}
 * eventType : 0
 * status : 0
 * timeStamp : 2016-11-10T06:09:53.000Z
 * senderInfo : {"id":1,"name":"Admin","gender":"M","avatar":null,"type":1}
 * receiverInfo : {"id":"356879451687865","name":"Aoi-Handsome","gender":"M","avatar":null,"type":0}
 */

}
