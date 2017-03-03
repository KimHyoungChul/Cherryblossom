package com.commax.onvif_device_manager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.commax.onvif_device_manager.uitls.ProgressDialogManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by OWNER on 2016-11-02.
 */

public class NetworkModeFinder {

    //네트워크가 DHCP를 사용하는지 고정 IP를 사용하는지 체크
    private static final long DELAY_TIME = 5000;
    private static final String DHCP_INFO_FILE_PATH = "/user/app/bin/dhcp_info.i";
    public static final int DHCP_NOT_EXIST = 0;
    public static final int DHCP_EXIST = 1;
    private static final String LOG_TAG = "dhcpDiscoverTest";

    private final Context mContext;
    private NetworkModeListener mListener;

    public NetworkModeFinder(Context context) {
        mContext = context;
        try {
            mListener = (NetworkModeListener) context;
        } catch (ClassCastException e) {
            Log.d(LOG_TAG, "ClassCastException: " + e.getMessage());
        }
    }

    public void run() {
        showProgressDialog();
        runDhcpDiscover();
    }

    /**
     * 로딩바 표시
     */
    private void showProgressDialog() {
        ProgressDialogManager.showProgessDialog(mContext, "DHCP 서버 찾는 중...");
    }


    /**
     * 5초 동안 dhcp discover 수행
     */

    private void runDhcpDiscover() {


        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;


        try {
            //루팅이 안되었으면 오류 발생
            //su 명령을 실행하지 않으면 권한 문제로 socket fail 발생
            process = Runtime.getRuntime().exec("su");

            //DataOutputStream을 사용해야지 Runtime.getRuntime().exec를 사용하면 최고관리자 권한을 행사할 수 없는 듯함
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());


            dataOutputStream.writeBytes("./dhcp_discover_daemon\n");
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();


            int av = -1;
            while (av != 0) {

                //로그 출력(LOG와 다른 것임)
                av = dataInputStream.available();
                if (av != 0) {
                    byte[] b = new byte[av];
                    dataInputStream.read(b);
                    System.out.println(new String(b));
                }
            }


        } catch (Exception e) {
            ProgressDialogManager.removeProgressDialog();
            Log.d(LOG_TAG, "오류: " + e.getMessage());
            return;

        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }

                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                ProgressDialogManager.removeProgressDialog();
                Log.d(LOG_TAG, "오류: " + e.getMessage());
                return;
            }
        }


        //5초 딜레이를 준 다음 DHCP 서버 유무 표시
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressDialogManager.removeProgressDialog();
                assignFilePermission();
                mListener.onFind(getDhcpDiscoverResult());
            }
        }, DELAY_TIME);


    }


    /**
     * dhcp_info.i파일을 권한문제로 읽지 못해서 권한 부여
     */

    private void assignFilePermission() {

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;


        try {
            //루팅이 안되었으면 오류 발생
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());


            dataOutputStream.writeBytes("chmod 777 /user/app/bin/dhcp_info.i\n");
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();


            int av = -1;
            while (av != 0) {

                //로그 출력(LOG와 다른 것임)
                av = dataInputStream.available();
                if (av != 0) {
                    byte[] b = new byte[av];
                    dataInputStream.read(b);
                    System.out.println(new String(b));
                }
            }


        } catch (Exception e) {


            Log.d(LOG_TAG, "오류: " + e.getMessage());


        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }

                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {


                Log.d(LOG_TAG, "오류: " + e.getMessage());

            }
        }


    }


    /**
     * DHCP Discover 결과 가져옴
     *
     * @return
     */
    private int getDhcpDiscoverResult() {


        final String SYM_EQUAL = "=";
        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(DHCP_INFO_FILE_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                String[] tokens = null;


                while ((receiveString = bufferedReader.readLine()) != null) {

                    if (!receiveString.contains(SYM_EQUAL)) {
                        return DHCP_NOT_EXIST;
                    }

                    tokens = receiveString.split(SYM_EQUAL);

                    if (tokens[1] != null && tokens[1].equals("enabled")) {
                        return DHCP_EXIST;
                    }
                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Can not read file: " + e.toString());
        }
        return DHCP_NOT_EXIST;
    }
}
