package com.commax.login.Uracle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.R;
import com.commax.login.SubActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by OWNER on 2016-07-14.
 */
public class ChangePassword {
    int status;
    String TAG = ChangePassword.class.getSimpleName();
    AboutFile aboutFile = new AboutFile();
    RequestToken requestToken = new RequestToken();
    String password;

    public ChangePassword() {

    }

    public void password_change(HttpURLConnection urlConnection, String pwd, Handler mHandler) {
        status = 0;
        // password 의 길이를 알기 위해서 이전 password의 값을 가지고 있는다.
        password = pwd;
        Log.d(TAG, "password change");
        try {
            urlConnection.setRequestMethod("PUT");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);

            //num : 1 user register
            JSONObject json = new JSONObject();
            JSONObject user = new JSONObject();
            OutputStream os = null;

            try {
                //password url encording
                pwd = URLEncoder.encode(pwd, "UTF-8");

                user.put("password", pwd);
                json.put("user", user);

                //비밀번호 관련된 로그는 다 지워야 한다(보안을 위해서)
                //Log.d(TAG,"json : " +json);

                Log.d(TAG, "urlConnection exe");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "urlConnection fail");
                throw new IOException();
            }
            Log.d(TAG, " check ");

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
                status = urlConnection.getResponseCode();
                Log.d(TAG, "status  : " + status);
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
                e.printStackTrace();
            }
            if (status == HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "response success !!!!!");
                //handler 처리
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                String change_success = SubActivity.getInstance().getString(R.string.passwrod_change_success);
                msg.obj = String.valueOf(change_success);
                mHandler.sendMessage(msg);
                aboutFile.writeFile("password", String.valueOf(password.length()));
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

                    if (errorMessage.equals("Invalid or Expired token")) {

                        String[] token = {"oauth", "authorize", SubActivity.getInstance().Mac_address, SubActivity.getInstance().model_name, SubActivity.getInstance().client_id, SubActivity.getInstance().client_secret};
                        SubActivity.getInstance().startTask(token);
                        String[] pwd_change = {"v1", "user/me", "13", password};
                        SubActivity.getInstance().startTask(pwd_change);

                    } else {
                        //handler 처리
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = String.valueOf(errorMessage);
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //handler 처리
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.network_error));
                    mHandler.sendMessage(msg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //handler 처리
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.check_internet_connect));
            mHandler.sendMessage(msg);

        } finally {
            urlConnection.disconnect();
        }
    }
}

