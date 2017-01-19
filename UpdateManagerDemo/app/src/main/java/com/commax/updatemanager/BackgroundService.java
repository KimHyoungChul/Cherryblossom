package com.commax.updatemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.commax.updatemanager.Common.AboutFile;
import com.commax.updatemanager.Common.ServerIPLocal;
import com.commax.updatemanager.GetAPPList_Download.GetAppList;
import com.commax.updatemanager.GetAPPList_Download.VersionCompare;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by OWNER on 2016-04-21.
 */
public class BackgroundService extends Service {
    private static final String TAG = BackgroundService.class.getSimpleName();
    AboutFile aboutFile = new AboutFile();
    VersionCompare versionCompare = new VersionCompare();

    //for version compare
    PackageInfo versionInfo;
    String Wallversion;
    String[] serversion; // "." 을 기준으로 파싱해서 값 비교하기 때문에 배열
    String[] oldversion; // "."을 기준으로 파싱해서 값 비교하기 위해 만들 배열
    //for notification
    private NotificationManager mNM;
    //for Time service
    private CheckTask task;
    private Timer timer;

    String CloudServerIP;
    String type = "update";
    //for background service
    public static final int serviceDelay = 1000 * 60 * 60 ; //  60초 후 스케줄을 실행하고
    public static final int servicePeriod = 1000 * 60 * 60; // 60분 (한시간) 마다 동작 시킴

    Context context;
    private TimerTask timerTask = new TimerTask() {

        private Handler mHandler = new Handler();
        public void run() {
            mHandler.post(new Runnable() {
                public void run() {
                    // 한 시간 간격으로 이 함수가 호출된다
                    Log.d(TAG, "timerTask");
                    mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//                    stopTask();//??
                    try{
                        int badgeCount = BadgeCount();

                        Log.e(TAG, "Before badgeCount : " + aboutFile.readFile("BadgeCount"));
                        Log.e(TAG, "Present badgeCount : " + badgeCount);

                        //TODO 업데이트 뱃지 카운트가 올라가면 이벤트 발생으로 인식 하여 노티함
                        //TODO app실행후 처음 1시간후에는 노티함

                        if( Integer.valueOf(aboutFile.readFile("BadgeCount")) < badgeCount)
                        {
                            ShowNotification(badgeCount);
                            aboutFile.writeFile("BadgeCount",String.valueOf(badgeCount));
                            IconBadge(badgeCount);
                            Log.i(TAG, "show notification");
                        }
                        else
                        {
                            Log.i(TAG," no notification");
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG,"badge error");
                    }
                    task = new CheckTask();
                    task.execute();
                }
            });
        }
    };

