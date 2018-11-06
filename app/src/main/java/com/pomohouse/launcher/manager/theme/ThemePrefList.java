package com.pomohouse.launcher.manager.theme;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Admin on 9/12/16 AD.
 */
public class ThemePrefList{

    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;
    @SerializedName("data")
    @Expose
    private ArrayList<ThemePrefModel> dataThemePrefModels;

    public ArrayList<ThemePrefModel> getDataThemePrefModels() {
        return dataThemePrefModels;
    }

    public void setDataThemePrefModels(ArrayList<ThemePrefModel> dataThemePrefModels) {
        this.dataThemePrefModels = dataThemePrefModels;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

}