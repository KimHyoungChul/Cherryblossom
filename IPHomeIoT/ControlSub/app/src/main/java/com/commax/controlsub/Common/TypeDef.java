package com.commax.controlsub.Common;

import android.app.Application;

/**
 * Created by gbg on 2016-08-03.
 */

public class TypeDef extends Application {

    /* enum Types--------------------------------- */
    /* Category Type */
    public static enum CategoryType {
        eNone,
        eFavorite,
        eLight,
        eIndoorEnv,
        eEnergy,
        eSafe
    }

    /* Card Type */
    public static enum CardType {
        eNone,

        //eLight
        eMainSwitch,
        eLightSwitch,
        eDimmerSwitch,

        //eIndoorEnv
        eBoiler,
        eAircon,
        eFan,
        eFCU,
        eCurtain,

        //eEnergy
        eSmartPlug,
        eStandbyPower,
        eSmartMeter,
        eElevator, //임시(가상)

        //eSafe
        eGasSensor,
        eMotionSensor,
        eFireSensor,
        eDoorSensor,
        eWaterSensor,
        eDetectSensor,
        eSmokeSensor,
        eAwaySensor,
    }

    /* Category Type */
    public static enum Curtain_OP {
        eOff,
        ePause,
        eOn
    }

    /* Constant define ----------------------------- */

    /* Compile Option */
    //FCU option
    public static int DEF_FCU_MODE = 0; //(0: No FAN, 1: with FAN, 2: with FAN Auto , 3: FCU FUll 기능)

    /* 디바이스 삭제 기능 사용 여부 */
    public static boolean OPTION_DELETE_MODE = false; //디바이스 삭제 모드 사용 여부 (default : flase)
    /* 네비게이션 바 컴파일 옵션*/
    public static boolean OPTION_NAVIGATION_IPHomeIoT = false;
    public static boolean OPTION_NAVIGATION_LOTTE = false;

    /* Thread period */
//    public static final int DEVICE_SETUP_TIME_MS = 500; //need to adjust
//    public static final int REFRESH_POLLING_PERIOD_MS = 30;  //need to adjust
    public static final int PRIORITY_HIGH = 0;
    public static final int PRIORITY_MID = 1;
    public static final int PRIORITY_LOW = 2;
    public static final int DEVICE_LONGPRESS_TIME_MS = 600; //long press timeout
    public static final int DEVICE_SETUP_TIME_MS[] = {200,300,500}; //Priority 구분(상,중,하) : 제어 후 대기시간
    public static final int REFRESH_POLLING_PERIOD_MS[] = {30,250,1000};  //Priority 구분(상,중,하) : 화면 업데이트 반응시간


    /* root Device name */
    public static final String ROOT_DEVICE_SWITCH = "switch";
    public static final String ROOT_DEVICE_DIMMER = "dimmer";
    public static final String ROOT_DEVICE_THERMOSTAT = "thermostat";
    public static final String ROOT_DEVICE_PLUG = "plug";
    public static final String ROOT_DEVICE_METER = "meter";
    public static final String ROOT_DEVICE_LOCK = "lock";
    public static final String ROOT_DEVICE_DSENSOR = "detectSensors";

    /* commax Device name */
    public static final String COMMAX_DEVICE_LIGHT = "light";
    public static final String COMMAX_DEVICE_MAINLIGHT = "mainSwitch";
    public static final String COMMAX_DEVICE_BOILER = "boiler";
    public static final String COMMAX_DEVICE_AIRCON = "aircon";
    public static final String COMMAX_DEVICE_FAN = "fanSystem";
    public static final String COMMAX_DEVICE_FCU = "fcu";
    public static final String COMMAX_DEVICE_PLUG = "smartPlug";
    public static final String COMMAX_DEVICE_CURTAIN = "curtain";
    public static final String COMMAX_DEVICE_STANDBYPOWER = "standbyPowerSwitch";
    public static final String COMMAX_DEVICE_METER = "smartMetering";
    public static final String COMMAX_DEVICE_AWAYSWITCH = "awaySwitch";
    public static final String COMMAX_DEVICE_GASLOCK = "gasLock";
    public static final String COMMAX_DEVICE_GASSENSOR = "gasSensor";
    public static final String COMMAX_DEVICE_MOTIONSENSOR = "motionSensor";
    public static final String COMMAX_DEVICE_FIRESENSOR = "fireSensor";
    public static final String COMMAX_DEVICE_DOORSENSOR = "doorSensor";
    public static final String COMMAX_DEVICE_WATERSENSOR = "waterSensor";
    public static final String COMMAX_DEVICE_CONTACTSENSOR = "contactSensor";
    public static final String COMMAX_DEVICE_DETECTSENSOR = "detectSensors";
    public static final String COMMAX_DEVICE_SMOKESENSOR = "smokeSensor";

