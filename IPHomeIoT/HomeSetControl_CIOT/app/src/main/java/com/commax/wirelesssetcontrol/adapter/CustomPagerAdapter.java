package com.commax.wirelesssetcontrol.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.touchmirror.view.IconGridPageFragment;
import com.commax.wirelesssetcontrol.view.CustomPageFragment;
import com.commax.wirelesssetcontrol.Log;

/**
 * Created by OWNER on 2017-02-15.
 */
public class CustomPagerAdapter extends FragmentStatePagerAdapter {
    private String TAG = "pager_adpater";

    private SparseArray<CustomPageFragment> mRegisterFragment;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
        mRegisterFragment = new SparseArray<>();
    }

    @Override
    public CustomPageFragment getItem(int position) {
        return CustomPageFragment.create(position);
    }


    @Override
    public int getCount() {
        return PageDataManager.getInst().getPageSize();
    }

    @Override
    public int getItemPosition(Object object) {
        Log.d(TAG, "getItemPosition");
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CustomPageFragment fragment = (CustomPageFragment) super.instantiateItem(container, position);
        mRegisterFragment.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisterFragment.remove(position);
        super.destroyItem(container, position, object);
    }

    public CustomPageFragment getPageFrament(int position){
        return mRegisterFragment.get(position);
    }
}
