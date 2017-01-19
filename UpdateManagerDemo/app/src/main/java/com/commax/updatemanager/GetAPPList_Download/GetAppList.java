package com.commax.updatemanager.GetAPPList_Download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.updatemanager.JSONHelper;
import com.commax.updatemanager.MainActivity;
import com.commax.updatemanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-12-01.
 */
public class GetAppList {

    String TAG = GetAppList.class.getSimpleName();
    JSONHelper jsonHelper = new JSONHelper();
    DevideArrayList devideArrayList;

    //for Server array count number
    public int list_cnt;
    public String[] getPackageName;
    public String[] getVersionName;
    public String[] getAppName;

    private static GetAppList instance;

    public GetAppList()
    {
    }


    public static GetAppList getInstance(){
        if(instance == null)
        {
            instance = new GetAppList();
        }
        return instance;
    }


    public void get_app_list(HttpURLConnection urlConnection, Handler mHandler , Context context) throws IOException {
        Log.d(TAG, "get_app_list");
        instance = this;
        devideArrayList = new DevideArrayList(context);
        try
        {
            InputStream in; //서버에서 return 값 들어옴
            Log.d(TAG,"InputStream initial");
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d(TAG, String.valueOf(in));
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException();
            }

            String ret = readStream(in);
            ret = ret.replace("\"",""); // " 큰따음표를 공백으로 대체
            JSONObject jsonObject;

            try {
                String Update = "Update";

                if(ret.length()>0) {
                    jsonObject = new JSONObject(ret);
                    try {
                        Update = jsonObject.getString(Update);
                        Log.d(TAG,"Update :" +Update);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG,"jsonObject.getString(Update)");
                    }

                    //TODO editted form web page
                    try {
                        JSONArray jArray = new JSONArray(Update);
                        list_cnt = jArray.length();
                        Log.d(TAG,"list_cnt :" +list_cnt);

                        getPackageName = new String[list_cnt];
                        getVersionName = new String[list_cnt];
                        getAppName = new String[list_cnt];

                        for(int i = 0 ; i < list_cnt  ; i++){
                            JSONObject jsonArrayObject = jArray.getJSONObject(i);
                            getPackageName[i] = jsonArrayObject.getString("PackageName");
                            getVersionName[i] = jsonArrayObject.getString("VersionName");
                            getAppName[i] = jsonArrayObject.getString("AppName");

                            Log.i(TAG,"array ["+i+"] packagename : "+getPackageName[i] +"        version name : " +
                                    getVersionName[i] + "       AppName:" + getAppName[i]);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG,"JsonArray Error");
                    }
                }
                else{
                    Log.d(TAG,"ret.length() <= 0 else exe");
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        catch (Exception e)
        {
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_connect_error));
            mHandler.sendMessage(msg);
            Log.d(TAG, " 인터넷이 끊김");
            e.printStackTrace();
        }

        /* 추후 Applist 들을 이름순으로 정렬이 필요할 경우 ArrayList 를 3개로 나누어서 해당 ArrayList들 마다 정렬을 적용 해야한다 .
        * 아래 devide_array_list_header는 해당 용도로 ArrayList들을 분리 해 놓은 것이다. */
//        devideArrayList.devide_array_list_header();
    }

    private String readStream(InputStream in) {

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.util.Log.d("tag", total.toString());
        return total.toString();
    }

}
