package com.commax.wirelesssetcontrol.touchmirror.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;

import java.util.ArrayList;

/**
 * Created by Shin Sung on 2017-02-07.
 *
 * [그리그를 제공하는 Layout]
 */
public class GridFrameLayout extends GridParentLayout {
    private final String TAG = "GridFrameLayout";

    protected IconLayout[][] mMapData; //실제 보이는 영역 그리드 데이터
    protected Point mMapSizeMax; //그리드 크기 저장

    protected boolean mAddLock = false;

    public GridFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /////////////////[public method]///////////////////
    public void init(int col, int row){ //필수 실행요소
        mMapData = new IconLayout[col][row];
        mMapSizeMax = new Point(col, row);
    }

    //그리드 맵 정보 반환
    public IconLayout[][] getMapData(){
        return mMapData;
    }

    public void clearMap(){
        removeAllViews();
        for(int j=0; j<mMapSizeMax.y; j++)
            for (int i = 0; i < mMapSizeMax.x; i++)
                mMapData[i][j] = null;
    }

    //한개의 아이콘 너비 반환
    public int getIconWidth(){
        return getWidth()/mMapSizeMax.x;
    }

    //한개의 아이콘 높이 반환
    public int getIconHeight(){
        return getHeight()/mMapSizeMax.y;
    }

    //자동으로 빈 공간에 아이템 넣기 (좌->우 & 위->아래)
    public IconLayout addItem(IconData data){
        if(mAddLock)
            return null;

       return setIconAutoPosition(data);
    }

    //자동으로 빈 공간에 크기 조정값으로 넣기 (좌->우 & 위->아래)
    public boolean addItemResize(IconData data, Point size){
        return setIconAutoResizePosition(data, size);
    }

    //터치 좌표 기준으로 가까운 그리드에 아이템 넣기
    public IconLayout addItem(int touchX, int touchY, IconData data){
        if(mAddLock)
           return null;

        return setIconTouchPosition(touchX, touchY, data);
    }

    //인스턴스화 되있는 아이템 붙이기
    public boolean addItem(IconLayout layout){
        IconData data = layout.getIconData();
        Point point = getMapPosition(data.getPosition().x, data.getPosition().y);

        if(checkIconPosition(mMapData, point.x, point.y, data.getSize().x, data.getSize().y)){
            setIconPosition(mMapData, point.x, point.y, data.getSize(), layout);
            addView(layout);
            layout.updatePosition();
        }
        else
            return false;

        return true;
    }

    //아이템 추가 잠금
    public void setAddLock(boolean isLock){
        mAddLock = isLock;
    }

    //아이템 추가 잠금 상태
    public boolean isAddLock(){
        return mAddLock;
    }


    //뷰 삭제 - 데이터 기반
    public void removeItem(IconData data){
        for(int j=0; j<mMapSizeMax.y; j++)
            for (int i = 0; i < mMapSizeMax.x; i++){
                if(mMapData[i][j] != null && mMapData[i][j].getIconData().getId() == data.getId()){
                    if (data.getType() == IconData.TYPE_DEVICE){
                        DeviceIconData deviceIconData = (DeviceIconData)data;
                        deviceIconData.setControlling(false, null, -1);
                    }
                    removeView(mMapData[i][j]);
                    setIconPosition(mMapData, i, j, data.getSize(), null);

                }
            }
    }

    //뷰 데이터 찾기 - 타입 별
    public ArrayList<IconData> getItemForType(int type){
        ArrayList<IconData> iconList = new ArrayList<IconData>();

        for(int j=0; j<mMapSizeMax.y; j++)
            for (int i = 0; i < mMapSizeMax.x; i++){
                if(mMapData[i][j] != null &&
                        (mMapData[i][j].getIconData().getType() == type ||
                                mMapData[i][j].getIconData().getType() == IconData.TYPE_ALL)){
                    iconList.add(mMapData[i][j].getIconData());
                }
            }

        if(iconList.size() == 0)
            return null;

        return iconList;
    }

    //터치 된 좌표에 가장 가까운 아이템 정보를 반환
    public IconData getTouchItemData(int touchX, int touchY){
        try {
            int itemX = touchX / getIconWidth();
            int itemY = touchY / getIconHeight();
            if (mMapData[itemX][itemY] != null)
                return mMapData[itemX][itemY].getIconData();
        }catch(Exception e){
            Log.d(TAG, "getTouchItemData excep : " + e.getMessage());
        }
        return null;
    }

