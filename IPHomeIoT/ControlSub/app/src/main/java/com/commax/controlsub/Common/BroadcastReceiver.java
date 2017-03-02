package com.commax.controlsub.Common;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.commax.controlsub.MainActivity;

import org.json.JSONObject;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    static final String PAM_ACTION = "com.commax.services.AdapterService.PAM_ACTION";   /* PAM을 통해 report 받음(add, remove, update) */
    static final String NIKNAME_ACTION = "com.commax.gateway.service.HomeIoT.NickName_ACTION";
    static final String MY_ACTION = "com.commax.controlsub.NickName_ACTION";
    static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Log.d(TAG, "action = " +action );

            if (action.equals(PAM_ACTION)) { /* PAM을 통해 report 받음(add, remove, update) */

                String strVal = intent.getStringExtra("eventString");
                Log.d(TAG, "PAM_ACTION : " + strVal);

                String event = getEventType(strVal); //add, remove, update report type 분별

                if (!TextUtils.isEmpty(event)) {
                    switch (event) {
                        case "report": //update report 일 경우 Device 정보 갱신
                            //TODO 여기에서 settingpoint 인지를 파악하는 json parsing 해서 setting point 이면 flag 하나를 true로 한다
                            // 해당 값이 true 이고 setpogress 에서 들어온 값과 현재 표시되어 있는 값이 다르면 2초 정도 더 기다린 다음 다시 setprogress(deviceinfo.getValue4,5) 를 호출 해서 다시 값 setting
                            if(MainActivity.getInstance() != null)
                            {
                                MainActivity.getInstance().updateReport(strVal);
                            }
                            break;

                        case "removeReport":
                            Log.d(TAG, "removeReport");
                            //삭제 에 관한 이벤트 들어오면 해당 rootUuid 에 관해서 ArrayList에서 해당 list를 빼줘야한다.
                            if(MainActivity.getInstance() != null)
                            {
                               /* ControlNameEdit controlNameEdit = new ControlNameEdit(context);
                                controlNameEdit.remove_report(strVal);*/
                                MainActivity.getInstance().removeReport(strVal);
                            }
                            break;

                        case "addReport":
                            Log.d(TAG, "addReport");
                            if(MainActivity.getInstance() != null)
                            {
                                MainActivity.getInstance().add_updateReport(strVal);
                            }
                            break;
                    }
                }
            }
            else if (action.equals(NIKNAME_ACTION)) { /* PAM을 통해 report 받음(add, remove, update) */

                String rootUuid = intent.getStringExtra("rootUuidStr");
                String nickname = intent.getStringExtra("nickNameStr");

                Log.d(TAG, "PAM_ACTION rootuuid : " + rootUuid + " , nick name : " + nickname);

                if(!TextUtils.isEmpty(rootUuid))
                {
                    Log.d(TAG , "nickname edit response");
//                    MainActivity.getInstance().nicknameUpdate_report(rootUuid , nickname);
                }

                /*String event = getEventType(strVal); //add, remove, update report type 분별
                //TODO 임시로 null처리 0922

                if (!TextUtils.isEmpty(event)) {
                    switch (event) {
                        case "report": //update report 일 경우 Device 정보 갱신
                            MainActivity.getInstance().nicknameUpdate_report();
                            break;
                    }
                }*/
            }
            else if(action.equals(MY_ACTION))
            {
                String rootUuid = intent.getStringExtra("rootUuidStr");
                String nickname = intent.getStringExtra("nickNameStr");

                Log.d(TAG, "PAM_ACTION rootuuid : " + rootUuid + " , nick name : " + nickname);
            }
        } catch (Exception e) {
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
                    command = jObject.getString("command");
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if(!TextUtils.isEmpty(command)) {
            switch (command) {
                case "addReport":
                    return command;
                case "removeReport":
                    return command;
                case "report":
                    return command;
                default:
                    return "no";
            }
        }
        return "no";
    }


}
