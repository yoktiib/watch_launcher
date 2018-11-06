package com.pomohouse.launcher.manager.sleep_time;

import android.content.Context;

import com.google.gson.Gson;
import com.pomohouse.library.manager.shared.AbstractSharedPreferences;

import static com.pomohouse.launcher.manager.DataManagerConstant.KEY_SLEEP_TIME;

/**
 * Created by Admin on 9/5/16 AD.
 */
public class SleepTimePrefManager extends AbstractSharedPreferences implements ISleepTimeManager {
    private final String SLEEP_TIME = KEY_SLEEP_TIME;

    public SleepTimePrefManager(Context mContext) {
        super(mContext);
    }

    @Override
    public void addSleepTime(SleepTimePrefModel sleep) {
        writeString(KEY_SLEEP_TIME, new Gson().toJson(sleep));
    }

    @Override
    public SleepTimePrefModel getSleepTime() {
        SleepTimePrefModel miniSettingModel = new Gson().fromJson(readString(KEY_SLEEP_TIME), SleepTimePrefModel.class);
        if (miniSettingModel == null)
            addSleepTime(miniSettingModel = new SleepTimePrefModel());
        return miniSettingModel;
    }

    @Override
    public void removeSleepTime() {
        removeKey(KEY_SLEEP_TIME);
    }
}
