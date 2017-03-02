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

import com.commax.control.Common.TypeDef;
import com.commax.control.DeviceInfo;
import com.commax.control.MainActivity;
import com.commax.control.R;

/**
 * Created by gbg on 2016-07-22.
 */
public class Card_Curtain extends LinearLayout implements View.OnClickListener{

    static final String TAG = "Card_Curtain";
    static final boolean PAUSE_CONTROL_ENABLE = false;

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
    //ImageView btn_smallpower;
    Button m_btn_open;
    Button m_btn_close;

    TextView m_text_value;
    TextView m_btn_more;
    ImageView m_icon_more;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private int m_UserEventCnt = 0; //user Event Counter

    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;

    private TypeDef.Curtain_OP m_internal_State;
    private TypeDef.Curtain_OP m_received_State;

    public Card_Curtain(Context context) {
        super(context);
        init(context);
    }

    public Card_Curtain(Context context, DeviceInfo devicedata) {
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

        mContext = context.getApplicationContext();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.device_curtain, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        //m_btn_smallpower = (ImageView) rootView.findViewById(R.id.btn_smallpower);

        m_btn_open = (Button) rootView.findViewById(R.id.btn_open);
        m_btn_close = (Button) rootView.findViewById(R.id.btn_close);
        m_text_value = (TextView) rootView.findViewById(R.id.text_value);

        m_btn_more = (TextView) rootView.findViewById(R.id.btn_more);
        m_icon_more = (ImageView) rootView.findViewById(R.id.icon_more);

        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_MID;
        m_text_title.setText(m_devicedata.getNickName());
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

        m_btn_open.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BtnOpen_Clicked(v);
            }
        });

        m_btn_close.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                BtnClose_Clicked(v);
            }
        });
    }

    // Card Manager Thread start
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

    // Card Manager Thread
    class MonitorThread extends Thread {
        public void run() {
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
    private TypeDef.Curtain_OP getDeviceStatus() {
        TypeDef.Curtain_OP getstatus = TypeDef.Curtain_OP.eOff;
        try {
            if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_ON)) {
                getstatus = TypeDef.Curtain_OP.eOn;
            }else {
                if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_STOP)) {
                    if(PAUSE_CONTROL_ENABLE) {
                        getstatus = TypeDef.Curtain_OP.ePause;
                    } else {
                        getstatus = TypeDef.Curtain_OP.eOn;
                    }

                }else {
                    getstatus = TypeDef.Curtain_OP.eOff;
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getstatus;
    }

    private void setBtnSelected(boolean bOnOff)
    {
        m_btn_open.setSelected(bOnOff);
        m_btn_close.setSelected(!bOnOff);

        if(bOnOff) {
            m_btn_open.setBackgroundResource(R.drawable.round_bt_on_selector);
            m_btn_close.setBackgroundResource(R.drawable.round_bt_off_selector);
        } else {
            m_btn_open.setBackgroundResource(R.drawable.round_bt_off_selector);
            m_btn_close.setBackgroundResource(R.drawable.round_bt_on_selector);
        }
    }

    private void controlDevice(String value) {

        //Timer 등록
        pushTimeoutValue();
        handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS*2); //2배로 늘림

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

    private void pushTimeoutValue() {
        SEND_CMD_COUNTER++;
    }

    private boolean checkTimeoutValue() {
        boolean getState = false;

        SEND_CMD_COUNTER--;
        if(SEND_CMD_COUNTER == 0)
        {
            if(m_internal_State == m_received_State){
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

    private boolean isNeedtoupdate() {
        boolean bupdate = false;

        if (m_devicedata.isbUpdated()) bupdate = true;
        else if (m_received_State != getDeviceStatus()) bupdate = true;

        return bupdate;
    }

    private void updateDevice() {

        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() +"-" + m_devicedata.getValue());

        m_received_State = getDeviceStatus();
        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_favorite.setAlpha(1.0F);

        if(m_UserEventCnt == 0) { //사용자에 의한 업데이트가 아닐경우 바로 업데이트함
            m_internal_State = m_received_State;
        }
        if(m_internal_State == m_received_State) {
            m_UserEventCnt = 0;
        }

        if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_ON)) {
            //m_internal_State = TypeDef.Curtain_OP.eOn;
            m_btn_open.setText(R.string.card_curtain_control_open);
            setBtnSelected(true);
            m_btn_open.setText(getResources().getText(R.string.card_curtain_control_open));
            m_text_value.setText(getResources().getText(R.string.card_curtain_status_open));
        }else  if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.SWITCH_STOP)) {
            //m_internal_State = TypeDef.Curtain_OP.ePause;
            m_btn_open.setText(R.string.card_curtain_control_open);
            m_btn_open.setSelected(true);
            m_btn_close.setSelected(false);
            setBtnSelected(true);
            //m_btn_open.setBackgroundResource(R.mipmap.ic_dc_pause_n);
            m_btn_open.setText(getResources().getText(R.string.card_curtain_control_stop));
            m_text_value.setText(getResources().getText(R.string.card_curtain_status_stop));
        }else {
            //m_internal_State = TypeDef.Curtain_OP.eOff;
             setBtnSelected(false);
            m_text_value.setText(getResources().getText(R.string.card_curtain_status_close));
        }

        m_devicedata.setbUpdated(false);
        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() +"-" + m_devicedata.getValue());
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

    public void BtnOpen_Clicked(View v)
    {
        //Log.d(TAG, "BtnOpen_Clicked()");

        setBtnSelected(true); //fake
        m_internal_State = TypeDef.Curtain_OP.eOn;
        controlDevice(TypeDef.SWITCH_ON);
        m_UserEventCnt++;

        //Pause 처리
        if(PAUSE_CONTROL_ENABLE) {
            if (m_internal_State == TypeDef.Curtain_OP.eOff) {
                m_internal_State = TypeDef.Curtain_OP.eOn;
                controlDevice(TypeDef.SWITCH_ON);
            } else {
                if (m_internal_State == TypeDef.Curtain_OP.eOn) {
                    m_internal_State = TypeDef.Curtain_OP.ePause;
                    controlDevice(TypeDef.SWITCH_STOP);
                } else {
                    m_internal_State = TypeDef.Curtain_OP.eOn;
                    controlDevice(TypeDef.SWITCH_ON);
                }
            }
        }
    }

    public void BtnClose_Clicked(View v)
    {
        //Log.d(TAG, "BtnClose_Clicked()");

        setBtnSelected(false); //fake
        m_internal_State = TypeDef.Curtain_OP.eOff;
        controlDevice(TypeDef.SWITCH_OFF);
        m_UserEventCnt++;
    }

    @Override
    public void onClick(View v) {

    }
    
}

