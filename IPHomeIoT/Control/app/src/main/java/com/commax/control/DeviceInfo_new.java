package com.commax.control;

import com.commax.control.Common.TypeDef;

import java.util.ArrayList;

/**
 * Created by gbg on 2016-08-01.
 */

//TODO for test
/* Device Information */
public class DeviceInfo_new {

   /* Protocol Format (Hierarchical database)
      JSONObject -> command | object
      object     -> commaxDevice | rootDevice | rootUuid | subDevice
      subDevice  -> sort | subUuid | type | value | precision | scale | ...
   */

    //1. Object(root Device) Information
    String nickName;        //user defined device name
    String rootUuid;        //36Byte
    String rootDevice;      //Commax defined device type
    String CommaxDevice;    //Commax defined device name

    TypeDef.CategoryType nCategory;      //Category
    TypeDef.CardType nCardType;          //cardType

    String groupID;         //target groupID : grouping 시 사용함
    boolean bFavorite;      //Favorite
    boolean bVirtualDevice; //bVirtualDevice
    boolean bUpdated;       //Updated

    ArrayList<SubDevice_new> subDevices;
    public void initDevice(String rootUuid, String rootDevice, TypeDef.CategoryType nCategory){
        this.rootUuid = rootUuid;
        this.rootDevice = rootDevice;
        this.nCategory = nCategory;
    }
}
