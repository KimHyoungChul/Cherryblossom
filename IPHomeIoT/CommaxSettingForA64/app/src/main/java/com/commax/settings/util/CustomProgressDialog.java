package com.commax.settings.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.commax.settings.R;

/**
 * 커스텀 ProgressDialog
 */

public class CustomProgressDialog extends Dialog {

    private TextView mtv_text;
    private String mPrintMessge;

    public CustomProgressDialog(Context context) {
        super(context);
        this.mPrintMessge = "";
    }

    public CustomProgressDialog(Context context, String message) {
        super(context);
        this.mPrintMessge = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 다이얼 로그 제목을 날림
        setContentView(R.layout.custom_progress_dialog); // 다이얼로그 레이아웃

        initView();
    }

    /**
     * FocusChanged 될때 NavigationBar 숨김
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

    private void initView() {

        //다이얼로그 배경을 투명처리
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if(!mPrintMessge.isEmpty()) {
            mtv_text = (TextView) findViewById(R.id.progress_title);
            mtv_text.setText(mPrintMessge);
        }
    }

    public void setMessage(String message) {
        mPrintMessge = message;
    }

    public boolean isShowing() {
        return true;
    }

}
