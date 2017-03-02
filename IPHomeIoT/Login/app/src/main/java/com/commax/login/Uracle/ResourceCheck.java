package com.commax.login.Uracle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.MainActivity;

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
 * Created by OWNER on 2016-07-19.
 */
public class ResourceCheck {
    // 현재 사용안함
    private static final String TAG = ResourceCheck.class.getSimpleName();

    public static int TIMEOUT_VALUE = 10000;   // 10초
    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();

    public ResourceCheck() {

    }

    public void resource_register_check(HttpURLConnection urlConnection, String[] params, Handler mHandler) {
        //parmas[0] : type , [1] : param1 , [2] :mac ,  [3] : model name , [4] : nation code
        //num : 2 user register
        JSONObject json = new JSONObject();
        JSONObject resouce = new JSONObject();
        OutputStream os = null;
        try {
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);

            try {
                resouce.put("mac", params[2]);
                resouce.put("model", params[3]);

                json.put("resource", resouce);
                json.put("countryCode", params[4]);
                json.put("client_id", MainActivity.getInstance().client_id);
                json.put("client_secret", MainActivity.getInstance().client_secret);
                Log.d(TAG, "json : " + json);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "urlConnection exe");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "urlConnection fail");
        }

        try {
            os = urlConnection.getOutputStream();
            os.write(json.toString().getBytes());
            os.flush();
            Log.d(TAG, "os success ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                    Log.d(TAG, "os close success");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            urlConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int status = 0;
        try {
            status = urlConnection.getResponseCode();
        } catch (IOException e) {
            Log.d(TAG, " error ");
            e.printStackTrace();
        }

        if (status == HttpURLConnection.HTTP_OK) {
            InputStream in = null; //서버에서 return 값 들어옴
            Log.d(TAG, "InputStream initial");
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d(TAG, "in : " + String.valueOf(in));
            } catch (IOException e) {
                Log.e(TAG, "서버를 다시 확인해주세요");
                //TODO ip error
                e.printStackTrace();
            }

            String ret = readStream(in);
            JSONObject jsonObject;
            try {
                String resourceNo = null;
                if (ret.length() > 0) {
                    Log.d(TAG, "ret :" + ret);
                    jsonObject = new JSONObject(ret);

                    try {
                        resourceNo = jsonObject.getString("resourceNo");
                        Log.d(TAG, "resourceNo : -> " + resourceNo);


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
            Log.e(TAG, "response success !!!!!");
            aboutFile.writeFile("resource", "yes");
            try {
                in.close();
                urlConnection.disconnect();
            } catch (IOException e) {
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
            try {
                while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            }

            try {
                urlConnection.setConnectTimeout(TIMEOUT_VALUE); //Timeout 5seconds
            } catch (Exception e) {
                Log.e(TAG, "Timeout error");
                e.printStackTrace();
            }
            try {
                is.close();
                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
