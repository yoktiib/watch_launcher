package com.pomohouse.launcher.manager.sleep_time;

/**
 * Created by Admin on 9/8/16 AD.
 */
public interface ISleepTimeManager {

    void addSleepTime(SleepTimePrefModel miniSetting);

    SleepTimePrefModel getSleepTime();

    void removeSleepTime();
}
