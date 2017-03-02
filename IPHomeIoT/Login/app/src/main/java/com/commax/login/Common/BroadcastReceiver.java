package com.commax.login.Common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.commax.login.JSONHelper_main;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    static final String MY_ACTION = "com.commax.login.Create_account_ACTION";
    static  final String UX_ACTION = "com.commax.login.cloud.Create_account_ACTION";

    static final String TAG = "BroadcastReceiver";

    JSONHelper_main jsonHelper_main = new JSONHelper_main();
    GetMacaddress getMacaddress;
    String mac_address ;
    Context mContext;
    AboutFile aboutFile = new AboutFile();
    boolean popup_flag = true;

    Thread thread;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mContext = context;
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);


            if (action.equals(MY_ACTION)) {
                String rootUuid = intent.getStringExtra("create_account");

                Log.d(TAG, "Create_account_ACTION create_account : " + rootUuid);
            }
            else if (action.equals(UX_ACTION))
            {
                //TODO 부팅되면 파일의 위치를 못잡아와서 앱이 실행될수도 있다 해당 처리 다시 확인하기
                if(TextUtils.isEmpty(aboutFile.readFile("create_account")))
                {
                    //service 에서 10초가 넘지 않도록 해야한다.
                    thread = new Thread(new Runnable(){
                        @Override
                        public void run(){

                            while (popup_flag) {
                                try {
                                    try
                                    {
                                        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                                        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                                        Log.d(TAG, " wifi check");
                                        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
                                        if (wifi.isConnected() || mobile.isConnected()) {
                                            Log.i(TAG , "인터넷이 연결 되었습니다.");
                                            popup_flag = false;
                                            //API 호출
                                            getMacaddress = new GetMacaddress(mContext);
                                            mac_address = getMacaddress.getMacAddress();
                                            Log.d(TAG, "get Macaddress : " + mac_address);

                                            String[] get_id_pwd = {"before_server","http://www.ruvie.co.kr/member_info.html?method=memberInfo&mac=", mac_address , "boot"};
                                            jsonHelper_main.restCall(mContext, "127.0.0.1",null , get_id_pwd[0], get_id_pwd[1], get_id_pwd, null);
                                            Log.d(TAG, "working");
                                            break;

                                        } else {
                                            //TODO 인터넷 연결 안되어 있으면 타이머 설정하고 주기적으로 체크하기?
                                            Log.i(TAG , "인터넷이 연결되지 않았습니다. 다시 한번 확인해주세요");
                                            popup_flag = true;

                                        }
                                    }catch (Exception e)
                                    {
                                        Log.d(TAG, " error");
                                        e.printStackTrace();
                                    }
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    thread.start();
                    Log.d(TAG, "끝");
                }
                else
                {
                    Log.d(TAG, " 회원가입 되어 있습니다.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
