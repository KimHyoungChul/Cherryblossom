package com.commax.control.Card_list;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
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
public class Card_MultiSensor_PIR extends LinearLayout implements View.OnClickListener {

    static final String TAG = Card_MultiSensor_PIR.class.getSimpleName();

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

    TextView m_text_motion;
    TextView m_text_temperature;
    TextView m_text_humid;
    TextView m_text_battery;

    TextView m_text_temperature_unit;
    TextView m_text_humidity_unit;
    TextView m_text_battery_unit;

    LinearLayout battery_layout;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;

    public Card_MultiSensor_PIR(Context context) {
        super(context);
        init(context);
    }

    public Card_MultiSensor_PIR(Context context, DeviceInfo devicedata) {
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.device_multisensor, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);

        m_text_motion = (TextView) rootView.findViewById(R.id.motion_value);
        m_text_temperature = (TextView) rootView.findViewById(R.id.temperature_value);
        m_text_humid = (TextView) rootView.findViewById(R.id.humid_value);
        m_text_battery = (TextView)rootView.findViewById(R.id.battery_value);

        m_text_temperature_unit = (TextView)rootView.findViewById(R.id.temperature_unit);
        m_text_humidity_unit = (TextView)rootView.findViewById(R.id.humidity_unit);
        m_text_battery_unit = (TextView)rootView.findViewById(R.id.battery_unit);

        //TODO 2016-12-09 현재 배터리는 표시 안함 추후 예정
        battery_layout = (LinearLayout)rootView.findViewById(R.id.battery_layout);
        battery_layout.setVisibility(GONE);


        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_LOW;
        m_text_title.setText(m_devicedata.getNickName());


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
                    //if(msg.what == MSG_SEND_ID_TIMEOUT) checkTimeoutValue();
                }
            };

            //start Monitor
            MonitorThread monitor_thread;
            monitor_thread = new MonitorThread();
            if(monitor_thread != null) {
                try {
                    monitor_thread.start();
                    m_bmonitor_start = true;
                    Log.d(TAG, "monitor Thread start");
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
    private Pair getDeviceValue_bySort(int index) {
        String getFvalue;
        int getPrecision = 0;
        String getScale ;
        Pair<String, String> result_val = null;

        try {
            if(index == 100)
            {
                //TODO vlaue 값
                getFvalue = m_devicedata.getValue();
                getPrecision = Integer.valueOf(m_devicedata.getPrecision());
                getScale = m_devicedata.getScale();

                result_val = new Pair<>(getFvalue , getScale);
            }
            else
            {
                getFvalue = m_devicedata.getOtherValue(index);
                getPrecision = Integer.valueOf(m_devicedata.getOtherPrecision(index));
                getScale = m_devicedata.getOtherScale(index);

                result_val = new Pair<>(getFvalue , getScale);
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return result_val;
    }

    public void getDevicevalue(String sortName)
    {

    }


    private void controlDevice(String value) {
        //Timer 등록
        //pushTimeoutValue();
        //handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
    }

    private void updateDevice() {
        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName());

        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_favorite.setAlpha(1.0F);
        //TODO sort 값을 확인해서 value 값 넣어야 한다.

        if(m_devicedata.getValue().equals(TypeDef.DETECT_DETECTED))
        {
            m_text_motion.setText(getContext().getString(R.string.card_magnetic_status_detected));
        }
        else if(m_devicedata.getValue().equals(TypeDef.DETECT_UNDETECTED))
        {
            m_text_motion.setText(getContext().getString(R.string.card_multi_status_undetected));
        }

        m_text_temperature.setText(m_devicedata.getOtherValue(0));
        //value 나 scale 이 없으면 ° 안보이도록 처리한다.
        if(TextUtils.isEmpty(m_devicedata.getOtherValue(0)) || TextUtils.isEmpty(m_devicedata.getOtherScale(0)))
        {
            m_text_temperature_unit.setText(m_devicedata.getOtherScale(0));
        }
        else
        {
            m_text_temperature_unit.setText("°"+m_devicedata.getOtherScale(0));
        }


        m_text_humid.setText(m_devicedata.getOtherValue(1));
        m_text_humidity_unit.setText(m_devicedata.getOtherScale(1));

        m_devicedata.setbUpdated(false);

        Log.d(TAG, "updateDevice ... OK " + m_devicedata.getNickName());
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

    @Override
    public void onClick(View v) {

    }
}