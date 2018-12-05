package com.pomohouse.launcher.api.requests;

public class WifiAccessPoint {
    private String macAddress;
    private String age;
    private String channel;
    private String frequency;
    private String signalStrength;
    private String signalToNoiseRatio;

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getSignalToNoiseRatio() {
        return signalToNoiseRatio;
    }

    public void setSignalToNoiseRatio(String signalToNoiseRatio) {
        this.signalToNoiseRatio = signalToNoiseRatio;
    }
}
