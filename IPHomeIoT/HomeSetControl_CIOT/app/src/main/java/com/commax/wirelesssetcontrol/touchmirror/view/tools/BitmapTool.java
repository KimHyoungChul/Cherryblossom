package com.commax.wirelesssetcontrol.touchmirror.view.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.commax.wirelesssetcontrol.touchmirror.view.res.CBitmapDrawable;


/**
 * Created by Shin sung on 2017-02-21.
 */
public class BitmapTool {
    //res id로 부터 객체 복사
    public static Drawable copy(Context context, int resId){
        Drawable drawable = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
                drawable = context.getApplicationContext().getResources().getDrawable(resId);
            else
                drawable = ResourcesCompat.getDrawable(context.getApplicationContext().getResources(), resId, null);
        }catch(Exception e){
            Log.d("BitmapTool", e.getMessage());
        }

        if(drawable == null)
            return null;

        Bitmap bitmap = null;
        if(drawable instanceof StateListDrawable) {
            DrawableContainer.DrawableContainerState contiainerState = (DrawableContainer.DrawableContainerState) drawable.getConstantState();
            Drawable[] children = contiainerState.getChildren();
            for (Drawable child : children) {
                if(child instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) child).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    break;
                }
            }
        }
        else
            bitmap = ((BitmapDrawable)drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);

        CBitmapDrawable cDrawable = new CBitmapDrawable(context.getApplicationContext().getResources(), bitmap);
        cDrawable.setPossibleRecycle();
        return cDrawable;
    }

    //Drawable로 부터 객체 복사
    public static Drawable copy(Context context, Drawable drawable){
        if(drawable == null)
            return null;

        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        CBitmapDrawable cDrawable = new CBitmapDrawable(context.getApplicationContext().getResources(), bitmap);
        cDrawable.setPossibleRecycle();
        return cDrawable;
    }

    //Bitmap으로 부터 객체 복사
    public static Drawable copy(Context context, Bitmap bitmap){
        if(bitmap == null)
            return null;

        CBitmapDrawable cDrawable = new CBitmapDrawable(context.getApplicationContext().getResources(), bitmap.copy(Bitmap.Config.ARGB_8888, true));
        cDrawable.setPossibleRecycle();
        return cDrawable;
    }

    public static void recursive(View root){
        if (root == null)
            return;

        Drawable background = root.getBackground();
        if(root instanceof ImageView) {
            Drawable drawable = ((ImageView) root).getDrawable();

            if (drawable != null) {
                try {
                    drawable.setCallback(null);
                    if(drawable instanceof CBitmapDrawable){
                        CBitmapDrawable cDrawble = (CBitmapDrawable) drawable;
                        if(cDrawble.isPossibleRecycle()){
                            if(cDrawble.getBitmap() != null && !cDrawble.getBitmap().isRecycled())
                                cDrawble.getBitmap().recycle();
                        }
                    }
                    ((ImageView) root).setImageDrawable(null);
                } catch (ClassCastException e) {
                    Log.d("RecycleTool", "ClassCastException : " + e.getMessage());
                }
            }
        }

        if(background != null){
            try {
                background.setCallback(null);
                if(background instanceof CBitmapDrawable){
                    CBitmapDrawable cDrawble = (CBitmapDrawable) background;
                    if(cDrawble.isPossibleRecycle()){
                        if(cDrawble.getBitmap() != null && !cDrawble.getBitmap().isRecycled())
                            cDrawble.getBitmap().recycle();
                    }
                }
                root.setBackground(null);
            }catch(ClassCastException e){
                Log.d("RecycleTool", "ClassCastException : " + e.getMessage());
            }
        }

        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)root;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                recursive(group.getChildAt(i));
            }

            if (!(root instanceof AdapterView)) {
                group.removeAllViews();
            }
        }
    }

    //블러 효과 주기
    public static CBitmapDrawable blur(Context context, CBitmapDrawable drawable, int radius) {
        try {
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, drawable.getBitmap(), Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(drawable.getBitmap());
        }catch(Exception e){
            Log.d("BitmapTool", e.getMessage());
        }

        return drawable;
    }
}
