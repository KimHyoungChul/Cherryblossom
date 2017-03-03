package com.commax.wirelesssetcontrol.tools;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.commax.wirelesssetcontrol.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ShinSung on 2017-02-14.
 */
public class PublicTools {
    static final String TAG = "PublicTools";

    public static void hideActionBar(AppCompatActivity act){
        try {
            if (act instanceof AppCompatActivity && act.getSupportActionBar() != null) {
                act.getSupportActionBar().hide();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void hideNavigationBar(Activity act){
        try {
            // 액티비티 아래의 네비게이션 바가 안보이게
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            act.getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = act.getWindow().getDecorView();
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

    public static void clearNavigationBar(Activity act){
        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            act.getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getEventType(String str){            /* event classification (add, remove, update) */
//        Log.d(DEBUG_TAG, "getEventType received: "+str);

        String raw = str;
        JSONObject jObject=null;
        String command = "no";

        try {
            if (!TextUtils.isEmpty(raw)) {
                jObject = new JSONObject(raw.toString());

                if (jObject!=null) {
                    command = jObject.getString("command");
                }
            }else {
                Log.d(TAG, "getEventType get RootUuid raw maybe null");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getEventType get Json Object failed");
        } catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "getEventType get Json Object failed2");
        }

        if(!TextUtils.isEmpty(command)) {
            switch (command) {
                case "addReport":
                    return command;
                case "removeReport":
                    return command;
                case "report":
                    return command;
                case "factoryReport":
                    return command;
                default:
                    return "no";
            }
        }
        return "no";
    }
}
