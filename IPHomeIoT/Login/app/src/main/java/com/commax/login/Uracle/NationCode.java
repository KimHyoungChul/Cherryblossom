package com.commax.login.Uracle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.MainActivity;
import com.commax.login.R;

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
 * Created by OWNER on 2016-07-25.
 */
public class NationCode {
    String TAG = NationCode.class.getSimpleName();

    public NationCode() {
    }

    public String[] countryCode;
    public String[] englishName;
    public String[] countryName;
    String countries = "countries";
    String country = "country";
    int status;

    public void GetNationList(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
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
            int list_cnt;

            if (ret.length() > 0) {
                try {
                    Log.d(TAG, "ret :" + ret);
                    jsonObject = new JSONObject(ret);

                    countries = jsonObject.getString("countries");
                    Log.d(TAG, "countries : -> " + countries);

                    jsonObject1 = new JSONObject(countries);
                    country = jsonObject1.getString("country");

                    JSONArray jArray = new JSONArray(country);

                    list_cnt = jArray.length();
                    Log.d(TAG, "list_cnt :" + list_cnt);
                    //TODO 주석 제거
                    countryCode = new String[list_cnt];
                    englishName = new String[list_cnt];
                    countryName = new String[list_cnt];

//                MainActivity.getInstance().mListData.add(MainActivity.getInstance().getResources().getString(R.string.select_nation));
//                MainActivity.getInstance().mListCode.add("not");
                    for (int i = 0; i < list_cnt; i++) {
                        JSONObject jsonArrayObject = jArray.getJSONObject(i);

                        countryCode[i] = jsonArrayObject.getString("countryCode");
                        englishName[i] = jsonArrayObject.getString("englishName");
                        countryName[i] = jsonArrayObject.getString("countryName");
                        MainActivity.getInstance().mListData.add(englishName[i]+ " (" +countryName[i] + ")");
//                        MainActivity.getInstance().mListData.add(englishName[i]);
                        MainActivity.getInstance().mListCode.add(countryCode[i]);


                        Log.e(TAG, "countryCode: " + countryCode[i] + ",  englishName : " + englishName[i] + " , countryName : " + countryName[i]);
                    }
                    // UI thread 에서 처리되어야 함
                    MainActivity.getInstance().spinnerAdapter();
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
//            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.check_internet_connect));
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
