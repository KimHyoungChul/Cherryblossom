package com.commax.settings.content_provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Content Provider 관련 상수(테이블명, 칼럼명 등)
 *
 * @author Jeonggyu Park
 */
public class ContentProviderConstants {

    public static final String CONTENT_AUTHORITY = "com.commax.settings.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /**
     * 가치정보 사용여부
     */
    public static class UseValueInfoEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "UseValueInfoTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_USE_VALUE_INFO = "useValueInfo";

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
     * OnVif 도어폰
     */
    public static class OnvifDoorCameraDeviceEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "OnvifDoorCameraTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_IP = "ip";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_DEVICE_NAME = "deviceName";
        public static final String COLUMN_NAME_STREAM_URL = "streamUrl";
        public static final String COLUMN_NAME_SIP_NO = "sipNo";
        public static final String COLUMN_NAME_IS_OK = "isOk";

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
     * IP Door Camera 설정:사용안함
     */
    public static class IPDoorCameraEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "ipDoorCameraTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_MAIN_CODEC = "mainCodec";
        public static final String COLUMN_NAME_MAIN_RESOLUTION = "mainResolution";
        public static final String COLUMN_NAME_MAIN_GOP = "mainGop";
        public static final String COLUMN_NAME_MAIN_FRAMERATE = "mainFramerate";
        public static final String COLUMN_NAME_MAIN_BITRATE = "mainBitrate";
        public static final String COLUMN_NAME_SUB_CODEC = "subCodec";
        public static final String COLUMN_NAME_SUB_RESOLUTION = "subResolution";
        public static final String COLUMN_NAME_SUB_GOP = "subGop";
        public static final String COLUMN_NAME_SUB_FRAMERATE = "subFramerate";
        public static final String COLUMN_NAME_SUB_BITRATE = "subBitrate";

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
     * Custom 도어폰
     */
    public static class DoorCameraEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "DoorCameraTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_MODEL_NAME = "modelName";
        public static final String COLUMN_NAME_MAC = "macadress";
        public static final String COLUMN_NAME_USN = "usn";
        public static final String COLUMN_NAME_IPV4 = "ipv4";
        public static final String COLUMN_NAME_IPV4_GATEWAY = "gateway";
        public static final String COLUMN_NAME_IPV4_SUBNET = "subnet";
        public static final String COLUMN_NAME_SIP_NO = "sipNo";  //2017-01,24,yslee::SIP 번호 항목 추가함
        public static final String COLUMN_NAME_DNS = "dns";
        public static final String COLUMN_NAME_WEBPORT = "webport";
        public static final String COLUMN_NAME_RTSPPORT = "rtspport";
        public static final String COLUMN_NAME_FIRST_STREAM_URL = "firstStreamUrl";
        public static final String COLUMN_NAME_SECOND_STREAM_URL = "secondStreamUrl";
        public static final String COLUMN_NAME_IS_OK = "isOk";  //2017-01,24,yslee::IS_OK 항목 추가함


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
     * OnVif CCTV
     */
    public static class OnvifCctvEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "OnvifCctvTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_IP = "ip";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_DEVICE_NAME = "deviceName";
        public static final String COLUMN_NAME_STREAM_URL = "streamUrl";

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
     * 연속통화시간
     */
    public static class CallTimeEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "CallTimeTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_CALLTIME = "callTime";

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
     * 영상녹화시간
     */
    public static class MovieRecordTimeEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "MovieRecordTimeTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_MOVIE_RECORDTIME = "movieRecordTime";

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
     * 영상자동저장
     */
    public static class MovieAutoSaveEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "MovieAutoSaveTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_MOVIE_AUTO_SAVE = "movieAutoSave";

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
     * 스마트폰 통화수신
     */
    public static class SmartPhoneCallEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "SmartPhoneCallTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_SMARTPHONE_CALL = "smartphoneCall";

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
     * 월패드 비밀번호
     */
    public static class WallpadPasswordEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "WallpadPasswordTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_PASSWORD = "password";

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
     * 스크린세이버 시간 설정
     */
    public static class ScreenSaverEntry implements BaseColumns {
        //같은 테이블 이름이 있으면 오류 발생
        public static final String TABLE_NAME = "screenSaverTable";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_TIME_TYPE = "timeType";
        public static final String COLUMN_NAME_START_TIME = "startTime";
        public static final String COLUMN_NAME_FINISH_TIME = "finishTime";
        public static final String COLUMN_NAME_INTERVAL_TIME = "intervalTime";

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




  

