package com.commax.commaxwidget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by OWNER on 2017-02-20.
 */

public class CustomImageView extends ImageView{
    private int value = 0;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defstyle) {
        super(context, attrs, defstyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("test","===== onDraw");
//       Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.FILL);
//        canvas.drawRect(0,0, getWidth() * 0.05f, value, paint);
    }


    public void setValue(int value){
        this.value = value;
        invalidate();
    }
}
