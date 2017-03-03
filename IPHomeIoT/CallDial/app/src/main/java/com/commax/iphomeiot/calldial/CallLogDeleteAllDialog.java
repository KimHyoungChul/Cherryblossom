package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CallLogDeleteAllDialog extends Dialog implements OnClickListener {
	/* Call Log Delete All Event Listener */
	private OnCallLogDeleteAllEventListener onCallLogDeleteAllListener;

	/* Interface Call Log Delete All Event Listener */
	public interface OnCallLogDeleteAllEventListener {
		/* Call Log Delete All Event */
		public void onCallLogDeleteAllEvent(boolean bResponse);
	}

	public CallLogDeleteAllDialog(Context context, OnCallLogDeleteAllEventListener onCallLogDeleteAllListener) {
		super(context);

		/* Call Log Delete All Event Listener */
		this.onCallLogDeleteAllListener = onCallLogDeleteAllListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calllog_delete_all_dialog);

		((Button)findViewById(R.id.btnCallLogDeleteAllDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnCallLogDeleteAllDialogOk)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnCallLogDeleteAllDialogCancel))) {
			/* Call Log Delete All Event */
			onCallLogDeleteAllListener.onCallLogDeleteAllEvent(false);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnCallLogDeleteAllDialogOk))) {
			/* Call Log Delete All Event */
			onCallLogDeleteAllListener.onCallLogDeleteAllEvent(true);

			dismiss();
		}
	}
}
