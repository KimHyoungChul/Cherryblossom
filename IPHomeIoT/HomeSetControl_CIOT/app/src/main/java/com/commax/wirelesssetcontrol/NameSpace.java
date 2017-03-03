package com.commax.wirelesssetcontrol;

public class NameSpace {

    /* path for location, room, mode name */
    static String LOCATION_PREFERENCE                   = "XYLocation";
    static String WIDGET_LOCATION_PREFERENCE            = "WidgetLocation";
    //public static String ROOM_PREFERENCE                = "RoomInfo";
    static String TIP_PREFERENCE                        = "Tips";
    static String WIDGET_PREFERENCE                     = "WidgetInfo";
    static String NOT_SHOW_TIP                          = "no_more_tip";
    static String ACCOUNT_TIP                           = "account_tip";
    static String ROOM_PREFERENCE_PATH                  = "/data/data/com.commax.wirelesssetcontrol/shared_prefs/RoomInfo.xml";
    static String PXD_CONFIG_PATH                       = "/user/app/bin/pxd_config.i";
    static String NAME_MODE_PATH                        = "/sdcard/modeNickName.i";
    static String NETWORK_CHECK_PATH                    = "/networkState.i";
    static String NETWORK_STATE_KEY                     = "networkState";
    static String ACCOUNT_STATE_KEY                     = "create_account";
    static String DOOR_STATE_KEY                        = "EXTRA_DOOR_CAMERA_STATE";
    static String EXTRA_ADD_STRING                      = "EXTRA_ADD_STRING";
    static String EXTRA_ADD_CANCEL                      = "ADD_CANCEL";
    static int REQUEST_ADD_DEVICE                       = 2046;
    static int REQUEST_ADD_WIDGET                       = 2047;
    public static int APPWIDGET_HOST_ID                 = 2024;
    public static String AREA_MAIN                      = "AREA_MAIN";
    public static String AREA_MIRROR                    = "AREA_MIRROR";
    static String CONFIG_MODEL_TYPE = "model_type";
    static String MODEL_TYPE_FULL_IP = "fullip";
    static String MODEL_TYPE_A20 = "A20";

    static final int REQUEST_CREATE_APPWIDGET           = 5;
    static final int REQUEST_PICK_ICON                  = 9;
    static final int REQUEST_BIND_APPWIDGET             = 11;

    /* keys for handler or intent */
    public static String NUM_PAGES                             = "NUM_PAGES";
    public static String ROOT_UUID                             = "RootUuid";
    public static String STATUS                                = "status";
    public static String SORT                                  = "sort";
    public static String SUB_DEVICE                            = "subDevice";

    /* mac delay setting */
    public static int MAX_PAGE                                 = 12;
    public static int MAC_DELAY_UNIT                           = 2000;
    public static int REPEAT_MIN                               = 60;

    public static int INTRO_TIME                               = 120000;
    public static int END_TERM                                 = 1000;

    /* Space Activity mode */
    public static String ACTIVITY_MODE                         = "ACTIVITY_MODE";
    public static String EDIT_MODE                             = "EDIT";
    public static String ADD_MODE                              = "ADD";
    public static String ADD_NO_DEVICE_MODE                    = "ADD_NO_DEVICE";

    /* to launch sub control app */
    static String MORE_COMMAX_DEVICE                    = "more_commax_device";
    static String MODE_KEY                              = "modeNickNameStr";

    /* broadcast actions */
    public static String PAM_ACTION                            = "com.commax.services.AdapterService.PAM_ACTION";   /* PAM을 통해 report 받음(add, remove, update) */
    public static final String NAME_ACTION                     = "com.commax.gateway.service.HomeIoT.NickName_ACTION";
    public static final String NAME_ACTION_CONTROL             = "com.commax.controlsub.NickName_ACTION";
    static final String MODE_NAME_ACTION                = "com.commax.homeiot.mode.run.NickName_ACTION";
    static final String WOEID_ACTION                    = "com.commax.com.settingwoeid.WOEID_SAVED_ACTION";
    static final String NETWORK_ACTION                  = "com.commax.homeiot.service.NetworkSate_ACTION";
    static final String LANGUAGE_ACTION                 = "android.intent.action.LOCALE_CHANGED";
    static final String SERVICE_COMPLETE_ACTION         = "com.commax.iphomiot.servicewatcher";
    static final String ACCOUNT_INITIALIZE_ACTION       = "com.commax.login.Create_account_ACTION";
    static final String DOOR_STATE_ACTION               = "com.commax.commaxwidget.ACTION_CHANGED_DOOR_CAMERA_STATE";

    /* app config keys */
    static String CONFIG_LOGO_KEY                       = "logo";
    static String CONFIG_SUPPORT_INFO_KEY               = "support_info";
    static String CONFIG_SUPPORT_SPACE_KEY              = "support_space";
    static String CONFIG_DEVICE_DOOR                    = "device_door";
    static String CONFIG_APP1                           = "app1";
    static String CONFIG_APP4_NAME                      = "app4_name";
    static String CONFIG_SUPPORT_QUICK                  = "support_quick";
    static String CONFIG_SUPPORT_CONTROL                = "support_control";