    //터치 된 좌표에 가장 가까운 아이템을 반환
    public IconLayout getTouchItem(int touchX, int touchY){
        int itemX = touchX / getIconWidth();
        int itemY = touchY / getIconHeight();

        try {
            if (mMapData[itemX][itemY] != null)
                return mMapData[itemX][itemY];
        }catch(Exception e){
            Log.d(TAG, "getTouchItem excep : " + e.getMessage());
        }

        return null;
    }

    //터치 된 좌표에 클릭 효과 주기
    public IconLayout setClickMotion(int touchX, int touchY, boolean isClick){
        int itemX = touchX / getIconWidth();
        int itemY = touchY / getIconHeight();

        try {
            if (mMapData[itemX][itemY] != null)
                mMapData[itemX][itemY].setClickMotion(isClick);
        }catch(Exception e){
            Log.d(TAG, "setClickMotion excep : " + e.getMessage());
        }

        return null;
    }

    //데이터와 일치하는 레이아웃 반환
    public IconLayout findItem(IconData data){
        for(int j=0; j<mMapSizeMax.y; j++)
            for (int i = 0; i < mMapSizeMax.x; i++){
                if(mMapData[i][j] != null && mMapData[i][j].getIconData().getId() == data.getId()){
                    return mMapData[i][j];
                }
            }

        return null;
    }

    public IconLayout findItem(String packageName){
        for(int j=0; j<mMapSizeMax.y; j++)
            for (int i = 0; i < mMapSizeMax.x; i++){
                if(mMapData[i][j] != null && mMapData[i][j].getIconData().getPackageName().equals(packageName)){
                    return mMapData[i][j];
                }
            }
        return null;
    }

    //대상 좌표계에서 현재 뷰 좌표계 비율로 바꿈
    public Point convertLocalXY(int targetWidth, int targetHeight, int x, int y){
        double screenWidth = getWidth() / (float)targetWidth;
        double screenHeight = getHeight() / (float)targetHeight;

        Point point = new Point();
        point.x = (int)(Math.ceil(x * screenWidth));
        point.y = (int)(Math.ceil(y * screenHeight));

        return point;
    }

    //현재 뷰 좌표계에서 대상 좌표계 비율로 바꿈
    public Point convertGlobalXY(int targetWidth, int targetHeight, int x, int y){
        double screenWidth = getWidth() / (float)targetWidth;
        double screenHeight = getHeight() / (float)targetHeight;
        Point point = new Point();
        point.x = (int)(Math.ceil(x / screenWidth));
        point.y = (int)(Math.ceil(y / screenHeight));
        return point;
    }

    //아이콘 생성
    public IconLayout createIcon(IconData data){
        LayoutInflater inflater = (LayoutInflater)  getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        IconLayout iconLayout = (IconLayout)inflater.inflate(R.layout.s_icon_layout, this, false);
        iconLayout.setIconData(data, new Point(getIconWidth(), getIconHeight()));
        addView(iconLayout);
        iconLayout.updatePosition();
        return iconLayout;
    }

