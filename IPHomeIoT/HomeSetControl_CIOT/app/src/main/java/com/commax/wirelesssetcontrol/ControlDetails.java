package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.device.SubSort;

public class ControlDetails extends LinearLayout implements View.OnTouchListener{

    static final String TAG = "ControlDetails";
    Context mContext;
    Handler mHandler;
    FrameLayout lay_setbg;
    LinearLayout lay_right;
    LinearLayout lay_left;
    LinearLayout lay_top;
    LinearLayout lay_bottom;

    int width = 236;
    int height = 136;
    int margin = 0;
    private DeviceInfo deviceInfo;
    View detail_view;

    public ControlDetails(Context context, DeviceInfo deviceInfo, Handler handler) {
        super(context);
        mContext=context;
        mHandler=handler;
        init(deviceInfo);
    }

    public ControlDetails(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init(DeviceInfo deviceInfo){

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.control_details, this);
        lay_setbg = (FrameLayout) rootView.findViewById(R.id.lay_setbg);
        lay_right = (LinearLayout) rootView.findViewById(R.id.lay_right);
        lay_left = (LinearLayout) rootView.findViewById(R.id.lay_left);
        lay_top = (LinearLayout) rootView.findViewById(R.id.lay_top);
        lay_bottom = (LinearLayout) rootView.findViewById(R.id.lay_bottom);
        lay_setbg.setOnTouchListener(this);

        try {
            this.deviceInfo = deviceInfo;
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.MODE_BINARY)) {
                addAutoManual(deviceInfo);
            } else if ((deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.THERMOSTAT_RUNMODE))
                    || (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.THERMOSTAT_SETPOINT))) {
                addTemperature(deviceInfo);
            } else if (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.SWITCH_DIMMER)) {
                addDimming(deviceInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            width = (int) getResources().getDimension(R.dimen.control_detail_width);
            height = (int) getResources().getDimension(R.dimen.control_detail_height);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateDeviceInfo(DeviceInfo deviceInfo){

        try {
            this.deviceInfo = deviceInfo;
            updateStatus(deviceInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getRootUuid(){
        return deviceInfo.rootUuid;
    }

    public String getSort(){
        return deviceInfo.subDevices.get(0).sort;
    }

    public void updateStatus(DeviceInfo deviceInfo){
        try {
            if (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.MODE_BINARY)) {
                DetailAutoManual detailAutoManual = (DetailAutoManual) detail_view;
                detailAutoManual.updateDevice(deviceInfo);
            } else if (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.SWITCH_DIMMER)) {
                DetailDimming detailDimming = (DetailDimming) detail_view;
                detailDimming.updateDevice(deviceInfo);
            } else if ((deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.THERMOSTAT_RUNMODE))
                    || (deviceInfo.subDevices.get(0).sort.equalsIgnoreCase(SubSort.THERMOSTAT_SETPOINT))) {
                DetailTemperature detailTemperature = (DetailTemperature) detail_view;
                detailTemperature.updateDevice(deviceInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addAutoManual(DeviceInfo deviceInfo){
        try {
            DetailAutoManual detailAutoManual = new DetailAutoManual(mContext, deviceInfo, mHandler);
            detail_view = detailAutoManual;
            lay_setbg.addView(detailAutoManual);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addTemperature(DeviceInfo deviceInfo){
        try {
            DetailTemperature detailTemperature = new DetailTemperature(mContext, deviceInfo, mHandler);
            detail_view = detailTemperature;
            lay_setbg.addView(detailTemperature);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addDimming(DeviceInfo deviceInfo){
        try {
            DetailDimming detailDimming = new DetailDimming(mContext, deviceInfo, mHandler);
            detail_view = detailDimming;
            lay_setbg.addView(detailDimming);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG,"Action was DOWN");
                break;

            case MotionEvent.ACTION_OUTSIDE:
//                Log.d(TAG, "outside");
                break;

        }
        return true;
    }

    public void setLocation(float x, float y, float offsetX, float offsetY){

        try {
            float set_x = x + (offsetX);
            float set_y = y + (offsetX);

            int right_margin = 0;
            int left_margin = 0;

            if (MainActivity.projectOptions.str_support_info.equalsIgnoreCase("true")){
                right_margin = (int)getResources().getDimension(R.dimen.info_width);
            }

            if (MainActivity.projectOptions.str_support_quick.equalsIgnoreCase("true")){
                left_margin = 30;
            }

            int right_limit = (int) getResources().getDimension(R.dimen.control_detail_right_limit) + left_margin;
            int left_limit = (int) getResources().getDimension(R.dimen.control_detail_left_limit) - right_margin;
            int center_limit = (int) getResources().getDimension(R.dimen.control_detail_center_limit);

            Log.d(TAG, "setLocation = x " + set_x + " / y " + set_y + " / offsetX " + offsetX + " / offsetY " + offsetY);

            if (set_x <= right_limit) {
                Log.d(TAG, "case right");
                setX(set_x + offsetX);
                setY(set_y - ((height / 2) - margin));
                lay_left.setVisibility(VISIBLE);
            } else if (set_x >= left_limit) {
                Log.d(TAG, "case left");
                setX(x - width);
                setY(set_y - ((height / 2) - margin));
                lay_right.setVisibility(VISIBLE);
            } else {
                if (set_y >= center_limit) {
                    Log.d(TAG, "case top_center");
                    setX(set_x - (width / 2));
                    setY(y - height);
                    lay_bottom.setVisibility(VISIBLE);
                } else {
                    Log.d(TAG, "case bottom");
                    setX(set_x - (width / 2));
                    setY(y + offsetY + offsetY);
                    lay_top.setVisibility(VISIBLE);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
