package com.commax.wirelesssetcontrol.touchmirror.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Shin sung on 2017-02-13.
 */
public class GridParentLayout extends FrameLayout{
    public GridParentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //해당 뷰에서 마진을 좌, 상 좌표를 뺀 값 반환
    public Point convertNoMarginXY(int x, int y){
        if(getParent() instanceof FrameLayout) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            x -= params.leftMargin;
            y -= params.topMargin;
        }
        else if(getParent() instanceof LinearLayout){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
            x -= params.leftMargin;
            y -= params.topMargin;
        }

        return new Point(x, y);
    }
}
