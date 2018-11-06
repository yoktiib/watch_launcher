package com.pomohouse.library;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by administrator on 3/28/2016 AD.
 */

public class WearerInfoUtils {
    private static WearerInfoUtils ourInstance = new WearerInfoUtils();
    private String imei = "";
    private String pomoVersion = "1.0.2";
    private String language = "en";
    private String platform = "POMOWatch";
    private boolean haveSimCard = false;

    public static WearerInfoUtils getInstance() {
        return ourInstance;
    }

    public static WearerInfoUtils getInstance(Context mContext) {
        if (ourInstance == null)
            ourInstance = new WearerInfoUtils();
        if (ourInstance.getImei() == null || ourInstance.getImei().isEmpty())
            ourInstance.initWearerInfoUtils(mContext);
        return ourInstance;
    }

    public static WearerInfoUtils newInstance(Context mContext) {
        return ourInstance = new WearerInfoUtils().initWearerInfoUtils(mContext);
    }

    public WearerInfoUtils initWearerInfoUtils(Context mContext) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
            haveSimCard = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        } catch (Exception ignore) {
        }
        return this;
    }

    public boolean isHaveSimCard() {
        return haveSimCard;
    }

    public String getImei() {
        return imei;
    }

    public String getPlatform() {
        if (platform == null)
            return "POMOWatch";
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLanguage() {
        if (language == null)
            return "en";
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPomoVersion() {
        if (pomoVersion == null)
            return "1.0.2";
        return pomoVersion;
    }

    public void setPomoVersion(String pomoVersion) {
        this.pomoVersion = pomoVersion;
    }


}