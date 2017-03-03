package com.commax.pairing.ip_device_setting;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * IP Device 모델
 * Created by bagjeong-gyu on 2016. 9. 12..
 */
public class IPDevice implements Parcelable{
    private String name;
    private String ipAddress;
    private String newIPAddress;
    private String mac;
    private String sipPhoneNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getNewIPAddress() {
        return newIPAddress;
    }

    public void setNewIPAddress(String newIPAddress) {
        this.newIPAddress = newIPAddress;
    }

    public String getSipPhoneNo() {
        return sipPhoneNo;
    }

    public void setSipPhoneNo(String sipPhoneNo) {
        this.sipPhoneNo = sipPhoneNo;
    }

    public IPDevice() {
    }

    public IPDevice(Parcel in) {
        readFromParcel(in);
    }

    public IPDevice(String _name, String _ipAddress, String _newIPAddress, String _mac, String _sipPhoneNo) {
        this.name = _name;
        this.ipAddress = _ipAddress;
        this.newIPAddress = _newIPAddress;
        this.mac = _mac;
        this.sipPhoneNo = _sipPhoneNo;

    }

// -------------------------------------------------------------------------
// Getters & Setters section - 각 필드에 대한 get/set 메소드들
// 여기서는 생략했음
// ....
// ....
// -------------------------------------------------------------------------


    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(ipAddress);
        dest.writeString(newIPAddress);
        dest.writeString(mac);
        dest.writeString(sipPhoneNo);

    }

    private void readFromParcel(Parcel in){
        name = in.readString();
        ipAddress = in.readString();
        newIPAddress = in.readString();
        mac = in.readString();
        sipPhoneNo = in.readString();

    }

    public static final Creator CREATOR = new Creator() {
        public IPDevice createFromParcel(Parcel in) {
            return new IPDevice(in);
        }

        public IPDevice[] newArray(int size) {
            return new IPDevice[size];
        }
    };



}