    //아이콘에 변경사항 업데이트
    public void refreshIconData(IconData data){
        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                if (mMapData[i][j] != null) {
                    if(mMapData[i][j].getIconData().getId() == data.getId()){
                        int x = data.getPosition().x / getIconWidth();
                        int y = data.getPosition().y / getIconHeight();
                        IconLayout layout = mMapData[i][j];
                        layout.setIconData(data, new Point(getIconWidth(), getIconHeight()));
                        clearIconPosition(mMapData, i, j, data.getSize(), layout);
                        setIconPosition(mMapData, x, y, data.getSize(), layout);
                        return;
                    }
                }
            }
        }
    }
	
	//RootUuid가 일치하는 정보 찾기
    public IconLayout getMatchDeviceIconData(String rootUuid){
        try{
            for(int j=0; j<mMapSizeMax.y; j++) {
                for (int i = 0; i < mMapSizeMax.x; i++) {
                    if (mMapData[i][j] != null) {
                        if (mMapData[i][j].getIconData() instanceof DeviceIconData){
                            DeviceIconData data = (DeviceIconData)mMapData[i][j].getIconData();
                            if (data.getRootUuid().equalsIgnoreCase(rootUuid)){
                                return mMapData[i][j];
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //해당 위치가 사용 가능한지 조사
    public boolean checkIconPosition(IconLayout[][] map, int itemX, int itemY, int width, int height){
        if(itemX+width > mMapSizeMax.x || itemY+height > mMapSizeMax.y)
            return false;

        synchronized (map) {
            for (int i = itemX; i < itemX + width; i++) {
                for (int j = itemY; j < itemY + height; j++) {
                    if (map[i][j] != null)
                        return false;
                }
            }
        }
        return true;
    }

    public void recycle(){
        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                if(mMapData[i][j] != null)
                    mMapData[i][j] = null;
            }
        }
        mMapData = null;
        mMapSizeMax = null;
    }

    /////////////////[protected method]///////////////////
    //해당 위치에 아이콘 정보 삽입
    protected void setIconPosition(IconLayout[][] map, int itemX, int itemY, Point size, IconLayout layout){
        synchronized (map) {
            for (int i = itemX; i < itemX + size.x; i++) {
                for (int j = itemY; j < itemY + size.y; j++) {
                    if (i < mMapSizeMax.x && j < mMapSizeMax.y)
                        map[i][j] = layout;
                }
            }
        }
    }

    //해당 위치에 아이콘 정보와 일치하는 항목 삭제
    protected void clearIconPosition(IconLayout[][] map, int itemX, int itemY, Point size, IconLayout layout){
        synchronized (map) {
            for (int i = itemX; i < itemX + size.x; i++) {
                for (int j = itemY; j < itemY + size.y; j++) {
                    if (i < mMapSizeMax.x && j < mMapSizeMax.y) {
                        if (map[i][j] == layout)
                            map[i][j] = null;
                    }
                }
            }
        }
    }

    //맵데이터 복사
    protected void copyMapData(IconLayout[][] target){
        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                target[i][j] = mMapData[i][j];
            }
        }
    }

    //좌표에 맞게 아이콘 생성
    protected IconLayout setIconDragPosition(int touchX, int touchY, IconData data){
        Point point = getMapPosition(touchX, touchY);
        data.setX(point.x * getIconWidth());
        data.setY(point.y * getIconHeight());
        return createIcon(data);
    }

    /////////////////[private method]///////////////////
    //자동으로 그리드의 빈공간에 추가하기
    private IconLayout setIconAutoPosition(IconData data){
        for(int j=0; j<mMapSizeMax.y; j++){
            for(int i=0; i<mMapSizeMax.x; i++){
                if(checkIconPosition(mMapData, i, j, data.getSize().x, data.getSize().y)){
                    data.setX(i * getIconWidth());
                    data.setY(j * getIconHeight());
                    IconLayout layout = createIcon(data);
                    if(layout != null) {
                        setIconPosition(mMapData, i, j, data.getSize(), layout);
                        return layout;
                    }
                }
            }
        }
        return null;
    }

    //자동으로 그리드의 빈공간에 추가하기
    private boolean setIconAutoResizePosition(IconData data, Point size){
        for(int j=0; j<mMapSizeMax.y; j++){
            for(int i=0; i<mMapSizeMax.x; i++){
                if (checkIconPosition(mMapData, i, j, size.x, size.y)) {
                    data.setX(i * getIconWidth());
                    data.setY(j * getIconHeight());

                    IconLayout layout = createIcon(data);
                    if (layout != null)
                        setIconPosition(mMapData, i, j, size, layout);
                    return true;
                }
            }
        }
        return false;
    }

    //터치 좌표를 기준으로 아이콘 그리기
    private IconLayout setIconTouchPosition(int touchX, int touchY, IconData data){
        Point point = getMapPosition(touchX, touchY);
        if(checkIconPosition(mMapData, point.x, point.y, data.getSize().x, data.getSize().y)){
            data.setX(point.x * getIconWidth());
            data.setY(point.y * getIconHeight());
            IconLayout layout = createIcon(data);
            if(layout != null)
                setIconPosition(mMapData, point.x, point.y, data.getSize(), layout);
            return layout;
        }

        return null;
    }

    //실제 좌표를 맵좌표로 반환
    private Point getMapPosition(int x, int y){
        int itemX = 0;
        int itemY = 0;

        if(x != 0 && getIconWidth() > 0)
            itemX = x / getIconWidth();
        if(y != 0 && getIconHeight() > 0)
            itemY = y / getIconHeight();

        return new Point(itemX, itemY);
    }
}
