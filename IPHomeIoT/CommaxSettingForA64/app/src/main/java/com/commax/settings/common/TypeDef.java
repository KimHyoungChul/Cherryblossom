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
    public static boolean OP_FULLSCREEN_TYPE_ENABLE         = false;  //코맥스설정 FullScreen Type 사용여부(default: false):신규 PXD
    public static boolean OP_PASSWORD_CHECK_ENABLE          = false;  //코맥스설정 짐입시 패스워드체크 여부(default: false)
    public static boolean OP_CUSTOM_DOORCAMERA_ENABLE       = false;  //도어카메라 수동검색모드 사용여부(default: false)
    public static boolean OP_NEW_AIDL_ADAPTOR_ENABLE        = false;  //신규 AIDL Adaptor 적용 여부(default: false)
    public static boolean OP_NEWDELETE_ADAPTOR_ENABLE       = false;  //신규 Delete Adaptor 적용 여부(default: false):신규 PXD
    public static boolean OP_SAPERATE_EDIT_DEL_ENABLE       = false;  //현관,CCTV 편집 및 삭제 분리메뉴 사용여부(default: false):신규 PXD
    public static boolean OP_NEW_CCTV_SCANMODE_ENABLE       = false;  //CCTV Scan방법을 현관과 동일하게 변경 사용여부(default: false):신규 PXD

    //Display Option
    public static boolean DISPLAY_LOGIN_ENABLE              = false;  //메인메뉴에 회원가입 표시여부(default: false)
    public static boolean DISPLAY_DONGHO_ENABLE             = false;  //세대기설정내 동호설정 표시여부(default: false)
    public static boolean DISPLAY_GUARD_ENABLE              = false;  //세대기설정내 경비실번호설정 표시여부(default: false)
    public static boolean DISPLAY_CENTER_ENABLE             = false;  //세대기설정내 관리실번호설정 표시여부(default: false)
    public static boolean DISPLAY_DEVICE_SERVERIP_ENABLE    = false;  //세대기설정내 디바이스서버IP 표시여부(default: false)
    public static boolean DISPLAY_UPDATE_SERVERIP_ENABLE    = false;  //세대기설정내 업데이트서버IP 표시여부(default: false)
    public static boolean DISPLAY_LOCAL_SERVERIP_ENABLE     = false;  //세대기설정내 단지서버IP 표시여부(default: false)
    public static boolean DISPLAY_SMARTPHONE_ENABLE         = false;  //통화설정내 스마트폰통화수신 표시여부(default: false)
    public static boolean DISPLAY_HOMESCREEN_TIP_ENABLE     = false;  //홈테마설정내 홈스크린도움말 표시여부(default: false)
    public static boolean DISPLAY_EXT_RINGTONE_ENABLE       = false;  //벨소리설정내 경비실,로비 벨소리설정 표시여부(default: false)
    public static boolean DISPLAY_TFR_ENABLE                = false;  //메인메뉴에 TFR(가치정보인트로) 표시여부(default: false)
    public static boolean DISPLAY_TABLET_INFO_ENABLE        = false;  //세대기정보내 태블렛정보 표시여부(default: false)
    public static boolean DISPLAY_UPDATE_INFO_ENABLE        = false;  //세대기정보내 업데이트확인 표시여부(default: false)


     /* String table */


    /* Class End ----------------------------- */
}