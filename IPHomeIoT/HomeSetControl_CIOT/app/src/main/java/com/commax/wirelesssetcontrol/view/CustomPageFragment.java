package com.commax.wirelesssetcontrol.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.commax.wirelesssetcontrol.HandlerEvent;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.R;
import com.commax.wirelesssetcontrol.data.PageData;
import com.commax.wirelesssetcontrol.data.PageDataManager;
import com.commax.wirelesssetcontrol.data.ResourceManager;
import com.commax.wirelesssetcontrol.touchmirror.common.Constants;
import com.commax.wirelesssetcontrol.touchmirror.view.GridActionLayout;
import com.commax.wirelesssetcontrol.touchmirror.view.data.DeviceIconData;
import com.commax.wirelesssetcontrol.touchmirror.view.data.IconData;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.BitmapTool;
import com.commax.wirelesssetcontrol.touchmirror.view.tools.IconDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomPageFragment extends Fragment {
    private final String TAG = "CustomPageFragment";
    public static final String ARG_PAGE = "page";

    private Context mContext;

    private int mPageNumber;

    private FrameLayout mViewBg;
    private GridActionLayout mTouchArea;
    boolean attached = false;

    public static CustomPageFragment create(int pageNumber) {
        CustomPageFragment fragment = new CustomPageFragment();
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

    private void setRoomBackground(){
        Drawable res = null;

        try {
            PageData pageData = PageDataManager.getInst().getPageData(mPageNumber);
            if(pageData != null)
                res = ResourceManager.getRoomBackgroundResource(pageData.background, mContext);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(res != null)
            mViewBg.setBackground(res);
        else
            mViewBg.setBackground(BitmapTool.copy(mContext, R.mipmap.bg_home_img_1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_custom_page, container, false);
        mViewBg = (FrameLayout) rootView.findViewById(R.id.custom_page_fragment_bg);
        mTouchArea = (GridActionLayout) rootView.findViewById(R.id.custom_page_fragment_touch);
        mTouchArea.init(Constants.AREA_TOUCH_COL, Constants.AREA_TOUCH_ROW);

        setRoomBackground();
        setTouchArea();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        attached = true;
    }

    @Override
    public void onDestroy() {
        BitmapTool.recursive(mViewBg);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        attached = false;
    }

    //터치 영역 레이아웃 반환
    public GridActionLayout getTouchArea(){
        return mTouchArea;
    }

    //JSON데이터를 기반으로 페이지를 다시 그린다.
    private void setTouchArea(){
        Log.d(TAG, mPageNumber + " data update.");
        mTouchArea.post(new Runnable() {
            @Override
            public void run() {
                PageData pageData = PageDataManager.getInst().getPageData(mPageNumber);
                if(pageData == null)
                    return ;

                String jsonStr = pageData.iconData;
                if(jsonStr != null && jsonStr.length() > 0) {
                    JSONArray array = IconDataParser.getIconDataJsonArrayForString(jsonStr);
                    for(int i=0; i<array.length(); i++){
                        try {
                            JSONObject obj = array.getJSONObject(i);
                            IconData data = IconDataParser.getIconDataForJson(mContext, obj);
                            if(data != null)
                                mTouchArea.addItem(data.getPosition().x, data.getPosition().y, data);
                        }catch(JSONException e){
                            Log.d(TAG, "Refresh Parse Error : " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
