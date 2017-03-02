package com.commax.headerlist;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by OWNER on 2016-04-22.
 */

//TODO 박도욱 주임과과 인터페이스 정리해서 수정해야함
public class JSONHelper {

    JSONHelper(){

    }

    public static String TAG = JSONHelper.class.getSimpleName();
    AboutFile aboutFile = new AboutFile();
    //for Server array count number
    public static int list_cnt;
    public static int TIMEOUT_VALUE = 10000;   // 10초
    public static String[] getPackageName;
    public static String[] getVersionName;
    public static String[] getAppName;
    public static int first = 0;
    static Context mContext;

    public String restCall(String LocalIp, String cloud_svr, String type, String param1) throws IOException, IllegalArgumentException
    {
        String serverip = "";
        if (LocalIp.equals("127.0.0.1")) serverip = cloud_svr;
        else serverip = LocalIp;
        serverip = cloud_svr;
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException();
        }
        URL url;
        try {
            if (type.equals("test_app_info")) {
                //TODO must be editted
                //param1 = woeid, param2 = c or f
                url = new URL("http://" + serverip + "/public/" + type +".php?method=get" + param1 );

                //TODO now fake url
//                url = new URL("http://220.120.109" +
//                        ".88/public/publicinfo" +
//                        ".php?method=getWoeidList&location=%EA%B4%91%EC%A3%BC&lang=ko-KR");
//                url = new URL("http://10.14.0.1/public/test_app_info.php?method=getApplication");
                Log.d(TAG, "server URL : " + String.valueOf(url));
            } else {
                url = new URL("");
            }
        } catch (MalformedURLException e) {
            Log.e(TAG,"url error");
            throw new IOException();
        }
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.d(TAG,"urlConnection exe");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"urlConnection fail");
            throw new IOException();
        }
        try {
            urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 5seconds
            Log.d(TAG,"urlconnection Timeout exe");
        }catch (Exception e){
            Log.e(TAG,"Timeout error");
            list_cnt = 0;
            e.printStackTrace();
        }
        InputStream in; //서버에서 return 값 들어옴
        Log.d(TAG,"InputStream initial");
        try {
            in = new BufferedInputStream(urlConnection.getInputStream());
            Log.d(TAG, String.valueOf(in));
        }
        catch (IOException e)
        {
            Log.e(TAG, "서버를 다시 확인해주세요");
            //TODO ip error
//            MainActivity.getInstance().mMainHandler.sendEmptyMessage(MainActivity.getInstance().SEND_ERROR_IP);
            e.printStackTrace();
            throw new IOException();
        }
        String ret = readStream(in);
        ret = ret.replace("\"",""); // " 큰따음표를 공백으로 대체
        JSONObject jsonObject;

        /*
        //TODO from diana Kim
        try {
            String woeidinfo = "woeidinfo";
            if(ret.length()>0) {
                jsonObject = new JSONObject(ret);
                try {
                    woeidinfo = jsonObject.getString(woeidinfo);
                    Log.d(TAG,"woeidinfo : -> " +woeidinfo);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"jsonObject.getString(woeidinfo)");
                }
            catch (JSONException e){
            e.printStackTrace();
        }

              */

        try {
            String Update = "Update";
            String Period = "Period";
            if(ret.length()>0)
            {
                Log.d(TAG, "ret :" + ret);
                jsonObject = new JSONObject(ret);
                try {
                    Update = jsonObject.getString(Update);
                    //TODO period
//                    Period = jsonObject.getString(Period);

                    Log.d(TAG,"Update : -> " +Update);
//                    Log.d(TAG,"Period : -> " + Period);
                    //TODO 여기에서 자르나?

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG,"jsonObject.getString(woeidinfo)");
                }
                //TODO editted form web page
                try {

                    JSONArray jArray = new JSONArray(Update);
                    list_cnt = jArray.length();
                    Log.d(TAG,"list_cnt :" +list_cnt);
                    //TODO 주석 제거
                    getPackageName = new String[list_cnt];
                    getVersionName = new String[list_cnt];
                    getAppName = new String[list_cnt];
                    for(int i = 0 ; i < list_cnt  ; i++){
                        JSONObject jsonArrayObject = jArray.getJSONObject(i);
//                        Log.d("JSON Object", jsonArrayObject + "");
                        getAppName[i] = jsonArrayObject.getString("AppName");
                        getVersionName[i] = jsonArrayObject.getString("VersionName");
                        getPackageName[i] = jsonArrayObject.getString("PackageName");
                        Log.e(TAG, "getAppName: " + getAppName[i] +  " getVersionName : " + getVersionName[i] +" getPackageName : " +getPackageName[i]);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("get JsonArray","JsonArray Error");
                }

                //TODO period
                /*
                try {
                    JSONArray jArraytime = new JSONArray(Period);
                    JSONObject jsonArrayObject = jArraytime.getJSONObject(0);
                    String timeperiod = jsonArrayObject.getString("Period");
                    Log.d(TAG,"timeperiod : " + timeperiod);
                    int period = Integer.valueOf(timeperiod) * 1000 * 60 ; // 시간단위
                    aboutFile.writeFile("period","30000");

                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("get JsonArray","JsonArray Error");
                }
                */
                aboutFile.writeFile("period","30000"); //30초 period
            }
            else{
                Log.d(TAG,"ret.length() <= 0 else exe");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        urlConnection.disconnect(); //TODO for test
        return null;
    }

    private static String readStream(InputStream in) {
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
        Log.d("tag", total.toString());
        return total.toString();
    }
}
