package com.commax.control.Common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.commax.control.MainActivity;
import com.commax.control.R;

public class AlertDialog extends Dialog implements OnClickListener {

	Context m_Context;
	private Button mRightButton;
	private TextView mtv_text;
	private String mPrintMessge;
	private boolean mbExitProgram;
	
	public AlertDialog(Context context) {
		super(context);
		m_Context = context;
		this.mPrintMessge = "";
		this.mbExitProgram = false;
	}

	public AlertDialog(Context context, String message) {
		super(context);
		m_Context = context;
		this.mPrintMessge = message;
		this.mbExitProgram = false;
	}

	public AlertDialog(Context context, String message, boolean exitProgram) {
		super(context);
		m_Context = context;
		this.mPrintMessge = message;
		this.mbExitProgram = exitProgram;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_alert);
	}
		
	@Override
	protected void onStart() {
		super.onStart();
		setContent();
	}

	private void setContent() {

		mRightButton = (Button) findViewById(R.id.btn_ok);
		mtv_text = (TextView) findViewById(R.id.tv_text);
		mtv_text.setText(mPrintMessge);
        mRightButton.setOnClickListener(this);
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
			if(mbExitProgram) MainActivity.getInstance().finish();
			else cancel();
		}
	}

}