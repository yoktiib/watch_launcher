package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.launcher.models.locations.LocationOfWatch;
import com.pomohouse.launcher.manager.in_class_mode.InClassModePrefModel;
import com.pomohouse.launcher.manager.sleep_time.SleepTimePrefModel;
import com.pomohouse.library.networks.ResultModel;

import java.util.ArrayList;

/**
 * Created by Admin on 10/27/2016 AD.
 */

public class DeviceInfoModel extends ResultModel {
    @SerializedName("location")
    @Expose
    private LocationOfWatch location;
    @SerializedName("device")
    @Expose
    private DeviceSetUpDao deviceSetUp;
    @SerializedName("inClassMode")
    @Expose
    private InClassModePrefModel inClassMode;
    @SerializedName("sleepMode")
    @Expose
    private SleepTimePrefModel sleepMode;
    @SerializedName("alarm")
    @Expose
    private ArrayList<AlarmModel> alarmModelList;
    @SerializedName("theme")
    @Expose
    private ArrayList<ThemeModel> themeModelList;


    public ArrayList<ThemeModel> getThemeModelList() {
        return themeModelList;
    }

    public void setThemeModelList(ArrayList<ThemeModel> themeModelList) {
        this.themeModelList = themeModelList;
    }

    public ArrayList<AlarmModel> getAlarmModelList() {
        return alarmModelList;
    }

    public void setAlarmModelList(ArrayList<AlarmModel> alarmModelList) {
        this.alarmModelList = alarmModelList;
    }

    public InClassModePrefModel getInClassMode() {
        return inClassMode;
    }

    public SleepTimePrefModel getSleepMode() {
        return sleepMode;
    }

    public void setSleepMode(SleepTimePrefModel sleepMode) {
        this.sleepMode = sleepMode;
    }

    public void setInClassMode(InClassModePrefModel inClassMode) {
        this.inClassMode = inClassMode;
    }

    //private alarm;
    public LocationOfWatch getLocation() {
        return location;
    }

    public void setLocation(LocationOfWatch location) {
        this.location = location;
    }

    public DeviceSetUpDao getDeviceSetUp() {
        return deviceSetUp;
    }

    public void setDeviceSetUp(DeviceSetUpDao deviceSetUp) {
        this.deviceSetUp = deviceSetUp;
    }
}
