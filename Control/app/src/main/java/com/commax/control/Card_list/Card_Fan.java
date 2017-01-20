package com.commax.control.Card_list;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.control.Common.Log;
import com.commax.control.Common.TypeDef;
import com.commax.control.DeviceInfo;
import com.commax.control.MainActivity;
import com.commax.control.R;

/**
 * Created by gbg on 2016-08-03.
 */
public class Card_Fan extends LinearLayout implements View.OnClickListener {

    static final String TAG = "Card_Fan";

    static String FAN_OP_MODE[] = {"auto","manual"};
    static final int MAX_OP_MODE = 2;

    // Context
    Context mContext;

    // data
    DeviceInfo m_devicedata;

    // Handler
    Handler handler;

    // layout
    LayoutInflater inflater;
    View rootView;
    LinearLayout src_layout;

    // items
    TextView m_text_title;
    ImageView m_btn_favorite;
    //ImageView m_btn_smallpower;
    ImageView m_btn_normalpower;
    Button m_btn_opmode;
    TextView m_btn_more;
    ImageView m_icon_more;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private int m_UserEventCnt = 0; //user Event Counter
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;
    private boolean m_internal_Value = false;
    private boolean m_received_Value = false;
    private int m_internal_Opmode = 0;
    private int m_received_Opmode = 0;

    //SubDevice를 찾기위한 Index
    private int m_ID_modeBinary = -1;
    private int m_ID_fanSpeed = -1;

    //해당 디바이스 On/Off 제어값 정의
    private String MY_SWITCH_ON = TypeDef.SWITCH_ON;
    private String MY_SWITCH_OFF = TypeDef.SWITCH_OFF;

    public Card_Fan(Context context) {
        super(context);
        init(context);
    }

    public Card_Fan(Context context, DeviceInfo devicedata) {
        super(context);

        m_devicedata = devicedata;
        init(context);
    }

    public void setDeviceData(DeviceInfo devicedata) {
        m_devicedata = devicedata;
    }

    public DeviceInfo getDeviceData() {
        return m_devicedata;
    }

    public void stopDeviceAction() {
        m_bmonitor_stop = true;
    }

