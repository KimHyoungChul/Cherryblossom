package com.commax.settings.screensaver;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;


/**
 * 가치정보 설정화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class ScreensaverFragment extends Fragment implements ScreensaverTimeChangeListener {


    public ScreensaverFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screensaver, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //showScreensaverTime();
        addMenuItemListener();
        addSwitchListener();

        initSwitch(); //2017.01.04,yslee::스크린세이버 초기값 표시
    }

    private void addSwitchListener() {
        //가치정보 사용여부
        CheckBox useValueInfoSwitch = (CheckBox) getActivity().findViewById(R.id.useValueInfoSwitch);
        useValueInfoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                handleUseValueInfoSwitch(isChecked);
                sendUseValueInfoBroadcast();

            }
        });
    }

    private void initSwitch() {
        //가치정보 CheckBox
        String useValueInfo = ContentProviderManager.getUseValueInfo(getActivity());
        CheckBox useValueInfoSwitch = (CheckBox) getActivity().findViewById(R.id.useValueInfoSwitch);
        useValueInfoSwitch.setChecked(useValueInfo != null ? useValueInfo.equals(CommaxConstants.ENABLED) ? true : false : true);

    }

    private void sendUseValueInfoBroadcast() {
        String useValueInfo = ContentProviderManager.getUseValueInfo(getActivity());

        Intent intent = new Intent(CommaxConstants.BROADCAST_USE_VALUE_INFO);
        intent.putExtra(CommaxConstants.KEY_USE_VALUE_INFO, useValueInfo);
        getActivity().sendBroadcast(intent);

    }

    private void handleUseValueInfoSwitch(boolean isChecked) {

        String value = isChecked ? CommaxConstants.ENABLED : CommaxConstants.DISABLED;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.UseValueInfoEntry.COLUMN_NAME_USE_VALUE_INFO, value);


        ContentProviderManager.deletePreviousUseValueInfo(getActivity());
        ContentProviderManager.saveUseValueInfo(getActivity(), contentValues);
    }

    /**
     * 메뉴 항목에 리스너 추가
     */
    private void addMenuItemListener() {


//        //스크린세이버 시간 설정
//        RelativeLayout screensaverTimeSetting = (RelativeLayout) getActivity().findViewById(R.id.screensaverTimeSetting);
//        screensaverTimeSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                showScreensaverTimeSettingPopup();
//            }
//        });


    }

//    /**
//     * 스크린세이버 시간 선택 팝업창 표시
//     */
//    private void showScreensaverTimeSettingPopup() {
//        ScreensaverTimeSelectPopup popup = new ScreensaverTimeSelectPopup(getActivity(), this);
//        popup.show();
//    }


//    private void showScreensaverTime() {
//        TextView textView = (TextView) getActivity().findViewById(R.id.screensaverTimeValue);
//        textView.setText(getScreensaverTimeText());
//    }
//
//    private String getScreensaverTimeText() {
//
//        Screensaver screensaver = getPreviousScreensaverTime();
//        String selectedMode = screensaver.getTimeType();
//
//        if(selectedMode == null) {
//            return "항상 켜짐";
//        }
//
//        switch (selectedMode) {
//            case "0":
//                return "항상 꺼짐";
//
//            case "1":
//                return "항상 켜짐";
//            case "2":
//
//                return screensaver.getStartTime() + " " + "부터" + " " + screensaver.getFinishTime() + " " + "까지 항상 켜짐";
//
//            case "3":
//                return "매 시간 마다 " + screensaver.getIntervalTime() + " " + "켜짐";
//        }
//
//
//        return "항상 켜짐";
//    }
//
//    private Screensaver getPreviousScreensaverTime() {
//        return ContentProviderManager.getPreviousSelectedScreensaverTime(getActivity());
//    }


    @Override
    public void onScreensaverTimeChanged() {
        // showScreensaverTime();
    }
}
