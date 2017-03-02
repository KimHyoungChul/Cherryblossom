package com.commax.controlsub.MoreActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.controlsub.Common.CircularSeekBar;
import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.DeviceInfo;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

/**
 * Created by OWNER on 2016-08-30.
 */
public class More_FCU_no_ventilation_nofanspeed extends LinearLayout implements View.OnClickListener{

    String TAG = More_FCU_no_ventilation_nofanspeed.class.getSimpleName();
    DeviceInfo deviceInfo;
    Context mContext;

    // layout
    LayoutInflater inflater;
    View rootView;
    // Handler
    Handler handler;

    ImageView ic_dc_warm_cool_icon;
    ImageView title_power_button;
    ImageView title_back_button;
    TextView title_text;
    TextView fcu_data_value;
    TextView fcu_current_data_value;
    TextView fcu_data_scale1;
    TextView fcu_data_scale2;
    Button cooling_binary_button;
    Button heating_binary_button;
    Button circular_button;
    Button away_on_button;
    Button away_off_button;
    Button high_button;
    Button middle_button;
    Button low_button;
    Button auto_button;
    ImageButton fcu_minus_button;
    ImageButton fcu_plus_button;
    ImageView outgoint_ventilation;
    LinearLayout fanspeed_layout;
    View fanspeed_upper_bar;

    CircularSeekBar circularSeekBar;
    private boolean m_received_Value = false;
    boolean m_bmonitor_start = false;
    int MSG_SEND_ID;

    private  int plus_minus_longButton_time_gap = 500;

    //seek bar
    private int cool_m_LEVEL_MIN = 0;
    private int cool_m_LEVEL_MAX = 0;
    private int cool_m_LEVEL_RANGE = 0;
    private int m_LEVEL_MIN = 0;
    private int m_LEVEL_MAX = 0;
    private int m_LEVEL_RANGE = 0;
    private int m_RepeatBtnCnt = 0; //Repeat버튼 Counter
    private int m_UpdatePostCnt = 0; //제어 중일때 대기시간 Counter

    private int MY_PRIORITY; //내부 Thread의 Priority(실행주기)

    private int m_Checktimems = 0;
    private int m_Needtimems = 0;
    private int m_RepeatBtnType = 0;

    public boolean timer_count = false;
    // - ,+ button timer task
    public SubOptionsControlTimerTask mSubOptionsControlTimerTask;
    Thread mSubOptionsThread;
    // control fail check timer task
    public Timer_count_Task mTimerCountTask;
    Thread mTimerCountThread;

    //circular bar handler threa 실 디바이스에서 circular 바가 움찔하는 현상이 발생하여 해당 사항 처리
    boolean circular_handler_flag = false;

    private static final int CONTROL_OPTIONS_TIMEOUT = 5;

    public More_FCU_no_ventilation_nofanspeed(Context context) {
        super(context);
        init(context);
    }

    public More_FCU_no_ventilation_nofanspeed(Context context, DeviceInfo devicedata) {
        super(context);
        Log.d(TAG , "More_FCU");
        deviceInfo = devicedata;
        init(context);
    }
    public void setDeviceData(DeviceInfo devicedata) {
        deviceInfo = devicedata;
    }

    public void init(Context context)
    {
        Log.d(TAG, "init()" + deviceInfo);
        mContext = context.getApplicationContext();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.more_fcu, this); // from : 가져올 layout 지정
        circularSeekBar = (CircularSeekBar)findViewById(R.id.circularSeekBar_control);

        title_text = (TextView)rootView.findViewById(R.id.title_text);
        title_power_button = (ImageView)rootView.findViewById(R.id.title_power_button);
        title_back_button =  (ImageView)rootView.findViewById(R.id.title_back_button);
        ic_dc_warm_cool_icon = (ImageView)rootView.findViewById(R.id.ic_dc_icon);
        fcu_data_value = (TextView)rootView.findViewById(R.id.fcu_data_value) ;
        fcu_current_data_value = (TextView)rootView.findViewById(R.id.fcu_current_data_value);
        fcu_data_scale1 = (TextView)rootView.findViewById(R.id.fcu_data_scale1);
        fcu_data_scale2 = (TextView)rootView.findViewById(R.id.fcu_data_scale2);
        cooling_binary_button = (Button)rootView.findViewById(R.id.cooling_mode);
        heating_binary_button = (Button)rootView.findViewById(R.id.heating_mode);
        circular_button = (Button)rootView.findViewById(R.id.circulating_mode);
        away_on_button = (Button)rootView.findViewById(R.id.away_on);
        away_off_button = (Button)rootView.findViewById(R.id.away_off);
        high_button = (Button)rootView.findViewById(R.id.high_mode);
        middle_button = (Button)rootView.findViewById(R.id.middle_mode);
        low_button  = (Button)rootView.findViewById(R.id.low_mode);
        auto_button = (Button)rootView.findViewById(R.id.auto_mode);

