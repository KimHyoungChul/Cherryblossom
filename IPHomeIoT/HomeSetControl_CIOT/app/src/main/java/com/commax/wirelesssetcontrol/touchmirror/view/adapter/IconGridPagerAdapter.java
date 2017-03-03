package com.commax.wirelesssetcontrol.touchmirror.view.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.touchmirror.view.IconGridPageFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shin Sung on 2017-02-15.
 */
public class IconGridPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "pager_adpater";

    private static ArrayList<String> mPageArray;

    private SparseArray<IconGridPageFragment> mRegisterFragment;

    public IconGridPagerAdapter(FragmentManager fm) {
        super(fm);
        mPageArray = new ArrayList<>();
        mRegisterFragment = new SparseArray<>();
    }

    @Override
    public IconGridPageFragment getItem(int position) {
        return IconGridPageFragment.create(position);
    }

    @Override
    public int getCount() {
        return mPageArray.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        IconGridPageFragment fragment = (IconGridPageFragment) super.instantiateItem(container, position);
        mRegisterFragment.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisterFragment.remove(position);
        super.destroyItem(container, position, object);
    }

    public IconGridPageFragment getPageFrament(int position){
        return mRegisterFragment.get(position);
    }

    public static void setPageSize(int size){
        for(int i=0; i<size; i++)
            mPageArray.add("[]");
    }

    public static void addPageData(int idx, JSONObject json){
        if(idx < mPageArray.size()) {
            synchronized (mPageArray) {
                try {
                    JSONArray jsonArray = new JSONArray(mPageArray.get(idx));
                    jsonArray.put(json);
                    mPageArray.set(idx, jsonArray.toString());
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException : " + e.getMessage());
                }
            }
        }
    }

    public static void removePageDataByRootUuid(String rootUuid){
        try {
            for (int i = 0; i < mPageArray.size(); i++) {
                JSONArray jsonArray = new JSONArray(mPageArray.get(i));
                JSONArray jsonCopy = new JSONArray();
                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject obj = jsonArray.getJSONObject(j);
                    if(!obj.isNull("rootUuid") && obj.getString("rootUuid").equals(rootUuid))
                       Log.d(TAG, ">> delete : " + rootUuid);
                    else
                        jsonCopy.put(obj);
                }
                mPageArray.set(i, jsonCopy.toString());
            }
        }catch(JSONException e){
            Log.d(TAG, "JSONException : " + e.getMessage());
        }
    }

    public static void replacePageData(int idx, String pageStr){
        mPageArray.set(idx, pageStr);
    }

    public static String getPageData(int idx){
        if(mPageArray == null)
            return null;

        return mPageArray.get(idx);
    }
}
