package com.commax.commaxwidget.listwidget;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by OWNER on 2017-02-09.
 */
public class ListWidgetItemData implements Parcelable, Serializable{
    private String mTitleText = "";
    private String mSubText = "";
    private int mIconResId;

    public ListWidgetItemData(){}
    public String getItemTitle() {
        return mTitleText;
    }

    public void setItemTitle(String name) {
        this.mTitleText = name;
    }

    public String getItemSubText() {
        return mSubText;
    }

    public void setItemSubText(String subtText) {
        this.mSubText = subtText;
    }

    public int getIconResId() {
        return mIconResId;
    }

    public void setIconResId(int iconResId) {
        this.mIconResId = iconResId;
    }

    protected ListWidgetItemData(Parcel in) {
        mTitleText = in.readString();
        mSubText = in.readString();
        mIconResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitleText);
        dest.writeString(mSubText);
        dest.writeInt(mIconResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListWidgetItemData> CREATOR = new Creator<ListWidgetItemData>() {
        @Override
        public ListWidgetItemData createFromParcel(Parcel in) {
            return new ListWidgetItemData(in);
        }

        @Override
        public ListWidgetItemData[] newArray(int size) {
            return new ListWidgetItemData[size];
        }
    };
}
