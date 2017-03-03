package com.commax.security;

import com.commax.security.NameSpace;
import com.commax.security.dialog.SecurityZoneEditDialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;


public class SecuritySetOption extends Activity implements OnClickListener, OnCheckedChangeListener{
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
	
	private static int nType = 0;   

    public static Config m_Config = null;                       /* System Config Object */
    public static SktManager m_SktManager = null;               /* Socket Manager Object */
    private BroadcastReceiver m_BroadcastReceiver = null;       // Broadcast Receiver
    
    private Context m_Context = null;    
    
    public static SecuritySetOption m_instance;

    public static SecuritySetOption getInstance() {
        return m_instance;
    }
        
    private static ProgressDialog mLoagingDialog = null; 
    private static ProgressDialog mStartLoagingDialog = null; 
    
    /* 버튼 */
    private Button btn_close;
    private Button btn_edit;
    
    /* 구역 */
    private LinearLayout ll_sector_1;
    private LinearLayout ll_sector_2;
    private LinearLayout ll_sector_3;
    private LinearLayout ll_sector_4;
    private LinearLayout ll_sector_5;
    
    /* 구역 이름 */
    private TextView sector_name_1;
    private TextView sector_name_2;
    private TextView sector_name_3;
    private TextView sector_name_4;
    private TextView sector_name_5;
    
    /* 체크박스 (ON/OFF) */
    private CheckBox checkbox_1;
    private CheckBox checkbox_2;
    private CheckBox checkbox_3;
    private CheckBox checkbox_4;
    private CheckBox checkbox_5;
    
    /* 팝업 다이얼로그 */
    public SecurityZoneEditDialog editPopup;
    
    private static PreferenceTool mTool = null;
    
    // PreferenceTool Key Values
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
    
        
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {       
        super.onCreate(savedInstanceState);
        
        m_Context = this;
        m_instance = this;
        
        mTool = new PreferenceTool(m_Context);
        
        /* Socket Manager Object */
        if(m_SktManager == null)
            m_SktManager = new SktManager(getApplicationContext());

        /* System Config Object */
        if(m_Config == null)
            m_Config = new Config(getContentResolver()); 
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.securityzone_setandedit);

		// 2017.01.06_yclee : ?????? ?????
		hideNavigationBar();
        
        /* 버튼 */
        btn_close = (Button) findViewById(R.id.btn_close);	// 닫기
        btn_close.setOnClickListener(this);
        btn_edit = (Button) findViewById(R.id.btn_edit);		// 이름 바꾸기
        btn_edit.setOnClickListener(this);
		
        ll_sector_1 = (LinearLayout) findViewById(R.id.ll_sector_1);
        ll_sector_2 = (LinearLayout) findViewById(R.id.ll_sector_2);
        ll_sector_3 = (LinearLayout) findViewById(R.id.ll_sector_3);
        ll_sector_4 = (LinearLayout) findViewById(R.id.ll_sector_4);
        ll_sector_5 = (LinearLayout) findViewById(R.id.ll_sector_5);
        
        sector_name_1 = (TextView) findViewById(R.id.text_sector_1);
        sector_name_2 = (TextView) findViewById(R.id.text_sector_2);
        sector_name_3 = (TextView) findViewById(R.id.text_sector_3);
        sector_name_4 = (TextView) findViewById(R.id.text_sector_4);
        sector_name_5 = (TextView) findViewById(R.id.text_sector_5);
        
        checkbox_1 = (CheckBox) findViewById(R.id.check_1);
        checkbox_2 = (CheckBox) findViewById(R.id.check_2);
        checkbox_3 = (CheckBox) findViewById(R.id.check_3);
        checkbox_4 = (CheckBox) findViewById(R.id.check_4);
        checkbox_5 = (CheckBox) findViewById(R.id.check_5);
        checkbox_1.setOnCheckedChangeListener(this);
        checkbox_2.setOnCheckedChangeListener(this);
        checkbox_3.setOnCheckedChangeListener(this);
        checkbox_4.setOnCheckedChangeListener(this);
        checkbox_5.setOnCheckedChangeListener(this);
        
        
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
        
