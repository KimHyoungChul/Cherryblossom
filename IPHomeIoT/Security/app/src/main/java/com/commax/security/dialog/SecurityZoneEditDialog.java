package com.commax.security.dialog;

import com.commax.security.R;
import com.commax.security.SecuritySetOption;
import com.commax.security.Config;
import com.commax.security.NameSpace;
import com.commax.security.PreferenceTool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SecurityZoneEditDialog extends Dialog implements OnClickListener, OnFocusChangeListener {
			
	private Button mLeftButton, mRightButton;
	Context m_Context;	
	
	public SecurityZoneEditDialog(Context context) {
		super(context);
		m_Context = context;
	}
	
	public static PreferenceTool mTool = null;
	
	// PreferenceTool Key Values
    private static final String Sector1 = "sector1";
    private static final String Sector2 = "sector2";
    private static final String Sector3 = "sector3";
    private static final String Sector4 = "sector4";
    private static final String Sector5 = "sector5";
    
    /* 구역 */
    private LinearLayout ll_sector1;
    private LinearLayout ll_sector2;
    private LinearLayout ll_sector3;
    private LinearLayout ll_sector4;
    private LinearLayout ll_sector5;
    
    public static EditText sector1;
    public static EditText sector2;
    public static EditText sector3;
    public static EditText sector4;
    public static EditText sector5;
    
    private ImageView underLine1;
	private ImageView underLine2;
	private ImageView underLine3;
	private ImageView underLine4;
	private ImageView underLine5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_securityedit);

		// 2017.01.06_yclee : ?????? ?????
//		hideNavigationBar();
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		
		setContent();
	}

	private void setContent() {
		mTool = new PreferenceTool(m_Context);
		
		ll_sector1 = (LinearLayout) findViewById(R.id.sector1);
	    ll_sector2 = (LinearLayout) findViewById(R.id.sector2);
	    ll_sector3 = (LinearLayout) findViewById(R.id.sector3);
	    ll_sector4 = (LinearLayout) findViewById(R.id.sector4);
	    ll_sector5 = (LinearLayout) findViewById(R.id.sector5);
				       
        sector1 = (EditText) findViewById(R.id.edit_sector1);
        sector2 = (EditText) findViewById(R.id.edit_sector2);
        sector3 = (EditText) findViewById(R.id.edit_sector3);
        sector4 = (EditText) findViewById(R.id.edit_sector4);
        sector5 = (EditText) findViewById(R.id.edit_sector5);
        
        /* 팝업 실행시 첫 번째 항목에 포거스 가도록 함 */
        sector1.requestFocus();
        
        /* 밑줄 색상 변경시 필요 */
        sector1.setOnFocusChangeListener(this);
        sector2.setOnFocusChangeListener(this);
        sector3.setOnFocusChangeListener(this);
        sector4.setOnFocusChangeListener(this);
        sector5.setOnFocusChangeListener(this);
        
        underLine1 = (ImageView) findViewById(R.id.underLine1);
		underLine2 = (ImageView) findViewById(R.id.underLine2);
		underLine3 = (ImageView) findViewById(R.id.underLine3);
		underLine4 = (ImageView) findViewById(R.id.underLine4);
		underLine5 = (ImageView) findViewById(R.id.underLine5);
		
		/* 저장된 구역 이름으로 보여줌 */ 
		sector1.setText(mTool.getString(Sector1, ""));
		sector2.setText(mTool.getString(Sector2, ""));
		sector3.setText(mTool.getString(Sector3, ""));
		sector4.setText(mTool.getString(Sector4, ""));
		sector5.setText(mTool.getString(Sector5, ""));
        		
		mLeftButton = (Button) findViewById(R.id.btn_cancel);
		mRightButton = (Button) findViewById(R.id.btn_ok); 
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);
        
        sensors_show();
    }
				
	private void sensors_show() {
		if (Config.nPreventSensorUsed1State == -1) {        	
			ll_sector1.setVisibility(View.GONE);
        }
        else {
        	ll_sector1.setVisibility(View.VISIBLE);
        }
		
		if (Config.nPreventSensorUsed2State == -1) {        	
			ll_sector2.setVisibility(View.GONE);
        }
        else {
        	ll_sector2.setVisibility(View.VISIBLE);
        }
		
		if (Config.nPreventSensorUsed3State == -1) {        	
			ll_sector3.setVisibility(View.GONE);
        }
        else {
        	ll_sector3.setVisibility(View.VISIBLE);
        }
		
		if (Config.nPreventSensorUsed4State == -1) {        	
			ll_sector4.setVisibility(View.GONE);
        }
        else {
        	ll_sector4.setVisibility(View.VISIBLE);
        }
		
		if (Config.nPreventSensorUsed5State == -1) {        	
			ll_sector5.setVisibility(View.GONE);
        }
        else {
        	ll_sector5.setVisibility(View.VISIBLE);
        }
	}
	
	/* 다이얼로그 배경 터치시 키보드 감추기 */
