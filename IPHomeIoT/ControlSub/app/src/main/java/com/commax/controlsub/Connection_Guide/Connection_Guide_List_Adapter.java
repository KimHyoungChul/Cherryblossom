package com.commax.controlsub.Connection_Guide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.controlsub.Common.TypeDef;
import com.commax.controlsub.MainActivity;
import com.commax.controlsub.R;

import java.util.ArrayList;

/**
 * Created by OWNER on 2017-01-06.
 */
public class Connection_Guide_List_Adapter extends BaseAdapter {
    String TAG = Connection_Guide_List_Adapter.class.getSimpleName();


    LayoutInflater inflater;
    Context mContext;
    ArrayList<Connection_Guide_ListCell> mListData;

    ImageView more_button;


    public long getItemId(int position) {
//        return 0;
        return position;
    }
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
        //return null;
    }

    public Connection_Guide_List_Adapter(Context context , ArrayList<Connection_Guide_ListCell> items)
    {
        super();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mListData = items;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        final Connection_Guide_ListCell cell = mListData.get(position);

        v = inflater.inflate(R.layout.guide_list_item_layout, null);

        TextView device_name = (TextView)v.findViewById(R.id.device_name);
        TextView device_company_name = (TextView)v.findViewById(R.id.device_company_name);
        ImageView device_image = (ImageView)v.findViewById(R.id.device_image);

        device_name.setText(mListData.get(position).DeviceName);
        device_company_name.setText(mListData.get(position).CompanyName);
        device_image.setImageDrawable(mListData.get(position).mIcon);

        more_button = (ImageView) v.findViewById(R.id.more_button);

        more_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                MainActivity.getInstance().connectionGuideMain.change_mode(TypeDef.CONNECT_DEVICE_DETAIL_MODE , cell);
            }
        });

        //click listitem
        v.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "List Item clicked : " + cell.getDeviceName());
                MainActivity.getInstance().connectionGuideMain.change_mode(TypeDef.CONNECT_DEVICE_DETAIL_MODE , cell);
            }
        });
        return v;
    }

    public void setDynamicHeight(ListView mListView) {
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
    }

}
