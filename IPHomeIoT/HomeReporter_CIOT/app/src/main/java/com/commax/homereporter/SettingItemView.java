package com.commax.homereporter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingItemView extends LinearLayout {

    static final String TAG = "SettingItemView";
    public ImageView mIcon;
    TextView mText1;
    ImageView bt_arrow;
    boolean mSelected;

    public SettingItemView(Context context, SettingItem aItem){
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        mIcon = (ImageView)findViewById(R.id.iconItem);
        mIcon.setImageDrawable(aItem.getIcon());
        mText1 = (TextView)findViewById(R.id.dataItem01);
        mText1.setText(aItem.getData());
        bt_arrow = (ImageView) findViewById(R.id.bt_arrow);

    }

    private void setArrow(String data){
        try{
            Log.d(TAG, "setArrow "+ data);
            if ((data.equalsIgnoreCase(getResources().getString(R.string.info_weather)))
                    ||(data.equalsIgnoreCase(getResources().getString(R.string.info_air)))
                    ||(data.equalsIgnoreCase(getResources().getString(R.string.info_health_life)))){
                bt_arrow.setAlpha(1.0f);
            } else {
                bt_arrow.setVisibility(INVISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setChecked(String type){
        mSelected = !mSelected;
        updateCheck(mSelected, type);
    }

    public void updateCheck(boolean selected, String type){
        Log.d(TAG, "updateCheck "+selected);
        mSelected = selected;

        try {
            if (selected) {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.btn_power_s));
                mText1.setTextColor(0xff0c1a3d);
                bt_arrow.setAlpha(1.0f);
                if (type.equalsIgnoreCase(NameSpace.INFO_WEATHER)){
                    mIcon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_dc_default));
                }
            } else {
                mIcon.setImageDrawable(getResources().getDrawable(R.drawable.btn_power_n));
                mText1.setTextColor(0x80999ca5);
                bt_arrow.setAlpha(0.3f);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setText(int index, String data){
        try {
            if (index == 0) {
                mText1.setText(data);
                setArrow(data);
            } else {
                throw new IllegalArgumentException();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setArrowAlpha(float alpha){
        try {
            bt_arrow.setAlpha(alpha);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
