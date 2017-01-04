package com.commax.login.Common;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    static final String MY_ACTION = "com.commax.login.Create_account_ACTION";
    static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Log.d(TAG, "action = " + action);


            if (action.equals(MY_ACTION)) {
                String rootUuid = intent.getStringExtra("create_account");

                Log.d(TAG, "Create_account_ACTION create_account : " + rootUuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
