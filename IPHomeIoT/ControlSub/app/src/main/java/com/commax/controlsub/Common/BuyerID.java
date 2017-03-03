package com.commax.controlsub.Common;

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
    public static final int BUYER_ID_IP_HOME_IOT             = 3;   //IP Home IoT

    //TODO : 여기에 바이어 또는 현장명을 추가하세요~
    //TODO : 개발이력은 VersionInfo 를 참조하세요.


    /* Select Buyer ID */
//    public static final int BUYER_ID_DEF = BUYER_ID_LOTTEWORLDTOWER;
//    public static final int BUYER_ID_DEF = BUYER_ID_CANADA_TRIDEL;
//    public static final int BUYER_ID_DEF = BUYER_ID_DEVELOPVERSION;  //test only
    public static final int BUYER_ID_DEF = BUYER_ID_IP_HOME_IOT;



    public BuyerID() {

        //Initialize Compile Option according to the Buyer ID : TypeDef 참조
        switch(BUYER_ID_DEF)
        {
            case BUYER_ID_CANADA_TRIDEL: //캐나다

                Log.d(TAG, ">> BuyerID.... BUYER_ID_CANADA_TRIDEL");

                /* Compile Option */
                TypeDef.DEF_FCU_MODE = 1; //(0:FCU No FAN, 1: with FAN, 2: with FAN Auto)
                TypeDef.OPTION_DELETE_MODE = false; // 디바이스 삭제 모드 사용 (default : false)
                break;

            case BUYER_ID_LOTTEWORLDTOWER: //롯데월드타워용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_LOTTEWORLDTOWER");

                /* Compile Option */
                TypeDef.DEF_FCU_MODE = 0; //(0: No FAN, 1: with FAN, 2: with FAN Auto)
                TypeDef.OPTION_DELETE_MODE = false; // 디바이스 삭제 모드 사용 (default : false)
                TypeDef.OPTION_NAVIGATION_LOTTE = true;
                break;

            case BUYER_ID_IP_HOME_IOT:
                Log.d(TAG,">> BuyerID.... BUYER_ID_IPHomeIoT");
                /* Compile Option */
                TypeDef.DEF_FCU_MODE = 3; //(0: No FAN, 1: with FAN, 2: with FAN Auto , 3: FCU full 기능)
                TypeDef.OPTION_DELETE_MODE = true; // 디바이스 삭제 모드 사용 (default : false)
                TypeDef.OPTION_NAVIGATION_IPHomeIoT = true;


            case BUYER_ID_DEVELOPVERSION: //개발용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_DEVELOPVERSION");

                /* Compile Option */
                TypeDef.DEF_FCU_MODE = 1; //(0: No FAN, 1: with FAN, 2: with FAN Auto)
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
