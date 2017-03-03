package com.commax.wirelesssetcontrol.touchmirror.view.worker;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;

import com.commax.wirelesssetcontrol.FileEx;
import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.RootDevice;
import com.commax.wirelesssetcontrol.device.CmxDeviceDataReceiveInterface;
import com.commax.wirelesssetcontrol.device.CmxDeviceManager;
import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.touchmirror.TouchMirrorAct;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin sung on 2017-02-21.
 */
public class IconDrawWorker implements Runnable, CmxDeviceDataReceiveInterface {
    private int mType;
    private Context mContext;
    private Handler mHandler;

    public IconDrawWorker(Context context, int type, Handler handler) {
        mContext = context;
        mType = type;
        mHandler = handler;
    }

    @Override
    public void run() {
        if (mType == TouchMirrorAct.MODE_APPS)
            createAppsData();
        else if (mType == TouchMirrorAct.MODE_WIDGET)
            createWidgetData();
        else if (mType == TouchMirrorAct.MODE_DEVICE)
            createDeviceData();
    }

    //앱스 페이지 데이터를 넣음
    private AsyncTask<ResolveInfo, IconData, Integer> mIconCreateTask = null;
    private void createAppsData() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        mIconCreateTask = new AsyncTask<ResolveInfo, IconData, Integer>() {
            @Override
            protected Integer doInBackground(ResolveInfo... launcherList) {
                PackageManager packageManager = mContext.getApplicationContext().getPackageManager();
                ResolveInfo item;

                FileEx fileEx = new FileEx();
                ArrayList<Pair<String, String>> hide = fileEx.readHideFile();

                for (int i = 0; i < launcherList.length; i++) {
                    item = launcherList[i];
                    boolean draw = true;

                    Drawable drawable = BitmapTool.copy(mContext, item.loadIcon(packageManager));
                    CharSequence name = item.loadLabel(packageManager);

                    try {
                        if (hide != null) {
                            for (int count = 0; count < hide.size(); count++) {
                                if (item.activityInfo.name.equalsIgnoreCase(hide.get(count).second)) {
                                    draw = false;
                                    continue;
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (draw) {
                        IconData data = new IconData(IconData.TYPE_APPS, drawable, name.toString(), 0, 0, 1, 1);
                        data.setPackageName(item.activityInfo.packageName);
                        data.setComponentName(item.activityInfo.name);
                        publishProgress(data);
                    }
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(IconData... data) {
                if(data[0] != null)
                    sendMessage(HandlerEvent.EVENT_HANDLE_ADD_GRID_ITEM, 0, (Object) data[0]);
            }
        };

        List<ResolveInfo> launcherList = mContext.getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);
        ResolveInfo[] array = new ResolveInfo[launcherList.size()];
        array = launcherList.toArray(array);
        mIconCreateTask.execute(array);
    }

    //위젯 페이지 데이터를 넣음
    private void createWidgetData() {
        AppWidgetManager manager = AppWidgetManager.getInstance(mContext.getApplicationContext());
        ArrayList<AppWidgetProviderInfo> widgetList = (ArrayList)manager.getInstalledProviders();
        AppWidgetProviderInfo item;

        PackageManager packageManager = mContext.getPackageManager();
        for (int i = widgetList.size()-1; i >=0; i--) {
            item = widgetList.get(i);

            Drawable drawable;
            CharSequence name;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                drawable = BitmapTool.copy(mContext, item.loadPreviewImage(mContext, DisplayMetrics.DENSITY_HIGH));
                if(drawable == null)
                    drawable = BitmapTool.copy(mContext, item.loadIcon(mContext, DisplayMetrics.DENSITY_XXXHIGH));
                name = item.loadLabel(packageManager);
            }
            else{
                drawable = BitmapTool.copy(mContext, R.drawable.android_default_icon);
                name = "empty";
            }

            float widgetGridSize = 72.0f * mContext.getResources().getDisplayMetrics().density;
            IconData data = new IconData(IconData.TYPE_WIDGET, drawable, name.toString(), 0, 0,
                    Math.round(item.minWidth / widgetGridSize),
                    Math.round(item.minHeight / widgetGridSize));
            data.setPackageName(item.provider.getPackageName());
            data.setComponentName(item.provider.getClassName());
            data.setAlignWidget(true);

            sendMessage(HandlerEvent.EVENT_HANDLE_ADD_GRID_ITEM, 0, (Object)data);
        }
    }

    private void createDeviceData(){
        String[] list = {RootDevice.SWITCH, RootDevice.LOCK};
        CmxDeviceManager.getInst().getDevice(list, true, this);
    }

    private void sendMessage(int what, int arg1, Object obj){
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = arg1;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    @Override
    public void getDeviceInfoCallback(ArrayList<DeviceInfo> deviceList) {
        DeviceInfo deviceInfo;
        for(int i=0; i<deviceList.size(); i++){
            deviceInfo = deviceList.get(i);
            String status = "";

            if(deviceInfo == null)
                continue;
            else if(deviceInfo.mainDevice == null || deviceInfo.deviceType == null)
                continue;
            else if(deviceInfo.deviceType.length() == 0)
                continue;

            Drawable drawable = BitmapTool.copy(mContext, ResourceManager.getDeviceBgResId(status));
            DeviceIconData data =
                    new DeviceIconData(IconData.TYPE_DEVICE,
                            drawable,
                            deviceInfo.nickName,
                            0,
                            0,
                            1,
                            1,
                            deviceInfo.rootUuid,
                            deviceInfo.deviceType,
                            deviceInfo.nickName,
                            deviceInfo.controlType,
                            deviceInfo.main_subUuid,
                            deviceInfo.mainDevice.value);
            data.setDeviceDefault(true);
            sendMessage(HandlerEvent.EVENT_HANDLE_ADD_GRID_ITEM, 0, (Object)data);
        }
    }

    public void closeTask(){
        if(mIconCreateTask!=null)
            mIconCreateTask.cancel(true);
    }
}
