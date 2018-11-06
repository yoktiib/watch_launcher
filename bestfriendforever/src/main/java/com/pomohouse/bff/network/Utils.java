package com.pomohouse.bff.network;

/**
 * Created by Admin on 8/30/16 AD.
 */
public class Utils {
    private int TIME_LOOPER = 5;
    private static Utils ourInstance = new Utils();

    public int getTIME_LOOPER() {
        return TIME_LOOPER;
    }

    public void setTIME_LOOPER(int TIME_LOOPER) {
        this.TIME_LOOPER = TIME_LOOPER;
    }

    public static Utils getOurInstance() {
        return ourInstance;
    }

    public static void setOurInstance(Utils ourInstance) {
        Utils.ourInstance = ourInstance;
    }

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }
}
