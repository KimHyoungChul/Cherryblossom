package com.commax.settings.screensaver;

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
 * 시간 스피너 어댑터
 * Created by bagjeong-gyu on 2016. 9. 30..
 */

public class SpinnerAdapter extends ArrayAdapter<String> {
    private final LayoutInflater mLayoutInflater;

    public SpinnerAdapter(Context context, int textViewResourceId, List<String> items) {
        super(context, textViewResourceId, items);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.spinner_header, null);
        }
        String name = getItem(position);

        TextView nameView = PlusViewHolder.get(v, R.id.spinnerHeaderName);

        nameView.setText(name);
        return v;
    }

    /**
     * 드롭다운 뷰 생성
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // notifyDataSetChanged();
        View v = convertView;
        if (v == null) {
            v = mLayoutInflater.inflate(R.layout.spinner_item, null);
        }
        String name = getItem(position);

        TextView nameView = PlusViewHolder.get(v, R.id.spinnerItemName);

        nameView.setText(name);
        return v;
    }
}
