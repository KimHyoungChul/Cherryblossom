package com.commax.wirelesssetcontrol.touchmirror;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.TabletTransformer;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.device.CmxDeviceDataParser;
import com.commax.wirelesssetcontrol.device.CmxDeviceDataReceiveInterface;
import com.commax.wirelesssetcontrol.device.CmxDeviceManager;
import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.tools.PublicTools;
import com.commax.wirelesssetcontrol.touchmirror.common.Constants;
import com.commax.wirelesssetcontrol.touchmirror.view.GridActionLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.GridFrameLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.IconGridPageFragment;
import com.commax.wirelesssetcontrol.touchmirror.view.IconGridViewPager;
import com.commax.wirelesssetcontrol.touchmirror.view.LinearParentLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.adapter.IconGridPagerAdapter;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.res.CBitmapDrawable;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.GridChecker;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;
import com.commax.wirelesssetcontrol.touchmirror.view.worker.IconDrawWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin Sung on 2017-02-07.
 */
public class TouchMirrorAct extends FragmentActivity {
    private final String TAG = "Touch_act";

    public static final int MODE_APPS = 0;
    public static final int MODE_MENU_APPS = 1;
    public static final int MODE_WIDGET = 2;
    public static final int MODE_DEVICE = 3;

    private final int LONG_TOUCH = 2;
    boolean activity_on=false;

    private final int SCREEN_TOUCH_GRID = 0; //터치 화면 상태값
    private final int SCREEN_ICON_GRID = 1;  //아이콘 화면 상태값

    private int mScreenState; //화면 모드 설정값
    private int mPageSaveCnt = 0; //아이템이 몇페이지 쌓였는지 저장

    private int mMode; //추가 하면 모드 (APPS, WIDGET)
    private Point mPrevTopScreenPoint; //이전 액티비티 상단 영역 데이터 저장용
    private Point mPrevBtmScreenPoint; //이전 액티비티 하단 영역 데이터 저장용

    //뷰 관련 데이터
    private FrameLayout mParent;
    private LinearParentLayout mTouchAllLayout, mIconGridBottom;
    private LinearLayout mLayIndicator, mTouchGridBg;
    private RelativeLayout mIconGridTitle;
    private FrameLayout mIconList;
    private IconGridViewPager mIconGridPager;
    private GridActionLayout mTouchGrid, mTouchBtmGrid;
    private TextView mIconListTitle, mIconLongTapText;
    private ImageButton mBtnBack;
    private LinearLayout mBgWrap;

    private IconGridPagerAdapter mPagerAdapter;
    private int mIconGridPagePosition = 0;

    private GridChecker mGridChecker;

    private IconDrawWorker mIconDrawWorker = null;

