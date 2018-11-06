package com.pomohouse.library.manager.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.pomohouse.library.manager.AppContextor;

public abstract class AbstractSharedPreferences {

    protected SharedPreferences sharedPref;

    private static final String PREF_FILENAME = AppContextor.getInstance().getContext().getPackageName();
    private static final int PRIVATE_MODE = 0;

    public AbstractSharedPreferences(Context mContext) {
        sharedPref = mContext.getSharedPreferences(PREF_FILENAME, PRIVATE_MODE);
    }

    public AbstractSharedPreferences(Context mContext, String prefName) {
        sharedPref = mContext.getSharedPreferences(prefName, PRIVATE_MODE);
    }

    protected void writeBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    protected boolean readBoolean(String key, boolean defValue) {
        return sharedPref.getBoolean(key, defValue);
    }

    protected void clearAllPreferences() {
        getEditor().clear().commit();
    }

    protected void writeString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    protected String readString(String key, String defValue) {
        return sharedPref.getString(key, defValue);
    }

    protected String readString(String key) {
        return sharedPref.getString(key, null);
    }

    protected void writeInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    protected int readInt(String key, int defValue) {
        return sharedPref.getInt(key, defValue);
    }

    protected void removeKey(String key) {
        getEditor().remove(key).commit();
    }

    private Editor getEditor() {
        return sharedPref.edit();
    }
}
