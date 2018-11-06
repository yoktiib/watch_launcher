package com.pomohouse.launcher.utils.callbacks;

/**
 * Created by Admin on 9/12/16 AD.
 */
public interface SignalInfoListener {
    void onSignalChanged(int signal);
    void onNoSimCard();
    void onSimCardReady();
}
