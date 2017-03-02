package com.commax.controlsub;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.commax.controlsub.Common.TwoLevelCircularProgressBar;


/**
 * Created by OWNER on 2016-12-22.
 */
public class DialogActivity extends Activity {
    String TAG = DialogActivity.class.getSimpleName();

    public static DialogActivity _instance;
    public static DialogActivity getInstance()  {
        return _instance;
    }

    Button mRightButton;
    TextView title_text;
    TextView component_text;
    TwoLevelCircularProgressBar circle_progressbar;

    int sec = 0;
    boolean circle_progress_timer_flag = true;

    @Override
    protected void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try
        {
            setContentView(R.layout.custom_dialog_one_button);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        _instance = this;

        hideNavigationBar();

        init();
    }

    public void init()
    {
        mRightButton = (Button) findViewById(R.id.btn_ok);
        title_text = (TextView)findViewById(R.id.title_text);
        component_text = (TextView)findViewById(R.id.popup_component);

        title_text.setText(MainActivity.getInstance().getString(R.string.menu_add_string));
        component_text.setText(MainActivity.getInstance().getString(R.string.popup_device_add_text));

        circle_progressbar = (TwoLevelCircularProgressBar)findViewById(R.id.circle_progressbar);

        mHandler.sendEmptyMessage(0);
    }

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

    public void  onClick(View view)
    {
        if(view.equals(mRightButton))
        {
            MainActivity.getInstance().sendDeviceAddcancleCommand();
            circle_progress_timer_flag =  false;
            finish();
        }
    }

    //외곽 터치시 다이얼로그 닫히는 거 막기 위해서 onTuouchEvent 를 false 로 리턴한다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //** 초시간을 잰다 *//*
            if(circle_progress_timer_flag)
            {
                circle_progressbar.setProgressValue(sec);
                if(sec >= 60){
                    Log.d(TAG, "over 1min");
                    MainActivity.getInstance().showToastOnWorking(getString(R.string.try_agin));
                    finish();
                }
                else
                {
                    sec++;
                    this.sendEmptyMessageDelayed(0, 1000);
                }
            }
        }
    };
}
