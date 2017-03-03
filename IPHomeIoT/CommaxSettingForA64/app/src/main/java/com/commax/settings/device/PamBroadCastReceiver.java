package com.commax.settings.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

public class PamBroadCastReceiver extends BroadcastReceiver {

	private final String LOG_TAG = this.getClass().getSimpleName();
	public static final String		COMMAND_RESPONSE_REPORT				= "report";
	public static final String		COMMAND_RESPONSE_ADD_REPORT			= "addReport";
	public static final String		COMMAND_RESPONSE_REMOVE_REPORT		= "removeReport";
	public static final String		COMMAND_RESPONSE_FACTORY_REPORT	= "factoryReport";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		//월패드의 디바이스 정보가 초기화
		if(intent.getAction().equals("com.commax.services.AdapterService.PAM_ACTION")) {
			context.sendBroadcast(new Intent("DEVICE_INIT_BROADCAST_RECEIVE"));

			String strVal = intent.getStringExtra("eventString");
			String event = getEventType(strVal); //add, remove, update report type 분별
			if(event.equalsIgnoreCase(COMMAND_RESPONSE_FACTORY_REPORT)) {
				Log.i(LOG_TAG, "디바이스 정보 초기화: " + strVal);
				Log.d(LOG_TAG, "getEventType:" + event);
			}

		//클라우드에 저장된 디바이스 정보, 씬, 시나리오 정보가 초기화
		} else if(intent.getAction().equals("com.commax.gateway.service.sendToServer.Factory_ACTION")) {
			context.sendBroadcast(new Intent("CLOUD_INIT_BROADCAST_RECEIVE"));

			String strVal = intent.getStringExtra("response");
			Log.i(LOG_TAG, "클라우드 정보 초기화: " + strVal);


		}
		//Toast.makeText(context.getApplicationContext(), R.string.device_reset_success , Toast.LENGTH_SHORT).show();
	}

	private String getEventType(String str){            /* event classification (add, remove, update) */

		String raw = str;
		JSONObject jObject=null;
		String command = "no";

		try {
			if (!TextUtils.isEmpty(raw)) {
				jObject = new JSONObject(raw.toString());

				if (jObject!=null) {
					command = jObject.getString("command");
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		if(!TextUtils.isEmpty(command)) {
			switch (command) {
				case COMMAND_RESPONSE_ADD_REPORT:
					return command;
				case COMMAND_RESPONSE_REMOVE_REPORT:
					return command;
				case COMMAND_RESPONSE_REPORT:
					return command;
				case COMMAND_RESPONSE_FACTORY_REPORT:
					return command;
				default:
					return "no";
			}
		}
		return "no";
	}

}
