package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by Admin on 8/30/16 AD.
 */
@Parcel
public class ContactCollection extends ResultModel {

    @SerializedName("resCode")
    @Expose
    int resCode;
    @SerializedName("resDesc")
    @Expose
    String resDesc;
    @SerializedName("data")
    @Expose
    List<ContactModel> contactModelList;

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

    public List<ContactModel> getContactModelList() {
        return contactModelList;
    }

    public void setContactModelList(List<ContactModel> contactModelList) {
        this.contactModelList = contactModelList;
    }
}
