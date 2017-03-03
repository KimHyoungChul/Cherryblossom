package com.commax.homereporter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ParkingView extends LinearLayout {

    TextView tv_car_number;
    TextView tv_second;
//    TextView tv_first;
//    FrameLayout lay_first;

    public ParkingView(Context context, String number, String position) {
        super(context);
        init(context, number, position);
    }

    public void init(Context context, String number, String position){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.parking, this);

        tv_car_number = (TextView) rootView.findViewById(R.id.tv_car_number);
        tv_second = (TextView) rootView.findViewById(R.id.tv_second);
//        tv_first = (TextView) rootView.findViewById(R.id.tv_first);
//        lay_first = (FrameLayout) rootView.findViewById(R.id.lay_first);
        ImageView iv_status = (ImageView) rootView.findViewById(R.id.iv_status);

        setData(number, position);

    }

    private void setData(String number, String position){

        try {
            tv_car_number.setText(number + " ");
        }catch (Exception e){
            e.printStackTrace();
        }

//        String first="";
//        String second="";
//
//        if(position.contains("-")){
//            String[] posi = position.split("-");
//            first = posi[0];
//
//            if(posi.length>1) {
//                for (int i = 1; i < posi.length; i++) {
//                    second=second+posi[i];
//                }
//            }
//            lay_first.setVisibility(VISIBLE);
//        }else {
//            second = position;
//            lay_first.setVisibility(GONE);
//        }
//
//        tv_first.setText(first);

        try {
            tv_second.setText(position);
            tv_second.setSelected(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
