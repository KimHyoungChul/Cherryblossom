package com.commax.login.Common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;

/**
 * Created by OWNER on 2016-08-01.
 */
public class ApiTimer {
    //사용 안함
    static String TAG = ApiTimer.class.getSimpleName();
    static public Timer timer;
    Handler mHandler;

    ApiTimer(Handler handler) {
        mHandler = handler;
    }

    public int TimeOut_check(int status) {
        Log.d(TAG, "myTask");
        if (status == 200) {
            Log.d(TAG, "finalstatus = " + status);
        } else {
            Log.d(TAG, " else finalstatus = " + status);
            Message msg = mHandler.obtainMessage();
            msg.what = 0;
            msg.obj = String.valueOf("인터넷이 끊겼습니다.");
            mHandler.sendMessage(msg);
        }

        /*
        timer = new Timer();
//            timer.schedule(ApiTimer.myTask, 3000);  // 5초후 실행하고 종료

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "myTask");

                    int finalStatus = status;
                    if(finalStatus == 200)
                    {
                        Log.d(TAG, "finalstatus = " + finalStatus);
                    }
                    else
                    {
                        Log.d(TAG, " else finalstatus = " + finalStatus);
                        Message msg = MainActivity.getInstance().toastHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = String.valueOf("서버네트워크 에러입니다.");
                        MainActivity.getInstance().toastHandler.sendMessage(msg);
                    }


            }
        }, 0);  // 5초후 실행하고 종료
        */



        /*
        myTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "myTask");
                if(status == 200)
                {
                    Log.d(TAG, "finalstatus = " + status);
                }
                else
                {
                    Log.d(TAG, " else finalstatus = " + status);
                    Message msg = MainActivity.getInstance().toastHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf("타이머 적용");
                    MainActivity.getInstance().toastHandler.sendMessage(msg);
                }
            }
        };
      */
        return status;
    }
}
