package com.commax.security;

import com.commax.security.dialog.CustomAlertsDialog;
import com.commax.security.dialog.OutmodeSetDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OutModeView extends Activity implements View.OnClickListener{
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
    
    private static int nType = 0;   
    
    /*CHS_700AW 꼐열인지 아닌지 확인 해서 App 올린다 */
    public static boolean IS_CHS_700AW = false;
    
    private static Config m_Config = null;                       // System Config Object
    private static SktManager m_SktManager = null;       // Socket Manager Object
    private Context m_Context = null;

    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receiver
        
    public static OutModeView m_instance;

    public static OutModeView getInstance() {
        return m_instance;
    }    
    
    private static PreferenceTool mTool = null;
    
    /* Password Intent 값 */
    public static final String KEY = "passcode";
    public static final int VALID = 1;
    public static final int INVALID = 0;

    
    public static ProgressDialog mLoagingDialog = null; 
    public static ProgressDialog mStartLoagingDialog = null; 
    
    /* 타이틀 레이아웃 */
    private LinearLayout mTitleLL;
    private LinearLayout mGoOutMode;
    
    /* 버튼 */
    private ImageButton title_btn_close;
    private Button tab_btn_outmode;
    private Button tab_btn_security;
    private Button btn_option;
    private Button btn_set;
    
    /* 구역 */
    private FrameLayout fr_sector1;
    private FrameLayout fr_sector2;
    private FrameLayout fr_sector3;
    private FrameLayout fr_sector4;
    private FrameLayout fr_sector5;
    private FrameLayout fr_capture;
    private FrameLayout fr_turnoffLight;
    private FrameLayout fr_closeGas;
    private FrameLayout fr_bypassCall;
    
    /* 구역 이름 */
    private TextView sector1_name;
    private TextView sector2_name;
    private TextView sector3_name;
    private TextView sector4_name;
    private TextView sector5_name;
    
    /* 시간 및 구역 상태 (ON/OFF) */
    private TextView delayTime;
    private TextView returnTime;    
    private TextView sector1_status;
    private TextView sector2_status;
    private TextView sector3_status;
    private TextView sector4_status;
    private TextView sector5_status;
    private TextView capture_status;
    private TextView turnofflight_status;
    private TextView closegas_status;
    private TextView bypasscall_status;
    
    /* PreferenceTool Key Values */
    private static final String DelayTime = "delaytime";
    private static final String ReturnTime = "returntime";
    private static final String Sector1 = "sector1";
    private static final String Sector2 = "sector2";
    private static final String Sector3 = "sector3";
    private static final String Sector4 = "sector4";
    private static final String Sector5 = "sector5";
    private static final String Sector1_Checked = "sector1_checked_out";
    private static final String Sector2_Checked = "sector2_checked_out";
    private static final String Sector3_Checked = "sector3_checked_out";
    private static final String Sector4_Checked = "sector4_checked_out";
    private static final String Sector5_Checked = "sector5_checked_out";
    private static final String Capture_Checked = "capture_checked_out";
    private static final String Light_Checked = "light_checked_out";
    private static final String Gas_Checked = "gas_checked_out";
    private static final String Bypasscall_Checked = "bypasscall_checked_out";
    
    private static final int REQUESTCODE_SET = 1;
    private static final int REQUESTCODE_RELEASE = 2;
    private static final int REQUESTCODE_OPTION = 3;
    private static final int REQUESTCODE_SECURITY_RELEASE = 4;
    
    private String[] arr_delayTime;
    private String[] arr_returnTime;
    
    /* 타이머 관련 */
	private TextView timer_min;
	private TextView timer_colon;
	private TextView timer_sec1;
	private TextView timer_sec2;
	private TextView text_setstate;
    
	private Intent intentTimer = null;
	
	/* Alerts 다이얼로그 */
    private static CustomAlertsDialog alertsDialog;
    
    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		
		/* Socket Manager Object */
		if (m_SktManager == null)
			m_SktManager = new SktManager(getApplicationContext());

		NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
		
		if (NameSpace.SecurityOutingMode == NameSpace.GetMode) {
			Intent intentRelease = new Intent();
			intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
			intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivityForResult(intentRelease, REQUESTCODE_RELEASE);
			intentRelease = null;
		}
	}

	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
         
		/* Socket Manager Object */
		if (m_SktManager == null)
			m_SktManager = new SktManager(getApplicationContext());
		
		/* System Config Object */
        if(m_Config == null)
            m_Config = new Config(getContentResolver());

		m_Context = this;
		m_instance = this;
				
		mTool = new PreferenceTool(m_Context);
		
		arr_delayTime = getResources().getStringArray(R.array.delaytime);
        arr_returnTime = getResources().getStringArray(R.array.returntime);
		
		// /* Broadcast Receiver call*/
		// createBroadcastReceiver();

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// setContentView(R.layout.securityzone_view);
		setContentView(R.layout.frame_main);

        // 2017.01.06_yclee : ?????? ?????
        hideNavigationBar();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		/* Main LinearLayout */
		LinearLayout mainLinear = new LinearLayout(this);
		mainLinear.setOrientation(LinearLayout.VERTICAL);

		mTitleLL = (LinearLayout) View.inflate(m_Context, R.layout.frame_title, null);
		mTitleLL.setVisibility(View.VISIBLE);

		mGoOutMode = (LinearLayout) View.inflate(m_Context, R.layout.outmode_view, null); // 중앙 내용
		mGoOutMode.setVisibility(View.VISIBLE);

		title_btn_close = (ImageButton) mTitleLL.findViewById(R.id.btn_close);
