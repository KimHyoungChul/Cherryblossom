package com.commax.ipdoorcamerasetting.registration;

/**
 * Created by OWNER on 2016-11-18.
 */

public class Device {
    private String modelName;
    private String mac;
    private String usn; //unique serial number
    private String ipv4;
    private String ipv4gateway;
    private String ipv4subnet;
    private String dns;
    private String webPort;
    private String rtspPort;
    private String firstStreamUrl;
    private String secondStreamUrl;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
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
}
