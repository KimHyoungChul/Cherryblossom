package com.commax.control;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.commax.control.Common.TypeDef;

import org.json.JSONObject;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    static final String PAM_ACTION = "com.commax.services.AdapterService.PAM_ACTION";   /* PAM을 통해 report 받음(add, remove, update) */
    static final String NAME_ACTION1 = "com.commax.gateway.service.HomeIoT.NickName_ACTION";
    static final String NAME_ACTION2 = "com.commax.controlsub.NickName_ACTION";
    static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();

            if (action.equalsIgnoreCase(PAM_ACTION))
            { /* PAM을 통해 report 받음(add, remove, update) */

                String strVal = intent.getStringExtra("eventString");
                //Log.d(TAG, "PAM_ACTION : " + strVal);
                Log.v(TAG, "PAM_ACTION event... ");

                String event = getEventType(strVal); //add, remove, update report type 분별
                if (MainActivity.getInstance() != null) {
                    if (!TextUtils.isEmpty(event)) {
                        switch (event) {
                            case TypeDef.COMMAND_RESPONSE_REPORT: //update report 일 경우 Device 정보 갱신
                                MainActivity.getInstance().updateReport(strVal);
                                break;
                            case TypeDef.COMMAND_RESPONSE_ADD_REPORT: //add report 일 경우 Device 정보 Rescan
                                MainActivity.getInstance().addReport(strVal);

                                break;
                            case TypeDef.COMMAND_RESPONSE_REMOVE_REPORT: //remove report 일 경우 Device 정보 Rescan
                                MainActivity.getInstance().removeReport(strVal);
                                break;
                        }
                    }
                }
            }else if (action.equalsIgnoreCase(NAME_ACTION1))
            {

                Log.d(TAG, "NAME_ACTION1 event...");
                String rootUuid = intent.getStringExtra("rootUuidStr");
                //Log.d(TAG, "rootUuid : " + rootUuid);
                String nickName = intent.getStringExtra("nickNameStr");
                //Log.d(TAG, "nickName : " + nickName);
                MainActivity.getInstance().updateReportName(rootUuid, nickName);
            } else if (action.equalsIgnoreCase(NAME_ACTION2))
            {

                Log.d(TAG, "NAME_ACTION2 event...");
                String rootUuid = intent.getStringExtra("rootUuidStr");
                //Log.d(TAG, "rootUuid : " + rootUuid);
                String nickName = intent.getStringExtra("nickNameStr");
                //Log.d(TAG, "nickName : " + nickName);
                MainActivity.getInstance().updateReportName(rootUuid, nickName);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEventType(String str){            /* event classification (add, remove, update) */

        String raw = str;
        JSONObject jObject=null;
        String command = "no";

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject!=null) {
                    command = jObject.getString(TypeDef.CGP_KEY_COMMAND);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(command)) {
            switch (command) {
                case TypeDef.COMMAND_RESPONSE_ADD_REPORT:
                    return command;
                case TypeDef.COMMAND_RESPONSE_REMOVE_REPORT:
                    return command;
                case TypeDef.COMMAND_RESPONSE_REPORT:
                    return command;
                default:
                    return "no";
            }
        }
        return "no";
    }


}
