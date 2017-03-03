package com.commax.wirelesssetcontrol.device;

import java.util.ArrayList;

public class DeviceInfo {
    public String rootUuid="";
    public String commaxDevice="";
    public String deviceType=""; // rootDevice
    public String nickName="";
    public String controlType="";
    public String main_subUuid="";
    public String x="";
    public String y="";
    public int room=-1;
    public int subCount=0;

    public SubDevice mainDevice;
    public ArrayList<SubDevice> subDevices;

    public void initDevice(String rootUuid, String deviceType, String controlType, String main_subUuid, SubDevice subDevice){
        this.rootUuid = rootUuid;
        this.deviceType = deviceType;
        this.controlType = controlType;
        this.main_subUuid = main_subUuid;
        this.mainDevice = subDevice;
    }

}
