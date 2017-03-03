package com.commax.iphomiot.doorcall.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.commax.cmxua.Call;
import com.commax.cmxua.CallList;
import com.commax.cmxua.NdkWrap;
import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.nubbyj.network.NetworkUtil;
import com.commax.settings.common.Log;

public class SipService extends Service {
    private class SipHandler extends Handler {
        private int connectedCallHandle_ = -1;
        private AudioInThread audioInThread_ = null;
        private AudioOutThread audioOutThread_ = null;

        public void handleMessage(Message msg) {
            int step = msg.getData().getInt(NdkWrap.BUNDLE_KEY_STEP);
            int handle = msg.getData().getInt(NdkWrap.BUNDLE_KEY_HANDLE);

            String param = NdkWrap.nativeGetParam(handle);
            String[] params = param.split(",");

            Call call;
            call = callList_.findCallList(handle);
            if (call == null && handle != 0) {
                if (params.length < 12)
                    call = new Call(handle, "", "", "", "", "", "", "", "", "", "", "", "");
                else
                    call = new Call(handle, params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11]);
            }
            switch (step) {
                case NdkWrap.EVENT_OFFERING:
                    if (!DoorCallApplication.getInstance().isRegisteredCall(call)) {
                        sipNdkWrap_.nativeCallReject(call.m_nHandle);
                        break;
                    }
                    callList_.addCallList(call);
                    DoorCallApplication.getInstance().createVideoCallActivity(call);
                    break;
                case NdkWrap.EVENT_CONNECTED:
                    if (connectedCallHandle_ == -1) {
                        connectedCallHandle_ = call.m_nHandle;
                        try {
                            sipNdkWrap_.nativeAudioRtpStart(call.m_strIP, Integer.parseInt(call.m_strAudioRemotePort), Integer.parseInt(call.m_strAudioLocalPort));
                        }
                        catch (NumberFormatException e) {
                            call.print();
                            Log.e("DoorCall", "Invalid Call Information");
                            sipNdkWrap_.nativeCallClose(call.m_nHandle);
                            return;
                        }
                        audioInThread_ = new AudioInThread(sipNdkWrap_);
                        audioOutThread_ = new AudioOutThread(sipNdkWrap_);
                        audioInThread_.start();
                        audioOutThread_.start();
                        DoorCallApplication.getInstance().connectedCall(call);
                    }
                    break;
                case NdkWrap.EVENT_DISCONNECTED:
                    Log.d("DoorCall", String.format("EVENT_DISCONNECTED - %d", call.m_nHandle));
                    callList_.removeCallList(handle);
                    DoorCallApplication.getInstance().disconnectedCall(call);
                    if (connectedCallHandle_ == call.m_nHandle) {
                        sipNdkWrap_.nativeAudioRtpStop();
                        audioInThread_.close();
                        audioOutThread_.close();
                        connectedCallHandle_ = -1;
                    }
                    break;
                case NdkWrap.EVENT_REG_REGISTERING:
                    SystemClock.sleep(1000);
                    break;
                case NdkWrap.EVENT_REG_REGISTERED:
                    SystemClock.sleep(1000);
                    break;
                case NdkWrap.EVENT_RINGING:
           //         callList_.addCallList(call);
                    DoorCallApplication.getInstance().ringingCall(call);
                    break;
            }
        }
    }

    public class ServiceHandler extends Handler {
        public static final int MSG_DISCONNECT_CALL = 3001;
        public static final int MSG_ACCEPT_CALL = 3002;
        public static final int MSG_OUTGOING_CALL = 3003;
        public static final int MSG_REJECT_CALL = 3004;

        public void handleMessage(Message msg) {
            Call call;
            switch (msg.what) {
                case MSG_DISCONNECT_CALL:
                    call = (Call) msg.obj;
                    if (call != null) {
                        sipNdkWrap_.nativeCallClose(call.m_nHandle);
                        Log.d("DoorCall", String.format("nativeCallClose - %d", call.m_nHandle));
                    }
                    break;
                case MSG_ACCEPT_CALL:
                    call = (Call) msg.obj;
                    if (call != null)
                        sipNdkWrap_.nativeCallAccept(call.m_nHandle);
                    break;
                case MSG_OUTGOING_CALL :
                    String[] callInfo = (String[]) msg.obj;
                    if (callInfo != null) {
                        sipNdkWrap_.nativeCallCreate(callInfo[0],  callInfo[1], callInfo[2]); //callInfo[0]
                    }
                    break;
                case MSG_REJECT_CALL:
                    call = (Call)msg.obj;
                    if (call != null)
                        sipNdkWrap_.nativeCallReject(call.m_nHandle);
                    break;
            }
        }
    }

    private NdkWrap sipNdkWrap_ = null;
    private SipHandler sipHandler_ = new SipHandler();
    private CallList callList_ = new CallList();
    private ServiceHandler serviceHandler_ = new ServiceHandler();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sipNdkWrap_ = new NdkWrap(sipHandler_);
        sipNdkWrap_.nativeInitSIP(NetworkUtil.getLocalIpAddress(), "101", NetworkUtil.getLocalIpAddress());
        sipNdkWrap_.nativeInit();

        DoorCallApplication.getInstance().setServiceHandler(serviceHandler_);

        return START_STICKY;
    }
}
