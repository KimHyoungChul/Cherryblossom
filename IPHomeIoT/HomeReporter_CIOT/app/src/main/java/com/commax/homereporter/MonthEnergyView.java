package com.commax.homereporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class MonthEnergyView extends LinearLayout {

    Context mContext;
    LinearLayout lay_energy;
    TextView tv_date;

    public MonthEnergyView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.month_energy, this);

        lay_energy = (LinearLayout) rootView.findViewById(R.id.lay_energy);
        tv_date = (TextView) rootView.findViewById(R.id.tv_date);
        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);

        setPeriod();
    }

    private void setPeriod(){

        try {
            Calendar cal = Calendar.getInstance();
            String start = CalendarUtils.firstDotFormat(cal);
//        cal.add(Calendar.MONTH, -1);
            String end = CalendarUtils.dotFormat(cal);

            tv_date.setText(start + " ~ " + end);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void addMonthItem(EnergyItem energyItem){

        try {
            lay_energy.addView(energyItem);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
