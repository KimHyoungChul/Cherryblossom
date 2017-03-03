package com.commax.settings.content_provider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * Content Provider 관리
 * Created by bagjeong-gyu on 2016. 9. 26..
 */

public class ContentProviderManager {

    /**
     * 이전 가치정보 사용여부 데이터 삭제
     *
     * @param context
     */
    public static void deletePreviousUseValueInfo(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.UseValueInfoEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 가치정보 사용여부 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveUseValueInfo(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.UseValueInfoEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * 가치정보 사용여부 값 가져옴
     */
    public static String getUseValueInfo(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.UseValueInfoEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String useValueInfo = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();


            useValueInfo = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.UseValueInfoEntry.COLUMN_NAME_USE_VALUE_INFO));

        }

        return useValueInfo;


    }


    /**
     * OnVif 도어폰 카메라 모든 데이터 삭제
     *
     * @param context
     */
    public static void deleteAllOnvifDoorCamera(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * OnVif 도어폰 카메라 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveOnvifDoorCamera(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * OnVif 도어폰 카메라 이름 update
     *
     * @param context
     * @param contentValues
     */
    public static void updateOnvifDoorCameraName(Context context, ContentValues contentValues, String ip) {


        int result =
                context.getContentResolver().update(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        contentValues, ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IP + "=?", new String[] {ip});


    }

    /**
     * Onvif 도어폰 카메라가 Content Provider에 존재하는지 체크
     *
     * @param context
     * @return
     */
    public static boolean isOnvifDoorCameraIpExistOnContentProvider(final Context context, String ip) {
        String[] args = {ip};
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        null,
                        ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IP + "=?",
                        args,
                        null);


        if(cursor == null) {
            return false;
        }


        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }


    /**
     * 모든 OnvifDevice 도어폰 카메라 항목 가져옴
     *
     * @param context
     * @return
     */
    public static List<OnvifDevice> getAllOnvifDoorCamera(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);


        List<OnvifDevice> deviceInfos = new ArrayList<>();

        if(cursor == null) {
            return deviceInfos;
        }



        OnvifDevice deviceInfo = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String ip = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IP));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_ID));
            String password = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_PASSWORD));
            String deviceName = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_DEVICE_NAME));

            String streamUrl = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_STREAM_URL));

            String sipNo = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_SIP_NO));

            String isOk = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IS_OK));

            deviceInfo = new OnvifDevice();
            deviceInfo.setIpAddress(ip);
            deviceInfo.setId(id);
            deviceInfo.setPassword(password);
            deviceInfo.setName(deviceName);
            deviceInfo.setStreamUrl(streamUrl);
            deviceInfo.setSipPhoneNo(sipNo);
            deviceInfo.setIsOk(isOk);

            deviceInfos.add(deviceInfo);

        }

        cursor.close();

        return deviceInfos;
    }


    /**
     * OnVif CCTV 모든 데이터 삭제
     *
     * @param context
     */
    public static void deleteAllOnvifCctv(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * OnVif CCTV 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveOnvifCctv(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * onvif CCTV가 Content Provider에 존재하는지 체크
     *
     * @param context
     * @return
     */
    public static boolean isOnvifCctvIpExistOnContentProvider(final Context context, String ip) {
        String[] args = {ip};
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        null,
                        ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP + "=?",
                        args,
                        null);

        if(cursor == null) {
            return false;
        }


        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }


    /**
     * 모든 Onvif CCTV 항목 가져옴
     *
     * @param context
     * @return
     */
    public static List<OnvifDevice> getAllOnvifCctv(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        List<OnvifDevice> deviceInfos = new ArrayList<>();

        if(cursor == null) {
            return deviceInfos;
        }

        OnvifDevice deviceInfo = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String ip = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_ID));
            String password = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_PASSWORD));
            String deviceName = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_DEVICE_NAME));

            String streamUrl = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_STREAM_URL));

            deviceInfo = new OnvifDevice();
            deviceInfo.setIpAddress(ip);
            deviceInfo.setId(id);
            deviceInfo.setPassword(password);
            deviceInfo.setName(deviceName);
            deviceInfo.setStreamUrl(streamUrl);

            deviceInfos.add(deviceInfo);

        }

        cursor.close();

        return deviceInfos;
    }


    /**
     * 이전 월패드 비밀번호 데이터 삭제
     *
     * @param context
     */
    public static void deletePreviousWallpadPassword(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.WallpadPasswordEntry.CONTENT_URI,
                        null, null);
    }

    /**
     * 디바이스 IP가 Content Provider에 존재하는지 체크
     *
     * @param context
     * @return
     */
    public static boolean isIpExistOnContentProvider(final Context context, String ip) {
        String[] args = {ip};
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.DoorCameraEntry.CONTENT_URI,
                        null,
                        ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4 + "=?",
                        args,
                        null);

        if (cursor == null) {
            return false;
        }

        if (cursor.getCount() > 0) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Toast.makeText(context, R.string.saved_setting_already, Toast.LENGTH_SHORT).show();
                }
            });

            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }


    /**
     * 월패드 비밀번호 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveWallpadPassword(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.WallpadPasswordEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 월패드 비밀번호 가져옴
     */
    public static String getWallpadPassword(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.WallpadPasswordEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String password = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            password = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG));
        }

        return password;


    }


    /**
     * 이전 동호 데이터 삭제
     *
     * @param context
     */
    public static void deletePreviousDongho(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 동호 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveDongho(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 동호 가져옴
     */
    public static Dongho getDongho(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        Dongho dongho = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            dongho = new Dongho();

            dongho.setDong(cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG)));

            dongho.setHo(cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO)));

        }

        return dongho;


    }

    /**
     * 이전 연속통화시간 삭제
     *
     * @param context
     */
    public static void deletePreviousCallTime(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.CallTimeEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 연속통화시간 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveCallTime(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.CallTimeEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 연속통화시간 가져옴
     */
    public static String getCallTime(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.CallTimeEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String callTime = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();


            callTime = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.CallTimeEntry.COLUMN_NAME_CALLTIME));

        }

        return callTime;


    }


    /**
     * 이전 영상녹화시간 삭제
     *
     * @param context
     */
    public static void deletePreviousMovieRecordTime(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.MovieRecordTimeEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 영상녹화시간 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveMovieRecordTime(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.MovieRecordTimeEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 영상녹화시간 가져옴
     */
    public static String getMovieRecordTime(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.MovieRecordTimeEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String movieRecordTime = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();


            movieRecordTime = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.MovieRecordTimeEntry.COLUMN_NAME_MOVIE_RECORDTIME));

        }

        return movieRecordTime;


    }


    /**
     * 이전 영상자동저장 삭제
     *
     * @param context
     */
    public static void deletePreviousMovieAutoSave(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.MovieAutoSaveEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 영상자동저장 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveMovieAutoSave(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.MovieAutoSaveEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 영상자동저장 값 가져옴
     */
    public static String getMovieAutoSave(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.MovieAutoSaveEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String movieAutoSave = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();


            movieAutoSave = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.MovieAutoSaveEntry.COLUMN_NAME_MOVIE_AUTO_SAVE));

        }

        return movieAutoSave;


    }


    /**
     * 이전 스마트폰 통화수신 삭제
     *
     * @param context
     */
    public static void deletePreviousSmartphoneCall(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.SmartPhoneCallEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 스마트폰 통화수신 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveSmartphoneCall(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.SmartPhoneCallEntry.CONTENT_URI,
                        contentValues);


    }


    /**
     * 스마트폰 통화수신 값 가져옴
     */
    public static String getSmartphoneCall(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.SmartPhoneCallEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        String smartphoneCall = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();


            smartphoneCall = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.SmartPhoneCallEntry.COLUMN_NAME_SMARTPHONE_CALL));

        }

        return smartphoneCall;


    }


    /**
     * My Device 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveScreenSaverTime(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.ScreenSaverEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * 스크린세이버에 설정된 시간 타입 가져옴
     */
    public static String getPreviousSelectedMode(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.ScreenSaverEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        //디폴트로 항상 켜짐
        String timeType = ScreensaverModeConstants.MODE_ALWAYS_ON;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            timeType = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE));

        }

        return timeType;


    }

    /**
     * 스크린세이버에 설정된 시간 타입 가져옴
     */
    public static Screensaver getPreviousSelectedScreensaverTime(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.ScreenSaverEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        //디폴트로 항상 켜짐

        Screensaver screensaver = new Screensaver();
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();

            String timeType = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE));
            screensaver.setTimeType(timeType);

            String startTime = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_START_TIME));
            screensaver.setStartTime(startTime);

            String finishTime = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_FINISH_TIME));
            screensaver.setFinishTime(finishTime);

            String intervalTime = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_INTERVAL_TIME));
            screensaver.setIntervalTime(intervalTime);

        }

        return screensaver;

    }

    /**
     * 스크린세이버에 설정된 시간 삭제
     *
     * @param context
     */
    public static void deletePreviousScreenSaverTime(Context context) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.ScreenSaverEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 등록된 Onvif 도어카메라 삭제
     * @param context
     * @param ipAddress
     */
    public static void deleteOnvifDoorCamera(Context context, String ipAddress) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.OnvifDoorCameraDeviceEntry.CONTENT_URI,
                        ContentProviderConstants.OnvifDoorCameraDeviceEntry.COLUMN_NAME_IP + "=?", new String[]{ipAddress});
    }

    /**
     * 등록된 CCTV 이름 update
     * @param context
     * @param contentValues
     * @param ip
     */
    public static void updateOnvifCctvName(Context context, ContentValues contentValues, String ip) {
        int result =
                context.getContentResolver().update(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        contentValues, ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP + "=?", new String[] {ip});
    }

    /**
     * 등록된 CCTV 삭제
     * @param context
     * @param ipAddress
     */
    public static void deleteOnvifCctv(Context context, String ipAddress) {
        int result =
                context.getContentResolver().delete(ContentProviderConstants.OnvifCctvEntry.CONTENT_URI,
                        ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP + "=?", new String[]{ipAddress});
    }
}
