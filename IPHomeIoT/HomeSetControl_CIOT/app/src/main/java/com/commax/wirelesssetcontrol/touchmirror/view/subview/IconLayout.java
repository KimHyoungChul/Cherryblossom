package com.commax.wirelesssetcontrol.touchmirror.view.subview;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.wirelesssetcontrol.CustomAppWidgetHost;
import com.commax.wirelesssetcontrol.MainActivity;
import com.commax.wirelesssetcontrol.NameSpace;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.touchmirror.view.res.CBitmapDrawable;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.view.WidgetView;
import com.commax.wirelesssetcontrol.data.ResourceData;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.animate.IconMoveAnimation;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;

import org.w3c.dom.Text;

/**
 * Created by Shin Sung on 2017-02-07.
 *
 * [IconData를 기반으로 한 아이콘 뷰]
 */
public class IconLayout extends LinearLayout {
    private final String TAG = "IconLayout";

    private IconData mIconData;
    private Point mIconAreaSize; //아이콘 영역 사이즈

    private Point mOffset; //margin 값을 제외한 Offset

    //회피 액션 관련 변수
    private boolean isAwayAction = false;
    private Point mBackupPoint = null; //비켜주기 이전 좌표 저장
    private Context mContext;

    public IconLayout(Context context){
        super(context);
        mContext = context;
    };

    public IconLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setIconData(IconData data, Point IconAreaSize){
        mIconData = data;
        mIconAreaSize = IconAreaSize;
        mOffset = new Point(0,0);
        updateIconData();
        updatePosition();
    }

    //////////////// [public method] //////////////////
    public IconData getIconData(){
        return mIconData;
    }

    //위치 좌표 업데이트
    public void updatePosition(){
        this.post(new Runnable() {
            @Override
            public void run() {
                setMargin();
                setVisibility(View.VISIBLE);
            }
        });
    }

    //마진을 제외한 오프셋 설정
    public void setOffset(int offsetX, int offsetY){
        mOffset.x = offsetX;
        mOffset.y = offsetY;
    }

    /******회피 동작 관련 함수 [시작]******/
    //회피 액션이 설정중인지 확인
    public boolean getAwayActionState(){
        return isAwayAction;
    }

    //회피 액션 동작
    public void awayAction(int desX, int desY){
        if(isAwayAction)
            return;

        clearAnimation();
        isAwayAction = true;

        //현재 좌표 저장
        mBackupPoint = new Point(mIconData.getPosition().x, mIconData.getPosition().y);

        //목적지 설정 후 애니메이션
        mIconData.setX(desX);
        mIconData.setY(desY);
        moveAnimate(mBackupPoint.x, mBackupPoint.y, -(mBackupPoint.x - desX), -(mBackupPoint.y - desY));
    }

    //회피 액션 취소
    public void cancelAwayAction(boolean action){
        clearAnimation();
        isAwayAction = false;

        if(!action)
            return ;

        //현재 좌표 설정
        Point point = new Point(mIconData.getPosition().x, mIconData.getPosition().y);

        //저장된 좌표로 값변경
        mIconData.setX(mBackupPoint.x);
        mIconData.setY(mBackupPoint.y);

        //현재 좌표에서 저장 좌표로 이동
        moveAnimate(point.x, point.y, -(point.x - mBackupPoint.x), -(point.y - mBackupPoint.y));
    }
    /***회피 동작 관련 함수 [끝]***/

    //해당 레이아웃만 자원 반환
    public void drawableRecycle(){
        BitmapTool.recursive(getRootView());
    }


    //////////////// [private method] //////////////////
    //위치 및 크기 설정
    private void setMargin(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();

        if(getIconData().getType() == IconData.TYPE_WIDGET && getIconData().isAlignWidget()){ //Widget 추가 화면에서의 아이콘 정렬 설정 : size 1로 통일
            layoutParams.width = mIconAreaSize.x;
            layoutParams.height = mIconAreaSize.y;
        }
        else {
            layoutParams.width = getIconData().getSize().x * mIconAreaSize.x;
            layoutParams.height = getIconData().getSize().y * mIconAreaSize.y;
        }
        layoutParams.leftMargin = mIconData.getPosition().x + mOffset.x;
        layoutParams.topMargin = mIconData.getPosition().y + mOffset.y;
        setLayoutParams(layoutParams);
    }

