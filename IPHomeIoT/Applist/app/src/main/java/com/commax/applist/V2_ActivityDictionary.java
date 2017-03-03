package com.commax.applist;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2016-09-29.
 */
public class V2_ActivityDictionary {

    private List<ResolveInfo> oldLoadApps = null;
    private static V2_ActivityDictionary mInstance = null;
    static public void  clearInstance()  {
        mInstance = null;
    }

    static public V2_ActivityDictionary getInstance(Context context)
    {
        if( mInstance == null)   {
            mInstance = new V2_ActivityDictionary(context);
        }

        return mInstance;
    }

    V2_ActivityDictionary(Context context)    {
        reloadApplications(context);
    }

    public  ArrayList<ApplicationInfo> mApplications = null;

    private Drawable resize(Context context, Drawable image) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, context.getResources()
                .getDimensionPixelSize(R.dimen.icon_size), context.getResources()
                .getDimensionPixelSize(R.dimen.icon_size), true);

        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

    public  Drawable getIcon(String packageAndActivityName )   {

        for( int i = 0; i < mApplications.size(); ++i) {

            if( mApplications.get(i).name.equals( packageAndActivityName) ) {
                return mApplications.get(i).icon;
            }
        }

        return null;
    }

    public boolean reloadApplications(Context context) {

        try {
            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>();
            }

            PackageManager manager = context.getPackageManager();

            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> apps = manager.queryIntentActivities(
                    mainIntent, 0);

            if( apps == null)
            {
                return false;
            }

            Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

            boolean bChanged = (oldLoadApps == null) || (oldLoadApps.size() != apps.size());

            if (bChanged) {

                oldLoadApps = apps;

                mApplications.clear();

                final int count = apps.size();
                ThemeLoader theme = new ThemeLoader(context);

                for (int i = 0; i < count; i++) {
                    ApplicationInfo application = new ApplicationInfo();
                    ResolveInfo info = apps.get(i);

                    //icon

                    Drawable icon = theme.getIcon(info.activityInfo.name, "");
                    if (icon == null) {
                        icon = info.activityInfo.loadIcon(manager);
                    }
                    icon = resize(context, icon);

                    application.title = info.loadLabel(manager);
                    application.setActivity(new ComponentName(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    application.icon = icon;
                    application.name = info.activityInfo.name;

                    // boolean hide = false;
                    // hide = checkHide(application.name);

                    //if (!hide) {
                    mApplications.add(application);
                    // }
//				if(!application.name.contains("com.skyarmy.orientation.locker.fresh")) {
//					mApplications.add(application);
//				}

                }

                return true;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public  String getTitle(String packageAndActivityName )   {

        for( int i = 0; i < mApplications.size(); ++i) {

            if( mApplications.get(i).name.equals( packageAndActivityName) ) {
                return mApplications.get(i).title.toString();//.icon;
            }
        }

        return "NoTitle";
    }

    public  String getPackageName(String packageAndActivityName )   {

        for( int i = 0; i < mApplications.size(); ++i) {

            if( mApplications.get(i).name.equals( packageAndActivityName) ) {
                return mApplications.get(i).intent.getComponent().getPackageName();//.icon;
            }
        }

        return EMPTY_PACKAGE;
    }


    public static final String EMPTY_PACKAGE ="NoTitle";

    public  String getPackageActivityName(String packageAndActivityName )   {

        String packageName = getPackageName(packageAndActivityName);
        String result = packageAndActivityName.replace(packageName, "");
        String result2  = result.substring(1, result.length());

        return result2;
    }
}
