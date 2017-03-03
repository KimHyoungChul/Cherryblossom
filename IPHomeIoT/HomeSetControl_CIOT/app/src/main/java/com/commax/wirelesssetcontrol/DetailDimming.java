package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.device.SubSort;

public class DetailDimming extends LinearLayout implements View.OnClickListener {

    static final String TAG = "DetailDimming";
    DeviceInfo deviceInfo;
    CommandTools commandTools;
    Context mContext;
    Handler mHandler;

    TextView tv_temp;
    ImageButton bt_plus;
    ImageButton bt_minus;

    boolean set_command_ok = false;
    boolean view_removed = false;
    boolean doing_control = false;
    final int control_time_out = 10;
    int off_color = 0x80312f62;
    int on_color = 0xff312f62;

    String crt_dimming = "0";

    public DetailDimming(Context context, DeviceInfo deviceInfo, Handler handler) {
        super(context);
        this.deviceInfo = deviceInfo;
        mHandler = handler;
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.detail_dimming, this);

        LinearLayout bt_more = (LinearLayout) rootView.findViewById(R.id.bt_more);
        bt_plus = (ImageButton) rootView.findViewById(R.id.bt_plus);
        tv_temp = (TextView) rootView.findViewById(R.id.tv_temp);
        bt_minus = (ImageButton) rootView.findViewById(R.id.bt_minus);

        mContext = context;

        try {
            commandTools = new CommandTools();
        }catch (Exception e){
            e.printStackTrace();
        }

        bt_plus.setOnClickListener(this);
        bt_minus.setOnClickListener(this);
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

        switch (v.getId()) {
            case R.id.bt_minus:
                setMinus();
                break;

            case R.id.bt_plus:
                setPlus();
                break;

            case R.id.bt_more:
                showMore();
                break;
        }

    }

    private void setView() {

        try {
            String type = deviceInfo.subDevices.get(0).value;

            //check integer
            try {
                int value = Integer.parseInt(type);
            }catch (Exception e){
                e.printStackTrace();
                type = "";
            }

            tv_temp.setText(type);
            crt_dimming = type;

            if(deviceInfo.mainDevice.value.equalsIgnoreCase(Status.OFF)){
                bt_minus.setAlpha(0.5f);
                bt_plus.setAlpha(0.5f);
                bt_minus.setClickable(false);
                bt_plus.setClickable(false);
                tv_temp.setTextColor(off_color);
            }else if (deviceInfo.mainDevice.value.equalsIgnoreCase(Status.ON)){
                bt_minus.setAlpha(1.0f);
                bt_plus.setAlpha(1.0f);
                bt_minus.setClickable(true);
                bt_plus.setClickable(true);
                tv_temp.setTextColor(on_color);
            } else {
                bt_minus.setAlpha(0.5f);
                bt_plus.setAlpha(0.5f);
                bt_minus.setClickable(false);
                bt_plus.setClickable(false);
                tv_temp.setTextColor(off_color);
            }

            if (TextUtils.isEmpty(type)){

                bt_minus.setAlpha(0.5f);
                bt_plus.setAlpha(0.5f);
                bt_minus.setClickable(false);
                bt_plus.setClickable(false);
                tv_temp.setText("--");
                tv_temp.setTextColor(off_color);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateDevice(DeviceInfo deviceInfo) {

        set_command_ok = true;
        doing_control = false;

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

    private void setMinus() {
        try {
            int crt = Integer.valueOf(crt_dimming);
            int min = Integer.valueOf(deviceInfo.subDevices.get(0).option1);

            if ((crt - 1) >= min) {
                String set = String.valueOf("" + (crt - 1));
                setStatus(set);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setPlus() {
        try {
            int crt = Integer.valueOf(crt_dimming);
            int max = Integer.valueOf(deviceInfo.subDevices.get(0).option2);

            if ((crt + 1) <= max) {
                String set = String.valueOf("" + (crt + 1));
                setStatus(set);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void setStatus(String temp) {

        set_command_ok = false;
        view_removed = false;

        if (!doing_control) {
            doing_control = true;
            try {
                Log.d(TAG, " setting to : " + temp);

                setStatusAddSort(deviceInfo.rootUuid, RootDevice.SWITCH, deviceInfo.subDevices.get(0).subUuid, temp, SubSort.SWITCH_DIMMER);
                checkResult();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStatusAddSort(String rootUuid, String rootDevice, String subUuid, String value, String sort) {

        try {
            if (rootDevice.equalsIgnoreCase(RootDevice.LOCK)) {
//                Toast.makeText(mContext, mContext.getString(R.string.only_locking), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "set : rootUuid = " + rootUuid + " / rootDevice : " + rootDevice + " / subUuid : " + subUuid + " / value : " + value);
                if ((rootUuid != null) && (rootDevice != null) && (subUuid != null) && (value != null)) {
                    if ((!TextUtils.isEmpty(rootUuid)) && (!TextUtils.isEmpty(rootDevice)) && (!TextUtils.isEmpty(subUuid)) && (!TextUtils.isEmpty(value))) {
                        commandTools.sendCommand(commandTools.JsonStringAddSort(rootUuid, rootDevice, subUuid, value, sort));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeWorkingToast(){
        try {
            Message workingToast = mHandler.obtainMessage();
            workingToast.what = HandlerEvent.EVENT_HANDLE_TOAST_ON_WORKING;
            mHandler.sendMessage(workingToast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkResult() {

        makeWorkingToast();

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = 0; i < (control_time_out * 10); i++) {
                        if (set_command_ok) {
                            doing_control = false;
                            return;
                        }
                        if (view_removed) {
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
                    doing_control = false;
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
