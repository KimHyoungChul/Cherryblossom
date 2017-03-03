package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

  
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);  
          
        initView(context, attrs);  
    }  
  
    public CustomTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
          
        initView(context, attrs);  
    }  
  
    public CustomTextView(Context context) {  
        super(context);  
    }  
      
    private void initView(Context context, AttributeSet attrs) {
        try {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            boolean stroke = a.getBoolean(R.styleable.CustomTextView_textStroke, false);
            float strokeWidth = (getResources().getDimension(R.dimen.text_stroke_width));

            if (stroke) {
                setShadowLayer(strokeWidth, 0, 0, Color.BLACK);
            }

            a.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}  
