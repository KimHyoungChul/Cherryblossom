package com.commax.wirelesssetcontrol;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

public class ResizeAnimation extends Animation {
    private LinearLayout mView;
    private float mToHeight;
    private float mFromHeight;

    private float mToWidth;
    private float mFromWidth;

    public ResizeAnimation(LinearLayout v) {
        mToHeight = v.getHeight();
        mToWidth = 0;
        mFromHeight = v.getHeight();
        mFromWidth = v.getWidth();
        mView = v;
        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams)mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        p.leftMargin=0;
        mView.requestLayout();
        mView.setVisibility(View.INVISIBLE);
        mView.requestFocus();
    }

    @Override
    public void setAnimationListener(AnimationListener listener) {
        super.setAnimationListener(listener);
    }
}