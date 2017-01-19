package com.commax.updatemanager.GetAPPList_Download;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.commax.updatemanager.R;

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
        String Addinstall = context.getString(R.string.install);
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

            //버전 비교하기 위해서는 월패드에 깔려있는 버전의 자릿수만 더 늘려주면 된다.
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
                else if(Integer.parseInt(newversion.get(i)) < Integer.parseInt(old.get(i)))
                {
                    value = Newest;
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
        Log.d(TAG, "value : " + value);
        return value;
    }



  /*
  //Version Compare 2016-12-02 ListHeaderAdapter 에 있던거 가져옴 (backup)

    public String versionCompare(String Package, String VersionName){
        String value = null;
        String wallVersion = null;
        //VersionName 가져오기
        final PackageInfo versionInfo;
        try {

            try{
                //현재 월패드에 설치 되어 있는지를 파악 한다.
                versionInfo = MainActivity.getInstance().getPackageManager().getPackageInfo(Package,PackageManager.GET_META_DATA);
                wallVersion = versionInfo.versionName;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                //설치되어 있지 않으면 catch로 떨어지면서 install로 카테고리 지정
                value = Install;
            }

            Log.e(TAG, "appName : " + Package + " , wallpad version : " + wallVersion + " , server version : " + VersionName);
            serversion = VersionName.split("[.]");

            if(TextUtils.isEmpty(wallVersion))
            {
                Log.i(TAG , Package + "  = > is not installed in wallpad ");
            }
            else
            {
                oldversion = wallVersion.split("[.]");
                //더 긴 자릿수를 파악하여 해당 자릿수만큼 길이를 비교한다.
                int j = serversion.length <= oldversion.length ? oldversion.length : serversion.length ;
                Log.d(TAG, " long version name length " + String.valueOf(j));
                for(int i = 0; i< j ;i++)
                {
                    try
                    {
                        // ex) 2.7  vs  2.7.1
                        //ex) 4 vs 1.3  월패드의 버전이 더 놓은 경우는?
                        // ex) 1.5.2 vs 2.1.1
                        Log.d(TAG ,"serversion : " + serversion[i] + "oldversion : " +oldversion[i]);
                        if(TextUtils.isEmpty(oldversion[i]))
                        {
                            value = Upgrade;
                        }
                        else {
                            if(Integer.parseInt(serversion[i]) > Integer.parseInt(oldversion[i]))
                            {
                                value = Upgrade;
                                listupdatecount++;
                                break;
                            }
                            else if(Integer.parseInt(serversion[i]) < Integer.parseInt(oldversion[i]))
                            {
                                value = Newest;
                                break;
                            }
                            else
                            {
                                value = Newest;
                            }
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        value = Upgrade;
                    }

                }
            }
            oldversion = null;
        }
        catch (Exception e) {
            Log.e(TAG, " version compare Error");
        }
        return value;
    }
*/

}
