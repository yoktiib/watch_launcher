package com.pomohouse.launcher.content_provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import timber.log.Timber;

/**
 * Created by Admin on 9/9/16 AD.
 */
public class POMOContentProvider extends ContentProvider {
    // Use an int for each URI we will run, this represents the different queries
    public static final int WEARER = 100;
    public static final int CONTACT = 200;
    public static final int SETTING = 300;
    public static final int EVENT = 400;
    public static final int CALL = 500;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private POMOSQLiteHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new POMOSQLiteHelper(getContext());
        return true;
    }

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = POMOContract.CONTENT_AUTHORITY;
        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, POMOContract.PATH_WEARER, WEARER);
        matcher.addURI(content, POMOContract.PATH_CONTACT, CONTACT);
        matcher.addURI(content, POMOContract.PATH_SETTING, SETTING);
        matcher.addURI(content, POMOContract.PATH_EVENT, EVENT);
        matcher.addURI(content, POMOContract.PATH_CALLS, CALL);
        return matcher;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case WEARER:
                Log.i("", "getType = WEARER");
                return POMOContract.WearerEntry.CONTENT_TYPE;
            case CONTACT:
                Log.i("", "getType = CONTACT");
                return POMOContract.ContactEntry.CONTENT_TYPE;
            case SETTING:
                Log.i("", "getType = SETTING");
                return POMOContract.SettingEntry.CONTENT_TYPE;
            case EVENT:
                Log.i("", "getType = EVENT");
                return POMOContract.EventEntry.CONTENT_TYPE;
            case CALL:
                Log.i("", "getType = CALL");
                return POMOContract.CallEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case WEARER:
                Timber.i("query = Start WEARER");
                retCursor = db.query(
                        POMOContract.WearerEntry.TB_WEARER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Timber.i("query = End WEARER");
                break;
            case CONTACT:
                Timber.i("query = Start CONTACT");
                retCursor = db.query(
                        POMOContract.ContactEntry.TB_CONTACT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Timber.i("query = End CONTACT");
                break;
            case SETTING:
                Timber.i("query = Start SETTING");
                retCursor = db.query(
                        POMOContract.SettingEntry.TB_SETTING,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Timber.i("query = End SETTING");
                break;
            case EVENT:
                Timber.i("query = Start EVENT");
                retCursor = db.query(
                        POMOContract.EventEntry.TB_EVENT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Timber.i("query = End EVENT");
                break;
            case CALL:
                Timber.i("query = Start CALL");
                retCursor = db.query(
                        POMOContract.CallEntry.TB_CALL,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Timber.i("query = End CALL");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        Context context = getContext();
        if (context != null)
            retCursor.setNotificationUri(context.getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;
        switch (sUriMatcher.match(uri)) {
            case WEARER:
                _id = insertOrUpdateById(db, uri, POMOContract.WearerEntry.TB_WEARER,
                        values, POMOContract.WearerEntry.IMEI);
                if (_id > 0) {
                    Timber.i("Insert = Info Success");
                    returnUri = POMOContract.WearerEntry.buildPOMOInfoUri(_id);
                } else {
                    Timber.i("Insert = Info Error");
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CONTACT:
                _id = insertOrUpdateById(db, uri, POMOContract.ContactEntry.TB_CONTACT,
                        values, POMOContract.ContactEntry.CONTACT_ID);
                if (_id > 0) {
                    Timber.i("Insert = Contact Success");
                    returnUri = POMOContract.ContactEntry.buildContactUri(_id);
                } else {
                    Timber.i("Insert = Contact Error");
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case SETTING:
                _id = insertOrUpdateById(db, uri, POMOContract.SettingEntry.TB_SETTING,
                        values, POMOContract.SettingEntry._ID);
                if (_id > 0) {
                    Timber.i("Insert = SETTING Success");
                    returnUri = POMOContract.SettingEntry.buildSettingUri(_id);
                } else {
                    Timber.i("Insert = SETTING Error");
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case EVENT:
                _id = insertOrUpdateById(db, uri, POMOContract.EventEntry.TB_EVENT,
                        values, POMOContract.EventEntry.EVENT_ID);
                if (_id > 0) {
                    Timber.i("Insert = EVENT Success");
                    returnUri = POMOContract.EventEntry.buildEventUri(_id);
                } else {
                    Timber.i("Insert = EVENT Error");
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            case CALL:
                _id = db.insertOrThrow(POMOContract.CallEntry.TB_CALL, null, values);
                returnUri = POMOContract.EventEntry.buildEventUri(_id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        Context context = getContext();
        if (context != null)
            context.getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private long insertOrUpdateById(SQLiteDatabase db, Uri uri, String table,
                                    ContentValues values, String column) throws SQLException {
        try {
            db.insertOrThrow(table, null, values);
            return 1;
        } catch (SQLiteConstraintException e) {
            int nrRows = update(uri, values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;
            else return 1;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows; // Number of rows effected

        switch (sUriMatcher.match(uri)) {
            case WEARER:
                rows = db.delete(POMOContract.WearerEntry.TB_WEARER, selection, selectionArgs);
                break;
            case CONTACT:
                rows = db.delete(POMOContract.ContactEntry.TB_CONTACT, selection, selectionArgs);
                break;
            case SETTING:
                rows = db.delete(POMOContract.SettingEntry.TB_SETTING, selection, selectionArgs);
                break;
            case EVENT:
                rows = db.delete(POMOContract.EventEntry.TB_EVENT, selection, selectionArgs);
                break;
            case CALL:
                rows = db.delete(POMOContract.CallEntry.TB_CALL, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows:
        if (rows != 0) {
            Context context = getContext();
            if (context != null)
                context.getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case WEARER:
                rows = db.update(POMOContract.WearerEntry.TB_WEARER, values, selection, selectionArgs);
                break;
            case CONTACT:
                rows = db.update(POMOContract.ContactEntry.TB_CONTACT, values, selection, selectionArgs);
                break;
            case SETTING:
                rows = db.update(POMOContract.SettingEntry.TB_SETTING, values, selection, selectionArgs);
                break;
            case EVENT:
                rows = db.update(POMOContract.EventEntry.TB_EVENT, values, selection, selectionArgs);
                break;
            case CALL:
                rows = db.update(POMOContract.CallEntry.TB_CALL, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rows != 0) {
            Context context = getContext();
            if (context != null)
                context.getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
}