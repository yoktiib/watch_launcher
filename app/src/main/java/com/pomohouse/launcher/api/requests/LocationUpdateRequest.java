package com.pomohouse.launcher.api.requests;

import android.location.Location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class LocationUpdateRequest{

    /**
     * imei : string
     * power : 0
     * lng : 0
     * lat : 0
     */
    @SerializedName("power")
    @Expose
    private double power;/*
    @SerializedName("speed")
    @Expose
    private double speed;*/
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("locationType")
    @Expose
    private int locationType;
    @SerializedName("step")
    @Expose
    private int step;
    @SerializedName("accuracy")
    @Expose
    private float accuracy;
    @SerializedName("eventList")
    @Expose
    private String eventList;
    @SerializedName("wifiAccessPoints")
    @Expose
    private ArrayList<WifiAccessPoint> wifiAccessPoint;
    @SerializedName("cellTowers")
    @Expose
    private ArrayList<CellTower> cellTower;

    public ArrayList<CellTower> getCellTower() {
        return cellTower;
    }

    public void setCellTower(ArrayList<CellTower> cellTower) {
        this.cellTower = cellTower;
    }

    public ArrayList<WifiAccessPoint> getWifiAccessPoint() {
        return wifiAccessPoint;
    }

    public void setWifiAccessPoint(ArrayList<WifiAccessPoint> wifiAccessPoint) {
        this.wifiAccessPoint = wifiAccessPoint;
    }
    public LocationUpdateRequest() {
    }

    public LocationUpdateRequest(Location currLocation) {
    //    speed = currLocation.getSpeed();
        accuracy = currLocation.getAccuracy();
        lat = currLocation.getLatitude();
        lng = currLocation.getLongitude();
    }
/*
    public LocationUpdateRequest setPower(int power) {
        this.power = power;
        return this;
    }*/
/*
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }*/

    public String getEventList() {
        return eventList;
    }

    public void setEventList(String eventList) {
        this.eventList = eventList;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