        fcu_minus_button = (ImageButton)rootView.findViewById(R.id.fcu_minus_button);
        fcu_plus_button = (ImageButton)rootView.findViewById(R.id.fcu_plus_button);
        outgoint_ventilation = (ImageView)rootView.findViewById(R.id.component_outgoint_ventilacion);
        fanspeed_layout = (LinearLayout)rootView.findViewById(R.id.fanspeed_layout);
        fanspeed_upper_bar = (View)rootView.findViewById(R.id.fanspeed_upper_bar);

        //customize for LLT
        //모드중에 환기 제거 , 풍속 제거
        circular_button.setVisibility(GONE);
        fanspeed_layout.setVisibility(GONE);
        fanspeed_upper_bar.setVisibility(GONE);

        if(mSubOptionsControlTimerTask == null){
            mSubOptionsControlTimerTask = new SubOptionsControlTimerTask();
        }

        if(mTimerCountTask == null){
            mTimerCountTask = new Timer_count_Task();
        }

//        updateDevice();
        deviceUIsetting();
        startMonitor();

        //onclickListner
        title_power_button.setOnClickListener(this);
        title_back_button.setOnClickListener(this);
        cooling_binary_button.setOnClickListener(this);
        heating_binary_button.setOnClickListener(this);
        away_on_button.setOnClickListener(this);
        away_off_button.setOnClickListener(this);
        high_button.setOnClickListener(this);
        middle_button.setOnClickListener(this);
        low_button.setOnClickListener(this);
        auto_button.setOnClickListener(this);
        circular_button.setOnClickListener(this);
//        fcu_minus_button.setOnClickListener(this);
//        fcu_plus_button.setOnClickListener(fcu_plus_onClickListner);

        fcu_minus_button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(MotionEvent.ACTION_DOWN == action){
                    android.util.Log.d(TAG, "plusButton ActionDown");
                    onHandler1("minus");
                }else if(MotionEvent.ACTION_UP == action){
                    android.util.Log.d(TAG, "plusButton Actionup");
                    String value = setminus_button();
                    mHandler.removeCallbacks(r);
                    if(TextUtils.isEmpty(value))
                    {
                        Log.d(TAG , "value = null => power off");
                    }
                    else {
                        mSubOptionsControlTimerTask.stopTimer();
                        if(deviceInfo.getValue3().equalsIgnoreCase(TypeDef.FCU_MODE_HEAT))
                        {
                            mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid5(), deviceInfo.getSort5(), value );
                            mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid5() , deviceInfo.getSort5());
                        }
                        else if(deviceInfo.getValue3().equals(TypeDef.FCU_MODE_COOL))
                        {
                            mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid4(), deviceInfo.getSort4(), value );
                            mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid4() , deviceInfo.getSort4());
                        }
                    }
                }
                return false;
            }
        });

        fcu_plus_button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if(MotionEvent.ACTION_DOWN == action){
                    android.util.Log.d(TAG, "plusButton ActionDown");
                    onHandler1("plus");
                }else if(MotionEvent.ACTION_UP == action){
                    android.util.Log.d(TAG, "plusButton Actionup");
                    String value = setplus_button();
                    mHandler.removeCallbacks(r);
                    if(TextUtils.isEmpty(value))
                    {
                        Log.d(TAG , "value = null => power off");
                    }
                    else
                    {
                        mSubOptionsControlTimerTask.stopTimer();
                        if(deviceInfo.getValue3().equalsIgnoreCase(TypeDef.FCU_MODE_HEAT))
                        {
                            mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid5(), deviceInfo.getSort5(), value );
                            mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid5() , deviceInfo.getSort5());
                        }
                        else if(deviceInfo.getValue3().equals(TypeDef.FCU_MODE_COOL))
                        {
                            mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid4(), deviceInfo.getSort4(), value );
                            mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid4() , deviceInfo.getSort4());
                        }
                    }
                }
                return false;
            }
        });

        //TODO seekbar 는 updatedevice에서 해야할것으로 생각됨
        //seekbar
