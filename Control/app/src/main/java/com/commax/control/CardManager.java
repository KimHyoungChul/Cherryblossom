package com.commax.control;

import android.content.Context;

import com.commax.control.Card_list.Card_Aircon;
import com.commax.control.Card_list.Card_Boiler;
import com.commax.control.Card_list.Card_Curtain;
import com.commax.control.Card_list.Card_DimmerSwitch;
import com.commax.control.Card_list.Card_DoorLock;
import com.commax.control.Card_list.Card_FCU;
import com.commax.control.Card_list.Card_Fan;
import com.commax.control.Card_list.Card_GasDetectSensor;
import com.commax.control.Card_list.Card_GasLock;
import com.commax.control.Card_list.Card_LightSwitch;
import com.commax.control.Card_list.Card_MagneticSensor;
import com.commax.control.Card_list.Card_MainSwitch;
import com.commax.control.Card_list.Card_MultiSensor_PIR;
import com.commax.control.Card_list.Card_SmartMeter;
import com.commax.control.Card_list.Card_SmartPlug;
import com.commax.control.Card_list.Card_StandbyPower;
import com.commax.control.Card_list.Card_WaterSensor;
import com.commax.control.Common.Log;

import java.util.ArrayList;

/**
 * Created by gbg on 2016-10-26.
 */

public class CardManager {

    static final String TAG = "CardManager";

    //Test Option
    public static final boolean CARD_MANAGER_DISABLE = false; //default(false)

    public static CardManager mInst = null;

    Context mContext;

    //Card List
    private ArrayList<Card_Aircon> arAircon;
    private ArrayList<Card_Boiler> arBoiler;
    private ArrayList<Card_Curtain> arCurtain;
    private ArrayList<Card_MultiSensor_PIR> arMultiSensor;
    //TODO erMagneticSensor 로 따로 사용중
//    private ArrayList<Card_DetectSensor> arDetectSensor;
    private ArrayList<Card_MagneticSensor> arMagneticSensor;
    private ArrayList<Card_DimmerSwitch> arDimmerSwitch;
    private ArrayList<Card_Fan> arFan;
    private ArrayList<Card_FCU> arFCU;
    private ArrayList<Card_GasLock> arGasLock;
    private ArrayList<Card_LightSwitch> arLightSwitch;
    private ArrayList<Card_MainSwitch> arMainSwitch;
    private ArrayList<Card_SmartMeter> arSmartMeter;
    private ArrayList<Card_SmartPlug> arSmartPlug;
    private ArrayList<Card_StandbyPower> arStandbyPower;
    private ArrayList<Card_DoorLock> arDoorLock; //todo
    private ArrayList<Card_WaterSensor> arWaterSensor;
    private ArrayList<Card_GasDetectSensor> arGasDetectSensor;

    public static CardManager getInst() {
        if (mInst == null) {
            Context context = MainActivity.getInstance();
            mInst = new CardManager(context);
        }
        return mInst;
    }

    public CardManager(Context context) {
        Log.d(TAG, "create CardManager() ...");

        mContext = context;

        //New Class Preference Array
        arAircon = new ArrayList<Card_Aircon>();
        arBoiler = new ArrayList<Card_Boiler>();
        arCurtain = new ArrayList<Card_Curtain>();
        arMultiSensor = new ArrayList<Card_MultiSensor_PIR>();
        //TODO magneticSensor 로 따로 사용중
//        arDetectSensor = new ArrayList<Card_DetectSensor>();
        arDimmerSwitch = new ArrayList<Card_DimmerSwitch>();
        arFan = new ArrayList<Card_Fan>();
        arFCU = new ArrayList<Card_FCU>();
        arGasLock = new ArrayList<Card_GasLock>();
        arLightSwitch = new ArrayList<Card_LightSwitch>();
        arMainSwitch = new ArrayList<Card_MainSwitch>();
        arSmartMeter = new ArrayList<Card_SmartMeter>();
        arSmartPlug = new ArrayList<Card_SmartPlug>();
        arStandbyPower = new ArrayList<Card_StandbyPower>();
        arDoorLock = new ArrayList<Card_DoorLock>();
        arMagneticSensor = new ArrayList<Card_MagneticSensor>();
        arWaterSensor = new ArrayList<Card_WaterSensor>();
        arGasDetectSensor = new ArrayList<Card_GasDetectSensor>();
    }

