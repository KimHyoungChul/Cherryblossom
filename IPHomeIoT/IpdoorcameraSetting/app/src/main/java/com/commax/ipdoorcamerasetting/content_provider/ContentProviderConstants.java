package com.commax.ipdoorcamerasetting.content_provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Content Provider 상수
 *
 * @author Jeonggyu Park
 */
public class ContentProviderConstants {

    public static final String CONTENT_AUTHORITY = "com.commax.ipdoorcamerasetting.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * 동호
     */
    public static class DonghoEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "DonghoTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_DONG = "dong";
        public static final String COLUMN_NAME_HO = "ho";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * 디바이스
     */
    public static class DeviceEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "DeviceTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_MODEL_NAME = "modelName";
        public static final String COLUMN_NAME_MAC = "macadress";
        public static final String COLUMN_NAME_USN = "usn";
        public static final String COLUMN_NAME_IPV4 = "ipv4";
        public static final String COLUMN_NAME_IPV4_GATEWAY = "gateway";
        public static final String COLUMN_NAME_IPV4_SUBNET = "subnet";
        public static final String COLUMN_NAME_DNS = "dns";
        public static final String COLUMN_NAME_WEBPORT = "webport";
        public static final String COLUMN_NAME_RTSPPORT = "rtspport";
        public static final String COLUMN_NAME_FIRST_STREAM_URL = "firstStreamUrl";
        public static final String COLUMN_NAME_SECOND_STREAM_URL = "secondStreamUrl";


        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildFlavorsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}




  

