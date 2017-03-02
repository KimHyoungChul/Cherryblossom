package com.commax.login.Common;

import android.util.Log;

/**
 * Created by gbg on 2016-10-05.
 */
public class BuyerID {

    static final String TAG = "BuyerID";


    /* Buyer ID */
    public static final int BUYER_ID_DEVELOPVERSION          = 0;   //개발용
    public static final int BUYER_ID_LOTTEWORLDTOWER         = 1;   //롯데월드타워
    public static final int BUYER_ID_GUARD                    = 2;   //경비실기
    public static final int BUYER_ID_IP_HOME_IOT             = 3;   //IP Home IoT

    //TODO for test
    public static final int BUYER_ID_Nexel_70UX         = 4;   //롯데월드타워
    /*
    * CDV-70UX 넥셀 버전 : androidManifest 에서 android:theme="@style/Base.AlertDialog.AppCompat.Light" 로 바꾸고 android:theme="@android:style/Theme.NoTitleBar" 추가해줘야한다.
    * ClearEditText 에서 AppCompatEditText -> EditText 로 바꾸어주어야 한다.
    *
    * MaicActivity 에서 activity_register_main_optional_nexel 로바꾸어 주어야 한다.
    * Spinner 높이 주석 처리 해야한다
    * extends 넥셀 : EditText A20 : AppCompatEditText
    *
    * SubActivity 에서 activity_sub__main_nexel로 바꾸어 주어야 한다.
    *
    * build.gradle 파일 바꿈
    * */
    public static final int BUYER_ID_A20_70UX           = 5;   //캐나다

    //TODO : 여기에 바이어 또는 현장명을 추가하세요~
    //TODO : 개발이력은 VersionInfo 를 참조하세요.


    /* Select Buyer ID */
    public static final int BUYER_ID_DEF = BUYER_ID_IP_HOME_IOT;

    public BuyerID() {

        //Initialize Compile Option according to the Buyer ID : TypeDef 참조
        switch(BUYER_ID_DEF)
        {
            case BUYER_ID_GUARD: //경비실기
                /* Compile Option
                * 로컬 서버에서 사이트 코드 가져오는 거 연동
                * 하단바 제거 안함
                * */
                Log.d(TAG, ">> BuyerID.... BUYER_ID_CANADA_TRIDEL");
                TypeDef.Connect_Local_Server = false;
                TypeDef.UC_Guard_register = true;

                break;

            case BUYER_ID_LOTTEWORLDTOWER: //롯데월드타워용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_LOTTEWORLDTOWER");
                /* Compile Option : 심형일 책임님이 만든 하단 네비게이션 바 적용 */
                TypeDef.Lotte_navigation = true;
                TypeDef.Connect_Local_Server = true;

                break;

            case BUYER_ID_IP_HOME_IOT:
                Log.d(TAG,">> BuyerID.... BUYER_ID_IPHomeIoT");
                /* Compile Option  :  보슬이가 적용한 하단 네비게이션 바 Hide,  show 적용*/
                TypeDef.IP_Home_IoT_navigation = true;
                TypeDef.Connect_Local_Server = false;
                TypeDef.Network_i_status = true;
                TypeDef.ip_home_iot_new_ui = true;

                break;

            case BUYER_ID_DEVELOPVERSION: //개발용
                Log.d(TAG, ">> BuyerID.... BUYER_ID_DEVELOPVERSION");
                /* Compile Option */
                break;

            case BUYER_ID_A20_70UX:
                Log.d(TAG, ">> BuyerID.... BUYER_ID_A20_70UX");
                TypeDef.a20_activity = true;
                TypeDef.compile_70UX = false;
                TypeDef.IP_Home_IoT_navigation = false;
                TypeDef.Lotte_navigation = false;
                break;

            case BUYER_ID_Nexel_70UX:
                Log.d(TAG, ">> BuyerID.... BUYER_ID_Nexel_70UX");
                TypeDef.nexel_activity_70UX = true;
                TypeDef.compile_70UX = true;
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
