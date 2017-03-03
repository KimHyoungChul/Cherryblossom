package com.commax.wirelesssetcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.R;

public class MovingSpaceCell extends LinearLayout implements View.OnLongClickListener{

    String TAG = MovingSpaceCell.class.getSimpleName();
    private	LayoutInflater inflater;
    TextView space_name;
    ImageView space_img;
    ImageButton btn_delete;
    FrameLayout valid_space;
    FrameLayout invalid_space;

    int index = -1;

    Handler mHandler;

    public MovingSpaceCell(Context context, Handler handler, String name, String img) {
        super(context);
        mHandler = handler;
        createView(context, name, img);
        setValid();
    }

    public MovingSpaceCell(Context context, AttributeSet attrs, Handler handler, String name, String img) {
        super(context, attrs);
        mHandler = handler;
        createView(context, name, img);
        setValid();
    }

    private void createView(Context context, String name, String img) {

        inflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.space_cell, null, false);

        space_name = (TextView) view.findViewById(R.id.space_name);
        space_img = (ImageView) view.findViewById(R.id.space_img);
        btn_delete = (ImageButton) view.findViewById(R.id.btn_delete);
        valid_space = (FrameLayout) view.findViewById(R.id.valid_space);
        invalid_space = (FrameLayout) view.findViewById(R.id.invalid_space);

        valid_space.setOnLongClickListener(this);
        addView(view);

        setSpaceImg(img);
        setSpaceName(name);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick");

        return false;
    }

    public void setLocation(float loc1, float loc2){
        try {
            setX(loc1);
            setY(loc2);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setValid(){
        valid_space.setVisibility(VISIBLE);
        invalid_space.setVisibility(INVISIBLE);
    }

    public int getIndex(){
        return index;
    }

    public void setSpaceImg(String img_name){

        switch (img_name){
            case "0":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_1));
                break;

            case "1":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_1));
                break;

            case "2":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_2));
                break;

            case "3":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_3));
                break;

            case "4":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_4));
                break;

            case "5":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_5));
                break;

            case "6":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_6));
                break;

            case "7":
                space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_7));
                break;

            default:
                try{
                    FileEx fileEx = new FileEx();
                    Bitmap bitmap = fileEx.findImage(img_name,
                            (int)getResources().getDimension(R.dimen.space_cell_img_width),
                            (int)getResources().getDimension(R.dimen.space_cell_img_height));
                    boolean failed = false;

                    if (bitmap==null){
                        failed=true;
                    }else {
                        try {
                            Drawable drawable = new BitmapDrawable(bitmap);
                            space_img.setBackground(drawable);
                        }catch (Exception e){
                            failed=true;
                            e.getMessage();
                        }
                    }

                    if (failed){
                        space_img.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.bg_home_img_sm_1, null));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

        if(img_name.equalsIgnoreCase("0")){
            space_img.setBackground(getResources().getDrawable(R.mipmap.bg_home_img_sm_1));
        }else {

        }
    }

    public void setSpaceIndex(int index){
        this.index = index;
    }

    public int getSpaceIndex(){
        return index;
    }

    public void setSpaceName(String name) {
        space_name.setText(name);
    }
}
