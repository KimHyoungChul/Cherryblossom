package com.commax.controlsub.DeviceListEdit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.commax.controlsub.R;


/**
 * Created by OWNER on 2016-08-02.
 */
public class Customdialog_small_one_button extends Dialog implements  View.OnClickListener{
//    AboutFile aboutFile = new AboutFile();
    String TAG = Customdialog_small_one_button.class.getSimpleName();

    Button mRightButton;
    Button mLeftButton;

    String[] mparams;
    TextView title_text;
    TextView component_text;

    private static final int LENGTH = 20;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public Customdialog_small_one_button(Context context , String[] params) {
        super(context);
        //params[0] : title , [1] : component text
        mparams = params;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다이얼로그 외부 화면 흐리게 표현
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_two_button);
        hideNavigationBar();

        mRightButton = (Button) findViewById(R.id.btn_ok);
        mRightButton.setOnClickListener(this);

        mLeftButton = (Button)findViewById(R.id.btn_cancle);
        mLeftButton.setOnClickListener(this);

        title_text = (TextView)findViewById(R.id.title_text);
        component_text = (TextView)findViewById(R.id.popup_component);

        title_text.setText(mparams[0]);
        component_text.setText(mparams[1]);

        mLeftButton.setVisibility(View.GONE);
    }

    //외곽 터치시 다이얼로그 닫히는 거 막기 위해서 onTuouchEvent 를 false 로 리턴한다.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {
       if(v.equals(mRightButton))
        {
            dismiss();
        }
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
}
