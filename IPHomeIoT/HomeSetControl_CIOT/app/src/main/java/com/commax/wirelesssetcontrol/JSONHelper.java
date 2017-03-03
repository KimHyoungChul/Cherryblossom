package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

public class JSONHelper {


    public static int TIMEOUT_VALUE = 20000;   // 5초

    public static String restCall(Context mContext, String LocalIp, String cloud_svr, String local_port, String type, String param1, String param2, int param3) throws IOException, IllegalArgumentException {
        String serverip = "";
        //TODO Release시 아래 2줄 주석 제거
        if(LocalIp.equals("127.0.0.1")) serverip=cloud_svr;
        else serverip=LocalIp+local_port;

//        if(NameSpace.TEST){
//            serverip="220.120.109.11:4503";
//        }else {
//            serverip = cloud_svr;
//        }

        if (type==null) {
            throw new IllegalArgumentException();
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException();
        }
        URL url;
        try {
            if (type.equals("weather")) {
                //param1 = woeid, param2 = c or f
                if(param3!=0) {
                    url = new URL("http://" + serverip + "/v1/public/weather/" + param1 + "?unit=" + param2+"&limit="+param3);
                    Log.d("JSONHelper", "http://" + serverip + "/v1/public/weather/" + param1 + "?unit=" + param2+"&limit="+param3);
                }else {
                    url = new URL("http://" + serverip + "/v1/public/weather/" + param1 + "?unit=" + param2);
                    Log.d("JSONHelper", "http://" + serverip + "/v1/public/weather/" + param1 + "?unit=" + param2);
                }
            } else if (type.equalsIgnoreCase("air")) {
                url = new URL("http://" + serverip + "/v1/public/air/index?tmX=" + param1 + "&tmY=" + param2);
                Log.d("JSONHelper", "http://" + serverip + "/v1/public/air/index?tmX=" + param1 + "&tmY=" + param2);
            } else if (type.equalsIgnoreCase("health")) {
                url = new URL("http://" + serverip + "/v1/public/life/areas/" + param1+"/cold");
                Log.d("JSONHelper", "http://" + serverip + "/v1/public/life/areas/" + param1+"/cold");
            } else if (type.equalsIgnoreCase("life")) {
                url = new URL("http://" + serverip + "/v1/public/life/areas/" + param1+"/index");
                Log.d("JSONHelper", "http://" + serverip + "/v1/public/life/areas/" + param1+"/index");
            } else if (type.equals(NameSpace.YELLOWSAND)) {
                url = new URL("http://" + serverip + "/v1/public/air/yellowSand");
                Log.d("JSONHelper", "http://" + serverip + "/v1/public/air/yellowSand");
            } else  if(type.equalsIgnoreCase(NameSpace.WOEID)) {
                Locale locale;
                locale = mContext.getResources().getConfiguration().locale;
                String str_locale = locale.getCountry();
                String str_lang = locale.getLanguage();

                String location = param1;
                try {
                    location = URLEncoder.encode(param1);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("JSONHelper : ", "URL Encoding fail");
                }

                if (!TextUtils.isEmpty(str_lang)) {
                    url = new URL("http://" + serverip + "/v1/public/weather/areaCodes/" + location + "?countryCode=" + str_lang + "-" + str_locale); //TODO test2 temp
                    Log.d("JSONHelper : ", "URL : " + "http://" + serverip + "/v1/public/weather/areaCodes/" + location + "?countryCode=" + str_lang + "-" + str_locale);
                } else {
                    url = new URL("http://" + serverip + "/v1/public/weather/areaCodes/" + location + "?countryCode=" + "ko-KR"); //TODO test2 temp
                    Log.d("JSONHelper : ", "URL : " + "http://" + serverip + "/v1/public/weather/areaCodes/" + location + "?countryCode=" + "ko-KR");
                }
            }else if(type.equalsIgnoreCase(NameSpace.TMXTMY)){

                String location = param1;
                try {
                    location = URLEncoder.encode(param1);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("JSONHelper : ", "URL Encoding fail");
                }

                url = new URL("http://" + serverip + "/v1/public/air/transverseMercator/" + location);
                Log.d("JSONHelper : ", "URL : " + "http://" + serverip + "/v1/public/air/transverseMercator/" + location);

            }else if(type.equalsIgnoreCase(NameSpace.AREACODE3)){

                String location = param1;
                try {
                    location = URLEncoder.encode(param1);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("JSONHelper : ", "URL Encoding fail");
                }

                url = new URL("http://" + serverip + "/v1/public/life/city/areas/"+location+"/codes");
                Log.d("JSONHelper : ", "URL : " + "http://" + serverip + "/v1/public/life/city/areas/"+location+"/codes");

            } else {
                url = new URL("");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new IOException();
        }
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            urlConnection.setRequestProperty("Accept", "text/xml, application/xml, application/json");
            urlConnection.setRequestProperty("Accept-Language", "{ko|en|jp}");
            urlConnection.setRequestProperty("Host", ""+serverip);
            urlConnection.setRequestProperty("cmx-dvc-type", "wp");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();

        }

        try {
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
        }catch (Exception e){
            e.printStackTrace();
        }

        InputStream in;
        try {
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
        String ret = readStream(in);

        urlConnection.disconnect();
//        ret = ret.replace("\"", "");
        return ret;
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

    public static boolean checkConnected(String LocalIp, String cloud_svr) throws IOException, IllegalArgumentException{
        Log.d("JSONHelper", "checkConnected LocalIp : "+LocalIp);

        String serverip = "";
        //TODO Release시 아래 2줄 주석 제거
        if(LocalIp.equals("127.0.0.1")) serverip=cloud_svr;
        else serverip=LocalIp;
        serverip=cloud_svr;  //TODO 주석 제거해야함

        Log.d("JSONHelper", "checkConnected serverip : "+serverip);
        URL url=null;
        try {
            url = new URL("http://"+serverip);

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
            urlConnection.setRequestProperty("Content-Type",  "application/soap+xml;charset=utf-8");

            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
            urlConnection.setDoOutput(true);

            dout = new DataOutputStream(urlConnection.getOutputStream());
            dout.write(param.getBytes());

            if( urlConnection.getResponseCode()== HttpURLConnection.HTTP_OK) {
                dis= new DataInputStream(urlConnection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
