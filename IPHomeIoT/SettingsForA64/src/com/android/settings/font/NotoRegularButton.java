package com.commax.newsetting.font;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

/**
 * Noto Regular 폰트가 적용된 Button
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class NotoRegularButton extends Button {
    public NotoRegularButton(Context context) {
        super(context);
        setType(context);
    }

    public NotoRegularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoRegularButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

   

    private void setType(Context context) {
        //asset에 폰트 복사
        //NotoSnat 경령화된 폰트 위치: https://github.com/theeluwin/NotoSansKR-Hestia
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "NotoSansKR-Regular-Hestia.otf"));
    }
}
