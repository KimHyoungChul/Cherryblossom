package com.commax.control;

import com.commax.control.Common.TypeDef;

import java.util.ArrayList;

/**
 * Created by gbg on 2016-08-01.
 */

/* Device Information */
public class DeviceInfo {

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
    String batteryLevel;   //batteryLevel
    String batteryEvent;    //batteryEvent : low
    String ifRunvisible;    //IFRUN : true/false
    String subVisible;      //sub Visible : true/false

    // sub sort는 max 5개까지(컨테이너는 Simple정보만 표시함)
    int other_subcount;      //subcontrol 개수
    String other_sort[];
    String other_subUuid[];
    String other_value[];
    String other_precision[];
    String other_scale[];
    String other_option1[];
    String other_option2[];

    //3. User defined Data
    TypeDef.CategoryType nCategory;      //Category
    TypeDef.CardType nCardType;          //cardType
    int nTabID;             //target nTabID : Tab ID로 사용함
    int nLayoutID;          //target LayoutID : message ID로 사용함
    String groupID;         //target groupID : grouping 시 사용함
    boolean bFavorite;      //Favorite
    boolean bValidate;      //Validate
    boolean bVirtualDevice; //bVirtualDevice
    boolean bUpdated;       //Updated

    //4. Constructor
    public DeviceInfo(String rootUuid, String sort) {
        this.rootUuid=rootUuid;
        this.sort=sort;

        this.other_sort= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_subUuid= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_value= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_precision= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_scale= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_option1= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
        this.other_option2= new String[TypeDef.MAX_SUBDEV_CONTROLLER];
    }

    public DeviceInfo()
    {

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
    public String getOption1() { return option1; }
    public String getOption2() { return option2; }
    public int getOtherSubcount() { return other_subcount; }
    public String getOtherSort(int index) { return other_sort[index]; }
    public String getOtherSubUuid(int index) { return other_subUuid[index]; }
    public String getOtherValue(int index) { return other_value[index]; }
    public String getOtherPrecision(int index) { return other_precision[index]; }
    public String getOtherScale(int index) { return other_scale[index]; }
    public String getOtherOption1(int index) { return other_option1[index]; }
    public String getOtherOption2(int index) { return other_option2[index]; }
    public String getBatteryLevel() { return batteryLevel; }
    public String getBatteryEvent() { return batteryEvent; }

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
    public void setOtherSubcount(int subcount) { this.other_subcount = subcount; }
    public void setOtherSort(int index, String sort) { this.other_sort[index]= sort; }
    public void setOtherSubUuid(int index, String subUuid) { this.other_subUuid[index]= subUuid; }
    public void setOtherValue(int index, String value) { this.other_value[index]= value; }
    public void setOtherPrecision(int index, String subUuid) { this.other_precision[index]= subUuid; }
    public void setOtherScale(int index, String value) { this.other_scale[index]= value; }
    public void setOption1(int index, String option) { this.other_option1[index] = option; }
    public void setOption2(int index, String option) { this.other_option2[index] = option; }
    public void setBatteryLevel(String value)  { this.batteryLevel = value; }
    public void setBatteryEvent(String value)  { this.batteryEvent = value; }

    //Extra
    public TypeDef.CategoryType getnCategory() { return nCategory; }
    public TypeDef.CardType getnCardType() { return nCardType; }
    public int getnTabID() { return this.nTabID; }
    public int getnLayoutID() { return nLayoutID; }
    public String getGroupID() { return groupID; }
    public boolean isFavorite() { return bFavorite; }
    public boolean isValidate() { return bValidate; }
    public boolean isVirtualDevice() { return bVirtualDevice; }
    public boolean isbUpdated() { return bUpdated; }

    public void setnCategory(TypeDef.CategoryType nCategory) { this.nCategory = nCategory; }
    public void setnCardType(TypeDef.CardType nCardType) { this.nCardType = nCardType; }
    public void setnTabID(int nTabID) { this.nTabID = nTabID; }
    public void setnLayoutID(int nLayoutID) { this.nLayoutID = nLayoutID; }
    public void setGroupID(String groupID) { this.groupID = groupID; }
    public void setFavorite(boolean bFavorite) { this.bFavorite = bFavorite; }
    public void setValidate(boolean bValidate) { this.bValidate = bValidate; }
    public void setVirtualDevice(boolean bVirtualDevice) { this.bVirtualDevice = bVirtualDevice; }
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

    /* Class End -----------------------------*/


    ArrayList<SubDevice_new> subDevices;


}
