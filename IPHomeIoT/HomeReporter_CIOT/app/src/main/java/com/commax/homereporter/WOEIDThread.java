package com.commax.homereporter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;

class WOEIDThread implements Runnable {

    final String TAG = "WOEIDThread";
    String txtFromEditBox = "";
    Context mContext;
    Handler mHandler;
    String intent_type = "";

    String LOCAL_PORT = "";       //Cloud server can change(/user/app/bin/cloud_svr.i)

    public WOEIDThread(Context context, Handler handler, String text, String info_sort) {
        txtFromEditBox = text;
        mContext = context;
        mHandler = handler;
        intent_type = info_sort;
    }

    public void run() {

        try {

            if (!Thread.currentThread().isInterrupted()) {

                Thread.sleep(1);

                try {

                    boolean connected = false;

                    String server_ip = getSettingIP();
                    String cloud_svr = readCloudDNS();

                    try {
                        //check if server can connect
                        connected = JSONHelper.checkConnected(server_ip, cloud_svr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "connection check : " + connected);

                    if (connected) {

                        // search location and woeid by text
                        if(NameSpace.TEST){
                            if(intent_type.equalsIgnoreCase(NameSpace.INFO_WEATHER)){
                                getWOEID(txtFromEditBox, server_ip, cloud_svr);
                            }else if (intent_type.equalsIgnoreCase(NameSpace.INFO_AIR)){
                                getTMXTMYList(txtFromEditBox, server_ip, cloud_svr);
                            }else if (intent_type.equalsIgnoreCase(NameSpace.INFO_HEALTH_LIFE)){
                                getAreaCode(txtFromEditBox, server_ip, cloud_svr);
                            }
                        }

                    } else {

//                        // if not connected.
                        try {
                            Message workingToast = mHandler.obtainMessage();
                            workingToast.what = HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG;
                            mHandler.sendMessage(workingToast);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//
                        // network err toast
                        try {
                            Message workingToast = mHandler.obtainMessage();
                            workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_NETWORK_ERR;
                            mHandler.sendMessage(workingToast);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }catch (InterruptedException e){
            e.printStackTrace();

             try {
                Message workingToast = mHandler.obtainMessage();
                workingToast.what = HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG;
                mHandler.sendMessage(workingToast);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    private void getAreaCode(String str, String server_ip, String cloud_svr){

        try {
            if (!TextUtils.isEmpty(str)) {

                String result = "";
                String b_dong = "";
                String h_dong = "";
                String h_sido = "";
                String h_sgg = "";
                String areaCode = "";

                try{
                    result = JSONHelper.restCall(mContext, server_ip, cloud_svr, LOCAL_PORT, NameSpace.AREACODE3, str, "", 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    if(!TextUtils.isEmpty(result)) {

                        JSONObject jsonObject = new JSONObject(result);
                        String areas = jsonObject.getString(NameSpace.AREAS);

                        int array_size = 0;
                        if(NameSpace.TEST){
                            JsonTools jsonTools = new JsonTools();
                            array_size = jsonTools.restSIZE(NameSpace.AREA, areas);
                        }

                        Log.d(TAG, "array size : " + String.valueOf(array_size));

                        if(array_size==0){

                            Message workingToast = mHandler.obtainMessage();
                            workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                            mHandler.sendMessage(workingToast);

                        }else {

                            if (array_size > NameSpace.MAX_RESULT_SIZE) {
                                array_size = NameSpace.MAX_RESULT_SIZE;
                            }

                            JSONObject areasObject = new JSONObject(areas);
                            String area = areasObject.getString(NameSpace.AREA);

                            JSONArray locations = new JSONArray(area);

                            for (int i = 0; i < locations.length(); i++) {
                                String location_item = locations.getString(i);
                                JSONObject location_obj = new JSONObject(location_item);

                                //get code
//                            b_dong = location_obj.getString(NameSpace.LEGALNM);
//                            Log.d(DEBUG_TAG, "b_dong = "+b_dong);

                                h_sido = location_obj.getString(NameSpace.SIDONM);
                                Log.d(TAG, "h_dong = " + h_sido);

                                h_sgg = location_obj.getString(NameSpace.SGGNM);
                                Log.d(TAG, "h_dong = " + h_sgg);

                                h_dong = location_obj.getString(NameSpace.UMDNM);
                                Log.d(TAG, "h_dong = " + h_dong);

                                b_dong = location_obj.getString(NameSpace.LEGALNM);
                                Log.d(TAG, "b_dong = " + b_dong);

                                areaCode = location_obj.getString(NameSpace.CODE);
                                Log.d(TAG, "areaCode = " + areaCode);

//                            //get value
//                            String value = location_obj.getString(NameSpace.VALUE);
//                            Log.d(DEBUG_TAG, "value = "+value);

                                Bundle bundle = new Bundle();
                                bundle.putInt("area_size", array_size);
                                bundle.putString("h_sido" + i, h_sido);
                                bundle.putString("h_sgg" + i, h_sgg);
                                bundle.putString("h_dong" + i, h_dong);
                                bundle.putString("b_dong" + i, b_dong);
                                bundle.putString("areaCode" + i, areaCode);

                                Thread.sleep(1);

                                try {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_SEARCH_RESULT;
                                    msg.setData(bundle);
                                    mHandler.sendMessage(msg);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                    } else {
                        Message workingToast = mHandler.obtainMessage();
                        workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                        mHandler.sendMessage(workingToast);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG;
            mHandler.sendMessage(workingToast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getTMXTMYList(String str, String server_ip, String cloud_svr){

        try{
            if(!TextUtils.isEmpty(str)){

                String result = "";

                try{
                    result = JSONHelper.restCall(mContext, server_ip, cloud_svr, LOCAL_PORT, NameSpace.TMXTMY, str, "", 0);
                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.d(TAG, "getTMXTMYList = " + result);

                try{
                    if(!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject = new JSONObject(result);
                        String root_transverse = jsonObject.getString(NameSpace.TRANSVERSEMERCATOR);

                        JSONObject root_trans_obj = new JSONObject(root_transverse);
                        String sub_transverse = root_trans_obj.getString(NameSpace.TRANSVERSEMERCATOR);

                        JSONArray locations = new JSONArray(sub_transverse);

                        int tm_size = locations.length();

                        if(tm_size==0){

                            Message workingToast = mHandler.obtainMessage();
                            workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                            mHandler.sendMessage(workingToast);

                        }else {

                            Bundle bundle = new Bundle();
                            bundle.putInt("tm_size", tm_size);

                            if (tm_size > NameSpace.MAX_RESULT_SIZE) {
                                tm_size = NameSpace.MAX_RESULT_SIZE;
                            }

                            for (int index = 0; index < locations.length(); index++) {

                                try {
                                    String location_item = locations.getString(index);
                                    JSONObject location_obj = new JSONObject(location_item);

                                    //get location
                                    String location = location_obj.getString(NameSpace.LOCATION);
                                    Log.d(TAG, "location = " + location);

                                    //TODO test

                                    //get tmX, tmY
                                    String tmX = location_obj.getString(NameSpace.TMX);
                                    String tmY = location_obj.getString(NameSpace.TMY);
                                    Log.d(TAG, "tmX = " + tmX + " / tmY = " + tmY);


                                    bundle.putInt(NameSpace.TM_SIZE, tm_size);
                                    bundle.putString("location" + index, location);
                                    bundle.putString("tmX" + index, tmX);
                                    bundle.putString("tmY" + index, tmY);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                Message msg = mHandler.obtainMessage();
                                msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_SEARCH_RESULT;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        Message workingToast = mHandler.obtainMessage();
                        workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                        mHandler.sendMessage(workingToast);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG;
            mHandler.sendMessage(workingToast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getWOEID(final String str, String server_ip, String cloud_svr) throws InterruptedException{

        if (!TextUtils.isEmpty(str)){

            String result = "result";

            if((str.equalsIgnoreCase("세종"))||(str.equalsIgnoreCase("세종시"))
                    ||(str.equalsIgnoreCase("세종특별자치시"))||(str.equalsIgnoreCase("세종특별자치도"))){
                try {
                    result = JSONHelper.restCall(mContext, server_ip, cloud_svr, LOCAL_PORT, NameSpace.WOEID, "연기군", "", 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {

                try {
                    result = JSONHelper.restCall(mContext, server_ip, cloud_svr, LOCAL_PORT, NameSpace.WOEID, str, "", 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                if(!TextUtils.isEmpty(result)) {

                    int MAX_SIZE = NameSpace.MAX_RESULT_SIZE;                                  //MAX ListView size
                    JsonTools jsonTools = new JsonTools();
                    int array_size=0;

                    try {
                        array_size = jsonTools.restSIZE(NameSpace.LOCATIONS, result);
                        Log.d(TAG, "array size : " + String.valueOf(array_size));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(array_size==0){

                        Message workingToast = mHandler.obtainMessage();
                        workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                        mHandler.sendMessage(workingToast);

                    }else {


                        if (array_size > MAX_SIZE) {
                            array_size = MAX_SIZE;
                        }

                        for (int index = 0; index < array_size; index++) {


                            String location="";
                            String woeid="";

                            location = jsonTools.restGET(NameSpace.LOCATIONS, result, index, "location");
                            woeid = jsonTools.restGET(NameSpace.LOCATIONS, result, index, "woeid");

                            Log.d(TAG, "location : " + location + " / woeid : " + woeid);

                            Bundle bundle = new Bundle();
                            bundle.putInt(NameSpace.WOEID_SIZE, array_size);
                            bundle.putString(NameSpace.LOCATION + index, location);
                            bundle.putString(NameSpace.WOEID + index, woeid);

                            Thread.sleep(1);

                            try {
                                Message msg = mHandler.obtainMessage();
                                msg.what = HandlerEvent.EVENT_HANDLE_UPDATE_SEARCH_RESULT;
                                msg.setData(bundle);
                                mHandler.sendMessage(msg);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else {
                    Message workingToast = mHandler.obtainMessage();
                    workingToast.what = HandlerEvent.EVENT_HANDLE_SHOW_TOAST_RETRY;
                    mHandler.sendMessage(workingToast);
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        try {
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_DISMISS_PROGRESS_DIALOG;
            mHandler.sendMessage(workingToast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSettingIP() {

        try {
            //getting server ip
            String serverip="";
            SettingValues settings;
            settings = new SettingValues(mContext);

            serverip = settings.getLocalServerIp();
            return serverip;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readCloudDNS(){

        FileEx io = new FileEx();
        String[] files = null;
        String server_dns = "";
        String local_port = "";
        String cloud_svr="";

        try {

            //read file(/user/app/bin/cloud_svr.i)
            files = io.readFile(NameSpace.CLOUD_SERVER_INFO_FILE);

        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }

        if (files == null) {
            return null;
        }

        if(files.length>0) {
            // ���� üũ
            if (files == null) {
                return null;
            }
            if ("".equals(files[0])) {
                return null;
            }
            if ("-1".equals(files[0])) {
                return null;
            }
        }

        try {
            for (int i = 0; i < files.length; i++) {
                String line = files[i];

                if (line.contains(NameSpace.KEY_PUBLIC_SERVER_DNS)) {
                    server_dns = line.replace(NameSpace.KEY_PUBLIC_SERVER_DNS + "=", "");
                }

                if(line.contains(NameSpace.KEY_LOCAL_SERVER_PORT)) {
                    local_port = line.replace(NameSpace.KEY_LOCAL_SERVER_PORT + "=", "");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Log.d("WOEIDThread", "Cloud server DNS :"+ server_dns);

        if((server_dns!=null)&&(!TextUtils.isEmpty(server_dns))) {
            cloud_svr = server_dns;
        }else {
            Log.d("WOEIDThread", "Getting cloud server DNS failed, Crt CLOUD_SERVER : " + cloud_svr);
        }

        if((local_port!=null)&&(!TextUtils.isEmpty(local_port))) {
            LOCAL_PORT = local_port;
        }

        if(!TextUtils.isEmpty(cloud_svr)){
            return cloud_svr;
        }else {
            return null;
        }
    }
}

