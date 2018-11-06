package com.pomohouse.launcher.content_provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Admin on 9/9/16 AD.
 */
class POMOSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pomohouse_waffle.db";
    private static final int DATABASE_VERSION = 1;

    POMOSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TableGenerator.DATABASE_CREATE_WEARER);
        database.execSQL(TableGenerator.DATABASE_CREATE_CONTACT);
        database.execSQL(TableGenerator.DATABASE_CREATE_SETTING);
        database.execSQL(TableGenerator.DATABASE_CREATE_EVENT);
        database.execSQL(TableGenerator.DATABASE_CREATE_CALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        /*database.execSQL(TableGeneratorV2.DATABASE_CREATE_CONTACT_AVATAR_TYPE);
        database.execSQL(TableGeneratorV2.DATABASE_CREATE_CONTACT_ROLE);
        database.execSQL(TableGeneratorV2.DATABASE_CREATE_CONTACT_CONTACT_ROLE);*/
    }

    /*private static final class TableGeneratorV2 {
        static final String DATABASE_CREATE_CONTACT_AVATAR_TYPE = "ALTER table "
                + POMOContract.ContactEntry.TB_CONTACT + " ADD COLUMN "
                + POMOContract.ContactEntry.AVATAR_TYPE + " integer";

        static final String DATABASE_CREATE_CONTACT_ROLE = "ALTER table "
                + POMOContract.ContactEntry.TB_CONTACT + " ADD COLUMN "
                + POMOContract.ContactEntry.ROLE + " integer";

        static final String DATABASE_CREATE_CONTACT_CONTACT_ROLE = "ALTER table "
                + POMOContract.ContactEntry.TB_CONTACT + " ADD COLUMN "
                + POMOContract.ContactEntry.CONTACT_ROLE + " integer";
    }
*/
    private static final class TableGenerator {
        static final String DATABASE_CREATE_WEARER = "create table "
                + POMOContract.WearerEntry.TB_WEARER + "( "
                + POMOContract.WearerEntry.IMEI + " text primary key, "
                + POMOContract.WearerEntry.LATITUDE + " text, "
                + POMOContract.WearerEntry.LONGITUDE + " text,"
                + "UNIQUE(" + POMOContract.WearerEntry.IMEI + "));";

        static final String DATABASE_CREATE_CONTACT = "create table "
                + POMOContract.ContactEntry.TB_CONTACT + " ("
                + POMOContract.ContactEntry.CONTACT_ID + " text primary key, "
                + POMOContract.ContactEntry.NAME + " text, "
                + POMOContract.ContactEntry.AVATAR + " text, "
                + POMOContract.ContactEntry.AVATAR_TYPE + " integer, "
                + POMOContract.ContactEntry.GENDER + " text, "
                + POMOContract.ContactEntry.PHONE + " text, "
                + POMOContract.ContactEntry.ROLE + " integer, "
                + POMOContract.ContactEntry.CONTACT_ROLE + " integer, "
                + POMOContract.ContactEntry.CALL_TYPE + " text, "
                + POMOContract.ContactEntry.CONTACT_TYPE + " text);";

        static final String DATABASE_CREATE_SETTING = "create table "
                + POMOContract.SettingEntry.TB_SETTING + " ( "
                + POMOContract.SettingEntry.IMEI + " text primary key, "
                + POMOContract.SettingEntry.BRIGHTNESS + " integer, "
                + POMOContract.SettingEntry.VOLUME + " integer, "
                + POMOContract.SettingEntry.REQUEST_EVENT_TIMER + " integer, "
                + POMOContract.SettingEntry.REQUEST_LOCATION_TIMER + " integer);";

        static final String DATABASE_CREATE_CALL = "create table "
                + POMOContract.CallEntry.TB_CALL + " ( "
                + POMOContract.CallEntry.NUMBER + " text, "
                + POMOContract.CallEntry.DATE + " text, "
                + POMOContract.CallEntry.DURATION + " integer, "
                + POMOContract.CallEntry.IS_READ + " integer, "
                + POMOContract.CallEntry.TYPE + " integer);";

        static final String DATABASE_CREATE_EVENT = "create table "
                + POMOContract.EventEntry.TB_EVENT + " ( "
                + POMOContract.EventEntry.EVENT_ID + " integer primary key, "
                + POMOContract.EventEntry.EVENT_CODE + " integer not null, "
                + POMOContract.EventEntry.EVENT_TYPE + " integer, "
                + POMOContract.EventEntry.SENDER + " varchar(50), "
                + POMOContract.EventEntry.RECEIVE + " varchar(50), "
                + POMOContract.EventEntry.SENDER_INFO + " text, "
                + POMOContract.EventEntry.RECEIVE_INFO + " text, "
                + POMOContract.EventEntry.CONTENT + " text, "
                + POMOContract.EventEntry.STATUS + " integer, "
                + POMOContract.EventEntry.TIME_STAMP + " varchar(50));";
    }
}
