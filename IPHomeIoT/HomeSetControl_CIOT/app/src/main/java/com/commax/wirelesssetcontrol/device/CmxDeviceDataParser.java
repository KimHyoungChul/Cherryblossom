package com.commax.wirelesssetcontrol.device;

import android.content.Context;
import android.text.TextUtils;

import com.commax.pam.db.interfaces.MySQLConnection;
import com.commax.wirelesssetcontrol.CommaxDevice;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.RootDevice;
import com.commax.wirelesssetcontrol.Status;
import com.commax.wirelesssetcontrol.data.PageData;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 정리필요
 */

public class CmxDeviceDataParser {
    private static final String TAG = "CmxDeviceDataParser";

    public boolean checkValidConnection(){

        try{

            int count = 0;
            count = getRootUuidCount();
            Log.d(TAG, "checkValidConnection");

            if(count>0) {
                return true;
            }else {
                return false;
            }


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
//        return true;
    }

    public int getRootUuidCount() {
        int count = 0;

        try {
            if (MySQLConnection.getInstance()!=null) {
                count = MySQLConnection.getInstance().getDeviceCount(RootDevice.SWITCH);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        String query = "Select COUNT(rootUuid) FROM rootDevice";
//
//        try {
//            PreparedStatement prep = MySQLConnection.getInstance().getConnectionVariable().prepareStatement(query);
//            ResultSet e = prep.executeQuery();
//            ResultSetMetaData rsmd = null;
//            rsmd = e.getMetaData();
//
//            int rowCnt1 = rsmd.getColumnCount();
//
//            if(rowCnt1 != 0) {
//                if(e.next()){
//                    count = e.getInt(1);
//                }
//            }
//            e.close();
//            prep.close();
//        } catch (SQLException var8) {
//            var8.getMessage();
//        }

        return count;
    }

    public String getDeviceStatus(String subUuid){
        String sub_raw = "";
        String status="";

        try {
            sub_raw = MySQLConnection.getInstance().getRawSubJson(subUuid);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            status = getStatusFromRaw(sub_raw);
        }catch (Exception e){
            status= Status.UNKNOWN;
            e.printStackTrace();
        }

        return status;
    }

    public SubDevice getSubDeviceStatus(String subUuid, String subSort){
        String sub_raw = "";
        SubDevice subDevice = new SubDevice();

        try {
            sub_raw = MySQLConnection.getInstance().getRawSubJson(subUuid);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            subDevice = getSubDeviceStatusFromRaw(sub_raw, subUuid, subSort);
        }catch (Exception e){
            subDevice=null;
            e.printStackTrace();
        }

        return subDevice;
    }


    public SubDevice getSubDeviceStatusFromRaw(String sub_raw, String subUuid, String subSort){

        SubDevice subDevice = new SubDevice();
        String status = "";
        String option1 = "";
        String option2 = "";
        String scale = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(sub_raw)) {
                jObject = new JSONObject(sub_raw.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
//                        Log.d(TAG, "getSubDeviceStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    status = jsonArray.getJSONObject(i).getString("value");

                                    try{
                                        option1 = jsonArray.getJSONObject(i).getString("option1");
                                        option2 = jsonArray.getJSONObject(i).getString("option2");
                                    }catch (Exception e){
                                        e.getMessage();
                                    }

                                    try{
                                        String str_scale = jsonArray.getJSONObject(i).getString("scale");
                                        JSONArray arr_scale = new JSONArray(str_scale.trim());
                                        scale = arr_scale.get(0).toString();
                                    }catch (Exception e){

                                    }

                                    try{
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER)){
                                            String precision = jsonArray.getJSONObject(i).getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(TAG, "getAccurateValue : "+status);
                                        }
                                    }catch (Exception e){

                                    }
                                    Log.d(TAG, "getSubDeviceStatusFromRaw status : " + status);
                                }
                            }catch (Exception e){
                                e.printStackTrace();

//                                Log.d(TAG, "getStatusFromRaw get status retry");
                                try{
                                    JSONObject subDeviceObj = (new JSONObject(subObject.trim()));
                                    status = subDeviceObj.getString("value");

                                    try{
                                        option1 = subDeviceObj.getString("option1");
                                        option2 = subDeviceObj.getString("option2");
                                    }catch (Exception e1){
                                        e1.getMessage();
                                    }

                                    try{
                                        String str_scale = subDeviceObj.getString("scale");
                                        JSONArray arr_scale = new JSONArray(str_scale.trim());
                                        scale = arr_scale.get(0).toString();
                                    }catch (Exception e1){

                                    }

                                    try{
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER)){
                                            String precision = subDeviceObj.getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(TAG, "getAccurateValue : "+status);
                                        }
                                    }catch (Exception e2){

                                    }
                                    Log.d(TAG, "getSubDeviceStatusFromRaw status retry : " + status);
                                }catch (Exception e1){
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }else {
                Log.d(TAG, "getSubDeviceStatusFromRaw sub_raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getSubDeviceStatusFromRaw get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getSubDeviceStatusFromRaw get Json Object failed2");
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
                Log.d(TAG, "getSubDeviceStatusFromRaw : " + subSort + " / status " + status + " saved");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return subDevice;
    }

