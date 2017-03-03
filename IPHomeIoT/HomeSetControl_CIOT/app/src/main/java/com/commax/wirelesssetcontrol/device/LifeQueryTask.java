package com.commax.wirelesssetcontrol.device;

import com.commax.pam.db.interfaces.MySQLConnection;
import com.commax.wirelesssetcontrol.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Shin on 2017-02-28.
 * MySql 생명 주기 연장용
 */
public class LifeQueryTask {
    private final int START_TIME = 10000;  //10초 뒤에 실행
    private final int SEND_TIME = 900000; //15분마다 확인

    private TimerTask mTimerTask;
    private Timer mTimer;

    public LifeQueryTask(){
        mTimerTask = new TimerTask(){
            @Override
            public void run() {
                Log.d("LifeQueryTask", ">> LifeQueryTask DB query.");
                try {
                    MySQLConnection.getInstance().getDevcieIndex("1234");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, START_TIME, SEND_TIME);
    }

    public void close(){
        mTimer.cancel();
        mTimerTask.cancel();
    }
}
