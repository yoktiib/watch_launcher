package com.pomohouse.bff.dao;


import com.pomohouse.library.networks.ResultModel;

import org.parceler.Parcel;
import org.parceler.ParcelProperty;

/**
 * Created by sirawit on 8/28/16 AD.
 */
@Parcel(Parcel.Serialization.BEAN)
public class FriendItemModel extends ResultModel {
    /**
     * imei : 356879451687866
     * friendImei : 356879451687863
     * name : P Kha
     * gender : F
     * avatar : http://203.151.232.222:3000/api/app/utils/image?imagePath=/home/git/pomodonut.git/src/wearer_image/WEA313568794516878631467353052974_128.jpg
     */
    @ParcelProperty("imei")
    String imei;
    @ParcelProperty("friendImei")
    String friendImei;
    @ParcelProperty("name")
    String name;
    @ParcelProperty("gender")
    String gender;
    @ParcelProperty("avatar")
    String avatar;
    @ParcelProperty("avatarType")
    int avatarType;

    public int getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(int avatarType) {
        this.avatarType = avatarType;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
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
}
