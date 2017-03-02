package com.commax.login.Common;

import android.app.Application;

/**
 * Created by OWNER on 2016-10-18.
 */
public class TypeDef extends Application{
    /* 당장 눈에 보이는 것들만 일차적으로 정리 */

    //심형일 책임님 하단 key bar
    public static final String SYSTEM_KEY_SHOW_ACTION		= "com.commax.systemkey.SHOW_ACTION";	/* System Key Show Action */
    public static final String SYSTEM_KEY_HIDE_ACTION		= "com.commax.systemkey.HIDE_ACTION";	/* System Key Hide Action */

    public String UC_Group_new = "UC_Group_new";
    public String UC_User_new = "UC_User_new";

    public static final String LocalServerIP = "LocalServerIP";




    //read From cloud_svr.i 파일
    public static final String AuthServer_DNS ="AuthServer_DNS";
    public static final String Client_ID = "Client_ID";
    public static final String Client_Secret = "Client_Secret";
    public static final String ProductModel = "ProductModel";

    /* CreateAccount porperties , sharedpreference */
    public static final String create_account = "create_account";
    public static final String id = "id";
    public static final String password = "password";
    public static final String resourceNo = "resourceNo";
    public static final String mac_address = "mac_address";
    public static final String workType = "workType";

    public static final String access = "access";
    public static final String ews = "ews";
    public static final String ns = "ns";
    public static final String turn = "turn";
    public static final String stun = "stun";

    public static final String access_ssl = "access_ssl";
    public static final String access_ip = "access_ip";
    public static final String access_port = "access_port";
    public static final String token = "token";

    public static final String SiteCode = "SiteCode";
    public static final String map = "map";
    public static final String new_string = "new";
    public static final String resourceNo_send = "resourceNo_send";
    public static final String Dong = "Dong";
    public static final String Ho = "Ho";

    public static final String UC_Group_Register = "UC_Group_Register";
    public static final String UC_User_Register = "UC_User_Register";

    /*write String*/
    public static final String yes = "yes";
    public static final String No = "No";

    /*Spinner*/
    public static final String not= "not";

    /*Activity Name*/
    public static final String MainActivity = "MainActivity";
    public static final String SubActivity = "SubActivity";

    /* token 갱신 카운트*/
    public static int try_count_token_access = 0;
    public static int try_count_token_initial = 0;
    /*UC 그룹 , 사용자 등록 갱신 카운트*/
    public static int access_try_count = 0;
    public static int uc_group_try_count =0;
    public static int uc_user_try_count =0;

    public static boolean access_uc_register = false;

    /* Strings */
    public static final String fasle = "false";

    /* one button dialog type */
    public static final String network_dialog = "network_dialog";
    public static final String ux_dialog = "ux_dialog";
    public static final String uc_dialog = "uc_dialog";

    //-------------------------------------------컴파일 옵션  -------------------

    /* Navigation Bar */
    public static  boolean Lotte_navigation = false;
    public static  boolean IP_Home_IoT_navigation = false;

    /*로컬 서버 연동 */
    public static  boolean Connect_Local_Server = false;

    /* network.i 파일에서 값 연동*/
    public static boolean Network_i_status = false;

    /* 경비실기 UC 사용자 , 그룹 등록 */
    public static boolean UC_Guard_register = false;

    /* 70ux 컴파일 버전 넥셀 , A20 분리됨 */
    public static boolean nexel_activity_70UX = false;
    public static boolean a20_activity = false;
    public static boolean compile_70UX  = false;
    public static boolean ip_home_iot_new_ui = false;

}
