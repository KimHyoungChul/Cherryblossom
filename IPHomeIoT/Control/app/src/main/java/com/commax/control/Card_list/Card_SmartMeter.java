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
public class Card_SmartMeter extends LinearLayout implements View.OnClickListener {

    static final String TAG = "Card_SmartMeter";

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

    TextView m_text_electric;
    TextView m_text_gas;
    TextView m_text_water;
    TextView m_text_warm;
    TextView m_text_heat;
    TextView m_btn_more;
    ImageView m_icon_more;

    private int MSG_SEND_ID; //내부 고유 message ID
    private int MSG_SEND_ID_TIMEOUT;
    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)
    private int SEND_CMD_COUNTER = 0;
    private boolean m_bmonitor_start = false;
    private boolean m_bmonitor_stop = false;
    private float m_received_electric = 0;
    private float m_received_gas = 0;
    private float m_received_water = 0;
    private float m_received_warm = 0;
    private float m_received_heat = 0;


    public Card_SmartMeter(Context context) {
        super(context);
        init(context);
    }

    public Card_SmartMeter(Context context, DeviceInfo devicedata) {
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
        rootView = inflater.inflate(R.layout.device_smartmeter, this); // from : 가져올 layout 지정

        //get resource handle
        src_layout = (LinearLayout)rootView.findViewById(R.id.container_layout); //to : 화면에 추가할 layout 지정
        m_text_title = (TextView) rootView.findViewById(R.id.title_textview);
        m_btn_favorite = (ImageView) rootView.findViewById(R.id.btn_favorite);
        m_text_electric = (TextView) rootView.findViewById(R.id.eletric_value);
        m_text_gas = (TextView) rootView.findViewById(R.id.gas_value);
        m_text_water = (TextView) rootView.findViewById(R.id.water_value);
        m_btn_more = (TextView) rootView.findViewById(R.id.btn_more);
        m_icon_more = (ImageView) rootView.findViewById(R.id.icon_more);

        //set OnClickListener
        //src_layout.setOnClickListener(this);

        //value initialize
        MSG_SEND_ID = m_devicedata.getnLayoutID();
        MSG_SEND_ID_TIMEOUT = MSG_SEND_ID +1000;
        MY_PRIORITY = TypeDef.PRIORITY_LOW;
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
    private float getDeviceValue_bySort(String sortName) {
        float getFvalue = 0;
        int getPrecision = 0;
        Pair<String, String> result_val;
        try {
            //group단위 액세스
            result_val = MainActivity.getInstance().groupValue(m_devicedata.getnCategory(), m_devicedata.getGroupID(), sortName);
            //Log.d(TAG, "getDeviceValue_bySort ... " + result_val.first + "," + result_val.second);

            if (!TextUtils.isEmpty(result_val.first)) {
                getFvalue = Float.parseFloat(result_val.first);
                //getvalue = Integer.parseInt(result_val.first);
            }
            if (!TextUtils.isEmpty(result_val.second)) {
                getPrecision = Integer.parseInt(result_val.second);
            }
            if(getPrecision > 0) getFvalue = getFvalue/(float)Math.pow(10,getPrecision);

        } catch (Exception e) {
            //e.printStackTrace();
        }
        return getFvalue;
    }

    private void controlDevice(String value) {
        //Timer 등록
        //pushTimeoutValue();
        //handler.sendEmptyMessageDelayed(MSG_SEND_ID_TIMEOUT, TypeDef.DEVICE_CONTROLTIMEOUT_TIME_MS);

        //AIDL 을 통한 Device 제어
    }

    private void updateDevice() {

        String strvalue;
        //Log.d(TAG, "updateDevice ... " + m_devicedata.getNickName());

        m_text_title.setText(m_devicedata.getNickName());
        m_btn_favorite.setSelected(m_devicedata.isFavorite());
        m_btn_favorite.setAlpha(1.0F);

        m_received_electric = getDeviceValue_bySort(TypeDef.SUB_DEVICE_ELECTRICMETER);
        m_received_gas = getDeviceValue_bySort(TypeDef.SUB_DEVICE_WATERMETER);
        m_received_water = getDeviceValue_bySort(TypeDef.SUB_DEVICE_GASMETER);
        m_received_warm = getDeviceValue_bySort(TypeDef.SUB_DEVICE_WARMMETER);
        m_received_heat = getDeviceValue_bySort(TypeDef.SUB_DEVICE_HEATMETER);

        strvalue = String.format("%.1f", m_received_electric);
        m_text_electric.setText(strvalue);
        strvalue = String.format("%.1f", m_received_gas);
        m_text_gas.setText(strvalue);
        strvalue = String.format("%.1f", m_received_water);
        m_text_water.setText(strvalue);

        //strvalue = String.format("%.1f", m_received_warm);
        //m_text_warm.setText(strvalue);
        //strvalue = String.format("%.1f", m_received_heat);
        //m_text_heat.setText(strvalue);

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