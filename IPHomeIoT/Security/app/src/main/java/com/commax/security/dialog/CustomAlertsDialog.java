package com.commax.security.dialog;

import com.commax.security.Config;
import com.commax.security.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class CustomAlertsDialog extends Dialog implements OnClickListener {
	
	public Button btn_cancel, btn_ok;
	public TextView docu_text;
	
	Context m_Context;	
	String m_strValue;
	
	public CustomAlertsDialog(Context context, String strValue) {
		super(context);
		m_Context = context;
		m_strValue = strValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_alerts);

		// 2017.01.06_yclee : ?????? ?????
		hideNavigationBar();
		
		docu_text = (TextView) findViewById(R.id.docu_text);		
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok = (Button) findViewById(R.id.btn_ok); 
		btn_cancel.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
			
		if (m_strValue.equals("7")) {	// Network failed
			docu_text.setText((String) m_Context.getResources().getString(R.string.msg_check_network));
			btn_cancel.setVisibility(View.GONE);
		}
		else if (m_strValue.equals("8")) {	// 이미 외출모드 상태
			docu_text.setText((String) m_Context.getResources().getString(R.string.msg_turnoff_outmode));
			btn_cancel.setVisibility(View.VISIBLE);
		}
		else if (m_strValue.equals("9")) {	// 이미 방범모드 상태
			docu_text.setText((String) m_Context.getResources().getString(R.string.msg_turnoff_security));
			btn_cancel.setVisibility(View.VISIBLE);
		}
		else {	// 센서 열림 상태
			docu_text.setText(getFormattedString(m_strValue));
			btn_cancel.setVisibility(View.GONE);
		}
	}
		
	// 열린 센서 표시해주는 다이얼로그에서 사용
    private String getFormattedString(String value){
        String strFrom = "";
        strFrom = (String) m_Context.getResources().getString(R.string.msg_setting_fail_sensor_open);
        String strNum = TextUtils.htmlEncode(value);
        String strResult = String.format(strFrom, strNum);
        return strResult;
    }

	@Override
	public void onClick(View v) {
		if (v == btn_cancel) {
			Config.bOK_btn_click = false;
			cancel();
		}
		else {
			Config.bOK_btn_click = true;
			dismiss();
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