package com.commax.login.LocalServer;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.commax.login.Common.AboutFile;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-10-31.
 */
public class BeforeServer {

    private static final String TAG = BeforeServer.class.getSimpleName();

    AboutFile aboutFile = new AboutFile();

    //get the ID/PWD from before server (not cloud)
    public BeforeServer() {
    }

    public Bundle get_id_password(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        Log.d(TAG, "Access Register");
        Bundle bundle = new Bundle();

        try {
            //num : 1 user register
            //parmas[0] :type , [1] :url , [2] : mac

          /*  try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);

                Log.d(TAG, "urlConnection exe");

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "urlConnection fail");
                throw new IOException();
            }
*/

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
                String id_str = "id";
                String password_str = "password";

                try {

                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);

                        try {
                            id_str = jsonObject.getString(id_str);
                            password_str = jsonObject.getString(password_str);

                            bundle.putString("id",id_str);
                            bundle.putString("password",password_str);

                            Log.d(TAG, " id / password : " + id_str + "/" +password_str);
                            Log.e(TAG, "get ID/pwd success !!! ");

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, " json error");

                        }
                    } else {
                        Log.d(TAG, "ret.length() <= 0 else exe");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
            {
                Log.d(TAG, "status : " + status);
                InputStream is;
                ByteArrayOutputStream baos;
                try
                {
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
                }catch (Exception e)
                {
                    Log.d(TAG, "return null");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " Catch Exception() ");
            e.printStackTrace();
           /*
           //TODO toast message
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.check_internet_connect));
            mHandler.sendMessage(msg);*/
        } finally {
            urlConnection.disconnect();
        }

        return bundle;
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
