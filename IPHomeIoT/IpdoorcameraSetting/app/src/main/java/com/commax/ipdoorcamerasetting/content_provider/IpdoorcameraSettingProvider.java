package com.commax.ipdoorcamerasetting.content_provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * SIP 관련 레코드 관리하는 Content Provider
 */
public class IpdoorcameraSettingProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = IpdoorcameraSettingProvider.class.getSimpleName();
    private DBHelper mOpenHelper;

    // Codes for the UriMatcher //////
    private static final int DONGHO = 100;
    private static final int DEVICE = 200;

    ////////

    /**
     * URI Matcher 생성
     *
     * @return UriMatcher
     */
    private static UriMatcher buildUriMatcher() {
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContentProviderConstants.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, ContentProviderConstants.DonghoEntry.TABLE_NAME, DONGHO);
        matcher.addURI(authority, ContentProviderConstants.DeviceEntry.TABLE_NAME, DEVICE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DBHelper(getContext());

        return true;
    }


    /**
     * URI 타입 가져옴
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        Log.d(LOG_TAG, "uri match: " + match);

        switch (match) {
            case DONGHO: {
                return ContentProviderConstants.DonghoEntry.CONTENT_DIR_TYPE;
            }

            case DEVICE: {
                return ContentProviderConstants.DeviceEntry.CONTENT_DIR_TYPE;
            }


            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * 레코드 조회
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case DONGHO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ContentProviderConstants.DonghoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case DEVICE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ContentProviderConstants.DeviceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }


            default: {
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * 레코드 삽입
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case DONGHO: {
                long _id = db.insert(ContentProviderConstants.DonghoEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = ContentProviderConstants.DonghoEntry.buildFlavorsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            case DEVICE: {
                long _id = db.insert(ContentProviderConstants.DeviceEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    returnUri = ContentProviderConstants.DeviceEntry.buildFlavorsUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }


            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    /**
     * 레코드 삭제
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match) {
            case DONGHO:
                numDeleted = db.delete(
                        ContentProviderConstants.DonghoEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        ContentProviderConstants.DonghoEntry.TABLE_NAME + "'");
                break;
            case DEVICE:
                numDeleted = db.delete(
                        ContentProviderConstants.DeviceEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        ContentProviderConstants.DeviceEntry.TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    /**
     * 레코드 일괄 삽입
     *
     * @param uri
     * @param values
     * @return
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
//        switch (match) {
//            case SCREENSAVER_TIME:
//                // allows for multiple transactions
//                db.beginTransaction();
//
//                // keep track of successful inserts
//                int numInserted = 0;
//                try {
//                    for (ContentValues value : values) {
//                        if (value == null) {
//                            throw new IllegalArgumentException("Cannot have null content values");
//                        }
//                        long _id = -1;
//                        try {
//                            _id = db.insertOrThrow(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
//                                    null, value);
//                        } catch (SQLiteConstraintException e) {
//                            Log.w(NewSettingConstants.LOG_TAG, "Attempting to insert " +
//                                    value.getAsString(
//                                            ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE)
//                                    + " but value is already in database.");
//                        }
//                        if (_id != -1) {
//                            numInserted++;
//                        }
//                    }
//                    if (numInserted > 0) {
//                        // If no errors, declare a successful transaction.
//                        // database will not populate if this is not called
//                        db.setTransactionSuccessful();
//                    }
//                } finally {
//                    // all transactions occur at once
//                    db.endTransaction();
//                }
//                if (numInserted > 0) {
//                    // if there was successful insertion, notify the content resolver that there
//                    // was a change
//                    getContext().getContentResolver().notifyChange(uri, null);
//                }
//                return numInserted;
//
//            default:
//                return super.bulkInsert(uri, values);
//        }


        //위 주석을 푼 다음 아래 라인 삭제하세요!!
        return match;
    }

    /**
     * 레코드 업데이트
     *
     * @param uri
     * @param contentValues
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (contentValues == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case DONGHO: {
                numUpdated = db.update(ContentProviderConstants.DonghoEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case DEVICE: {
                numUpdated = db.update(ContentProviderConstants.DeviceEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }


}
