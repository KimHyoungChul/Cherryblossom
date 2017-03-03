package com.commax.wirelesssetcontrol.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

/**
 * Created by Shin on 2016-11-12.
 */

public class Prefs {
    public static void put(Context context, String prefName, String key, String value){
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
        try {
            Process process = Runtime.getRuntime().exec("sync");
            process.getErrorStream().close();
            process.getInputStream().close();
            process.getOutputStream().close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String get(Context context, String prefName, String key){
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public static void remove(Context context, String prefName, String key){
        SharedPreferences pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }
}
