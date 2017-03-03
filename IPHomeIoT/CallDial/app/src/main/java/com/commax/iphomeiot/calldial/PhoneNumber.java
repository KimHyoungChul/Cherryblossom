package com.commax.iphomeiot.calldial;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class PhoneNumber {
	private static final String PROVIDER_URI = "content://com.commax.provider.settings/setting";
	private static final int PROVIDER_COLUMN_INDEX = 2;
	private static final int GUARD_PROVIDER_INDEX = 7;
	private static final int OFFICE_INDEX = 8;
	private Context context;

	public PhoneNumber(Context context) {
		this.context = context;
	}

	public String getValue(int id) {
		String ret = null;

		Uri uri = Uri.parse(PROVIDER_URI + "/" + Integer.toString(id));
		Cursor c = context.getContentResolver().query(uri, null, null, null,
				null);
		if (c != null) {
			if (c.moveToFirst()) {
				ret = c.getString(PROVIDER_COLUMN_INDEX);
			}
			c.close();
		}

		return ret;
	}

	public String getGuard() {

		return getValue(GUARD_PROVIDER_INDEX);

	}

	public String getOffice() {

		return getValue(OFFICE_INDEX);
	}
}
