package com.pomohouse.launcher.manager.notifications;

/**
 * Created by Admin on 9/12/16 AD.
 */
public class NotificationPrefModel {
    private boolean isHaveMessage = false;
    private boolean isHaveGroupChat = false;
    private boolean isHaveSilent = false;
    private boolean isHaveMute = false;
    private boolean isHaveMissCall = false;

    public boolean isHaveMessage() {
        return isHaveMessage;
    }

    public void setHaveMessage(boolean haveMessage) {
        isHaveMessage = haveMessage;
    }

    public boolean isHaveGroupChat() {
        return isHaveGroupChat;
    }

    public void setHaveGroupChat(boolean haveGroupChat) {
        isHaveGroupChat = haveGroupChat;
    }

    public boolean isHaveSilent() {
        return isHaveSilent;
    }

    public void setHaveSilent(boolean haveSilent) {
        isHaveSilent = haveSilent;
    }

    public boolean isHaveMute() {
        return isHaveMute;
    }

    public void setHaveMute(boolean haveMute) {
        isHaveMute = haveMute;
    }

    public boolean isHaveMissCall() {
        return isHaveMissCall;
    }

    public void setHaveMissCall(boolean haveMissCall) {
        isHaveMissCall = haveMissCall;
    }
}
