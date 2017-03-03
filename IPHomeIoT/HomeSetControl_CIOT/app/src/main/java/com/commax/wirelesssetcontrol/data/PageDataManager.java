package com.commax.wirelesssetcontrol.data;

import android.content.Context;

import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.device.DeviceInfo;
import com.commax.wirelesssetcontrol.tools.Prefs;
import com.commax.wirelesssetcontrol.touchmirror.view.adapter.IconGridPagerAdapter;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.subview.IconLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by OWNER on 2017-02-20.
 */
public class PageDataManager {
    private static final String TAG = "PageDataManager";

    private static final String PAGE_PREFERENCE = "PageInfo";

    private static final String KEY_ROOM = "room_data";
    private static final String KEY_BOTTOM = "bottom_data";

    private static PageDataManager mManager;
    private static Context mContext;

    public static void init(Context context){
        mManager = new PageDataManager();
        mContext = context;
    }

    public static PageDataManager getInst(){
        return mManager;
    }

    /***하단 영역***/
    public void putBottomGrid(String data){
        Prefs.put(mContext, PAGE_PREFERENCE, KEY_BOTTOM, data);
    }

    public String getBottomGrid(){
        return Prefs.get(mContext, PAGE_PREFERENCE, KEY_BOTTOM);
    }


    /***상단 영역***/
    //페이지 정보 추가
    public void addPage(PageData data){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);
        try {
            JSONArray jsonArray;
            if(array == null || (array != null && array.length() == 0))
                jsonArray = new JSONArray();
            else
                jsonArray = new JSONArray(array);

            JSONObject jsonData = convertJSONObject(data);
            jsonArray.put(jsonData);
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, jsonArray.toString());
        } catch (JSONException e) {
            Log.d(TAG, "addPage exception " + e.getMessage());
        }
    }

    //페이지 정보 수정
    public void replacePage(int page, PageData data){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            JSONArray afterArray = new JSONArray();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                if(i != page)
                    afterArray.put(obj);
                else{
                    JSONObject dataObj = convertJSONObject(data);
                    afterArray.put(dataObj);
                }
            }
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, afterArray.toString());
        } catch (JSONException e) {
            Log.d(TAG, "addPage exception " + e.getMessage());
        }
    }

    //해당 페이지에 아이템 넣기
    public void addPageItem(int page, DeviceIconData data){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            JSONArray jsonCopy = new JSONArray();
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if(page == i) {
                    JSONArray iconArray = new JSONArray(obj.getString("icon_data"));
                    iconArray.put(IconDataParser.getJSonForIconData(data));
                    obj.remove("icon_data");
                    obj.put("icon_data", iconArray.toString());
                }
                jsonCopy.put(obj);
            }
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, jsonCopy.toString());
        } catch (JSONException e) {
            Log.d(TAG, "getPageData exception " + e.getMessage());
        }

    }

    //페이지 정보 수정 JSON 기반
    public void replacePage(int page, JSONObject data){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            JSONArray afterArray = new JSONArray();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                if(i != page)
                    afterArray.put(obj);
                else
                    afterArray.put(data);
            }
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, afterArray.toString());
        } catch (JSONException e) {
            Log.d(TAG, "addPage exception " + e.getMessage());
        }
    }

    //페이지 사이즈 반환
    public int getPageSize(){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            return jsonArray.length();
        } catch (JSONException e) {
            Log.d(TAG, "getPageSize exception " + e.getMessage());
        }
        return 0;
    }

    //페이지 정보 반환
    public PageData getPageData(int page){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            return convertPageData(jsonArray.getJSONObject(page));
        } catch (JSONException e) {
            Log.d(TAG, "getPageData exception " + e.getMessage());
        }

        return null;
    }

    //페이지 삭제
    public void removePage(int page){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            JSONArray afterArray = new JSONArray();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                if(i != page)
                    afterArray.put(obj);
            }
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, afterArray.toString());
        } catch (JSONException e) {
            Log.d(TAG, "removePage exception " + e.getMessage());
        }
    }

    //페이지 교체
    public void changePagePos(int des, int source){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray jsonArray = new JSONArray(array);
            JSONArray afterArray = new JSONArray();
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);
                if(i == des)
                    afterArray.put(jsonArray.getJSONObject(source));
                else if(i == source)
                    afterArray.put(jsonArray.getJSONObject(des));
                else
                    afterArray.put(obj);
            }
            Prefs.put(mContext, PAGE_PREFERENCE, KEY_ROOM, afterArray.toString());
        } catch (JSONException e) {
            Log.d(TAG, "addPage exception " + e.getMessage());
        }
    }


    //페이지 데이터로 변환
    private PageData convertPageData(JSONObject obj){
        PageData data = new PageData();
        try {
            data.name = obj.getString("name");
            data.background = obj.getString("background");
            data.iconData = obj.getString("icon_data");
        } catch (JSONException e) {
            Log.d(TAG, "convertPageData exception " + e.getMessage());
        }
        return data;
    }

    //JSON 데이터로 변환
    private JSONObject convertJSONObject(PageData data){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", data.name);
            obj.put("background", data.background);
            obj.put("icon_data", data.iconData);
        } catch (JSONException e) {
            Log.d(TAG, "convertPageData exception " + e.getMessage());
        }
        return obj;
    }

    //디바이스 상태 변경 변경
    public static int PAGE_MODE_DEVICE_UPDATE_STATUS = 0;  //상태변경
    public static int PAGE_MODE_DEVICE_UPDATE_NICK_NAME = 1;  //닉네임 변경
    public static int PAGE_MODE_DEVICE_REMOVE = 2;   //root uuid 일치 항목 삭제
    public static int PAGE_MODE_DEVICE_REMOVE_TYPE = 3; //해당타입 모두 삭제

    public void updateDevice(int mode, String rootUuid, String value){
        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);

        try {
            JSONArray dataArray = new JSONArray(array);
            for(int i=0; i<dataArray.length(); i++){
                JSONObject pageObj = dataArray.getJSONObject(i);
                JSONArray copyList = new JSONArray();
                JSONArray arrayList = new JSONArray(pageObj.getString("icon_data"));
                pageObj.remove("icon_data");

                for(int j=0; j<arrayList.length(); j++){
                    JSONObject obj = arrayList.getJSONObject(j);
                    if(mode == PAGE_MODE_DEVICE_UPDATE_STATUS) {
                        if (!obj.isNull("rootUuid") && obj.getString("rootUuid").equals(rootUuid)) {
                            obj.remove("status");
                            obj.put("status", value);
                        }
                        copyList.put(obj);
                    }
                    else if(mode == PAGE_MODE_DEVICE_UPDATE_NICK_NAME) {
                        if(!obj.isNull("rootUuid")  && obj.getString("rootUuid").equals(rootUuid)){
                            obj.remove("nickName");
                            obj.put("nickName", value);
                        }
                        copyList.put(obj);
                    }
                    else if(mode == PAGE_MODE_DEVICE_REMOVE) {
                        if(!(!obj.isNull("rootUuid") && obj.getString("rootUuid").equals(rootUuid)))
                            copyList.put(obj);
                    }
                    else if(mode == PAGE_MODE_DEVICE_REMOVE_TYPE) {
                        if (obj.getInt("type") != Integer.valueOf(value))
                            copyList.put(obj);
                    }
                }
                pageObj.put("icon_data", copyList.toString());
                replacePage(i, pageObj);
            }
        }catch (JSONException e) {
            android.util.Log.d(TAG, "updateDevice json error : " + e.getMessage());
        }
    }

    //json 디바이스 상태 갱신
    public void updateDeviceJsonData(DeviceInfo deviceInfo){
        if(deviceInfo == null)
            return;
        else if(deviceInfo.mainDevice == null || deviceInfo.deviceType == null)
            return;
        else if(deviceInfo.deviceType.length() == 0)
            return;

        String array = Prefs.get(mContext, PAGE_PREFERENCE, KEY_ROOM);
        try {
            JSONArray dataArray = new JSONArray(array);
            for(int i=0; i<dataArray.length(); i++){
                JSONObject pageObj = dataArray.getJSONObject(i);
                JSONArray copyList = new JSONArray();
                JSONArray arrayList = new JSONArray(pageObj.getString("icon_data"));
                pageObj.remove("icon_data");

                for(int j=0; j<arrayList.length(); j++){
                    JSONObject obj = arrayList.getJSONObject(j);
                    if(!obj.isNull("rootUuid") && obj.getString("rootUuid").equals(deviceInfo.rootUuid)) {
                        obj.put("name", deviceInfo.nickName);
                        obj.put("nickName", deviceInfo.nickName);
                        obj.put("status", deviceInfo.mainDevice.value == null ? "" : deviceInfo.mainDevice.value);
                    }
                    copyList.put(obj);
                }
                pageObj.put("icon_data", copyList.toString());
                replacePage(i, pageObj);
            }
        }catch (JSONException e) {
            android.util.Log.d(TAG, "updateDevice json error : " + e.getMessage());
        }
    }
}
