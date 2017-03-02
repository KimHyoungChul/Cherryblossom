package com.commax.login.UC;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.TypeDef;
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
public class UC_User_Register {

    private static final String TAG = UC_User_Register.class.getSimpleName();
    //    URL url;
//    HttpURLConnection urlConnection = null;
    public static int TIMEOUT_VALUE = 10000;   // 10초
    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();

    //1 : Register ID , resources
    public UC_User_Register() {
    }

    public void UC_User_Register(HttpURLConnection urlConnection, String[] params, Handler mHanler) throws IOException {
        Log.i(TAG, "UC User Register");

        try {
            //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
            /*-
                                    -domainID: 사이트코드_UUID_동_호_W
                                    - lastName: 동
                                    - firstName: 호
                                    - displayInfo: 동_호
                                    - bizGroup: 사이트코드
                                    - groupID: 사이트코드_UUID_동_호_G
                                    - cellPhone: 동호(숫자만 입력)
                                    - localExtension: 동호 (숫자만 입력)
                                    - password: linuxonchip
                                    - position: worker
                                    - email: 동호@commax.com
                                    - domainName: commax.com
                                    - positionValue: 10
                                    - phoneNumber: 동호(숫자만 입력)
                                    - networkType: "0"
                                    - fax: ""
                                    - forwardingLevel: "1"
                                    - mediumNumber: ""
                                    - macAddress: ""
                                    - phoneType: ""
                                    - phoneCfgFile: ""
                                    - staticIp: ""
                                    - callwaiting: "0"
                                    - anonymous: "0"
                                    - displaycaller: "0"
                                    - chargingType: "0"
            */

            /*
            * 에러 메세지
            * result_code	result_msg	설명
                1	OK	정상처리
                2	PARAMETER_INVALID	파라미터 불일치
                3	DB_CONN_ERR	DB 연결 실패
                4	DB_SQL_ERR	DB 쿼리 실행 오류
                5	PROCESS_ERR	처리 실패
                6	EXCEPTION	Exception 오류
                8	DUPLICATE_USER	중복 사용자 존재 오류
                16	DUPLICATE_LOCALEXTENSION	중복 내선 번호 오류
                20	It exceeds the allowed account.	허용된 계정 수를 초과합니다.
                21	FMC subscriber number can not be greater than the maximum number of subscribers.	FMC 가입자 수가 FMC 최대 가입자 수 보다 많을 수 없습니다.
                22	The presence of duplicate MAC addresses.	중복된 MAC 주소가 존재합니다.
                23	ProvId duplicate exists.	중복된 provId 가 존재합니다.
                    (추후 지정)
            * */
            JSONObject json = new JSONObject();
            JSONObject arguments = new JSONObject();
            OutputStream os = null;

            String site_resourceNo_dong_ho_W;
            if(TypeDef.UC_Guard_register)
            {
                //경비실기
                site_resourceNo_dong_ho_W = params[3] +"_"+ params[6]+"_"+params[4]+"_"+params[5]+"_GP";
            }
            else
            {
                //월패드
                site_resourceNo_dong_ho_W = params[3] + "_" + params[6] + "_" + params[4] + "_" + params[5] + "_W";
            }

            String site_resourceNo_dong_ho_G = params[3] + "_" + params[6] + "_" + params[4] + "_" + params[5] + "_G";
            String dong_ho = params[4] + "_" + params[5];
            String password = "linuxonchip";
            String position = "worker";

            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                try {
                    if (params[2].equals("ADD")) {
                        arguments.put("domainID", site_resourceNo_dong_ho_W);
                        arguments.put("lastName", params[4]);
                        arguments.put("firstName", params[5]);
                        arguments.put("displayInfo", dong_ho);
                        arguments.put("bizGroup", params[3]);
                        arguments.put("groupID", site_resourceNo_dong_ho_G);
                        arguments.put("cellPhone", params[4] + params[5]);
                        arguments.put("localExtension", params[4] + params[5]);
                        arguments.put("password", password);
                        arguments.put("position", position);
                        arguments.put("email", params[4] + params[5] + "@" + params[7]);
                        arguments.put("domainName", params[7]);
                        arguments.put("positionValue", "10");
                        arguments.put("phoneNumber", params[4] + params[5]);
                        arguments.put("networkType", "0");
                        arguments.put("fax", "");
                        arguments.put("forwardingLevel", "1");
                        arguments.put("mediumNumber", "");
                        arguments.put("macAddress", "");
                        arguments.put("phoneType", "");
                        arguments.put("phoneCfgFile", "");
                        arguments.put("staticIp", "");
                        arguments.put("callwaiting", "0");
                        arguments.put("anonymous", "0");
                        arguments.put("displaycaller", "0");
                        arguments.put("chargingType", "0");
                    } else if (params[2].equals("DELETE")) {
                        arguments.put("domainID", site_resourceNo_dong_ho_W);
                        arguments.put("groupID", site_resourceNo_dong_ho_G);
                        arguments.put("bizGroup", params[3]);
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

                            if (result_code.equals("1"))
                            {
                                Log.e(TAG, "User response success !!!!!");
                                if (params[2].equals("ADD"))
                                {
                                    aboutFile.writeFile(TypeDef.UC_User_Register, TypeDef.yes);
                                    if(!TextUtils.isEmpty(aboutFile.readFile(TypeDef.access)))
                                    {
                                        //UC 사용자 등록 까지 진행이 완료되면 프로그레스 바 멈춤
                                        mHanler.sendEmptyMessage(1);
                                    }
                                    else
                                    {
                                        //UC는 가입이 되었고 Access가 가입되지 않은 경우 다이얼로그 띄움
                                        mHanler.sendEmptyMessage(3);
                                    }
                                }
                                else if (params[2].equals("DELETE"))
                                {
                                    //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
                                    String[] group_delete = {"uc_group", aboutFile.readFile("ews")+SubActivity.getInstance().readCloudDNSfile("UC_Group_port"), "DELETE", params[3], params[4], params[5], params[6], params[7]};
                                    SubActivity.getInstance().startTask(group_delete);
                                }
                                SubActivity.getInstance().repeat_count_user = 0;
                            }
                            else if (result_code.equals("8"))
                            {
                                if (params[2].equals("ADD"))
                                {
                                    Log.d(TAG, "result msg " + result_msg);
                                    aboutFile.writeFile(TypeDef.UC_User_Register, TypeDef.yes);
                                    if(aboutFile.readFile(TypeDef.access).equals(TypeDef.yes))
                                    {
                                        //UC 사용자 등록 까지 진행이 완료되면 프로그레스 바 멈춤
                                        mHanler.sendEmptyMessage(1);
                                    }
                                    else
                                    {
                                        //UC는 가입이 되었고 Access가 가입되지 않은 경우 다이얼로그 띄움
                                        mHanler.sendEmptyMessage(3);
                                    }
                                }
                            }
                            else
                            {
                                Log.e(TAG, " user register error");
                                //TODO ADD , DELETE 를 분리해서 응답 처리?
                                if (params[2].equals("ADD"))
                                {
                                    mHanler.sendEmptyMessage(3);
                                }
                                else if(params[2].equals("DELETE"))
                                {
                                    //delete 에러시 토스트 메세지
                                    Message msg = mHanler.obtainMessage();
                                    msg.what = overlap_error;
                                    msg.obj = String.valueOf("User Register Error :" + result_msg);
                                    mHanler.sendMessage(msg);
                                }
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
                Log.e(TAG, "response success !!!!!");
            }
            else
            {
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
                        //TODO ADD  , DELETE 를 분리할 필요성이 있는지 없는지 판단해 보기
                        if (SubActivity.getInstance().repeat_count_user <= 2)
                        {
                            SubActivity.getInstance().startTask(params);
                            SubActivity.getInstance().repeat_count_user++;
                        }
                        else if (SubActivity.getInstance().repeat_count_user > 2)
                        {
                            Log.d(TAG, " ADD error");
                            SubActivity.getInstance().repeat_count_user = 0;
                            mHanler.sendEmptyMessage(3);

                        }
                    } else if (params[2].equals("DELETE"))
                    {

                        if (SubActivity.getInstance().repeat_count_user <= 2) {
                            SubActivity.getInstance().startTask(params);
                            SubActivity.getInstance().repeat_count_user++;
                        }
                        else if (SubActivity.getInstance().repeat_count_user > 2)
                        {
                            SubActivity.getInstance().repeat_count_user = 0;
                            //delete 시 에러는 토스트로 표출
                            Message msg = mHanler.obtainMessage();
                            msg.what = 0;
                            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.mac_error));
                            mHanler.sendMessage(msg);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    if (params[2].equals("ADD"))
                    {
                        // dialog
                        mHanler.sendEmptyMessage(3);
                    }
                    else if(params[2].equals("DELETE"))
                    {
                        //delete 에러시 토스트 메세지
                        //network error
                        Message msg = mHanler.obtainMessage();
                        msg.what = overlap_error;
                        msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.network_error));
                        mHanler.sendMessage(msg);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //타임아웃일 경우에는 ADD 이건 DELETE 이건 3번 요청 진행
            if(TypeDef.uc_user_try_count <= 2)
            {
                SubActivity.getInstance().startTask(params);
                TypeDef.uc_user_try_count ++;
            }
            else if(TypeDef.uc_user_try_count > 2)
            {
                TypeDef.uc_user_try_count = 0;

                //TODO access , UC 가입 안된 경우에 다이얼로그 표출
                if (params[2].equals("ADD"))
                {
                    // dialog
                    mHanler.sendEmptyMessage(3);
                }
                else if(params[2].equals("DELETE"))
                {
                    //delete 에러시 토스트 메세지
                    Message msg = mHanler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.mac_error));
                    mHanler.sendMessage(msg);
                }
            }

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
