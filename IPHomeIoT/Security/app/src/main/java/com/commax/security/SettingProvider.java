package com.commax.security;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SettingProvider {

	private static final String PROVIDER_URI = "content://com.commax.provider.settings/setting";
	private static  final String PROVIDER_KEY = "value";

	private static final int PROVIDER_COLUMN_INDEX = 2;

	private Context context;

	public SettingProvider(Context context) {
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

	public ArrayList<String> getAllValues() {

		ArrayList<String> array = new ArrayList<String>();

		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.parse(PROVIDER_URI);
		Cursor c = cr.query(uri, null, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {

				do {
					StringBuilder builder =new StringBuilder();
					builder.append(c.getString(0));
					builder.append(" / ");
					builder.append(c.getString(1));
					builder.append(" / ");
					builder.append(c.getString(2));
					array.add(builder.toString());

				} while (c.moveToNext());

			}
			c.close();
		}

		return array;
	}

	public Boolean setValue(int id, String value) {
		if (value == null) {
			return false;
		}

		Uri uri = Uri.parse(PROVIDER_URI + "/" + Integer.toString(id));
		ContentValues cv = new ContentValues();
		cv.put(PROVIDER_KEY, value);
		int ret = 0;
		try {
			ret =context.getContentResolver().update(uri, cv, null, null);
		} catch (Exception e) {
			//
		}
		

		if (ret > 0) {
			return true;
		} else {
			return false;
		}

	}
}
