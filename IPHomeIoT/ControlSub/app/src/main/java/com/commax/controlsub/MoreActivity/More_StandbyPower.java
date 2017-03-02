package com.commax.controlsub.MoreActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.DeviceInfo;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

/**
 * Created by OWNER on 2016-08-25.
 */
public class More_StandbyPower extends LinearLayout implements View.OnClickListener{
    String TAG = More_StandbyPower.class.getSimpleName();
    Custom_standy_cutoff_dialog cutoff_dialog;

    Context mContext;
    DeviceInfo deviceInfo;
    // Handler
    Handler handler;
    //ui
    TextView title_device;
    TextView data_number;
    TextView data_scale;
    TextView readypower_text;
    ImageView title_power_button;
    ImageView title_back_button;
    Button auto_binary_button;
    Button manual_binary_button;
    Button cutoff_button;
    // layout
    LayoutInflater inflater;
    View rootView;
    LinearLayout src_layout;

    int MSG_SEND_ID;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)

    boolean m_bmonitor_start = false;
    private boolean m_received_Value = false;

    // control fail check timer task
    public Timer_count_Task mTimerCountTask;
    Thread mTimerCountThread;
    public boolean timer_count = false;
    private static final int CONTROL_OPTIONS_TIMEOUT = 5;


     /*
    *  value : on/ off  , value2 : data number , value3 : cutoff number , value4 : auto / manual
    *
    * */

    public More_StandbyPower(Context context) {
        super(context);
//        init(context);
    }

    public More_StandbyPower(Context context, DeviceInfo devicedata) {
        super(context);

        deviceInfo = devicedata;
        init(context);
    }
    public void setDeviceData(DeviceInfo devicedata) {
        deviceInfo = devicedata;
    }

    void init(Context context)
    {
        Log.d(TAG, "init : deviceinfo " + deviceInfo);
        mContext = context.getApplicationContext();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.more_standbypower, this); // from : 가져올 layout 지정
        //title
        title_device = (TextView)findViewById(R.id.title_text);
        title_power_button = (ImageView)findViewById(R.id.title_power_button);
        title_back_button = (ImageView)findViewById(R.id.title_back_button);
        //component
        data_number = (TextView)findViewById(R.id.data_number);
        data_scale = (TextView)findViewById(R.id.data_scale);
        readypower_text = (TextView)findViewById(R.id.readypower_text);
        //button
        auto_binary_button = (Button)findViewById(R.id.auto_button);
        manual_binary_button = (Button)findViewById(R.id.manual_button);
        cutoff_button = (Button)findViewById(R.id.cutoff_button);

        title_back_button.setOnClickListener(this);
        title_power_button.setOnClickListener(this);
        manual_binary_button.setOnClickListener(this);
        auto_binary_button.setOnClickListener(this);
        cutoff_button.setOnClickListener(this);

        MSG_SEND_ID = deviceInfo.getnLayoutID();
        Log.d(TAG, " MSG_SEDN_ID : " + MSG_SEND_ID);

        MY_PRIORITY = TypeDef.PRIORITY_MID;

        if(mTimerCountTask == null){
            mTimerCountTask = new Timer_count_Task();
        }

        updateDevice();
        startMonitor();
    }

    public void updateDevice()
    {
        m_received_Value = getDeviceValue();
        title_device.setText(deviceInfo.getNickName());
        cutoff_button.setText(deviceInfo.getValue3() + "w");
        Log.i(TAG , "getValue2 = " + deviceInfo.getValue2());

        if(deviceInfo.getValue4().equalsIgnoreCase(TypeDef.OP_MODE_MANUAL))
        {
            Log.d(TAG, "getValue4 = manual");
            manual_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
            auto_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
            manual_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
            auto_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));

        }else if (deviceInfo.getValue4().equals(TypeDef.OP_MODE_AUTO))
        {
            Log.d(TAG, "getValue4 = auto");
            manual_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
            auto_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
            manual_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
            auto_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
        }

        if(deviceInfo.getValue().equals(TypeDef.SWITCH_ON))
        {
            title_power_button.setSelected(true);
            data_number.setText(deviceInfo.getValue2());
            screenStateByPower(true);

        }
        else if(deviceInfo.getValue().equals(TypeDef.SWITCH_OFF))
        {
            title_power_button.setSelected(false);
            data_number.setText(deviceInfo.getValue2());
            screenStateByPower(false);
        }

        deviceInfo.setbUpdated(false);
    }

    public void onClick(View v) {
        String setting_on_off = null;
        String setting_auto_manual = null;

        if(v.equals(title_back_button))
        {
            if(mTimerCountTask != null){
                mTimerCountTask.stopTimer();
                mTimerCountTask = null;
            }

            MainActivity.getInstance().finish();
        }
        else if(v.equals(title_power_button))
        {
            Log.d(TAG,"click power button");
            if(deviceInfo.getValue().equals(TypeDef.SWITCH_OFF))
            {
                title_power_button.setSelected(true);
                setting_on_off = TypeDef.SWITCH_ON;
                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid() + " value : " + setting_on_off);
                controlDevice(setting_on_off, deviceInfo.getSubUuid() , deviceInfo.getSort());
            }
            else if(deviceInfo.getValue().equals(TypeDef.SWITCH_ON))
            {
                title_power_button.setSelected(false);
                setting_on_off = TypeDef.SWITCH_OFF;
                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid() + " value : " + setting_on_off);
                controlDevice(setting_on_off, deviceInfo.getSubUuid() , deviceInfo.getSort());
            }
            else
            {
                Log.e(TAG, "else setting_on_off : " + setting_on_off);
            }
        }

       /* else if(v.equals(manual_binary_button))
        {
            // on, off 상태 전환
            try {
                if (!v.isSelected()) {
                    setting_auto_manual = TypeDef.OP_MODE_MANUAL;
                    manual_binary_button.setSelected(true);
                    auto_binary_button.setSelected(false);
                    //TODO manual /auto 갱신하는 거 확인해복;
                    Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid4());
                    controlDevice(setting_auto_manual, deviceInfo.getSubUuid4(), TypeDef.SUB_DEVICE_MODEBINARY );
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(v.equals(auto_binary_button))
        {
            // on, off 상태 전환
            try {
                if (!v.isSelected()) {
                    Log.d(TAG, "clicked MANUAL");
                    setting_auto_manual = TypeDef.OP_MODE_AUTO;
                    auto_binary_button.setSelected(true);
                    manual_binary_button.setSelected(false);
                    Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid4());
                    controlDevice(setting_auto_manual, deviceInfo.getSubUuid4() , TypeDef.SUB_DEVICE_MODEBINARY);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }*/
        else if(v.equals(manual_binary_button))
        {
            // on, off 상태 전환
            try {
                if (deviceInfo.getValue4().equals(TypeDef.OP_MODE_AUTO)){
                    Log.d(TAG, "clicked MANUAL");
                    setting_auto_manual = TypeDef.OP_MODE_MANUAL;
                    manual_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                    auto_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                    manual_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                    auto_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                    //TODO manual /auto 갱신하는 거 확인해복;
                    Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid4());
                    controlDevice(setting_auto_manual, deviceInfo.getSubUuid4(), TypeDef.SUB_DEVICE_MODEBINARY );
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(v.equals(auto_binary_button))
        {
            // on, off 상태 전환
            try {
                if (deviceInfo.getValue4().equals(TypeDef.OP_MODE_MANUAL)) {
                    Log.d(TAG, "clicked AUTO");
                    setting_auto_manual = TypeDef.OP_MODE_AUTO;
                    manual_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                    auto_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                    manual_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                    auto_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                    Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid4());
                    controlDevice(setting_auto_manual, deviceInfo.getSubUuid4() , TypeDef.SUB_DEVICE_MODEBINARY);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else if(v.equals(cutoff_button))
        {
            try {
                cutoff_dialog = new Custom_standy_cutoff_dialog(MainActivity.getInstance().mContext , deviceInfo);
                cutoff_dialog.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try {
            mTimerCountTask.stopTimer();
            mTimerCountTask.startThread();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void screenStateByPower(boolean bpower) {
        if(bpower) {
            //switch on
            manual_binary_button.setEnabled(true);
            auto_binary_button.setEnabled(true);
            // ready power value and scale
            data_number.setTextColor(getResources().getColorStateList(R.color.data_number_text));
            data_scale.setTextColor(getResources().getColorStateList(R.color.data_number_text));
        }else {
            //switch off
            manual_binary_button.setEnabled(false);
            auto_binary_button.setEnabled(false);
            data_number.setTextColor(getResources().getColorStateList(R.color.data_number_text_disable));
            data_scale.setTextColor(getResources().getColorStateList(R.color.data_number_text_disable));
        }
    }

    public void controlDevice(String value , String SubUuid , String sort) {
        Log.d(TAG, "controlDevice");
        //AIDL 을 통한 Device 제어
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                deviceInfo.getRootUuid(),
                deviceInfo.getRootDevice(),
                SubUuid,
                value,
                sort
        );
    }
    private void startMonitor() {

        if(m_bmonitor_start == false){

            //handler init
            handler = new Handler(){

                public void handleMessage(android.os.Message msg) {
                    if(msg.what == MSG_SEND_ID)
                    {
//                        getData(deviceInfo);
                        updateDevice();
                    }
                }
            };

            //start Monitor
            MonitorThread monitor_thread;
            monitor_thread = new MonitorThread();
            if(monitor_thread != null) {
                try {
                    monitor_thread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* Class Methods(public) ----------------------------------------*/
    public boolean getDeviceValue() {
        boolean getvalue = false;

        if (deviceInfo.getValue().equalsIgnoreCase(TypeDef.SWITCH_ON))
            getvalue = true;
        else if(deviceInfo.getValue().equalsIgnoreCase(TypeDef.SWITCH_OFF))
            getvalue = false;

        return getvalue;
    }

    class MonitorThread extends Thread {
        public void run() {
            try {
                Log.d(TAG, "MonitorThread ... Start");

                while(true) {
                    //Update Event와 지연처리시 화면 갱신함
                    if ( deviceInfo.isbUpdated() || (m_received_Value !=  getDeviceValue()) ) {

                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);

                        handler.sendEmptyMessage(MSG_SEND_ID);

                        Thread.sleep(TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY]);
                    }else
                    {
                        Thread.sleep(TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //TODO  exception 처리
    Handler timercountHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            if(msg.what == 1)
            {
                Log.d(TAG," toast :  " + msg.obj);
                MainActivity.getInstance().showToastOnWorking((String) msg.obj);
            }
            else if(msg.what ==2)
            {
                Log.d(TAG, "not controled so UI update");
                updateDevice();
            }

           /* *//** 초시간을 잰다 *//*
            int div = msg.what;
            android.util.Log.d(TAG, "mainTime = " + div);

            if(div == 0 || div < 0)
            {
                Log.d(TAG, "mainTime 0  = " + div);


            }else
            {
                android.util.Log.d(TAG, "mainTime = " + div);
                this.sendEmptyMessageDelayed(0, 1000);
            }
            div--;*/
        }
    };


    public class Timer_count_Task implements Runnable{

        boolean timerFlag = false;
        int count = 0;


        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(timerFlag){

                if(count == 500){

                    if(timer_count == true) {
                        //TODO 업데이트가 되어 오면 true로 변경경
                        break;
                    }
                    else if(timer_count ==false)
                    {
                       /* MainActivity.getInstance().showToastOnWorking("제어가 되지 않았습니다.");*/

                        //timer
                        Message msg = timercountHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = getResources().getString(R.string.not_controled);//"제어가 동작하지 않았습니다.";
                        timercountHandler.sendMessage(msg);
                        count  = 0;

                        Message msg1 = timercountHandler.obtainMessage();
                        msg1.what = 2;
                        timercountHandler.sendMessage(msg1);

//                        deviceUIsetting();
                        break;
                    }
                }

                count++;
//                com.commax.controlsub.Common.Log.d(TAG,"count : " + count);

                try {
                    Thread.sleep(CONTROL_OPTIONS_TIMEOUT);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }

        public void startThread(){
            timerFlag = true;
            count = 0;
            mTimerCountThread = new Thread(mTimerCountTask);
            mTimerCountThread.start();
        }

        public void stopTimer(){

            count = 0;
            timerFlag = false;


            if(mTimerCountThread != null){

                try {
                    mTimerCountThread.interrupt();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            mTimerCountThread = null;

        }
    }



}
