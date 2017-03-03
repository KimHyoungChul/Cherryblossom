package com.commax.settings.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.commax.settings.R;

/**
 * 버블 텍스트가 포함된 커스텀 SeekBar
 * Created by bagjeong-gyu on 2016. 9. 29..
 */

public class SeekBarWithBubble extends LinearLayout {

    TextView mBubble = null;
    MySeekBar mSeekbar = null;

    private int mBubbleHalfWidth;
    private int mMarginLeftLength;

    public SeekBarWithBubble(Context context) {
        this(context, null);
    }

    public SeekBarWithBubble(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarWithBubble(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //Inflate and attach the content
        LayoutInflater.from(context).inflate(R.layout.seekbar_with_bubble, this);

        mBubble = (TextView) findViewById(R.id.bubble);
        mSeekbar = (MySeekBar) findViewById(R.id.seekbar);

        setBubbleHalfWidth();
        setMarginLeftLength();

    }

    private void setMarginLeftLength() {

        mMarginLeftLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    }

    private void setBubbleHalfWidth() {
        float bubbleWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, getResources().getDisplayMetrics());
        mBubbleHalfWidth = (int) (bubbleWidthPx / 2);
    }

    public void setBubbleText(String text) {
        mBubble.setText(text);
    }

    public void setBubblePosition() {
        LayoutParams p = new LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //RelativeLayout을 사용하는 경우 아래 코드 사용
        //p.addRule(RelativeLayout.ABOVE, mSeekbar.getId());
        Rect thumbRect = mSeekbar.getSeekBarThumb().getBounds();
        p.setMargins(
                thumbRect.centerX() - mBubbleHalfWidth + mMarginLeftLength, 0, 0, 0);


        if (!mBubble.isShown()) {
            mBubble.setVisibility(View.VISIBLE);
        }


        mBubble.setLayoutParams(p);


    }

    public void setMax(int i) {
        mSeekbar.setMax(i);
    }

    public void setProgress(int i) {
        mSeekbar.setProgress(i);
    }

    public void setThumb(Drawable thumb) {
        mSeekbar.setThumb(thumb);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }
}
