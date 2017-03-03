package com.commax.settings.content_provider;

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
 * 코맥스 설정 Content Provider
 */
public class CommaxSettingProvider extends ContentProvider {

    private String LOG_TAG = CommaxSettingProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DBHelper mOpenHelper;

    // Codes for the UriMatcher //////

    private static final int USE_VALUE_TIP = 2100;

    private static final int USE_VALUE_INFO = 2000;

    private static final int SCREENSAVER_TIME = 100;
    private static final int SCREENSAVER_TIME_WITH_ID = 200;


    private static final int IP_DOOR_CAMERA = 900;
    private static final int IP_DOOR_CAMERA_ITEM = 1000;

    private static final int DONGHO = 1100;
    private static final int WALLPAD_PASSWORD = 1200;

    private static final int DOOR_CAMERA = 1300;

    private static final int CALL_TIME = 1400;
    private static final int MOVIE_RECORD_TIME = 1500;
    private static final int MOVIE_AUTO_SAVE = 1600;
    private static final int SMARTPHONE_CALL = 1700;

    private static final int ONVIF_DOOR_CAMERA = 1800;
    private static final int ONVIF_CCTV = 1900;
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
        matcher.addURI(authority, ContentProviderConstants.ScreenSaverEntry.TABLE_NAME, SCREENSAVER_TIME);
        matcher.addURI(authority, ContentProviderConstants.ScreenSaverEntry.TABLE_NAME + "/#", SCREENSAVER_TIME_WITH_ID);
        matcher.addURI(authority, ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME, IP_DOOR_CAMERA);
        matcher.addURI(authority, ContentProviderConstants.DonghoEntry.TABLE_NAME, DONGHO);
        matcher.addURI(authority, ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME, WALLPAD_PASSWORD);
        matcher.addURI(authority, ContentProviderConstants.DoorCameraEntry.TABLE_NAME, DOOR_CAMERA);
        matcher.addURI(authority, ContentProviderConstants.CallTimeEntry.TABLE_NAME, CALL_TIME);
        matcher.addURI(authority, ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME, MOVIE_RECORD_TIME);
        matcher.addURI(authority, ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME, MOVIE_AUTO_SAVE);
        matcher.addURI(authority, ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME, SMARTPHONE_CALL);
        matcher.addURI(authority, ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME, ONVIF_DOOR_CAMERA);
        matcher.addURI(authority, ContentProviderConstants.OnvifCctvEntry.TABLE_NAME, ONVIF_CCTV);
        matcher.addURI(authority, ContentProviderConstants.UseValueInfoEntry.TABLE_NAME, USE_VALUE_INFO);
        matcher.addURI(authority, ContentProviderConstants.UseValueTipEntry.TABLE_NAME, USE_VALUE_TIP);

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
            case SCREENSAVER_TIME: {
                return ContentProviderConstants.ScreenSaverEntry.CONTENT_DIR_TYPE;
            }
            case SCREENSAVER_TIME_WITH_ID: {
                return ContentProviderConstants.ScreenSaverEntry.CONTENT_ITEM_TYPE;
            }


            case IP_DOOR_CAMERA: {
                return ContentProviderConstants.IPDoorCameraEntry.CONTENT_DIR_TYPE;
            }

            case DONGHO: {
                return ContentProviderConstants.DonghoEntry.CONTENT_DIR_TYPE;
            }

            case WALLPAD_PASSWORD: {
                return ContentProviderConstants.WallpadPasswordEntry.CONTENT_DIR_TYPE;
            }

            case DOOR_CAMERA: {
                return ContentProviderConstants.DoorCameraEntry.CONTENT_DIR_TYPE;
            }

            case CALL_TIME: {
                return ContentProviderConstants.CallTimeEntry.CONTENT_DIR_TYPE;
            }

            case MOVIE_RECORD_TIME: {
                return ContentProviderConstants.MovieRecordTimeEntry.CONTENT_DIR_TYPE;
            }

            case MOVIE_AUTO_SAVE: {
                return ContentProviderConstants.MovieAutoSaveEntry.CONTENT_DIR_TYPE;
            }

