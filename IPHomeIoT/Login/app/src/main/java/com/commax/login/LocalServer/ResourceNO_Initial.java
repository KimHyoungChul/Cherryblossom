package com.commax.login.LocalServer;

import android.os.Handler;
import android.util.Log;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.TypeDef;

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
public class ResourceNO_Initial {
    // 현재 사용안함
    private static final String TAG = ResourceNO_Initial.class.getSimpleName();

    public static int TIMEOUT_VALUE = 10000;   // 10초
    int overlap_error = 0;
    AboutFile aboutFile = new AboutFile();

    public ResourceNO_Initial() {

    }

    public void resource_number_initial(HttpURLConnection urlConnection, String[] params, Handler mHandler) throws IOException {
        //get 방식으로 params 값을 사용하지 않는다.
        //로컬 서버에 resourceNo 등록 해 놓은걸 삭제한다.

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

            try {
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int status = 0;
            try {
                status = urlConnection.getResponseCode();
            } catch (IOException e) {
                status = urlConnection.getResponseCode();
                e.printStackTrace();
            }

            if (status == HttpURLConnection.HTTP_OK) {
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

                String ret = readStream(in);
                Log.d(TAG, "ret : " + ret);

                Log.e(TAG, "response success !!!!!");
                aboutFile.writeFile(TypeDef.resourceNo_send, TypeDef.No);

                try {
                    in.close();
                    urlConnection.disconnect();
                } catch (IOException e) {
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

                try {
                    is.close();
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                  /*
                try {
                    //handler 처리
                    //로컬 서버 연동에 대한 에러 메세지는 사용자가 알 필요 없는 사항일아 토스트 메세지 표시 안하기로 talk with 권희훈 책임님
                    Message msg = mHandler.obtainMessage();
                    msg.what = overlap_error;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.check_internet_connect));
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        }catch (Exception e)
        {
            e.printStackTrace();
           /*
           //로컬 서버 연동에 대한 에러 메세지는 사용자가 알 필요 없는 사항일아 토스트 메세지 표시 안하기로 talk with 권희훈 책임님
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.check_internet_connect));
            mHandler.sendMessage(msg);*/
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
