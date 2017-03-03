package com.commax.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.EditText;

public class PreferenceTool {
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEditor;

	public PreferenceTool(Context mContext) {
		mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		mEditor = mPref.edit();
	}

	public PreferenceTool(Context mContext, String prefName) {
		mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
		mEditor = mPref.edit();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return mPref.getBoolean(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return mPref.getInt(key, defValue);
	}

	public String getString(String key, String defValue) {
		return mPref.getString(key, defValue);
	}

	public void putBoolean(final String key, final boolean value) {
		new AsyncTask<Void, Void, Void>() {
        	@Override
        	protected Void doInBackground(Void... params) {       
        		mEditor.putBoolean(key, value).commit();        		
        		return null;
        	}        	
		}.execute();    		
	}

	public void putInt(final String key, final int value) {
		new AsyncTask<Void, Void, Void>() {
        	@Override
        	protected Void doInBackground(Void... params) {       
        		mEditor.putInt(key, value).commit();        		
        		return null;
        	}        	
		}.execute();		
	}

	public void putString(final String key, final String value) {
		new AsyncTask<Void, Void, Void>() {
        	@Override
        	protected Void doInBackground(Void... params) {       
        		mEditor.putString(key, value).commit();        		
        		return null;
        	}        	
		}.execute();		
	}

	public void clear() {
		mEditor.clear().commit();
	}

	public void remove(String key) {
		mEditor.remove(key).commit();
	}
}
