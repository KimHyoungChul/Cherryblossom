package com.commax.wirelesssetcontrol.touchmirror.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IconGridViewPager extends ViewPager {
    public IconGridViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        try {
            return super.onInterceptHoverEvent(event);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
    }
}