    public boolean isAlreadyPlaced(Context context, String rootUuid){
        boolean placed = false;

        try{
            for (int count = 0; count< PageDataManager.getInst().getPageSize(); count++) {
                PageData pageData = PageDataManager.getInst().getPageData(count);
                if (pageData == null)
                    return false;

                String jsonStr = pageData.iconData;

                if (jsonStr != null && jsonStr.length() > 0) {
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(jsonStr);
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            JSONObject obj = array.getJSONObject(i);
                            IconData data = IconDataParser.getIconDataForJson(context, obj);
                            if (data instanceof DeviceIconData) {
                                DeviceIconData deviceIconData = (DeviceIconData) data;
                                if (deviceIconData.getRootUuid().equalsIgnoreCase(rootUuid)){
                                    return true;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //장치 종류별 목록 조회
    public ArrayList<DeviceInfo> getDeviceInfo(Context context, String rootDevice, boolean alradyPlacedCheck){
        ArrayList<DeviceInfo> listDeviceInfo = new ArrayList<>();
        ArrayList<String> switchCount = new ArrayList<>();

        try {
            switchCount = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDevice);
            Log.d(TAG, "get"+rootDevice+"Count : " + switchCount);
        }catch (Exception e){
            Log.d(TAG, "getDeviceInfo : " + e.getMessage());
        }

        try {
            if (switchCount != null) {
                for (int i = 0; i < switchCount.size(); i++) {
                    String deviceNumber = switchCount.get(i);

                    if (deviceNumber != null) {
                        String raw = "";

                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(rootDevice, deviceNumber);
                        } catch (Exception e) {
                            Log.d(TAG, ">> getDevicePropertybyRootUuid : " + e.getMessage());
                        }

                        DeviceInfo deviceData = null;

                        if((rootDevice.equalsIgnoreCase(RootDevice.SWITCH))||(rootDevice.equalsIgnoreCase(CommaxDevice.DIMMER))){
                            deviceData = getSwitchStatusByRaw(raw);
                        } else if(rootDevice.equalsIgnoreCase(RootDevice.BOILER)){
                            deviceData = getThermostatStatusByRaw(raw);
                        } else if(rootDevice.equalsIgnoreCase(RootDevice.DETECT_SENSORS)){
//                            deviceData = getSensorStatusByRaw(raw);
                        }  else if(rootDevice.equalsIgnoreCase(RootDevice.LOCK)){
                            deviceData = getLockStatusByRaw(raw);
                        }

                        if (deviceData != null) {
                            if(!alradyPlacedCheck || !isAlreadyPlaced(context, deviceData.rootUuid))
                                listDeviceInfo.add(deviceData);
                        }
                    }
                }
            } else {
                Log.d(TAG, "get"+rootDevice+"Count : null");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return listDeviceInfo;
    }

    public DeviceIconData getStatusByDeviceIconDate(DeviceIconData data){
        try{

            DeviceIconData returnData = data;
            boolean validRootUuid = false;

            String rootDevice = getRootDeviceByDeviceType(data.getDeviceType());

            validRootUuid = checkValidRootUuid(rootDevice, data.getRootUuid());

            Log.d(TAG, "validRootUuid "+validRootUuid);

            if (validRootUuid) {
                String status = getDeviceStatus(data.getMain_subUuid());
                returnData.setStatus(status);
                try {
                    String nickName = MySQLConnection.getInstance().getDeviceNickName(data.getRootUuid());
                    returnData.setNickName(nickName);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return returnData;
            }

            return null;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getRootDeviceByDeviceType(String commaxDevice){
        String rootDevice = "";

        try{
            if(!TextUtils.isEmpty(commaxDevice)) {
                Log.d(TAG, "getRootDeviceByDeviceType commaxDevice "+commaxDevice);
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.LIGHT)) {
                    rootDevice = RootDevice.SWITCH;
                }else if(commaxDevice.equalsIgnoreCase(CommaxDevice.SMART_PLUG)){
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.MAIN_SWITCH)) {
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.AWAY_SWITCH)) {
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.FAN_SYSTEM)) {
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.CURTAIN)) {
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.STANDBY_POWER)) {
                    rootDevice = RootDevice.SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)) {
                    rootDevice = RootDevice.BOILER;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.GAS_LOCK)) {
                    rootDevice = RootDevice.LOCK;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.GAS_LOCK)) {
                    rootDevice = RootDevice.LOCK;
                } else {
                    rootDevice = RootDevice.SWITCH;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return rootDevice;
    }

    public boolean checkValidRootUuid(String rootDevice, String rootUuid){

        boolean hasThis = false;

        try{

            ArrayList<String> switchCount = new ArrayList<>();

            try {
                //PowerSwitch(SmartPlug or readyPower)
                android.util.Log.d(TAG, "checkValidRootUuid rootDevice "+rootDevice);
                switchCount = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDevice);
                Log.d(TAG, "get"+rootDevice+"Count : " + switchCount);
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                if (switchCount != null) {
                    for (int a = 0; a < switchCount.size(); a++) {

                        String deviceNumber = switchCount.get(a);
//                        Log.d(TAG, "get" + rootDevice + "Count : get(" + a + ") = " + deviceNumber);

                        if (deviceNumber != null) {
                            if (deviceNumber.equalsIgnoreCase(rootUuid)){
                                hasThis=true;
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hasThis;
    }

    public DeviceInfo getLockStatusByRaw(String raw){
        DeviceInfo device_data = new DeviceInfo();

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType =SubSort.GAS_LOCK;
            String commaxDevice = getCommaxDevice(raw);
            int subDeviceCount = getSubCount(raw);
            device_data.subCount = subDeviceCount;

            if(!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.GAS_LOCK)) {
                    device_sort = CommaxDevice.GAS_LOCK;
                }
            }

            Log.d(TAG, "getLockStatusByRw : commax " + commaxDevice + " subCount "+subDeviceCount);

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


                        Log.d(TAG, "getLockStatusByRw : status " + status + " saved");
                    }
                }

            } else {
                Log.d(TAG, "getLockStatusByRw : no device_sort");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return device_data;
    }

    public DeviceInfo getSwitchStatusByRaw(String raw){
        DeviceInfo device_data = new DeviceInfo();
//        Log.d(TAG, "getSwitchStatusByRaw : raw : " + raw);

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType = SubSort.SWITCH_BINARY;
            String rootDevice = "";
            String commaxDevice = getCommaxDevice(raw);
            int subDeviceCount = getSubCount(raw);
            device_data.subCount = subDeviceCount;

            if(!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.LIGHT)) {
                    device_sort = CommaxDevice.LIGHT;
                }else if(commaxDevice.equalsIgnoreCase(CommaxDevice.SMART_PLUG)){
                    device_sort = CommaxDevice.SMART_PLUG;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.MAIN_SWITCH)) {
                    device_sort = CommaxDevice.MAIN_SWITCH;
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.AWAY_SWITCH)) {
                    return null;
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

//            if (TextUtils.isEmpty(device_sort)) {
//                String rootDevice = getRootDevice(raw);
//                if (!TextUtils.isEmpty(rootDevice)) {
//                    if (rootDevice.equalsIgnoreCase(RootDevice.SWITCH)) {
//                        device_sort = RootDevice.SWITCH;
//                    }
//                }
//
//            }

            try{
                rootDevice = getRootDevice(raw);
            }catch (Exception e){
                e.printStackTrace();
            }

            Log.d(TAG, "getSwitchStatusByRaw : commax " + commaxDevice + " subCount "+subDeviceCount);

            if (!TextUtils.isEmpty(device_sort)) {

                subUuid = getSubUuidFromMatchingSubDevice(controlType, raw);   //if it is null, this is not meter device

                if (!TextUtils.isEmpty(subUuid)) {
                    rootUuid = getRootUuidFromRaw(raw);

                    String status = getDeviceStatus(subUuid);
                    String nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);
                    Log.d(TAG, "getSwitchStatusByRaw : status before " + status + " saved");

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        device_data.mainDevice = new SubDevice();
                        device_data.mainDevice.subUuid = subUuid;
                        device_data.mainDevice.value = status;
                        device_data.mainDevice.sort = controlType;
                        device_data.nickName = nickName;

                        device_data.initDevice(rootUuid, device_sort, controlType, subUuid, device_data.mainDevice);


                        Log.d(TAG, "getSwitchStatusByRaw : status " + status + " saved");
                    }
                }

                //add details

                if(commaxDevice.equalsIgnoreCase(CommaxDevice.STANDBY_POWER)
                        ||commaxDevice.equalsIgnoreCase(CommaxDevice.FAN_SYSTEM)){

                    subUuid = getSubUuidFromMatchingSubDevice(SubSort.MODE_BINARY, raw);   //if it is null, this is not meter device

                    if (!TextUtils.isEmpty(subUuid)) {
                        rootUuid = getRootUuidFromRaw(raw);

                        String status = getDeviceStatus(subUuid);

                        if ((!TextUtils.isEmpty(rootUuid))) {

                            SubDevice subDevice = new SubDevice();
                            subDevice.subUuid = subUuid;
                            subDevice.sort = SubSort.MODE_BINARY;
                            subDevice.value = status;

                            device_data.subDevices = new ArrayList<>();
                            device_data.subDevices.add(subDevice);

                            Log.d(TAG, "getSwitchStatusByRaw : sub device status " + status + " saved");
                        }
                    }

                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.LIGHT)){

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        if (rootDevice.equalsIgnoreCase(RootDevice.DIMMER)) {
                            device_data.subDevices = new ArrayList<>();

                            SubDevice subDevice = new SubDevice();
                            subDevice = getSubDevice(rootUuid, raw, SubSort.SWITCH_DIMMER);
                            device_data.subDevices.add(subDevice);
                        }
                    }
                } else if (commaxDevice.equalsIgnoreCase(CommaxDevice.SMART_PLUG)){

                    if ((!TextUtils.isEmpty(rootUuid))) {

                        if (rootDevice.equalsIgnoreCase(RootDevice.SWITCH)) {
                            device_data.subDevices = new ArrayList<>();

                            SubDevice subDevice = new SubDevice();
                            subDevice = getSubDevice(rootUuid, raw, SubSort.ELECTRIC_METER);
                            device_data.subDevices.add(subDevice);
                        }
                    }
                }
            } else {
                Log.d(TAG, "getSwitchStatusByRaw : no device_sort");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return device_data;
    }


    public DeviceInfo getThermostatStatusByRaw(String raw){
        DeviceInfo device_data = new DeviceInfo();
//        Log.d(TAG, "getThermostatStatusByRaw : raw : " + raw);

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType =SubSort.THERMOSTAT_MODE;
            String commaxDevice = getCommaxDevice(raw);
            int subDeviceCount = getSubCount(raw);
            device_data.subCount = subDeviceCount;

            if(!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)) {
                    device_sort = CommaxDevice.FCU;
                }
            }

