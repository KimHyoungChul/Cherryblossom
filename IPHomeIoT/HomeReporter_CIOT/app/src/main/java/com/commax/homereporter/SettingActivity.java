package com.commax.homereporter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class SettingActivity extends Activity {

    static final String TAG = "SettingActivity";
    ListAdapter adapter;
    ListAdapter adapter2;
    ListAdapter adapter3;
    ListAdapter adapter4;
    ListAdapter adapter5;

    BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        hideNavigationBar();

        ListView listView = (ListView) findViewById(R.id.listView);
        ListView listView2 = (ListView) findViewById(R.id.listView2);
        ListView listView3 = (ListView) findViewById(R.id.listView3);
        ListView listView4 = (ListView) findViewById(R.id.listView4);
        ListView listView5 = (ListView) findViewById(R.id.listView5);
        ImageBT bt_ok = (ImageBT) findViewById(R.id.bt_ok);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        ImageBT bt_back = (ImageBT) findViewById(R.id.bt_back);

        RelativeLayout item_title2 = (RelativeLayout)findViewById(R.id.item_title2);
        LinearLayout   item_title = (LinearLayout)findViewById(R.id.item_title);
        LinearLayout   item_title3 = (LinearLayout)findViewById(R.id.item_title3);
        LinearLayout   item_title4 = (LinearLayout)findViewById(R.id.item_title4);
        LinearLayout   item_title5 = (LinearLayout)findViewById(R.id.item_title5);

        adapter = new ListAdapter(this);
        adapter2 = new ListAdapter(this);
        adapter3 = new ListAdapter(this);
        adapter4 = new ListAdapter(this);
        adapter5 = new ListAdapter(this);

        ArrayList<String> list_unselected_info = new ArrayList<>();
        ArrayList<String> list_support_info = new ArrayList<>();
        InfoTools infoTools = new InfoTools();
        list_unselected_info = infoTools.getUnSelectedInfo();
        list_support_info = infoTools.getSupportInfo();

        if (list_support_info.contains(NameSpace.INFO_WEATHER)) {
            adapter2.addItem(new SettingItem(NameSpace.INFO_WEATHER, getResources().getString(R.string.info_weather), true));
        }

        if (list_support_info.contains(NameSpace.INFO_AIR)) {
            adapter2.addItem(new SettingItem(NameSpace.INFO_AIR, getResources().getString(R.string.info_air), !list_unselected_info.contains(NameSpace.INFO_AIR)));
        }

        if (list_support_info.contains(NameSpace.INFO_HEALTH_LIFE)) {
            adapter2.addItem(new SettingItem(NameSpace.INFO_HEALTH_LIFE, getResources().getString(R.string.info_health_life), !list_unselected_info.contains(NameSpace.INFO_HEALTH_LIFE)));
        }

        if (list_support_info.contains(NameSpace.INFO_TODAY_EMS)) {
            adapter.addItem(new SettingItem(NameSpace.INFO_TODAY_EMS, getResources().getString(R.string.info_today_ems), !list_unselected_info.contains(NameSpace.INFO_TODAY_EMS)));
        }

        if (list_support_info.contains(NameSpace.INFO_MONTH_EMS)) {
            adapter.addItem(new SettingItem(NameSpace.INFO_MONTH_EMS, getResources().getString(R.string.info_month_ems), !list_unselected_info.contains(NameSpace.INFO_MONTH_EMS)));
        }

        if (list_support_info.contains(NameSpace.INFO_VISITOR)) {
            adapter3.addItem(new SettingItem(NameSpace.INFO_VISITOR, getResources().getString(R.string.info_missed), !list_unselected_info.contains(NameSpace.INFO_VISITOR)));
        }

        if (list_support_info.contains(NameSpace.INFO_NOTICE)) {
            adapter4.addItem(new SettingItem(NameSpace.INFO_NOTICE, getResources().getString(R.string.info_notice), !list_unselected_info.contains(NameSpace.INFO_NOTICE)));
        }

        if (list_support_info.contains(NameSpace.INFO_PARKING)) {
            adapter4.addItem(new SettingItem(NameSpace.INFO_PARKING, getResources().getString(R.string.info_parking), !list_unselected_info.contains(NameSpace.INFO_PARKING)));
        }

        if (list_support_info.contains(NameSpace.INFO_INDOOR_TEMP)) {
            adapter5.addItem(new SettingItem(NameSpace.INFO_INDOOR_TEMP, getResources().getString(R.string.info_indoor_temp), !list_unselected_info.contains(NameSpace.INFO_INDOOR_TEMP)));
        }

        if (list_support_info.contains(NameSpace.INFO_INDOOR_HUMID)) {
            adapter5.addItem(new SettingItem(NameSpace.INFO_INDOOR_HUMID, getResources().getString(R.string.info_indoor_humid), !list_unselected_info.contains(NameSpace.INFO_INDOOR_HUMID)));
        }

        if (list_support_info.contains(NameSpace.INFO_SUPPORT)) {
            adapter4.addItem(new SettingItem(NameSpace.INFO_SUPPORT, getResources().getString(R.string.info_support), !list_unselected_info.contains(NameSpace.INFO_SUPPORT)));
        }

        if (list_support_info.contains(NameSpace.INFO_SMART_PLUG)) {
            adapter.addItem(new SettingItem(NameSpace.INFO_SMART_PLUG, getResources().getString(R.string.info_smart_plug), !list_unselected_info.contains(NameSpace.INFO_SMART_PLUG)));
        }

        listView.setAdapter(adapter);
        listView2.setAdapter(adapter2);
        listView3.setAdapter(adapter3);
        listView4.setAdapter(adapter4);
        listView5.setAdapter(adapter5);

        if (adapter.getCount()<1){
            item_title.setVisibility(View.GONE);
        }
        if (adapter2.getCount()<1){
            item_title2.setVisibility(View.GONE);
        }
        if (adapter3.getCount()<1){
            item_title3.setVisibility(View.GONE);
        }
        if (adapter4.getCount()<1){
            item_title4.setVisibility(View.GONE);
        }
        if (adapter5.getCount()<1){
            item_title5.setVisibility(View.GONE);
        }

        adapter.setDynamicHeight(listView);
        adapter2.setDynamicHeight(listView2);
        adapter3.setDynamicHeight(listView3);
        adapter4.setDynamicHeight(listView4);
        adapter5.setDynamicHeight(listView5);

        bt_ok.setOnClickListener(mClick);
        bt_back.setOnClickListener(mClick);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NameSpace.SCREEN_OFF_ACTION);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();

                if(action.equals(NameSpace.SCREEN_OFF_ACTION)){

                    Log.d(TAG, "SCREEN_OFF_ACTION event caught");
                    finish();
                }
            }
        };

        try {
            registerReceiver(mBroadcastReceiver, intentFilter);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void save(){

        try {
            ArrayList<SettingItem> items = new ArrayList<>();

            items = viewChecked(adapter, items);
            items = viewChecked(adapter2, items);
            items = viewChecked(adapter3, items);
            items = viewChecked(adapter4, items);
            items = viewChecked(adapter5, items);

            try {
                File dir = new File(Environment.getExternalStorageDirectory() + "/InfoFile/");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                dir = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File(Environment.getExternalStorageDirectory() + "/InfoFile/" + NameSpace.INFO_PROPERTIES);
            Properties mProperty = new Properties();

            try {
                if (!file.exists()) {
                    file.createNewFile();
                    Log.e("Property", " Create INFO_PPROPERTIES ");
                }

                FileInputStream fis = new FileInputStream(file);
                mProperty.load(fis);
                mProperty.clear();

                FileOutputStream fos = new FileOutputStream(file);

                //true : 사용, false : 지원x,  not: 사용안함

                for (int i = 0; i < items.size(); i++) {
                    try {
                        SettingItem settingItem = (SettingItem) items.get(i);
                        Log.d(TAG, "item " + settingItem.getType() + " = " + settingItem.isChecked());
                        if (settingItem.isChecked()) {
                            mProperty.setProperty(settingItem.getType(), "true");
                        } else {
                            mProperty.setProperty(settingItem.getType(), "not");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mProperty.store(fos, "selected info");

                fos.close();
                fis.close();
                fis = null;
                fos = null;
                Log.e("Property", " File Saved");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private ArrayList<SettingItem> viewChecked(ListAdapter adapter, ArrayList<SettingItem> items){

        ArrayList<SettingItem> temp_items = new ArrayList<>();

        try {
            temp_items = items;

            for (int i = 0; i < adapter.getCount(); i++) {
                SettingItem settingItem = (SettingItem) adapter.getItem(i);
//            Log.d(TAG, "item " + settingItem.getType() + " = " + settingItem.isChecked());
                temp_items.add(settingItem);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return temp_items;
    }

    View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bt_back:
                    setResult(101);
                    finish();
                    break;

                case R.id.bt_ok:
                    save();
                    setResult(100);
                    finish();
                    break;
            }

        }
    };

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "rep lifecycle onDestroy");

        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
