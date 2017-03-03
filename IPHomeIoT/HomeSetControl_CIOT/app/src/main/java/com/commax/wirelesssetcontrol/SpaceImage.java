package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by OWNER on 2017-02-15.
 */
public class SpaceImage extends LinearLayout {

    Context mContext;
    boolean selected = false;

    ImageView iv_image;
    ImageView iv_check;

    String file_name;

    public SpaceImage(Context context) {
        super(context);
        init(context);
    }

    public SpaceImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.space_image, this);

//        FrameLayout lay_image = (FrameLayout) rootView.findViewById(R.id.lay_image);
        iv_check = (ImageView) rootView.findViewById(R.id.iv_check);
        iv_image = (ImageView) rootView.findViewById(R.id.iv_image);

    }

    public void setImage(Drawable drawable, String fileName){
        try{
            iv_image.setBackground(drawable);
            file_name=fileName;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setImage(Bitmap bitmap, String fileName){
        try{
            Drawable drawable = new BitmapDrawable(bitmap);
            iv_image.setBackground(drawable);
            file_name=fileName;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateCheck(){
        if (selected){
            iv_check.setVisibility(VISIBLE);
        }else {
            iv_check.setVisibility(INVISIBLE);
        }
    }

    public void setSelected(){
        selected = true;
        updateCheck();
    }

    public void setUnSelected(){
        selected = false;
        updateCheck();
    }

    public boolean isSelected(){
        return selected;
    }

    public String getFileName(){
        return file_name;
    }

}
