package com.commax.controlsub;

import com.commax.controlsub.Common.TypeDef;

import java.io.Serializable;

/**
 * Created by gbg on 2016-08-01.
 */
/*
//Category Type
//public static
enum CategoryType {
        eNone,
        eFavorite,
        eLight,
        eIndoorEnv,
        eEnergy,
        eSafe
}

enum CardType1 {
        eNone,
        eMainSwitch,
        eLightSwitch,
        eDimmerSwitch
}

enum CardType2 {
        eNone,
        eBoiler,
        eAircon,
        eFan,
        eFCU,
        eCurtain
}

enum CardType3 {
        eNone,
        eSmartPlug,
        eStandbySwitch,
        eSmartMeter
}

enum CardType4 {
        eNone,
        eGasSensor,
        eMotionSensor,
        eFireSensor,
        eDoorSensor,
        eFloodSensor,
        eDetectSensor,
        eSmokeSensor,
        eAwaySensor,
}
*/


/* BasicInfo : Basic Device Information */
/*
class BasicInfo {

   / Protocol Format (Hierarchical database)
      JSONObject -> command | object
      object     -> commaxDevice | rootDevice | rootUuid | subDevice
      subDevice  -> sort | subUuid | type | value | precision | scale | ...
   /

    //1. Object(root Device) Information
    String nickName;        //user defined device name
    String rootUuid;        //36Byte
    String rootDevice;      //Commax defined device type
    String CommaxDevice;    //Commax defined device name

    //2. sub Object(sub Device) Information -> 여러개 subObject가 있을경우 고려 필요함(추후고려)
    String funcCommand;
    String sort;            //Commax defined sort name
    String subUuid;         //36Byte
    String type;            //read, readWrite
    String value;           //control value(ex:on/off,detected/undected, auto/manual, cool/warm, int/float
    String precision;       //precision      -> 추가구현필요
    String scale;           //scale unit     -> 추가구현필요
    String option1;         //option1        -> 추가구현필요
    String option2;         //option2        -> 추가구현필요

    // etc(?)
    String ifRunvisible;    //IFRUN : true/false
    String subVisible;      //sub Visible : true/false

    //3. Method (Getter,Setter)
    public String getNickName() { return nickName; }
    public String getRootUuid() { return rootUuid; }
    public String getRootDevice() { return rootDevice; }
    public String getCommaxDevice() { return CommaxDevice; }
    public String getFuncCommand() { return funcCommand; }
    public String getSort() { return sort; }
    public String getSubUuid() { return subUuid; }
    public String getType() { return type; }
    public String getValue() { return value; }
    public String getPrecision() { return precision; }
    public String getScale() { return scale; }
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }

    public void setNickName(String nickName) { this.nickName = nickName; }
    public void setRootUuid(String rootUuid) { this.rootUuid = rootUuid; }
    public void setRootDevice(String rootDevice) { this.rootDevice = rootDevice; }
    public void setCommaxDevice(String commaxDevice) { CommaxDevice = commaxDevice; }
    public void setFuncCommand(String funcCommand) { this.funcCommand = funcCommand; }
    public void setSort(String sort) { this.sort = sort; }
    public void setSubUuid(String subUuid) { this.subUuid = subUuid; }
    public void setType(String type) { this.type = type; }
    public void setValue(String value) { this.value = value; }
    public void setPrecision(String precision) { this.precision = precision; }
    public void setScale(String scale) { this.scale = scale; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
}
*/

/* ExternalInfo : External Device Information */
/*
class ExternalInfo {

    //1. User defined Data
    int nCategory;          //Category
    int ncardType;          //cardType
    int nLayoutID;          //target LayoutID
    boolean bFavorite;      //Favorite
    boolean bsubcontrol;    //subcontrol
    boolean bValidate;      //Validate
    //void * ftn_display();

    //2. Method (Getter,Setter)
}


/* Device Information */
public class DeviceInfo implements Serializable {

    String TAG = DeviceInfo.class.getSimpleName();

   /* Protocol Format (Hierarchical database)
      JSONObject -> command | object
      object     -> commaxDevice | rootDevice | rootUuid | subDevice
      subDevice  -> sort | subUuid | type | value | precision | scale | ...
   */

