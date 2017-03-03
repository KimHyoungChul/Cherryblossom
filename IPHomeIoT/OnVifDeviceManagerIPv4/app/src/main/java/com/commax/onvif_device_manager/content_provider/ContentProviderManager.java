package com.commax.onvif_device_manager.content_provider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.commax.onvif_device_manager.device.DeviceInfo;
import com.commax.onvif_device_manager.device.OnvifDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Content Provider 관리
 * Created by bagjeong-gyu on 2016. 9. 26..
 */

public class ContentProviderManager {


    /**
     * OnVif 디바이스 모든 데이터 삭제
     *
     * @param context
     */
    public static void deleteAllOnvifDevice(Context context) {
        int cursor =
                context.getContentResolver().delete(ContentProviderConstants.OnvifDeviceEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * OnVif 디바이스 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveOnvifDevice(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.OnvifDeviceEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * 디바이스가 Content Provider에 존재하는지 체크
     * @param context
     * @return
     */
    public static boolean isIpExistOnContentProvider(final Context context, String ip) {
        String[] args = { ip };
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifDeviceEntry.CONTENT_URI,
                        null,
                        ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_IP + "=?",
                        args,
                        null);

        if(cursor.getCount() >0) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "이미 Content Provider에 저장되었습니다", Toast.LENGTH_SHORT).show();
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
     * 모든 OnvifDevice 항목 가져옴
     * @param context
     * @return
     */
    public static List<OnvifDevice> getAllOnvifDevice(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.OnvifDeviceEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        List<OnvifDevice> deviceInfos = new ArrayList<>();

        OnvifDevice deviceInfo = null;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            String ip = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_IP));
            String id = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_ID));
            String password = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_PASSWORD));
            String deviceName = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_DEVICE_NAME));

            String macaddress = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_MACADDRESS));

            deviceInfo = new OnvifDevice();
            deviceInfo.setIpAddress(ip);
            deviceInfo.setId(id);
            deviceInfo.setPassword(password);
            deviceInfo.setAlias(deviceName);
            deviceInfo.setMac(deviceName);

            deviceInfos.add(deviceInfo);

        }

        cursor.close();

        return deviceInfos;
    }



}
