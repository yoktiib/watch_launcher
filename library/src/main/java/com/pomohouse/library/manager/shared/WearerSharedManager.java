package com.pomohouse.library.manager.shared;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.library.manager.shared.controls.ISharedWearerInfo;

/**
 * Created by Admin on 4/27/2016 AD.
 */
public class WearerSharedManager extends AbstractSharedPreferences implements ISharedWearerInfo {
    private final String WATCH_INFO = "KEY_WATCH_INFO";

    public WearerSharedManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addWearerInfo(WearerModelPref wearerModel) {
        writeString(WATCH_INFO, new Gson().toJson(wearerModel));
    }

    @Override
    public WearerModelPref getWearerInfo() {
        return new Gson().fromJson(readString(WATCH_INFO), WearerModelPref.class);
    }

    @Override
    public void removeWearerInfo() {
        removeKey(WATCH_INFO);
    }
}
