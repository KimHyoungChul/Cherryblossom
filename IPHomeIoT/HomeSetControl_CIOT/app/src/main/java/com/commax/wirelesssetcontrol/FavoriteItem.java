package com.commax.wirelesssetcontrol;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class FavoriteItem {

    static final String TAG = "FavoriteItem";
    private Drawable mIcon;
    private String str_name;
    private String str_package;
    private boolean mSelected=true;

    public FavoriteItem(String app_name, String app_package, boolean selected){
        str_name=app_name;
        str_package=app_package;
        mSelected = selected;
        Log.d(TAG, "FavoriteItem " + str_name);
    }

    public String getAppName(){
        Log.d(TAG, "getAppName " + str_name);
        return str_name;
    }

    public String getPackageName(){
        return str_package;
    }

    public boolean isChecked(){
        return mSelected;
    }

    public void setChecked(){
        mSelected = !mSelected;
        Log.d(TAG, "updateCheck " + mSelected);
    }

    public void setAppName(String obj){
        str_name=obj;
    }

    public void setPackageName(String obj){
        str_package=obj;
    }

    public void setIcon(Drawable icon){
        mIcon=icon;
    }

    public Drawable getIcon(){
        return mIcon;
    }


}