    //아이콘 업데이트
    public void updateIconData(){
        try {
            ImageView iconImg = (ImageView) findViewById(R.id.icon_img);
            ImageView deviceIcon = (ImageView) findViewById(R.id.icon_device);

            Drawable drawable = mIconData.getDrawable();
            if (drawable != null)
                iconImg.setImageDrawable(drawable);
            else
                iconImg.setImageDrawable(BitmapTool.copy(mContext, R.drawable.android_default_icon));

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iconImg.getLayoutParams();
            if (mIconData.getType() == IconData.TYPE_APPS || mIconData.getType() == IconData.TYPE_FIXED) {
                if (getIconData().getImgResId() != IconData.NO_RESOURCE)
                    iconImg.setImageResource(getIconData().getImgResId());

                TextView iconName = (TextView) findViewById(R.id.icon_apps_name);
                iconName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                String name = mIconData.getName();

                if (getIconData().getPackageName().equals("com.commax.homeiot")) {
                    params.width = mIconAreaSize.y * mIconData.getSize().y;
                    params.height = mIconAreaSize.y * mIconData.getSize().y;
                    iconName.setVisibility(View.GONE);
                    TextView centerTxt = (TextView) findViewById(R.id.icon_center_txt);
                    centerTxt.setText(getIconData().getName());

                    FrameLayout.LayoutParams centerTxtParams = (FrameLayout.LayoutParams) centerTxt.getLayoutParams();
                    centerTxtParams.width = (int) (params.width * 0.57f);
                    centerTxt.setLayoutParams(centerTxtParams);

                    centerTxt.setVisibility(VISIBLE);
                } else if (name != null && name.length() > 0 && !getIconData().isFullSize()) {
                    LinearLayout.LayoutParams iconNameParams = (LayoutParams) iconName.getLayoutParams();
                    iconName.setText(name);
                    iconName.setVisibility(View.VISIBLE);
                    iconNameParams.width = (int) (params.width * 1.4f);
                    iconName.setLayoutParams(iconNameParams);

                    params.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.15f);
                    params.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.6f);
                    params.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.6f);
                } else {
					params.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.15f);
                    params.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.7f);
                    params.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.7f);
                    iconName.setVisibility(View.GONE);
                }
                iconImg.setLayoutParams(params);

            } else if (mIconData.getType() == IconData.TYPE_WIDGET) {
                TextView widgetName = (TextView) findViewById(R.id.icon_widget_name);
                TextView widgetSize = (TextView) findViewById(R.id.icon_widget_size);

                if (!mIconData.isRealWidget()) { //Preview 상태일 경우
                    if (mIconData.isFullSize()) {
                        params.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.07f);
                        params.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.85f);
                        params.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.85f);
                        widgetName.setVisibility(View.GONE);
                        widgetSize.setVisibility(View.GONE);
                    } else {
                        if (mIconData.isAlignWidget()) { //Widget 추가 화면에서의 아이콘 정렬 설정 : size 1로 통일
                            params.topMargin = (int) (mIconAreaSize.y * 0.26f);
                            params.width = (int) (mIconAreaSize.x * 0.55f);
                            params.height = (int) (mIconAreaSize.y * 0.55f);
                        } else {
                            params.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.26f);
                            params.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.55f);
                            params.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.55f);
                        }

                        widgetName.setText(mIconData.getName());
                        widgetName.setVisibility(View.VISIBLE);

                        widgetSize.setText(mIconData.getSize().x + "x" + mIconData.getSize().y);
                        widgetSize.setVisibility(View.VISIBLE);
                    }
                    iconImg.setLayoutParams(params);
                } else { // 실제 Widget 일 경우, WidgetHostView 만 보여주기 위해서 나머지 안보이게 처리
                    widgetName.setVisibility(View.GONE);
                    widgetSize.setVisibility(View.GONE);
                    iconImg.setVisibility(GONE);
                    addWidgetHostView();
                }
            } else if (mIconData.getType() == IconData.TYPE_DEVICE) {

                DeviceIconData deviceIconData = (DeviceIconData) mIconData;

                ResourceData resData;

                if (deviceIconData.isDeviceDefault()) {
                    //기본 이미지 설정
                    resData = ResourceManager.getDeviceResId("", deviceIconData.getDeviceType());
                } else {
                    resData = ResourceManager.getDeviceResId(deviceIconData.getStatus(), deviceIconData.getDeviceType());
                }

                //아이콘 영역
                FrameLayout.LayoutParams deviceParams = (FrameLayout.LayoutParams) deviceIcon.getLayoutParams();
                CBitmapDrawable deviceIconDrawable = (CBitmapDrawable) BitmapTool.copy(mContext, resData.arg2);
                deviceParams.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.08f);
                deviceParams.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.6f);
                deviceParams.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.6f);
                deviceIcon.setImageDrawable(deviceIconDrawable);
                deviceIcon.setLayoutParams(deviceParams);

                //아이콘 배경 영역
                iconImg.setImageDrawable(BitmapTool.copy(mContext, ResourceManager.getDeviceBgResId(resData.arg1)));
                params.topMargin = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.15f);
                params.width = (int) (mIconAreaSize.y * mIconData.getSize().x * 0.6f);
                params.height = (int) (mIconAreaSize.y * mIconData.getSize().y * 0.6f);
                iconImg.setLayoutParams(params);
                TextView iconName = (TextView) findViewById(R.id.icon_apps_name);
                iconName.setVisibility(View.VISIBLE);
                iconName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                //텍스트 영역
                LinearLayout.LayoutParams iconNameParams = (LayoutParams) iconName.getLayoutParams();
                iconNameParams.width = (int) (params.width * 1.4f);
                iconName.setLayoutParams(iconNameParams);
                iconName.setText(deviceIconData.getNickName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //위젯 호스트 뷰 추가
    public void addWidgetHostView(){
        mIconData.setRealWidget();

        AppWidgetProviderInfo appWidgetInfo = AppWidgetManager.getInstance(mContext.getApplicationContext()).getAppWidgetInfo(mIconData.getWidgetId());
        WidgetView widgetHostView = (WidgetView) MainActivity.getWidgetHost().createView(mContext, mIconData.getWidgetId(), appWidgetInfo);
        widgetHostView.setId(mIconData.getWidgetId());
        widgetHostView.setAppWidget(mIconData.getWidgetId(), appWidgetInfo);

        LinearLayout widgetContainer = (LinearLayout)findViewById(R.id.icon_host_view);
        widgetContainer.removeAllViews();

        widgetContainer.addView(widgetHostView);
        widgetContainer.setVisibility(VISIBLE);

        ViewGroup.LayoutParams params = widgetHostView.getLayoutParams();
        params.width =  mIconAreaSize.x * mIconData.getSize().x;
        params.height = mIconAreaSize.y * mIconData.getSize().y;
        widgetHostView.setLayoutParams(params);
    }

    //클릭 효과
    public void setClickMotion(boolean isCilck){
        if(isCilck)
            setAlpha(0.35f);
        else
            setAlpha(1f);
    }

    public void setCenterText(String str){
        TextView centerTxt = (TextView) findViewById(R.id.icon_center_txt);
        getIconData().setName(str);
        centerTxt.setText(str);
        centerTxt.setSelected(true);
    }

    //이동 애니메이션
    private IconMoveAnimation mAnimation;
    private void moveAnimate(final int fromX, final int fromY, final int moveWidth, final int moveHeight){
        mAnimation = new IconMoveAnimation(this, new Point(fromX, fromY), new Point(moveWidth, moveHeight), mOffset);
        mAnimation.setDuration(250);
        synchronized (mAnimation) {
            post(new Runnable() {
                @Override
                public void run() {
                    startAnimation(mAnimation);
                }
            });
        };
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        mAnimation = null;
    }
}
