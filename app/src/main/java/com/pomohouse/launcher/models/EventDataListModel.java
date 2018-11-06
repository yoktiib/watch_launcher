package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.util.List;

/**
 * Created by Admin on 10/3/2016 AD.
 */

public class EventDataListModel  extends ResultModel {

    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;
    @SerializedName("data")
    @Expose
    private List<EventDataInfo> dataInfoList;

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

    public List<EventDataInfo> getDataInfoList() {
        return dataInfoList;
    }

    public void setDataInfoList(List<EventDataInfo> dataInfoList) {
        this.dataInfoList = dataInfoList;
    }
}
