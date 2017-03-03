package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CallLogDeleteDialog extends Dialog implements OnClickListener {
	/* Call Log Delete Event Listener */
	private OnCallLogDeleteEventListener onCallLogDeleteListener;

	/* Interface Call Log Delete Event Listener */
	public interface OnCallLogDeleteEventListener {
		/* Call Log Delete Event */
		public void onCallLogDeleteEvent(boolean bResponse);
	}

	public CallLogDeleteDialog(Context context, OnCallLogDeleteEventListener onCallLogDeleteListener) {
		super(context);

		/* Call Log Delete Event Listener */
		this.onCallLogDeleteListener = onCallLogDeleteListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.calllog_delete_dialog);

		((Button)findViewById(R.id.btnCallLogDeleteDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnCallLogDeleteDialogOk)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnCallLogDeleteDialogCancel))) {
			/* Call Log Delete Event */
			onCallLogDeleteListener.onCallLogDeleteEvent(false);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnCallLogDeleteDialogOk))) {
			/* Call Log Delete Event */
			onCallLogDeleteListener.onCallLogDeleteEvent(true);

			dismiss();
		}
	}
}
