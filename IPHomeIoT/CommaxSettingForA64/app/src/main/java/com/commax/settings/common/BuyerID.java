package com.commax.settings.common;

import android.util.Log;


/**
 * Created by gbg on 2016-10-05.
 */
public class BuyerID {

    static final String TAG = "BuyerID";

    /* Buyer ID */
    public static final int BUYER_ID_DEVELOPVERSION          = 0;   //개발용
    public static final int BUYER_ID_LOTTEWORLDTOWER         = 1;   //롯데월드타워
    public static final int BUYER_ID_CANADA_TRIDEL           = 2;   //캐나다
    public static final int BUYER_ID_GENERAL_CIOT            = 3;   //CIOT-700M 싱글형 모델
    public static final int BUYER_ID_GENERAL_CIOT_SYSTEM    = 4;   //CIOT-700M 시스템형 모델

    //TODO : 여기에 바이어 또는 현장명을 추가하세요~
    //TODO : 개발이력은 VersionInfo 를 참조하세요.



    /* Select Buyer ID */
    //public static final int BUYER_ID_DEF = BUYER_ID_DEVELOPVERSION; //test only
    //public static final int BUYER_ID_DEF = BUYER_ID_LOTTEWORLDTOWER;
    //public static final int BUYER_ID_DEF = BUYER_ID_CANADA_TRIDEL;
    //public static final int BUYER_ID_DEF = BUYER_ID_GENERAL_CIOT;
    public static final int BUYER_ID_DEF = BUYER_ID_GENERAL_CIOT_SYSTEM;


