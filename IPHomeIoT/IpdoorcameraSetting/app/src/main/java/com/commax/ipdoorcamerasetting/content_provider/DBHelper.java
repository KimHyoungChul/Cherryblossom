package com.commax.ipdoorcamerasetting.content_provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 디비 관리
 *
 * @author Jeonggyu Park
 */
class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "IpdoorcameraSetting.db";
    //데이터베이스 버전을 높여야 재설치했을 때 새로운 테이블과 칼럼등이 생성됨
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    //동호 테이블 생성
    private static final String SQL_CREATE_DONGHO_TABLE = "CREATE TABLE "
            + ContentProviderConstants.DonghoEntry.TABLE_NAME + " (" +  ContentProviderConstants.DonghoEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            +  ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO + TEXT_TYPE + " )";

    //동호 테이블 삭제
    private static final String SQL_DELETE_DONGHO_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.DonghoEntry.TABLE_NAME;

    //디바이스 테이블 생성
    private static final String SQL_CREATE_DEVICE_TABLE = "CREATE TABLE "
            + ContentProviderConstants.DeviceEntry.TABLE_NAME + " (" +  ContentProviderConstants.DeviceEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_MODEL_NAME + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_MAC + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_USN + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_IPV4 + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_IPV4_GATEWAY + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_IPV4_SUBNET + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_DNS + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_WEBPORT + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_RTSPPORT + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_FIRST_STREAM_URL + TEXT_TYPE + COMMA_SEP
            +  ContentProviderConstants.DeviceEntry.COLUMN_NAME_SECOND_STREAM_URL + TEXT_TYPE + " )";

    //디바이스 테이블 삭제
    private static final String SQL_DELETE_DEVICE_TABLE = "DROP TABLE IF EXISTS "
            + ContentProviderConstants.DeviceEntry.TABLE_NAME;



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_DONGHO_TABLE);
        db.execSQL(SQL_CREATE_DEVICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블 삭제
        db.execSQL(SQL_DELETE_DONGHO_TABLE);
        db.execSQL(SQL_DELETE_DEVICE_TABLE);
        // 새로 DB 생성
        onCreate(db);

    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}