    private void stopTask() {
        Log.d(TAG,"stopTask()");
        if (task != null) {
            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }
            task = null;
        }
    }

    private class CheckTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            Log.d(TAG,"doInBackground()");
            try {
                Log.d(TAG, "Local IP  : " + CloudServerIP);
                JSONHelper.getInstance().restCall(null, CloudServerIP, type , null , context);
                Log.d(TAG,"JSONHelper.restCall()");
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
    public void onCreate() { //서비스 실행시 처음 한번만 실행
        super.onCreate();
        context = this;
        Log.d(TAG, "onCreate");
        try {
            if (timer != null) {
                Log.d(TAG,"timer != null");
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            Log.e(TAG,"onCreate error");
        }
        Log.d(TAG,"schedule 동작");
        timer = new Timer();
        timer.schedule(timerTask, serviceDelay ,servicePeriod);  //1초 후에 실행하고 1초 간격으로 반복하라


        try{
            //Local Server IP
            ServerIPLocal serverIPLocal = new ServerIPLocal(this);
            CloudServerIP = serverIPLocal.getValue();
            if(TextUtils.isEmpty(CloudServerIP))
            {
//                CloudServerIP = "220.120.109.31";
                CloudServerIP = "10.1.0.2";
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.d(TAG, "serverIP : " +CloudServerIP);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { // 서비스가 호출될때마다 매번 실행
    // onResume()과 비슷
        Log.d(TAG,"onStartCommand()");
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
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
        // TODO Auto-generated method stub
        Log.d(TAG,"onDestroy()");

        stopTask();
        timer.cancel();
        timer = null;
        super.onDestroy();
    }

    public void ShowNotification(int count){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Resources res = getResources();

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(MainActivity.getInstance().getString(R.string.app_name))
                .setContentText(count + " "+MainActivity.getInstance().getString(R.string.notification_message) )
                .setTicker(MainActivity.getInstance().getString(R.string.noti_title))
                .setSmallIcon(R.mipmap.ic_apps_main_app34_n)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_apps_main_app34_n))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults( Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS)
                .setNumber(count);

        Notification  n = builder.build();
        nm.notify(1234, n);
    }

    int BadgeCount()
    //1시간마다 upgrade카운트하여 뱃지카운트에 넘겨준다.
    {
        int count=0;
        try {
            // 맨 처음 앱 실행에서는 배열이 선언되지 않아서 동작 하지 않음
            Log.d(TAG,"Pacakage[].lenth : " + GetAppList.getInstance().getPackageName.length);
            PackageManager pm = null;
            try
            {
                pm = context.getPackageManager();
            }catch (Exception e)
            {
                pm = MainActivity.getInstance().getPackageManager();
                e.printStackTrace();
            }

            //배열에 저장되어 있는 APP List 들의 버전을 비교하여 UPdate 가 되어야 하는 Count 를 파악한다.
            for(int i = 0 ; i < GetAppList.getInstance().getPackageName.length; i++)
            {
                try{
                    versionInfo = pm.getPackageInfo(GetAppList.getInstance().getPackageName[i], PackageManager.GET_META_DATA);

                    Wallversion = versionInfo.versionName;
                    Log.i(TAG, "Package Name : " +  GetAppList.getInstance().getPackageName[i] +
                            " , versionName : " + Wallversion  +
                            ", server version : " + GetAppList.getInstance().getVersionName[i]);
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    Log.e(TAG,GetAppList.getInstance().getPackageName[i] + "설치되어 있지 않은 App");
                }

                serversion = GetAppList.getInstance().getVersionName[i].split("[.]");
                if(TextUtils.isEmpty(Wallversion))
                {
                    Log.d(TAG, " Wallpad에 설치되어 있지 않습니다.");
                    oldversion = new String[serversion.length];

                    //TODO 해주어야 하나?
                    if(TextUtils.isEmpty(oldversion[0]) || oldversion.equals("null"))
                    {
                        for(int k = 0 ; k < serversion.length ; k++)
                        {
                            oldversion[k] = "0";
                        }
                        Log.e(TAG,"null 처리");
                    }
                }else
                {
                    oldversion = Wallversion.split("[.]");
                    Log.d(TAG, "oldversion : " + oldversion);
                }

                int j = serversion.length>=oldversion.length ? oldversion.length:serversion.length;
                Log.d(TAG, " short version Number " + String.valueOf(j));
                for(int k = 0; k< j ;k++)
                {
                    //version compare 0.0.0  ,  0.0.1
                    //version compare 1.0.0  ,  0.0.1
                    if(TextUtils.isEmpty(Wallversion))
                    {
                        Log.d(TAG ," 설치 되어있지 않은 앱입니다.");
                        break;
                    }
                   else if(Integer.parseInt(serversion[k]) > Integer.parseInt(oldversion[k]))
                    {
                        count++;
                        Log.d(TAG,"Update count : " + count);
                        break;
                    }
                    else if(Integer.parseInt(serversion[k]) < Integer.parseInt(oldversion[k]))
                    {
                        Log.d(TAG, "Wall version is higher than Server version ");
                        break;
                    }
                    else{
                        Log.d(TAG, " version compare else");
                    }
                }
                Wallversion = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "BadgeCount Error");
        }


        Log.d(TAG, "count : " + count);

        return count;
    }


    void IconBadge(int badgeCount) {
        try {  // for badge
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", badgeCount);
            // 메인 메뉴에 나타나는 어플의  패키지 명
            intent.putExtra("badge_count_package_name", MainActivity.getInstance().getComponentName().getPackageName());
            // 메인메뉴에 나타나는 어플의 클래스 명
            intent.putExtra("badge_count_class_name", MainActivity.getInstance().getComponentName().getClassName());
            MainActivity.getInstance().sendBroadcast(intent);
            Log.d(TAG, "badgeCount : " + badgeCount);
        } catch (Exception e) {
            Log.e(TAG, "badge error");
        }
    }
}
