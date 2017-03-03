package com.commax.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.commax.control.Common.TwoLevelCircularProgressBar;
import com.commax.control.Common.TypeDef;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by OWNER on 2016-12-22.
 */
public class DialogActivity extends Activity {
    String TAG = DialogActivity.class.getSimpleName();

    public static DialogActivity _instance;
    public static DialogActivity getInstance()  {
        return _instance;
    }

    Button mRightButton;
    TextView title_text;
    TextView component_text;
    TwoLevelCircularProgressBar circle_progressbar;
    //setcontrol intent int number
    public int call_int = 0;
    public ArrayList<String> add_raw_list;
    Context mContext;

    int sec = 0;
    boolean circle_progress_timer_flag = true;
    //정적 BroadCastReceiver
    static final String PAM_ACTION = "com.commax.services.AdapterService.PAM_ACTION";   /* PAM을 통해 report 받음(add, remove, update) */
    private BroadcastReceiver mReceiver = null;
    /*Timer*/
    int mainTime = 5;
    boolean timer_flag = true;
    boolean add_report_send = false;
    boolean add_report_setcontrol_send = false;

    //for timer counter
    CountDown m_countDown;
    TextView timercount;

    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_one_button_timer);
        _instance = this;
        mContext = this;
        try{
            Intent intent = getIntent();
            if(intent != null){
                Log.d(TAG, "intent : " + intent);
                call_int  = intent.getIntExtra("tapid",0);  //call_int -> 11 : setcontrol에서 DialogActivity 호출
                Log.d(TAG, "intent int call_int = " + call_int);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        add_raw_list = new ArrayList<String>();
        if (TypeDef.IPHomeIoT_NAVIGATION_BAR)
        {
            hideNavigationBar();
        }

        init();
    }

    public void init()
    {
        Log.d(TAG, "init()");
        mRightButton = (Button) findViewById(R.id.btn_ok);
        title_text = (TextView)findViewById(R.id.title_text);
        component_text = (TextView)findViewById(R.id.popup_component);

        title_text.setText(getString(R.string.menu_add_string));
        component_text.setText(getString(R.string.popup_device_add_text));

        circle_progressbar = (TwoLevelCircularProgressBar)findViewById(R.id.circle_progressbar);

        mHandler.sendEmptyMessage(0);
        //for timer counter
        m_countDown = new CountDown(60000, 1000);//1분 카운트 , 1초 간격으로
        m_countDown.start();
        timercount = (TextView)findViewById(R.id.timer);
        timercount.setVisibility(View.VISIBLE);

    }

    private void hideNavigationBar(){

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

    public void  onClick(View view)
    {
        Log.d(TAG, "onClick()");
        if(view.equals(mRightButton))
        {
            //cancel 버튼 누름
            try
            {
                if(call_int != TypeDef.setcontrol_add_dialog)
                {
                    MainActivity.getInstance().sendDeviceAddcancleCommand();
                }
                else
                {
                    Log.d(TAG, "array list size = " + add_raw_list.size());
                    //setcontrol 에 addreport 값 넘겨주기
                    Intent intent = new Intent();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(TypeDef.EXTRA_ADD_STRING, add_raw_list);
                    bundle.putBoolean(TypeDef.ADD_CANCEL, true);

                    intent.putExtras(bundle);
                    add_report_setcontrol_send = true;
                    setResult(RESULT_OK, intent);
                    mainTime = 0;

                }
                circle_progress_timer_flag =  false;
                finish();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //외곽 터치시 다이얼로그 닫히는 거 막기 위해서 onTuouchEvent 를 false 로 리턴한다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            /* 초시간을 잰다 */
            if(msg.what == 0)
            {
                if(circle_progress_timer_flag)
                {
                    circle_progressbar.setProgressValue(sec);
                    if(sec >= 60){
                        Log.d(TAG, "over 1min");
                        if(call_int != TypeDef.setcontrol_add_dialog)
                        {
                            MainActivity.getInstance().showToastBox(getString(R.string.try_agin), true);
                        }
                        else
                        {
                            if(add_raw_list.size() > 0)
                            {
                                //1분이 지나면 보내서 list 의 사이즈가 존재하면
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putStringArrayList("EXTRA_ADD_STRING", add_raw_list);
                                bundle.putBoolean("ADD_CANCEL", false);
                                intent.putExtras(bundle);
                                setResult(RESULT_OK, intent);
                            }
                        }
                        finish();
                    }
                    else
                    {
                        sec++;
                        this.sendEmptyMessageDelayed(0, 1000);
                    }
                }
            }
            else if(msg.what == 1)
            {
                mainTime--;
                Log.d(TAG, "mainTime = " + mainTime);

                if (mainTime == 0 || mainTime < 0) {
                    Log.d(TAG, "mainTime 0  = " + mainTime);
                    circle_progress_timer_flag =  false;

                    //setcontrol 에 addreport 값 넘겨주기
                    Intent intent = new Intent();

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(TypeDef.EXTRA_ADD_STRING, add_raw_list);
                    bundle.putBoolean(TypeDef.ADD_CANCEL, false);
                    intent.putExtras(bundle);
                    add_report_setcontrol_send = true;
                    setResult(RESULT_OK, intent);

                    finish();
                }
                else
                {
                    Log.d(TAG, "mainTime = " + mainTime);
                    this.sendEmptyMessageDelayed(1, 1000);
                }
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if(call_int == 11)
        {
            registerReceiver();
        }
    }

    @Override
    public void onPause() {

        Log.d(TAG, "onPause()");
        if(call_int == 11)
        {
            if((!add_report_setcontrol_send) && (add_raw_list.size() > 0))
            {
                Log.d(TAG ,"list size  > 0 => size : " + add_raw_list.size());

                //setcontrol 에 addreport 값 넘겨주기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList(TypeDef.EXTRA_ADD_STRING, add_raw_list);
                        bundle.putBoolean(TypeDef.ADD_CANCEL, true);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);

                        Log.d(TAG ,"list size  > 0 => size : " + add_raw_list.size());
                    }
                });
            }
            unregisterReceiver();
        }


        if(m_countDown != null){
            Log.d(TAG, "m_countDown != null ");
            m_countDown.cancel();
        }
        else
        {
            Log.d(TAG, " m_countDonw == null ");
        }

        super.onPause();
    }

   /* @Override
    public void finish() {
        Log.d(TAG, "finish()");
        if(call_int == 11)
        {
            if((!add_report_setcontrol_send) && (add_raw_list.size() > 0))
            {
                Log.d(TAG ,"list size  > 0 => size : " + add_raw_list.size());

                //setcontrol 에 addreport 값 넘겨주기
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(TypeDef.EXTRA_ADD_STRING, add_raw_list);
                bundle.putBoolean(TypeDef.ADD_CANCEL, true);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);

                Log.d(TAG ,"list size  > 0 => size : " + add_raw_list.size());
            }
            unregisterReceiver();
        }
        super.finish();

    }*/


    @Override
    protected void onStop() {
        super.onStop();

    }

    private void registerReceiver() {
        Log.d(TAG, "registerReceiver()");
        if(mReceiver != null)
        return;

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(PAM_ACTION);
        this.mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG , "action : " + action);
                if (intent.getAction().equals(PAM_ACTION))
                {
                     /* PAM을 통해 report 받음(add, remove, update) */
                    String strVal = intent.getStringExtra("eventString");
                    //Log.d(TAG, "PAM_ACTION : " + strVal);
                    Log.v(TAG, "PAM_ACTION event... ");
                    String event = getEventType(strVal); //add, remove, update report type 분별

                    if (!TextUtils.isEmpty(event)) {
                        switch (event) {
                            case TypeDef.COMMAND_RESPONSE_REPORT: //update report 일 경우 Device 정보 갱신
                                break;
                            case TypeDef.COMMAND_RESPONSE_ADD_REPORT: //add report 일 경우 Device 정보 Rescan
                                addReport(strVal);
                                break;
                            case TypeDef.COMMAND_RESPONSE_REMOVE_REPORT: //remove report 일 경우 Device 정보 Rescan
                                break;
                        }
                    }
                }
            }
        };
        this.registerReceiver(this.mReceiver, theFilter);
    }
    private void unregisterReceiver() {
        if(mReceiver != null)
        this.unregisterReceiver(mReceiver);
    }

    public void addReport(String raw)
    {
        if(!add_report_send)
        {
            mHandler.sendEmptyMessage(1);
            add_report_send = true;
        }

        //TODO addreport 처음 한번 받은 후로 3초간 타이머하여 arraylist 에 넣어진 list를 보낸다.
        Log.d(TAG, "raw : " + raw);
        add_raw_list.add(raw);
        for(int i = 0 ; i < add_raw_list.size() ; i++)
        {
            Log.d(TAG, "LIst(" +i+") : " + add_raw_list.get(i));
        }

    }

    private String getEventType(String str){            /* event classification (add, remove, update) */

        String raw = str;
        JSONObject jObject=null;
        String command = "no";

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject!=null) {
                    command = jObject.getString(TypeDef.CGP_KEY_COMMAND);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(command)) {
            switch (command) {
                case TypeDef.COMMAND_RESPONSE_ADD_REPORT:
                    return command;
                case TypeDef.COMMAND_RESPONSE_REMOVE_REPORT:
                    return command;
                case TypeDef.COMMAND_RESPONSE_REPORT:
                    return command;
                default:
                    return "no";
            }
        }
        return "no";
    }

    //TODO
    private String getFormattedString(int resId, String value){
        String strFrom = "";
        strFrom = getResources().getString(resId);
        String strNum = TextUtils.htmlEncode(value);
        String strResult = String.format(strFrom, strNum);
        return strResult;
    }


    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {

            Log.i(TAG, "Count onFinish !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            finish();
        }

        public void onTick(long millisUntilFinished) {
//            Log.d(TAG, "onTick : " + formatTime(millisUntilFinished));
//            m_textSecond.setText(getFormattedString(com.commax.elevator.R.string.second , formatTime(millisUntilFinished)));
            timercount.setText(formatTime(millisUntilFinished));
        }
    }

    @SuppressWarnings("unused")
    public String formatTime(long millis) {
        String output = "00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        output = minutesD + ":" + secondsD;
        return output;
    }

}
