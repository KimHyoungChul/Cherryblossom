package com.commax.security;

import android.content.Context;

public class SettingValues {
	private static final int HOME_INDEX = 6;
	
	private	Context context;
	
	public SettingValues(Context context){
		this.context=context;
	}
	
	public String getHomeServerIp() {
		return	new SettingProvider(context).getValue(HOME_INDEX);
	}
}
