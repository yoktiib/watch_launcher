package com.pomohouse.launcher.api.requests;

public class CellTower {
    private String radioType;
    private String MNC;
    private String MCC;
    private int lac;
    private int Rxlev;
    private int cellid;

    public String getRadioType() {
        return radioType;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public String getMNC() {
        return MNC;
    }

    public void setMNC(String MNC) {
        this.MNC = MNC;
    }

    public String getMCC() {
        return MCC;
    }

    public void setMCC(String MCC) {
        this.MCC = MCC;
    }

    public int getLac() {
        return lac;
    }

    public void setLac(int lac) {
        this.lac = lac;
    }

    public int getRxlev() {
        return Rxlev;
    }

    public void setRxlev(int rxlev) {
        Rxlev = rxlev;
    }

    public int getCellid() {
        return cellid;
    }

    public void setCellid(int cellid) {
        this.cellid = cellid;
    }
}
