package com.pomohouse.launcher.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Admin on 3/28/2016 AD.
 */

public class WearerInfo implements Parcelable {
    private static WearerInfo instance = null;

    public WearerInfo() {
    }

    public static WearerInfo initInstance(WearerInfo wearerInfo) {
        instance = wearerInfo;
        return instance;
    }

    public static WearerInfo getInstance() {
        if (instance == null) {
            instance = new WearerInfo();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    public String getLat() {
        return lat;
    }

    public WearerInfo setLat(String lat) {
        this.lat = lat;
        return this;
    }

    public WearerInfo setLat(double lat) {
        this.lat = String.valueOf(lat);
        return this;
    }

    public String getLng() {
        return lng;
    }

    public WearerInfo setLng(String lng) {
        this.lng = lng;
        return this;
    }

    public WearerInfo setLng(double lng) {
        this.lng = String.valueOf(lng);
        return this;
    }

    public String getImei() {
        return imei;
    }

    public WearerInfo setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public WearerInfo setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }



    protected WearerInfo(Parcel in) {
        imei = in.readString();
        phoneNumber = in.readString();
        lat = in.readString();
        lng = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imei);
        dest.writeString(phoneNumber);
        dest.writeString(lat);
        dest.writeString(lng);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WearerInfo> CREATOR = new Parcelable.Creator<WearerInfo>() {
        @Override
        public WearerInfo createFromParcel(Parcel in) {
            return new WearerInfo(in);
        }

        @Override
        public WearerInfo[] newArray(int size) {
            return new WearerInfo[size];
        }
    };
}