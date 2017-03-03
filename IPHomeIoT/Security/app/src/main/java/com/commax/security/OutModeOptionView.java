package com.commax.security;

import java.util.ArrayList;
import java.util.Iterator;
import android.support.v7.app.AlertDialog;
import java.util.List;

import com.commax.security.dialog.SecurityZoneEditDialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class OutModeOptionView extends Activity implements
        View.OnClickListener, OnCheckedChangeListener {
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    public static SktManager m_SktManager = null; /* Socket Manager Object */
    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receive
    
    private static Config m_Config = null;
    private Context m_Context = null;
    
    public static OutModeOptionView m_instance;
    
    private static ProgressDialog mLoagingDialog = null; 
    private static ProgressDialog mStartLoagingDialog = null; 

    public static OutModeOptionView getInstance() {
        return m_instance;
    }  
    
    private static PreferenceTool mTool = null;
    
    /* 버튼 */
    private Button btn_close;
    
    /* 구역 */
    private LinearLayout ll_sector_1;
    private LinearLayout ll_sector_2;
    private LinearLayout ll_sector_3;
    private LinearLayout ll_sector_4;
    private LinearLayout ll_sector_5;
    private LinearLayout ll_Capture;
    private LinearLayout ll_TurnoffLight;
    private LinearLayout ll_CloseGas;
    private LinearLayout ll_BypassCall;
    
    /* 구역 이름 */
    private TextView sector_name_1;
    private TextView sector_name_2;
    private TextView sector_name_3;
    private TextView sector_name_4;
    private TextView sector_name_5;
    private TextView capture;
    private TextView turnoffLight;
    private TextView closeGas;
    private TextView bypassCall;
    
    /* 체크박스 (ON/OFF) */
    private CheckBox checkbox_1;
    private CheckBox checkbox_2;
    private CheckBox checkbox_3;
    private CheckBox checkbox_4;
    private CheckBox checkbox_5;
    private CheckBox checkbox_Capture;
    private CheckBox checkbox_TurnoffLight;
    private CheckBox checkbox_CloseGas;
    private CheckBox checkbox_BypassCall;  
            
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
    
    private Spinner spin_delayTime;
    private Spinner spin_returnTime; 
    private String[] arr_delayTime;
    private String[] arr_returnTime;
    
    ArrayList<String> mListdelayTime = new ArrayList<String>();
    ArrayList<String> mListreturnTime = new ArrayList<String>();
    
    private int nFirstnDelayDialogPosition = 0;
    private int nFirstnReturnDialogPosition = 0;
    private int nDelayDialogPosition = 0;
    private int nReturnDialogPosition = 0;
    
    private Thread m_Thread = null;
        
    
    /* OutModeOptionView에서 OutModeView 종료하기 위해 */
    //OutModeView OutModeViewAcivity = (OutModeView)OutModeView.OutModeActivity;
    
    
    /* Android Version Check */
    static final int m_SdkVersion = Integer.parseInt(Build.VERSION.SDK);
    
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Socket Manager Object */
        if (m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());
        
        /* System Config Object */
        if(m_Config == null)
            m_Config = new Config(getContentResolver());
             
        /* 비상관련 이벤트 체크 요청 */
        m_SktManager.sendRequestCheckEvent(Config.m_strHomeSvrIPAddr, NameSpace.ReqEventEmerEventReq, "");
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.outmode_option);

		// 2017.01.06_yclee : ?????? ?????
		hideNavigationBar();
        
        m_Context = this;
        m_instance = this;
        
        mTool = new PreferenceTool(m_Context);
        
        arr_delayTime = getResources().getStringArray(R.array.delaytime);
        arr_returnTime = getResources().getStringArray(R.array.returntime);
        
        /* AgentType 가져옴 */
        m_SktManager.getAgentType();
        
        /* 버튼 */
        btn_close = (Button) findViewById(R.id.btn_close);	// 닫기
        btn_close.setOnClickListener(this);
        
        ll_sector_1 = (LinearLayout) findViewById(R.id.ll_sector_1);
        ll_sector_2 = (LinearLayout) findViewById(R.id.ll_sector_2);
        ll_sector_3 = (LinearLayout) findViewById(R.id.ll_sector_3);
        ll_sector_4 = (LinearLayout) findViewById(R.id.ll_sector_4);
        ll_sector_5 = (LinearLayout) findViewById(R.id.ll_sector_5);
        ll_Capture = (LinearLayout) findViewById(R.id.ll_Capture);
        ll_TurnoffLight = (LinearLayout) findViewById(R.id.ll_TurnoffLight);
        ll_CloseGas = (LinearLayout) findViewById(R.id.ll_CloseGas);
        ll_BypassCall = (LinearLayout) findViewById(R.id.ll_BypassCall);
        
        sector_name_1 = (TextView) findViewById(R.id.text_sector_1);
        sector_name_2 = (TextView) findViewById(R.id.text_sector_2);
        sector_name_3 = (TextView) findViewById(R.id.text_sector_3);
        sector_name_4 = (TextView) findViewById(R.id.text_sector_4);
        sector_name_5 = (TextView) findViewById(R.id.text_sector_5);
        capture = (TextView) findViewById(R.id.text_capture);
        turnoffLight = (TextView) findViewById(R.id.text_TurnoffLight);
        closeGas = (TextView) findViewById(R.id.text_CloseGas);
        bypassCall = (TextView) findViewById(R.id.text_BypassCall);
        
        checkbox_1 = (CheckBox) findViewById(R.id.check_1);
        checkbox_2 = (CheckBox) findViewById(R.id.check_2);
        checkbox_3 = (CheckBox) findViewById(R.id.check_3);
        checkbox_4 = (CheckBox) findViewById(R.id.check_4);
        checkbox_5 = (CheckBox) findViewById(R.id.check_5);
        checkbox_Capture = (CheckBox) findViewById(R.id.check_capture);
        checkbox_TurnoffLight = (CheckBox) findViewById(R.id.check_TurnoffLight);
        checkbox_CloseGas = (CheckBox) findViewById(R.id.check_CloseGas);
        checkbox_BypassCall = (CheckBox) findViewById(R.id.check_BypassCall);
        checkbox_1.setOnCheckedChangeListener(this);
        checkbox_2.setOnCheckedChangeListener(this);
        checkbox_3.setOnCheckedChangeListener(this);
        checkbox_4.setOnCheckedChangeListener(this);
        checkbox_5.setOnCheckedChangeListener(this);
        checkbox_Capture.setOnCheckedChangeListener(this);
        checkbox_TurnoffLight.setOnCheckedChangeListener(this);
        checkbox_CloseGas.setOnCheckedChangeListener(this);
        checkbox_BypassCall.setOnCheckedChangeListener(this);
      
        
        /* Backkey 터치시 Opacity 값 변경 */
        btn_close.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = btn_close.getBackground();
					alpha.setAlpha(0x99); // Opacity 60%
				}
				else if ((event.getAction() == MotionEvent.ACTION_UP)) {
					Drawable alpha = btn_close.getBackground();
					alpha.setAlpha(0xff); // Opacity 100%
				}

				return false;
			}
		});
        
        /* 저장된 구역 이름으로 보여줌 */ 
        if (mTool.getString(Sector1, "").equals("")) {
			mTool.putString(Sector1, sector_name_1.getText().toString());
		}
		else {
			sector_name_1.setText(mTool.getString(Sector1, ""));
		}

		if (mTool.getString(Sector2, "").equals("")) {
			mTool.putString(Sector2, sector_name_2.getText().toString());
		}
		else {
			sector_name_2.setText(mTool.getString(Sector2, ""));
		}

		if (mTool.getString(Sector3, "").equals("")) {
			mTool.putString(Sector3, sector_name_3.getText().toString());
		}
		else {
			sector_name_3.setText(mTool.getString(Sector3, ""));
		}

		if (mTool.getString(Sector4, "").equals("")) {
			mTool.putString(Sector4, sector_name_4.getText().toString());
		}
		else {
			sector_name_4.setText(mTool.getString(Sector4, ""));
		}
		
		if (mTool.getString(Sector5, "").equals("")) {
			mTool.putString(Sector5, sector_name_5.getText().toString());
		}
		else {
			sector_name_5.setText(mTool.getString(Sector5, ""));
		}
		
		init_data();
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
    
    @Override
    protected void onResume() {
        super.onResume();  
        
        new DoOptionStart().execute();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        Log.i ("yclee", "OutmodeOption  onStop ");
        
        /* 저장 되었습니다. */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_popup,
                (ViewGroup) findViewById(R.id.custom_toast_layout));
        
        TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
        text.setText(getResources().getString(R.string.toast_saveed));

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
        
        this.finish();
    }
    
    public void init_data() {
    	spin_delayTime = (Spinner) findViewById(R.id.spinner_delaytime);    	
    	spin_returnTime = (Spinner) findViewById(R.id.spinner_returntime);   	
    	
    	for (int i = 0; i < arr_delayTime.length; i++) {
    		mListdelayTime.add(arr_delayTime[i]);
    	}
    	
    	for (int i = 0; i < arr_returnTime.length; i++) {
    		mListreturnTime.add(arr_returnTime[i]);
    	}
    	
		spinnerAdapter();
	}

	void spinnerAdapter() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				/* 외출 지연 시간 */
				ArrayAdapter<String> delaytime_adapter = new ArrayAdapter<String>(m_Context, R.layout.custom_spinner_item, mListdelayTime);
				Log.d(NameSpace.DEBUG_TAG, "mListdelayTime.length() = " + mListdelayTime.size());
				delaytime_adapter.setDropDownViewResource(R.layout.custom_spinner_item);

				spin_delayTime.setAdapter(delaytime_adapter);

				DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
				int width = dm.widthPixels;
				int height = dm.heightPixels;
				Log.v("Clear Edit ", "width: " + width + " , height : " + height);

				if (width == 1920) {
					// 12.5inch componenet overlap
					spin_delayTime.setDropDownVerticalOffset(-76);
				}
				else {
					// 7 , 10inch componenet overlap
					spin_delayTime.setDropDownVerticalOffset(-43);
				}
				
				spin_delayTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		            @Override
		            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		            	NameSpace.nOutingDelayTime = (position + 1) * 60;
		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> parent) {}
		        });
				
				/* 귀가 복귀 시간 */
				ArrayAdapter<String> returntime_adapter = new ArrayAdapter<String>(m_Context, R.layout.custom_spinner_item, mListreturnTime);
				Log.d(NameSpace.DEBUG_TAG, "mListdelayTime.length() = " + mListdelayTime.size());
				returntime_adapter.setDropDownViewResource(R.layout.custom_spinner_item);

				spin_returnTime.setAdapter(returntime_adapter);

