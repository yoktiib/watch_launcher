package com.pomohouse.launcher.utils;

import static com.pomohouse.launcher.utils.EventConstant.EventSetting.EVENT_THEME_MANAGEMENT_CODE;

/**
 * Created by Admin on 11/10/2016 AD.
 */

public class EventConstant {
    public static class EventContact {
        public static final int EVENT_BFF_CODE = 1141;
        public static final int EVENT_SYNC_CONTACT = 1101;
        public static final int EVENT_UPDATE_LOCAL_CONTACT = 1102;
    }

    public static class EventPair {
        public static final int EVENT_PAIR_CODE = 1111;
        public static final int EVENT_UN_PAIR_CODE = 1121;
        public static final int EVENT_GET_PAIR_CODE = 1151;
    }

    public static class EventAlarmService {
        public static final int EVENT_ALARM_SET_UP_CODE = 1211;
        public static final int EVENT_IN_CLASS_MODE_SET_UP_CODE = 1221;
        public static final int EVENT_SLEEP_TIME_SET_UP_CODE = 1231;
    }

    public static class EventChat {
        public static final int EVENT_MESSAGE_CODE = 1311;
        public static final int EVENT_GROUP_CHAT_CODE = 1321;
    }

    public static class EventDeviceInfo {
        public static final int EVENT_RESET_FACTORY_CODE = 1131;
        public static final int EVENT_RESTART_CODE = 1132;
        public static final int EVENT_SHUTDOWN_CODE = 1133;

        public static final int EVENT_APP_TIMEZONE_CODE = 1800;

        public static final int EVENT_AUTO_ANSWER_CODE = 1811;
        public static final int EVENT_TIMER_LOCATION_CODE = 1821;
        public static final int EVENT_SILENT_MODE_CODE = 1831;
        public static final int EVENT_TIMEZONE_CODE = 1841;
        public static final int EVENT_WEARER_STATUS_CODE = 1851;
        public static final int EVENT_BRIGHTNESS_TIME_OUT_CODE = 1871;
        public static final int EVENT_LOCATION_REQUEST_CODE = 1881;
        public static final int EVENT_LANGUAGE_CODE = 1891;

        public static final int EVENT_TIMEFORMAT_CODE = 1842;
    }

    public static class EventSetting {
        public static final int EVENT_NOTIFICATION_VOLUME_CODE = 3111;
        public static final int EVENT_NOTIFICATION_MESSAGE_CODE = 3112;
        public static final int EVENT_NOTIFICATION_CALL_CODE = 3113;
        public static final int EVENT_NETWORK_CODE = 3131;
        static final int EVENT_THEME_MANAGEMENT_CODE = 3121;
    }

    public static class EventLocal {
        public static final int EVENT_LOCK_SCREEN_DEFAULT_CODE = 3911;
        public static final int EVENT_UNLOCK_SCREEN_DEFAULT_CODE = 3912;
        public static final int EVENT_UNLOCK_SCREEN_DEFAULT_TO_LAUNCHER_CODE = 3913;
        public static final int EVENT_LOCK_SCREEN_IN_CLASS_CODE = 3921;
        public static final int EVENT_UNLOCK_SCREEN_IN_CLASS_CODE = 3922;
        public static final int EVENT_SOS_CODE = 3931;
        public static final int EVENT_ALARM_CODE = 3942;
        public static final int EVENT_BATTERY_CHARGING_CODE = 3951;
        public static final int EVENT_UPDATE_FCM_TOKEN_CODE = 3101;
        public static final int EVENT_UPDATE_LOCATION_CODE = 3201;
        public static final int EVENT_REFRESH_LOCATION_CODE = 3202;

        public static final int EVENT_LOCK_SCREEN_NOTIFICATION_MESSAGE_CODE = 3811;
        public static final int EVENT_LOCK_SCREEN_NOTIFICATION_MUTE_CODE = 3821;
        public static final int EVENT_LOCK_SCREEN_NOTIFICATION_CALL_CODE = 3831;

        public static final int EVENT_START_POMO_SERVICE_CODE = 191;
        public static final String CONTENT_START_POMO_SERVICE = "{\"content\":\"{}\",\"eventCode\":%d,\"eventDesc\":\"START POMO SERVICE\"}";
        public static final String CONTENT_THEME_MANAGEMENT = "{\"content\":\"%1$s\",\"eventCode\":" + EVENT_THEME_MANAGEMENT_CODE + ",\"eventDesc\":\"Theme Management\"}";
    }
}
