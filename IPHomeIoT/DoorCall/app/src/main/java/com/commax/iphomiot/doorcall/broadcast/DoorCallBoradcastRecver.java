package com.commax.iphomiot.doorcall.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.iphomiot.doorcall.service.SipService;
import com.commax.iphomiot.doorcall.view.OutgoingCallActivity;

public class DoorCallBoradcastRecver extends BroadcastReceiver {
    private void processBootCompleted(Context context) {
        context.startService(new Intent(context, SipService.class));
    }

    private void processOutgoingCall(Intent intent) {
        String callNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        String callName = intent.getExtras().getString("android.intent.extra.DISPLAY_NAME");
        DoorCallApplication.getInstance().createOutgoingCallActivity(callNumber, callName);
    }

    private void processOpenDoor(Intent intent) {
        String callMode = intent.getExtras().getString("from");
        String doorType = intent.getExtras().getString("type");
        String ip = intent.getExtras().getString("ip");
        if (!callMode.equals("ucagent"))
            return;

        if (doorType.equals("door"))
            DoorCallApplication.getInstance().openDoor(ip, 80, "admin", "123456");
        else if (doorType.equals("lobby"))
            DoorCallApplication.getInstance().openLobby(ip);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        if (actionName.equals("android.intent.action.BOOT_COMPLETED"))
            processBootCompleted(context);
        else if (actionName.equals("com.commax.iphomeiot.calldial.outgoingcall"))
            processOutgoingCall(intent);
        else if (actionName.equals("com.commax.app.DOOR_MONITOR"))
            processOpenDoor(intent);
    }
}
