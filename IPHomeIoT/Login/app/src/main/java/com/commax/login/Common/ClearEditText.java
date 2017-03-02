package com.commax.login.Common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.commax.login.R;


/**
 * Created by TedPark on 16. 4. 11..
 */

/* TODO extends 넥셀 : EditText A20 : AppCompatEditText*/
public class ClearEditText extends AppCompatEditText implements TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {

    String TAG = ClearEditText.class.getSimpleName();
    private Drawable clearDrawable;
    private OnFocusChangeListener onFocusChangeListener;
    private OnTouchListener onTouchListener;
    Context mContext;

    public ClearEditText(final Context context) {
        super(context);
        init();
        mContext = context;
    }

    public ClearEditText(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
        mContext = context;
    }

    public ClearEditText(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        mContext = context;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }


    private void init() {

        DisplayMetrics dm = getContext().getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
//        Log.v("Clear Edit ", "width: " + width + " , height : " + height);
        Drawable tempDrawable;
        if(TypeDef.ip_home_iot_new_ui)
        {
            tempDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.btn_title_close_white_n);
//            setHintTextColor(getResources().getColor(R.color.text_color));
        }
        else
        {
            tempDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.btn_coach_close);
//            setHintTextColor(getResources().getColor(R.color.popup_edit_text_color));
        }
        //R.drawable.abc_ic_clear_mtrl_alpha);
        clearDrawable = DrawableCompat.wrap(tempDrawable);
//        DrawableCompat.setTintList(clearDrawable, getHintTextColors());
        if (width == 1920) {
            clearDrawable.setBounds(0, 0, 60, 60);
        } else {
            clearDrawable.setBounds(0, 0, 30, 30);
        }
//        Log.i("ClearEdittext", "x button Width : " + clearDrawable.getIntrinsicWidth() + " height : " + clearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);


        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }


    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }

        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }


    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (clearDrawable.isVisible() && x > getWidth() - getPaddingRight() - clearDrawable.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setError(null);
                setText(null);
            }
            return true;
        }

        if (onTouchListener != null) {
            return onTouchListener.onTouch(view, motionEvent);
        } else {
            return false;
        }

    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private void setClearIconVisible(boolean visible) {
        clearDrawable.setVisible(visible, false);
        setCompoundDrawables(null, null, visible ? clearDrawable : null, null);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
           /* InputMethodManager mgr = (InputMethodManager)
                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);*/
            // TODO: Hide your view as you do it in your activity
            Log.e(TAG, "onKeyPrelme");
            //TODO   임시 주석 처리
//            MainActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
        }
        return false;
    }


}
