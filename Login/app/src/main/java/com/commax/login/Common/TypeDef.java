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
    public static final String resourceNo = "resourceNo";
    public static final String create_accout = "create_account";

    public static final String map = "map";
    public static final String new_string = "new";
    public static final String SiteCode = "SiteCode";

    //read From cloud_svr.i 파일
    public static final String AuthServer_DNS ="AuthServer_DNS";
    public static final String Client_ID = "Client_ID";
    public static final String Client_Secret = "Client_Secret";
    public static final String ProductModel = "ProductModel";


    /*Spinner*/
    public static final String not= "not";



}
