package com.commax.security;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Process;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EmergencyView extends Activity implements View.OnClickListener {
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
   
    private int nDestroyCount = 0;
    
    public static SoundManager m_SoundManager = null;   /* Sound Manager Object */
    private static AudioManager m_AudioManager = null;
    public static Config m_Config = null; /* System Config Object */
    public static SktManager m_SktManager = null; /* Socket Manager Object */
    
    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receiver
    private PowerManager m_PowerManager = null;
    private WakeLock m_mWakeLock = null;
    private Context m_Context = null;

    public static EmergencyView m_instance;

    public static EmergencyView getInstance() {
        return m_instance;
    }
    
    /* Broadcast Data */
    public static int m_isTest = 0;
    public static int m_masterPort = 0;
    public static int m_slavePort = 0;
    public static int m_emerType = 0;
    public static int m_emerState = 0;
    
    public static int m_emerTypeReq = 0;
    public static int m_emerStateReq = 0;
    
    /* Password Intent 값 */
    public static final String KEY = "passcode";
    public static final int VALID = 1;
    public static final int INVALID = 0;
    
    /* 비상 사운드 타이머 속성값*/
    public static final int ALARM_EFFECT_PLAY_COUNT             = 180;          /* Emergency Alarm Effect Play Count(3분) */
    public static final int ALARM_EFFECT_STOP_COUNT             = 120;          /* Emergency Alarm Effect Stop Count(2분) */
    public static final int ALARM_EFFECT_TOLERANT_COUNT      = 3;            /* Emergency Alarm Effect Tolerant Count(3회) */
    private int m_nEmerAlarmEffectCount = 0;                        /* Emergency Alarm Effect Count */
    private int  m_nEmerAlarmEffectTolerantCount = 0;                /* Emergency Alarm Effect Tolerant Count */
    private boolean m_bEmerAlarmEffectPauseFlag = false;                /* Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
    private boolean m_dwEmerAlarmEffectTimer = false;                   /* Emergency Alarm Effect Play/Stop Timer */
    private boolean m_bEmerAlarmEffectFinishFlag = false;               /* Emergency Alarm Effect Tolerant Max Flag(Emergency Alarm Effect Stop) */

        
    /* 버튼 연속 클릭 금지 */
    private boolean m_buttonClick = false;
    
    /* Timer */
    private Timer m_soundTimer;
    
    /* Task */
    private MyTask myTask;
        
    class MyTask extends TimerTask {
        public void run() {          
            setEmergencyAlarmEffectTolerantTimer();
        }
    };
    
    
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* 센서테스트 : 0, 비상 : 1 */
        Config.SoundType = 1;
        
        /* Full Screen 사용 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /* 홈 모바일 화면 잠금 상태일 경우 잠금 해제 */
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED  
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.emergency_view);

        // 2017.01.06_yclee : ?????? ?????
        hideNavigationBar();
        
        m_Context = this;
        m_instance = this;

        /* Socket Manager Object */
        if (m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());

        /* System Config Object */
        if (m_Config == null)
            m_Config = new Config(getContentResolver());
        
        /* Sound Manager Object */
        if (m_SoundManager == null)
            m_SoundManager = new SoundManager();
        
        /* 버튼 이미지 변경 */
//        ((Button) findViewById(R.id.btn_close)).setOnClickListener(this);          // 닫기 버튼
        ((Button) findViewById(R.id.btn_stop)).setOnClickListener(this);           // 정지
        ((Button) findViewById(R.id.btn_return)).setOnClickListener(this);           // 복귀
        ((Button) findViewById(R.id.btn_security_release)).setOnClickListener(this);       // 방범,외출설정 해제
        
        /* PXD에서 삭제 */
        /* 버튼 동작 */