    public BuyerID() {

        //Initialize Compile Option according to the Buyer ID : TypeDef 참조
        switch(BUYER_ID_DEF)
        {
            case BUYER_ID_GENERAL_CIOT_SYSTEM: //CIOT-700 시스템형 모델

                Log.d(TAG, ">> BuyerID.... BUYER_ID_GENERAL_CIOT");

                /* Compile Option */
                TypeDef.OP_FULLSCREEN_TYPE_ENABLE         = false;  //코맥스설정 FullScreen Type 사용여부(default: false):신규 PXD
                TypeDef.OP_PASSWORD_CHECK_ENABLE          = false;  //코맥스설정 짐입시 패스워드체크 여부(default: false)
                TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE       = false;  //도어카메라 수동검색모드 사용여부(default: false)
                TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE       = true;   //신규 Delete Adaptor 적용 여부(default: false):신규 PXD
                TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE       = true;   //현관,CCTV 편집 및 삭제 분리메뉴 사용여부(default: false):신규 PXD
                TypeDef.OP_NEW_CCTV_SCANMODE_ENABLE       = true;   //CCTV Scan방법을 현관과 동일하게 사용여부(default: false):신규 PXD

//개발중 테스트
//TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE       = true;
TypeDef.OP_FULLSCREEN_TYPE_ENABLE         = true;

//NEW_AIDL 테스트
//DISPLAY_DEVICE_SERVERIP_ENABLE = true;
//OP_NEW_AIDL_ADAPTOR_ENABLE     = true;

                 /* Display Option */
                TypeDef.DISPLAY_LOGIN_ENABLE              = false;  //메인메뉴에 회원가입 표시여부(default: false)
                TypeDef.DISPLAY_DONGHO_ENABLE             = true;  //세대기설정내 동호설정 표시여부(default: false)
                TypeDef.DISPLAY_GUARD_ENABLE              = true;  //세대기설정내 경비실번호설정 표시여부(default: false)
                TypeDef.DISPLAY_CENTER_ENABLE             = true;  //세대기설정내 관리실번호설정 표시여부(default: false)
                TypeDef.DISPLAY_DEVICE_SERVERIP_ENABLE    = true;  //세대기설정내 디바이스서버IP 표시여부(default: false)
                TypeDef.DISPLAY_UPDATE_SERVERIP_ENABLE    = true;  //세대기설정내 업데이트서버IP 표시여부(default: false)
                TypeDef.DISPLAY_LOCAL_SERVERIP_ENABLE     = true;  //세대기설정내 단지서버IP 표시여부(default: false)
                TypeDef.DISPLAY_SMARTPHONE_ENABLE         = true;   //통화설정내 스마트폰통화수신 표시여부(default: false)
                TypeDef.DISPLAY_HOMESCREEN_TIP_ENABLE     = true;   //홈테마설정내 홈스크린도움말 표시여부(default: false)
                TypeDef.DISPLAY_EXT_RINGTONE_ENABLE       = true;   //벨소리설정내 경비실,로비 벨소리설정 표시여부(default: false)
                TypeDef.DISPLAY_TFR_ENABLE                = false;  //메인메뉴에 TFR(가치정보인트로) 표시여부(default: false)
                TypeDef.DISPLAY_TABLET_INFO_ENABLE        = false;  //세대기정보내 태블렛정보 표시여부(default: false)
                TypeDef.DISPLAY_UPDATE_INFO_ENABLE        = true;   //세대기정보내 업데이트확인 표시여부(default: false)

                break;

            case BUYER_ID_GENERAL_CIOT: //CIOT-700 싱글형 모델

                Log.d(TAG, ">> BuyerID.... BUYER_ID_GENERAL_CIOT");

                /* Compile Option */
                TypeDef.OP_FULLSCREEN_TYPE_ENABLE         = false;  //코맥스설정 FullScreen Type 사용여부(default: false):신규 PXD
                TypeDef.OP_PASSWORD_CHECK_ENABLE          = false;  //코맥스설정 짐입시 패스워드체크 여부(default: false)
                TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE       = false;  //도어카메라 수동검색모드 사용여부(default: false)
                TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE       = true;   //신규 Delete Adaptor 적용 여부(default: false):신규 PXD
                TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE       = false;  //현관,CCTV 편집 및 삭제 분리메뉴 사용여부(default: false):신규 PXD
                TypeDef.OP_NEW_CCTV_SCANMODE_ENABLE       = false;  //CCTV Scan방법을 현관과 동일하게 사용여부(default: false):신규 PXD

//개발중 테스트
//TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE       = true;
//TypeDef.OP_FULLSCREEN_TYPE_ENABLE         = true;

//NEW_AIDL 테스트
//DISPLAY_DEVICE_SERVERIP_ENABLE = true;
//OP_NEW_AIDL_ADAPTOR_ENABLE     = true;

                 /* Display Option */
                TypeDef.DISPLAY_LOGIN_ENABLE              = false;  //메인메뉴에 회원가입 표시여부(default: false)
                TypeDef.DISPLAY_DONGHO_ENABLE             = false;  //세대기설정내 동호설정 표시여부(default: false)
                TypeDef.DISPLAY_DEVICE_SERVERIP_ENABLE    = false;  //세대기설정내 디바이스서버IP 표시여부(default: false)
                TypeDef.DISPLAY_UPDATE_SERVERIP_ENABLE    = false;  //세대기설정내 업데이트서버IP 표시여부(default: false)
                TypeDef.DISPLAY_SMARTPHONE_ENABLE         = true;   //통화설정내 스마트폰통화수신 표시여부(default: false)
                TypeDef.DISPLAY_HOMESCREEN_TIP_ENABLE     = false;  //홈테마설정내 홈스크린도움말 표시여부(default: false)
                TypeDef.DISPLAY_EXT_RINGTONE_ENABLE       = false;  //벨소리설정내 경비실,로비 벨소리설정 표시여부(default: false)
                TypeDef.DISPLAY_TFR_ENABLE                = false;  //메인메뉴에 TFR(가치정보인트로) 표시여부(default: false)
                TypeDef.DISPLAY_TABLET_INFO_ENABLE        = false;  //세대기정보내 태블렛정보 표시여부(default: false)
                TypeDef.DISPLAY_UPDATE_INFO_ENABLE        = false;  //세대기정보내 업데이트확인 표시여부(default: false)

                break;

            case BUYER_ID_CANADA_TRIDEL: //캐나다

                Log.d(TAG, ">> BuyerID.... BUYER_ID_CANADA_TRIDEL");

                /* Compile Option */
                break;

            case BUYER_ID_LOTTEWORLDTOWER: //롯데월드타워용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_LOTTEWORLDTOWER");

                /* Compile Option */
                break;

            case BUYER_ID_DEVELOPVERSION: //개발용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_DEVELOPVERSION");

                /* Compile Option */
                break;

            default:
                Log.d(TAG, ">> BuyerID.... ERROR!!!");
                break;
        }
    }

    public int getBuyerID() {
        return this.BUYER_ID_DEF;
    }
}
