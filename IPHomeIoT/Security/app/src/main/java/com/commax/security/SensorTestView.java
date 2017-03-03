package com.commax.security;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Process;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SensorTestView extends Activity {
    /* CountDown 매개변수 */
    private int COUNTDOWN_TIME_SET = 300000;         // 5분 동안
    private int COUNTDOWN_TIME_INTERVAL = 1000;       // 1초 단위로
    
    private int playcount = 0;

    public static Config m_Config = null;                       // System Config Object
    public static SktManager m_SktManager = null;               // Socket Manager Object

    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receiver
    private PowerManager m_PowerManager = null;
    private WakeLock m_mWakeLock = null;
    
    private Context m_Context = null;

    public static SensorTestView m_instance;

    public static SensorTestView getInstance() {
        return m_instance;
    }
    
    public static SoundManager m_SoundManager = null;   /* Sound Manager Object */
    
    private CountDown m_countDown = null;                                  // CountDown

    private Button btn_close;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        
        /* 센서테스트 : 0, 비상 : 1 */
        Config.SoundType = 0;
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED  
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD  
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        
        setContentView(R.layout.sensortest_view);

        // 2017.01.06_yclee : 네비게이션바 안나오도록
        hideNavigationBar();
        
        m_Context = this;
        m_instance = this;
        
        /* Socket Manager Object */
        if(m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());

        /* System Config Object */
        if(m_Config == null)
            m_Config = new Config(getContentResolver());      
        
        /* Sound Manager Object */
        if (m_SoundManager == null)
            m_SoundManager = new SoundManager();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        btn_close = (Button) findViewById(R.id.btn_close);	// 닫기
        
        /* 버튼 터치시 Opacity 변경 */
        btn_close.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = ((Button)findViewById(R.id.btn_close)).getBackground();
					alpha.setAlpha(0x99);	// Opacity 60%
				}
				else if ((event.getAction() == MotionEvent.ACTION_UP)) {
					Drawable alpha = ((Button)findViewById(R.id.btn_close)).getBackground();
					alpha.setAlpha(0xff);	// Opacity 100%
				}
				
				return false;
			}
		});

        btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onDestroy();
			}			
		});
        
        /* 화면 안꺼지게 설정 */
        setSystemFullWakeLock();    
        
        /* Create Broadcast Receiver */
        createBroadcastReceiver();
        
        /* 센서 테스트모드 설정 */
        m_SktManager.setSensorTestModeRequest(m_Config.m_strHomeSvrIPAddr, true);
        
        /* CountDown 객체생성 */
        m_countDown = new CountDown(COUNTDOWN_TIME_SET, COUNTDOWN_TIME_INTERVAL);
        m_countDown.start();
    }
    
    
    /* Create Broadcast Receiver */
    protected void createBroadcastReceiver() {
        /* Broadcast Receiver */
        if(m_BroadcastReceiver == null) {
            m_BroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    /* Emergency Broadcast Message */
                    if(NameSpace.EMERGENCY_ACTION.equals(intent.getAction())) {
                        /* Display Security Sensor Status */
                        displaySecuritySensorStatus(intent);
                    }
                }
            };

            /* Emergency Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.EMERGENCY_ACTION));
            /* HW HomeKey Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REQ_HOME_KEY_EVENT));
            /* HomeKey Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REQ_HOME_CATEGOTY_EVENT));
        }
    }
    
    
    public class CountDown extends CountDownTimer {

        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            onDestroy();
        }

        public void onTick(long millisUntilFinished) {
           ((TextView) findViewById(R.id.txt_remind_time)).setText(formatTime(millisUntilFinished));
        }
    }

    public String formatTime(long millis) {
        String output = "00:00";
        long seconds = millis / 1000;
        long minutes = seconds / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String secondsD = String.valueOf(seconds);
        String minutesD = String.valueOf(minutes);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (minutes < 10)
            minutesD = "0" + minutes;

        output = minutesD + " : " + secondsD;
        return output;
    }
   
    
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
    
    
    /* Display Security Sensor Status */
    public void displaySecuritySensorStatus(Intent intent) {
        int nMasterPort = intent.getExtras().getInt("masterPort");
        int nEmerType = intent.getExtras().getInt("emerType");
        int nEmerState = intent.getExtras().getInt("emerStaus");        
        
        switch (nEmerType) {
            /* 비상 */
            case NameSpace.EmerEventEmerType:
                ((CheckBox) findViewById(R.id.ckb_emer))
                        .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                if (0 < playcount) {
                    SoundManager.getInstance().setEffectSoundPlay(R.raw.tick, false);
                }
                Log.i("*++*+*+*+*+*+", "Play Count : " + playcount);
                playcount++;
                break;

            /* 화재 */
            case NameSpace.EmerEventFireType:
                ((CheckBox) findViewById(R.id.ckb_fire))
                        .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                break;

            /* 가스 */
            case NameSpace.EmerEventGasType:
                ((CheckBox) findViewById(R.id.ckb_gas))
                        .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                break;

            /* 방범 */
            case NameSpace.EmerEventPrevType:                
                switch (nMasterPort) {
                    /* 방범 1 구역 */
                    case NameSpace.MasterPortPrev01Type:
                        ((CheckBox) findViewById(R.id.ckb_sector1))
                                .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                        break;
                    /* 방범 2 구역 */
                    case NameSpace.MasterPortPrev02Type:
                        ((CheckBox) findViewById(R.id.ckb_sector2))
                                .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                        break;
                    /* 방범 3 구역 */
                    case NameSpace.MasterPortPrev03Type:
                        ((CheckBox) findViewById(R.id.ckb_sector3))
                                .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                        break;
                    /* 방범 4 구역 */
                    case NameSpace.MasterPortPrev04Type:
                        ((CheckBox) findViewById(R.id.ckb_sector4))
                                .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                        break;
                    /* 방범 5 구역 */
                    case NameSpace.MasterPortPrev05Type:
                        ((CheckBox) findViewById(R.id.ckb_sector5))
                                .setChecked((nEmerState == NameSpace.EmerEventOccurType) ? false : true);
                        break;
                        
                    default:
                    	break;
                }
                break;
                
            default:
            	break;
        }
    }

    @Override
    protected void onUserLeaveHint() {        
        super.onUserLeaveHint();
        
        NameSpace.HomeKeyEventCheck = 1;
    }
    
    protected void onDestroy() {
        super.onDestroy();

        if(this.m_BroadcastReceiver != null) {
            this.unregisterReceiver(this.m_BroadcastReceiver);
        }
        
        /* 센서 테스트모드 해제 
         * 반드시 해제 해주어야 한다. 센서 테스트 모드상태에서 해재 안하고 나가버리면
         * 비상이 왔을 경우 비상벨이 울리지 않는 문제가 발생하게 된다.
         */        
        m_SktManager.setSensorTestModeRequest(m_Config.m_strHomeSvrIPAddr, false);
        setResult(RESULT_OK, new Intent());
        
        setResult(Activity.RESULT_CANCELED);
        finishAffinity();
		System.exit(0);
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
}