//        ((TextView) findViewById(R.id.btn_stop)).setOnClickListener(this);
//        ((TextView) findViewById(R.id.btn_return)).setOnClickListener(this);
//        ((TextView) findViewById(R.id.tv_release)).setOnClickListener(this);   
        
        /* 첫 비상 발생시에는 복귀 버튼 안보임 */
        ((Button) findViewById(R.id.btn_return)).setVisibility(View.GONE);           // 복귀
        ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);       // 방범,외출설정 해제
        
        /* 화면 안꺼지게 설정 */
        setSystemFullWakeLock();
        
        /* 비상관련 이벤트 체크 요청 */
        m_SktManager.sendRequestCheckEvent(m_Config.m_strHomeSvrIPAddr,
                NameSpace.ReqEventEmerEventReq, "");
                                
        myTask = new MyTask();
        m_soundTimer = new Timer();
        m_soundTimer.schedule(myTask, 0, 1000);
    }
    
    /*@Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
	}*/


    /* 2015.11.04_yclee : onStart() 추가 */
    @Override
	protected void onResume() {
		super.onResume();

		/* Create Broadcast Receiver */
        createBroadcastReceiver();
	}
    
    /* Create Broadcast Receiver */
    private void createBroadcastReceiver() {
        /* Broadcast Receiver */
        if (m_BroadcastReceiver == null) {
            m_BroadcastReceiver = new BroadcastReceiver() {                
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        Log.i(NameSpace.DEBUG_TAG, "intent.getAction() : " + intent.getAction());

                        /* Emergency Broadcast Message */
                        if (NameSpace.EMERGENCY_ACTION.equals(intent.getAction())) {
                            m_isTest = intent.getExtras().getInt("test");
                            m_masterPort = intent.getExtras().getInt("masterPort");
                            m_slavePort = intent.getExtras().getInt("slavePort");
                            m_emerType = intent.getExtras().getInt("emerType");
                            m_emerState = intent.getExtras().getInt("emerStaus");

                            m_buttonClick = false;
                            EmergencyView.getInstance().eventUpdateMessage(m_emerState, m_emerType);
                        }

                        if (NameSpace.REG_CHECK_EVENT_ACTION.equals(intent.getAction())) {
                            int type = intent.getExtras().getInt("type");
                            if ((type == NameSpace.RegPrevSensorRelease) || (type == NameSpace.RegOutingSensorRelease)) {
                                EmergencyView.getInstance().m_eventRelease.sendMessage(Message.obtain(EmergencyView.getInstance().m_eventRelease,
                                        type, 0, 0));
                            }
                        }

                        if (NameSpace.DBUS_DISCONNECT_ACTION.equals(intent.getAction())) {
                            Log.e(NameSpace.DEBUG_TAG, "DBUS_DISCONNECT_ACTION : " + NameSpace.DBUS_DISCONNECT_ACTION);
                            m_SoundManager.eventSoundStop();
                            EmergencyView.getInstance().displayDialog(NameSpace.MSG_NETWORK_FAIL);
                        }
                        
                        /* 2014.10.07_스크린off시 프로세스 죽임(시스템서비스에서 홈을 불러줌) */
                        /* HomeKey 눌렀을 경우 프로세스 종료 */
                        if (NameSpace.REQ_HOME_CATEGOTY_EVENT.equals(intent.getCategories())) {
                            onDestroy();
                        }

                    }
                    catch (Exception e) {
                        m_SoundManager.eventSoundStop();
                    }     
                }
            };

            /* Emergency Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.EMERGENCY_ACTION));
            /* Register Check Event Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REG_CHECK_EVENT_ACTION));
            /* DBus Disconnect Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.DBUS_DISCONNECT_ACTION));
            /* 2014.10.07_스크린off시 프로세스 죽임(시스템서비스에서 홈을 불러줌) */
            /* HomeKey Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REQ_HOME_CATEGOTY_EVENT));
        }
    }

	private Handler m_eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int m_currentEmerType = -1;
            int m_currentEmerState = -1;
            
            /* PXD에서 삭제 */
