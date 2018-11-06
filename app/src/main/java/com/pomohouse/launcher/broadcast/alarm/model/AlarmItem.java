package com.pomohouse.launcher.broadcast.alarm.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.pomohouse.launcher.models.AlarmModel;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.Calendar;


public class AlarmItem extends SimplePropertyCollection implements Serializable {
    private static final long serialVersionUID = -6373647736400889313L;
    public static final int TYPE_ALARM = 1;
    public AlarmItem() {
        super(ALARM_DEFAULTS_ALL);
    }

    public AlarmItem(BufferedReader reader, boolean skipId) throws Exception {
        super(ALARM_DEFAULTS_ALL, reader, skipId, KEY_ROW_ID);
    }

    public AlarmItem(SimpleProperty[] defaults, Cursor cur) {
        super(defaults, cur);
    }

    public AlarmItem(Cursor cur) {
        super(ALARM_DEFAULTS_ALL, cur);
    }

    public AlarmItem setUpData(AlarmModel item) {
        set(AlarmItem.KEY_ALARM_ID, item.getAlarmId());
        set(AlarmItem.KEY_SOUND, item.getSoundId());
        set(AlarmItem.KEY_IMEI, item.getImei());
        set(AlarmItem.KEY_SECTION_TYPE, AlarmItem.TYPE_ALARM);
        set(AlarmItem.KEY_TYPE, item.getAlarmType());
        set(AlarmItem.KEY_NAME, item.getName());
        set(AlarmItem.KEY_ENABLED, item.getStatus().equalsIgnoreCase("Y"));
        manageTime(item.getTime().split(":"));
        manageDayOfWeek(item.getPeriodDate().split(","));
        return this;
    }

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_IMEI = "_imei";
    public static final String KEY_ALARM_ID = "alarm_id";
    public static final String KEY_HOUR = "alarm_hour";
    public static final String KEY_MINUTE = "alarm_minute";
    public static final String KEY_SOUND = "alarm_sound";
    public static final String KEY_RPT_MON = "alarm_rpt_mon";
    public static final String KEY_RPT_TUES = "alarm_rpt_tues";
    public static final String KEY_RPT_WED = "alarm_rpt_wed";
    public static final String KEY_RPT_THURS = "alarm_rpt_thurs";
    public static final String KEY_RPT_FRI = "alarm_rpt_fri";
    public static final String KEY_RPT_SAT = "alarm_rpt_sat";
    public static final String KEY_RPT_SUN = "alarm_rpt_sun";
    public static final String KEY_ENABLED = "alarm_enabled";
    public static final String KEY_NAME = "alarm_name";
    public static final String KEY_SECTION_TYPE = "alarm_section";
    public static final String KEY_TYPE = "alarm_type";
    public static final String KEY_MEDIA_VOLUME = "alarm_media_volume";
    public static final String KEY_DO_NOT_LAUNCH_ON_CALL = "alarm_do_not_launch_call";

    static final SimpleProperty[] ALARM_DEFAULTS_ALL = new SimpleProperty[]{
            new SimpleProperty(KEY_ROW_ID, 0),
            new SimpleProperty(KEY_ALARM_ID, 0),
            new SimpleProperty(KEY_IMEI, ""),
            new SimpleProperty(KEY_NAME, ""),
            new SimpleProperty(KEY_HOUR, 12),
            new SimpleProperty(KEY_MINUTE, 0),
            new SimpleProperty(KEY_SOUND, 0),
            new SimpleProperty(KEY_TYPE, 0),
            new SimpleProperty(KEY_SECTION_TYPE, 0),
            new SimpleProperty(KEY_MEDIA_VOLUME, 15),
            new SimpleProperty(KEY_DO_NOT_LAUNCH_ON_CALL, true),
            new SimpleProperty(KEY_RPT_MON, false),
            new SimpleProperty(KEY_RPT_TUES, false),
            new SimpleProperty(KEY_RPT_WED, false),
            new SimpleProperty(KEY_RPT_THURS, false),
            new SimpleProperty(KEY_RPT_FRI, false),
            new SimpleProperty(KEY_RPT_SAT, false),
            new SimpleProperty(KEY_RPT_SUN, false),
            new SimpleProperty(KEY_ENABLED, false)
    };

    public static final SimpleProperty[] ALARM_DEFAULTS_LIST = new SimpleProperty[]{
            new SimpleProperty(KEY_ROW_ID, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_ALARM_ID, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_NAME, SimpleProperty.TYPE_TEXT, 0),
            new SimpleProperty(KEY_IMEI, SimpleProperty.TYPE_TEXT, 0),
            new SimpleProperty(KEY_HOUR, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_MINUTE, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_SOUND, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_TYPE, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_SECTION_TYPE, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_MEDIA_VOLUME, SimpleProperty.TYPE_INT, 0),
            new SimpleProperty(KEY_DO_NOT_LAUNCH_ON_CALL, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_MON, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_TUES, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_WED, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_THURS, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_FRI, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_SAT, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_RPT_SUN, SimpleProperty.TYPE_BOOL, 0),
            new SimpleProperty(KEY_ENABLED, SimpleProperty.TYPE_BOOL, 0)
    };

