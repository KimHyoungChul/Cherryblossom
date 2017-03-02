package com.commax.headerlist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by OWNER on 2016-04-21.
 */
public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getSimpleName();
    JSONHelper jsonHelper = new JSONHelper();
    AboutFile aboutFile = new AboutFile();
    VersionCompare versionCompare = new VersionCompare();
    //for notification
    private NotificationManager mNM;
    private Context context;
    //for Time service
    private CheckTask Asynctask;
    private Timer timer;
    private TimerThread timerThread;
    String localIP= "127.0.0.1";
    public static String CloudServerIP = "220.120.109.31";

    static List<String> serversion;
    static ArrayList<String> oldversion;
    static String Wallversion;
    //for ip url
    static String type = "test_app_info";
    static String param1 = "Application";
    //for background service
    public static final int serviceDelay = 1000 * 1 * 15;// 30초 후 스케줄을 실행하고
    public static int servicePeriod = 1000 * 1 * 60; //30 초 (한시간) 마다 동작 시킴
    // 1000 * 60 * 60; 1시간간격으로 반복해라

    static int before_badge_count;
    int badgeCount;

    @Override
    public void onCreate() { //서비스 실행시 처음 한번만 실행
        super.onCreate();
        Log.d(TAG, "onCreate");
        context = this;

        timerThread = new TimerThread(true);
        timerThread.start();
        Log.d(TAG, "timerThread start");

        /*
        try {
            if (timer != null) {
                Log.d(TAG,"timer != null");
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            Log.e(TAG,"onStartcommanc error");
        }
        Log.d(TAG,"schedule 동작");
        Log.d(TAG, "servicePeriod : " +servicePeriod );
        timer = new Timer();
        timer.schedule(timerTask, serviceDelay ,servicePeriod);  //1분 후에 실행하고 1분 간격으로 반복하라
        */

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // 서비스가 호출될때마다 매번 실행
        //TODO test
        if(timerThread.isPlay == false)
        {
            timerThread.isPlay = true;
        }
        // (onResume()과 비슷
        Log.d(TAG,"onStartCommand()");
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    //TODO test
    class TimerThread extends Thread {

        private boolean isPlay = false;
        public TimerThread(boolean isPlay){
            this.isPlay = isPlay;
        }
        public void stopThread(){
            if(isPlay == true)
            {
                isPlay = !isPlay;
            }
        }
        @Override
        public void run() {
            super.run();
            while (isPlay) {
                try {
                    stopTask();
                    Asynctask = new CheckTask();
                    Asynctask.execute();
                    Log.d(TAG, "task.execute()");
                    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                    try
                    {
                        Thread.sleep(Integer.valueOf(aboutFile.readFile("period"))); //period 마다
                        Log.d(TAG,"period :" + Integer.valueOf(aboutFile.readFile("period")));
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private TimerTask timerTask = new TimerTask() {

        private Handler mHandler = new Handler();

        public void run() {
            mHandler.post(new Runnable() {
                public void run() {
                    Log.d(TAG, "timerTask");
                    // 한 시간 간격으로 이 함수가 호출된다
                    /*
                    try {
                        if (timer != null) {
                            Log.d(TAG,"timer != null");
                            timer.cancel();
                            timer = null;
                        }
                    } catch (Exception e) {
                        Log.e(TAG,"onStartcommanc error");
                    }

                    //thread  sleep
                    try{
                        Thread.sleep(5000);
                        Log.d(TAG, "Thread sleep 성공");
                    }
                    catch (Exception e)
                    {
                        Log.d(TAG, "sleep 실패");
                    }

                    //handler sleep
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {

                            Log.d(TAG, "postDelay handler");

                        }
                    }, 3000);  // 0.5초초
                    Log.d(TAG,"schedule 동작");
                    // delay 500ms 줘보기
                    timer = new Timer();
                    timer.schedule(timerTask, serviceDelay ,servicePeriod);  //1분 후에 실행하고 1분 간격으로 반복하라
                    */
                    stopTask();
                    Asynctask = new CheckTask();
                    Asynctask.execute();
                    Log.d(TAG, "task.execute()");
                    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                }
            });
        }
    };

    private void stopTask() {
        Log.d(TAG,"stopTask()");
        if (Asynctask != null) {
            if (Asynctask.getStatus() == AsyncTask.Status.RUNNING) {
                Asynctask.cancel(true);
            }
            Asynctask = null;
        }
    }

    private class CheckTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d(TAG,"doInBackground()");
            try {
                Log.d(TAG, "CloudeServerIP :" + CloudServerIP);
                jsonHelper.restCall(localIP, CloudServerIP , type, param1);
                Log.d(TAG,"JSONHelper.restCall()");

                before_badge_count = Integer.valueOf(aboutFile.readFile("before_badge"));
                badgeCount = BadgeCount();
                IconBadge(badgeCount, context);
                Log.d(TAG,"badgeCount :" + badgeCount  + "before_badge_count : " + before_badge_count);
                if(before_badge_count < badgeCount)
                {
                    ShowNotification(badgeCount);
                    Log.e(TAG, "notification");
                }
                else
                {
                    Log.e(TAG," no notification");
                }
                aboutFile.writeFile("before_badge",String.valueOf(badgeCount));

            } catch (Exception e) {
                Log.e(TAG,"restCall error");
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // view Upload할수 있다.
            //TODO 현재 서버 에서 정보값 못가져옴
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG,"onBind()");
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO Auto-generated method stub
        Log.d(TAG,"onDestroy()");
        stopTask();
        timer.cancel();
        timer = null;
        //thread stop
        timerThread.stopThread();

    }

    void ShowNotification(int count){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = getResources();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(count + getString(R.string.notification_message) )
                .setTicker(getString(R.string.noti_title))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults( Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                .setNumber(count);

        Notification  n = builder.build();
        nm.notify(1234, n);
    }

    int BadgeCount() //1시간마다 upgrade 카운트하여 뱃지카운트에 넘겨준다.
    {
        Log.d(TAG, "BadgeCount()");
        int count=0;
        try {
            Log.d(TAG, "badge : JsonHelper.listcnt" + JSONHelper.list_cnt);
            //TODO 서버 이후 작업
            for(int i = 0 ; i < JSONHelper.list_cnt ; i++) // JSONHelper.getPackageName.length; i++)
            {
                Log.d(TAG, "i = " + i);
                String category = versionCompare.Compare(JSONHelper.getPackageName[i], JSONHelper.getVersionName[i] , getApplicationContext());
                if(category.equals(getString(R.string.upgrade)))
                {
                   count++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, " badge count Error");
        }
        return count;
    }

    void IconBadge(int badgeCount , Context context) {
        try {  // for badge
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", badgeCount);
            // 메인 메뉴에 나타나는 어플의  패키지 명
            intent.putExtra("badge_count_package_name", "com.commax.headerlist");//context.getPackageName());//getApplicationInfo().packageName);
            // 메인메뉴에 나타나는 어플의 클래스 명
            intent.putExtra("badge_count_class_name", "com.commax.headerlist.MainActivity");//getComponentName().getClass());//getApplicationContext().getClass());
            //((Activity) context).getComponentName().getClassName()
            Log.d(TAG, "before sendbroadcast");
            context.sendBroadcast(intent);
            Log.d(TAG, "badgeCount : " + badgeCount);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "badge error");
        }
    }
}
