package com.commax.iphomiot.doorcall.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.commax.settings.doorphone_custom.CustomDevice;
import com.commax.iphomiot.doorcall.R;

import java.util.List;

public class ChooseDoorAdapter extends BaseAdapter {
    private List<CustomDevice> doorList_ = null;

    public ChooseDoorAdapter(List<CustomDevice> doorList) {
        super();
        doorList_ = doorList;
    }

    @Override
    public int getCount() {
        return doorList_.size();
    }

    @Override
    public Object getItem(int position) {
        return doorList_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.choosedoor_item, parent, false);
        }
        TextView tvDoorName = (TextView)convertView.findViewById(R.id.tvDoorName);
        tvDoorName.setText(doorList_.get(position).getModelName());

        return convertView;
    }
}
