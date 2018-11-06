package com.pomohouse.bff.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pomohouse.library.networks.ResultModel;

import java.io.Serializable;

/**
 * Created by Admin on 8/24/16 AD.
 */
public class EventDataInfo extends ResultModel implements Serializable {
    private static final long serialVersionUID = -2294330423760519862L;

    /**
     * eventId : 35
     * from : 33
     * to : 356879451687865
     * "senderInfo": "{\"id\":6,\"name\":\"yeen\",\"gender\":\"F\",\"avatar\":null,\"type\":1}",
     "receiverInfo": "{\"id\":\"356879451687869\",\"name\":\"GoNunGil\",\"gender\":\"M\",\"type\":0}"
     * eventType : 0
     * status : 0
     * code : 105
     */
    @SerializedName("eventId")
    @Expose
    private int eventId;
    @SerializedName("from")
    @Expose
    private String senderId;
    @SerializedName("to")
    @Expose
    private String receiveId;
    @SerializedName("eventType")
    @Expose
    private int eventType;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("eventCode")
    @Expose
    private int eventCode;
    @SerializedName("eventDesc")
    @Expose
    private String eventDesc;
    @SerializedName("timeStamp")
    @Expose
    private String timeStamp;
    @SerializedName("receiverInfo")
    @Expose
    private String receiverInfo;
    @SerializedName("senderInfo")
    @Expose
    private String senderInfo;

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getEventCode() {
        return eventCode;
    }

    public EventDataInfo setEventCode(int eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    @Override
    public String toString() {
        return eventCode + " : " + eventDesc + " : " + content;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
