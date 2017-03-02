package com.commax.controlsub;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.adapter.aidl.IAdapter;
import com.commax.control.Common.DeviceInfoSimple;
import com.commax.controlsub.Common.BroadcastReceiver;
import com.commax.controlsub.Common.BuyerID;
import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.Connection_Guide.Connection_Guide_Component;
import com.commax.controlsub.Connection_Guide.Connection_Guide_ListCell;
import com.commax.controlsub.Connection_Guide.Connection_Guide_Main;
import com.commax.controlsub.DeviceListEdit.ControlName_Edit_Main;
import com.commax.controlsub.DeviceListEdit.DeviceNameEdit;
import com.commax.controlsub.MoreActivity.More_FCU;
import com.commax.controlsub.MoreActivity.More_FCU_no_ventilation_nofanspeed;
import com.commax.controlsub.MoreActivity.More_HVAC;
import com.commax.controlsub.MoreActivity.More_StandbyPower;

import java.util.ArrayList;

public class MainActivity extends Activity {
    static final String TAG = MainActivity.class.getSimpleName();
    public static MainActivity _instance;
    public Context mContext;
    public DataManager dataManager;  //Data Manager

    //for LLT
    public More_StandbyPower more_standbyPower;
    //FCU 구분
    public More_FCU_no_ventilation_nofanspeed more_fcu0;
    public More_HVAC more_fcu1;
    public More_FCU more_fcu3;

    public DeviceNameEdit deviceListEdit;
    public ControlName_Edit_Main editView_test;

    public Connection_Guide_Main connectionGuideMain;
    public BuyerID mBuyerID ;

    //flags
    String ROOTUUID ;
    //TODO for test
    String more_cmx_device ;
    public boolean main_stop_flag = false;
    /* Member Variables ----------------------------- */
    ArrayList<DeviceInfo> mCategory_eFavorite;
    ArrayList<DeviceInfo> mCategory_eLight;
    ArrayList<DeviceInfo> mCategory_eIndoorEnv;
    ArrayList<DeviceInfo> mCategory_eEnergy;
    ArrayList<DeviceInfo> mCategory_eSafe;
    // nickname list reading from DB (now this is not used)
    ArrayList<DeviceInfo> mNickname_list;
    // nickname list getting from Control container
    ArrayList<com.commax.control.Common.DeviceInfoSimple> mDevice_simpleList = null;

    static final int MAX_CATEGORY = 5;

    //custom toast pxd
    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;

    //UI
    RelativeLayout main_back_button_layout;
    ImageButton back_button;
    public LinearLayout container;
    private View mLoadingContainer;
    BroadcastReceiver mReceiver;

    public static MainActivity getInstance()  {
        return _instance;
    }
    /* Export Methods -----------------------------*/
    // RemoteServiceConnection 은 AIDL 을 통한 Device 제어에 사용됨
    public static RemoteServiceConnection m_rc;
    public static RemoteServiceConnection getRemoteConnection() {
        return m_rc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG ,"onCreate()");
        //IP Home IoT navigation bar hide
        hideNavigationBar();
        setContentView(R.layout.activity_main);
        //buyer ID
        mBuyerID = new BuyerID();

