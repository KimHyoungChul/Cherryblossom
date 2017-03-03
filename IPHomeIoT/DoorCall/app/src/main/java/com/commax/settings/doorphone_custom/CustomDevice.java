package com.commax.settings.doorphone_custom;

/**
 * Created by OWNER on 2016-11-18.
 */

/**
 * 커스텀 카메라 디바이스 데이터 모델 => 테스트용
 */
public class CustomDevice {
    private String modelName; //name
    private String nickname; //임시 디바이스 명(yslee)
    private String mac;
    private String usn; //unique serial number
    private String ipv4;
    private String ipv4gateway;
    private String ipv4subnet;
    private String dns;
    private String webPort;
    private String rtspPort;
    private String sipPhoneNo; //SIP 번호(2017-01,24,yslee::항목 추가함)
    private String firstStreamUrl;
    private String secondStreamUrl;
    private String isOk; //IS_OK(2017-01,24,yslee::항목 추가함)

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getNickName() {
        return nickname;
    }

    public void setNickName(String name) {
        this.nickname = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
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

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getSipPhoneNo() {
        return sipPhoneNo;
    }

    public void setSipPhoneNo(String sipPhoneNo) {
        this.sipPhoneNo = sipPhoneNo;
    }

    public String getWebPort() {
        return webPort;
    }

    public void setWebPort(String webPort) {
        this.webPort = webPort;
    }

    public String getRtspPort() {
        return rtspPort;
    }

    public void setRtspPort(String rtspPort) {
        this.rtspPort = rtspPort;
    }

    public String getFirstStreamUrl() {
        return firstStreamUrl;
    }

    public void setFirstStreamUrl(String firstStreamUrl) {
        this.firstStreamUrl = firstStreamUrl;
    }

    public String getSecondStreamUrl() {
        return secondStreamUrl;
    }

    public void setSecondStreamUrl(String secondStreamUrl) {
        this.secondStreamUrl = secondStreamUrl;
    }

    public String getIsOk() {
        return isOk;
    }

    public void setIsOk(String isOk) {
        this.isOk = isOk;
    }

    public CustomDevice() {
    }

    public CustomDevice(String _modelName, String _mac, String _usn, String _ipv4, String _ipv4gateway, String _ipv4subnet,String _sipPhoneNo,  String _dns, String _webPort, String _rtspPort, String _firstStreamUrl, String _secondStreamUrl, String _isOK) {
        this.modelName = _modelName;
        this.mac = _mac;
        this.usn = _usn;
        this.ipv4 = _ipv4;
        this.ipv4gateway = _ipv4gateway;
        this.ipv4subnet = _ipv4subnet;
        this.sipPhoneNo = _sipPhoneNo;
        this.dns = _dns;
        this.webPort = _webPort;
        this.rtspPort = _rtspPort;
        this.firstStreamUrl = _firstStreamUrl;
        this.secondStreamUrl = _secondStreamUrl;
        this.isOk = _isOK;

    }
}
