package com.pomohouse.launcher.tcp;

import java.io.Serializable;

public class TCPMessenger implements Serializable {

    private int length;
    private String model;
    private String imei;
    private String launcherVersion;
    private int CMD;
    private String data;
    private int sum;

    public TCPMessenger convertValueToModel(String[] value) {
        length = Integer.parseInt(value[1]);
        model = value[2];
        imei = value[3];
        launcherVersion = value[4];
        CMD = Integer.parseInt(value[5]);
        data = value[6];
        sum = Integer.parseInt(value[7]);
        return this;
    }

    public String convertModelToValue() {
        String packageSender = "<PMHStart>";
        packageSender += "<" + length + ">";
        packageSender += "<" + model + ">";
        packageSender += "<" + imei + ">";
        packageSender += "<" + launcherVersion + ">";
        packageSender += "<" + CMD + ">";
        packageSender += "<" + data + ">";
        packageSender += "<" + sum + ">";
        packageSender += "<PMHEnd>";
        return packageSender;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLauncherVersion() {
        return launcherVersion;
    }

    public void setLauncherVersion(String launcherVersion) {
        this.launcherVersion = launcherVersion;
    }

    public int getCMD() {
        return CMD;
    }

    public void setCMD(int CMD) {
        this.CMD = CMD;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
