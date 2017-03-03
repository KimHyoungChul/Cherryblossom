package com.commax.homecoach;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;

import com.commax.homecoach.CustomAlertsDialog;

/**
 * Created by YOUNG on 2016-10-07.
 */

public class TipsActivity extends Activity implements View.OnClickListener {

    private Context m_Context = null;

    /* Tip Names */
    private String ELEC = "elec";
    private String GAS = "gas";
    private String WATER = "water";
    private String HOTWATER = "hotwater";
    private String HEATER = "heater";
    private String INDOOR = "indoor";

    /* Tip 내용들 */
    private Button title_btn_back;
    private TextView views_title;
    private ImageView img_Icon;
    private TextView text_Subtitle;
    private TextView text_Docu;
    private LinearLayout button_1;
    private TextView btn_name1;
    private LinearLayout button_2;
    private TextView btn_name2;

    private String tips_name;

    private static CustomAlertsDialog alertsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_Context = this;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_tips);

        hideNavigationBar();

        Intent intent = getIntent();
        tips_name = intent.getExtras().getString("TIPS");

        // 타이틀 바
        title_btn_back = (Button) findViewById(R.id.btn_back);
        views_title = (TextView) findViewById(R.id.views_title);

        // 내용 부분
        img_Icon = (ImageView) findViewById(R.id.img_Icon);
        text_Subtitle = (TextView) findViewById(R.id.text_Subtitle);
        text_Docu = (TextView) findViewById(R.id.text_Docu);
        button_1 = (LinearLayout) findViewById(R.id.button_1);
        btn_name1 = (TextView) findViewById(R.id.btn_name1);
        button_2 = (LinearLayout) findViewById(R.id.button_2);
        btn_name2 = (TextView) findViewById(R.id.btn_name2);

        title_btn_back.setOnClickListener(this);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);

        title_btn_back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
                    Drawable alpha = title_btn_back.getBackground();
                    alpha.setAlpha(0x99); // Opacity 60%
                }
                else if ((event.getAction() == MotionEvent.ACTION_UP)) {
                    Drawable alpha = title_btn_back.getBackground();
                    alpha.setAlpha(0xff); // Opacity 100%
                }

                return false;
            }
        });

        showTips();
    }

    private void showTips() {
        if (tips_name.equals(GAS)) {
            views_title.setText(getResources().getText(R.string.title_tips_gas));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_gas);
            text_Subtitle.setText(getResources().getText(R.string.gas_subtitle));
            text_Docu.setText(getResources().getText(R.string.gas_docu));
            button_1.setVisibility(View.GONE);
            button_2.setVisibility(View.GONE);
            btn_name2.setText(getResources().getText(R.string.btn_ems_gas));
        }
        else if (tips_name.equals(WATER)) {
            views_title.setText(getResources().getText(R.string.title_tips_water));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_water);
            text_Subtitle.setText(getResources().getText(R.string.water_subtitle));
            text_Docu.setText(getResources().getText(R.string.water_docu));
            button_1.setVisibility(View.GONE);
            btn_name2.setText(getResources().getText(R.string.btn_ems_water));
        }
        else if (tips_name.equals(HOTWATER)) {
            views_title.setText(getResources().getText(R.string.title_tips_hotwater));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_hotwater);
            text_Subtitle.setText(getResources().getText(R.string.hotwater_subtitle));
            text_Docu.setText(getResources().getText(R.string.hotwater_docu));
            btn_name1.setText(getResources().getText(R.string.btn_ems_hotwater) + "_1");
            btn_name2.setText(getResources().getText(R.string.btn_ems_hotwater) + "_2");
        }
        else if (tips_name.equals(HEATER)) {
            views_title.setText(getResources().getText(R.string.title_tips_heater));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_heater);
            text_Subtitle.setText(getResources().getText(R.string.heater_subtitle));
            text_Docu.setText(getResources().getText(R.string.heater_docu));
            btn_name1.setText(getResources().getText(R.string.btn_heater));
            btn_name2.setText(getResources().getText(R.string.btn_ems_heater));
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent_btn1 = new Intent();
        Intent intent_btn2 = new Intent();

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.button_1:
                if (tips_name.equals(HEATER)) {
                    intent_btn1.setClassName("com.commax.control", "com.commax.control.MainActivity");
                    intent_btn1.putExtra("tapid", 3);
                    intent_btn1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent_btn1);
                }
                else if (tips_name.equals(HOTWATER)) {
                    try {
                        intent_btn1.putExtra("PERIOD", "ThisMonth");
                        intent_btn1.putExtra("ENERGY", "6");
                        intent_btn1.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn1);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }
                break;

            case R.id.button_2:
                if (tips_name.equals(ELEC)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "0");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }

                else if (tips_name.equals(GAS)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "1");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }

                else if (tips_name.equals(WATER)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "2");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }

                else if (tips_name.equals(HOTWATER)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "7");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }

                else if (tips_name.equals(HEATER)) {
                    try {
                        intent_btn2.putExtra("PERIOD", "ThisMonth");
                        intent_btn2.putExtra("ENERGY", "4");
                        intent_btn2.setClassName("com.commax.pxdemsmonitor", "com.commax.pxdemsmonitor.MainActivity");
                        intent_btn2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent_btn2);
                    }
                    catch (NullPointerException e) {
                        /* Alerts 다이얼로그 생성자 */
                        alertsDialog = new CustomAlertsDialog(m_Context);

                        alertsDialog.setOnDismissListener(new OnDismissListener() {

                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });

                        alertsDialog.show();
                    }
                }
                break;

            default:
                break;
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
