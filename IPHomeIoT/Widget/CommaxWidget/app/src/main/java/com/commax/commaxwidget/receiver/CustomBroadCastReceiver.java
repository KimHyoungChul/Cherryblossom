package com.commax.commaxwidget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.commax.commaxwidget.util.Constants;

/**
 * Created by OWNER on 2017-02-13.
 */
public class CustomBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
         String action = intent.getAction();
         if(action.equals(Constants.ACTION_LOTATION_WIDGET)){
             Intent sendIntent = new Intent(Constants.ACTION_LOTATION_WIDGET_SERVICE);
             context.sendBroadcast(sendIntent);
        }
    }
}
