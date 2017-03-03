package com.commax.commaxwidget.wallpad.data;

import android.os.Parcel;
import android.os.Parcelable;

public class SubDevice implements Parcelable {
    public String subUuid="";
    public String  sort="";
    public String value="";
    public String scale="";
    public String option1="";
    public String option2="";

    public  SubDevice(){}
    protected SubDevice(Parcel in) {
        subUuid = in.readString();
        sort = in.readString();
        value = in.readString();
        scale = in.readString();
        option1 = in.readString();
        option2 = in.readString();
    }

    public static final Creator<SubDevice> CREATOR = new Creator<SubDevice>() {
        @Override
        public SubDevice createFromParcel(Parcel in) {
            return new SubDevice(in);
        }

        @Override
        public SubDevice[] newArray(int size) {
            return new SubDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subUuid);
        parcel.writeString(sort);
        parcel.writeString(value);
        parcel.writeString(scale);
        parcel.writeString(option1);
        parcel.writeString(option2);
    }
}
