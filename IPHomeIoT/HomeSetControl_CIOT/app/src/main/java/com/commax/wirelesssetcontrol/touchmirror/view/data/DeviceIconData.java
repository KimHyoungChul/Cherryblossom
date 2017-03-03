package com.commax.wirelesssetcontrol.touchmirror.view.data;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.MainActivity;
import com.commax.wirelesssetcontrol.NameSpace;

/**
 * Created by OWNER on 2017-02-14.
 */
public class DeviceIconData extends IconData {

    final int control_time_out=10;

    String rootUuid="";
    String deviceType=""; // rootDevice
    String nickName="";
    String controlType="";
    String main_subUuid="";
    String status="";
    boolean controlling=false;

    Handler mHandler;

    public DeviceIconData(int type, Drawable drawable, String name, int x, int y, int width, int height,
                          String rootUuid, String deviceType, String nickName, String controlType, String main_subUuid, String status) {
        super(type, drawable, name, x, y, width, height);
        this.controlType = controlType;
        this.deviceType = deviceType;
        this.main_subUuid = main_subUuid;
        this.nickName = nickName;
        this.rootUuid = rootUuid;
        this.status = status;
    }

    public void setControlling(boolean controlling, Handler handler, int crt_page){
        this.controlling=controlling;
        if (controlling) {
            if (handler!=null) {
                mHandler = handler;
            }
            checkResult(crt_page);
        }
    }

    public void setStatus(String status){
        this.status=status;
        setControlling(false, null, -1);
    }

    public void setNickName(String name){
        nickName=name;
    }

    public String getRootUuid(){
        return rootUuid;
    }

    public String getDeviceType(){
        return deviceType;
    }

    public String getNickName(){
        return nickName;
    }

    public String getControlType(){
        return controlType;
    }

    public String getMain_subUuid(){
        return main_subUuid;
    }

    public String getStatus(){
        return status;
    }

    public boolean isControlling(){
        return controlling;
    }

    private void checkResult(final int crt_page){

        try {
            makeToastOnWorking();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < (control_time_out*10); i++) {
                        if ((crt_page!=MainActivity.CRT_PAGE)){
                            setControlling(false, null, -1);
                            return;
                        }

                        if (!isControlling()) {
                            return;
                        }
//                        if (!MainActivity.getInstance().activity_on){
//                            android.util.Log.d(TAG, "activity is not foreground");
//                            return;
//                        }
//                        if (view_removed){
//                            Log.d(TAG, "view is removed");
//                            return;
//                        }
                        if ((i % 10) == 0) {
                            Log.d("DeviceIconData", "Try " + i / 10);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        if (deviceInfo.room != MainActivity.CRT_PAGE) {
//                            return;
//                        }
                    }


                    if (isControlling()) {
//                        if (!view_removed) {

                        try {
                            makeToastControlFailed();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                            Log.d("DeviceIconData", "status : unknown");

//                        }
                    }
                    setControlling(false, null, -1);
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void makeToastNeedSync(){
        if (mHandler!=null) {
            Message syncToast = mHandler.obtainMessage();
            syncToast.what = HandlerEvent.EVENT_HANDLE_TOAST_NEED_SYNC;
            mHandler.sendMessage(syncToast);
        }
    }

    private void makeToastOnlyLocking(){
        if (mHandler!=null) {
            Message syncToast = mHandler.obtainMessage();
            syncToast.what = HandlerEvent.EVENT_HANDLE_TOAST_ONLY_LOCK;
            mHandler.sendMessage(syncToast);
        }
    }

    private void makeToastOnWorking(){
        if (mHandler!=null) {
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_TOAST_ON_WORKING;
            mHandler.sendMessage(workingToast);
        }
    }

    private void makeToastControlFailed() {
        if (mHandler != null) {
            Message failToast = mHandler.obtainMessage();
            failToast.what = HandlerEvent.EVENT_HANDLE_TOAST_CONTROL_FAILED;
            mHandler.sendMessage(failToast);
        }
    }
}
