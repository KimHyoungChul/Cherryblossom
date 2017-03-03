package com.commax.security;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;

public class EmergencyOccurView extends Activity implements View.OnClickListener {
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    public static Config m_Config = null; /* System Config Object */
    public static SktManager m_SktManager = null; /* Socket Manager Object */
    private static PowerManager m_PowerManager = null;
    private WakeLock m_mWakeLock = null;

    public static EmergencyOccurView m_instance;

    public static EmergencyOccurView getInstance() {
        return m_instance;
    }
    
    /** Called when the activity is first created. */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.emergencyoccur_view);

        // 2017.01.06_yclee : ?????? ?????
        hideNavigationBar();
        
        m_instance = this;

        /* Socket Manager Object */
        if (m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());

        /* System Config Object */
        if (m_Config == null)
            m_Config = new Config(getContentResolver());
        
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
        /* 항상 화면 켜짐 */
        setSystemFullWakeLock();      
        
        /* AgentType 가져옴 */
        m_SktManager.getAgentType();  
        
        /* AgentType에 따라 Layout 적용 */
        if (NameSpace.nAgentType == NameSpace.MobileUserAgentType) {
            /* 홈 모바일의 Wi-Fi 상태 체크 */
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (!wifiManager.isWifiEnabled()) {
                Log.d("wifi", "wifi is Enabled");
                Log.d("wifi", "SSID : " + wifiInfo.getSSID());
                showDialog(NameSpace.DIALOG_NETWORK_FAIL);
            }
            else {
                showDialog(NameSpace.DIALOG_IS_GOON);
            }
        }
        else {
            showDialog(NameSpace.DIALOG_IS_GOON);
        }
        
        /* 비상관련 이벤트 체크 요청 */
        m_SktManager.sendRequestCheckEvent(m_Config.m_strHomeSvrIPAddr,
                NameSpace.ReqEventEmerEventReq, "");
        
        ((Button) findViewById(R.id.btn_close)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_ok)).setOnClickListener(this);
        
        ((Button) findViewById(R.id.btn_close)).setOnTouchListener(new OnTouchListener() {
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

	/* Set System Full Wake Lock */
    public void setSystemFullWakeLock() {
        try {
            if (m_PowerManager == null)
                m_PowerManager = (PowerManager) EmergencyOccurView.getInstance()
                        .getSystemService(Context.POWER_SERVICE);

            if (m_PowerManager != null) {
                if ((m_mWakeLock == null) || (!m_mWakeLock.isHeld())) {
                    m_mWakeLock = m_PowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ON_AFTER_RELEASE | PowerManager.ACQUIRE_CAUSES_WAKEUP, NameSpace.DEBUG_TAG);

                    if (m_mWakeLock != null) {
                        m_mWakeLock.acquire();
                    }
                }
            }
        } catch (Exception e) {
            /* Set System Full Wake Lock Release */
            setSystemFullWakeLockRelease();

            Log.e(NameSpace.DEBUG_TAG, "Power Manager Exception. !!!");
        }
    }

    
    /* Set System Full Wake Lock Release */
    public void setSystemFullWakeLockRelease() {
        if ((m_mWakeLock != null) && (m_mWakeLock.isHeld())) {
            m_mWakeLock.release();
        	m_mWakeLock = null;
    	}
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        
        if (1 == NameSpace.HomeKeyEventCheck) {
            onDestroy();
        }
    }

    @Override
    protected void onUserLeaveHint() {        
        super.onUserLeaveHint();
        
        NameSpace.HomeKeyEventCheck = 1;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        setSystemFullWakeLockRelease();
        
//        ActivityCompat.finishAffinity(this);
        setResult(Activity.RESULT_CANCELED);
        finishAffinity();
		System.exit(0);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_close) {
			onDestroy();
		}
		else if (v.getId() == R.id.btn_ok) {
			m_SktManager.sendRequestEmergencyEvent(m_Config.m_strHomeSvrIPAddr, 0, -1, -1, NameSpace.EmerEventEmerType,
	                  NameSpace.EmerEventOccurType);
		}
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