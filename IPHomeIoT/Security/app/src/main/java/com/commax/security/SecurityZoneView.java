package com.commax.security;

import java.util.Iterator;
import java.util.List;

import com.commax.security.dialog.CustomAlertsDialog;
import com.commax.security.dialog.SecurityZoneEditDialog;
import com.commax.security.dialog.SecurityZoneSetDialog;
import com.commax.security.Log;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SecurityZoneView extends Activity implements OnClickListener {
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    private static int nType = 0;   

    private static Config m_Config = null;                       // System Config Object
    private static SktManager m_SktManager = null;               /* Socket Manager Object */
    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receiver
    
    private Context m_Context = null;    
    
    public static SecurityZoneView m_instance;

    public static SecurityZoneView getInstance() {
        return m_instance;
    }
    
    /* Password Intent 값 */
    public static final String KEY = "passcode";
    public static final int VALID = 1;
    public static final int INVALID = 0;
        
    public static ProgressDialog mLoagingDialog = null; 
    public static ProgressDialog mStartLoagingDialog = null; 
    
    /* 버튼 */
    private ImageButton title_btn_close;
    private Button tab_btn_outmode;
    private Button tab_btn_security;
    private Button btn_option;
    private Button btn_set;
    
    /* 구역 */
    private FrameLayout sector1;
    private FrameLayout sector2;
    private FrameLayout sector3;
    private FrameLayout sector4;
    private FrameLayout sector5;
    
    /* 구역 이름 */
    private TextView sector1_name;
    private TextView sector2_name;
    private TextView sector3_name;
    private TextView sector4_name;
    private TextView sector5_name;
    
    /* 구역 상태 (ON/OFF) */
    private TextView sector1_status;
    private TextView sector2_status;
    private TextView sector3_status;
    private TextView sector4_status;
    private TextView sector5_status;
    
    private static PreferenceTool mTool = null;
	
	/* PreferenceTool Key Values */
    private static final String Sector1 = "sector1";
    private static final String Sector2 = "sector2";
    private static final String Sector3 = "sector3";
    private static final String Sector4 = "sector4";
    private static final String Sector5 = "sector5";
    private static final String Sector1_Checked = "sector1_checked";
    private static final String Sector2_Checked = "sector2_checked";
    private static final String Sector3_Checked = "sector3_checked";
    private static final String Sector4_Checked = "sector4_checked";
    private static final String Sector5_Checked = "sector5_checked";
        
    private static final int REQUESTCODE_OPTION = 0;
    private static final int REQUESTCODE_RELEASE = 1;
    private static final int REQUESTCODE_OUTMODE_RELEASE = 2;
    
    /* 타이틀 레이아웃 */
    private LinearLayout mTitleLL;
    private LinearLayout mSecurityZone;
    
    
    /* Alerts 다이얼로그 */
    private static CustomAlertsDialog alertsDialog;

    private Intent intentTimer = null;

    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		
		/* Socket Manager Object */
		if (m_SktManager == null)
			m_SktManager = new SktManager(getApplicationContext());
		
		
		NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
		
		if (NameSpace.SecurityPreventMode == NameSpace.GetMode) {
			Intent intentRelease = new Intent();
			intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
			intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intentRelease, REQUESTCODE_RELEASE);
			intentRelease = null;
		}
	}
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {       
        super.onCreate(savedInstanceState);
        
        Log.i(NameSpace.DEBUG_TAG, "SecurityZoneView onCreate");
        
        /* Socket Manager Object */
        if(m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());
        
        /* System Config Object */
        if(m_Config == null)
            m_Config = new Config(getContentResolver());

        m_Context = this;        
        m_instance = this;
		
//		NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
//		
//		if (NameSpace.SecurityPreventMode == NameSpace.GetMode) {
//			Intent intentRelease = new Intent();
//			intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
//			intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			startActivityForResult(intentRelease, REQUESTCODE_RELEASE);
//			intentRelease = null;
//		}
        
        mTool = new PreferenceTool(m_Context);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.frame_main);

        // 2017.01.06_yclee : 네비게이션바 안나오도록
        hideNavigationBar();
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        /* Main LinearLayout */
        LinearLayout mainLinear = new LinearLayout(this);
        mainLinear.setOrientation(LinearLayout.VERTICAL);
        
        mTitleLL = (LinearLayout) View.inflate(m_Context, R.layout.frame_title, null);
        mTitleLL.setVisibility(View.VISIBLE);
        
        mSecurityZone = (LinearLayout) View.inflate(m_Context, R.layout.securityzone_view, null); // 중앙 내용
        mSecurityZone.setVisibility(View.VISIBLE);
        
        title_btn_close = (ImageButton) mTitleLL.findViewById(R.id.btn_close);
        tab_btn_outmode = (Button) mTitleLL.findViewById(R.id.tab_outmode);
        tab_btn_security = (Button) mTitleLL.findViewById(R.id.tab_security);
        
        title_btn_close.setOnClickListener(this);
        tab_btn_outmode.setOnClickListener(this);
        tab_btn_security.setOnClickListener(this);
        
        tab_btn_outmode.setBackground(null);
        
        mainLinear.addView(mTitleLL);
        mainLinear.addView(mSecurityZone);
        setContentView(mainLinear);                
             
        /* 버튼 */
        btn_option = (Button) findViewById(R.id.btn_option);	// 구역 설정 및 이름 바꾸기
        btn_option.setOnClickListener(this);
        btn_set = (Button) findViewById(R.id.btn_set);		// 방범 설정
        btn_set.setOnClickListener(this);
        
        /* 구역 : 설정된 구역만 보여주고 설정안된 구역은 안보이도록 'setVisibility'를 사용하기 위해 */
        sector1 = (FrameLayout) findViewById(R.id.sector_1);
        sector2 = (FrameLayout) findViewById(R.id.sector_2);
        sector3 = (FrameLayout) findViewById(R.id.sector_3);
        sector4 = (FrameLayout) findViewById(R.id.sector_4);
        sector5 = (FrameLayout) findViewById(R.id.sector_5);
        
        /* 구역 이름 : FrameLayout에 포함 */
        sector1_name = (TextView) findViewById(R.id.text_sector_1);
        sector2_name = (TextView) findViewById(R.id.text_sector_2);
        sector3_name = (TextView) findViewById(R.id.text_sector_3);
        sector4_name = (TextView) findViewById(R.id.text_sector_4);
        sector5_name = (TextView) findViewById(R.id.text_sector_5);
    	
        /* 구역 ON / OFF 설정 상태 : FrameLayout에 포함 */
        sector1_status = (TextView) findViewById(R.id.onoff_sector_1);
        sector2_status = (TextView) findViewById(R.id.onoff_sector_2);
        sector3_status = (TextView) findViewById(R.id.onoff_sector_3);
        sector4_status = (TextView) findViewById(R.id.onoff_sector_4);
        sector5_status = (TextView) findViewById(R.id.onoff_sector_5);
        
        /* Backkey 터치시 Opacity 값 변경 */
		title_btn_close.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = title_btn_close.getBackground();
					alpha.setAlpha(0x99); // Opacity 60%
				}
				else if ((event.getAction() == MotionEvent.ACTION_UP)) {
					Drawable alpha = title_btn_close.getBackground();
					alpha.setAlpha(0xff); // Opacity 100%
				}

				return false;
			}
		});

		title_btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onFinish();
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
    
    /* Create Broadcast Receiver */
    protected void createBroadcastReceiver() {
        /* Broadcast Receiver */
        if (m_BroadcastReceiver == null) {
            m_BroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    try {                        
                        Log.i(NameSpace.DEBUG_TAG, "intent.getAction() : " + intent.getAction());
                        
                        if ((NameSpace.DBUS_DISCONNECT_ACTION.equals(intent.getAction()))
                                || NameSpace.SYSTEM_NETWROK_LINK_ACTION.equals(intent.getAction())){
                            createDialog(NameSpace.MSG_NETWORK_FAIL);
                        }
                                                
                        else if (NameSpace.REG_CHECK_EVENT_ACTION.equals(intent.getAction())) {
                            nType = intent.getExtras().getInt("type");
                            String dummy = intent.getExtras().getString("dummy");
                            
                            if (nType == NameSpace.ReqEventRelPrevMode) {
                                if (SecurityZoneView.getInstance() != null) {
                                	createDialog(NameSpace.DIALOG_NETWORK_FAIL);
                                }
                            }

                            /* 방범 구역 센서 open 일 때 */
                            if (NameSpace.RegPrevSensor1Failed <= nType
                                    && nType <= NameSpace.RegPrevSensor5Failed) {
                                if (SecurityZoneView.getInstance() != null) {
                                    // 결과 Dialog 박스 Show..
                                    Log.i(NameSpace.DEBUG_TAG, "[Security Setting]-SecurityZoneView.getInstance() : "
                                                + SecurityZoneView.getInstance());
                                    Log.i(NameSpace.DEBUG_TAG, "[Security Setting]-type : " + nType);
                                    Log.i(NameSpace.DEBUG_TAG, "[Security Setting]-dummy : " + dummy);
                                    
                                    /* dialog show */
                                    createDialog(nType);
                                    displayNonMode();
                                }
                            }
                            
                            // 방범 설정 성공
                            if (nType == NameSpace.RegPrevSensorSuccess) {
                            	displaySecurityPreventMode();
                            	
                            	/* 방범센서 적용 토스트 */
                            	/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.securityset_toast,
                                        (ViewGroup) findViewById(R.id.custom_toast_layout));
                                
                                TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
                                text.setText(getResources().getString(R.string.msg_setprevent));

                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();*/

                                // 2017.01.06_yclee : 네이게이션바 안나오도록
                                intentTimer = new Intent();
                                intentTimer.setClassName("com.commax.security", "com.commax.security.dialog.SecurityZoneSetDialog");
                                intentTimer.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intentTimer);

                                intentTimer = null;
                            }
                            
                            // 방범 해제 성공
                            else if (nType == NameSpace.RegPrevSensorRelease) {
                            	                            	
                            	/* 방범센서 해제 토스트 : 2016.09.26 - 잠정 사용안함 */
//                            	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                                LayoutInflater inflater = getLayoutInflater();
//                                View layout = inflater.inflate(R.layout.securityset_toast,
//                                        (ViewGroup) findViewById(R.id.custom_toast_layout));
//                                
//                                TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
//                                text.setText(getResources().getString(R.string.msg_releseprevent));
//
//                                Toast toast = new Toast(getApplicationContext());
//                                toast.setDuration(Toast.LENGTH_LONG);
//                                toast.setView(layout);
//                                toast.show();
                            	
//                            	displayNonMode();
                            	
                            	onFinish();
                            }
                            
//                            if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
//                                /* 이미 외출 모드 일 때 메시지 팝업 */
//                            	createDialog(NameSpace.DIALOG_ALREADY_OUT_SETTED);
//                            }
                        }                        
                    } 
                    catch (Exception e) {
    					Log.e(NameSpace.DEBUG_TAG, "Unsupported Operation Exception. !!!");
    				}
                }
            };
            /* DBus Disconnect Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.DBUS_DISCONNECT_ACTION));
            /* Network Disconnect Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.SYSTEM_NETWROK_LINK_ACTION));
            /* Register Check Event Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REG_CHECK_EVENT_ACTION));
        }
    }    
    
    
    private void sensorInit() {
    	Log.i(NameSpace.DEBUG_TAG, "sensorInit()");
    	if (m_Config.nPreventSensorUsed1State != -1) {
            m_Config.nPreventSensorUsed1State = 0;
            mTool.putBoolean(Sector1_Checked, false);
        }
        if (m_Config.nPreventSensorUsed2State != -1) {
            m_Config.nPreventSensorUsed2State = 0;
            mTool.putBoolean(Sector2_Checked, false);
        }
        if (m_Config.nPreventSensorUsed3State != -1) {
            m_Config.nPreventSensorUsed3State = 0;
            mTool.putBoolean(Sector3_Checked, false);
        }
        if (m_Config.nPreventSensorUsed4State != -1) {
            m_Config.nPreventSensorUsed4State = 0;
            mTool.putBoolean(Sector4_Checked, false);
        }
        if (m_Config.nPreventSensorUsed5State != -1) {
            m_Config.nPreventSensorUsed5State = 0;
            mTool.putBoolean(Sector5_Checked, false);
        }
    }

    @Override
    public void onClick(View v) {        
        switch (v.getId()) {
        	case R.id.tab_outmode:
        		Intent intentOutmode = new Intent();
        		intentOutmode.setClassName("com.commax.security", "com.commax.security.OutModeView");
        		intentOutmode.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentOutmode);
                intentOutmode = null;  
        		break;
        		
            case R.id.btn_option:       // 옵션             	
				Intent intentOption = new Intent();
				intentOption.setClassName("com.commax.security", "com.commax.security.SecuritySetOption");
				intentOption.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intentOption);//(intentOption, REQUESTCODE_OPTION);
				intentOption = null;
                break;
                
            case R.id.btn_set: // 방범 설정 
            	if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityNonMode) {            		
            		new DoPreventSet().execute();
            	}
            	else {
                     Intent intentRelease = new Intent();
                     intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
                     intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                     startActivityForResult(intentRelease, REQUESTCODE_RELEASE);                     
                     intentRelease = null;
            	}                
                break;
                
            default:
            	break;
        }
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(NameSpace.DEBUG_TAG, "onActivityResult()");

        if (requestCode == REQUESTCODE_RELEASE) {     // 방범 해제
            if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                    if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
                        if (m_Config.nPreventSensorUsed1State != -1) {
                            m_Config.nPreventSensorUsed1State = 0;
                        }
                        if (m_Config.nPreventSensorUsed2State != -1) {
                            m_Config.nPreventSensorUsed2State = 0;
                        }
                        if (m_Config.nPreventSensorUsed3State != -1) {
                            m_Config.nPreventSensorUsed3State = 0;
                        }
                        if (m_Config.nPreventSensorUsed4State != -1) {
                            m_Config.nPreventSensorUsed4State = 0;
                        }
                        if (m_Config.nPreventSensorUsed5State != -1) {
                            m_Config.nPreventSensorUsed5State = 0;
                        }

                        m_SktManager.releasePreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
                    }
                }
                else {
                    finishActivity(REQUESTCODE_RELEASE);
                }
            }
        }
                
        if (requestCode == REQUESTCODE_OUTMODE_RELEASE) {     // 외출 설정해제
            if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                    if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
                        m_SktManager.releaseOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);
                    }
                }
                else {
                    finishActivity(REQUESTCODE_OUTMODE_RELEASE);
                }
            }
        }
    } 
    
    
    /* 앱 실행시 */
    private class DoPreventStart extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mStartLoagingDialog = ProgressDialog.show(SecurityZoneView.this, null, 
                    getResources().getString(R.string.on_loading), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {  
            /* AgentType 가져옴 */
//            m_SktManager.getAgentType();  
            
            /* AgentType에 따라 Layout 적용 */
//            if (NameSpace.nAgentType == NameSpace.MobileUserAgentType) {
//                /* 홈 모바일의 Wi-Fi 상태 체크 */
//                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//
//                if (wifiManager.isWifiEnabled() == false) {
//                    Log.d("wifi", "wifi is Enabled");
//                    Log.d("wifi", "SSID : " + wifiInfo.getSSID());
//                    createDialog(NameSpace.MSG_NETWORK_FAIL);
//                }
//            }
            
            /* 방범 모드 설정 가져오기 */
            m_SktManager.getPreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
            
            /* 비상관련 이벤트 체크 요청 */        
            m_SktManager.sendRequestCheckEvent(m_Config.m_strHomeSvrIPAddr,
                    NameSpace.ReqEventEmerEventReq, "");
            
            NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
            
            return null;   
        }

        @Override
        protected void onPostExecute(Long result) {            
        	if (NameSpace.SecurityPreventMode == NameSpace.GetMode) {
            	Log.i(NameSpace.DEBUG_TAG, "APP Start Call");
            	displaySecurityPreventMode();
            }
            
            else if (NameSpace.SecurityNonMode == NameSpace.GetMode) {
//                if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) 
//                        != NameSpace.SecurityPreventMode) {                	
//                	sensorInit();
//                }
                displayNonMode();
            }
            
            if (mStartLoagingDialog != null) {
            	mStartLoagingDialog.dismiss();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
    private void displayNonMode() {
    	/* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */   
    	
        /* 구역 1 */
        if (m_Config.nPreventSensorUsed1State == -1) {        	
        	sector1.setVisibility(View.GONE);
        }
        else {
        	sector1.setVisibility(View.VISIBLE);
        	
        	if (mTool.getBoolean(Sector1_Checked, true)) {
        		sector1_status.setText(R.string.str_prevstate_on); 
        		sector1_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nPreventSensorUsed1State = 1;
        	}
        	else {
        		sector1_status.setText(R.string.str_prevstate_off);
        		sector1_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nPreventSensorUsed1State = 0;
        	}        	
        	
        	m_Config.bSecurity_Check_1 = false;
        }
        
        /* 구역 2 */
        if (m_Config.nPreventSensorUsed2State == -1) {
            sector2.setVisibility(View.GONE);
        }
        else {
        	sector2.setVisibility(View.VISIBLE);      
        	
        	if (mTool.getBoolean(Sector2_Checked, true)) {
        		sector2_status.setText(R.string.str_prevstate_on);
        		sector2_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nPreventSensorUsed2State = 1;
        	}
        	else {
        		sector2_status.setText(R.string.str_prevstate_off);  
        		sector2_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nPreventSensorUsed2State = 0;
        	}          	
        	
        	m_Config.bSecurity_Check_2 = false;
        }
        
        /* 구역 3 */
        if (m_Config.nPreventSensorUsed3State == -1) {
            sector3.setVisibility(View.GONE);
        }
        else {
        	sector3.setVisibility(View.VISIBLE);        
        	
        	if (mTool.getBoolean(Sector3_Checked, true)) {
        		sector3_status.setText(R.string.str_prevstate_on); 
        		sector3_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nPreventSensorUsed3State = 1;
        	}
        	else {
        		sector3_status.setText(R.string.str_prevstate_off);  
        		sector3_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nPreventSensorUsed3State = 0;
        	}  
        	
        	
        	m_Config.bSecurity_Check_3 = false;
        }
        
        /* 구역 4 */
        if (m_Config.nPreventSensorUsed4State == -1) {
            sector4.setVisibility(View.GONE);
        }
        else {  
        	sector4.setVisibility(View.VISIBLE);   
        	
        	if (mTool.getBoolean(Sector4_Checked, true)) {
        		sector4_status.setText(R.string.str_prevstate_on);   
        		sector4_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nPreventSensorUsed4State = 1;
        	}
        	else {
        		sector4_status.setText(R.string.str_prevstate_off);  
        		sector4_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nPreventSensorUsed4State = 0;
        	}           	
        	
        	m_Config.bSecurity_Check_4 = false;
        }
        
        /* 구역 5 */
        if (m_Config.nPreventSensorUsed5State == -1) {
            sector5.setVisibility(View.GONE);
        }
        else {  
        	sector5.setVisibility(View.VISIBLE);        	
        	
        	if (mTool.getBoolean(Sector5_Checked, true)) {
        		sector5_status.setText(R.string.str_prevstate_on); 
        		sector5_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nPreventSensorUsed5State = 1;
        	}
        	else {
        		sector5_status.setText(R.string.str_prevstate_off);  
        		sector5_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nPreventSensorUsed5State = 0;
        	}        	
        	
        	m_Config.bSecurity_Check_5 = false;
        }
        
        // 방범 미설정시 옵션버튼 클릭가능하고 버튼 Normal 상태로 변경
        btn_option.setClickable(true);
        Drawable alpha = btn_option.getBackground();
		alpha.setAlpha(0xFF); // Opacity 100%
		btn_option.setTextColor(0xFF534E7F); // Opacity 100%
		
        btn_set.setText(getResources().getString(R.string.str_prevset));
    }
    
    private void displaySecurityPreventMode() {
        /* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
    	
        /* 구역 1 */
        if (m_Config.nPreventSensorUsed1State == -1) {
            sector1.setVisibility(View.GONE);
        }
        else if (m_Config.nPreventSensorUsed1State == 0) {
        	sector1.setVisibility(View.VISIBLE);        	
        	sector1_status.setText(R.string.str_prevstate_off);   
        	sector1_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bSecurity_Check_1 = false;
        }
        else {
        	sector1.setVisibility(View.VISIBLE);
        	sector1_status.setText(R.string.str_prevstate_on);
        	sector1_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bSecurity_Check_1 = true;
        }
        
        /* 구역 2 */
        if (m_Config.nPreventSensorUsed2State == -1) {
            sector2.setVisibility(View.GONE);
        }
        else if (m_Config.nPreventSensorUsed2State == 0) {
        	sector2.setVisibility(View.VISIBLE);
        	sector2_status.setText(R.string.str_prevstate_off);
        	sector2_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bSecurity_Check_2 = false;
        }
        else {
        	sector2.setVisibility(View.VISIBLE);
        	sector2_status.setText(R.string.str_prevstate_on);       
        	sector2_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bSecurity_Check_2 = true;
        }
        
        /* 구역 3 */
        if (m_Config.nPreventSensorUsed3State == -1) {
            sector3.setVisibility(View.GONE);
        }
        else if (m_Config.nPreventSensorUsed3State == 0) {
        	sector3.setVisibility(View.VISIBLE);
        	sector3_status.setText(R.string.str_prevstate_off);
        	sector3_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bSecurity_Check_3 = false;
        }
        else {
        	sector3.setVisibility(View.VISIBLE);
        	sector3_status.setText(R.string.str_prevstate_on);  
        	sector3_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bSecurity_Check_3 = true;
        }
        
        /* 구역 4 */
        if (m_Config.nPreventSensorUsed4State == -1) {
            sector4.setVisibility(View.GONE);
        }
        else if (m_Config.nPreventSensorUsed4State == 0) {
        	sector4.setVisibility(View.VISIBLE);
        	sector4_status.setText(R.string.str_prevstate_off);
        	sector4_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bSecurity_Check_4 = false;
        }
        else {
        	sector4.setVisibility(View.VISIBLE);
        	sector4_status.setText(R.string.str_prevstate_on); 
        	sector4_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bSecurity_Check_4 = true;
        }
        
        /* 구역 5 */
        if (m_Config.nPreventSensorUsed5State == -1) {
            sector5.setVisibility(View.GONE);
        }
        else if (m_Config.nPreventSensorUsed5State == 0) {
        	sector5.setVisibility(View.VISIBLE);
        	sector5_status.setText(R.string.str_prevstate_off);
        	sector5_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bSecurity_Check_5 = false;
        }
        else {
        	sector5.setVisibility(View.VISIBLE);
        	sector5_status.setText(R.string.str_prevstate_on);
        	sector5_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bSecurity_Check_5 = true;
        }        
        
        // 방범 설정시 옵션버튼 클릭 불가능하고 버튼 Dimmed 상태로 변경
        btn_option.setClickable(false);
        Drawable bg_alpha = btn_option.getBackground();
        bg_alpha.setAlpha(0x4D); // Opacity 30%
        btn_option.setTextColor(0x80534E7F); // Opacity 50%
		
        btn_set.setText(getResources().getString(R.string.str_relelase));
    }
    
    /* 방범 설정시 */
    private class DoPreventSet extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
//			mLoagingDialog = ProgressDialog.show(SecurityZoneView.this, null, 
//					getResources().getString(R.string.on_setting), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {
            return null;   
        }

        @Override
        protected void onPostExecute(Long result) {           	
			if (m_Config.nPreventSensorUsed1State == 1 
					|| m_Config.nPreventSensorUsed2State == 1 
					|| m_Config.nPreventSensorUsed3State == 1
					|| m_Config.nPreventSensorUsed4State == 1 
					|| m_Config.nPreventSensorUsed5State == 1) {

				m_SktManager.setPreventSecurityMode(
						m_Config.m_strHomeSvrIPAddr, 
						m_Config.nPreventSensorUsed1State, 
						m_Config.nPreventSensorUsed2State,
						m_Config.nPreventSensorUsed3State, 
						m_Config.nPreventSensorUsed4State, 
						m_Config.nPreventSensorUsed5State);
			}
			else {
				/* 방범 구역을 선택하십시오. */
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast_popup, (ViewGroup) findViewById(R.id.custom_toast_layout));

				TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
				text.setText(getResources().getString(R.string.msg_set_sector));

				Toast toast = new Toast(getApplicationContext());
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setView(layout);
				toast.show();
			}
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
           

    protected void createDialog(int nType) {        
        switch (nType) {
            case NameSpace.RegPrevSensor1Failed:            	
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "1");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;

            case NameSpace.RegPrevSensor2Failed:
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "2");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;

            case NameSpace.RegPrevSensor3Failed:
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "3");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;

            case NameSpace.RegPrevSensor4Failed:
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "4");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;

            case NameSpace.RegPrevSensor5Failed:
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "5");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;
                
            case NameSpace.DIALOG_NETWORK_FAIL:
            	/* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "7");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;
                
            case NameSpace.DIALOG_ALREADY_OUT_SETTED:
                /* 방범 모드 설정 가져오기 */
                m_SktManager.getPreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
                
                /* Alerts 다이얼로그 생성자 */
        		alertsDialog = new CustomAlertsDialog(m_Context, "8");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (Config.bOK_btn_click == true) {
							Intent intentRelease = new Intent();
							intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
							intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivityForResult(intentRelease, REQUESTCODE_OUTMODE_RELEASE);
							intentRelease = null;
						}
						
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
				
                break;
                
            default:
            	break;
        }
    }
    

    @Override
    protected void onStart() {
        super.onStart();  
        
        Log.i(NameSpace.DEBUG_TAG, "SecurityZoneView onStart-------------------------");
        
        /* 저장된 구역 이름으로 보여줌 */ 
        sector1_name.setText(mTool.getString(Sector1, getResources().getString(R.string.str_sector1).toString()));
        sector2_name.setText(mTool.getString(Sector2, getResources().getString(R.string.str_sector2).toString()));
        sector3_name.setText(mTool.getString(Sector3, getResources().getString(R.string.str_sector3).toString()));
        sector4_name.setText(mTool.getString(Sector4, getResources().getString(R.string.str_sector4).toString()));
        sector5_name.setText(mTool.getString(Sector5, getResources().getString(R.string.str_sector5).toString()));
    	    	    	
    	if (mTool.getBoolean(Sector1_Checked, false)) {
    		m_Config.nPreventSensorUsed1State = 0;  
    	}
    	else {
    		m_Config.nPreventSensorUsed1State = 1; 
    	}
    	if (mTool.getBoolean(Sector2_Checked, false)) {
    		m_Config.nPreventSensorUsed2State = 0;  
    	}
    	else {
    		m_Config.nPreventSensorUsed2State = 1; 
    	}
    	if (mTool.getBoolean(Sector3_Checked, false)) {
    		m_Config.nPreventSensorUsed3State = 0;  
    	}
    	else {
    		m_Config.nPreventSensorUsed3State = 1; 
    	}
    	if (mTool.getBoolean(Sector4_Checked, false)) {
    		m_Config.nPreventSensorUsed4State = 0;  
    	}
    	else {
    		m_Config.nPreventSensorUsed4State = 1; 
    	}
    	if (mTool.getBoolean(Sector5_Checked, false)) {
    		m_Config.nPreventSensorUsed5State = 0;  
    	}
    	else {
    		m_Config.nPreventSensorUsed5State = 1; 
    	}
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		
		Log.i(NameSpace.DEBUG_TAG, "SecurityZoneView onResume-------------------------");
		
		NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
		
		if (NameSpace.SecurityOutingMode == NameSpace.GetMode) {
            /* 이미 외출 모드 일 때 메시지 팝업 */
        	createDialog(NameSpace.DIALOG_ALREADY_OUT_SETTED);
        }
		
		/* Broadcast Receiver call*/
		if (this.m_BroadcastReceiver == null) {
			createBroadcastReceiver();
		}		
		
		new DoPreventStart().execute();
	}
	

	@Override
    protected void onStop() {
        super.onStop();
        
        if (alertsDialog != null) {
        	alertsDialog.cancel();
        }
        
        if (this.m_BroadcastReceiver != null) {
            this.unregisterReceiver(this.m_BroadcastReceiver);            
        }
        
        this.m_BroadcastReceiver = null;
        
//        ActivityManager actvityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(1);
//
//        for (Iterator iterator = taskInfos.iterator(); iterator.hasNext();) {
//            RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
//
////            Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! Prevent runningTaskInfo : " + runningTaskInfo.topActivity.getClassName());
//            
//            if (runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandardSmall")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandard")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.story.Main")) {
//                onFinish();
//            }            
//        }
    }
    
//	@Override
//    protected void onDestroy() {
//        super.onDestroy();
//        
//        onFinish();
//    }
	
    protected void onFinish() {
        
        Log.i(NameSpace.DEBUG_TAG, "SecurityZoneView onFinish-------------------------");
        
//        if (this.m_BroadcastReceiver != null) {
//            this.unregisterReceiver(this.m_BroadcastReceiver);            
//        }
//		
//		this.m_BroadcastReceiver = null;		
        
//		ActivityCompat.finishAffinity(this);
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

    private void clearNavigationBar(){

        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
// This work only for android 4.4+
            ((Activity)m_Context).getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}