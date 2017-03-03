package com.commax.wirelesssetcontrol.touchmirror.view.tools;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin sung on 2017-02-10.
 */
public class ResourceFinder {
    private static Context mContext;

    public static void init(Context context){
        mContext = context;
    }

    public static List<ResolveInfo> getApps(){
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mContext.getPackageManager().queryIntentActivities(intent, 0);
    }

    public static ArrayList<AppWidgetProviderInfo> getWidgets(){
        AppWidgetManager manager = AppWidgetManager.getInstance(mContext);
        return (ArrayList)manager.getInstalledProviders();
    }
}
