package com.pomohouse.launcher.broadcast.alarm.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class AlarmDatabase {
    private static final String TABLE_NAME_ALARMS = "alarms";

    private static final String DATABASE_NAME = "dbE6AlarmAppChooser";
    private static final int DATABASE_VERSION = 3;
    private static final String[] DATABASE_DROP = {
            "DROP TABLE IF EXISTS " + TABLE_NAME_ALARMS
    };

    private final Context mCtx;

    private static final String TAG = "AlarmDatabase";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SimplePropertyCollection.getCreateTableStatement(AlarmItem.ALARM_DEFAULTS_ALL, TABLE_NAME_ALARMS));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            for (String aDATABASE_DROP : DATABASE_DROP) {
                db.execSQL(aDATABASE_DROP);
            }
            onCreate(db);
        }
    }

    public AlarmDatabase(Context ctx) {
        this.mCtx = ctx;
    }

    public AlarmDatabase open() {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        if (mDbHelper != null)
            mDbHelper.close();
    }

    public Cursor fetchAllAlarms() {
        return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_LIST), null, null, null, null, AlarmItem.KEY_ROW_ID);
    }

    public Cursor fetchAllAlarmsForBackup() {
        return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_ALL), null, null, null, null, AlarmItem.KEY_ROW_ID);
    }

    public Cursor fetchEnabledAlarms() {
        mDb = mDbHelper.getWritableDatabase();
        return mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_LIST), AlarmItem.KEY_ENABLED + "=1", null, null, null, AlarmItem.KEY_ROW_ID);
    }

    public AlarmItem getAlarmById(long id) {
        mDb = mDbHelper.getWritableDatabase();
        if (id == 0) {
            return new AlarmItem();
        }
        Cursor cur = mDb.query(TABLE_NAME_ALARMS, SimplePropertyCollection.getKeyArray(AlarmItem.ALARM_DEFAULTS_ALL), AlarmItem.KEY_ROW_ID + "=" + id, null, null, null, AlarmItem.KEY_ROW_ID);
        if (cur == null) {
            return new AlarmItem();
        }
        cur.moveToFirst();
        if (cur.isAfterLast()) {
            cur.close();
            return new AlarmItem();
        } else {
            AlarmItem ai = new AlarmItem(cur);
            cur.close();
            return ai;
        }
    }

    public AlarmItem getNewAlarm() {
        return new AlarmItem();
    }

    public void saveAlarm(AlarmItem ai) {
        //mDb = mDbHelper.getWritableDatabase();
        ai.saveItem(mDb, TABLE_NAME_ALARMS, AlarmItem.KEY_ROW_ID);
    }

    public void setAlarmEnabled(long alarmId, boolean enabled) {
        mDb = mDbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AlarmItem.KEY_ENABLED, enabled);
        mDb.update(TABLE_NAME_ALARMS, cv, AlarmItem.KEY_ROW_ID + "=" + alarmId, null);
        mDb.close();
    }

    public void deleteAlarm(long alarm_id) {
        mDb.delete(TABLE_NAME_ALARMS, AlarmItem.KEY_ROW_ID + "=" + alarm_id, null);
        mDb.close();
    }

    public void deleteAlarmByType(long alarm_type) {
        mDb = mDbHelper.getWritableDatabase();
        mDb.delete(TABLE_NAME_ALARMS, AlarmItem.KEY_SECTION_TYPE + "=" + alarm_type, null);
        mDb.close();
    }

    public void deleteAllAlarms() {
        mDb.delete(TABLE_NAME_ALARMS, null, null);
        mDb.close();
    }
}
