package com.commax.wirelesssetcontrol;

import com.commax.wirelesssetcontrol.device.DeviceInfo;

import java.util.ArrayList;

public class DeviceInfoSaver {

    ArrayList<DeviceInfo> hvac_list;

    public DeviceInfoSaver() {
    }

    public void addHVAC(DeviceInfo info){

        try{
            hvac_list.add(info);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
