package com.pomohouse.bff.base;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Admin on 9/9/16 AD.
 */
public class POMOContract {
    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    static final String CONTENT_AUTHORITY = "com.pomohouse";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */
    static final String PATH_WEARER = "wearer";
    static final String PATH_CONTACT = "contact";
    static final String PATH_SETTING = "setting";
    static final String PATH_EVENT = "event";
    static final String PATH_CALLS = "calls";

    /**
     * Create one class for each table that handles all information regarding the table schema and
     * the URIs related to it.
     */
    public static final class WearerEntry implements BaseColumns {

        static final String TB_WEARER = "TB_WEARER";
        public static final String IMEI = "IMEI";
        public static final String LATITUDE = "LATITUDE";
        public static final String LONGITUDE = "LONGITUDE";
        // Content URI represents the base location for the table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEARER).build();

        // These are special type prefixes that specify if a URI returns a list or a specific item
        static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_WEARER;
        /*public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_WEARER;*/

        // Define a function to build a URI to find a specific movie by it's identifier
        static Uri buildPOMOInfoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ContactEntry implements BaseColumns {
        static final String TB_CONTACT = "TB_CONTACT";
        public static final String CONTACT_ID = "CONTACT_ID";
        public static final String GENDER = "GENDER";
        public static final String NAME = "NAME";
        public static final String AVATAR = "AVATAR";
        public static final String AVATAR_TYPE = "AVATAR_TYPE";
        public static final String PHONE = "PHONE";
        public static final String CONTACT_ROLE = "CONTACT_ROLE";
        public static final String CALL_TYPE = "CALL_TYPE";
        public static final String ROLE = "ROLE";
        public static final String CONTACT_TYPE = "CONTACT_TYPE";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACT).build();

        static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_CONTACT;/*
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONTACT;*/

        static Uri buildContactUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class EventEntry implements BaseColumns {
        static final String TB_EVENT = "TB_EVENT";
        public static final String EVENT_ID = "EVENT_ID";
        public static final String EVENT_CODE = "EVENT_CODE";
        public static final String EVENT_TYPE = "EVENT_TYPE";
        public static final String SENDER = "SENDER";
        public static final String RECEIVE = "RECEIVE";
        public static final String SENDER_INFO = "SENDER_INFO";
        public static final String RECEIVE_INFO = "RECEIVE_INFO";
        public static final String CONTENT = "CONTENT";
        public static final String TIME_STAMP = "TIME_STAMP";
        public static final String STATUS = "STATUS";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_EVENT;/*
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONTACT;*/

        static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SettingEntry implements BaseColumns {
        static final String TB_SETTING = "TB_SETTING";
        static final String IMEI = "IMEI";
        static final String BRIGHTNESS = "BRIGHTNESS";
        static final String VOLUME = "VOLUME";
        static final String LANGUAGE = "VOLUME";
        static final String REQUEST_LOCATION_TIMER = "REQUEST_LOCATION_TIMER";
        static final String REQUEST_EVENT_TIMER = "REQUEST_EVENT_TIMER";

        static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SETTING).build();

        static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_SETTING;/*
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONTACT;*/

        static Uri buildSettingUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CallEntry implements BaseColumns {
        static final String TB_CALL = "TB_CALL";
        public static final String NUMBER = "NUMBER";
        public static final String TYPE = "TYPE";
        public static final String DATE = "DATE";
        public static final String DURATION = "DURATION";
        public static final String IS_READ = "IS_READ";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALLS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_CALLS;/*
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_CONTACT;*/

        public static Uri buildCallUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        /** Call log type for incoming calls. */
        public static final int INCOMING_TYPE = 1;
        /** Call log type for outgoing calls. */
        public static final int OUTGOING_TYPE = 2;
        /** Call log type for missed calls. */
        public static final int MISSED_TYPE = 3;
        /** Call log type for voicemails. */
        public static final int VOICEMAIL_TYPE = 4;
        /** Call log type for calls rejected by direct user action. */
        public static final int REJECTED_TYPE = 5;
        /** Call log type for calls blocked automatically. */
        public static final int BLOCKED_TYPE = 6;
    }
}