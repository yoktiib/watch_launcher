package com.pomohouse.launcher.manager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Admin on 6/11/2017 AD.
 */

public class TimeManager {

    private Date startTime, endTime, startCurrentTime, endCurrentTime;

    protected boolean validateDays(String days) {
        try {
            return days.contains((new SimpleDateFormat("EE", Locale.getDefault())).format(new Date()));
        } catch (Exception ignore) {
            return false;
        }
    }

    protected boolean validateTimeManager(String begin, String end) {
        try {
            String timeFormat = "HH:mm";
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            startTime = new SimpleDateFormat(timeFormat, Locale.getDefault()).parse(begin);
            endTime = new SimpleDateFormat(timeFormat, Locale.getDefault()).parse(end);
            if (startTime.after(endTime))
                endTime.setTime(endTime.getTime() + 24 * 60 * 60 * 1000);
            startCurrentTime = new SimpleDateFormat(timeFormat, Locale.getDefault()).parse(hours + ":" + (minutes > 0 ? (minutes + 1) : minutes));
            endCurrentTime = new SimpleDateFormat(timeFormat, Locale.getDefault()).parse(hours + ":" + (minutes > 0 ? (minutes - 1) : minutes));

            return startCurrentTime.after(startTime) && endCurrentTime.before(endTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartCurrentTime() {
        return startCurrentTime;
    }

    public void setStartCurrentTime(Date startCurrentTime) {
        this.startCurrentTime = startCurrentTime;
    }

    public Date getEndCurrentTime() {
        return endCurrentTime;
    }

    public void setEndCurrentTime(Date endCurrentTime) {
        this.endCurrentTime = endCurrentTime;
    }
}
