package com.commax.settings.ringtone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.util.PlusViewHolder;

import java.util.List;

/**
 * 코맥스 벨소리 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class CommaxRingtoneListAdapter extends ArrayAdapter<CommaxRingtone> {


    private final LayoutInflater mLayoutInflater;
    private final List<CommaxRingtone> mDatas;


    public CommaxRingtoneListAdapter(Context context, int resource, List<CommaxRingtone> datas) {
        super(context, resource, datas);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = datas;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_ringtone,
                    parent, false);
        }


        TextView name = PlusViewHolder.get(convertView, R.id.name);

        String nameString = mDatas.get(position).getName();
        name.setText(nameString);


        return convertView;
    }


}
