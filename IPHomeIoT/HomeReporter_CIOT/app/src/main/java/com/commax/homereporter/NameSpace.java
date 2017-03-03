package com.commax.homereporter;

public class NameSpace {

    /* User Agent Type */

    static final String VISITOR_IMAGE_FOLDER				= "/sdcard/cmx_data/visitor/";			/* Visitor Capture Image Folder */
    static final String EXTRA_VISITOR_IMAGE_FOLDER			= "/mnt/extsd/cmx_data/visitor/";		/* Extra Visitor Capture Image Folder */

    static final String VISITOR_COUNT_FOLDER				= "/sdcard/cmx_data/NewVisitorImage";			/* Visitor Capture Image Folder */

    static final String CLOUD_SERVER_INFO_FILE				= "/user/app/bin/cloud_svr.i";			/* Cloud Server Information File */
    static final String SECTION_CLOUD_SERVER_INFO			= "[CLOUD_SERVER_INFO]";
    static final String KEY_CLOUD_SERVER_DNS				= "Server_DNS";		/* Cloud Server DNS */
    static final String KEY_PUBLIC_SERVER_DNS				= "Public_DNS";		/* Public Server DNS */
    static final String KEY_LOCAL_SERVER_PORT				= "Local_Port";		/* Local Server Port */
    static final String NETWORK_CHECK_PATH                  = "/networkState.i";
    static final String NETWORK_STATE_KEY                   = "networkState";

    static final String WOEID_ACTION = "com.commax.com.settingwoeid.WOEID_SAVED_ACTION";
    static final String TM_ACTION = "com.commax.com.settingwoeid.TM_SAVED_ACTION";
    static final String AREA_ACTION = "com.commax.com.settingwoeid.AREA_SAVED_ACTION";
    static final String SCREEN_OFF_ACTION = "android.intent.action.SCREEN_OFF";

    static final String INFO_PROPERTIES = "info2.properties";
    static final String SUPPORT_PROPERTIES = "/user/app/bin/support.properties";
    static final String PXD_CONFIG_PATH = "/user/app/bin/pxd_config.i";
    static String CONFIG_APP4_NAME = "app4_name";
    static String CONFIG_MODEL_TYPE = "model_type";
    static String MODEL_TYPE_FULL_IP = "fullip";

    static boolean TEST                                 =true;
    static int MAX_RESULT_SIZE                          =1000;
    static int MAX_NOTICE                               =3;
    static int MAX_PARKING                              =10;

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

    static final String UNIT_FINE_DUST                  ="㎍/㎥";
    static final String UNIT_OZONE                      ="ppm";

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

    static final String WOEID_SIZE                      ="woeid_size";
    static final String TM_SIZE                         ="tm_size";
    static final String AREA_SIZE                       ="area_size";

    static final String AIR_O3                          = "ovalue";
    static final String AIR_DUST                        = "pmvalue";
    static final String AIR_TOTAL                       = "khaivalue";
    static final String HEALTH_COLD                     = "cvalue";
    static final String LIFE_SENSORYTEM                 = "svalue";
    static final String LIFE_DSPLS                      = "dvalue";
    static final String LIFE_ULTRV                      = "uvalue";

    static final String MISSED_CALL                     = "missed_call";
    static final String MISSED_VISITOR                  = "missed_visitor";
    static final String MISSED_DELIVERY                 = "missed_delivery";

    static final String WEATHER_VALUE                   = "weatherValue";
    static final String WEATHER_CODE                    = "weatherCode";
    static final String AIR_VALUE                       = "airValue";
    static final String HEALTH_LIFE_VALUE               = "healthLifeValue";

    /* keys used to call control app */
    static final String KEY_TAP_ID                      = "tapid";
    static final int KEY_TAP_ENERGY                     = 4;

}