    //1. Object(root Device) Information
    public  String nickName;        //user defined device name
    String rootUuid;        //36Byte
    String rootDevice;      //Commax defined device type
    String CommaxDevice;    //Commax defined device name

    //2. sub Object(sub Device) Information -> 여러개 subObject가 있을경우 고려 필요함(추후고려)
    String funcCommand;
    String sort;            //Commax defined sort name
    String subUuid;         //36Byte
    String type;            //read, readWrite
    String value;           //control value(ex:on/off,detected/undected, auto/manual, cool/warm, int/float
    String precision;       //precision      -> 추가구현필요
    String scale;           //scale unit     -> 추가구현필요
    String scale2;           //scale unit     -> 추가구현필요
    String scale3;           //scale unit     -> 추가구현필요
    String option1;         //option1        -> 추가구현필요
    String option2;         //option2        -> 추가구현필요
    String option3;         //option1        -> 추가구현필요
    String option4;         //option2        -> 추가구현필요


    // sub sort는 max 3개까지(컨테이너는 Simple정보만 표시함)
    String sort2;            //Commax defined sort name
    String subUuid2;         //36Byte
    String value2;
    String value5;
    String value6;
    String value7;
    String sort3;            //Commax defined sort name
    String sort4;
    String sort5;
    String sort6;
    String sort7;
    String subUuid3;         //36Byte
    String subUuid4;
    String subUuid5;
    String subUuid6;
    String subUuid7;
    String value3;
    String value4;


    //3. User defined Data
    TypeDef.CategoryType nCategory;          //Category
    TypeDef.CardType nCardType;          //cardType
    int nLayoutID;          //target LayoutID
    boolean bFavorite;      //Favorite
    boolean bsubcontrol;    //subcontrol
    boolean bValidate;      //Validate
    boolean bUpdated;       //Updated

    //4. Constructure
    public DeviceInfo(String rootUuid, String sort) {
        this.rootUuid=rootUuid;
        this.sort=sort;
    }

    //5. Method
    //Getter
    public String getNickName() { return nickName; }
    public String getRootUuid() { return rootUuid; }
    public String getRootDevice() { return rootDevice; }
    public String getCommaxDevice() { return CommaxDevice; }
    public String getFuncCommand() { return funcCommand; }
    public String getSort() { return sort; }
    public String getSubUuid() { return subUuid; }
    public String getType() { return type; }
    public String getValue() { return value; }
    public String getPrecision() { return precision; }
    public String getScale() { return scale; }
    public String getScale2() { return scale2; }
    public String getScale3() { return scale3; }
    public String getSort2() { return sort2; }
    public String getSort3() { return sort3; }
    public String getSort4() { return sort4; }
    public String getSort5() { return sort5; }
    public String getSort6() { return sort6; }
    public String getSort7() { return sort7; }
    public String getSubUuid2() { return subUuid2; }
    public String getSubUuid3() { return subUuid3; }
    public String getSubUuid4() { return subUuid4; }
    public String getSubUuid5() { return subUuid5; }
    public String getSubUuid6() { return subUuid6; }
    public String getSubUuid7() { return subUuid7; }
    public String getValue2() { return value2; }
    public String getValue3() { return value3; }
    public String getValue4() { return value4; }
    public String getValue5() { return value5; }
    public String getValue6() { return value6; }
    public String getValue7() { return value7; }

    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public String getOption3() { return option3; }
    public String getOption4() { return option4; }