//				DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//				int width = dm.widthPixels;
//				int height = dm.heightPixels;
				Log.v("Clear Edit ", "width: " + width + " , height : " + height);

				if (width == 1920) {
					// 12.5inch componenet overlap
					spin_returnTime.setDropDownVerticalOffset(-76);
				}
				else {
					// 7 , 10inch componenet overlap
					spin_returnTime.setDropDownVerticalOffset(-43);
				}
				
				spin_returnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		            @Override
		            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		            	if (0 == position) {
		            		NameSpace.nOutingReturnTime = 0;
		            	}
		            	else {
		            		NameSpace.nOutingReturnTime = (position) * 10;
		            	}
		            }
		            @Override
		            public void onNothingSelected(AdapterView<?> parent) {}
		        });
			}
		});
	}    
        
    /* 앱 실행시 */
    private class DoOptionStart extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mStartLoagingDialog = ProgressDialog.show(OutModeOptionView.this, null, 
                    getResources().getString(R.string.on_loading), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {
        	/* 외출 설정 가져오기 */
            m_SktManager.getOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);
            
            /* 비상관련 이벤트 체크 요청 */        
            m_SktManager.sendRequestCheckEvent(m_Config.m_strHomeSvrIPAddr,
                    NameSpace.ReqEventEmerEventReq, "");
            
            return null;   
        }

		@Override
		protected void onPostExecute(Long result) {
			displayOutModeState();

			if (mStartLoagingDialog != null) {
				mStartLoagingDialog.dismiss();
				mStartLoagingDialog = null;
			}
		}

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
    private void displayOutModeState() {
    	/* 외출 지연 시간 */     	
    	if (60 == NameSpace.nOutingDelayTime) {
    		spin_delayTime.setSelection(0);
    	}
    	else if (120 == NameSpace.nOutingDelayTime) {
    		spin_delayTime.setSelection(1);
    	}
    	else {
    		spin_delayTime.setSelection(2);
    	}
    		
    	/* 귀가 복귀 시간 */
    	if (0 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(0);
    	}
    	else if (10 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(1);
    	}
    	else if (20 == NameSpace.nOutingReturnTime){
    		spin_returnTime.setSelection(2);
    	}
    	else if (30 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(3);
    	}
    	else if (40 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(4);
    	}
    	else if (50 == NameSpace.nOutingReturnTime){
    		spin_returnTime.setSelection(5);
    	}
    	else if (60 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(6);
    	}
    	else if (70 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(7);
    	}
    	else if (80 == NameSpace.nOutingReturnTime) {
    		spin_returnTime.setSelection(8);
    	}
    	else {
    		spin_returnTime.setSelection(9);
    	}
    	
        /* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */    	
    	if (NameSpace.SecurityOutingMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
        	if (Config.bGoOut_Check_1) {
				checkbox_1.setChecked(true);
			}
			else {
				checkbox_1.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_2) {
				checkbox_2.setChecked(true);
			}
			else {
				checkbox_2.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_3) {
				checkbox_3.setChecked(true);
			}
			else {
				checkbox_3.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_4) {
				checkbox_4.setChecked(true);
			}
			else {
				checkbox_4.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_5) {
				checkbox_5.setChecked(true);
			}
			else {
				checkbox_5.setVisibility(View.GONE);
			}
			
			if (Config.bGoOut_Check_Capture) {
				checkbox_Capture.setChecked(true);
			}
			else {
				checkbox_Capture.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_TurnoffLight) {
				checkbox_TurnoffLight.setChecked(true);
			}
			else {
				checkbox_TurnoffLight.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_CloseGas) {
				checkbox_CloseGas.setChecked(true);
			}
			else {
				checkbox_CloseGas.setVisibility(View.GONE);
			}

			if (Config.bGoOut_Check_BypassCall) {
				checkbox_BypassCall.setChecked(true);
			}
			else {
				checkbox_BypassCall.setVisibility(View.GONE);
			}
        }
		else {
			checkbox_1.setVisibility(View.VISIBLE);
			checkbox_2.setVisibility(View.VISIBLE);
			checkbox_3.setVisibility(View.VISIBLE);
			checkbox_4.setVisibility(View.VISIBLE);
			checkbox_5.setVisibility(View.VISIBLE);
			checkbox_Capture.setVisibility(View.VISIBLE);
			checkbox_TurnoffLight.setVisibility(View.VISIBLE);
			checkbox_CloseGas.setVisibility(View.VISIBLE);
			checkbox_BypassCall.setVisibility(View.VISIBLE);
			
			/* 체크박스 상태 표시 */
			if (mTool.getBoolean(Sector1_Checked, true)) {
				checkbox_1.setChecked(true);
			}
			else {
				checkbox_1.setChecked(false);
			}

			if (mTool.getBoolean(Sector2_Checked, true)) {
				checkbox_2.setChecked(true);
			}
			else {
				checkbox_2.setChecked(false);
			}

			if (mTool.getBoolean(Sector3_Checked, true)) {
				checkbox_3.setChecked(true);
			}
			else {
				checkbox_3.setChecked(false);
			}

			if (mTool.getBoolean(Sector4_Checked, true)) {
				checkbox_4.setChecked(true);
			}
			else {
				checkbox_4.setChecked(false);
			}

			if (mTool.getBoolean(Sector5_Checked, true)) {
				checkbox_5.setChecked(true);
			}
			else {
				checkbox_5.setChecked(false);
			}
			
			if (mTool.getBoolean(Capture_Checked, true)) {
				checkbox_Capture.setChecked(true);
			}
			else {
				checkbox_Capture.setChecked(false);
			}

			if (mTool.getBoolean(Light_Checked, true)) {
				checkbox_TurnoffLight.setChecked(true);
			}
			else {
				checkbox_TurnoffLight.setChecked(false);
			}

			if (mTool.getBoolean(Gas_Checked, true)) {
				checkbox_CloseGas.setChecked(true);
			}
			else {
				checkbox_CloseGas.setChecked(false);
			}

			if (mTool.getBoolean(Bypasscall_Checked, true)) {
				checkbox_BypassCall.setChecked(true);
			}
			else {
				checkbox_BypassCall.setChecked(false);
			}
		}
    	
        /* 구역 1 */
        if (Config.nOutingSensorUsed1State == -1) {
            ll_sector_1.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.GONE);
        }
        else if (Config.nOutingSensorUsed1State == 0) {
        	ll_sector_1.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_1.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 2 */
        if (Config.nOutingSensorUsed2State == -1) {
            ll_sector_2.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.GONE);
        }
        else if (Config.nOutingSensorUsed2State == 0) {
        	ll_sector_2.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_2.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 3 */
        if (Config.nOutingSensorUsed3State == -1) {
            ll_sector_3.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.GONE);
        }
        else if (Config.nOutingSensorUsed3State == 0) {
        	ll_sector_3.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_3.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 4 */
        if (Config.nOutingSensorUsed4State == -1) {
            ll_sector_4.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.GONE);
        }
        else if (Config.nOutingSensorUsed4State == 0) {
        	ll_sector_4.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_4.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 5 */
        if (Config.nOutingSensorUsed5State == -1) {
            ll_sector_5.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.GONE);
        }
        else if (Config.nOutingSensorUsed5State == 0) {
        	ll_sector_5.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_5.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.VISIBLE);
        }
        
        /* 방문자 사진 저장 */
        if (Config.nOutingVisitorCatpure == -1) {
        	ll_Capture.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine_capture)).setVisibility(View.GONE);
        }
        else if (Config.nOutingVisitorCatpure == 0) {
        	ll_Capture.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_capture)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_Capture.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_capture)).setVisibility(View.VISIBLE);
        }
        
        /* 전등 모두 끄기 */
        if (Config.nOutingLightAllOff == -1) {
        	ll_TurnoffLight.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine_TurnoffLight)).setVisibility(View.GONE);
        }
        else if (Config.nOutingLightAllOff == 0) {
        	ll_TurnoffLight.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_TurnoffLight)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_TurnoffLight.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_TurnoffLight)).setVisibility(View.VISIBLE);
        }
        
        /* 가스 닫기 */
        if (Config.nOutingGasClose == -1) {
        	ll_CloseGas.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine_CloseGas)).setVisibility(View.GONE);
        }
        else if (Config.nOutingGasClose == 0) {
        	ll_CloseGas.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_CloseGas)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_CloseGas.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_CloseGas)).setVisibility(View.VISIBLE);
        }
        
        /* 우회 통화 */
        if (Config.nOutingBypassCall == -1) {
        	ll_BypassCall.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine_BypassCall)).setVisibility(View.GONE);
        }
        else if (Config.nOutingBypassCall == 0) {
        	ll_BypassCall.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_BypassCall)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_BypassCall.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine_BypassCall)).setVisibility(View.VISIBLE);
        }
        
        sector_name_1.setText(mTool.getString(Sector1, sector_name_1.getText().toString()));
    	sector_name_2.setText(mTool.getString(Sector2, sector_name_2.getText().toString()));
    	sector_name_3.setText(mTool.getString(Sector3, sector_name_3.getText().toString()));
    	sector_name_4.setText(mTool.getString(Sector4, sector_name_4.getText().toString()));
    	sector_name_5.setText(mTool.getString(Sector5, sector_name_5.getText().toString()));
    }
    
    
    protected final void createDialog(int id) { 
        NameSpace.OutModeOptionHomeKeyEventCheck = 0;
        
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OutModeOptionView.this);
        
        switch (id) {
            case NameSpace.MSG_NETWORK_FAIL:
				alertBuilder/*.setIcon(R.drawable.icon_info)*/
						.setTitle(R.string.msg_title_info)
						.setMessage(R.string.msg_check_network)
						.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								finish();
							}
						});
				break;

            default:
                break;
        }
        
        Dialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {                
            case R.id.btn_close: // 취소
				if (1 != NameSpace.nOutingVisitorCatpure && 1 != NameSpace.nOutingLightAllOff && 1 != NameSpace.nOutingGasClose
						&& 1 != NameSpace.nOutingBypassCall && 1 != NameSpace.nOutingSensorUsed1State && 1 != NameSpace.nOutingSensorUsed2State
						&& 1 != NameSpace.nOutingSensorUsed3State && 1 != NameSpace.nOutingSensorUsed4State
						&& 1 != NameSpace.nOutingSensorUsed5State) {

					/* 외출설정 옵션을 선택하도록함. */
					getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.toast_popup, (ViewGroup) findViewById(R.id.custom_toast_layout));

					TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
					text.setText(getResources().getString(R.string.msg_set_outmodeoption));

					Toast toast = new Toast(getApplicationContext());
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(layout);
					toast.show();
					
					SystemClock.sleep(3000);
					
					this.finish();
				}
				else {
					optionSetting();
					
					this.finish();
				}
				
                break;            
                
            default:
            	break;
        }
    }
    
    private void optionSetting() {
        m_SktManager.setOutingSecurityMode(
                Config.m_strHomeSvrIPAddr, // 홈 서버 IP
                0,      // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
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
        
        optionResult("SUCCESS");
    }

    
    public void optionResult(String data) {
        Log.i(NameSpace.DEBUG_TAG , "optionResult : " + data);
        
        if (data.equalsIgnoreCase("SUCCESS")) {
            Log.i(NameSpace.DEBUG_TAG , "success process start");
            
            Intent intent = new Intent();
            intent.putExtra("capture", NameSpace.nOutingVisitorCatpure);
            intent.putExtra("lightalloff", NameSpace.nOutingLightAllOff);
            intent.putExtra("gasclose", NameSpace.nOutingGasClose);
            intent.putExtra("bypasscall", NameSpace.nOutingBypassCall);
            intent.putExtra("sector1", NameSpace.nOutingSensorUsed1State);
            intent.putExtra("sector2", NameSpace.nOutingSensorUsed2State);
            intent.putExtra("sector3", NameSpace.nOutingSensorUsed3State);
            intent.putExtra("sector4", NameSpace.nOutingSensorUsed4State);
            intent.putExtra("sector5", NameSpace.nOutingSensorUsed5State);
            intent.putExtra("delaytime", NameSpace.nOutingDelayTime); 
            intent.putExtra("returntime", NameSpace.nOutingReturnTime); 
            this.setResult(RESULT_OK, intent);  
                        
            this.finish();
        }
    }
    
    
    @Override
    public void onCheckedChanged(CompoundButton chkView, boolean isChecked) {
		/* 구역 1 */
		if (chkView.getId() == R.id.check_1) {
			Log.i(NameSpace.DEBUG_TAG, "isChecked1 : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Sector1_Checked, true);
			}
			else {
				mTool.putBoolean(Sector1_Checked, false);

				if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_1.setVisibility(View.GONE);
				}
				else {
					checkbox_1.setVisibility(View.VISIBLE);
				}
			}
		}

		/* 구역 2 */
		if (chkView.getId() == R.id.check_2) {
			Log.i(NameSpace.DEBUG_TAG, "isChecked2 : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Sector2_Checked, true);
			}
			else {
				mTool.putBoolean(Sector2_Checked, false);

				if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_2.setVisibility(View.GONE);
				}
				else {
					checkbox_2.setVisibility(View.VISIBLE);
				}
			}
		}

		/* 구역 3 */
		if (chkView.getId() == R.id.check_3) {
			Log.i(NameSpace.DEBUG_TAG, "isChecked3 : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Sector3_Checked, true);
			}
			else {
				mTool.putBoolean(Sector3_Checked, false);

				if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_3.setVisibility(View.GONE);
				}
				else {
					checkbox_3.setVisibility(View.VISIBLE);
				}
			}
		}

		/* 구역 4 */
		if (chkView.getId() == R.id.check_4) {
			Log.i(NameSpace.DEBUG_TAG, "isChecked4 : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Sector4_Checked, true);
			}
			else {
				mTool.putBoolean(Sector4_Checked, false);

				if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_4.setVisibility(View.GONE);
				}
				else {
					checkbox_4.setVisibility(View.VISIBLE);
				}
			}
		}

		/* 구역 5 */
		if (chkView.getId() == R.id.check_5) {
			Log.i(NameSpace.DEBUG_TAG, "isChecked5 : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Sector5_Checked, true);
			}
			else {
				mTool.putBoolean(Sector5_Checked, false);

				if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_5.setVisibility(View.GONE);
				}
				else {
					checkbox_5.setVisibility(View.VISIBLE);
				}
			}
		}

		/* 방문자 사진 저장 */
		if (chkView.getId() == R.id.check_capture) {
			Log.i(NameSpace.DEBUG_TAG, "check_capture : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Capture_Checked, true);
			}
			else {
				mTool.putBoolean(Capture_Checked, false);

				if (NameSpace.SecurityOutingMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_Capture.setVisibility(View.GONE);
				}
				else {
					checkbox_Capture.setVisibility(View.VISIBLE);
				}
			}
		}
		
		/* 전등 모두 끄기 */
		if (chkView.getId() == R.id.check_TurnoffLight) {
			Log.i(NameSpace.DEBUG_TAG, "check_TurnoffLight : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Light_Checked, true);
			}
			else {
				mTool.putBoolean(Light_Checked, false);

				if (NameSpace.SecurityOutingMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_TurnoffLight.setVisibility(View.GONE);
				}
				else {
					checkbox_TurnoffLight.setVisibility(View.VISIBLE);
				}
			}
		}
		
		/* 가스 닫기 */
		if (chkView.getId() == R.id.check_CloseGas) {
			Log.i(NameSpace.DEBUG_TAG, "check_CloseGas : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Gas_Checked, true);
			}
			else {
				mTool.putBoolean(Gas_Checked, false);

				if (NameSpace.SecurityOutingMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_CloseGas.setVisibility(View.GONE);
				}
				else {
					checkbox_CloseGas.setVisibility(View.VISIBLE);
				}
			}
		}
		
		/* 우회 통화 */
		if (chkView.getId() == R.id.check_BypassCall) {
			Log.i(NameSpace.DEBUG_TAG, "check_BypassCall : " + isChecked);
			if (isChecked) {
				mTool.putBoolean(Bypasscall_Checked, true);
			}
			else {
				mTool.putBoolean(Bypasscall_Checked, false);

				if (NameSpace.SecurityOutingMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
					checkbox_BypassCall.setVisibility(View.GONE);
				}
				else {
					checkbox_BypassCall.setVisibility(View.VISIBLE);
				}
			}
		}
    }
    
    /* 옵션 설정 저장 */