            case SMARTPHONE_CALL: {
                return ContentProviderConstants.SmartPhoneCallEntry.CONTENT_DIR_TYPE;
            }

            case ONVIF_DOOR_CAMERA: {
                return ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_DIR_TYPE;
            }

            case ONVIF_CCTV: {
                return ContentProviderConstants.OnvifCctvEntry.CONTENT_DIR_TYPE;
            }

            case USE_VALUE_INFO: {
                return ContentProviderConstants.UseValueInfoEntry.CONTENT_DIR_TYPE;
            }

            case USE_VALUE_TIP: {
                return ContentProviderConstants.UseValueTipEntry.CONTENT_DIR_TYPE;
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

        try {
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

                case WALLPAD_PASSWORD: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case SCREENSAVER_TIME: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case SCREENSAVER_TIME_WITH_ID: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                            projection,
                            ContentProviderConstants.ScreenSaverEntry._ID + " = ?",
                            new String[]{String.valueOf(ContentUris.parseId(uri))},
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case CALL_TIME: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.CallTimeEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case MOVIE_RECORD_TIME: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case MOVIE_AUTO_SAVE: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case SMARTPHONE_CALL: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case ONVIF_DOOR_CAMERA: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case DOOR_CAMERA: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.DoorCameraEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case IP_DOOR_CAMERA: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case ONVIF_CCTV: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.OnvifCctvEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case USE_VALUE_INFO: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.UseValueInfoEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);
                    return retCursor;
                }

