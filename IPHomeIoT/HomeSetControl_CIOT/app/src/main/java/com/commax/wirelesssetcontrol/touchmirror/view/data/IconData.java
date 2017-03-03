package com.commax.wirelesssetcontrol.touchmirror.view.data;

import android.graphics.Point;
import android.graphics.drawable.Drawable;

import java.util.Random;

/**
 * Created by Shin Sung on 2017-02-07.
 * 아이콘 정보에 대한 데이터 포멧
 */
public class IconData{
    public static final int TYPE_APPS = 0;
    public static final int TYPE_WIDGET = 1;
    public static final int TYPE_DEVICE = 2;
    public static final int TYPE_FIXED = 3;
    public static final int TYPE_ALL = -1;

    public static final int NO_RESOURCE = -1;

    private int mType;
    private int mId = -1;  //고유키
    private int mWidgetId = -1;
    private boolean mIsFullSize = false;
    private boolean mIsRealWidget = false;
    private boolean mIsAlignWidget = false;
    private boolean mIsDeviceDefault = false;

    private String mPackageName  = "";  //패키지 이름 저장
    private String mComponentName  = "";  //컴포넌트 이름 저장
    private String mName = "undefined";

    private int mImgResId = NO_RESOURCE; //리소스 id
    private Drawable mDrawable; //이미지

    private Point mPosition;
    private Point mSize;

    public IconData(){
        /*기본 설정*/
        mId = new Random().nextInt(10000);
        mType = TYPE_APPS;
        mPosition = new Point(0, 0);
        mSize = new Point(1, 1);
        mIsFullSize = false;
    }

    //상세 데이터 생성자
    public IconData(int type, Drawable drawable, String name, int x, int y, int width, int height){
        this();
        mType = type;
        mDrawable = drawable;
        mName = name;
        mPosition.set(x,y);
        mSize.set(width, height);
    }

    //아이콘 데이터 복사
    public static IconData copy(IconData data){
        if (data instanceof DeviceIconData) {

            DeviceIconData temp = (DeviceIconData)data;
            DeviceIconData deviceIconData = new DeviceIconData(data.getType(), data.getDrawable(), data.getName(),
                    data.getPosition().x, data.getPosition().y,
                    data.getSize().x, data.getSize().y,
                    temp.getRootUuid(), temp.getDeviceType(), temp.getNickName(), temp.getControlType(), temp.getMain_subUuid(), temp.getStatus());
            return deviceIconData;
        }else {
            IconData iconData = new IconData(data.getType(), data.getDrawable(), data.getName(),
                    data.getPosition().x, data.getPosition().y,
                    data.getSize().x, data.getSize().y);
            iconData.setPackageName(data.getPackageName());
            iconData.setComponentName(data.getComponentName());
            iconData.changeId(data.getId());
            return iconData;
        }
    }

    //getter, setter
    public void changeId(int id){
        mId = id;
    }

    public int getId(){ return mId; }

    public void setImgResId(int resId){ mImgResId = resId; }

    public int getImgResId(){ return mImgResId; }

    public int getType(){
        return mType;
    }

    public Point getPosition(){
        return mPosition;
    }

    public Point getSize(){
        return mSize;
    }

    public void setX(int x){
        mPosition.x = x;
    }

    public void setY(int y){
        mPosition.y = y;
    }

    public void setDrawable(Drawable drawable){
        mDrawable = drawable;
    }

    public Drawable getDrawable(){
        return mDrawable;
    }

    public void setName(String name){
        mName = name;
    }

    public String getName(){
        return mName;
    }

    public void setPackageName(String packageName){
        mPackageName = packageName;
    }

    public String getPackageName(){
        return mPackageName;
    }

    public void setComponentName(String componentName){
        mComponentName = componentName;
    }

    public String getComponentName(){
        return mComponentName;
    }

    public boolean isFullSize(){
        return mIsFullSize;
    }

    public void setFullSize(boolean isFull){
        mIsFullSize = isFull;
    }

    public boolean isDeviceDefault(){
        return mIsDeviceDefault;
    }

    public void setDeviceDefault(boolean deviceDefault){
        mIsDeviceDefault = deviceDefault;
    }

    public boolean isRealWidget(){
        return mIsRealWidget;
    }

    public void setAlignWidget(boolean align){
        mIsAlignWidget = align;
    }

    public boolean isAlignWidget(){
        return mIsAlignWidget;
    }

    public void setRealWidget(){
        mIsRealWidget = true;
    }

    public void setWidgetId(int id){
        mWidgetId=id;
    }

    public int getWidgetId(){
        return mWidgetId;
    }

    public int getX(){
        return mPosition.x;
    }

    public int getY(){
        return mPosition.y;
    }
}
