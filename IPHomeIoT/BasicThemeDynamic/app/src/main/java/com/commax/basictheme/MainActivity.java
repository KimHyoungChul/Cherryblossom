package com.commax.basictheme;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 메인액티비티
 * Created by bagjeong-gyu on 2016. 9. 7..
 */
public class MainActivity extends Activity {


    //final String defaultModeName = "모드추가";
    static final String TAG = "MainActivity";

    private boolean mHomeDown;
    private boolean mBackDown;

    String defaultModeName = null;
    final String MODE_NAME_ACTION = "com.commax.homeiot.mode.run.NickName_ACTION";
    final String NETWORK_ACTION = "com.commax.homeiot.service.NetworkSate_ACTION";
    final String UPDATE_I_FILE_PATH = "user/app/bin/favorite.i";
    final String QUICK_PATH = "user/app/bin/quick.i";
    final String MODE_FILE_PATH = "mnt/sdcard/modeNickName.i";
    final String BOTTOM_BAR_TYPE_FILE_PATH = "user/app/bin/basic.i";
    final int SIMPLE_BOTTOM_BAR = 0;
    final int NORMAL_BOTTOM_BAR = 1;
    private int mBottomBarType;
    private Timer mTimer;

    BroadcastReceiver mBR = null;          //To receive br
    BroadcastReceiver mInfoReceiver = null;//To receive Notice
    IntentFilter iFilter;                            //for mInfoReceiver
    IntentFilter bFilter;                  //for mBR
    ImageView iv_network;

    static boolean bLanguage_changed = false;

    Animation growAnim;
    Animation growAnim2;
    FrameLayout lay_intro;
    LinearLayout lay_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        iv_network = (ImageView)findViewById(R.id.iv_network);
        lay_intro = (FrameLayout)findViewById(R.id.lay_intro);
        lay_progress = (LinearLayout)findViewById(R.id.lay_progress);

        hideNavigationBar();
        checkIntro();
        addInfoReceiver();
        startIntro();

