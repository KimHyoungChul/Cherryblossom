package com.commax.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.adapter.aidl.IAdapter;
import com.commax.control.Card_list.Card_Aircon;
import com.commax.control.Card_list.Card_Boiler;
import com.commax.control.Card_list.Card_Curtain;
import com.commax.control.Card_list.Card_DimmerSwitch;
import com.commax.control.Card_list.Card_DoorLock;
import com.commax.control.Card_list.Card_FCU;
import com.commax.control.Card_list.Card_Fan;
import com.commax.control.Card_list.Card_GasDetectSensor;
import com.commax.control.Card_list.Card_GasLock;
import com.commax.control.Card_list.Card_LightSwitch;
import com.commax.control.Card_list.Card_MagneticSensor;
import com.commax.control.Card_list.Card_MainSwitch;
import com.commax.control.Card_list.Card_MultiSensor_PIR;
import com.commax.control.Card_list.Card_SmartMeter;
import com.commax.control.Card_list.Card_SmartPlug;
import com.commax.control.Card_list.Card_StandbyPower;
import com.commax.control.Card_list.Card_WaterSensor;
import com.commax.control.Common.AlertDialog;
import com.commax.control.Common.BuyerID;
import com.commax.control.Common.Customdialog_one_button;
import com.commax.control.Common.DeviceInfoSimple;
import com.commax.control.Common.TypeDef;

import java.util.ArrayList;

import static com.commax.control.Common.TypeDef.CardType.eSmartMeter;
import static com.commax.control.Common.TypeDef.FAKE_DATA_ENABLE;

public class MainActivity extends Activity {

    /* Constant define ----------------------------- */
    static final String TAG = "MainActivity";
    private static final int MSG_SEND_ID_FIRST = 5001;
    private static final int MSG_SEND_ID_UPDATE = 5002;
    private static final int MSG_SEND_ID_REFRESH = 5003;
    private static final int MSG_SEND_TAP_ABLE_CHEKC = 50004;

    private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    /* Data Types----------------------------------- */

    /* Instance define ----------------------------- */
    public static MainActivity _instance;
    public Context mContext;
    public DataManager dataManager;  //Data Manager
    public BuyerID mBuyerID;

    /* Private Variables ---------------------------- */
    private HorizontalScrollView scrollview_layout;
    private LinearLayout cardlist_layout;
    ProgressDialog wait_dialog;
    Toast custom_toast;
    View toastLayout;
    TextView toastTextView;
    ImageView btn_home;
    ImageView btn_edit;
    ImageView btn_add;
    ImageView btn_guide;
    LinearLayout.LayoutParams container_layoutparams;
    LinearLayout.LayoutParams containerbar_layoutparams;
    View m_FavoriteGuideView = null;
    TextView m_subtitle1;
    TextView m_subtitle2;
    TextView m_subtitle3;
    TextView m_subtitle4;
    TextView m_subtitle5;

    int mStartTabID = 0;
    int mSelect_TapId = 0;
    int mFavorite_Counter = 0;
    int mbMenu_Touchcounter = 0;
    boolean activity_on = false;
    boolean mbReadData_finish = false;
    boolean mbNeedTo_addReport = false;
    boolean mbKeep_currentMenu = false;
    boolean mbApp_hasFocus = false;
    Handler mMain_handler = null;

    /* Member Variables ----------------------------- */
    ArrayList<DeviceInfo> mCategory_eFavorite;
    ArrayList<DeviceInfo> mCategory_eLight;
    ArrayList<DeviceInfo> mCategory_eIndoorEnv;
    ArrayList<DeviceInfo> mCategory_eEnergy;
    ArrayList<DeviceInfo> mCategory_eSafe;
    ArrayList<DeviceInfoSimple> mDevice_simpleList;

    /* Export Methods -----------------------------*/
    // RemoteServiceConnection 은 AIDL 을 통한 Device 제어에 사용됨
    public static RemoteServiceConnection m_rc;

    public static RemoteServiceConnection getRemoteConnection() {
        return m_rc;
    }

    /* Dialog one Button : device add button --------------*/
    Customdialog_one_button customdialog_one_button;
    int div = 0;

