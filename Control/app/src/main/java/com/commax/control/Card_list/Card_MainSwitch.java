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
public class Card_MainSwitch extends LinearLayout implements View.OnClickListener {

    static final String TAG = "Card_MainSwitch"; //Group Switch 전용임, 원래의 MainSwitch는 LightSwitch를 사용함
    static final int RETRY_TIMEOUT_MS = 10000;

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
    TextView m_btn_more;
    ImageView m_icon_more;

    //Mainswitch items
    ImageView m_btn_normalpower;
    TextView m_text_value;

    //Thermostat items
    Button m_btn_power;
    Button m_btn_awayon;
    TextView m_text_powervalue;
    TextView m_text_awayonvalue;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;
    private boolean m_THERMOSTAT_MODE = false; //에어컨,보일러,FCU 디바이스일 경우 화면모드 변경함
    private boolean m_internal_Value[] = {false,false};
    private boolean m_received_Value[] =  {false,false};
    private int m_internal_counter[] = {0,0};
    private int m_received_counter[] = {0,0};
    private int m_retry_counter[] = {0,0};

    //SubDevice를 찾기위한 Index
    private int m_ID_switchBinary = -1;
    private int m_ID_awayswitchBinary = -1;

    private int m_Checktimems[] = {0,0};
    private int m_Needtimems[] = {0,0};
    private int m_OccurEventType = 0;

    //해당 디바이스 On/Off 제어값 정의
    private String MY_SWITCH_ON[]  = {TypeDef.SWITCH_ON,TypeDef.SWITCH_ON};
    private String MY_SWITCH_OFF[] = {TypeDef.SWITCH_OFF,TypeDef.SWITCH_OFF};

    public Card_MainSwitch(Context context) {
        super(context);
        init(context);
    }

