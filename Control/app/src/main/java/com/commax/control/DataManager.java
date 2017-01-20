package com.commax.control;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.commax.control.Common.TypeDef;
import com.commax.pam.db.interfaces.MySQLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;

import static com.commax.control.Common.TypeDef.VIRTUAL_DEVICE_ROOTUUID;

public class DataManager {

    /* Tag define */
    static final String TAG = "DataManager";

    /* max try counter for db access : unit 100ms */
    static final int MYSQL_TRY_COUNT = 10; //default:50

    /* Member Variable */
    Context mContext;
    boolean mConnect= false;
    boolean mValidation= false;
    int auto_counter = 0;

    public DataManager(Context context){ mContext = context; }



    /********** MySQL API Design Start ************************************************************/

    /* Protocol Format (Hierarchical database)
       JSONObject -> command | object
       object     -> commaxDevice | rootDevice | rootUuid | subDevice
       subDevice  -> sort | subUuid | type | value | precision | scale | ...
     */

    // MySQL Open/Close *********************************************

    // Open MySQL
    // Ref : open()
    public boolean mysql_open(){
        boolean b_open=false;

        Log.d(TAG, "try to open MySQL");
        try {
            MySQLConnection.getInstance().getConnection(mContext.getContentResolver());

        }catch (Exception e){
            e.printStackTrace();
        }

        return b_open;
    }

    // Desc: Close MySQL
    // Ref : close()
    public boolean mysql_close(){
        boolean close_ok = false;
        Log.d(TAG, "try to close MySQL");
        try {
            close_ok= MySQLConnection.getInstance().close();  //MySQLConnection is MySQL
            MySQLConnection.getInstance().clearConnectionVariable();
        }catch (Exception e){
            e.printStackTrace();
            MySQLConnection.getInstance().clearConnectionVariable();
        }
        mConnect = false;
        mValidation = false;
        return close_ok;
    }

    // MySQL Object Access by item unit *********************************************

    // Desc: Check MySQL connection
    // Ref : isConnected()
    public boolean mysql_isConnected(){
        boolean connected= false;
        try {
            if (MySQLConnection.getInstance().getConnectionVariable() != null) connected= true;
            else connected= false;
        }catch (Exception e){
            //e.printStackTrace();
            connected= false;
        }
        mConnect = connected;
        return connected;
    }


    // Desc: get MySQL jObject counter From MySQL
    // Ref : getRootUuidCount()
    public int mysql_getjObjectCount() {
        int count = 0;
        String query = "Select COUNT(rootUuid) FROM rootDevice";

        try {
            PreparedStatement prep = MySQLConnection.getInstance().getConnection(mContext.getContentResolver()).prepareStatement(query);
            ResultSet e = prep.executeQuery();
            ResultSetMetaData rsmd = null;
            rsmd = e.getMetaData();

            int rowCnt1 = rsmd.getColumnCount();

            if(rowCnt1 != 0) {
                if(e.next()){
                    count = e.getInt(1);
                }
            }else {
                count = 0;
            }
            e.close();
            prep.close();
        } catch (SQLException var8) {
            var8.getMessage();
        }

        Log.d(TAG, "mysql_getRootUuidCount() = " + count);

        return count;
    }

    // Desc: get MySQL selected rootDevice counter From MySQL
    // Ref : getSwitchValueList()
    public int mysql_getSelRootDeviceCount(String rootDeviceName) {
        int count = 0;
        ArrayList<String> rootDeviceList = new ArrayList<>();

        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice를 찾아 RootUuid List를 얻는다
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            //Log.d(TAG, "rootDevice : " + rootDeviceName +  " => " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        if (rootDeviceList != null) {
            count = rootDeviceList.size();
        }
        Log.d(TAG, "The number of found " + rootDeviceName +  " is " + count);

