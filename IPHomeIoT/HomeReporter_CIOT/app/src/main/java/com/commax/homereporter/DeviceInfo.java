package com.commax.homereporter;

import java.util.ArrayList;

public class DeviceInfo {

    String rootUuid="";
    String commaxDevice="";
    String deviceType=""; // rootDevice
    String nickName="";
    String controlType="";
    String main_subUuid="";
    int subCount=0;

    SubDevice mainDevice;
    ArrayList<SubDevice> subDevices;

    public void initDevice(String rootUuid, String deviceType, String controlType, String main_subUuid, SubDevice subDevice){
        this.rootUuid = rootUuid;
        this.deviceType = deviceType;
        this.controlType = controlType;
        this.main_subUuid = main_subUuid;
        this.mainDevice = subDevice;
    }

}
