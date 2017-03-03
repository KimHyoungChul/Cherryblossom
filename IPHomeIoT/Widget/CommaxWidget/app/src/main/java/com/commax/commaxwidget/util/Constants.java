package com.commax.commaxwidget.util;

/**
 * Created by OWNER on 2017-02-08.
 */
public class Constants {
    public final static String Tag = "commax_widget";

    public final static String ACTION_UPDATE_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_WIDGET";

    public final static String ACTION_UPDATE_DEVICE_PAM_ACTION = "com.commax.services.AdapterService.PAM_ACTION";
    public final static String ACTION_UPDATE_DEVICE_NICKNAME = "com.commax.gateway.service.HomeIoT.NickName_ACTION";
    public final static String ACTION_UPDATE_DEVICE_CONTROL_NICKNAME = " com.commax.controlsub.NickName_ACTION";

    public final static String ACTION_UPDATE_SMART_PLUG_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_SMART_PLUG_WIDGET";
    public final static String ACTION_UPDATE_SMART_PLUG_SERVICE = "com.commax.commaxwidget.ACTION_UPDATE_SMART_PLUG_SERVICE";
    public final static String ACTION_UPDATE_NO_SMART_PLUG_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_NO_SMART_PLUG_WIDGET";
    public final static String ACTION_UPDATE_SMART_PLUG_REFRESH = "com.commax.commaxwidget.ACTION_UPDATE_SMART_PLUG_REFRESH";


    public final static String ACTION_UPDATE_DETECT_SENSOR_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_DETECT_SENSOR_WIDGET";
    public final static String ACTION_UPDATE_DETECT_SENSOR_SERVICE = "com.commax.commaxwidget.ACTION_UPDATE_DETECT_SENSOR_SERVICE";
    public final static String ACTION_UPDATE_NO_DETECT_SENSOR_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_NO_DETECT_SENSOR_WIDGET";
    public final static String ACTION_UPDATE_DETECT_SENSOR_REFRESH = "com.commax.commaxwidget.ACTION_UPDATE_DETECT_SENSOR_REFRESH";


    public final static String ACTION_UPDATE_WEATHER_WIDGET = "com.commax.commaxwidget.ACTION_UPDATE_WEATHER_WIDGET";
    public final static String ACTION_UPDATE_WEATHER_SERVICE = "com.commax.commaxwidget.ACTION_UPDATE_WEATHER_SERVICE";
    public final static String ACTION_UPDATE_WEATHER_REFRESH = "com.commax.commaxwidget.ACTION_UPDATE_WEATHER_REFRESH";


    public final static String ACTION_LISTVIEW_CLICK = "com.commax.commaxwidget.ACTION_LISTVIEW_CLICK";
    public final static String ACTION_DETAIL_CANCEL_WIDGET = "com.commax.commaxwidget.ACTION_DETAIL_CANCEL_WIDGET";

    public final static String ACTION_REFRESH_WIDGET = "com.commax.commaxwidget.ACTION_REFRESH_WIDGET";
    public final static String ACTION_LOTATION_WIDGET = "com.commax.commaxwidget.ACTION_LOTATION_WIDGET";
    public final static String ACTION_LOTATION_WIDGET_SERVICE = "com.commax.commaxwidget.ACTION_LOTATION_WIDGET_SERVICE";

    public final static String EXTRA_BG_TYPE = "EXTRA_BG_TYPE";
    public final static String EXTRA_SELECT_LIST_INDEX = "EXTRA_SELECT_LIST_INDEX";

    public final static String EXTRA_WEATHER_TEMP = "EXTRA_WEATHER_TEMP";
    public final static String EXTRA_WEATHER_CODE = "EXTRA_WEATHER_CODE";
    public final static String EXTRA_WEATHER_HUMID = "EXTRA_WEATHER_HUMID";
    public final static String EXTRA_WEATHER_LOCATION = "EXTRA_WEATHER_LOCATION";

    public final static String EXTRA_SMART_PLUG_DEVICES = "EXTRA_SMART_PLUG_DEVICES";
    public final static String EXTRA_SMART_PLUG_DEVICES_ELECTRIC_METER = "EXTRA_SMART_PLUG_DEVICES_ELECTRIC_METER";
    public final static String EXTRA_SMART_PLUG_DEVICES_SWITCH_BINARY = "EXTRA_SMART_PLUG_DEVICES_SWITCH_BINARY";


    public final static String EXTRA_CURRENT_DETECT_SENSOR = "EXTRA_CURRENT_DETECT_SENSOR";
    public final static String EXTRA_CURRENT_DETECT_SENSOR_TEMP = "EXTRA_CURRENT_DETECT_SENSOR_TEMP";
    public final static String EXTRA_CURRENT_DETECT_SENSOR_HUMIDITY = "EXTRA_CURRENT_DETECT_SENSOR_HUMIDITY";

    /* STATUS NOTIFICATION */
    public final static int TYPE_WIFI = 1;
    public final static int TYPE_MOBILE = 2;
    public final static int TYPE_NOT_CONNECTED = 0;

    public final static String ACTION_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public final static String ACTION_WIFE_STATE_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";

    public final static String ACTION_CLOUD_STATE_CHANGED = "com.commax.homeiot.service.NetworkSate_ACTION";
    public final static String EXTRA_CLOUD_STAE = "networkState";

    public final static String ACTION_CHANGED_DOOR_CAMERA_STATE = "com.commax.commaxwidget.ACTION_CHANGED_DOOR_CAMERA_STATE";
    public final static String EXTRA_DOOR_CAMERA_STATE = "EXTRA_DOOR_CAMERA_STATE";

    public final static String EXTRA_KEY_FROM ="from";
    public static final String FROM_DOORPHONE_SETTING = "doorphoneSetting";

    public final static String ACTION_CHANGED_CREATE_ACCOUNT = "com.commax.login.Create_account_ACTION";
    public final static String EXTRA_CREATE_ACCOUNT_STATE = "create_account";

    public final static String ACTION_CLICK_EXTERNAL_NETWORK = "com.commax.commaxwidget.ACTION_CLICK_EXTERNAL_NETWORK";

    public final static String TRUE_STATE = "true";
    public final static String FALSE_STATE = "false";

    public final static String ON_STATE = "on";
    public final static String OFF_STATE = "off";

    public final static String YES_STATE = "yes";
    public final static String NO_STATE = "no";
}