    /* Override Methods -----------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Lifecycle onCreate()...");
        _instance = this;
        mContext = this; //getApplicationContext();

        // TEST: 앱 아이콘 삭제
        if(!TypeDef.APP_ICON_ENABLE)
        {
            /*
            PackageManager pm = getPackageManager();
            pm.setApplicationEnabledSetting("com.commax.control",
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    //PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                    PackageManager.DONT_KILL_APP);
            */
        }

        /* Get Intent Data */
        get_IntentData();

        /* start bindService */
        start_IAdapter();

        /* Select Buyer ID */
        mBuyerID = new BuyerID();

        /* Custom Toast */
        customToast_init();

        /* wait dialog */
        wait_dialog = new ProgressDialog(MainActivity.this);

        /* Message handler init */
        mMain_handler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what)
                {
                    case MSG_SEND_ID_FIRST:
                        data_init(mContext);
                        break;
                    case MSG_SEND_ID_UPDATE: //addreport
                        Log.d(TAG, "handler msg_send_id_update");
                        updateDeviceInfo();
                        break;
                    case MSG_SEND_ID_REFRESH:
                        redrawTabList();
                        break;
                    //해당 tab 에 list가 없으면 해당 tab disable 처리 진행
                    case MSG_SEND_TAP_ABLE_CHEKC:
                        Tab_able_disable();
                    break;
                }
            }
        };

        /* wait Dialog */
        mbApp_hasFocus = true; //forced
        waitDialogEnable(getResources().getString(R.string.message_dbread_string),true);

        /* initialize */
        container_init(mContext);

        //data_init(mContext);
        mMain_handler.sendEmptyMessageDelayed(MSG_SEND_ID_FIRST,100); //after 100ms(waitDialog를 먼저 띄우기 위함)
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "Lifecycle onStart()...");
        activity_on = true;

        /* Get Intent Data */
        get_IntentData();

        /* start bindService */
        start_IAdapter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d(TAG, "Lifecycle onRestart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Lifecycle onResume()...");

        //IP Home IoT hide navigation bar
        hideNavigationBar();

        setNavigationBar(false);

        //Tab 카테고리 disable , able
        try
        {
            mMain_handler.sendEmptyMessage(MSG_SEND_TAP_ABLE_CHEKC);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        //add,remove 이벤트 감지후 DB 업데이트 요망!
        if(mbNeedTo_addReport) {
            Log.d(TAG, "mbNeedTo_addReport = " + mbNeedTo_addReport);
            waitDialogEnable(getResources().getString(R.string.message_dbupdate_string), true);
            mMain_handler.sendEmptyMessageDelayed(MSG_SEND_ID_UPDATE, 100);
        }else{
            if(!mbKeep_currentMenu) { //화면 초기화

                int display_TabID;
                if(mStartTabID == TypeDef.TAB_NONE) {
                    display_TabID = TypeDef.TAB_FAV;
                } else {
                    display_TabID = mStartTabID;
                }

                changeTabTitle(display_TabID);
                mSelect_TapId = display_TabID;
                mMain_handler.sendEmptyMessageDelayed(MSG_SEND_ID_REFRESH, 100);
                //sendDeviceAddCommand(); //for test: 시작시 add 디바이스 체크함

                /*
                changeTabTitle(TypeDef.TAB_FAV);
                mSelect_TapId = TypeDef.TAB_FAV;
                mMain_handler.sendEmptyMessageDelayed(MSG_SEND_ID_REFRESH, 100);
                sendDeviceAddCommand(); //시작시 add 디바이스 체크함
                */
            }
        }
        mbKeep_currentMenu = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //setNavigationBar(true);
        //CardManager.getInst().destoryCardManager(); //todo
        //activity_on = false;
        Log.d(TAG, "Lifecycle onStop()...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // exit program
        setNavigationBar(true);
        CardManager.getInst().destoryCardManager(); //todo

        // unbindService
        if(m_rc != null){
            Log.i(TAG, "onDestroy()... unbindService");
            try {
                unbindService(m_rc);

                m_rc = null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                m_rc = null;
            }
        }

        activity_on = false;
        Log.d(TAG, "Lifecycle onDestroy()...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Lifecycle onActivityResult()...");
//        Selected_TapId();
        mbKeep_currentMenu = true; //ACK가 오면 More창을 호출한 것으로 간주함-> 이전화면을 유지함
        //TODO: Extra 사용시 추가요망!
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mbApp_hasFocus = hasFocus; //Focus 상태
        //Log.d(TAG, "Lifecycle onWindowFocusChanged()..." + mbApp_hasFocus);

        /*
        if (hasFocus) {
            //JB에서는 동작안함!
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        */
    }

    /* Class Methods(private)-----------------------------*/
    private void get_IntentData() {
        //intent에서 TabID 받음
        Intent intent = getIntent();
        mStartTabID = TypeDef.TAB_NONE;
        if(intent != null){
            mStartTabID = intent.getIntExtra("tapid",0);
            Log.d(TAG, "getExtra(tapid)..." + mStartTabID);
        }
    }

    private void start_IAdapter() {
        if (m_rc == null) {
            try {
                m_rc = new RemoteServiceConnection();

                // 2016.12.06,yslee:: SDK 버전 21(롤리팝) 대응- 명시적으로 호출
                //boolean is = bindService(new Intent(IAdapter.class.getName()), m_rc, BIND_AUTO_CREATE);

                Intent pamIntent = new Intent(IAdapter.class.getName());
                pamIntent.setPackage("com.commax.pam.service");
                boolean is = bindService(pamIntent, m_rc ,BIND_AUTO_CREATE);

                if (is == true) Log.i(TAG, "start_IAdapter()... bindService Succeed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void data_init(Context context) {

        /* DeviceInfo */
        mCategory_eFavorite = new ArrayList<DeviceInfo>();
        mCategory_eLight = new ArrayList<DeviceInfo>();
        mCategory_eIndoorEnv = new ArrayList<DeviceInfo>();
        mCategory_eEnergy = new ArrayList<DeviceInfo>();
        mCategory_eSafe = new ArrayList<DeviceInfo>();
        mDevice_simpleList = new ArrayList<DeviceInfoSimple>();

        /* open dataManager */
        dataManager = new DataManager(mContext);
        dataManager.initFavoriteFile();

        /* Card Resource Manager */
        new CardManager(mContext);

        /*
        //for test
        //dataManager.makeJsonObject_Dummy(0); //for test
        //SystemClock.sleep(5 * 1000);
        //for test
        */

        /* open DB */
        OpenDB();
        ReadDB();

        /* Stop Wait */
        waitDialogEnable("", false);

        //Check error
        if (!dataManager.mConnect) {
            //Toast.makeText(getApplicationContext(), "Failed to connect MySQL DB !", Toast.LENGTH_LONG).show();
            //showToastBox("Failed to connect MySQL DB !", true);
            AlertDialog alert = new AlertDialog(this, getResources().getString(R.string.message_check_ds), true);
            alert.show();
        }
        //IP Home IoT 에서는 해당 다이얼로그 필요 없다. 2017-01-06
        /*else if(!dataManager.mValidation) {
            //TODO IPHomeIoT 에서는 false , LLT 에서는 true
            AlertDialog alert = new AlertDialog(this, getResources().getString(R.string.message_device_not_found), false);
            alert.show();
        }*/

        //dynamic Tab menu
        tablist_init();

        //default Tablist
        if(mStartTabID == TypeDef.TAB_NONE)
            draw_TabList(TypeDef.TAB_FAV);
        else
            draw_TabList(mStartTabID);

        /*
        //for test
        sendDeviceResetCommand();
        sendDeviceAddCommand();
        */
    }

    private void container_init(Context context) {

        Log.d(TAG, "container_init()...");

        /* variable init  */
        mSelect_TapId = TypeDef.TAB_NONE;

        /* control Handler */
        scrollview_layout = (HorizontalScrollView) findViewById(R.id.cardscroll_layout);
        cardlist_layout = (LinearLayout) findViewById(R.id.cardlist_layout);
        btn_add = (ImageView) findViewById(R.id.btn_add);
        btn_guide = (ImageView) findViewById(R.id.btn_guide);
        m_subtitle1 = (TextView) findViewById(R.id.sub1_title);
        m_subtitle2 = (TextView) findViewById(R.id.sub2_title);
        m_subtitle3 = (TextView) findViewById(R.id.sub3_title);
        m_subtitle4 = (TextView) findViewById(R.id.sub4_title);
        m_subtitle5 = (TextView) findViewById(R.id.sub5_title);

        LinearLayout getlayout;
        getlayout = (LinearLayout) findViewById(R.id.device_card1);
        container_layoutparams = (LinearLayout.LayoutParams) getlayout.getLayoutParams();
        getlayout = (LinearLayout) findViewById(R.id.line_bar1);
        containerbar_layoutparams = (LinearLayout.LayoutParams) getlayout.getLayoutParams();
        cardlist_layout.removeAllViews(); //2016-11-09,yslee : 카드 리스트 모두 지움

        //Touch Listener
        touchevent_init();

        /* Display option */
        if(!TypeDef.TAB_SAFE_ENABLE) { //Safe Category 숨기기
            m_subtitle5.setVisibility(View.GONE);
        }
        if(!TypeDef.MENU_ADD_ENABLE) {
            btn_add.setVisibility(View.GONE); //add 버튼 숨기기
        }
        if(!TypeDef.GUIDE_BUTTON_ENABLE){
            btn_guide.setVisibility(View.GONE);
        }
    }

    private void tablist_init() {
        //dynamic Tab menu
        if(TypeDef.TAB_DYNAMIC_MODE_ENABLE) {
            if (mCategory_eLight.size() == 0) {
                m_subtitle2.setVisibility(View.GONE);
            } else {
                m_subtitle2.setVisibility(View.VISIBLE);
            }
            if (mCategory_eIndoorEnv.size() == 0) {
                m_subtitle3.setVisibility(View.GONE);
            } else {
                m_subtitle3.setVisibility(View.VISIBLE);
            }
            if (mCategory_eEnergy.size() == 0) {
                m_subtitle4.setVisibility(View.GONE);
            } else {
                m_subtitle4.setVisibility(View.VISIBLE);
            }
            if (TypeDef.TAB_SAFE_ENABLE) {
                if (mCategory_eSafe.size() == 0) {
                    m_subtitle5.setVisibility(View.GONE);
                } else {
                    m_subtitle5.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    //Touch Listener 등록
    private void touchevent_init() {

        btn_home = (ImageView) findViewById(R.id.btn_home);
        btn_home.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.6F);
                if (event.getAction() == MotionEvent.ACTION_UP)   v.setAlpha(1.0F);
                return false;
            }
        });
        btn_edit = (ImageView) findViewById(R.id.btn_edit);
        btn_edit.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.6F);
                if (event.getAction() == MotionEvent.ACTION_UP)   v.setAlpha(1.0F);
                return false;
            }
        });
        btn_add = (ImageView) findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.6F);
                if (event.getAction() == MotionEvent.ACTION_UP)   v.setAlpha(1.0F);
                return false;
            }
        });
        btn_guide = (ImageView) findViewById(R.id.btn_guide);
        btn_guide.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.6F);
                if (event.getAction() == MotionEvent.ACTION_UP)   v.setAlpha(1.0F);
                return false;
            }
        });


    }

    private void OpenDB() {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.
        ConnectionThread OpenDB_thread;

        //Try to DB connection
        OpenDB_thread = new ConnectionThread();
        if (OpenDB_thread != null) {
            try {
                OpenDB_thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Wait for DB connection
            try {
                OpenDB_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
            //check complete
            //while(true)
            {
                //if (dataManager.mConnect) break;
                msDelay(100);
            }
            */
        }
    }

    class ConnectionThread extends Thread {
        public void run() {
            try {
                if (!Thread.currentThread().isInterrupted())
                    Thread.sleep(1);

                Log.d(TAG, "try to open MySQL");
                if (dataManager == null) return;

                for (int i = 0; i < dataManager.MYSQL_TRY_COUNT; i++) {
                    //MySQL 서버에 바로 연결이 되지 않은 경우가 자주 발생함
                    dataManager.mysql_isConnected();
                    dataManager.mysql_checkValidConnection();
                    if (dataManager.mConnect && dataManager.mValidation) {
                        Log.d(TAG, "open MySQL ... Success");
                        break;
                    } else {
                        dataManager.mysql_close();
                        Thread.sleep(100);
                        dataManager.mysql_open();
                        Thread.sleep(100);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ReadDB() {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        ReadDevicesThread ReadDB_thread;
        ReadDB_thread = new ReadDevicesThread();

        //Try to DB connection
        if (ReadDB_thread != null) {
            try {
                ReadDB_thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Wait for DB connection
            try {
                ReadDB_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
            //check complete
            while(true)
            {
                if (mbReadData_finish) break;
                msDelay(100);
            }
            */
        }
    }

    class ReadDevicesThread extends Thread {
        public void run() {
            try {
                if (!Thread.currentThread().isInterrupted())
                    Thread.sleep(1);

                Log.d(TAG, "try to read MySQL");

                if (dataManager == null) return;
                for (int i = 0; i < dataManager.MYSQL_TRY_COUNT; i++) {
                    //MySQL 서버에 연결될때까지 대기함
                    //Log.d(TAG, "ReadDevicesThread..Ready wait " + i);
                    //if (!dataManager.mysql_isConnected() || !dataManager.mysql_checkValidConnection()) {
                    dataManager.mysql_isConnected();
                    dataManager.mysql_checkValidConnection();
                    if (!dataManager.mConnect && !dataManager.mValidation) {
                        Thread.sleep(100);
                        continue; //retry
                    }
                    break;
                }

                //read all devices
                readDevicesAll();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void msDelay(int nDelay) {
        long nStart = System.currentTimeMillis();
        while(true) {
            if( (System.currentTimeMillis() - nStart) >  nDelay )
                break;
        }
    }


    private void readDevicesAll() {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        // Note: Warning - Test Only!!!
        if(FAKE_DATA_ENABLE) {
            dataManager.mConnect = true;
            dataManager.mValidation = true;
        }
        // Note: Warning - Test Only!!!

        if (!dataManager.mConnect) {
            Log.d(TAG, "*** Failed to connect MySQL DB !!!");
            return;
        }

        //Reset Data List
        try {
            mCategory_eFavorite.clear();
            mCategory_eLight.clear();
            mCategory_eIndoorEnv.clear();
            mCategory_eEnergy.clear();
            mCategory_eSafe.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Scan All Device by Category
        try {
            for (int i = 0; i <= TypeDef.MAX_CATEGORY; i++) {
                readDevices(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        mbReadData_finish = true;

        Log.d(TAG, mCategory_eLight.size() + "," + mCategory_eIndoorEnv.size() + "," + mCategory_eEnergy.size() + "," + mCategory_eSafe.size());

        try
        {
            //리스트 사이즈 없는 탭은 disable 처리
            mMain_handler.sendEmptyMessage(MSG_SEND_TAP_ABLE_CHEKC);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void readDevices(int category_number) {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        // Note: Warning - Test Only!!!
        if (FAKE_DATA_ENABLE) {
            Log.d(TAG, "*** Warning : This is FAKE Mode !!! ***");
            readDevices_FAKE(category_number);
            return;
        }
        // Note: Warning - Test Only!!!

        if (!dataManager.mConnect) {
            Log.d(TAG, "*** Failed to connect MySQL DB !!!");
            return;
        }

        try {
            ArrayList<DeviceInfo> readDeviceList;
            TypeDef.CategoryType categorytype;
            DeviceInfo virtual_device;
            int detectcounter = 0;

            switch (category_number) {

                case TypeDef.TAB_FAV : // eFavorite
                    categorytype = TypeDef.CategoryType.eFavorite;
                    // TODO: 2016-08-02
                    break;

                case TypeDef.TAB_LIGHR : //eLight Scan
                    categorytype = TypeDef.CategoryType.eLight;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_MAINLIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DIMMER, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    detectcounter += readDeviceList.size();
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    //for test(to be del) : // TODO:
                    if (false) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eLightSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.nickName = "Light 01";
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                        detectcounter++;

                        device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCHDIMMER);
                        device_data.nCardType = TypeDef.CardType.eDimmerSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_DIMMER;
                        device_data.nickName = "Dimmer 01";
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                        detectcounter++;

                        device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCHDIMMER);
                        device_data.nCardType = TypeDef.CardType.eDimmerSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_DIMMER;
                        device_data.nickName = "Dimmer 02";
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                        detectcounter++;
                    }

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_LIGHT_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupLight_titlebar_string); //전체조명
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                        mCategory_eLight.add(0, virtual_device);
                    }
                    break;

                case TypeDef.TAB_INDOOR : //eIndoorEnv Scan
                    categorytype = TypeDef.CategoryType.eIndoorEnv;

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_THERMOSTAT, TypeDef.COMMAX_DEVICE_FCU, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_FCU_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupFCU_titlebar_string); //전체냉난방
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_FCU, categorytype);
                        mCategory_eIndoorEnv.add(mCategory_eIndoorEnv.size() - detectcounter, virtual_device);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_THERMOSTAT, TypeDef.COMMAX_DEVICE_BOILER, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_BOILER_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupBoiler_titlebar_string); //전체보일러
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_BOILER, categorytype);
                        mCategory_eIndoorEnv.add(mCategory_eIndoorEnv.size() - detectcounter, virtual_device);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_THERMOSTAT, TypeDef.COMMAX_DEVICE_AIRCON, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_AIRCON_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupAircon_titlebar_string); //전체에어컨
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_AIRCON, categorytype);
                        mCategory_eIndoorEnv.add(mCategory_eIndoorEnv.size() - detectcounter, virtual_device);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_FAN, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_FAN_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupFan_titlebar_string); //전체환기
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_FAN, categorytype);
                        mCategory_eIndoorEnv.add(mCategory_eIndoorEnv.size() - detectcounter, virtual_device);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_CURTAIN, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_CURTAIN_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupCurtain_titlebar_string); //전체커튼
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_CURTAIN, categorytype);
                        mCategory_eIndoorEnv.add(mCategory_eIndoorEnv.size() - detectcounter, virtual_device);
                    }

                    //MultiSensor
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_DETECTSENSOR, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();

                    //FloodSensor
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.ROOT_DEVICE_WATERSENSOR, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();

                    break;

                case TypeDef.TAB_ENERGY : //eEnergy Scan
                    categorytype = TypeDef.CategoryType.eEnergy;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_PLUG, categorytype);
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //for test(to be del) : // TODO:
                    if (false) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eSmartPlug;
                        device_data.nickName = "SmartPlug 01";
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_PLUG;
                        device_data.value = "on";
                        mCategory_eEnergy.add(device_data);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_STANDBYPOWER, categorytype);
                    detectcounter = readDeviceList.size();
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Group : 추후 설정에 의해 추가되도록 하자!
                    if (TypeDef.GROUP_STANDBYPOWER_ENABLE && (detectcounter > 1)) {
                        String nickName = getResources().getString(R.string.card_groupStandbyPwr_titlebar_string); //전체대기전력
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_STANDBYPOWER, categorytype);
                        mCategory_eEnergy.add(mCategory_eEnergy.size() - detectcounter, virtual_device);
                    }

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_METER, TypeDef.COMMAX_DEVICE_METER, categorytype);
                    if (readDeviceList.size() > 0) { //가상 스마트미터링 그룹디바이스 추가함
                        String nickName = getResources().getString(R.string.card_smartmeter_titlebar_string); //스마트미터링
                        virtual_device = dataManager.mysql_getVirtualDeviceInfo(nickName, TypeDef.COMMAX_DEVICE_METER, categorytype);
                        mCategory_eEnergy.add(virtual_device);
                    }
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();

                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    break;

                case TypeDef.TAB_SAFE : //eSafe Scan
                    if(!TypeDef.TAB_SAFE_ENABLE) break; //optional : 디바이스 검색안함

                    categorytype = TypeDef.CategoryType.eSafe;

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_LOCK, TypeDef.COMMAX_DEVICE_GASLOCK, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_LOCK, TypeDef.COMMAX_DEVICE_DOORLOCK, categorytype); //todo : check 필요
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //가스 감지기
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_GASSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_FIRESENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_CONTACTSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    //Magnetic Sensor
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_DOORSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();


                    //for test(to be del) : // TODO:
                    if (false) { //임시(가상)
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eDoorLock;
                        device_data.nickName = "Door Lock 01";
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_LOCK;
                        device_data.value = "lock";
                        mCategory_eSafe.add(device_data);
                    }

                    /* 외출 스위치는 제어에서 제외함
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_AWAYSWITCH, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    */
                    break;
                default:
                    categorytype = TypeDef.CategoryType.eNone;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDevices_FAKE(int category_number) {
        // Note: Warning - Test Only!!!
        if (FAKE_DATA_ENABLE == false) {
            return;
        }
        Log.d(TAG, "*** Warning : This is FAKE Mode !!! ***");
        // Note: Warning - Test Only!!!

        try {
            //ArrayList<DeviceInfo> readDeviceList;
            TypeDef.CategoryType categorytype;
            //DeviceInfo virtual_device;
            //int detectcounter = 0;

            switch (category_number) {
                case TypeDef.TAB_LIGHR : //eLight Scan
                    if (TypeDef.FAKE_DEVICE.eLightSwitch.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eLightSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.nickName = "Light 01";
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eMainSwitch.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eLightSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.nickName = "Main 01";
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eDimmerSwitch.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCHDIMMER);
                        device_data.nCardType = TypeDef.CardType.eDimmerSwitch;
                        device_data.nCategory = TypeDef.CategoryType.eLight;
                        device_data.groupID = TypeDef.GroupID.eMainSwitch.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_DIMMER;
                        device_data.nickName = "Dimmer 01";
                        device_data.value = "on";
                        mCategory_eLight.add(device_data);
                    }
                    break;

                case TypeDef.TAB_INDOOR : //eIndoorEnv Scan
                    if (TypeDef.FAKE_DEVICE.eAircon.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eAircon;
                        device_data.nCategory = TypeDef.CategoryType.eIndoorEnv;
                        device_data.groupID = TypeDef.GroupID.eAircon.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_THERMOSTAT;
                        device_data.nickName = "Aircon 01";
                        device_data.value = "18";
                        mCategory_eIndoorEnv.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eBoiler.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eBoiler;
                        device_data.nCategory = TypeDef.CategoryType.eIndoorEnv;
                        device_data.groupID = TypeDef.GroupID.eBoiler.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_THERMOSTAT;
                        device_data.nickName = "Boiler 01";
                        device_data.value = "18";
                        mCategory_eIndoorEnv.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eFCU.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eFCU;
                        device_data.nCategory = TypeDef.CategoryType.eIndoorEnv;
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_THERMOSTAT;
                        device_data.groupID = TypeDef.GroupID.eFCU.value();
                        device_data.nickName = "FCU 01";
                        device_data.value = "18";
                        mCategory_eIndoorEnv.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eFan.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eFan;
                        device_data.nCategory = TypeDef.CategoryType.eIndoorEnv;
                        device_data.groupID = TypeDef.GroupID.eFan.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.nickName = "Fan 01";
                        device_data.value = "on";
                        mCategory_eIndoorEnv.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eCurtain.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eCurtain;
                        device_data.nCategory = TypeDef.CategoryType.eIndoorEnv;
                        device_data.groupID = TypeDef.GroupID.eCurtain.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.nickName = "Curtain 01";
                        device_data.value = "open";
                        mCategory_eIndoorEnv.add(device_data);
                    }
                    break;

                case TypeDef.TAB_ENERGY : //eEnergy Scan
                    if (TypeDef.FAKE_DEVICE.eSmartPlug.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eSmartPlug;
                        device_data.nCategory = TypeDef.CategoryType.eEnergy;
                        device_data.groupID = TypeDef.GroupID.eSmartPlug.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_PLUG;
                        device_data.nickName = "SmartPlug 01";
                        device_data.value = "on";
                        mCategory_eEnergy.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eStandbyPower.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_SWITCH);
                        device_data.nCardType = TypeDef.CardType.eStandbyPower;
                        device_data.nCategory = TypeDef.CategoryType.eEnergy;
                        device_data.groupID = TypeDef.GroupID.eStandbyPower.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_SWITCH;
                        device_data.nickName = "StandbyPower 01";
                        device_data.value = "on";
                        mCategory_eEnergy.add(device_data);
                    }
                    break;

                case TypeDef.TAB_SAFE : //eSafe Scan
                    if(!TypeDef.TAB_SAFE_ENABLE) break; //optional : 디바이스 검색안함

                    if (TypeDef.FAKE_DEVICE.eGasLock.value()) {
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_GASLOCK);
                        device_data.nCardType = TypeDef.CardType.eGasLock;
                        device_data.nCategory = TypeDef.CategoryType.eSafe;
                        device_data.groupID = TypeDef.GroupID.eSafe.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_LOCK;
                        device_data.nickName = "Gas Lock 01";
                        device_data.value = "open";
                        mCategory_eSafe.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eDoorLock.value()) { //임시(가상)
                        DeviceInfo device_data = new DeviceInfo("1234", TypeDef.SUB_DEVICE_DOORLOCK);
                        device_data.nCardType = TypeDef.CardType.eDoorLock;
                        device_data.nCategory = TypeDef.CategoryType.eSafe;
                        device_data.groupID = TypeDef.GroupID.eSafe.value();
                        device_data.rootDevice = TypeDef.ROOT_DEVICE_LOCK;
                        device_data.nickName = "Door Lock 01";
                        device_data.value = "open";
                        mCategory_eSafe.add(device_data);
                    }
                    if (TypeDef.FAKE_DEVICE.eDetectSensor.value()) {
                        //todo
                    }

                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawCardList(ArrayList<DeviceInfo> deviceList, boolean bFavorite) {
        int listsize;
        DeviceInfo device_data;

        if (deviceList != null) {

            listsize = deviceList.size();

            //Log.d(TAG, "--------------------------------------------");
            Log.d(TAG, "drawCardList size : " + listsize);

            for (int i = 0; i < listsize; i++) {
                device_data = deviceList.get(i);

                //Favorite list만 표시함
                if (bFavorite) {
                    if (device_data.bFavorite == false) continue;
                    else mFavorite_Counter++;
                }

                //SmartMeter는 가상그룹 디바이스로 표시함
                if(device_data.nCardType == eSmartMeter) {
                    if (!device_data.bVirtualDevice) continue;
                }

                //Create Target Card container
                LinearLayout container; //Card
                container = new LinearLayout(this);
                container.setLayoutParams(container_layoutparams);
                //container.setBackgroundResource(R.drawable.rectangle_outline);
                cardlist_layout.addView(container);

                LinearLayout linebar; //boundary
                linebar = new LinearLayout(this);
                linebar.setLayoutParams(containerbar_layoutparams);
                linebar.setBackgroundColor(0xffb5b6c4);
                cardlist_layout.addView(linebar);

                //Add Card List -> Layout Inflation
                //Log.d(TAG, " -nickName : " + device_data.nickName);
                switch (device_data.nCardType) {
                    case eNone:
                        break;

                    //eLight
                    case eMainSwitch: //eMainSwitch는 GroupSwitch로 전용함
                        //Card_MainSwitch card_mainswitch = new Card_MainSwitch(mContext, device_data);
                        //container.addView(card_mainswitch);
                        Card_MainSwitch card_mainswitch = (Card_MainSwitch)CardManager.getInst().getDeviceCard(device_data);
                        if(card_mainswitch == null) {
                            card_mainswitch = new Card_MainSwitch(mContext, device_data);
                            container.addView(card_mainswitch);
                            CardManager.getInst().addDeviceCard(card_mainswitch, device_data);
                        } else {
                            container.addView(card_mainswitch.getRootView());
                        }
                        break;
                    case eLightSwitch: //eMainSwitch와 eLightSwitch와 합침
                        //Card_LightSwitch card_lightswitch = new Card_LightSwitch(mContext, device_data);
                        //container.addView(card_lightswitch);
                        Card_LightSwitch card_lightswitch = (Card_LightSwitch)CardManager.getInst().getDeviceCard(device_data);
                        if(card_lightswitch == null) {
                            card_lightswitch = new Card_LightSwitch(mContext, device_data);
                            container.addView(card_lightswitch);
                            CardManager.getInst().addDeviceCard(card_lightswitch, device_data);
                        } else {
                            container.addView(card_lightswitch.getRootView());
                        }
                         break;
                    case eDimmerSwitch:
                        //Card_DimmerSwitch card_dimmerswitch = new Card_DimmerSwitch(mContext, device_data);
                        //container.addView(card_dimmerswitch);
                        Card_DimmerSwitch card_dimmerswitch = (Card_DimmerSwitch)CardManager.getInst().getDeviceCard(device_data);
                        if(card_dimmerswitch == null) {
                            card_dimmerswitch = new Card_DimmerSwitch(mContext, device_data);
                            container.addView(card_dimmerswitch);
                            CardManager.getInst().addDeviceCard(card_dimmerswitch, device_data);
                        } else {
                            container.addView(card_dimmerswitch.getRootView());
                        }
                        break;

                    //eIndoorEnv
                    case eThermostat:  //Group Device only
                        // TODO: 2016-09-02
                        break;
                    case eBoiler:
                        //Card_Boiler card_boiler = new Card_Boiler(mContext, device_data);
                        //container.addView(card_boiler);
                        Card_Boiler card_boiler = (Card_Boiler)CardManager.getInst().getDeviceCard(device_data);
                        if(card_boiler == null) {
                            card_boiler = new Card_Boiler(mContext, device_data);
                            container.addView(card_boiler);
                            CardManager.getInst().addDeviceCard(card_boiler, device_data);
                        } else {
                            container.addView(card_boiler.getRootView());
                        }
                        break;
                    case eAircon:
                        //Card_Aircon card_aircon = new Card_Aircon(mContext, device_data);
                        //container.addView(card_aircon);
                        Card_Aircon card_aircon = (Card_Aircon)CardManager.getInst().getDeviceCard(device_data);
                        if(card_aircon == null) {
                            card_aircon = new Card_Aircon(mContext, device_data);
                            container.addView(card_aircon);
                            CardManager.getInst().addDeviceCard(card_aircon, device_data);
                        } else {
                            container.addView(card_aircon.getRootView());
                        }
                        break;
                    case eFan:
                        //Card_Fan card_fan = new Card_Fan(mContext, device_data);
                        //container.addView(card_fan);
                        Card_Fan card_fan = (Card_Fan)CardManager.getInst().getDeviceCard(device_data);
                        if(card_fan == null) {
                            card_fan = new Card_Fan(mContext, device_data);
                            container.addView(card_fan);
                            CardManager.getInst().addDeviceCard(card_fan, device_data);
                        } else {
                            container.addView(card_fan.getRootView());
                        }
                        break;
                    case eFCU:
                        //Card_FCU card_fcu = new Card_FCU(mContext, device_data);
                        //container.addView(card_fcu);
                        Card_FCU card_fcu = (Card_FCU)CardManager.getInst().getDeviceCard(device_data);
                        if(card_fcu == null) {
                            card_fcu = new Card_FCU(mContext, device_data);
                            container.addView(card_fcu);
                            CardManager.getInst().addDeviceCard(card_fcu, device_data);
                        } else {
                            container.addView(card_fcu.getRootView());
                        }
                         break;
                    case eCurtain:
                        //Card_Curtain card_curtain = new Card_Curtain(mContext, device_data);
                        //container.addView(card_curtain);
                        Card_Curtain card_curtain = (Card_Curtain)CardManager.getInst().getDeviceCard(device_data);
                        if(card_curtain == null) {
                            card_curtain = new Card_Curtain(mContext, device_data);
                            container.addView(card_curtain);
                            CardManager.getInst().addDeviceCard(card_curtain, device_data);
                        } else {
                            container.addView(card_curtain.getRootView());
                        }
                        break;

                    case eDetectSensor:
                        //Card_DetectSensor card_detectsensor = new Card_DetectSensor(mContext, device_data);
                        //container.addView(card_detectsensor);
                        Card_MultiSensor_PIR card_detectsensor = (Card_MultiSensor_PIR)CardManager.getInst().getDeviceCard(device_data);
                        if(card_detectsensor == null) {
                            card_detectsensor = new Card_MultiSensor_PIR(mContext, device_data);
                            container.addView(card_detectsensor);
                            CardManager.getInst().addDeviceCard(card_detectsensor, device_data);
                        } else {
                            container.addView(card_detectsensor.getRootView());
                        }
                        break;

                    case eWaterSensor:
                        Card_WaterSensor card_waterSensor = (Card_WaterSensor)CardManager.getInst().getDeviceCard(device_data);
                        if(card_waterSensor == null) {
                            card_waterSensor = new Card_WaterSensor(mContext, device_data);
                            container.addView(card_waterSensor);
                            CardManager.getInst().addDeviceCard(card_waterSensor, device_data);
                        } else {
                            container.addView(card_waterSensor.getRootView());
                        }
                        break;


                    //eEnergy
                    case eSmartPlug:
                        //Card_SmartPlug card_smartplug = new Card_SmartPlug(mContext, device_data);
                        //container.addView(card_smartplug);
                        Card_SmartPlug card_smartplug = (Card_SmartPlug)CardManager.getInst().getDeviceCard(device_data);
                        if(card_smartplug == null) {
                            card_smartplug = new Card_SmartPlug(mContext, device_data);
                            container.addView(card_smartplug);
                            CardManager.getInst().addDeviceCard(card_smartplug, device_data);
                        } else {
                            container.addView(card_smartplug.getRootView());
                        }
                        break;
                    case eStandbyPower:
                        //Card_StandbyPower card_standbypower = new Card_StandbyPower(mContext, device_data);
                        //container.addView(card_standbypower);
                        Card_StandbyPower card_standbypower = (Card_StandbyPower)CardManager.getInst().getDeviceCard(device_data);
                        if(card_standbypower == null) {
                            card_standbypower = new Card_StandbyPower(mContext, device_data);
                            container.addView(card_standbypower);
                            CardManager.getInst().addDeviceCard(card_standbypower, device_data);
                        } else {
                            //Log.d(TAG, "*** card_standbypower.getRootView() " + card_standbypower.getRootView() + " " + card_standbypower.getRootView().getWidth());
                            container.addView(card_standbypower.getRootView());
                        }
                        break;
                    case eSmartMeter:
                        //SmartMeter는 가상그룹 디바이스로 표시함
                        if(device_data.bVirtualDevice) {
                            //Card_SmartMeter card_smartmeter = new Card_SmartMeter(mContext, device_data);
                            //container.addView(card_smartmeter);
                            Card_SmartMeter card_smartmeter = (Card_SmartMeter)CardManager.getInst().getDeviceCard(device_data);
                            if(card_smartmeter == null) {
                                card_smartmeter = new Card_SmartMeter(mContext, device_data);
                                container.addView(card_smartmeter);
                                CardManager.getInst().addDeviceCard(card_smartmeter, device_data);
                            } else {
                                container.addView(card_smartmeter.getRootView());
                            }
                        }
                        break;
                    case eElevator: //임시(가상)
                        // TODO: 2016-09-02
                        break;

                    //eSafe
                    case eGasLock:
                        //Card_GasLock card_gaslock = new Card_GasLock(mContext, device_data);
                        //container.addView(card_gaslock);
                        Card_GasLock card_gaslock = (Card_GasLock)CardManager.getInst().getDeviceCard(device_data);
                        if(card_gaslock == null) {
                            card_gaslock = new Card_GasLock(mContext, device_data);
                            container.addView(card_gaslock);
                            CardManager.getInst().addDeviceCard(card_gaslock, device_data);
                        } else {
                            container.addView(card_gaslock.getRootView());
                        }
                        break;
                    case eGasSensor:
                        Card_GasDetectSensor card_gasDetectSensor = (Card_GasDetectSensor)CardManager.getInst().getDeviceCard(device_data);
                        if(card_gasDetectSensor == null) {
                            card_gasDetectSensor = new Card_GasDetectSensor(mContext, device_data);
                            container.addView(card_gasDetectSensor);
                            CardManager.getInst().addDeviceCard(card_gasDetectSensor, device_data);
                        } else {
                            container.addView(card_gasDetectSensor.getRootView());
                        }
                        break;
                    case eMotionSensor:
                        // TODO: 2016-09-02
                        break;
                    case eFireSensor:
                        // TODO: 2016-09-02
                        break;
                    case eDoorLock: //임시(가상)
                        //Card_DoorLock card_doorlock = new Card_DoorLock(mContext, device_data);
                        //container.addView(card_doorlock);
                        Card_DoorLock card_doorlock = (Card_DoorLock)CardManager.getInst().getDeviceCard(device_data);
                        if(card_doorlock == null) {
                            card_doorlock = new Card_DoorLock(mContext, device_data);
                            container.addView(card_doorlock);
                            CardManager.getInst().addDeviceCard(card_doorlock, device_data);
                        } else {
                            container.addView(card_doorlock.getRootView());
                        }
                        break;
                    case eDoorSensor:
                        //magnetic sensor
                        Card_MagneticSensor card_magneticSensor = (Card_MagneticSensor)CardManager.getInst().getDeviceCard(device_data);
                        //TODO 여기서 Async 나는 것 같다.
                        //card_magneticSensor = null;
                        if(card_magneticSensor == null) {
                            card_magneticSensor = new Card_MagneticSensor(mContext, device_data);
                            container.addView(card_magneticSensor);
                            CardManager.getInst().addDeviceCard(card_magneticSensor, device_data);
                        } else {
                            container.addView(card_magneticSensor.getRootView());
                        }
                        break;

                    case eSmokeSensor:
                        // TODO: 2016-09-02
                        break;
                    case eAwaySensor:
                        // TODO: 2016-09-02
                        break;
                }
            }
        } else {

            Log.d(TAG, "*** drawCardList() : deviceList is null");
        }
    }

    //removecardview
    private void removeCardList(int tap_id) {

        try {
            if (tap_id != mSelect_TapId) {

                //Log.d(TAG, "removeCardList " + tap_id);
                cardlist_layout.removeAllViews(); //카드 모두 지워짐
                scrollview_layout.setScrollX(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawFavoriteMsgBox() {
        //Show Guide Message
        if(m_FavoriteGuideView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            m_FavoriteGuideView = inflater.inflate(R.layout.favorite_msgbox, null);
        }
        //cardlist_layout.setOrientation(LinearLayout.VERTICAL);
        //cardlist_layout.setGravity(Gravity.CENTER);
        //cardlist_layout.setWeightSum(1.0f);
        cardlist_layout.addView(m_FavoriteGuideView);
    }

    //change TabTitle
    private void changeTabTitle(int tap_id) {
        TextView m_subtitle;
        int newtab = tap_id;
        int oldtab = mSelect_TapId;

        //Normal Tab
        m_subtitle = (TextView) findViewById(R.id.sub1_title); //default
        switch (oldtab) {
            case TypeDef.TAB_FAV :
                m_subtitle = (TextView) findViewById(R.id.sub1_title);
                break;
            case TypeDef.TAB_LIGHR :
                m_subtitle = (TextView) findViewById(R.id.sub2_title);
                break;
            case TypeDef.TAB_INDOOR :
                m_subtitle = (TextView) findViewById(R.id.sub3_title);
                break;
            case TypeDef.TAB_ENERGY :
                m_subtitle = (TextView) findViewById(R.id.sub4_title);
                break;
            case TypeDef.TAB_SAFE :
                m_subtitle = (TextView) findViewById(R.id.sub5_title);
                break;
            default:
                break;
        }
        m_subtitle.setSelected(false);

        //Selected Tab
        switch (newtab) {
            case TypeDef.TAB_FAV :
                m_subtitle = (TextView) findViewById(R.id.sub1_title);
                break;
            case TypeDef.TAB_LIGHR :
                m_subtitle = (TextView) findViewById(R.id.sub2_title);
                break;
            case TypeDef.TAB_INDOOR :
                m_subtitle = (TextView) findViewById(R.id.sub3_title);
                break;
            case TypeDef.TAB_ENERGY :
                m_subtitle = (TextView) findViewById(R.id.sub4_title);
                break;
            case TypeDef.TAB_SAFE :
                m_subtitle = (TextView) findViewById(R.id.sub5_title);
                break;
            default:
                break;
        }
        m_subtitle.setSelected(true);
    }

    private void drawFavoriteList() {
        mFavorite_Counter = 0;
        drawCardList(mCategory_eLight, true);
        drawCardList(mCategory_eIndoorEnv, true);
        drawCardList(mCategory_eEnergy, true);
        drawCardList(mCategory_eSafe, true);

        Log.d(TAG, "drawFavoriteList: " + mFavorite_Counter);

        //todo: FavoriteList empty 일때 메세지 박스 출력
        if(mFavorite_Counter == 0) {
            drawFavoriteMsgBox();
        }
    }

    private void draw_TabList(int tap_id) {
        //Log.d(TAG, "draw_TabList: " + mSelect_TapId + "->" + tap_id);

        mbMenu_Touchcounter = 0; //for test
        if (tap_id != mSelect_TapId) {

            removeCardList(tap_id);
            switch (tap_id) {
                case TypeDef.TAB_FAV : //eFavorite
                    //drawCardList(mCategory_eFavorite, true);
                    drawFavoriteList();
                    break;
                case TypeDef.TAB_LIGHR : //eLight
                    drawCardList(mCategory_eLight, false);
                    break;
                case TypeDef.TAB_INDOOR : //eIndoorEnv
                    drawCardList(mCategory_eIndoorEnv, false);
                    break;
                case TypeDef.TAB_ENERGY : //eEnergy
                    drawCardList(mCategory_eEnergy, false);
                    break;
                case TypeDef.TAB_SAFE : //eSafe
                    drawCardList(mCategory_eSafe, false);
                    break;
                default:
                    break;
            }

            changeTabTitle(tap_id);
            mSelect_TapId = tap_id;
        }
    }

    public void customToast_init() {
        try {
            /*
            //Old Toast
            custom_toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
            ViewGroup toastLayout = (ViewGroup) custom_toast.getView();
            TextView toastTV = (TextView) toastLayout.getChildAt(0);
            toastTV.setTextSize((int) getResources().getDimension(R.dimen.toast_text_size));
            */

            //New Toast
            LayoutInflater toastInflater;
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.text_toastmessage);
            custom_toast = new Toast(getApplicationContext());
            custom_toast.setDuration(Toast.LENGTH_SHORT);
            custom_toast.setView(toastLayout);
            custom_toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToastBox(String message, boolean bForce) {

        //Log.d(TAG, "showToastBox() ..." + mbApp_hasFocus + "-" + bForce);
        //Log.d(TAG, "showToastBox(): " + message);
        if(mbApp_hasFocus || bForce)
        {
            //Old Toast
            //custom_toast.setText(message);
            //custom_toast.show();

            //New Toast
            toastTextView.setText(message);
            custom_toast.show();
        }
    }

    public void waitDialogEnable(String message, boolean bEnable)
    {
        try {
            if (bEnable) {
                showToastBox(message, false);
            }
            /*
            // ProgressBar 미동작 문제로 보류함
            if (bEnable) {
                wait_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                wait_dialog.setMessage(message);
                wait_dialog.show();
                TextView msgView = (TextView) wait_dialog.findViewById(android.R.id.message);
                msgView.setTextSize((int) getResources().getDimension(R.dimen.toast_text_size));
            } else {
                wait_dialog.dismiss();
            }
            */
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //for test
    public void printDeviceInfo(DeviceInfo device_data) {

        if (device_data != null) {
            Log.i(TAG, "--------------------------------------------");
            Log.i(TAG, " -nickName : " + device_data.nickName);
            Log.i(TAG, " -rootUuid : " + device_data.rootUuid);
            Log.i(TAG, " -rootDevice : " + device_data.rootDevice);
            Log.i(TAG, " -CommaxDevice : " + device_data.CommaxDevice);
            Log.i(TAG, " -subUuid : " + device_data.subUuid);
            Log.i(TAG, " -sort : " + device_data.sort);
            Log.i(TAG, " -value : " + device_data.value);
            Log.i(TAG, " -precision... : " + device_data.precision);
            Log.i(TAG, " -scale... : " + device_data.scale);
            Log.i(TAG, " -option1... : " + device_data.option1);
            Log.i(TAG, " -option2... : " + device_data.option2);
            Log.i(TAG, " -batteryLevel... : " + device_data.batteryLevel);
            Log.i(TAG, " -batteryEvent... : " + device_data.batteryEvent);
            Log.i(TAG, " -Category... : " + device_data.nCategory);
            Log.i(TAG, " -CardType... : " + device_data.nCardType);
            Log.i(TAG, " -TabID... : " + device_data.nTabID);
            Log.i(TAG, " -LayoutID... : " + device_data.nLayoutID);
            Log.i(TAG, " -groupID... : " + device_data.groupID);
            Log.i(TAG, " -Favorite... : " + device_data.bFavorite);
            Log.i(TAG, " -Validate... : " + device_data.bValidate);
            Log.i(TAG, " -Updated... : " + device_data.bUpdated);
            Log.i(TAG, " -VirtualDevice... : " + device_data.bVirtualDevice);
            Log.i(TAG, " -other_subcount : " + device_data.other_subcount);
            if (device_data.other_subcount > 0) {
                for (int k = 0; k < device_data.other_subcount; k++) {
                    Log.i(TAG, "  -other index : " + k);
                    Log.i(TAG, "  -subUuid : " + device_data.other_subUuid[k]);
                    Log.i(TAG, "  -sort : " + device_data.other_sort[k]);
                    Log.i(TAG, "  -value : " + device_data.other_value[k]);
                    Log.i(TAG, "  -precision : " + device_data.other_precision[k]);
                    Log.i(TAG, "  -scale : " + device_data.other_scale[k]);
                }
            }
            Log.i(TAG, "--------------------------------------------");
        }
    }

    //for test
    public void printDeviceList(ArrayList<DeviceInfo> deviceList) {
        DeviceInfo device_data;
        if (deviceList != null) {
            Log.i(TAG, "--------------------------------------------");
            Log.i(TAG, "Total Device list : " + deviceList.size());

            for (int i = 0; i < deviceList.size(); i++) {
                device_data = deviceList.get(i);
                Log.i(TAG, "index : " + i);
                printDeviceInfo(device_data);
            }
            Log.i(TAG, "--------------------------------------------");
        } else {
            Log.i(TAG, "***printDeviceList() : deviceList is null");
        }
    }

    public void redrawTabList()
    {
        //dynamic Tab menu
        tablist_init();

        //Redraw selected Tablist
        int tap_id = mSelect_TapId;
        mSelect_TapId = TypeDef.TAB_NONE;
        draw_TabList(tap_id);
    }

    public void updateDeviceInfo()
    {
        Log.d(TAG, "updateDevice Info() ");
        Log.d(TAG, "updateDeviceInfo : " + mbNeedTo_addReport);

        //Reset All Card
        CardManager.getInst().destoryCardManager(); //2016-11-01 yslee:: hangup issue

         //Rescan DB
         OpenDB();
         ReadDB();

         //Stop Wait
         waitDialogEnable("", false);

         //Redraw selected Tablist
         redrawTabList();
        try{
            //add dialog dismiss
            DialogActivity.getInstance().finish();
            showToastBox(getString(R.string.device_added),true);
            DialogActivity.getInstance().circle_progress_timer_flag = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

         //mbUpdate_Started = false;
         mbNeedTo_addReport = false;
    }

    public void getDeviceInfoSimple()
    {
        mDevice_simpleList.clear();
        addDeviceInfoSimple(mCategory_eLight, TypeDef.TAB_LIGHR);
        addDeviceInfoSimple(mCategory_eIndoorEnv, TypeDef.TAB_INDOOR);
        addDeviceInfoSimple(mCategory_eEnergy, TypeDef.TAB_ENERGY);
        addDeviceInfoSimple(mCategory_eSafe, TypeDef.TAB_SAFE);
        Log.d(TAG, "getDeviceInfoSimple : " + mDevice_simpleList.size());

    }

    public void addDeviceInfoSimple(ArrayList<DeviceInfo> deviceList, int nCategory)
    {
        DeviceInfo device_data;
        //DeviceInfoSimple simple_data;

        if (deviceList != null) {
            for (int i = 0; i < deviceList.size(); i++) {
                device_data = deviceList.get(i);
                if(!device_data.bVirtualDevice) {
                    //skip device
                    if ( device_data.rootDevice.equalsIgnoreCase(TypeDef.ROOT_DEVICE_METER) ) continue;

                    //add device
                    DeviceInfoSimple simple_data = new DeviceInfoSimple();
                    //simple_data.setDeviceInfo(device_data.rootUuid, device_data.nickName, Integer.toString(nCategory));
                    simple_data.rootUuid = device_data.rootUuid;
                    simple_data.nickName = device_data.nickName;
                    simple_data.nCategory = Integer.toString(nCategory);
                    mDevice_simpleList.add(simple_data);
                }
            }
        }
        //Log.d(TAG, "added Device list : " + mDevice_simpleList.size());
    }

    /* Class Methods(public) ----------------------------------------*/
    public static MainActivity getInstance() {
        return _instance;
    }

    public int getcurrentTabID() { return mSelect_TapId; }

    public class RemoteServiceConnection implements ServiceConnection {

        IAdapter iadpter;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            try {
                iadpter = IAdapter.Stub.asInterface(service);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            iadpter = null;
        }
    }

    public void sendCommand(String raw) {

        if (m_rc.iadpter != null) {
            try {
                int result = m_rc.iadpter.sendToPAM(raw); //AIDL
                Log.d(TAG, "result : " + result);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void sendDeviceResetCommand() {
        try{
            sendCommand("{\"command\":\"factoryReset\"} ");
            Log.d(TAG, "send factoryReset Command");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendDeviceAddCommand() {
        try{
            sendCommand("{\"command\":\"add\"} ");
            Log.d(TAG, "send add Command");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendDeviceAddcancleCommand() {

        try{
            sendCommand("{\"command\":\"addCancle\"} ");
            Log.d(TAG, "send addCancle Command");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public DeviceInfo findDeviceLoop(ArrayList<DeviceInfo> deviceList, String rootUuid, String subUuid) {
        boolean bfind= false;
        DeviceInfo device_data= null;

        for (int i = 0; i < deviceList.size() ; i++) {
            device_data = deviceList.get(i);
            if ( rootUuid.equalsIgnoreCase(device_data.getRootUuid()) ) {
                //Log.d(TAG, "updateReport getRootUuid : " + device_data.getRootUuid() + " " + device_data.getSubUuid());
                if ( subUuid.equalsIgnoreCase(TypeDef.SUB_DEVICE_NONE) ) { //rootUuid 만 검색함
                    bfind= true;
                    break;
                }
                if ( subUuid.equalsIgnoreCase(device_data.getSubUuid()) ) {
                    bfind= true;
                }
                if(device_data.other_subcount > 0) { //subDevice 처리
                    for (int k = 0; k < device_data.other_subcount ; k++) {
                        if ( subUuid.equalsIgnoreCase(device_data.getOtherSubUuid(k)) ) {
                            bfind = true;
                        }
                    }
                }
                if(bfind) break;
            }
        }
        if(!bfind) device_data= null;

        //Log.d(TAG, "findDeviceLoop : " + bfind);

        return device_data;
    }

    public String removeDeviceLoop(ArrayList<DeviceInfo> deviceList, String rootUuid, String subUuid) {
        boolean bfind= false;
        String nickName= "";
        String groupID = "";
        DeviceInfo device_data= null;

        for (int i = 0; i < deviceList.size() ; i++) {
            device_data = deviceList.get(i);
            if (rootUuid.equalsIgnoreCase(device_data.getRootUuid())) {
                //rootUuid 만 비교해도 됨
                bfind= true;
                nickName = device_data.getNickName();
                groupID = device_data.getGroupID();
                deviceList.remove(i);
                break;
            }
        }
        //Log.d(TAG, "removeDeviceLoop : " + bfind);

        if(bfind) {
            int devicecount = 0;
            //그룹내 디바이스가 하나도 없으면 그룹 삭제함
            devicecount = groupControl(device_data.getnCategory(), device_data.getGroupID(), -1, TypeDef.STATUS_UNKNOWN, true);
            if(devicecount == 0) {
                for (int i = 0; i < deviceList.size() ; i++) {
                    device_data = deviceList.get(i);
                    if (device_data.bVirtualDevice && groupID.equalsIgnoreCase(device_data.groupID)) {
                        deviceList.remove(i);
                        break;
                    }
                }
            }
        }
        return nickName;
    }

    public String readNickName(String rootUuid) {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.
        String nickName;

        class simpleReadThread extends Thread {
            // Note: DB 액세스는 꼭 thread를 이용해야 한다.
            String getrootUuid;
            String getnickName;

            public simpleReadThread(String rootUuid) {
                getrootUuid = rootUuid;
            }
            public void run() {
                getnickName = dataManager.mysql_getNickName(getrootUuid);
            }
        }

        //read DB
        simpleReadThread ReadDB_thread;
        ReadDB_thread = new simpleReadThread(rootUuid);
        ReadDB_thread.start();
        try {
            ReadDB_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        nickName = ReadDB_thread.getnickName;
        return nickName;
    }

    public int groupControl(TypeDef.CategoryType category, String groupID, int subID, String value, boolean bReadOnly) {
        int listsize = 0;
        int counter = 0;
        DeviceInfo device_data;
        ArrayList<DeviceInfo> deviceList = null;

        //Log.d(TAG, "groupControl : " + category + "-" + groupID + "-" + value + "-" + bReadOnly);

        //Get Device List
        switch (category) {
            case eLight: deviceList = mCategory_eLight;
                break;
            case eIndoorEnv: deviceList = mCategory_eIndoorEnv;
                break;
            case eEnergy: deviceList = mCategory_eEnergy;
                break;
            case eSafe: deviceList = mCategory_eSafe;
                break;
            default:
                break;
        }

        if (deviceList != null) {
            String getvalue = "";
            String getsubUuid = "";
            String getSort = "";

            listsize = deviceList.size();
            for (int i = 0; i < listsize; i++) {
                device_data = deviceList.get(i);
                if (!device_data.bVirtualDevice && groupID.equalsIgnoreCase(device_data.groupID)) {
                    //get subUuid
                    if(subID == -1) //simple device
                    {
                        getvalue = device_data.value;
                        getsubUuid = device_data.getSubUuid();
                        getSort = device_data.getSort();
                    }else{
                        getvalue = device_data.getOtherValue(subID);
                        getsubUuid = device_data.getOtherSubUuid(subID);
                        getSort = device_data.getOtherSort(subID);
                    }

                    //Read or control
                    if(bReadOnly){
                        //Log.d(TAG, "groupControl getvalue : " + value + "-" + getvalue);
                        if(value.equalsIgnoreCase(TypeDef.STATUS_UNKNOWN)) {
                            //해당 group의 전체 개수 파악함
                            counter++;
                        }else {
                            // 특정 상태인 Device 개수만 파악함
                            if (value.equalsIgnoreCase(getvalue)) {
                                counter++;
                            }
                        }
                    } else {
                        //AIDL 을 통한 Device 제어
                        counter++;
                        Log.e(TAG, "counter : " + counter);
                        if(!value.equalsIgnoreCase(getvalue)){ //상태가 다를때만 제어함
                            dataManager.setOnOrOffAddSort(
                                    device_data.getRootUuid(),
                                    device_data.getRootDevice(),
                                    getsubUuid,
                                    value,
                                    getSort
                            );
                        }
                    }
                }
            }
        }
        //Log.d(TAG, "groupControl counter : " + counter);
        return counter;
    }

    public Pair<String,String> groupValue(TypeDef.CategoryType category, String groupID, String sortName) {
        int listsize = 0;
        DeviceInfo device_data;
        ArrayList<DeviceInfo> deviceList = null;
        Pair<String,String> result = new Pair<>("0", "0");
        String getvalue = "";
        String getprecision = "";
        String nickName = "";

        //Log.d(TAG, "groupValue : " + category + "-" + groupID + "-" + sortName);

        //Get Device List
        switch (category) {
            case eLight: deviceList = mCategory_eLight;
                break;
            case eIndoorEnv: deviceList = mCategory_eIndoorEnv;
                break;
            case eEnergy: deviceList = mCategory_eEnergy;
                break;
            case eSafe: deviceList = mCategory_eSafe;
                break;
            default:
                break;
        }

        if (deviceList != null) {
            listsize = deviceList.size();
            for (int i = 0; i < listsize; i++) {
                device_data = deviceList.get(i);
                if (!device_data.bVirtualDevice && groupID.equalsIgnoreCase(device_data.groupID)) {

                    // TODO: 2016-09-01 : 추후 가상디바이스별로 정리 필요함
                    if( groupID.equalsIgnoreCase(TypeDef.GroupID.eSmartMetering.value()) ){
                        if( sortName.equalsIgnoreCase(device_data.sort) ) {
                            getvalue = device_data.value;
                            getprecision = device_data.precision;
                            nickName = device_data.nickName;
                            result = new Pair<>(getvalue, getprecision);
                        }
                    }
                }
            }
        }
        //Log.d(TAG, "groupValue : " + nickName + "-" + getvalue + "/" + getprecision);

        return result;
    }

    public void markGroupValue(TypeDef.CategoryType category, String groupID) {
        ArrayList<DeviceInfo> deviceList = null;
        DeviceInfo device_data= null;
        String getgroupID = groupID;

        //Get Device List
        switch (category) {
            case eLight: deviceList = mCategory_eLight;
                break;
            case eIndoorEnv: deviceList = mCategory_eIndoorEnv;
                break;
            case eEnergy: deviceList = mCategory_eEnergy;
                break;
            case eSafe: deviceList = mCategory_eSafe;
                break;
            default:
                break;
        }

        //eMainSwitch도 eLight group에 포함시킴
        if( groupID.equalsIgnoreCase(TypeDef.GroupID.eMainSwitch.value()) )
        {
            getgroupID = TypeDef.GroupID.eLight.value();
        }

        if (deviceList != null) {
            for (int i = 0; i < deviceList.size(); i++) {
                device_data = deviceList.get(i);
                if (device_data.bVirtualDevice && getgroupID.equalsIgnoreCase(device_data.groupID)) {
                    device_data.bValidate = false;
                    //Log.d(TAG, "markGroupValue: " + device_data.nickName + " " + getgroupID);
                    break;
                }
            }
        }
    }

    public int findDeviceControllerID(DeviceInfo device_data, String controller) {
        int index = -1;
        for(int i=0; i< device_data.getOtherSubcount(); i++) {
            if (device_data.getOtherSort(i).equalsIgnoreCase(controller)) {
                index = i;
                break;
            }
        }
        //Log.d(TAG, "findDeviceControllerID: " + index);
        return index;
    }

    //BroadcastReceiver : updateReport
    public void updateReport(final String add_raw) {

        Log.d(TAG, "updateReport " + add_raw);

        DeviceInfo device_data;
        String rootUuid = "";
        String subUuid = "";

        //get device info
        rootUuid = dataManager.mysql_getRootUuid(add_raw);
        try {
            subUuid = dataManager.mysql_getsubUuid(add_raw);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "updateReport: " + rootUuid + "/" + subUuid);

        //해당 디바이스 검색함
        device_data = findDeviceLoop(mCategory_eLight, rootUuid, subUuid);
        if(device_data == null) {
            device_data = findDeviceLoop(mCategory_eIndoorEnv, rootUuid, subUuid);
            if(device_data == null) {
                device_data = findDeviceLoop(mCategory_eEnergy, rootUuid, subUuid);
                if(device_data == null) {
                    device_data = findDeviceLoop(mCategory_eSafe, rootUuid, subUuid);
                }
            }
        }

        if(device_data != null) {
            String nickName = readNickName(rootUuid);
            String get_value = dataManager.mysql_getValue(add_raw);
            String get_sort = dataManager.mysql_getSort(add_raw);
            //String batteryLevel = ??? //todo
            String batteryEvent = dataManager.mysql_getbatteryEvent(add_raw);

            //Update data
            if(nickName != null){
                if(!nickName.equalsIgnoreCase(device_data.nickName)) {
                    device_data.nickName = nickName; //nickName 변경 고려함
                    device_data.bUpdated = true; //같으면 업데이트 안함
                }
            }

            if(get_sort.equalsIgnoreCase(device_data.sort))
            {
                Log.d(TAG, "root value get_value : " + get_value + " , device_data.value : " + device_data.value);
                //default subDevice info
                if(!get_value.equalsIgnoreCase(device_data.value)) {
                    device_data.value = get_value;
                    device_data.bUpdated = true; //같으면 업데이트 안함
                }
            }
            else {
                //read from other subDevice info
                if(device_data.other_subcount > 0) {
                    //other value get index
                    int sub_id = findDeviceControllerID(device_data, get_sort);

                    if(sub_id != -1) {
                        if(!get_value.equalsIgnoreCase(device_data.other_value[sub_id])) {
                            Log.d(TAG, "other sub get_value : " + get_value + " , device_data.value : " + device_data.other_value[sub_id]);
                            device_data.other_value[sub_id] = get_value;
                            device_data.bUpdated = true; //같으면 업데이트 안함
                        }
                        else {
                            Log.d(TAG,"other sub get_value : " + get_value + ",  no changeed ");
                        }
                    }
                }
            }

            //2016-12-01,yslee::Doorlock 대응
            if(device_data.CommaxDevice.equalsIgnoreCase(TypeDef.SUB_DEVICE_DOORLOCK)) {
                if (!TextUtils.isEmpty(batteryEvent)) {
                    if(!batteryEvent.equalsIgnoreCase(device_data.batteryEvent)) {
                        device_data.batteryEvent = batteryEvent; //batteryEvent 변경 고려함
                        device_data.bUpdated = true; //같으면 업데이트 안함
                    }
                }
            }

            //Update value 과정 : bUpdated flag set -> Thread -> handler call ->  UI Update value
            Log.i(TAG, "updateReport : " + device_data.nickName + "-" + get_sort + "-" + get_value + " ,  updated : " + device_data.bUpdated);
        }
    }

    //BroadcastReceiver : updateReportName
    public void updateReportName(final String rootUuid, final String nickName) {

        Log.d(TAG, "updateReportName " + rootUuid + "-" + nickName);

        //해당 디바이스 검색함
        DeviceInfo device_data;

        device_data = findDeviceLoop(mCategory_eLight, rootUuid, TypeDef.SUB_DEVICE_NONE);
        if(device_data == null) {
            device_data = findDeviceLoop(mCategory_eIndoorEnv, rootUuid, TypeDef.SUB_DEVICE_NONE);
            if(device_data == null) {
                device_data = findDeviceLoop(mCategory_eEnergy, rootUuid, TypeDef.SUB_DEVICE_NONE);
                if(device_data == null) {
                    device_data = findDeviceLoop(mCategory_eSafe, rootUuid, TypeDef.SUB_DEVICE_NONE);
                }
            }
        }

        if(device_data != null) {
            //Update data
            if(nickName != null){
                if(!nickName.equalsIgnoreCase(device_data.nickName)) {
                    device_data.nickName = nickName; //nickName 변경 고려함
                    device_data.bUpdated = true; //같으면 업데이트 안함
                }
            }

            //Update value 과정 : bUpdated flag set -> Thread -> handler call ->  UI Update value
            Log.d(TAG, "updateReportName : " + device_data.nickName);
        }
    }

    //BroadcastReceiver : addReport
    public void addReport(final String add_raw) {

     /*   try{
            //add dialog dismiss
            DialogActivity.getInstance().finish();
//        customdialog_one_button.dismiss();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        Log.d(TAG, "addReport " + add_raw);
        String rootUuid = dataManager.mysql_getRootUuid(add_raw);
        Log.d(TAG, "addReport : " + rootUuid);

        if(!mbNeedTo_addReport)
        {
            Log.d(TAG, "mbNeddTo_addReport :  " + mbNeedTo_addReport);
            waitDialogEnable(getResources().getString(R.string.message_dbupdate_string), true);
            mMain_handler.sendEmptyMessageDelayed(MSG_SEND_ID_UPDATE, TypeDef.DEVICE_UPDATE_INTERVAL_MS);
        }

        if(MainActivity.getInstance() == null)
        {
            mbNeedTo_addReport = true;
        }
    }

    //BroadcastReceiver : removeReport
    public void removeReport(final String add_raw) {

        Log.d(TAG, "removeReport " + add_raw);

        String rootUuid = "";
        String subUuid = "";
        String nickName = "";

        //get device info
        rootUuid = dataManager.mysql_getRootUuid(add_raw);
        Log.d(TAG, "removeReport : " + rootUuid);

        if (!TextUtils.isEmpty(rootUuid))
        {
            nickName = removeDeviceLoop(mCategory_eLight, rootUuid, subUuid);
            if (TextUtils.isEmpty(nickName)) {
                nickName = removeDeviceLoop(mCategory_eIndoorEnv, rootUuid, subUuid);
                if (TextUtils.isEmpty(nickName)) {
                    nickName = removeDeviceLoop(mCategory_eEnergy, rootUuid, subUuid);
                    if (TextUtils.isEmpty(nickName)) {
                        nickName = removeDeviceLoop(mCategory_eSafe, rootUuid, subUuid);
                    }
                }
            }
            if (!TextUtils.isEmpty(nickName)){
                //showToastBox(nickName + " " + getResources().getString(R.string.message_removedevice_string,false));
                redrawTabList();
            }
        }
        //remove value 과정 : device list remove ->  UI Update
        Log.d(TAG, "removeReport : " + nickName);

        try
        {
            mMain_handler.sendEmptyMessage(MSG_SEND_TAP_ABLE_CHEKC);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    //NavigationBar Control
    public void setNavigationBar(boolean bShow) {
        /* JB4.2에서는 안된다....*/
/*
        Log.d(TAG, "setNavigationBar : " + bShow);
        try {
            Process process;
            if(bShow) process = Runtime.getRuntime().exec("am startservice -n com.android.systemui/.SystemUIService");
            else process = Runtime.getRuntime().exec("service call activity 42 s16 com.android.systemui");
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
/*
        if(bShow == false) {
            //Log.d(TAG, "setNavigationBar : " + bShow);
            hideSystemBar();
        }
*/
    }

    /*
    //참조: http://masashi-k.blogspot.kr/2013/09/hide-show-system-bar-of-android.html

    private void showSystemBar() {
        String commandStr = "am startservice -n com.android.systemui/.SystemUIService";
        runAsRoot(commandStr);
    }

    private void hideSystemBar() {
        try {
            //REQUIRES ROOT
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79"; //HONEYCOMB AND OLDER

            //v.RELEASE  //4.0.3
            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }

            String commandStr = "service call activity " +
                    ProcID + " s16 com.android.systemui";
            runAsRoot(commandStr);
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }

    private void runAsRoot(String commandStr) {
        try {
            CommandCapture command = new CommandCapture(0, commandStr);
            RootTools.getShell(true).add(command).waitForFinish();
        } catch (Exception e) {
            // something went wrong, deal with it here
        }
    }
    */

    /* Class Methods(event) -----------------------------*/
    public void BtnHome_Clicked(View v) {

        Log.d(TAG, "BtnHome_Clicked()");

        //실행 속도를 높이기 위함(다음 실행시 onResume으로 유도함)
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_FORWARD_RESULT
                | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);

        finish(); //todo :??
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //실행 속도를 높이기 위함(다음 실행시 onResume으로 유도함)
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Log.d(TAG, "onKeyDown...KEYCODE_BACK");
            //TODO 보슬이가 검토 요청
            BtnHome_Clicked(null);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void BtnGuide_Clicked(View v) {
        Log.d(TAG, "Edit => RootUuid: " + TypeDef.VIRTUAL_DEVICE_ROOTUUID + ", more_commax_device: " + TypeDef.CONNECTION_GUIDE_DEVICE);
        /*//Show message
        showToastBox(getResources().getString(R.string.message_move_page),false);*/
        getDeviceInfoSimple();
        try {
            Intent intent = new Intent();
            intent.setClassName(TypeDef.MORE_CLASS_NAME, TypeDef.MORE_ACTIVITY_NAME);
            intent.putExtra(TypeDef.MORE_ROOT_UUID, TypeDef.VIRTUAL_DEVICE_ROOTUUID);
            intent.putExtra(TypeDef.MORE_COMMAX_DEVICE, TypeDef.CONNECTION_GUIDE_DEVICE);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //mContext.startActivity(intent);
            startActivityForResult(intent, TypeDef.CONTROL_MSG_ID); //NoACK를 이용해 이전화면을 유지함

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void BtnEdit_Clicked(View v) {
        Log.d(TAG, "Edit => RootUuid: " + TypeDef.VIRTUAL_DEVICE_ROOTUUID + ", more_commax_device: " + TypeDef.EDIT_COMMAX_DEVICE);
        /*//Show message
        showToastBox(getResources().getString(R.string.message_move_page),false);*/

        getDeviceInfoSimple();
        try {
            Intent intent = new Intent();
            intent.setClassName(TypeDef.MORE_CLASS_NAME, TypeDef.MORE_ACTIVITY_NAME);
            intent.putExtra(TypeDef.MORE_ROOT_UUID, TypeDef.VIRTUAL_DEVICE_ROOTUUID);
            intent.putExtra(TypeDef.MORE_COMMAX_DEVICE, TypeDef.EDIT_COMMAX_DEVICE);
            intent.putExtra(TypeDef.EDIT_COMMAX_DEVICE, mDevice_simpleList); //데이터 추가함
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //mContext.startActivity(intent);
            startActivityForResult(intent, TypeDef.CONTROL_MSG_ID); //NoACK를 이용해 이전화면을 유지함

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Hidden
    public void BtnTitle_Clicked(View v) {
        Log.d(TAG, "BtnTitle_Clicked()" + mbMenu_Touchcounter);
        if(mSelect_TapId == TypeDef.TAB_ENERGY)
        {
            mbMenu_Touchcounter++;
            //for test : print all device list
            if(mbMenu_Touchcounter > 10) {
                Log.d(TAG, "*** Enter Hidden Menu" + mbMenu_Touchcounter);
                printDeviceList(mCategory_eLight);
                printDeviceList(mCategory_eIndoorEnv);
                printDeviceList(mCategory_eEnergy);
                printDeviceList(mCategory_eSafe);
                mbMenu_Touchcounter = 0;
            }
        }
    }

    public void onClick(View view)
    {
        if(view.equals(btn_add))
        {
//            String[] dialog_component = {getString(R.string.menu_add_string) , getString(R.string.popup_device_add_text)};
//            customdialog_one_button = new Customdialog_one_button(this,dialog_component );
//            customdialog_one_button.show();

            startActivityForResult(new Intent(this, DialogActivity.class) , 0);
            sendDeviceAddCommand();

        }else if(view.equals(m_subtitle1))
        {
            draw_TabList(TypeDef.TAB_FAV);
        }
        else if(view.equals(m_subtitle2))
        {
            draw_TabList(TypeDef.TAB_LIGHR);
        }
        else if(view.equals(m_subtitle3))
        {
            draw_TabList(TypeDef.TAB_INDOOR);
        }else if(view.equals(m_subtitle4))
        {
            draw_TabList(TypeDef.TAB_ENERGY);
        }
        else if(view.equals(m_subtitle5))
        {
            draw_TabList(TypeDef.TAB_SAFE);
        }
        mStartTabID = mSelect_TapId;
    }

    public void Tab_able_disable()
    {
        Log.d(TAG, " tab able/disable check ");
        try
        {
            if(mCategory_eLight.size() == 0)
            {
                m_subtitle2.setEnabled(false);
            }
            else {
                m_subtitle2.setEnabled(true);
            }

            if(mCategory_eIndoorEnv.size() == 0)
            {
                m_subtitle3.setEnabled(false);
            }
            else {
                m_subtitle3.setEnabled(true);
            }

            if(mCategory_eEnergy.size() == 0)
            {
                m_subtitle4.setEnabled(false);
            }
            else {
                m_subtitle4.setEnabled(true);
            }

            if(mCategory_eSafe.size() == 0)
            {
                m_subtitle5.setEnabled(false);
                Log.d(TAG, "enalbe false ");
            }
            else {
                m_subtitle5.setEnabled(true);
                Log.d(TAG, "enalbe true ");
            }

            Selected_TapId();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void Selected_TapId()
    {
        //기존에 safe가 눌린 상태에서 safe의 디바이스를 삭제하고 돌아오면 list 의 사이즈를 파악해서 Favorite Tab으로 넘김
        if(m_subtitle2.isSelected())
        {
            if(mCategory_eLight.size() == 0)
            {
                draw_TabList(TypeDef.TAB_FAV);
            }
        }else if(m_subtitle3.isSelected())
        {
            if(mCategory_eIndoorEnv.size() == 0)
            {
                draw_TabList(TypeDef.TAB_FAV);
            }
        }else if(m_subtitle4.isSelected())
        {
            if(mCategory_eEnergy.size() == 0)
            {
                draw_TabList(TypeDef.TAB_FAV);
            }
        }else if(m_subtitle5.isSelected())
        {
            if(mCategory_eSafe.size() == 0)
            {
                draw_TabList(TypeDef.TAB_FAV);
            }
        }
    }

    public void hideNavigationBar(){

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
    /* Class End -----------------------------*/
}
