package com.commax.wirelesssetcontrol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialog extends Dialog implements OnClickListener {
	
	private Button mRightButton;
	Context m_Context;
	String pop_up_text;
	TextView tv_text;
	
	public AlertDialog(Context context, String text) {
		super(context);
		m_Context = context;
		pop_up_text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_alert);

		hideNavigationBar();
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		
		setContent();
	}

	private void setContent() {

		mRightButton = (Button) findViewById(R.id.btn_ok);
        mRightButton.setOnClickListener(this);
		tv_text = (TextView) findViewById(R.id.tv_text);
		tv_text.setText(pop_up_text);
    }

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

	@Override
	public void onClick(View v) {
		if (v == mRightButton) {
//			Config.bOK_btn_click = false;
	        
			cancel();
		}
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