        //2016-09-15 join 사용으로 인한 UI 멈춤 현상으로 loading 사용 할 수 없음
        container = (LinearLayout)findViewById(R.id.container);
        mLoadingContainer = findViewById(R.id.loading_container);
        // 빈 메인 화면
        main_back_button_layout = (RelativeLayout)findViewById(R.id.relative_layout);
        back_button = (ImageButton)findViewById(R.id.main_back_button);
        mContext = this;
        _instance = this;
        //pxd custom toast
        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        }catch (Exception e){
            e.printStackTrace();
        }

         /*get Intent*/
        try {
            Intent myintent = getIntent();
            Log.d(TAG, "myintent :  " + String .valueOf(myintent));
            if(myintent != null){
                try
                {
                    ROOTUUID = myintent.getStringExtra("RootUuid");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                more_cmx_device = myintent.getStringExtra("more_commax_device");
                Log.d(TAG, "===> myintent rootuuid: " + ROOTUUID + ", commaxdevice: " + more_cmx_device );

                mDevice_simpleList= (ArrayList<com.commax.control.Common.DeviceInfoSimple>) myintent.getSerializableExtra("nickname");
                if(mDevice_simpleList != null) {
                    Log.d(TAG, "myintent size:" + mDevice_simpleList.size());
                    com.commax.control.Common.DeviceInfoSimple simple_data;
                    for (int i = 0; i < mDevice_simpleList.size(); i++) {
                        simple_data = mDevice_simpleList.get(i);
                        Log.i(TAG, "getRootUuid :" + simple_data.rootUuid +"    getNickName :" + simple_data.nickName +"    getCategory :" + simple_data.nCategory );
                    }
                }
            }
            Log.d(TAG, "more_cmx_device() = " + more_cmx_device + " ,  ROOTUUID() : " + ROOTUUID);
        }catch (Exception e){
            e.printStackTrace();
        }
        check_more_device(more_cmx_device , ROOTUUID , mDevice_simpleList , null);
    }
    //pxd custom toast
    public void showToastOnWorking(String txt){
        try
        {
            if(!main_stop_flag)
            {
                Toast toast = new Toast(getApplicationContext());
                toastTextView.setText(txt);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.show();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void onClick(View view)
    {
        if(view.equals(back_button)){
            finish();
        }
    }

    public void check_more_device(String more_cmx_device , String ROOTUUID , ArrayList<DeviceInfoSimple> arrayList , Connection_Guide_ListCell listCell)
    {

        /* intent 에서 받아온 값으로 판단하여 어떤 디바이시의 상세 페이지 인지 파악 및 view add */
        try
        {
            Log.e(TAG, "more_commax_device() : " + more_cmx_device + " , RootUuid() : " +ROOTUUID);
            if( TextUtils.isEmpty(more_cmx_device) || TextUtils.isEmpty(ROOTUUID))
            {
                // 노 디바이스
                showToastOnWorking(getString(R.string.no_device));
            }
            else if(more_cmx_device.equals(TypeDef.NickName))
            {
                // 기기편집 페이지
                //open dataManager
                dataManager = new DataManager(mContext);
                Log.e(TAG, "more_commax_device : " + more_cmx_device + " , RootUuid : " +ROOTUUID);
                CallActivity(null , ROOTUUID , arrayList);
            }
            else if(more_cmx_device.equalsIgnoreCase(TypeDef.Connection_Guide))
            {
                CallActivity(null,ROOTUUID , null);
            }
            else if(more_cmx_device.equals("guide_detail_page"))
            {
                Log.d(TAG, " guide detail page");
                Connection_Guide_Component connection_guide_component = new Connection_Guide_Component(mContext , listCell);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                container.addView(connection_guide_component, layoutParams);
            }
            else
            {
                // 디바이스 상세 페이지
                //카테고리 별로 분류하는 함수를 활용해서 해당 카테고리 list와 rootUuid 로 해당 상세 페이지 호출
                data_init(mContext);
                Log.e(TAG, "more_commax_device : " + more_cmx_device + " , RootUuid : " +ROOTUUID);
                if(more_cmx_device.equals(TypeDef.COMMAX_DEVICE_STANDBYPOWER))
                {
                    CallActivity(mCategory_eEnergy , ROOTUUID , null);
                }
                else if(more_cmx_device.equals(TypeDef.COMMAX_DEVICE_FCU))
                {
                    CallActivity(mCategory_eIndoorEnv , ROOTUUID , null);
                }
                else {
                    showToastOnWorking(getString(R.string.not_support_device));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void CallActivity(ArrayList<DeviceInfo> deviceList ,String rootUuid , ArrayList<DeviceInfoSimple> nicknamelist)
    {
        main_back_button_layout.setVisibility(View.GONE);
        back_button.setVisibility(View.GONE);
        if(more_cmx_device.equalsIgnoreCase(TypeDef.NickName))
        {
            editView_test = new ControlName_Edit_Main(mContext , nicknamelist);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(editView_test, layoutParams);

          //현재 해당 view는 clear edit text 가 먹히지 않는다 이유를 파악 못함
          /*   deviceListEdit = new DeviceNameEdit(mContext , nicknamelist);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(deviceListEdit, layoutParams);*/
        }
        else if(more_cmx_device.equalsIgnoreCase(TypeDef.Connection_Guide))
        {
            connectionGuideMain = new Connection_Guide_Main(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(connectionGuideMain, layoutParams);
        }
        else
        {
            for(int i = 0 ; i < deviceList.size() ; i++)
            {
                if(deviceList.get(i).getRootUuid().equals(rootUuid))
                {

                    if(more_cmx_device.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU))
                    {
                        //FCU모드 구분 (0: No FAN, 1: with FAN, 2: with FAN Auto , 3: FCU FUll 기능)
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        if(TypeDef.DEF_FCU_MODE == 0) {
                            more_fcu0 = new More_FCU_no_ventilation_nofanspeed(mContext, deviceList.get(i));
                            container.addView(more_fcu0, layoutParams);
                        } else if(TypeDef.DEF_FCU_MODE == 1) {
                            more_fcu1 = new More_HVAC(mContext, deviceList.get(i));
                            container.addView(more_fcu1, layoutParams);
                        } else if(TypeDef.DEF_FCU_MODE == 2) {
                            more_fcu1 = new More_HVAC(mContext, deviceList.get(i));
                            container.addView(more_fcu1, layoutParams);
                        }
                        else{
                            more_fcu3 = new More_FCU(mContext, deviceList.get(i));
                            container.addView(more_fcu3, layoutParams);
                        }
                    }
                    else if(more_cmx_device.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_STANDBYPOWER))
                    {
                        more_standbyPower = new More_StandbyPower(mContext, deviceList.get(i));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        container.addView(more_standbyPower, layoutParams);
                    }
                }
            }
        }
    }

    private void data_init(Context context){
        Log.d(TAG, "data_init()");
        /* DeviceInfo */
        mCategory_eFavorite = new ArrayList<DeviceInfo>();
        mCategory_eLight = new ArrayList<DeviceInfo>();
        mCategory_eIndoorEnv = new ArrayList<DeviceInfo>();
        mCategory_eEnergy = new ArrayList<DeviceInfo>();
        mCategory_eSafe = new ArrayList<DeviceInfo>();
        //now this is not used
        mNickname_list = new ArrayList<DeviceInfo>();
        /* open dataManager */
        dataManager = new DataManager(context);
        /* open DB */
        OpenDB();
        ReadDB();
//        printDeviceList(mCategory_eLight); //for test
    }

    public void removeReport(String remove_raw)
    {
        editView_test.remove_report(remove_raw);
    }

    public void add_updateReport(String add_raw)
    {
        try{
            //TODO 기기 추가 리포트가 오면 해당 디바이스 카테고리 파악해서 카테코리 리스트에 해당 디바이스 추가
            try
            {
                if(editView_test.mArrayList.isEmpty())
                {
                    Log.d(TAG, "mArrayLIst is empty");
                }
                else
                {
                    //TODO 닉네임 변경에서 addreport가 필요한가?
//                    editView_test.add_updateReport(add_raw);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

            try
            {
                if(DialogActivity.getInstance() != null)
                {
                    DialogActivity.getInstance().finish();
                    DialogActivity.getInstance().circle_progress_timer_flag = false;
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
//            showToastOnWorking(getString(R.string.device_added));
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    public void updateReport(final String add_raw)
    {
        try
        {
            if(MainActivity.getInstance() == null || dataManager == null)
            {

            }else
            {
                Log.i(TAG, "updateReport : add_raw :" + add_raw);
                String rootUuid = dataManager.mysql_getRootUuid(add_raw);
//        String nickName = dataManager.mysql_getNickName(rootUuid);
//        Log.d(TAG, "updateReport : nickName : " + nickName);
                String sort = dataManager.mysql_getSort(add_raw);
                Log.d(TAG, "updateReport() sort : " +sort);

                //get device info
                DeviceInfo device_data;
                String subUuid = dataManager.mysql_getsubUuid(add_raw);
                Log.d(TAG, "subUuid : " + subUuid);

                device_data = findDeviceLoop(mCategory_eLight, rootUuid, subUuid);
                if(device_data == null)
                {
                    device_data = findDeviceLoop(mCategory_eIndoorEnv, rootUuid, subUuid);
                    if(device_data == null) {
                        device_data = findDeviceLoop(mCategory_eEnergy, rootUuid, subUuid);
                        if(device_data == null) {
                            device_data = findDeviceLoop(mCategory_eSafe, rootUuid, subUuid);
                        }
                    }
                }

                //TODO 1분다마 갱신시 대기전력1과 대기전력 2가 동시에 bind됨 => 구별해야함
                if(device_data != null && rootUuid.equals(device_data.getRootUuid())) {
                    Log.e(TAG, "---- devicd bind----- ");
                    String nickName = readNickName(rootUuid);
                    Log.d(TAG, "updateReport :  nickName : " + nickName);
                    String get_value = dataManager.mysql_getValue(add_raw);

                    //Update data
                    if(nickName != null) device_data.nickName = nickName; //nickName 변경 고려함

                    //해당 subUuid 의 value를 갱신 시켜야 한다.
                    if(subUuid.equals(device_data.getSubUuid()))
                    {
                        device_data.setValue(get_value);
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid2()))
                    {
                        device_data.value2 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid3()))
                    {
                        device_data.value3 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid4()))
                    {
                        device_data.value4 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid5()))
                    {
                        device_data.value5 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid6()))
                    {
                        device_data.value6 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }
                    else if(subUuid.equals(device_data.getSubUuid7()))
                    {
                        device_data.value7 = get_value;
                        Log.i(TAG, "update report value : " + get_value);
                    }


                    device_data.bUpdated = true;

                    //Update value 과정 : bUpdated flag set -> Thread -> handler call ->  UI Update value
                    Log.d(TAG, "updateReport : " + device_data.nickName + "-" + get_value);
                }

                rootUuid = null;
                //TODO 보슬이 소스 주석 처리
        /*try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String updateRoot = dataManager.mysql_getRootUuid(add_raw);
                    String sort = dataManager.mysql_getSort(add_raw);
                    Log.d(TAG, "updateReport : rootUuid : " + updateRoot + " / sort :" + sort);

                    try {
                        if ((!updateRoot.isEmpty()) && (!updateRoot.equalsIgnoreCase("String"))) {

                            if ((sort.equalsIgnoreCase(TypeDef.SUB_DEVICE_SWITCHBINARY))) {
                                updateDeviceByRootUuid(updateRoot, add_raw, sort);

                            }else if(sort.equalsIgnoreCase(TypeDef.SUB_DEVICE_ELECTRICMETER)){
                                updateMeterState(updateRoot, add_raw, sort);
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();


        }catch (Exception e){
            e.printStackTrace();
        }*/
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void nicknameUpdate_report(String rootuuid , String nickname)
    {
        //현재는 닉네임 변경 하자마자 해당 페이지 나가서 필요가 없음
        DeviceNameEdit.getInstance().nickname_update_report(rootuuid , nickname);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        nickName = ReadDB_thread.getnickName;
        return nickName;
    }

    public DeviceInfo findDeviceLoop(ArrayList<DeviceInfo> deviceList, String rootUuid, String subUuid) {
        boolean bfind= false;
        DeviceInfo device_data= null;

        if(deviceList != null)
        {
            for (int i = 0; i < deviceList.size() ; i++) {
                device_data = deviceList.get(i);
                if (rootUuid.equalsIgnoreCase(device_data.getRootUuid())) {
                    //rootUuid 만 비교해서 진행해도 될듯
                    //subUuid 만 비교해도 됨
                    if (subUuid.equalsIgnoreCase(device_data.getSubUuid())) {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid2()))
                    {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid3()))
                    {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid4()))
                    {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid5()))
                    {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid6()))
                    {
                        bfind= true;
                        break;
                    }
                    else if(subUuid.equalsIgnoreCase(device_data.getSubUuid7()))
                    {
                        bfind= true;
                        break;
                    }
                /*bfind= true;
                break;*/
                }
            }
        }

        if(!bfind) return null;

        return device_data;
    }

    private void updateMeterState(String rootUuid, String raw, final String sort){

/*        try {
            Pair<String, String> result_val = dataManager.getValueFromRaw(raw);

            String value = "String", scale = "String";
            if (!TextUtils.isEmpty(result_val.first)) {
                value = result_val.first;
            }
            if (!TextUtils.isEmpty(result_val.second)) {
                scale = dataManager.getScale(result_val.second);
            }

            android.util.Log.d(TAG, "updateDeviceViewByRootUuid value : " + value + scale + " / sort : " + sort);

            final String mValue = value;
            final String mScale = scale;

            if ((!TextUtils.isEmpty(value)) && (!TextUtils.isEmpty(scale))) {
                for (int i = 0; i < container.getChildCount(); i++) {

                    if (container.getChildAt(i) instanceof DeviceElement) {
                        final DeviceElement deviceElement = (DeviceElement) container.getChildAt(i);
                        if (deviceElement.getRootUuid().equals(rootUuid)) {
                            if (sort.equals(DataManager.ELECTRIC_METER)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            deviceElement.updateMeter(mValue, mScale);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }

    private void updateDeviceByRootUuid(final String rootUuid, String raw, String sort){
       /* try {

            String result_val = "";
            result_val = dataManager.getStatusFromRaw(raw);

            if (!TextUtils.isEmpty(result_val)) {

                String status = "";
                status = result_val;

                Log.d(TAG, "updateDeviceByRootUuid value : " + status + " / sort : " + sort);

                final String mStatus = status;

                try {
                    for (int i = 0; i < container.getChildCount(); i++) {

                        if (container.getChildAt(i) instanceof DeviceElement) {

                            final DeviceElement deviceElement = (DeviceElement) container.getChildAt(i);

                            if (deviceElement.getRootUuid().equalsIgnoreCase(rootUuid)) {
                                if (deviceElement.getControlType().equalsIgnoreCase(sort)) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {
                                                Log.d(TAG, "updateDeviceByRootUuid :" + deviceElement.getRootUuid() + " updated / " + mStatus);
                                                deviceElement.initStatus(mStatus);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
*/
    }

    public void OpenDB() {
        Log.d(TAG, "OpenDB");
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        ConnectionThread OpenDB_thread;

        //Try to DB connection
        OpenDB_thread = new ConnectionThread();
        if(OpenDB_thread != null) {
            try {
                OpenDB_thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Wait for DB connection
            try {
                OpenDB_thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ConnectionThread extends Thread {
        public void run() {
            try {
                if (!Thread.currentThread().isInterrupted())
                    Thread.sleep(1);

                Log.d(TAG, "try to open MySQL");
                if (dataManager == null) return;
                //TODO 마스터가 꺼져있을경우 인터넷이 안될경우 예외처리 진행 해야함

                for (int i = 0; i < dataManager.MYSQL_TRY_COUNT; i++) {
                    //MySQL 서버에 바로 연결이 되지 않은 경우가 자주 발생함
                    if (dataManager.mysql_isConnected() && dataManager.mysql_checkValidConnection()) {
                        dataManager.mConnect = true;
                        Log.e(TAG, "open MySQL ... success");
                        break;
                    } else {
                        dataManager.mysql_close();
                        Thread.sleep(200);
                        dataManager.mysql_open();
                        Thread.sleep(200);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void ReadDB() {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.
        Log.d(TAG, "ReadDB");
        ReadDevicesThread ReadDB_thread;
        ReadDB_thread = new ReadDevicesThread();
        //Try to DB connection
        if(ReadDB_thread != null) {
            try {
                ReadDB_thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Wait for DB connection
            try {
                ReadDB_thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    Log.d(TAG, "ReadDevicesThread..Ready wait " + i);
                    if (!dataManager.mysql_isConnected() || !dataManager.mysql_checkValidConnection()) {
                        Thread.sleep(100);
                        continue;
                    }
                    break;
                }

                //read all devices
                readDevicesAll(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readDevicesAll(boolean controlname_edit_flag) {
        //TODO controlname_deit_flag 사용 할지 말지 판단 해야한다.a
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        if (!dataManager.mConnect) {
            Log.d(TAG, "*** Not connected to MySQL DB !!!");
//            showToastOnWorking("Not connected to MySQL DB");
            return;
        }

        //Reset Data List
        try {
            mCategory_eFavorite.clear();
            mCategory_eLight.clear();
            mCategory_eIndoorEnv.clear();
            mCategory_eEnergy.clear();
            mCategory_eSafe.clear();
            mNickname_list.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO 추후 해당 TAB에 대해서 판별 하는 함수 하나 구현해서 해당 카테고리 분류해서
        //TODO 그 영역 디바이스 Read 하도록 설정 해놓고 디바이스 기기편집은 넘겨주는 data값으로만 적용

        //Scan All Device by Category
        if(!controlname_edit_flag)
        {
            try {
                int i =0 ;
                if(more_cmx_device.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_STANDBYPOWER))
                {
                    i = 4;
                    readDevices(i);
                }
                else if(more_cmx_device.equalsIgnoreCase(TypeDef.COMMAX_DEVICE_FCU))
                {
                    i = 3;
                    readDevices(i);
                }
                else if(more_cmx_device.equalsIgnoreCase(TypeDef.NickName))
                {
//                dataManager.mysql_getSelRootDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH);
                    for( i = 2;  i <= (MAX_CATEGORY-1) ; i++)
                    {
                        readDevices(i);
                    }
                    mNickname_list.addAll(mCategory_eLight);
                    mNickname_list.addAll(mCategory_eIndoorEnv);
                    mNickname_list.addAll(mCategory_eEnergy);
                    Log.d(TAG, "mNickName_list size  = " + mNickname_list.size());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(controlname_edit_flag)
        {
            for(int i = 2;  i <= (MAX_CATEGORY-1) ; i++)
            {
                //전체에 대한 디바이스를 읽고 해당 카테고리별 list로 분류
                readDevices(i);
            }
        }
    }

    private void readDevices(int category_number) {
        // Note: DB 액세스는 꼭 thread를 이용해야 한다.

        if (!dataManager.mConnect) {
            Log.d(TAG, "*** Not connected to MySQL DB !!!");
            return;
        }

        try {
            ArrayList<DeviceInfo> readDeviceList;
            TypeDef.CategoryType categorytype;

            switch(category_number)
            {
               /* case 0 : // eNone
                    categorytype = TypeDef.CategoryType.eNone;
                    break;

                case 1 : // eFavorite
                    categorytype = TypeDef.CategoryType.eFavorite;
                    // TODO: 2016-08-02
                    break;
                */

                case 2 : //eLight Scan
                   /* categorytype = TypeDef.CategoryType.eLight;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_MAINLIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    dataManager.setCardType(TypeDef.CardType.eMainSwitch);
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                  *//*  //for test(to be del) : // TODO: 2016-08-17
                    DeviceInfo device_data;
                    device_data = mCategory_eLight.get(mCategory_eLight.size()-1);
                    device_data.nCardType = TypeDef.CardType.eDimmerSwitch;
                    device_data.nickName = "Dimmer 01";
                    device_data.value = "50";*//*

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DIMMER, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());*/

                    categorytype = TypeDef.CategoryType.eLight;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_MAINLIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DIMMER, TypeDef.COMMAX_DEVICE_LIGHT, categorytype);
                    mCategory_eLight.addAll(readDeviceList);
                    readDeviceList.clear();
                    //Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eLight.size());

                    break;

                case 3 : //eIndoorEnv Scan
                   categorytype = TypeDef.CategoryType.eIndoorEnv;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_THERMOSTAT, TypeDef.COMMAX_DEVICE_BOILER, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_AIRCON, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_FAN, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_THERMOSTAT, TypeDef.COMMAX_DEVICE_FCU, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_CURTAIN, categorytype);
                    mCategory_eIndoorEnv.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    break;

                case 4 : //eEnergy Scan
                 categorytype = TypeDef.CategoryType.eEnergy;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_PLUG, TypeDef.COMMAX_DEVICE_PLUG, categorytype);
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                      readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_METER, TypeDef.COMMAX_DEVICE_METER, categorytype);
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_STANDBYPOWER, categorytype);
                    mCategory_eEnergy.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    break;

              /*  case 5 : //eSafe Scan
                    categorytype = TypeDef.CategoryType.eSafe;
                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_GASSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_FIRESENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_CONTACTSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_DSENSOR, TypeDef.COMMAX_DEVICE_DETECTSENSOR, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());

                    readDeviceList = dataManager.mysql_getSelSubDeviceInfoList(TypeDef.ROOT_DEVICE_SWITCH, TypeDef.COMMAX_DEVICE_AWAYSWITCH, categorytype);
                    mCategory_eSafe.addAll(readDeviceList);
                    readDeviceList.clear();
                    Log.d(TAG, " --> readAllDevices() -> size  " + mCategory_eIndoorEnv.size());
                    break;*/
                default:
                    categorytype = TypeDef.CategoryType.eNone;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

//        activity_on = true;
        if (m_rc == null) {
            try {
                m_rc = new RemoteServiceConnection();
                //Wall Pad (2016-12-06) SDK 21 이상에서는 명시적 호출이 필요
                //boolean is = bindService(new Intent(IAdapter.class.getName()), m_rc, BIND_AUTO_CREATE);

                //In Hand Pad SDK 21 이상에서 동작
                Intent pamIntent = new Intent(IAdapter.class.getName());
                pamIntent.setPackage("com.commax.pam.service");
                boolean is = bindService(pamIntent, m_rc ,BIND_AUTO_CREATE);

                if (is == true) Log.i(TAG, "[Ventilation Activity]bindService Succeed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        //TODO mysql close 시켜야 한다?
       /* if(dataManager.mysql_isConnected())
        {
            dataManager.mysql_close();
        }*/
        if(m_rc != null ){
            try {
                unbindService(m_rc);
                m_rc = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        main_stop_flag = true;
        //기기 삭제 에서 monitor Thread 를 멈추어야 한다.
        if(editView_test != null)
        {
            editView_test.m_bmonitor_start = false;
        }
        finish();
//        activity_on = false;
    }

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
    public void sendCommand(String raw){
        try{
            if(m_rc.iadpter != null){
                try {
                    int result = m_rc.iadpter.sendToPAM(raw);
                    Log.d(TAG, "result : "+result);
                } catch (RemoteException e) { //TODO error android.os.DeadObjectException
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //카테고리 분류
    public String divide_category_by_commaxdevice(String commaxDevice) {
        TypeDef.CategoryType test = TypeDef.CategoryType.eSafe;
        String category = "None";

        switch (commaxDevice) {
            case TypeDef.COMMAX_DEVICE_LIGHT:
            case TypeDef.COMMAX_DEVICE_MAINLIGHT:
                category = "Light";
                break;
            case TypeDef.COMMAX_DEVICE_BOILER:
            case TypeDef.COMMAX_DEVICE_FAN:
            case TypeDef.COMMAX_DEVICE_FCU:
            case TypeDef.COMMAX_DEVICE_CURTAIN:
            case TypeDef.COMMAX_DEVICE_DETECTSENSOR:
                category = "Indoor";
                break;
            case TypeDef.COMMAX_DEVICE_PLUG:
            case TypeDef.COMMAX_DEVICE_STANDBYPOWER:
            case TypeDef.COMMAX_DEVICE_METER:
                category = "Energy";
                break;
            case TypeDef.COMMAX_DEVICE_GASLOCK:
            case TypeDef.COMMAX_DEVICE_GASSENSOR:
            case TypeDef.COMMAX_DEVICE_MOTIONSENSOR:
            case TypeDef.COMMAX_DEVICE_FIRESENSOR:
            case TypeDef.COMMAX_DEVICE_DOORSENSOR:
            case TypeDef.COMMAX_DEVICE_WATERSENSOR:
            case TypeDef.COMMAX_DEVICE_SMOKESENSOR:
            case TypeDef.COMMAX_DEVICE_AWAYSWITCH:
                category = "Safety";
                break;
        }
        return category;
    }

    //made by dianna Kim for IP Home IoT navigation bar hide
    public void hideNavigationBar()
    {
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

    public void clearNavigationBar(){

        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            ((Activity)mContext).getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //TODO for test

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Lifecycle onActivityResult()...");

        if(resultCode == RESULT_OK)
        {
            Bundle bundle = new Bundle();
            bundle = data.getExtras();
            boolean cancel = bundle.getBoolean("ADD_CANCEL");
            Log.d(TAG, "cancel : " + cancel);
            if(cancel)
            {
                sendDeviceAddcancleCommand();
            }
        }
    }
}
