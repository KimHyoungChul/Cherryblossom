package com.commax.onvif_device_manager.device;

import com.commax.onvif_device_manager.device.OnvifDevice;

/**
 * Created by OWNER on 2016-10-12.
 */

public interface DeviceInfoConfirmListener {
    public void onDeviceInfoConfirmed(int position, OnvifDevice deviceInfo);
}
