package com.pomohouse.launcher.api.requests;

public class CellTower {
    private String radioType;
    private int MNC;
    private int MCC;
    private int lac;
    private int Rxlev;
    private int cellid;

    public String getRadioType() {
        return radioType;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public int getMNC() {
        return MNC;
    }

    public void setMNC(int MNC) {
        this.MNC = MNC;
    }

    public int getMCC() {
        return MCC;
    }

    public void setMCC(int MCC) {
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
