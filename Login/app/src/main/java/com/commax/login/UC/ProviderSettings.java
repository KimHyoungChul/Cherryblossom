package com.commax.login.UC;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

public class ProviderSettings {

    String TAG = ProviderSettings.class.getSimpleName();
    private final String PROVIDER_URI = "content://com.commax.provider.settings/setting";
    private final String PROVIDER_KEY = "value";

    private final int PROVIDER_COLUMN_INDEX = 2;

    private Context context;

    public ProviderSettings(Context context) {
        this.context = context;
    }

    public String getValue(int id) {
        String str = "";
        Cursor c = null;
        try {
            Uri uri = Uri.parse(PROVIDER_URI + "/" + Integer.toString(id));
            c = context.getContentResolver().query(uri, null, null, null, null);

            if (c.moveToFirst()) {
                str = c.getString(PROVIDER_COLUMN_INDEX);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return str;
    }

    public ArrayList<String> getAllValues() {

        ArrayList<String> array = new ArrayList<String>();
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.parse(PROVIDER_URI);
            Cursor c = cr.query(uri, null, null, null, null);

            if (c.moveToFirst()) {

                do {
                    String value = c.getString(0) + " / " + c.getString(1)
                            + " / " + c.getString(2);
                    array.add(value);

                } while (c.moveToNext());

            }
            c.close();

        } catch (Exception e) {

        }
        return array;
    }

    public Boolean setValue(int id, String value) {

        try {
            Uri uri = Uri.parse(PROVIDER_URI + "/" + Integer.toString(id));
            ContentValues cv = new ContentValues();
            cv.put(PROVIDER_KEY, value);
            context.getContentResolver().update(uri, cv, null, null);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
