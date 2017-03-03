package com.commax.settings.wallpad_info;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.settings.R;

import java.util.List;

/**
 * 코맥스 App 어댑터
 * Created by OWNER on 2016-11-25.
 */

public class AppAdapter extends ArrayAdapter<AppInfo> {

    private final Context mContext;
    private PackageManager pm = null;
    private List<AppInfo> mDatas;

    AppAdapter(Context context, PackageManager pm, List<AppInfo> apps) {
        super(context, R.layout.list_item_row, apps);
        this.pm = pm;
        mContext = context;
        mDatas = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(parent);
        }

        bindView(position, convertView);

        return (convertView);
    }

    private View newView(ViewGroup parent) {
        return (((Activity) mContext).getLayoutInflater().inflate(R.layout.list_item_row, parent, false));
    }

    private void bindView(int position, View row) {
        TextView appName = (TextView) row.findViewById(R.id.appName);

        appName.setText(getItem(position).getLabel());

        TextView version = (TextView) row.findViewById(R.id.version);

        version.setText(getItem(position).getVersion());

        ImageView icon = (ImageView) row.findViewById(R.id.icon);

        icon.setImageDrawable(getItem(position).getIcon());
    }

    /**
     * //2017-01-05,yslee::Device 추가
     *
     * @param appinfo
     */
    public void addAppInfo(AppInfo appInfo) {

        mDatas.add(appInfo);
        notifyDataSetChanged();
        //showDeviceOnList();
    }

    /**
     * 리스트에 Device 표시 추가
     *
     * @param device
     */
    private void showDeviceOnList() {

        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /*
     //2017-01-05,yslee::페이지 단위 앱리스트로 수정
     */
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

    /*
    //2017-01-12,yslee::앱리스트 Drawing 속도 높이기 위함
    */
    public void setFullExtendHeight(ListView mListView) {
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);

        //get Item unit height
        View listItem = getView(0, null, mListView);
        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
        height = listItem.getMeasuredHeight();

        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        params.height = height*getCount() + (mListView.getDividerHeight() * (getCount() - 1));
        mListView.setLayoutParams(params);
        mListView.requestLayout();
    }
}