    /* subDevice sort name */
    public static final String SUB_DEVICE_SWITCHBINARY= "switchBinary";
    public static final String SUB_DEVICE_SWITCHDIMMER= "switchDimmer";
    public static final String SUB_DEVICE_AIRTEMP= "airTemperature";
    public static final String SUB_DEVICE_THERMOSTATMODE= "thermostatMode";
    public static final String SUB_DEVICE_THERMOSTATRUNMODE= "thermostatRunMode";
    public static final String SUB_DEVICE_THERMOSTATSETPOINT= "thermostatSetPoint";
    public static final String SUB_DEVICE_THERMOSTATSETCOLLINGPOINT= "thermostatSetCoolingpoint";
    public static final String SUB_DEVICE_THERMOSTATSETHEATINGPOINT= "thermostatSetHeatingpoint";
    public static final String SUB_DEVICE_FANSPEED = "fanSpeed";
    public static final String SUB_DEVICE_THERMOSTATAWAYMODE = "thermostatAwayMode";
    public static final String SUB_DEVICE_ELECTRICMETER= "electricMeter";
    public static final String SUB_DEVICE_METERSETTING= "metersetting";
    public static final String SUB_DEVICE_MODEBINARY= "modeBinary";
    public static final String SUB_DEVICE_WATERMETER= "waterMeter";
    public static final String SUB_DEVICE_GASMETER= "gasMeter";
    public static final String SUB_DEVICE_WARMMETER= "warmMeter";
    public static final String SUB_DEVICE_HEATMETER= "heatMeter";
    public static final String SUB_DEVICE_GASLOCK= "gasLock";
    public static final String SUB_DEVICE_GASSENSOR= "gas";
    public static final String SUB_DEVICE_MOTIONSENSOR = "motion";
    public static final String SUB_DEVICE_FIRESENSOR = "fire";
    public static final String SUB_DEVICE_DOORSENSOR = "door";
    public static final String SUB_DEVICE_WATERSENSOR = "water";
    public static final String SUB_DEVICE_DETECTSENSOR = "generalPurpose";
    public static final String SUB_DEVICE_SMOKESENSOR = "smoke";

    /* Device status */
    public static final String SWITCH_ON= "on";
    public static final String SWITCH_OFF= "off";
    public static final String SWITCH_OFF_CAPITAL= "OFF";
    public static final String AWAY_ON = "awayOn";
    public static final String AWAY_OFF = "awayOff";
    public static final String SWITCH_STOP= "stop";
    public static final String SWITCH_LOCK= "lock";
    public static final String SWITCH_UNLOCK= "unlock";
    public static final String AIRCON_MODE_COOL= "cool";
    public static final String AIRCON_MODE_DRY= "dry";
    public static final String AIRCON_MODE_FAN= "fan";
    public static final String FCU_MODE_COOL= "cool";
    public static final String FCU_MODE_HEAT= "heat";
    public static final String OP_MODE_VENTILATION= "ventilation";
    public static final String OP_MODE_AUTO= "auto";
    public static final String OP_MODE_MANUAL= "manual";
    public static final String DETECT_TRUE= "detected";
    public static final String DETECT_FALSE= "undetected";
    public static final String STATUS_UNKNOWN= "unknown";

    /*Device NickName List*/
    public static final String NickName = "nickname";
    public static final String Connection_Guide = "Guide";

    public static final String SYSTEM_KEY_SHOW_ACTION		= "com.commax.systemkey.SHOW_ACTION";	/* System Key Show Action */
    public static final String SYSTEM_KEY_HIDE_ACTION		= "com.commax.systemkey.HIDE_ACTION";	/* System Key Hide Action */


    public static final String CONNECT_DEVICE_DETAIL_MODE = "more_device_guide";
    public static final String CONNECT_DEVICE_LIST_MODE = "device_connect_list";
    /* Class End ----------------------------- */
}