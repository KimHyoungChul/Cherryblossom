package com.commax.control.Card_list;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.control.Common.CircularSeekBar;
import com.commax.control.Common.Log;
import com.commax.control.Common.TypeDef;
import com.commax.control.DeviceInfo;
import com.commax.control.MainActivity;
import com.commax.control.R;

/**
 * Created by gbg on 2016-08-03.
 */
public class Card_FCU extends LinearLayout implements View.OnClickListener {

    /****** 개발자 노트 *************
     *문제점: 사용자의 제어속도에 비해 제어속도 늦을경우 화면지연 업데이트 문제 발생함(특히, 레벨값 조절시)
     *증상 : 제어시 지연처리로 인한 Sync 문제로 화면의 설정값이 요동함
     * 대응안 : 화면 업데이트 예외처리 추가함
     1. 사용자 제어 -> 제어값 화면에 선 적용
     2. Send Control Command(ftn : controlDevice)
     3. Receive Report -> Set Update Flag(MainActivity : updateReport)
     4. Indicate update from Card Manager -> Send message for update(ftn : MonitorThread)
     5. 예외처리 추가 : Update를 인지하더라고 화면 업데이트 대기함(제어버튼을 누르고 있을때 또는 업데이트 레포트가 늦게올때)
     6. Wait Complete or Timeout(m_UpdatePostCnt) -> 화면 업데이트함(ftn : updateDevice)
     */

    static final String TAG = "Card_FCU";

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
    ImageView m_btn_smallpower;
    //ImageView m_btn_normalpower;
    CircularSeekBar m_seekbar_level;
    ImageView m_btn_levelup;
    ImageView m_btn_leveldown;
    ImageView m_icon_fcuopmode;
    ImageView m_icon2_fcuopmode;

    TextView m_text_settempature;
    TextView m_text_curtempature;
    TextView m_text_settempature_unit1;
    TextView m_text_settempature_unit2;
    TextView m_text_curtemperature_unit3;
    TextView m_btn_more;
    ImageView m_icon_more;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;
    private boolean m_internal_Value = false;
    private boolean m_received_Value = false;
    private int m_received_airTemp = 0;
    private int m_received_OPMode = 0;
    private boolean m_received_AwayMode = false;
    private int m_received_setCoolPoint = 0;
    private int m_received_setHeatPoint = 0;
    private int m_internal_setPoint = 0;
    private int m_received_setPoint = 0;
    private String m_LastSetttingValue = "";

    //Level Min,Max 정의
    private int m_LEVEL_MIN = 0;
    private int m_LEVEL_MAX = 0;
    private int m_LEVEL_RANGE = 0;

    //화면 업데이트 Sync Time 제어 변수
    private int m_Checktimems = 0; //누적 Timer 값
    private int m_Needtimems = 0; //사용자 정의 Timeout 값
    private int m_RepeatBtnType = 0; //Repeat버튼 Type(up,down)
    private int m_RepeatBtnCnt = 0; //Repeat버튼 Counter
    private int m_UserEventCnt = 0; //user Event Counter
    private int m_UpdatePostCnt = 0; //제어 중일때 대기시간 Counter
    private int m_UpdatePostTimeoutCnt = 0; //제어 중일때 대기시간 Counter timeout

    //SubDevice를 찾기위한 Index
    private int m_ID_thermostatMode = -1;
    private int m_ID_thermostatAwayMode = -1;
    private int m_ID_thermostatRunMode = -1;
    private int m_ID_thermostatSetCoolpoint = -1;
    private int m_ID_thermostatSetHeatpoint = -1;

    public Card_FCU(Context context) {
        super(context);
        init(context);
    }

    public Card_FCU(Context context, DeviceInfo devicedata) {
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
        //Log.d(TAG, "MonitorThread ... Stop");
        m_bmonitor_stop = true;
    }