    /* cloud server ip info */
    static final String CLOUD_SERVER_INFO_FILE			= "/user/app/bin/cloud_svr.i";			/* Cloud Server Information File */
    static final String KEY_PUBLIC_SERVER_DNS			= "Public_DNS";		/* Public Server DNS */
    static final String KEY_LOCAL_SERVER_PORT			= "Local_Port";		/* Local Server Port */

    /* cloud server info keys */
    static final String AIR                             ="air";
    static final String WEATHER                         ="weather";
    static final String HEALTH                          ="area";
    static final String LIFE                            ="area";
    static final String AREACODE                        ="areaCode";
    static final String CONDITION                       ="condition";
    static final String FORECAST                        ="forecast";
    static final String INDEX                           ="index";
    static final String COLD                            ="cold";
    static final String YELLOWSAND                      ="yellowSand";
    static final String TMAREA                          ="tmArea";
    static final String VALUE                           ="value";
    static final String TMX                             ="tmX";
    static final String TMY                             ="tmY";

    static final String LOCATIONS                       ="locations";
    static final String LOCATION                        ="location";
    static final String COUNTY                          ="county";
    static final String ADMIN1                          ="admin1";
    static final String ADMIN2                          ="admin2";
    static final String LOCALITY                        ="locality";
    static final String CODE                            ="code";
    static final String NAME                            ="name";
    static final String WOEID                           ="woeid";
    static final String TMXTMY                          ="tmXtmY";
    static final String TRANSVERSEMERCATORS             ="transverseMercators";
    static final String TRANSVERSEMERCATOR              ="transverseMercator";
    static final String STATES                          ="states";
    static final String STATE                           ="state";
    static final String AREACODE2                       ="areaCode2";
    static final String CITIES                          ="cities";
    static final String CITY                            ="city";
    static final String AREACODE3                       ="areaCode3";
    static final String AREA                            ="area";
    static final String AREAS                           ="areas";
    static final String ADMINSTRATIVE                   ="adminstrative";
    static final String SIDONM                          ="sidoNm";
    static final String SGGNM                           ="sggNm";
    static final String UMDNM                           ="umdNm";
    static final String LEGALNM                         ="legalNm";

    /* to save selected info */
    static final String INFO_PROPERTIES                 = "info2.properties";

    static final String INFO_WEATHER                    ="1";
    static final String INFO_AIR                        ="2";
    static final String INFO_HEALTH_LIFE                ="3";
    static final String INFO_YELLOW                     ="4";
    static final String INFO_MONTH_EMS                  ="5";
    static final String INFO_NOTICE                     ="6";
    static final String INFO_DELIVERY                   ="7";
    static final String INFO_VISITOR                    ="8";
    static final String INFO_DEVICE                     ="9";
    static final String INFO_TODAY_EMS                  ="11";
    static final String INFO_PARKING                    ="12";
    static final String INFO_INDOOR_TEMP                ="13";
    static final String INFO_INDOOR_HUMID               ="14";
    static final String INFO_INDOOR_AIR                 ="15";
    static final String INFO_SUPPORT                    ="16";
    static final String INFO_SMART_PLUG                 ="17";
    static final String INFO_SORT                       ="info_sort";
    static final String INFO_DEFAULT                    ="99";

    /* right info list sort index */
    static final String SORT_WEATHER                    ="01";
    static final String SORT_INDOOR_TEMP                ="02";
    static final String SORT_INDOOR_HUMID               ="03";
    static final String SORT_NOTICE                     ="04";
    static final String SORT_SMART_PLUG                 ="05";

    /* Quick Menu */
    static final int MAX_QUICK                          = 5;
    static final String FILE_PATH                       = "/user/app/bin";
    static final String FILE_NAME                       = "/user/app/bin/quick.i";

    public static final String HIDE_FILE                = "/user/app/bin/hide_app.i";
    public static final String SYM_AMPERSAND = "&";

    /* App launch - Door Monitoring */
    static final String BROADCAST_DOOR_MONITOR          = "com.commax.app.DOOR_MONITOR"; // 현관 모니터링 서비스를 호출하는 브로드캐스트
    static final String KEY_FROM                        = "from";                        // 현관 모니터링 서비스를 호출한 위치 구분
    static final String SET_CONTROL                     = "setControl";                  // Calling app name (checked by monitoring app)
    static final String KEY_IP                          = "ip";

    /* keys used to call control app */
    static final String KEY_TAP_ID                      = "tapid";
    static final int KEY_TAP_LIGHT                      = 2;
    static final int KEY_TAP_INDOOR                     = 3;
    static final int KEY_TAP_ENERGY                     = 4;
    static final int KEY_TAP_SAFETY                     = 5;
    static final int KEY_ADD_DEVICE                     = 11;

}
