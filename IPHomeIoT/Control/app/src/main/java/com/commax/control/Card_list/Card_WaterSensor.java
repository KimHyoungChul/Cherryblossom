package com.commax.control.Card_list;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
public class Card_WaterSensor extends LinearLayout implements View.OnClickListener {

    static final String TAG = Card_WaterSensor.class.getSimpleName();
    //todo
    static final int DEF_LOW_BATTERY_MIN = 20;  //need to adjust
    static final boolean DEF_BATTERY_ENABLE = true; //Battery 표시 여부

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
    ImageView m_icon_sensor;
    TextView m_text_status;
    TextView m_text_battery; //optional
    TextView m_text_battery_value; //optional
    TextView m_text_battery_unit; //optional
    TextView m_text_lowbattery; //optional
    ImageView m_icon_lowbattery; //optional

    TextView m_btn_more;
    ImageView m_icon_more;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;

    private int m_Checktimems = 0; //누적 Timer 값
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;
    private boolean m_received_Value = false;
    // battery Low : default => false  / false = battery enough , true = battery low
    private boolean m_received_Battery_Low = false;
    private boolean m_bToggle = false;
    private int m_received_battery_Value = 1;


    public Card_WaterSensor(Context context) {
        super(context);
        Log.d(TAG, "context");
        init(context);
    }

