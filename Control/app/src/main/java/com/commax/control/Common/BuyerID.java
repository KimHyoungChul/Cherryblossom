package com.commax.control.Common;

/**
 * Created by gbg on 2016-10-05.
 */
public class BuyerID {

    static final String TAG = "BuyerID";

    /* Buyer ID */
    public static final int BUYER_ID_DEVELOPVERSION          = 0;   //개발용
    public static final int BUYER_ID_LOTTEWORLDTOWER         = 1;   //롯데월드타워
    public static final int BUYER_ID_CANADA_TRIDEL           = 2;   //캐나다
    public static final int BUYER_ID_GENERAL_CIOT            = 3;   //CIOT-700 모델

    //TODO : 여기에 바이어 또는 현장명을 추가하세요~
    //TODO : 개발이력은 VersionInfo 를 참조하세요.



    /* Select Buyer ID */
    public static final int BUYER_ID_DEF = BUYER_ID_GENERAL_CIOT;
//    public static final int BUYER_ID_DEF = BUYER_ID_LOTTEWORLDTOWER;
    //public static final int BUYER_ID_DEF = BUYER_ID_CANADA_TRIDEL;
    //public static final int BUYER_ID_DEF = BUYER_ID_DEVELOPVERSION;  //test only




    public BuyerID() {

        //Initialize Compile Option according to the Buyer ID : TypeDef 참조
        switch(BUYER_ID_DEF)
        {
            case BUYER_ID_CANADA_TRIDEL: //캐나다

                Log.d(TAG, ">> BuyerID.... BUYER_ID_CANADA_TRIDEL");

                /* Compile Option */
                TypeDef.APP_ICON_ENABLE             = false;    //App Icon 표시여부(default:true)
                TypeDef.FAKE_DATA_ENABLE            = false;    //테스트용 FAKE 디바이스 추가함(default: false)

                /* Category Enable Option */
                TypeDef.TAB_SAFE_ENABLE             = true;     //eSafe 포함여부(default:true)
                TypeDef.MENU_ADD_ENABLE             = false;    //add버튼 포함여부(default:true)
                TypeDef.TAB_DYNAMIC_MODE_ENABLE     = false;    //dynamic Tab menu(default:false)

                /* Group Device Enable */
                TypeDef.GROUP_LIGHT_ENABLE          = true;     //eLight(default:true)
                TypeDef.GROUP_BOILER_ENABLE         = false;    //eBoiler(default:true)
                TypeDef.GROUP_AIRCON_ENABLE         = false;    //eAircon(default:true)
                TypeDef.GROUP_FCU_ENABLE            = false;    //eFCU(default:true)
                TypeDef.GROUP_FAN_ENABLE            = false;    //eFan(default:true)
                TypeDef.GROUP_CURTAIN_ENABLE        = false;    //eCurtain(default:true)
                TypeDef.GROUP_STANDBYPOWER_ENABLE   = false;    //eStandbyPower(default:true)

                /* List Operation Option */
                TypeDef.OPT_SIMPLE_STANDBYPOWER_ENABLE  = true; //simple mode standbypower 모드 여부 (default:false)
                TypeDef.OPT_READONLY_DOORLOCK_ENABLE    = true; //readonly doorlock 모드 여부 (default:false)
                TypeDef.OPT_SEEKBAR_TOUCH_ENABLE        = false; //Seekbar Touch Control 여부(default:false)
                TypeDef.OPT_RETRY_CONTROL_ENABLE        = true; //Group Retry Control 여부 (default:true)
                break;

            case BUYER_ID_LOTTEWORLDTOWER: //롯데월드타워용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_LOTTEWORLDTOWER");

                /* Compile Option */
                TypeDef.APP_ICON_ENABLE             = false;    //App Icon 표시여부(default:true)
                TypeDef.FAKE_DATA_ENABLE            = false;    //테스트용 FAKE 디바이스 추가함(default: false) *** 주의바람!!!

                /* Category Enable Option */
                TypeDef.TAB_SAFE_ENABLE             = false;    //eSafe 포함여부(default:true)
                TypeDef.MENU_ADD_ENABLE             = false;    //add버튼 포함여부(default:true)
                TypeDef.TAB_DYNAMIC_MODE_ENABLE     = false;    //dynamic Tab menu(default:false)

                /* Group Device Enable */
                TypeDef.GROUP_LIGHT_ENABLE          = true;     //eLight(default:true)
                TypeDef.GROUP_BOILER_ENABLE         = false;    //eBoiler(default:true)
                TypeDef.GROUP_AIRCON_ENABLE         = false;    //eAircon(default:true)
                TypeDef.GROUP_FCU_ENABLE            = true;     //eFCU(default:true)
                TypeDef.GROUP_FAN_ENABLE            = false;    //eFan(default:true)
                TypeDef.GROUP_CURTAIN_ENABLE        = false;    //eCurtain(default:true)
                TypeDef.GROUP_STANDBYPOWER_ENABLE   = false;    //eStandbyPower(default:true)

                /* List Operation Option */
                TypeDef.OPT_SIMPLE_STANDBYPOWER_ENABLE  = true; //simple mode standbypower 모드 여부 (default:false)
                TypeDef.OPT_SEEKBAR_TOUCH_ENABLE        = false; //Seekbar Touch Control 여부(default:false)
                TypeDef.OPT_RETRY_CONTROL_ENABLE        = true; //Group Retry Control 여부 (default:true)
                break;

            case BUYER_ID_GENERAL_CIOT: //IP Home IoT 개발중

                Log.d(TAG, ">> BuyerID.... BUYER_ID_GENERAL_CIOT");

                /* Compile Option */
                TypeDef.APP_ICON_ENABLE             = false;    //App Icon 표시여부(default:true)
                TypeDef.FAKE_DATA_ENABLE            = false;    //테스트용 FAKE 디바이스 추가함(default: false) *** 주의바람!!!

                /* Category Enable Option */
                TypeDef.TAB_SAFE_ENABLE             = true;    //eSafe 포함여부(default:true)
                TypeDef.MENU_ADD_ENABLE             = true;    //add버튼 포함여부(default:true)
                TypeDef.TAB_DYNAMIC_MODE_ENABLE     = false;    //dynamic Tab menu(default:false)

                /* Group Device Enable */
                TypeDef.GROUP_LIGHT_ENABLE          = true;     //eLight(default:true)
                TypeDef.GROUP_BOILER_ENABLE         = false;    //eBoiler(default:true)
                TypeDef.GROUP_AIRCON_ENABLE         = false;    //eAircon(default:true)
                TypeDef.GROUP_FCU_ENABLE            = true;     //eFCU(default:true)
                TypeDef.GROUP_FAN_ENABLE            = false;    //eFan(default:true)
                TypeDef.GROUP_CURTAIN_ENABLE        = false;    //eCurtain(default:true)
                TypeDef.GROUP_STANDBYPOWER_ENABLE   = false;    //eStandbyPower(default:true)

                /* List Operation Option */
                TypeDef.OPT_SIMPLE_STANDBYPOWER_ENABLE  = true; //simple mode standbypower 모드 여부 (default:false)
                TypeDef.OPT_SEEKBAR_TOUCH_ENABLE        = false; //Seekbar Touch Control 여부(default:false)
                TypeDef.OPT_RETRY_CONTROL_ENABLE        = true; //Group Retry Control 여부 (default:true)
                TypeDef.GUIDE_BUTTON_ENABLE             = true;
                break;


            case BUYER_ID_DEVELOPVERSION: //개발용

                Log.d(TAG, ">> BuyerID.... BUYER_ID_DEVELOPVERSION");

                /* Compile Option */
                TypeDef.APP_ICON_ENABLE             = true;    //App Icon 표시여부(default:true)
                TypeDef.FAKE_DATA_ENABLE            = true;    //테스트용 FAKE 디바이스 추가함(default: false)

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
