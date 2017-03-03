package com.commax.homereporter;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class ImageBT extends ImageButton implements View.OnTouchListener {

    private float imageOpacity = 0.0f;

    public ImageBT(Context context) {
        super(context);
        setOnTouchListener(this);
    }

    public ImageBT(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageBT);
        imageOpacity = a.getFloat(R.styleable.ImageBT_imageOpacity, 0.0f);
        setOnTouchListener(this);
    }

    public ImageBT(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.setAlpha(imageOpacity);
                    break;
                case MotionEvent.ACTION_UP:
                    v.setAlpha(1.0f);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
