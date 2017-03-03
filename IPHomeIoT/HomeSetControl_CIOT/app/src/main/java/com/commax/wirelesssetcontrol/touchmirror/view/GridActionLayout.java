package com.commax.wirelesssetcontrol.touchmirror.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.touchmirror.common.Constants;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.res.CBitmapDrawable;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;

import java.util.ArrayList;

/**
 * Created by Shin Sung on 2017-02-07.
 * GridFrameLayout를 상속 받아 회피, 그림자 효과 구현
 */
public class GridActionLayout extends GridFrameLayout{
    private final String TAG = "GridActionLayout";

    private IconLayout[][] mMapDummy; //회피 좌표 설정을 위한 더미 데이터
    private Paint mPaint;

    private boolean mEnableGuideLine = false;

    public GridActionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int col, int row){ //필수 실행요소
        super.init(col, row);
        mMapDummy = new IconLayout[mMapSizeMax.x][mMapSizeMax.y];
        mPaint = new Paint();
    }

    /////////////////[public method]///////////////////
    //그리드 가이드 라인 켜기
    public void enableGuideLine(boolean enabled){
        mEnableGuideLine = enabled;
        setWillNotDraw(false);
        invalidate();
    }

    //드래그 효과
    private IconData mDragEffectData = null;
    private Point mDragEffectPoint;
    private IconLayout mDragIconLayout = null;
    private CBitmapDrawable mShadowDrawable = null;

    //드래그 효과 시작
    public void setDragEffect(IconData data){
        mDragEffectData = data;
        mDragEffectPoint = new Point(-1, -1);
    }

    //아이콘 그림자로 사용될 비트맵 설정
    public void setDragIconBitmap(Bitmap iconBitmap){
        mShadowDrawable = (CBitmapDrawable) BitmapTool.copy(getContext(), iconBitmap);
    }

    //드래그 효과 진행
    public void actionDragEffect(int x, int y){
        if(mDragEffectData != null){ //setDragEffect가 선행 되야함
            Point point = new Point(x/getIconWidth(), y/getIconHeight()); //그리드 좌표로 변환

            if(mDragEffectPoint.x != point.x || mDragEffectPoint.y != point.y){ //그리드 좌표에 변화가 있다면
                //지난 위치 그림자 아이콘 사라지기 효과
                if(mDragIconLayout != null)
                    fadeOutIcon();

                //현재위치 그림자 아이콘 생성
                createShadowIcon(x, y);

                //회피 액션 부분
                cancelAwayAction(true);
                Point size = mDragEffectData.getSize();
                if(checkAwayAction(point.x, point.y, size.x, size.y, mDragIconLayout))
                    awayAction();
                else
                    mDragIconLayout.setBackgroundColor(Color.RED);

                //현재 좌표 저장
                mDragEffectPoint.x = point.x;
                mDragEffectPoint.y = point.y;
            }
        }
    }

    //드래그 효과 종료
    public void stopDragEffect(){
        recycleShadowBitmap();
        mDragEffectData = null;
        mDragEffectPoint = null;
        mShadowDrawable = null;
        cancelAwayAction(false);
        if(mDragIconLayout != null) {
            mDragIconLayout.removeAllViews();
            removeView(mDragIconLayout);
        }
        mDragIconLayout = null;
    }

    //드래그 아이콘 보이기/숨기기
    public void showDragIcon(boolean show){
        if(mDragIconLayout != null){
            if(show)
                mDragIconLayout.setVisibility(View.VISIBLE);
            else {
                mDragEffectPoint.y -= 1; //드래그 효과 유지 용
                mDragIconLayout.setVisibility(View.INVISIBLE);
            }
        }

    }
    //회피로 인하여 업데이트가 일어 난 항목 조사
    public ArrayList<IconData> getUpdateData(){
        ArrayList<IconData> updateDataList = new ArrayList<IconData>();

        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                if (mMapData[i][j] != null && mMapData[i][j].getAwayActionState()) {
                    boolean isAdded = true;
                    for(int k=0; k<updateDataList.size(); k++){
                        if(updateDataList.get(k) == mMapData[i][j].getIconData()) {
                            isAdded = false;
                            break;
                        }
                    }
                    if(isAdded)
                        updateDataList.add(mMapData[i][j].getIconData());
                }
            }
        }

        return updateDataList;
    }

    //Apps 타입 중앙정렬 - 하단 정렬 전용, FIXED 타입은 우측 정렬시킴
    public void alignByCenterApps(){
        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                if (mMapData[i][j] != null){
                    IconData data = mMapData[i][j].getIconData();
                    if(data.getType() == IconData.TYPE_APPS || data.getComponentName().equals(Constants.DEFAULT_ITEM_APPS)){
                        Point result;
                        int center = mMapSizeMax.x/2;
                        if(i < center){ //좌측 영역
                            result = searchLeft(center, i, j);
                            if(result != null){
                                mMapData[i][j].cancelAwayAction(false);
                                mMapData[i][j].awayAction(result.x * getIconWidth(), j * getIconHeight());
                                mMapData[result.x][j] = mMapData[i][j];
                                mMapData[i][j] = null;
                            }
                        }
                        else if(i > center){ //우측 영역
                            result = searchRight(center, i, j);
                            if(result != null){
                                if(mMapData[result.x][j] != null){ //Apps이면
                                    Point nextResult = searchRight(result.x, mMapSizeMax.x, j); //이후에 빈 공간을 찾음
                                    if(nextResult != null){
                                        mMapData[result.x][j].cancelAwayAction(false);
                                        mMapData[result.x][j].awayAction(nextResult.x * getIconWidth(), j * getIconHeight());
                                        mMapData[i][j].cancelAwayAction(false);
                                        mMapData[i][j].awayAction(result.x * getIconWidth(), j * getIconHeight());

                                        mMapData[nextResult.x][j] = mMapData[result.x][j];
                                        mMapData[result.x][j] = mMapData[i][j];
                                        mMapData[i][j] = null;
                                    }
                                    else if(result.x < i){ //만약 Apps보다 오른편에 있다면 서로 위치를 바꿈
                                        mMapData[i][j].cancelAwayAction(false);
                                        mMapData[i][j].awayAction(result.x * getIconWidth(), j * getIconHeight());
                                        mMapData[result.x][j].cancelAwayAction(false);
                                        mMapData[result.x][j].awayAction(i * getIconWidth(), j * getIconHeight());

                                        IconLayout temp = mMapData[i][j];
                                        mMapData[i][j] = mMapData[result.x][j];
                                        mMapData[result.x][j] = temp;
                                    }
                                }
                                else { //빈 공간이면
                                    mMapData[i][j].cancelAwayAction(false);
                                    mMapData[i][j].awayAction(result.x * getIconWidth(), j * getIconHeight());
                                    mMapData[result.x][j] = mMapData[i][j];
                                    mMapData[i][j] = null;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /////////////////[private method]///////////////////
    //중심으로부터 좌측으로 탐색
    private Point searchLeft(int center, int x, int y){
        while (--center > x) {
            if (mMapData[center][y] == null) {  //빈공간이면 이동
                return new Point(center, y);
            }
        }
        return null;
    }

    //중심으로부터 우측으로 탐색
    private Point searchRight(int center, int x, int y){
        while (++center < x) {
            if (mMapData[center][y] == null||
                    (mMapData[center][y].getIconData().getComponentName().equals(Constants.DEFAULT_ITEM_APPS))) {  //빈공간이면 이동
                return new Point(center, y);
            }
        }
        return null;
    }

    //아이콘 사라짐 효과
    private void fadeOutIcon(){
        Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.ani_icon_fade_out);
        mDragIconLayout.setAnimation(fadeOut);

        postDelayed(new Runnable() {
            IconLayout layout = mDragIconLayout;
            @Override
            public void run() {
                removeView(layout);
            }
        }, Constants.FADE_OUT_TIME);
    }

    //그림자 아이콘 생성
    private void createShadowIcon(int x, int y){
        IconData dummyData = IconData.copy(mDragEffectData);

        if(mShadowDrawable != null)
            dummyData.setDrawable(mShadowDrawable);

        dummyData.setFullSize(true);

        mDragIconLayout = setIconDragPosition(x, y, dummyData);
        if (mDragIconLayout != null) {
            mDragIconLayout.setBackgroundResource(R.drawable.touch_border);
            mDragIconLayout.setAlpha(0.5f);
        }
    }

    /*** 아이템 회피 구현 부 ***/
    private ArrayList<IconLayout> layoutBufList = new ArrayList<IconLayout>();
    private boolean checkAwayAction(int x, int y, int width, int height, IconLayout layout){
        if(y < 0 || x < 0)
            return false;
        else if(x+width > mMapSizeMax.x || y+height > mMapSizeMax.y)
            return false;
        else if(mMapData[x][y] != null && mMapData[x][y].getIconData().getType() == IconData.TYPE_FIXED) //고정 타입이 자리잡고 있으면 pass
            return false;
        else if(mAddLock) //추가모드가 아니면 pass
            return false;

        copyMapData(mMapDummy);
        layoutBufList.clear();

        for (int j=y; j<y+height; j++) { //이동 목록 리스트업
            for (int i = x; i < x + width; i++) {
                if(mMapDummy[i][j] != null) {
                    boolean isAdd = true;
                    for (int k = 0; k < layoutBufList.size(); k++) {
                        if (layoutBufList.get(k) == mMapDummy[i][j]) {
                            isAdd = false;
                            break;
                        }
                    }
                    if(isAdd)
                        layoutBufList.add(mMapDummy[i][j]);
                }
            }
        }

        //데이터 셋팅
        for (int k = 0; k < layoutBufList.size(); k++) {
            IconLayout layoutBuf = layoutBufList.get(k);
            IconData dataBuf = layoutBuf.getIconData();

            clearIconPosition(mMapDummy, dataBuf.getPosition().x / getIconWidth(), dataBuf.getPosition().y / getIconHeight(),
                    dataBuf.getSize(), layoutBuf); //기존 자리 지움
        }
        setIconPosition(mMapDummy, x, y, new Point(width, height), layout); //터치 영역 설정

        //이동좌표 설정
        for (int k = 0; k < layoutBufList.size(); k++) {
            IconLayout layoutBuf = layoutBufList.get(k);
            Point point = layoutBuf.getIconData().getPosition();
            Point size = layoutBuf.getIconData().getSize();
            point = getEmptyIndex(point.x/getIconWidth(), point.y/getIconHeight(), size.x, size.y);
            if(point != null) {
                setIconPosition(mMapDummy, point.x, point.y, size, layoutBuf);
            }
            else
                return false;
        }
        return true;
    }

    //회피 액션 실행
    private void awayAction() {
        for(int j=0; j<mMapSizeMax.y; j++){
            for(int i=0; i<mMapSizeMax.x; i++){
                if (mMapData[i][j] != mMapDummy[i][j] && mMapData[i][j] != null && mMapDummy[i][j] != null) {
                    Point findPoint = findDummyXY(mMapData[i][j]);
                    if(findPoint != null)
                        mMapData[i][j].awayAction(findPoint.x * getIconWidth(), findPoint.y * getIconHeight());
                }
            }
        }
    }

    //회피 액션 취소
    public void cancelAwayAction(boolean action){
        for(int j=0; j<mMapSizeMax.y; j++) {
            for (int i = 0; i < mMapSizeMax.x; i++) {
                if (mMapData[i][j] != null) {
                    if(mMapData[i][j].getAwayActionState()){
                        mMapData[i][j].cancelAwayAction(action);
                    }
                }
            }
        }
    }

    //더미 맵에 설정된 좌표 반환
    private Point findDummyXY(IconLayout data){
        for(int j=0; j<mMapSizeMax.y; j++){
            for(int i=0; i<mMapSizeMax.x; i++){
                if(mMapDummy[i][j] == data)
                    return new Point(i, j);
            }
        }
        return null;
    }

    //이동을 위한 빈자리 탐색
    private Point getEmptyIndex(int x, int y, int width, int height){
        Point point = null;
        int rangeMax = (mMapSizeMax.x > mMapSizeMax.y) ? mMapSizeMax.x : mMapSizeMax.y;

        //해당 지점으로부터 원형 탐색
        for(int range=1; range<rangeMax; range++){
            for (int j=y-range; j<=y+range; j++) {
                for(int i=x-range; i<=x+range; i++) {
                    if(i == x-range || i == x+range || j == y-range || j == y+range) {  //테두리 검출
                        if (i >= 0 && j >= 0 && i < mMapSizeMax.x && j < mMapSizeMax.y) { //범위 검사
                            if(checkIconPosition(mMapDummy, i, j, width, height))
                                return new Point(i, j);
                        }
                    }
                }
            }
        }

        return point;
    }

    /*** drawing ***/
    private final int GUIDE_LINE_WIDTH = 10;
    private final int GUIDE_LINE_HEIGHT = 1;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mEnableGuideLine){
            for(int i=0; i<=mMapSizeMax.x; i++){
                for(int j=0; j<=mMapSizeMax.y; j++){
                    mPaint.setColor(Color.argb(255, 255, 255, 255));
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setStrokeWidth(0);
                    drawGrid(canvas, i, j);
                }
            }
        }
    }

    private void drawGrid(Canvas canvas, int i, int j) {
        canvas.drawRect(i * getIconWidth() - GUIDE_LINE_WIDTH,
                j * getIconHeight() - GUIDE_LINE_HEIGHT,
                i * getIconWidth() + GUIDE_LINE_WIDTH,
                j * getIconHeight() + GUIDE_LINE_HEIGHT, mPaint);

        canvas.drawRect(i * getIconWidth() - GUIDE_LINE_HEIGHT,
                j * getIconHeight() - GUIDE_LINE_WIDTH,
                i * getIconWidth() + GUIDE_LINE_HEIGHT,
                j * getIconHeight() + GUIDE_LINE_WIDTH, mPaint);
    }

    private void recycleShadowBitmap(){
        if(mShadowDrawable == null)
            return;

        if(mShadowDrawable instanceof CBitmapDrawable) {
            if(mShadowDrawable.isPossibleRecycle() && !mShadowDrawable.getBitmap().isRecycled()) {
                mShadowDrawable.getBitmap().recycle();
            }
        }
    }
}
