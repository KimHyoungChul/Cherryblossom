package com.commax.control.Common;

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

    /* Card Type  : commaxDevice */
    public static enum CardType {
        eNone,

        //eLight
        eMainSwitch,
        eLightSwitch,
        eDimmerSwitch,

        //eIndoorEnv
        eThermostat,
        eBoiler,
        eAircon,
        eFan,
        eFCU,
        eCurtain,
        eWaterSensor,
        eDetectSensor, //multi sensor , detect sensor(접점센서-safety)

        //eEnergy
        eSmartPlug,
        eStandbyPower,
        eSmartMeter,
        eElevator, //임시(가상)

        //eSafe
        eGasLock,
        eGasSensor,
        eMotionSensor,
        eFireSensor,
        eDoorLock,
        eDoorSensor,
        eSmokeSensor,
        eAwaySensor,
    }

    /* Group ID */
    public static enum GroupID {
        eUnknown("0000"),

        //eLight
        eLight("1000"),
        eMainSwitch("1001"),
        eLightSwitch("1002"),
        eDimmerSwitch("1003"),

        //eIndoorEnv
        eThermostat("2000"),
        eBoiler("2001"),
        eAircon("2002"),
        eFan("2003"),
        eFCU("2004"),
        eCurtain("2005"),

        //eEnergy
        eEnergy("3000"),
        eSmartPlug("3001"),
        eStandbyPower("3002"),
        eSmartMetering("3003"),

        //eSafe
        eSafe("4000");

        private final String value;
        GroupID(String value) {this.value = value;}
        public String value() {return value;}
    }

    /* Fake Device Enable Flag : 0-Disable, 1-Enable */
    public static enum FAKE_DEVICE {

        //eLight
        eMainSwitch(true),
        eLightSwitch(true),
        eDimmerSwitch(true),

        //eIndoorEnv
        eBoiler(true),
        eAircon(true),
        eFan(true),
        eFCU(true),
        eCurtain(true),

        //eEnergy
        eSmartPlug(true),
        eStandbyPower(true),
        eSmartMetering(true),

        //eSafe
        eGasLock(true),
        eDoorLock(true),
        eDetectSensor(true),

        eUnknown(false);

        private final boolean value;
        FAKE_DEVICE(boolean value) {this.value = value;}
        public boolean value() {return value;}
    }

    /* Curtain_OP Type */
    public static enum Curtain_OP {
        eOff,
        ePause,
        eOn
    }

    /* Curtain_OP Type */
    public static enum Fan_OP {
        eAuto,
        eManual
    }

    /* Constant define ----------------------------- */

    /* Compile Option */
    public static boolean APP_ICON_ENABLE           = true;  //App Icon 표시여부(default:true) -> 사용안함
    public static boolean FAKE_DATA_ENABLE          = false; //테스트용 FAKE 디바이스 추가함(default: false) -> Test Only
    public static boolean APP_INCLUDE_MORE          = false; //More를 제어앱에 포함함(default: false) -> 테스트중(사용금지)


    /* Category Enable Option */
    public static boolean TAB_SAFE_ENABLE           = true; //eSafe 포함여부(default:true)
    public static boolean MENU_ADD_ENABLE           = true; //add버튼 포함여부(default:true)
    public static boolean TAB_DYNAMIC_MODE_ENABLE   = false; //dynamic Tab menu(default:false)
    public static boolean GUIDE_BUTTON_ENABLE       = false;  // 기기 연결 다이드 여부 (default : false)

    /* Group Device Enable */
    public static boolean GROUP_LIGHT_ENABLE        = true; //eLight(default:true)
    public static boolean GROUP_BOILER_ENABLE       = true; //eBoiler(default:true)
    public static boolean GROUP_AIRCON_ENABLE       = true; //eAircon(default:true)
    public static boolean GROUP_FCU_ENABLE          = true; //eFCU(default:true)
    public static boolean GROUP_FAN_ENABLE          = true; //eFan(default:true)
    public static boolean GROUP_CURTAIN_ENABLE      = true; //eCurtain(default:true)
    public static boolean GROUP_STANDBYPOWER_ENABLE = true; //eStandbyPower(default:true)

    /* List Operation Option */
    public static boolean OPT_SIMPLE_STANDBYPOWER_ENABLE = false; //simple mode standbypower 모드 여부 (default:false)
    public static boolean OPT_READONLY_DOORLOCK_ENABLE = false; //readonly doorlock 모드 여부 (default:false)
    public static boolean OPT_RETRY_CONTROL_ENABLE = true; //Group Retry Control 여부 (default:true)
    public static boolean OPT_SEEKBAR_TOUCH_ENABLE = false; //Seekbar Touch Control 여부(default:true)


    /* Navigation bar Compile Option */
    public static boolean IPHomeIoT_NAVIGATION_BAR = false;
    public static boolean IPHomeIOT_ACTIVITY        = false; //IPHomeIoT 릴리즈


    /* Device Information Define */
    public static final int MAX_SUBDEV_CONTROLLER = 10;  //need to adjust
    public static final int MAX_GROUP_RETRY_COUNTER = 5; //Group 제어시 재시도 회수(3초 주기로 재시도)
    public static final int DEVICE_UPDATE_INTERVAL_MS = 5000; //DB Update시 일정시간 정보를 모았다가 처리하는 시간

    /* Thread period */
    public static final int PRIORITY_HIGH = 0;
    public static final int PRIORITY_MID = 1;
    public static final int PRIORITY_LOW = 2;
    public static final int DEVICE_LONGPRESS_TIME_MS = 600; //long press timeout
    public static final int DEVICE_POSTUPDATE_TIME_MS = 3000; //post ui update timeout(3초)
    public static final int DEVICE_CONTROLTIMEOUT_TIME_MS = 10000; //device control timeout(10초)
    public static final int DEVICE_SETUP_TIME_MS[] = {200,300,500}; //Priority 구분(상,중,하) : 제어 후 대기시간
    public static final int REFRESH_POLLING_PERIOD_MS[] = {100,250,1000};  //Priority 구분(상,중,하) : 화면 업데이트 반응시간

    /* Device Control Range Define */
    public static final int DEF_DIMMER_MIN_MAX[] = {0,8};
    public static final int DEF_COOL_MIN_MAX[] = {18,30};
    public static final int DEF_HEAT_MIN_MAX[] = {5,28};

    /* TAB ID Define */
    public static final int TAB_NONE   = 0; //eNone
    public static final int TAB_FAV    = 1; //eFavorite
    public static final int TAB_LIGHR  = 2; //eLight
    public static final int TAB_INDOOR = 3; //eIndoorEnv
    public static final int TAB_ENERGY = 4; //eEnergy
    public static final int TAB_SAFE   = 5; //eSafe
    public static final int MAX_CATEGORY = 5;
    public static final int CONTROL_ADD_DIALOG = 12; //device add dialog 띄움
    public static final int ADD_DIALOG = 11; //device add dialog 띄움

    /* Virtual Device */
    public static final String VIRTUAL_DEVICE_ROOTUUID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    public static final String VIRTUAL_DEVICE_SUBUUID = "ffffffff-ffff-ffff-ffff-ffffffffffff";
    public static final String ROOT_DEVICE_VIRTUAL = "switch";
    public static final String COMMAX_DEVICE_VIRTUAL = "virtualSwitch";
    public static final String SUB_DEVICE_VIRTUAL = "switchBinary";

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
    public static final String COMMAX_DEVICE_DOORLOCK = "doorLock"; //임시(가상)
    public static final String COMMAX_DEVICE_DOORSENSOR = "doorSensor";
    public static final String COMMAX_DEVICE_WATERSENSOR = "waterSensor";
    public static final String COMMAX_DEVICE_CONTACTSENSOR = "contactSensor";
    public static final String COMMAX_DEVICE_DETECTSENSOR = "detectSensors";
    public static final String COMMAX_DEVICE_SMOKESENSOR = "smokeSensor";
    public static final String COMMAX_DEVICE_ELEVATOR = "elevator"; //임시(가상)
    public static final String ROOT_DEVICE_WATERSENSOR = "waterSensor";
    public static final String COMMAX_DEVICE_PREVENTSWITCH  = "preventSwitch";

    /* subDevice sort name */
    public static final String SUB_DEVICE_NONE= "none";
    public static final String SUB_DEVICE_SWITCH= "switchBinary";
    public static final String SUB_DEVICE_SWITCHDIMMER= "switchDimmer";
    public static final String SUB_DEVICE_AIRTEMP= "airTemperature";
    public static final String SUB_DEVICE_HUMIDITY= "humidity";
    public static final String SUB_DEVICE_THERMOSTATMODE= "thermostatMode";
    public static final String SUB_DEVICE_THERMOSTATAWAYMODE= "thermostatAwayMode";
    public static final String SUB_DEVICE_THERMOSTATRUNMODE= "thermostatRunMode";
    public static final String SUB_DEVICE_THERMOSTATSETPOINT= "thermostatSetpoint";
    public static final String SUB_DEVICE_THERMOSTATSETCOLLINGPOINT= "thermostatSetCoolingpoint";
    public static final String SUB_DEVICE_THERMOSTATSETHEATINGPOINT= "thermostatSetHeatingpoint";
    public static final String SUB_DEVICE_ELECTRICMETER= "electricMeter";
    public static final String SUB_DEVICE_METERSETTING= "metersetting";
    public static final String SUB_DEVICE_MODEBINARY= "modeBinary";
    public static final String SUB_DEVICE_FANSPEED= "fanSpeed";
    public static final String SUB_DEVICE_WATERMETER= "waterMeter";
    public static final String SUB_DEVICE_GASMETER= "gasMeter";
    public static final String SUB_DEVICE_WARMMETER= "warmMeter";
    public static final String SUB_DEVICE_HEATMETER= "heatMeter";
    public static final String SUB_DEVICE_GASLOCK= "gasLock";
    public static final String SUB_DEVICE_GASSENSOR= "gas";
    public static final String SUB_DEVICE_MOTIONSENSOR = "motion";
    public static final String SUB_DEVICE_FIRESENSOR = "fire";
    public static final String SUB_DEVICE_DOORLOCK = "doorLock"; //임시(가상)
    public static final String SUB_DEVICE_DOORSENSOR = "door";
    public static final String SUB_DEVICE_WATERSENSOR = "water";
    public static final String SUB_DEVICE_DETECTSENSOR = "generalPurpose";
    public static final String SUB_DEVICE_SMOKESENSOR = "smoke";
    public static final String SUB_DEVICE_ELEVATOR = "elevator"; //임시(가상)

    /* subDevice JSON key name */
    public static final String		CGP_KEY_COMMAND						= "command";
    public static final String		CGP_KEY_STATUS						= "status";
    public static final String		CGP_KEY_OBJ							= "object";
    public static final String		CGP_KEY_OBJ_R_UUID					= "rootUuid";
    public static final String		CGP_KEY_OBJ_R_NiCKUUID				= "uuid";
    public static final String		CGP_KEY_OBJ_R_DEVICE				    = "rootDevice";
    public static final String		CGP_KEY_OBJ_R_COMMAX_DEVICE			= "commaxDevice";
    public static final String		CGP_KEY_OBJ_R_VISIBLE				= "visible";
    public static final String		CGP_KEY_OBJ_R_NICKNAME				= "nickname";
    public static final String		CGP_KEY_OBJ_R_ROOM_ID				= "roomId";
    public static final String		CGP_KEY_OBJ_R_EMER_REASON			= "emergencyReason";
    public static final String		CGP_KEY_OBJ_S_DEVICE				    = "subDevice";
    public static final String		CGP_KEY_OBJ_S_DEVICE_S_UUID			= "subUuid";
    public static final String		CGP_KEY_OBJ_S_DEVICE_TYPE			= "type";
    public static final String		CGP_KEY_OBJ_S_DEVICE_SORT			= "sort";
    public static final String		CGP_KEY_OBJ_S_DEVICE_FUNC_COMMAND	= "funcCommand";
    public static final String		CGP_KEY_OBJ_S_DEVICE_VALUE			= "value";
    public static final String		CGP_KEY_OBJ_S_DEVICE_IF_RUN_VISBILE	= "ifRunvisible";
    public static final String		CGP_KEY_OBJ_S_DEVICE_SUB_OPTION		= "subOption";
    public static final String		CGP_KEY_OBJ_S_DEVICE_PRECISION		= "precision";
    public static final String		CGP_KEY_OBJ_S_DEVICE_SCALE			= "scale";
    public static final String		CGP_KEY_OBJ_S_DEVICE_OPTION_1		= "option1";
    public static final String		CGP_KEY_OBJ_S_DEVICE_OPTION_2		= "option2";
    public static final String		CGP_KEY_OBJ_S_DEVICE_OPTION_3		= "option3";
    public static final String		CGP_KEY_OBJ_S_DEVICE_OPTION_4		= "option4";
    public static final String		CGP_KEY_BATTERY_ATTRIBUTE			= "batteryAttribute";
    public static final String		CGP_KEY_BATTERY_ATTRIBUTE_LEVEL	= "batteryLevel";
    public static final String		CGP_KEY_BATTERY_ATTRIBUTE_EVENT	= "batteryEvent";
    public static final String		CGP_KEY_AWAYMODE					    = "awayMode";
    public static final String		CGP_KEY_AWAYMODE_EVENT				= "eventValue";

    /* COMMAND Set */
    public static final String		COMMAND_SEND_LIST					    = "list";
    public static final String		COMMAND_SEND_ADD					    = "add";
    public static final String		COMMAND_SEND_ADD_CANCEL				= "addCancel";
    public static final String		COMMAND_SEND_REMOVE					= "remove";
    public static final String		COMMAND_SEND_REMOVE_MODE			= "removeMode";
    public static final String		COMMAND_SEND_REMOVE_MODE_CANCEL	= "removeModeCancel";
    public static final String		COMMAND_FACTORYRESET				    = "factoryReset";
    public static final String		COMMAND_SEND_SET					    = "set";
    public static final String		COMMAND_SEND_GET					    = "get";
    public static final String		COMMAND_RESPONSE_REPORT				= "report";
    public static final String		COMMAND_RESPONSE_ADD_REPORT			= "addReport";
    public static final String		COMMAND_RESPONSE_REMOVE_REPORT		= "removeReport";
    public static final String		COMMAND_RESPONSE_FACTORY_REPORT	= "factoryReport";
    public static final String		COMMAND_RESPONSE_EMERGENCY_REPORT	= "emergencyReport";
    public static final String		COMMAND_RESPONSE_LIST_REPORT		= "listReport";
    public static final String		COMMAND_RESPONSE_NOTIFY				= "notify";
    public static final String       	COMMAND_UPDATE_PROFILE				= "updateProfile";
    public static final String		COMMAND_RESPONSE_GATEWAY			= "gateway";
    public static final String		COMMAND_RESPONSE_SCENE				= "scene";
    public static final String		COMMAND_RESPONSE_REG_ROOM			= "regRoom";
    public static final String		COMMAND_RESPONSE_REG_NICKNAME		= "regNickname";

     /* subDevice type */
    public static final String		S_DEVICE_TYPE_STRING_READ_ONLY		= "read";
    public static final String		S_DEVICE_TYPE_STRING_WRITE_ONLY	= "write";
    public static final String		S_DEVICE_TYPE_STRING_READ_WRITE	= "readWrite";

    /* Device status */
    public static final String SWITCH_ON= "on";
    public static final String SWITCH_OFF= "off";
    public static final String SWITCH_STOP= "stop";
    public static final String SWITCH_LOCK= "lock";
    public static final String SWITCH_UNLOCK= "unlock";
    public static final String SWITCH_AWAYON= "awayOn";
    public static final String SWITCH_AWAYOFF= "awayOff";
    public static final String AIRCON_MODE_COOL= "cool";
    public static final String AIRCON_MODE_DRY= "dry";
    public static final String AIRCON_MODE_FAN= "fan";
    public static final String BOILDE_MODE_HEAT= "heat";
    public static final String FCU_MODE_COOL= "cool";
    public static final String FCU_MODE_HEAT= "heat";
    public static final String FCU_MODE_VENTILATION= "ventilation";
    public static final String FAN_SPEED_LOW= "low";
    public static final String FAN_SPEED_MID= "medium";
    public static final String FAN_SPEED_HIGH= "high";
    public static final String OP_MODE_AUTO= "auto";
    public static final String OP_MODE_MANUAL= "manual";
    public static final String DETECT_TRUE= "detected";
    public static final String DETECT_FALSE= "undetected";
    public static final String BATTERY_LOW= "low";
    public static final String STATUS_UNKNOWN= "unknown";
    public static final String DETECT_DETECTED = "detected";
    public static final String DETECT_UNDETECTED = "undetected";


    /*Device NickName List*/
    public static final String NickName = "nickname";
    public static final String SYSTEM_KEY_SHOW_ACTION		= "com.commax.systemkey.SHOW_ACTION";	/* System Key Show Action */
    public static final String SYSTEM_KEY_HIDE_ACTION		= "com.commax.systemkey.HIDE_ACTION";	/* System Key Hide Action */


    /* SubControl Parameter */
    public static final int CONTROL_MSG_ID = 5000;
    public static final String MORE_ROOT_UUID = "RootUuid";
    public static final String MORE_COMMAX_DEVICE = "more_commax_device";
    public static final String EDIT_COMMAX_DEVICE = "nickname";
    public static final String CONNECTION_GUIDE_DEVICE = "guide";
    public static final String MORE_CLASS_NAME = "com.commax.controlsub";
    public static final String MORE_ACTIVITY_NAME = "com.commax.controlsub.MainActivity";
    public static final String SET_CONTROL = "SetControl";
    public static final String MAINACITIVITY = "MainActivity";
    //setcontrol 에서 디바이스 추가 다이얼로그 호출시 Interface Strings
    public static final int setcontrol_add_dialog = 11;
    public static final String EXTRA_ADD_STRING = "EXTRA_ADD_STRING";
    public static final String ADD_CANCEL = "ADD_CANCEL";

    //public static final String MORE_CLASS_NAME = "com.commax.tfr";
    //public static final String MORE_ACTIVITY_NAME = "com.commax.tfr.MainActivity";

    /* 디바이스가 갯수가 0 일때 alert Dialog */

    /* Class End ----------------------------- */
}