    //Setter
    public void setNickName(String nickName) { this.nickName = nickName; }
    public void setRootUuid(String rootUuid) { this.rootUuid = rootUuid; }
    public void setRootDevice(String rootDevice) { this.rootDevice = rootDevice; }
    public void setCommaxDevice(String commaxDevice) { CommaxDevice = commaxDevice; }
    public void setFuncCommand(String funcCommand) { this.funcCommand = funcCommand; }
    public void setSort(String sort) { this.sort = sort; }
    public void setSubUuid(String subUuid) { this.subUuid = subUuid; }
    public void setType(String type) { this.type = type; }
    public void setValue(String value) { this.value = value; }
    public void setPrecision(String precision) { this.precision = precision; }
    public void setScale(String scale) { this.scale = scale; }
    public void setScale2(String scale2) { this.scale2 = scale2; }
    public void setScale3(String scale3) { this.scale3 = scale3; }
    public void setSort2(String sort2) { this.sort2 = sort2; }
    public void setSort3(String sort3) { this.sort3 = sort3; }
    public void setSort4(String sort4) { this.sort4 = sort4; }
    public void setSort5(String sort5) { this.sort5 = sort5; }
    public void setSort6(String sort6) { this.sort6 = sort6; }
    public void setSort7(String sort7) { this.sort7 = sort7; }
    public void setSubUuid2(String subUuid2) { this.subUuid2 = subUuid2; }
    public void setSubUuid3(String subUuid3) { this.subUuid3 = subUuid3; }
    public void setSubUuid4(String subUuid4) { this.subUuid4 = subUuid4; }
    public void setSubUuid5(String subUuid5) { this.subUuid5 = subUuid5; }
    public void setSubUuid6(String subUuid6) { this.subUuid6 = subUuid6; }
    public void setSubUuid7(String subUuid7) { this.subUuid7 = subUuid7; }
    public void setValue2(String value2) { this.value2 = value2; }
    public void setValue3(String value3) { this.value3 = value3; }
    public void setValue4(String value4) { this.value4 = value4; }
    public void setValue5(String value5) { this.value5 = value5; }
    public void setValue6(String value6) { this.value6 = value6; }
    public void setValue7(String value7) { this.value7 = value7; }
    public void setOption1(String option1) { this.option1 = option1; }
    public void setOption2(String option2) { this.option2 = option2; }
    public void setOption3(String option3) { this.option3 = option3; }
    public void setOption4(String option4) { this.option4 = option4; }

    //Extra
    public TypeDef.CategoryType getnCategory() { return nCategory; }
    public TypeDef.CardType getnCardType() { return nCardType; }
    public int getnLayoutID() { return nLayoutID; }
    public boolean isFavorite() { return bFavorite; }
    public boolean isSubcontrol() { return bsubcontrol; }
    public boolean isValidate() { return bValidate; }
    public boolean isbUpdated() { return bUpdated; }

    public void setnCategory(TypeDef.CategoryType nCategory) { this.nCategory = nCategory; }
    public void setnCardType(TypeDef.CardType nCardType) { this.nCardType = nCardType; }
    public void setnLayoutID(int nLayoutID) { this.nLayoutID = nLayoutID; }
    public void setFavorite(boolean bFavorite) { this.bFavorite = bFavorite; }
    public void setSubcontrol(boolean bsubcontrol) { this.bsubcontrol = bsubcontrol; }
    public void setValidate(boolean bValidate) { this.bValidate = bValidate; }
    public void setbUpdated(boolean bUpdated) { this.bUpdated = bUpdated; }

    //Method
    public void addDeviceInfo(String rootDevice, String CommaxDevice){
        this.rootDevice=rootDevice;
        this.CommaxDevice=CommaxDevice;
    }

    public void setStatus(String subUuid_sb, String value_sb){
        this.subUuid=subUuid_sb;
        this.value=value_sb;
    }

    //add,set,remove,clear list
    //drawcardview
    //removecardview
    //updatecardview
    //savefile
    //loadfile

/*
    BasicInfo basicInfo = new BasicInfo();;
    ExternalInfo externalInfo = new ExternalInfo();

    public DeviceInfo(BasicInfo basicInfo) {
       this.basicInfo = basicInfo;
    }

    public DeviceInfo(ExternalInfo externalInfo) {
       this.externalInfo = externalInfo;
    }

    public DeviceInfo(BasicInfo basicInfo, ExternalInfo externalInfo) {
       this.basicInfo = basicInfo;
       this.externalInfo = externalInfo;
    }

    //2. Method
    //add,set,remove,clear list
    //drawcardview
    //removecardview
    //updatecardview
    //savefile
    //loadfile
*/
    /* Class End -----------------------------*/
}
