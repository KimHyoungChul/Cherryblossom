package com.commax.login.Uracle;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.TypeDef;
import com.commax.login.MainActivity;
import com.commax.login.R;
import com.commax.login.SubActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-07-15.
 */
public class Initialize {
    String TAG = Initialize.class.getSimpleName();
    int status;
    AboutFile aboutFile = new AboutFile();

    public Initialize() {

    }

    public void Initialize_User(HttpURLConnection urlConnection, String[] params, Handler mHandler) {
        status = 0;
        String deletefile = AboutFile.CMX_data_file_path;
        try {
            urlConnection.setRequestMethod("DELETE");
//            urlConnection.setDoOutput(true);
//            urlConnection.setDoInput(true);
//            urlConnection.setUseCaches(false);

            Log.d(TAG, "status check ");

            try {
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
            }
            Log.d(TAG, " status : " + status);
            if (status == 200) {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.user_id_initialize_success));
                mHandler.sendMessage(msg);
                Log.e(TAG, "response success !!!!!");
                // intent broadcast
                Intent intent = new Intent("com.commax.login.Create_account_ACTION");
                intent.putExtra("create_account", "no");
                SubActivity.getInstance().sendBroadcast(intent);

                Log.d(TAG, "delete file : " + deletefile);
                try {
                    File file = new File(deletefile);
                    file.delete();
                    Intent intentMainActivity = new Intent(SubActivity.getInstance().getApplicationContext(), MainActivity.class);
                    SubActivity.getInstance().startActivity(intentMainActivity);
                    SubActivity.getInstance().finish();
                } catch (Exception e) {
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

                    if (errorMessage.equals("Invalid or Expired token")) {

                        String[] token = {"oauth", "authorize", SubActivity.getInstance().Mac_address, SubActivity.getInstance().model_name, SubActivity.getInstance().client_id, SubActivity.getInstance().client_secret};
                        SubActivity.getInstance().startTask(token);
                        /*//handler 처리
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.do_it_again));
                        mHandler.sendMessage(msg);*/
                        if(TypeDef.try_count_token_initial < 3)
                        {
                            String[] initialize = {"v1", "user/me" ,"14" , aboutFile.readFile("token")};
                            SubActivity.getInstance().startTask(initialize);
                            TypeDef.try_count_token_initial ++ ;
                        }
                        else if(TypeDef.try_count_token_initial >= 3)
                        {
                            //handler 처리
                            Message msg = mHandler.obtainMessage();
                            msg.what = 0;
                            msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.do_it_again));
                            mHandler.sendMessage(msg);
                            //initial
                            TypeDef.try_count_token_initial =0;
                        }

                    } else if (errorMessage.equalsIgnoreCase("Not found User Info")) {
                        //서버에서 이미 사용자를 지운 경우 파일삭제하고 초기화로 간주한다.
                        //TODO client_id 가 다른경우 다른 그룹의 사용자 이므로 not found user info 응답이 올수있다 이경우는 ?
                        File file = new File(deletefile);
                        file.delete();
                        Intent intentMainActivity = new Intent(SubActivity.getInstance().getApplicationContext(), MainActivity.class);
                        SubActivity.getInstance().startActivity(intentMainActivity);
                        SubActivity.getInstance().finish();
                    } else {
                        //handler 처리
                        Message msg = mHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = String.valueOf(errorMessage);
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.network_error));
                    mHandler.sendMessage(msg);
                }
            }
        } catch (IOException e) {
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