package com.pomohouse.launcher.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Admin on 11/4/2016 AD.
 */

public class EventMember implements Serializable {
    private static final long serialVersionUID = -7768760697627926739L;
    /**
     * gender : M
     * memberName : Admin
     * name : Beer-Hansome
     * avatar : avatar
     * {\"id\":1,\"name\":\"Admin\",\"gender\":\"M\",\"avatar\":null,\"type\":1}"
     */
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("type")
    @Expose
    private String type;


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
