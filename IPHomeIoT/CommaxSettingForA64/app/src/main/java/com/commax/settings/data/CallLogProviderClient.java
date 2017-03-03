package com.commax.settings.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.util.ArrayList;

public class CallLogProviderClient {
    public int getMissedCallCount(Context context) {
        Cursor cursor =  context.getContentResolver().query(CallLogProviderDefine.CONTENT_URI,
                                                            new String[]{CallLog.Calls.TYPE},
                                                            CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.NEW + " = ?",
                                                            new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE), "1"},
                                                            CallLog.Calls.DATE + " DESC " );
        if (cursor == null)
            return 0;

        int ret = cursor.getCount();
        cursor.close();

        return ret;
    }

    public CallLogInfo getCallLogInfo(Context context, String filePath) {
        Cursor cursor = context.getContentResolver().query(CallLogProviderDefine.CONTENT_URI,
                                                           new String[]{CallDBScheme.COLUMN_NAME_TYPE, CallDBScheme.COLUMN_NAME_OTHERPARTY},
                                                           CallDBScheme.COLUMN_NAME_RECORDFILEPATH + "=?",
                                                           new String[]{filePath}, "");
        if (cursor == null)
            return null;
        if (!cursor.moveToFirst())
            return null;

        CallLogInfo callInfo = new CallLogInfo();
        callInfo.callType_ = cursor.getInt(0);
        callInfo.otherParty_ = cursor.getString(1);
        cursor.close();

        return callInfo;
    }

    public int getLogCount(Context context) {
        Cursor cursor = context.getContentResolver().query(CallLogProviderDefine.CONTENT_URI, new String[]{CallDBScheme.COLUMN_NAME_TYPE}, "", null, "");
        if (cursor == null)
            return 0;

        int ret = cursor.getCount();
        cursor.close();

        return ret;
    }

    public ArrayList<CallLogInfo> getAllLog(Context context) {
        String[] queryColumn = {CallDBScheme.COLUMN_NAME_ID, CallDBScheme.COLUMN_NAME_TYPE, CallDBScheme.COLUMN_NAME_OTHERPARTY, CallDBScheme.COLUMN_NAME_DATE, CallDBScheme.COLUMN_NAME_RECORDFILEPATH};
        Cursor cursor = context.getContentResolver().query(CallLogProviderDefine.CONTENT_URI, queryColumn, "", null, CallDBScheme.COLUMN_NAME_DATE + " DESC ");
        if (cursor == null)
            return  null;

        ArrayList<CallLogInfo> ret = new ArrayList<>();
        CallLogInfo logInfo;
        while (cursor.moveToNext()) {
            logInfo = new CallLogInfo();
            logInfo.key_ = cursor.getInt(0);
            logInfo.callType_ = cursor.getInt(1);
            logInfo.otherParty_ = cursor.getString(2);
            logInfo.callDate_ = cursor.getString(3);
            logInfo.recordFilePath_ = cursor.getString(4);
            ret.add(logInfo);
        }
        cursor.close();

        return ret;
    }

    public void removeMissedcallNewValue(Context context) {
        ContentValues updateValues = new ContentValues();
        updateValues.put(CallDBScheme.COLUMN_NAME_NEW, 0);
        context.getContentResolver().update(CallLogProviderDefine.CONTENT_URI, updateValues, null, null);
    }

    public void deleteLog(int key, Context context) {
        context.getContentResolver().delete(CallLogProviderDefine.CONTENT_URI, CallDBScheme.COLUMN_NAME_ID + "=?", new String[]{Integer.toString(key)});
    }

    public void deleteLogAll(Context context) {
        context.getContentResolver().delete(CallLogProviderDefine.CONTENT_URI, null, null);
    }
}
