package com.commax.pairing.configure;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;


import com.commax.pairing.DeviceManagerConstants;
import com.commax.pairing.content_provider.ContentProviderConstants;
import com.commax.pairing.content_provider.ContentProviderManager;
import com.commax.pairing.ip_device_setting.IPDeviceManager;
import com.commax.pairing.udp.CommaxProtocolConstants;
import com.commax.pairing.udp.UDPReceiveDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bagjeong-gyu on 2016. 9. 26..
 */
public class BootCompleteReceiver extends BroadcastReceiver implements UDPReceiveDataListener {


    private Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {
        //액티비티 실행 테스트
//        Intent i = new Intent(context, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);



        mContext = context;

        if (isSlaveWallpad()) {
            checkConfigure();
        }

    }



    private boolean isSlaveWallpad() {


        return DeviceManagerConstants.MODE == DeviceManagerConstants.MODE_SLAVE;

        //원래 코드
//        //자기 ip addr.가 xx.xx.xx.11 인 경우에는 Master W/P로 인식
//        String ipAddress = IPAddressFinder.getIPAddress(true);
//        //"."은 특수문자라서 split이 안됨. "\\."은 예전에는 작동했으나 지금은 안된다고 함
//        String[] tokens = ipAddress.split(Pattern.quote("."));
//
//        return !tokens[3].equals("11");
    }

    /**
     * 자신의 디바이스가 Configure되었는지 체크
     *
     */
    public void checkConfigure() {
        Cursor cursor =
                mContext.getContentResolver().query(ContentProviderConstants.ConfigureEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            String isConfigured = cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.ConfigureEntry.COLUMN_NAME_IS_CONFIGURED));

            Toast.makeText(mContext, "Configure 여부: " + isConfigured, Toast.LENGTH_SHORT).show();
            if (isConfigured.equals("false")) {
                Toast.makeText(mContext, "sendConfigureBroadcastToMasterWallpad", Toast.LENGTH_SHORT).show();
                sendConfigureBroadcastToMasterWallpad();
            }
        } else {
            Toast.makeText(mContext, "sendConfigureBroadcastToMasterWallpad", Toast.LENGTH_SHORT).show();
            sendConfigureBroadcastToMasterWallpad();
        }



    }

    private void sendConfigureBroadcastToMasterWallpad() {

        JSONObject deviceInfoJson = new JSONObject();

        try {
            deviceInfoJson.put(CommaxProtocolConstants.KEY_ACTION, CommaxProtocolConstants.ACTION_CHECK_CONFIGURE);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        IPDeviceManager ipDeviceManager = new IPDeviceManager(mContext, this);
        ipDeviceManager.sendUdpBroadcast(deviceInfoJson.toString());
    }

    @Override
    public void onReceive(String data) {
        handleBroadcast(data);
    }

    /**
     * 브로드캐스트 처리
     *
     * @param data
     */
    private void handleBroadcast(String data) {
        JSONObject dataJson = null;

        String action = null;


        try {
            dataJson = new JSONObject(data);
            action = dataJson.optString(CommaxProtocolConstants.KEY_ACTION);
            if (action == null || action.equals("")) {
                return;
            }

            //하위 Device 정보 브로드캐스트를 받은 경우
            if (action.equals(CommaxProtocolConstants.ACTION_SUB_DEVICE_INFO)) {


                saveSubDeviceInfo(dataJson);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 하위 Device 정보 저장
     *
     * @param dataJson
     */
    private void saveSubDeviceInfo(JSONObject dataJson) {
        deletePreviousSubDeviceInfo();
        deletePreviousConfigure();
        saveConfigure(true);

        saveSubDeviceInfoToContentProvider(dataJson);
    }

    /**
     * 하위 Device 정보를 Content Provider에 저장
     *
     * @param dataJson
     */
    private void saveSubDeviceInfoToContentProvider(JSONObject dataJson) {


        try {
            JSONArray devicesJsonArray = dataJson.getJSONArray(CommaxProtocolConstants.KEY_DEVICES);


            int deviceSize = devicesJsonArray.length();
            JSONObject deviceJsonObject = null;
            ContentValues[] contentValuesArray = new ContentValues[deviceSize];
            ContentValues contentValues = null;

            for (int i = 0; i < deviceSize; i++) {

                deviceJsonObject = (JSONObject) devicesJsonArray.get(i);

                contentValues = new ContentValues();
                contentValues.put(ContentProviderConstants.DeviceEntry.COLUMN_NAME_DEVICE_TYPE, deviceJsonObject.optString(CommaxProtocolConstants.KEY_DEVICE_NAME));
                contentValues.put(ContentProviderConstants.DeviceEntry.COLUMN_NAME_IP_ADDRESS, deviceJsonObject.optString(CommaxProtocolConstants.KEY_NEW_IP));
                contentValues.put(ContentProviderConstants.DeviceEntry.COLUMN_NAME_SIP_PHONE_NO, deviceJsonObject.optString(CommaxProtocolConstants.KEY_SIP_PHONE_NO));

                contentValuesArray[i] = contentValues;

            }
            ContentProviderManager.saveSubDeviceInfo(mContext, contentValuesArray);


        } catch (JSONException e) {
            e.printStackTrace();

        }


    }

    /**
     * 이전 Configure 삭제
     */
    private void deletePreviousConfigure() {
        ContentProviderManager.deletePreviousConfigure(mContext);
    }

    /**
     * 이전 Device 정보 삭제
     */
    private void deletePreviousSubDeviceInfo() {
        ContentProviderManager.deletePreviousSubDeviceInfo(mContext);
    }

    /**
     * Configure 정보 저장
     *
     * @param isConfigured
     */
    public void saveConfigure(boolean isConfigured) {


        ContentProviderManager.saveConfigure(mContext, isConfigured);


    }


}
