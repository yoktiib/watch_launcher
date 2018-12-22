package com.pomohouse.launcher.api.requests;

public class WifiLoc {
    private String rssi;
    private String MAC;

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
}
