package com.commax.settings;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.commax.settings.util.PlusClickGuard;

/**
 * 공통 액티비티
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CommonActivity extends Activity {

    /**
     * 화면 타이틀 표시
     *
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
     *
     * @param view
     */
    public void goBack(View view) {
        PlusClickGuard.doIt(view);
        finish();
    }

    /**
     * FocusChanged 될때 NavigationBar 숨김(여기서 일괄처리,단 팝업 다이얼로그는 별도처리 요망)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            try {
                // 액티비티 아래의 네비게이션 바가 안보이게
                final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                getWindow().getDecorView().setSystemUiVisibility(flags);
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
    }


}
