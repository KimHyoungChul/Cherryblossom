package com.commax.commaxwidget.wallpad.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class DeviceInfo implements Parcelable {

    public String rootUuid = "";
    public String commaxDevice = "";
    public String deviceType = ""; // rootDevice
    public String nickName = "";
    public String controlType = "";
    public String main_subUuid = "";
    public int subCount = 0;

    public SubDevice mainDevice;
    public ArrayList<SubDevice> subDevices;

    public DeviceInfo(){
    }
    protected DeviceInfo(Parcel in) {
        rootUuid = in.readString();
        commaxDevice = in.readString();
        deviceType = in.readString();
        nickName = in.readString();
        controlType = in.readString();
        main_subUuid = in.readString();
        subCount = in.readInt();
    }

    public static final Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel in) {
            return new DeviceInfo(in);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };

    public void initDevice(String rootUuid, String deviceType, String controlType, String main_subUuid, SubDevice subDevice) {
        this.rootUuid = rootUuid;
        this.deviceType = deviceType;
        this.controlType = controlType;
        this.main_subUuid = main_subUuid;
        this.mainDevice = subDevice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(rootUuid);
        parcel.writeString(commaxDevice);
        parcel.writeString(deviceType);
        parcel.writeString(nickName);
        parcel.writeString(controlType);
        parcel.writeString(main_subUuid);
        parcel.writeInt(subCount);
    }
}
