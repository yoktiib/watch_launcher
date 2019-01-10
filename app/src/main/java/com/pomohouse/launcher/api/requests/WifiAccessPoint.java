package com.pomohouse.launcher.api.requests;

public class WifiAccessPoint {

    private int rssi;
    private String MAC;

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
}
