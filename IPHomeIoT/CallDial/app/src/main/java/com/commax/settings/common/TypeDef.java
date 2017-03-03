package com.commax.settings.common;

import android.app.Application;

/**
 * Created by gbg on 2016-08-03.
 */

public class TypeDef extends Application {

    /* enum Types--------------------------------- */

    /* Constant define ----------------------------- */

    /* Compile Option */

    //Operation Option
    public static boolean OP_PASSWORD_CHECK_ENABLE          = false;  //코맥스설정 짐입시 패스워드체크 여부(default: false)
    public static boolean OP_ONVIF_DOORCAMERA_ENABLE        = false;  //도어카메라 검색방법 ONVIF 자동검색사용 여부(default: false)
    public static boolean OP_NEW_AIDL_ADAPTOR_ENABLE        = false;  //신규 AIDL Adaptor 적용 여부(default: false)
    public static boolean OP_NEWDELETE_ADAPTOR_ENABLE       = false;  //신규 Delete Adaptor 적용 여부(default: false)

    //Display Option
    public static boolean DISPLAY_LOGIN_ENABLE              = false;  //메인메뉴에 회원가입 표시여부(default: false)
    public static boolean DISPLAY_TFR_ENABLE                = false;  //메인메뉴에 TFR(가치정보인트로) 표시여부(default: false)
    public static boolean DISPLAY_DONGHO_ENABLE             = false;  //세대기설정내 동호설정 표시여부(default: false)
    public static boolean DISPLAY_TABLET_INFO_ENABLE        = false;  //세대기정보내 태블렛정보 표시여부(default: false)
    public static boolean DISPLAY_DEVICE_SERVERIP_ENABLE    = false;  //세대기설정내 디바이스서버IP 표시여부(default: false)
    public static boolean DISPLAY_SMARTPHONE_ENABLE         = false;  //통화설정내 스마트폰통화수신 표시여부(default: false)
    public static boolean DISPLAY_EXT_RINGTONE_ENABLE       = false;  //벨소리설정내 경비실,로비 벨소리설정 표시여부(default: false)



     /* String table */


    /* Class End ----------------------------- */
}