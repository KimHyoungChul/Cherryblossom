package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ContactsDeleteAllDialog extends Dialog implements OnClickListener {
	/* Contacts Delete All Event Listener */
	private OnContactsDeleteAllEventListener onContactsDeleteAllListener;

	/* Interface Contacts Delete All Event Listener */
	public interface OnContactsDeleteAllEventListener {
		/* Contacts Delete All Event */
		public void onContactsDeleteAllEvent(boolean bResponse);
	}

	public ContactsDeleteAllDialog(Context context, OnContactsDeleteAllEventListener onContactsDeleteAllListener) {
		super(context);

		/* Contacts Delete All Event Listener */
		this.onContactsDeleteAllListener = onContactsDeleteAllListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_delete_all_dialog);

		((Button)findViewById(R.id.btnContactsDeleteAllDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnContactsDeleteAllDialogOk)).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnContactsDeleteAllDialogCancel))) {
			/* Contacts Delete All Event */
			onContactsDeleteAllListener.onContactsDeleteAllEvent(false);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnContactsDeleteAllDialogOk))) {
			/* Contacts Delete All Event */
			onContactsDeleteAllListener.onContactsDeleteAllEvent(true);

			dismiss();
		}
	}
}
