package com.commax.login.Uracle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-07-12.
 */
public class AccessRegister {


    private static final String TAG = AccessRegister.class.getSimpleName();

    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();

    //1 : Access Register
    public AccessRegister() {
    }

    public void RegisterID(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        Log.d(TAG, "Access Register");

        try {
            //num : 1 user register
            //params[0] : type , [1] : dong , [2] : ho , [3] : dangi server , [4] : workType new/map
            JSONObject json = new JSONObject();
            JSONObject service = new JSONObject();
            JSONObject address = new JSONObject();
            OutputStream os = null;
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                try {
                    address.put("buildingNo", params[1]);
                    address.put("roomNo", params[2]);

                    service.put("centerCode", params[3]);
                    service.put("address", address);
                    service.put("resourceType", "wallpad");

                    json.put("service", service);
                    Log.d(TAG, "json : " + json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "urlConnection exe");

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "urlConnection fail");
                throw new IOException();
            }

            try {
                os = urlConnection.getOutputStream();
                os.write(json.toString().getBytes());
                os.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }

            int status = 0;
            try {
                Log.d(TAG, "try");
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                Log.d(TAG, "catch");
                status = urlConnection.getResponseCode();
                Log.e(TAG, "status : " + status);
            }

            if (status == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "status : " + status);

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

                String ret = readStream(in);
                JSONObject jsonObject;
                JSONObject serviceObject;
                String service_name = "service";

                try {

                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);

                        try {
                            serviceObject = jsonObject.getJSONObject(service_name);
                            Log.d(TAG, "service : -> " + serviceObject);
                            service_name = serviceObject.getString("centerIp");
//                            resourceNo = jsonObject.getString("centerIp");
                            Log.d(TAG, "centerIp : -> " + service_name);

                            /*//handler 처리
                            Message msg = SubActivity.getInstance().toastHandler.obtainMessage();
                            msg.what = 0;
                            msg.obj = String.valueOf(service_name);
                            SubActivity.getInstance().toastHandler.sendMessage(msg);*/

                            aboutFile.writeFile("access", "yes");

                            Log.d(TAG, "Access register success !!! ");

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, " json error");

                            String errorCode = jsonObject.getString("errorCode");
                            String errorMessage = jsonObject.getString("errorMessage");
                            //handler 처리
                            Message msg = mHandler.obtainMessage();
                            msg.what = 0;
                            msg.obj = String.valueOf(errorMessage);
                            mHandler.sendMessage(msg);

                            Log.e(TAG, "errorCode : " + errorCode + " ,  errorMessage : " + errorMessage);
                            if (errorMessage.equals("ACCESS : 이미 등록된 리소스 입니다.")) {
                                Log.d(TAG, " already registered resource !! ");
                                aboutFile.writeFile("access", "yes");
                            }
                        }
                    } else {
                        Log.d(TAG, "ret.length() <= 0 else exe");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
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
                try {

                    JSONObject responseJSON = new JSONObject(response);
                    String errorCode = responseJSON.getString("errorCode");
                    Log.d(TAG, "errorCode : -> " + errorCode);
                    String errorMessage = responseJSON.getString("errorMessage");
                    Log.d(TAG, "errorMessage : -> " + errorMessage);


                    //handler 처리
                    //TODO toast message
                    Message msg = mHandler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(errorMessage);
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    //network error
                    Message msg = mHandler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.network_error));
                    mHandler.sendMessage(msg);

                }
            }
        } catch (Exception e) {
            Log.e(TAG, " Catch Exception() ");
            e.printStackTrace();
            //TODO toast message
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.check_internet_connect));
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