    public Card_WaterSensor(Context context, DeviceInfo devicedata) {
        super(context);
        Log.d(TAG, "context , devicedata");
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

        Log.d(TAG, "init()");
        mContext = context.getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.device_watersensor, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        m_icon_sensor = (ImageView) rootView.findViewById(R.id.icon_sensor);
        m_text_status = (TextView) rootView.findViewById(R.id.text_status);
        m_text_battery = (TextView) rootView.findViewById(R.id.text_battery);
        m_text_battery_value = (TextView) rootView.findViewById(R.id.text_battery_value);
        m_text_battery_unit = (TextView) rootView.findViewById(R.id.text_battery_unit);
        m_text_lowbattery = (TextView) rootView.findViewById(R.id.text_lowbattery);
        m_icon_lowbattery = (ImageView) rootView.findViewById(R.id.icon_lowbattery);

        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_HIGH;
        m_text_title.setText(m_devicedata.getNickName());

        //m_btn_more.setVisibility(View.INVISIBLE);
        //m_icon_more.setVisibility(View.INVISIBLE);
        if(!DEF_BATTERY_ENABLE) {
            m_text_battery.setVisibility(View.INVISIBLE);
            m_text_battery_value.setVisibility(View.INVISIBLE);
            m_text_battery_unit.setVisibility(View.INVISIBLE);
            m_text_lowbattery.setVisibility(View.INVISIBLE);
            m_icon_lowbattery.setVisibility(View.INVISIBLE);
        } else
        {
            if (TextUtils.isEmpty(m_devicedata.getBatteryLevel())) {
                m_text_battery.setVisibility(View.INVISIBLE);
                m_text_battery_value.setVisibility(View.INVISIBLE);
                m_text_battery_unit.setVisibility(View.INVISIBLE);
            }
        }

        updateDevice();
        startMonitor();

        //OnClickListener
        m_text_title.setOnTouchListener(new OnTouchListener(){
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
        m_btn_favorite.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(0.5F);
                if (event.getAction() == MotionEvent.ACTION_CANCEL) v.setAlpha(1.0F);
                return false;
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
                    else if (msg.what == MSG_SEND_ID + 1001) toggleBatteryStatus();
                    //else if(msg.what == MSG_SEND_ID_TIMEOUT)  checkTimeoutValue();
                }
            };

            //start Monitor
            MonitorThread monitor_thread;
            monitor_thread = new MonitorThread();
            if(monitor_thread != null) {
                try {
                    Log.d(TAG, "monitor_thread start");
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
                    if ( m_devicedata.isbUpdated()) {

                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        //Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);

                        handler.sendEmptyMessage(MSG_SEND_ID);
                        Thread.sleep(TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY]);
                        m_Checktimems += TypeDef.DEVICE_SETUP_TIME_MS[MY_PRIORITY];
                    } else {
                        Thread.sleep(TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY]);
                        m_Checktimems += TypeDef.REFRESH_POLLING_PERIOD_MS[MY_PRIORITY];
                    }

                    /////////////////////////////////////
                    //Timer 처리
                    if (m_received_Battery_Low && (m_Checktimems >= 500)) {
                        handler.sendEmptyMessage(MSG_SEND_ID + 1001);
                        m_Checktimems = 0;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Class Methods(private) ----------------------------------------*/
    private boolean getDeviceValue() {
        boolean getvalue = false;
        try {
            if (m_devicedata.getValue().equalsIgnoreCase(TypeDef.DETECT_TRUE)) getvalue = true;
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getvalue;
    }

    private boolean getDevicebatteryStatus() {
        boolean getvalue = false;
        try {
            if (m_devicedata.getBatteryEvent().equalsIgnoreCase(TypeDef.BATTERY_LOW)) getvalue = true;
        } catch (Exception e) {
            //e.printStackTrace();
        }

        //Log.d(TAG, "getDeviceValue ... " + getvalue);
        return getvalue;
    }

    private int getDeviceBatteryValue() {
        int getvalue = -1;

        try {
            //todo
            //getvalue = Integer.parseInt(m_devicedata.getOtherValue(0)); //Battery
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //Log.d(TAG, "getDeviceBatteryValue ... " + getvalue);

        return getvalue;
    }

    private void controlDevice(String value) {
        //Timer 등록
        //pushTimeoutValue();
        //handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                m_devicedata.getRootUuid(),
                m_devicedata.getRootDevice(),
                m_devicedata.getSubUuid(),
                value,
                m_devicedata.getSort()
        );
    }

    private void updateBatteryStatus()
    {
        //battery Value
        if (m_received_battery_Value == -1) {
            m_text_battery.setVisibility(View.INVISIBLE);
            m_text_battery_value.setVisibility(View.INVISIBLE);
            m_text_battery_unit.setVisibility(View.INVISIBLE);
        } else {
            m_text_battery.setVisibility(View.VISIBLE);
            m_text_battery_value.setVisibility(View.VISIBLE);
            m_text_battery_unit.setVisibility(View.VISIBLE);
            m_text_battery_value.setText(Integer.toString(m_received_battery_Value));
        }

        //Low Battery Message
        if(m_received_Battery_Low) {
            m_icon_lowbattery.setVisibility(View.VISIBLE);
            m_text_lowbattery.setVisibility(View.VISIBLE);
        } else {
            m_icon_lowbattery.setVisibility(View.INVISIBLE);
            m_text_lowbattery.setVisibility(View.INVISIBLE);
        }
    }

    private void toggleBatteryStatus() {
        if(m_received_Battery_Low) {
            m_bToggle = !m_bToggle;
            if(m_bToggle) {
                m_icon_lowbattery.setVisibility(View.VISIBLE);
                m_text_lowbattery.setVisibility(View.VISIBLE);
            } else {
                m_icon_lowbattery.setVisibility(View.INVISIBLE);
                m_text_lowbattery.setVisibility(View.INVISIBLE);
            }
            //Log.d(TAG, "toggleBatteryStatus ... " + m_bToggle);
        }
    }

    private void updateDevice() {

        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName() + "-" + m_devicedata.getValue());

        m_received_Value = getDeviceValue();
        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_favorite.setAlpha(1.0F);
        m_icon_sensor.setSelected(m_received_Value);
        if (m_received_Value) {
            m_text_status.setText(getResources().getText(R.string.card_magnetic_status_detected));
        } else {
            m_text_status.setText(getResources().getText(R.string.card_magnetic_status_normal));
        }

        //todo : 배터리 상태 추가
        if(DEF_BATTERY_ENABLE) {
            m_received_battery_Value = getDeviceBatteryValue();
            m_received_battery_Value = getDeviceBatteryValue();
            updateBatteryStatus();
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

    public void BtnMore_Clicked(View v)
    {
        Log.d(TAG, "BtnMore_Clicked()");
    }

    @Override
    public void onClick(View v) {

    }
}
