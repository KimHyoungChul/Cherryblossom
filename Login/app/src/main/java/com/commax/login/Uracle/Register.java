package com.commax.login.Uracle;

import android.content.Intent;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by OWNER on 2016-07-12.
 */
public class Register {

    private static final String TAG = Register.class.getSimpleName();
    //    URL url;
//    HttpURLConnection urlConnection = null;
    public static int TIMEOUT_VALUE = 10000;   // 10초
    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();
    String password;

    //1 : Register ID , resources
    public Register() {
    }

    public void RegisterID(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        Log.d(TAG, "Register");

        try {
            //num : 1 user register
            //params[0] : type , [1] : param1 , [2] : name , [3] : id , [4] :password , [5] : mac ,[6] :model name , [7] : nation code , [8] : new/ map
            JSONObject json = new JSONObject();
            JSONObject resouce = new JSONObject();
            JSONObject user = new JSONObject();
            OutputStream os = null;
            //for password length
            password = params[4];
            params[4] = URLEncoder.encode(params[4], "UTF-8");
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                try {
                    resouce.put("mac", params[5]);
                    resouce.put("model", params[6]);

                    user.put("username", params[3]);
                    user.put("password", params[4]);
                    user.put("name", params[2]);

                    json.put("resource", resouce);
                    json.put("user", user);
                    json.put("countryCode", params[7]);
                    json.put("workType", params[8]);
                    json.put("client_id", MainActivity.getInstance().client_id);
                    json.put("client_secret", MainActivity.getInstance().client_secret);
                    //비밀번호관련 로그로 주석 처리
//                    Log.d(TAG, "json : " + json);
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

                String ret = readStream(in);
                JSONObject jsonObject;

                String userNo = null;
                String resourceNo = null;
                try {

                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);

                        try {
                            userNo = jsonObject.getString("userNo");
                            Log.d(TAG, "userNo : -> " + userNo);
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

                // 가입 완료되면 토큰 요청 후 저장
//            String[] gettoken = { "oauth","authorize", params[5] ,MainActivity.getInstance().model_name};
//            MainActivity.getInstance().startTask(gettoken);

                aboutFile.writeFile("create_account", "yes");
                aboutFile.writeFile("id", params[3]);
                //비밀번호 관련 로그로 주석처리 진행
//                Log.d(TAG, "password : " + password);
                //TODO 자릿수로 임시 처리
                aboutFile.writeFile("password", String.valueOf(password.length()));
                aboutFile.writeFile("resourceNo", resourceNo);
                aboutFile.writeFile("mac_address", params[5]);
                //2016-11-11  Access API변동으로 인한 수정 사항
                aboutFile.writeFile("workType",params[8]);
                aboutFile.writeFile("ProductModel", params[6]);

                //send broadcast
                Intent intent = new Intent("com.commax.login.Create_account_ACTION");
                intent.putExtra("create_account","yes");
                MainActivity.getInstance().sendBroadcast(intent);


                //handler 처리 가입완료 Toast 메세지
                Message msg = mHandler.obtainMessage();
                msg.what = overlap_error;
                msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.register_success));
                mHandler.sendMessage(msg);

                Intent intentSubActivity = new Intent(MainActivity.getInstance().getApplicationContext(), SubActivity.class);
                MainActivity.getInstance().startActivity(intentSubActivity);
                //TODO must be editted
                MainActivity.getInstance().finish();
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
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_error));
                    mHandler.sendMessage(msg);

                }

                        /*
                        Log.d(TAG, " status : " + status);
                        InputStream in; //서버에서 return 값 들어옴
                        try {
                            in = new BufferedInputStream(urlConnection.getInputStream());
                            Log.d(TAG, "in : " + String.valueOf(in));

                        }
                        catch (IOException e)
                        {
                            Log.e(TAG, "서버를 다시 확인해주세요");
                            //TODO ip error
                            e.printStackTrace();
                            throw new IOException();
                        }
                        JSONObject jsonObject;
                        String ret = readStream(in);
                        Log.d(TAG, "ret : " + ret);
                        try {
                            String errorCode = null;
                            String errorNessage = null;
                            if(ret.length()>0)
                            {
                                Log.d(TAG, "ret :" + ret);
                                jsonObject = new JSONObject(ret);

                                try {
                                    errorCode = jsonObject.getString("errorCode");
                                    Log.d(TAG,"userNo : -> " + errorCode);
                                    errorNessage = jsonObject.getString("errorNessage");
                                    Log.d(TAG, "resourceNo : -> " + errorNessage);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG,"jsonObject.getString(woeidinfo)");
                                }
                            }
                            else{
                                Log.d(TAG,"ret.length() <= 0 else exe");
                            }

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    */

            }
        } catch (Exception e) {
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
