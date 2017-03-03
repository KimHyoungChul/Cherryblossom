package com.commax.onvif_device_manager.device;

/**
 * 디바이스 데이터 모델
 * Created by OWNER on 2016-10-12.
 */

public class DeviceInfo {

    private String ip; //ip
    private String id; //아이디
    private String password; //비밀번호
    private String alias; //디바이스 별명
    private String isConfirmed; //디바이스 사용 가능 여부

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
}
