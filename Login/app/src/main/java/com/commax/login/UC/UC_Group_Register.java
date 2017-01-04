package com.commax.login.UC;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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

/**
 * Created by OWNER on 2016-09-07.
 */
public class UC_Group_Register {

    private static final String TAG = UC_Group_Register.class.getSimpleName();
    //    URL url;
//    HttpURLConnection urlConnection = null;

    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();

    //1 : Register ID , resources
    public UC_Group_Register() {
    }

    public void UC_Group_Register(HttpURLConnection urlConnection, String[] params, Handler mHanler) throws IOException {
        Log.i(TAG, "UC Group Register");
        Message msg = mHanler.obtainMessage();

       /* //TODO test
        Message msg = mHanler.obtainMessage();
        msg.what = overlap_error;
        msg.obj = String.valueOf("그룹등록 호출");
        mHanler.sendMessage(msg);*/
        try {
            //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
            //num : 1 user register
            // [0] : type , [1] : ip:port , [2] : Add, delete , update ,
            // [3] : groupID =>             sitecode_UUID_dong_ho_G
            // [4] :bizGroup =>          sitecode
            // [5] : parentGroupID  =>   ""
            // [6] :displayInfo =>       dong_ho
            // [7] : localExtention=>    sitecode_UUID_dong_ho_G
            // [8] :domainName =>        ruvie.co.kr
            JSONObject json = new JSONObject();
            JSONObject arguments = new JSONObject();
            OutputStream os = null;

            String site_resourceNo_dong_ho_G = params[3] + "_" + params[6] + "_" + params[4] + "_" + params[5] + "_G";
            String dong_ho = params[4] + "_" + params[5];

            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                try {
                    if (params[2].equals("ADD")) {
                        arguments.put("groupID", site_resourceNo_dong_ho_G);
                        arguments.put("bizGroup", params[3]);
                        arguments.put("parentGroupID", params[3]);
                        arguments.put("displayInfo", dong_ho);
                        arguments.put("localExtension", site_resourceNo_dong_ho_G);
                        arguments.put("domainName", params[7]);
                    } else if (params[2].equals("DELETE")) {
                        arguments.put("groupID", site_resourceNo_dong_ho_G);
                        arguments.put("domainName", params[7]);
                    }

                    json.put("operationType", params[2]);
                    json.put("arguments", arguments);

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
                status = urlConnection.getResponseCode();
            } catch (Exception e) {
                status = urlConnection.getResponseCode();
            }
            Log.d(TAG, "status : " + status);

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

                String result_code = null;
                String result_msg = null;
                try {

                    if (ret.length() > 0) {
                        Log.d(TAG, "ret :" + ret);
                        jsonObject = new JSONObject(ret);

                        try {
                            result_code = jsonObject.getString("result_code");
                            Log.d(TAG, "result_code : -> " + result_code);
                            result_msg = jsonObject.getString("result_msg");
                            Log.d(TAG, "resourceNo : -> " + result_msg);

                            if (result_code.equals("1")) {
                                Log.e(TAG, "Group response success !!!!!");

                                if (params[2].equals("ADD")) {
                                    aboutFile.writeFile("UC_Group_Register", "yes");
                                    //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
                                    //TODO 사용자 등록 실행 해야함
                                    String[] user_register = {"uc_user", SubActivity.getInstance().uc_user_ip_port, "ADD", params[3], params[4], params[5], params[6], params[7]};
                                    SubActivity.getInstance().startTask(user_register);
                                } else if (params[2].equals("DELETE")) {
                                    //TODO 사용자 삭제 임시 주석 처리
                                    String initialize[] = {"v1", "user/me", "14", aboutFile.readFile("token")};
                                    SubActivity.getInstance().startTask(initialize);
                                }
                                SubActivity.getInstance().repeat_count = 0;
                            } else if (result_code.equals("5")) {
                                //그룹 등록 되어 있지 않는데 그룹 삭제를 요청 한 경우
                                if (params[2].equals("DELETE")) {
                                    String initialize[] = {"v1", "user/me", "14", aboutFile.readFile("token")};
                                    SubActivity.getInstance().startTask(initialize);
                                }
                            }
                            //ADD 일때 등록이 성공하지 못하면
                            else if (result_code.equals("15")) {
                                //중복 확인이 될때는 파일에 등록해주고 사용자 등록이 진행 되었는지를 체크
                                aboutFile.writeFile("UC_Group_Register", "yes");
                                if (TextUtils.isEmpty(aboutFile.readFile("UC_User_Register"))) {
                                    //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
                                    //TODO 사용자 등록 실행 해야함
                                    String[] user_register = {"uc_user", SubActivity.getInstance().uc_user_ip_port, "ADD", params[3], params[4], params[5], params[6], params[7]};
                                    SubActivity.getInstance().startTask(user_register);
                                }
                                else
                                {
                                    Log.d(TAG , " UC User 이 등록되어 있습니다.");
                                }
                            } else if (result_code.equals("6")) {
                                if (params[2].equals("ADD")) {
                                    msg = mHanler.obtainMessage();
                                    msg.what = overlap_error;
                                    msg.obj = String.valueOf("테넌트 등록 에러입니다." + result_msg);
                                    mHanler.sendMessage(msg);
                                }
                            } else {
                                Log.e(TAG, " group register error");

                                msg = mHanler.obtainMessage();
                                msg.what = overlap_error;
                                msg.obj = String.valueOf("그롭 등록 에러 입니다. " + result_msg);
                                mHanler.sendMessage(msg);
                            }

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

//                aboutFile.writeFile("create_account","yes");
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
                    String result_code = responseJSON.getString("result_code");
                    Log.d(TAG, "result_code : -> " + result_code);
                    String result_msg = responseJSON.getString("result_msg");
                    Log.d(TAG, "result_msg : -> " + result_msg);

                    if (params[2].equals("ADD")) {
                        //ADD 일때 등록이 성공하지 못하면
                        if (result_msg.equalsIgnoreCase("DUPLICATE_GROUP")) {
                            //중복 확인이 될때는 파일에 등록해주고 사용자 등록이 진행 되었는지를 체크
                            aboutFile.writeFile("UC_Group_Register", "yes");
                            if (TextUtils.isEmpty(aboutFile.readFile("UC_User_Register"))) {
                                //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
                                //TODO 사용자 등록 실행 해야함
                                String[] user_register = {"uc_user", SubActivity.getInstance().uc_user_ip_port, "ADD", params[3], params[4], params[5], params[6], params[7]};
                                SubActivity.getInstance().startTask(user_register);
                            }
                        } else {
                            if (SubActivity.getInstance().repeat_count == 0) {
                                SubActivity.getInstance().repeat_count++;
                                //중복 요인이 아닐 경우는 다시한번 그룹 등록 호출
                                String[] group_register = {params[0], params[1], "ADD", params[3], params[4], params[5], params[6], params[7]};
                                SubActivity.getInstance().startTask(group_register);
                            } else if (SubActivity.getInstance().repeat_count > 0) {
                                SubActivity.getInstance().repeat_count = 0;
                            }
                        }
                    } else if (params[2].equals("DELETE")) {
                        //DELETE 일때 삭제가 성공하지 못하면

                    }
                /*    //handler 처리
                    //TODO toast message
                    msg = mHanler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(result_msg);
                    mHanler.sendMessage(msg);*/
                } catch (Exception e) {
                    e.printStackTrace();
                    //network error
                    msg = mHanler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_error));
                    mHanler.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO toast message
            msg = mHanler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.check_internet_connect));
            mHanler.sendMessage(msg);
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