        Log.d(TAG, "lifeCycle onCreate lang_changed " + bLanguage_changed);

    }

    private void checkIntro(){
        try{
            WatchPackageList watchPackageList = new WatchPackageList();
            if (watchPackageList.getSize() == 0) {
                hideIntro();
                return;
            }

            ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            watchPackageList.resetCheck();
            for (ActivityManager.RunningServiceInfo runServiceInfo:activityManager.getRunningServices(Integer.MAX_VALUE))
                watchPackageList.setCheck(runServiceInfo.service.getPackageName());
            if (watchPackageList.isAllCheck()) {
                hideIntro();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startIntro(){

        try {
            if (bLanguage_changed){
                lay_intro.setVisibility(View.INVISIBLE);
            }else {

                try {
                    growAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.grow);
                    growAnim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.grow2);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    lay_progress.startAnimation(growAnim);
                }catch (Exception e){
                    e.printStackTrace();
                }

                Message intro_msg = mHandler.obtainMessage();
                intro_msg.what = HandlerEvent.EVENT_HANDLE_HIDE_INTRO;
                mHandler.sendMessageDelayed(intro_msg, NameSpace.INTRO_TIME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        addBroadcastReceiver();
    }

    private void addInfoReceiver(){


        try {
            iFilter = new IntentFilter();
            iFilter.addAction(NETWORK_ACTION);
            iFilter.addAction(NameSpace.LANGUAGE_ACTION);
            iFilter.addAction(NameSpace.SERVICE_COMPLETE_ACTION);

        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            Log.d(TAG, "mInfoReceiver : " + mInfoReceiver);
            if (mInfoReceiver == null) {
                mInfoReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        try {
                            String action = intent.getAction();

                            if (action.equals(NETWORK_ACTION)) {

                                String strVal = intent.getStringExtra("networkState");

                                Log.d(TAG, "NETWORK_ACTION event caught "+strVal);
                                updateNetworkState(strVal);
                            }else if (action.equalsIgnoreCase(NameSpace.LANGUAGE_ACTION)){
                                bLanguage_changed = true;
                                Log.d(TAG, "LANGUAGE_ACTION event caught");
                            }else if (action.equalsIgnoreCase(NameSpace.SERVICE_COMPLETE_ACTION)){

                                Log.d(TAG, "SERVICE_COMPLETE_ACTION event caught");
                                boolean bCompleted = intent.getBooleanExtra("SERVICE_LOAD_RESULT", true);

                                if (bCompleted){

                                    try {
                                        Message progress_end = mHandler.obtainMessage();
                                        progress_end.what = HandlerEvent.EVENT_HANDLE_PROGRESS_END;
                                        mHandler.sendMessage(progress_end);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    Message intro_msg = mHandler.obtainMessage();
                                    intro_msg.what = HandlerEvent.EVENT_HANDLE_HIDE_INTRO;
                                    mHandler.sendMessageDelayed(intro_msg, NameSpace.END_TERM);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {
                    registerReceiver(mInfoReceiver, iFilter);

                    Log.d(TAG, "addInfoReceiver registerReceiver");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    mHomeDown = true;
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!event.isCanceled()) {
                        // Do BACK behavior.
                    }
                    mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    if (!event.isCanceled()) {
                        // Do HOME behavior.
                    }
                    mHomeDown = true;
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case HandlerEvent.EVENT_HANDLE_HIDE_INTRO:
                    hideIntro();
                    break;

                case HandlerEvent.EVENT_HANDLE_PROGRESS_END:
                    startProgressEndAnimation();
                    break;
            }
        }
    };

    private void hideIntro(){
        try{
            lay_intro.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startProgressEndAnimation(){
        try{
            lay_progress.startAnimation(growAnim2);
        }catch (Exception e){

        }
    }

    private void addBroadcastReceiver(){

        try {
            bFilter = new IntentFilter();
            bFilter.addAction(MODE_NAME_ACTION);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (mBR == null) {
                Log.d(TAG, "addBroadcastReceiver : new broadcast");
                mBR = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        try {
                            Log.d(TAG, "onReceive "+intent.getAction());
                            String action = intent.getAction();

                            if(action.equalsIgnoreCase(MODE_NAME_ACTION)){

                                Log.d(TAG, "MODE_NAME_ACTION event caught");

                                String modeName = intent.getStringExtra("modeNickNameStr");
                                Log.d(TAG, "modeName : " + modeName);

                                setModeName(modeName);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {
                    getApplicationContext().registerReceiver(mBR, bFilter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateNetworkState(String state){
        try{
            if (!TextUtils.isEmpty(state)){
                if (state.equalsIgnoreCase("true")){
                    iv_network.setVisibility(View.GONE);
                }else {
                    iv_network.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if(mBR!=null) {
                getApplicationContext().unregisterReceiver(mBR);
                mBR=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            iFilter = null;
            if (mInfoReceiver != null) {
                unregisterReceiver(mInfoReceiver);
                Log.d(TAG, "onDestroy unregisterReceiver");
                mInfoReceiver = null;
            }
        } catch (Exception e) {

        }
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

    @Override
    protected void onResume() {
        super.onResume();
        defaultModeName = getString(R.string.add_mode);


        initAppInfo();
        setBottomBar();
        createLauncherIcons();
        setModeTitle();

        initTimer();


    }

    private void initTimer() {
        ShowTimeTimerTask timerTask = new ShowTimeTimerTask();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(timerTask, 1000, 1000 * 60);
    }

    private void setBottomBar() {
        //원래 코드
        mBottomBarType = readBottomBarTypeFile();

        //테스트후 삭제하세요!
//        mBottomBarType = NORMAL_BOTTOM_BAR;



        switch(mBottomBarType) {
            case NORMAL_BOTTOM_BAR:
                makeNormalBottomBarLayout();
                break;

            case SIMPLE_BOTTOM_BAR:
                makeSimpleBottomBarLayout();
                break;
        }

    }

    private void makeSimpleBottomBarLayout() {
        LinearLayout simpleBottomBar = (LinearLayout) findViewById(R.id.lay_menu_simple);
        LinearLayout normalBottomBar = (LinearLayout) findViewById(R.id.lay_menu_normal);

        simpleBottomBar.setVisibility(View.VISIBLE);
        normalBottomBar.setVisibility(View.GONE);

    }

    private void makeNormalBottomBarLayout() {
        LinearLayout simpleBottomBar = (LinearLayout) findViewById(R.id.lay_menu_simple);
        LinearLayout normalBottomBar = (LinearLayout) findViewById(R.id.lay_menu_normal);

        simpleBottomBar.setVisibility(View.GONE);
        normalBottomBar.setVisibility(View.VISIBLE);
    }

    /**
     * 모드 명 파일을 읽음
     *
     * @return
     */
    private int readBottomBarTypeFile() {

        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(BOTTOM_BAR_TYPE_FILE_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";



                while ((receiveString = bufferedReader.readLine()) != null) {

                   if(receiveString.equals("1")) {
                       return NORMAL_BOTTOM_BAR;
                   }



                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Can not read file: " + e.toString());
        }
        return SIMPLE_BOTTOM_BAR;
    }

    /**
     * 모드 버튼의 타이틀 설정
     */
    private void setModeTitle() {
        try {
            String modeName = readModeFile();
            setModeName(modeName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setModeName(String str){

        try {
            TextView modeTitle = null;

            if(mBottomBarType == SIMPLE_BOTTOM_BAR) {
                modeTitle = (TextView) findViewById(R.id.bt_mode_simple);
            } else {
                modeTitle = (TextView) findViewById(R.id.bt_mode_normal);
            }

            if (!TextUtils.isEmpty(str)) {
                modeTitle.setText(str);
            }else {
                modeTitle.setText(getString(R.string.add_mode));
            }

            modeTitle.setSelected(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 모드 명 파일을 읽음
     *
     * @return
     */
    private String readModeFile() {
        final String SYM_EQUAL = "=";
        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(MODE_FILE_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                String[] tokens = null;


                while ((receiveString = bufferedReader.readLine()) != null) {

                    if (!receiveString.contains(SYM_EQUAL)) {
                        return defaultModeName;
                    }

                    tokens = receiveString.split(SYM_EQUAL);

                    return tokens[1];

                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Can not read file: " + e.toString());
        }
        return defaultModeName;
    }

    /**
     * 실행할 수 없는 앱 회색 처리
     */
//    private void setBottomButtonDisabled() {
//        //버튼 회색 처리!!
//        ImageButton coachButton = (ImageButton) findViewById(R.id.bt_coach);
//        ImageButton appsButton = (ImageButton) findViewById(R.id.bt_apps);
//        com.commax.basictheme.font.NotoRegularTextView modeButton = (com.commax.basictheme.font.NotoRegularTextView) findViewById(R.id.bt_mode);
//
//        ColorUtil.setBackground(this, false, coachButton, R.mipmap.btn_home_coach);
//        ColorUtil.setBackground(this, false, appsButton, R.mipmap.btn_home_apps);
//        ColorUtil.setBackground(this, false, modeButton, R.mipmap.btn_home_mode_n);
//
//    }

    /**
     * 앱 정보 데이터 초기화
     */
    private void initAppInfo() {
        AppInfoMap.init(this);
    }

    /**
     * 론처 아이콘 생성
     */
    private void createLauncherIcons() {


        Stack<String> selectedApps = readQuickFile();
        makeLayout(selectedApps);
    }


    /**
     * favorite.i 파일 읽음
     *
     * @return
     */
    private Stack<String> readFavoriteIFile() {
        final String SYM_AMPERSAND = "&";
        Stack<String> selectedApps = new Stack<>();
        selectedApps.clear();


        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(UPDATE_I_FILE_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                String[] tokens = null;
                String app = null;

                while ((receiveString = bufferedReader.readLine()) != null) {

                    if (!receiveString.contains(SYM_AMPERSAND)) {
                        continue;
                    }

                    tokens = receiveString.split(SYM_AMPERSAND);

                    app = tokens[1].trim();
                    Log.d(BasicThemeConstants.LOG_TAG, "app: " + app);

                    if (selectedApps.contains(app)) {
                        continue;
                    }
                    selectedApps.add(app);

                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "File not found: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Can not read file: " + e.toString());
            return null;
        } catch (Exception e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Error: " + e.toString());
            return null;
        }

        return selectedApps;
    }

    /**
     * quick.i 파일 읽음
     *
     * @return
     */
    private Stack<String> readQuickFile() {
        final String SYM_AMPERSAND = "&";
        Stack<String> selectedApps = new Stack<>();
        selectedApps.clear();


        try {
            //openFileInput 메소드를 사용하면 path separator가 포함되어 있다는 오류 발생
            //openFileInput의 앱의 사적인 영역에 있는 파일만 오픈
            InputStream inputStream = new FileInputStream(new File(QUICK_PATH));

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";


                String[] tokens = null;
                String app = null;

                while ((receiveString = bufferedReader.readLine()) != null) {

                    if (!receiveString.contains(SYM_AMPERSAND)) {
                        continue;
                    }

                    tokens = receiveString.split(SYM_AMPERSAND);

                    app = tokens[1].trim();
                    Log.d(BasicThemeConstants.LOG_TAG, "app: " + app);

                    if (selectedApps.contains(app)) {
                        continue;
                    }
                    selectedApps.add(app);

                }

                inputStream.close();

            }
        } catch (FileNotFoundException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "File not found: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Can not read file: " + e.toString());
            return null;
        } catch (Exception e) {
            Log.e(BasicThemeConstants.LOG_TAG, "Error: " + e.toString());
            return null;
        }

        return selectedApps;
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

        try {
            RelativeLayout container = (RelativeLayout) findViewById(R.id.dateTimeContainer);
            RelativeLayout englishContainer = (RelativeLayout) findViewById(R.id.englishDateTimeContainer);
            container.setVisibility(View.VISIBLE);
            englishContainer.setVisibility(View.GONE);

            Date date = new Date();
            Format timeFormat = new SimpleDateFormat("hh:mm");
            String str_time = timeFormat.format(date);

            Calendar c = Calendar.getInstance();
            int dayInt = c.get(Calendar.DAY_OF_MONTH);
            int monthInt = c.get(Calendar.MONTH);
//            int amPmInt = c.get(Calendar.AM_PM);

            Format amPmFormat = new SimpleDateFormat("a");
            String str_amPm = amPmFormat.format(date);

            String dayString = String.valueOf(dayInt);
            String monthString = String.valueOf(monthInt + 1);
//            String amPmString = (amPmInt == Calendar.AM) ? getString(R.string.am) : getString(R.string.pm);
            TextView min = (TextView) findViewById(R.id.min);
            TextView day = (TextView) findViewById(R.id.day);
            TextView month = (TextView) findViewById(R.id.month);
            TextView amPm = (TextView) findViewById(R.id.amPm);
            TextView tv_day = (TextView) findViewById(R.id.day_7);

            Format dayFormat = new SimpleDateFormat("EEEE");
            String str_dayOfWeek = dayFormat.format(date);

            min.setText(str_time);
            day.setText(dayString);
            month.setText(monthString);
            amPm.setText(str_amPm);
            tv_day.setText(str_dayOfWeek);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 영어 시간(월 일 오전/오후 표시
     */
    private void showEnglishTimeAndDate() {

        try {
            RelativeLayout container = (RelativeLayout) findViewById(R.id.dateTimeContainer);
            RelativeLayout englishContainer = (RelativeLayout) findViewById(R.id.englishDateTimeContainer);
            container.setVisibility(View.GONE);
            englishContainer.setVisibility(View.VISIBLE);

            Date date = new Date();
            Format timeFormat = new SimpleDateFormat("hh:mm");
            String str_time = timeFormat.format(date);

            Calendar c = Calendar.getInstance();
            int monthInt = c.get(Calendar.MONTH);
            Log.d(TAG, "month "+monthInt);
            int dayInt = c.get(Calendar.DAY_OF_MONTH);

            String monthString = getMonthString(monthInt);

//            int amPmInt = c.get(Calendar.AM_PM);
//            String amPmString = (amPmInt == Calendar.AM) ? getString(R.string.am) : getString(R.string.pm);

            Format amPmFormat = new SimpleDateFormat("a");
            String str_amPm = amPmFormat.format(date);
            TextView min = (TextView) findViewById(R.id.min);

            min.setText(str_time);

            Format dayFormat = new SimpleDateFormat("EEEE");
            String str_dayOfWeek = dayFormat.format(date);

            TextView englishTime = (TextView) findViewById(R.id.englishTime);
            englishTime.setText(str_dayOfWeek+", "+ monthString + " " + dayInt);

            TextView englishAmPm = (TextView) findViewById(R.id.englishAmPm);
            englishAmPm.setText(str_amPm);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

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


    /**
     * 론처 아이콘 레이아웃 생성
     *
     * @param selectedApps
     */
    private void makeLayout(Stack<String> selectedApps) {

        try {

            ImageButton app1 = (ImageButton) findViewById(R.id.app1);
            ImageButton app2 = (ImageButton) findViewById(R.id.app2);
            ImageButton app3 = (ImageButton) findViewById(R.id.app3);
            ImageButton app4 = (ImageButton) findViewById(R.id.app4);
            ImageButton app5 = (ImageButton) findViewById(R.id.app5);

            app1.setBackgroundResource(R.mipmap.ic_rp_default);
            app2.setBackgroundResource(R.mipmap.ic_rp_default);
            app3.setBackgroundResource(R.mipmap.ic_rp_default);
            app4.setBackgroundResource(R.mipmap.ic_rp_default);
            app5.setBackgroundResource(R.mipmap.ic_rp_default);

            app1.setOnClickListener(null);
            app2.setOnClickListener(null);
            app3.setOnClickListener(null);
            app4.setOnClickListener(null);
            app5.setOnClickListener(null);

            //모든 아이콘을 초기화한후 파일을 파싱한 후에 문제가 있으면 return
            if (selectedApps == null) {
                return;
            }

            Stack<View> appButtons = new Stack<>();
            appButtons.push(app1);
            appButtons.push(app2);
            appButtons.push(app3);
            appButtons.push(app4);
            appButtons.push(app5);

            TextView appTitle1 = (TextView) findViewById(R.id.appTitle1);
            TextView appTitle2 = (TextView) findViewById(R.id.appTitle2);
            TextView appTitle3 = (TextView) findViewById(R.id.appTitle3);
            TextView appTitle4 = (TextView) findViewById(R.id.appTitle4);
            TextView appTitle5 = (TextView) findViewById(R.id.appTitle5);

            appTitle1.setText("");
            appTitle2.setText("");
            appTitle3.setText("");
            appTitle4.setText("");
            appTitle5.setText("");

            Stack<View> appTitles = new Stack<>();
            appTitles.push(appTitle1);
            appTitles.push(appTitle2);
            appTitles.push(appTitle3);
            appTitles.push(appTitle4);
            appTitles.push(appTitle5);


            //가운데 정렬을 요구하는 경우 코딩이 복잡해질 수 있음!!

            String packageName = null;
            String activityName = null;
            ImageButton appButton = null;
            TextView appTitle = null;
            while (!selectedApps.isEmpty()) {
                try {
                    activityName = selectedApps.pop();

                    // Log.d(BasicThemeConstants.LOG_TAG, "activity name: " + activityName);

                    if (!AppInfoMap.containsActivity(activityName)) {
                        continue;
                    }


                    packageName = AppInfoMap.getPackageName(activityName);
                    appButton = (ImageButton) appButtons.pop();
                    appTitle = (TextView) appTitles.pop();

                    appButton.setBackgroundResource(AppInfoMap.getIconId(activityName));
                    appTitle.setText(AppInfoMap.getAppName(MainActivity.this, activityName));
                    final String finalPackageName = packageName;
                    final String finalActivityName = activityName;
                    appButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchApp(finalPackageName, finalActivityName);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    /**
     * 앱 실행
     *
     * @param packageName
     * @param activityName
     */
    private void launchApp(String packageName, String activityName) {
        try {
            Intent intent = new Intent();
            intent.setClassName(packageName, activityName);
            startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 모드 앱 실행
     *
     * @param view
     */
    public void launchModeApp(View view) {

        TextView modeTitle = null;

        if(mBottomBarType == SIMPLE_BOTTOM_BAR) {
            modeTitle = (TextView) findViewById(R.id.bt_mode_simple);
        } else {
            modeTitle = (TextView) findViewById(R.id.bt_mode_normal);
        }
        modeTitle.performClick();


        Intent intent = new Intent();
        intent.setClassName("com.commax.homeiot", "com.commax.homeiot.ui.MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "모드 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
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

    /**
     * 우리집 코치 앱 실행
     *
     * @param view
     */
    public void launchHomeCoach(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.commax.homecoach", "com.commax.homecoach.MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "우리집 코치 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 앱 리스트 앱 실행
     *
     * @param view
     */
    public void launchAppList(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.commax.applist", "com.commax.applist.V2_MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "앱 리스트 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 제어 앱 실행
     *
     * @param view
     */
    public void launchControl(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.commax.control", "com.commax.control.MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "제어 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 우리집 리포터 앱 실행
     *
     * @param view
     */
    public void launchHomeReporter(View view) {
        Intent intent = new Intent();
        intent.setClassName("com.commax.homereporter", "com.commax.homereporter.MainActivity");

        if (isAvailable(intent)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "우리집 리포터 앱이 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }


}