//            long now = System.currentTimeMillis();
//            Date date = new Date(now);
//            SimpleDateFormat sdfNowTime = new SimpleDateFormat("yyyy.MM.dd. HH:mm:ss");
//            String srtNowTime = sdfNowTime.format(date);

            if (m_emerType == NameSpace.EmerEventGasType) {
            	((FrameLayout) findViewById(R.id.aniLayout)).setBackgroundResource(R.drawable.bg_emergency03);
            }
            else {
            	((FrameLayout) findViewById(R.id.aniLayout)).setBackgroundResource(R.drawable.bg_emergency02);
            }
            try {
                switch (m_emerState) {
                    /* 비상 발생 */
                    case NameSpace.EmerEventOccurType:
                        Log.i(NameSpace.DEBUG_TAG, "EmerEventOccurType!!!");

                        m_bEmerAlarmEffectPauseFlag = false;
                        setEmergencyAlarmEffectRecovery();

                        ((Button) findViewById(R.id.btn_stop)).setVisibility(View.VISIBLE);
                        ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);

                        if (m_emerType == NameSpace.EmerEventEmerType) {
                            /* 비상 */
                            if (m_isTest == 0) {
                                if ((m_isTest != -1) && ((m_currentEmerType != m_emerType) || (m_currentEmerState != m_emerState))) {
                                    Log.i(NameSpace.DEBUG_TAG, "EmerEventOccurType : " + m_emerType);
                                    Log.i(NameSpace.DEBUG_TAG, "EmerEventOccurState : " + m_emerState);

                                    ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.GONE);
                                    setEmerInfoText(getResources().getString(R.string.msg_occur_panic));
                                    ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.ic_coach01_alarm);
                                }
                            }
                        }
                        /* 화재 */
                        else if (m_emerType == NameSpace.EmerEventFireType) {
                            ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.GONE);
                            setEmerInfoText(getResources().getString(R.string.msg_occur_fire));
                            ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.ic_coach01_fire);
                        }
                        /* 가스 */
                        else if (m_emerType == NameSpace.EmerEventGasType) {
                            ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.GONE);
                            setEmerInfoText(getResources().getString(R.string.msg_occur_gas));
                            ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.ic_coach01_gas);
                        }
                        /* 방범 */
                        else if (m_emerType == NameSpace.EmerEventPrevType) {
                            if (m_emerState == NameSpace.EmerEventStopType) {
                                /* 방범 설정 유무 얻어옴 */
                                if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
                                    ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                                    ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                                    ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.VISIBLE);
                                    ((Button) findViewById(R.id.btn_security_release)).setText(R.string.str_prevrestore);
                                }

                                /* 외출 설정 유무 얻어옴 */
                                else if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
                                    ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                                    ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                                    ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.VISIBLE);
                                    ((Button) findViewById(R.id.btn_security_release)).setText(R.string.str_outtingrelease);
                                }
                            }
                            ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.VISIBLE);
                            setEmerInfoText(getResources().getString(R.string.msg_occur_security));
                            ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.ic_coach01_prevention);
                        }
                        
                        /* 피난사다리 */
                        else if (m_emerType == NameSpace.EmerEventLadderType) {
                            ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.GONE);
                            setEmerInfoText(getResources().getString(R.string.msg_occur_ladder));
                            ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.ic_coach01_ladder);
                        }
                        
                        /* 금고 */
                        else if (m_emerType == NameSpace.EmerEventSafeType) {
                            ((TextView) findViewById(R.id.text_EmerSector)).setVisibility(View.GONE);
                            setEmerInfoText(getResources().getString(R.string.msg_occur_safe));
//                            ((ImageView) findViewById(R.id.img_emer_background)).setBackgroundResource(R.drawable.emergency_safe);
                        }
                        break;

                    /* 비상 정지 */
                    case NameSpace.EmerEventStopType:
                        Log.i(NameSpace.DEBUG_TAG, "EmerEventStopType!!!");

                        if (m_soundTimer != null) {
                            m_soundTimer.cancel();
                            m_soundTimer = null;
                        }

                        m_bEmerAlarmEffectPauseFlag = true;
                        setEmergencyAlarmEffectRecovery();

                        if (m_emerType == NameSpace.EmerEventPrevType) {
                            /* 방범 설정 유무 얻어옴 */
                            if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
                                ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                                ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.VISIBLE);
                                ((Button) findViewById(R.id.btn_security_release)).setText(R.string.str_prevrestore);
                            }

                            /* 외출 설정 유무 얻어옴 */
                            else if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
                                ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                                ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.VISIBLE);
                                ((Button) findViewById(R.id.btn_security_release)).setText(R.string.str_outtingrelease);
                            }

                            else {
                                ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                                ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);
                            }
                        }
                        else {
                            ((Button) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                            ((Button) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                            ((Button) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);
                        }
                        break;

                    /* 비상 복귀 */
                    case NameSpace.EmerEventReturnType:
                        Log.i(NameSpace.DEBUG_TAG, "EmerEventReturnType!!!!");
                        onDestroy();
                        break;
                        
                    case NameSpace.EmerEventNonType:                        
                        onDestroy();
                        break;
                        
                    default:
                    	break;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
    
    
    /* Set Emergency Alarm Effect Tolerant Timer */
    public void setEmergencyAlarmEffectTolerantTimer() {
        /* Emergency Alarm Effect Tolerant Count */
        if(m_nEmerAlarmEffectTolerantCount >= ALARM_EFFECT_TOLERANT_COUNT)
        {
            /* Emergency Alarm Effect Init */
            m_nEmerAlarmEffectCount = 0;

            /* Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
            m_bEmerAlarmEffectPauseFlag = false;

            /* Emergency Alarm Effect Tolerant Count */
            m_nEmerAlarmEffectTolerantCount = 0;
            
            /* Emergency Alarm Effect Tolerant Max Flag(Emergency Alarm Effect Stop) */
            m_bEmerAlarmEffectFinishFlag = true;
            
            if(m_dwEmerAlarmEffectTimer != false) {
                m_soundTimer.cancel();
                m_soundTimer = null;
            }

            m_dwEmerAlarmEffectTimer = false;
        }
        else {
            /* Emergency Alarm Effect Count */
            m_nEmerAlarmEffectCount++;

            Log.i(NameSpace.DEBUG_TAG, "m_nEmerAlarmEffectCount : " + m_nEmerAlarmEffectCount);
            
            /* Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
            if(m_bEmerAlarmEffectPauseFlag == false)
            {               
                /* Emergency Alarm Effect Play Count(3분) */
                if(m_nEmerAlarmEffectCount >= ALARM_EFFECT_PLAY_COUNT)
                {
                    /* Emergency Alarm Effect Count */
                    m_nEmerAlarmEffectCount = 0;

                    /* Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
                    m_bEmerAlarmEffectPauseFlag = true;

                    /* Emergency Alarm Effect Tolerant Count */
                    m_nEmerAlarmEffectTolerantCount++;
                    
                    Log.i(NameSpace.DEBUG_TAG, "m_nEmerAlarmEffectTolerantCount : " + m_nEmerAlarmEffectTolerantCount);
                    Log.i(NameSpace.DEBUG_TAG, "EmerAlarmPlayCount : " + m_nEmerAlarmEffectCount);
                
                    /* Set Emergency Alarm Effect Recovery */
                    setEmergencyAlarmEffectRecovery();         
                }
            }
            else {
                /* Emergency Alarm Effect Stop Count(2분) */
                if(m_nEmerAlarmEffectCount >= ALARM_EFFECT_STOP_COUNT)
                {
                    Log.i(NameSpace.DEBUG_TAG, "EmerAlarmStopCount : " + m_nEmerAlarmEffectCount);
                    
                    /* Emergency Alarm Effect Count */
                    m_nEmerAlarmEffectCount = 0;

                    /* Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
                    m_bEmerAlarmEffectPauseFlag = false;

                    /* Set Emergency Alarm Effect Recovery */
                    setEmergencyAlarmEffectRecovery();
                }
            }
        }
    }
    
    
    /* Set Emergency Alarm Effect Recovery */
    public void setEmergencyAlarmEffectRecovery() {
        Log.i(NameSpace.DEBUG_TAG, "playsound : " + m_emerType + ", " + m_emerState);
        Log.i(NameSpace.DEBUG_TAG, "m_bEmerAlarmEffectPauseFlag : " + m_bEmerAlarmEffectPauseFlag);

        ActivityManager activityManager = (ActivityManager) m_Context.getSystemService(m_Context.ACTIVITY_SERVICE);        
        List<ActivityManager.RunningTaskInfo> runningTask = activityManager.getRunningTasks(1);

        switch (m_emerType) {
            /* 비상 */
            case NameSpace.EmerEventEmerType:
                /* Emergency State가 발생 & Emergency Alarm Effect Pause Flag(TRUE : Alarm Effect Pause) */
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventEmerType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            /* 화재 */
            case NameSpace.EmerEventFireType:
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventFireType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            /* 가스 */
            case NameSpace.EmerEventGasType:
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventGasType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            /* 방범 */
            case NameSpace.EmerEventPrevType:
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventPrevType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            /* 피난사다리 */
            case NameSpace.EmerEventLadderType:
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventLadderType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            /* 금고 */
            case NameSpace.EmerEventSafeType:
                if ((m_emerState == NameSpace.EmerEventOccurType) && (m_bEmerAlarmEffectPauseFlag == false)) {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventSafeType);
                }
                else {
                    m_SoundManager.sendEventMessage(NameSpace.EmerEventNonType);
                }
                break;
                
            default:
            	break;
        }
    }
    
    
    /* Message Handler */
    private Handler m_handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == NameSpace.MSG_NETWORK_FAIL) {
                showDialog(NameSpace.DIALOG_NETWORK_FAIL);
            }
        }
    };
    
    
    /* Event Update Message */
    public void eventUpdateMessage(int status, int type) {
        try {
            /* Message Update Dialog */
            m_eventHandler.sendMessage(Message.obtain(m_eventHandler, status, type, 0));
        } catch (Exception ex) {

        }
    }
    
        
    /* Broadcast Release Handler */
    private Handler m_eventRelease = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case NameSpace.RegOutingSensorRelease:
                ((TextView) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);
                break;
            case NameSpace.RegPrevSensorRelease:
                ((TextView) findViewById(R.id.btn_security_release)).setVisibility(View.GONE);
                break;
            default:
            	break;
            }
        }
    };
    
    
    /* Set System Full Wake Lock */
    // 비상 발생시 화면 꺼져있으면 LCD 키자.!
    public void setSystemFullWakeLock() {        
        try {
            if (m_PowerManager == null)
                m_PowerManager = (PowerManager) EmergencyView.getInstance()
                        .getSystemService(Context.POWER_SERVICE);

            if (m_PowerManager != null) {
                if ((m_mWakeLock == null) || (m_mWakeLock.isHeld() == false)) {
                    m_mWakeLock = m_PowerManager.newWakeLock(
                            PowerManager.FULL_WAKE_LOCK
                                    | PowerManager.ON_AFTER_RELEASE
                                    | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                                    NameSpace.DEBUG_TAG);

                    if (m_mWakeLock != null) {
                        m_mWakeLock.acquire();
                    }
                }
            }
        } catch (Exception e) {
            /* Set System Full Wake Lock Release */
            setSystemFullWakeLockRelease();

            Log.e(NameSpace.DEBUG_TAG, "Power Manager Exception. !!! : " + e.getMessage());
        }
    }

    /* Set System Full Wake Lock Release */
    public void setSystemFullWakeLockRelease() {
        if ((m_mWakeLock != null) && (m_mWakeLock.isHeld() == true)) {
            m_mWakeLock.release();
            m_mWakeLock = null;
        }        
    }
    
    
    public void displayDialog(int id) {
        showDialog(id);
    }
    
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case NameSpace.MSG_NETWORK_FAIL:
                return new AlertDialog.Builder(this)/*.setIcon(R.drawable.icon_info)*/.setTitle(R.string.msg_title_info)
                        .setMessage(R.string.msg_check_network)
                        .setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();                                
                                finish();
                            }
                        }).create();
            default:
                return null;
        }
    }

    
    public void setEmerInfoText(String infoTxt) {
        PropertyUtil pu = PropertyUtil.getInstance();
        int index = 0;
        
        ((TextView) findViewById(R.id.text_comment)).setText(infoTxt);
//        ((TextView) findViewById(R.id.tv_EmerTime)).setText(nowTime);
        
        Animation emer_ani = AnimationUtils.loadAnimation(m_Context, R.anim.emer_ani);
        ((FrameLayout) findViewById(R.id.aniLayout)).startAnimation(emer_ani);
        
        
		if (m_emerType == NameSpace.EmerEventPrevType) {
			for (int i = 1; i <= NameSpace.MAXSENSOR; i++) {
				if (i == m_masterPort) {
					index = m_masterPort;
				}
			}
			
			if (pu.getProperty("sensor_" + index) == null) {
				((TextView) findViewById(R.id.text_EmerSector)).setText(" (" + getResources().getString(R.string.str_sector) + index + ")");
			}
			else {
				((TextView) findViewById(R.id.text_EmerSector)).setText(" (" + pu.getProperty("sensor_" + index) + ")");
			}
		}
    }
    
    
    /* 볼륨키, 홈, 백키 동작 못하도록 막음 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
            case KeyEvent.KEYCODE_HOME:
        		return true;
            case KeyEvent.KEYCODE_BACK:
        		return true;
            default:
            	break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /* PXD에서 삭제 */