    public String getAlarmText() {
        return getAlarmText(getInt(KEY_HOUR), getInt(KEY_MINUTE));
    }

    private static String getAlarmText(int hour, int minute) {
        String str;
        String hr = hour + "";
        String mn = minute + "";
        String ap = " PM";
        if (hour < 12) {
            ap = " AM";
        } else if (hour > 12) {
            hr = (hour - 12) + "";
        }
        if (hour == 0) {
            hr = "12";
        }
        if (minute < 10) {
            mn = "0" + minute;
        }

        str = hr + ":" + mn + ap;
        return str;
    }

    public String getRepeatText() {

        return getRepeatText(getBool(KEY_RPT_MON), getBool(KEY_RPT_TUES), getBool(KEY_RPT_WED), getBool(KEY_RPT_THURS), getBool(KEY_RPT_FRI), getBool(KEY_RPT_SAT), getBool(KEY_RPT_SUN));
    }


    public void manageTime(String[] time) {
        if (time.length < 2)
            return;
        set(AlarmItem.KEY_HOUR, Integer.parseInt(time[0]));
        set(AlarmItem.KEY_MINUTE, Integer.parseInt(time[1]));
    }

    public void manageDayOfWeek(String[] dayAlarms) {
        if (dayAlarms == null)
            return;
        if (dayAlarms.length > 0) {
            for (String day : dayAlarms) {
                switch (day) {
                    case "Sun":
                        set(AlarmItem.KEY_RPT_SUN, true);
                        break;
                    case "Mon":
                        set(AlarmItem.KEY_RPT_MON, true);
                        break;
                    case "Tue":
                        set(AlarmItem.KEY_RPT_TUES, true);
                        break;
                    case "Wed":
                        set(AlarmItem.KEY_RPT_WED, true);
                        break;
                    case "Thu":
                        set(AlarmItem.KEY_RPT_THURS, true);
                        break;
                    case "Fri":
                        set(AlarmItem.KEY_RPT_FRI, true);
                        break;
                    case "Sat":
                        set(AlarmItem.KEY_RPT_SAT, true);
                        break;
                }
            }
        }
    }

    private static String getRepeatText(boolean mon, boolean tue, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
        String str = "";
        if (mon) {
            str = addDayToRepeatText(str, "Mon");
        }
        if (tue) {
            str = addDayToRepeatText(str, "Tues");
        }
        if (wed) {
            str = addDayToRepeatText(str, "Wed");
        }
        if (thur) {
            str = addDayToRepeatText(str, "Thur");
        }
        if (fri) {
            str = addDayToRepeatText(str, "Fri");
        }
        if (sat) {
            str = addDayToRepeatText(str, "Sat");
        }
        if (sun) {
            str = addDayToRepeatText(str, "Sun");
        }
        if (str.contentEquals(""))
            str = "Never repeat";
        return str;
    }

    private static String addDayToRepeatText(String str, String day) {
        if (!str.contentEquals(""))
            str += ", ";
        return str + day;
    }

    public boolean hasRepeat() {
        return getBool(KEY_RPT_MON) || getBool(KEY_RPT_TUES) || getBool(KEY_RPT_WED) || getBool(KEY_RPT_THURS) || getBool(KEY_RPT_FRI) || getBool(KEY_RPT_SAT) || getBool(KEY_RPT_SUN);
    }

    private ContentValues getRptVals() {
        ContentValues cv = new ContentValues();
        cv.put(Calendar.SUNDAY + "", getBool(KEY_RPT_SUN));
        cv.put(Calendar.MONDAY + "", getBool(KEY_RPT_MON));
        cv.put(Calendar.TUESDAY + "", getBool(KEY_RPT_TUES));
        cv.put(Calendar.WEDNESDAY + "", getBool(KEY_RPT_WED));
        cv.put(Calendar.THURSDAY + "", getBool(KEY_RPT_THURS));
        cv.put(Calendar.FRIDAY + "", getBool(KEY_RPT_FRI));
        cv.put(Calendar.SATURDAY + "", getBool(KEY_RPT_SAT));
        return cv;
    }

    private Calendar getNextAlarmCalendar() {
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTimeInMillis(System.currentTimeMillis());

        Calendar aCal = Calendar.getInstance();
        aCal.setTimeInMillis(todayCal.getTimeInMillis());
        aCal.set(Calendar.HOUR_OF_DAY, getInt(KEY_HOUR));
        aCal.set(Calendar.MINUTE, getInt(KEY_MINUTE));
        aCal.set(Calendar.SECOND, 0);

        if (todayCal.compareTo(aCal) == 1) {
            aCal.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (hasRepeat()) {
            int day = aCal.get(Calendar.DAY_OF_WEEK);
            ContentValues cv = getRptVals();
            while (!cv.getAsBoolean(day + "")) {
                aCal.add(Calendar.DAY_OF_YEAR, 1);
                day = aCal.get(Calendar.DAY_OF_WEEK);
            }
        }
        return aCal;
    }

    public long getNextAbsoluteTimeInMillis() {
        return getNextAlarmCalendar().getTimeInMillis();
    }

    public boolean isNew() {
        return (getInt(KEY_ROW_ID) == 0);
    }

}


