package com.commax.homecoach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TipsPagerActivity extends Activity implements OnClickListener, ViewPager.OnPageChangeListener {
    private ViewPager mPager;

    private Context m_Context = null;

    /* Tip Names */
    private String ELEC = "elec";
    private String INDOOR = "indoor";

    /* Tip 내용들 */
    private Button title_btn_back;
    private TextView views_title;
    private ImageView img_Icon;

    // Indicator
    public static ImageView indicator_1;
    public static ImageView indicator_2;
    public static ImageView indicator_3;
    public static ImageView indicator_4;

    private String tips_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_tips_pager);

        hideNavigationBar();

        m_Context = this;

        Intent intent = getIntent();
        tips_name = intent.getExtras().getString("TIPS");

        if (tips_name.equals(ELEC)) {
            Config.TIPS_NAME = tips_name;
        }
        else if (tips_name.equals(INDOOR)) {
            Config.TIPS_NAME = tips_name;
        }

        // 타이틀 바
        title_btn_back = (Button) findViewById(R.id.btn_back);
        views_title = (TextView) findViewById(R.id.views_title);

        // 내용 부분
        img_Icon = (ImageView) findViewById(R.id.img_Icon);

        // Indicator
        indicator_1 = (ImageView) findViewById(R.id.indicator_1);
        indicator_2 = (ImageView) findViewById(R.id.indicator_2);
        indicator_3 = (ImageView) findViewById(R.id.indicator_3);
        indicator_4 = (ImageView) findViewById(R.id.indicator_4);

        title_btn_back.setOnClickListener(this);
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

        mPager = (ViewPager) findViewById(R.id.tips_pager);
        mPager.setOnPageChangeListener(this);

        //ViewPager에 설정할 Adapter 객체 생성
        //ListView에서 사용하는 Adapter와 같은 역할.
        //다만. ViewPager로 스크롤 될 수 있도록 되어 있다는 것이 다름
        //PagerAdapter를 상속받은 CustomAdapter 객체 생성
        //CustomAdapter에게 LayoutInflater 객체 전달
        CustomPagerAdapter adapter = new CustomPagerAdapter(m_Context, getLayoutInflater());

        //ViewPager에 Adapter 설정
        mPager.setAdapter(adapter);

        showTips();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void showTips() {
        if (tips_name.equals(ELEC)) {
            views_title.setText(getResources().getText(R.string.title_tips_elec));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_eletric);
        }
        else if (tips_name.equals(INDOOR)) {
            views_title.setText(getResources().getText(R.string.title_indoor2));
            img_Icon.setBackgroundResource(R.mipmap.ic_coach04_homeair);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("yclee", "onPageScrolled : " + positionOffset);
    }

    @Override
    public void onPageSelected(int position) {
        Log.i("yclee", "onPageSelected : " + position);

        if (0 == position) {
            indicator_1.setBackgroundResource(R.drawable.view_indicator_shape_select);
            indicator_2.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_3.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_4.setBackgroundResource(R.drawable.view_indicator_shape_normal);
        }
        else if (1 == position) {
            indicator_1.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_2.setBackgroundResource(R.drawable.view_indicator_shape_select);
            indicator_3.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_4.setBackgroundResource(R.drawable.view_indicator_shape_normal);
        }
        else if (2 == position) {
            indicator_1.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_2.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_3.setBackgroundResource(R.drawable.view_indicator_shape_select);
            indicator_4.setBackgroundResource(R.drawable.view_indicator_shape_normal);
        }
        else {
            indicator_1.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_2.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_3.setBackgroundResource(R.drawable.view_indicator_shape_normal);
            indicator_4.setBackgroundResource(R.drawable.view_indicator_shape_select);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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
