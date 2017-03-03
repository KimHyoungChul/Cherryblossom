package com.commax.iphomeiot.calldial;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ContactsAddUserDialog extends Dialog implements OnClickListener, OnFocusChangeListener {
	private Context m_Context = null;
	/* Contacts Add User Event Listener */
	private OnContactsAddUserEventListener onContactsAddUserListener;

	private int m_nFragmentIndex = 0;			/* Fragment Index */
	private String m_strUserKey = "";			/* Contact User Key */
	private String m_strUserName = "";			/* Contact User Name */
	private String m_strUserNumber = "";		/* Contact User Number */

	/* Interface Contacts Add User Event Listener */
	public interface OnContactsAddUserEventListener {
		/* Contacts Add User Event */
		public void onContactsAddUserEvent(boolean bResponse);
	}

	public ContactsAddUserDialog(Context context, int nFragmentIndex, String strKey, String strName, String strNumber, OnContactsAddUserEventListener onContactsAddUserListener) {
		super(context);

		m_Context = context;

		/* Fragment Index */
		m_nFragmentIndex = nFragmentIndex;
		/* Contact User Key */
		m_strUserKey = strKey;
		/* Contact User Name */
		m_strUserName = strName;
		/* Contact User Number */
		m_strUserNumber = strNumber;

		/* Contacts Add User Event Listener */
		this.onContactsAddUserListener = onContactsAddUserListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contacts_add_user_dialog);

		/* Contact User Name */
		((EditText)findViewById(R.id.edtContactUserName)).setOnFocusChangeListener(this);
		/* Contact User Number */
		((EditText)findViewById(R.id.edtContactUserNumber)).setOnFocusChangeListener(this);

		((Button)findViewById(R.id.btnContactsAddDialogCancel)).setOnClickListener(this);
		((Button)findViewById(R.id.btnContactsAddDialogOk)).setOnClickListener(this);

		/* Contact User Name */
		((EditText)findViewById(R.id.edtContactUserName)).setText(m_strUserName);
		/* Contact User Number */
		((EditText)findViewById(R.id.edtContactUserNumber)).setText(m_strUserNumber);

		if((m_strUserKey.isEmpty() == false) && (m_strUserName.isEmpty() == false) && (m_strUserNumber.isEmpty() == false)) {
			/* STR : edit */
			((Button)findViewById(R.id.btnContactsAddDialogOk)).setText(R.string.STR_MODIFY);
		}
		else {
			/* STR : save */
			((Button)findViewById(R.id.btnContactsAddDialogOk)).setText(R.string.STR_SAVE);
		}

		((EditText)findViewById(R.id.edtContactUserName)).requestFocus();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		/* Send Custom Broadcast Message(System Key Show/Hide Action) */
		sendCustomBroadcastMessage((hasFocus == true) ? NameSpace.SYSTEM_KEY_SHOW_ACTION : NameSpace.SYSTEM_KEY_HIDE_ACTION);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		/* Contacts Add User Event */
		onContactsAddUserListener.onContactsAddUserEvent(false);

		/* Send Custom Broadcast Message(System Key Hide Action) */
		sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);

		cancel();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		/* Edit Contact User Name Focus */
		if(v.equals(findViewById(R.id.edtContactUserName))) {
			((ImageView)findViewById(R.id.imgContactUserNameLine)).setBackgroundColor(Color.parseColor((hasFocus == true) ? "#6A71CC" : "#CDCDDF"));
		}
		/* Edit Contact User Number Focus */
		else if(v.equals(findViewById(R.id.edtContactUserNumber))) {
			((ImageView)findViewById(R.id.imgContactUserNumberLine)).setBackgroundColor(Color.parseColor((hasFocus == true) ? "#6A71CC" : "#CDCDDF"));
		}
	}

	@Override
	public void onClick(View v) {
		if(v.equals(findViewById(R.id.btnContactsAddDialogCancel))) {
			/* Contacts Add User Event */
			onContactsAddUserListener.onContactsAddUserEvent(false);

			/* Send Custom Broadcast Message(System Key Hide Action) */
			sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);

			cancel();
		}
		else if(v.equals(findViewById(R.id.btnContactsAddDialogOk))) {
			/* Set Contacts Add User */
			if(setContactsAddUser() == true) {
				/* Contacts Add User Event */
				onContactsAddUserListener.onContactsAddUserEvent(true);

				/* Send Custom Broadcast Message(System Key Hide Action) */
				sendCustomBroadcastMessage(NameSpace.SYSTEM_KEY_HIDE_ACTION);

				dismiss();
			}
		}
	}

	/* Set Contacts Add User */
	private boolean setContactsAddUser() {
		String strUserName = "";
		String strUserNumber = "";

		/* Contact User Name */
		strUserName = ((EditText)findViewById(R.id.edtContactUserName)).getText().toString();
		/* Contact User Number */
		strUserNumber = ((EditText)findViewById(R.id.edtContactUserNumber)).getText().toString();

		if(strUserName.length() <= 0) {
			((EditText)findViewById(R.id.edtContactUserName)).requestFocus();

			/* Invalid User Input */
			invalidUserInput((EditText)findViewById(R.id.edtContactUserName));
			return false;
		}

		if(strUserNumber.length() <= 0) {
			((EditText)findViewById(R.id.edtContactUserNumber)).requestFocus();

			/* Invalid User Input */
			invalidUserInput((EditText)findViewById(R.id.edtContactUserNumber));
			return false;
		}

		/* Contact User Key */
		if(m_strUserKey.length() > 0) {
			/* Delete Contact */
			MainActivity.m_ContactsFragment.deleteContact(m_strUserKey);
		}

		/* Fragment Index */
		if(m_nFragmentIndex == NameSpace.TAB_DIALER_FRAGMENT) {
			/* Add Contact */
			MainActivity.m_DialerFragment.addContact(strUserName, strUserNumber);
		}
		else {
			/* Add Contact */
			MainActivity.m_ContactsFragment.addContact(strUserName, strUserNumber);
		}

		return true;
	}

	/* Invalid User Input */
	private void invalidUserInput(View view) {
		/* 에러 에니메이션 */
		Animation shake = AnimationUtils.loadAnimation(m_Context, R.anim.phone_shake);
		view.startAnimation(shake);
	}

	/* Send Custom Broadcast Message */
	private void sendCustomBroadcastMessage(String strBroadcastAction) {
		Intent intent = new Intent(strBroadcastAction);

		m_Context.sendBroadcast(intent);

		intent = null;
	}
}
