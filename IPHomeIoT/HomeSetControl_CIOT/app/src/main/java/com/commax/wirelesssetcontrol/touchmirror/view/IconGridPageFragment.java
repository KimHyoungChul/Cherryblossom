package com.commax.wirelesssetcontrol.touchmirror.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.touchmirror.view.adapter.IconGridPagerAdapter;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;

import org.json.JSONArray;
import org.json.JSONObject;

public class IconGridPageFragment extends Fragment {
    private final String TAG = "IconGridPage";
    public static final String ARG_PAGE = "page";

    private Context mContext;
    private LinearLayout mParent;
    private GridFrameLayout mTouchArea;
    private FrameLayout mLeftMargin;

    private static Point mScreenSize = new Point();

    private int mPageNumber = 0;

    public static IconGridPageFragment create(int pageNumber) {
        IconGridPageFragment fragment = new IconGridPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        try {
            mPageNumber = getArguments().getInt(ARG_PAGE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.s_fragment_icon_page, container, false);
        mParent = (LinearLayout) rootView.findViewById(R.id.icon_grid_parent);
        mTouchArea = (GridFrameLayout)rootView.findViewById(R.id.icon_grid_fragment);
        mTouchArea.init(mScreenSize.x, mScreenSize.y);
        mLeftMargin = (FrameLayout)rootView.findViewById(R.id.icon_grid_fragment_left);
        setTouchArea();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        BitmapTool.recursive(mParent);
        mContext = null;
        mTouchArea = null;
        mParent = null;
        mLeftMargin = null;
        super.onDestroy();
    }

    public static void setScreenSize(int col, int row){
        mScreenSize.set(col, row);
    }

    //터치 영역 레이아웃 반환
    public GridFrameLayout getTouchArea(){
        return mTouchArea;
    }

    public int getLeftmargin(){
        return mLeftMargin.getWidth();
    }

    //JSON데이터를 기반으로 페이지를 다시 그린다.
    private void setTouchArea(){
        Log.d(TAG, mPageNumber + " data update.");
        mTouchArea.post(new Runnable() {
            @Override
            public void run() {
                String jsonStr = IconGridPagerAdapter.getPageData(mPageNumber);
                if(jsonStr == null)
                    return ;

                if(jsonStr != null && jsonStr.length() > 0) {
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(jsonStr);
                    for(int i=0; i<array.length(); i++){
                        try {
                            JSONObject obj = array.getJSONObject(i);
                            IconData data = IconDataParser.getIconDataForJson(mContext, obj);
                            if(data.getType() == IconData.TYPE_WIDGET)
                                data.setAlignWidget(true);
                            mTouchArea.addItemResize(data, new Point(1,1));
                        }catch(Exception e){
                            Log.d(TAG, "Refresh Parse Error : " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
