package com.commax.securityservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ServiceStarter extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			ComponentName comName = new ComponentName(context.getPackageName(), SecurityService.class.getName());
			context.startService(new Intent().setComponent(comName));
		}
	}
}
