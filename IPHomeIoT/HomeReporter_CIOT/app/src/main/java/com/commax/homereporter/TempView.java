package com.commax.homereporter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class TempView extends LinearLayout {

    TextView tv_status;
    ImageView iv_status;
    TextView tv_temp;
    TextView tv_scale;
    Locale locale;

    Context mContext;

    public TempView(Context context, String temp, String unit) {
        super(context);
        mContext = context;
        init(context, temp, unit);
    }

    public void init(Context context, String temp, String unit){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.weather, this);

        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_scale = (TextView) rootView.findViewById(R.id.tv_scale);
        tv_temp = (TextView) rootView.findViewById(R.id.tv_temp);
        iv_status = (ImageView) rootView.findViewById(R.id.iv_status);
        locale = mContext.getResources().getConfiguration().locale;

        //TODO main(house) icon setting is needed
        try {
            tv_status.setText(getResources().getString(R.string.indoor_temp));
            iv_status.setImageResource(R.mipmap.ic_rp_temp);
        }catch (Exception e){
            e.printStackTrace();
        }
        setDegree(temp);
        setCF(unit);
    }

    public void updateTemp(String temp, String unit){
        setDegree(temp);
        setCF(unit);
    }

    private void setDegree(String value){
        try {
            tv_temp.setText("" + value);
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
}
