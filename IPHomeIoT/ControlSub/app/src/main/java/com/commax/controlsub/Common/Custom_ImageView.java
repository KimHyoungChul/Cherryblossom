package com.commax.controlsub.Common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.commax.controlsub.R;

/**
 * Created by gbg on 2016-08-05.
 */
public class Custom_ImageView extends ImageView {

    static final String TAG = "Custom_ImageView";

    // Context
    Context mContext;

    float getalpha_press  = 0.6F;
    float getalpha_normal = 1.0F;

    public Custom_ImageView(Context context) {
        super(context);
        init(context);
    }

    public Custom_ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

        //Get Parameter
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Custom_ImageView);
        getalpha_normal= a.getFloat(R.styleable.Custom_ImageView_layout_alpha_normal, 1.0F);
        getalpha_press= a.getFloat(R.styleable.Custom_ImageView_layout_alpha_pressed, 1.0F);
        Log.d(TAG, "Custom_ImageView : " + getalpha_press);

        this.setAlpha(getalpha_normal);
        this.setOnTouchListener(new OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d(TAG, "onTouch : " + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_DOWN) v.setAlpha(getalpha_press);
                if (event.getAction() == MotionEvent.ACTION_UP)   v.setAlpha(getalpha_normal);
                return false;
            }
        });

        a.recycle();
    }

    private void init(Context context){

        mContext = context.getApplicationContext();
    }
}
