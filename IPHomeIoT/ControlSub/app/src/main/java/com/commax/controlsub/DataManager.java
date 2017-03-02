package com.commax.controlsub;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.commax.controlsub.Common.TypeDef;
import com.commax.pam.db.interfaces.MySQLConnection;
import com.commax.pam.db.interfaces.PAMDBFieldContentKind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {

    /* Tag define */
    static final String TAG = DataManager.class.getSimpleName();

    /* max try counter for db access : unit 100ms */
    static final int MYSQL_TRY_COUNT = 20;

/*
    //Type define : Grouping Data by Category
    public class CategorizedData {
        CategoryType nCategory;

        //Card List by sort -> 고민필요: 무기명으로 운영할 것인기?
        ArrayList<DeviceInfo> mTotalDeviceList;
        ArrayList<DeviceInfo> mGroupList1;
        ArrayList<DeviceInfo> mGroupList2;
        ArrayList<DeviceInfo> mGroupList3;
        ArrayList<DeviceInfo> mGroupList4;
        ArrayList<DeviceInfo> mGroupList5;

        //Method
    }

    public CategorizedData mCategory_eFavorite = new CategorizedData();
    public CategorizedData mCategory_eLight;
    public CategorizedData mCategory_eIndoorEnv;
    public CategorizedData mCategory_eEnergy;
    public CategorizedData mCategory_eSafe;
*/

    /* Member Variable */
    Context mContext;
    boolean mConnect= false;
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
            e.printStackTrace();
            connected= false;
        }
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
    public ArrayList<DeviceInfo> mysql_getSelRootDeviceCount(String rootDeviceName) {

        ArrayList<DeviceInfo> list_rootUuidSortAndValues = new ArrayList<>();
        ArrayList<String> rootDeviceList = new ArrayList<>();

        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice를 찾아 RootUuid List를 얻는다
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            Log.d(TAG, "rootDevice : " + rootDeviceName +  " => " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    Log.d(TAG, "powerSwitchCount : get(" + a + ") = " + deviceNumber); //rootDevice 가 switch 인것의 rootUuid
                    if (deviceNumber != null) {

                        String raw = "";

                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(TypeDef.ROOT_DEVICE_SWITCH, deviceNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //TODO 임시 주석 처리
//                        DeviceInfo device_data = getRootDeviceStatusByRaw(raw , rootDeviceName);
//                        if (device_data != null) {
//                            list_rootUuidSortAndValues.add(device_data);
//                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list_rootUuidSortAndValues;
    }

    //TODO
    //getSmartPlugStatusByRaw
    public void getRootDeviceStatusByRaw(String raw , String cmxrootDeviceName , DeviceInfo device_data){

        try {
            String device_sort = "", rootUuid = "", subUuid = "";

            //1st check (commaxDevice 인지 확인)
            String commaxDevice = mysql_getCommaxDevice(raw);
            Log.i(TAG , "commaxDevice : " + commaxDevice);

            if(!TextUtils.isEmpty(commaxDevice)) {
                if (commaxDevice.equalsIgnoreCase(cmxrootDeviceName)) {
                    //TODO 배열 위치의 처음에 있는 sort를 가져옴
                    device_sort = mysql_getSort(raw);
                }
            }

            //2nd check (commmaxDeivce 가 아닐 경우 rootDevice 로 sort 분류)
            if (TextUtils.isEmpty(device_sort)) {
                String rootDevice = mysql_getRootDevice(raw);

                if (!TextUtils.isEmpty(rootDevice)) {   // 대기전력
                    if (rootDevice.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH)) {
                        device_sort = TypeDef.ROOT_DEVICE_SWITCH;
                    }
                    else  if (rootDevice.equalsIgnoreCase(TypeDef.ROOT_DEVICE_THERMOSTAT)) {
                        device_sort = TypeDef.ROOT_DEVICE_THERMOSTAT;
                    }
                }
            }

           /* //TODO 코맥스 디바이스가 아닌경우?
            if (!TextUtils.isEmpty(device_sort)) {
                subUuid = getSubUuidFromMatchingSubRaw(device_sort, raw);   //if it is null, this is not meter device
                if (!TextUtils.isEmpty(subUuid)) {
                    rootUuid = mysql_getRootUuid(raw);
                    String status = getDeviceStatus(subUuid);
                    if ((!TextUtils.isEmpty(rootUuid))) {
                        //TODO 각 디바이스에 맞게 고쳐야 함
//                        device_data = new DeviceInfo(rootUuid, device_sort);
                        device_data.setStatus(subUuid, status);
                        Log.d(TAG, "getSmartPlugStatusByRaw : status " + status + " saved");
                    }
                }
            }*/


            if(commaxDevice.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_STANDBYPOWER)) //대기전력
            {
                try{
                    //index 1 : power on/off
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_SWITCHBINARY, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid(subUuid);
                    String status = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status);
                    device_data.setValue(status);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 2 : real time data
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_ELECTRICMETER, raw);   //if it is null, this is not meter device
                    if (!TextUtils.isEmpty(subUuid)) {
                        device_data.setSubUuid2(subUuid);
                        Pair<String, String> value_scale = getDeviceStatus("float", subUuid);
                        Log.d(TAG, "getMeterValue : value_scale : " + value_scale.first + " / " + value_scale.second);
                        device_data.setValue2(value_scale.first);
                        device_data.setScale(value_scale.second);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 3 : cut off value setting
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_METERSETTING, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid3(subUuid);
                    String status3 = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status3);
                    device_data.setValue3(status3);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try{
                    //index 4 :  auto/ manual
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_MODEBINARY, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid4(subUuid);
                    String status4 = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status4);
                    device_data.setValue4(status4);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (commaxDevice.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU)) //FCU
            {

                try
                {
                    //index 1 : real time temperature
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_AIRTEMP, raw);   //if it is null, this is not meter device
                    if (!TextUtils.isEmpty(subUuid)) {
                        Pair<String, String> value_scale = getDeviceStatus("float", subUuid);
                        Log.d(TAG, "getMeterValue : value_scale : " + value_scale.first + " / " + value_scale.second);
                        device_data.setSubUuid(subUuid);
                        device_data.setValue(value_scale.first);
                        String[] scales = value_scale.second.split("\"");
                        device_data.setScale(scales[1]);
                        device_data.setSort(TypeDef.SUB_DEVICE_AIRTEMP);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 2 : power on/off
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_THERMOSTATMODE, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid2(subUuid);
                    String status2 = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status2);
                    device_data.setValue2(status2);
                    device_data.setSort2(TypeDef.SUB_DEVICE_THERMOSTATMODE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 3 : mode  heating/ cooling
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_THERMOSTATRUNMODE, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid3(subUuid);
                    String status3 = getDeviceStatus(subUuid);
                    Log.e(TAG ,"metersetting status : "+status3);
                    device_data.setValue3(status3);
                    device_data.setSort3(TypeDef.SUB_DEVICE_THERMOSTATRUNMODE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 4 : cooling point
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_THERMOSTATSETCOLLINGPOINT, raw);   //if it is null, this is not meter device
                    if (!TextUtils.isEmpty(subUuid)) {
                        Pair<String, String> value_scale4 = getDeviceStatus("float", subUuid);
                        Log.d(TAG, "getMeterValue : value_scale : " + value_scale4.first + " / " + value_scale4.second);
                        device_data.setValue4(value_scale4.first);
                        //c , f 만 parsing 해서 넣음
                        String[] scales = value_scale4.second.split("\"");
                        device_data.setScale2(scales[1]);
                        device_data.setSort4(TypeDef.SUB_DEVICE_THERMOSTATSETCOLLINGPOINT);
                        device_data.setSubUuid4(subUuid);
                        String option2[] = new String[2];
                        mysql_getAllOption(raw, option2 , subUuid);
                        device_data.setOption1(option2[0]);
                        device_data.setOption2(option2[1]);
                        Log.d(TAG, "option1 : " + option2[0] + ", option2 : " + option2[1]);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //index 5 : heating point
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_THERMOSTATSETHEATINGPOINT, raw);   //if it is null, this is not meter device
                    if (!TextUtils.isEmpty(subUuid)) {
                        Pair<String, String> value_scale5 = getDeviceStatus("float", subUuid);
                        device_data.setSubUuid5(subUuid);
                        device_data.setValue5(value_scale5.first);
                        String[] scales = value_scale5.second.split("\"");
                        device_data.setScale3(scales[1]);
                        device_data.setSort5(TypeDef.SUB_DEVICE_THERMOSTATSETHEATINGPOINT);
                        String option3[] = new String[2];
                        mysql_getAllOption(raw, option3 , subUuid);
                        device_data.setOption3(option3[0]);
                        device_data.setOption4(option3[1]);
                    }
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                }

               /* try
                {
                    //index 6 : fan speed
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_FANSPEED, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid6(subUuid);
                    String status6 = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status6);
                    device_data.setValue6(status6);
                    device_data.setSort6(TypeDef.SUB_DEVICE_FANSPEED);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }*/

                try
                {
                    //index 7 : away on / off
                    subUuid = getSubUuidFromMatchingSubRaw(TypeDef.SUB_DEVICE_THERMOSTATAWAYMODE, raw);   //if it is null, this is not meter device
                    device_data.setSubUuid7(subUuid);
                    String status7 = getDeviceStatus(subUuid);
                    Log.d(TAG ,"metersetting status : "+status7);
                    device_data.setValue7(status7);
                    device_data.setSort7(TypeDef.SUB_DEVICE_THERMOSTATAWAYMODE);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // subUuid 로 Device 정보를 찾아 상태를 parsing
    private Pair<String,String> getDeviceStatus(String return_type, String subUuid) {
        Pair<String, String> result = new Pair<>("", "");
        HashMap<String, String> beforeFilter;
        String value = "", precision = "", scale = "";

        try {
            beforeFilter = MySQLConnection.getInstance().getDeviceValue(subUuid);

            value = beforeFilter.get(PAMDBFieldContentKind.DEVICE_VALUE);
            precision = beforeFilter.get(PAMDBFieldContentKind.DEVICE_PRECISION);
            scale = beforeFilter.get(PAMDBFieldContentKind.DEVICE_SCALE);
            Log.d(TAG, "value : "+ value + ", precision : " +precision + " , scale : " +scale);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if ((value != null) && (precision != null) && (scale != null) && (!value.equals("")) && (!precision.equals("")) && (!scale.equals(""))) {
            String re_value = "";
            if (return_type.equalsIgnoreCase("Integer"))
                re_value = getIntegerValue(value, precision);
            else re_value = getAccurateValue(value, precision);

            if ((re_value != null) && (!re_value.equals(""))) {
                result = new Pair<>(re_value, scale);
            }
            Log.d("UsageData", "getDeviceStatus of " + subUuid + " = value : " + re_value + " / precision : " + precision + " / scale : " + scale);
        }

        return result;
    }

    // float to limited integer value
    public String getIntegerValue(String value, String precision){
        String val="";

        try {
            if ((value != null) && (precision != null) && (!value.equalsIgnoreCase("String"))) {
                val = getAccurateValue(value, precision);
//            Log.d("IntegerArray", val);
                if ((val != null)) {
                    if (val.contains(".")) {
                        String[] array = val.split("\\.");
                        if (array[0].contains("-")) {
//                        Log.d("array0", "" + array[0] + "/ length" + array[0].length());
                            if (array[0].length() > 4) return "-999";
                        } else {
                            if (array[0].length() > 3) return "999";
                        }
                        return array[0];
                    } else {
                        if (val.contains("-")) {
                            if (val.length() > 4) return "-999";
                        } else {
                            if (val.length() > 3) return "999";
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return val;
    }

    // value 와 precision 을 조합하여 실수로 변환
    // value 20 이고 precision 이 1일 경우 2.0 임
    // value 20 이고 precision 이 2일 경우 0.2
    private String getAccurateValue(String value, String precision){        //TODO 검증 필요
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

                        if (right.length() > 1) right = right.substring(0, 1);
                        result = "0." + right;
                    } else {
                        left = val.substring(0, length - (Integer.parseInt(precision)));
                        right = val.substring(length - (Integer.parseInt(precision)));
                        if (right.length() > 1) right = right.substring(0, 1);
                        result = left + "." + right;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }



    public String getDeviceStatus(String subUuid){
        String sub_raw = "";
        String status="";

        try {
            sub_raw = MySQLConnection.getInstance().getRawSubJson(subUuid);
            Log.d(TAG,"getDeviceStatus sub_raw : "+ sub_raw);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            status = getStatusFromRaw(sub_raw);
        }catch (Exception e){
            status=TypeDef.STATUS_UNKNOWN;
            e.printStackTrace();
        }

        return status;
    }

    public String getStatusFromRaw(String sub_raw){

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
                        Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!= null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    status = jsonArray.getJSONObject(i).getString("value");
                                    Log.d(TAG, "getStatusFromRaw status : " + status);
                                }
                            }catch (Exception e){
//                                e.printStackTrace();

                                Log.d(TAG, "getStatusFromRaw get status retry");
                                try{
                                    JSONObject subDevice = (new JSONObject(subObject.trim()));
                                    status = subDevice.getString("value");
                                    Log.d(TAG, "getStatusFromRaw status retry : " + status);
                                }catch (Exception e1){
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

        return status;
    }


    // sub raw string 를 parsing 하여 원하는 sub sort 의 sub uuid return
    private String getSubUuidFromMatchingSubRaw(String deviceSort, String raw){
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
                        Log.d(TAG, "getMatchingSubDevice subDevice : " + subObject);
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
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "getSubUuidFromMatchingSubRaw subUuid : " + subUuid);
        return subUuid;
    }

    // Desc: get MySQL CommaxDevice From jObject
    // Ref : getCommaxDevice(String raw)
    // Commax 에서 정의한 디바이스 sort 인지 확인
    public String mysql_getCommaxDevice(String str_jObject){
        String commaxResult = "";
        JSONObject jObject = null;

        try {                                           //object -> commaxDevice 인지 체크함
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                String object = jObject.getString("object");
                try {
                    commaxResult = (new JSONObject(object.trim())).getString("commaxDevice");
                    Log.d(TAG, "commaxDevice : " + commaxResult);
                } catch (Exception e){
                    e.printStackTrace();
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
                String object = jObject.getString("object");
                try {
                    rootResult = (new JSONObject(object.trim())).getString("rootDevice");
                    Log.d(TAG, "RootDevice : " + rootResult);
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
            Log.d(TAG, "nickName : " + nickName);
        }catch (Exception e){
            e.printStackTrace();
        }

        return nickName;
    }

    public String mysql_getNickName2(String rootUuid, String rootDeviceName) {

        //ArrayList<DeviceInfo> list_DeviceSortAndValues = new ArrayList<>();
        ArrayList<String> rootDeviceList = new ArrayList<>();
        ArrayList<String> rootNickNameList = new ArrayList<>();


        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice를 찾아 RootUuid List를 얻는다
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            Log.d(TAG, rootDeviceName +  " list : " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    Log.d(TAG, "get(" + a + ") = " + deviceNumber);

                    if (deviceNumber != null) {
                        String raw = "";
                        try {
                            raw = MySQLConnection.getInstance().getDevicePropertybyRootUuid(rootDeviceName, deviceNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //Get DeviceInfo
                        DeviceInfo device_data = null;

                        String getrootUuid = mysql_getRootUuid(raw);
                        if(getrootUuid.equalsIgnoreCase(rootUuid)) {
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
                                //list_DeviceSortAndValues.add(device_data);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String nickName = "";

        try {
            nickName = MySQLConnection.getInstance().getDeviceNickName(rootUuid);
            Log.d(TAG, "nickName : " + nickName);
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
                    String object = jObject.getString("object");
                    try {
                        rootUuid = (new JSONObject(object.trim())).getString("rootUuid");
                        Log.d(TAG, "RootUuid : " + rootUuid);
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

    // Desc: get MySQL sort From jObject(jSubObject)
    // Ref : getSortFromRaw(String raw)
    public String mysql_getSort(String str_jObject){
        String sort = "";
        JSONObject jObject=null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        //Log.d(TAG, "subDevice : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                sort = jsonArray.getJSONObject(0).getString("sort");
                                if(!TextUtils.isEmpty(sort)){
                                    Log.d(TAG, "subDevice sort: " + sort);
                                    return sort;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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

    // Desc: get MySQL subUuid From jObject(jSubObject)
    // Ref : getSubUuidFromMatchingSubRaw(String deviceSort, String raw)
    public String mysql_getsubUuid(String str_jObject){
        String subUuid = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        Log.d(TAG, "get subDevice info : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    subUuid = jsonArray.getJSONObject(i).getString("subUuid");
                                    if(!TextUtils.isEmpty(subUuid)){
                                        Log.d(TAG, "subDevice subUuid : " + subUuid);
                                        return subUuid;
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
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

    // Desc: get MySQL value From jObject(jSubObject)
    // Ref : getStatusFromRaw(String sub_raw)
    public String mysql_getValue(String str_jObject){
        String value = "";
        JSONObject jObject = null;
        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());

                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        Log.d(TAG, "getStatusFromRaw subDevice : " + subObject);
                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    value = jsonArray.getJSONObject(i).getString("value");
                                    if(!TextUtils.isEmpty(value)){
                                        Log.d(TAG, "subDevice value : " + value);
                                        return value;
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
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

    // Desc: get MySQL value From jObject(jSubObject)
    // Ref : getDeviceStatus(String subUuid)
    public String mysql_getSubObject(String subUuid){
        String jSubObject = "";
        String status="";

        try {
            jSubObject = MySQLConnection.getInstance().getRawSubJson(subUuid);
        }catch (Exception e){
            e.printStackTrace();
        }

        return jSubObject;
    }


    // Desc: get all MySQL item by user input From jObject
    // Ref :
    public int mysql_getAllOption(String str_jObject, String findlist[] , String subUuid){
        int index = 0;
        JSONObject jObject = null;

        try {
            if (!TextUtils.isEmpty(str_jObject)) {
                jObject = new JSONObject(str_jObject.toString());
                if (jObject!=null) {
                    String object = jObject.getString("object");

                    JSONObject jSubObject = new JSONObject(object);
                    if(jSubObject!=null) {
                        String subObject = jSubObject.getString("subDevice");
                        //Log.d(TAG, "get subDevice info : " + subObject);

                        if(subObject!=null) {
                            try {
                                JSONArray jsonArray = (new JSONArray(subObject.trim()));
                                //Log.d(TAG, "get jsonArray info : " + jsonArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    if(jsonArray.getJSONObject(i).getString("subUuid").equalsIgnoreCase(subUuid))
                                    {
                                        try {
                                            //Log.d(TAG, "get jsonArray.getJSONObject info : " + jsonArray.getJSONObject(i));
                                            findlist[0] = jsonArray.getJSONObject(i).getString("option1");
                                            findlist[1] = jsonArray.getJSONObject(i).getString("option2");
                                            if (!TextUtils.isEmpty(findlist[0])) {
                                                Log.d(TAG, "subDevice option : " + findlist[0] + "-" + findlist[1]);
                                                break;
                                            }
                                        }catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
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
            Log.d(TAG, rootDeviceName +  " list : " + rootDeviceList);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    Log.d(TAG, "get(" + a + ") = " + deviceNumber);

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

        Log.d(TAG, "Total list : " + list_DeviceSortAndValues.size());
        Log.d(TAG, "Total list" + "(" + rootDeviceName + ") : " + list_DeviceSortAndValues.size());

        return list_DeviceSortAndValues;
    }

    public void setCardType(TypeDef.CardType cardtype)
    {
        Log.d(TAG, " => setCardType : " + cardtype);
    }

    // Desc: 원하는 RootDevice+subDevice 를 MySQL DB 에서 쿼리하여 SubDeviceInfo List return
    // get MySQL selected subDevice list From MySQL
    // Ref : getSwitchValueList()
    public ArrayList<DeviceInfo> mysql_getSelSubDeviceInfoList(String rootDeviceName, String CommaxDeviceName, TypeDef.CategoryType category){
        Log.d(TAG ,"mysql_getSelSubDeviceInfoList() rootDeviceName : " + rootDeviceName + " , CommaxDeviceName : " +CommaxDeviceName + " , category : " +category);

        ArrayList<DeviceInfo> list_DeviceSortAndValues = new ArrayList<>();
        ArrayList<String> rootDeviceList = new ArrayList<>();

        //주로 사용하는 rootDevice는 ROOT_DEVICE_SWITCH,ROOT_DEVICE_DSENSOR 가 대부분임
        try {
            // 주어진 모든 rootDevice의 이름의 모든 RootUuid List를 얻는다.  ex) switch
            rootDeviceList = MySQLConnection.getInstance().getRootDeviceRootUuid(rootDeviceName);
            Log.d(TAG,"mysql_getSelSubDeviceInfoList rootDeviceName : " + rootDeviceName +  " list : " + rootDeviceList);
            Log.d(TAG,"rootDeviceList.size() = " + rootDeviceList.size());

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (rootDeviceList != null) {
                for (int a = 0; a < rootDeviceList.size(); a++) {
                    String deviceNumber = rootDeviceList.get(a);
                    Log.d(TAG, "get(" + a + ") = " + deviceNumber);

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
                        //TODO raw 를 가져와서 부터 기기별로 나누어 정보를 다르게 가져와야 한다.
                        if(CommaxDevice.equalsIgnoreCase(CommaxDeviceName)) {
                            Log.d(TAG, "mysql_getSelSubDeviceInfoList raw : " +raw);

                            String rootUuid = mysql_getRootUuid(raw);
                            String nickName = mysql_getNickName(rootUuid);
                            String rootDevice = mysql_getRootDevice(raw);
                            String subUuid = mysql_getsubUuid(raw);
                            String device_sort = mysql_getSort(raw);
                            String subObject = mysql_getSubObject(subUuid);
                            //TODO raw 넣어보기 subObject -> raw
                            String ststus_value = mysql_getValue(raw); //값은 sub에서 읽어와야함

                            //TODO for test 앞으로 빼옴
                            device_data = new DeviceInfo(rootUuid, device_sort);
                            getRootDeviceStatusByRaw(raw , CommaxDeviceName , device_data);

                            if ((!TextUtils.isEmpty(rootUuid))) {
//                                device_data = new DeviceInfo(rootUuid, device_sort);
                                device_data.nickName = nickName;
                                device_data.addDeviceInfo(rootDevice, CommaxDevice);
                                //TODO 임시
//                                device_data.setStatus(subUuid, ststus_value);
                                device_data.nCategory = category;
                                device_data.nCardType = mysql_getcardType(rootDeviceName,CommaxDeviceName,category);
                                device_data.nLayoutID = auto_counter++;
                                device_data.bUpdated = false;
                                list_DeviceSortAndValues.add(device_data);
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d(TAG, "mysql_getSelSubDeviceInfoList Total list" + "(" + rootDeviceName +"," + CommaxDeviceName + ") : " + list_DeviceSortAndValues.size());

        return list_DeviceSortAndValues;
    }


    // MySQL Utility *********************************************

    // Desc: MySQL 이 가용상태인지 체크
    // count 정보를 가져올 수 있다면 MySQL 이 연결된 상태로 간주하여 true
    // Ref : checkValidConnection()
    public boolean mysql_checkValidConnection(){
        Log.d(TAG, "mysql_checkValidConnection()");
        try{
            int count = 0;
            count = mysql_getjObjectCount();

            if(count>0) {
                return true;
            }else {
                return false;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

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
                    catetype = TypeDef.CardType.eMainSwitch;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_LIGHT)) {
                    catetype = TypeDef.CardType.eLightSwitch;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DIMMER) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_LIGHT)) {
                    catetype = TypeDef.CardType.eDimmerSwitch;
                }
                break;

            case eIndoorEnv: //eIndoorEnv Scan
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_BOILER)) {
                    catetype = TypeDef.CardType.eBoiler;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_AIRCON)) {
                    catetype = TypeDef.CardType.eAircon;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FAN)) {
                    catetype = TypeDef.CardType.eFan;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU)) {
                    catetype = TypeDef.CardType.eFCU;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_SWITCH) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_CURTAIN)) {
                    catetype = TypeDef.CardType.eCurtain;
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
                break;

            case eSafe: //eSafe Scan
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_GASSENSOR)) {
                    catetype = TypeDef.CardType.eGasSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_MOTIONSENSOR)) {
                    catetype = TypeDef.CardType.eMotionSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FIRESENSOR)) {
                    catetype = TypeDef.CardType.eFireSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_DOORSENSOR)) {
                    catetype = TypeDef.CardType.eDoorSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_WATERSENSOR)) {
                    catetype = TypeDef.CardType.eWaterSensor;
                }

                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_CONTACTSENSOR)) {
                    catetype = TypeDef.CardType.eDetectSensor;
                }
                if (rootDeviceName.equalsIgnoreCase(TypeDef.ROOT_DEVICE_DSENSOR) && CommaxDeviceName.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_DETECTSENSOR)) {
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

    /********** MySQL API Design End****************************************************************/


    /********** AIDL API Design Start****************************************************************/
    public void setOnOrOffAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        try {
            if (rootDevice.equalsIgnoreCase(TypeDef.ROOT_DEVICE_LOCK)) {
//                MainActivity.getInstance().mToast.setText(mContext.getString(R.string.only_locking));
//                MainActivity.getInstance().mToast.show();
            } else {
//            Toast.makeText(mContext, mContext.getString(R.string.working), Toast.LENGTH_SHORT).show();
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
        Log.d("JsonString", "result : " + result);
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
            Log.d(TAG, "json : " + result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    /********** AIDL API Design End****************************************************************/

}
