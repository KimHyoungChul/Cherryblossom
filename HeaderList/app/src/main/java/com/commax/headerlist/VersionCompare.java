package com.commax.headerlist;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by OWNER on 2016-06-27.
 */
public class VersionCompare {

    public VersionCompare() {

    }

    private static final String TAG = VersionCompare.class.getSimpleName();
    static List<String> newversion;
    static ArrayList<String> old;

    //Version Compare
    public String Compare(String Package, String VersionName , Context context){
        String value = null;
        String wallVersion = null;
        String Addinstall = context.getString(R.string.addinstall);
        String Upgrade = context.getString(R.string.upgrade);
        String Newest =context.getString(R.string.newest);

        //VersionName 가져오기
        final PackageInfo versionInfo;
        try {
            try{
                versionInfo = context.getPackageManager().getPackageInfo(Package, PackageManager.GET_META_DATA);
                wallVersion = versionInfo.versionName;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                value = Addinstall;
            }
            Log.d(TAG,"wallpad version : " + wallVersion);
            Log.d(TAG, "server version : " + VersionName);
            newversion  = Arrays.asList(VersionName.split("[.]"));
            if(TextUtils.isEmpty(wallVersion))
            {
                Log.d(TAG, Package + "설치되어야 하는 APP");
                old = null;
            }
            else
            {
                old = new ArrayList<String>(Arrays.asList(wallVersion.split("[.]")));
            }
            //긴 버전 길이로 버전 비교
            int j = newversion.size() <= old.size() ? old.size() : newversion.size() ;
            Log.d(TAG, "new.size()" + newversion.size() + " old.size() : " + old.size());
            Log.d(TAG, " long version Number " + String.valueOf(j));
            int k = newversion.size() - old.size();

            if(newversion.size() > old.size())
            {
                Log.d(TAG ," 버전 비교 if 문");
                //TODO 3자리와 1자리 생각해보기
                for(int i = k ; i > 0 ; i--)
                {
                    Log.d(TAG ,"i = " +i);
                    old.add(newversion.size() -i, "0");
                }
                Log.d(TAG ,"after add");
            }
            //월패드 버전이 더 길때는 필요가 없음 길이만큼 for문이 다 돌기전에 업데이트 category 잡음
            for(int i = 0; i< j ;i++)
            {
                Log.d(TAG ,"newversion : " + newversion.get(i) + "old : " +old.get(i));
                if(Integer.parseInt(newversion.get(i)) > Integer.parseInt(old.get(i)))
                {
                    value = Upgrade;
            //        listupdatecount++;
                    break;
                }
                else
                {
                    value = Newest;
                    Log.d(TAG, "category = Newesst");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error");
        }
        // 같은주릿 수 일때
		/*
		try {
			try{
				versionInfo = MainActivity.getInstance().getPackageManager().getPackageInfo(Package,PackageManager.GET_META_DATA);
				wallVersion = versionInfo.versionName;
			}
			catch (PackageManager.NameNotFoundException e)
			{
				value = Addinstall;
			}
			Log.d(TAG,"wallpad version : " + wallVersion);
			Log.d(TAG, "server version : " + VersionName);
			serversion = VersionName.split("[.]");

			if(TextUtils.isEmpty(wallVersion))
			{
				Log.d(TAG, Package + "설치되어야 하는 APP");
				oldversion = null;
			}
			else
			{
				oldversion = wallVersion.split("[.]");
			}
			//짧은 길이로 버전 비교
			int j = serversion.length >= oldversion.length ? oldversion.length : serversion.length ;
			Log.d(TAG, " short version Number " + String.valueOf(j));

			for(int i = 0; i< j ;i++)
			{
				Log.d(TAG ,"serversion : " + serversion[i] + "oldversion : " +oldversion[i]);
				if(Integer.parseInt(serversion[i]) > Integer.parseInt(oldversion[i]))
				{
					value = Upgrade;
					listupdatecount++;
					break;
				}
				else
				{
					value = Newest;
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error");
		}
		*/
        return value;
    }

}
