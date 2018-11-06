package com.pomohouse.launcher.models.locations;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by Admin on 9/8/16 AD.
 */
public class LocationServiceData implements Serializable, Cloneable {
    private static final long serialVersionUID = -8688842738804906571L;
    private int hpe = 0;
    private float score = 0;
    private int NAP = 0;
    private int NSat = 0;
    private int NCell = 0;
    private int NLac = 0;
    private int historicalLocationCount = 0;
    private String ip;
    private long serverTimestamp;
    private double latitude;
    private double longitude;

    private String mProvider;
    private long mTime = 0;
    private long mElapsedRealtimeNanos = 0;
    private double mAltitude = 0.0f;
    private float mSpeed = 0.0f;
    private float mBearing = 0.0f;
    private float mAccuracy = 0.0f;

    public LocationServiceData() {
    }

    public LocationServiceData(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mProvider = location.getProvider();
        mAccuracy = location.getAccuracy();
        mSpeed = location.getSpeed();
        mBearing = location.getBearing();
        mAltitude = location.getAltitude();
        mTime = location.getTime();
        mElapsedRealtimeNanos = location.getElapsedRealtimeNanos();
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long mTime) {
        this.mTime = mTime;
    }

    public long getElapsedRealtimeNanos() {
        return mElapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long mElapsedRealtimeNanos) {
        this.mElapsedRealtimeNanos = mElapsedRealtimeNanos;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float mSpeed) {
        this.mSpeed = mSpeed;
    }

    public float getBearing() {
        return mBearing;
    }

    public void setBearing(float mBearing) {
        this.mBearing = mBearing;
    }

    public float getAccuracy() {
        return mAccuracy;
    }

    public void setAccuracy(float mAccuracy) {
        this.mAccuracy = mAccuracy;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getHpe() {
        return hpe;
    }

    public void setHpe(int hpe) {
        this.hpe = hpe;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getNAP() {
        return NAP;
    }

    public void setNAP(int NAP) {
        this.NAP = NAP;
    }

    public int getNSat() {
        return NSat;
    }

    public void setNSat(int NSat) {
        this.NSat = NSat;
    }

    public int getNCell() {
        return NCell;
    }

    public void setNCell(int NCell) {
        this.NCell = NCell;
    }

    public int getNLac() {
        return NLac;
    }

    public void setNLac(int NLac) {
        this.NLac = NLac;
    }

    public int getHistoricalLocationCount() {
        return historicalLocationCount;
    }

    public void setHistoricalLocationCount(int historicalLocationCount) {
        this.historicalLocationCount = historicalLocationCount;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }


}