    /* Class initialize(private)-----------------------------*/
    private void init(Context context) {

        mContext = context.getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.device_fcu, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        m_btn_smallpower = (ImageView) rootView.findViewById(R.id.btn_smallpower);
        //btn_normalpower = (ImageView) rootView.findViewById(R.id.btn_normalpower);
        m_text_settempature = (TextView) rootView.findViewById(R.id.text_settemperature);
        m_text_settempature_unit1 = (TextView) rootView.findViewById(R.id.text_settemperature_unit1);
        m_text_settempature_unit2 = (TextView) rootView.findViewById(R.id.text_settemperature_unit2);
        m_text_curtemperature_unit3 = (TextView) rootView.findViewById(R.id.text_curtemperature_unit3);
        m_text_curtempature = (TextView) rootView.findViewById(R.id.text_curtemperature);
        m_icon_fcuopmode = (ImageView) rootView.findViewById(R.id.icon_opmode);
        m_icon2_fcuopmode = (ImageView) rootView.findViewById(R.id.icon2_opmode);

        m_btn_more = (TextView) rootView.findViewById(R.id.btn_more);
        m_icon_more = (ImageView) rootView.findViewById(R.id.icon_more);
        m_btn_levelup = (ImageView) rootView.findViewById(R.id.btn_levelup);
        m_btn_leveldown = (ImageView) rootView.findViewById(R.id.btn_leveldown);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_MID;
        m_text_title.setText(m_devicedata.getNickName());

        m_ID_thermostatMode = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATMODE);
        m_ID_thermostatAwayMode = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATAWAYMODE);
        m_ID_thermostatRunMode = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATRUNMODE);
        m_ID_thermostatSetCoolpoint = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATSETCOLLINGPOINT);
        m_ID_thermostatSetHeatpoint = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATSETHEATINGPOINT);

        m_received_OPMode = getDevicethermostatMode();
        m_received_AwayMode = getDeviceAwayMode();
        m_UpdatePostTimeoutCnt = TypeDef.DEVICE_POSTUPDATE_TIME_MS/TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY];

        //seekbar
        initProgressBar();

        updateDevice();
        startMonitor();

        //set OnClickListener
        //src_layout.setOnClickListener(this);

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

        m_btn_smallpower.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnSmallpower_Clicked(v);
            }
        });
        m_btn_smallpower.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.5F);
                if (event.getAction() == MotionEvent.ACTION_CANCEL) v.setAlpha(1.0F);
                return false;
            }
        });

        m_btn_more.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BtnMore_Clicked(v);
            }
        });
        m_btn_more.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) m_icon_more.setAlpha(0.6F);
                if (event.getAction() == MotionEvent.ACTION_UP)   m_icon_more.setAlpha(1.0F);
                return false;
            }
        });

        m_btn_levelup.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BtnLevelup_Clicked(v,true); //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
            }
        });

        m_btn_leveldown.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BtnLeveldown_Clicked(v,true); //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
            }
        });

        m_btn_levelup.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Press시 처리
                    m_Checktimems = 0;
                    m_Needtimems = TypeDef.DEVICE_LONGPRESS_TIME_MS; //setup time
                    m_RepeatBtnCnt = 0;
                    m_RepeatBtnType = 1;
                    m_UpdatePostCnt = 0; //wait event
                    //BtnLevelup_Clicked(v,true); //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    m_RepeatBtnType = 0; //stop
                    if(m_RepeatBtnCnt > 0) {
                        //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
                        //controlDevice_Setpoint(Integer.toString(getProgressValue())); //btn release시 제어하게끔 고려하자
                    }
                }
                return false;
            }
        });

        m_btn_leveldown.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Press시 처리
                    m_Checktimems = 0;
                    m_Needtimems = TypeDef.DEVICE_LONGPRESS_TIME_MS; //setup time
                    m_RepeatBtnCnt = 0;
                    m_RepeatBtnType = 2;
                    m_UpdatePostCnt = 0; //wait event
                    //BtnLeveldown_Clicked(v,true); //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
                }
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    m_RepeatBtnType = 0; //stop
                    if(m_RepeatBtnCnt > 0) {
                        //2016-10-24,yslee: btn release시 한번만 제어하도록 수정
                        //controlDevice_Setpoint(Integer.toString(getProgressValue())); //btn release시 제어하게끔 고려하자
                    }
                }
                return false;
            }
        });

        m_seekbar_level.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener(){
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                //Log.d(TAG, "onProgressChanged..." + progress + "-" + fromUser);
                if(fromUser == true) SeekBar_Clicked(progress, false);
            }
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                //Log.d(TAG, "onStopTrackingTouch...");
                SeekBar_Clicked(m_internal_setPoint, true); //Press/Release일때만 제어함
            }
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                //Log.d(TAG, "onStartTrackingTouch...");
                SeekBar_Clicked(m_internal_setPoint, true); //Press/Release일때만 제어함
            }
        });
    }

    private void initProgressBar() {

        //seekbar
        m_seekbar_level = (CircularSeekBar) findViewById(R.id.circularSeekBar_control);
        initProgressRange(m_received_OPMode);
        m_seekbar_level.setIsTouchEnabled(TypeDef.OPT_SEEKBAR_TOUCH_ENABLE);//필요시 Touch Disable
        if(!TypeDef.OPT_SEEKBAR_TOUCH_ENABLE) {
            m_seekbar_level.setPointerAlpha(0); //default:100
            m_seekbar_level.setIsPointEnabled(false);
        }
    }

    private void initProgressRange(int mode) {

        //init progress range
        if( (mode == 2) || (m_received_AwayMode == true) ) {
            //ventilation or awayon
            m_LEVEL_MIN = 0;
            m_LEVEL_MAX = 10;
            m_seekbar_level.setCircleProgressGradientColor(0xffc9c8d6, 0x88c9c8d6); //disable
            m_icon_fcuopmode.setVisibility(View.INVISIBLE);
        } else {
            if (mode == 0) { //cool
                m_LEVEL_MIN = TypeDef.DEF_COOL_MIN_MAX[0];
                m_LEVEL_MAX = TypeDef.DEF_COOL_MIN_MAX[1];
                m_seekbar_level.setCircleProgressGradientColor(0xff433fa3, 0xff4ec3e5); //Cool
                m_icon_fcuopmode.setVisibility(View.VISIBLE);
                m_icon_fcuopmode.setSelected(false);
            } else if (mode == 1) { //heat
                m_LEVEL_MIN = TypeDef.DEF_HEAT_MIN_MAX[0];
                m_LEVEL_MAX = TypeDef.DEF_HEAT_MIN_MAX[1];
                m_seekbar_level.setCircleProgressGradientColor(0xffe02f38, 0xfff8b54c); //Warm
                m_icon_fcuopmode.setVisibility(View.VISIBLE);
                m_icon_fcuopmode.setSelected(true);
            }
            if (!TextUtils.isEmpty(m_devicedata.getOption1()))
                m_LEVEL_MIN = Integer.parseInt(m_devicedata.getOption1());
            if (!TextUtils.isEmpty(m_devicedata.getOption2()))
                m_LEVEL_MAX = Integer.parseInt(m_devicedata.getOption2());
        }

        m_LEVEL_RANGE = m_LEVEL_MAX - m_LEVEL_MIN;
        m_seekbar_level.setMax(m_LEVEL_RANGE);

        //Log.d(TAG, "Seekbar range is " + m_LEVEL_RANGE + ": " + m_LEVEL_MIN + "," + m_LEVEL_MAX);
    }

    private int getProgressValue() {
        int value = m_seekbar_level.getProgress() + m_LEVEL_MIN;
        if(value > m_LEVEL_MAX ) value = m_LEVEL_MAX;
        //Log.d(TAG, "getProgressValue: " + m_seekbar_level.getProgress() + "+" + m_LEVEL_MIN + "=" + value);
        //Log.d(TAG, "getProgressValue: " + m_devicedata.getNickName() + "->" + value);

        return value;
    }

    private int setProgressValue(int progress) {
        int value = progress - m_LEVEL_MIN;
        if(value < 0 ) value = 0;
        m_seekbar_level.setProgress(value);
        //Log.d(TAG, "setProgressValue: " +  m_devicedata.getNickName() + " " + progress + "->" + value);
        return value;
    }

    // Card Manager Thread start
    private void startMonitor() {

        if(m_bmonitor_start == false){

            //handler init
            handler = new Handler(){

                public void handleMessage(android.os.Message msg) {
                    if(msg.what == MSG_SEND_ID)  updateDevice();
                    if(msg.what == MSG_SEND_ID + 1001) BtnLevelup_Clicked(null,false);
                    if(msg.what == MSG_SEND_ID + 1002) BtnLeveldown_Clicked(null,false);
                    if(msg.what == MSG_SEND_ID_TIMEOUT) checkTimeoutValue();
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

    // Card Manager Thread
    class MonitorThread extends Thread {
        public void run() {
            try {
                //Log.d(TAG, "MonitorThread ... Start");

                while(true) {

                    //Exit Trread
                    if(m_bmonitor_stop) break;

                    //Update Event와 지연처리시 화면 갱신함
                    if(isNeedtoupdate()) {

                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        //Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);
                        handler.sendEmptyMessage(MSG_SEND_ID);

                        Thread.sleep(TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY]);
                        m_Checktimems += TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY];
                    }else
                    {
                        Thread.sleep(TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY]);
                        m_Checktimems += TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY];
                    }

                    /////////////////////////////////////
                    //버튼 Repeat event처리
                    if(m_RepeatBtnType > 0) {
                        if (m_Checktimems > m_Needtimems){
                            if(m_RepeatBtnType == 1){ //up
                                handler.sendEmptyMessage(MSG_SEND_ID+1001);
                            }else{
                                handler.sendEmptyMessage(MSG_SEND_ID+1002);
                            }
                            m_Checktimems = 0;
                            m_Needtimems = 1000/m_LEVEL_RANGE; //repeat period
                            m_RepeatBtnCnt++;
                        }
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
    > airTemperature
    0:thermostatMode
    1:thermostatAwayMode
    2:thermostatRunMode
    3:thermostatSetCoolingpoint
    4:thermostatSetHeatingpoint
    */
    // TODO: 2016-09-07 : 외출설정 고려 필요함! 

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
            if (m_ID_thermostatMode != -1) {
                if (m_devicedata.getOtherValue(m_ID_thermostatMode).equalsIgnoreCase(TypeDef.SWITCH_ON))
                    getvalue = true; //thermostatMode
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getvalue;
    }

    private int getDeviceairTemperature() {
        int getvalue = 0;

        try {
            getvalue = Integer.parseInt(m_devicedata.getValue());
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //Log.d(TAG, "getDeviceTemperature ... " + getvalue);

        return getvalue;
    }

    private boolean getDeviceAwayMode() {
        boolean getvalue = false;
        try {
            if (m_ID_thermostatMode != -1) {
                if (m_devicedata.getOtherValue(m_ID_thermostatAwayMode).equalsIgnoreCase(TypeDef.SWITCH_AWAYON))
                    getvalue = true; //thermostatAwayMode
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //Log.d(TAG, "getDeviceAwayMode ... " + getvalue);

        return getvalue;
    }

    private int getDevicethermostatMode() {
        int getvalue = 0; //cool

        try {
            if (m_ID_thermostatRunMode != -1) { //thermostatMode
                if (m_devicedata.getOtherValue(m_ID_thermostatRunMode).equalsIgnoreCase(TypeDef.FCU_MODE_HEAT)) {
                    getvalue = 1; //heat
                } else if (m_devicedata.getOtherValue(m_ID_thermostatRunMode).equalsIgnoreCase(TypeDef.FCU_MODE_VENTILATION)){
                    getvalue = 2; //ventilation
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        //Log.d(TAG, "getDevicethermostatMode ... " + getvalue);
        return getvalue;
    }

    private int getDevicethermostatSetpoint(int mode) {
        int getvalue = 0;

        try {
            if ((m_ID_thermostatSetCoolpoint != -1) && (m_ID_thermostatSetHeatpoint != -1)) {
                if (mode == 0) { //cool
                    getvalue = Integer.parseInt(m_devicedata.getOtherValue(m_ID_thermostatSetCoolpoint));
                } else { //heat
                    getvalue = Integer.parseInt(m_devicedata.getOtherValue(m_ID_thermostatSetHeatpoint));
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        //Log.d(TAG, "getDevicethermostatSetpoint ... " + getvalue);
        return getvalue;
    }

    private int getSetpointValue(int mode) {
        int getvalue = 0;
        switch(mode)
        {
            case 0: getvalue = m_received_setCoolPoint; //cool
                break;
            case 1: getvalue = m_received_setHeatPoint; //heat
                break;
            default: getvalue = 0;
                break;
        }

        //Log.d(TAG, "getSetpointValue ... " + getvalue);
        return getvalue;
    }

    private void controlDevice(String value) {
        //Timer 등록
        pushTimeoutValue(m_ID_thermostatMode);
        handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
        if (m_ID_thermostatMode == -1) return;
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                m_devicedata.getRootUuid(),
                m_devicedata.getRootDevice(),
                m_devicedata.getOtherSubUuid(m_ID_thermostatMode),
                value,
                m_devicedata.getOtherSort(m_ID_thermostatMode)
        );
        MainActivity.getInstance().markGroupValue(m_devicedata.getnCategory(), m_devicedata.getGroupID());
    }

    private void controlDevice_Setpoint(String value) {
        int control_id = 0;

        if(m_received_OPMode == 0) //cool
            control_id = m_ID_thermostatSetCoolpoint;
        else //heat
            control_id = m_ID_thermostatSetHeatpoint;

        //Timer 등록
        pushTimeoutValue(control_id);
        handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
        if (control_id == -1) return;
        if (m_received_OPMode > 1) return;
        if(!value.equalsIgnoreCase(m_LastSetttingValue)) {
            m_LastSetttingValue = value;
            MainActivity.getInstance().dataManager.setOnOrOffAddSort( //2:thermostatSetpoint
                m_devicedata.getRootUuid(),
                m_devicedata.getRootDevice(),
                m_devicedata.getOtherSubUuid(control_id),
                value,
                m_devicedata.getOtherSort(control_id)
            );
        }
    }

    private void pushTimeoutValue(int controllerID) {
        //제어요청 이외의 디바이스들 상태는 최신으로 업데이트함
        if(controllerID == m_ID_thermostatMode) {
            m_internal_setPoint = m_received_setPoint;
        } else {
            m_internal_Value = m_received_Value;
        }
        SEND_CMD_COUNTER++;
    }

    private boolean checkTimeoutValue() {
        boolean getState = false;

        SEND_CMD_COUNTER--;
        if(SEND_CMD_COUNTER == 0)
        {
            if( (m_internal_Value == m_received_Value)
                    && (m_internal_setPoint == m_received_setPoint) ){
                getState = true;
            } else {
                String message = m_devicedata.getNickName() + " " + getResources().getString(R.string.message_control_fail);
                MainActivity.getInstance().showToastBox(message,false);
            }
            //Log.d(TAG, "checkTimeoutValue ... " + getState + "->" + m_internal_Value + "/" + m_received_Value);
            //Log.d(TAG, "checkTimeoutValue ... " + getState + "->" +  m_internal_setPoint + "/" + m_received_setPoint);
            updateDevice();
        }
        return getState;
    }

    private void screenStateByPower(boolean bpower) {
        m_btn_favorite.setAlpha(1.0F);
        m_btn_smallpower.setAlpha(1.0F);
        initProgressRange(m_received_OPMode);

        if( (m_received_OPMode == 2) || (m_received_AwayMode == true) ) {
            //ventilation or awayon
            m_icon_fcuopmode.setVisibility(View.INVISIBLE);
            m_icon2_fcuopmode.setVisibility(View.VISIBLE);
            if(m_received_AwayMode)  m_icon2_fcuopmode.setImageResource(R.mipmap.ic_dc_outdoor1);
            else                     m_icon2_fcuopmode.setImageResource(R.mipmap.ic_dc_sethome_ventilation1);
            m_text_settempature.setVisibility(View.INVISIBLE);
            m_text_settempature_unit1.setVisibility(View.GONE);
            m_text_settempature_unit2.setVisibility(View.GONE);
            m_seekbar_level.setCircleProgressGradientColor(0xffc9c8d6, 0x88c9c8d6); //disable
            m_seekbar_level.setPointerColor(0xffc9c8d6); //default : 0xff6a71cc
            m_seekbar_level.setPointerAlpha(0); //default:100
            m_seekbar_level.setIsTouchEnabled(false);
            m_btn_levelup.setSelected(true); //disable
            m_btn_leveldown.setSelected(true); //disable
            m_btn_levelup.setEnabled(false); //Touch disable
            m_btn_leveldown.setEnabled(false); //Touch disable
            if (bpower) {
                m_icon2_fcuopmode.setImageAlpha(255);
            } else {
                m_icon2_fcuopmode.setImageAlpha(100);
            }
        } else {
            //cool or heat
            m_icon_fcuopmode.setVisibility(View.VISIBLE);
            m_icon2_fcuopmode.setVisibility(View.INVISIBLE);
            m_text_settempature.setVisibility(View.VISIBLE);
            m_text_settempature_unit1.setVisibility(View.VISIBLE);
            m_text_settempature_unit2.setVisibility(View.VISIBLE);
            if (bpower) {
                m_text_settempature.setTextColor(0xff312f62);
                m_text_settempature_unit1.setTextColor(0xff312f62);
                m_text_settempature_unit2.setTextColor(0xff312f62);
                m_icon_fcuopmode.setImageAlpha(255);
                m_seekbar_level.setPointerColor(0xff6a71cc); //default : 0xff6a71cc
                if(TypeDef.OPT_SEEKBAR_TOUCH_ENABLE){
                    m_seekbar_level.setIsTouchEnabled(true);
                    m_seekbar_level.setPointerAlpha(100); //default:100
                }else{
                    m_seekbar_level.setPointerAlpha(0); //default:100
                }
                m_btn_levelup.setSelected(false); //Enable
                m_btn_leveldown.setSelected(false); //Enable
                m_btn_levelup.setEnabled(true); //Touch Enable
                m_btn_leveldown.setEnabled(true); //Touch Enable
            } else {
                m_text_settempature.setTextColor(0x80312f62);
                m_text_settempature_unit1.setTextColor(0x80312f62);
                m_text_settempature_unit2.setTextColor(0x80312f62);
                m_icon_fcuopmode.setImageAlpha(100);
                m_seekbar_level.setCircleProgressGradientColor(0xffc9c8d6, 0x88c9c8d6); //disable
                m_seekbar_level.setPointerColor(0xffc9c8d6); //default : 0xff6a71cc
                m_seekbar_level.setPointerAlpha(0); //default:100
                m_seekbar_level.setIsTouchEnabled(false);
                m_btn_levelup.setSelected(true); //disable
                m_btn_leveldown.setSelected(true); //disable
                m_btn_levelup.setEnabled(false); //Touch disable
                m_btn_leveldown.setEnabled(false); //Touch disable
            }
        }
    }

    private boolean isDoscreenUpdate() {
        int currentTabID = 0;
        boolean bupdate = false;
        // 현재 선택된 Tab일때만 화면 업데이트함
        currentTabID = MainActivity.getInstance().getcurrentTabID();
        if( (currentTabID == TypeDef.TAB_FAV) || (currentTabID == m_devicedata.getnTabID()) ) {
            bupdate = true;
        }
        //Log.d(TAG, "isDoscreenUpdate() : " + currentTabID + "-" + m_devicedata.getnTabID() + "->" + bupdate);
        return bupdate;
    }

    private boolean isNeedtoupdate() {
        boolean bupdate = false;

        if(!isDoscreenUpdate()){
            bupdate = false;
            return bupdate;
        }

        if (m_devicedata.isbUpdated()) bupdate = true;
        else if (m_received_Value != getDeviceValue()) bupdate = true;
        else {
            if(m_received_OPMode != getDevicethermostatMode()) bupdate = true;
            else if (m_received_AwayMode != getDeviceAwayMode()) bupdate = true;

            if( (m_received_OPMode < 2) && (m_received_AwayMode == false) ) {
                if (m_received_setPoint != getProgressValue()) {
                    //bupdate = true;
                    //Log.d(TAG, "isNeedtoupdate... " + m_devicedata.getNickName() + "->" + bupdate);
                }
            }
        }
        //Log.d(TAG, "isNeedtoupdate ... " + m_devicedata.getNickName() +"->" + bupdate);

        return bupdate;
    }

    private void updateDevice() {

        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() +"-" + m_devicedata.getValue());

        m_received_Value = getDeviceValue();
        m_received_airTemp = getDeviceairTemperature();
        m_received_OPMode = getDevicethermostatMode();
        m_received_AwayMode = getDeviceAwayMode();
        m_received_setCoolPoint = getDevicethermostatSetpoint(0);
        m_received_setHeatPoint = getDevicethermostatSetpoint(1);
        m_received_setPoint = getSetpointValue(m_received_OPMode);

        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_smallpower.setSelected(m_received_Value);
        screenStateByPower(m_received_Value);
        m_text_curtempature.setText(Integer.toString(m_received_airTemp));
        if( !TextUtils.isEmpty(m_devicedata.getScale()) )  {
            m_text_settempature_unit2.setText(m_devicedata.getScale());
            m_text_curtemperature_unit3.setText(m_devicedata.getScale());
        }

        if( (m_received_OPMode == 2) || (m_received_AwayMode == true) ) {
            //ventilation or awayon
            setProgressValue(m_LEVEL_MIN);
            m_devicedata.setbUpdated(false);
        } else {
            //cool or heat
            if (m_UserEventCnt == 0) { //사용자에 의한 업데이트가 아닐경우 바로 업데이트함
                //Log.d(TAG, "updateDevice0 ... " + m_UpdatePostCnt);
                m_internal_setPoint = m_received_setPoint;
                m_internal_Value = m_received_Value;
            }

            if (m_internal_setPoint == m_received_setPoint) {
                m_UpdatePostCnt = 0;
                m_UserEventCnt = 0;
                setProgressValue(m_received_setPoint);
                m_text_settempature.setText(Integer.toString(m_received_setPoint));
                //getProgressValue(); //dummy??
                m_devicedata.setbUpdated(false);
                //Log.d(TAG, "updateDevice1 ... " + m_internal_setPoint +" "+ m_received_setPoint + "/" + getProgressValue());
            } else {
                //예외처리: 화면지연 업데이트 문제 대응(제어시 지연처리에 따른 Sync 문제로 화면이 요동함)
                //Log.d(TAG, "updateDevice2 ... " + m_internal_setPoint +" "+ m_received_setPoint);
                if (m_RepeatBtnCnt == 0) m_UpdatePostCnt++; //Repeat일때는 업데이트 화면갱신 안함
                if (m_UpdatePostCnt > m_UpdatePostTimeoutCnt){
                    m_internal_setPoint = m_received_setPoint; //max 2초정도 Sync Time가짐
                }
            }
        }

        //Log.d(TAG, "updateDevice ... OK " + m_devicedata.getNickName() + "-" + m_received_Value + "-" + m_received_setPoint + "-" + m_received_AwayMode);
        //Log.d(TAG, "updateDevice ... OK " + m_devicedata.getNickName() + "-" + m_received_Value + "-" + m_received_setPoint + "-" + getProgressValue());
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

    public void BtnSmallpower_Clicked(View v)
    {
        //Log.d(TAG, "BtnSmallpower_Clicked()");

        if(v.isSelected()){
            m_internal_Value = false;
            v.setSelected(false); //fake
            screenStateByPower(false); //fake
            controlDevice(TypeDef.SWITCH_OFF);
        }
        else{
            m_internal_Value = true;
            v.setSelected(true); //fake
            screenStateByPower(true); //fake
            controlDevice(TypeDef.SWITCH_ON);
        }
    }

    public void BtnLevelup_Clicked(View v, boolean fromUser)
    {
        //Log.d(TAG, "BtnLevelup_Clicked()" + fromUser);

        if(m_btn_smallpower.isSelected()) {
            int getlevel = getProgressValue();
            if (getlevel < m_LEVEL_MAX) {
                getlevel++;
                m_internal_setPoint = getlevel;
                setProgressValue(getlevel); //fake
                m_text_settempature.setText(Integer.toString(getlevel));
                if (fromUser) {
                    controlDevice_Setpoint(Integer.toString(getlevel)); //btn release시 제어하게끔 고려하자
                }
                m_UserEventCnt++;
            }
        }
    }

    public void BtnLeveldown_Clicked(View v, boolean fromUser)
    {
        //Log.d(TAG, "BtnLeveldown_Clicked()" + fromUser);

        if(m_btn_smallpower.isSelected()) {
            int getlevel = getProgressValue();
            if (getlevel > m_LEVEL_MIN) {
                getlevel--;
                m_internal_setPoint = getlevel;
                setProgressValue(getlevel); //fake
                m_text_settempature.setText(Integer.toString(getlevel));
                if (fromUser) {
                    controlDevice_Setpoint(Integer.toString(getlevel)); //btn release시 제어하게끔 고려하자
                }
                m_UserEventCnt++;
            }
        }
    }

    public void SeekBar_Clicked(int progress, boolean fromUser) {
        int getlevel = getProgressValue();
        //Log.d(TAG, "SeekBar_Clicked..." + getlevel);

        String selvalue = Integer.toString(getlevel);
        m_internal_setPoint = getlevel;
        m_text_settempature.setText(selvalue); //fake
        if (fromUser) {
            controlDevice_Setpoint(Integer.toString(getlevel)); //직접 터치시에만 제어함
        }
        m_Checktimems = 0;
        m_UserEventCnt++;
    }

    public void BtnMore_Clicked(View v) {
        Log.d(TAG, "More => RootUuid: " + m_devicedata.getRootUuid() + ", more_commax_device: " + m_devicedata.getCommaxDevice());
        try {
            //Show message
            String message = m_devicedata.getNickName() + "-" + getResources().getString(R.string.message_move_more);
            MainActivity.getInstance().showToastBox(message, false);

            //move page
            Intent intent = new Intent();
            intent.putExtra(TypeDef.MORE_ROOT_UUID, m_devicedata.getRootUuid());
            intent.putExtra(TypeDef.MORE_COMMAX_DEVICE, m_devicedata.getCommaxDevice());
            intent.setClassName(TypeDef.MORE_CLASS_NAME, TypeDef.MORE_ACTIVITY_NAME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //-
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //mContext.startActivity(intent); //-
            MainActivity.getInstance().startActivityForResult(intent, TypeDef.CONTROL_MSG_ID); //NoACK를 이용해 이전화면을 유지함
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

    }
}