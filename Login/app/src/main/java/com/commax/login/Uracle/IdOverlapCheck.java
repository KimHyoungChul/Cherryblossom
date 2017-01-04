package com.commax.login.Uracle;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.MainActivity;
import com.commax.login.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-07-13.
 */
public class IdOverlapCheck {
    String exist = null;
    public static int TIMEOUT_VALUE = 10000;   // 10초
    int status = 0;

    public IdOverlapCheck() {

    }

    String TAG = IdOverlapCheck.class.getSimpleName();

    public void Check(Context context, final HttpURLConnection urlConnection, String[] params, Handler mHandler) {
        status = 0;

        try {

            /*
                    InputStream is = urlConnection.getInputStream();        //input스트림 개방

                    StringBuilder builder = new StringBuilder();   //문자열을 담기 위한 객체
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));  //문자열 셋 세팅
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line+ "\n");
                    }

                   String result = builder.toString();

                    Log.d(TAG, "result : " + result);

              */

//            timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "myTask");
//                    Log.d(TAG, "status : " +status);
//                    status = ApiTimer.TimeOut_check(status);
//                    Log.d(TAG, "status : " +status);
//
//                }
//            }, 3000);  // 5초후 실행하고 종료

            try {
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
            }
            Log.d(TAG, "status : " + status);


            if (status == 200) {
                InputStream in; //서버에서 return 값 들어옴
                Log.d(TAG, "InputStream initial");
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    Log.d(TAG, String.valueOf(in));
                } catch (IOException e) {
                    Log.e(TAG, "서버를 다시 확인해주세요");
                    e.printStackTrace();
                    throw new IOException();
                }
                String ret = readStream(in);
                JSONObject jsonObject;

                try {

                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);

                        exist = jsonObject.getString("exist");
                        Log.d(TAG, "exist : -> " + exist);
                    } else {
                        Log.d(TAG, "ret.length() <= 0 else exe");
                    }

                    String overlap_error;
                    if (exist.equals("true")) {
                        Log.d(TAG, " unavailable ID ");
                        //flag 를 true 로 중복됨
                        MainActivity.getInstance().id_overlap_check_flag = true;
                        //handler 처리
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        overlap_error = MainActivity.getInstance().getString(R.string.unavailable_Id);
                        msg.obj = String.valueOf(overlap_error);
                        mHandler.sendMessage(msg);


                    } else if (exist.equals("false")) {
                        Log.d(TAG, "available ID");
                        //flag 를 false 중복되지 않음
                        MainActivity.getInstance().id_overlap_check_flag = false;

                        MainActivity.getInstance().before_id = params[2];
                        //handler 처리
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        overlap_error = MainActivity.getInstance().getString(R.string.available_id);
                        msg.obj = String.valueOf(overlap_error);
                        mHandler.sendMessage(msg);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "status : " + status);
                //TODO toast message
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_error));
                mHandler.sendMessage(msg);
            }

        } catch (Exception e) {
            Log.d(TAG, " error ");
            e.printStackTrace();
            //TODO toast message
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
