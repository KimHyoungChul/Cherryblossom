package com.commax.settings.setting_provider;

import android.content.Context;

public class ServerIp extends GetSetBase {

    private static final int CCTV_PROVIDER_INDEX = 4;
    private static final String CCTV_GET_QUERY = "CALL db_house.proc_cctv_info()";
    private static final String CCTV_SET_QUERY = "CALL db_house.proc_cctv_update(\"%1$s\")";
    private static final String CCTV_BROADCAST_ACTION = "commax.action.updated.CCTV_SERVER_IP";

    private static final int DEVICE_PROVIDER_INDEX = 5;
    private static final String DEVICE_GET_QUERY = "CALL db_house.proc_device_service_info()";
    private static final String DEVICE_SET_QUERY = "CALL db_house.proc_device_service_update(\"%1$s\")";
    // private static final String SET_QUERY
    // ="UPDATE tbl_wp_server_set SET wss_device_service = ";
    private static final String DEVICE_BROADCAST_ACTION = "commax.action.updated.DEVICE_SERVER_IP";

    private static final int ENERGY_INDEX = 23;
    public static final String ENERGY_BROADCAST_ACTION = "commax.action.updated.ENERGY_SERVER_IP";

    public static final int HOME_INDEX = 6;
    private static final String HOME_BROADCAST_ACTION = "commax.action.updated.HOME_SERVER_IP";

    private static final int LOCAL_PROVIDER_INDEX = 1;
    private static final String LOCAL_GET_QUERY = "CALL db_house.proc_ens_info()";
    private static final String LOCAL_SET_QUERY = "CALL db_house.proc_ens_update(\"%1$s\")";
    private static final String LOCAL_BROADCAST_ACTION = "commax.action.updated.LOCAL_SERVER_IP";

    private static final int SIP_PROVIDER_INDEX = 2;
    private static final String SIP_GET_QUERY = "CALL db_house.proc_sip_info()";
    private static final String SIP_SET_QUERY = "CALL db_house.proc_sip_update(\"%1$s\")";
    private static final String SIP_BROADCAST_ACTION = "commax.action.updated.SIP_SERVER_IP";

    private static final int UPDATE_PROVIDER_INDEX = 3;
    private static final String UPDATE_GET_QUERY = "CALL db_house.proc_update_info()";
    private static final String UPDATE_SET_QUERY = "CALL db_house.proc_update_update(\"%1$s\")";
    private static final String UPDATE_BROADCAST_ACTION = "commax.action.updated.UPDATE_SERVER_IP";

    public ServerIp(Context context) {
        super(context);
    }

    public String getCctv() {
        return getValue(CCTV_PROVIDER_INDEX, CCTV_GET_QUERY);
    }

    public void getCctvInBackground(
            final OnStringCallback l) {
        getValueInBackground(CCTV_PROVIDER_INDEX, CCTV_GET_QUERY, l);
    }

    public Boolean setCctv(String value) {
        return setValue(CCTV_PROVIDER_INDEX, CCTV_SET_QUERY, value,
                CCTV_BROADCAST_ACTION);
    }

    public void setCctvInBackground(String value, OnBooleanCallback l) {
        setValueInBackground(CCTV_PROVIDER_INDEX, CCTV_SET_QUERY, value,
                CCTV_BROADCAST_ACTION, l);
    }

    public String getDevice() {
        return getValue(DEVICE_PROVIDER_INDEX, DEVICE_GET_QUERY);
    }

    public String getDeviceProvider() {
        SettingProvider ps = new SettingProvider(context);
        return ps.getValue(DEVICE_PROVIDER_INDEX);
    }

    public void getDeviceInBackground(
            final OnStringCallback l) {
        getValueInBackground(DEVICE_PROVIDER_INDEX, DEVICE_GET_QUERY, l);
    }

    public boolean setDevice(String value) {
        return setValue(DEVICE_PROVIDER_INDEX, DEVICE_SET_QUERY, value,
                DEVICE_BROADCAST_ACTION);
    }

    public void setDeviceInBackground(String value, OnBooleanCallback l) {
        setValueInBackground(DEVICE_PROVIDER_INDEX, DEVICE_SET_QUERY, value,
                DEVICE_BROADCAST_ACTION, l);
    }

    public String getEnergy() {
        SettingProvider ps = new SettingProvider(context);
        return ps.getValue(ENERGY_INDEX);
    }

    public Boolean setEnergy(String value) {
        if (value == null) {
            return false;
        }
        SettingProvider ps = new SettingProvider(context);
        boolean ret = ps.setValue(ENERGY_INDEX, value);
        if (ret) {
            sendBroadcast(ENERGY_BROADCAST_ACTION);
        }
        return ret;
    }

    public String getHome() {
        SettingProvider ps = new SettingProvider(context);
        return ps.getValue(HOME_INDEX);
    }

    public Boolean setHome(String value) {
        if (value == null) {
            return false;
        }
        SettingProvider ps = new SettingProvider(context);
        boolean ret = ps.setValue(HOME_INDEX, value);
        if (ret) {
            sendBroadcast(HOME_BROADCAST_ACTION);
        }
        return ret;
    }

    public String getLocalProvider() {
        SettingProvider ps = new SettingProvider(context);
        return ps.getValue(LOCAL_PROVIDER_INDEX);

    }

    public String getLocal() {
        return getValue(LOCAL_PROVIDER_INDEX, LOCAL_GET_QUERY);
    }

    public void getLocalInBackground(
            final OnStringCallback l) {
        getValueInBackground(LOCAL_PROVIDER_INDEX, LOCAL_GET_QUERY, l);
    }

    public boolean setLocal(String value) {
        return setValue(LOCAL_PROVIDER_INDEX, LOCAL_SET_QUERY, value,
                LOCAL_BROADCAST_ACTION);
    }

    public void setLocalInBackground(String value, OnBooleanCallback l) {
        setValueInBackground(LOCAL_PROVIDER_INDEX, LOCAL_SET_QUERY, value,
                LOCAL_BROADCAST_ACTION, l);
    }

    public String getSip() {
        return getValue(SIP_PROVIDER_INDEX, SIP_GET_QUERY);
    }

    public void getSipInBackground(
            final OnStringCallback l) {
        getValueInBackground(SIP_PROVIDER_INDEX, SIP_GET_QUERY, l);
    }

    public boolean setSip(String value) {
        return setValue(SIP_PROVIDER_INDEX, SIP_SET_QUERY, value,
                SIP_BROADCAST_ACTION);
    }

    public void setSipInBackground(String value, OnBooleanCallback l) {
        setValueInBackground(SIP_PROVIDER_INDEX, SIP_SET_QUERY, value,
                SIP_BROADCAST_ACTION, l);
    }

    public String getUpdate() {
        return getValue(UPDATE_PROVIDER_INDEX, UPDATE_GET_QUERY);
    }

    public void getUpdateInBackground(
            final OnStringCallback l) {
        getValueInBackground(UPDATE_PROVIDER_INDEX, UPDATE_GET_QUERY, l);
    }

    public boolean setUpdate(String value) {
        return setValue(UPDATE_PROVIDER_INDEX, UPDATE_SET_QUERY, value,
                UPDATE_BROADCAST_ACTION);
    }

    public void setUpdateInBackground(String value, OnBooleanCallback l) {
        setValueInBackground(UPDATE_PROVIDER_INDEX, UPDATE_SET_QUERY, value,
                UPDATE_BROADCAST_ACTION, l);
    }
}