//		title_btn_edit = (Button) mTitleLL.findViewById(R.id.btn_edit);
		tab_btn_outmode = (Button) mTitleLL.findViewById(R.id.tab_outmode);
		tab_btn_security = (Button) mTitleLL.findViewById(R.id.tab_security);

		title_btn_close.setOnClickListener(this);
//		title_btn_edit.setOnClickListener(this);
		tab_btn_outmode.setOnClickListener(this);
		tab_btn_security.setOnClickListener(this);

		tab_btn_security.setBackground(null);

		mainLinear.addView(mTitleLL);
		mainLinear.addView(mGoOutMode);
		setContentView(mainLinear);
		
		/* 버튼 */
        btn_option = (Button) findViewById(R.id.btn_option);	// 외출옵션
        btn_option.setOnClickListener(this);
        btn_set = (Button) findViewById(R.id.btn_set);		// 외출설정
        btn_set.setOnClickListener(this);
        
        /* 구역 : 설정된 구역만 보여주고 설정안된 구역은 안보이도록 'setVisibility'를 사용하기 위해 */
        fr_sector1 = (FrameLayout) findViewById(R.id.sector_1);
        fr_sector2 = (FrameLayout) findViewById(R.id.sector_2);
        fr_sector3 = (FrameLayout) findViewById(R.id.sector_3);
        fr_sector4 = (FrameLayout) findViewById(R.id.sector_4);
        fr_sector5 = (FrameLayout) findViewById(R.id.sector_5);
        fr_capture = (FrameLayout) findViewById(R.id.frameCapture);
        fr_turnoffLight = (FrameLayout) findViewById(R.id.frameTurnoffLight);
        fr_closeGas = (FrameLayout) findViewById(R.id.frameCloseGas);
        fr_bypassCall = (FrameLayout) findViewById(R.id.frameBypassCall);
        
        /* 구역 이름 : FrameLayout에 포함 */
        sector1_name = (TextView) findViewById(R.id.text_sector_1);
        sector2_name = (TextView) findViewById(R.id.text_sector_2);
        sector3_name = (TextView) findViewById(R.id.text_sector_3);
        sector4_name = (TextView) findViewById(R.id.text_sector_4);
        sector5_name = (TextView) findViewById(R.id.text_sector_5);
    	
        /* 구역 ON / OFF 설정 상태 : FrameLayout에 포함 */
        delayTime = (TextView) findViewById(R.id.text_delaytime_min);
        returnTime = (TextView) findViewById(R.id.text_returntime_sec);
        sector1_status = (TextView) findViewById(R.id.onoff_sector_1);
        sector2_status = (TextView) findViewById(R.id.onoff_sector_2);
        sector3_status = (TextView) findViewById(R.id.onoff_sector_3);
        sector4_status = (TextView) findViewById(R.id.onoff_sector_4);
        sector5_status = (TextView) findViewById(R.id.onoff_sector_5);
        capture_status = (TextView) findViewById(R.id.onoff_capture);
        turnofflight_status = (TextView) findViewById(R.id.onoff_TurnoffLight);
        closegas_status = (TextView) findViewById(R.id.onoff_CloseGas);
        bypasscall_status = (TextView) findViewById(R.id.onoff_BypassCall);
        
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
                            || NameSpace.SYSTEM_NETWROK_LINK_ACTION.equals(intent.getAction())) {
                            createDialog(NameSpace.DIALOG_NETWORK_FAIL);
                        }
                        
                        /* Register Check Event Broadcast Message */
                        else if (NameSpace.REG_CHECK_EVENT_ACTION.equals(intent.getAction())) {                        	
                            nType = intent.getExtras().getInt("type");
                            String dummy = intent.getExtras().getString("dummy");
                            
//                            if (nType == NameSpace.ReqEventRelOutMode) {
//                                if (OutModeView.getInstance() != null) {   
//                                	createDialog(NameSpace.ReqEventRelOutMode);
////                                    OutModeView.getInstance().m_handler
////                                            .sendMessage(Message.obtain(
////                                                    OutModeView.getInstance().m_handler,
////                                                    NameSpace.ReqEventRelOutMode, 0, 0));
//                                }
//                            }

                            /* 방범 구역 센서 open 일 때 */
                            if (NameSpace.RegPrevSensor1Failed <= nType
                                    && nType <= NameSpace.RegPrevSensor5Failed) {
                                if (OutModeView.getInstance() != null) {
                                    Log.i(NameSpace.DEBUG_TAG, "[Go Out]-type : " + nType);
                                    Log.i(NameSpace.DEBUG_TAG, "[Go Out]-dummy : " + dummy);
                                    createDialog(nType);
                                }
                            }

                            /* 외출설정 시작 */
                            if (nType == NameSpace.RegOutingSensorSuccess) {
								if (intentTimer == null) {
                                    // 2017.01.06_yclee : ?????? ?????
                                    clearNavigationBar();

									intentTimer = new Intent();
									intentTimer.setClassName("com.commax.security", "com.commax.security.dialog.OutmodeSetDialog");
									intentTimer.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									startActivity(intentTimer);
									
									intentTimer = null;
								}
                            	                            	
                            	displayGoOutMode();
                            }
                            
                            /* 외출설정 해제 */
                            else if (nType == NameSpace.RegOutingSensorRelease) {
                            	/* 외출설정 해제 토스트 : 2016.09.26 - 잠정 사용안함 */
//                            	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                                LayoutInflater inflater = getLayoutInflater();
//                                View layout = inflater.inflate(R.layout.securityset_toast,
//                                        (ViewGroup) findViewById(R.id.custom_toast_layout));
//                                
//                                TextView mode_name = (TextView) layout.findViewById(R.id.text_modename);
//                                mode_name.setText(getResources().getString(R.string.str_header_outmode));
//                                
//                                ImageView img = (ImageView) layout.findViewById(R.id.btn_control_icon);
//                                img.setBackgroundResource(R.drawable.mode_outdoor_icon_s);
//                                
//                                TextView toast_state = (TextView) layout.findViewById(R.id.custom_toast_text);
//                                toast_state.setText(getResources().getString(R.string.msg_releseoutmode));                                
//
//                                Toast toast = new Toast(getApplicationContext());
//                                toast.setDuration(Toast.LENGTH_LONG);
//                                toast.setView(layout);
//                                toast.show();
                                
//                                displayNonMode();
                            	
                            	onFinish();
                            }
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
            /* Emergency Broadcast Message */
            registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REG_CHECK_EVENT_ACTION));
        }
    }      
   
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {        		
        	case R.id.tab_security:
        		Intent intentOutmode = new Intent();
        		intentOutmode.setClassName("com.commax.security", "com.commax.security.SecurityZoneView");
        		intentOutmode.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentOutmode);
                intentOutmode = null;  
        		break;
        		
            case R.id.btn_option:       // 옵션
				Intent intentOption = new Intent();
				intentOption.setClassName("com.commax.security", "com.commax.security.OutModeOptionView");
				intentOption.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intentOption); //(intentOption, REQUESTCODE_OPTION);
				intentOption = null;
                break;
                
            case R.id.btn_set: // 외출 설정 
            	if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityNonMode) {            		
            		new DoOutmodetSet().execute();
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
        
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                    m_SktManager.setOutingSecurityMode(
                            m_Config.m_strHomeSvrIPAddr, // 홈 서버 IP
                            1, // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
                            NameSpace.nOutingVisitorCatpure,
                            NameSpace.nOutingLightAllOff, 
                            NameSpace.nOutingGasClose,
                            NameSpace.nOutingBypassCall,
                            NameSpace.nOutingSensorUsed1State,
                            NameSpace.nOutingSensorUsed2State,
                            NameSpace.nOutingSensorUsed3State,
                            NameSpace.nOutingSensorUsed4State,
                            NameSpace.nOutingSensorUsed5State,
                            NameSpace.nOutingDelayTime, 
                            NameSpace.nOutingReturnTime);
                }
                else {
                    finishActivity(0);
                }
            }
        }
        
        if (requestCode == REQUESTCODE_SET) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                    NameSpace.nOutingVisitorCatpure = Integer.parseInt(data.getExtras()
                            .get("capture").toString());
                    NameSpace.nOutingLightAllOff = Integer.parseInt(data.getExtras()
                            .get("lightalloff").toString());
                    NameSpace.nOutingGasClose = Integer.parseInt(data.getExtras()
                            .get("gasclose").toString());
                    NameSpace.nOutingBypassCall = Integer.parseInt(data.getExtras()
                            .get("bypasscall").toString());
                    NameSpace.nOutingSensorUsed1State = Integer.parseInt(data.getExtras()
                            .get("sector1").toString());
                    NameSpace.nOutingSensorUsed2State = Integer.parseInt(data.getExtras()
                            .get("sector2").toString());
                    NameSpace.nOutingSensorUsed3State = Integer.parseInt(data.getExtras()
                            .get("sector3").toString());
                    NameSpace.nOutingSensorUsed4State = Integer.parseInt(data.getExtras()
                            .get("sector4").toString());
                    NameSpace.nOutingSensorUsed5State = Integer.parseInt(data.getExtras()
                            .get("sector5").toString());
                    NameSpace.nOutingDelayTime = Integer.parseInt(data.getExtras()
                            .get("delaytime").toString());
                    NameSpace.nOutingReturnTime = Integer.parseInt(data.getExtras()
                            .get("returntime").toString());

                    /* 외출 설정 */
                    m_SktManager.setOutingSecurityMode(
                            m_Config.m_strHomeSvrIPAddr, // 홈 서버 IP
                            1, // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
                            NameSpace.nOutingVisitorCatpure,
                            NameSpace.nOutingLightAllOff, 
                            NameSpace.nOutingGasClose,
                            NameSpace.nOutingBypassCall,
                            NameSpace.nOutingSensorUsed1State,
                            NameSpace.nOutingSensorUsed2State,
                            NameSpace.nOutingSensorUsed3State,
                            NameSpace.nOutingSensorUsed4State,
                            NameSpace.nOutingSensorUsed5State,
                            NameSpace.nOutingDelayTime, 
                            NameSpace.nOutingReturnTime);
                }
                else {
                    finishActivity(REQUESTCODE_SET);
                }
            }
        }

        if (requestCode == REQUESTCODE_RELEASE) {
            if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                    if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {                    	
                        m_SktManager.releaseOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);                    }
                }
                else {
                    finishActivity(REQUESTCODE_RELEASE);
                }
            }
        }
        
        // 이미 방범모드 설정시면 방범모드 해제
        if (requestCode == REQUESTCODE_SECURITY_RELEASE) {
        	if (resultCode == RESULT_OK) {
                if (data.getExtras().getInt(KEY) == VALID) {
                	if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
                        m_SktManager.releasePreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
                    }
                }
                else {
                    finishActivity(REQUESTCODE_SECURITY_RELEASE);
                }
        	}        
        }
    }
    
    
    private void displayNonMode() {
    	/* 외출 지연 시간 */     	
//    	delayTime.setTextColor(ColorStateList.valueOf(0xffcdcddf));
    	
    	if (60 == NameSpace.nOutingDelayTime) {
    		delayTime.setText(arr_delayTime[0].toString());
    	}
    	else if (120 == NameSpace.nOutingDelayTime) {
    		delayTime.setText(arr_delayTime[1].toString());
    	}
    	else {
    		delayTime.setText(arr_delayTime[2].toString());
    	}
    		
    	/* 귀가 복귀 시간 */
//    	returnTime.setTextColor(ColorStateList.valueOf(0xffcdcddf));
    	
    	if (0 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[0].toString());
    	}
    	else if (10 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[1].toString());
    	}
    	else if (20 == NameSpace.nOutingReturnTime){
    		returnTime.setText(arr_returnTime[2].toString());
    	}
    	else if (30 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[3].toString());
    	}
    	else if (40 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[4].toString());
    	}
    	else if (50 == NameSpace.nOutingReturnTime){
    		returnTime.setText(arr_returnTime[5].toString());
    	}
    	else if (60 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[6].toString());
    	}
    	else if (70 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[7].toString());
    	}
    	else if (80 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[8].toString());
    	}
    	else {
    		returnTime.setText(arr_returnTime[9].toString());
    	}
    	
    	/* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */ 
        /* 구역 1 */
        if (m_Config.nOutingSensorUsed1State == -1) {        	
        	fr_sector1.setVisibility(View.GONE);
        }
        else {
        	fr_sector1.setVisibility(View.VISIBLE);
        	
        	if (mTool.getBoolean(Sector1_Checked, true)) {
        		sector1_status.setText(R.string.str_prevstate_on); 
        		sector1_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingSensorUsed1State = 1;
        	}
        	else {
        		sector1_status.setText(R.string.str_prevstate_off);
        		sector1_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingSensorUsed1State = 0;
        	}
        	
        	m_Config.bGoOut_Check_1 = false;
        }
        
        /* 구역 2 */
        if (m_Config.nOutingSensorUsed2State == -1) {
            fr_sector2.setVisibility(View.GONE);
        }
        else {
        	fr_sector2.setVisibility(View.VISIBLE);      
        	
        	if (mTool.getBoolean(Sector2_Checked, true)) {
        		sector2_status.setText(R.string.str_prevstate_on);
        		sector2_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingSensorUsed2State = 1;
        	}
        	else {
        		sector2_status.setText(R.string.str_prevstate_off);  
        		sector2_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingSensorUsed2State = 0;
        	}          	
        	
        	m_Config.bGoOut_Check_2 = false;
        }
        
        /* 구역 3 */
        if (m_Config.nOutingSensorUsed3State == -1) {
            fr_sector3.setVisibility(View.GONE);
        }
        else {
        	fr_sector3.setVisibility(View.VISIBLE);        
        	
        	if (mTool.getBoolean(Sector3_Checked, true)) {
        		sector3_status.setText(R.string.str_prevstate_on); 
        		sector3_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingSensorUsed3State = 1;
        	}
        	else {
        		sector3_status.setText(R.string.str_prevstate_off);  
        		sector3_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingSensorUsed3State = 0;
        	}  
        	
        	
        	m_Config.bGoOut_Check_3 = false;
        }
        
        /* 구역 4 */
        if (m_Config.nOutingSensorUsed4State == -1) {
            fr_sector4.setVisibility(View.GONE);
        }
        else {  
        	fr_sector4.setVisibility(View.VISIBLE);   
        	
        	if (mTool.getBoolean(Sector4_Checked, true)) {
        		sector4_status.setText(R.string.str_prevstate_on);  
        		sector4_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingSensorUsed4State = 1;
        	}
        	else {
        		sector4_status.setText(R.string.str_prevstate_off);  
        		sector4_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingSensorUsed4State = 0;
        	}           	
        	
        	m_Config.bGoOut_Check_4 = false;
        }
        
        /* 구역 5 */
        if (m_Config.nOutingSensorUsed5State == -1) {
            fr_sector5.setVisibility(View.GONE);
        }
        else {  
        	fr_sector5.setVisibility(View.VISIBLE);        	
        	
        	if (mTool.getBoolean(Sector5_Checked, true)) {
        		sector5_status.setText(R.string.str_prevstate_on); 
        		sector5_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingSensorUsed5State = 1;
        	}
        	else {
        		sector5_status.setText(R.string.str_prevstate_off);  
        		sector5_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingSensorUsed5State = 0;
        	}        	
        	
        	m_Config.bGoOut_Check_5 = false;
        }
        
        /* 방문자 사진 저장 */
        if (m_Config.nOutingVisitorCatpure == -1) {
        	fr_capture.setVisibility(View.GONE);
        }
        else {
        	fr_capture.setVisibility(View.VISIBLE);      
        	
        	if (mTool.getBoolean(Capture_Checked, true)) {
        		capture_status.setText(R.string.str_prevstate_on);
        		capture_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingVisitorCatpure = 1;
        	}
        	else {
        		capture_status.setText(R.string.str_prevstate_off); 
        		capture_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingVisitorCatpure = 0;
        	}          	
        	
        	m_Config.bGoOut_Check_2 = false;
        }
        
        /* 전등 모두 끄기 */
        if (m_Config.nOutingLightAllOff == -1) {
        	fr_turnoffLight.setVisibility(View.GONE);
        }
        else {
        	fr_turnoffLight.setVisibility(View.VISIBLE);        
        	
        	if (mTool.getBoolean(Light_Checked, true)) {
        		turnofflight_status.setText(R.string.str_prevstate_on); 
        		turnofflight_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingLightAllOff = 1;
        	}
        	else {
        		turnofflight_status.setText(R.string.str_prevstate_off);  
        		turnofflight_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingLightAllOff = 0;
        	}  
        	
        	
        	m_Config.bGoOut_Check_3 = false;
        }
        
        /* 가스 닫힘 */
        if (m_Config.nOutingGasClose == -1) {
        	fr_closeGas.setVisibility(View.GONE);
        }
        else {  
        	fr_closeGas.setVisibility(View.VISIBLE);   
        	
        	if (mTool.getBoolean(Gas_Checked, true)) {
        		closegas_status.setText(R.string.str_prevstate_on);   
        		closegas_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingGasClose = 1;
        	}
        	else {
        		closegas_status.setText(R.string.str_prevstate_off);  
        		closegas_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingGasClose = 0;
        	}           	
        	
        	m_Config.bGoOut_Check_4 = false;
        }
        
        /* 우회 통화 */
        if (m_Config.nOutingBypassCall == -1) {
        	fr_bypassCall.setVisibility(View.GONE);
        }
        else {  
        	fr_bypassCall.setVisibility(View.VISIBLE);        	
        	
        	if (mTool.getBoolean(Bypasscall_Checked, true)) {
        		bypasscall_status.setText(R.string.str_prevstate_on); 
        		bypasscall_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        		m_Config.nOutingBypassCall = 1;
        	}
        	else {
        		bypasscall_status.setText(R.string.str_prevstate_off);  
        		bypasscall_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        		m_Config.nOutingBypassCall = 0;
        	}        	
        	
        	m_Config.bGoOut_Check_5 = false;
        }
        
        // 방범 미설정시 옵션버튼 클릭가능하고 버튼 Normal 상태로 변경
        btn_option.setClickable(true);
        Drawable alpha = btn_option.getBackground();
		alpha.setAlpha(0xFF); // Opacity 100%
		btn_option.setTextColor(0xFF534E7F); // Opacity 100%
		
        btn_set.setText(getResources().getString(R.string.str_prevset));
    }
    
    private void displayGoOutMode() {
    	/* 외출 지연 시간 */
//    	delayTime.setTextColor(ColorStateList.valueOf(0xff6b73c9));
    	
    	if (60 == NameSpace.nOutingDelayTime) {
    		delayTime.setText(arr_delayTime[0].toString());
    	}
    	else if (120 == NameSpace.nOutingDelayTime) {
    		delayTime.setText(arr_delayTime[1].toString());
    	}
    	else {
    		delayTime.setText(arr_delayTime[2].toString());
    	}
    		
    	/* 귀가 복귀 시간 */
//    	returnTime.setTextColor(ColorStateList.valueOf(0xff6b73c9));
    	
    	if (0 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[0].toString());
    	}
    	else if (10 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[1].toString());
    	}
    	else if (20 == NameSpace.nOutingReturnTime){
    		returnTime.setText(arr_returnTime[2].toString());
    	}
    	else if (30 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[3].toString());
    	}
    	else if (40 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[4].toString());
    	}
    	else if (50 == NameSpace.nOutingReturnTime){
    		returnTime.setText(arr_returnTime[5].toString());
    	}
    	else if (60 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[6].toString());
    	}
    	else if (70 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[7].toString());
    	}
    	else if (80 == NameSpace.nOutingReturnTime) {
    		returnTime.setText(arr_returnTime[8].toString());
    	}
    	else {
    		returnTime.setText(arr_returnTime[9].toString());
    	}
    	
    	/* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */    	
        /* 구역 1 */
        if (m_Config.nOutingSensorUsed1State == -1) {
            fr_sector1.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingSensorUsed1State == 0) {
        	fr_sector1.setVisibility(View.VISIBLE);        	
        	sector1_status.setText(R.string.str_prevstate_off);   
        	sector1_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_1 = false;
        }
        else {
        	fr_sector1.setVisibility(View.VISIBLE);
        	sector1_status.setText(R.string.str_prevstate_on);
        	sector1_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_1 = true;
        }
        
        /* 구역 2 */
        if (m_Config.nOutingSensorUsed2State == -1) {
        	fr_sector2.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingSensorUsed2State == 0) {
        	fr_sector2.setVisibility(View.VISIBLE);
        	sector2_status.setText(R.string.str_prevstate_off);
        	sector2_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_2 = false;
        }
        else {
        	fr_sector2.setVisibility(View.VISIBLE);
        	sector2_status.setText(R.string.str_prevstate_on);       
        	sector2_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_2 = true;
        }
        
        /* 구역 3 */
        if (m_Config.nOutingSensorUsed3State == -1) {
            fr_sector3.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingSensorUsed3State == 0) {
        	fr_sector3.setVisibility(View.VISIBLE);
        	sector3_status.setText(R.string.str_prevstate_off);
        	sector3_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_3 = false;
        }
        else {
        	fr_sector3.setVisibility(View.VISIBLE);
        	sector3_status.setText(R.string.str_prevstate_on);  
        	sector3_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_3 = true;
        }
        
        /* 구역 4 */
        if (m_Config.nOutingSensorUsed4State == -1) {
            fr_sector4.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingSensorUsed4State == 0) {
        	fr_sector4.setVisibility(View.VISIBLE);
        	sector4_status.setText(R.string.str_prevstate_off);
        	sector4_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_4 = false;
        }
        else {
        	fr_sector4.setVisibility(View.VISIBLE);
        	sector4_status.setText(R.string.str_prevstate_on); 
        	sector4_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_4 = true;
        }
        
        /* 구역 5 */
        if (m_Config.nOutingSensorUsed5State == -1) {
            fr_sector5.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingSensorUsed5State == 0) {
        	fr_sector5.setVisibility(View.VISIBLE);
        	sector5_status.setText(R.string.str_prevstate_off);
        	sector5_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_5 = false;
        }
        else {
        	fr_sector5.setVisibility(View.VISIBLE);
        	sector5_status.setText(R.string.str_prevstate_on);
        	sector5_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_5 = true;
        }
        
        /* 방문자 사진 저장 */
        if (m_Config.nOutingVisitorCatpure == -1) {
        	fr_capture.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingVisitorCatpure == 0) {
        	fr_capture.setVisibility(View.VISIBLE);
        	capture_status.setText(R.string.str_prevstate_off);
        	capture_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_2 = false;
        }
        else {
        	fr_capture.setVisibility(View.VISIBLE);
        	capture_status.setText(R.string.str_prevstate_on);       
        	capture_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_2 = true;
        }
        
        /* 전등 모두 끄기 */
        if (m_Config.nOutingLightAllOff == -1) {
        	fr_turnoffLight.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingLightAllOff == 0) {
        	fr_turnoffLight.setVisibility(View.VISIBLE);
        	turnofflight_status.setText(R.string.str_prevstate_off);
        	turnofflight_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_3 = false;
        }
        else {
        	fr_turnoffLight.setVisibility(View.VISIBLE);
        	turnofflight_status.setText(R.string.str_prevstate_on);  
        	turnofflight_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_3 = true;
        }
        
        /* 가스 닫힘 */
        if (m_Config.nOutingGasClose == -1) {
        	fr_closeGas.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingGasClose == 0) {
        	fr_closeGas.setVisibility(View.VISIBLE);
        	closegas_status.setText(R.string.str_prevstate_off);
        	closegas_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_4 = false;
        }
        else {
        	fr_closeGas.setVisibility(View.VISIBLE);
        	closegas_status.setText(R.string.str_prevstate_on); 
        	closegas_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_4 = true;
        }
        
        /* 우회 통화 */
        if (m_Config.nOutingBypassCall == -1) {
        	fr_bypassCall.setVisibility(View.GONE);
        }
        else if (m_Config.nOutingBypassCall == 0) {
        	fr_bypassCall.setVisibility(View.VISIBLE);
        	bypasscall_status.setText(R.string.str_prevstate_off);
        	bypasscall_status.setTextColor(ColorStateList.valueOf(0xffcdcddf));
        	m_Config.bGoOut_Check_5 = false;
        }
        else {
        	fr_bypassCall.setVisibility(View.VISIBLE);
        	bypasscall_status.setText(R.string.str_prevstate_on);
        	bypasscall_status.setTextColor(ColorStateList.valueOf(0xff6b73c9));
        	m_Config.bGoOut_Check_5 = true;
        }
        
        // 방범 설정시 옵션버튼 클릭 불가능하고 버튼 Dimmed 상태로 변경
        btn_option.setClickable(false);
        Drawable bg_alpha = btn_option.getBackground();
        bg_alpha.setAlpha(0x4D); // Opacity 30%
        btn_option.setTextColor(0x80534E7F); // Opacity 50%
		
        btn_set.setText(getResources().getString(R.string.str_relelase));
    }
    
    /* 외출 시작시 */
    private class DoGoOutmodeStart extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mStartLoagingDialog = ProgressDialog.show(OutModeView.this, null, 
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
            
            /* 외출 설정 가져오기 */
            m_SktManager.getOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);
            
            /* 비상관련 이벤트 체크 요청 */        
            m_SktManager.sendRequestCheckEvent(m_Config.m_strHomeSvrIPAddr,
                    NameSpace.ReqEventEmerEventReq, "");
            
            NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
            
            return null;   
        }

        @Override
        protected void onPostExecute(Long result) {            
            
            if (NameSpace.SecurityOutingMode == NameSpace.GetMode) {
            	displayGoOutMode();
            }
            
            else if (NameSpace.SecurityNonMode == NameSpace.GetMode) {
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
    
    
    /* 외출 설정시 */
    private class DoOutmodetSet extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mLoagingDialog = ProgressDialog.show(OutModeView.this, null, 
                    getResources().getString(R.string.on_setting), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {
        	m_SktManager.setOutingSecurityMode(
                    m_Config.m_strHomeSvrIPAddr, // 홈 서버 IP
                    1, // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
                    NameSpace.nOutingVisitorCatpure,
                    NameSpace.nOutingLightAllOff, 
                    NameSpace.nOutingGasClose,
                    NameSpace.nOutingBypassCall,
                    NameSpace.nOutingSensorUsed1State,
                    NameSpace.nOutingSensorUsed2State,
                    NameSpace.nOutingSensorUsed3State,
                    NameSpace.nOutingSensorUsed4State,
                    NameSpace.nOutingSensorUsed5State,
                    NameSpace.nOutingDelayTime, 
                    NameSpace.nOutingReturnTime);
        	
            return null;   
        }

        @Override
        protected void onPostExecute(Long result) {
//            NameSpace.OutModeHomeKeyEventCheck = 0;
//            
//            /* AgentType 가져옴 */
//            m_SktManager.getAgentType();  
//            
//            /* AgentType에 따라 Layout 적용 */
//            if (NameSpace.nAgentType == NameSpace.MobileUserAgentType) {
//                Intent intentSetup = new Intent();
//                intentSetup.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
//                startActivityForResult(intentSetup, 0);
//            }
//            else {
//                m_SktManager.setOutingSecurityMode(
//                        m_Config.m_strHomeSvrIPAddr, // 홈 서버 IP
//                        1, // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
//                        NameSpace.nOutingVisitorCatpure,
//                        NameSpace.nOutingLightAllOff, 
//                        NameSpace.nOutingGasClose,
//                        NameSpace.nOutingBypassCall,
//                        NameSpace.nOutingSensorUsed1State,
//                        NameSpace.nOutingSensorUsed2State,
//                        NameSpace.nOutingSensorUsed3State,
//                        NameSpace.nOutingSensorUsed4State,
//                        NameSpace.nOutingSensorUsed5State,
//                        NameSpace.nOutingDelayTime, 
//                        NameSpace.nOutingReturnTime);
//                
////                Intent sendintent = new Intent();
////                sendintent.setAction("ACTION_AWAY_SET");
////                sendintent.putExtra("AWAY_DATA", "set");
////                sendBroadcast(sendintent);
////                sendintent = null;
//            }
            
            if (mLoagingDialog != null) {
            	mLoagingDialog.dismiss();
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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

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
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

        		alertsDialog = new CustomAlertsDialog(m_Context, "7");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialog.dismiss();
					}
				});
            	
				alertsDialog.show();
                break;
                
            case NameSpace.DIALOG_TRUNOFF_SECURITY:        
            	/* 방범 모드 설정 가져오기 */
                m_SktManager.getPreventSecurityMode(m_Config.m_strHomeSvrIPAddr);
                
            	/* Alerts 다이얼로그 생성자 */
                // 2017.01.06_yclee : ?????? ?????
                clearNavigationBar();

        		alertsDialog = new CustomAlertsDialog(m_Context, "9");
            	
            	alertsDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (Config.bOK_btn_click == true) {
							Intent intentRelease = new Intent();
							intentRelease.setClassName("com.commax.settings", "com.commax.settings.PasscodeCheckActivity");
							intentRelease.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							startActivityForResult(intentRelease, REQUESTCODE_SECURITY_RELEASE);
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
        
        Log.i(NameSpace.DEBUG_TAG, "onStart-------------------------");
        
        /* 저장된 구역 이름으로 보여줌 */ 
        sector1_name.setText(mTool.getString(Sector1, getResources().getString(R.string.str_sector1).toString()));
        sector2_name.setText(mTool.getString(Sector2, getResources().getString(R.string.str_sector2).toString()));
        sector3_name.setText(mTool.getString(Sector3, getResources().getString(R.string.str_sector3).toString()));
        sector4_name.setText(mTool.getString(Sector4, getResources().getString(R.string.str_sector4).toString()));
        sector5_name.setText(mTool.getString(Sector5, getResources().getString(R.string.str_sector5).toString()));

    	
        if (mTool.getBoolean(Sector1_Checked, false)) {
    		m_Config.nOutingSensorUsed1State = 0;  
    	}
    	else {
    		m_Config.nOutingSensorUsed1State = 1; 
    	}
        
    	if (mTool.getBoolean(Sector2_Checked, false)) {
    		m_Config.nOutingSensorUsed2State = 0;  
    	}
    	else {
    		m_Config.nOutingSensorUsed2State = 1; 
    	}
    	
    	if (mTool.getBoolean(Sector3_Checked, false)) {
    		m_Config.nOutingSensorUsed3State = 0;  
    	}
    	else {
    		m_Config.nOutingSensorUsed3State = 1; 
    	}
    	
    	if (mTool.getBoolean(Sector4_Checked, false)) {
    		m_Config.nOutingSensorUsed4State = 0;  
    	}
    	else {
    		m_Config.nOutingSensorUsed4State = 1; 
    	}
    	
    	if (mTool.getBoolean(Sector5_Checked, false)) {
    		m_Config.nOutingSensorUsed5State = 0;  
    	}
    	else {
    		m_Config.nOutingSensorUsed5State = 1; 
    	}
    	
    	if (mTool.getBoolean(Capture_Checked, false)) {
    		m_Config.nOutingVisitorCatpure = 0;  
    	}
    	else {
    		m_Config.nOutingVisitorCatpure = 1; 
    	}
    	
    	if (mTool.getBoolean(Light_Checked, false)) {
    		m_Config.nOutingLightAllOff = 0;  
    	}
    	else {
    		m_Config.nOutingLightAllOff = 1; 
    	}
    	
    	if (mTool.getBoolean(Gas_Checked, false)) {
    		m_Config.nOutingGasClose = 0;  
    	}
    	else {
    		m_Config.nOutingGasClose = 1; 
    	}
    	
    	if (mTool.getBoolean(Bypasscall_Checked, false)) {
    		m_Config.nOutingBypassCall = 0;  
    	}
    	else {
    		m_Config.nOutingBypassCall = 1; 
    	}
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		NameSpace.GetMode = m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr);
		
		if (NameSpace.SecurityPreventMode == NameSpace.GetMode) {
			/* 이미 방범 설정 일 때 메시지 팝업 */
            createDialog(NameSpace.DIALOG_TRUNOFF_SECURITY);
        }
		
		/* Broadcast Receiver call*/
		if (this.m_BroadcastReceiver == null) {
			createBroadcastReceiver();
		}
		
		new DoGoOutmodeStart().execute();
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
////            Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! OutMode runningTaskInfo : " + runningTaskInfo.topActivity.getClassName());
//            
//            if (runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandardSmall")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandard")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.story.Main")) {
//                onFinish();
//            }
//        }
    }
    
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        
//        onFinish();
//    }
        
    
    protected void onFinish() {
                
        Log.i(NameSpace.DEBUG_TAG, "OutmodeView onFinish-------------------------");
        
//        if(this.m_BroadcastReceiver != null) {
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
