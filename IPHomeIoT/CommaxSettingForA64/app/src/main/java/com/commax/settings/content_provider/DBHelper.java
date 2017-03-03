package com.commax.settings.content_provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.commax.settings.common.Log;


/**
 * 디비 관리
 *
 * @author Jeonggyu Park
 */
class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "newSetting.db";
    //데이터베이스 버전을 높여야 재설치했을 때 새로운 테이블과 칼럼등이 생성됨

    //업데이트 이력
    // 10 -> 11 : DoorCameraEntry에 SIP번호,IS_OK 항목 추가함(2017-01-24)
    // 11 -> 12 : 홈스크린도움말 사용여부 테이블 추가함(2017-02-14)
    private static final int DATABASE_VERSION = 12;


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private String LOG_TAG = CommaxSettingProvider.class.getSimpleName();

    //2017-01-16,yslee::New DB 디폴트값
    private static final int INDEX_DB_TABLE= 0;
    private static final int INDEX_DB_FIELD= 1;
    private static final int INDEX_DB_VALUE= 2;
    private String new_dbData[][] = {
            // table name, field name, value
            {ContentProviderConstants.DonghoEntry.TABLE_NAME, ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG
                    + "," + ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO, "1234"+"\", \""+"5678"},
            {ContentProviderConstants.CallTimeEntry.TABLE_NAME, ContentProviderConstants.CallTimeEntry.COLUMN_NAME_CALLTIME, "180"},
            {ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME, ContentProviderConstants.MovieRecordTimeEntry.COLUMN_NAME_MOVIE_RECORDTIME, "60"},
            {ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME, ContentProviderConstants.MovieAutoSaveEntry.COLUMN_NAME_MOVIE_AUTO_SAVE, CommaxConstants.ENABLED},
            {ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME, ContentProviderConstants.SmartPhoneCallEntry.COLUMN_NAME_SMARTPHONE_CALL, CommaxConstants.ENABLED},
            {ContentProviderConstants.UseValueInfoEntry.TABLE_NAME, ContentProviderConstants.UseValueInfoEntry.COLUMN_NAME_USE_VALUE_INFO, CommaxConstants.ENABLED},
            {ContentProviderConstants.UseValueTipEntry.TABLE_NAME, ContentProviderConstants.UseValueTipEntry.COLUMN_NAME_USE_VALUE_TIP, CommaxConstants.TRUE}
            //여기에 초기값 테이블을 추가하세요.
    };

    //홈스크린도움말 사용여부 테이블 생성
    private static final String SQL_CREATE_USE_VALUE_TIP_TABLE = "CREATE TABLE "
            + ContentProviderConstants.UseValueTipEntry.TABLE_NAME + " (" + ContentProviderConstants.UseValueTipEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.UseValueTipEntry.COLUMN_NAME_USE_VALUE_TIP + TEXT_TYPE + " )";

    //홈스크린도움말 사용여부 테이블 삭제
    private static final String SQL_DELETE_USE_VALUE_TIP_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.UseValueTipEntry.TABLE_NAME;


    //가치정보 사용여부 테이블 생성
    private static final String SQL_CREATE_USE_VALUE_INFO_TABLE = "CREATE TABLE "
            + ContentProviderConstants.UseValueInfoEntry.TABLE_NAME + " (" + ContentProviderConstants.UseValueInfoEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.UseValueInfoEntry.COLUMN_NAME_USE_VALUE_INFO + TEXT_TYPE + " )";

    //가치정보 사용여부 테이블 삭제
    private static final String SQL_DELETE_USE_VALUE_INFO_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.UseValueInfoEntry.TABLE_NAME;


    //OnVif 도어폰 카메라 테이블 생성
    private static final String SQL_CREATE_ONVIF_DOOR_CAMERA_TABLE = "CREATE TABLE "
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME + " (" + ContentProviderConstants.OnvifDoorCameraDeviceEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IP + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_DEVICE_NAME + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_STREAM_URL + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_SIP_NO + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IS_OK + TEXT_TYPE + " )";


    //OnVif 도어폰 카메라 테이블 삭제
    private static final String SQL_DELETE_ONVIF_DOOR_CAMERA_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.OnvifDoorCameraDeviceEntry.TABLE_NAME;

    //IP Door Camera 테이블 생성 : 사용안함
    private static final String SQL_CREATE_IP_DOOR_CAMERA_TABLE = "CREATE TABLE "
            + ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME + " (" + ContentProviderConstants.IPDoorCameraEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_MAIN_CODEC + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_MAIN_RESOLUTION + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_MAIN_GOP + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_MAIN_FRAMERATE + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_MAIN_BITRATE + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_SUB_CODEC + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_SUB_RESOLUTION + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_SUB_GOP + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_SUB_FRAMERATE + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.IPDoorCameraEntry.COLUMN_NAME_SUB_BITRATE + TEXT_TYPE + " )";

    //IP Door Camera 테이블 삭제
    private static final String SQL_DELETE_IP_DOOR_CAMERA_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.IPDoorCameraEntry.TABLE_NAME;

    //Custom Door Camera  테이블 생성
    private static final String SQL_CREATE_DEVICE_TABLE = "CREATE TABLE "
            + ContentProviderConstants.DoorCameraEntry.TABLE_NAME + " (" + ContentProviderConstants.DoorCameraEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_MODEL_NAME + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_MAC + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_USN + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4 + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4_GATEWAY + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4_SUBNET + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_DNS + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_SIP_NO + TEXT_TYPE + COMMA_SEP       //2017-01,24,yslee::SIP 번호 항목 추가함
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_WEBPORT + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_RTSPPORT + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_FIRST_STREAM_URL + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_SECOND_STREAM_URL + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IS_OK + TEXT_TYPE + " )"; //2017-01,24,yslee::IS_OK 번호 항목 추가함

    //Custom Door Camera 테이블 삭제
    private static final String SQL_DELETE_DEVICE_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.DoorCameraEntry.TABLE_NAME;

    //OnVif CCTV 테이블 생성
    private static final String SQL_CREATE_ONVIF_CCTV_TABLE = "CREATE TABLE "
            + ContentProviderConstants.OnvifCctvEntry.TABLE_NAME + " (" + ContentProviderConstants.OnvifCctvEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_DEVICE_NAME + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_STREAM_URL + TEXT_TYPE + " )";


    //OnVif CCTV 테이블 삭제
    private static final String SQL_DELETE_ONVIF_CCTV_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.OnvifCctvEntry.TABLE_NAME;

    //동호 테이블 생성
    private static final String SQL_CREATE_DONGHO_TABLE = "CREATE TABLE "
            + ContentProviderConstants.DonghoEntry.TABLE_NAME + " (" + ContentProviderConstants.DonghoEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO + TEXT_TYPE + " )";

    //동호 테이블 삭제
    private static final String SQL_DELETE_DONGHO_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.DonghoEntry.TABLE_NAME;

    //연속통화시간 테이블 생성
    private static final String SQL_CREATE_CALLTIME_TABLE = "CREATE TABLE "
            + ContentProviderConstants.CallTimeEntry.TABLE_NAME + " (" + ContentProviderConstants.CallTimeEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.CallTimeEntry.COLUMN_NAME_CALLTIME + TEXT_TYPE + " )";

    //영상녹화시간 테이블 생성
    private static final String SQL_CREATE_MOVIE_RECORD_TIME_TABLE = "CREATE TABLE "
            + ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME + " (" + ContentProviderConstants.MovieRecordTimeEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.MovieRecordTimeEntry.COLUMN_NAME_MOVIE_RECORDTIME + TEXT_TYPE + " )";

    //영상자동저장 테이블 생성
    private static final String SQL_CREATE_MOVIE_AUTO_SAVE_TABLE = "CREATE TABLE "
            + ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME + " (" + ContentProviderConstants.MovieAutoSaveEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.MovieAutoSaveEntry.COLUMN_NAME_MOVIE_AUTO_SAVE + TEXT_TYPE + " )";

    //스마트폰 통화수신 테이블 생성
    private static final String SQL_CREATE_SMARTPHONE_CALL_TABLE = "CREATE TABLE "
            + ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME + " (" + ContentProviderConstants.SmartPhoneCallEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.SmartPhoneCallEntry.COLUMN_NAME_SMARTPHONE_CALL + TEXT_TYPE + " )";

    //연속통화시간 테이블 삭제
    private static final String SQL_DELETE_CALLTIME_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.CallTimeEntry.TABLE_NAME;

    //영상녹화시간 테이블 삭제
    private static final String SQL_DELETE_MOVIE_RECORD_TIME_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.MovieRecordTimeEntry.TABLE_NAME;

    //영상자동저장 테이블 삭제
    private static final String SQL_DELETE_MOVIE_AUTO_SAVE_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.MovieAutoSaveEntry.TABLE_NAME;

    //스마트폰 통화수신 테이블 삭제
    private static final String SQL_DELETE_SMARTPHONE_CALL_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.SmartPhoneCallEntry.TABLE_NAME;


    //스크린세이버 테이블 생성
    private static final String SQL_CREATE_SCREENSAVER_TABLE = "CREATE TABLE "
            + ContentProviderConstants.ScreenSaverEntry.TABLE_NAME + " (" + ContentProviderConstants.ScreenSaverEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            + ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_INTERVAL_TIME + TEXT_TYPE + COMMA_SEP
            + ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_FINISH_TIME + TEXT_TYPE + " )";

    //스크린세이버 테이블 삭제
    private static final String SQL_DELETE_SCREENSAVER_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.ScreenSaverEntry.TABLE_NAME;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DONGHO_TABLE);
        db.execSQL(SQL_CREATE_DEVICE_TABLE);
        db.execSQL(SQL_CREATE_SCREENSAVER_TABLE);
        db.execSQL(SQL_CREATE_IP_DOOR_CAMERA_TABLE);
        db.execSQL(SQL_CREATE_CALLTIME_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_RECORD_TIME_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_AUTO_SAVE_TABLE);
        db.execSQL(SQL_CREATE_SMARTPHONE_CALL_TABLE);
        db.execSQL(SQL_CREATE_ONVIF_DOOR_CAMERA_TABLE);
        db.execSQL(SQL_CREATE_ONVIF_CCTV_TABLE);
        db.execSQL(SQL_CREATE_USE_VALUE_INFO_TABLE);
        db.execSQL(SQL_CREATE_USE_VALUE_TIP_TABLE);

        //DB 초기화
        initDBTableInfo(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블 삭제
        db.execSQL(SQL_DELETE_DONGHO_TABLE);
        db.execSQL(SQL_DELETE_DEVICE_TABLE);
        db.execSQL(SQL_DELETE_SCREENSAVER_TABLE);
        db.execSQL(SQL_DELETE_IP_DOOR_CAMERA_TABLE);
        db.execSQL(SQL_DELETE_CALLTIME_TABLE);
        db.execSQL(SQL_DELETE_MOVIE_RECORD_TIME_TABLE);
        db.execSQL(SQL_DELETE_MOVIE_AUTO_SAVE_TABLE);
        db.execSQL(SQL_DELETE_SMARTPHONE_CALL_TABLE);
        db.execSQL(SQL_DELETE_ONVIF_CCTV_TABLE);
        db.execSQL(SQL_DELETE_ONVIF_DOOR_CAMERA_TABLE);
        db.execSQL(SQL_DELETE_USE_VALUE_INFO_TABLE);
        db.execSQL(SQL_DELETE_USE_VALUE_TIP_TABLE);

        // 새로 DB 생성
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * New DB 디폴트값(초기 생성시)
     */
    private void initDBTableInfo(SQLiteDatabase db) {
        Log.d(LOG_TAG,"create DBTable...");

        //SQL 쿼리를 사용해 초기값 입력
        for (int i = 0; i < new_dbData.length; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ");
            builder.append(new_dbData[i][INDEX_DB_TABLE]);//table
            builder.append(" (");
            builder.append(new_dbData[i][INDEX_DB_FIELD]);//field
            builder.append(")VALUES");
            builder.append("(\"");
            builder.append(new_dbData[i][INDEX_DB_VALUE]); //value
            builder.append("\");");
            db.execSQL(builder.toString());
        }
    }

}