        return count;
    }


    // Desc: get MySQL CommaxDevice From jObject
    // Ref : getCommaxDevice(String raw)
    public String mysql_getCommaxDevice(String str_jObject){
        String commaxResult = "";
        JSONObject jObject = null;

        try {                                           //object -> commaxDevice 인지 체크함
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ
                try {
                    commaxResult = (new JSONObject(object.trim())).getString("commaxDevice"); //TypeDef.CGP_KEY_OBJ_R_COMMAX_DEVICE
                    //Log.d(TAG, "commaxDevice : " + commaxResult);
                } catch (Exception e){
                    //e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return commaxResult;
    }

    // Desc: get MySQL RootDevice From jObject
    // Ref : getRootDevice(String raw)
    public String mysql_getRootDevice(String str_jObject){
        String rootResult = "";
        JSONObject jObject = null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ
                try {
                    rootResult = (new JSONObject(object.trim())).getString("rootDevice"); //TypeDef.CGP_KEY_OBJ_R_DEVICE
                    //Log.d(TAG, "RootDevice : " + rootResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return rootResult;
    }

    // Desc: get MySQL NickName by RootUuid
    // Ref : getIndexByRootUuid(String rootUuid)
    public String mysql_getNickName(String rootUuid) {
        String nickName = "";

        try {
            nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);
            //Log.d(TAG, "nickName : " + nickName);
        }catch (Exception e){
            e.printStackTrace();
        }

        return nickName;
    }

    // Desc: get MySQL RootUuid From jObject
    // Ref : getRootUuidFromRaw(String raw)
    public String mysql_getRootUuid(String str_jObject){
        JSONObject jObject=null;
        String rootUuid ="";

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ
                    try {
                        rootUuid = (new JSONObject(object.trim())).getString("rootUuid"); //TypeDef.CGP_KEY_OBJ_R_UUID
                        //Log.d(TAG, "RootUuid : " + rootUuid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return rootUuid;
    }

    // Desc: get MySQL subUuid From jObject
    // Ref : getSubUuidFromMatchingSubRaw(String deviceSort, String raw)
    public String mysql_getsubUuid(String str_jObject){
        String subUuid = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object");//TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");//TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "get subDevice info : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    subUuid = jsonArray.getJSONObject(i).getString("subUuid");//TypeDef.CGP_KEY_OBJ_S_DEVICE_S_UUID
                                    if(!TextUtils.isEmpty(subUuid)){
                                        //Log.d(TAG, "subDevice subUuid : " + subUuid);
                                        return subUuid;
                                    }
                                }
                            }catch (Exception e){
                                //e.printStackTrace();
                                //Log.d(TAG, "subObject retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    subUuid = subDevice.getString("subUuid");//TypeDef.CGP_KEY_OBJ_S_DEVICE_S_UUID
                                    //Log.d(TAG, "subDevice subUuid: " + subUuid);
                                }catch (Exception e1){
                                    //e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return subUuid;
    }

    // Desc: get all MySQL subUuid From jObject
    // Ref :
    public int mysql_getAllsubUuid(String str_jObject, String subUuid[]){
        int index = 0;
        JSONObject jObject = null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");//TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "get subDevice info : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    subUuid[index] = jsonArray.getJSONObject(i).getString("subUuid");//TypeDef.CGP_KEY_OBJ_S_DEVICE_S_UUID
                                    if(!TextUtils.isEmpty(subUuid[index])){
                                        //Log.d(TAG, "subDevice subUuid" + index + ": " + subUuid[index]);
                                        if(index < TypeDef.MAX_SUBDEV_CONTROLLER) index++;
                                        else break;
                                    }
                                }
                            }catch (Exception e){
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return index;
    }

    // Desc: get all MySQL item by user input From jObject
    // Ref :
    public int mysql_getAllOption(String str_jObject, String findlist[]){
        int index = 0;
        JSONObject jObject = null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice"); //TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "get subDevice info : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                //Log.d(TAG, "get jsonArray info : " + jsonArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        //Log.d(TAG, "get jsonArray.getJSONObject info : " + jsonArray.getJSONObject(i));
                                        findlist[0] = jsonArray.getJSONObject(i).getString("option1"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_OPTION_1
                                        findlist[1] = jsonArray.getJSONObject(i).getString("option2"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_OPTION_2
                                        if (!TextUtils.isEmpty(findlist[0])) {
                                            //Log.d(TAG, "subDevice option : " + findlist[0] + "-" + findlist[1]);
                                            break;
                                        }
                                    }catch (Exception e1) {
                                        //e1.printStackTrace();
                                    }
                                }
                            }catch (Exception e){
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return index;
    }

    // Desc: get MySQL value From jObject(jSubObject)
    // Ref : getDeviceStatus(String subUuid)
    public String mysql_getSubObject(String subUuid){
        String jSubObject = "";

        try {
            jSubObject = MySQLConnection.getInstance().getRawSubJson(subUuid);
        }catch (Exception e){
            e.printStackTrace();
        }

        return jSubObject;
    }

    // Desc: get MySQL sort From jObject(jSubObject)
    // Ref : getSortFromRaw(String raw)
    public String mysql_getSort(String str_jObject){
        String sort = "";
        JSONObject jObject=null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice"); //TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "subDevice : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    sort = jsonArray.getJSONObject(i).getString("sort"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_SORT
                                    if(!TextUtils.isEmpty(sort)){
                                        //Log.d(TAG, "subDevice sort: " + sort);
                                        return sort;
                                    }
                                }
                            } catch (JSONException e) {
                                //e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return sort;
    }

    // Desc: get MySQL value From jObject(jSubObject)
    // Ref : getStatusFromRaw(String sub_raw)
    public String mysql_getValue(String str_jObject){
        String value = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice"); //TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        value = jsonArray.getJSONObject(i).getString("value"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_VALUE
                                        if (!TextUtils.isEmpty(value)) {
                                            //Log.d(TAG, "subDevice value : " + value);
                                            return value;
                                        }
                                    }catch (Exception e1) {
                                        //e1.printStackTrace();
                                    }
                                }
                            }catch (Exception e){
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(subObject);
                                    value = jsonObject.getString("value");
                                }catch (Exception e1)
                                {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return value;
    }

    // Desc: get MySQL Precision From jObject(jSubObject)
    // Ref :
    public String mysql_getPrecision(String str_jObject){
        String value = "0";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice"); //TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                value = jsonArray.getJSONObject(0).getString("precision"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_PRECISION
                                if(TextUtils.isEmpty(value)) value = "0";
                                //Log.d(TAG, "subDevice precision: " + value);
                            }catch (Exception e){
                                //e.printStackTrace();
                                //Log.d(TAG, "subObject retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    value = subDevice.getString("precision"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_PRECISION
                                    //Log.d(TAG, "subDevice precision: " + value);
                                }catch (Exception e1){
                                    //e1.printStackTrace();
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return value;
    }

    // Desc: get MySQL Precision From jObject(jSubObject)
    // Ref :
    public String mysql_getScale(String str_jObject){
        String value = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice"); //TypeDef.CGP_KEY_OBJ_S_DEVICE
                        //Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                value = jsonArray.getJSONObject(0).getString("scale"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_SCALE
                                //Log.d(TAG, "subDevice scale: " + value);
                            }catch (Exception e){
                                //e.printStackTrace();
                                //Log.d(TAG, "subObject retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    value = subDevice.getString("scale"); //TypeDef.CGP_KEY_OBJ_S_DEVICE_SCALE
                                    //Log.d(TAG, "subDevice scale: " + value);
                                }catch (Exception e1){
                                    //e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(!TextUtils.isEmpty(value)){
                if (value.contains("[") || value.contains("]")) {
                    value = value.replace("[", "");
                    value = value.replace("]", "");
                }
                if (value.contains("\"")) {
                    value = value.replace("\"", "");
                }
            }
        }

        return value;
    }

    // Desc: get MySQL value From jObject(jSubObject)
    // Ref :
    public String mysql_getbatteryEvent(String str_jObject){
        String value = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object"); //TypeDef.CGP_KEY_OBJ
                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("batteryAttribute"); //TypeDef.CGP_KEY_BATTERY_ATTRIBUTE
                        //Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                value = jsonArray.getJSONObject(0).getString("batteryEvent"); //TypeDef.CGP_KEY_BATTERY_ATTRIBUTE_EVENT
                                //Log.d(TAG, "subDevice batteryEvent: " + value);
                            }catch (Exception e){
                                //e.printStackTrace();
                                //Log.d(TAG, "subObject retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    value = subDevice.getString("batteryEvent"); //TypeDef.CGP_KEY_BATTERY_ATTRIBUTE_EVENT
                                    //Log.d(TAG, "batteryEvent scale: " + value);
                                }catch (Exception e1){
                                    //e1.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            //e.printStackTrace();
        }

        return value;
    }

    // MySQL Object Access by group unit *********************************************

    // Desc: 원하는 RootDevice 를 MySQL DB 에서 쿼리하여 DeviceInfo List return
    // get MySQL selected rootDevice list From MySQL
    // Ref : getSwitchValueList()
    public ArrayList<DeviceInfo> mysql_getSelRootDeviceInfoList(String rootDeviceName){

        ArrayList<DeviceInfo> list_DeviceSortAndValues = new ArrayList<>();
        ArrayList<String> rootDeviceList = new ArrayList<>();

        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice를 찾아 RootUuid List를 얻는다
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            //Log.d(TAG, rootDeviceName +  " list : " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    //Log.d(TAG, "get(" + a + ") = " + deviceNumber);

                    if (deviceNumber != null) {
                        String raw = "";
                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(rootDeviceName, deviceNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Get DeviceInfo
                        DeviceInfo device_data = null;

                        String rootUuid = mysql_getRootUuid(raw);
                        String nickName = mysql_getNickName(rootUuid);
                        String rootDevice = mysql_getRootDevice(raw);
                        String CommaxDevice = mysql_getCommaxDevice(raw);
                        String subUuid = mysql_getsubUuid(raw);
                        String device_sort = mysql_getSort(raw);
                        //String subObject = mysql_getSubObject(subUuid);
                        //String ststus_value = mysql_getValue(subObject);
                        String ststus_value = mysql_getValue(raw);

                        //DeviceInfo device_data = getSmartPlugStatusByRaw(raw);
                        if ((!TextUtils.isEmpty(rootUuid))) {
                            device_data = new DeviceInfo(rootUuid, device_sort);
                            device_data.nickName = nickName;
                            device_data.addDeviceInfo(rootDevice, CommaxDevice);
                            device_data.setStatus(subUuid, ststus_value);
                            list_DeviceSortAndValues.add(device_data);
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(TAG, "Total list" + "(" + rootDeviceName + ") : " + list_DeviceSortAndValues.size());

        return list_DeviceSortAndValues;
    }

    public void setCardType(TypeDef.CardType cardtype)
    {
        // TODO: 2016-08-19
        Log.d(TAG, " => setCardType : " + cardtype);
    }

    // Desc: 원하는 RootDevice+subDevice 를 MySQL DB 에서 쿼리하여 SubDeviceInfo List return
    // get MySQL selected subDevice list From MySQL
    // Ref : getSwitchValueList()
    public ArrayList<DeviceInfo> mysql_getSelSubDeviceInfoList(String rootDeviceName, String CommaxDeviceName, TypeDef.CategoryType category){

        int count = 0;
        ArrayList<DeviceInfo> list_DeviceSortAndValues = new ArrayList<>();
        ArrayList<String> rootDeviceList = new ArrayList<>();

        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice를 찾아 RootUuid List를 얻는다
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            //Log.d(TAG, rootDeviceName +  " list : " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    //Log.d(TAG, "get(" + a + ") = " + deviceNumber);

                    if (deviceNumber != null) {
                        String raw = "";
                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(rootDeviceName, deviceNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Get SubDeviceInfo
                        DeviceInfo device_data = null;
                        String CommaxDevice = mysql_getCommaxDevice(raw);

                        //CommaxDevice에서 원하는 SubDevice 정보만 찾아냄
                        if(CommaxDevice.equalsIgnoreCase(CommaxDeviceName)) {
                            String rootUuid = mysql_getRootUuid(raw);
                            String nickName = mysql_getNickName(rootUuid);
                            String rootDevice = mysql_getRootDevice(raw);
                            String subUuid = mysql_getsubUuid(raw);
                            //String device_sort = mysql_getSort(raw); //from rootDevice
                            String batteryEvent = mysql_getbatteryEvent(raw); //todo

                            //read from subDevice info
                            String subObject = mysql_getSubObject(subUuid);
                            String device_sort = mysql_getSort(subObject); //from subDevice
                            String ststus_value = mysql_getValue(subObject);
                            String ststus_precision = mysql_getPrecision(subObject);

                            if ((!TextUtils.isEmpty(rootUuid))) {
                                device_data = new DeviceInfo(rootUuid, device_sort);
                                device_data.nickName = nickName;
                                device_data.addDeviceInfo(rootDevice, CommaxDevice);
                                device_data.setStatus(subUuid, ststus_value);
                                device_data.precision = ststus_precision;
                                device_data.batteryEvent = batteryEvent;
                                device_data.nCategory = category;
                                device_data.nTabID = mysql_getTabID(category);
                                device_data.nCardType = mysql_getcardType(rootDeviceName,CommaxDeviceName,category);
                                device_data.nLayoutID = auto_counter++;
                                device_data.groupID = mysql_getgroupID(CommaxDeviceName, category);
                                device_data.bUpdated = false;
                                device_data.bVirtualDevice = false;
                                device_data.bFavorite = checkFavoriteFile(subUuid);

                                //read from other subDevice info
                                String subUuidlist[] = new String[TypeDef.MAX_SUBDEV_CONTROLLER];
                                String option[] = new String[2];
                                mysql_getAllOption(raw, option);
                                device_data.option1 = option[0];
                                device_data.option2 = option[1];
                                device_data.scale = mysql_getScale(subObject);
                                count = mysql_getAllsubUuid(raw, subUuidlist);
                                device_data.other_subcount = count-1;
                                if(device_data.other_subcount > 0) {
                                    for (int k = 1; k < count; k++) {
                                        //Log.d(TAG, "subUuidlist" + k + ": " + subUuidlist[k]);
                                        subObject = mysql_getSubObject(subUuidlist[k]);
                                        device_data.other_subUuid[k-1] = subUuidlist[k];
                                        device_data.other_sort[k-1] = mysql_getSort(subObject);
                                        device_data.other_value[k-1] = mysql_getValue(subObject);
                                        device_data.other_precision[k-1] = mysql_getPrecision(subObject);
                                        device_data.other_scale[k-1] = mysql_getScale(subObject);
                                    }
                                }
                                //add list
                                list_DeviceSortAndValues.add(device_data);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //TODO 전등인 경우에만 적용 해야 하나?
        list_DeviceSortAndValues = DeviceItemsOrderBy(list_DeviceSortAndValues);

        Log.d(TAG, "Total list" + "(" + rootDeviceName +"," + CommaxDeviceName + ") : " + list_DeviceSortAndValues.size());

        return list_DeviceSortAndValues;
    }


    //2016-12-13 무선 4구전등 nickname 별 순차 정렬
    protected ArrayList<DeviceInfo> DeviceItemsOrderBy(ArrayList<DeviceInfo> arrayList) {
        ArrayList<DeviceInfo> arDeviceItems = new ArrayList<DeviceInfo>();
        while(arrayList.size() > 0) {
            if(arrayList.size() == 1) {
                arDeviceItems.add(arrayList.get(0));
                break;
            }
            else {
                int priorityIndex = 0;
                DeviceInfo deviceItem = arrayList.get(priorityIndex);

                for(int i=1; i<arrayList.size(); i++) {
                    int checkOrder = compare(deviceItem.getNickName(), arrayList.get(i).getNickName());
                    //Log.d(TAG, "checkOrder : " + checkOrder + " NickName : " + deviceItem.getRootDevice().getNickName());
                    if(checkOrder >= 0) {
                        priorityIndex = i;
                        deviceItem = arrayList.get(priorityIndex);
                    }
                }
                arDeviceItems.add(arrayList.get(priorityIndex));
                arrayList.remove(priorityIndex);
            }
        }
        return arDeviceItems;
    }

    //2016-12-13 string 별 순차 정렬
    protected int compare(String str1, String str2) {
        Collator.getInstance().setStrength(Collator.PRIMARY);
        return Collator.getInstance().compare(str1, str2);
    }

    // MySQL Utility *********************************************

    // Desc: MySQL 이 가용상태인지 체크
    // count 정보를 가져올 수 있다면 MySQL 이 연결된 상태로 간주하여 true
    // Ref : checkValidConnection()
    public boolean mysql_checkValidConnection(){

        try{
            int count = 0;
            count = mysql_getjObjectCount();

            if(count>0) {
                mValidation = true;
                return true;
            }else {
                mValidation = false;
                return false;
            }

        }catch (Exception e){
            //e.printStackTrace();
            mValidation = false;
            return false;
        }
    }

    // Desc: Screen에 표시할 cardType을 정의함
    // Ref :
    public TypeDef.CardType mysql_getcardType(String rootDeviceName, String CommaxDeviceName, TypeDef.CategoryType category){

        TypeDef.CardType catetype = TypeDef.CardType.eNone;

        switch(category) {
            case eNone:
                catetype = TypeDef.CardType.eNone;
                break;

            case eFavorite:
                // TODO: 2016-08-02
                break;

            case eLight:
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_MAINLIGHT)) {
                    //eMainSwitch와 eLightSwitch와 합쳐도 될것 같음
                    //catetype = TypeDef.CardType.eMainSwitch;
                    catetype = TypeDef.CardType.eLightSwitch;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_LIGHT)) {
                    catetype = TypeDef.CardType.eLightSwitch;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DIMMER) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_LIGHT)) {
                    catetype = TypeDef.CardType.eDimmerSwitch;
                }
                break;

            case eIndoorEnv: //eIndoorEnv Scan
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_THERMOSTAT) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_BOILER)) {
                    catetype = TypeDef.CardType.eBoiler;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_THERMOSTAT) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_AIRCON)) {
                    catetype = TypeDef.CardType.eAircon;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FAN)) {
                    catetype = TypeDef.CardType.eFan;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_THERMOSTAT) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU)) {
                    catetype = TypeDef.CardType.eFCU;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_CURTAIN)) {
                    catetype = TypeDef.CardType.eCurtain;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_DETECTSENSOR)) {
                    catetype = TypeDef.CardType.eDetectSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_WATERSENSOR)) {
                    catetype = TypeDef.CardType.eWaterSensor;
                }

                break;

            case eEnergy: //eEnergy Scan
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_PLUG) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_PLUG)) {
                    catetype = TypeDef.CardType.eSmartPlug;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_STANDBYPOWER)) {
                    catetype = TypeDef.CardType.eStandbyPower;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_METER) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_METER)) {
                    catetype = TypeDef.CardType.eSmartMeter;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_ELEVATOR)) {
                    catetype = TypeDef.CardType.eElevator; //임시(가상)
                }

                //TODO 2016-12-08 현재 스마트 플러그가 rootdevicename 이 switch 로 되어있는데 plug 로 바뀌어야 한다.
                //TODO test용
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_PLUG)) {
                    catetype = TypeDef.CardType.eSmartPlug;
                }
                break;

            case eSafe: //eSafe Scan
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_LOCK) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_GASLOCK)) {
                    catetype = TypeDef.CardType.eGasLock;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_GASSENSOR)) {
                    catetype = TypeDef.CardType.eGasSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_MOTIONSENSOR)) {
                    catetype = TypeDef.CardType.eMotionSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FIRESENSOR)) {
                    catetype = TypeDef.CardType.eFireSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_LOCK) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_DOORLOCK)) {
                    catetype = TypeDef.CardType.eDoorLock; //todo
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_DOORSENSOR)) {
                    catetype = TypeDef.CardType.eDoorSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_CONTACTSENSOR)) {
                    catetype = TypeDef.CardType.eDetectSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_SMOKESENSOR)) {
                    catetype = TypeDef.CardType.eSmokeSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_AWAYSWITCH)) {
                    catetype = TypeDef.CardType.eAwaySensor;
                }
                break;
            default:
                catetype = TypeDef.CardType.eNone;
                break;
        }

        return catetype;
    }

    // Desc: Screen에 표시할 TabID를 정의함
    // Ref :
    public int mysql_getTabID(TypeDef.CategoryType category) {
        int tabid;
        //Log.d(TAG, "category : " + category);
        switch (category) {
            case eFavorite: tabid = TypeDef.TAB_FAV; break;
            case eLight: tabid = TypeDef.TAB_LIGHR; break;
            case eIndoorEnv: tabid = TypeDef.TAB_INDOOR; break;
            case eEnergy: tabid = TypeDef.TAB_ENERGY; break;
            case eSafe: tabid = TypeDef.TAB_SAFE; break;
            default: tabid = 0; break;
        }
        return tabid;
    }

    // Desc: Virtual Device를 위한 GroupID를 정의함
    // Ref :
    public String mysql_getgroupID(String CommaxDeviceName, TypeDef.CategoryType category) {
        String groupid;

        switch (category) {
            case eLight:
                groupid = TypeDef.GroupID.eLight.value(); //default
                if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_MAINLIGHT)) {
                    groupid = TypeDef.GroupID.eMainSwitch.value(); //eMainSwitch는 group에서 제외함
                }
                break;
            case eIndoorEnv: // TODO: 2016-08-24 : 세분화 필요함
                groupid = TypeDef.GroupID.eThermostat.value(); //default

                if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_BOILER)) {
                    groupid = TypeDef.GroupID.eBoiler.value();
                }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_AIRCON)) {
                    groupid = TypeDef.GroupID.eAircon.value();
                } else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU)) {
                    groupid = TypeDef.GroupID.eFCU.value();
                }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FAN)) {
                    groupid = TypeDef.GroupID.eFan.value();
                }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_CURTAIN)) {
                    groupid = TypeDef.GroupID.eCurtain.value();
                }
                break;
            case eEnergy: // TODO: 2016-08-24 : 세분화 필요함
                groupid = TypeDef.GroupID.eEnergy.value(); //default
                if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_PLUG)) {
                    groupid = TypeDef.GroupID.eSmartPlug.value();
                }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_STANDBYPOWER)) {
                    groupid = TypeDef.GroupID.eStandbyPower.value();
                }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_METER)) {
                    groupid = TypeDef.GroupID.eSmartMetering.value();
                }
                break;
            case eSafe:
                groupid = TypeDef.GroupID.eSafe.value(); //default
                break;
            default:
                groupid = TypeDef.GroupID.eUnknown.value();
                break;
        }
        return groupid;
    }

    // Desc: Virtual DeviceInfo List return
    // Ref :
    public DeviceInfo mysql_getVirtualDeviceInfo(String nickName, String CommaxDeviceName, TypeDef.CategoryType category){
        DeviceInfo device_data;
        String rootUuid,subUuid, groupid;

        //Create subUuid : subUuid + groupID (36Byte + 4byte = 40byte)
        groupid = mysql_getgroupID(CommaxDeviceName, category);
        rootUuid = VIRTUAL_DEVICE_ROOTUUID + groupid; //Virtual device 구분위함
        subUuid = TypeDef.VIRTUAL_DEVICE_SUBUUID + groupid; //Virtual device 구분위함
        //Log.d(TAG, "Created subUuid: " + subUuid);

        device_data = new DeviceInfo(VIRTUAL_DEVICE_ROOTUUID, TypeDef.SUB_DEVICE_VIRTUAL);
        device_data.nickName = nickName;
        device_data.addDeviceInfo(TypeDef.ROOT_DEVICE_VIRTUAL, CommaxDeviceName);
        device_data.rootUuid = rootUuid;
        device_data.subUuid = subUuid;
        device_data.value = TypeDef.SWITCH_OFF;
        device_data.nCategory = category;
        device_data.nTabID = mysql_getTabID(category);
        // TODO: 2016-08-31
        if( CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_AIRCON)
                || CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_BOILER)
                || CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU))
        {
            //device_data.nCardType = TypeDef.CardType.eThermostat;
            device_data.nCardType = TypeDef.CardType.eMainSwitch;
        }else if(CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_METER)) {
            device_data.nCardType = TypeDef.CardType.eSmartMeter;
        }else {
            device_data.nCardType = TypeDef.CardType.eMainSwitch;
        }
        device_data.nLayoutID = auto_counter++;
        device_data.groupID = groupid;
        device_data.bUpdated = false;
        device_data.bValidate = false;
        device_data.bVirtualDevice = true; //Virtual device 구분위함
        device_data.bFavorite = checkFavoriteFile(subUuid);
        device_data.other_subcount = 0;

        return device_data;
    }

    /********** MySQL API Design End****************************************************************/


    /********** AIDL API Design Start****************************************************************/
    public void setOnOrOffAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        boolean bControl = false;

        try {

            //Check condition
            if (rootDevice.equalsIgnoreCase(TypeDef.ROOT_DEVICE_LOCK)) {
                if (value.equalsIgnoreCase(TypeDef.SWITCH_LOCK))
                    bControl = true;
                else {
                    bControl = true; //for test
                    //MainActivity.getInstance().mToast.setText(mContext.getString(R.string.only_locking));
                    //MainActivity.getInstance().mToast.show();
                }
            }else {
                bControl = true;
            }

            //Do Control
            if(bControl){
                //Toast.makeText(mContext, mContext.getString(R.string.working), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "setOn : rootUuid = " + rootUuid + " / rootDevice : " + rootDevice + " / subUuid : " + subUuid + " / value : " + value);
                if ((rootUuid != null) && (rootDevice != null) && (subUuid != null) && (value != null)) {
                    if ((!TextUtils.isEmpty(rootUuid)) && (!TextUtils.isEmpty(rootDevice)) && (!TextUtils.isEmpty(subUuid)) && (!TextUtils.isEmpty(value))) {
                        MainActivity.getInstance().sendCommand(JsonStringAddSort(rootUuid, rootDevice, subUuid, value, sort));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //for test: doorlock
    //if(BuyerID.BUYER_ID_DEF == BuyerID.BUYER_ID_CANADA_TRIDEL)
    public String makeJsonObject_Dummy(int cmdtype){

        String result = "";

        try{

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("command", "ddlReport");
                switch(cmdtype) {
                    case 0:
                        jsonObject.put("deviceStatus", "add");
                        break;
                    case 1:
                        jsonObject.put("doorStatus", "open");
                        break;
                    case 2:
                        jsonObject.put("doorStatus", "close");
                        break;
                    case 3:
                        jsonObject.put("battStatus", "low");
                        break;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            result = jsonObject.toString();
            //Log.d(TAG, "json : " + result);

            MainActivity.getInstance().sendCommand(result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    // command 구문 생성
    private String JsonStringAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        String result = "";

        try {
            String cmd = "set";
            String[] object = new String[2];
            object[0] = rootUuid;
            object[1] = rootDevice;

            String[] subDevice = new String[4];
            subDevice[0] = subUuid;
            subDevice[1] = "set";
            subDevice[2] = value;
            subDevice[3] = sort;

            try {
                result = makeJsonAddSort(cmd, object, subDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(TAG , "result : " + result);
        return result;
    }

    private String makeJsonAddSort(String str, String[] str2, String[] str3){

        String result = "";

        try{

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("command", str);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONObject subDeviceObject = new JSONObject();
            try {
                subDeviceObject.put("subUuid", str3[0]);
                subDeviceObject.put("funcCommand", str3[1]);
                subDeviceObject.put("value", str3[2]);
                subDeviceObject.put("sort", str3[3]);
            }catch (Exception e){
                e.printStackTrace();
            }

            JSONArray subDeviceArray = new JSONArray();
            try {
                subDeviceArray.put(subDeviceObject);
            }catch (Exception e){
                e.printStackTrace();
            }

            //object array

            JSONObject object = new JSONObject();
            try {
                object.put("rootUuid", str2[0]);
                object.put("rootDevice", str2[1]);
                object.put("subDevice", subDeviceArray);
            }catch (Exception e){
                e.printStackTrace();
            }

            try{
                jsonObject.put("object",object);
            }catch (Exception e){
                e.printStackTrace();
            }


            result = jsonObject.toString();
            //Log.d(TAG, "json : " + result);

        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }
    /********** AIDL API Design End****************************************************************/

    /********** SharedPreferences API Design Start*************************************************/
    public static final String FAVORITE_SHARED_FILENAME = "favoriteList";
    private SharedPreferences mFavPref;
    private SharedPreferences.Editor editor;

    public boolean initFavoriteFile(){
        boolean bresult = false;

        //MODE_PRIVATE: allow all other applications to have read access to the created file.
        mFavPref = mContext.getSharedPreferences(FAVORITE_SHARED_FILENAME, Context.MODE_PRIVATE);
        if (mFavPref != null) bresult = true;

        Log.d(TAG, "initFavoriteFile " + bresult);

        return bresult;
    }

    public void addFavoriteFile(String subUuid, String nickName){
        Log.d(TAG, "addFavoriteFile : " + subUuid + "-" + nickName);

        try {
            if (mFavPref != null) {
                editor = mFavPref.edit();
                editor.putString(subUuid, nickName);
                editor.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        syncFavoriteFile();
    }

    public boolean checkFavoriteFile(String subUuid) {
        boolean bresult = false;
        try {
            if (mFavPref != null) {
                if (mFavPref.contains(subUuid)) {
                    String nickName = mFavPref.getString(subUuid, "");
                    Log.d(TAG, "getFavoriteFile : " + subUuid + "-" + nickName);
                    if (!TextUtils.isEmpty(nickName)) bresult = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return bresult;
    }

    public String getFavoriteFile(String subUuid) {
        String nickName = "";
        try {
            if (mFavPref != null) {
                if (mFavPref.contains(subUuid)) {
                    nickName = mFavPref.getString(subUuid, "");
                    Log.d(TAG, "getFavoriteFile : " + subUuid + "-" + nickName);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return nickName;
    }

    public void deleteFavoriteFile(String subUuid, boolean bAllData) {

        Log.d(TAG, "deleteFavoriteFile : " + subUuid + "-" + bAllData);
        try {
            if (mFavPref != null) {
                editor = mFavPref.edit();
                if(bAllData) editor.clear();
                else         editor.remove(subUuid);
                editor.commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        syncFavoriteFile();
    }

    public void syncFavoriteFile() {

        //특정 OS 버전에서 파일이 제대로 저장되지 않는 경우 발생. sync 명령을 줘야 제대로 파일에 저장됨.(from hong)
        try {
            Process process = Runtime.getRuntime().exec("sync");
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /********** SharedPreferences API Design End****************************************************/
}
