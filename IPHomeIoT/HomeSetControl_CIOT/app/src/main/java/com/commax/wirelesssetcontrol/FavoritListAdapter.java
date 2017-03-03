package com.commax.wirelesssetcontrol;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class FavoritListAdapter extends BaseAdapter {

    static final String TAG = "ListAdapter";
    private Context mContext;

    private List<FavoriteItem> mItems = new ArrayList<FavoriteItem>();

    public FavoritListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(FavoriteItem it) {
        mItems.add(it);
    }

    public void setListItems(List<FavoriteItem> lit) {
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

        final FavoriteItemView itemView;

        if(convertView==null){
            itemView=new FavoriteItemView(mContext, mItems.get(position));
        }else {
            itemView = (FavoriteItemView)convertView;
        }

        try {

            itemView.setText(0, mItems.get(position).getAppName());
            itemView.updateCheck(mItems.get(position).isChecked());
            itemView.mIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!mItems.get(position).isChecked()) {

                        int checked_count = getCheckedCount();

                        if (!(checked_count>(NameSpace.MAX_QUICK-1))){

                            mItems.get(position).setChecked();
                            itemView.setChecked();
                        }
                    }else {

                        int checked_count = getCheckedCount();

                        if (checked_count>1) {
                            mItems.get(position).setChecked();
                            itemView.setChecked();
                        }else {
                            Log.d(TAG, "least 1");
                        }
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        return itemView;
    }

    private int getCheckedCount(){

        int count = 0;

        for(int i=0;i<mItems.size();i++){
            if (mItems.get(i).isChecked()){
                count++;
            }
        }

        Log.d(TAG, "getCheckedCount " + count);
        return count;

    }
}