package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuItem extends LinearLayout {

    Context mContext;

    TextView textView1;
    ImageView imageView1;

    public MenuItem(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init(){

        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_menu, null, false);

        textView1 = (TextView) view.findViewById(R.id.textView1);
        imageView1 = (ImageView) view.findViewById(R.id.imageView1);

        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        addView(view, params);
    }

    public void setItemImageDrawable(Drawable d) {

        try {
            imageView1.setImageDrawable(d);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setItemText(String title) {
        try {
            textView1.setText(title);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
