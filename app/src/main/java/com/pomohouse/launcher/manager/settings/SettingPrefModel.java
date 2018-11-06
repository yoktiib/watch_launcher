package com.pomohouse.launcher.manager.settings;

/**
 * Created by Admin on 9/8/16 AD.
 */
public class SettingPrefModel {
    private boolean isFirstTime = true;
    private boolean isFirstTimeNoSim = true;
    private String FCMToken = "";
    private int volumeLevel = 3;
    private int brightLevel = 3;
    private int screenOffTimer = 30;
    private boolean silentMode = false;
    private String timeZone;
    private int positionTiming = 600;
    private int stepSyncTiming = 3600;
    private boolean autoTimezone = true;
    private boolean wearerStatus = false;
    private boolean autoAnswer = false;
    private String lang = "en";
    private boolean isMobileData = true;

    public int getStepSyncTiming() {
        if (stepSyncTiming == 0)
            return 3600;
        return stepSyncTiming;
    }

    public void setStepSyncTiming(int stepSyncTiming) {
        this.stepSyncTiming = stepSyncTiming;
    }

    public boolean isMobileData() {
        return isMobileData;
    }

    public void setMobileData(boolean mobileData) {
        isMobileData = mobileData;
    }

    public boolean isFirstTimeNoSim() {
        return isFirstTimeNoSim;
    }

    public void setFirstTimeNoSim(boolean firstTimeNoSim) {
        isFirstTimeNoSim = firstTimeNoSim;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public boolean isAutoAnswer() {
        return autoAnswer;
    }

    public void setAutoAnswer(boolean autoAnswer) {
        this.autoAnswer = autoAnswer;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public int getPositionTiming() {
        return positionTiming;
    }

    public void setPositionTiming(int positionTiming) {
        this.positionTiming = positionTiming;
    }

    public boolean isAutoTimezone() {
        return autoTimezone;
    }

    public void setAutoTimezone(boolean autoTimezone) {
        this.autoTimezone = autoTimezone;
    }

    public boolean isWearerStatus() {
        return wearerStatus;
    }

    public void setWearerStatus(boolean wearerStatus) {
        this.wearerStatus = wearerStatus;
    }

    public boolean isSilentMode() {
        return silentMode;
    }

    public void setSilentMode(boolean silentMode) {
        this.silentMode = silentMode;
    }
    //String content = "{\"volumeLevel\":1,\"brightLevel\":0,\"savingMode\":false}";

    public int getScreenOffTimer() {
        return screenOffTimer;
    }

    public SettingPrefModel setScreenOffTimer(int screenOffTimer) {
        this.screenOffTimer = screenOffTimer;
        return this;
    }

    public int getVolumeLevel() {
        return volumeLevel;
    }

    public void setVolumeLevel(int volumeLevel) {
        this.volumeLevel = volumeLevel;
    }

    public int getBrightLevel() {
        return brightLevel;
    }

    public void setBrightLevel(int brightLevel) {
        this.brightLevel = brightLevel;
    }
}
