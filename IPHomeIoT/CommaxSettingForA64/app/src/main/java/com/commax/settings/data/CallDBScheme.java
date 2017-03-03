package com.commax.settings.data;

import android.provider.CallLog;

public class CallDBScheme {
    public static class CallType {
        public static final int INCOMING_CALL = CallLog.Calls.INCOMING_TYPE;
        public static final int OUTGOING_CALL = CallLog.Calls.OUTGOING_TYPE;
        public static final int MISEED_CALL = CallLog.Calls.MISSED_TYPE;
    }

    public static final String TABLENAME = "call_log";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_TYPE = CallLog.Calls.TYPE;
    public static final String COLUMN_NAME_OTHERPARTY = "otherparty";
    public static final String COLUMN_NAME_DATE = CallLog.Calls.DATE;
    public static final String COLUMN_NAME_RECORDFILEPATH = "recordfilepath";
    public static final String COLUMN_NAME_NEW = CallLog.Calls.NEW;
    public static final String STATEMENT_CREATE_TABLE =
            "create table " + TABLENAME + "("
                    + COLUMN_NAME_ID + " integer primary key autoincrement, "
                    + COLUMN_NAME_TYPE + " integer not null, "
                    + COLUMN_NAME_OTHERPARTY + " text not null , "
                    + COLUMN_NAME_DATE + " text not null, "
                    + COLUMN_NAME_RECORDFILEPATH + " text, "
                    + COLUMN_NAME_NEW + " integer);";

    public static class SoundDB {
        public static class SoundType{
            public static final int BELL_SOUND = 0;
            public static final int INCOMING_SOUND = 1;
        }

        public static final String TABLENAME = "call_sound";
        public static final String COLUMN_NAME_SOUNDKEY = "soundkey";
        public static final String COLUMN_NAME_SOUNDVALUE = "value";
        public static final String STATEMENT_CREATE_TABLE =
                "create table " + TABLENAME + "("
                + COLUMN_NAME_SOUNDKEY + " integer not null,"
                + COLUMN_NAME_SOUNDVALUE + " integer not null);";
    }
}