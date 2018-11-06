package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.util.List;

/**
 * Created by Admin on 8/29/16 AD.
 */
public class FetchFriendCollection extends ResultModel {

    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;
    @SerializedName("data")
    @Expose
    private List<FetchFriend> fetchFriendList;

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

    public List<FetchFriend> getFetchFriendList() {
        return fetchFriendList;
    }

    public void setFetchFriendList(List<FetchFriend> fetchFriendList) {
        this.fetchFriendList = fetchFriendList;
    }
}
