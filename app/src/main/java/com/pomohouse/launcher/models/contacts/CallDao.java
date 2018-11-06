package com.pomohouse.launcher.models.contacts;

/**
 * Created by Admin on 3/7/2017 AD.
 */

public class CallDao {
    private String number;
    private int type;
    private int duration;
    private String callDate;
    private int isRead;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getDuration() {
        return duration;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }
}
