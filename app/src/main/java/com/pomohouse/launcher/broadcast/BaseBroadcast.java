package com.pomohouse.launcher.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Admin on 8/24/16 AD.
 */
public class BaseBroadcast extends BroadcastReceiver {
    public final static String EXTRA_DATA = "EXTRA_DATA";
    /**
     * Broadcast intent action requesting a single location update, LOCATION_SKY_HOOK_UPDATE_INTENT will be broadcast in response
     *
     * @see #LOCATION_SKY_HOOK_UPDATE_INTENT
     */
    public final static String GET_LOCATION_UPDATE_INTENT = "com.pomohouse.service.GET_LOCATION_UPDATE_INTENT";

    public final static String LOCATION_SKY_HOOK_UPDATE_INTENT = "com.pomohouse.service.LOCATION_SKY_HOOK_UPDATE_INTENT";

    //public final static String LOCATION_CELL_OR_WIFI_PROVIDER_INTENT = "com.pomohouse.service.LOCATION_CELL_OR_WIFI_PROVIDER_INTENT";
    public final static String LOCATION_GPS_AND_NETWORK_PROVIDER_INTENT = "com.pomohouse.service.LOCATION_GPS_AND_NETWORK_PROVIDER_INTENT";

    //public final static String STEP_UPDATE_INTENT = "com.pomohouse.service.STEP_UPDATE_INTENT";
    public final static String LOCK_SCREEN_INTENT = "com.pomohouse.service.LOCK_SCREEN_INTENT";

    public final static String ALARM_INTENT = "com.pomohouse.service.ALARM_INTENT";
    /**
     * Broadcast intent action requesting a single location update, SEND_EVENT_UPDATE_INTENT will be broadcast in response
     *
     * @see #SEND_EVENT_UPDATE_INTENT
     */
    public final static String SEND_EVENT_TIME_UPDATE_INTENT = "com.pomohouse.service.EVENT_TIME_UPDATE_INTENT";
    //public final static String SEND_EVENT_UPDATE_CONTACT_INTENT = "com.pomohouse.service.EVENT_UPDATE_CONTACT_INTENT";
    public final static String SEND_AUTO_ANSWER_CALL_INTENT = "com.pomohouse.service.AUTO_ANSWER";
    public final static String SEND_IN_CLASS_INTENT = "com.pomohouse.service.IN_CLASS";

    public final static String RECEIVE_FROM_OTHER_APP = "com.pomohouse.service.RECEIVE_FROM_OTHER_APP";
    public final static String SEND_EVENT_UPDATE_INTENT = "com.pomohouse.service.EVENT_UPDATE_INTENT";
    public final static String SEND_EVENT_KILL_APP = "com.pomohouse.KILL_APP";
    public final static String SEND_EVENT_INTERNET_AVAILABLE = "com.pomohouse.service.SEND_EVENT_INTERNET_AVAILABLE";
    public final static String SEND_EVENT_INTERNET_UN_AVAILABLE = "com.pomohouse.service.SEND_EVENT_INTERNET_UN_AVAILABLE";
    public final static String SEND_EVENT_UNLOCK_DEFAULT_INTENT = "com.pomohouse.service.EVENT_UNLOCK_SCREEN";
    public final static String SEND_EVENT_UNLOCK_IN_CLASS_INTENT = "com.pomohouse.service.EVENT_UNLOCK_IN_CLASS";
    public final static String SEND_EVENT_UNLOCK_DEFAULT_TO_LAUNCHER_INTENT = "com.pomohouse.service.EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER";
    public final static String SEND_EVENT_LOCK_DEFAULT_INTENT = "com.pomohouse.service.EVENT_LOCK_DEFAULT";
    public final static String SEND_EVENT_LOCK_IN_CLASS_INTENT = "com.pomohouse.service.EVENT_LOCK_IN_CLASS";
    //public final static String SEND_EVENT_LOCAL_INTENT = "com.pomohouse.service.SEND_EVENT_LOCAL_INTENT";
    public final static String SEND_SENSOR_INTENT = "com.pomohouse.service.SENSOR";
    //public final static String SEND_EVENT_PAIR_INTENT = "com.pomohouse.service.EVENT_PAIR_INTENT";
    //public final static String SEND_EVENT_MESSAGE_INTENT = "com.pomohouse.service.EVENT_MESSAGE_INTENT";
    //public final static String SEND_EVENT_GROUP_CHAT_INTENT = "com.pomohouse.service.EVENT_GROUP_CHAT_INTENT";
    //public final static String SEND_EVENT_IN_CLASS_MODE_INTENT = "com.pomohouse.service.EVENT_IN_CLASS_MODE_INTENT";
    public final static String SEND_EVENT_ALARM_INTENT = "com.pomohouse.service.EVENT_IN_ALARM_INTENT";
    //public final static String SEND_EVENT_DATE_CHANGE_INTENT = "com.pomohouse.service.EVENT_DATE_CHANGE_INTENT";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