//	private void hideKeyboard() {
//	    InputMethodManager imm = (InputMethodManager) m_Context.getSystemService(m_Context.INPUT_METHOD_SERVICE);
//	    
//	    imm.hideSoftInputFromWindow(sector1.getWindowToken(), 0);
//	    imm.hideSoftInputFromWindow(sector2.getWindowToken(), 0);
//	    imm.hideSoftInputFromWindow(sector3.getWindowToken(), 0);
//	    imm.hideSoftInputFromWindow(sector4.getWindowToken(), 0);
//	    imm.hideSoftInputFromWindow(sector5.getWindowToken(), 0);
//	}
//	
//	@Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Rect dialogBounds = new Rect();
//        getWindow().getDecorView().getHitRect(dialogBounds);
// 
//        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
//        	hideKeyboard();
//        }
//        return super.dispatchTouchEvent(ev);
//    }
	/* 다이얼로그 배경 터치시 키보드 감추기 */

	@Override
	public void onClick(View v) {
		if (v == mLeftButton) {
			Config.bOK_btn_click = false;
	        
			cancel();
		}
		else {
			Config.bOK_btn_click = true;
			
			mTool.putString(Sector1, sector1.getText().toString());
	        mTool.putString(Sector2, sector2.getText().toString());
	        mTool.putString(Sector3, sector3.getText().toString());
	        mTool.putString(Sector4, sector4.getText().toString());
	        mTool.putString(Sector5, sector5.getText().toString());
	        
			dismiss();
		}
	}

	/* EditText 밑줄 색상 변경 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.edit_sector1:
				if (hasFocus) {
					underLine1.setBackgroundColor(Color.parseColor("#6A71CC"));
				}
				else {
					underLine1.setBackgroundColor(Color.parseColor("#CDCDDF"));
				}
				break;

			case R.id.edit_sector2:
				if (hasFocus) {
					underLine2.setBackgroundColor(Color.parseColor("#6A71CC"));
				}
				else {
					underLine2.setBackgroundColor(Color.parseColor("#CDCDDF"));
				}
				break;
				
			case R.id.edit_sector3:
				if (hasFocus) {
					underLine3.setBackgroundColor(Color.parseColor("#6A71CC"));
				}
				else {
					underLine3.setBackgroundColor(Color.parseColor("#CDCDDF"));
				}
				break;
				
			case R.id.edit_sector4:
				if (hasFocus) {
					underLine4.setBackgroundColor(Color.parseColor("#6A71CC"));
				}
				else {
					underLine4.setBackgroundColor(Color.parseColor("#CDCDDF"));
				}
				break;
				
			case R.id.edit_sector5:
				if (hasFocus) {
					underLine5.setBackgroundColor(Color.parseColor("#6A71CC"));
				}
				else {
					underLine5.setBackgroundColor(Color.parseColor("#CDCDDF"));
				}
				break;
				
			default:
				break;
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		/* Send Custom Broadcast Message(System Key Show/Hide Action) */
		sendCustomBroadcastMessage((hasFocus == true) ? NameSpace.SYSTEM_KEY_SHOW_ACTION : NameSpace.SYSTEM_KEY_HIDE_ACTION);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		/* Send Custom Broadcast Message(System Key Hide Action) */
		sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);

		cancel();
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