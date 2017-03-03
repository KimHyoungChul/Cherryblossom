package com.commax.pairing.content_provider;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

/**
 * Content Provider 관리
 * Created by bagjeong-gyu on 2016. 9. 26..
 */

public class ContentProviderManager {

    private static final String SET_TRUE = "true";
    private static final String SET_FALSE = "false";

    /**
     * 이전 My Device 데이터 삭제
     * @param context
     */
    public static void deletePreviousMyDeviceInfo(Context context) {
        int cursor =
                context.getContentResolver().delete(ContentProviderConstants.MyDeviceEntry.CONTENT_URI,
                        null, null);
    }

    /**
     * 이전 Configure 데이터 삭제
     * @param context
     */
    public static void deletePreviousConfigure(Context context) {
        int cursor =
                context.getContentResolver().delete(ContentProviderConstants.ConfigureEntry.CONTENT_URI,
                        null, null);    }

    /**
     * 이전 하위 Device 데이터 삭제
     * @param context
     */
    public static void deletePreviousSubDeviceInfo(Context context) {
        int cursor =
                context.getContentResolver().delete(ContentProviderConstants.DeviceEntry.CONTENT_URI,
                        null, null);
    }

    /**
     * My Device 데이터 저장
     * @param context
     * @param contentValues
     */
    public static void saveMyDeviceInfo(Context context, ContentValues contentValues) {



        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.MyDeviceEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * 하위 Device 데이터 저장
     * @param context
     * @param contentValuesArray
     */
    public static void saveSubDeviceInfo(Context context,ContentValues[] contentValuesArray) {



        int cursor =
                context.getContentResolver().bulkInsert(ContentProviderConstants.DeviceEntry.CONTENT_URI,
                        contentValuesArray);

    }

    /**
     * Configure 데이터 저장
     * @param context
     * @param isConfigured
     */
    public static void saveConfigure(Context context,boolean isConfigured) {

        String isConfiguredString = isConfigured? SET_TRUE: SET_FALSE;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.ConfigureEntry.COLUMN_NAME_IS_CONFIGURED, isConfiguredString);


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.ConfigureEntry.CONTENT_URI,
                        contentValues);


    }

}
