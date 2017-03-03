package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ContactsDeleteDialog extends Dialog implements OnClickListener {
	/* Contacts Delete Event Listener */
	private OnContactsDeleteEventListener onContactsDeleteListener;

	/* Interface Contacts Delete Event Listener */
	public interface OnContactsDeleteEventListener {
		/* Contacts Delete Event */
		public void onContactsDeleteEvent(boolean bResponse);
	}

	public ContactsDeleteDialog(Context context, OnContactsDeleteEventListener onContactsDeleteListener) {
		super(context);

		/* Contacts Delete Event Listener */
		this.onContactsDeleteListener = onContactsDeleteListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_delete_dialog);

		((Button)findViewById(R.id.btnContactsDeleteDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnContactsDeleteDialogOk)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnContactsDeleteDialogCancel))) {
			/* Contacts Delete Event */
			onContactsDeleteListener.onContactsDeleteEvent(false);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnContactsDeleteDialogOk))) {
			/* Contacts Delete Event */
			onContactsDeleteListener.onContactsDeleteEvent(true);

			dismiss();
		}
	}
}
