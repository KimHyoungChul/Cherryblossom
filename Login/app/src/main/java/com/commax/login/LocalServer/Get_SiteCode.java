package com.commax.login.LocalServer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.MainActivity;
import com.commax.login.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Created by OWNER on 2016-07-19.
 */
public class Get_SiteCode {
    // 현재 사용안함
    private static final String TAG = Get_SiteCode.class.getSimpleName();

    public static int TIMEOUT_VALUE = 10000;   // 10초
    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();
    String ret = null;

    public Get_SiteCode() {

    }

    public String get_site_code(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        //get 방식으로 params 값을 사용하지 않는다.
        try
        {
            try {
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                Log.d(TAG, "urlConnection exe");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "urlConnection fail");
            }

            int status = 0;
            try {
                status = urlConnection.getResponseCode();
            } catch (IOException e) {
                status = urlConnection.getResponseCode();
                Log.d(TAG, " error ");
                e.printStackTrace();
            }

            Log.d(TAG, "status :  " + status);

            if (status == HttpURLConnection.HTTP_OK)
            {
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

                ret = readStream(in);
                ret = ret.replace("\"", "");
                Log.d(TAG, "ret : " +ret);
                Log.e(TAG, "response success !!!!!");

                aboutFile.writeFile("SiteCode", ret);

            }
            else
            {
                //로컬 서버에서 200이 아닌 response 값을 보내줄때
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

                /*
                try {
                    //handler 처리
                    //TODO 인터넷 통신은 되는것인데 OK응답이 안올때
                    Message msg = mHandler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.check_internet_connect));
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.localserver_status_check));
                mHandler.sendMessage(msg);

                try {
                    is.close();
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            // 로컬 서버에서 응답이 없어서 타임 아웃 걸렸을때
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.localserver_status_check));
            mHandler.sendMessage(msg);
        }

        return ret;
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
