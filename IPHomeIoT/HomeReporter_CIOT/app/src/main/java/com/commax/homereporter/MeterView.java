package com.commax.homereporter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class MeterView extends LinearLayout {

    static final String TAG = "MeterView";

    TextView tv_status;
    ImageView iv_status;
    TextView tv_meter;
    TextView tv_scale;
    TextView tv_dot;
    Locale locale;
    ImageButton ib_detail_plug;

    Context mContext;

    public MeterView(Context context, String temp, String unit, String nickName) {
        super(context);
        mContext = context;
        init(context, temp, unit, nickName);
    }

    public void init(Context context, String temp, String unit, String nickName){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.plug, this);

        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_scale = (TextView) rootView.findViewById(R.id.tv_scale);
        tv_meter = (TextView) rootView.findViewById(R.id.tv_temp);
        tv_dot = (TextView) rootView.findViewById(R.id.tv_dot);
        iv_status = (ImageView) rootView.findViewById(R.id.iv_status);
        ib_detail_plug = (ImageButton) rootView.findViewById(R.id.ib_detail_plug);
        locale = mContext.getResources().getConfiguration().locale;

        tv_dot.setVisibility(GONE);
        //TODO main(house) icon setting is needed
        try {
            iv_status.setImageResource(R.mipmap.ic_rp_eletric);
            getDeviceNickName(nickName);
        }catch (Exception e){
            e.printStackTrace();
        }
        setDegree(temp);
        setCF(unit);

        ib_detail_plug.setOnClickListener(mClick);
    }

    OnClickListener mClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_detail_plug:
                    try {
                        Log.d(TAG, "ib_detail_plug");
                        showPlug();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void showPlug(){

        try{
            int calling_key = NameSpace.KEY_TAP_ENERGY;

            if (calling_key!=0){
                Log.d(TAG, "showControl calling_key : "+calling_key);
                Intent intent = new Intent();
                intent.putExtra(NameSpace.KEY_TAP_ID, calling_key);
                intent.setClassName("com.commax.control", "com.commax.control.MainActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateTemp(String temp, String unit){
        setDegree(temp);
        setCF(unit);
    }

    private void setDegree(String value){
        try {
            tv_meter.setText("" + value);
            Log.d("MeterView", "setDegree length " + value.length());
            if (value.length()>=6){
                tv_meter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.weather_temp_text_so_long));
            } else if ((value.length()>=5)&&(value.length()<6)){
                tv_meter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, getResources().getDimension(R.dimen.weather_temp_text_long));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCF(String unit){
        try {
            tv_scale.setText(unit);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getDeviceNickName(String name){
        try {
            tv_status.setText(name);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
