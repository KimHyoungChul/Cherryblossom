package com.commax.wirelesssetcontrol;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

import com.commax.adapter.aidl.IAdapter;
import com.commax.wirelesssetcontrol.activity.EditSpaceActivity;
import com.commax.wirelesssetcontrol.adapter.CustomPagerAdapter;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.device.CmxDeviceDataParser;
import com.commax.wirelesssetcontrol.device.CmxDeviceDataReceiveInterface;
import com.commax.wirelesssetcontrol.device.CmxDeviceManager;
import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.data.PageData;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.device.LifeQueryTask;
import com.commax.wirelesssetcontrol.tools.Prefs;
import com.commax.wirelesssetcontrol.touchmirror.TouchMirrorAct;
import com.commax.wirelesssetcontrol.touchmirror.common.Constants;
import com.commax.wirelesssetcontrol.touchmirror.view.GridActionLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.GridFrameLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;
import com.commax.wirelesssetcontrol.tools.PublicTools;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.ResourceFinder;
import com.commax.wirelesssetcontrol.view.CustomPageFragment;
import com.commax.wirelesssetcontrol.view.CustomViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    static final String TAG = "MainActivity";
    boolean quick_show = false;

    LinearLayout lay_main_top;
    FrameLayout lay_quick;
    FrameLayout lay_tip;
    LinearLayout quick_back;
    LinearLayout lay_indicator;

    /*** 터치 액션 관련 ****/
    private FrameLayout mTouchIndicatorLayout, mTouchDummy;
    private LinearLayout mTouchParentLayout;
    private GridActionLayout mTouchBottomArea;
    private RelativeLayout mIconDeleteLayout;
    /*** 터치 액션 관련 ****/

    private static CustomAppWidgetHost mAppWidgetHost;

    private LifeQueryTask mLifeQueryTask; //DB 생명 주기 연장용

    CustomTextView tv_space_name;

    LinearLayout edit_device;
    FrameLayout bt_tip_close;
    ImageButton bt_tip_x;
    ImageButton bt_check_show_tip;
    ImageView iv_wifi;
    ImageView iv_door;
    LinearLayout iv_account;
    ImageView iv_external_network;
    LinearLayout quick_container;
    FrameLayout lay_intro;
    LinearLayout lay_progress;
    LinearLayout edit_space;
    LinearLayout add_widget;
    LinearLayout add_device;
    LinearLayout add_apps;
    LinearLayout lay_account_tip;
    LinearLayout btn_create_account;
    ImageView account_tip_close;
    ImageView account_arrow, mDeleteIcon;
    TextView tv_guidance_account;

    private SharedPreferences tipPref;               //Saving Tip show or not
    public static SharedPreferences.Editor xy_editor;
    SharedPreferences.Editor editor;
    BroadcastReceiver mBroadcastReceiver = null;     //To receive PAM event
    BroadcastReceiver mInfoReceiver = null;          //To receive Notice
    IntentFilter mFilter;                            //for mBroadcastReceiver
    IntentFilter iFilter;                            //for mInfoReceiver

    public static RemoteServiceConnection m_rc;
    public static RemoteServiceConnection getRemoteConnection(){
        return m_rc;
    }

    CustomViewPager mViewPager;
    CustomPagerAdapter mPagerAdapter;

    public static int CRT_PAGE = 0;

    boolean onStop_called=false;
    boolean activity_on=false;

    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;
    CommandTools commandTools;

    public static ProjectOptions projectOptions;

    boolean bFading = false;
    boolean bNo_more_tip = false;
    static boolean bLanguage_changed = false;

    private Animation mAnimGrow, mAnimGrow2, mAnimFadeIn, mAnimFadeOut, mAnimDeviceGrow;

    Intent temp_intent;

    //host 객체 공유
    public static CustomAppWidgetHost getWidgetHost(){
        return mAppWidgetHost;
    }

    /*** 터치 액션 관련 Value***/
    private final int LONG_TOUCH = -1;
    private int mTouchState; //터치 상태 저장
    private Point mBackupPoint = null; //원래 아이콘 위치 저장
    private Point mPrevPoint = null; //최초 터치 위치 저장
    private IconLayout mLongTouchLayout = null;
    private Handler mLongTouchCheckHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case LONG_TOUCH:
                    android.util.Log.i(TAG, "Action : LONG TOUCH");
                    int x = msg.arg1, y = msg.arg2;
                    IconData data = getTouchItem(x, y);
                    actionLongTouch(data, x, y);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        PublicTools.hideNavigationBar(this);
        CmxDeviceManager.init(this);
        PageDataManager.init(this);
        ResourceFinder.init(this);


        mViewPager = (CustomViewPager) findViewById(R.id.vp);
        mPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(false, new TabletTransformer());
        mViewPager.setOnPageChangeListener(mPageListener);


        /*** 터치 관런 소스 ***/
        mTouchIndicatorLayout = (FrameLayout) findViewById(R.id.touch_indicator_area);
        mTouchDummy = (FrameLayout) findViewById(R.id.touch_dummy);
        mTouchParentLayout = (LinearLayout) findViewById(R.id.parent_touch_area);
        mIconDeleteLayout = (RelativeLayout) findViewById(R.id.lay_icon_delete);
        mTouchBottomArea = (GridActionLayout) findViewById(R.id.touch_bottom_area);
        mTouchBottomArea.init(Constants.AREA_TOUCH_BOTTOM_COL, Constants.AREA_TOUCH_BOTTOM_ROW);
        setDefaultIcon();
        /*** 터치 관런 소스 ***/

        lay_main_top = (LinearLayout) findViewById(R.id.lay_main_top);

        lay_indicator = (LinearLayout) findViewById(R.id.lay_indicator);
        edit_device = (LinearLayout) findViewById(R.id.edit_device);
        tv_space_name = (CustomTextView) findViewById(R.id.tv_space_name);

        quick_container = (LinearLayout) findViewById(R.id.quick_container);

        lay_quick = (FrameLayout) findViewById(R.id.lay_quick);
        quick_back = (LinearLayout) findViewById(R.id.quick_back);
        lay_tip = (FrameLayout) findViewById(R.id.lay_tip);
        bt_tip_close = (FrameLayout) findViewById(R.id.bt_tip_close);
        bt_tip_x = (ImageButton) findViewById(R.id.bt_tip_x);
        bt_check_show_tip = (ImageButton) findViewById(R.id.bt_check_show_tip);
        iv_wifi = (ImageView) findViewById(R.id.iv_wifi);
        iv_door = (ImageView) findViewById(R.id.iv_door);
        iv_external_network = (ImageView) findViewById(R.id.iv_external_network);
        iv_account = (LinearLayout) findViewById(R.id.iv_account);
        lay_intro = (FrameLayout) findViewById(R.id.lay_intro);
        lay_progress = (LinearLayout) findViewById(R.id.lay_progress);
        edit_space = (LinearLayout) findViewById(R.id.edit_space);
        add_widget = (LinearLayout) findViewById(R.id.add_widget);
        add_device = (LinearLayout) findViewById(R.id.add_device);
        add_apps = (LinearLayout) findViewById(R.id.add_apps);
        lay_account_tip = (LinearLayout) findViewById(R.id.lay_account_tip);
        account_tip_close = (ImageView) findViewById(R.id.close_account_tip);
        account_arrow = (ImageView) findViewById(R.id.account_arrow);
        btn_create_account = (LinearLayout) findViewById(R.id.btn_create_account);
        tv_guidance_account = (TextView) findViewById(R.id.tv_guidance_account);
        mDeleteIcon = (ImageView) findViewById(R.id.iv_icon_delete);
        mDeleteIcon.setSelected(false);

        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {            mAnimFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            mAnimFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            mAnimFadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    lay_quick.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        edit_device.setOnClickListener(mClick);
        quick_back.setOnClickListener(mClick);
        bt_tip_close.setOnClickListener(mClick);
        bt_check_show_tip.setOnClickListener(mClick);
        bt_tip_x.setOnClickListener(mClick);
        edit_space.setOnClickListener(mClick);
        add_widget.setOnClickListener(mClick);
        add_device.setOnClickListener(mClick);
        add_apps.setOnClickListener(mClick);
        iv_door.setOnClickListener(mClick);
        iv_account.setOnClickListener(mClick);
        iv_external_network.setOnClickListener(mClick);
        iv_wifi.setOnClickListener(mClick);
        account_tip_close.setOnClickListener(mClick);
        btn_create_account.setOnClickListener(mClick);
        tv_guidance_account.setOnClickListener(mClick);
    }

    private void initData(){
        mLifeQueryTask = new LifeQueryTask();
        mBackupPoint = new Point();
        mPrevPoint = new Point();
        mAppWidgetHost = new CustomAppWidgetHost(getApplicationContext(), NameSpace.APPWIDGET_HOST_ID);

        checkIntro();
        projectOptions = new ProjectOptions();
        getProjectOptions();
        setAppConfig();
        setIntentFilter();
        roomInitializer();
        setRoomName(CRT_PAGE);
        commandTools = new CommandTools();
        addInfoReceiver();
        startIntro();
        showTips();

        Log.d(TAG, "lifeCycle onCreate lang_changed " + bLanguage_changed);
//        checkNetworkState();
    }

    private void initEvent() {
        mTouchParentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTouchState = motionEvent.getAction();
                int x = (int)motionEvent.getX(), y = (int)motionEvent.getY();

                switch (mTouchState) {
                    case MotionEvent.ACTION_DOWN:
                        android.util.Log.i(TAG, "Action : TOUCH DOWN");
                        Message msg = new Message();
                        msg.what = LONG_TOUCH;
                        msg.arg1 = x;
                        msg.arg2 = y;
                        mPrevPoint.set(x, y);
                        setIconClickMotion(x, y, true);
                        mLongTouchCheckHandler.sendMessageDelayed(msg, Constants.LONG_TOUCH_TIME);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(checkTouchPosition(x, y)) {
                            mLongTouchCheckHandler.removeMessages(LONG_TOUCH);
                            setIconClickMotion(mPrevPoint.x, mPrevPoint.y, false);
                        }

                        if(mLongTouchLayout != null){
                            setLongTouchIconPositon(x, y);
                            dragSendEvent(x, y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        android.util.Log.i(TAG, "Action : TOUCH UP");
                        setIconClickMotion(mPrevPoint.x, mPrevPoint.y, false);

                        if(mLongTouchCheckHandler.hasMessages(LONG_TOUCH) && !checkTouchPosition(x, y)) //터치 인 경우
                            actionTouch(getTouchItem(x, y));
                        else if(mLongTouchLayout != null)
                            longTouchFinishAction(x, y);

                        mLongTouchCheckHandler.removeMessages(LONG_TOUCH);
                        getTouchTopArea().stopDragEffect();
                        mTouchBottomArea.stopDragEffect();
                        break;
                }

                //롱터치가 아닐시 뷰페이저에 이벤트를 전송
                if(mLongTouchLayout == null && !quick_show)
                    mViewPager.dispatchTouchEvent(motionEvent);
                return true;
            }
        });
    }

    //터치 영역 체크
    private final int SCOPE = 20;
    private boolean checkTouchPosition(int x, int y){
        if(mPrevPoint.x - SCOPE > x || mPrevPoint.x + SCOPE < x ||
                mPrevPoint.y - SCOPE > y || mPrevPoint.y + SCOPE < y)
            return true;

        return false;
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
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    mAnimGrow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.grow);
                    mAnimGrow2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.grow2);
                    mAnimDeviceGrow = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.device_grow);
                    lay_progress.startAnimation(mAnimGrow);
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

    /* Set Start Service */
    private void setStartService(String strPackage, String strClassName) {
        try {
            Intent intent = new Intent();
            ComponentName comName = new ComponentName(strPackage, strClassName);

            intent.setComponent(comName);
            startService(new Intent().setComponent(comName));

            intent = null;
            comName = null;
        }catch (Exception e){

        }
    }

    private void checkWifiState(){
        try{
            ConnectivityManager manager =
                    (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            boolean connected = false;

            if (wifi != null) {

                connected = wifi.isConnected();
                Log.d(TAG, "checkWifiState connected "+connected);

                if (connected) {
                    iv_wifi.setBackgroundResource(R.mipmap.ic_network_wifi_white_36dp);
                } else {
                    iv_wifi.setBackgroundResource(R.mipmap.ic_signal_wifi_off_white_36dp);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkNetworkState(){
        try{
            String state = "";

            state = getNetworkState();

            updateNetworkState(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkAccountState(){
        try{
            String state = "";
            FileEx fileEx = new FileEx();

            state = fileEx.readAccountInfo(NameSpace.ACCOUNT_STATE_KEY);

            updateAccountState(state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getNetworkState(){

        String network_state = "";
        try {
            FileEx file = new FileEx();
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = dirPath + NameSpace.NETWORK_CHECK_PATH;
            String[] files = null;

            try {
                files = file.readFile(fileName);
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
            } catch (IOException e) {
                // e.printStackTrace();
            }

            if (files == null) {
                return network_state;
            }

            if (files.length > 0) {
                // ���� üũ
                if (files == null) {
                    return network_state;
                }
                if ("".equals(files[0])) {
                    return network_state;
                }
                if ("-1".equals(files[0])) {
                    return network_state;
                }
            }

            for (int i = 0; i < files.length; i++) {
                String line = files[i];

                try {
                    if (line.contains(NameSpace.NETWORK_STATE_KEY)) {
                        network_state = line.replace(NameSpace.NETWORK_STATE_KEY + "=", "");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return network_state;
    }

    private void showTips(){
        try{

            boolean not_show = checkTipShow();

            if (!not_show) {
                createTips();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTips(){

        try{
            Log.d(TAG, "not createTips");
//            lay_tip.setVisibility(View.VISIBLE);
//            lay_tip.setClickable(true);
//            setCheckTipImage();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean checkTipShow(){

        boolean not_show = false;
        bNo_more_tip = false;

        try{
            tipPref = getApplicationContext().getSharedPreferences(NameSpace.TIP_PREFERENCE, 0);

            if (tipPref.contains(NameSpace.NOT_SHOW_TIP)){
                if (tipPref.getString(NameSpace.NOT_SHOW_TIP,"").equalsIgnoreCase("true")){
                    bNo_more_tip = true;
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return not_show;
    }

    private boolean checkAccountTipShow(){

        boolean not_show = true;

        try{
            lay_account_tip.setVisibility(View.VISIBLE);
            account_arrow.setVisibility(View.VISIBLE);

            tipPref = getApplicationContext().getSharedPreferences(NameSpace.TIP_PREFERENCE, 0);

            if (tipPref.contains(NameSpace.ACCOUNT_TIP)){
                if (tipPref.getString(NameSpace.ACCOUNT_TIP, "").equalsIgnoreCase("false")){
                    account_arrow.setVisibility(View.GONE);
                    lay_account_tip.setVisibility(View.GONE);
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return not_show;
    }

    private void setAppConfig(){
        try{
            if (projectOptions!=null){
                try {
                    if (projectOptions.str_support_control.equalsIgnoreCase("false")) {
                        edit_device.setVisibility(View.GONE);
                    }

                    if (!projectOptions.str_model_type.equalsIgnoreCase(NameSpace.MODEL_TYPE_FULL_IP)) {
                        iv_door.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getProjectOptions(){
        try{
//            Log.d(TAG, "getProjectOptions");

            try {
                FileEx io = new FileEx();
                String[] files = null;

                try {
                    files = io.readFile(NameSpace.PXD_CONFIG_PATH);
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                if (files == null) {
                    return;
                }

                if (files.length > 0) {

                    if (files == null) {
                        return;
                    }
                    if ("".equals(files[0])) {
                        return;
                    }
                    if ("-1".equals(files[0])) {
                        return;
                    }
                }

                try {
                    for (int i = 0; i < files.length; i++) {
                        String line = files[i];

                        try {
                            if (line.contains(NameSpace.CONFIG_LOGO_KEY)) {
                                projectOptions.str_logo = line.replace(NameSpace.CONFIG_LOGO_KEY + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_SUPPORT_INFO_KEY)) {
                                projectOptions.str_support_info = line.replace(NameSpace.CONFIG_SUPPORT_INFO_KEY + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_SUPPORT_SPACE_KEY)) {
                                projectOptions.str_support_space = line.replace(NameSpace.CONFIG_SUPPORT_SPACE_KEY + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_DEVICE_DOOR)) {
                                projectOptions.str_device_door = line.replace(NameSpace.CONFIG_DEVICE_DOOR + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_APP1)) {
                                projectOptions.app1 = line.replace(NameSpace.CONFIG_APP1 + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_APP4_NAME)) {
                                projectOptions.app4_name = line.replace(NameSpace.CONFIG_APP4_NAME + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_SUPPORT_QUICK)) {
                                projectOptions.str_support_quick = line.replace(NameSpace.CONFIG_SUPPORT_QUICK + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_SUPPORT_CONTROL)) {
                                projectOptions.str_support_control = line.replace(NameSpace.CONFIG_SUPPORT_CONTROL + "=", "");
                            }
                            if (line.contains(NameSpace.CONFIG_MODEL_TYPE)) {
                                projectOptions.str_model_type = line.replace(NameSpace.CONFIG_MODEL_TYPE + "=", "");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                Log.d(TAG, "getProjectOptions : "+projectOptions.str_logo+" / "+projectOptions.str_support_info+" / "+projectOptions.str_support_space
                        +" / "+projectOptions.str_device_door+" / "+projectOptions.app1+" / "+projectOptions.app4_name+ " / "+projectOptions.str_model_type);

            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            setIndicator();
        }

        @Override
        public void onPageSelected(int position) {
            CRT_PAGE = position;
            Log.d(TAG, "onPageSelected : position " + position);
            setRoomName(CRT_PAGE);
            checkDeviceValue(false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state){
                case ViewPager.SCROLL_STATE_DRAGGING:
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    break;
            }
        }
    };

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerEvent.EVENT_HANDLE_REMOVE_MOVABLE_VIEW:
                    removeMovableView(msg.obj);
                    break;

                case HandlerEvent.EVENT_HANDLE_UPDATE_DEVICE:
                    updateDevice(msg.obj);
                    break;

                case HandlerEvent.EVENT_HANDLE_TOAST_CONTROL_FAILED:
                    showToastControlFailed();
                    break;

                case HandlerEvent.EVENT_HANDLE_TOAST_ON_WORKING:
                    showToastOnWorking();
                    break;

                case HandlerEvent.EVENT_HANDLE_TOAST_NEED_SYNC:
                    showToastNeedSync();
                    break;

                case HandlerEvent.EVENT_HANDLE_TOAST_ONLY_LOCK:
                    showToastOnlyLocking();
                    break;

                case HandlerEvent.EVENT_HANDLE_REMOVE_REPORT:
                    removeByRootUuid(msg.obj);
                    break;

                case HandlerEvent.EVENT_HANDLE_HIDE_INTRO:
                    hideIntro();
                    break;

                case HandlerEvent.EVENT_HANDLE_PROGRESS_END:
                    startProgressEndAnimation();
                    break;

                case HandlerEvent.EVENT_HANDLE_SHOW_MENU:
                    showMenu();
                    break;

                case HandlerEvent.EVENT_HANDLE_MAKE_WIDGET:
                    makeWidget((ComponentName) msg.obj);
                    break;

                case HandlerEvent.EVENT_HANDLE_REDRAW:
                    try{
                        mPagerAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public void makeWidget(ComponentName componentName){
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        android.util.Log.d(TAG, "makeWidget appWidgetId " + appWidgetId);

        try {
            Intent intent = new Intent(
                    AppWidgetManager.ACTION_APPWIDGET_BIND);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER,
                    componentName);
            startActivityForResult(intent, NameSpace.REQUEST_BIND_APPWIDGET);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showMenu(){
        try{
            quick_show=true;
            setQuickVisibility(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hideMenu(){
        try{
            quick_show=false;
            setQuickVisibility(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

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
            lay_progress.startAnimation(mAnimGrow);
        }catch (Exception e){

        }
    }

    private void removeByRootUuid(Object object){
        try {
            Bundle bundle = (Bundle)object;
            String rootUuid = bundle.getString(NameSpace.ROOT_UUID);

            if(!TextUtils.isEmpty(rootUuid)) {
                com.commax.wirelesssetcontrol.Log.d(TAG, "removeByRootUuid : rootUuid = " + rootUuid);

                IconLayout layout = getTouchTopArea().getMatchDeviceIconData(rootUuid);
                if (layout!=null)
                    getTouchTopArea().removeItem(layout.getIconData());

                PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_REMOVE, rootUuid, "");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToastOnWorking(){
        if (activity_on) {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.working));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    private void showToastControlFailed(){
        if (activity_on) {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.control_fail));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    private void showToastNeedSync(){
        if (activity_on) {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.need_sync));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    private void showToastOnlyLocking(){
        if (activity_on) {
            Toast toast = new Toast(getApplicationContext());
            toastTextView.setText(getString(R.string.only_locking));
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            toast.show();
        }
    }

    public class RemoteServiceConnection implements ServiceConnection {

        IAdapter iadpter;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            try {
                iadpter = IAdapter.Stub.asInterface(service);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            iadpter = null;
        }

    }

    private void sendAddCommand(){
        try{
            commandTools.sendCommand("{\"command\":\"add\"} ");
            Log.d(TAG, "send Add Command");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendAddCancelCommand(){
        try{
            commandTools.sendCommand("{\"command\":\"addCancel\"} ");
            Log.d(TAG, "send Add Cancel Command");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setIntentFilter(){

        try {
            mFilter = new IntentFilter();
            mFilter.addAction(NameSpace.PAM_ACTION);
            mFilter.addAction(NameSpace.NAME_ACTION);
            mFilter.addAction(NameSpace.NAME_ACTION_CONTROL);
            mFilter.addAction(NameSpace.MODE_NAME_ACTION);
            mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            mFilter.addAction(NameSpace.ACCOUNT_INITIALIZE_ACTION);
            mFilter.addAction(NameSpace.NETWORK_ACTION);
            mFilter.addAction(NameSpace.DOOR_STATE_ACTION);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void makeIndicator(){
        lay_indicator.removeAllViewsInLayout();
        lay_indicator.removeAllViews();

        for (int i=0;i<PageDataManager.getInst().getPageSize();i++) {

            ImageView iv = new ImageView(getApplicationContext());

            if(i == CRT_PAGE)
                iv.setBackgroundResource(R.drawable.indicator_on);
            else
                iv.setBackgroundResource(R.drawable.indicator_off);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            int margin = (int)getResources().getDimension(R.dimen.main_indicator_right_left);
            layoutParams.setMargins(margin,0,margin,0);
            lay_indicator.addView(iv, layoutParams);
        }
    }

    private void setIndicator(){
        for (int i=0;i<lay_indicator.getChildCount();i++){

            if(lay_indicator.getChildAt(i) instanceof ImageView) {
                ImageView iv = (ImageView)lay_indicator.getChildAt(i);

                if(i == CRT_PAGE)
                    iv.setBackgroundResource(R.drawable.indicator_on);
                else
                    iv.setBackgroundResource(R.drawable.indicator_off);
            }
        }
    }


    private void addInfoReceiver(){


        try {
            iFilter = new IntentFilter();
            iFilter.addAction(NameSpace.PAM_ACTION);
            iFilter.addAction(NameSpace.WOEID_ACTION);
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

                            if (action.equals(NameSpace.PAM_ACTION)) {

                                if (projectOptions.str_support_info.equalsIgnoreCase("true")) {

                                    String strVal = intent.getStringExtra("eventString");
                                    Log.d(TAG, "mInfoReceiver PAM_ACTION : " + strVal);

                                    String event = PublicTools.getEventType(strVal);

                                    if (!TextUtils.isEmpty(event)) {
                                        switch (event) {
                                            case "removeReport":
                                                removeDevice(strVal);
                                                break;
                                            case "factoryReport":
                                                removeAllDeviceInfoView();
                                        }
                                    }
                                }
                            } else if (action.equals(NameSpace.WOEID_ACTION)) {

                                Log.d(TAG, "WOEID_ACTION event caught");

                                try {
                                    if (projectOptions.str_support_info.equalsIgnoreCase("true")) {

                                        //TODO 해당 버전에서 가치정보 거르지 않음
//                                    ArrayList<String> list_unselected_info = null;
//                                    InfoTools infoTools = new InfoTools();
//                                    list_unselected_info = infoTools.getUnSelectedInfo();
//
//                                    if (!list_unselected_info.contains(NameSpace.INFO_WEATHER)) {

                                        //infoList.updateWeather();
//                                    }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    private void updateAccountState(String state){
        try{
            if (!TextUtils.isEmpty(state)){
                if (state.equalsIgnoreCase("yes")){
                    iv_account.setVisibility(View.GONE);
                    lay_account_tip.setVisibility(View.GONE);
                    account_arrow.setVisibility(View.GONE);
                }else {
                    iv_account.setVisibility(View.VISIBLE);
                    lay_account_tip.setVisibility(View.VISIBLE);
                    checkAccountTipShow();
                }
            }else {
                iv_account.setVisibility(View.VISIBLE);
                lay_account_tip.setVisibility(View.VISIBLE);
                checkAccountTipShow();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateNetworkState(String state){
        try{
            if (!TextUtils.isEmpty(state)){
                if (state.equalsIgnoreCase("true")){
                    iv_external_network.setVisibility(View.GONE);
                }else {
                    iv_external_network.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateDoorState(String state){
        try{
            if (projectOptions.str_model_type.equalsIgnoreCase(NameSpace.MODEL_TYPE_FULL_IP)) {
                if (!TextUtils.isEmpty(state)) {
                    if (state.equalsIgnoreCase("on")) {
                        iv_door.setVisibility(View.GONE);
                    } else {
                        iv_door.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                iv_door.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //디바이스 타입만 삭제
    private void removeAllDeviceInfoView(){
        try{
            if (projectOptions.str_support_info.equalsIgnoreCase("true")){
                PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_REMOVE_TYPE, "", String.valueOf(IconData.TYPE_DEVICE));
                mPagerAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        activity_on = true;
        PublicTools.hideNavigationBar(this);
        Log.d(TAG, "lifeCycle onStart : activity_on = " + activity_on);
        checkAccountState();
        checkWifiState();

        try {
            if (mBroadcastReceiver == null) {
                Log.d(TAG, "onStart : new broadcast");
                mBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        try {
                            Log.d(TAG, "onReceive"+intent.getAction());
                            String action = intent.getAction();

                            if (action.equals(NameSpace.PAM_ACTION)) {

                                try {
                                    String strVal = intent.getStringExtra("eventString");
                                    Log.d(TAG, "PAM_ACTION : " + strVal);

                                    String event = PublicTools.getEventType(strVal);

                                    if (!TextUtils.isEmpty(event)) {
                                        switch (event) {
                                            case "addReport":
                                                addDevice(strVal);
                                                break;
                                            case "removeReport":
                                                removeDevice(strVal);
                                                break;
                                            case "report":
                                                updateReport(strVal);
                                                break;
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else if(action.equalsIgnoreCase(NameSpace.NAME_ACTION)){

                                Log.d(TAG, "NAME_ACTION event caught");

                                String rootUuidStr = intent.getStringExtra("rootUuidStr");
                                Log.d(TAG, "rootUuidStr : " + rootUuidStr);

                                String nickNameStr = intent.getStringExtra("nickNameStr");
                                Log.d(TAG, "nickNameStr : " + nickNameStr);

                                updateDeviceNickName(rootUuidStr, nickNameStr);

                            }else if(action.equalsIgnoreCase(NameSpace.NAME_ACTION_CONTROL)){

                                Log.d(TAG, "NAME_ACTION event caught");

                                String rootUuidStr = intent.getStringExtra("rootUuidStr");
                                Log.d(TAG, "rootUuidStr : " + rootUuidStr);

                                String nickNameStr = intent.getStringExtra("nickNameStr");
                                Log.d(TAG, "nickNameStr : " + nickNameStr);

                                updateDeviceNickName(rootUuidStr, nickNameStr);

                            }else if(action.equalsIgnoreCase(NameSpace.MODE_NAME_ACTION)){

                                Log.d(TAG, "MODE_NAME_ACTION event caught");

                                String modeName = intent.getStringExtra("modeNickNameStr");
                                Log.d(TAG, "modeName : " + modeName);

                                setModeText(modeName);

                            } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                                Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION: WifiManager changed event caught");

                                try {
                                    boolean connected = false;
                                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                                    if (info != null) {
                                        connected = info.isConnected();
                                        if (connected){
                                            iv_wifi.setBackgroundResource(R.mipmap.ic_network_wifi_white_36dp);
                                        } else {
                                            iv_wifi.setBackgroundResource(R.mipmap.ic_signal_wifi_off_white_36dp);
                                        }
                                    }

                                    android.util.Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION: Network connected : " + connected);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (action.equalsIgnoreCase(NameSpace.ACCOUNT_INITIALIZE_ACTION)){

                                Log.d(TAG, "ACCOUNT_INITIALIZE_ACTION");

                                try{
                                    String account_state = intent.getStringExtra(NameSpace.ACCOUNT_STATE_KEY);

                                    updateAccountState(account_state);

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            } else if (action.equals(NameSpace.NETWORK_ACTION)) {

                                String strVal = intent.getStringExtra("networkState");

                                Log.d(TAG, "NETWORK_ACTION event caught "+strVal);
                                updateNetworkState(strVal);
                            } else if (action.equals(NameSpace.DOOR_STATE_ACTION)) {

                                String strVal = intent.getStringExtra(NameSpace.DOOR_STATE_KEY);

                                Log.d(TAG, "DOOR_STATE_ACTION event caught "+strVal);
                                updateDoorState(strVal);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {
                    getApplicationContext().registerReceiver(mBroadcastReceiver, mFilter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (m_rc == null) {
                try {
                    m_rc = new RemoteServiceConnection();
                    Intent pamIntent = new Intent(IAdapter.class.getName());
                    pamIntent.setPackage("com.commax.pam.service");
                    boolean is = bindService(pamIntent, m_rc, BIND_AUTO_CREATE);
                    if (is) Log.i(TAG, "[Ventilation Activity]bindService Succeed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mAppWidgetHost.startListening();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "lifeCycle onResume");
        reloadPage();
        getModeName();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activity_on = false;
        Log.d(TAG, "lifeCycle onStop : activity_on =" + activity_on);

        try {
            onStop_called=true;
            if(mBroadcastReceiver!=null) {
                getApplicationContext().unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver=null;
            }

            mAppWidgetHost.stopListening();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(m_rc != null ){
            try {
                unbindService(m_rc);
                m_rc = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLifeQueryTask != null) {
            mLifeQueryTask.close();
            mLifeQueryTask = null;
        }
        CmxDeviceManager.getInst().closeDB();

        try {
            iFilter=null;
            if(mInfoReceiver!=null) {
                unregisterReceiver(mInfoReceiver);
                Log.d(TAG, "onDestroy unregisterReceiver");
                mInfoReceiver = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reloadPage(){
        makeIndicator();
        setRoomName(CRT_PAGE);
        checkDeviceValue(true);
        mPagerAdapter.notifyDataSetChanged();
    }

    private void roomInitializer(){
        PageDataManager.init(this);
        if(PageDataManager.getInst().getPageData(0) == null){
            Log.d(TAG, "No RoomInfo.xml data");
            makeFirstRoom();
        }
    }

    private void makeFirstRoom(){
        PageData data = new PageData();
        data.name = getString(R.string.living_room);
        data.background = "1";
        PageDataManager.getInst().addPage(data);
    }

    private void setRoomName(int page){
        PageData data = PageDataManager.getInst().getPageData(page);
        if(data != null)
            tv_space_name.setText(data.name);
    }

    private void removeMovableView(Object object){

        try {
            if(object != null){
                getTouchTopArea().removeView((View) object);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateDevice(Object object){

        try {
            if(object != null){

                String status ="", rootUuid="", sort="";
                Bundle bundle = (Bundle)object;
                status = bundle.getString(NameSpace.STATUS);
                rootUuid = bundle.getString(NameSpace.ROOT_UUID);
                sort = bundle.getString(NameSpace.SORT);
                updateDevice(rootUuid, sort, status);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateDeviceNickName(String rootUuid, String name){
        try{
            Log.d(TAG, "updateDeviceNickName : rootUuid = "+rootUuid+" / name "+name);
            if((!TextUtils.isEmpty(rootUuid))&&(!TextUtils.isEmpty(name))){
                IconLayout layout = getTouchTopArea().getMatchDeviceIconData(rootUuid);
                if (layout != null) {
                    ((DeviceIconData)layout.getIconData()).setNickName(name);
                    layout.updateIconData();
                }

                PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_UPDATE_NICK_NAME, rootUuid, name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateDevice(String rootUuid, String sort, String status){
        try{
            Log.d(TAG, "updateDevice : rootUuid = " + rootUuid + " / sort " + sort + " / status " + status);
            if((!TextUtils.isEmpty(rootUuid))&&(!TextUtils.isEmpty(sort))&&(!TextUtils.isEmpty(status))){
                IconLayout layout = getTouchTopArea().getMatchDeviceIconData(rootUuid);
                if (layout != null) {
                    DeviceIconData data = (DeviceIconData) layout.getIconData();
                    if (data.getControlType().equalsIgnoreCase(sort)) {
                        data.setStatus(status);
                        layout.updateIconData();
                    }
                }
                PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_UPDATE_STATUS, rootUuid, status);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setModeText(String str){
        IconLayout sceneLayout = mTouchBottomArea.findItem("com.commax.homeiot");
        if(sceneLayout != null) {
            try {
                if (!TextUtils.isEmpty(str)) {
                    sceneLayout.setCenterText(str);
                } else {
                    sceneLayout.setCenterText(getString(R.string.add_mode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getModeName(){
        try{
//            Log.d(TAG, "getModeName");

            try {
                FileEx io = new FileEx();
                String[] files = null;
                String mode_name = "";

                try {
                    files = io.readFile(NameSpace.NAME_MODE_PATH);
                } catch (FileNotFoundException e) {
                    // e.printStackTrace();
                } catch (IOException e) {
                    // e.printStackTrace();
                }

                if (files == null) {
                    return;
                }

                if (files.length > 0) {
                    if (files == null) {
                        return;
                    }
                    if ("".equals(files[0])) {
                        return;
                    }
                    if ("-1".equals(files[0])) {
                        return;
                    }
                }

                try {
                    for (int i = 0; i < files.length; i++) {
                        String line = files[i];

                        if (line.contains(NameSpace.MODE_KEY)) {
                            mode_name = line.replace(NameSpace.MODE_KEY + "=", "");
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                try {
                    Log.d(TAG, "getModeName :" + mode_name);

                    if (!TextUtils.isEmpty(mode_name)) {
                        setModeText(mode_name);
                    } else {
                        setModeText(getString(R.string.add_mode));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.edit_space:
                    try {
                        startEditSpaceActivity();
                        if (!bFading) {
                            bFading = true;
                            quick_show = false;
                            setQuickVisibility(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.edit_device:
                    try {
                        editDevice();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.quick_back:
                    try {
                        if (!bFading) {
                            bFading = true;
                            quick_show = false;
                            setQuickVisibility(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.bt_tip_close:
                    try {
                        lay_tip.setVisibility(View.INVISIBLE);
                        saveCheckTipShow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.bt_tip_x:
                    try {
                        lay_tip.setVisibility(View.INVISIBLE);
                        saveCheckTipShow();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.bt_check_show_tip:
                    try {
                        bNo_more_tip = !bNo_more_tip;
                        setCheckTipImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.add_widget:
                    try {
                        addWidget();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.add_device:
                    try {
                        actionAddDevice();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.add_apps:
                    try {
                        actionAddApps();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.iv_door:
                    try {
                        startDoorSettingActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.iv_account:
                    try {
                        runActivity("com.commax.login", "com.commax.login.MainActivity");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.iv_external_network:
                    try{
                        AlertDialog alertDialog = new AlertDialog(MainActivity.this, getString(R.string.external_network_pop));
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case R.id.iv_wifi:
                    try{
                        startWifiSettingActivity();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case R.id.btn_create_account:
                    try {
                        runActivity("com.commax.login", "com.commax.login.MainActivity");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.close_account_tip:
                    try{
                        saveCheckAccountTipShow();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case R.id.tv_guidance_account:
                    try {
                        runActivity("com.commax.login", "com.commax.login.MainActivity");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void actionAddApps(){
        try{
            hideMenu();
            startTouchMirrorAct(TouchMirrorAct.MODE_MENU_APPS);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void actionAddDevice(){
        try{
            hideMenu();
            startAddDeviceActivity();
            sendAddCommand();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startAddDeviceActivity(){
        try{

            //TODO Start add Device Activity
            Intent intent = new Intent();
            intent.putExtra(NameSpace.KEY_TAP_ID, NameSpace.KEY_ADD_DEVICE);
            intent.setClassName("com.commax.control", "com.commax.control.DialogActivity");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(intent, NameSpace.REQUEST_ADD_DEVICE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void editDevice(){
        try{
            hideMenu();
            startTouchMirrorAct(TouchMirrorAct.MODE_DEVICE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addWidget(){
        try{
            hideMenu();
            startTouchMirrorAct(TouchMirrorAct.MODE_WIDGET);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startEditSpaceActivity(){
        try{
            PageData data = PageDataManager.getInst().getPageData(CRT_PAGE);
            if(data != null) {
                Intent intent = new Intent(MainActivity.this, EditSpaceActivity.class);
                intent.putExtra(Constants.INTENT_KEY_ROOM_IDX, data.background);
                startActivity(intent);
                overridePendingTransition(0, 0); //전환 효과 제거
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startDoorSettingActivity(){
        try{

            Intent intent = new Intent();
            intent.setClassName("com.commax.settings", "com.commax.settings.MainActivity");
            intent.putExtra("from", "doorphoneSetting");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void startWifiSettingActivity(){
        try{

            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveCheckTipShow(){
        try{

            if (tipPref!=null){

                editor = tipPref.edit();
                if (bNo_more_tip) {
                    editor.putString(NameSpace.NOT_SHOW_TIP, "true");
                }else {
                    editor.putString(NameSpace.NOT_SHOW_TIP, "false");
                }
                editor.commit();

                try {
                    Process process = Runtime.getRuntime().exec("sync");
                    process.getErrorStream().close();
                    process.getInputStream().close();
                    process.getOutputStream().close();
                    process.waitFor();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveCheckAccountTipShow(){
        try{

            if (tipPref!=null){

                editor = tipPref.edit();
                editor.putString(NameSpace.ACCOUNT_TIP, "false");
                editor.commit();

                account_arrow.setVisibility(View.GONE);
                lay_account_tip.setVisibility(View.GONE);

                try {
                    Process process = Runtime.getRuntime().exec("sync");
                    process.getErrorStream().close();
                    process.getInputStream().close();
                    process.getOutputStream().close();
                    process.waitFor();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCheckTipImage(){
        try{
            Log.d(TAG, "setCheckTipImage "+bNo_more_tip);
            if (bNo_more_tip){
                bt_check_show_tip.setBackgroundResource(R.mipmap.btn_checkbox_s);
            }else {
                bt_check_show_tip.setBackgroundResource(R.mipmap.btn_checkbox_n);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setQuickVisibility(boolean animationOn){
        try {
            if (quick_show) {
                bFading = false;
                lay_quick.setVisibility(View.VISIBLE);
                if(animationOn)
                    lay_quick.startAnimation(mAnimFadeIn);
                lay_quick.setClickable(true);
            } else {
                if(animationOn)
                    lay_quick.startAnimation(mAnimFadeOut);
                else
                    lay_quick.setVisibility(View.GONE);
                lay_quick.setClickable(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode "+requestCode+" resultCode "+resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == NameSpace.REQUEST_ADD_DEVICE) {
                //addDeviceAutomatically(data);
                checkAddCancel(data);
            } else if (requestCode == NameSpace.REQUEST_PICK_ICON) {
                Log.d(TAG, "onActivityResult REQUEST_PICK_ICON ");
                updatePickData(data);
            } else if (requestCode == NameSpace.REQUEST_CREATE_APPWIDGET) {
                Log.d(TAG, "onActivityResult REQUEST_CREATE_APPWIDGET ");
                createWidget(data);
            } else if (requestCode == NameSpace.REQUEST_BIND_APPWIDGET){
                Log.d(TAG, "onActivityResult REQUEST_BIND_APPWIDGET ");
                configureWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }

            if (requestCode == NameSpace.REQUEST_ADD_DEVICE) {
                //addDeviceAutomatically(data);
                checkAddCancel(data);
            }

        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == NameSpace.REQUEST_ADD_DEVICE) {
                sendAddCancelCommand();
            }
        }
        PublicTools.hideNavigationBar(this);
    }

    /**
     * Checks if the widget needs any configuration. If it needs, launches the
     * configuration activity.
     */
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, NameSpace.REQUEST_CREATE_APPWIDGET);
        } else
            createWidget(data);
    }

    /**
     * Creates the widget and adds to our view layout.
     */
    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        android.util.Log.d(TAG, "createWidget appWidgetId " + appWidgetId);

        String addItemJson = (String) temp_intent.getExtras().get(Constants.INTENT_KEY_ADD_DATA);

        if(addItemJson == null || (addItemJson != null && addItemJson.length() == 0))
            return ;

        JSONObject jsonObject = IconDataParser.getJsonObjectForString(addItemJson);
        IconData addData = IconDataParser.getIconDataForJson(getApplicationContext(), jsonObject);

        if (addData != null) {
            addData.setRealWidget();
            addData.setWidgetId(appWidgetId);
            if (addData.getType() == IconData.TYPE_WIDGET) {
                getTouchTopArea().addItem(addData.getPosition().x, addData.getPosition().y, addData);
                savePageData();
                mPagerAdapter.notifyDataSetChanged();
            }
        }
    }

    //디바이스 자동 추가
    /* receiver에서 처리가 가능한 부분으로 임시 주석처리 - 삭제 nono
    private void addDeviceAutomatically(Intent data){
        try{
            Bundle extras = data.getExtras();
            ArrayList<String> strVal = extras.getStringArrayList(NameSpace.EXTRA_ADD_STRING);
            Log.d(TAG, "addDeviceAutomatically whole"+strVal);

            if (strVal!=null) {
                for (int i = 0; i < strVal.size(); i++) {
                    try {
                        Log.d(TAG, "addDeviceAutomatically item(" + i + ") : " + strVal.get(i));
                        addDevice(strVal.get(i));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
			savePageData();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private void checkAddCancel(Intent data){
        try{

            Bundle extras = data.getExtras();
            boolean addCancel = extras.getBoolean(NameSpace.EXTRA_ADD_CANCEL);
            Log.d(TAG, "checkAddCancel " + addCancel);
            if (addCancel){
                sendAddCancelCommand();
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
                  //  mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                  //  mHomeDown = true;
                    return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (!event.isCanceled()) {
                        // Do BACK behavior.
                    }
                   // mBackDown = true;
                    return true;
                case KeyEvent.KEYCODE_HOME:
                    if (!event.isCanceled()) {
                        // Do HOME behavior.
                    }
                    //mHomeDown = true;
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /***** TouchMirror Act 관련 메소드 *****/
    //기본 아이콘
    private void setDefaultIcon(){
        mTouchBottomArea.post(new Runnable() {
            @Override
            public void run() {
                String jsonStr = PageDataManager.getInst().getBottomGrid();

                if (jsonStr != null) {
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(jsonStr);
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject obj = array.getJSONObject(i);
                                IconData data = IconDataParser.getIconDataForJson(getApplicationContext(), obj);
                                mTouchBottomArea.addItem(data.getPosition().x, data.getPosition().y, data);
                            } catch (Exception e) {
                                Log.d(TAG, "Refresh Parse Error : " + e.getMessage());
                            }
                        }
                    } else {
                        IconData data = new IconData(IconData.TYPE_FIXED, null, "", 1, 1, 1, 1);
                        data.setImgResId(R.mipmap.btn_home_mode_n);
                        data.setPackageName("com.commax.homeiot");
                        data.setComponentName("com.commax.homeiot.ui.MainActivity");
                        data.setFullSize(true);
                        mTouchBottomArea.addItem((int) (mTouchBottomArea.getWidth() * 0.5f), 10, data);
                        getModeName();

                        data = new IconData(IconData.TYPE_FIXED, null, Constants.DEFAULT_ITEM_APPS, 1, 1, 1, 1);
                        data.setPackageName(Constants.DEFAULT_ITEM_APPS);
                        data.setComponentName(Constants.DEFAULT_ITEM_APPS);
                        data.setImgResId(R.mipmap.ic_apps_apps_n);
                        mTouchBottomArea.addItem((int) (mTouchBottomArea.getWidth() * 0.65f), 10, data);
                    }
                }

                mTouchBottomArea.alignByCenterApps();
            }
        });
    }

    //TouchMirror act 실행
    private void startTouchMirrorAct(int mode){
        Intent intent = new Intent(getApplicationContext(), TouchMirrorAct.class);

        PageData pageData = PageDataManager.getInst().getPageData(CRT_PAGE);
        if(pageData != null)
            intent.putExtra(Constants.INTENT_KEY_ROOM_IDX, pageData.background);

        intent.putExtra(Constants.INTENT_KEY_MODE, mode);
        intent.putExtra(Constants.INTENT_KEY_SCREEN_DATA_TOP, IconDataParser.getScreenXYToString(getTouchTopArea().getWidth(), getTouchTopArea().getHeight()));
        intent.putExtra(Constants.INTENT_KEY_SCREEN_DATA_BOTTOM, IconDataParser.getScreenXYToString(mTouchBottomArea.getWidth(), mTouchBottomArea.getHeight()));
        intent.putExtra(Constants.INTENT_KEY_ICON_DATA_TOP, IconDataParser.getIconDataForString(getTouchTopArea().getMapData()));
        intent.putExtra(Constants.INTENT_KEY_ICON_DATA_BTM, IconDataParser.getIconDataForString(mTouchBottomArea.getMapData()));

        startActivityForResult(intent, NameSpace.REQUEST_PICK_ICON);
        overridePendingTransition(0, 0); //전환 효과 제거
    }

    //미러 뷰로부터 수신된 데이터 처리
    private void updatePickData(Intent data){
        setUpdateData(data);//업데이트 정보 -> 먼저 처리
        setAddData(data);//추가된 아이템
        mTouchBottomArea.alignByCenterApps();
        savePageData();
    }

    //페이지 데이터 저장
    private void savePageData(){
        String jsonMap = IconDataParser.getIconDataForString(getTouchTopArea().getMapData());
        PageData pageData = PageDataManager.getInst().getPageData(CRT_PAGE);
        if(pageData != null) {
            pageData.iconData = jsonMap;
            PageDataManager.getInst().replacePage(CRT_PAGE, pageData);
        }

        String jsonBtmMap = IconDataParser.getIconDataForString(mTouchBottomArea.getMapData());
        PageDataManager.getInst().putBottomGrid(jsonBtmMap);
    }

    //뷰페이저에서 현재 터치영역을 가져옴
    private GridActionLayout getTouchTopArea(){
        CustomPageFragment customPageFragment = mPagerAdapter.getPageFrament(CRT_PAGE);
        return customPageFragment.getTouchArea();
    }

    //뷰페이저에서 특정 페이지 터치영역을 가져옴
    private GridActionLayout getTouchTopAreaAt(int page){
        CustomPageFragment customPageFragment = null;
        if (page>=0) {
            customPageFragment = mPagerAdapter.getPageFrament(page);
        }

        if (customPageFragment!=null){
            return customPageFragment.getTouchArea();
        }else {
            return null;
        }
    }

    //업데이트 정보를 갱신
    private void setUpdateData(Intent data){
        String updateType = (String) data.getExtras().get(Constants.INTENT_KEY_UPDATE_TARGET);
        String updateItemJson = (String) data.getExtras().get(Constants.INTENT_KEY_UPDATE_DATA);
        if (updateItemJson != null) {
            JSONArray updateArray = IconDataParser.getIconDataJsonArrayForString(updateItemJson);
            for (int i = 0; i < updateArray.length(); i++) {
                try {
                    IconData iconData = IconDataParser.getIconDataForJson(getApplicationContext(), updateArray.getJSONObject(i));
                    if (iconData.getType() == IconData.TYPE_WIDGET)
                        iconData.setFullSize(true);

                    if(updateType.equals(Constants.TARGET_TOP))
                        getTouchTopArea().refreshIconData(iconData);
                    else
                        mTouchBottomArea.refreshIconData(iconData);

                } catch (JSONException e) {
                    android.util.Log.d(TAG, "update pasre error : " + e.getMessage());
                }
            }
        }
    }

    //추가 정보를 갱신
    private void setAddData(Intent data){
        String updateType = (String) data.getExtras().get(Constants.INTENT_KEY_UPDATE_TARGET);
        String addItemJson = (String) data.getExtras().get(Constants.INTENT_KEY_ADD_DATA);

        if(addItemJson == null || (addItemJson != null && addItemJson.length() == 0))
            return ;

        JSONObject jsonObject = IconDataParser.getJsonObjectForString(addItemJson);
        IconData addData = IconDataParser.getIconDataForJson(getApplicationContext(), jsonObject);

        if (addData != null) {
            if (updateType.equals(Constants.TARGET_TOP)) {
                if (addData.getType() == IconData.TYPE_WIDGET) {
                    if(!getTouchTopArea().checkIconPosition(getTouchTopArea().getMapData(),
                            addData.getX()/getTouchTopArea().getIconWidth() , addData.getY()/getTouchTopArea().getIconHeight(),
                            addData.getSize().x, addData.getSize().y))
                        return ;

                    // Widget 일 경우, 임시로 인텐트를 저장하고 위젯 권한 허용창을 띄움
                    saveTempIntent(data);

                    ComponentName componentName = new ComponentName(addData.getPackageName(), addData.getComponentName());
                    makeWidget(componentName);
                }
                else if (addData.getType() == IconData.TYPE_DEVICE) {
                    if (addData instanceof DeviceIconData) {
                        DeviceIconData deviceIconData = (DeviceIconData) addData;
                        getTouchTopArea().addItem(addData.getPosition().x, addData.getPosition().y, deviceIconData);
                    }
                }
                else
                    getTouchTopArea().addItem(addData.getPosition().x, addData.getPosition().y, addData);
            }
            else
                mTouchBottomArea.addItem(addData.getPosition().x, addData.getPosition().y, addData);
        }
    }

    private void saveTempIntent(Intent data){
        temp_intent = data;
    }

    //터치 영역이 상단인지 하단인지 반환
    private GridActionLayout getTouchGrid(int y){
        int indicatorHeight = mTouchIndicatorLayout.getHeight();
        int topHeight = getTouchTopArea().getHeight();

        if(indicatorHeight <= y && y < indicatorHeight + topHeight)
            return getTouchTopArea();
        else if(indicatorHeight+ topHeight <= y)
            return mTouchBottomArea;
        return null;
    }

    //act실행
    private void runActivity(String packgeName, String className) {
        try {
            Intent intent = new Intent();
            intent.setClassName(packgeName, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            overridePendingTransition(0, 0); //전환 효과 제거
        } catch (Exception e) {
            Log.d(TAG, "runActivity excepton : " + e.getMessage());
        }
    }

    //롱터치에 대한 UI 변경
    private void setLongTouchMode(boolean isOn){
        if(isOn){
            getTouchTopArea().enableGuideLine(true);
            lay_main_top.setVisibility(View.INVISIBLE);
            lay_indicator.setVisibility(View.INVISIBLE);
            mIconDeleteLayout.setVisibility(View.VISIBLE);

            lay_main_top.startAnimation(mAnimFadeOut);
            lay_indicator.startAnimation(mAnimFadeOut);
            mIconDeleteLayout.startAnimation(mAnimFadeIn);
        }
        else {
            getTouchTopArea().enableGuideLine(false);
            lay_main_top.setVisibility(View.VISIBLE);
            lay_indicator.setVisibility(View.VISIBLE);
            mIconDeleteLayout.setVisibility(View.INVISIBLE);

            mIconDeleteLayout.startAnimation(mAnimFadeOut);
            lay_main_top.startAnimation(mAnimFadeIn);
            lay_indicator.startAnimation(mAnimFadeIn);
        }
    }

    //터치된 곳 아이템 정보 가져오기
    private IconData getTouchItem(int x, int y){
        int indicatorHeight = mTouchIndicatorLayout.getHeight();
        GridFrameLayout target = getTouchGrid(y);
        IconData data = null;

        if(target == getTouchTopArea())
            data = target.getTouchItemData(x, y-indicatorHeight);
        else if(target == mTouchBottomArea)
            data = target.getTouchItemData(x, y - getTouchTopArea().getHeight()-indicatorHeight);

        return data;
    }

    //해당 좌표를 구분하여 드래그 이벤트를 보냄
    private void dragSendEvent(int touchX, int touchY){
        int indicatorHeight = mTouchIndicatorLayout.getHeight();
        GridActionLayout target = getTouchGrid(touchY);

        mDeleteIcon.setSelected(false);
        if(target == getTouchTopArea()){ //상단 영역 일 때
            getTouchTopArea().actionDragEffect(touchX, touchY - indicatorHeight);
            mTouchBottomArea.showDragIcon(false);
            mTouchBottomArea.cancelAwayAction(true);
        }
        else if(target == mTouchBottomArea){ //하단 영역 일 때
            mTouchBottomArea.actionDragEffect(touchX, touchY - getTouchTopArea().getHeight() - indicatorHeight);
            getTouchTopArea().showDragIcon(false);
            getTouchTopArea().cancelAwayAction(true);
        }
        else { //인디케이터 영역일때
            if(checkDeleteIconArea(touchX, touchY))
                mDeleteIcon.setSelected(true);
            mTouchBottomArea.showDragIcon(false);
            getTouchTopArea().showDragIcon(false);
            getTouchTopArea().cancelAwayAction(true);
            mTouchBottomArea.cancelAwayAction(true);
        }
    }

    //터치 좌표가 휴지통 영역 인지 확인
    private boolean checkDeleteIconArea(int x, int y){
        int delIconX = (int) mDeleteIcon.getX();
        int delIconY = (int) mDeleteIcon.getY();
        int delIconWidth = mDeleteIcon.getWidth();
        int delIconHeight = mDeleteIcon.getHeight();

        if(x >= delIconX && x <= delIconX + delIconWidth &&
                y >= delIconY && y <= delIconY + delIconHeight){
            return true;
        }

        return false;
    }

    //롱터치 아이콘 포지션 변경
    private void setLongTouchIconPositon(int x, int y){
        if(mLongTouchLayout != null) {
            IconData data = mLongTouchLayout.getIconData();
            data.setX(x);
            data.setY(y);
            mLongTouchLayout.updatePosition();
        }
    }

    //롱터치가 끝났을 때 액션
    private void longTouchFinishAction(int x, int y){
        IconData replaceData = mLongTouchLayout.getIconData();
        Point touchPoint = replaceData.getPosition();
        int indicatorHeight = mTouchIndicatorLayout.getHeight();
        mTouchDummy.removeView(mLongTouchLayout);
        replaceData.setFullSize(false);

        GridActionLayout target = getTouchGrid(touchPoint.y);
        if(target == null){ //아이콘 삭제
            getTouchTopArea().cancelAwayAction(true);
            mTouchBottomArea.cancelAwayAction(true);
            if(checkDeleteIconArea(x, y)) {
                getTouchTopArea().removeItem(replaceData);
                mTouchBottomArea.removeItem(replaceData);
                mLongTouchLayout.drawableRecycle();
                mAppWidgetHost.deleteAppWidgetId(replaceData.getWidgetId());
            }
            else
                resetIcon(replaceData);
        }
        else {
            ArrayList<IconData> data = target.getUpdateData();

            int updateX = replaceData.getPosition().x;  //새로운 자리
            int updateY;

            if (target == getTouchTopArea()) {
                updateY = replaceData.getPosition().y - indicatorHeight;
                mTouchBottomArea.cancelAwayAction(true);
            } else {
                updateY = replaceData.getPosition().y - getTouchTopArea().getHeight() - indicatorHeight;
                getTouchTopArea().cancelAwayAction(true);
            }

            if (data.size() == 0) { //업데이트 항목이 없을 경우
                //해당 위치 추가하나 자리가 없는 경우 원래 좌표로 복귀
                if (target.addItem(updateX, updateY, replaceData) == null)
                    resetIcon(replaceData);
            } else { //업데이트 항목이 있는 경우 업데이트
                for (int i = 0; i < data.size(); i++) {
                    try {
                        IconData iconData = data.get(i);
                        if (iconData.getType() == IconData.TYPE_WIDGET)
                            iconData.setFullSize(true);
                        target.refreshIconData(iconData);
                    } catch (Exception e) {
                        android.util.Log.d(TAG, "update error : " + e.getMessage());
                    }
                }

                target.addItem(updateX, updateY, replaceData);
            }
        }

        savePageData();
        mTouchBottomArea.alignByCenterApps();
        mLongTouchLayout = null;
        setLongTouchMode(false);
    }

    //원래 위치로 되돌리기
    private void resetIcon(IconData replaceData){
        GridActionLayout target = getTouchGrid(mBackupPoint.y); //추가가 일어나지 않은 경우
        int indicatorHeight = mTouchIndicatorLayout.getHeight(), y;

        if (target == getTouchTopArea())
            y = mBackupPoint.y - indicatorHeight;
        else
            y = mBackupPoint.y - getTouchTopArea().getHeight() - indicatorHeight;

        target.addItem(mBackupPoint.x, y, replaceData);
    }

    //클릭 효과 켜기/끄기
    private void setIconClickMotion(int x, int y, boolean isClick){
        int indicatorHeight = mTouchIndicatorLayout.getHeight();
        GridFrameLayout target = getTouchGrid(y);

        if(target == getTouchTopArea())
            target.setClickMotion(x, y-indicatorHeight, isClick);
        else if(target == mTouchBottomArea)
            target.setClickMotion(x, y - getTouchTopArea().getHeight()-indicatorHeight, isClick);

    }

    //일반 액션
    private void actionTouch(IconData data){
        if(data != null) {
            int type = data.getType();

            if(type == IconData.TYPE_APPS) {
                Log.d(TAG, "[Click] TYPE_APPS : " + data.getName());
                runActivity(data.getPackageName(), data.getComponentName());
            }
            else if(type == IconData.TYPE_WIDGET) {
                Log.d(TAG, "[Click] TYPE_WIDGET : " + data.getName());
            }
            else if(type == IconData.TYPE_DEVICE) {
                Log.d(TAG, "[Click] TYPE_DEVICE : " + data.getName());
                controlDevice(data);
            }
            else if(type == IconData.TYPE_FIXED) {
                Log.d(TAG, "[Click] TYPE_FIXED : " + data.getName() + ", " + data.getComponentName());
                if(data.getComponentName().equals(Constants.DEFAULT_ITEM_APPS))
                    startTouchMirrorAct(TouchMirrorAct.MODE_APPS);
                else
                    runActivity(data.getPackageName(), data.getComponentName());
            }
        }
    }

    //롱터치 액션
    private void actionLongTouch(IconData data, int x, int y){
        mTouchBottomArea.cancelAwayAction(false);

        if(data == null) { //빈공간 클릭인 경우
            Message showMenu = mHandler.obtainMessage();
            showMenu.what = HandlerEvent.EVENT_HANDLE_SHOW_MENU;
            mHandler.sendMessage(showMenu);
        }
        else if(data.getType() != IconData.TYPE_FIXED){ //아이콘 인 경우, 고정타입이 아니면 드래그 효과 시작
            Log.d(TAG, "[LongTouch] : " + data.getName());

            data.setFullSize(true);

            GridFrameLayout target = getTouchGrid(y);
            mLongTouchLayout = target.findItem(data); //선택된 아이콘 Grid에서 지우기
            mLongTouchLayout.updateIconData();
            mLongTouchLayout.setDrawingCacheEnabled(true);
            target.removeItem(data);

            //드래그 효과 설정
            getTouchTopArea().setDragEffect(data);
            if(data.getType() == IconData.TYPE_WIDGET)
                getTouchTopArea().setDragIconBitmap(mLongTouchLayout.getDrawingCache());
            mTouchBottomArea.setDragEffect(data);
            if(data.getType() == IconData.TYPE_WIDGET)
                mTouchBottomArea.setDragIconBitmap(mLongTouchLayout.getDrawingCache());

            //하단 영역은 Apps아이콘만 적용
            if(data.getType() != IconData.TYPE_APPS)
                mTouchBottomArea.setAddLock(true);
            else
                mTouchBottomArea.setAddLock(false);

            setLongTouchMode(true);

            //원래 위치 저장
            GridFrameLayout prevTarget = getTouchGrid(mPrevPoint.y);
            Point iconPosition = data.getPosition();
            if(prevTarget == getTouchTopArea())
                mBackupPoint.set(iconPosition.x, iconPosition.y + mTouchIndicatorLayout.getHeight());
            else if(prevTarget == mTouchBottomArea)
                mBackupPoint.set(iconPosition.x, iconPosition.y + mTouchIndicatorLayout.getHeight() + getTouchTopArea().getHeight());
            else
                mBackupPoint.set(0, 0);

            //터치 이미지에 따른 오프셋
            mLongTouchLayout.setOffset(-target.getIconWidth()/2, -target.getIconHeight()/2);

            //더비 프레임으로 아이콘 이동
            mLongTouchLayout.setVisibility(View.INVISIBLE);
            mTouchDummy.addView(mLongTouchLayout);
            mTouchDummy.post(new Runnable() { //뷰 이동시 깜박임 때문에 포스트 처리
                @Override
                public void run() {
                    if(mLongTouchLayout != null)
                        mLongTouchLayout.updatePosition();
                }
            });
        }
    }

    public void controlDevice(IconData data){
        try{
            if (data instanceof DeviceIconData) {
                DeviceIconData deviceIconData = (DeviceIconData)data;

                if (!deviceIconData.isControlling()) {
                    String command_status = commandTools.getAfterStatus(deviceIconData.getStatus(), deviceIconData.getDeviceType());
                    Log.d(TAG, "controlDevice : set " + command_status);

                    if ((deviceIconData.getDeviceType().equalsIgnoreCase(CommaxDevice.GAS_LOCK)) &&
                            (deviceIconData.getStatus().equalsIgnoreCase(Status.LOCK))){

                        Log.d(TAG, "controlDevice : can use only locking");
                        showToastOnlyLocking();
                    }else{
                        deviceIconData.setControlling(true, mHandler, CRT_PAGE);
                        getTouchTopArea().refreshIconData(data);
                    }

                    commandTools.setStatus(deviceIconData, command_status);
                }else {
                    Log.d(TAG, "controlDevice : already controlling");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void removeDevice(String raw){
        String removedRoot = CmxDeviceDataParser.getRootUuidFromRaw(raw);

        Message removeDevice = mHandler.obtainMessage();
        removeDevice.what = HandlerEvent.EVENT_HANDLE_REMOVE_REPORT;
        Bundle bundle = new Bundle();
        bundle.putString(NameSpace.ROOT_UUID, removedRoot);
        removeDevice.obj = bundle;
        mHandler.sendMessage(removeDevice);
    }

    private void addDevice(final String raw){
        CmxDeviceManager.getInst().getMatchDevice(raw, new CmxDeviceDataReceiveInterface() {
            @Override
            public void getDeviceInfoCallback(final ArrayList<DeviceInfo> deviceList) {
                getTouchTopArea().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < deviceList.size(); i++) {
                            DeviceInfo deviceInfo = deviceList.get(i);

                            if (deviceInfo == null)
                                continue;
                            else if (deviceInfo.mainDevice == null || deviceInfo.deviceType == null)
                                continue;
                            else if (deviceInfo.deviceType.length() == 0)
                                continue;

                            String status = deviceInfo.mainDevice.value;

                            Drawable drawable = BitmapTool.copy(getApplicationContext(), ResourceManager.getDeviceBgResId(status));
                            DeviceIconData data =
                                    new DeviceIconData(IconData.TYPE_DEVICE,
                                            drawable,
                                            deviceInfo.nickName,
                                            0,
                                            0,
                                            1,
                                            1,
                                            deviceInfo.rootUuid,
                                            deviceInfo.deviceType,
                                            deviceInfo.nickName,
                                            deviceInfo.controlType,
                                            deviceInfo.main_subUuid,
                                            deviceInfo.mainDevice.value == null ? "" : deviceInfo.mainDevice.value);
                            IconLayout layout = getTouchTopArea().addItem(data);
                            if(layout != null) {
                                Log.d(">>> " + ((DeviceIconData) layout.getIconData()).getNickName());
                            }
                        }
                        savePageData();
                    }
                }, 1000); //화면 그려지는 속도를 고려햐여 딜레이 설정
            }
        });
    }

    //업데이트 리포트
    private void updateReport(String raw){
        String status = CmxDeviceDataParser.getStatusFromRaw(raw);

        if (!TextUtils.isEmpty(status)) {
            String updateRoot = CmxDeviceDataParser.getRootUuidFromRaw(raw);
            String sort = CmxDeviceDataParser.getSortFromRaw(raw);

            Message updateDevice = mHandler.obtainMessage();
            updateDevice.what = HandlerEvent.EVENT_HANDLE_UPDATE_DEVICE;
            Bundle bundle = new Bundle();
            bundle.putString(NameSpace.ROOT_UUID, updateRoot);
            bundle.putString(NameSpace.STATUS, status);
            bundle.putString(NameSpace.SORT, sort);
            updateDevice.obj = bundle;
            mHandler.sendMessage(updateDevice);
        }
    }

    //디바이스 아이콘 상태정보 업데이트 - 제어 디바이스
    private void checkDeviceValue(final boolean redraw){
        String[] list = {RootDevice.SWITCH, RootDevice.LOCK};
        CmxDeviceManager.getInst().getDevice(list, false, new CmxDeviceDataReceiveInterface() {
            @Override
            public void getDeviceInfoCallback(ArrayList<DeviceInfo> deviceList) {

                try {
                    android.util.Log.d(TAG, "getDeviceInfoCallback deviceList " + deviceList);

                    if ((deviceList!=null)&&(deviceList.size()>=1)) {

                        boolean removed = false;

                        for (int count = 0; count < PageDataManager.getInst().getPageSize(); count++) {
                            PageData pageData = PageDataManager.getInst().getPageData(count);
                            if (pageData == null)
                                return;

                            String jsonStr = pageData.iconData;

                            if (jsonStr != null && jsonStr.length() > 0) {
                                JSONArray array = IconDataParser.getIconDataJsonArrayForString(jsonStr);
                                int cnt = array.length();
                                for (int i = 0; i < cnt; i++) {

                                    boolean has_this = false;

                                    try {
                                        JSONObject obj = array.getJSONObject(i);
                                        IconData data = IconDataParser.getIconDataForJson(getApplicationContext(), obj);
                                        if (data instanceof DeviceIconData) {
                                            DeviceIconData deviceIconData = (DeviceIconData) data;

                                            for (int item = 0; item < deviceList.size(); item++) {


                                                DeviceInfo deviceInfo = deviceList.get(item);
                                                if (deviceInfo.rootUuid.equalsIgnoreCase(deviceIconData.getRootUuid())) {
                                                    has_this = true;
                                                }
                                                PageDataManager.getInst().updateDeviceJsonData(deviceInfo);
                                                updateScreenIcon(deviceInfo);
                                            }

                                            if (!has_this) {
                                                removed = true;
                                                android.util.Log.d(TAG, "getDeviceInfoCallback array.length() " + array.length());
                                                PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_REMOVE, deviceIconData.getRootUuid(), "");
                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        if (redraw&&removed) {
                            Message intro_msg = mHandler.obtainMessage();
                            intro_msg.what = HandlerEvent.EVENT_HANDLE_REDRAW;
                            mHandler.sendMessage(intro_msg);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    //실제 화면 데이터 갱신
    private void updateScreenIcon(DeviceInfo deviceInfo){
        if(deviceInfo == null)
            return;
        else if(deviceInfo.mainDevice == null || deviceInfo.deviceType == null)
            return;
        else if(deviceInfo.deviceType.length() == 0)
            return;

        try {
            final IconLayout layout = getTouchTopArea().getMatchDeviceIconData(deviceInfo.rootUuid);
            if(layout != null) {
                DeviceIconData data = (DeviceIconData) layout.getIconData();
                data.setNickName(deviceInfo.nickName);
                data.setStatus(deviceInfo.mainDevice.value == null ? "" : deviceInfo.mainDevice.value);
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            layout.updateIconData();
                        }
                    });
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
