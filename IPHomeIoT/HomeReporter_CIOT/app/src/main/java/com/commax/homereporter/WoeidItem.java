package com.commax.homereporter;

import android.util.Log;

public class WoeidItem {

    static final String TAG = "WoeidItem";
//    private Drawable mIcon;
    private String[] mData;
    boolean mSelected = false;

    public WoeidItem(String[] obj){
//        mIcon=icon;
        mData=obj;
    }

//    public WoeidItem(String obj01, String obj02){
////        mIcon=icon;
//
//        mData=new String[3];
//        mData[0]=obj01;
//        mData[1]=obj02;
////        mData[2]=obj03;
//    }

    public WoeidItem(String obj01, String obj02, String obj03, String obj04, String obj05){
//        mIcon=icon;

        mData=new String[5];
        mData[0]=obj01;
        mData[1]=obj02;
        mData[2]=obj03;
        mData[3]=obj04;
        mData[4]=obj05;
//        mData[2]=obj03;
    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        if(mData==null||index>=mData.length){
            return null;
        }
        return mData[index];
    }

    public void setData(String[] obj){
        mData=obj;
    }


    public boolean isChecked(){
        return mSelected;
    }

    public void setChecked(){
        mSelected = true;
    }

    public void setUnchecked(){
        mSelected = false;
    }

}
