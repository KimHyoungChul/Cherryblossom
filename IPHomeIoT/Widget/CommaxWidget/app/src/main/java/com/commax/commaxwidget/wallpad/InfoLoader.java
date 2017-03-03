package com.commax.commaxwidget.wallpad;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.commax.commaxwidget.wallpad.constans.CommaxDevice;
import com.commax.commaxwidget.wallpad.constans.HandlerEvent;
import com.commax.commaxwidget.wallpad.constans.NameSpace;
import com.commax.commaxwidget.wallpad.constans.RootDevice;
import com.commax.commaxwidget.wallpad.data.DeviceInfo;
import com.commax.commaxwidget.wallpad.data.ProjectOptions;
import com.commax.commaxwidget.wallpad.data.SubDevice;
import com.commax.commaxwidget.wallpad.data.SubSort;
import com.commax.commaxwidget.wallpad.data.WeatherInfo;
import com.commax.commaxwidget.wallpad.utils.InfoTools;
import com.commax.commaxwidget.wallpad.utils.SettingValues;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InfoLoader implements Runnable {

    private final String DEBUG_TAG = "InfoLoader";
    private Context mContext;
    private Handler mHandler;
    private UsageData data;
    private ArrayList<String> list_unselected_info;
    private ArrayList<String> list_support_info;
    private boolean mSignState = false;
    private ProjectOptions projectOptions;

    private boolean running = true;
    public boolean end_thread_running = true;

    public ArrayList<DeviceInfo> mSmartPlugList = null;
    public ArrayList<DeviceInfo> mSensorList = null;

    public InfoLoader(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        projectOptions = new ProjectOptions();
    }

    @Override
    public void run() {
        running = true;
        try {
            Log.d(DEBUG_TAG, "CreateView Thread started");

            try {
                //clock view
                Thread.sleep(1000);

                Message addViewMsg = mHandler.obtainMessage();
                addViewMsg.what = HandlerEvent.EVENT_HANDLE_SHOW_PROGRESS;
                mHandler.sendMessage(addViewMsg);

                getProjectOptions();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1);
                end_thread_running = false;

                if (setConnection()) {

                    if ((running)) {

                        FileEx fileEx = new FileEx();
                        String netState = fileEx.getNetworkState();

                        boolean localConnted = false;
                        boolean flexibleConnted = false;

                        list_unselected_info = new ArrayList<>();
                        list_support_info = new ArrayList<>();
                        InfoTools infoTools = new InfoTools();
                        list_unselected_info = infoTools.getUnSelectedInfo();
                        list_support_info = infoTools.getSupportInfo();
                        //   mSignState = infoTools.getCreateAccountState();

                        preLoadLocalInfo();

                        if (netState.equalsIgnoreCase("true")) {
                            try {
                                localConnted = data.checkLocalNetwork();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            try {
                                flexibleConnted = data.checkNetworkFlexible();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        boolean my_sqlConnected = false;
                        boolean tried = false;

                        if (checkUseDevice()) {
                            for (int j = 0; j < 50; j++) {

                                if (j % 10 == 0) {
                                    Log.d(DEBUG_TAG, "attempt " + j / 10);
                                }

                                if ((data != null) && (data.isConnected())) {

                                    if (!data.checkValidConnection()) {

                                        if ((data != null) && (data.isConnected())) {
                                            data.close();

                                            Thread.sleep(100);
                                        }

                                        data.open();
                                        Thread.sleep(100);
                                    }

                                    for (int i = 0; i < 100; i++) {

                                        if ((data != null) && (data.isConnected())) {

                                            if (data.checkValidConnection()) {
                                                my_sqlConnected = true;
                                                break;
                                            }
                                            Thread.sleep(100);
                                        } else {
                                            Thread.sleep(100);
                                        }
                                    }
                                    break;
                                } else {

                                    if (data != null) {

                                        if (!tried) {
                                            tried = true;

                                            data.close();

                                            Thread.sleep(100);

                                            data.open();
                                        }
                                        Thread.sleep(100);
                                    } else {

                                        Thread.sleep(100);
                                    }
                                }
                            }
                        }

                        createView(localConnted, flexibleConnted, my_sqlConnected);
                    }

                }

                fillView();
                end_thread_running = true;

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkUseDevice() {

        boolean bUse = false;

        try {

            if (list_support_info.contains(NameSpace.INFO_INDOOR_TEMP) && (!list_unselected_info.contains(NameSpace.INFO_INDOOR_TEMP))) {
                bUse = true;
            }

            if (list_support_info.contains(NameSpace.INFO_INDOOR_HUMID) && (!list_unselected_info.contains(NameSpace.INFO_INDOOR_HUMID))) {
                bUse = true;
            }

            if (list_support_info.contains(NameSpace.INFO_INDOOR_AIR) && (!list_unselected_info.contains(NameSpace.INFO_INDOOR_AIR))) {
                bUse = true;
            }

            if (list_support_info.contains(NameSpace.INFO_SMART_PLUG) && (!list_unselected_info.contains(NameSpace.INFO_SMART_PLUG))) {
                bUse = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(DEBUG_TAG, "checkUseDevice bUse " + bUse);
        return bUse;

    }

    private void preLoadLocalInfo() {
        try {

            if (list_support_info.contains(NameSpace.INFO_VISITOR) && (!list_unselected_info.contains(NameSpace.INFO_VISITOR))) {
                boolean type_fullip = false;

                try {
                    if (projectOptions.str_model_type.equalsIgnoreCase(NameSpace.MODEL_TYPE_FULL_IP)) {
                        type_fullip = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

              /*  if (type_fullip) {
                    drawCall();
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createView(boolean localConnected, boolean flexibleConnected, boolean bMysql_connected) {
        Log.d(DEBUG_TAG, "createView network : local : " + localConnected + " flexible : " + flexibleConnected + " mysql : " + bMysql_connected);

        try {
            if ((!localConnected) && (!flexibleConnected)) {
                Message addViewMsg = mHandler.obtainMessage();
                addViewMsg.what = HandlerEvent.EVENT_HANDLE_SHOW_NETWORK_ERR;
                mHandler.sendMessage(addViewMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (flexibleConnected) {
            try {
                drawTodayWeather();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (list_support_info.contains(NameSpace.INFO_AIR) && (!list_unselected_info.contains(NameSpace.INFO_AIR))) {
                    drawAirView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (list_support_info.contains(NameSpace.INFO_HEALTH_LIFE) && (!list_unselected_info.contains(NameSpace.INFO_HEALTH_LIFE))) {
                    drawLifeView();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            if (bMysql_connected) {
                if (checkUseDevice()) {
                    try {
                        drawDeviceViews();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        if(localConnected){

            try {
                if (list_support_info.contains(NameSpace.INFO_TODAY_EMS)&&(!list_unselected_info.contains(NameSpace.INFO_TODAY_EMS))){
                    drawTodayEMS();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                if (list_support_info.contains(NameSpace.INFO_MONTH_EMS)&&(!list_unselected_info.contains(NameSpace.INFO_MONTH_EMS))) {
                    drawMonthEnergy();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                if (list_support_info.contains(NameSpace.INFO_PARKING)&&(!list_unselected_info.contains(NameSpace.INFO_PARKING))) {
                    drawParkingArea();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        */

        try {
            if (list_support_info.contains(NameSpace.INFO_VISITOR) && (!list_unselected_info.contains(NameSpace.INFO_VISITOR))) {
                drawMissed(localConnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        try {
            if (localConnected) {
                if (list_support_info.contains(NameSpace.INFO_NOTICE)&&(!list_unselected_info.contains(NameSpace.INFO_NOTICE))) {
                    drawNotice();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (localConnected) {
                if (list_support_info.contains(NameSpace.INFO_SUPPORT)&&(!list_unselected_info.contains(NameSpace.INFO_SUPPORT))) {
                    drawSupport();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    private void drawMissed(boolean localConnected) {

        try {

            boolean type_fullip = false;

            try {
                if (projectOptions.str_model_type.equalsIgnoreCase(NameSpace.MODEL_TYPE_FULL_IP)) {
                    type_fullip = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
/*
            if (type_fullip){
               drawCall();
            }else {
               drawMissedAll(localConnected);
            }
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
        private void drawMissedAll(boolean localConnected){

            try {

                ArrayList<MissedItem> missedItems = new ArrayList<>();

                // Visitor
                boolean check_state = false;
                int missed_visitor = 0;

                try {
                    check_state = data.checkSupportVisitor();
                    Log.d(DEBUG_TAG, "makeNewVisitorView : check_state : " + check_state);

                    if (check_state) {
                        missed_visitor = data.getVisitorCount();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Call
                int missed_call = 0;
                boolean call_dimmed = false;

                try {
                    missed_call = data.getMissedCall(CallLog.Calls.CONTENT_URI);

                    if (missed_call < 0) {
                        call_dimmed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Delivery
                int delivery_count = -1;
                boolean delivery_dimmed = false;

                try {
                    if (localConnected) {
                        delivery_count = data.checkNewDelivery();
                    }

                    if ((!localConnected) || (delivery_count < 0)) {
                        delivery_dimmed = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(DEBUG_TAG, "missed call " + missed_call + " / visitor " + missed_visitor + " / delivery " + delivery_count);
                if ((missed_call > 0) || (missed_visitor > 0) || (delivery_count > 0)) {

                    MissedView missedView = new MissedView(mContext);

                    try {
                        MissedItem missedVisitor = new MissedItem(mContext, NameSpace.MISSED_VISITOR, !check_state, missed_visitor);
                        missedVisitor.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent();
                                    Log.d("InfLoader", "call missedVisitor");
                                    intent.setClassName("com.commax.pxdsamplegallery", "com.commax.pxdsamplegallery.GalleryActivity");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        missedItems.add(missedVisitor);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        MissedItem missedCall = new MissedItem(mContext, NameSpace.MISSED_CALL, call_dimmed, missed_call);
                        missedCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    Intent intent = new Intent();
                                    Log.d("InfLoader", "call missedCall");
                                    intent.setClassName("com.commax.dialer", "com.commax.dialer.CallLogActivity");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    mContext.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        missedItems.add(missedCall);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        MissedItem missedDelivery = new MissedItem(mContext, NameSpace.MISSED_DELIVERY, delivery_dimmed, delivery_count);
                        if (!delivery_dimmed) {
                            missedDelivery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent intent = new Intent();
                                        Log.d("InfLoader", "call missedDelivery");
                                        intent.setClassName("com.commax.webappbase", "com.commax.webappbase.MainActivityDelivery");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        mContext.startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        missedItems.add(missedDelivery);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        for (MissedItem item : missedItems) {
                            missedView.addMonthItem(item);
                        }

                        Message addViewMsg = mHandler.obtainMessage();
                        addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                        addViewMsg.obj = missedView;
                        mHandler.sendMessage(addViewMsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    */
  /*  private void drawCall(){

        try {

            ArrayList<MissedItem> missedItems = new ArrayList<>();

            // Call
            int missed_call = 0;
            boolean call_dimmed = false;

            try {
                missed_call = data.getMissedCall(CallLogProviderDefine.CONTENT_URI);

                if (missed_call < 0) {
                    call_dimmed = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(DEBUG_TAG, "missed call " + missed_call);
            if (missed_call > 0) {

                MissedView missedView = new MissedView(mContext);

                try {
                    MissedItem missedCall = new MissedItem(mContext, NameSpace.MISSED_CALL, call_dimmed, missed_call);
                    missedCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent();
                                Log.d("InfLoader", "call missedCall");
                                intent.setClassName("com.commax.iphomiot.calllogview", "com.commax.iphomiot.calllogview.MainActivity");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                mContext.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    missedItems.add(missedCall);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    for (MissedItem item : missedItems) {
                        missedView.addMonthItem(item);
                    }

                    Message addViewMsg = mHandler.obtainMessage();
                    addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    addViewMsg.obj = missedView;
                    mHandler.sendMessage(addViewMsg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

*/
    public void updateWeather() {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {

                    double weatherValue[] = {-1, -1, -1}; //temp, code, humidity
                    Pair<double[], String> condition = new Pair<>(weatherValue, "Seoul");
                    Pair<Pair<double[], String>, String> whole = new Pair<>(new Pair<>(weatherValue, "Seoul"), "");
                    String forecast_raw = "";

                    int days = 0;

                    if (NameSpace.TEST) {
                        days = 2;
                    }

                    try {
                        //TODO 작업필요
                        String location = "Seoul";
                        whole = data.getWeatherData(days);

                        condition = whole.first;
                        forecast_raw = whole.second;

                        weatherValue = condition.first;
                        location = condition.second;

                        Log.d(DEBUG_TAG, "weatherData : " + "temp : " + weatherValue[0] + "/code : " + weatherValue[1] + "/humid : " + weatherValue[2] + "/location : " + location);

                        try {
//                            if ((weatherValue[0] < 9999) && (weatherValue[1] != 3200) && (weatherValue[2] != -1)) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_WEATHER;

                            Bundle bundle = new Bundle();
                            bundle.putDoubleArray(NameSpace.WEATHER_VALUE, weatherValue);
                            bundle.putString(NameSpace.LOCATION, location);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAir() {
        try {
            if (data.airSupport()) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        try {
                            double airValue[] = {-1, -1, -1};
                            airValue = data.getAirData();

                            Log.d(DEBUG_TAG, "airData : " + "o3 : " + airValue[0] + "/dust : " + airValue[1] + "/total : " + airValue[2]);

//                            if ((airValue[0] != -1) || (airValue[1] != -1) || (airValue[2] != -1)) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_AIR;

                            Bundle bundle = new Bundle();
                            bundle.putDoubleArray(NameSpace.AIR_VALUE, airValue);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateHealthLife() {
        try {
            if (data.healthLifeSupport()) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        try {
                            double hlValue = -1;
                            hlValue = data.getHealthLifeData();

                            Log.d(DEBUG_TAG, "lifeHealthData : " + "uv : " + hlValue);

//                            if (hlValue != -1) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_HEALTH_LIFE;

                            Bundle bundle = new Bundle();
                            bundle.putDouble(NameSpace.HEALTH_LIFE_VALUE, hlValue);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawDeviceViews() {
        try {
//            drawThermostat();
            drawDetectSensor();
            drawSwitch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawSwitch() {
        try {
            if (mSmartPlugList == null) {
                mSmartPlugList = new ArrayList<DeviceInfo>();
            } else {
                mSmartPlugList.clear();
            }
            ArrayList<DeviceInfo> switchList = data.getDeviceInfo(RootDevice.SWITCH);
            getSwitch(switchList);

            Message dataDetectSensor = mHandler.obtainMessage();
            dataDetectSensor.what = HandlerEvent.EVENT_HANDLE_UPDATE_SMART_PLUG;
            dataDetectSensor.obj = mSmartPlugList;
            mHandler.sendMessage(dataDetectSensor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawDetectSensor() {
        try {
            if (mSensorList == null) {
                mSensorList = new ArrayList<DeviceInfo>();
            } else {
                mSensorList.clear();
            }

            ArrayList<DeviceInfo> sensorList = data.getDeviceInfo(RootDevice.DETECT_SENSORS);
            getSwitch(sensorList);

            Message dataDetectSensor = mHandler.obtainMessage();
            dataDetectSensor.what = HandlerEvent.EVENT_HANDLE_UPDATE_DETECT_SENSOR;
            dataDetectSensor.obj = mSensorList;
            mHandler.sendMessage(dataDetectSensor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawThermostat() {
        try {
            ArrayList<DeviceInfo> switchList = data.getDeviceInfo(RootDevice.BOILER);
            getSwitch(switchList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSwitch(ArrayList<DeviceInfo> getSwitchInfoList) {
        if (getSwitchInfoList != null) {
            Log.d(DEBUG_TAG, "getSwitch size : " + getSwitchInfoList.size());
            if (getSwitchInfoList.size() > 0) {
                for (int i = 0; i < getSwitchInfoList.size(); i++) {

                    try {
                        DeviceInfo deviceInfo = getSwitchInfoList.get(i);

                        String rootUuid = deviceInfo.rootUuid;
                        String sort = deviceInfo.deviceType;
                        String subUuid = deviceInfo.main_subUuid;

//                        Log.d(DEBUG_TAG, "getSwitch sort : " + sort);
                        if ((!TextUtils.isEmpty(rootUuid)) && (!TextUtils.isEmpty(sort))) {
                            drawDevice(sort, deviceInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } else {
            Log.d(DEBUG_TAG, "getSmartPlugValueList is null");
        }
    }

    private void drawDevice(String sort, DeviceInfo deviceInfo) {
        try {
            if (sort.equalsIgnoreCase(CommaxDevice.DETECT_SENSORS)) {
                mSensorList.add(deviceInfo);
                //    drawTempView(deviceInfo);
                //  drawHumidityView(deviceInfo);
            } else if (sort.equalsIgnoreCase(CommaxDevice.SMART_PLUG)) {
                for (int i = 0; i < deviceInfo.subDevices.size(); i++)
                    mSmartPlugList.add(deviceInfo);
                drawSmartPlugView(deviceInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawTempView(DeviceInfo deviceInfo) {
        try {
            SubDevice airTemperature = new SubDevice();
            String str_temp = "";

            if (list_support_info.contains(NameSpace.INFO_INDOOR_TEMP) && (!list_unselected_info.contains(NameSpace.INFO_INDOOR_TEMP))) {
                // airTemperature
                for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                    if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.AIR_TEMPERATURE)) {
                        airTemperature = deviceInfo.subDevices.get(i);
                    }
                }

                if (airTemperature != null) {
                    if (!TextUtils.isEmpty(airTemperature.value)) {
                        str_temp = airTemperature.value;
                        Log.d("junhwe", "junhwe setView heat temp " + str_temp);
                        //check integer
                        try {
                            int value = Integer.parseInt(str_temp);
                        } catch (Exception e) {
                            e.printStackTrace();
                            str_temp = "";
                        }
                    }
/*
                    TempView tempView = new TempView(mContext, str_temp, airTemperature.scale);
                    Message drawSwitch = mHandler.obtainMessage();
                    drawSwitch.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    drawSwitch.obj = tempView;
                    mHandler.sendMessage(drawSwitch);
*/
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void drawHumidityView(DeviceInfo deviceInfo) {
        try {

            SubDevice humidity = new SubDevice();

            String str_humid = "";


            if (list_support_info.contains(NameSpace.INFO_INDOOR_HUMID) && (!list_unselected_info.contains(NameSpace.INFO_INDOOR_HUMID))) {
                //Humidity
                for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                    if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.HUMIDITY)) {
                        humidity = deviceInfo.subDevices.get(i);
                    }
                }

                if (humidity != null) {
                    if (!TextUtils.isEmpty(humidity.value)) {
                        str_humid = humidity.value;
                        Log.d("junhwe", "junhwe setView heat temp " + str_humid);
                        //check integer
                        try {
                            int value = Integer.parseInt(str_humid);
                        } catch (Exception e) {
                            e.printStackTrace();
                            str_humid = "";
                        }
                    }
/*
                    HumidView humidView = new HumidView(mContext, str_humid, humidity.scale);
                    Message drawSwitch = mHandler.obtainMessage();
                    drawSwitch.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    drawSwitch.obj = humidView;
                    mHandler.sendMessage(drawSwitch);
*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawSmartPlugView(DeviceInfo deviceInfo) {
        try {
            //Smart plug
            if (list_support_info.contains(NameSpace.INFO_SMART_PLUG) && (!list_unselected_info.contains(NameSpace.INFO_SMART_PLUG))) {
                SubDevice electric_meter = new SubDevice();
                String str_meter = "";

                for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                    if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.ELECTRIC_METER)) {
                        electric_meter = deviceInfo.subDevices.get(i);
                    }
                }
                if (electric_meter != null) {
                    if (!TextUtils.isEmpty(electric_meter.value)) {
                        str_meter = electric_meter.value;
                        //check integer
                        try {
                            double value = Double.parseDouble(str_meter);
                            if (value >= 10000) {
                                str_meter = "9999.9";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            str_meter = "";
                        }
                    }
/*
                    MeterView meterView = new MeterView(mContext, str_meter, electric_meter.scale, deviceInfo.nickName);
                    Message drawSwitch = mHandler.obtainMessage();
                    drawSwitch.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    drawSwitch.obj = meterView;
                    mHandler.sendMessage(drawSwitch);
*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private void drawSupport(){
        try {
            SupportView supportView = new SupportView(mContext);
            supportView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent();
                        Log.d(DEBUG_TAG, "support web view");
                        //TODO package name edit is needed
                        intent.setClassName("com.commax.webappbase", "com.commax.webappbase.MainActivityUserSupport");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Message addViewMsg = mHandler.obtainMessage();
            addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
            addViewMsg.obj = supportView;
            mHandler.sendMessage(addViewMsg);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void drawNotice(){
        try{
            ArrayList<NoticeItem> noticeItems = new ArrayList<>();
            noticeItems = data.getNotice();

            for (int i=0;i<noticeItems.size();i++){
                try{
                    if (i<NameSpace.MAX_NOTICE) {
                        NoticeView noticeView = new NoticeView(mContext, mHandler, noticeItems.get(i));

                        Message addViewMsg = mHandler.obtainMessage();
                        addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                        addViewMsg.obj = noticeView;
                        mHandler.sendMessage(addViewMsg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    */
/*
    private void drawParkingArea(){
        try{

            ArrayList<CarItem> carItems = new ArrayList<>();
            carItems = data.getCarPosition();

            for (int i=0;i<carItems.size();i++){
                try {

                    String position = carItems.get(i).position;
                    String number = carItems.get(i).carNumber;

                    ParkingView parkingView = new ParkingView(mContext, number, position);
                    Message addViewMsg = mHandler.obtainMessage();
                    addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    addViewMsg.obj = parkingView;
                    mHandler.sendMessage(addViewMsg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    */

    private void drawLifeView() {
        try {
            Message msg;

            if (data.healthLifeSupport()) {
                double hlValue = -1;
                hlValue = data.getHealthLifeData();

                String hlType = NameSpace.LIFE_ULTRV;
                Log.d(DEBUG_TAG, "lifeHealthData : " + hlValue);
/*
                if (data.supportedMonth(hlType)) {

                    if (hlValue != -1) {
                        AirView lifeView = new AirView(mContext, hlType, hlValue);

                        Message addViewMsg = mHandler.obtainMessage();
                        addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                        addViewMsg.obj = lifeView;
                        mHandler.sendMessage(addViewMsg);
                    }
                }
                */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawTodayWeather() {

        try {

            double weatherValue[] = {9999, 3200, -1}; //temp, code, humidity
            Pair<double[], String> condition = new Pair<>(weatherValue, "Seoul");
            Pair<Pair<double[], String>, String> whole = new Pair<>(new Pair<>(weatherValue, "Seoul"), "");

            int days = 0;

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            String strDate = dateFormat.format(date);

            Log.d(DEBUG_TAG, "date : " + strDate);

            if (NameSpace.TEST) {
                days = 2;
            }

            try {
                String location = "Seoul";
                whole = data.getWeatherData(days);

                condition = whole.first;

                weatherValue = condition.first;
                location = condition.second;

                Log.d(DEBUG_TAG, "weatherData : " + "temp : " + weatherValue[0] + "/code : " + weatherValue[1] + "/humid : " + weatherValue[2] + "/location : " + location);

                if ((weatherValue[0] < 9999) && (weatherValue[1] != 3200)) {

                    Message addViewMsg = mHandler.obtainMessage();
                    addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    WeatherInfo info = new WeatherInfo();
                    info.arg1 = weatherValue[0];
                    info.arg2 = weatherValue[1];
                    info.arg3 = weatherValue[2];
                    info.arg4 = location;

                    addViewMsg.obj = (Object) info;
                    mHandler.sendMessage(addViewMsg);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawAirView() {
        try {
            if (data.airSupport()) {

                double airValue[] = {-1, -1, -1};
                airValue = data.getAirData();

                String airType[] = {NameSpace.AIR_O3, NameSpace.AIR_DUST, NameSpace.AIR_TOTAL};
                Log.d(DEBUG_TAG, "airData : " + "o3 : " + airValue[0] + "/dust : " + airValue[1] + "/total : " + airValue[2]);
/*
                for (int i = 0; i < 3; i++) {
                    if (airValue[i] != -1) {
                        AirView airView = new AirView(mContext, airType[i], airValue[i]);

                        Message addViewMsg = mHandler.obtainMessage();
                        addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                        addViewMsg.obj = airView;
                        mHandler.sendMessage(addViewMsg);

                    }
                }
                */
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    private void drawMonthEnergy(){

        List<String> list = new ArrayList<String>();
        ArrayList<EnergyItem> energyItems = new ArrayList<>();
        MonthEnergyView monthEnergyView = new MonthEnergyView(mContext);
        int ems_count = 0;

        try {
            list = data.getSupports();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(DEBUG_TAG, "list.size()"+list.size());

        try {
            if (list.size() > 0) {

                ArrayList<String> list_thisMonth = new ArrayList<>();
                ArrayList<String> list_lastMonth = new ArrayList<>();
                List<String> units = new ArrayList<>();

                Calendar cal = Calendar.getInstance();

                list_thisMonth = data.getThisMonthEms(cal);
                list_lastMonth = data.getLastMonthEms(cal);

                String unit = "";
                try {
                    units = data.getUnits();
                }catch (Exception e){
                    e.printStackTrace();
                }

                for (int i = 0; i < list.size(); i++) {

                    try {
                        try {
                            unit = units.get(i);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        EnergyItem energyItem = drawEnergyItem(list.get(i), list_thisMonth, list_lastMonth, unit);

                        final String energy_id = list.get(i);

                        if (energyItem != null) {
                            energyItem.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Intent intent = new Intent();
                                        Log.d("drawMonthEnergy", "call ems Monthly id " + energy_id);
                                        intent.putExtra("PERIOD", "Monthly");
                                        intent.putExtra("ENERGY", energy_id);
                                        intent.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        mContext.startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            monthEnergyView.addMonthItem(energyItem);
                            ems_count++;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                Log.d("drawMonthEnergy", "ems_count = "+ems_count);
                if (ems_count>0) {
                    Message addViewMsg = mHandler.obtainMessage();
                    addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    addViewMsg.obj = monthEnergyView;
                    mHandler.sendMessage(addViewMsg);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private EnergyItem drawEnergyItem(String id, ArrayList<String> list_thisMonth, ArrayList<String> list_lastMonth, String str_unit){

        try {

            String thisMonth = "", lastMonth = "", unit = "";
            double emsThisMonth = 0.0, emsLastMonth = 0.0, emsPercent = 0.0;

            thisMonth = getMonthEMSById(Items.ITEM_MAP.get(id).id, list_thisMonth);
            lastMonth = getMonthEMSById(Items.ITEM_MAP.get(id).id, list_lastMonth);

            try {
                unit = str_unit;
            } catch (Exception e){

            }

            if (unit == null || !(unit.length() > 0)) {
                try {
                    TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.units);
                    unit = typedArray.getString(0);
                    typedArray.recycle();
                } catch (Exception e) {

                }
            }

            Log.d(DEBUG_TAG, "drawEnergyItem : " + "thisMonth : " + thisMonth + "/ lastMonth : " + lastMonth+"/unit :"+unit);

            emsThisMonth = Double.parseDouble(thisMonth);
            emsLastMonth = Double.parseDouble(lastMonth);

            if (emsThisMonth == 0) emsPercent = 0; // 우선 임시 예외 처리
            else if (emsLastMonth == 0) emsPercent = emsThisMonth;
            else emsPercent = (emsThisMonth * 100 / emsLastMonth);

            if (emsThisMonth != 0 && emsLastMonth == 0) {
                emsPercent = 0.0;
            }
            Log.d(DEBUG_TAG, "drawEnergyItem : " + "percent : " + emsPercent + "/ emsThisMonth : " + emsThisMonth + "/ emsLastMonth: " + emsLastMonth);

            EnergyItem energyItem = new EnergyItem(mContext, id, emsThisMonth, emsPercent, unit);
            return energyItem;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private String getMonthEMSById(String id, ArrayList<String> list_month){

        String ret = "";

        try {
            int index = Items.ITEM_MAP.get(id).index;
            if (list_month.size() > index) {
                ret = list_month.get(index);
                return ret;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
    */
/*
    private void drawTodayEMS(){

        try {
            if (data.checkSupportElec()) {

                String today = "", yesterday = "", unit = "";
                double emsToday = 0.0, emsYesterday = 0.0, emsPercent = 0.0;

                Calendar cal = Calendar.getInstance();

                today = data.getTodayEms(Items.ELEC, cal);
                yesterday = data.getYesterdayEms(Items.ELEC, cal);

                try {
                    unit = data.getUnit(Items.ELEC);
                } catch (Exception e) {

                }

                if (unit == null || !(unit.length() > 0)) {
                    try {
                        TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.units);
                        unit = typedArray.getString(0);
                        typedArray.recycle();
                    } catch (Exception e) {

                    }
                }

                Log.d(DEBUG_TAG, "drawTodayEMS : " + "today : " + today + "/ yesterday : " + yesterday + "/unit :" + unit);

                try {

                    emsToday = Double.parseDouble(today);
                    emsYesterday = Double.parseDouble(yesterday);

                    if (emsToday == 0) emsPercent = 0; // 우선 임시 예외 처리
                    else if (emsYesterday == 0) emsPercent = emsToday;
                    else emsPercent = (emsToday * 100 / emsYesterday);

                    if (emsToday != 0 && emsYesterday == 0) {
                        emsPercent = 0.0;
                    }
                    Log.d(DEBUG_TAG, "drawTodayEMS : " + "percent : " + emsPercent + "/ emsToday : " + emsToday + "/ emsYesterday: " + emsYesterday);

                    TodayEnergyView todayEnergyView = new TodayEnergyView(mContext, Items.ELEC, emsToday, emsPercent, unit);

                    Message addViewMsg = mHandler.obtainMessage();
                    addViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW;
                    addViewMsg.obj = todayEnergyView;
                    mHandler.sendMessage(addViewMsg);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    */

    private void fillView() {

        try {
            Message hide = mHandler.obtainMessage();
            hide.what = HandlerEvent.EVENT_HANDLE_HIDE_PROGRESS;
            mHandler.sendMessage(hide);

            Message addViewMsg = mHandler.obtainMessage();
            addViewMsg.what = HandlerEvent.EVENT_HANDLE_FILL_VIEW;
            mHandler.sendMessage(addViewMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean setConnection() {        //To use DataUsage Class(Soap Communication)
        SettingValues settings;
        try {
            Log.d(DEBUG_TAG, "setConnection : " + "Context : " + mContext);
            if (mContext == null) return false;
            settings = new SettingValues(mContext);

            String serverip = settings.getLocalServerIp();
            String dong = settings.getDong();
            String ho = settings.getHo();

            if (!(serverip.length() > 0)) {
                Log.d(DEBUG_TAG, "setConnection : " + "fail to load server IP");
                return false;
            }
            if (!(dong.length() > 0)) {
                Log.d(DEBUG_TAG, "setConnection : " + "fail to load dong");
                return false;
            }
            if (!(ho.length() > 0)) {
                Log.d(DEBUG_TAG, "setConnection : " + "fail to load ho");
                return false;
            }

            data = new UsageData(mContext, serverip, dong, ho);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void getProjectOptions() {
        try {

            try {
                FileEx io = new FileEx();
                String[] files = null;

                try {
                    files = io.readFile(NameSpace.PXD_CONFIG_PATH);
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

                try {
                    for (int i = 0; i < files.length; i++) {
                        String line = files[i];
                        if (line.contains(NameSpace.CONFIG_MODEL_TYPE)) {
                            projectOptions.str_model_type = line.replace(NameSpace.CONFIG_MODEL_TYPE + "=", "");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(DEBUG_TAG, "getProjectOptions model : " + projectOptions.str_model_type);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}