//        initProgressBar();


        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener(){
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
//                Log.d(TAG, "onProgressChanged..." + progress + "-" + fromUser);
//                if(fromUser == true) SeekBar_Clicked(progress);
            }
            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                Log.d(TAG ,"onSotopTrackingTounch");

                int getlevel = getProgressValue();
                Log.d(TAG, "SeekBar_Clicked..." + getlevel);

                String selvalue = Integer.toString(getlevel);

                mSubOptionsControlTimerTask.stopTimer();
                if(deviceInfo.getValue3().equalsIgnoreCase(TypeDef.FCU_MODE_HEAT))
                {
                    mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid5(), deviceInfo.getSort5(), selvalue );
                    mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid5() , deviceInfo.getSort5());
                }
                else if(deviceInfo.getValue3().equals(TypeDef.FCU_MODE_COOL))
                {
                    mSubOptionsControlTimerTask.setValue(deviceInfo.getSubUuid4(), deviceInfo.getSort4(), selvalue );
                    mSubOptionsControlTimerTask.startThread();
//                            controlDevice(value, deviceInfo.getSubUuid4() , deviceInfo.getSort4());
                }

                SeekBar_Clicked(selvalue);
//                circularSeekBar.setIsTouchEnabled(true);
            }
            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                Log.d(TAG ,"onStartTrackingTouch");
            }
        });
    }

    //onTouch listener
    private Handler mHandler;
    private Runnable r;
    private void onHandler1(final String minus_plus) {
        mHandler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                //TODO minus button
                mSubOptionsControlTimerTask.stopTimer();
                int int_value = 0;
                if(minus_plus.equalsIgnoreCase("minus"))
                {
                    setminus_button();
                }
                else if(minus_plus.equalsIgnoreCase("plus"))
                {
                    Log.d(TAG, "plus button clicked");
                    setplus_button();
                }
                mHandler.postDelayed(r, plus_minus_longButton_time_gap);
            }
        };
        mHandler.postDelayed(r, plus_minus_longButton_time_gap);
    }

    public void SeekBar_Clicked(String value) {
//        int getlevel = getProgressValue();
        Log.d(TAG, "SeekBar_Clicked..." + value);
//
//        String selvalue = Integer.toString(getlevel);
        fcu_data_value.setText(value);
        /*if(deviceInfo.getValue3().equalsIgnoreCase(TypeDef.FCU_MODE_HEAT))
        {
            controlDevice(selvalue, deviceInfo.getSubUuid5() , deviceInfo.getSort5());
        }
        else if(deviceInfo.getValue3().equals(TypeDef.FCU_MODE_COOL))
        {
            controlDevice(selvalue, deviceInfo.getSubUuid4() , deviceInfo.getSort4());
        }*/
    }

    private void initProgressBar(String min , String max) {
        //TODO for 임시값
        //init progress range
        m_LEVEL_MIN = 5;
        m_LEVEL_MAX = 40;
       if(!TextUtils.isEmpty(min))
            m_LEVEL_MIN = Integer.parseInt(min);
        if(!TextUtils.isEmpty(max))
            m_LEVEL_MAX = Integer.parseInt(max);
        m_LEVEL_RANGE = m_LEVEL_MAX - m_LEVEL_MIN;
        Log.d(TAG, "Seekbar range is " + m_LEVEL_RANGE + ": " + m_LEVEL_MIN + "," + m_LEVEL_MAX);
        Log.d(TAG , "db option1 : " + deviceInfo.getOption1() + " , option2 : " + deviceInfo.getOption2());
        circularSeekBar.setMax(m_LEVEL_RANGE);
    }

    public void updateDevice()
    {
        timer_count = true;
        try {
            mTimerCountTask.stopTimer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //TODO 2016-11-16
       deviceUIsetting();
    }

    public void deviceUIsetting()
    {
        Log.d(TAG, "deviceUisetting() ");
        int update_count_flag = 0;
        title_text.setText(deviceInfo.getNickName());
        fcu_current_data_value.setText(deviceInfo.getValue());

        try {
            //FCU mode
            if(deviceInfo.getValue3().equalsIgnoreCase(TypeDef.FCU_MODE_COOL))
            {
                circularSeekBar.setCircleProgressGradientColor(0xff433fa3, 0xff4ec3e5); //Cool
                //seekbar
                initProgressBar(deviceInfo.getOption1() , deviceInfo.getOption2());
                cooling_binary_button.setSelected(true);
                heating_binary_button.setSelected(false);
//            circular_button.setSelected(false);

                fcu_data_scale1.setVisibility(View.VISIBLE);
                fcu_data_scale2.setVisibility(View.VISIBLE);
                fcu_data_value.setVisibility(View.VISIBLE);

                Log.i(TAG, "heating value : " + deviceInfo.getValue4());
                setProgressValue(Integer.valueOf(deviceInfo.getValue4()));
                fcu_data_value.setText(deviceInfo.getValue4());
                fcu_data_scale2.setText(deviceInfo.getScale2());
                ic_dc_warm_cool_icon.setVisibility(View.VISIBLE);
                ic_dc_warm_cool_icon.setImageResource(R.mipmap.ic_dc_cool_l);
                outgoint_ventilation.setVisibility(GONE);

                //TODO 0912
                cooling_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                heating_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
//            circular_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                cooling_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                heating_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
//            circular_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));

            }
            else if (deviceInfo.getValue3().equals(TypeDef.FCU_MODE_HEAT))
            {
                circularSeekBar.setCircleProgressGradientColor(0xffe02f38, 0xfff8b54c); //Warm
                initProgressBar(deviceInfo.getOption3() , deviceInfo.getOption4());
                cooling_binary_button.setSelected(false);
                heating_binary_button.setSelected(true);
//            circular_button.setSelected(false);
                setProgressValue(Integer.valueOf(deviceInfo.getValue5()));
                fcu_data_scale1.setVisibility(View.VISIBLE);
                fcu_data_scale2.setVisibility(View.VISIBLE);
                fcu_data_value.setVisibility(View.VISIBLE);
                fcu_data_value.setText(deviceInfo.getValue5());
                fcu_data_scale2.setText(deviceInfo.getScale3());
                ic_dc_warm_cool_icon.setVisibility(View.VISIBLE);
                ic_dc_warm_cool_icon.setImageResource(R.mipmap.ic_dc_warm_l);
                outgoint_ventilation.setVisibility(GONE);

                //TODO 0912
                cooling_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                heating_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
//            circular_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                cooling_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                heating_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
//            circular_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
            }
            else  if(deviceInfo.getValue3().equals(TypeDef.OP_MODE_VENTILATION))
            {
                Log.i(TAG, "ventilation mode");
                cooling_binary_button.setSelected(false);
                heating_binary_button.setSelected(false);
                circular_button.setSelected(true);
                fcu_data_scale1.setVisibility(View.GONE);
                fcu_data_scale2.setVisibility(View.GONE);
                fcu_data_value.setVisibility(View.GONE);
                circularSeekBar.setCircleProgressGradientColor(0xffe4e3ea, 0xffe4e3ea); //OFF
                circularSeekBar.setProgress(0);
                ic_dc_warm_cool_icon.setVisibility(GONE);
                outgoint_ventilation.setVisibility(View.VISIBLE);
                outgoint_ventilation.setImageResource(R.mipmap.img_dc_sethome_ventilation);

                //TODO 0912
                cooling_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                heating_binary_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
//            circular_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));

                cooling_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                heating_binary_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
//            circular_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(update_count_flag > 0)
            {
                Log.d(TAG," already top up notice");
                update_count_flag = 0;
            }
            else if(update_count_flag == 0)
            {
                MainActivity.getInstance().showToastOnWorking(getResources().getString(R.string.please_reset));
                update_count_flag ++;
            }

        }

        //10-05
        // LLT 현장 스펙에 풍속 없음 제거
/*        try {
            //FCU fan speed
            if(deviceInfo.getValue6().equals("high"))
            {
                high_button.setSelected(true);
                middle_button.setSelected(false);
                low_button.setSelected(false);
                auto_button.setSelected(false);

                //0912
                high_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                middle_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                low_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                auto_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                high_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                middle_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                low_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                auto_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));

            }
            else if(deviceInfo.getValue6().equals("medium"))
            {
                high_button.setSelected(false);
                middle_button.setSelected(true);
                low_button.setSelected(false);
                auto_button.setSelected(false);

                //0912
                high_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                middle_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                low_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                auto_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                high_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                middle_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                low_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                auto_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
            }
            else if(deviceInfo.getValue6().equals("low"))
            {
                high_button.setSelected(false);
                middle_button.setSelected(false);
                low_button.setSelected(true);
                auto_button.setSelected(false);

                //0912
                high_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                middle_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                low_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                auto_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                high_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                middle_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                low_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                auto_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
            }
            else if(deviceInfo.getValue6().equals("auto"))
            {
                high_button.setSelected(false);
                middle_button.setSelected(false);
                low_button.setSelected(false);
                auto_button.setSelected(true);
                //0912
                high_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                middle_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                low_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                auto_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));

                high_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                middle_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                low_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                auto_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
            }
            else
            {
                Log.i(TAG, "fan speed else()");
                high_button.setSelected(false);
                middle_button.setSelected(false);
                low_button.setSelected(false);
                auto_button.setSelected(false);

                high_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                middle_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                low_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                auto_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(update_count_flag > 0)
            {
                Log.d(TAG," already top up notice");
                update_count_flag = 0;
            }
            else if(update_count_flag == 0)
            {
                update_count_flag ++;
                MainActivity.getInstance().showToastOnWorking(getResources().getString(R.string.please_reset));

            }
        }*/

        try{
            if(deviceInfo.getValue7().equals(TypeDef.AWAY_ON))
            {

                away_off_button.setSelected(false);
                away_on_button.setSelected(true);
                //외출 처리
//            title_power_button.setSelected(false);
                fcu_data_value.setVisibility(View.GONE);
                fcu_data_scale1.setVisibility(View.GONE);
                fcu_data_scale2.setVisibility(View.GONE);
                circularSeekBar.setCircleProgressGradientColor(0xffe4e3ea, 0xffe4e3ea); //OFF
                circularSeekBar.setProgress(0);
                ic_dc_warm_cool_icon.setVisibility(GONE);

                outgoint_ventilation.setVisibility(VISIBLE);
                outgoint_ventilation.setImageResource(R.mipmap.img_dc_outdoor);

                //TODO 0912
                away_on_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));
                away_off_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));

                away_off_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));
                away_on_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));

                screenStateByPower(false);
            }
            else if(deviceInfo.getValue7().equals(TypeDef.AWAY_OFF))
            {

                //TODO 다시 보기
                away_off_button.setSelected(true);
                away_on_button.setSelected(false);

                fcu_data_value.setTextColor(getResources().getColor(R.color.data_number_text));
                fcu_data_scale1.setTextColor(getResources().getColor(R.color.data_number_text));
                fcu_data_scale2.setTextColor(getResources().getColor(R.color.data_number_text));
                ic_dc_warm_cool_icon.setImageAlpha(255);
                outgoint_ventilation.setImageAlpha(255);

                outgoint_ventilation.setVisibility(GONE);

                //TODO 0912
                away_on_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
                away_off_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));

                away_off_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
                away_on_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));

                screenStateByPower(true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(update_count_flag > 0)
            {
                Log.d(TAG," already top up notice");
                update_count_flag = 0;
            }
            else if(update_count_flag == 0)
            {
                update_count_flag ++;
                MainActivity.getInstance().showToastOnWorking(getResources().getString(R.string.please_reset));

            }
        }

        try {
            //power on/off
            if(deviceInfo.getValue2().equals(TypeDef.SWITCH_ON))
            {
                title_power_button.setSelected(true);
//            away_off_button.setSelected(true);
//            away_on_button.setSelected(false);
                //TODO on 일때는 + , - 버튼 먹히도록
                if(deviceInfo.getValue7().equals(TypeDef.AWAY_ON))
                {
                    screenStateByPower(false);
                }
                else
                {
                    screenStateByPower(true);
                }

                //0913 회의 내용 적용
                fcu_data_value.setTextColor(getResources().getColor(R.color.data_number_text));
                fcu_data_scale1.setTextColor(getResources().getColor(R.color.data_number_text));
                fcu_data_scale2.setTextColor(getResources().getColor(R.color.data_number_text));
                ic_dc_warm_cool_icon.setImageAlpha(255);
                outgoint_ventilation.setImageAlpha(255);

          /*  //0912
            away_on_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
            away_off_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));

            away_off_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
            away_on_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));*/
            }
            else if(deviceInfo.getValue2().equals(TypeDef.SWITCH_OFF))
            {
                title_power_button.setSelected(false);
//            fcu_data_value.setText(TypeDef.SWITCH_OFF_CAPITAL);
//            fcu_data_scale1.setVisibility(View.GONE);
//            fcu_data_scale2.setVisibility(View.GONE);
                screenStateByPower(false);
                //0913 회의 내용 적용
                fcu_data_value.setTextColor(getResources().getColor(R.color.data_number_text_disable));
                fcu_data_scale1.setTextColor(getResources().getColor(R.color.data_number_text_disable));
                fcu_data_scale2.setTextColor(getResources().getColor(R.color.data_number_text_disable));
                circularSeekBar.setCircleProgressGradientColor(0xffc9c8d6, 0xffc9c8d6); //OFF
//            circularSeekBar.setProgress(0);
//            ic_dc_warm_cool_icon.setVisibility(VISIBLE);
                ic_dc_warm_cool_icon.setImageAlpha(100);
                outgoint_ventilation.setImageAlpha(100);

                //TODO on 일때는 + , - 버튼 안먹히도록

                //0912
         /*   away_on_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_normal));
            away_off_button.setBackground(getResources().getDrawable(R.drawable.middle_round_btn_seletor_selected));

            away_off_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_selected));
            away_on_button.setTextColor(getResources().getColorStateList(R.color.middle_button_text_color_normal));*/

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if(update_count_flag > 0)
            {
                Log.d(TAG," already top up notice");
                update_count_flag = 0;
            }
            else if(update_count_flag == 0)
            {
                update_count_flag ++;
                MainActivity.getInstance().showToastOnWorking(getResources().getString(R.string.please_reset));

            }
        }

        deviceInfo.setbUpdated(false);
    }

    private void screenStateByPower(boolean bpower) {
        if(bpower) {
            //switch on
            circularSeekBar.setPointerColor(0xff6a71cc); //default : 0xff6a71cc
            circularSeekBar.setPointerAlpha(100); //default:100
            circularSeekBar.setIsTouchEnabled(true);
            //사용가능하도록
            fcu_plus_button.setEnabled(true);
            fcu_minus_button.setEnabled(true);
            //버튼 눌림처리해서 색사 어둡게
            fcu_minus_button.setSelected(false); //disable
            fcu_plus_button.setSelected(false); //disable

            heating_binary_button.setEnabled(true);
            cooling_binary_button.setEnabled(true);
//            circular_button.setEnabled(true);
            high_button.setEnabled(true);
            middle_button.setEnabled(true);
            low_button.setEnabled(true);
            auto_button.setEnabled(true);

        }else {
            //switch off
            circularSeekBar.setCircleProgressGradientColor(0xffc9c8d6, 0x88c9c8d6); //disable
            circularSeekBar.setPointerColor(0xffc9c8d6); //default : 0xff6a71cc
            circularSeekBar.setPointerAlpha(0); //default:100
            circularSeekBar.setIsTouchEnabled(false);

            //버튼 눌림처리해서 색사 어둡게
            fcu_plus_button.setSelected(true); //disable
            fcu_minus_button.setSelected(true); //disable
            // 동작 하지 않도록 설정
            fcu_minus_button.setEnabled(false);
            fcu_plus_button.setEnabled(false);

            heating_binary_button.setEnabled(false);
            cooling_binary_button.setEnabled(false);

//            circular_button.setEnabled(false);
            high_button.setEnabled(false);
            middle_button.setEnabled(false);
            low_button.setEnabled(false);
            auto_button.setEnabled(false);


        }
    }

    /* Class Methods(public) ----------------------------------------*/
    public boolean getDeviceValue() {
        boolean getvalue = false;
        //TODO
        try {
            if (deviceInfo.getValue2().equalsIgnoreCase(TypeDef.SWITCH_ON))
                getvalue = true;
            else if(deviceInfo.getValue2().equalsIgnoreCase(TypeDef.SWITCH_OFF))
                getvalue = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return getvalue;
    }

    public String setminus_button()
    {
        Log.d(TAG, "clicked minus button");
        int getlevel = getProgressValue();
        String string_level = null;
        if(!fcu_minus_button.isSelected())
        {
            if (getlevel > m_LEVEL_MIN) {
                getlevel--;
                setProgressValue(getlevel);
                string_level = String.valueOf(getlevel);
                fcu_data_value.setText(string_level);
            }
            else if(getlevel == m_LEVEL_MIN)
            {
                string_level = String.valueOf(m_LEVEL_MIN);
            }

        }
        return string_level;
    }

    public String setplus_button()
    {
        Log.d(TAG, "clicked plus button");
        int getlevel = getProgressValue();
        String string_level = null;
        if(!fcu_minus_button.isSelected()) {
            if (getlevel < m_LEVEL_MAX) {
                getlevel++;
                setProgressValue(getlevel);
                string_level = String.valueOf(getlevel);
                fcu_data_value.setText(string_level);
            }
            else if(getlevel == m_LEVEL_MAX)
            {
                string_level = String .valueOf(m_LEVEL_MAX);
            }
        }
        return string_level;
    }
    public void onClick(View view)
    {
        Log.d(TAG, "onClick");
        // onclick 누르면 무조건 보내기로

        String setting_on_off = null;
        String setting_auto_manual= null;

        timer_count = false;


        if(view.equals(title_back_button))
        {
            Log.d(TAG, "Clicked back Button");

            if(mSubOptionsControlTimerTask != null){
                mSubOptionsControlTimerTask = null;
            }

            if(mTimerCountTask != null){
                mTimerCountTask = null;
            }

            MainActivity.getInstance().finish();
        }
        else if(view.equals(title_power_button))
        {
            Log.d(TAG,"click power button");
            if(deviceInfo.getValue2().equals(TypeDef.SWITCH_OFF))
            {
                // power on
                title_power_button.setSelected(true);
                setting_on_off = TypeDef.SWITCH_ON;

                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid2() + " value : " + setting_on_off);
                controlDevice(setting_on_off, deviceInfo.getSubUuid2() , deviceInfo.getSort2());
            }
            else if(deviceInfo.getValue2().equals(TypeDef.SWITCH_ON))
            {
                // power off
                title_power_button.setSelected(false);
                setting_on_off = TypeDef.SWITCH_OFF;

                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid2() + " value : " + setting_on_off);
                controlDevice(setting_on_off, deviceInfo.getSubUuid2() , deviceInfo.getSort2());
            }
            else
            {
                Log.e(TAG, "else setting_on_off : " + setting_on_off);
            }
        }
        else if(view.equals(cooling_binary_button))
        {
            // on, off , circular 상태 전환
            try {
                setting_auto_manual = TypeDef.FCU_MODE_COOL;

//                    cooling_binary_button.setSelected(true);
//                    heating_binary_button.setSelected(false);
//                    circular_button.setSelected(false);
                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid3());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid3(), TypeDef.SUB_DEVICE_THERMOSTATRUNMODE );

            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else if(view.equals(heating_binary_button))
        {
            // on, off, circular 상태 전환
            try {
                setting_auto_manual = TypeDef.FCU_MODE_HEAT;
//                    heating_binary_button.setSelected(true);
//                    cooling_binary_button.setSelected(false);
//                    circular_button.setSelected(false);

                android.util.Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid3());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid3(), TypeDef.SUB_DEVICE_THERMOSTATRUNMODE );
                Log.d(TAG, " timer_count = true");
                timer_count = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(circular_button))
        {
            // on, off, circular 상태 전환
            try {
                setting_auto_manual = TypeDef.OP_MODE_VENTILATION;
//                    heating_binary_button.setSelected(false);
//                    cooling_binary_button.setSelected(false);
//                    circular_button.setSelected(true);

                android.util.Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid3());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid3(), TypeDef.SUB_DEVICE_THERMOSTATRUNMODE );
                timer_count = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(high_button))
        {
            // hight , middle , low 상태 전환
            try {
                setting_auto_manual = "high";
//                    high_button.setSelected(true);
//                    middle_button.setSelected(false);
//                    low_button.setSelected(false);
//                    auto_button.setSelected(false);

                Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid6());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid6(), TypeDef.SUB_DEVICE_FANSPEED );
                timer_count = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(middle_button))
        {
            // hight , middle , low 상태 전환
            try {
                setting_auto_manual = "medium";
//                    high_button.setSelected(false);
//                    middle_button.setSelected(true);
//                    low_button.setSelected(false);
//                    auto_button.setSelected(false);

                android.util.Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid6());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid6(), TypeDef.SUB_DEVICE_FANSPEED );

                timer_count = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(low_button))
        {
            // hight , middle , low 상태 전환
            try {
                setting_auto_manual = "low";
//                    high_button.setSelected(false);
//                    middle_button.setSelected(false);
//                    low_button.setSelected(true);
//                    auto_button.setSelected(false);

                android.util.Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid6());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid6(), TypeDef.SUB_DEVICE_FANSPEED );

                timer_count = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(auto_button))
        {
            // hight , middle , low 상태 전환
            try {
                setting_auto_manual = "auto";
//                    high_button.setSelected(false);
//                    middle_button.setSelected(false);
//                    low_button.setSelected(false);
//                    auto_button.setSelected(true);

                android.util.Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid6());
                controlDevice(setting_auto_manual, deviceInfo.getSubUuid6(), TypeDef.SUB_DEVICE_FANSPEED );

                timer_count = true;

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(view.equals(away_on_button))
        {
            setting_on_off = TypeDef.AWAY_ON;
            controlDevice(setting_on_off, deviceInfo.getSubUuid7() , deviceInfo.getSort7());
        }
        else if(view.equals(away_off_button))
        {
//            title_power_button.setSelected(true);
            setting_on_off = TypeDef.AWAY_OFF;
            Log.d(TAG, "subUuid : " + deviceInfo.getSubUuid7() + " value : " + setting_on_off);
            controlDevice(setting_on_off, deviceInfo.getSubUuid7() , deviceInfo.getSort7());
//            screenStateByPower(true);
        }

        //TODO 2초 사이에 계속적으로 버튼을 누르면... 틀어짐
        if(view.equals(title_back_button))
        {
            Log.d(TAG,"back button clicked");
        }
        else
        {
            try {
                mTimerCountTask.stopTimer();
                mTimerCountTask.startThread();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public void controlDevice(String value , String SubUuid , String sort) {
        android.util.Log.i(TAG, "controlDevice");
        //AIDL 을 통한 Device 제어
        MainActivity.getInstance().dataManager.setOnOrOffAddSort(
                deviceInfo.getRootUuid(),
                deviceInfo.getRootDevice(),
                SubUuid,
                value,
                sort
        );
    }

    //circle seek bar
    private int getProgressValue() {
        int value = circularSeekBar.getProgress() + m_LEVEL_MIN;
        if(value > m_LEVEL_MAX ) value = m_LEVEL_MAX;
        Log.d(TAG, "getProgressValue: " + value);
        return value;
    }

    private int setProgressValue(int progress) {
        int value = progress - m_LEVEL_MIN;
        if(value < 0 ) value = m_LEVEL_MIN;
        circularSeekBar.setProgress(value);
        Log.d(TAG, "setProgressValue: " + progress + "->" + value);
        return value;
    }

    private void startMonitor() {

        if(m_bmonitor_start == false){

            //handler init
            handler = new Handler(){

                public void handleMessage(Message msg) {
                    Log.d(TAG, "startMonitor handleMessage");
                    if(msg.what == MSG_SEND_ID)
                    {
//                        getData(deviceInfo);
                        //TODO 여기서 sort 에서 point에 대한 update가 들어오면 1초가량 딜레이 주고 updatereport부른다?
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

    class MonitorThread extends Thread {
        public void run() {
            try {
                android.util.Log.d(TAG, "MonitorThread ... Start");

                while(true) {

                    //Update Event와 지연처리시 화면 갱신함
                    if ( deviceInfo.isbUpdated() ) {
                        //UI는 Handler, AsyncTask를 이용해 간접 제어해야함
                        android.util.Log.d(TAG, "Updated ... Event" + MSG_SEND_ID);

                        handler.sendEmptyMessage(MSG_SEND_ID);

                        Thread.sleep(TypeDef.DEVICE_SETUP_TIME_MS[1]);
                    }else
                    {
                        Thread.sleep(TypeDef.REFRESH_POLLING_PERIOD_MS[1]);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNeedtoupdate() {
        boolean bupdate = false;

        if (deviceInfo.isbUpdated()) bupdate = true;
        else if (m_received_Value != getDeviceValue()) bupdate = true;
        return bupdate;
    }

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
                deviceUIsetting();
            }
            else if(msg.what == 3)
            {
                  /* 초시간을 잰다 */
                int div = (int) msg.obj;
                android.util.Log.d(TAG, "mainTime = " + div);
                while (circular_handler_flag)
                {

                    if(div == 0 || div < 0)
                    {
                        Log.d(TAG, "mainTime 0  = " + div);
                        //TODO 다비아스 모드에 따른 처리 필요

                    }else
                    {
                        android.util.Log.d(TAG, "mainTime = " + div);
                        this.sendEmptyMessageDelayed(0, 1000);
                    }
                    div--;
                }
            }
        }
    };

    public class SubOptionsControlTimerTask implements Runnable{
    //circular seek bar 에서 - , + , circular seek bar 라운드 라인 누를때
        String subUuid;
        String subSort;
        String value;

        boolean timerFlag = false;

        //public final int   TIMEOUT      = 10;

        int count = 0;


        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(timerFlag){

                if(count == 600){

                    if(value == null) {
                        //TODO value 가 0이면 보낼필요 없음
                        return;
                    }
                    //TODO 제어 신호 보냄
                    Log.d(TAG, "send controldevice");
                    controlDevice(value, subUuid , subSort);
                    try {
                        mTimerCountTask.stopTimer();
//                        mTimerCountTask.startThread();
                        timer_count = false;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
                count++;
                Log.d(TAG, "count : " + count);
                try {
                    Thread.sleep(CONTROL_OPTIONS_TIMEOUT);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        public void setValue(String subuuid, String subsort, String value){
            this.subUuid = subuuid;
            this.subSort = subsort;
            this.value = value;
        }

        public void startThread(){
            timerFlag = true;
            count = 0;
            mSubOptionsThread = new Thread(mSubOptionsControlTimerTask);
            mSubOptionsThread.start();
        }

        public void stopTimer(){

            count = 0;
            timerFlag = false;


            if(mSubOptionsThread != null){

                try {
                    mSubOptionsThread.interrupt();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            mSubOptionsThread = null;

        }


    }

    public class Timer_count_Task implements Runnable{

        boolean timerFlag = false;
        int count = 0;


        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(timerFlag){

                if(count == 10000){

                    if(timer_count == true) {
                        //TODO 업데이트가 되어 오면 true로 변경
                       break;
                    }
                    else if(timer_count ==false)
                    {
                        //timer
                        Message msg = timercountHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = getResources().getString(R.string.not_controled);//"제어가 동작하지 않았습니다.";
                        timercountHandler.sendMessage(msg);
                        count  = 0;

                        Message msg1 = timercountHandler.obtainMessage();
                        msg1.what = 2;
                        timercountHandler.sendMessage(msg1);
                        break;
                    }
                }
                count++;
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
