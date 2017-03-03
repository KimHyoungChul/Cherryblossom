package com.commax.homereporter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class AirView extends LinearLayout {

    TextView tv_type;
    TextView tv_value;
    TextView tv_status;
    TextView tv_unit;
    ImageView iv_status;

    String mType = "";
    Context mContext;

    public AirView(Context context, String type, double data) {
        super(context);
        mContext = context;
        init(context, type, data);
    }

    public void init(Context context, String type, double data) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.air, this);

        tv_value = (TextView) rootView.findViewById(R.id.tv_value);
        tv_type = (TextView) rootView.findViewById(R.id.tv_type);
        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_unit = (TextView) rootView.findViewById(R.id.tv_unit);
        iv_status = (ImageView) rootView.findViewById(R.id.iv_status);

        setData(type, data);
        setGrade(type, data);

    }

    public void updateAir(double data){

        try {
            Log.d("AirView", "" + mType + "updateAir " + data);
            setData(mType, data);
            setGrade(mType, data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setData(String type, double data){
        try {
            switch (type) {
                case NameSpace.AIR_DUST:
                    tv_type.setText(getResources().getString(R.string.dust) + " ");
                    tv_value.setText("" + (int) data);
                    tv_unit.setText(NameSpace.UNIT_FINE_DUST);
                    break;
                case NameSpace.AIR_O3:
                    tv_type.setText(getResources().getString(R.string.o3) + " ");
                    tv_value.setText("" + data);
                    tv_unit.setText(NameSpace.UNIT_OZONE);
                    break;
                case NameSpace.AIR_TOTAL:
                    tv_type.setText(getResources().getString(R.string.total_air) + " ");
                    tv_value.setText("" + (int) data);
                    break;
                case NameSpace.LIFE_ULTRV:
                    tv_type.setText(getResources().getString(R.string.uv) + " ");
                    tv_value.setText("" + (int) data);
                    break;
            }

            try {
                this.mType = type;
                Log.d("AirView", "setData " + mType);
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setGrade(String type, double data){

        Locale locale;
        String country = "";

        try {
            locale = mContext.getResources().getConfiguration().locale;
            country = locale.getCountry();
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            if ((!TextUtils.isEmpty(country)) && (country.equalsIgnoreCase("KR"))) {
                tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text));
            } else {
                tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_other));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            switch (type) {
                case NameSpace.AIR_DUST:
                    if (data <= 30) {
                        tv_status.setText(getResources().getString(R.string.good));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_good);
                    } else if (data <= 80) {
                        tv_status.setText(getResources().getString(R.string.normal));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_normal);
                    } else if (data <= 150) {
                        tv_status.setText(getResources().getString(R.string.bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_bad);
                    } else if (data > 150) {
                        tv_status.setText(getResources().getString(R.string.so_bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_verybad);

                        if ((!TextUtils.isEmpty(country)) && (country.equalsIgnoreCase("KR"))) {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_big));
                        } else {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_other));
                        }
                    }
                    break;
                case NameSpace.AIR_O3:
                    if (data <= 0.030) {
                        tv_status.setText(getResources().getString(R.string.good));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_ozone);
                    } else if (data <= 0.090) {
                        tv_status.setText(getResources().getString(R.string.normal));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_ozone);
                    } else if (data <= 0.150) {
                        tv_status.setText(getResources().getString(R.string.bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_ozone);
                    } else if (data > 0.150) {
                        tv_status.setText(getResources().getString(R.string.so_bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_ozone);

                        if ((!TextUtils.isEmpty(country)) && (country.equalsIgnoreCase("KR"))) {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_big));
                        } else {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_other));
                        }
                    }
                    break;
                case NameSpace.AIR_TOTAL:
                    if (data <= 50) {
                        tv_status.setText(getResources().getString(R.string.good));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_total);
                    } else if (data <= 100) {
                        tv_status.setText(getResources().getString(R.string.normal));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_total);
                    } else if (data <= 250) {
                        tv_status.setText(getResources().getString(R.string.bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_total);
                    } else if (data > 250) {
                        tv_status.setText(getResources().getString(R.string.so_bad));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_total);

                        if ((!TextUtils.isEmpty(country)) && (country.equalsIgnoreCase("KR"))) {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_big));
                        } else {
                            tv_status.setTextSize(getResources().getDimension(R.dimen.air_status_text_other));
                        }
                    }
                    break;

                case NameSpace.LIFE_ULTRV:
                    if (data <= 2) {
                        tv_status.setText(getResources().getString(R.string.low));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_uv);
                    } else if (data <= 5) {
                        tv_status.setText(getResources().getString(R.string.normal));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_uv);
                    } else if (data <= 7) {
                        tv_status.setText(getResources().getString(R.string.high));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_uv);
                    } else if (data <= 10) {
                        tv_status.setText(getResources().getString(R.string.extremely_high));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_uv);
                    } else if (data > 10) {
                        tv_status.setText(getResources().getString(R.string.danger));
                        iv_status.setImageResource(R.mipmap.ic_rp_air_uv);
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getType(){
        return mType;
    }
}
