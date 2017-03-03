package com.commax.wirelesssetcontrol;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.commax.wirelesssetcontrol.activity.AddSpaceActivity;
import com.commax.wirelesssetcontrol.activity.EditSpaceActivity;

public class SpaceDialog extends Dialog implements OnClickListener {

	private Button mRightButton;
	private Button mLeftButton;
	Context m_Context;

	private View.OnClickListener mLeftClickListener;
	private View.OnClickListener mRightClickListener;

	public SpaceDialog(Context context) {
		super(context);
		m_Context = context;
	}

	public SpaceDialog(Context context,
					   View.OnClickListener leftListener , View.OnClickListener rightListener) {
		super(context);
		m_Context = context;
		this.mLeftClickListener = leftListener;
		this.mRightClickListener = rightListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_space);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

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
		mLeftButton = (Button) findViewById(R.id.btn_cancel);
		mLeftButton.setOnClickListener(this);
		setClickListener(mLeftClickListener, mRightClickListener);
    }

	private void setClickListener(View.OnClickListener left , View.OnClickListener right){
		if(left!=null && right!=null){
			mLeftButton.setOnClickListener(left);
			mRightButton.setOnClickListener(right);
		}else if(left!=null && right==null){
			mLeftButton.setOnClickListener(left);
		}else {

		}
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
		try {
			cancel();
//
//			((EditSpaceActivity)m_Context).finish();
		}catch (Exception e){
			e.printStackTrace();
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