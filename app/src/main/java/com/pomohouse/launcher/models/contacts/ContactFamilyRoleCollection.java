package com.pomohouse.launcher.models.contacts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.util.List;

/**
 * Created by Admin on 8/30/16 AD.
 */
public class ContactFamilyRoleCollection extends ResultModel {

    @SerializedName("resCode")
    @Expose
    private int resCode;
    @SerializedName("resDesc")
    @Expose
    private String resDesc;
    @SerializedName("data")
    @Expose
    private List<ContactFamilyRole> contactFamilyRoles;

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

    public List<ContactFamilyRole> getContactFamilyRoles() {
        return contactFamilyRoles;
    }

    public void setContactFamilyRoles(List<ContactFamilyRole> contactFamilyRoles) {
        this.contactFamilyRoles = contactFamilyRoles;
    }
}
