package com.commax.provider.settings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingsDBHelper extends SQLiteOpenHelper {
    public static String TABLE_NAME = "settings";
    public static String COL_ID = "_id";
    public static String COL_PROPERTY = "property";

    //단지 서버가 없는 경우 디폴트값 127.0.0.1
    //단지 서버가 있는 경우 해당 단지 서버값으로 변경
    //ciot used : 비밀번호,동/호,서버IP,벨소리
    private String dbData[][] = {{"local_server_ip", "127.0.0.1"}, //1
            {"sip_server_ip", "10.0.0.2"},
            {"update_server_ip", "10.0.0.2"},
            {"cctv_server_ip", "10.0.0.2"},
            {"device_server_ip", "127.0.0.1"},
            {"home_server_ip", "127.0.0.1"},   //ciot used
            {"guard_phone_num", "7000010050"},
            {"mng_office_phone_num", "7000010050"},
            {"residence_num", "1234-5678"}, //ciot used
            {"system_id", "100"},
            {"my_phone_num", "501"},
            {"password", "1234"},  //ciot used
            {"door_ringtone", "call_door1.mp3"},  //ciot used
            {"guard_ringtone", "call_tel1.mp3"},  //ciot used
            {"public_ringtone", "call_door1.mp3"},  //ciot used
            {"extension_ringtone", "call_tel1.mp3"},  //ciot used
            {"pstn_ringtone", "call_tel1.mp3"},  //ciot used
            {"time_auto", "1"},
            {"time_server", "10.0.0.2"},
            {"sync_interval", "720"},
            {"bypass_call_enabled", "0"},
            {"bypass_call_time", "10"},
            {"energy_server_ip", "10.0.0.2"}};

    public SettingsDBHelper(Context context, String name,
                            CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {

        String sqlCreate = "create table " + TABLE_NAME + " (" + COL_ID
                + " INTEGER PRIMARY KEY, " + COL_PROPERTY
                + " TEXT UNIQUE NOT NULL, value TEXT); ";
        arg0.execSQL(sqlCreate);

        for (int i = 0; i < dbData.length; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ");
            builder.append(TABLE_NAME);
            builder.append(" VALUES((select max(");
            builder.append(COL_ID);
            builder.append(") from ");
            builder.append(TABLE_NAME);
            builder.append(")+1, \"");
            builder.append(dbData[i][0]);
            builder.append("\", \"");
            builder.append(dbData[i][1]);
            builder.append("\");");
            arg0.execSQL(builder.toString());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

        arg0.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(arg0);
    }

}
