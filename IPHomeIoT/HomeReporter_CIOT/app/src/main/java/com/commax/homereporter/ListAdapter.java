package com.commax.homereporter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends BaseAdapter {

    static final String TAG = "ListAdapter";
    private Context mContext;

    private List<SettingItem> mItems = new ArrayList<SettingItem>();

    public ListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(SettingItem it) {
        mItems.add(it);
    }

    public void setListItems(List<SettingItem> lit) {
        mItems = lit;
    }

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent){

        final SettingItemView itemView;

        if(convertView==null){
            itemView=new SettingItemView(mContext, mItems.get(position));
        }else {
            itemView = (SettingItemView)convertView;
        }

        try {
//        itemView.setIcon(mItems.get(position).getIcon());
            itemView.setText(0, mItems.get(position).getData());
            itemView.updateCheck(mItems.get(position).isChecked(), mItems.get(position).getType());
            itemView.mIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_WEATHER)) {
                        mItems.get(position).setChecked();
                        itemView.setChecked(mItems.get(position).getType());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "item clicked");
                    try {
                        if(itemView.mSelected) {
                            if (mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_WEATHER)) {
                                clearNavigationBar();
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra(NameSpace.INFO_SORT, NameSpace.INFO_WEATHER);
                                mContext.startActivity(intent);
                            } else if (mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_AIR)) {
                                clearNavigationBar();
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra(NameSpace.INFO_SORT, NameSpace.INFO_AIR);
                                mContext.startActivity(intent);
                            } else if (mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_HEALTH_LIFE)) {
                                clearNavigationBar();
                                Intent intent = new Intent(mContext, LocationActivity.class);
                                intent.putExtra(NameSpace.INFO_SORT, NameSpace.INFO_HEALTH_LIFE);
                                mContext.startActivity(intent);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            try {

//                if(itemView.mSelected) {
                    if ((mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_WEATHER))
                            || (mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_AIR))
                            || (mItems.get(position).getType().equalsIgnoreCase(NameSpace.INFO_HEALTH_LIFE))) {
                        itemView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                try {
                                    if (itemView.mSelected) {
                                        switch (event.getAction()) {
                                            case MotionEvent.ACTION_DOWN:
                                                itemView.setArrowAlpha(0.6f);
                                                break;
                                            case MotionEvent.ACTION_UP:
                                                itemView.setArrowAlpha(1.0f);
                                                break;
                                            case MotionEvent.ACTION_CANCEL:
                                                itemView.setArrowAlpha(1.0f);
                                                break;

                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                return false;
                            }
                        });
                    }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return itemView;
    }

    public void setDynamicHeight(ListView mListView) {

        try {
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < getCount(); i++) {
                View listItem = getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void clearNavigationBar(){

        try {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;

            // This work only for android 4.4+
            ((Activity)mContext).getWindow().getDecorView().setSystemUiVisibility(flags);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}