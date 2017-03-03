package com.commax.wirelesssetcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.R;

public class SpaceCell extends LinearLayout implements View.OnLongClickListener, View.OnTouchListener, View.OnClickListener{

    String TAG = SpaceCell.class.getSimpleName();
    private	LayoutInflater inflater;
    TextView space_name;
    ImageView space_img;
    ImageButton btn_delete;
    FrameLayout valid_space;
    FrameLayout invalid_space;

    Context mContext;
    String name = "";
    String img="0";
    int index=-1;
    Handler mHandler;
    boolean clicked = false;
    int top = 0;

    MovingSpaceCell movingSpaceCell;
    int windowwidth=0;
    int windowheight=0;
    int offsetX;
    int offsetY;

    public SpaceCell(Context context, Handler handler) {
        super(context);
        mContext = context;
        mHandler = handler;
        createView(context);
    }

    public SpaceCell(Context context, AttributeSet attrs, Handler handler) {
        super(context, attrs);
        mContext = context;
        mHandler = handler;
        createView(context);
    }

    private void createView(Context context) {

        inflater = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.space_cell, null, false);

        space_name = (TextView) view.findViewById(R.id.space_name);
        space_img = (ImageView) view.findViewById(R.id.space_img);
        btn_delete = (ImageButton) view.findViewById(R.id.btn_delete);
        valid_space = (FrameLayout) view.findViewById(R.id.valid_space);
        invalid_space = (FrameLayout) view.findViewById(R.id.invalid_space);

        valid_space.setOnLongClickListener(this);
        valid_space.setOnTouchListener(this);
        valid_space.setOnClickListener(this);
        addView(view);

        top = (int)getResources().getDimension(R.dimen.header_height);

        try {
            offsetX = (int)(getResources().getDimension(R.dimen.space_cell_width)/2);
            offsetY = (int)(getResources().getDimension(R.dimen.space_cell_height)/2);
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            WindowManager windowManager = (WindowManager) context
                    .getSystemService(Context.WINDOW_SERVICE);
            Point p = new Point();
            windowManager.getDefaultDisplay().getSize(p);

            windowwidth = p.x;
            windowheight = p.y;
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        Log.d(TAG, "onClick");

        try {
            Message start_page_edit = mHandler.obtainMessage();
            start_page_edit.what = HandlerEvent.EVENT_HANDLE_START_EDIT_SPACE;
            start_page_edit.obj = index;
            mHandler.sendMessage(start_page_edit);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick");

        setClicked();

        movingSpaceCell = new MovingSpaceCell(mContext, mHandler, name, img);

        int[] location = new int[2];
        int top = (int)getResources().getDimension(R.dimen.header_height);
        try {
            getLocationInWindow(location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "location = x :" + location[0] + " y :" + (location[1]-top));

        Message removeViewMsg = mHandler.obtainMessage();
        removeViewMsg.what = HandlerEvent.EVENT_HANDLE_ADD_MOVING_SPACE;
        removeViewMsg.obj = movingSpaceCell;
        removeViewMsg.arg1 = location[0];
        removeViewMsg.arg2 = (location[1]-top);
        mHandler.sendMessage(removeViewMsg);

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (clicked) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:

                    float _x = event.getRawX()-offsetX;
                    float _y = event.getRawY()-top-offsetY;

                    if (_x > (windowwidth - (offsetX * 2))) {
                        _x = (windowwidth - (offsetX * 2));
                    }

                    if (_x < 0) {
                        _x = 0;
                    }

                    if (_y > (windowheight - (offsetY*2) - top)) {
                        _y = (windowheight - (offsetY*2) - top);
                    }

                    if (_y < 0) {
                        _y = 0;
                    }

//                    Log.d(TAG, "move to " + event.getRawX() + " / " + event.getRawY());

                    movingSpaceCell.setX(_x);
                    movingSpaceCell.setY(_y);

                    break;

                case MotionEvent.ACTION_UP:

                    setUnClicked();

                    movingSpaceCell.setSpaceIndex(index);
                    Message removeViewMsg = mHandler.obtainMessage();
                    removeViewMsg.what = HandlerEvent.EVENT_HANDLE_CHECK_MOVING_SPACE;
                    removeViewMsg.obj = movingSpaceCell;
                    removeViewMsg.arg1 = (int)event.getRawX();
                    removeViewMsg.arg2 = (int)(event.getRawY()-top);
                    mHandler.sendMessage(removeViewMsg);

                    break;

                default:
                    break;
            }
        }
        return false;
    }

    public void setClicked(){
        clicked=true;
    }

    public void setUnClicked(){
        clicked=false;
    }

    public void setValid(){
        valid_space.setVisibility(VISIBLE);
        invalid_space.setVisibility(INVISIBLE);
    }

    public int getIndex(){
        return index;
    }

    public ImageButton getDeleteBtn(){
        return btn_delete;
    }

    public void SetDeleteBtnInvisible(boolean visible){
        if (visible) {
            btn_delete.setVisibility(VISIBLE);
        }else {
            btn_delete.setVisibility(INVISIBLE);
        }
    }


    public void setInvalid(){
        valid_space.setVisibility(INVISIBLE);
        invalid_space.setVisibility(VISIBLE);
    }

    public void setSpaceImg(String img_name){

        img = img_name;

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

    public void setSpaceName(String name) {
        space_name.setText(name);
        this.name = name;
    }

    public void setSpaceIndex(int index){
        this.index = index;
    }

    public int getSpaceIndex(){
        return index;
    }

//
//    public void setItemImageDrawable(Drawable d) {
//
//        imageView.setImageDrawable(d);
//    }

}
