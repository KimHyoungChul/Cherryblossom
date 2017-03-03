package com.commax.homecoach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements View.OnClickListener {
//    private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;

    /* Tip Names */
    private String ELEC = "elec";
    private String GAS = "gas";
    private String WATER = "water";
    private String HOTWATER = "hotwater";
    private String HEATER = "heater";
    private String INDOOR = "indoor";

    private ImageButton title_btn_close;
    private LinearLayout btn_tip_elec;
    private LinearLayout btn_tip_gas;
    private LinearLayout btn_tip_water;
    private LinearLayout btn_tip_hotwater;
    private LinearLayout btn_tip_heater;
    private LinearLayout btn_tip_indoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tip);

        hideNavigationBar();

        title_btn_close = (ImageButton) findViewById(R.id.btn_close);

        btn_tip_elec = (LinearLayout) findViewById(R.id.btn_tip_elec);
        btn_tip_gas = (LinearLayout) findViewById(R.id.btn_tip_gas);
        btn_tip_water = (LinearLayout) findViewById(R.id.btn_tip_water);
        btn_tip_hotwater = (LinearLayout) findViewById(R.id.btn_tip_hotwater);
        btn_tip_heater = (LinearLayout) findViewById(R.id.btn_tip_heater);
        btn_tip_indoor = (LinearLayout) findViewById(R.id.btn_tip_indoor);
        btn_tip_elec.setOnClickListener(this);
        btn_tip_gas.setOnClickListener(this);
        btn_tip_water.setOnClickListener(this);
        btn_tip_hotwater.setOnClickListener(this);
        btn_tip_heater.setOnClickListener(this);
        btn_tip_indoor.setOnClickListener(this);

         /* Backkey 터치시 Opacity 값 변경 */
        title_btn_close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
                    Drawable alpha = title_btn_close.getBackground();
                    alpha.setAlpha(0x99); // Opacity 60%
                } else if ((event.getAction() == MotionEvent.ACTION_UP)) {
                    Drawable alpha = title_btn_close.getBackground();
                    alpha.setAlpha(0xff); // Opacity 100%
                }

                return false;
            }
        });

        title_btn_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                onFinish();
            }
        });
    }

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }*/

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()) {
            case R.id.btn_tip_elec:
                intent.putExtra("TIPS", ELEC);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsPagerActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btn_tip_gas:
                intent.putExtra("TIPS", GAS);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btn_tip_water:
                intent.putExtra("TIPS", WATER);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btn_tip_hotwater:
                intent.putExtra("TIPS", HOTWATER);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btn_tip_heater:
                intent.putExtra("TIPS", HEATER);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            case R.id.btn_tip_indoor:
                intent.putExtra("TIPS", INDOOR);
                intent.setClassName("com.commax.homecoach", "com.commax.homecoach.TipsPagerActivity");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

            default:
                break;
        }
    }



    protected void onDestroy() {
        super.onDestroy();

        onFinish();
    }

    protected void onFinish() {
        Log.i(Log.DEBUG_TAG, "Coach Tip onFinish-------------------------");

        setResult(Activity.RESULT_CANCELED);
        finishAffinity();
        System.exit(0);
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
