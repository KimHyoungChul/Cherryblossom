package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FavoriteItemView extends LinearLayout {

    static final String TAG = "FavoriteItemView";
    public ImageView mIcon;
    TextView mText1;
    boolean mSelected;

    public FavoriteItemView(Context context, FavoriteItem aItem){
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.favorite_listitem, this, true);

        mIcon = (ImageView)findViewById(R.id.iconItem);
        mIcon.setImageDrawable(aItem.getIcon());
        mText1 = (TextView)findViewById(R.id.dataItem01);
        mText1.setText(aItem.getAppName());

    }

    public void setChecked(){
        mSelected = !mSelected;
        updateCheck(mSelected);
    }

    public void updateCheck(boolean selected){
        Log.d(TAG, "updateCheck " + selected);
        mSelected = selected;

        try {
            if (selected) {
                mIcon.setImageDrawable(getResources().getDrawable(R.mipmap.btn_checkbox_s));
            } else {
                mIcon.setImageDrawable(getResources().getDrawable(R.mipmap.btn_checkbox_n));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setText(int index, String data){
        try {
            if (index == 0) {
                mText1.setText(data);
            } else {
                throw new IllegalArgumentException();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
