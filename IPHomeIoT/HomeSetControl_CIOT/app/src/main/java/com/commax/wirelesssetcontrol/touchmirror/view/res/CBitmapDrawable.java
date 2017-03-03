package com.commax.wirelesssetcontrol.touchmirror.view.res;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Shin on 2017-02-22.
 */
public class CBitmapDrawable extends BitmapDrawable {
    private boolean mPossibleRecycle = false;

    public CBitmapDrawable(Resources res, Bitmap bitmap){
        super(res, bitmap);
    }

    public void setPossibleRecycle(){
        mPossibleRecycle = true;
    }

    public boolean isPossibleRecycle(){
        return mPossibleRecycle;
    }
}
