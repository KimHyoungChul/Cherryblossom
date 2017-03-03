package com.commax.iphomeiot.calldial;

import android.net.Uri;
import android.view.KeyEvent;

public class NameSpace {
	static final String DEBUG_TAG = "DIALER";

	static final String SETTING_FILE_PATH = "/user/app/bin/settings.i";
	static final String SLAVE_ID_TABLE_FILE_NAME = "/user/app/bin/slave_id_table.i";
	static final String CONCIERGE_INFO_LIST_FILE = "/user/app/bin/concierge_list.i";	/* Concierge Information List File */

	static final int DIAL_MAX_PSTN = 18;
	static final int DIAL_MAX_NEIG = 9;
	static final int DIAL_MAX_EXT = 3;

	static final String KEY_PSTN = "dialer_pstn";
	static final String KEY_NEIG = "dialer_neighbor";
	static final String KEY_EXT = "dialer_ext";
	static final String KEY_DOOR = "dialer_door";
	static final String KEY_GUARD = "dialer_guard";
	static final String KEY_OFFICE = "dialer_office";
	static final String KEY_TYPE = "type";

	static final int TAB_DIALER_FRAGMENT = 1;							/* Tab Dialer Fragment */
	static final int TAB_CALLLOG_FRAGMENT = 2;							/* Tab Call Log Fragment */
	static final int TAB_CONTACTS_FRAGMENT = 3;							/* Tab Contacts Fragment */

	static final String STRING_TAB_DIALER_FRAGMENT = "Dialer";			/* String Tab Dialer Fragment */
	static final String STRING_TAB_CALLLOG_FRAGMENT = "CallLog";		/* String Tab Call Log Fragment */
	static final String STRING_TAB_CONTACTS_FRAGMENT = "Contacts";		/* String Tab Contacts Fragment */
	
	static final String ADMIN_CODE = "" + KeyEvent.KEYCODE_0 + KeyEvent.KEYCODE_DEL + KeyEvent.KEYCODE_0 + KeyEvent.KEYCODE_DEL + KeyEvent.KEYCODE_0 + KeyEvent.KEYCODE_DEL;

	/* Local Server IP Context URI */
	static final Uri LOCAL_SERVER_IP_CONTEXT_URI	= Uri.parse("content://com.commax.provider.settings/setting/1");
	/* Home Address(dong/ho) Context URI */
	static final Uri HOME_ADDRESS_CONTEXT_URI		= Uri.parse("content://com.commax.provider.settings/setting/9");

	static final String SYSTEM_KEY_SHOW_ACTION		= "com.commax.systemkey.SHOW_ACTION";	/* System Key Show Action */
	static final String SYSTEM_KEY_HIDE_ACTION		= "com.commax.systemkey.HIDE_ACTION";	/* System Key Hide Action */

	static final int OUTGOING_NEIGHBORHOOD = 0;
	static final int OUTGOING_DOOR = 1;
	static final int OUTGOING_GUARD = 2;
	static final int OUTGOING_OFFICE = 3;
}
