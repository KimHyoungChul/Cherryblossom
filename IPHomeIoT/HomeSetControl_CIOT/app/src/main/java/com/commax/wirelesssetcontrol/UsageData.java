package com.commax.wirelesssetcontrol;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

public class UsageData {

    static final String DEBUG_TAG = "UsageData";
    int TIMEOUT_VALUE = 10000;   // 5초

    String CLOUD_SERVER = "220.120.109.88";
    String LOCAL_PORT = "";       //Cloud server can change(/user/app/bin/cloud_svr.i)

	private String serverip;

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

		this.serverip=serverip;

        mContext=context;

        try {
            locale = mContext.getResources().getConfiguration().locale;
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            readCloudDNS();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * read public data server (/user/app/bin/cloud_svr.i)
     */

    private void readCloudDNS(){

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

                if(line.contains(NameSpace.KEY_LOCAL_SERVER_PORT)) {
                    local_port = line.replace(NameSpace.KEY_LOCAL_SERVER_PORT + "=", "");
                }
            }

            Log.d(DEBUG_TAG, "Cloud server DNS :" + server_dns);

            if ((server_dns != null) && (!TextUtils.isEmpty(server_dns))) {
                CLOUD_SERVER = server_dns;
            } else {
                Log.d(DEBUG_TAG, "Getting cloud server DNS failed, Crt CLOUD_SERVER : " + CLOUD_SERVER);
            }

            if((local_port!=null)&&(!TextUtils.isEmpty(local_port))) {
                LOCAL_PORT = local_port;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check public data server (e.g. air, weather...)
     * if local server is 127.0.0.1, check cloud server.
     * if not check local server:port.
     * @return true if connected
     * @throws IOException
     * @throws IllegalArgumentException
     */

    public boolean checkNetworkFlexible() throws IOException, IllegalArgumentException {
        try {
            Log.d(DEBUG_TAG, "checkNetworkFlexible LocalIp : " + serverip);

            String flexip = "";
            if (serverip.equals("127.0.0.1")) flexip = CLOUD_SERVER;
            else flexip=serverip+LOCAL_PORT;

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
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * check local server connected.
     * @return  false if local server is 127.0.0.1.
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
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * if US serve Fahrenheit data, else celsius.
     * @param days default 2 (how many days do you want read forecast?)
     * @return info data{temp, code, humidity}, location, raw data
     */
    public Pair<Pair<double[], String>, String> getWeatherData(int days){ // Pair<Pair<weather, location>, forecast_raw>

        try {
//            Log.d("getWeatherData", "locale : " + locale.getCountry());
        }catch (Exception e){
            e.printStackTrace();
        }

        double data[] = {9999, 3200, -1};           //{temp, code, humidity}
        String location = "Seoul";
        String forecast_raw = "";

        Pair<double[], String> whole_data = new Pair<>(data, location);
        String result = "";
        String woeid = "1132599";
//        String woeid = "4118"; // Toronto

        try{

            FileInputStream fis=null;
            Properties mProperty=null;
            String filename = "woeid.properties";
            File file = new File(Environment.getExternalStorageDirectory()+"/WOEID/"+filename);
            String imsi = "";

            if(file.exists()) {
//                Log.d("Property", "File exists");
                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);
                    imsi = mProperty.getProperty("woeid");

                    try{
                        if(mProperty.getProperty("location")!=null) {
                            location = mProperty.getProperty("location");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
//                    Log.d(DEBUG_TAG, "imsi : "+imsi);
                    if((!TextUtils.isEmpty(imsi))&&(TextUtils.isDigitsOnly(imsi))){
                        woeid=imsi;
                    }

                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(DEBUG_TAG, "woeid from file : " + woeid);
            }
            else{
                Log.d("Property", "File not exists");
            }
        }catch (Exception e){
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

            //forecast test

            try {
                if(days>0) {
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
        }catch (Exception e){
            e.printStackTrace();
        }

        return new Pair<>(whole_data, forecast_raw);
    }

    /**
     * Parse str by info type
     * @param type info type (e.g. AIR, WEATHER)
     * @param sub_type parse type (AIR - INDEX, WEATHER - CONDITION)
     * @param what what do you want to get (e.g. ozone, temperature)
     * @param str raw string(JSON data)
     * @return value of what
     * @throws IOException
     */

    private String restGET(String type, String sub_type, String what, String str) throws IOException {
        JSONObject jsonObject;
        String result="";

        try {
            if(str.length()>0) {
//                Log.d(DEBUG_TAG, "restGET str = " + str);
                jsonObject = new JSONObject(str);

                try{
                    String mType = jsonObject.getString(type);
                    if((type.equalsIgnoreCase(NameSpace.WEATHER))
                            ||(type.equalsIgnoreCase(NameSpace.AIR))
                            ||(type.equalsIgnoreCase(NameSpace.HEALTH))){
                        JSONObject typeObj = new JSONObject(mType);
                        if(sub_type.equalsIgnoreCase(NameSpace.FORECAST)){
                            result = typeObj.getString(sub_type);
                        }else {
                            String sub_str = typeObj.getString(sub_type);
                            result = restGETDetail(what, sub_str);
                        }
                    }else if(type.equalsIgnoreCase(NameSpace.YELLOWSAND)){
                        JSONObject typeObj = new JSONObject(mType);
                        result = typeObj.getString(sub_type);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return result;
    }

    private String restGETDetail(String what, String str) throws IOException {
        JSONObject jsonObject;
        String result="";

        try {
            if(str.length()>0) {
                jsonObject = new JSONObject(str);

                try {
                    result = jsonObject.getString(what);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){    //content.isEmpty() or ""
            e.printStackTrace();
        }
        return result;
    }

    public int getNoticeCount() throws IOException,IllegalArgumentException {

        String notice_result="";

        notice_result= SoapHelper.call(serverip,"getNotice");

        Log.d("getCount", "called + serverip : "+serverip);
        String value = notice_result;

        Log.d("getCount", "notice_result : "+notice_result);
//        value="08110812#16001800#Test1$08120813#17002000#Test2";
//        value="11#08110815#10001800#Test00$10#08110811#10001800#Test0$9#08110812#16001800#Test1$2#08120813#17002000#Test2$1#08100810#07002100#Test3";
        if(value==null) return 0;
        if(value.equalsIgnoreCase("null")) return 0;
        value = value.trim();
        if (value.contains("$")) {
            String[] array = value.split("\\$");
            return array.length;
        } else return 1;
    }

}
