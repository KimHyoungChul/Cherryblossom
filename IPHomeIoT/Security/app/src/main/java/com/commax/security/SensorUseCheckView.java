package com.commax.security;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class SensorUseCheckView extends Activity implements View.OnClickListener, OnCheckedChangeListener {

	public static Config m_Config = null; /* System Config Object */
	public static SktManager m_SktManager = null; /* Socket Manager Object */
	private Context m_Context = null;

	public static ProgressDialog mLoagingDialog = null;

	public static SensorUseCheckView m_instance;
	private BroadcastReceiver m_BroadcastReceiver = null; // Broadcast Receiver

	public static SensorUseCheckView getInstance() {
		return m_instance;
	}

	private Button btn_close;
	
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.used_check_view);

		// 2017.01.06_yclee : 네비게이션바 안나오도록
		hideNavigationBar();

		m_Context = this;
		m_instance = this;

		/* Socket Manager Object */
		if (m_SktManager == null)
			m_SktManager = new SktManager(getApplicationContext());

		/* System Config Object */
		if (m_Config == null)
			m_Config = new Config(getContentResolver());

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		/* 메뉴 동작 */
		((LinearLayout) findViewById(R.id.menu_Setall)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.menu_Desetall)).setOnClickListener(this);
		((LinearLayout) findViewById(R.id.menu_Set)).setOnClickListener(this);

		/* 구역 동작 */
		((CheckBox) findViewById(R.id.ckb_sector1)).setOnCheckedChangeListener(this);
		((CheckBox) findViewById(R.id.ckb_sector2)).setOnCheckedChangeListener(this);
		((CheckBox) findViewById(R.id.ckb_sector3)).setOnCheckedChangeListener(this);
		((CheckBox) findViewById(R.id.ckb_sector4)).setOnCheckedChangeListener(this);
		((CheckBox) findViewById(R.id.ckb_sector5)).setOnCheckedChangeListener(this);
		
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
        
        /* 버튼 터치시 Opacity 변경 */
        btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onDestroy();
			}			
		});

		/* 센서 사용여부 가져오기 */
		m_SktManager.getSensorAreaUsed(m_Config.m_strHomeSvrIPAddr);

		/* Get Outing Security Mode */
		m_SktManager.getOutingSecurityMode(m_Config.m_strHomeSvrIPAddr);

		displaySensorAreaUsed();

		/* 이미 외출 모드 일 때 메시지 팝업 */
		if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityOutingMode) {
			createDialog(NameSpace.DIALOG_ALREADY_OUT_SETTED);
		}

		/* 이미 방범 설정 일 때 메시지 팝업 */
		else if (m_SktManager.getSecurityMode(m_Config.m_strHomeSvrIPAddr) == NameSpace.SecurityPreventMode) {
			createDialog(NameSpace.DIALOG_TRUNOFF_SECURITY);
		}

		/* Broadcast Receiver call */
		createBroadcastReceiver();
	}

	/* Create Broadcast Receiver */
	protected void createBroadcastReceiver() {
		/* Broadcast Receiver */
		if (m_BroadcastReceiver == null) {
			m_BroadcastReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					try {
						Log.i(NameSpace.DEBUG_TAG, "intent.getAction() : " + intent.getAction());

						/* HomeKey 눌렀을 경우 프로세스 종료 */
						if (NameSpace.REQ_HOME_CATEGOTY_EVENT.equals(intent.getCategories())) {
							onDestroy();
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			/* HomeKey Broadcast Message */
			registerReceiver(m_BroadcastReceiver, new IntentFilter(NameSpace.REQ_HOME_CATEGOTY_EVENT));
		}
	}

	/* Display Sensor Area Used */
	protected void displaySensorAreaUsed() {

		/* 센서구역(설정 않함 : 0, 사용함 설정 : 1) */
		/* 구역 1 */
		if (NameSpace.nSensorUsed1State == -1) {
			((FrameLayout) findViewById(R.id.frameLayout1)).setVisibility(View.GONE);
		}
		else {
			((FrameLayout) findViewById(R.id.frameLayout1)).setVisibility(View.VISIBLE);
			((CheckBox) findViewById(R.id.ckb_sector1)).setChecked((NameSpace.nSensorUsed1State == 0) ? false : true);
		}

		/* 구역 2 */
		if (NameSpace.nSensorUsed2State == -1) {
			((FrameLayout) findViewById(R.id.frameLayout2)).setVisibility(View.GONE);
		}
		else {
			((FrameLayout) findViewById(R.id.frameLayout2)).setVisibility(View.VISIBLE);
			((CheckBox) findViewById(R.id.ckb_sector2)).setChecked((NameSpace.nSensorUsed2State == 0) ? false : true);
		}

		/* 구역 3 */
		if (NameSpace.nSensorUsed3State == -1) {
			((FrameLayout) findViewById(R.id.frameLayout3)).setVisibility(View.GONE);
		}
		else {
			((FrameLayout) findViewById(R.id.frameLayout3)).setVisibility(View.VISIBLE);
			((CheckBox) findViewById(R.id.ckb_sector3)).setChecked((NameSpace.nSensorUsed3State == 0) ? false : true);
		}

		/* 구역 4 */
		if (NameSpace.nSensorUsed4State == -1) {
			((FrameLayout) findViewById(R.id.frameLayout4)).setVisibility(View.GONE);
		}
		else {
			((FrameLayout) findViewById(R.id.frameLayout4)).setVisibility(View.VISIBLE);
			((CheckBox) findViewById(R.id.ckb_sector4)).setChecked((NameSpace.nSensorUsed4State == 0) ? false : true);
		}

		/* 구역 5 */
		if (NameSpace.nSensorUsed5State == -1) {
			((FrameLayout) findViewById(R.id.frameLayout5)).setVisibility(View.GONE);
		}
		else {
			((FrameLayout) findViewById(R.id.frameLayout5)).setVisibility(View.VISIBLE);
			((CheckBox) findViewById(R.id.ckb_sector5)).setChecked((NameSpace.nSensorUsed5State == 0) ? false : true);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			/* 메뉴 */
			case R.id.menu_Setall: // 전체 선택

				// /* 동작중... */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_on_working, Toast.LENGTH_SHORT).show();

				((CheckBox) findViewById(R.id.ckb_sector1)).setChecked(true);
				((CheckBox) findViewById(R.id.ckb_sector2)).setChecked(true);
				((CheckBox) findViewById(R.id.ckb_sector3)).setChecked(true);
				((CheckBox) findViewById(R.id.ckb_sector4)).setChecked(true);
				((CheckBox) findViewById(R.id.ckb_sector5)).setChecked(true);

				NameSpace.nSensorUsed1State = 1;
				NameSpace.nSensorUsed2State = 1;
				NameSpace.nSensorUsed3State = 1;
				NameSpace.nSensorUsed4State = 1;
				NameSpace.nSensorUsed5State = 1;

				/* 센서 구역 설정 */
				// setSensorUsedArea();
				new DoSave().execute();

				// /* 설정완료 */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_setting_success, Toast.LENGTH_SHORT).show();

				break;

			case R.id.menu_Desetall: // 전체 해제
				// /* 동작중... */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_on_working, Toast.LENGTH_SHORT).show();

				((CheckBox) findViewById(R.id.ckb_sector1)).setChecked(false);
				((CheckBox) findViewById(R.id.ckb_sector2)).setChecked(false);
				((CheckBox) findViewById(R.id.ckb_sector3)).setChecked(false);
				((CheckBox) findViewById(R.id.ckb_sector4)).setChecked(false);
				((CheckBox) findViewById(R.id.ckb_sector5)).setChecked(false);

				NameSpace.nSensorUsed1State = 0;
				NameSpace.nSensorUsed2State = 0;
				NameSpace.nSensorUsed3State = 0;
				NameSpace.nSensorUsed4State = 0;
				NameSpace.nSensorUsed5State = 0;

				/* 센서 구역 설정 */
				// setSensorUsedArea();
				new DoSave().execute();

				// /* 설정완료 */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_setting_success, Toast.LENGTH_SHORT).show();

				break;

			case R.id.menu_Set: // 설정
				// /* 동작중... */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_on_working, Toast.LENGTH_SHORT).show();

				/* 센서 구역 설정 */
				// setSensorUsedArea();
				new DoSave().execute();

				// /* 설정완료 */
				// Toast.makeText(SensorUseCheckView.this,
				// R.string.msg_setting_success, Toast.LENGTH_SHORT).show();

				break;

			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton chkView, boolean isChecked) {
		switch (chkView.getId()) {

			/* 구역 1 */
			case R.id.ckb_sector1:
				if (isChecked) {
					((CheckBox) findViewById(R.id.ckb_sector1)).setChecked(true);
					NameSpace.nSensorUsed1State = 1;
				}
				else {
					((CheckBox) findViewById(R.id.ckb_sector1)).setChecked(false);
					NameSpace.nSensorUsed1State = 0;
				}
				break;

			/* 구역 2 */
			case R.id.ckb_sector2:
				if (isChecked) {
					((CheckBox) findViewById(R.id.ckb_sector2)).setChecked(true);
					NameSpace.nSensorUsed2State = 1;
				}
				else {
					((CheckBox) findViewById(R.id.ckb_sector2)).setChecked(false);
					NameSpace.nSensorUsed2State = 0;
				}
				break;

			/* 구역 3 */
			case R.id.ckb_sector3:
				if (isChecked) {
					((CheckBox) findViewById(R.id.ckb_sector3)).setChecked(true);
					NameSpace.nSensorUsed3State = 1;
				}
				else {
					((CheckBox) findViewById(R.id.ckb_sector3)).setChecked(false);
					NameSpace.nSensorUsed3State = 0;
				}
				break;

			/* 구역 4 */
			case R.id.ckb_sector4:
				if (isChecked) {
					((CheckBox) findViewById(R.id.ckb_sector4)).setChecked(true);
					NameSpace.nSensorUsed4State = 1;
				}
				else {
					((CheckBox) findViewById(R.id.ckb_sector4)).setChecked(false);
					NameSpace.nSensorUsed4State = 0;
				}
				break;

			/* 구역 5 */
			case R.id.ckb_sector5:
				if (isChecked) {
					((CheckBox) findViewById(R.id.ckb_sector5)).setChecked(true);
					NameSpace.nSensorUsed5State = 1;
				}
				else {
					((CheckBox) findViewById(R.id.ckb_sector5)).setChecked(false);
					NameSpace.nSensorUsed5State = 0;
				}
				break;

			default:
				break;
		}
	}

	protected void createDialog(int id) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SensorUseCheckView.this);

		switch (id) {
			case NameSpace.DIALOG_ALREADY_OUT_SETTED:
				alertBuilder/*.setIcon(R.drawable.icon_info)*/.setTitle(R.string.msg_already_outmode_restore).setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								onDestroy();
							}
						});
				break;

			case NameSpace.DIALOG_TRUNOFF_SECURITY:
				alertBuilder/*.setIcon(R.drawable.icon_info)*/.setTitle(R.string.msg_turnoff_security).setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								onDestroy();
							}
						});
				break;
			default:
				break;
		}

		Dialog alert = alertBuilder.create();
		alert.show();
	}

	/* 센서 구역 설정 */
	private void setSensorUsedArea() {
		Log.i(NameSpace.DEBUG_TAG, "NameSpace.nSensorUsed1State : " + NameSpace.nSensorUsed1State);
		Log.i(NameSpace.DEBUG_TAG, "NameSpace.nSensorUsed2State : " + NameSpace.nSensorUsed2State);
		Log.i(NameSpace.DEBUG_TAG, "NameSpace.nSensorUsed3State : " + NameSpace.nSensorUsed3State);
		Log.i(NameSpace.DEBUG_TAG, "NameSpace.nSensorUsed4State : " + NameSpace.nSensorUsed4State);
		Log.i(NameSpace.DEBUG_TAG, "NameSpace.nSensorUsed5State : " + NameSpace.nSensorUsed5State);

		m_SktManager.setSensorAreaUsed(m_Config.m_strHomeSvrIPAddr, // 홈 서버 IP
				NameSpace.nSensorUsed1State, NameSpace.nSensorUsed2State, NameSpace.nSensorUsed3State, NameSpace.nSensorUsed4State,
				NameSpace.nSensorUsed5State);

		if ((NameSpace.nSensorUsed1PrevState != NameSpace.nSensorUsed1State) && (1 == NameSpace.nSensorUsed1State)) {
			NameSpace.nOutingSensorUsed1State = 1;
			NameSpace.nSensorUsed1PrevState = NameSpace.nOutingSensorUsed1State;
		}
		if ((NameSpace.nSensorUsed2PrevState != NameSpace.nSensorUsed2State) && (1 == NameSpace.nSensorUsed2State)) {
			NameSpace.nOutingSensorUsed2State = 1;
			NameSpace.nSensorUsed2PrevState = NameSpace.nOutingSensorUsed2State;
		}
		if ((NameSpace.nSensorUsed3PrevState != NameSpace.nSensorUsed3State) && (1 == NameSpace.nSensorUsed3State)) {
			NameSpace.nOutingSensorUsed3State = 1;
			NameSpace.nSensorUsed3PrevState = NameSpace.nOutingSensorUsed3State;
		}
		if ((NameSpace.nSensorUsed4PrevState != NameSpace.nSensorUsed4State) && (1 == NameSpace.nSensorUsed4State)) {
			NameSpace.nOutingSensorUsed4State = 1;
			NameSpace.nSensorUsed4PrevState = NameSpace.nOutingSensorUsed4State;
		}
		if ((NameSpace.nSensorUsed5PrevState != NameSpace.nSensorUsed5State) && (1 == NameSpace.nSensorUsed5State)) {
			NameSpace.nOutingSensorUsed5State = 1;
			NameSpace.nSensorUsed5PrevState = NameSpace.nOutingSensorUsed5State;
		}

		m_SktManager.setOutingSecurityMode(m_Config.m_strHomeSvrIPAddr, // 홈 서버
																		// IP
				0, // 외출 모드 설정 요청(외출 모드 옵션 변경 : 0, 외출 모드 요청 : 1)
				NameSpace.nOutingVisitorCatpure, NameSpace.nOutingLightAllOff, NameSpace.nOutingGasClose, NameSpace.nOutingBypassCall,
				NameSpace.nOutingSensorUsed1State, NameSpace.nOutingSensorUsed2State, NameSpace.nOutingSensorUsed3State,
				NameSpace.nOutingSensorUsed4State, NameSpace.nOutingSensorUsed5State, NameSpace.nOutingDelayTime, NameSpace.nOutingReturnTime);
	}

	/* 설정 저장시 */
	private class DoSave extends AsyncTask<String, Integer, Long> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mLoagingDialog = ProgressDialog.show(SensorUseCheckView.this, null, getResources().getString(R.string.on_loading), true);
		}

		@Override
		protected Long doInBackground(String... arg0) {
			setSensorUsedArea();
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			mLoagingDialog.dismiss();
			
			/* 설정완료 */
        	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_popup,
                    (ViewGroup) findViewById(R.id.custom_toast_layout));
            
            TextView text = (TextView) layout.findViewById(R.id.custom_toast_text);
            text.setText(getResources().getString(R.string.msg_setting_success));

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();

		NameSpace.HomeKeyEventCheck = 1;
	}

	protected void onDestroy() {
		super.onDestroy();

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
