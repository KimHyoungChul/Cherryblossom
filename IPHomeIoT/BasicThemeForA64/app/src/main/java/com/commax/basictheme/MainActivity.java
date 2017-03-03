package com.commax.basictheme;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.basictheme.content_provider.ContentProviderManager;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 메인액티비티
 * Created by bagjeong-gyu on 2016. 9. 7..
 */
public class MainActivity extends Activity {


    private Timer mTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFullscreen();
        hideNavigationBar();


    }

    /**
     * 전체화면 모드
     */
    private void setFullscreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    protected void onResume() {
        super.onResume();

        initTimer();


    }

    /**
     * 시간 표시 타이머 초기화
     */
    private void initTimer() {
        ShowTimeTimerTask timerTask = new ShowTimeTimerTask();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(timerTask, 1000, 1000 * 60);
    }


    /**
     * 시간과 날짜 표시
     */
    private void displayTimeAndDate() {

        //영어와 다른 언어(한중일) 다르게 처리
        String language = Locale.getDefault().getLanguage();

        //영어인 경우
        if (language.equals("en")) {

            showEnglishTimeAndDate();


            //다른 언어(한중일)인 경우
        } else {


            showTimeAndDate();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();

    }

    /**
     * 설정앱 실행
     *
     * @param view
     */
    public void launchSetting(View view) {
        launchApp("com.android.settings", "com.android.settings.Settings");
    }

    /**
     * 현관모니터링 앱 실행
     *
     * @param view
     */
    public void launchDoor(View view) {
//        Intent intent = new Intent(BasicThemeConstants.BROADCAST_DOOR_MONITOR);
//        intent.putExtra(BasicThemeConstants.KEY_FROM, BasicThemeConstants.BASIC_THEME);
//        String ip = getIpFromContentProvider();
//
//        if(ip == null) {
//            return;
//        }
//
//
//        intent.putExtra(BasicThemeConstants.KEY_IP, ip);
//        sendBroadcast(intent);

        launchApp("com.commax.yuri.onvifservice", "com.commax.yuri.onvifservice.MainActivity");
    }

    /**
     * Content Provider에서 도어폰 IP가져옴
     *
     * @return
     */
    private String getIpFromContentProvider() {
        //여러 개의 도어폰 카메라가 있는 경우 첫 번째 카메라의 ip 전송
        if(ContentProviderManager.getAllOnvifDoorCamera(this).size() > 0) {
            return ContentProviderManager.getAllOnvifDoorCamera(this).get(0).getIpAddress();
        } else {
            Toast.makeText(this, R.string.register_doorphone_camera_at_setting, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * CCTV 앱 실행
     *
     * @param view
     */
    public void launchCctv(View view) {
        launchApp("commax.wallpad.cctvview", "commax.wallpad.cctvview.MainActivity");
    }

    /**
     * 영상녹화 앱 실행
     *
     * @param view
     */
    public void launchRecordView(View view) {
        launchApp("commax.wallpad.videoplayer", "commax.wallpad.videoplayer.MainActivity");
    }


    /**
     * 앱 실행
     *
     * @param packageName
     * @param activityName
     */
    private void launchApp(String packageName, String activityName) {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);
        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 앱이 실행 가능한지 체크
     *
     * @param intent
     * @return
     */
    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public void launchCommaxSetting(View view) {
        launchApp("com.commax.settings", "com.commax.settings.MainActivity");

    }

    /**
     * 1분에 한 번씩 시간, 날짜 갱신
     */
    class ShowTimeTimerTask extends TimerTask {

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayTimeAndDate();
                }
            });

        }
    }


    /**
     * 한중일 시간(월 일 오전/오후 표시
     */
    private void showTimeAndDate() {

        RelativeLayout container = (RelativeLayout) findViewById(R.id.dateTimeContainer);
        RelativeLayout englishContainer = (RelativeLayout) findViewById(R.id.englishDateTimeContainer);
        container.setVisibility(View.VISIBLE);
        englishContainer.setVisibility(View.GONE);


        Calendar c = Calendar.getInstance();
        int minInt = c.get(Calendar.MINUTE);
        //정오인 경우 이상한 것 같던데(0)
        int hourInt = c.get(Calendar.HOUR);
        int dayInt = c.get(Calendar.DAY_OF_MONTH);
        int monthInt = c.get(Calendar.MONTH);
        int amPmInt = c.get(Calendar.AM_PM);

        String minString = (minInt < 10) ? "0" + String.valueOf(minInt) : String.valueOf(minInt);
        String hourString = (hourInt < 10) ? "0" + String.valueOf(hourInt) : String.valueOf(hourInt);
        String dayString = String.valueOf(dayInt);
        String monthString = String.valueOf(monthInt + 1);
        //String amPmString = (amPmInt == Calendar.AM) ? "오전" : "오후";
        String amPmString = (amPmInt == Calendar.AM) ? getString(R.string.am) : getString(R.string.pm);
        TextView min = (TextView) findViewById(R.id.min);
        TextView hour = (TextView) findViewById(R.id.hour);
        TextView day = (TextView) findViewById(R.id.day);
        TextView month = (TextView) findViewById(R.id.month);
        TextView amPm = (TextView) findViewById(R.id.amPm);

        min.setText(minString);
        hour.setText(hourString);
        day.setText(dayString);
        month.setText(monthString);
        amPm.setText(amPmString);
    }

    /**
     * 영어 시간(월 일 오전/오후 표시
     */
    private void showEnglishTimeAndDate() {
        RelativeLayout container = (RelativeLayout) findViewById(R.id.dateTimeContainer);
        RelativeLayout englishContainer = (RelativeLayout) findViewById(R.id.englishDateTimeContainer);
        container.setVisibility(View.GONE);
        englishContainer.setVisibility(View.VISIBLE);


        Format format = new SimpleDateFormat("MM dd");
        Date date = new Date();
        String englishTimeText = format.format(date);

        Calendar c = Calendar.getInstance();
        int minInt = c.get(Calendar.MINUTE);
        //정오인 경우 이상한 것 같던데(0)
        int hourInt = c.get(Calendar.HOUR);
        int monthInt = c.get(Calendar.MONTH);
        int dayInt = c.get(Calendar.DAY_OF_MONTH);

        String monthString = getMonthString(monthInt + 1);

        int amPmInt = c.get(Calendar.AM_PM);

        String minString = (minInt < 10) ? "0" + String.valueOf(minInt) : String.valueOf(minInt);
        String hourString = (hourInt < 10) ? "0" + String.valueOf(hourInt) : String.valueOf(hourInt);
        //String amPmString = (amPmInt == Calendar.AM) ? "오전" : "오후";
        String amPmString = (amPmInt == Calendar.AM) ? getString(R.string.am) : getString(R.string.pm);
        TextView min = (TextView) findViewById(R.id.min);
        TextView hour = (TextView) findViewById(R.id.hour);


        min.setText(minString);
        hour.setText(hourString);


        TextView englishTime = (TextView) findViewById(R.id.englishTime);
        englishTime.setText(monthString + " " + dayInt);

        TextView englishAmPm = (TextView) findViewById(R.id.englishAmPm);
        englishAmPm.setText(amPmString);

    }

    /**
     * 달(영어) 가져옴
     *
     * @param monthInt
     * @return
     */
    private String getMonthString(int monthInt) {
        switch (monthInt) {
            case 0:
                return "Jan";
            case 1:
                return "Feb";
            case 2:
                return "Mar";
            case 3:
                return "Apr";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "Aug";
            case 8:
                return "Sept";
            case 9:
                return "Oct";
            case 10:
                return "Nov";
            case 11:
                return "Dec";

        }
        return "Jan";
    }

    private void hideNavigationBar(){

        try {
            // 액티비티 아래의 네비게이션 바가 안보이게
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
