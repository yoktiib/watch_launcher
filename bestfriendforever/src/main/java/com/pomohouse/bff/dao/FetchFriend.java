package com.pomohouse.bff.dao;

import org.parceler.Parcel;
import org.parceler.ParcelProperty;

/**
 * Created by Admin on 8/29/16 AD.
 */
@Parcel(Parcel.Serialization.BEAN)
public class FetchFriend {

    /**
     * name : string
     * gender : F
     * avatar : null
     * fid : 37
     * imei : 356879451687866
     */


    @ParcelProperty("friendImei")
    String friendImei;
    @ParcelProperty("name")
    String name;
    @ParcelProperty("gender")
    String gender;
    @ParcelProperty("avatar")
    String avatar;
    @ParcelProperty("imei")
    String imei;
    @ParcelProperty("avatarType")
    int avatarType;

    public int getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public String getFriendImei() {
        return friendImei;
    }

    public void setFriendImei(String friendImei) {
        this.friendImei = friendImei;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
