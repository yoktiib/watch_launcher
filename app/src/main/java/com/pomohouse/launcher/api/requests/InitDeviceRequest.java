package com.pomohouse.launcher.api.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 10/12/2016 AD.
 */

public class InitDeviceRequest extends ImeiRequest{

    @SerializedName("simOperator")
    @Expose
    private String simOperator;
    @SerializedName("simSerialNumber")
    @Expose
    private String simSerialNumber;
    @SerializedName("simOperatorName")
    @Expose
    private String simOperatorName;
    @SerializedName("macAddress")
    @Expose
    private String macAddress;
    @SerializedName("BTName")
    @Expose
    private String BTName;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("launcherVersion")
    @Expose
    private String launcherVersion;
    @SerializedName("firmwareVersion")
    @Expose
    private String firmwareVersion;
    @SerializedName("fireBaseWatchToken")
    @Expose
    private String fireBaseWatchToken;

    public String getFireBaseWatchToken() {
        return fireBaseWatchToken;
    }

    public void setFireBaseWatchToken(String fireBaseWatchToken) {
        this.fireBaseWatchToken = fireBaseWatchToken;
    }

    public String getSimOperator() {
        return simOperator;
    }

    public void setSimOperator(String simOperator) {
        this.simOperator = simOperator;
    }

    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSimSerialNumber() {
        return simSerialNumber;
    }

    public void setSimSerialNumber(String simSerialNumber) {
        this.simSerialNumber = simSerialNumber;
    }

    public String getSimOperatorName() {
        return simOperatorName;
    }

    public void setSimOperatorName(String simOperatorName) {
        this.simOperatorName = simOperatorName;
    }

    public String getBTName() {
        return BTName;
    }

    public void setBTName(String BTName) {
        this.BTName = BTName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLauncherVersion() {
        return launcherVersion;
    }

    public void setLauncherVersion(String launcherVersion) {
        this.launcherVersion = launcherVersion;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
}
