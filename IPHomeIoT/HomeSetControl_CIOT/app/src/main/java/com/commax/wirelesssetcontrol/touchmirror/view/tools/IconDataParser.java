package com.commax.wirelesssetcontrol.touchmirror.view.tools;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.Status;
import com.commax.wirelesssetcontrol.touchmirror.view.adapter.IconGridPagerAdapter;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shin sung on 2017-02-08.
 */
public class IconDataParser {
    private static final String TAG = "launcherParser";

    public static String getScreenXYToString(int width, int height){
        JSONObject obj = new JSONObject();
        try {
            obj.put("width", width);
            obj.put("height", height);
        } catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }
        return obj.toString();
    };

    public static Point getScreenXYForString(String jsonStr){
        JSONObject obj;
        int width = 0, height = 0;
        try {
            obj = new JSONObject(jsonStr);
            width = (int)obj.get("width");
            height = (int)obj.get("height");
        } catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }
        return new Point(width, height);
    };

    public static ArrayList<IconLayout> getIayoutData(IconLayout[][] mapData){
        ArrayList<IconLayout> dataList = new ArrayList<IconLayout>();
        for(int i=0; i<mapData.length; i++){
            for(int j=0; j<mapData[i].length; j++){
                if(mapData[i][j] != null) {
                    dataList.add(mapData[i][j]);
                }
            }
        }
        return dataList;
    }
    public static String getIconDataForString(IconLayout[][] mapData){
        HashMap<Integer, Boolean> checkMap = new HashMap<Integer, Boolean>();
        JSONArray array = new JSONArray();

        JSONObject item;
        for(int i=0; i<mapData.length; i++){
            for(int j=0; j<mapData[i].length; j++){
                if(mapData[i][j] != null) {
                    if(checkMap.get(mapData[i][j].getIconData().getId()) == null) {
                        item = getJSonForIconData(mapData[i][j].getIconData());
                        if (item != null) {
                            array.put(item);
                            checkMap.put(mapData[i][j].getIconData().getId(), true);
                        }
                    }
                }
            }
        }
        return array.toString();
    }

    public static JSONObject getJSonForIconData(IconData data){
        final int fixed = 5; //좌표계 이동에 따라 소수점 연산에서 값이 소실되는 문제로 보정값을 둠

        JSONObject item = null;
        try {
            item = new JSONObject();
            item.put("id", data.getId());
            item.put("package", data.getPackageName());
            item.put("component", data.getComponentName());
            item.put("type", data.getType());
            item.put("imgResId", data.getImgResId());
            item.put("name", data.getName());
            item.put("x", data.getPosition().x + fixed);
            item.put("y", data.getPosition().y + fixed);
            item.put("width", data.getSize().x);
            item.put("height", data.getSize().y);
            item.put("real_widget", data.isRealWidget());
            item.put("widget_id", data.getWidgetId());

            if (data.getType() == IconData.TYPE_DEVICE){
                if (data instanceof DeviceIconData) {
                    DeviceIconData deviceIconData = (DeviceIconData) data;
                    item.put("rootUuid", deviceIconData.getRootUuid());
                    item.put("deviceType", deviceIconData.getDeviceType());
                    item.put("nickName", deviceIconData.getNickName());
                    item.put("controlType", deviceIconData.getControlType());
                    item.put("main_subUuid", deviceIconData.getMain_subUuid());
                    item.put("status", deviceIconData.getStatus());
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }

        return item;
    }

    public static JSONObject getJsonObjectForString(String str){
        JSONObject obj = null;
        if(str == null)
            return null;

        try{
            obj = new JSONObject(str);
        }catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }
        return obj;
    }

    public static JSONArray getIconDataJsonArrayForString(String str){
        JSONArray array = null;
        if(str == null)
            return null;

        try{
            array = new JSONArray(str);
        }catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }
        return array;
    }

    public static IconData getIconDataForJson(Context context, JSONObject json) {
        IconData data = null;
        try {
            int type = json.getInt("type");
            String componentName = json.getString("component");
            Drawable drawable = null;
            if (type == IconData.TYPE_APPS) {
                List<ResolveInfo> list = ResourceFinder.getApps();

                for (int i = 0; i < list.size(); i++) {
                    String packageName = list.get(i).activityInfo.name;
                    if (packageName != null && packageName.equals(componentName)) {
                        try {
                            PackageManager packageManager = context.getPackageManager();
                            drawable = BitmapTool.copy(context, list.get(i).loadIcon(packageManager));
                        } catch (Exception e) {
                            Log.d(TAG, "TYPE_APPS drawble load error" + e.getMessage());
                        }
                    }
                }
            } else if (type == IconData.TYPE_WIDGET) {
                ArrayList<AppWidgetProviderInfo> list = ResourceFinder.getWidgets();

                for (int i = 0; i < list.size(); i++) {
                    String packageName = list.get(i).provider.getClassName();
                    if (packageName != null && packageName.equals(componentName)) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                drawable = BitmapTool.copy(context, list.get(i).loadPreviewImage(context, DisplayMetrics.DENSITY_HIGH));
                                if(drawable == null)
                                    drawable = BitmapTool.copy(context, list.get(i).loadIcon(context, DisplayMetrics.DENSITY_XXXHIGH));
                            }
                            else
                                drawable = BitmapTool.copy(context, R.drawable.android_default_icon);
                        } catch (Exception e) {
                            Log.d(TAG, "TYPE_WIDGET drawble load error" + e.getMessage());
                        }
                    }
                }
            } else if (type == IconData.TYPE_DEVICE) {
                try {
                    drawable = BitmapTool.copy(context, initIcon(json.getString("status"), json.getString("deviceType")));
                } catch (Exception e) {
                    Log.d(TAG, "TYPE_DEVICE error" + e.getMessage());
                }
            }

            if (json.getInt("type") == IconData.TYPE_DEVICE) {
                data = new DeviceIconData(json.getInt("type"),
                        drawable,
                        json.getString("name"),
                        json.getInt("x"),
                        json.getInt("y"),
                        json.getInt("width"),
                        json.getInt("height"),
                        json.getString("rootUuid"),
                        json.getString("deviceType"),
                        json.getString("nickName"),
                        json.getString("controlType"),
                        json.getString("main_subUuid"),
                        json.getString("status"));
                data.changeId(json.getInt("id"));
            } else {
                data = new IconData(json.getInt("type"),
                        drawable,
                        json.getString("name"),
                        json.getInt("x"),
                        json.getInt("y"),
                        json.getInt("width"),
                        json.getInt("height"));

                if(json.getBoolean("real_widget"))
                    data.setRealWidget();

                data.setWidgetId(json.getInt("widget_id"));
                data.setPackageName(json.getString("package"));
                data.setComponentName(componentName);
                data.setImgResId(json.getInt("imgResId"));
                data.changeId(json.getInt("id"));
                data.setAlignWidget(false);
            }
        } catch (JSONException e) {
            Log.d(TAG, "parse error :" + e.getMessage());
        }
        return data;
    }

    public static int initIcon(String status, String deviceType){
        try {
            if (deviceType != null) {
                // 사이즈 확인을 위하여 Default 배경 처리, IconLayout.updateIconData 에서 이후 갱신됨
                if (status.equalsIgnoreCase(Status.ON)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.HEAT)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.UNLOCK)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.LOCK)) {
                    return R.mipmap.btn_home_setbg_n;
                } else if (status.equalsIgnoreCase(Status.OFF)) {
                    return R.mipmap.btn_home_setbg_n;
                } else if (status.equalsIgnoreCase(Status.COOL)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.AUTO)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.FAN)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.DRY)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.MOIST)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.ENERGYHEAT)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.ENERGYCOOL)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.FULLPOWER)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.AWAYON)) {
                    return R.mipmap.btn_home_setbg_n;
                } else if (status.equalsIgnoreCase(Status.RESERVATION)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.BATH)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.HEATWATER)) {
                    return R.mipmap.btn_home_setbg_s;
                } else if (status.equalsIgnoreCase(Status.H24RESERVATION)) {
                    return R.mipmap.btn_home_setbg_s;
                } else {
                    return R.mipmap.btn_home_setbg_n;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return R.drawable.android_default_icon;
    }
}
