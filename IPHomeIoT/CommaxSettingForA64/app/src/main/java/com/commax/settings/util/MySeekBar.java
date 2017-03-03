package com.commax.settings.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * 커스텀 SeekBar
 * Created by bagjeong-gyu on 2016. 9. 29..
 */

public class MySeekBar extends SeekBar {

    Drawable mThumb;

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
    }


    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    public Drawable getSeekBarThumb() {
        return mThumb;
    }
}
