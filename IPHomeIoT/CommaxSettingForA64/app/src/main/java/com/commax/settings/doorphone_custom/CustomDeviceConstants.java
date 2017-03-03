package com.commax.settings.doorphone_custom;

/**
 * Created by OWNER on 2016-11-23.
 */

public class CustomDeviceConstants {

    //공통 XML 형식 TAG
    public static final String MESSAGE_HEADER       = "MESSAGE_HEADER";
    public static final String MESSAGE_BODY         = "MESSAGE_BODY";

    //종버튼 3초 누르면 오는 XML형식의 브로드케스팅 정보(255.255.255.255:3003)
    public static final String DEVICE_TYPE          = "DEVICE_TYPE";
    public static final String IPC_SERIALNUMBER     = "IPC_SERIALNUMBER";
    public static final String LAN_CONFIG            = "LANConfig";

    //Items Type 정의
    public static final String Msg_type = "Msg_type";
    public static final String DeviceType = "DeviceType";
    public static final String DeviceModule = "DeviceModule";
    public static final String SerialNumber = "SerialNumber";

    public static final String MacAddress = "MacAddress";
    public static final String IPAddress = "IPAddress";
    public static final String Netmask = "Netmask";
    public static final String Gateway = "Gateway";

    //Message Type(Msg_type)
    public static final String MSG_SYSTEM_NOTIFY = "SYSTEM_NOTIFY_INFO_MESSAGE";
    public static final String MSG_AUXPTZ_HEARTBEAT = "AUXPTZ_HEARTBEAT_MESSAGE";
    public static final String MSG_SYSTEM_CONTROL = "SYSTEM_CONTROL_MESSAGE";

}
