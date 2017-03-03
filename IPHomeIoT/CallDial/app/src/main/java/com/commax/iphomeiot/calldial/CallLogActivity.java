package com.commax.iphomeiot.calldial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CallLogActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(CallLogActivity.this, MainActivity.class);
		/* String Tab Call Log Fragment */
		startActivity(intent.setAction(NameSpace.STRING_TAB_CALLLOG_FRAGMENT));

		intent = null;

		finish();
	}
}
