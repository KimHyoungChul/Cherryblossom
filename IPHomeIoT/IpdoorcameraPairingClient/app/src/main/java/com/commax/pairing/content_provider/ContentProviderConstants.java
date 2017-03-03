package com.commax.pairing.content_provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 디비 관련 상수(테이블명, 칼럼명 등)
 *
 * @author Jeonggyu Park
 */
public class ContentProviderConstants {

    public static final String CONTENT_AUTHORITY = "com.ipdevicemanager.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Configure 관련
     */
    public static class ConfigureEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "configureTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_IS_CONFIGURED = "isConfigured";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Device 관련
     */
    public static class DeviceEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "deviceTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_DEVICE_TYPE = "deviceType";
        public static final String COLUMN_NAME_IP_ADDRESS = "ipAddress";
        public static final String COLUMN_NAME_SIP_PHONE_NO = "sipPhoneNo";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * My Device 관련
     */
    public static class MyDeviceEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "myDeviceTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_DEVICE_TYPE = "deviceType";
        public static final String COLUMN_NAME_IP_ADDRESS = "ipAddress";
        public static final String COLUMN_NAME_SIP_PHONE_NO = "sipPhoneNo";

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }




  
}
