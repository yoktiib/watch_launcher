package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.util.List;

/**
 * Created by sirawit on 8/28/16 AD.
 */
public class FriendCollection extends ResultModel {
    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;
    @SerializedName("data")
    @Expose
    private List<FriendItemModel> friendDaoList;

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

    public List<FriendItemModel> getFriendDaoList() {
        return friendDaoList;
    }

    public void setFriendDaoList(List<FriendItemModel> friendDaoList) {
        this.friendDaoList = friendDaoList;
    }
}
