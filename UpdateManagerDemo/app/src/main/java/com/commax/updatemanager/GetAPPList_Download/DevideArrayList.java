package com.commax.updatemanager.GetAPPList_Download;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.commax.updatemanager.Common.TypeDef;
import com.commax.updatemanager.JSONHelper;

import java.util.ArrayList;

/**
 * Created by OWNER on 2016-12-05.
 */

/*
* 추후에 해당 앱이 업데이트가 필요할시 arraylist 를 3개로 분리하여 작업 하며 , updatecount 는 mListupdate의 length 로 보면 된다.*/
public class DevideArrayList {
    VersionCompare versionCompare =  new VersionCompare();
    JSONHelper jsonHelper = new JSONHelper();

    public  String TAG = DevideArrayList.class.getSimpleName();
    //Array List 들을 분리해야한다.
    ArrayList<ListCell> mListUpdate;
    ArrayList<ListCell> mListInstall;
    ArrayList<ListCell> mListNewest;

    Context mContext;
    private static PackageManager pm;


    DevideArrayList(Context context)
    {
        mContext = context;
    }

    public void devide_array_list_header()
    {
        init_data();
    }

    void init_data()
    {
        mListUpdate = new ArrayList<ListCell>();
        mListInstall = new ArrayList<ListCell>();
        mListNewest = new ArrayList<ListCell>();

        mListUpdate.clear();
        mListInstall.clear();
        mListNewest.clear();

        int count = 0;

        for(int i = 0 ; i < GetAppList.getInstance().getPackageName.length; i++)
        {
            String category = versionCompare.Compare(GetAppList.getInstance().getPackageName[i]  ,GetAppList.getInstance().getVersionName[i] , mContext );
            Log.d(TAG,"category =" +category);
            if(category.equals(TypeDef.Upgrade))
            {
                mListUpdate.add(new ListCell(getIcon(GetAppList.getInstance().getPackageName[i]), GetAppList.getInstance().getAppName[i],
                        GetAppList.getInstance().getPackageName[i], GetAppList.getInstance().getVersionName[i] , category));
            }
            else if(category.equals(TypeDef.Installation))
            {
                mListInstall.add(new ListCell(getIcon(GetAppList.getInstance().getPackageName[i]), GetAppList.getInstance().getAppName[i],
                        GetAppList.getInstance().getPackageName[i], GetAppList.getInstance().getVersionName[i] , category));

            }
            else if(category.equals(TypeDef.Newest))
            {
                mListNewest.add(new ListCell(getIcon(GetAppList.getInstance().getPackageName[i]), GetAppList.getInstance().getAppName[i],
                        GetAppList.getInstance().getPackageName[i], GetAppList.getInstance().getVersionName[i] , category));

            }
            if(category.equals(TypeDef.Upgrade))
            {
                count++;
                Log.d(TAG, "count : " + count);
            }
        }
        Log.d(TAG, "mListUpdate : " + mListUpdate);
        Log.d(TAG, "mListNewest : " + mListNewest);
        Log.d(TAG, "mListInstall : " + mListInstall);

    }

    // 설치 되어 있는 app의 아이콘 가져오기
    Drawable getIcon(String packageName)
    {

        pm = mContext.getPackageManager();
        Drawable d = null;
        try {
            d = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return d;
    }

}
