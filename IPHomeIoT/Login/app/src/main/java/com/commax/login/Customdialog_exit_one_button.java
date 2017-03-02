package com.commax.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.TypeDef;

/**
 * Created by OWNER on 2016-08-02.
 */
public class Customdialog_exit_one_button extends Dialog implements View.OnClickListener {
    AboutFile aboutFile = new AboutFile();
    String TAG = Customdialog_exit_one_button.class.getSimpleName();

    TextView mTitle;
    TextView mComponent;
    Button mLeftButton;
    Button mRightButton;
    String[] mparams;
    Context mContext;


    private static final int LENGTH = 20;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public Customdialog_exit_one_button(Context context, String[] params) {
        super(context);
        mContext = context;
        mparams = params;
        Log.d(TAG, "mparams : " + mparams);
        if(TypeDef.IP_Home_IoT_navigation)
        {
            clearNavigationBar();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        * mparam[0] : title
        * mparam[1] : componene text
        * mparam[2] : MainAcitivity , SubActivity
        * mparam[3] : ux dialog , network dilaog
        * */

        // 다이얼로그 외부 화면 흐리게 표현
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(TypeDef.Lotte_navigation)
        {
            if(mparams[2].equals(TypeDef.MainActivity))
            {
                MainActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
            }
            else if(mparams[2].equals(TypeDef.SubActivity))
            {
                SubActivity.getInstance().sendCustomBroadcastMessage(TypeDef.SYSTEM_KEY_HIDE_ACTION);
            }
        }
        else if(TypeDef.IP_Home_IoT_navigation)
        {
            hideNavigationBar();
        }

        setContentView(R.layout.custom_dialog_initialize);

        mTitle = (TextView)findViewById(R.id.title_text);
        mComponent = (TextView)findViewById(R.id.component);
        mLeftButton = (Button) findViewById(R.id.btn_cancel);
        mRightButton = (Button) findViewById(R.id.btn_ok);

        mLeftButton.setOnClickListener(this);
        mRightButton.setOnClickListener(this);

        if(mparams[3].equals(TypeDef.network_dialog) || mparams[3].equals(TypeDef.ux_dialog))
        {
            mLeftButton.setVisibility(View.GONE);
        }
        else if(mparams[3].equals(TypeDef.uc_dialog))
        {
            mLeftButton.setVisibility(View.GONE);
            mRightButton.setText(mparams[4]);
        }

        mTitle.setText(mparams[0]);
        mComponent.setText(mparams[1]);
    }

    //외곽 터치시 다이얼로그 닫히는 거 막기 위해서 onTuouchEvent 를 false 로 리턴한다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == mRightButton) {
            if(mparams[3].equals(TypeDef.network_dialog))
            {
                dismiss();
                if(mparams[2].equals(TypeDef.MainActivity))
                {
                    MainActivity.getInstance().finish();
                }
                else if(mparams[2].equals(TypeDef.SubActivity))
                {
                    SubActivity.getInstance().finish();
                }
            }
            else if(mparams[3].equals(TypeDef.ux_dialog))
            {
                dismiss();
            }
            else if(mparams[3].equals(TypeDef.uc_dialog))
            {
                SubActivity.getInstance().access_uc_api_call();
                dismiss();
            }
        }
        else if(v == mLeftButton)
        {
        }
    }

    //보슬이 네비게이션 바 하이드
    private void hideNavigationBar(){

        try {
            // 액티비티 아래의 네비게이션 바가 안보이게
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clearNavigationBar(){

        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            ((Activity)mContext).getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
