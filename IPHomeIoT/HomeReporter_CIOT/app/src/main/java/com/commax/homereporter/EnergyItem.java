package com.commax.homereporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class EnergyItem extends LinearLayout {

    TextView tv_status;
    TextView tv_value;
    TextView tv_unit;
    ImageView iv_type;

    Context mContext;

    public EnergyItem(Context context, String id, double today, double percent, String unit) {
        super(context);
        mContext = context;
        init(context, id, today, percent, unit);
    }

    public void init(Context context,String id, double today, double percent, String unit) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.energy_item, this);

        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_value = (TextView) rootView.findViewById(R.id.tv_value);
        tv_unit = (TextView) rootView.findViewById(R.id.tv_unit);
        LinearLayout lay_value = (LinearLayout) rootView.findViewById(R.id.lay_value);
        iv_type = (ImageView) rootView.findViewById(R.id.iv_type);

        setView(id, today, percent, unit);

    }

    private void setView(String id, double today, double percent, String unit){

        try {
            tv_value.setText("" + (int) today);

            int mPercent = 0;
            mPercent = (int) percent;

            if (mPercent < 97) {
                tv_status.setText(mContext.getResources().getString(R.string.save));
                setLowIcon(id);
            } else if (mPercent < 104) {
                tv_status.setText(mContext.getResources().getString(R.string.same));
                setGoodIcon(id);
            } else if (mPercent >= 104) {
                tv_status.setText(mContext.getResources().getString(R.string.over));
                setHighIcon(id);
            } else {
                tv_status.setText("");
            }

            tv_unit.setText(unit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setLowIcon(String id){

        try {
            switch (id) {
                case Items.ELEC:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_eletric_low);
                    break;

                case Items.GAS:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_gas_low);
                    break;

                case Items.WATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_water_low);
                    break;

                case Items.HOTWATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_hotwater_low);
                    break;

                case Items.HEATING:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_heater_low);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setGoodIcon(String id){
        try {
            switch (id) {
                case Items.ELEC:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_eletric_good);
                    break;

                case Items.GAS:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_gas_good);
                    break;

                case Items.WATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_water_good);
                    break;

                case Items.HOTWATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_hotwater_good);
                    break;

                case Items.HEATING:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_heater_good);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void setHighIcon(String id){
        try {
            switch (id) {
                case Items.ELEC:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_eletric_high);
                    break;

                case Items.GAS:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_gas_high);
                    break;

                case Items.WATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_water_high);
                    break;

                case Items.HOTWATER:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_hotwater_high);
                    break;

                case Items.HEATING:
                    iv_type.setBackgroundResource(R.mipmap.ic_rp_heater_high);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
