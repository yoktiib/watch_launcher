package com.pomohouse.launcher.lock_screen;

import android.content.Context;

import com.pomohouse.launcher.R;

/**
 * Created by Admin on 11/30/2016 AD.
 */

public class AlarmTypeManager {
    private Integer[] alarmTypeArr = {R.drawable.scheduler_wakeup, R.drawable.scheduler_brush_teeth, R.drawable.scheduler_shower, R.drawable.scheduler_meal, R.drawable.scheduler_school, R.drawable.scheduler_free_time, R.drawable.scheduler_chores, R.drawable.scheduler_homework, R.drawable.scheduler_ready_for_bed, R.drawable.scheduler_sleep};
    private String[] alarmNameArr;

    int getAlarmResource(int alarmType) {
        if (alarmTypeArr == null)
            return R.drawable.scheduler_brush_teeth;
        if (alarmTypeArr.length >= alarmType && alarmType != 0)
            return alarmTypeArr[alarmType - 1];
        else return R.drawable.scheduler_brush_teeth;
    }

    String getAlarmName(Context mContext, int alarmType) {
        if (alarmNameArr == null)
            alarmNameArr = mContext.getResources().getStringArray(R.array.scheduleNameArr);
        if (alarmTypeArr == null)
            return "";
        if (alarmNameArr.length >= alarmType && alarmType != 0)
            return alarmNameArr[alarmType - 1];
        else return "";
    }
}
