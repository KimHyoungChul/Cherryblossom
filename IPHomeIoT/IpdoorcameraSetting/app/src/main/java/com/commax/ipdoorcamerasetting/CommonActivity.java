package com.commax.ipdoorcamerasetting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 공통 액티비티
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CommonActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
    }

    /**
     * 화면 타이틀 표시
     * @param title
     */
    protected void setScreenTitle(String title) {
        TextView screenTitle = (TextView) findViewById(R.id.screenTitle);
        screenTitle.setText(title);
    }

    /**
     * 전체화면 표시(notification bar, app title bar 제거)
     */
    protected void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    /**
     * 뒤로가기
     * @param view
     */
    public void goBack(View view) {
        finish();
    }
}
