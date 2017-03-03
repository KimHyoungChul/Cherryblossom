package com.commax.settings.doorphone_custom;

/**
 * Created by OWNER on 2016-11-23.
 */

public class MessageData {

    private String LOG_TAG = MessageData.class.getSimpleName();

    //Message Data
    private String msgType;
    private String msgCode;
    private String msgFlag;
    private String deviceName; //device type(=modelName)
    private String deviceModule;
    private String usn; //unique serial number
    private String mac;
    private String ipv4;
    private String ipv4gateway;
    private String ipv4subnet;

    public MessageData() {
    }

    public String getLOG_TAG() {
        return LOG_TAG;
    }

    public void setLOG_TAG(String LOG_TAG) {
        this.LOG_TAG = LOG_TAG;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(String msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceModule() {
        return deviceModule;
    }

    public void setDeviceModule(String deviceModule) {
        this.deviceModule = deviceModule;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getIpv4gateway() {
        return ipv4gateway;
    }

    public void setIpv4gateway(String ipv4gateway) {
        this.ipv4gateway = ipv4gateway;
    }

    public String getIpv4subnet() {
        return ipv4subnet;
    }

    public void setIpv4subnet(String ipv4subnet) {
        this.ipv4subnet = ipv4subnet;
    }


}
