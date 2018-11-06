package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by Admin on 8/30/16 AD.
 */
@Parcel
public class ContactModel {
    /**
     * name : beer_on_sky
     * phone : 0867244435
     * type : family
     */
    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("imei")
    @Expose
    String imei;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("phone")
    @Expose
    String phone;
    @SerializedName("type")
    @Expose
    String type;
    @SerializedName("avatar")
    @Expose
    String avatar;
    @SerializedName("gender")
    @Expose
    String gender;
    @SerializedName("memberId")
    @Expose
    String memberId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
