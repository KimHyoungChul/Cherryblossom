package com.commax.commaxwidget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.commax.commaxwidget.util.Constants;
import com.commax.commaxwidget.util.SharePreferenceHelper;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SharePreferenceHelper mPreferHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("junhwe", "junhwe start activity");
        onInit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void onInit() {
        initData();
        initView();
    }

    private void initData() {
        mPreferHelper = new SharePreferenceHelper(this);
    }

    private void initView() {
        Button button = (Button) findViewById(R.id.test_start_button);
        Button stopButton = (Button) findViewById(R.id.test_stop_button);

        stopButton.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int state = mPreferHelper.getValue(SharePreferenceHelper.PREFERENCE_BG_STATE, 0);

        if (view.getId() == R.id.test_start_button) {
            startService(new Intent(getApplicationContext(), MainService.class));
        } else if (view.getId() == R.id.test_stop_button) {
            stopService(new Intent(getApplicationContext(), MainService.class));
        }
        mPreferHelper.put(SharePreferenceHelper.PREFERENCE_BG_STATE, state == 0 ? 1 : 0);
    }

    private int getState() {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        try {
            return pref.getInt("widget_bg", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    private void setState(int key) {
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("widget_bg", key);
        editor.commit();
    }
}
