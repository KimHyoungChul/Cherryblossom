package com.commax.homereporter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TodayEnergyView extends LinearLayout {

    TextView tv_value;
    TextView tv_status;
    TextView tv_unit;
    LinearLayout lay_detail;

    Context mContext;

    public TodayEnergyView(Context context, String id, double today, double percent, String unit) {
        super(context);
        mContext = context;
        init(context, id, today, percent, unit);
    }

    public void init(Context context, String id, double today, double percent, String unit){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.today_energy, this);

        ImageBT bt_more = (ImageBT) rootView.findViewById(R.id.bt_more);
        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_unit = (TextView) rootView.findViewById(R.id.tv_unit);
        tv_value = (TextView) rootView.findViewById(R.id.tv_value);
        ImageView iv_type = (ImageView) rootView.findViewById(R.id.iv_type);
        lay_detail = (LinearLayout) rootView.findViewById(R.id.lay_detail);

        final String energy_id = id;
        lay_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("TodayEnergyView", "call ems Daily id " + energy_id);
                    Intent intent = new Intent();
                    intent.putExtra("PERIOD", "Daily");
                    intent.putExtra("ENERGY", energy_id);
                    intent.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    mContext.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        setView(today, percent, unit);
    }

    private void setView(double today, double percent, String unit){

        try {
            tv_value.setText("" + (int) today);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            int mPercent = 0;
            mPercent = (int) percent;

            if (mPercent < 97) {
                tv_status.setText(getResources().getString(R.string.today_save));
            } else if (mPercent < 104) {
                tv_status.setText(getResources().getString(R.string.today_same));
            } else if (mPercent >= 104) {
                tv_status.setText(getResources().getString(R.string.today_over));
            } else {
                tv_status.setText("");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            tv_unit.setText(unit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
