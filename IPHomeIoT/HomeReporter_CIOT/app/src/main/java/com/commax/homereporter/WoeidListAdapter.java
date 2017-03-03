package com.commax.homereporter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class WoeidListAdapter extends BaseAdapter {

    static final String TAG = "WoeidListAdapter";
    private Context mContext;

    int selected = -1;

    private List<WoeidItem> mItems = new ArrayList<WoeidItem>();

    public WoeidListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(WoeidItem it) {
        mItems.add(it);
    }

    public void setListItems(List<WoeidItem> lit) {
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
        final WoeidView itemView;

        if(convertView==null){
            itemView=new WoeidView(mContext, mItems.get(position));
        }else {
            itemView = (WoeidView)convertView;
        }

        itemView.setText(0, mItems.get(position).getData(0));
        itemView.updateCheck(mItems.get(position).isChecked());
//        if(selected==position) {
//            Log.d(TAG, "selected "+position);
//            mItems.get(position).setChecked();
//            itemView.setChecked();
//        }else {
//            mItems.get(position).setUnchecked();
//            itemView.setUnchecked();
//        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = position;

                for(int i=0;i<mItems.size();i++) {
                    if(i==position) {
                        Log.d(TAG, "check position "+i);
                        mItems.get(position).setChecked();
                        itemView.setChecked();
                    }else {
                        Log.d(TAG, "not check position "+i);
                        mItems.get(i).setUnchecked();
                        itemView.setUnchecked();
                    }
                }
                notifyDataSetChanged();
            }
        });

        return itemView;
    }

    public String[] getData(int position){
        return new String[]{mItems.get(position).getData(0), mItems.get(position).getData(1), mItems.get(position).getData(2), mItems.get(position).getData(3), mItems.get(position).getData(4)};
    }

    public void clearItems(){
        mItems.clear();
    }
}
