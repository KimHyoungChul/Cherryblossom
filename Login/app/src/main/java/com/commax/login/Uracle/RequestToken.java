package com.commax.login.Uracle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.MainActivity;
import com.commax.login.R;
import com.commax.login.SubActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-07-14.
 */
public class RequestToken {
    String TAG = RequestToken.class.getSimpleName();
    AboutFile aboutFile = new AboutFile();
    int status;

    public RequestToken() {

    }

    public void GetToken(HttpURLConnection urlConnection, String params[], Handler mHandler) {
        Log.d(TAG, "Get Token ");
        //parmas[0] :type , [1] :param1 , [2] : mac , [3] : model name , [4] : client_id , [5] :client_secret
        status = 0;
        try {

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            StringBuffer buffer = new StringBuffer();
            Log.d(TAG, "client_id : " + params[4]);
            buffer.append("client_id").append("=").append(params[4]).append("&");                 // php 변수에 값 대입
            buffer.append("client_secret").append("=").append(params[5]).append("&");   // php 변수 앞에 '$' 붙이지 않는다
            buffer.append("grant_type").append("=").append("client_credentials").append("&");           // 변수 구분은 '&' 사용
            buffer.append("mac").append("=").append(params[2]);

            //저장된 mac에 대해서 토큰 요청
           /* String mac = aboutFile.readFile("mac_address");
            mac.replace("\"","");
            Log.d(TAG, "mac : " + mac);
            buffer.append("mac").append("=").append(mac);*/

            Log.d(TAG, "buffer : " + buffer);
            OutputStreamWriter outStream = new OutputStreamWriter(urlConnection.getOutputStream(), "EUC-KR");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();

            writer.close();
            outStream.close();

            try {
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
            }


            if (status == HttpURLConnection.HTTP_OK) {
                InputStream in; //서버에서 return 값 들어옴
                Log.d(TAG, "InputStream initial");
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    Log.d(TAG, "in : " + String.valueOf(in));
                } catch (IOException e) {
                    Log.e(TAG, "서버를 다시 확인해주세요");
                    //TODO ip error
                    e.printStackTrace();
                    throw new IOException();
                }
                String token_type = null;
                String access_token = null;
                String ret = readStream(in);
                JSONObject jsonObject;
                JSONObject jsonObject1;
                JSONObject jsonObject_server;
                JSONObject jsonObject_access;

                String ns = "ns";
                String ews = "ews";
                String server = "server";
                String access = "access";
                String access_ssl = "ssl";
                String access_ip = "ip";
                String access_port = "port";


                try {
                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);
                        try {
                            token_type = jsonObject.getString("token_type");
                            Log.d(TAG, "token_type : -> " + token_type);
                            access_token = jsonObject.getString("access_token");
                            Log.d(TAG, "access_token : -> " + access_token);
                            try
                            {
                                jsonObject1 = jsonObject.getJSONObject("ucServer");
                                ns = jsonObject1.getString(ns);
                                ews = jsonObject1.getString(ews);

                                Log.e(TAG, "ucServer :  ns/ews  =>" + ns +"/ "+ ews);
                                aboutFile.writeFile("ews",ews);
                                aboutFile.writeFile("ns",ns);

                                jsonObject_server = jsonObject.getJSONObject("server");
                                jsonObject_access = jsonObject_server.getJSONObject("access");

                                access_ssl = jsonObject_access.getString(access_ssl);
                                access_ip = jsonObject_access.getString(access_ip);
                                access_port = jsonObject_access.getString(access_port);

                                Log.d(TAG, "ssl / ip / port = " + access_ssl + access_ip+ access_port);

                                aboutFile.writeFile("access_ssl" , access_ssl);
                                aboutFile.writeFile("access_ip", access_ip);
                                aboutFile.writeFile("access_port", access_port);
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            aboutFile.writeFile("token", access_token);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "jsonObject.getString(woeidinfo)");
                        }
                    } else {
                        Log.d(TAG, "ret.length() <= 0 else exe");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    MainActivity.getInstance().resource_check = true;
                    Log.d(TAG, "resourxe_check = true  -> resource exist");
                } catch (Exception e) {
                    Log.d(TAG, "resource_check flag error");
                }

                Log.e(TAG, "response success !!!!!");

            } else {
                Log.d(TAG, "else");
                try {
                    Log.d(TAG, "status : " + status);
                    InputStream is;
                    ByteArrayOutputStream baos;
                    is = urlConnection.getErrorStream();
                    baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData = null;
                    int nLength = 0;
                    while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                        baos.write(byteBuffer, 0, nLength);
                    }

                    byteData = baos.toByteArray();
                    String response = new String(byteData);
                    Log.i(TAG, "DATA response = " + response);

                    JSONObject responseJSON = new JSONObject(response);
                    String errorCode = responseJSON.getString("errorCode");
                    Log.d(TAG, "errorCode : -> " + errorCode);
                    String errorMessage = responseJSON.getString("errorMessage");
                    Log.d(TAG, "errorMessage : -> " + errorMessage);

                    try {
                        MainActivity.getInstance().resource_check = false;
                        Log.d(TAG, "resourxe_check = flase  -> resource not exist");

                    } catch (Exception e) {
                        Log.d(TAG, "flag error");
                        String[] token = {"oauth", "authorize", SubActivity.getInstance().Mac_address, SubActivity.getInstance().model_name};
                        SubActivity.getInstance().startTask(token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO Network 문제
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_error));
                    mHandler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.check_internet_connect));
            mHandler.sendMessage(msg);
        } finally {
            urlConnection.disconnect();
        }
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
