package com.commax.commaxwidget.wallpad.constans;

public class NameSpace {
    /* User Agent Type */
    public static final String VISITOR_IMAGE_FOLDER = "/sdcard/cmx_data/visitor/";			/* Visitor Capture Image Folder */
    public static final String EXTRA_VISITOR_IMAGE_FOLDER = "/mnt/extsd/cmx_data/visitor/";		/* Extra Visitor Capture Image Folder */

    public static final String VISITOR_COUNT_FOLDER = "/sdcard/cmx_data/NewVisitorImage";			/* Visitor Capture Image Folder */

    public static final String CLOUD_SERVER_INFO_FILE = "/user/app/bin/cloud_svr.i";			/* Cloud Server Information File */
    public static final String SECTION_CLOUD_SERVER_INFO = "[CLOUD_SERVER_INFO]";
    public static final String KEY_CLOUD_SERVER_DNS = "Server_DNS";		/* Cloud Server DNS */
    public static final String KEY_PUBLIC_SERVER_DNS = "Public_DNS";		/* Public Server DNS */
    public static final String KEY_LOCAL_SERVER_PORT = "Local_Port";		/* Local Server Port */
    public static final String NETWORK_CHECK_PATH = "/networkState.i";
    public static final String NETWORK_STATE_KEY = "networkState";

    public static final String WOEID_ACTION = "com.commax.com.settingwoeid.WOEID_SAVED_ACTION";
    public static final String TM_ACTION = "com.commax.com.settingwoeid.TM_SAVED_ACTION";
    public static final String AREA_ACTION = "com.commax.com.settingwoeid.AREA_SAVED_ACTION";

    public static final String CREATE_ACCOUNT_PROPERTIES = "CreateAccount.properties ";
    public static final String INFO_PROPERTIES = "info2.properties";
    public static final String SUPPORT_PROPERTIES = "/user/app/bin/support.properties";
    public static final String PXD_CONFIG_PATH = "/user/app/bin/pxd_config.i";
    public static String CONFIG_APP4_NAME = "app4_name";
    public static String CONFIG_MODEL_TYPE = "model_type";
    public static String MODEL_TYPE_FULL_IP = "fullip";

    public static boolean TEST = true;
    public static int MAX_RESULT_SIZE = 1000;
    public static int MAX_NOTICE = 3;
    public static int MAX_PARKING = 10;

    public static final String AIR = "air";
    public static final String WEATHER = "weather";
    public static final String HEALTH = "area";
    public static final String LIFE = "area";
    public static final String AREACODE = "areaCode";
    public static final String CONDITION = "condition";
    public static final String FORECAST = "forecast";
    public static final String INDEX = "index";
    public static final String COLD = "cold";
    public static final String YELLOWSAND = "yellowSand";
    public static final String TMAREA = "tmArea";
    public static final String VALUE = "value";
    public static final String TMX = "tmX";
    public static final String TMY = "tmY";

    public static final String UNIT_FINE_DUST = "㎍/㎥";
    public static final String UNIT_OZONE = "ppm";

    public static final String INFO_WEATHER = "1";
    public static final String INFO_AIR = "2";
    public static final String INFO_HEALTH_LIFE = "3";
    public static final String INFO_YELLOW = "4";
    public static final String INFO_MONTH_EMS = "5";
    public static final String INFO_NOTICE = "6";
    public static final String INFO_DELIVERY = "7";
    public static final String INFO_VISITOR = "8";
    public static final String INFO_DEVICE = "9";
    public static final String INFO_TODAY_EMS = "11";
    public static final String INFO_PARKING = "12";
    public static final String INFO_INDOOR_TEMP = "13";
    public static final String INFO_INDOOR_HUMID = "14";
    public static final String INFO_INDOOR_AIR = "15";
    public static final String INFO_SUPPORT = "16";
    public static final String INFO_SMART_PLUG = "17";
    public static final String INFO_SORT = "info_sort";

    public static final String LOCATIONS = "locations";
    public static final String LOCATION = "location";
    public static final String COUNTY = "county";
    public static final String ADMIN1 = "admin1";
    public static final String ADMIN2 = "admin2";
    public static final String LOCALITY = "locality";
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String WOEID = "woeid";
    public static final String TMXTMY = "tmXtmY";
    public static final String TRANSVERSEMERCATORS = "transverseMercators";
    public static final String TRANSVERSEMERCATOR = "transverseMercator";
    public static final String STATES = "states";
    public static final String STATE = "state";
    public static final String AREACODE2 = "areaCode2";
    public static final String CITIES = "cities";
    public static final String CITY = "city";
    public static final String AREACODE3 = "areaCode3";
    public static final String AREA = "area";
    public static final String AREAS = "areas";
    public static final String ADMINSTRATIVE = "adminstrative";
    public static final String SIDONM = "sidoNm";
    public static final String SGGNM = "sggNm";
    public static final String UMDNM = "umdNm";
    public static final String LEGALNM = "legalNm";

    public static final String WOEID_SIZE = "woeid_size";
    public static final String TM_SIZE = "tm_size";
    public static final String AREA_SIZE = "area_size";

    public static final String AIR_O3 = "ovalue";
    public static final String AIR_DUST = "pmvalue";
    public static final String AIR_TOTAL = "khaivalue";
    public static final String HEALTH_COLD = "cvalue";
    public static final String LIFE_SENSORYTEM = "svalue";
    public static final String LIFE_DSPLS = "dvalue";
    public static final String LIFE_ULTRV = "uvalue";

    public static final String MISSED_CALL = "missed_call";
    public static final String MISSED_VISITOR = "missed_visitor";
    public static final String MISSED_DELIVERY = "missed_delivery";

    public static final String WEATHER_VALUE = "weatherValue";
    public static final String WEATHER_CODE = "weatherCode";
    public static final String AIR_VALUE = "airValue";
    public static final String HEALTH_LIFE_VALUE = "healthLifeValue";

    /* keys used to call control app */
    public static final String KEY_TAP_ID = "tapid";
    public static final int KEY_TAP_ENERGY = 4;
}
