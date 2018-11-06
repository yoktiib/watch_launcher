package com.pomohouse.launcher.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.io.Serializable;

/**
 * Created by Admin on 6/26/2017 AD.
 */

public class CallContact extends ResultModel implements Serializable {
    private static final long serialVersionUID = 3731025679837543063L;

    /**
     * toContactId : 2
     * fromContactId : 2
     * isInclassMode : Y
     * isAutoAnswer : N
     * isAllowCalling : Y
     */
    @SerializedName("toContactId")
    @Expose
    private String toContactId;
    @SerializedName("fromContactId")
    @Expose
    private String fromContactId;
    @SerializedName("isInClassMode")
    @Expose
    private String isInClassMode;
    @SerializedName("isAutoAnswer")
    @Expose
    private String isAutoAnswer;
    @SerializedName("isAllowCalling")
    @Expose
    private String isAllowCalling;


    public String getToContactId() {
        return toContactId;
    }

    public void setToContactId(String toContactId) {
        this.toContactId = toContactId;
    }

    public String getFromContactId() {
        return fromContactId;
    }

    public void setFromContactId(String fromContactId) {
        this.fromContactId = fromContactId;
    }

    public String getIsInClassMode() {
        return isInClassMode;
    }

    public void setIsInClassMode(String isInClassMode) {
        this.isInClassMode = isInClassMode;
    }

    public String getIsAutoAnswer() {
        return isAutoAnswer;
    }

    public void setIsAutoAnswer(String isAutoAnswer) {
        this.isAutoAnswer = isAutoAnswer;
    }

    public String getIsAllowCalling() {
        return isAllowCalling;
    }

    public void setIsAllowCalling(String isAllowCalling) {
        this.isAllowCalling = isAllowCalling;
    }
}