                case USE_VALUE_TIP: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            ContentProviderConstants.UseValueTipEntry.TABLE_NAME,
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
        } catch (Exception e){
            retCursor= null;
        }

        return retCursor;
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

        try {
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

                case WALLPAD_PASSWORD: {
                    long _id = db.insert(ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.WallpadPasswordEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case SCREENSAVER_TIME: {
                    long _id = db.insert(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.ScreenSaverEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case CALL_TIME: {
                    long _id = db.insert(ContentProviderConstants.CallTimeEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.CallTimeEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case MOVIE_RECORD_TIME: {
                    long _id = db.insert(ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.MovieRecordTimeEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case MOVIE_AUTO_SAVE: {
                    long _id = db.insert(ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.MovieAutoSaveEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case SMARTPHONE_CALL: {
                    long _id = db.insert(ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.SmartPhoneCallEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case ONVIF_DOOR_CAMERA: {
                    long _id = db.insert(ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.OnvifDoorCameraDeviceEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case IP_DOOR_CAMERA: {
                    long _id = db.insert(ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.IPDoorCameraEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case DOOR_CAMERA: {
                    long _id = db.insert(ContentProviderConstants.DoorCameraEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.DoorCameraEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case ONVIF_CCTV: {
                    long _id = db.insert(ContentProviderConstants.OnvifCctvEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.OnvifCctvEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case USE_VALUE_INFO: {
                    long _id = db.insert(ContentProviderConstants.UseValueInfoEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.UseValueInfoEntry.buildFlavorsUri(_id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into: " + uri);
                    }
                    break;
                }

                case USE_VALUE_TIP: {
                    long _id = db.insert(ContentProviderConstants.UseValueTipEntry.TABLE_NAME, null, values);
                    // insert unless it is already contained in the database
                    if (_id > 0) {
                        returnUri = ContentProviderConstants.UseValueTipEntry.buildFlavorsUri(_id);
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
        } catch (Exception e) {
            returnUri= null;
        }

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

        try {
            switch (match) {
                case DONGHO:
                    numDeleted = db.delete(
                            ContentProviderConstants.DonghoEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.DonghoEntry.TABLE_NAME + "'");
                    break;

                case WALLPAD_PASSWORD:
                    numDeleted = db.delete(
                            ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME + "'");
                    break;

                case DOOR_CAMERA:
                    numDeleted = db.delete(
                            ContentProviderConstants.DoorCameraEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.DoorCameraEntry.TABLE_NAME + "'");
                    break;


                case SCREENSAVER_TIME:
                    numDeleted = db.delete(
                            ContentProviderConstants.ScreenSaverEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.ScreenSaverEntry.TABLE_NAME + "'");
                    break;
                case SCREENSAVER_TIME_WITH_ID:
                    numDeleted = db.delete(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                            ContentProviderConstants.ScreenSaverEntry._ID + " = ?",
                            new String[]{String.valueOf(ContentUris.parseId(uri))});
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.ScreenSaverEntry.TABLE_NAME + "'");

                    break;

                case IP_DOOR_CAMERA:
                    numDeleted = db.delete(
                            ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME + "'");
                    break;

                case CALL_TIME:
                    numDeleted = db.delete(
                            ContentProviderConstants.CallTimeEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.CallTimeEntry.TABLE_NAME + "'");
                    break;

                case MOVIE_RECORD_TIME:
                    numDeleted = db.delete(
                            ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME + "'");
                    break;

                case MOVIE_AUTO_SAVE:
                    numDeleted = db.delete(
                            ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME + "'");
                    break;

                case SMARTPHONE_CALL:
                    numDeleted = db.delete(
                            ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME + "'");
                    break;

                case ONVIF_DOOR_CAMERA:
                    numDeleted = db.delete(
                            ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME + "'");
                    break;

                case ONVIF_CCTV:
                    numDeleted = db.delete(
                            ContentProviderConstants.OnvifCctvEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.OnvifCctvEntry.TABLE_NAME + "'");
                    break;

                case USE_VALUE_INFO:
                    numDeleted = db.delete(
                            ContentProviderConstants.UseValueInfoEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.UseValueInfoEntry.TABLE_NAME + "'");
                    break;

                case USE_VALUE_TIP:
                    numDeleted = db.delete(
                            ContentProviderConstants.UseValueTipEntry.TABLE_NAME, selection, selectionArgs);
                    // reset _ID
                    db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                            ContentProviderConstants.UseValueTipEntry.TABLE_NAME + "'");
                    break;


                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        } catch (Exception e) {
            numDeleted= 0;
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
        switch (match) {
            case SCREENSAVER_TIME:
                // allows for multiple transactions
                db.beginTransaction();

                // keep track of successful inserts
                int numInserted = 0;
                try {
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }
                        long _id = -1;
                        try {
                            _id = db.insertOrThrow(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                                    null, value);
                        } catch (SQLiteConstraintException e) {
                            Log.w(LOG_TAG, "Attempting to insert " +
                                    value.getAsString(
                                            ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE)
                                    + " but value is already in database.");
                        }
                        if (_id != -1) {
                            numInserted++;
                        }
                    }
                    if (numInserted > 0) {
                        // If no errors, declare a successful transaction.
                        // database will not populate if this is not called
                        db.setTransactionSuccessful();
                    }
                } finally {
                    // all transactions occur at once
                    db.endTransaction();
                }
                if (numInserted > 0) {
                    // if there was successful insertion, notify the content resolver that there
                    // was a change
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return numInserted;

            default:
                return super.bulkInsert(uri, values);
        }
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
            case WALLPAD_PASSWORD: {
                numUpdated = db.update(ContentProviderConstants.WallpadPasswordEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case DOOR_CAMERA: {
                numUpdated = db.update(ContentProviderConstants.DoorCameraEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case SCREENSAVER_TIME: {
                numUpdated = db.update(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case SCREENSAVER_TIME_WITH_ID: {
                numUpdated = db.update(ContentProviderConstants.ScreenSaverEntry.TABLE_NAME,
                        contentValues,
                        ContentProviderConstants.ScreenSaverEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            }

            case IP_DOOR_CAMERA: {
                numUpdated = db.update(ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case CALL_TIME: {
                numUpdated = db.update(ContentProviderConstants.CallTimeEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case MOVIE_RECORD_TIME: {
                numUpdated = db.update(ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case MOVIE_AUTO_SAVE: {
                numUpdated = db.update(ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case SMARTPHONE_CALL: {
                numUpdated = db.update(ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case ONVIF_DOOR_CAMERA: {
                numUpdated = db.update(ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case ONVIF_CCTV: {
                numUpdated = db.update(ContentProviderConstants.OnvifCctvEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case USE_VALUE_INFO: {
                numUpdated = db.update(ContentProviderConstants.UseValueInfoEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }

            case USE_VALUE_TIP: {
                numUpdated = db.update(ContentProviderConstants.UseValueTipEntry.TABLE_NAME,
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
