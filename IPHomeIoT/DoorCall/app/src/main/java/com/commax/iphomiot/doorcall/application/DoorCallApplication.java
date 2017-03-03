package com.commax.iphomiot.doorcall.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.commax.cmxua.Call;
import com.commax.iphomiot.doorcall.R;
import com.commax.iphomiot.doorcall.service.SipService;
import com.commax.iphomiot.doorcall.view.BaseVideoActivity;
import com.commax.iphomiot.doorcall.view.MonitoringActivity;
import com.commax.iphomiot.doorcall.view.OutgoingCallActivity;
import com.commax.iphomiot.doorcall.view.PreviewActivity;
import com.commax.iphomiot.doorcall.view.VideoCallActivity;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.content_provider.ContentProviderManagerEx;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class DoorCallApplication extends Application implements DoorOnvifReqThread.DoorOnvifReqThreadAction {
    private static DoorCallApplication instance_ = null;
    private BaseVideoActivity curVideoActivity_ = null;
    private SipService.ServiceHandler serviceHandler_ = null;
    private DoorOnvifReqThread onvifReqThread_ = null;
    private Handler callLimitHandler_ = null;
    private Runnable callLimitRunnable_ = null;
    private Call connectedCall_ = null;
    private Call outgoingCall_ = null;

    private void createCrashlytics() {
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier("lovechamisl");
        Crashlytics.setUserEmail("lovechamisl@commax.co.kr");
        Crashlytics.setUserName("lovechamisl");
    }

    private void createCallTimeLimit(Call incomingCall) {
        String continuosCallTime = ContentProviderManager.getCallTime(this);
        int limitCallTime;
        if (continuosCallTime == null)
            limitCallTime = 180 * 1000;
        else
            limitCallTime = Integer.parseInt(continuosCallTime) * 1000;

        connectedCall_ = incomingCall;
        callLimitRunnable_ = new Runnable() {
            @Override
            public void run() {
                if (connectedCall_ != null)
                	disconnectCall(connectedCall_);
            }
        };

        callLimitHandler_ = new Handler();
        callLimitHandler_.postDelayed(callLimitRunnable_, limitCallTime);
    }

    public void destroyCallTimeLimit(Call incomingCall) {
    	if (callLimitHandler_ == null)
    		return;
        if (connectedCall_ != null && connectedCall_.equals(incomingCall)) {
            callLimitHandler_.removeCallbacks(callLimitRunnable_);
            callLimitHandler_ = null;
            connectedCall_ = null;
        }
    }

    private void finishCurrentVideoActivity() {
        if (curVideoActivity_ == null)
            return;

        curVideoActivity_.finish();
        curVideoActivity_ = null;
    }

    public static DoorCallApplication getInstance() {
        return instance_;
    }

    public void setRunningVideoActivity(BaseVideoActivity videoActivity) {
        curVideoActivity_ = videoActivity;
    }

    public void setServiceHandler(Handler serviceHandler) {
        serviceHandler_ = (SipService.ServiceHandler)serviceHandler;
    }

    public void createMonitoringActivity(String ip) {
        finishCurrentVideoActivity();

        Intent intent = new Intent(this, MonitoringActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(MonitoringActivity.INTENT_KEY_IP, ip);
        intent.putExtra(MonitoringActivity.INTENT_KEY_AUTO_DESTROY_WHEN_SCREENOFF, true);
        startActivity(intent);
    }

    public void createPreviewActivity(String ip, Activity activityForResult, int reqCode) {
        finishCurrentVideoActivity();

        Intent intent = new Intent(activityForResult, PreviewActivity.class);
        intent.putExtra(MonitoringActivity.INTENT_KEY_IP, ip);
        intent.putExtra(MonitoringActivity.INTENT_KEY_AUTO_DESTROY_WHEN_SCREENOFF, true);
        activityForResult.startActivityForResult(intent, reqCode);
    }

    public void createVideoCallActivity(Call incomingCall) {
        if (curVideoActivity_ != null) {
            if (!curVideoActivity_.getClass().equals(VideoCallActivity.class))
                finishCurrentVideoActivity();
            else {
                VideoCallActivity videoCallActivity = (VideoCallActivity)curVideoActivity_;
                if (videoCallActivity.isOffering())
                    disconnectCall(incomingCall);
                else
                    videoCallActivity.createSubDisplay(incomingCall);
                return;
            }
        }

        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(VideoCallActivity.INTENT_KEY_CALLOBJ, incomingCall);
        intent.putExtra(VideoCallActivity.INTENT_KEY_ENABLE_WAITINGCALL, true);
        startActivity(intent);
    }

    public void goToConfigurationActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.commax.settings","com.commax.settings.MainActivity");
        intent.putExtra("from","doorphoneSetting");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void ringingCall(Call outgoingCall) {
        if (outgoingCall == null)
            Log.e("tag","ringing call is null");

        outgoingCall_ = outgoingCall;

        OutgoingCallActivity outgoingCallActivity = (OutgoingCallActivity) curVideoActivity_;
        outgoingCallActivity.ringingCall(outgoingCall_);
    }

    public void createCall(String callNumber, String callName, String localNumber) {
        String[] callInfo = new String[3];
        callInfo[0] = callNumber;
        callInfo[1] = callName;
        callInfo[2] = localNumber;

        serviceHandler_.handleMessage(Message.obtain(null, SipService.ServiceHandler.MSG_OUTGOING_CALL, callInfo));
    }

    public void disconnectCall(Call incomingCall) {
        serviceHandler_.sendMessage(Message.obtain(null, SipService.ServiceHandler.MSG_DISCONNECT_CALL, incomingCall));
    }

    public void acceptCall(Call incomingCall) {
        serviceHandler_.sendMessage(Message.obtain(null, SipService.ServiceHandler.MSG_ACCEPT_CALL, incomingCall));
    }

    public void rejectCall(Call incomingCall) {
        serviceHandler_.sendMessage(Message.obtain(null, SipService.ServiceHandler.MSG_REJECT_CALL, incomingCall));
    }

    public void openDoor(Call incomingCall, int port, String userId, String userPass) {
        if (incomingCall.getCallSender() == Call.CallSender.Door)
            onvifReqThread_.openDoorWithOnvif(incomingCall.m_strIP, port, userId, userPass);
        else if (incomingCall.getCallSender() == Call.CallSender.Lobby)
            onvifReqThread_.openDoorWithLobby(incomingCall.m_strIP);
    }

    public void openDoor(String ip, int port, String userId, String userPass) {
        onvifReqThread_.openDoorWithOnvif(ip, port, userId, userPass);
    }

    public void openLobby(String ip) {
        onvifReqThread_.openDoorWithLobby(ip);
    }

    public void connectedCall(Call incomingCall) {
         createCallTimeLimit(incomingCall);

        if (curVideoActivity_.getClass().equals(OutgoingCallActivity.class)) {
            OutgoingCallActivity outgoingCallActivity = (OutgoingCallActivity) curVideoActivity_;
            outgoingCallActivity.connectedCall(incomingCall);
        }
    }

    public void disconnectedCall(Call incomingCall) {
        if (curVideoActivity_ == null)
            return;

        destroyCallTimeLimit(incomingCall);
        if (curVideoActivity_.getClass().equals(VideoCallActivity.class)) {
            VideoCallActivity videoCallActivity = (VideoCallActivity)curVideoActivity_;
            if (videoCallActivity.isEqualCall(incomingCall))
                videoCallActivity.finish();
            else if (videoCallActivity.isIncludeWithSubcall(incomingCall))
                videoCallActivity.removeSubcall(incomingCall);
        }
        else if(curVideoActivity_.getClass().equals(OutgoingCallActivity.class)) {
            OutgoingCallActivity outgoingCallActivity = (OutgoingCallActivity)curVideoActivity_;
            if (outgoingCallActivity.isEqualCall(incomingCall))
                outgoingCallActivity.finish();
        }
        else {
        }
    }

    public boolean isRegisteredCall(Call incomingCall) {
        if (incomingCall.getCallSender() != Call.CallSender.Door)
            return true;

        return (ContentProviderManagerEx.isCustomDoorCameraIpExistOnContentProvider(this, incomingCall.m_strIP));
    }

    public void createVideoCallActivityWithSubCall(Call subCall) {
        finishCurrentVideoActivity();

        Intent intent = new Intent(this, VideoCallActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(VideoCallActivity.INTENT_KEY_CALLOBJ, subCall);
        intent.putExtra(VideoCallActivity.INTENT_KEY_ENABLE_WAITINGCALL, false);
        startActivity(intent);
    }

    public void createOutgoingCallActivity (String callNumber, String callName) {
        Intent outgoingcall = new Intent(this, OutgoingCallActivity.class);
        outgoingcall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        outgoingcall.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        outgoingcall.putExtra("callNumber", callNumber);
        outgoingcall.putExtra("callName", callName);
        startActivity(outgoingcall);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createCrashlytics();

        startService(new Intent(this, SipService.class));
        onvifReqThread_ = new DoorOnvifReqThread(this);
        onvifReqThread_.start();
        instance_ = this;
    }

    @Override
    public void onTerminate() {
        onvifReqThread_.terminate();
        super.onTerminate();
    }

    @Override
    public void onResultDoorOpen(boolean ret) {
        if (ret) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), getString(R.string.STR_DOOR_OPENED), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
