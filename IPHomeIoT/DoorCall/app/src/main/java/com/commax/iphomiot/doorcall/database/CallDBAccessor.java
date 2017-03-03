package com.commax.iphomiot.doorcall.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;

import com.commax.iphomeiot.common.db.CallDBScheme;

public class CallDBAccessor extends SQLiteOpenHelper {
    public CallDBAccessor(Context context) {
        super(context, "call_db.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CallDBScheme.STATEMENT_CREATE_TABLE);
        db.execSQL(CallDBScheme.SoundDB.STATEMENT_CREATE_TABLE);
        db.execSQL(String.format("insert into %s (%s, %s) values (%d, -1)", CallDBScheme.SoundDB.TABLENAME, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDKEY, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDVALUE,
                CallDBScheme.SoundDB.SoundType.BELL_SOUND));
        db.execSQL(String.format("insert into %s (%s, %s) values (%d, -1)", CallDBScheme.SoundDB.TABLENAME, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDKEY, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDVALUE,
                CallDBScheme.SoundDB.SoundType.INCOMING_SOUND));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CallDBScheme.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + CallDBScheme.SoundDB.TABLENAME);
        onCreate(db);
    }
}
