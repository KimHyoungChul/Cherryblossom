package com.commax.settings.content_provider;

import android.content.Context;

import com.commax.settings.doorphone_custom.CustomDevice;

import java.util.List;

public class ContentProviderManagerEx extends ContentProviderManager {
    public static String getDoorCameraName(Context context, String ip) {
        List<CustomDevice> doorList = getAllCustomDoorCamera(context);
        for (int i = 0; i < doorList.size(); i++) {
            if (doorList.get(i).getIpv4().equals(ip))
                return doorList.get(i).getModelName();
        }

        return "";
    }

    public static int getUsableSipNo(Context context) {
        int retSipNo = 201;
        List<CustomDevice> doorList = getAllCustomDoorCamera(context);
        if (doorList.size() == 0)
            return retSipNo;

        CustomDevice customDevice;
        int curSipNo;
        for (int i = 0; i < doorList.size(); i++) {
            customDevice = doorList.get(i);
            curSipNo = Integer.parseInt(customDevice.getSipPhoneNo());
            if (curSipNo - retSipNo > 1) {
                retSipNo++;
                return retSipNo;
            }
            retSipNo = curSipNo;
        }

        retSipNo++;
        return retSipNo;
    }
}
