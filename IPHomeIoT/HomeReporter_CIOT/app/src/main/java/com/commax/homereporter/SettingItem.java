package com.commax.homereporter;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class SettingItem {

    static final String TAG = "SettingItem";
    private Drawable mIcon;
    private String mData;
    private boolean mSelected=true;
    private String type;

    public SettingItem(String type, String str, boolean selected){
//        mIcon=icon;
        mData=str;
        mSelected = selected;
        this.type = type;
    }

    public String getData(){
        return mData;
    }

    public String getType(){
        return type;
    }

    public boolean isChecked(){
        return mSelected;
    }

    public void setChecked(){
        mSelected = !mSelected;
        Log.d(TAG, "updateCheck " + mSelected);
    }

    public void setData(String obj){
        mData=obj;
    }

    public void setIcon(Drawable icon){
        mIcon=icon;
    }

    public Drawable getIcon(){
        return mIcon;
    }


}
