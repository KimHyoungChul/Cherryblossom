package com.commax.control.Common;

import java.io.Serializable;

/**
 * Created by gbg on 2016-09-23.
 */
public class DeviceInfoSimple implements Serializable {

    private static final long serialVersionUID = 3511467623594314064L;

    public String rootUuid;        //36Byte
    public String nickName;        //user defined device name
    public String nCategory;       //Category

    public void DeviceInfoSimple() {
        this.rootUuid = "";
        this.nickName = "";
        this.nCategory = "";
    }

    public void DeviceInfoSimple(String rootUuid, String nickName, String nCategory) {
        this.rootUuid = rootUuid;
        this.nickName = nickName;
        this.nCategory = nCategory;
    }

    public void setDeviceInfo(String rootUuid, String nickName, String nCategory) {
        this.rootUuid = rootUuid;
        this.nickName = nickName;
        this.nCategory = nCategory;
    }

    public String getRootUuid() {
        return rootUuid;
    }

    public String getNickName() {
        return nickName;
    }

    public String getCategory() {
        return nCategory;
    }
}