    public void destoryCardManager() {

        Log.d(TAG, "destoryCardManager() ...");

        //Destory Class Instance handle array
        try {
            stopAllDeviceCard();

            arAircon.clear();
            arBoiler.clear();
            arCurtain.clear();
            arMultiSensor.clear();
            arDimmerSwitch.clear();
            arFan.clear();
            arFCU.clear();
            arGasLock.clear();
            arLightSwitch.clear();
            arMainSwitch.clear();
            arSmartMeter.clear();
            arSmartPlug.clear();
            arStandbyPower.clear();
            arDoorLock.clear();
            arMagneticSensor.clear();
            arWaterSensor.clear();
            arGasDetectSensor.clear();
/*
            arAircon = null;
            arBoiler = null;
            arCurtain = null;
            arDetectSensor = null;
            arDimmerSwitch = null;
            arFan = null;
            arFCU = null;
            arGasLock = null;
            arLightSwitch = null;
            arMainSwitch = null;
            arSmartMeter = null;
            arSmartPlug = null;
            arStandbyPower = null;
*/

            mInst = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDeviceCard(Object devCard, DeviceInfo devicedata) {

        //Test Option
        if (CARD_MANAGER_DISABLE == true) return;

        try {
            //Log.d(TAG, "addDeviceCard() ..." + devicedata.getRootUuid());
            switch (devicedata.nCardType) {
                case eNone:
                    break;

                //eLight
                case eMainSwitch: //eMainSwitch는 GroupSwitch로 전용함
                    arMainSwitch.add((Card_MainSwitch) devCard);
                    break;
                case eLightSwitch: //eMainSwitch와 eLightSwitch와 합침
                    arLightSwitch.add((Card_LightSwitch) devCard);
                    break;
                case eDimmerSwitch:
                    arDimmerSwitch.add((Card_DimmerSwitch) devCard);
                    break;

                //eIndoorEnv
                case eThermostat:  //Group Device only
                    // TODO: 2016-09-02
                    break;
                case eBoiler:
                    arBoiler.add((Card_Boiler) devCard);
                    break;
                case eAircon:
                    arAircon.add((Card_Aircon) devCard);
                    break;
                case eFan:
                    arFan.add((Card_Fan) devCard);
                    break;
                case eFCU:
                    arFCU.add((Card_FCU) devCard);
                    //Log.d(TAG, "*** add eFCU " + ((Card_FCU)devCard).getRootView() + " " + ((Card_FCU)devCard).getRootView().getWidth());
                    //Log.d(TAG, "*** add eFCU " + devicedata.getRootUuid());
                    break;
                case eCurtain:
                    arCurtain.add((Card_Curtain) devCard);
                    break;

                case eDetectSensor:
                    arMultiSensor.add((Card_MultiSensor_PIR) devCard);
//                    arDetectSensor.add((Card_DetectSensor) devCard);
                    break;
                case eWaterSensor:
                    arWaterSensor.add((Card_WaterSensor)devCard);
                    break;

                //eEnergy
                case eSmartPlug:
                    arSmartPlug.add((Card_SmartPlug) devCard);
                    break;
                case eStandbyPower:
                     arStandbyPower.add((Card_StandbyPower) devCard);
                     //Log.d(TAG, "*** add eStandbyPower " + ((Card_StandbyPower)devCard).getRootView() + " " + ((Card_StandbyPower)devCard).getRootView().getWidth());
                     break;
                case eSmartMeter:
                    //SmartMeter는 가상그룹 디바이스로 표시함
                    if (devicedata.bVirtualDevice) {
                        arSmartMeter.add((Card_SmartMeter) devCard);
                    }
                    break;
                case eElevator:
                    // TODO: 2016-09-02
                    break;

                //eSafe
                case eGasLock:
                    arGasLock.add((Card_GasLock) devCard);
                    break;
                case eGasSensor:
                    arGasDetectSensor.add((Card_GasDetectSensor) devCard);
                    break;
                case eMotionSensor:
                    // TODO: 2016-09-02
                    break;
                case eFireSensor:
                    // TODO: 2016-09-02
                    break;
                case eDoorLock:
                    arDoorLock.add((Card_DoorLock) devCard); //todo
                    break;
                case eDoorSensor:
                    arMagneticSensor.add((Card_MagneticSensor)devCard);
                    // TODO: 2016-09-02
                    break;
                case eSmokeSensor:
                    // TODO: 2016-09-02
                    break;
                case eAwaySensor:
                    // TODO: 2016-09-02
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getDeviceCard(DeviceInfo devicedata)  {
        Object devCard = null;
        DeviceInfo localdevicedata;

        //Test Option
        if (CARD_MANAGER_DISABLE == true) return devCard;

        int check_cardwidth = (int)mContext.getResources().getDimension(R.dimen.column_list_width);
        try {
            switch (devicedata.nCardType) {
                case eNone:
                    break;

                //eLight
                case eMainSwitch: //eMainSwitch는 GroupSwitch로 전용함
                    for (int i = 0; i < arMainSwitch.size(); i++) {
                        localdevicedata = arMainSwitch.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_MainSwitch) arMainSwitch.get(i);
                            if( ((Card_MainSwitch)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arMainSwitch.get(i).stopDeviceAction();
                                arMainSwitch.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eLightSwitch: //eMainSwitch와 eLightSwitch와 합침
                    for (int i = 0; i < arLightSwitch.size(); i++) {
                        localdevicedata = arLightSwitch.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_LightSwitch) arLightSwitch.get(i);
                            if( ((Card_LightSwitch)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arLightSwitch.get(i).stopDeviceAction();
                                arLightSwitch.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eDimmerSwitch:
                    for (int i = 0; i < arDimmerSwitch.size(); i++) {
                        localdevicedata = arDimmerSwitch.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_DimmerSwitch) arDimmerSwitch.get(i);
                            if( ((Card_DimmerSwitch)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arDimmerSwitch.get(i).stopDeviceAction();
                                arDimmerSwitch.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;

                //eIndoorEnv
                case eThermostat:  //Group Device only
                    // TODO: 2016-09-02
                    break;
                case eBoiler:
                    for (int i = 0; i < arBoiler.size(); i++) {
                        localdevicedata = arBoiler.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_Boiler) arBoiler.get(i);
                            if( ((Card_Boiler)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arBoiler.get(i).stopDeviceAction();
                                arBoiler.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eAircon:
                    for (int i = 0; i < arAircon.size(); i++) {
                        localdevicedata = arAircon.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_Aircon) arAircon.get(i);
                            if( ((Card_Aircon)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arAircon.get(i).stopDeviceAction();
                                arAircon.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eFan:
                    for (int i = 0; i < arFan.size(); i++) {
                        localdevicedata = arFan.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_Fan) arFan.get(i);
                            if( ((Card_Fan)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arFan.get(i).stopDeviceAction();
                                arFan.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eFCU:
                    for (int i = 0; i < arFCU.size(); i++) {
                        localdevicedata = arFCU.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_FCU) arFCU.get(i);
                            Log.d(TAG, "*** get eFCU " + devicedata.getRootUuid());
                            if( ((Card_FCU)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arFCU.get(i).stopDeviceAction();
                                arFCU.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eCurtain:
                    for (int i = 0; i < arCurtain.size(); i++) {
                        localdevicedata = arCurtain.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_Curtain) arCurtain.get(i);
                            if( ((Card_Curtain)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arCurtain.get(i).stopDeviceAction();
                                arCurtain.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;

                case eDetectSensor:
                    for (int i = 0; i < arMultiSensor.size(); i++) {
                        localdevicedata = arMultiSensor.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_MultiSensor_PIR) arMultiSensor.get(i);
//                            devCard = (Card_DetectSensor) arDetectSensor.get(i);
                            if( ((Card_MultiSensor_PIR)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arMultiSensor.get(i).stopDeviceAction();
                                arMultiSensor.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;

                case eWaterSensor:
                    for (int i = 0; i < arWaterSensor.size(); i++) {
                        localdevicedata = arWaterSensor.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_WaterSensor) arWaterSensor.get(i);
                            if( ((Card_WaterSensor)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arWaterSensor.get(i).stopDeviceAction();
                                arWaterSensor.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;

                //eEnergy
                case eSmartPlug:
                    for (int i = 0; i < arSmartPlug.size(); i++) {
                        localdevicedata = arSmartPlug.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_SmartPlug) arSmartPlug.get(i);
                            if( ((Card_SmartPlug)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arSmartPlug.get(i).stopDeviceAction();
                                arSmartPlug.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eStandbyPower:
                    for (int i = 0; i < arStandbyPower.size(); i++) {
                        localdevicedata = arStandbyPower.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_StandbyPower) arStandbyPower.get(i);
                            if( ((Card_StandbyPower)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arStandbyPower.get(i).stopDeviceAction();
                                arStandbyPower.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eSmartMeter:
                    //SmartMeter는 가상그룹 디바이스로 표시함
                    if (devicedata.bVirtualDevice) {
                        for (int i = 0; i < arSmartMeter.size(); i++) {
                            localdevicedata = arSmartMeter.get(i).getDeviceData();
                            if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                                devCard = (Card_SmartMeter) arSmartMeter.get(i);
                                if( ((Card_SmartMeter)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                    arSmartMeter.get(i).stopDeviceAction();
                                    arSmartMeter.remove(i);
                                    devCard = null;
                                }
                                break;
                            }
                        }
                    }
                    break;
                case eElevator:
                    // TODO: 2016-09-02
                    break;

                //eSafe
                case eGasLock:
                    for (int i = 0; i < arGasLock.size(); i++) {
                        localdevicedata = arGasLock.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_GasLock) arGasLock.get(i);
                            if( ((Card_GasLock)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arGasLock.get(i).stopDeviceAction();
                                arGasLock.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eGasSensor:
                    for (int i = 0; i < arGasDetectSensor.size(); i++) {
                        localdevicedata = arGasDetectSensor.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_GasDetectSensor) arGasDetectSensor.get(i);
                            if( ((Card_GasDetectSensor)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arGasDetectSensor.get(i).stopDeviceAction();
                                arGasDetectSensor.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eMotionSensor:
                    // TODO: 2016-09-02
                    break;
                case eFireSensor:
                    // TODO: 2016-09-02
                    break;
                case eDoorLock:
                    for (int i = 0; i < arDoorLock.size(); i++) {
                        localdevicedata = arDoorLock.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_DoorLock) arDoorLock.get(i);
                            if( ((Card_DoorLock)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arDoorLock.get(i).stopDeviceAction();
                                arDoorLock.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    break;
                case eDoorSensor:
                    for (int i = 0; i < arMagneticSensor.size(); i++) {
                        localdevicedata = arMagneticSensor.get(i).getDeviceData();
                        if (localdevicedata.getRootUuid().equals(devicedata.getRootUuid())) {
                            devCard = (Card_MagneticSensor) arMagneticSensor.get(i);
                            if( ((Card_MagneticSensor)devCard).getRootView().getWidth() != check_cardwidth) { //Check Content
                                arMagneticSensor.get(i).stopDeviceAction();
                                arMagneticSensor.remove(i);
                                devCard = null;
                            }
                            break;
                        }
                    }
                    // TODO: 2016-09-02
                    break;
                case eSmokeSensor:
                    // TODO: 2016-09-02
                    break;
                case eAwaySensor:
                    // TODO: 2016-09-02
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (devCard != null) {
            //Log.d(TAG, "getDeviceCard() ..." + devicedata.getRootUuid());
        }

        return devCard;
    }

    public void stopAllDeviceCard() {

        try {
            for (int i = 0; i < arAircon.size(); i++) {
                arAircon.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arBoiler.size(); i++) {
                arBoiler.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arCurtain.size(); i++) {
                arCurtain.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arMultiSensor.size(); i++) {
                arMultiSensor.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arDimmerSwitch.size(); i++) {
                arDimmerSwitch.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arFan.size(); i++) {
                arFan.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arFCU.size(); i++) {
                arFCU.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arGasLock.size(); i++) {
                arGasLock.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arLightSwitch.size(); i++) {
                arLightSwitch.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arMainSwitch.size(); i++) {
                arMainSwitch.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arSmartMeter.size(); i++) {
                arSmartMeter.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arSmartPlug.size(); i++) {
                arSmartPlug.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arStandbyPower.size(); i++) {
                arStandbyPower.get(i).stopDeviceAction();
            }
            for (int i = 0; i < arDoorLock.size(); i++) {
                arDoorLock.get(i).stopDeviceAction();
            }for (int i = 0; i < arWaterSensor.size(); i++) {
                arMultiSensor.get(i).stopDeviceAction();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
