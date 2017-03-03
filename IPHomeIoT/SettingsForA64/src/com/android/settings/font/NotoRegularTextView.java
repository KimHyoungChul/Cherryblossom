package com.commax.newsetting.font;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Noto Regular 폰트가 적용된 TextView
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class NotoRegularTextView extends TextView {
    public NotoRegularTextView(Context context) {
        super(context);
        setType(context);
    }

    public NotoRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }







    private void setType(Context context) {
        //asset에 폰트 복사
        //NotoSnat 경령화된 폰트 위치: https://github.com/theeluwin/NotoSansKR-Hestia
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "NotoSansKR-Regular-Hestia.otf"));
    }
}
