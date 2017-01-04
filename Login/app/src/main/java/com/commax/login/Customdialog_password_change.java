package com.commax.login;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.commax.login.Common.TypeDef;

import java.util.regex.Pattern;

/**
 * Created by OWNER on 2016-08-02.
 */

public class Customdialog_password_change extends Dialog implements View.OnClickListener {
    String TAG = Customdialog_password_change.class.getSimpleName();
    Button mLeftButton;
    Button mRightButton;
    EditText password_change;
    EditText password_change_check;

    Context mContext;

    private static final int LENGTH = 20;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public Customdialog_password_change(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        requestWindowFeature(Window.FEATURE_NO_TITLE);

//        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
//        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        lpWindow.dimAmount = 0.8f;
//        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog_passchange);

        mLeftButton = (Button) findViewById(R.id.btn_cancel);
        mRightButton = (Button) findViewById(R.id.btn_ok);
        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);

        password_change = (EditText) findViewById(R.id.password_dialog_change);
        password_change_check = (EditText) findViewById(R.id.password_dialog_change_check);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(LENGTH);

        password_change.setFilters(filterArray); //입력길이 제한
        password_change.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()}); //숫자와 알파벳 특수기호 입력
        password_change.setPrivateImeOptions("defaultInputmode=english;");                 //영어 키패드 먼저 나오기

        password_change_check.setFilters(filterArray); //입력길이 제한
        password_change_check.setFilters(new InputFilter[]{new CustomInputFilter_alpha_num_ect()}); //숫자와 알파벳 특수기호 입력
        password_change_check.setPrivateImeOptions("defaultInputmode=english;");                 //영어 키패드 먼저 나오기

        SubActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_SHOW_ACTION);
    }

    //외곽 터치시 다이얼로그 닫히는 거 막기 위해서 onTuouchEvent 를 false 로 리턴한다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
        Rect dialogBuounds = new Rect();

        getWindow().getDecorView().getHitRect(dialogBuounds);
        if(!dialogBuounds.contains((int)ev.getX(), (int)ev.getY()))
        {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
*/

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
        Log.d(TAG, "ondismisslistener");
        SubActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
    }




   /* //edittext clear view
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) SubActivity.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }*/


    @Override
    public void onClick(View v) {

        if (v == mLeftButton) {
            Log.d("dialog1", "left");
            SubActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
            cancel();
        } else {
            Log.d("dialog1", "right");
            if (password_change.length() == 0) {
                SubActivity.getInstance().pxd_custom_toast(SubActivity.getInstance().getString(R.string.password_hint));
            } else if (password_change_check.length() == 0) {
                SubActivity.getInstance().pxd_custom_toast(SubActivity.getInstance().getString(R.string.password_check_hint));
            } else if (password_change.length() > 20 || password_change.length() < 6) {
                SubActivity.getInstance().pxd_custom_toast(SubActivity.getInstance().getString(R.string.password_length_check));
            } else {
                if (password_change.getText().toString().equals(password_change_check.getText().toString())) {
                    //비밀번호 관련 로그로 주석 처리
//                    Log.d(TAG, " pwd : " + password_change.getText().toString());
                    String[] pwd_change = {"v1", "user/me", "13", password_change.getText().toString()};
                    SubActivity.getInstance().startTask(pwd_change);
                    //                    new AppTask().execute("v1" , "user/me" , "13" , pwd.getText().toString());
                    SubActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
                    dismiss();
                } else {
                    //handler 처리
                    Message msg = SubActivity.getInstance().toastHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(SubActivity.getInstance().getString(R.string.password_passwordcheck_different));
                    SubActivity.getInstance().toastHandler.sendMessage(msg);
                }
            }
        }
    }


    protected class CustomInputFilter_alpha_num_ect implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[!@#$%^&*+=-_a-zA-Z0-9]+$");

            if (source.equals("") || ps.matcher(source).matches()) {

                return source;
            } else {
                SubActivity.getInstance().pxd_custom_toast(SubActivity.getInstance().getString(R.string.edit_alpha_num_etc_only));
                return "";
            }
        }
    }


}
