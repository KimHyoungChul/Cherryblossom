package com.commax.homereporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MissedView extends LinearLayout {

    Context mContext;
    LinearLayout lay_missed;

    public MissedView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.missed, this);

        lay_missed = (LinearLayout) rootView.findViewById(R.id.lay_missed);
        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);

    }
    public void addMonthItem(MissedItem energyItem){
        try {
            lay_missed.addView(energyItem);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