    //롱터치 관련 Value
    private int mTouchState; //터치 상태 저장
    private Point mPrevPoint;
    private IconLayout mLongTouchLayout = null;
    private Handler mLongTouchCheckHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case LONG_TOUCH:
                    Log.i(TAG, "Action : LONG TOUCH");
                    setLongTouchAction(msg.arg1, msg.arg2);
                    break;
            }
        }
    };

    //BR 관련
    private IntentFilter mFilter;                            //for mBroadcastReceiver
    private BroadcastReceiver mBroadcastReceiver = null;     //To receive PAM event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setWindowAnimations(0);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.s_act_touch_mirror);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        activity_on = true;
        PublicTools.hideNavigationBar(this);
        com.commax.wirelesssetcontrol.Log.d(TAG, "lifeCycle onStart : activity_on = " + activity_on);

        try {
            if (mBroadcastReceiver == null && mMode == MODE_DEVICE) {
                com.commax.wirelesssetcontrol.Log.d(TAG, "onStart : new broadcast");
                mBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        try {
                            com.commax.wirelesssetcontrol.Log.d(TAG, "onReceive" + intent.getAction());
                            String action = intent.getAction();

                            if (action.equals(NameSpace.PAM_ACTION)) {

                                try {
                                    String strVal = intent.getStringExtra("eventString");
                                    com.commax.wirelesssetcontrol.Log.d(TAG, "PAM_ACTION : " + strVal);

                                    String event = PublicTools.getEventType(strVal);

                                    if (!TextUtils.isEmpty(event)) {
                                        switch (event) {
                                            case "addReport":
                                                addDevice(strVal);
                                                break;
                                            case "removeReport":
                                                removeDevice(strVal);
                                                break;
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else if(action.equalsIgnoreCase(NameSpace.NAME_ACTION)){

                                com.commax.wirelesssetcontrol.Log.d(TAG, "NAME_ACTION event caught");

                                String rootUuidStr = intent.getStringExtra("rootUuidStr");
                                com.commax.wirelesssetcontrol.Log.d(TAG, "rootUuidStr : " + rootUuidStr);

                                String nickNameStr = intent.getStringExtra("nickNameStr");
                                com.commax.wirelesssetcontrol.Log.d(TAG, "nickNameStr : " + nickNameStr);

                                //추후 처리예정
                                updateDeviceNickName(rootUuidStr, nickNameStr);

                            }else if(action.equalsIgnoreCase(NameSpace.NAME_ACTION_CONTROL)){

                                com.commax.wirelesssetcontrol.Log.d(TAG, "NAME_ACTION event caught");

                                String rootUuidStr = intent.getStringExtra("rootUuidStr");
                                com.commax.wirelesssetcontrol.Log.d(TAG, "rootUuidStr : " + rootUuidStr);

                                String nickNameStr = intent.getStringExtra("nickNameStr");
                                com.commax.wirelesssetcontrol.Log.d(TAG, "nickNameStr : " + nickNameStr);

                                //추후 처리예정
                                updateDeviceNickName(rootUuidStr, nickNameStr);
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        activity_on = false;
        Log.d(TAG, "lifeCycle onStop : activity_on =" + activity_on);

        try {
            if(mBroadcastReceiver!=null) {
                getApplicationContext().unregisterReceiver(mBroadcastReceiver);
                mBroadcastReceiver=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if(mIconDrawWorker != null)
            mIconDrawWorker.closeTask();
        mIconDrawWorker = null;

        BitmapTool.recursive(getWindow().getDecorView().getRootView());
        setParamNull();
        System.gc();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    private void init(){
        initView();
        initData();
        initEvent();

        setMode(SCREEN_ICON_GRID);
    }

    private void initView(){
        PublicTools.hideNavigationBar(this);

        mParent = (FrameLayout) findViewById(R.id.area_parent);
        mTouchAllLayout = (LinearParentLayout) findViewById(R.id.area_touch_all_layout);
        mIconGridBottom = (LinearParentLayout) findViewById(R.id.area_lay_main_bottom);
        mLayIndicator = (LinearLayout) findViewById(R.id.area_icon_grid_indicator);
        mTouchGridBg  = (LinearLayout) findViewById(R.id.area_background_dim);
        mIconGridTitle = (RelativeLayout) findViewById(R.id.area_lay_main_top);
        mIconList = (FrameLayout) findViewById(R.id.area_icon_list);
        mIconListTitle = (TextView) findViewById(R.id.area_icon_list_title);
        mIconLongTapText = (TextView) findViewById(R.id.area_icon_long_tap_txt);
        mTouchGrid = (GridActionLayout) findViewById(R.id.area_touch_grid);
        mTouchBtmGrid = (GridActionLayout) findViewById(R.id.area_touch_btm_grid);
        mIconGridPager = (IconGridViewPager) findViewById(R.id.area_icon_grid);
        mBgWrap = (LinearLayout) findViewById(R.id.area_bg_wrap);
        mBtnBack = (ImageButton)findViewById(R.id.bt_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    //데이터 초기화
    private void initData(){
        mPagerAdapter = new IconGridPagerAdapter(getSupportFragmentManager());
        mIconGridPager.setAdapter(mPagerAdapter);
        mIconGridPager.setPageTransformer(false, new TabletTransformer());
        mIconGridPager.addOnPageChangeListener(mPageListener);

        String screenDataStr = (String) getIntent().getExtras().get(Constants.INTENT_KEY_SCREEN_DATA_TOP);
        mPrevTopScreenPoint = IconDataParser.getScreenXYForString(screenDataStr);
        screenDataStr = (String) getIntent().getExtras().get(Constants.INTENT_KEY_SCREEN_DATA_BOTTOM);
        mPrevBtmScreenPoint = IconDataParser.getScreenXYForString(screenDataStr);
        Log.i(TAG, "이전 상단 영역 너비 : " + mPrevTopScreenPoint.x + ", 높이 :" + mPrevTopScreenPoint.y);
        Log.i(TAG, "이전 하단 영역 너비 : " + mPrevBtmScreenPoint.x + ", 높이 :" + mPrevBtmScreenPoint.y);

        mMode = getIntent().getExtras().getInt(Constants.INTENT_KEY_MODE);
        mPrevPoint = new Point();

        if(mMode == MODE_WIDGET) {
            //페이지 사이즈 산출
            AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
            ArrayList<AppWidgetProviderInfo> widgetList = (ArrayList)manager.getInstalledProviders();

            int pageSize = (int)Math.ceil(widgetList.size()/4.0f); //한화면에 4개의 위젯
            setPagerUpdate(pageSize);

            IconGridPageFragment.setScreenSize(Constants.AREA_WIDGET_COL, Constants.AREA_WIDGET_ROW);
            mIconListTitle.setText(R.string.tm_widget);
            mIconLongTapText.setText(R.string.widget_long_tap_str);
            mGridChecker = new GridChecker(Constants.AREA_WIDGET_COL, Constants.AREA_WIDGET_ROW);
            mIconDrawWorker = new IconDrawWorker(getApplicationContext(), MODE_WIDGET, mHandler);
            mIconGridPager.postDelayed(mIconDrawWorker, 50);
        }
        else if(mMode == MODE_DEVICE){
            setPagerUpdate(1); //디바이스 총 사이즈 계산 필요 @@@@
            IconGridPageFragment.setScreenSize(Constants.AREA_APPS_COL, Constants.AREA_APPS_ROW);
            mIconListTitle.setText(R.string.tm_device);
            mIconLongTapText.setText(R.string.device_long_tap_str);
            mGridChecker = new GridChecker(Constants.AREA_APPS_COL, Constants.AREA_APPS_ROW);
            mIconGridPager.postDelayed(new IconDrawWorker(getApplicationContext(), MODE_DEVICE, mHandler), 50);
        }
        else {
            //페이지 사이즈 산출
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> launcherList = getPackageManager().queryIntentActivities(intent, 0);

            int hide_count = 0;

            try {
                FileEx fileEx = new FileEx();
                ArrayList<Pair<String, String>> hide = fileEx.readHideFile();

                for (int i = 0; i < launcherList.size(); i++) {

                    ResolveInfo item = launcherList.get(i);

                    if (hide != null) {
                        for (int count = 0; count < hide.size(); count++) {
                            if (item.activityInfo.name.equalsIgnoreCase(hide.get(count).second)) {
                                Log.d(TAG, "hide " + hide.get(count).second);
                                hide_count++;
                                continue;
                            }
                        }
                    }
                }
                Log.d(TAG, "launcherList.size() "+launcherList.size()+" / hide_count " + hide_count);
            }catch (Exception e){
                e.printStackTrace();
            }

            int pageSize = (int)Math.ceil((launcherList.size()-hide_count)/(double)(Constants.AREA_APPS_COL * Constants.AREA_APPS_ROW));
            setPagerUpdate(pageSize);

            IconGridPageFragment.setScreenSize(Constants.AREA_APPS_COL, Constants.AREA_APPS_ROW);
            mIconListTitle.setText(R.string.apps);
            mIconLongTapText.setText(R.string.apps_long_tap_str);
            mGridChecker = new GridChecker(Constants.AREA_APPS_COL, Constants.AREA_APPS_ROW);
            mIconGridPager.postDelayed(new IconDrawWorker(this, MODE_APPS, mHandler), 50);
        }

        setBackground();
        setTouchAreaData();
        setTouchBtmAreaData();

        mFilter = new IntentFilter();
        mFilter.addAction(NameSpace.PAM_ACTION);
        mFilter.addAction(NameSpace.NAME_ACTION);
        mFilter.addAction(NameSpace.NAME_ACTION_CONTROL);
    }

    //이벤트 초기화
    private void initEvent(){
        mParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mTouchState = motionEvent.getAction();
                Point point;
                switch(mTouchState){
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "Action : TOUCH DOWN");
                        //point = mTouchAllLayout.convertNoMarginXY((int)motionEvent.getX(), (int)motionEvent.getY());
                        Message msg = new Message();
                        msg.what = LONG_TOUCH;
                        msg.arg1 = (int)motionEvent.getX();
                        msg.arg2 = (int)motionEvent.getY();
                        mPrevPoint.set((int)motionEvent.getX(), (int)motionEvent.getY());
                        setIconClickMotion(mPrevPoint.x, mPrevPoint.y, true);
                        mLongTouchCheckHandler.sendMessageDelayed(msg, Constants.LONG_TOUCH_TIME); //롱터치 인식 용
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(checkTouchPosition((int)motionEvent.getX(), (int)motionEvent.getY()))
                            mLongTouchCheckHandler.removeMessages(LONG_TOUCH);

                        if(mLongTouchLayout != null && mScreenState == SCREEN_TOUCH_GRID){
                            if(checkTouchPosition((int)motionEvent.getX(), (int)motionEvent.getY()))
                                setIconClickMotion(mPrevPoint.x, mPrevPoint.y, false);
                            point = mTouchAllLayout.convertNoMarginXY((int)motionEvent.getX(), (int)motionEvent.getY());
                            setLongTouchIconPositon(point.x, point.y);
                            dragSendEvent(point.x, point.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "Action : TOUCH UP");
                        setIconClickMotion(mPrevPoint.x, mPrevPoint.y, false);

                        if(mLongTouchLayout != null && mScreenState == SCREEN_TOUCH_GRID)
                            longTouchFinishAction();
                        else if(mScreenState == SCREEN_ICON_GRID){
                            if(mLongTouchCheckHandler.hasMessages(LONG_TOUCH) && MODE_APPS == mMode
                                    && !checkTouchPosition((int)motionEvent.getX(), (int)motionEvent.getY())) {
                                IconData iconData = getTouchItem((int) motionEvent.getX(), (int) motionEvent.getY());
                                if(iconData != null)
                                    runActivity(iconData.getPackageName(), iconData.getComponentName());
                            }

                        }
                        mLongTouchCheckHandler.removeMessages(LONG_TOUCH);
                        mTouchGrid.stopDragEffect();
                        mTouchBtmGrid.stopDragEffect();
                        break;
                    default:
                        mTouchState = -1;
                }

                if(mLongTouchLayout == null)
                    mIconGridPager.onTouchEvent(motionEvent);
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

    //터치된 곳 아이템 정보 가져오기
    private IconData getTouchItem(int x, int y){
        IconData data = null;

        if(getTouchAreaLeftMargin() < x && x < getTouchArea().getWidth() + getTouchAreaLeftMargin())
            data = getTouchArea().getTouchItemData(x-getTouchAreaLeftMargin(), y - mIconGridTitle.getHeight());

        return data;
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
            com.commax.wirelesssetcontrol.Log.d(TAG, "runActivity excepton : " + e.getMessage());
        }
    }

    //현재 선택된 영역을 기준으로 아이콘 크기를 알아옴
    private Point getTouchItemWidth(int y){
        if(mLongTouchLayout != null){
            Point point = new Point();

            IconData data = mLongTouchLayout.getIconData();
            GridActionLayout target = getTouchGrid(y);
            if(target == null)
                point.set((mTouchGrid.getIconWidth() * data.getSize().x)/2, mTouchGrid.getIconHeight() * data.getSize().y/2);
            else
                point.set((target.getIconWidth() * data.getSize().x)/2, target.getIconHeight() * data.getSize().y/2);
            return point;
        }

        return null;
    }

    //터치 영역이 상단인지 하단인지 반환
    private GridActionLayout getTouchGrid(int y){
        int topHeight = mTouchGrid.getHeight();
        if(y < topHeight)
            return mTouchGrid;

        return mTouchBtmGrid;
    }

    //해당 페이지의 배경을 그린다

    private void setBackground(){
        String roomName = getIntent().getExtras().getString(Constants.INTENT_KEY_ROOM_IDX);
        Drawable res = ResourceManager.getRoomBackgroundResource(roomName, getApplicationContext());
        if(res != null)
            mParent.setBackground(BitmapTool.blur(getApplicationContext(), (CBitmapDrawable) res, 15));
        else{
            res = BitmapTool.copy(this, R.mipmap.bg_home_img_1);
            mParent.setBackground(BitmapTool.blur(getApplicationContext(), (CBitmapDrawable) res, 15));
        }

        //디밍 영역
//        mTouchGridBg.setBackground(BitmapTool.copy(this, R.mipmap.bg_home_wrap));
        mBgWrap.setBackground(BitmapTool.copy(this, R.mipmap.bg_home_wrap));

        if(MODE_APPS == mMode) {
            mIconList.setBackground(BitmapTool.copy(this, R.mipmap.bg_sc_default));
            mBtnBack.setBackground(BitmapTool.copy(this, R.mipmap.btn_title_home_white_n));
        }
        else
            mBtnBack.setBackground(BitmapTool.copy(this, R.mipmap.btn_title_back_white_n));
    }

    //인디케이터 그리기
    private void makeIndicator(){
        mLayIndicator.removeAllViewsInLayout();
        mLayIndicator.removeAllViews();
        if(mPagerAdapter.getCount() <= 1)
            mLayIndicator.setVisibility(View.INVISIBLE);
        else{
            for (int i = 0; i< mPagerAdapter.getCount(); i++) {
                ImageView iv = new ImageView(getApplicationContext());

                if(i == mIconGridPagePosition)
                    iv.setBackgroundResource(R.drawable.indicator_on);
                else
                    iv.setBackgroundResource(R.drawable.indicator_off);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                int margin = (int)getResources().getDimension(R.dimen.main_indicator_right_left);

                layoutParams.setMargins(margin,0,margin,0);
                mLayIndicator.addView(iv, layoutParams);
            }
            mLayIndicator.setVisibility(View.VISIBLE);
        }
    }

    //이전 액티비티에서 전달 받은 데이터를 그림
    private void setTouchAreaData(){
        mTouchGrid.init(Constants.AREA_TOUCH_COL, Constants.AREA_TOUCH_ROW);
        mTouchGrid.enableGuideLine(true);
        mTouchGrid.post(new Runnable() {
            @Override
            public void run() {
                String itemListStr = (String) getIntent().getExtras().get(Constants.INTENT_KEY_ICON_DATA_TOP);

                if(itemListStr!=null){
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(itemListStr);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jsonItem = array.getJSONObject(i);
                                final IconData data = IconDataParser.getIconDataForJson(getApplicationContext(), jsonItem);
                                if (data != null) {
                                    if(data.getType() == IconData.TYPE_WIDGET) {
                                        data.setFullSize(true);
                                        Point point = mTouchGrid.convertLocalXY(mPrevTopScreenPoint.x, mPrevTopScreenPoint.y, data.getPosition().x, data.getPosition().y);
                                        mTouchGrid.addItem(point.x, point.y, data);
                                    } else if (data.getType() == IconData.TYPE_DEVICE){
                                        DeviceIconData deviceIconData = (DeviceIconData)data;
                                        Point point = mTouchGrid.convertLocalXY(mPrevTopScreenPoint.x, mPrevTopScreenPoint.y, deviceIconData.getPosition().x, deviceIconData.getPosition().y);
                                        mTouchGrid.addItem(point.x, point.y, deviceIconData);
                                    } else {
                                        Point point = mTouchGrid.convertLocalXY(mPrevTopScreenPoint.x, mPrevTopScreenPoint.y, data.getPosition().x, data.getPosition().y);
                                        mTouchGrid.addItem(point.x, point.y, data);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, "parse error : " + e.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    //터치 하단 영역 데이터
    private void setTouchBtmAreaData(){
        mTouchBtmGrid.init(Constants.AREA_TOUCH_BOTTOM_COL, Constants.AREA_TOUCH_BOTTOM_ROW);
        mTouchBtmGrid.post(new Runnable() {
            @Override
            public void run() {
                String itemListStr = (String) getIntent().getExtras().get(Constants.INTENT_KEY_ICON_DATA_BTM);

                if(itemListStr!=null){
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(itemListStr);
                    if (array != null) {
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jsonItem = array.getJSONObject(i);
                                final IconData data = IconDataParser.getIconDataForJson(getApplicationContext(), jsonItem);
                                if (data != null && data.getType() != IconData.TYPE_WIDGET) {
                                    Point point = mTouchBtmGrid.convertLocalXY(mPrevBtmScreenPoint.x, mPrevBtmScreenPoint.y, data.getPosition().x, data.getPosition().y);
                                    mTouchBtmGrid.addItem(point.x, point.y, data);
                                }
                            } catch (JSONException e) {
                                Log.d(TAG, "parse error : " + e.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    //화면 데이터를 저장
    private void addPageData(IconData data){
        JSONObject jsonData = IconDataParser.getJSonForIconData(data);
        mPagerAdapter.addPageData(mPageSaveCnt, jsonData);
    }

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerEvent.EVENT_HANDLE_ADD_GRID_DEVICE:
                case HandlerEvent.EVENT_HANDLE_ADD_GRID_ITEM:
                    Log.d(TAG, (msg.what == HandlerEvent.EVENT_HANDLE_ADD_GRID_ITEM ) ? "> EVENT_HANDLE_ADD_GRID_ITEM" : "> EVENT_HANDLE_ADD_GRID_DEVICE");
                    addIconData(msg.obj);
                    break;
            }
        }
    };

    //워커로부터 전달 받은 아이콘을 그림
    private void addIconData(Object object){
        try{
            if (!mGridChecker.addData(new Point(1, 1))){
                mPageSaveCnt++;
                mGridChecker.clear();
                mGridChecker.addData(new Point(1, 1));
            }

            IconData data = (IconData)object;
            addPageData(data);

            IconGridPageFragment iconGridPageFragment = mPagerAdapter.getPageFrament(mPageSaveCnt);
            if(iconGridPageFragment != null && iconGridPageFragment.getTouchArea() != null)
                iconGridPageFragment.getTouchArea().addItemResize(data, new Point(1, 1));
        }catch (Exception e){
            Log.d(TAG, ">> addIconData : " + e.getMessage());
        }
    }

    //화면 상태 설정
    private void setMode(int screenState){
        mScreenState = screenState;

        if(screenState == SCREEN_TOUCH_GRID){
            mTouchAllLayout.setVisibility(View.VISIBLE);
            mTouchGridBg.setVisibility(View.VISIBLE);
            mIconList.setVisibility(View.INVISIBLE);
            Log.d(TAG, "setMode mTouchGridBg VISIBLE");
        }
        else{
            mTouchAllLayout.setVisibility(View.INVISIBLE);
            mTouchGridBg.setVisibility(View.INVISIBLE);
            mIconList.setVisibility(View.VISIBLE);
            Log.d(TAG, "setMode mTouchGridBg VISIBLE");
        }
    }

    //롱 터치가 발생했을때 액션 처리
    private void setLongTouchAction(int x, int y){
        if(mScreenState == SCREEN_ICON_GRID){
            IconData data = getTouchItem(x, y);
            if(data != null) {
                data.setFullSize(true);
                if(data.getType() == IconData.TYPE_WIDGET) {
                    data.setAlignWidget(false);
                }
                else if (data.getType() == IconData.TYPE_DEVICE){
                    DeviceIconData deviceIconData = (DeviceIconData)data;
                    data = deviceIconData;
                }

                if(data.getType() != IconData.TYPE_APPS) //하단 영역은 Apps아이콘만 적용
                    mTouchBtmGrid.setAddLock(true);
                else
                    mTouchBtmGrid.setAddLock(false);

                mLongTouchLayout = mTouchGrid.createIcon(data);
                mLongTouchLayout.setDrawingCacheEnabled(true);
                mLongTouchLayout.setOffset(-mTouchGrid.getIconWidth()/2, -mTouchGrid.getIconHeight()/2);

                mTouchGrid.setDragEffect(data);
                mTouchBtmGrid.setDragEffect(data);
                setMode(SCREEN_TOUCH_GRID);
            }
        }
        else if(mScreenState == SCREEN_TOUCH_GRID) {
            //터치 그리드 롱터치 액션
        }
    }

    //해당 좌표를 구분하여 드래그 이벤트를 보냄
    private void dragSendEvent(int touchX, int touchY){
        GridActionLayout target = getTouchGrid(touchY);
        if(target == mTouchGrid){
            mTouchGrid.actionDragEffect(touchX, touchY);
            mTouchBtmGrid.showDragIcon(false);
        }
        else if(target == mTouchBtmGrid){
            mTouchBtmGrid.actionDragEffect(touchX, touchY - mTouchGrid.getHeight());
            mTouchGrid.showDragIcon(false);
        }
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
    private void longTouchFinishAction(){
        GridActionLayout target = getTouchGrid(mLongTouchLayout.getIconData().getPosition().y);
        Intent intent = new Intent();

        if(target == mTouchGrid)
            intent.putExtra(Constants.INTENT_KEY_UPDATE_TARGET, Constants.TARGET_TOP);
        else
            intent.putExtra(Constants.INTENT_KEY_UPDATE_TARGET, Constants.TARGET_BOTTOM);
        intent.putExtra(Constants.INTENT_KEY_ADD_DATA, getLongTouchEndItem(target));

        String updateStr = getUpdateItemList(target);
        if(updateStr != null && updateStr.length() > 0)
            intent.putExtra(Constants.INTENT_KEY_UPDATE_DATA, updateStr);

        setResult(RESULT_OK, intent);

        mLongTouchLayout.drawableRecycle();
        mLongTouchLayout = null;

        finish();
        overridePendingTransition(0, 0);
    }

    //터치 된 아이템 넣기
    private String getLongTouchEndItem(GridActionLayout target){
        IconData data = mLongTouchLayout.getIconData();
        if(target == mTouchBtmGrid && data.getType() != IconData.TYPE_APPS)
            return "";

        data = IconData.copy(mLongTouchLayout.getIconData());
        int yPos = data.getPosition().y;

        Point point;
        if(target == mTouchBtmGrid) {
            yPos -= mTouchGrid.getHeight();
            point = target.convertGlobalXY(mPrevBtmScreenPoint.x, mPrevBtmScreenPoint.y, data.getPosition().x, yPos);
        }
        else
            point = target.convertGlobalXY(mPrevTopScreenPoint.x, mPrevTopScreenPoint.y, data.getPosition().x, yPos);

        data.setX(point.x);
        data.setY(point.y);
        if(data.getType() == IconData.TYPE_WIDGET)
            data.setAlignWidget(false);

        if (data instanceof DeviceIconData)
            data.setDeviceDefault(false);

        JSONObject jsonObject = IconDataParser.getJSonForIconData(data);
        return jsonObject.toString();
    }

    //업데이트가 일어난 항목 넣기
    private String getUpdateItemList(GridActionLayout target){
        if(target == mTouchBtmGrid && mLongTouchLayout.getIconData().getType() != IconData.TYPE_APPS)
            return "";

        ArrayList<IconData> updateList = target.getUpdateData();
        JSONArray updateArray;

        if(updateList.size() > 0) {
            updateArray = new JSONArray();
            for (int i = 0; i < updateList.size(); i++) {
                Point updatePoint;
                if(target == mTouchBtmGrid)
                    updatePoint = target.convertGlobalXY(mPrevBtmScreenPoint.x, mPrevBtmScreenPoint.y,
                        updateList.get(i).getPosition().x, updateList.get(i).getPosition().y);
                else
                    updatePoint = target.convertGlobalXY(mPrevTopScreenPoint.x, mPrevTopScreenPoint.y,
                            updateList.get(i).getPosition().x, updateList.get(i).getPosition().y);
                updateList.get(i).setX(updatePoint.x);
                updateList.get(i).setY(updatePoint.y);
                updateArray.put(IconDataParser.getJSonForIconData(updateList.get(i)));
            }
            return updateArray.toString();
        }

        return null;
    }

    //뷰페이저에서 현재 터치영역을 가져옴
    private GridFrameLayout getTouchArea(){
        IconGridPageFragment iconGridPageFragment = mPagerAdapter.getPageFrament(mIconGridPagePosition);
        return iconGridPageFragment.getTouchArea();
    }

    //뷰페이저에서 현재 터치영역을 가져옴
    private int getTouchAreaLeftMargin(){
        IconGridPageFragment iconGridPageFragment = mPagerAdapter.getPageFrament(mIconGridPagePosition);
        return iconGridPageFragment.getLeftmargin();
    }

    //페이저 기본정보 업데이트
    private void setPagerUpdate(int size){
        IconGridPagerAdapter.setPageSize(size);
        mPagerAdapter.notifyDataSetChanged();
        makeIndicator();
    }


    //클릭 효과 켜기/끄기
    private void setIconClickMotion(int x, int y, boolean isClick){
        if(getTouchAreaLeftMargin() < x && x < getTouchArea().getWidth() + getTouchAreaLeftMargin() || !isClick)
            getTouchArea().setClickMotion(x - getTouchAreaLeftMargin(), y - mIconGridTitle.getHeight(), isClick);
    }

    private void setParamNull(){
        mPrevTopScreenPoint = null;
        mPrevBtmScreenPoint = null;
        mParent = null;
        mTouchAllLayout = null;
        mIconGridBottom = null;
        mLayIndicator = null;
        mIconGridTitle = null;
        mIconList = null;
        mIconGridPager = null;
        mTouchGrid = null;
        mTouchBtmGrid = null;
        mIconListTitle = null;
        mBtnBack = null;
        mPagerAdapter = null;
        mGridChecker = null;
        mPrevPoint = null;
        mLongTouchLayout = null;
        mLongTouchCheckHandler = null;
        mFilter = null;
        mBroadcastReceiver = null;
    }

    private void addDevice(final String raw){
        CmxDeviceManager.getInst().getMatchDevice(raw, new CmxDeviceDataReceiveInterface() {
            @Override
            public void getDeviceInfoCallback(ArrayList<DeviceInfo> deviceList) {
                for(int i=0; i<deviceList.size(); i++) {
                    final DeviceInfo deviceInfo = deviceList.get(i);

                    if(deviceInfo == null)
                        continue;
                    else if(deviceInfo.mainDevice == null || deviceInfo.deviceType == null)
                        continue;
                    else if(deviceInfo.deviceType.length() == 0)
                        continue;

                    mTouchGrid.post(new Runnable() {
                        @Override
                        public void run() {
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
                            mTouchGrid.addItem(data);
                            addPageData(data);  //화면 json 저장
                            mPagerAdapter.notifyDataSetChanged();

                            //PageDataManager.getInst().addPageItem(0, data); //홈 화면 json 저장
                        }
                    });
                }
            }
        });
    }

    private void removeDevice(String raw){
        String removedRoot = CmxDeviceDataParser.getRootUuidFromRaw(raw);
        IconLayout layout = getTouchArea().getMatchDeviceIconData(removedRoot);
        if (layout!=null)
            getTouchArea().removeItem(layout.getIconData());
        IconGridPagerAdapter.removePageDataByRootUuid(removedRoot); //현재 화면 json 변경
        PageDataManager.getInst().updateDevice(PageDataManager.PAGE_MODE_DEVICE_REMOVE, removedRoot, ""); //홈화면 json 변경
    }

    private void updateDeviceNickName(String rootUuid, String name){
        try{
            com.commax.wirelesssetcontrol.Log.d(TAG, "updateDeviceNickName : rootUuid = " + rootUuid + " / name " + name);
            if((!TextUtils.isEmpty(rootUuid))&&(!TextUtils.isEmpty(name))){
                IconLayout layout = getTouchArea().getMatchDeviceIconData(rootUuid);
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

    ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            //페이지 이동시 데이터를 저장
            mPagerAdapter.replacePageData(mIconGridPagePosition, IconDataParser.getIconDataForString(getTouchArea().getMapData()));
            mIconGridPagePosition = position;
            makeIndicator();
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };
}