        btn_edit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = ((Button)findViewById(R.id.btn_edit)).getBackground();
					alpha.setAlpha(0x99);	// Opacity 60%
				}
				else if ((event.getAction() == MotionEvent.ACTION_UP)) {
					Drawable alpha = ((Button)findViewById(R.id.btn_edit)).getBackground();
					alpha.setAlpha(0xff);	// Opacity 100%
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
		 
        /* 팝업 다이얼로그 생성자 */
        editPopup = new SecurityZoneEditDialog(SecuritySetOption.this);	// 구역 이름 바꾸기
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
    	Log.i ("yclee", "SecuritySetOption  onStop ");
        super.onStop();
        
        if (editPopup != null) {
        	editPopup.cancel();
        }
        
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.btn_edit:       // 이름바꾸기 
            	editPopup.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						/* Send Custom Broadcast Message(System Key Hide Action) */
						sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);						
					}
				});
            	editPopup.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (Config.bOK_btn_click) {
							/* Send Custom Broadcast Message(System Key Hide Action) */
							sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);
							
							new DoEidt().execute(); 
						}
					}
				});
            	
            	editPopup.show();
            	
                break;
                
            case R.id.btn_close: // 닫기
            	this.finish();               
                break;
                
            default:
            	break;
        }		
	}
    
    
    /* 앱 실행시 */
    private class DoOptionStart extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mStartLoagingDialog = ProgressDialog.show(SecuritySetOption.this, null, 
                    getResources().getString(R.string.on_loading), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {
            /* 방범 모드 설정 가져오기 */
//            m_SktManager.getPreventSecurityMode(Config.m_strHomeSvrIPAddr);
            
            /* 비상관련 이벤트 체크 요청 */        
//            m_SktManager.sendRequestCheckEvent(Config.m_strHomeSvrIPAddr,
//                    NameSpace.ReqEventEmerEventReq, "");
            
            return null;   
        }

		@Override
		protected void onPostExecute(Long result) {
			displayPreventState();

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
    
    private void displayPreventState() {        
        /* 센서 구역(설정 안함 : -1, 사용안함 설정 : 0, 사용함 설정 : 1) */
    	
    	if (NameSpace.SecurityPreventMode == m_SktManager.getSecurityMode(Config.m_strHomeSvrIPAddr)) {
        	if (Config.bSecurity_Check_1) {
				checkbox_1.setChecked(true);
			}
			else {
				checkbox_1.setVisibility(View.GONE);
			}

			if (Config.bSecurity_Check_2) {
				checkbox_2.setChecked(true);
			}
			else {
				checkbox_2.setVisibility(View.GONE);
			}

			if (Config.bSecurity_Check_3) {
				checkbox_3.setChecked(true);
			}
			else {
				checkbox_3.setVisibility(View.GONE);
			}

			if (Config.bSecurity_Check_4) {
				checkbox_4.setChecked(true);
			}
			else {
				checkbox_4.setVisibility(View.GONE);
			}

			if (Config.bSecurity_Check_5) {
				checkbox_5.setChecked(true);
			}
			else {
				checkbox_5.setVisibility(View.GONE);
			}
        }
		else {
			checkbox_1.setVisibility(View.VISIBLE);
			checkbox_2.setVisibility(View.VISIBLE);
			checkbox_3.setVisibility(View.VISIBLE);
			checkbox_4.setVisibility(View.VISIBLE);
			checkbox_5.setVisibility(View.VISIBLE);
			
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
		}
    	
    	
        /* 구역 1 */
        if (Config.nPreventSensorUsed1State == -1) {
            ll_sector_1.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.GONE);
        }
        else if (Config.nPreventSensorUsed1State == 0) {
        	ll_sector_1.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_1.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine1)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 2 */
        if (Config.nPreventSensorUsed2State == -1) {
            ll_sector_2.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.GONE);
        }
        else if (Config.nPreventSensorUsed2State == 0) {
        	ll_sector_2.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_2.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine2)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 3 */
        if (Config.nPreventSensorUsed3State == -1) {
            ll_sector_3.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.GONE);
        }
        else if (Config.nPreventSensorUsed3State == 0) {
        	ll_sector_3.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_3.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine3)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 4 */
        if (Config.nPreventSensorUsed4State == -1) {
            ll_sector_4.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.GONE);
        }
        else if (Config.nPreventSensorUsed4State == 0) {
        	ll_sector_4.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_4.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine4)).setVisibility(View.VISIBLE);
        }
        
        /* 구역 5 */
        if (Config.nPreventSensorUsed5State == -1) {
            ll_sector_5.setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.GONE);
        }
        else if (Config.nPreventSensorUsed5State == 0) {
        	ll_sector_5.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.VISIBLE);
        }
        else {
        	ll_sector_5.setVisibility(View.VISIBLE);
        	((ImageView) findViewById(R.id.dividerLine5)).setVisibility(View.VISIBLE);
        }
        
        sector_name_1.setText(mTool.getString(Sector1, sector_name_1.getText().toString()));
    	sector_name_2.setText(mTool.getString(Sector2, sector_name_2.getText().toString()));
    	sector_name_3.setText(mTool.getString(Sector3, sector_name_3.getText().toString()));
    	sector_name_4.setText(mTool.getString(Sector4, sector_name_4.getText().toString()));
    	sector_name_5.setText(mTool.getString(Sector5, sector_name_5.getText().toString()));
    }
    
    
    /* 이름 바꾸기 */
    private class DoEidt extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mLoagingDialog = ProgressDialog.show(SecuritySetOption.this, null, 
                    getResources().getString(R.string.on_loading), true);
        }

        @Override
        protected Long doInBackground(String... arg0) {
            return null;   
        }

        @Override
        protected void onPostExecute(Long result) {            
        	sector_name_1.setText(mTool.getString(Sector1, sector_name_1.getText().toString()));
        	sector_name_2.setText(mTool.getString(Sector2, sector_name_2.getText().toString()));
        	sector_name_3.setText(mTool.getString(Sector3, sector_name_3.getText().toString()));
        	sector_name_4.setText(mTool.getString(Sector4, sector_name_4.getText().toString()));
        	sector_name_5.setText(mTool.getString(Sector5, sector_name_5.getText().toString()));
            
            mLoagingDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    
    /* Send Custom Broadcast Message */
	private void sendCustomBroadcastMessage(String strBroadcastAction) {
		Intent intent = new Intent(strBroadcastAction);

		m_Context.sendBroadcast(intent);

		intent = null;
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
