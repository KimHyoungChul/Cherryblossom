package com.commax.wirelesssetcontrol.touchmirror.view.tools;

import android.graphics.Point;

import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;

/**
 * Created by Shin sung on 2017-02-20.
 * 페이저에 추가 되는 아이콘에 대하여 얼마나 많은 페이지를 생성하는지 계산하는 클래스
 */

public class GridChecker {
    private boolean[][] mMapData;
    private Point mMapSize;

    public GridChecker(int col, int row){
        mMapSize = new Point(col, row);
        mMapData = new boolean[col][row];
        clear();
    }

    public void clear(){
        for(int j=0; j<mMapSize.y; j++) {
            for (int i = 0; i < mMapSize.x; i++) {
                mMapData[i][j] = false;
            }
        }
    }

    public boolean addData(Point size){
        for(int j=0; j<mMapSize.y; j++){
            for(int i=0; i<mMapSize.x; i++){
                if (checkIconPosition(i, j, size)) {
                    setIconPosition(i, j, size, true);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkIconPosition(int itemX, int itemY, Point size){
        if(itemX+size.x > mMapSize.x || itemY+size.y > mMapSize.y)
            return false;

        synchronized (mMapData) {
            for (int i = itemX; i < itemX + size.x; i++) {
                for (int j = itemY; j < itemY + size.y; j++) {
                    if (mMapData[i][j] == true)
                        return false;
                }
            }
        }

        return true;
    }

    private void setIconPosition(int itemX, int itemY, Point size, boolean value){
        synchronized (mMapData) {
            for (int i = itemX; i < itemX + size.x; i++) {
                for (int j = itemY; j < itemY + size.y; j++) {
                    if (i < mMapSize.x && j < mMapSize.y)
                        mMapData[i][j] = value;
                }
            }
        }
    }
}
