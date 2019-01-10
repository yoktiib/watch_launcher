package com.pomohouse.library;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by administrator on 3/28/2016 AD.
 */

public class WearerInfoUtils {
    private static WearerInfoUtils ourInstance = new WearerInfoUtils();
    private String imei = "";
    private String pomoVersion = "1.1";
    private String language = "en";
    private String platform = "K8";
    private boolean haveSimCard = false;

    /*public static WearerInfoUtils getInstance() {
        return ourInstance;
    }*/

    public static WearerInfoUtils getInstance() {
        if (ourInstance == null)
            ourInstance = new WearerInfoUtils();
       //     ourInstance.initWearerInfoUtils(mContext);

        //if (ourInstance.getImei(mContext) == null || ourInstance.getImei(mContext).isEmpty())

        return ourInstance;
    }

    public static WearerInfoUtils newInstance(Context mContext) {
        return ourInstance = new WearerInfoUtils().initWearerInfoUtils(mContext);
    }

    public WearerInfoUtils initWearerInfoUtils(Context mContext) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                haveSimCard = telephonyManager.getSimState() != TelephonyManager.SIM_STATE_ABSENT;
                imei = telephonyManager.getImei();
            }
        } catch (Exception ignore) {
        }
        return this;
    }

    public boolean isHaveSimCard() {
        return haveSimCard;
    }

    public String getImei(Context mContext) {
        if (imei == null || imei.isEmpty()) initWearerInfoUtils(mContext);
        return imei;
        //return "869301030031654";
    }

    public String getPlatform() {
        if (platform == null) return "K8";
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLanguage() {
        if (language == null) return "en";
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPomoVersion() {
        if (pomoVersion == null) return "1.1";
        return pomoVersion;
    }

    public void setPomoVersion(String pomoVersion) {
        this.pomoVersion = pomoVersion;
    }


}