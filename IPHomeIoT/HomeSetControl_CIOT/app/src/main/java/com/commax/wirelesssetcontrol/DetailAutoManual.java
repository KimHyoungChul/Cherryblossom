package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.device.SubSort;

public class DetailAutoManual extends LinearLayout implements View.OnClickListener {

    static final String TAG = "DetailAutoManual";
    ImageView btn_auto_manual;
    TextView tv_auto_manual;
    DeviceInfo deviceInfo;
    CommandTools commandTools;
    Context mContext;
    Handler mHandler;

    boolean set_command_ok=false;
    boolean view_removed=false;
    boolean doing_control=false;
    final int control_time_out=10;

    public DetailAutoManual(Context context, DeviceInfo deviceInfo, Handler handler) {
        super(context);
        this.deviceInfo = deviceInfo;
        mHandler=handler;
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.detail_auto_manual, this);

        mContext=context;

        LinearLayout bt_more = (LinearLayout) rootView.findViewById(R.id.bt_more);
        tv_auto_manual = (TextView) rootView.findViewById(R.id.tv_auto_manual);
        btn_auto_manual = (ImageView) rootView.findViewById(R.id.btn_auto_manual);
        commandTools = new CommandTools();

        btn_auto_manual.setOnClickListener(this);
        bt_more.setOnClickListener(this);

        setView();

        try {
            if (deviceInfo.subCount > 2) {
                bt_more.setVisibility(VISIBLE);
            } else if (deviceInfo.subCount <= 2) {
                bt_more.setVisibility(GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_auto_manual:
                setCommand();
                break;

            case R.id.bt_more:
                showMore();
                break;
        }

    }

    private void setView(){

        try {
            boolean unknown = false;

            if (deviceInfo.subDevices.get(0).value.equalsIgnoreCase(Status.MANUAL)) {
                btn_auto_manual.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_detail_manual, null));
                tv_auto_manual.setText(getResources().getString(R.string.manual));
                tv_auto_manual.setTextColor(0x806a71cc);
            } else if (deviceInfo.subDevices.get(0).value.equalsIgnoreCase(Status.AUTO)) {
                btn_auto_manual.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_detail_auto, null));
                tv_auto_manual.setText(getResources().getString(R.string.auto));
                tv_auto_manual.setTextColor(0xffffffff);
            } else {
                btn_auto_manual.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_detail_disable, null));
                tv_auto_manual.setText(getResources().getString(R.string.unknown));
                tv_auto_manual.setTextColor(0x266a71cc);
                btn_auto_manual.setClickable(false);
                unknown = true;
            }

            Log.d(TAG, "setView deviceInfo.mainDevice.value"+deviceInfo.mainDevice.value);

            if (!unknown) {
                if (deviceInfo.mainDevice.value.equalsIgnoreCase(Status.OFF)) {
                    btn_auto_manual.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.btn_detail_disable, null));
                    tv_auto_manual.setTextColor(0x266a71cc);
                    btn_auto_manual.setClickable(false);
                } else if (deviceInfo.mainDevice.value.equalsIgnoreCase(Status.ON)) {
                    btn_auto_manual.setClickable(true);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateDevice(DeviceInfo deviceInfo){

        set_command_ok=true;
        doing_control=false;

        try {
            this.deviceInfo = deviceInfo;
        }catch (Exception e){
            e.printStackTrace();
        }

        setView();

    }

    private void showMore(){

        try {
            if (deviceInfo.subCount>2) {
                Intent intent = new Intent();
                intent.putExtra(NameSpace.MORE_COMMAX_DEVICE, deviceInfo.deviceType);
                intent.putExtra(NameSpace.ROOT_UUID, deviceInfo.rootUuid);
                intent.setClassName("com.commax.controlsub", "com.commax.controlsub.MainActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setCommand(){

        String settingValue="";

        try {
            if (deviceInfo.subDevices.get(0).value.equalsIgnoreCase(Status.MANUAL)) {
                settingValue = Status.AUTO;
            } else {
                settingValue = Status.MANUAL;
            }

            setStatus(settingValue);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    protected void setStatus(String menu_auto){

        set_command_ok=false;
        view_removed=false;

        if(!doing_control) {
            Log.d(TAG, " setting to : " + menu_auto);
            doing_control=true;
            try {

                setStatusAddSort(deviceInfo.rootUuid, RootDevice.SWITCH, deviceInfo.subDevices.get(0).subUuid, menu_auto, SubSort.MODE_BINARY);
                checkResult();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStatusAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort){

        try {
            if (rootDevice.equalsIgnoreCase(RootDevice.LOCK)) {
//                Toast.makeText(mContext, mContext.getString(R.string.only_locking), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "setStatusAddSort can only locking");
            } else {
                Log.d(TAG, "set : rootUuid = " + rootUuid + " / rootDevice : " + rootDevice + " / subUuid : " + subUuid + " / value : " + value);
                if ((rootUuid != null) && (rootDevice != null) && (subUuid != null) && (value != null)) {
                    if ((!TextUtils.isEmpty(rootUuid)) && (!TextUtils.isEmpty(rootDevice)) && (!TextUtils.isEmpty(subUuid)) && (!TextUtils.isEmpty(value))) {
                        commandTools.sendCommand(commandTools.JsonStringAddSort(rootUuid, rootDevice, subUuid, value, sort));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkResult(){

        try {
//            doing_control=true;
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_TOAST_ON_WORKING;
            mHandler.sendMessage(workingToast);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < (control_time_out*10); i++) {
                        if (set_command_ok) {
                            doing_control=false;
                            return;
                        }
                        if (view_removed){
                            android.util.Log.d(TAG, "view is removed");
                            return;
                        }
                        if ((i % 10) == 0) {
                            android.util.Log.d(TAG, "Try " + i / 10);
                        }
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (deviceInfo.room != MainActivity.CRT_PAGE) {
                            return;
                        }
                    }


                    if (!set_command_ok) {
                        if (!view_removed) {
                            Message failToast = mHandler.obtainMessage();
                            failToast.what = HandlerEvent.EVENT_HANDLE_TOAST_CONTROL_FAILED;
                            failToast.obj = this;
                            mHandler.sendMessage(failToast);

                            Log.d(TAG, "status : unknown");

                        }
                    }
                    doing_control=false;
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
