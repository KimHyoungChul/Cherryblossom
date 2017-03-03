package com.commax.iphomiot.doorcall.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Sung Shin on 2016-08-24.
 * 내용 : Calling 애니메이션 적용 View
 */

/**
 * Activity 인스턴스화 후 아래의 public 메소드를 호출한다.
 *
 * [COMMAX]_IOT_Mobile_Screen_GuideLine 참조
 *   - 7.8.1 통화 모션 레퍼런스 구현 사항
 *
 * [Method]
 * public void startCallingAnimite() : calling 애니메이션 스레드를 시작한다.
 * public void stopCallingAnimite() : calling 애니메이션 스레드를 종료한다.
 */
public class CallingAnimateView extends ImageView implements Runnable {
    private final int CIRCLE_L = 1 << 0;
    private final int CIRCLE_M = 1 << 1;
    private final int CIRCLE_S = 1 << 2;
    private int circleFlag = CIRCLE_L | CIRCLE_M | CIRCLE_S;

    private final int ANI_INTERVAL = 250;

    private boolean callAnimateIsRun = true;
    private Paint circlePaint;
    private int radius = 0;

    private Thread aniThread = null;

    public CallingAnimateView(Context context, AttributeSet attrs){
        super(context, attrs);
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        radius = MeasureSpec.getSize(widthMeasureSpec)/2;
    }

    private void initPaint(){
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStrokeWidth(2);
    }

    //calling 애니메이션 시작
    public void startCallingAnimite(){
        if(aniThread == null) {
            aniThread = new Thread(this);
            callAnimateIsRun = true;
            aniThread.start();
        }
    }

    //calling 애니메이션 종료
    public void stopCallingAnimite(){
        callAnimateIsRun = false;
        aniThread = null;
    }

    @Override
    public void run() {
        while(callAnimateIsRun){
            setCircleFlag(CIRCLE_S);
            setCircleFlag(CIRCLE_S | CIRCLE_M);
            setCircleFlag(CIRCLE_S | CIRCLE_M | CIRCLE_L);
            setCircleFlag(CIRCLE_M | CIRCLE_L);
            setCircleFlag(CIRCLE_L);
        }
    }

    private void setCircleFlag(int flag){
        try {
            circleFlag = flag;
            postInvalidate();
            Thread.sleep(ANI_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //작은원
        if((circleFlag & CIRCLE_S) != 0)
            circlePaint.setAlpha(125); //call guide spec : 50%
        else
            circlePaint.setAlpha(0);
        canvas.drawCircle(radius, radius, radius*0.71f, circlePaint); //call guide spec : 71%

        //중간원
        if((circleFlag & CIRCLE_M) != 0)
            circlePaint.setAlpha(65); //call guide spec : 25%
        else
            circlePaint.setAlpha(0);
        canvas.drawCircle(radius, radius, radius*0.856f, circlePaint);  //call guide spec : 85.6%

        //큰원
        if((circleFlag & CIRCLE_L) != 0)
            circlePaint.setAlpha(13); //call guide spec : 5%
        else
            circlePaint.setAlpha(0);
        canvas.drawCircle(radius, radius, radius, circlePaint);  //call guide spec : 100%
    }
}