//            if (TextUtils.isEmpty(device_sort)) {
//                String rootDevice = getRootDevice(raw);
//                if (!TextUtils.isEmpty(rootDevice)) {
//                    if (rootDevice.equalsIgnoreCase(RootDevice.BOILER)) {
//                        device_sort = RootDevice.BOILER;
//                    }
//                }
//
//            }

            Log.d(TAG, "getThermostatStatusByRaw : commax " + commaxDevice + " subCount "+subDeviceCount);

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


                        Log.d(TAG, "getSwitchStatusByRaw : status " + status + " saved");
                    }
                }

                //add details

                if(commaxDevice.equalsIgnoreCase(CommaxDevice.FCU)){

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
                Log.d(TAG, "getSwitchStatusByRaw : no device_sort");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return device_data;
    }

    public DeviceInfo getSensorStatusByRaw(String raw){
        DeviceInfo device_data = new DeviceInfo();

        try {
            String device_sort = "", rootUuid = "", subUuid = "", controlType =SubSort.NONE;
            String commaxDevice = getCommaxDevice(raw);
            int subDeviceCount = getSubCount(raw);
            device_data.subCount = subDeviceCount;

            if(!TextUtils.isEmpty(commaxDevice)) {
                //contain commaxDevice field
                if (commaxDevice.equalsIgnoreCase(CommaxDevice.DETECT_SENSORS)) {
                    device_sort = CommaxDevice.DETECT_SENSORS;
                }
            }

            Log.d(TAG, "getSensorStatusByRaw : commax " + commaxDevice + " subCount "+subDeviceCount);

            rootUuid = getRootUuidFromRaw(raw);

            if ((!TextUtils.isEmpty(rootUuid))) {

                device_data.mainDevice = new SubDevice();
                device_data.nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);

                device_data.initDevice(rootUuid, device_sort, controlType, subUuid, device_data.mainDevice);
            }

            if (!TextUtils.isEmpty(device_sort)) {

                //add details

                if(commaxDevice.equalsIgnoreCase(CommaxDevice.DETECT_SENSORS)){

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
                Log.d(TAG, "getSensorStatusByRaw : no device_sort");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return device_data;
    }

    private SubDevice getSubDevice(String rootUuid, String raw, String subSort){

        SubDevice subDevice = new SubDevice();

        try {
            String subUuid = getSubUuidFromMatchingSubDevice(subSort, raw);   //if it is null, this is not meter device

            if (!TextUtils.isEmpty(subUuid)) {

                subDevice = getSubDeviceStatus(subUuid, subSort);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return subDevice;
    }

    public String getCommaxDevice(String raw){
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
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d("UsageData", "get commaxDevice Object failed2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed2");
        }

        return commaxResult;
    }

    private int getSubCount(String raw){
        int subCount = 0;
        String subDevice = "";

        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());
                String object = jObject.getString("object");
                try {
                    subDevice = (new JSONObject(object.trim())).getString(NameSpace.SUB_DEVICE);

                    JSONArray jsonArray = new JSONArray(subDevice);
                    subCount = jsonArray.length();
//                    Log.d(TAG, "getSubCount subCount : " + subCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("UsageData", "get commaxDevice failed");
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d("UsageData", "get commaxDevice Object failed2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d("UsageData", "get Json Object failed2");
        }

        return subCount;
    }


    private String getRootDevice(String raw){
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
                    Log.d(TAG, "get rootResult failed");
                } catch (Exception e){
                    e.printStackTrace();
                    Log.d(TAG, "get rootResult Object failed2");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "get Json Object failed2");
        }

        return rootResult;
    }

    public String getSubUuidFromMatchingSubDevice(String deviceSort, String raw){
        String subUuid = "";

        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        if(subObject!=null) {
                            JSONArray jsonArray = (new JSONArray(subObject.trim()));
                            for(int i=0;i<jsonArray.length();i++) {
                                String sort = jsonArray.getJSONObject(i).getString("sort");
                                if(sort.equalsIgnoreCase(deviceSort)){
                                    subUuid = jsonArray.getJSONObject(i).getString("subUuid");
                                }
                            }
                        }
                    }
                }
            }else {
                Log.d(TAG, "getMatchingSubDevice get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getMatchingSubDevice get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getMatchingSubDevice get Json Object failed2");
        }
        return subUuid;
    }

    public static String getRootUuidFromRaw(String raw){
//        Log.d(TAG, "getRootUuidFromRaw : "+raw);

        JSONObject jObject=null;
        String rootUuid ="";

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object");

                    try {
                        rootUuid = (new JSONObject(object.trim())).getString("rootUuid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                Log.d(TAG, "getRootUuidFromRaw get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getRootUuidFromRaw get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getRootUuidFromRaw get Json Object failed2");
        }
        return rootUuid;
    }

    public static String getSortFromRaw(String raw){

        String result = "String";
        JSONObject jObject=null;

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        android.util.Log.d(TAG, "getSortFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            JSONArray jsonArray = (new JSONArray(subObject.trim()));

                            String sort = jsonArray.getJSONObject(0).getString("sort");
                            if(!TextUtils.isEmpty(sort))    return sort;
                        }
                    }
                }
            }else {
                Log.d(TAG, "getSortFromRaw get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getSortFromRaw get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getSortFromRaw get Json Object failed2");
        }

        return result;
    }

    public static String getStatusFromRaw(String sub_raw){
        String status = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(sub_raw)) {
                jObject = new JSONObject(sub_raw.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
//                        Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    status = jsonArray.getJSONObject(i).getString("value");

                                    try{
                                        String subSort = jsonArray.getJSONObject(i).getString("sort");
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER)){
                                            String precision = jsonArray.getJSONObject(i).getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(TAG, "getAccurateValue : "+status);
                                        }
                                    }catch (Exception e2){

                                    }
                                    Log.d(TAG, "getStatusFromRaw status : " + status);
                                }
                            }catch (Exception e){
                                e.getMessage();

//                                Log.d(TAG, "getStatusFromRaw get status retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    status = subDevice.getString("value");

                                    try{
                                        String subSort = subDevice.getString("sort");
                                        if (subSort.equalsIgnoreCase(SubSort.ELECTRIC_METER)){
                                            String precision = subDevice.getString("precision");
                                            status = getAccurateValue(status, precision);
                                            Log.d(TAG, "getAccurateValue : "+status);
                                        }
                                    }catch (Exception e2){

                                    }
                                    Log.d(TAG, "getStatusFromRaw status retry : " + status);
                                }catch (Exception e1){
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }else {
                Log.d(TAG, "getStatusFromRaw sub_raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getStatusFromRaw get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getStatusFromRaw get Json Object failed2");
        }

        return status;
    }

    private static String getAccurateValue(String value, String precision){        //TODO 검증 필요
        String val = value;
        String left="", right="", result="";
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

                        if (right.length()==1){
                            right = right.substring(0, 1);
                        }else if (right.length()>=2){
                            right = right.substring(0, 2);
                        }
                        result = "0." + right;
                    } else {
                        left = val.substring(0, length - (Integer.parseInt(precision)));
                        right = val.substring(length - (Integer.parseInt(precision)));
                        if (right.length()==1){
                            right = right.substring(0, 1);
                        }else if (right.length()>=2){
                            right = right.substring(0, 2);
                        }
                        result = left + "." + right;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        Log.d("getAccurateValue", ""+result);
        return result;
    }

}
