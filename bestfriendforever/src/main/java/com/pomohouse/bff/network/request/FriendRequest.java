package com.pomohouse.bff.network.request;

/**
 * Created by sirawit on 8/28/16 AD.
 */
public class FriendRequest {

    /**
     * myImei : string
     * fImei : string
     * allow : string
     */

    private String imei;
    private String friendImei;
    private String allow;

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

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }
}