//    private class OutmodeOptionSet extends AsyncTask<String, Integer, Long> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            mLoagingDialog = ProgressDialog.show(OutModeOptionView.this, null, 
//                    getResources().getString(R.string.on_setting), true);
//        }
//
//        @Override
//        protected Long doInBackground(String... arg0) {  
//        	optionSetting();            
//            return null;   
//        }
//
//        @Override
//        protected void onPostExecute(Long result) {            
//            if (mLoagingDialog != null) {
//            	mLoagingDialog.dismiss();
//            }
//            
//            displayOutModeState();
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//        }
//    }
    
//    @Override
//    protected void onStop() {
//        super.onStop();
//        
//        ActivityManager actvityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(1);
//
//        for (Iterator iterator = taskInfos.iterator(); iterator.hasNext();) {
//            RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
//
////            Log.i(NameSpace.DEBUG_TAG, "!!!!!!!!!!!!! runningTaskInfo : " + runningTaskInfo.topActivity.getClassName());
//
//            if (runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandardSmall")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.category.ActivityStandard")
//                    || runningTaskInfo.topActivity.getClassName().equals("com.commax.home.story.Main")) {
//                        
//                this.finish();
//                if (null != OutModeView.getInstance()) {
//                    OutModeView.getInstance().finish();
//                }
//            }
//        }
//        
//        if(this.m_BroadcastReceiver != null) {
//            this.unregisterReceiver(this.m_BroadcastReceiver);            
//        }
//		
//		this.m_BroadcastReceiver = null;
//    }

//    protected void onUserLeaveHint(View v) {        
//        super.onUserLeaveHint();
//        
//        Log.i(NameSpace.DEBUG_TAG, "OutModeOption onUserLeaveHint()!!!");
//        
//        if ((v.getId() == R.id.btn_ok) || (v.getId() == R.id.btn_cancel)) {
//            NameSpace.OutModeOptionHomeKeyEventCheck = 0;
//        }
//        else {
//            NameSpace.OutModeOptionHomeKeyEventCheck = 1;
//        }
//    }

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