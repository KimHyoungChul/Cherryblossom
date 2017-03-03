package com.commax.provider.settings;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class Settings extends ContentProvider {
    private static final String DB_NAME = "settings.db";
    /*
     * 5:���������� �߰�
     * 4:�����н��� �߰�
     */
    private static final int DB_VERSION = 5;//������Ʈ �� ������ų ��

    //	private static final String MIME_TYPE_ALL = "vnd.android.cursor.dir/vnd.commax.settings";
    private static final String MIME_TYPE_ONE = "vnd.android.cursor.item/vnd.commax.settings";

//	private static final int SETTING_ALL = 1;
//	private static final int SETTING_ID = 2;
//	private static UriMatcher uriMatcher;
//	static
//	{
//		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
//		uriMatcher
//				.addURI("com.commax.provider.settings", "setting", SETTING_ALL);
//		uriMatcher.addURI("com.commax.provider.settings", "setting/#",
//				SETTING_ID);
//	}

    private SQLiteDatabase settingsDB;


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public String getType(Uri uri) {

//		switch (uriMatcher.match(uri))
//		{
//		case SETTING_ALL:
//			return MIME_TYPE_ALL;
//		case SETTING_ID:
        return MIME_TYPE_ONE;
//		default:
//			throw new IllegalArgumentException("Unknown URI : " + uri);
//		}

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return null;
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        SettingsDBHelper dbHeloper = new SettingsDBHelper(context, DB_NAME, null,
                DB_VERSION);
        settingsDB = dbHeloper.getWritableDatabase();
        dbHeloper = null;
        if (settingsDB == null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SettingsDBHelper.TABLE_NAME);

//		switch (uriMatcher.match(uri))
//		{		
//		case SETTING_ID:
        qb.appendWhere(SettingsDBHelper.COL_ID + "="
                + uri.getPathSegments().get(1));
//			break;
//		default:
//			break;
//		}

        Cursor c = qb.query(settingsDB, projection, selection,
                selectionArgs, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        qb = null;

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int count = 0;

//		switch (uriMatcher.match(uri))
//		{
//		case SETTING_ALL:
//			count = settingsDB.update(SettingsDBHelper.TABLE_NAME, values, selection,
//					selectionArgs);
//			break;
//		case SETTING_ID:
        String segment = uri.getPathSegments().get(1);
        String where = "";
        if (!TextUtils.isEmpty(selection)) {
            where = " AND (" + selection + ")";
        }
        count = settingsDB.update(SettingsDBHelper.TABLE_NAME, values, "_id="
                + segment + where, selectionArgs);
//			break;
//		
//		default:
//			throw new IllegalArgumentException("Unknown URI " + uri);
//		}
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

}