    public Card_MainSwitch(Context context, DeviceInfo devicedata) {
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
    private void init(Context context){
        String groupID = m_devicedata.getGroupID();

        mContext = context.getApplicationContext();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if( groupID.equalsIgnoreCase(TypeDef.GroupID.eFCU.value())
                || groupID.equalsIgnoreCase(TypeDef.GroupID.eBoiler.value())
                || groupID.equalsIgnoreCase(TypeDef.GroupID.eAircon.value())
                ){
            rootView = inflater.inflate(R.layout.device_thermostat, this); // from : 가져올 layout 지정
            m_THERMOSTAT_MODE = true;
        }else {
            rootView = inflater.inflate(R.layout.device_mainswitch, this); // from : 가져올 layout 지정
            m_THERMOSTAT_MODE = false;
        }

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        m_btn_more = (TextView) rootView.findViewById(R.id.btn_more);
        m_icon_more = (ImageView) rootView.findViewById(R.id.icon_more);

        if(m_THERMOSTAT_MODE){
            //Thermostat items
            m_btn_power = (Button) rootView.findViewById(R.id.btn_power);
            m_btn_awayon = (Button) rootView.findViewById(R.id.btn_awayon);
            m_text_powervalue = (TextView) rootView.findViewById(R.id.text_powervalue);
            m_text_awayonvalue = (TextView) rootView.findViewById(R.id.text_awayonvalue);
        }else {
            //Mainswitch items
            m_btn_normalpower = (ImageView) rootView.findViewById(R.id.btn_normalpower);
            m_text_value = (TextView) rootView.findViewById(R.id.text_value);
        }

        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_HIGH;
        m_text_title.setText(m_devicedata.getNickName());
        m_btn_more.setVisibility(View.INVISIBLE);
        m_icon_more.setVisibility(View.INVISIBLE);

        m_ID_switchBinary = findDeviceControllerID(TypeDef.SUB_DEVICE_SWITCH);
        m_ID_awayswitchBinary = findDeviceControllerID(TypeDef.SUB_DEVICE_THERMOSTATAWAYMODE);
        MY_SWITCH_ON[0]  = getControlValue_byGroupID(true, m_ID_switchBinary);
        MY_SWITCH_OFF[0] = getControlValue_byGroupID(false, m_ID_switchBinary);
        MY_SWITCH_ON[1]  = getControlValue_byGroupID(true, m_ID_awayswitchBinary);
        MY_SWITCH_OFF[1] = getControlValue_byGroupID(false, m_ID_awayswitchBinary);

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

        if(m_THERMOSTAT_MODE)
        {
            m_btn_power.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BtnNormalpower_Clicked(v);
                }
            });
            m_btn_awayon.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    BtnAwayOn_Clicked(v);
                }
            });
        }
        else
        {
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
                    return false;
                }
            });
        }
    }

    // Card Manager Thread start
    private void startMonitor() {

        if(m_bmonitor_start == false){

            //handler init
            handler = new Handler(){

                public void handleMessage(android.os.Message msg) {
                    if(msg.what == MSG_SEND_ID)  updateDevice();
                    else if(msg.what == MSG_SEND_ID + 1001) groupControlVerify(m_ID_switchBinary);
                    else if(msg.what == MSG_SEND_ID + 1002) groupControlVerify(m_ID_awayswitchBinary);
                    else if(msg.what == MSG_SEND_ID_TIMEOUT) checkTimeoutValue(); //todo
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
            boolean bRetry = false;
            try {
                //Log.d(TAG, "MonitorThread ... Start");

                while(true) {
                    //Exit Trread
                    if(m_bmonitor_stop) break;

                    //Update Event와 지연처리시 화면 갱신함
                    if ( isNeedtoupdate() ) {

                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        //Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);

                        handler.sendEmptyMessage(MSG_SEND_ID);

                        Thread.sleep(TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY]);
                        m_Checktimems[0] += TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY];
                        m_Checktimems[1] += TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY];
                    }else
                    {
                        Thread.sleep(TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY]);
                        m_Checktimems[0] += TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY];
                        m_Checktimems[1] += TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY];
                    }

                    /////////////////////////////////////
                    //Group Control 완료여부 확인
                    if(TypeDef.OPT_RETRY_CONTROL_ENABLE) {
                        if (m_OccurEventType > 0) {
                            if (m_Checktimems[0] > m_Needtimems[0]) {
                                if ((m_OccurEventType == 1) && isNeedtoRetry(m_ID_switchBinary)) {
                                    //Log.d(TAG, "Retry ... Event" + MSG_SEND_ID);
                                    //Log.d(TAG, "Retry : " + m_devicedata.isbUpdated() + "/" + m_received_counter[0] + "-" + m_internal_counter[0] + "/" + m_received_Value[0] + "-" + m_internal_Value[0]);
                                    handler.sendEmptyMessage(MSG_SEND_ID + 1001);
                                    m_retry_counter[0]++;
                                    m_Checktimems[0] = 0;
                                    m_Needtimems[0] = RETRY_TIMEOUT_MS; //retry period
                                }

                                if (m_Checktimems[1] > m_Needtimems[1]) {
                                    if ((m_OccurEventType == 2) && isNeedtoRetry(m_ID_awayswitchBinary)) {
                                        //Log.d(TAG, "Retry ... Event" + MSG_SEND_ID);
                                        //Log.d(TAG, "Retry : " + m_devicedata.isbUpdated() + "/" + m_received_counter[1] + "-" + m_internal_counter[1] + "/" + m_received_Value[1] + "-" + m_internal_Value[1]);
                                        handler.sendEmptyMessage(MSG_SEND_ID + 1002);
                                        m_retry_counter[1]++;
                                        m_Checktimems[1] = 0;
                                        m_Needtimems[1] = RETRY_TIMEOUT_MS; //retry period
                                    }
                                }
                            }
                        }
                    }
                    /////////////////////////////////////
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Class Methods(private) ----------------------------------------*/
    private int findDeviceControllerID(String controller) {
        int index = -1;
        String groupID = m_devicedata.getGroupID();

        if(m_THERMOSTAT_MODE) {
            //todo : subid를 검색하는 방법을 고려하자
            if( controller.equalsIgnoreCase(TypeDef.SUB_DEVICE_SWITCH)) {
                index = 0; //0:thermostatMode
            }else if( controller.equalsIgnoreCase(TypeDef.SUB_DEVICE_THERMOSTATAWAYMODE)) {
                index = 1; //1:thermostatAwayMode
            }
        }else {
            if( controller.equalsIgnoreCase(TypeDef.SUB_DEVICE_SWITCH)) {
                index = -1;
            }
        }

        return index;
    }

    private String getControlValue_byGroupID(boolean bOn, int subid) {
        String groupID = m_devicedata.getGroupID();
        String getvalue = TypeDef.SWITCH_ON;

        if (m_THERMOSTAT_MODE) {
            if(bOn) {
                if (subid == m_ID_switchBinary) {
                    getvalue = TypeDef.SWITCH_ON;
                    if (groupID.equalsIgnoreCase(TypeDef.GroupID.eBoiler.value())) {
                        getvalue = TypeDef.BOILDE_MODE_HEAT;
                    } else if (groupID.equalsIgnoreCase(TypeDef.GroupID.eFan.value())) {
                        //getvalue = TypeDef.FAN_SPEED_LOW; //todo
                    }
                } else if (subid == m_ID_awayswitchBinary) {
                    getvalue = TypeDef.SWITCH_AWAYON;
                }
            }else{
                getvalue = TypeDef.SWITCH_OFF;
                if (subid == m_ID_awayswitchBinary) {
                    getvalue = TypeDef.SWITCH_AWAYOFF;
                }
            }
        } else {
            if(bOn) {
                getvalue = TypeDef.SWITCH_ON;
            }else{
                getvalue = TypeDef.SWITCH_OFF;
            }
        }


        return getvalue;
    }

    private int getSubIndex(int subid) {
        int index = 0;

        if (m_THERMOSTAT_MODE) {
            if (subid == m_ID_switchBinary) index = 0;
            else if (subid == m_ID_awayswitchBinary) index = 1;
        } else {
            index = 0;
        }
        return index;
    }

    private boolean getDeviceValue(int subid) {
        boolean getvalue = false;
        int index = getSubIndex(subid);

        if (m_devicedata.isVirtualDevice()) {
            int counter;
            counter = MainActivity.getInstance().groupControl(m_devicedata.getnCategory(), m_devicedata.getGroupID(),subid, MY_SWITCH_ON[index], true);
            if(counter > 0) getvalue = true;
            if(counter != m_received_counter[index]){
                m_devicedata.setbUpdated(true);
            }
            //Log.d(TAG, "getDeviceValue : " + m_devicedata.getNickName()+  "-"  + m_devicedata.isbUpdated() + "/" + getvalue + "-" + m_received_Value[index] );
        }else {
            try {
                if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_ON)) getvalue = true;
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return getvalue;
    }

    private void controlDevice(String value, int subid) {
        //Timer 등록
        int index = getSubIndex(subid);
        if(m_retry_counter[index] < 2) { //2번 시도 후 완료여부 체크시도함
            pushTimeoutValue(index);
            handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);
            m_devicedata.setValidate(true);
        }

        //AIDL 을 통한 Device 제어
        if(m_devicedata.isVirtualDevice()){
            m_Checktimems[index] = 0;
            m_Needtimems[index] = RETRY_TIMEOUT_MS; //retry period
            m_OccurEventType = 1 + index;
            m_internal_counter[index] = MainActivity.getInstance().groupControl(m_devicedata.getnCategory(), m_devicedata.getGroupID(), subid, value, false);
            //Log.d(TAG, "controlDevice :" + m_devicedata.getNickName() + "->" + value + "/" + m_internal_counter[index]);
            //Log.d(TAG, "groupControlVerify1 : " + m_internal_Value[index] + " " + m_received_Value[index]);
        }else {
            MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                    m_devicedata.getRootUuid(),
                    m_devicedata.getRootDevice(),
                    m_devicedata.getSubUuid(),
                    value,
                    m_devicedata.getSort()
            );
        }
    }

    private void setBtnSelected(int btnId, boolean bOnOff)
    {
        if(m_THERMOSTAT_MODE) {
            if (btnId == 0) {
                m_btn_power.setSelected(bOnOff);
                if (bOnOff) {
                    m_btn_power.setBackgroundResource(R.drawable.round_bt_on_selector);
                } else {
                    m_btn_power.setBackgroundResource(R.drawable.round_bt_off_selector);
                }
            } else {
                m_btn_awayon.setSelected(bOnOff);
                if (bOnOff) {
                    m_btn_awayon.setBackgroundResource(R.drawable.round_bt_on_selector);
                } else {
                    m_btn_awayon.setBackgroundResource(R.drawable.round_bt_off_selector);
                }
            }
        } else {
            m_btn_normalpower.setSelected(bOnOff);
        }
    }

    private void pushTimeoutValue(int controllerID) {
        //제어요청 이외의 디바이스들 상태는 최신으로 업데이트함
        if(controllerID == 0) {
            m_internal_Value[1] = m_received_Value[1];
        } else {
            m_internal_Value[0] = m_received_Value[0];
        }
        SEND_CMD_COUNTER++;
    }

    private boolean checkTimeoutValue() {
        String groupID = m_devicedata.getGroupID();
        boolean getState = false;

        SEND_CMD_COUNTER--;
        if(SEND_CMD_COUNTER == 0)
        {
            if((m_internal_Value[0] == m_received_Value[0])
                && (m_internal_Value[1] == m_received_Value[1])) {

                getState = true;
            } else {

                // 그룹제어시 중간에 개별제어하면 항상 오류가 난다....
                if(m_devicedata.isValidate()) {
                    String message = m_devicedata.getNickName() + " " + getResources().getString(R.string.message_control_fail);
                    MainActivity.getInstance().showToastBox(message,false);
                    m_devicedata.setValidate(false);
                }
            }
            updateDevice();
        }
        return getState;
    }

    private void groupControlVerify(int subid){
        int index = getSubIndex(subid);

        Log.d(TAG, "groupControlVerify... retry_counter :"+ index + "->" + m_retry_counter[index]);
        //Log.d(TAG, "groupControlVerify2 : " + m_internal_Value[index] + " " + m_received_Value[index]);

        if(m_internal_Value[index])
            controlDevice(MY_SWITCH_ON[index], subid);
        else
            controlDevice(MY_SWITCH_OFF[index], subid);
    }

    private boolean isNeedtoRetry(int subid) {
        boolean bRetry = false;
        int index = getSubIndex(subid);

        if(m_retry_counter[index] > TypeDef.MAX_GROUP_RETRY_COUNTER) {
            bRetry = false;
            return bRetry;
        }

        if (m_internal_Value[index]) { //all On
            if (m_received_counter[index] != m_internal_counter[index]) bRetry = true;
            else bRetry = false;
        } else { //all off
            if (m_received_counter[index] != 0) bRetry = true;
            else bRetry = false;
        }

        return bRetry;
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
        else if (m_received_Value[0] != getDeviceValue(m_ID_switchBinary)) bupdate = true;

        if(m_THERMOSTAT_MODE) {
            if(m_received_Value[1] != getDeviceValue(m_ID_awayswitchBinary)) bupdate = true;
        }

        return bupdate;
    }

    private void updateDevice() {

        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() +"-" + m_devicedata.getValue());

        if(m_devicedata.isVirtualDevice()) {

            if(m_THERMOSTAT_MODE) {
                m_received_Value[0] = getDeviceValue(m_ID_switchBinary);
                m_received_Value[1] = getDeviceValue(m_ID_awayswitchBinary);
                m_received_counter[0] = MainActivity.getInstance().groupControl(m_devicedata.getnCategory(), m_devicedata.getGroupID(), m_ID_switchBinary, MY_SWITCH_ON[0], true);
                m_received_counter[1] = MainActivity.getInstance().groupControl(m_devicedata.getnCategory(), m_devicedata.getGroupID(), m_ID_awayswitchBinary, MY_SWITCH_ON[1], true);
                //Log.d(TAG, "updateDevice ... " +  m_received_Value[1] + "->" + m_received_counter[1] + "-" + m_received_counter[1]);

                m_text_title.setText(m_devicedata.getNickName());
                m_btn_favorite.setSelected(m_devicedata.isFavorite());
                m_btn_favorite.setAlpha(1.0F);

                //power value
                if (m_received_counter[0] > 0) {
                    m_devicedata.setValue(TypeDef.SWITCH_ON);
                    setBtnSelected(0,true);
                    if (m_devicedata.getGroupID().equalsIgnoreCase(TypeDef.GroupID.eCurtain.value()))
                        m_text_powervalue.setText(m_received_counter[0] + getResources().getString(R.string.card_curtain_status_open_number));
                    else
                        m_text_powervalue.setText(m_received_counter[0] + getResources().getString(R.string.card_light_status_on_number));
                } else {
                    m_devicedata.setValue(TypeDef.SWITCH_OFF);
                    setBtnSelected(0,false);
                    if (m_devicedata.getGroupID().equalsIgnoreCase(TypeDef.GroupID.eCurtain.value()))
                        m_text_powervalue.setText(getResources().getString(R.string.card_curtain_status_close));
                    else
                        m_text_powervalue.setText(getResources().getString(R.string.card_light_status_off_number));
                }

                //other(away) value
                if (m_received_counter[1] > 0) {
                    m_devicedata.setOtherValue(m_ID_awayswitchBinary, MY_SWITCH_ON[1]);
                    setBtnSelected(1,true);
                    m_text_awayonvalue.setText(m_received_counter[1] + getResources().getString(R.string.card_thermostat_status_awayon_number));
                } else {
                    m_devicedata.setOtherValue(m_ID_awayswitchBinary, MY_SWITCH_OFF[1]);
                    setBtnSelected(1,false);
                    m_text_awayonvalue.setText(getResources().getString(R.string.card_thermostat_status_awayoff));
                }

                if(m_OccurEventType == 0) { //사용자에 의한 업데이트가 아닐경우 바로 업데이트함
                    m_internal_Value[0] = m_received_Value[0];
                    m_internal_Value[1] = m_received_Value[1];
                }

                //Check Complete
                if (m_OccurEventType == 1) {
                    if (isNeedtoRetry(m_ID_switchBinary) == false){
                        m_OccurEventType = 0;
                        m_retry_counter[0] = 0;
                        m_internal_Value[1] = m_received_Value[1];
                    }
                    //Log.d(TAG, "groupControlVerify ... OK " + m_OccurEventType + "/" + m_received_Value[0] + "->" + m_received_counter[0] + "-" + m_internal_counter[0]);
                } else if (m_OccurEventType == 2) {
                    if (isNeedtoRetry(m_ID_awayswitchBinary) == false){
                        m_OccurEventType = 0;
                        m_retry_counter[1] = 0;
                        m_internal_Value[0] = m_received_Value[0];
                    }
                    //Log.d(TAG, "groupControlVerify ... OK " + m_OccurEventType + "/" + m_received_Value[1] + "->" + m_received_counter[1] + "-" + m_internal_counter[1]);
                }
            }else {
                m_received_Value[0] = getDeviceValue(m_ID_switchBinary);
                m_received_counter[0] = MainActivity.getInstance().groupControl(m_devicedata.getnCategory(), m_devicedata.getGroupID(), m_ID_switchBinary, MY_SWITCH_ON[0], true);
                m_text_title.setText(m_devicedata.getNickName());
                m_btn_favorite.setSelected(m_devicedata.isFavorite());
                m_btn_favorite.setAlpha(1.0F);
                m_btn_normalpower.setAlpha(1.0F);

                if(m_OccurEventType == 0) { //사용자에 의한 업데이트가 아닐경우 바로 업데이트함
                    m_internal_Value[0] = m_received_Value[0];
                }

                if (m_received_counter[0] > 0) {
                    m_devicedata.setValue(TypeDef.SWITCH_ON);
                    m_btn_normalpower.setSelected(true);
                    if (m_devicedata.getGroupID().equalsIgnoreCase(TypeDef.GroupID.eCurtain.value()))
                        m_text_value.setText(m_received_counter[0] + getResources().getString(R.string.card_curtain_status_open_number));
                    else
                        m_text_value.setText(m_received_counter[0] + getResources().getString(R.string.card_light_status_on_number));
                } else {
                    m_devicedata.setValue(TypeDef.SWITCH_OFF);
                    m_btn_normalpower.setSelected(false);
                    if (m_devicedata.getGroupID().equalsIgnoreCase(TypeDef.GroupID.eCurtain.value()))
                        m_text_value.setText(getResources().getString(R.string.card_curtain_status_close));
                    else
                        m_text_value.setText(getResources().getString(R.string.card_light_status_off_number));
                }

                //Check Complete
                if (m_OccurEventType > 0) {
                    if (isNeedtoRetry(m_ID_switchBinary) == false){
                        m_OccurEventType = 0;
                        m_retry_counter[0] = 0;
                    }
                    //Log.d(TAG, "groupControlVerify ... OK " + m_OccurEventType + "/" + m_received_Value[0] + "->" + m_received_counter[0] + "-" + m_internal_counter[0]);
                }
            }
            //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() +"-" + m_devicedata.getValue());

        }else {
            m_received_Value[0] = getDeviceValue(m_ID_switchBinary);
            m_text_title.setText(m_devicedata.getNickName());
            m_btn_favorite.setSelected(m_devicedata.isFavorite());

            if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_ON)) {
                m_btn_normalpower.setSelected(true);
                m_text_value.setText(getResources().getString(R.string.card_light_status_on));
            } else {
                m_btn_normalpower.setSelected(false);
                m_text_value.setText(getResources().getString(R.string.card_light_status_off));
            }
        }
        m_devicedata.setbUpdated(false);

        //Log.d(TAG, "updateDevice ... OK " + m_devicedata.getNickName() + "-" + m_received_Value[0]);
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
        //Log.d(TAG, "BtnNormalpower_Clicked()");
        if(v.isSelected()) {
            Log.d(TAG, "isSelected ... 0");
            m_internal_Value[0] = false;
            setBtnSelected(0,false);  //fake
            controlDevice(MY_SWITCH_OFF[0], m_ID_switchBinary);
        } else {
            Log.d(TAG, "isSelected ... 1");
            m_internal_Value[0] = true;
            setBtnSelected(0,true);  //fake
            controlDevice(MY_SWITCH_ON[0], m_ID_switchBinary);
        }

        if(m_devicedata.isVirtualDevice()) {
            if(m_internal_counter[0] > 0) {
                //Toast.makeText(super.getContext() , getResources().getString(R.string.message_docontrol_string), Toast.LENGTH_LONG).show();
                MainActivity.getInstance().showToastBox(getResources().getString(R.string.message_docontrol_string),false);
            }
        }
    }

    public void BtnAwayOn_Clicked(View v)
    {
        //Log.d(TAG, "BtnAwayOn_Clicked()");

        if(v.isSelected())
        {
            Log.d(TAG, "isSelected ... 0");
            m_internal_Value[1] = false;
            //setBtnSelected(1,false);  //fake
            controlDevice(MY_SWITCH_OFF[1], m_ID_awayswitchBinary);
        } else{
            Log.d(TAG, "isSelected ... 1");
            m_internal_Value[1] = true;
            //setBtnSelected(1,true);  //fake
            controlDevice(MY_SWITCH_ON[1], m_ID_awayswitchBinary);
        }

        if(m_devicedata.isVirtualDevice()) {
            if(m_internal_counter[1] > 0) {
                MainActivity.getInstance().showToastBox(getResources().getString(R.string.message_docontrol_string),false);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}