    /* Class initialize(private)-----------------------------*/
    private void init(Context context) {

        mContext = context.getApplicationContext();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.device_fan, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        //m_btn_smallpower = (ImageView) rootView.findViewById(R.id.btn_smallpower);
        m_btn_normalpower = (ImageView) rootView.findViewById(R.id.btn_normalpower);
        m_btn_more = (TextView) rootView.findViewById(R.id.btn_more);
        m_icon_more = (ImageView) rootView.findViewById(R.id.icon_more);
        m_btn_opmode = (Button) rootView.findViewById(R.id.btn_opmode);

        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_MID;
        m_text_title.setText(m_devicedata.getNickName());

        m_ID_modeBinary = findDeviceControllerID(TypeDef.SUB_DEVICE_MODEBINARY);
        m_ID_fanSpeed = findDeviceControllerID(TypeDef.SUB_DEVICE_FANSPEED);
        m_btn_more.setVisibility(View.INVISIBLE);
        m_icon_more.setVisibility(View.INVISIBLE);


        updateDevice();
        startMonitor();

        //OnClickListener
        m_text_title.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(m_text_title.isSelected()) {
                        m_text_title.setEllipsize(TextUtils.TruncateAt.END);
                        m_text_title.setSelected(false); //Disable
                    }else{
                        m_text_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        m_text_title.setSelected(true); //Enable
                    }
                }
                return false;
            }
        });
        m_text_title.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                MainActivity.getInstance().printDeviceInfo(m_devicedata); //for test
                return false;
            }
        });

        m_btn_favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnFavorite_Clicked(v);
                v.setAlpha(1.0F);
            }
        });
        m_btn_favorite.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.5F);
                if (event.getAction() == MotionEvent.ACTION_CANCEL) v.setAlpha(1.0F);
                return false;
            }
        });

        m_btn_normalpower.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnNormalpower_Clicked(v);
            }
        });
        m_btn_normalpower.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.5F);
                if (event.getAction() == MotionEvent.ACTION_CANCEL) v.setAlpha(1.0F);

                //Log.d(TAG, "setOnTouchListener ... " + event.getAction());

                return false;
            }
        });

        m_btn_opmode.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                BtnOpmode_Clicked(v);
            }
        });
    }

    // Card Manager Thread
    private void startMonitor() {

        if(m_bmonitor_start == false){

            //handler init
            handler = new Handler(){
                public void handleMessage(android.os.Message msg) {
                    if(msg.what == MSG_SEND_ID)  updateDevice();
                    if(msg.what == MSG_SEND_ID_TIMEOUT)  checkTimeoutValue();
                }
            };

            //start Monitor
            MonitorThread monitor_thread;
            monitor_thread = new MonitorThread();
            if(monitor_thread != null) {
                try {
                    monitor_thread.start();
                    m_bmonitor_start = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MonitorThread extends Thread {

        public void run() {
            try {
                //Log.d(TAG, "MonitorThread ... Start");

                while(true) {
                    //Exit Trread
                    if(m_bmonitor_stop) break;

                    /////////////////////////////////////
                    //Update Event와 지연처리시 화면 갱신함
                    if ( m_devicedata.isbUpdated() || (m_received_Value != getDeviceValue()) ) {

                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        //Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);

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

    /* Class Methods(private) ----------------------------------------*/
    /*
    getOtherValue
     >switchBinary
     0:modeBinary
     1:fanSpeed
    */

    private int findDeviceControllerID(String controller) {
        int index = -1;
        for(int i=0; i< m_devicedata.getOtherSubcount(); i++) {
            if (m_devicedata.getOtherSort(i).equalsIgnoreCase(controller)) {
                index = i;
                break;
            }
        }
        //Log.d(TAG, "findDeviceControllerID ... " + m_devicedata.getNickName() + " " + controller + ": " + index);

        return index;
    }

    private boolean getDeviceValue() {
        boolean getvalue = false;
        try {
            if (m_devicedata.getValue().equalsIgnoreCase(MY_SWITCH_ON)) getvalue = true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getvalue;
    }

    private int getDeviceOpmode() {
        int getvalue = 0;

        try {
            // TODO: 2016-09-02 : 아직 FAN의 기능구현 안됨
            if (m_devicedata.getOtherValue(m_ID_modeBinary).equalsIgnoreCase(TypeDef.OP_MODE_AUTO))
                getvalue = 1; //auto

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getvalue;
    }

    private int getDeviceFanSpeed() {
        int getvalue = 0;

        try {
            // TODO: 2016-09-02 : 아직 FAN의 기능구현 안됨
            //m_devicedata.getOtherValue(m_ID_fanSpeed)

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getvalue;
    }

    private void controlDevice(String value) {
        //Timer 등록
        pushTimeoutValue();
        handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                m_devicedata.getRootUuid(),
                m_devicedata.getRootDevice(),
                m_devicedata.getSubUuid(),
                value,
                m_devicedata.getSort()
        );
        MainActivity.getInstance().markGroupValue(m_devicedata.getnCategory(), m_devicedata.getGroupID());
    }

    private void controlDevice_FanMode(String value) {
        //Timer 등록
        pushTimeoutValue();
        handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
        if (m_ID_modeBinary == -1) return;
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                m_devicedata.getRootUuid(),
                m_devicedata.getRootDevice(),
                m_devicedata.getOtherSubUuid(m_ID_modeBinary),
                value,
                m_devicedata.getOtherSort(m_ID_modeBinary)
        );
    }

    private void pushTimeoutValue() {
        SEND_CMD_COUNTER++;
    }

    private boolean checkTimeoutValue() {
        boolean getState = false;

        SEND_CMD_COUNTER--;
        if(SEND_CMD_COUNTER == 0)
        {
            if (m_internal_Value == m_received_Value) {
                //if(m_internal_Opmode == m_received_Opmode)
                getState = true;
            } else {
                String message = m_devicedata.getNickName() + " " + getResources().getString(R.string.message_control_fail);
                MainActivity.getInstance().showToastBox(message,false);
            }
            updateDevice();
            m_UserEventCnt = 0;
        }
        return getState;
    }

    private void updateDevice() {

        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() + "-" + m_devicedata.getValue()+ "-" + m_devicedata.getOtherValue(0));

        m_received_Value = getDeviceValue();
        m_received_Opmode = getDeviceOpmode();
        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_normalpower.setSelected(getDeviceValue());
        m_btn_favorite.setAlpha(1.0F);
        m_btn_normalpower.setAlpha(1.0F);
        if(m_UserEventCnt == 0) { //사용자에 의한 업데이트가 아닐경우 바로 업데이트함
            m_internal_Value = m_received_Value;
        }
        if(m_internal_Value == m_received_Value) {
            m_UserEventCnt = 0;
        }

        m_btn_opmode.setSelected(!m_received_Value);
        m_btn_opmode.setEnabled(m_received_Value);
        if(m_received_Opmode == 0) { //manual
            m_btn_opmode.setText(getResources().getString(R.string.card_fan_mode_manual));
        } else {
            m_btn_opmode.setText(getResources().getString(R.string.card_fan_mode_auto));
        }
        m_devicedata.setbUpdated(false);

        //Log.d(TAG, "updateDevice ... OK " + m_devicedata.getNickName() + "-" + m_received_Value);
    }

    /* Class Methods(event) -----------------------------*/
    public void BtnFavorite_Clicked(View v)
    {
        //Log.d(TAG, "Btnfavorite_Clicked()");

        if(v.isSelected()){
            m_devicedata.setFavorite(false);
            v.setSelected(false);
            MainActivity.getInstance().dataManager.deleteFavoriteFile(m_devicedata.getSubUuid(),false);
        }
        else{
            m_devicedata.setFavorite(true);
            v.setSelected(true);
            MainActivity.getInstance().dataManager.addFavoriteFile(m_devicedata.getSubUuid(), m_devicedata.getNickName());
        }
    }

    public void BtnNormalpower_Clicked(View v)
    {
        //Toast.makeText(super.getContext() , "button pressed",Toast.LENGTH_LONG).show();
        //Log.d(TAG, "BtnNormalpower_Clicked()");

        if(v.isSelected()){
            Log.d(TAG, "isSelected ... 0");
            m_internal_Value = false;
            v.setSelected(false); //fake
            m_btn_opmode.setSelected(true); //fake disable
            controlDevice(MY_SWITCH_OFF);
        }
        else{
            Log.d(TAG, "isSelected ... 1");
            m_internal_Value = true;
            v.setSelected(true); //fake
            m_btn_opmode.setSelected(false); //fake Enable
            controlDevice(MY_SWITCH_ON);
        }
        m_UserEventCnt++;
    }

    public void BtnOpmode_Clicked(View v)
    {
        //Log.d(TAG, "BtnOpmode_Clicked()");

        if(m_btn_normalpower.isSelected()) {
            if (m_received_Opmode == 0) {
                m_btn_opmode.setText(getResources().getText(R.string.card_fan_mode_auto));
                controlDevice_FanMode(TypeDef.OP_MODE_AUTO);
                m_internal_Opmode = 1; //auto
            }else {
                m_btn_opmode.setText(getResources().getText(R.string.card_fan_mode_manual));
                controlDevice_FanMode(TypeDef.OP_MODE_MANUAL);
                m_internal_Opmode = 0; //manual
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}