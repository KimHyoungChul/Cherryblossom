package com.commax.login.Uracle;

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
 * Created by OWNER on 2016-07-25.
 */
public class GetProfile {
    String TAG = GetProfile.class.getSimpleName();

    public GetProfile() {
    }

    public String[] countryCode;
    public String[] englishName;
    public String[] countryName;
    String user = "user";
    String country = "country";
    int status;

    //TODO 프로필 조회 대비용
    public void GetProfile_information(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        status = 0;
        try {
            try {
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
            }
            Log.d(TAG, "status : " + status);

            InputStream in = null; //서버에서 return 값 들어옴
            Log.d(TAG, "InputStream initial");
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
                Log.d(TAG, String.valueOf(in));
            } catch (IOException e) {
                Log.e(TAG, "Check server status ");
                e.printStackTrace();
            }
            String ret = readStream(in);

            JSONObject jsonObject;
            JSONObject jsonObject1;

            if (ret.length() > 0) {
                try {
                    Log.d(TAG, "ret :" + ret);
                    jsonObject = new JSONObject(ret);

                    user = jsonObject.getString("user");
                    Log.d(TAG, "user : -> " + user);

                    jsonObject1 = new JSONObject(user);

                    String userNo = jsonObject1.getString("userNo");
                    String username = jsonObject1.getString("username");
                    String countryCode = jsonObject1.getString("countryCode");

                    Log.d(TAG, "userNo : " + userNo + ", username : " + username + " , countryCode : " + countryCode );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "ret.length() <= 0 else exe");
                //TODO toast message

                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_error));
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            Log.d(TAG, " error");
            e.printStackTrace();
            Message msg = mHandler.obtainMessage();
            msg.what = 2;
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
