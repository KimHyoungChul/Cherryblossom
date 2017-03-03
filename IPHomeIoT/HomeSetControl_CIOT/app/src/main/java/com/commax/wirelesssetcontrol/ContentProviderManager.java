package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ContentProviderManager {


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

            deviceInfo = new OnvifDevice();
            deviceInfo.setIpAddress(ip);
            deviceInfo.setId(id);
            deviceInfo.setPassword(password);
            deviceInfo.setAlias(deviceName);
            deviceInfo.setStreamUrl(streamUrl);

            deviceInfos.add(deviceInfo);

        }

        cursor.close();

        return deviceInfos;
    }


}
