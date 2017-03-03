package com.commax.homecoach;

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
	
	public Button btn_ok;
	public TextView docu_text;
	
	Context m_Context;	
	String m_strValue;
	
	public CustomAlertsDialog(Context context) {
		super(context);
		m_Context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_alerts);
		
		docu_text = (TextView) findViewById(R.id.docu_text);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);

		docu_text.setText((String) m_Context.getResources().getString(R.string.check_ems_server));
	}

	@Override
	public void onClick(View v) {
		if (v == btn_ok) {
			dismiss();
		}
	}
}