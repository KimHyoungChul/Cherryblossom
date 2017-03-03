package com.commax.wirelesssetcontrol.touchmirror.view.animate;

import android.graphics.Point;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by OWNER on 2017-02-15.
 */
public class IconMoveAnimation extends Animation{
    private Point mFrom, mSize, mOffset;
    private LinearLayout mTargetView;

    public IconMoveAnimation(LinearLayout targetView, Point from, Point size,  Point offset){
        mTargetView = targetView;
        mFrom = from;
        mSize = size;
        mOffset = offset;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if(mTargetView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTargetView.getLayoutParams();
            layoutParams.leftMargin = mFrom.x + (int) (mSize.x * interpolatedTime) + mOffset.x;
            layoutParams.topMargin = mFrom.y + (int) (mSize.y * interpolatedTime) + mOffset.y;
            mTargetView.setLayoutParams(layoutParams);
        }
        else{
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mTargetView.getLayoutParams();
            layoutParams.leftMargin = mFrom.x + (int) (mSize.x * interpolatedTime) + mOffset.x;
            layoutParams.topMargin = mFrom.y + (int) (mSize.y * interpolatedTime) + mOffset.y;
            mTargetView.setLayoutParams(layoutParams);
        }
    }
}

