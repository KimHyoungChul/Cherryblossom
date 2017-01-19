package com.commax.updatemanager.GetAPPList_Download;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by OWNER on 2016-12-05.
 */
public class StartService {

    String TAG = StartService.class.getSimpleName();

    Context mContext;

    public StartService()
    {

    }

    public  void start_service(Context context , String package_name)
    {
        Log.d(TAG, "start service");
        mContext = context;
        getServiceFindList(package_name);

    }

    private void getServiceFindList(String package_name) {
        String strServiceInfo[][] = {{"com.commax.sipua", "com.commax.sipua.SipService"},
                {"com.commax.systemservice", "com.commax.systemservice.SystemService"},
                {"com.commax.opcemerservice", "com.commax.opcemerservice.OpcEmerService"},
                {"com.commax.pam.service", "com.commax.adapter.service.AdapterService"}};


            for(int i = 0; i < strServiceInfo.length; i++) {

            //Is Package Install
            if(isPackageInstall(strServiceInfo[i][0]) == true) {
                Log.i(TAG, "Install Package. !!! : " + strServiceInfo[i][0]);

                //Is Service Running
                if(isServiceRunning(strServiceInfo[i][1]) == false) {
                    Log.i(TAG, "Not Service Running. !!! : " + strServiceInfo[i][0]);

                //Set Start Service
                    setStartService(strServiceInfo[i][0], strServiceInfo[i][1]);
                }
                else {
                    Log.i(TAG, "Service Running. !!! : " + strServiceInfo[i][0]);
                }
            }
            else {
                Log.i(TAG, "Not Install Package. !!! : " + strServiceInfo[i][0]);
            }
        }
    }

//     Is Package Install
    private boolean isPackageInstall(String strPackage) {
        boolean bInstallFlag = false;

        PackageManager pm = mContext.getPackageManager();

        List<ApplicationInfo> packagesList = pm
                .getInstalledApplications(PackageManager.GET_SERVICES);

        //PackageManager.GET_UNINSTALLED_PACKAGES| PackageManager.GET_DISABLED_COMPONENTS

        Log.d(TAG, "list packageList : " + packagesList);
        for(ApplicationInfo packagesInfo : packagesList) {
            if(strPackage.equals(packagesInfo.packageName) == true) {
                bInstallFlag = true;
                break;
            }
        }

        return bInstallFlag;
    }


    /* Is Service Running */
    private boolean isServiceRunning(String strClassName) {
        boolean bRunning = false;

        try{
            ActivityManager am = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> servicesList = am.getRunningServices(Integer.MAX_VALUE);
            Iterator<ActivityManager.RunningServiceInfo> serviceList = servicesList.iterator();

            while(serviceList.hasNext()) {
                ActivityManager.RunningServiceInfo si = (ActivityManager.RunningServiceInfo)serviceList.next();
                if(strClassName.equals(si.service.getClassName()) == true) {
                    bRunning = true;
                    break;
                }
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return bRunning;
    }

    /* Set Start Service */
    private void setStartService(String strPackage, String strClassName) {
        Intent intent = new Intent();
        ComponentName comName = new ComponentName(strPackage, strClassName);

        intent.setComponent(comName);
        mContext.startService(new Intent().setComponent(comName));

        intent = null;
        comName = null;
    }

}
