package com.commax.iphomiot.doorcall.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.commax.iphomeiot.common.db.CallDBScheme;
import com.commax.iphomeiot.common.provider.CallLogProviderDefine;

public class CallLogProvider extends ContentProvider {
    private CallDBAccessor callLogAccessor_;
    private static final String AUTHORITY = CallLogProviderDefine.AUTHORITY;
    public static final Uri CONTENT_URI = CallLogProviderDefine.CONTENT_URI;
    public static final Uri SOUND_URI = Uri.parse("content://" + AUTHORITY + "/" + CallDBScheme.SoundDB.TABLENAME);

    @Override
    public boolean onCreate() {
        callLogAccessor_ = new CallDBAccessor(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase writeDb = callLogAccessor_.getWritableDatabase();
        Cursor cursor = null;
        if (uri.equals(CONTENT_URI))
            cursor = writeDb.query(CallDBScheme.TABLENAME, projection, selection, selectionArgs, null, null, sortOrder);
        else if (uri.equals(SOUND_URI))
            cursor = writeDb.query(CallDBScheme.SoundDB.TABLENAME, projection, selection, selectionArgs, null, null, sortOrder);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase writeDb = callLogAccessor_.getWritableDatabase();
        long insertId;
        if (uri.equals(CONTENT_URI)) {
            insertId = writeDb.insert(CallDBScheme.TABLENAME, null, values);
            if (insertId < 0) {
            }
        }
        else if (uri.equals(SOUND_URI)) {
            insertId = writeDb.insert(CallDBScheme.SoundDB.TABLENAME, null, values);
            if (insertId < 0) {
            }
        }

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase writeDb = callLogAccessor_.getWritableDatabase();
        if (uri.equals(CONTENT_URI))
            return writeDb.delete(CallDBScheme.TABLENAME, selection, selectionArgs);
        else if (uri.equals(SOUND_URI))
            return writeDb.delete(CallDBScheme.SoundDB.TABLENAME, selection, selectionArgs);

        return -1;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase writeDb = callLogAccessor_.getWritableDatabase();
        if (uri.equals(CONTENT_URI))
            return writeDb.update(CallDBScheme.TABLENAME, values, selection, selectionArgs);
        else if (uri.equals(SOUND_URI))
            return writeDb.update(CallDBScheme.SoundDB.TABLENAME, values, selection, selectionArgs);

        return -1;
    }
}