//    @Override
//    public boolean onTouch(View v, MotionEvent event) {       
//        int n_ImageID = 0;      
//        
//        if ((event.getAction() == MotionEvent.ACTION_DOWN)
//                || (event.getAction() == MotionEvent.ACTION_UP)) {
//            switch (v.getId()) {
////                /* 홈 버튼 */
////                case R.id.btn_home: 
////                    n_ImageID = (event.getAction() == MotionEvent.ACTION_DOWN) 
////                        ? R.drawable.home_02 : R.drawable.home_01;
////                    break;
//                    
//                /* 메뉴 */
//                case R.id.btn_stop:        // 정지
//                    n_ImageID = (event.getAction() == MotionEvent.ACTION_DOWN) 
//                        ? R.drawable.btn_stop_02 : R.drawable.btn_stop_01;    
//                    break;
//                    
//                case R.id.btn_return:        // 복귀
//                    n_ImageID = (event.getAction() == MotionEvent.ACTION_DOWN) 
//                        ? R.drawable.btn_return_02 : R.drawable.btn_return_01;   
//                    break;
//                    
//                case R.id.tv_release:        // 해제
//                    n_ImageID = (event.getAction() == MotionEvent.ACTION_DOWN) 
//                        ? R.drawable.btn_release_02 : R.drawable.btn_release_01;
//                    break;
//                default:
//                	break;
//            }           
//            
//            ((TextView)findViewById(v.getId())).setBackgroundDrawable(m_Context.getResources().getDrawable(n_ImageID));
//        }
//        
//        return false;
//    }
    

    @Override
    public void onClick(View v) {
        if (m_buttonClick == false) {
            switch (v.getId()) {
                /* 닫기 버튼 */
//                case R.id.btn_close:                    
//                    Intent intent_home = new Intent();
//                    intent_home.setAction("android.intent.action.MAIN");
//                    intent_home.addCategory("android.intent.category.HOME");
//                    intent_home.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
//                            | Intent.FLAG_ACTIVITY_FORWARD_RESULT
//                            | Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
//                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                    startActivity(intent_home);
//                    intent_home=null;
//                    
//                    break;

                /* 정지 */
                case R.id.btn_stop:
                    m_buttonClick = true;

                    Intent intent_stop = new Intent();
                    intent_stop.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
                    startActivityForResult(intent_stop, 0);
                    
                    break;

                /* 복귀 */
                case R.id.btn_return:
                    m_buttonClick = true;

                    m_SktManager.sendRequestEmergencyEvent(m_Config.m_strHomeSvrIPAddr,
                            m_isTest, m_masterPort, m_slavePort, m_emerType,
                            NameSpace.EmerEventReturnType);
                    
                    m_emerTypeReq = m_emerType;
                    m_emerStateReq = NameSpace.EmerEventReturnType;
                    break;

                /* 방범, 외출설정해제 */
                case R.id.btn_security_release:
                    /* 방범 설정 상태일 경우 */
                    if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
                        /* 방범구역 해제 */
                        m_SktManager.releasePreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
                        
                        ((TextView) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                        
                        Intent sendintent = new Intent();
                        sendintent.setAction("ACTION_SECURITY_RELEASE");
                        sendintent.putExtra("SECURITY_DATA", "release");
                        sendBroadcast(sendintent);
                        sendintent = null;
                    }

                    /* 외출 설정 상태일 경우 */
                    if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
                        /* 외출설정 해제 */
                        m_SktManager.releaseOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);
                        
                        ((TextView) findViewById(R.id.btn_stop)).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.btn_return)).setVisibility(View.VISIBLE);
                        
                        Intent sendintent = new Intent();
                        sendintent.setAction("ACTION_AWAY_RELEASE");
                        sendintent.putExtra("AWAY_DATA", "release");
                        sendBroadcast(sendintent);
                        sendintent = null;
                    }
                    break;
                    
				default:
					break;
            }
        }
    }
    
        
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        m_buttonClick = false;
        
        Log.e(NameSpace.DEBUG_TAG, "onActivityResult()");

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {                    
                if (data.getExtras().getInt(KEY) == VALID) {                       
                    finishActivity(0);
                    
                    m_SktManager.sendRequestEmergencyEvent(m_Config.m_strHomeSvrIPAddr,
                            m_isTest, m_masterPort, m_slavePort, m_emerType,
                            NameSpace.EmerEventStopType);
                    
                    m_emerTypeReq = m_emerType;
                    m_emerStateReq = NameSpace.EmerEventStopType;
                }
                else {
                    finishActivity(0);
                }
            }
        }
    }
    
    
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! single on onRestart()");       
        
        if((m_emerTypeReq == m_emerType) && (m_emerStateReq == m_emerState))
            return;
        
        Log.e(NameSpace.DEBUG_TAG, "m_emerState : " + m_emerState);
                 
        try {
            ActivityManager actvityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(1);

            for (Iterator iterator = taskInfos.iterator(); iterator.hasNext();) {
                RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();

//                Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! runningTaskInfo : " + runningTaskInfo.topActivity.getClassName());
                
                if (runningTaskInfo.topActivity.getClassName().equals("com.commax.sipra.SipCallView")) {
                    m_bEmerAlarmEffectPauseFlag = true;
                    setEmergencyAlarmEffectRecovery();
                }
                else {                    
//                    Log.e(NameSpace.DEBUG_TAG, "m_emerState : " + m_emerState);
                    
                    if (m_emerState == NameSpace.EmerEventOccurType) {
                        m_buttonClick = false;

                        m_bEmerAlarmEffectPauseFlag = false;
                        setEmergencyAlarmEffectRecovery();

                        m_AudioManager = (AudioManager) EmergencyView.getInstance().getSystemService(Context.AUDIO_SERVICE);
                        m_AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, NameSpace.EMERGENCY_OCCUR_STREAM_MUSIC,
                                AudioManager.FLAG_ALLOW_RINGER_MODES);
                    }
                }
            }
        } catch (Exception e) {
            Log.i(NameSpace.DEBUG_TAG, "Get topActivity failed in EmergencyView!!!");
        }
   }
    
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(0 != nDestroyCount)
            return;
        
        Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! single onDestroy()");  
        
        m_bEmerAlarmEffectPauseFlag = true;
        setEmergencyAlarmEffectRecovery(); 

        m_buttonClick = false;   
        
        if (null != m_BroadcastReceiver) {
            unregisterReceiver(m_BroadcastReceiver);
            m_BroadcastReceiver = null;
        }         
                
        if (null != m_soundTimer) {
            m_soundTimer.cancel();
            m_soundTimer = null;
        }
        
        if (null != m_eventHandler) {
            m_eventHandler.removeMessages(0);
            m_eventHandler = null;
        }
        
        if (null != m_handler) {
            m_handler.removeMessages(0);
            m_handler = null;
        }
        
        if (null != m_eventRelease) {
            m_eventRelease.removeMessages(0);
            m_eventRelease = null;
        }
                       
        setSystemFullWakeLockRelease();  
        nDestroyCount++;
        
        ((FrameLayout) findViewById(R.id.aniLayout)).clearAnimation();
        
//        ActivityCompat.finishAffinity(this);
        setResult(Activity.RESULT_CANCELED);
        finishAffinity();
		System.exit(0);
    }


	@Override
	protected void onPause() {
		super.onPause();
		Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! single onPause()"); 
	}


	@Override
	protected void onStop() {
		super.onStop();
		Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! single onStop()"); 
	}

    private void hideNavigationBar(){

        try {
            // ???? ??? ????? ?? ????
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
}