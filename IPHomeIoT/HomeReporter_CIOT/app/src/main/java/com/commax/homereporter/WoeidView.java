package com.commax.homereporter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WoeidView extends LinearLayout {

    static final String TAG = "WoeidView";
    ImageView mIcon;
    TextView mText1;
    boolean mSelected = false;

    public WoeidView(Context context, WoeidItem aItem){
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem, this, true);

        ImageView bt_arrow = (ImageView)findViewById(R.id.bt_arrow);
        bt_arrow.setVisibility(GONE);

        mIcon = (ImageView)findViewById(R.id.iconItem);
        mIcon.setBackground(getResources().getDrawable(R.mipmap.btn_radio_n));
        mText1 = (TextView)findViewById(R.id.dataItem01);
        mText1.setText(aItem.getData(0));
    }

    public void setText(int index, String data){
        if(index==0){
            mText1.setText(data);
        }else {
            throw  new IllegalArgumentException();
        }
    }

    public void setChecked(){
        mSelected = true;
        updateCheck(mSelected);
    }

    public void setUnchecked(){
        mSelected = false;
        updateCheck(mSelected);
    }

    public void updateCheck(boolean selected){
        mSelected = selected;
        if(selected){
            mIcon.setBackground(getResources().getDrawable(R.mipmap.btn_radio_s));
        }else {
            mIcon.setBackground(getResources().getDrawable(R.mipmap.btn_radio_n));
        }
    }
}
