package com.commax.homereporter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

public class WeatherView extends LinearLayout {

    TextView tv_status;
    ImageView iv_status;
    TextView tv_temp;
    TextView tv_scale;
    Locale locale;

    Context mContext;

    public WeatherView(Context context, double[] value) {
        super(context);
        mContext = context;
        init(context, value);
    }

    public void init(Context context, double[] value){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.weather, this);

        tv_status = (TextView) rootView.findViewById(R.id.tv_status);
        tv_scale = (TextView) rootView.findViewById(R.id.tv_scale);
        tv_temp = (TextView) rootView.findViewById(R.id.tv_temp);
        iv_status = (ImageView) rootView.findViewById(R.id.iv_status);
        locale = mContext.getResources().getConfiguration().locale;

        setData(value);
        setCF();
    }

    public void setWeather(double[] value){
        setData(value);
        setCF();
    }

    private void setData(double[] value){

        setDegree((int) value[0]);
        setWeatherStringImage(value[1]);
    }

    public void updateWeather(double[] value){
        setData(value);
        setCF();
    }

    private void setDegree(int value){
        tv_temp.setText(""+value);
    }

    private void setWeatherStringImage(double value){
        try{
            int val = (int)value;
            switch (val){
                case 0:
                    tv_status.setText(R.string.weather0);
                    iv_status.setImageResource(R.mipmap.ic_rpw_tropicalstorm);
                    break;
                case 1:
                    tv_status.setText(R.string.weather1);
                    iv_status.setImageResource(R.mipmap.ic_rpw_tropicalstorm);
                    break;
                case 2:
                    tv_status.setText(R.string.weather2);
                    iv_status.setImageResource(R.mipmap.ic_rpw_tropicalstorm);
                    break;
                case 3:
                    tv_status.setText(R.string.weather3);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 4:
                    tv_status.setText(R.string.weather4);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 5:
                    tv_status.setText(R.string.weather5);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 6:
                    tv_status.setText(R.string.weather6);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 7:
                    tv_status.setText(R.string.weather7);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 8:
                    tv_status.setText(R.string.weather8);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 9:
                    tv_status.setText(R.string.weather9);
                    iv_status.setImageResource(R.mipmap.ic_rpw_drizzle);
                    break;
                case 10:
                    tv_status.setText(R.string.weather10);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 11:
                    tv_status.setText(R.string.weather11);
                    iv_status.setImageResource(R.mipmap.ic_rpw_drizzle);
                    break;
                case 12:
                    tv_status.setText(R.string.weather12);
                    iv_status.setImageResource(R.mipmap.ic_rpw_drizzle);
                    break;
                case 13:
                    tv_status.setText(R.string.weather13);
                    iv_status.setImageResource(R.mipmap.ic_rpw_snow);
                    break;
                case 14:
                    tv_status.setText(R.string.weather14);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 15:
                    tv_status.setText(R.string.weather15);
                    iv_status.setImageResource(R.mipmap.ic_rpw_snow);
                    break;
                case 16:
                    tv_status.setText(R.string.weather16);
                    iv_status.setImageResource(R.mipmap.ic_rpw_snow);
                    break;
                case 17:
                    tv_status.setText(R.string.weather17);
                    iv_status.setImageResource(R.mipmap.ic_rpw_hail);
                    break;
                case 18:
                    tv_status.setText(R.string.weather18);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sleet);
                    break;
                case 19:
                    tv_status.setText(R.string.weather19);
                    iv_status.setImageResource(R.mipmap.ic_rpw_dust);
                    break;
                case 20:
                    tv_status.setText(R.string.weather20);
                    iv_status.setImageResource(R.mipmap.ic_rpw_foggy);
                    break;
                case 21:
                    tv_status.setText(R.string.weather21);
                    iv_status.setImageResource(R.mipmap.ic_rpw_foggy);
                    break;
                case 22:
                    tv_status.setText(R.string.weather22);
                    iv_status.setImageResource(R.mipmap.ic_rpw_smoky);
                    break;
                case 23:
                    tv_status.setText(R.string.weather23);
                    iv_status.setImageResource(R.mipmap.ic_rpw_windy);
                    break;
                case 24:
                    tv_status.setText(R.string.weather24);
                    iv_status.setImageResource(R.mipmap.ic_rpw_windy);
                    break;
                case 25:
                    tv_status.setText(R.string.weather25);
                    iv_status.setImageResource(R.mipmap.ic_rpw_clod);
                    break;
                case 26:
                    tv_status.setText(R.string.weather26);
                    iv_status.setImageResource(R.mipmap.ic_rpw_cloudy);
                    break;
                case 27:
                    tv_status.setText(R.string.weather27);
                    iv_status.setImageResource(R.mipmap.ic_rpw_mostlycloudy);
                    break;
                case 28:
                    tv_status.setText(R.string.weather28);
                    iv_status.setImageResource(R.mipmap.ic_rpw_mostlycloudy);
                    break;
                case 29:
                    tv_status.setText(R.string.weather29);
                    iv_status.setImageResource(R.mipmap.ic_rpw_cloudy_night);
                    break;
                case 30:
                    tv_status.setText(R.string.weather30);
                    iv_status.setImageResource(R.mipmap.ic_rpw_cloudy);
                    break;
                case 31:
                    tv_status.setText(R.string.weather31);
                    iv_status.setImageResource(R.mipmap.ic_rpw_clear);
                    break;
                case 32:
                    tv_status.setText(R.string.weather32);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sunny);
                    break;
                case 33:
                    tv_status.setText(R.string.weather33);
                    iv_status.setImageResource(R.mipmap.ic_rpw_clear);
                    break;
                case 34:
                    tv_status.setText(R.string.weather34);
                    iv_status.setImageResource(R.mipmap.ic_rpw_sunny);
                    break;
                case 35:
                    tv_status.setText(R.string.weather35);
                    iv_status.setImageResource(R.mipmap.ic_rpw_hail);
                    break;
                case 36:
                    tv_status.setText(R.string.weather36);
                    iv_status.setImageResource(R.mipmap.ic_rpw_hot);
                    break;
                case 37:
                    tv_status.setText(R.string.weather37);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 38:
                    tv_status.setText(R.string.weather38);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 39:
                    tv_status.setText(R.string.weather39);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 40:
                    tv_status.setText(R.string.weather40);
                    iv_status.setImageResource(R.mipmap.ic_rpw_rain);
                    break;
                case 41:
                    tv_status.setText(R.string.weather41);
                    iv_status.setImageResource(R.mipmap.ic_rpw_heavysnow);
                    break;
                case 42:
                    tv_status.setText(R.string.weather42);
                    iv_status.setImageResource(R.mipmap.ic_rpw_snow);
                    break;
                case 43:
                    tv_status.setText(R.string.weather43);
                    iv_status.setImageResource(R.mipmap.ic_rpw_heavysnow);
                    break;
                case 44:
                    tv_status.setText(R.string.weather44);
                    iv_status.setImageResource(R.mipmap.ic_rpw_mostlycloudy);
                    break;
                case 45:
                    tv_status.setText(R.string.weather45);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                case 46:
                    tv_status.setText(R.string.weather46);
                    iv_status.setImageResource(R.mipmap.ic_rpw_snow);
                    break;
                case 47:
                    tv_status.setText(R.string.weather47);
                    iv_status.setImageResource(R.mipmap.ic_rpw_thunderstorm);
                    break;
                default:
                    tv_status.setText(R.string.weather26);
                    iv_status.setImageResource(R.mipmap.ic_rpw_cloudy);
                    break;
            }
        }catch (Exception e){

        }
    }

    private void setCF(){
        tv_scale.setText("C");
    }
}
