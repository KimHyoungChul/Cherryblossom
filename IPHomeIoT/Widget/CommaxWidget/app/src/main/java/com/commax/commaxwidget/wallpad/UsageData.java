package com.commax.commaxwidget.wallpad;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.commax.commaxwidget.wallpad.constans.CommaxDevice;
import com.commax.commaxwidget.wallpad.constans.NameSpace;
import com.commax.commaxwidget.wallpad.constans.RootDevice;
import com.commax.commaxwidget.wallpad.constans.Status;
import com.commax.commaxwidget.wallpad.data.CarItem;
import com.commax.commaxwidget.wallpad.data.DeviceInfo;
import com.commax.commaxwidget.wallpad.data.Items;
import com.commax.commaxwidget.wallpad.data.NoticeItem;
import com.commax.commaxwidget.wallpad.data.SubDevice;
import com.commax.commaxwidget.wallpad.data.SubSort;
import com.commax.commaxwidget.wallpad.utils.CalendarUtils;
import com.commax.commaxwidget.wallpad.utils.SoapHelper;
import com.commax.pam.db.interfaces.MySQLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public class UsageData {

    static final String DEBUG_TAG = "UsageData";
    int TIMEOUT_VALUE = 10000;   // 5초

    final String SYMBOL_DOLLAR = "$";
    final String SYMBOL_SHARP = "#";

    String CLOUD_SERVER = "220.120.109.88";
    String LOCAL_PORT = "";       //Cloud server can change(/user/app/bin/cloud_svr.i)

    private String serverip;
    private String dong;
    private String ho;
    private String mac = "1234";
    private String sitecode = "1234";

    Context mContext;
    Locale locale;

    public UsageData(Context context, String serverip, String dong, String ho) {

        if (!(serverip.length() > 0)) {
            return;
        }
        if (!(dong.length() > 0)) {
            return;
        }
        if (!(ho.length() > 0)) {
            return;
        }

        this.serverip = serverip;
        this.dong = dong;
        this.ho = ho;

        mContext = context;

        try {
            locale = mContext.getResources().getConfiguration().locale;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            readCloudDNS();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void open() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MySQLConnection.getInstance().getConnection(mContext.getContentResolver());
                    Log.d(DEBUG_TAG, "my sql open");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean close() {
        boolean close_ok = false;
        Log.d(DEBUG_TAG, "try to close");
        try {
            close_ok = MySQLConnection.getInstance().close();  //MySQLConnection is MySQL
            MySQLConnection.getInstance().clearConnectionVariable();
        } catch (Exception e) {
            e.printStackTrace();
            MySQLConnection.getInstance().clearConnectionVariable();
        }
        return close_ok;
    }

    public boolean isConnected() {
        try {
            if (MySQLConnection.getInstance().getConnectionVariable() != null) return true;
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkValidConnection() {

        try {
//            ArrayList<Integer> result = MySQLConnection.getInstance().getRootDeviceRootUuid("WallPad");
//            Log.d(TAG, "checkValidConnection result "+result.get(0));

            int count = 0;
            count = getRootUuidCount();
//            Log.d(TAG, "checkValidConnection : count" + count);

            if (count > 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getRootUuidCount() {
        int count = 0;
        String query = "Select COUNT(rootUuid) FROM rootDevice";

        try {
            PreparedStatement prep = MySQLConnection.getInstance().getConnection(mContext.getContentResolver()).prepareStatement(query);
            ResultSet e = prep.executeQuery();
            ResultSetMetaData rsmd = null;
            rsmd = e.getMetaData();

            int rowCnt1 = rsmd.getColumnCount();

            if (rowCnt1 != 0) {
                if (e.next()) {
                    count = e.getInt(1);
                }
            } else {
                Log.d(DEBUG_TAG, "getRootUuidCount e.getInt(1) = 0");
            }
            e.close();
            prep.close();
        } catch (SQLException var8) {
            var8.getMessage();
        }

        return count;
    }

    public ArrayList<DeviceInfo> getDeviceInfo(String rootDevice) {       //RootUuid, Sort, Value, SubUuid;
        ArrayList<DeviceInfo> list_deviceInfo = new ArrayList<>();

        ArrayList<String> switchCount = new ArrayList<>();

        try {
            //PowerSwitch(SmartPlug or readyPower)
            switchCount = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDevice);
            Log.d(DEBUG_TAG, "getSwitchInfo switchCount : " + switchCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (switchCount != null) {
                for (int a = 0; a < switchCount.size(); a++) {

                    String deviceNumber = switchCount.get(a);
                    Log.d(DEBUG_TAG, "switchCount : get(" + a + ") = " + deviceNumber);

                    if (deviceNumber != null) {

                        String raw = "";

                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(rootDevice, deviceNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        DeviceInfo device_data = null;

                        if (rootDevice.equalsIgnoreCase(RootDevice.BOILER)) {
                            device_data = getThermostatStatusByRaw(raw);
                        } else if (rootDevice.equalsIgnoreCase(RootDevice.DETECT_SENSORS)) {
                            device_data = getSensorStatusByRaw(raw);
                        } else if (rootDevice.equalsIgnoreCase(RootDevice.SWITCH)) {
                            device_data = getSwitchStatusByRaw(raw);
                        }

                        if (device_data != null) {
                            list_deviceInfo.add(device_data);
                        }
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "switchCount : null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list_deviceInfo;
    }

    public DeviceInfo getSwitchStatusByRaw(String raw) {
        DeviceInfo device_data = new DeviceInfo();
//        Log.d(TAG, "getSwitchStatusByRaw : raw : " + raw);

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType = SubSort.SWITCH_BINARY;
            String rootDevice = "";
            String commaxDevice = getCommaxDevice(raw);

            if (!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.LIGHT)) {
                    device_sort = CommaxDevice.LIGHT;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.SMART_PLUG)) {
                    device_sort = CommaxDevice.SMART_PLUG;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.MAIN_SWITCH)) {
                    device_sort = CommaxDevice.MAIN_SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.AWAY_SWITCH)) {
                    device_sort = CommaxDevice.AWAY_SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.FAN_SYSTEM)) {
                    device_sort = CommaxDevice.FAN_SYSTEM;//TODO FAN 관련 전체 수정 필요
//                    controlType = SubSort.FAN_SPEED;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.CURTAIN)) {
                    device_sort = CommaxDevice.CURTAIN;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.STANDBY_POWER)) {
                    device_sort = CommaxDevice.STANDBY_POWER;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)) {
                    device_sort = CommaxDevice.FCU;
                }
            }

            try {
                rootDevice = getRootDevice(raw);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            Log.d(DEBUG_TAG, "getSwitchStatusByRaw : commax " + commaxDevice);

            if (!TextUtils.isEmpty(device_sort)) {

                subUuid = getSubUuidFromMatchingSubDevice(controlType, raw);   //if it is null, this is not meter device

                if (!TextUtils.isEmpty(subUuid)) {
                    rootUuid = getRootUuidFromRaw(raw);

                    String status = getDeviceStatus(subUuid);

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.mainDevice = new SubDevice();
                        device_data.mainDevice.subUuid = subUuid;
                        device_data.mainDevice.value = status;
                        device_data.mainDevice.sort = controlType;
                        device_data.nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);

                        device_data.initDevice(rootUuid, device_sort, controlType, subUuid, device_data.mainDevice);


                        Log.d(DEBUG_TAG, "getSwitchStatusByRaw : status " + status + " saved");
                    }
                }

                //add details
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.SMART_PLUG)) {

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        if (rootDevice.equalsIgnoreCase(RootDevice.SWITCH)) {
                            device_data.subDevices = new ArrayList<>();

                            SubDevice subDevice = new SubDevice();
                            subDevice = getSubDevice(rootUuid, raw, SubSort.ELECTRIC_METER);
                            device_data.subDevices.add(subDevice);
                            subDevice = getSubDevice(rootUuid, raw, SubSort.SWITCH_BINARY);
                            device_data.subDevices.add(subDevice);
                        }
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getSwitchStatusByRaw : no device_sort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return device_data;
    }

    public DeviceInfo getSensorStatusByRaw(String raw) {
        DeviceInfo device_data = new DeviceInfo();

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType = SubSort.NONE;
            String commaxDevice = getCommaxDevice(raw);

            if (!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.DETECT_SENSORS)) {
                    device_sort = CommaxDevice.DETECT_SENSORS;
                }
            }

//            Log.d(DEBUG_TAG, "getSensorStatusByRaw : commax " + commaxDevice);

            rootUuid = getRootUuidFromRaw(raw);

            if ((!TextUtils.isEmpty(rootUuid))) {

                device_data.mainDevice = new SubDevice();
                device_data.nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);

                device_data.initDevice(rootUuid, device_sort, controlType, subUuid, device_data.mainDevice);
            }

            if (!TextUtils.isEmpty(device_sort)) {

                //add details

                if (commaxDevice.equalsIgnoreCase(CommaxDevice.DETECT_SENSORS)) {

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.subDevices = new ArrayList<>();

                        SubDevice subDevice = new SubDevice();
                        subDevice = getSubDevice(rootUuid, raw, SubSort.AIR_TEMPERATURE);
                        device_data.subDevices.add(subDevice);

                        subDevice = getSubDevice(rootUuid, raw, SubSort.HUMIDITY);
                        device_data.subDevices.add(subDevice);
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getSensorStatusByRaw : no device_sort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return device_data;
    }

    public DeviceInfo getThermostatStatusByRaw(String raw) {
        DeviceInfo device_data = new DeviceInfo();
        Log.d(DEBUG_TAG, "getThermostatStatusByRaw : raw : " + raw);

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType = SubSort.THERMOSTAT_MODE;
            String commaxDevice = getCommaxDevice(raw);

            if (!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)) {
                    device_sort = CommaxDevice.FCU;
                }
            }

            Log.d(DEBUG_TAG, "getThermostatStatusByRaw : commax " + commaxDevice);

            if (!TextUtils.isEmpty(device_sort)) {

                subUuid = getSubUuidFromMatchingSubDevice(controlType, raw);   //if it is null, this is not meter device

                if (!TextUtils.isEmpty(subUuid)) {
                    rootUuid = getRootUuidFromRaw(raw);

                    String status = getDeviceStatus(subUuid);

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.mainDevice = new SubDevice();
                        device_data.mainDevice.subUuid = subUuid;
                        device_data.mainDevice.value = status;
                        device_data.mainDevice.sort = controlType;
                        device_data.nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);

                        device_data.initDevice(rootUuid, device_sort, controlType, subUuid, device_data.mainDevice);


                        Log.d(DEBUG_TAG, "getSwitchStatusByRaw : status " + status + " saved");
                    }
                }

                //add details

                if (commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)) {

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.subDevices = new ArrayList<>();

                        SubDevice subDevice = new SubDevice();
                        subDevice = getSubDevice(rootUuid, raw, SubSort.THERMOSTAT_RUNMODE);
                        device_data.subDevices.add(subDevice);

                        subDevice = getSubDevice(rootUuid, raw, SubSort.THERMOSTAT_COOLINGPOINT);
                        device_data.subDevices.add(subDevice);

                        subDevice = getSubDevice(rootUuid, raw, SubSort.THERMOSTAT_HEATINGPOINT);
                        device_data.subDevices.add(subDevice);

                        subDevice = getSubDevice(rootUuid, raw, SubSort.THERMOSTAT_AWAY_MODE);
                        device_data.subDevices.add(subDevice);

                        subDevice = getSubDevice(rootUuid, raw, SubSort.AIR_TEMPERATURE);
                        device_data.subDevices.add(subDevice);
                    }
                } else {
                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.subDevices = new ArrayList<>();

                        SubDevice subDevice = new SubDevice();
                        subDevice = getSubDevice(rootUuid, raw, SubSort.THERMOSTAT_SETPOINT);
                        device_data.subDevices.add(subDevice);
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getSwitchStatusByRaw : no device_sort");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return device_data;
    }

    private String getRootDevice(String raw) {
        String rootResult = "";

        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());
                String object = jObject.getString("object");
                try {
                    rootResult = (new JSONObject(object.trim())).getString("rootDevice");
//                    Log.d(TAG, "rootDeviceName : " + rootResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(DEBUG_TAG, "get rootResult failed");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(DEBUG_TAG, "get rootResult Object failed2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "get Json Object failed2");
        }

        return rootResult;
    }

    public SubDevice getSubDeviceStatusFromRaw(String sub_raw, String subUuid, String subSort) {

        SubDevice subDevice = new SubDevice();
        String status = "";
        String option1 = "";
        String option2 = "";
        String scale = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(sub_raw)) {
                jObject = new JSONObject(sub_raw.toString());

                if (jObject != null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if (jSubObject != null) {
                        String subObject = jSubObject.getString("subDevice");
                        if (subObject != null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    status = jsonArray.getJSONObject(i).getString("value");

                                    try {
                                        option1 = jsonArray.getJSONObject(i).getString("option1");
                                        option2 = jsonArray.getJSONObject(i).getString("option2");
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }

                                    try {
                                        String str_scale = jsonArray.getJSONObject(i).getString("scale");
                                        JSONArray arr_scale = new JSONArray(str_scale.trim());
                                        scale = arr_scale.get(0).toString();
                                    } catch (Exception e) {

                                    }

                                    try {
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER) || subSort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                                            String precision = jsonArray.getJSONObject(i).getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(DEBUG_TAG, "getAccurateValue : " + status);
                                        }
                                    } catch (Exception e) {

                                    }
//                                    Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw status : " + status);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

//                                Log.d(TAG, "getStatusFromRaw get status retry");
                                try {
                                    JSONObject subDeviceObj = (new JSONObject(subObject.trim()));
                                    status = subDeviceObj.getString("value");

                                    try {
                                        option1 = subDeviceObj.getString("option1");
                                        option2 = subDeviceObj.getString("option2");
                                    } catch (Exception e1) {
                                        e1.getMessage();
                                    }

                                    try {
                                        String str_scale = subDeviceObj.getString("scale");
                                        JSONArray arr_scale = new JSONArray(str_scale.trim());
                                        scale = arr_scale.get(0).toString();
                                    } catch (Exception e1) {

                                    }

                                    try {
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER) || subSort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                                            String precision = subDeviceObj.getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(DEBUG_TAG, "getAccurateValue : " + status);
                                        }
                                    } catch (Exception e2) {

                                    }
                                    Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw status retry : " + status);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw sub_raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw get Json Object failed2");
        }


        try {
            if (!TextUtils.isEmpty(status)) {
                subDevice.subUuid = subUuid;
                subDevice.sort = subSort;
                subDevice.value = status;

                if ((!TextUtils.isEmpty(option1)) && (!TextUtils.isEmpty(option2))) {
                    subDevice.option1 = option1;
                    subDevice.option2 = option2;
                }

                if (!TextUtils.isEmpty(scale)) {
                    subDevice.scale = scale;
                }
                Log.d(DEBUG_TAG, "getSubDeviceStatusFromRaw : " + subSort + " / status " + status + " saved");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subDevice;
    }

    public SubDevice getSubDeviceStatus(String subUuid, String subSort) {
        String sub_raw = "";
        SubDevice subDevice = new SubDevice();

        try {
            sub_raw = MySQLConnection.getInstance().getRawSubJson(subUuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            subDevice = getSubDeviceStatusFromRaw(sub_raw, subUuid, subSort);
        } catch (Exception e) {
            subDevice = null;
            e.printStackTrace();
        }

        return subDevice;
    }

    private SubDevice getSubDevice(String rootUuid, String raw, String subSort) {

        SubDevice subDevice = new SubDevice();

        try {
            String subUuid = getSubUuidFromMatchingSubDevice(subSort, raw);   //if it is null, this is not meter device

            if (!TextUtils.isEmpty(subUuid)) {

                subDevice = getSubDeviceStatus(subUuid, subSort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subDevice;
    }

    public String getDeviceStatus(String subUuid) {
        String sub_raw = "";
        String status = "";

        try {
            sub_raw = MySQLConnection.getInstance().getRawSubJson(subUuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            status = getStatusFromRaw(sub_raw);
        } catch (Exception e) {
            status = Status.UNKNOWN;
            e.printStackTrace();
        }

        return status;
    }

    public String getStatusFromRaw(String sub_raw) {

        String status = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(sub_raw)) {
                jObject = new JSONObject(sub_raw.toString());

                if (jObject != null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if (jSubObject != null) {
                        String subObject = jSubObject.getString("subDevice");
                        if (subObject != null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    status = jsonArray.getJSONObject(i).getString("value");

                                    try {
                                        String subSort = jsonArray.getJSONObject(i).getString("sort");
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER) || subSort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                                            String precision = jsonArray.getJSONObject(i).getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(DEBUG_TAG, "getAccurateValue : " + status);
                                        }
                                    } catch (Exception e2) {

                                    }
                                    Log.d(DEBUG_TAG, "getStatusFromRaw status : " + status);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();

//                                Log.d(TAG, "getStatusFromRaw get status retry");
                                try {
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    status = subDevice.getString("value");

                                    try {
                                        String subSort = subDevice.getString("sort");
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER) || subSort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                                            String precision = subDevice.getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(DEBUG_TAG, "getAccurateValue : " + status);
                                        }
                                    } catch (Exception e2) {

                                    }
                                    Log.d(DEBUG_TAG, "getStatusFromRaw status retry : " + status);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getStatusFromRaw sub_raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getStatusFromRaw get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getStatusFromRaw get Json Object failed2");
        }

        return status;
    }

    private String getAccurateValue(String value, String precision) {        //TODO 검증 필요
        String val = value;
        String left = "", right = "", result = "";
        try {
            if ((val != null)) {
                int length = val.length();

                if (precision == null || precision.equals("")) return "0.0";       //TODO 질문 필요

                if (precision.equals("0")) result = val;
                else {
                    if (length <= Integer.parseInt(precision)) {
                        right = val;
                        for (int i = 0; i < Integer.parseInt(precision) - length; i++) {
                            right = "0" + right;
                        }
//                        Log.d(TAG, "getAccurateValue small right "+right);

                        if (right.length() == 1) {
                            right = right.substring(0, 1);
                        } else if (right.length() >= 2) {
                            right = right.substring(0, 2);
                        }
                        result = "0." + right;
                    } else {
                        left = val.substring(0, length - (Integer.parseInt(precision)));
                        right = val.substring(length - (Integer.parseInt(precision)));
                        if (right.length() == 1) {
                            right = right.substring(0, 1);
                        } else if (right.length() >= 2) {
                            right = right.substring(0, 2);
                        }
                        result = left + "." + right;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("getAccurateValue", ""+result);
        return result;
    }

    public String getRootUuidFromRaw(String raw) {
//        Log.d(TAG, "getRootUuidFromRaw : "+raw);

        JSONObject jObject = null;
        String rootUuid = "";

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());
                if (jObject != null) {
                    String object = jObject.getString("object");

                    try {
                        rootUuid = (new JSONObject(object.trim())).getString("rootUuid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getRootUuidFromRaw get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getRootUuidFromRaw get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getRootUuidFromRaw get Json Object failed2");
        }
        return rootUuid;
    }

    private String getSubUuidFromMatchingSubDevice(String deviceSort, String raw) {
        String subUuid = "";

        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject != null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if (jSubObject != null) {
                        String subObject = jSubObject.getString("subDevice");
                        if (subObject != null) {
                            JSONArray jsonArray = (new JSONArray(subObject.trim()));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String sort = jsonArray.getJSONObject(i).getString("sort");
                                if (sort.equalsIgnoreCase(deviceSort)) {
                                    subUuid = jsonArray.getJSONObject(i).getString("subUuid");
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "getMatchingSubDevice get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getMatchingSubDevice get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(DEBUG_TAG, "getMatchingSubDevice get Json Object failed2");
        }
        return subUuid;
    }

    private String getCommaxDevice(String raw) {
        String commaxResult = "";

        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());
                String object = jObject.getString("object");
                try {
                    commaxResult = (new JSONObject(object.trim())).getString("commaxDevice");
//                    Log.d("UsageData", "commaxDevice JSONObject : " + commaxResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("UsageData", "get commaxDevice failed");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("UsageData", "get commaxDevice Object failed2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed2");
        }

        return commaxResult;
    }

    /**
     * read public data server (/user/app/bin/cloud_svr.i)
     */

    private void readCloudDNS() {

        try {
            FileEx io = new FileEx();
            String[] files = null;
            String server_dns = "";
            String local_port = "";

            try {
                files = io.readFile(NameSpace.CLOUD_SERVER_INFO_FILE);
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            } catch (IOException e) {
                // e.printStackTrace();
            }

            if (files == null) {
                return;
            }

            if (files.length > 0) {
                // ���� üũ
                if (files == null) {
                    return;
                }
                if ("".equals(files[0])) {
                    return;
                }
                if ("-1".equals(files[0])) {
                    return;
                }
            }

            for (int i = 0; i < files.length; i++) {
                String line = files[i];

                if (line.contains(NameSpace.KEY_PUBLIC_SERVER_DNS)) {
                    server_dns = line.replace(NameSpace.KEY_PUBLIC_SERVER_DNS + "=", "");
                }

                if (line.contains(NameSpace.KEY_LOCAL_SERVER_PORT)) {
                    local_port = line.replace(NameSpace.KEY_LOCAL_SERVER_PORT + "=", "");
                }
            }

            Log.d(DEBUG_TAG, "Cloud server DNS :" + server_dns);

            if ((server_dns != null) && (!TextUtils.isEmpty(server_dns))) {
                CLOUD_SERVER = server_dns;
            } else {
                Log.d(DEBUG_TAG, "Getting cloud server DNS failed, Crt CLOUD_SERVER : " + CLOUD_SERVER);
            }

            if ((local_port != null) && (!TextUtils.isEmpty(local_port))) {
                LOCAL_PORT = local_port;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check server support Electricity
     *
     * @return true when server support Electricity.
     */

    public boolean checkSupportElec() {

        List<String> list = new ArrayList<String>();

        try {
            list = getSupports();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(DEBUG_TAG, "list.size()" + list.size());

        try {
            if (list.size() > 0) {
                for (String id : list) {
                    if (id.equalsIgnoreCase(Items.ELEC)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * check public data server (e.g. air, weather...)
     * if local server is 127.0.0.1, check cloud server.
     * if not check local server:port.
     *
     * @return true if connected
     * @throws IOException
     * @throws IllegalArgumentException
     */

    public boolean checkNetworkFlexible() throws IOException, IllegalArgumentException {
        try {
            Log.d(DEBUG_TAG, "checkNetworkFlexible LocalIp : " + serverip);

            String flexip = "";
            if (serverip.equals("127.0.0.1")) flexip = CLOUD_SERVER;
            else flexip = serverip + LOCAL_PORT;

            Log.d(DEBUG_TAG, "checkNetworkFlexible flexip : " + flexip);

//            serverip = CLOUD_SERVER;

            URL url = null;
            try {
                url = new URL("http://" + flexip);

            } catch (MalformedURLException e) {
                throw new IOException();
            }

            HttpURLConnection urlConnection;
            DataInputStream dis = null;
            DataOutputStream dout = null;

            String param = "";

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content Type", "application/soap+xml;charset=utf-8");

                urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                urlConnection.setReadTimeout(TIMEOUT_VALUE);
                urlConnection.setDoOutput(true);

                dout = new DataOutputStream(urlConnection.getOutputStream());
                dout.write(param.getBytes());

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    dis = new DataInputStream(urlConnection.getInputStream());
                }

                urlConnection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * check local server connected.
     *
     * @return false if local server is 127.0.0.1.
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public boolean checkLocalNetwork() throws IOException, IllegalArgumentException {
        try {
            Log.d(DEBUG_TAG, "checkLocalNetwork LocalIp : " + serverip);

            if (serverip.equals("127.0.0.1")) return false;

            URL url = null;
            try {
                url = new URL("http://" + serverip);

            } catch (MalformedURLException e) {
                throw new IOException();
            }

            HttpURLConnection urlConnection;
            DataInputStream dis = null;
            DataOutputStream dout = null;

            String param = "";

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/soap+xml;charset=utf-8");

                urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                urlConnection.setReadTimeout(TIMEOUT_VALUE);
                urlConnection.setDoOutput(true);

                dout = new DataOutputStream(urlConnection.getOutputStream());
                dout.write(param.getBytes());

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    dis = new DataInputStream(urlConnection.getInputStream());
                }

                urlConnection.disconnect();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Serve air, life health info when system language is KR.
     *
     * @return true when system language is KR.
     */

    public boolean airSupport() {              //only support Korea
        try {
            Log.d("airSupport", "locale : " + locale.getCountry());
//            if (locale.getCountry().equals("KR")) return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getMissedCall(Uri call_content_uri) {

        Cursor missedCallCursor = null;

        try {
//            int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG);

//            if(permissionCheck != PackageManager.PERMISSION_DENIED) {
            missedCallCursor = mContext.getContentResolver().query(
                    call_content_uri,
                    new String[]{CallLog.Calls.TYPE},
                    CallLog.Calls.TYPE + " = ? AND " + CallLog.Calls.NEW + " = ?",
                    new String[]{Integer.toString(CallLog.Calls.MISSED_TYPE), "1"},
                    CallLog.Calls.DATE + " DESC ");

            final int count = missedCallCursor.getCount();
            Log.d(DEBUG_TAG, "getMissedCall count " + count);
            return count;
//            }
        } catch (UnsupportedOperationException e) {
            Log.e(DEBUG_TAG, "Unsupported Operation Exception. !!!");
        } catch (NullPointerException e) {
            Log.e(DEBUG_TAG, "Null Pointer Exception. !!!");
        } finally {
            if (missedCallCursor != null) {
                missedCallCursor.close();
            }
        }
        return -1;

    }

    public int getMissedVisitor() {

        Cursor imageCursor = null;
        int count = 0;

        /* MediaStore Column */
        String strCmd[] = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DESCRIPTION,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.ORIENTATION
        };

        try {
            imageCursor = mContext.getContentResolver().query(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    strCmd,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        } catch (UnsupportedOperationException e) {
            Log.e("checkVisitorImageCount", "Unsupported Operation Exception. !!!");
        } catch (NullPointerException e) {
            Log.e("checkVisitorImageCount", "Null Pointer Exception. !!!");
        }

        if (imageCursor != null) {
            // DB에서 최근날짜로 저장된 path 가져옴
            Log.i("checkVisitorImageCount", "imageCursor.getCount() : " + imageCursor.getCount());
            count = imageCursor.getCount();
        }
        imageCursor.close();
        Log.d(DEBUG_TAG, "getMissedVisitor count " + count);
        return count;
    }

    public boolean checkSupportVisitor() {
        try {
            File file = new File(NameSpace.EXTRA_VISITOR_IMAGE_FOLDER);

            if ((file != null) && (file.exists())) {
                Log.d(DEBUG_TAG, "checkSupportVisitor EXTRA_VISITOR_IMAGE_FOLDER : " + file.listFiles().length);
                if (file.listFiles().length > 0) return true;
            } else {
                File file1 = new File(NameSpace.VISITOR_IMAGE_FOLDER);
                if ((file1 != null) && (file1.exists())) {
                    Log.d(DEBUG_TAG, "checkSupportVisitor VISITOR_IMAGE_FOLDER : " + file1.listFiles().length);
                    if (file1.listFiles().length > 0) return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public int getVisitorCount() {

        int count = -1;
        try {

            File file = new File(NameSpace.VISITOR_COUNT_FOLDER);

            if ((file != null) && (file.exists())) {
                count = file.listFiles().length;
                Log.d(DEBUG_TAG, "getVisitorCount VISITOR_COUNT_FOLDER : " + file.listFiles().length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(DEBUG_TAG, "getVisitorCount count : " + count);
        return count;
    }

    public int checkNewDelivery() {


        try {
            String query = String
                    .format("deliveryInfo;dong=%1$samp;ho=%2$s", dong, ho);
            Log.d(DEBUG_TAG, "checkNewDelivery q = " + query);

            String value = SoapHelper.call(serverip, query);

            if (!TextUtils.isEmpty(value)) {

                if (value.equalsIgnoreCase("1")) {
                    Log.d(DEBUG_TAG, "checkNewDelivery value 1");
                    return 1;
                } else if (value.equalsIgnoreCase("0")) {
                    Log.d(DEBUG_TAG, "checkNewDelivery value 0");
                    return 0;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;

    }

    /**
     * @return CarItem (car number, parking position) list
     */

    public ArrayList<CarItem> getCarPosition() {

        ArrayList<CarItem> carItems = new ArrayList<>();

        try {
            String query = String
                    .format("carPositionamp;dong=%1$samp;ho=%2$s", dong, ho);
            Log.d(DEBUG_TAG, "getCarPosition q = " + query);

            String value = SoapHelper.call(serverip, query);

            if (!TextUtils.isEmpty(value)) {

                int added = 0;

                JSONObject jsonObject = new JSONObject(value);
                String infos = jsonObject.getString("parking");

                JSONArray jsonArray = new JSONArray(infos);

                for (int i = 0; i < jsonArray.length(); i++) {
                    if (added < (NameSpace.MAX_PARKING)) {
                        String raw = jsonArray.get(i).toString();
                        CarItem carItem = getCarItem(raw);
                        carItems.add(carItem);
                        added++;
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return carItems;

    }

    /**
     * @return CarItem (car number, parking position)
     */

    private CarItem getCarItem(String raw) {

        try {
            JSONObject jsonObject = new JSONObject(raw);
            String position = jsonObject.getString("position");
            String number = jsonObject.getString("number");

            if ((!TextUtils.isEmpty(position)) && (!TextUtils.isEmpty(number))) {
                CarItem carItem = new CarItem();
                carItem.carNumber = number;
                carItem.position = position;

                return carItem;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Serve air, life health info when system language is KR.
     *
     * @return true when system language is KR.
     */

    public boolean healthLifeSupport() {        //only support Korea
        try {
            Log.d("healthLifeSupport", "locale : " + locale.getCountry());
//            if (locale.getCountry().equals("KR")) return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Served info is different by month.
     *
     * @return true when some type of info is supported this month.
     */

    public boolean supportedMonth(String type) {
        try {
            Calendar cal = Calendar.getInstance();
            String month = String.valueOf((cal.get(Calendar.MONTH)));       //0~11 0:January 11:December
//        Log.d("supportedMonth", ""+month);

            switch (type) {
                case NameSpace.HEALTH_COLD:     // 9~4 : 8,9,10,11,0,1,2,3
                    if (month.equals("8") || month.equals("9") || month.equals("10") || month.equals("11") || month.equals("0") || month.equals("1") || month.equals("2") || month.equals("3"))
                        return true;
                    else return false;
                case NameSpace.LIFE_SENSORYTEM: // 11~3 : 10,11,0,1,2
                    if (month.equals("10") || month.equals("11") || month.equals("0") || month.equals("1") || month.equals("2"))
                        return true;
                    else return false;
                case NameSpace.LIFE_DSPLS:      // 6~9 : 5,6,7,8
                    if (month.equals("5") || month.equals("6") || month.equals("7") || month.equals("8"))
                        return true;
                    else return false;
                case NameSpace.LIFE_ULTRV:      // 3~11 : 2,3,4,5,6,7,8,9,10
                    if (month.equals("2") || month.equals("3") || month.equals("4") || month.equals("5") || month.equals("6") || month.equals("7") || month.equals("8") || month.equals("9") || month.equals("10"))
                        return true;
                    else return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public double getHealthLifeData() {
        double data = -1; // uv
        String result1 = "", result2 = "", result3 = "";

//        String areaCode = "1114052000"; // 서울시 중구 소공동
        String areaCode = "1171071000"; // 서울시 송파구 잠실6동

        try {

            FileInputStream fis = null;
            Properties mProperty = null;
            String filename = "woeid.properties";
            File file = new File(Environment.getExternalStorageDirectory() + "/WOEID/" + filename);
            String imsi = "";

            if (file.exists()) {
                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);

                    try {
                        if (mProperty.getProperty(NameSpace.AREACODE) != null) {
                            areaCode = mProperty.getProperty(NameSpace.AREACODE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(DEBUG_TAG, "areaCode from file : " + areaCode);
            } else {
                Log.d("Property", "areaCode not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            result2 = JSONHelper.restCall(mContext, serverip, CLOUD_SERVER, LOCAL_PORT, "life", areaCode, areaCode, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String uv = restGET(NameSpace.HEALTH, NameSpace.INDEX, NameSpace.LIFE_ULTRV, result2);
            try {
                if (!TextUtils.isEmpty(uv)) {
                    data = Double.parseDouble(uv);
                    if (data >= 1000) data = 999;
//                    data[3] = 999;
                } else data = -1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * if US serve Fahrenheit data, else celsius.
     *
     * @param days default 2 (how many days do you want read forecast?)
     * @return info data{temp, code, humidity}, location, raw data
     */
    public Pair<Pair<double[], String>, String> getWeatherData(int days) { // Pair<Pair<weather, location>, forecast_raw>

        try {
            Log.d("getWeatherData", "locale : " + locale.getCountry());
        } catch (Exception e) {
            e.printStackTrace();
        }

        double data[] = {9999, 3200, -1};           //{temp, code, humidity}
        String location = "Seoul";
        String forecast_raw = "";

        Pair<double[], String> whole_data = new Pair<>(data, location);
        String result = "";
        String woeid = "1132599";
//        String woeid = "4118"; //Toronto

        try {

            FileInputStream fis = null;
            Properties mProperty = null;
            String filename = "woeid.properties";
            File file = new File(Environment.getExternalStorageDirectory() + "/WOEID/" + filename);
            String imsi = "";

            if (file.exists()) {
//                Log.d("Property", "File exists");
                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);
                    imsi = mProperty.getProperty("woeid");

                    try {
                        if (mProperty.getProperty("location") != null) {
                            location = mProperty.getProperty("location");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    Log.d(DEBUG_TAG, "imsi : "+imsi);
                    if ((!TextUtils.isEmpty(imsi)) && (TextUtils.isDigitsOnly(imsi))) {
                        woeid = imsi;
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(DEBUG_TAG, "woeid from file : " + woeid);
            } else {
                Log.d("Property", "File not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            try {
                result = JSONHelper.restCall(mContext, serverip, CLOUD_SERVER, LOCAL_PORT, "weather", woeid, "c", days);
            } catch (IOException e) {
                e.printStackTrace();
                return new Pair<>(new Pair<>(data, "Seoul"), "");
            }

            try {
                String temp = restGET(NameSpace.WEATHER, NameSpace.CONDITION, "temp", result);
                try {
                    data[0] = Double.parseDouble(temp);
                    if (data[0] >= 1000) data[0] = 999;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String code = restGET(NameSpace.WEATHER, NameSpace.CONDITION, "code", result);
                try {
                    data[1] = Double.parseDouble(code);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String humidity = restGET(NameSpace.WEATHER, NameSpace.CONDITION, "humidity", result);
                try {
                    data[2] = Double.parseDouble(humidity);
                    if (data[2] >= 1000) data[2] = 999;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            try {
//                String city = restGET(NameSpace.WEATHER, NameSpace.CONDITION, "city", result);
//                if (!TextUtils.isEmpty(city)) {
//                    location = city;
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            //forecast test

            try {
                if (days > 0) {
                    forecast_raw = result;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                whole_data = new Pair<>(data, location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Pair<>(whole_data, forecast_raw);
    }

    /**
     * @return data[]{ozone, dust, totalAir}
     */
    public double[] getAirData() {
        double data[] = {-1, -1, -1};           //{o3, dust, totalAir}
        String result = "";

        // 서울특별시 송파구 신천동
        String tmx = "208679.927916";
        String tmy = "446858.5482";

        try {

            FileInputStream fis = null;
            Properties mProperty = null;
            String filename = "woeid.properties";
            File file = new File(Environment.getExternalStorageDirectory() + "/WOEID/" + filename);
            String imsi = "";

            if (file.exists()) {
//                Log.d("Property", "File exists");
                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);

                    try {
                        if (mProperty.getProperty(NameSpace.TMX) != null) {
                            tmx = mProperty.getProperty(NameSpace.TMX);
                        }

                        if (mProperty.getProperty(NameSpace.TMY) != null) {
                            tmy = mProperty.getProperty(NameSpace.TMY);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(DEBUG_TAG, "TMX TMY from file : " + tmx + " / " + tmy);
            } else {
                Log.d("Property", "TMX TMY not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            result = JSONHelper.restCall(mContext, serverip, CLOUD_SERVER, LOCAL_PORT, "air", tmx, tmy, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String o3 = restGET(NameSpace.AIR, NameSpace.INDEX, NameSpace.AIR_O3, result);
            try {
                data[0] = Double.parseDouble(o3);
                if (data[0] >= 10) data[0] = 9.999;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String dust = restGET(NameSpace.AIR, NameSpace.INDEX, NameSpace.AIR_DUST, result);
            try {
                data[1] = Double.parseDouble(dust);
                if (data[1] >= 1000) data[1] = 999;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String totalAir = restGET(NameSpace.AIR, NameSpace.INDEX, NameSpace.AIR_TOTAL, result);
            try {
                data[2] = Double.parseDouble(totalAir);
                if (data[2] >= 1000) data[2] = 999;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Parse str by info type
     *
     * @param type     info type (e.g. AIR, WEATHER)
     * @param sub_type parse type (AIR - INDEX, WEATHER - CONDITION)
     * @param what     what do you want to get (e.g. ozone, temperature)
     * @param str      raw string(JSON data)
     * @return value of what
     * @throws IOException
     */

    private String restGET(String type, String sub_type, String what, String str) throws IOException {
        JSONObject jsonObject;
        String result = "";

        try {
            if (str.length() > 0) {
//                Log.d(DEBUG_TAG, "restGET str = " + str);
                jsonObject = new JSONObject(str);

                try {
                    String mType = jsonObject.getString(type);
                    if ((type.equalsIgnoreCase(NameSpace.WEATHER))
                            || (type.equalsIgnoreCase(NameSpace.AIR))
                            || (type.equalsIgnoreCase(NameSpace.HEALTH))) {
                        JSONObject typeObj = new JSONObject(mType);
                        if (sub_type.equalsIgnoreCase(NameSpace.FORECAST)) {
                            result = typeObj.getString(sub_type);
                        } else {
                            String sub_str = typeObj.getString(sub_type);
                            result = restGETDetail(what, sub_str);
                        }
                    } else if (type.equalsIgnoreCase(NameSpace.YELLOWSAND)) {
                        JSONObject typeObj = new JSONObject(mType);
                        result = typeObj.getString(sub_type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return result;
    }

    private String restGETDetail(String what, String str) throws IOException {
        JSONObject jsonObject;
        String result = "";

        try {
            if (str.length() > 0) {
                jsonObject = new JSONObject(str);

                try {
                    result = jsonObject.getString(what);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return result;
    }

    /**
     * data example #08110812#16001800#Test1$1#08120813#17002000#Test2
     *
     * @return NoticeItem (title, time, index, date)
     */
    public ArrayList<NoticeItem> getNotice() {

        ArrayList<NoticeItem> noticeItems = new ArrayList<>();

        try {
            String query = String.format("getNotice");
            String value = SoapHelper.call(serverip, query);

//            String value="5#08110812#16001800#Test5$4#08110820#16301800#Test4$3#08110811#16001700#Test3$2#08110812#16001800#Test2$1#08120813#17002000#Test1";

            if (!TextUtils.isEmpty(value)) {
                int notice_count = getCount(value);
                ArrayList<String> removedIndex = new ArrayList<>();
                removedIndex = getRemovedIndex(); // 다시 보지 않기 처리한 공지사항을 거르기 위하여

//                for (int count = (notice_count - 1); count >= 0; count--) {

                for (int count = 0; count < notice_count; count++) {

                    if (!TextUtils.isEmpty(value)) {
                        value = value.trim();
                        if (value.contains("$")) {
                            String[] array = value.split("\\$");
                            if (array.length >= count) {
                                if (array[count].contains("#")) {
                                    String[] sub_arr = array[count].split("\\#");
                                    NoticeItem noticeItem = getNoticeItem(sub_arr, removedIndex);
                                    if (noticeItem != null) {
                                        noticeItems.add(noticeItem);
                                    } else {
                                        Log.d(DEBUG_TAG, "noticeItems null");
                                    }
                                }
                            }
                        } else {
                            String[] sub_arr = value.split("\\#");
                            NoticeItem noticeItem = getNoticeItem(sub_arr, removedIndex);
                            if (noticeItem != null) {
                                noticeItems.add(noticeItem);
                            } else {
                                Log.d(DEBUG_TAG, "noticeItems null2");
                            }
                        }
                    }
                }

                return noticeItems;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return index (do not show again data)
     */

    private ArrayList<String> getRemovedIndex() {

        /* Getting unselected notice items */
        Properties mProperty = new Properties();
        FileInputStream fis = null;
        ArrayList<String> result = new ArrayList<>();

        try {
            String filename = "notice.properties";
            File file = new File(Environment.getExternalStorageDirectory() + "/InfoFile/" + filename);
            String proValue = null;

            if (file.exists()) {

                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);
                    Set<Object> keys = mProperty.keySet();

                    for (Object key : keys) {
                        String str_key = (String) key;
                        proValue = mProperty.getProperty(str_key);
                        if (proValue != null) {
                            if (("not").equals(proValue)) {
                                result.add(str_key);
                                Log.d(DEBUG_TAG, "getRemovedIndex : not : " + str_key);
                            }
                        }
                    }
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d(DEBUG_TAG, "getRemovedIndex File not exists");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private NoticeItem getNoticeItem(String[] sub_arr, ArrayList<String> removed) {
        try {

            if (!removed.contains(sub_arr[0])) {
                NoticeItem noticeItem = new NoticeItem();

                if (sub_arr.length == 4) {
                    if (stringIsNumber(sub_arr[0])) noticeItem.index = sub_arr[0];
                    else noticeItem.index = "";

                    noticeItem.date = setDate(sub_arr[1]);
                    noticeItem.time = setTime(sub_arr[2]);
                    noticeItem.title = sub_arr[3];

                    if ((!TextUtils.isEmpty(noticeItem.index))
                            && (!TextUtils.isEmpty(noticeItem.date))
                            && (!TextUtils.isEmpty(noticeItem.time))
                            && (!TextUtils.isEmpty(noticeItem.title))) {
                        return noticeItem;
                    } else {
                        Log.d(DEBUG_TAG, "index " + noticeItem.index + ", date " + noticeItem.date + ", time " + noticeItem.time + ", title " + noticeItem.title);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean stringIsNumber(String str) {
        String regexStr = "^[0-9]*$";
        if (!str.trim().equals("")) {
            if (str.contains("-")) {
                str = str.substring(1);
            }
            if (str.trim().matches(regexStr)) return true;
            else return false;
        } else return false;
    }

    /**
     * format of date
     *
     * @param str e.g. 08110812#16001800
     * @return 08/11~08/12
     */

    public String setDate(String str) {
        if (str.length() == 8) {
            if (str.substring(0, 4).equals(str.substring(4))) {
                return str.substring(0, 2) + "/" + str.substring(2, 4);
            }
            return str.substring(0, 2) + "/" + str.substring(2, 4) + "~" + str.substring(4, 6) + "/" + str.substring(6);
        }
        return "";
    }

    /**
     * format of date
     *
     * @param str e.g. 08110812#16001800
     * @return 16:00~18:00
     */

    public String setTime(String str) {
        if (str.length() == 8) {
            if (str.substring(0, 4).equals(str.substring(4))) {
                return str.substring(0, 2) + ":" + str.substring(2, 4);
            }
            return str.substring(0, 2) + ":" + str.substring(2, 4) + "~" + str.substring(4, 6) + ":" + str.substring(6);
        }
        return str;
    }

    /**
     * get Notice count
     *
     * @param raw notice whole string from getNotice()
     * @return notice count
     * @throws IOException
     * @throws IllegalArgumentException
     */

    public int getCount(String raw) throws IOException, IllegalArgumentException {
        String value = raw;
//        value="08110812#16001800#Test1$08120813#17002000#Test2";
//        value="11#08110815#10001800#Test00$10#08110811#10001800#Test0$9#08110812#16001800#Test1$2#08120813#17002000#Test2$1#08100810#07002100#Test3";
        if (value == null) return 0;
        value = value.trim();
        if (value.contains("$")) {
            String[] array = value.split("\\$");
            return array.length;
        } else return 1;
    }

    // if input today calendar data, return yesterday data
    public String getYesterdayEms(String id, Calendar cal) { // id : Energy id(refer Items class)
        String value = "";
        cal.add(Calendar.DATE, -1);
        try {
            value = getHourlyValueOfMyHome(id, cal);
            Log.d(DEBUG_TAG, "getYesterdayEms :" + value);

        } catch (Exception e) {
            e.printStackTrace();
//            return 0.0;
        }
        return value;
    }

    // return today data
    public String getTodayEms(String id, Calendar cal) { // id : Energy id(refer Items class)
        String value = "";
        try {
            value = getHourlyValueOfMyHome(id, cal);
            Log.d(DEBUG_TAG, "getTodayEms :" + value);
        } catch (Exception e) {
            e.printStackTrace();
//            return 0.0;
        }
        return value;
    }


    // if input this month calendar data, return last month data
    public ArrayList<String> getLastMonthEms(Calendar cal) { // id : Energy id(refer Items class)
        ArrayList<String> value = new ArrayList<>();
        cal.add(Calendar.MONTH, -1);
        try {
            value = getMonthlyValueOfMyHome(cal);
            Log.d(DEBUG_TAG, "getLastMonthEms :" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    // if input this month calendar data, return this month data
    public ArrayList<String> getThisMonthEms(Calendar cal) {  // id : Energy id(refer Items class)
        ArrayList<String> value = new ArrayList<>();
        try {
            value = getMonthlyValueOfMyHome(cal);
            Log.d(DEBUG_TAG, "getThisMonthEMS :" + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    // EMS ���� �׸� ��û
    //
    // args
    // �����
    //
    // 1$2$
    //
    // returns
    // type: 1 => ����(Electricity), 2 => ����(Gas), 3 => ����(Water),
    // 4 => �¼�(HotWater), 5 => ����(Heating)
    public List<String> getSupports() throws IOException, IllegalArgumentException {
        ArrayList<String> ret = new ArrayList<String>();

        String value = SoapHelper.call(serverip, "getServiceList");
        if (value == null) {
            return ret;
        }
        value = value.trim();
        if (value.contains("$")) {
            String[] array = value.split("\\$");
            for (String string : array) {
                ret.add(string);
            }

        } else {
            ret.add(value);
        }

        return ret;
    }

    public List<String> getUnits() {

        ArrayList<String> ret = new ArrayList<>();

        try {
            String value = SoapHelper.call(serverip, "getMetering");
            if (value.endsWith(SYMBOL_DOLLAR)) {
                value = value.substring(0, value.length() - 1);
            }
            ret = split(value, SYMBOL_DOLLAR);
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }

    public String getUnit(String id) {

        String ret = "";
        ArrayList<String> unit_list = new ArrayList<String>();

        try {
            String value = SoapHelper.call(serverip, "getMetering");
            if (value.endsWith(SYMBOL_DOLLAR)) {
                value = value.substring(0, value.length() - 1);
            }
            unit_list = split(value, SYMBOL_DOLLAR);

        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        int index = (Integer.valueOf(id) - 1);
        return unit_list.get(index);
    }

    private ArrayList<String> split(String value, String symbol) {
        value = value.trim();
        ArrayList<String> ret = new ArrayList<String>();
        while (value.contains(symbol)) {
            try {
                String string = value.substring(0, value.indexOf(symbol));
                ret.add(string);
                value = value.substring(value.indexOf(symbol) + 1);
            } catch (Exception e) {
                //
            }
        }
        if (value.length() > 0) {
            ret.add(value);
        }
        return ret;
    }

    //
    // �ð��� ������ ��뷮 ��û call proc_get_hour_energy('000b4242aba2', 'ONC',
    // '101','102', 'Electricity', '20120501')
    //
    // Return
    // 2012050101#0.000$2012050102#1.000$2012050103#1.000$2012050104#1.000$
    // 2012050105#0.000$2012050106#1.000$2012050107#1.000$2012050108#1.000$
    //
    // 1 mac 2 sitecode 3 dong 4 ho 5 type 6 start
    //

    //use
    public String getHourlyValueOfMyHome(String id, String startDate,
                                         String endDate) throws IllegalArgumentException, IOException {
        String ret = "";

        try {
            String query = String
                    .format("call proc_get_hour_period_energy('%1$s', '%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
                            mac, sitecode, dong, ho, startDate, endDate);
            Log.d(DEBUG_TAG, "getHourlyValueOfMyHome q = " + query);
            ArrayList<String> totalValueOfSameHouseholds = convertValue(query, id);
            Log.d(DEBUG_TAG, "getHourlyValueOfMyHome totalValueOfSameHouseholds = " + totalValueOfSameHouseholds);

            int index = Items.ITEM_MAP.get(id).index;
            if (totalValueOfSameHouseholds.size() > index) {
                ret = totalValueOfSameHouseholds.get(index);
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ret;
        }
        return ret;
    }

    //use
    public String getHourlyValueOfMyHome(String id,
                                         Calendar cal) throws IllegalArgumentException, IOException {
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        StringBuilder builder = new StringBuilder();
        builder.append(cal.get(Calendar.YEAR)).append((month >= 10) ? "" : "0")
                .append(month).append((day >= 10) ? "" : "0").append(day)
                .append((hour >= 10) ? "" : "0").append(hour);
        String endDate = builder.toString();

        int firstHour = 1;

        StringBuilder builder2 = new StringBuilder();
        builder2.append(cal.get(Calendar.YEAR)).append((month >= 10) ? "" : "0")
                .append(month).append((day >= 10) ? "" : "0").append(day)
                .append((firstHour >= 10) ? "" : "0").append(firstHour);
        String startDate = builder2.toString();


        return getHourlyValueOfMyHome(id, startDate, endDate);
    }

    //use
    public ArrayList<String> getMonthlyValueOfMyHome(Calendar cal) throws IllegalArgumentException, IOException {
        String startDate = CalendarUtils.firstDateFormat(cal);

//        cal.add(Calendar.MONTH, - 1);
        String endDate = CalendarUtils.dateFormat(cal);
        ArrayList<String> ret = getMonthlyValueOfMyHome(startDate, endDate);
        return ret;
    }

    //use
    public ArrayList<String> getMonthlyValueOfMyHome(String startDate,
                                                     String endDate) throws IllegalArgumentException, IOException {
//        String ret = "";
        ArrayList<String> monthValues = new ArrayList<>();

        try {
            String query = String
                    .format("call proc_get_day_period_energy('%1$s', '%2$s', '%3$s', '%4$s', '%5$s', '%6$s')",
                            mac, sitecode, dong, ho, startDate, endDate);
            Log.d(DEBUG_TAG, "getMonthlyValueOfMyHome q = " + query);
            monthValues = convertValue(query, "");
            Log.d(DEBUG_TAG, "getMonthlyValueOfMyHome monthValues = " + monthValues);

//            int index = Items.ITEM_MAP.get(id).index;
//            if (monthValues.size() > index) {
//                ret = monthValues.get(index);
//                return ret;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return monthValues;
    }

    private ArrayList<String> valueParser(String value) {
        value = value.replace("$", "");
        ArrayList<String> list = new ArrayList<String>();
        while (value.contains(SYMBOL_SHARP)) {
            try {
                String string = value.substring(0, value.indexOf(SYMBOL_SHARP));
                list.add(string);
                value = value.substring(value.indexOf(SYMBOL_SHARP) + 1);
            } catch (Exception e) {
//
            }

        }
        if (value.length() > 0) {
            list.add(value);
        }

        return list;

    }

    // 20421.000#20273.000#20175.000#20533.000#20372.
    //use
    private ArrayList<String> convertValue(String query, String id) throws IllegalArgumentException, IOException {

        ArrayList<String> ret = new ArrayList<String>();
        String value = SoapHelper.call(serverip, query);

        if (value == null) {
            return ret;
        }
        value = value.trim();// �ʼ�. nullüũ ���� �ϰ� Ʈ���� ��

        // value="";
        // ret=valueParser(value, 0);//no
        // value="#";
        // ret=valueParser(value, 0);//no
        // value="###";
        // ret=valueParser(value, 0);//no
        // value="#$";
        // ret=valueParser(value, 0);//no
        // value ="20421.000";
        // ret=valueParser(value, 0);//yes
        // value ="20421.000#";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//no
        // value="20421.000#20273.000";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//yes
        // ret=valueParser(value, 2);//no
        // value="20421.000#20273.000#";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//yes
        // ret=valueParser(value, 2);//no
        // value="20421.000#20273.000$";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//yes
        // ret=valueParser(value, 2);//no
        // value="20421.000###20273.000";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//no
        // ret=valueParser(value, 2);//no
        // ret=valueParser(value, 3);//yes
        // ret=valueParser(value, 4);//no
        // value="20421.000###20273.000$";
        // ret=valueParser(value, 0);//yes
        // ret=valueParser(value, 1);//no
        // ret=valueParser(value, 2);//no
        // ret=valueParser(value, 3);//yes
        // ret=valueParser(value, 4);//no

        ret = valueParser(value);
        return ret;
    }
}
