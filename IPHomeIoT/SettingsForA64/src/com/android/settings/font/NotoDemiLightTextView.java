package com.commax.newsetting.font;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Noto DemiLight 폰트가 적용된 TextView
 * Created by PrographerJ on 2015-07-17.
 */
public class NotoDemiLightTextView extends TextView {
    public NotoDemiLightTextView(Context context) {
        super(context);
        setType(context);
    }

    public NotoDemiLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public NotoDemiLightTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setType(context);
    }

   

    private void setType(Context context) {
        //asset에 폰트 복사
        //NotoSnat 경령화된 폰트 위치: https://github.com/theeluwin/NotoSansKR-Hestia
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "NotoSansKR-DemiLight-Hestia.otf"));
    }
}
