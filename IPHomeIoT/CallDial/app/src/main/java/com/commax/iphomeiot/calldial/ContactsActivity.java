package com.commax.iphomeiot.calldial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ContactsActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(ContactsActivity.this, MainActivity.class);
		/* String Tab Contacts Fragment */
		startActivity(intent.setAction(NameSpace.STRING_TAB_CONTACTS_FRAGMENT));

		intent = null;

		finish();
	}
}
