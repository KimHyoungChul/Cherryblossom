package com.commax.settings.call;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

/**
 * 통화 설정
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class CallSettingFragment extends Fragment implements CallTimeConfirmListener, MovieRecordTimeConfirmListener {


    private static final String LOG_TAG = CallSettingFragment.class.getSimpleName();

    public CallSettingFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addMenuItemListener();
        addSwitchListener();
        showCallTime();
        showMovieRecordTime();
        initSwitch();
    }


    /**
     * 연속통화시간 표시
     */
    private void showCallTime() {

        String callTime = ContentProviderManager.getCallTime(getActivity());
        TextView callTimeValue = (TextView) getActivity().findViewById(R.id.callTimeValue);

        //나중에 수정할 수 있음!!
        if(callTime == null) callTime = "180"; //default
        //callTime += " " + getActivity().getString(callTimeUnit_sec);
        callTime = CallTimeSettingManager.getInstance(getActivity()).getListString(callTime, CallTimeSettingManager.INDEX_CALLTIME);
        callTimeValue.setText(callTime);
    }

    /**
     * 영상녹화시간 표시
     */
    private void showMovieRecordTime() {

        String movieRecordTime = ContentProviderManager.getMovieRecordTime(getActivity()); //2017-01-12,yslee::bug fixed
        TextView movieRecordTimeValue = (TextView) getActivity().findViewById(R.id.movieRecordTimeValue);

        //나중에 수정할 수 있음!!
        if(movieRecordTime == null) movieRecordTime = "60"; //default
        //movieRecordTime += " " + getActivity().getString(callTimeUnit_sec);
        movieRecordTime = CallTimeSettingManager.getInstance(getActivity()).getListString(movieRecordTime, CallTimeSettingManager.INDEX_MOVIE_RECORDTIME);
        movieRecordTimeValue.setText(movieRecordTime);
    }


    private void initSwitch() {

        String autoSave = ContentProviderManager.getMovieAutoSave(getActivity());
        CheckBox autoSaveSwitch = (CheckBox) getActivity().findViewById(R.id.autoSaveSwitch);
        autoSaveSwitch.setChecked(autoSave != null ? autoSave.equals(CommaxConstants.ENABLED) ? true : false : true);

        if(TypeDef.DISPLAY_SMARTPHONE_ENABLE) {
            String smartphoneCall = ContentProviderManager.getSmartphoneCall(getActivity());
            CheckBox smartPhoneCallSwitch = (CheckBox) getActivity().findViewById(R.id.smartPhoneCallSwitch);
            smartPhoneCallSwitch.setChecked(smartphoneCall != null ? smartphoneCall.equals(CommaxConstants.ENABLED) ? true : false : true);
        }
    }


    private void addSwitchListener() {
        //영상자동저장
        CheckBox autoSaveSwitch = (CheckBox) getActivity().findViewById(R.id.autoSaveSwitch);
        autoSaveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                handleAutoSaveSwitch(isChecked);
                sendMovieAutoSaveBroadcast();

            }
        });

        //스마트폰 통화수신
        if(TypeDef.DISPLAY_SMARTPHONE_ENABLE) {
            CheckBox smartPhoneCallSwitch = (CheckBox) getActivity().findViewById(R.id.smartPhoneCallSwitch);

            smartPhoneCallSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    handleSmartphoneCallSwitch(isChecked);
                    sendSmartphoneCallBroadcast();
                }
            });
        } else {
            RelativeLayout item_deviceServerIp = (RelativeLayout) getActivity().findViewById(R.id.smartPhoneCall);
            item_deviceServerIp.removeAllViews();
            item_deviceServerIp.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.smartphone_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }
    }

    /**
     * 스마트폰 통화수신 체크 처리
     *
     * @param isChecked
     */
    private void handleSmartphoneCallSwitch(boolean isChecked) {

        String value = isChecked ? CommaxConstants.ENABLED : CommaxConstants.DISABLED;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.SmartPhoneCallEntry.COLUMN_NAME_SMARTPHONE_CALL, value);


        ContentProviderManager.deletePreviousSmartphoneCall(getActivity());
        ContentProviderManager.saveSmartphoneCall(getActivity(), contentValues);


    }

    /**
     * 영상자동저장 체크 처리
     *
     * @param isChecked
     */
    private void handleAutoSaveSwitch(boolean isChecked) {

        String value = isChecked ? CommaxConstants.ENABLED : CommaxConstants.DISABLED;

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.MovieAutoSaveEntry.COLUMN_NAME_MOVIE_AUTO_SAVE, value);


        ContentProviderManager.deletePreviousMovieAutoSave(getActivity());
        ContentProviderManager.saveMovieAutoSave(getActivity(), contentValues);


    }


    /**
     * 메뉴 항목 클릭 리스너 추가
     */
    private void addMenuItemListener() {
        //연속통화시간 설정
        RelativeLayout callTime = (RelativeLayout) getActivity().findViewById(R.id.callTime);
        callTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                //showCallTimePopup();
                showCallTimeSelectPopup();
            }
        });

        //영상녹화시간 설정
        RelativeLayout movieRecordTime = (RelativeLayout) getActivity().findViewById(R.id.movieRecordTime);
        movieRecordTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                //showMovieRecordTimePopup();
                showMovieRecordTimeSelectPopup();
            }
        });

    }

    /**
     * 영상녹화시간 설정 팝업 표시(직접입력)
     */
    private void showMovieRecordTimePopup() {
        MovieRecordTimePopup popup = new MovieRecordTimePopup(getActivity(), this);
        popup.show();
    }

    /**
     * 영상녹화시간 설정 팝업 표시(리스트선택)
     */
    private void showMovieRecordTimeSelectPopup() {
        MovieRecordTimeSelectPopup popup = new MovieRecordTimeSelectPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.movieRecordTime));
        popup.setPopupData(CallTimeSettingManager.getInstance(getActivity()).getVideoRecordTimeList());

        String movieRecordTime = ContentProviderManager.getMovieRecordTime(getActivity());
        int selItem = CallTimeSettingManager.getInstance(getActivity()).getListIndex(movieRecordTime,CallTimeSettingManager.INDEX_MOVIE_RECORDTIME);
        popup.initList(getActivity(),selItem);
        popup.show();

    }

    /**
     * 연속통화시간 설정 팝업 표시(직접입력)
     */
    private void showCallTimePopup() {
        CallTimePopup popup = new CallTimePopup(getActivity(), this);
        popup.show();
    }

    /**
     * 연속통화시간 설정 팝업 표시(리스트선택)
     */
    private void showCallTimeSelectPopup() {

        CallTimePopupSelectPopup popup = new CallTimePopupSelectPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.callTime));
        popup.setPopupData(CallTimeSettingManager.getInstance(getActivity()).getCallTimeList());

        String callTime = ContentProviderManager.getCallTime(getActivity());
        int selItem = CallTimeSettingManager.getInstance(getActivity()).getListIndex(callTime,CallTimeSettingManager.INDEX_CALLTIME);
        popup.initList(getActivity(),selItem);
        popup.show();

    }

    @Override
    public void onCallTimeConfirm() {
        showCallTime();
        sendCallTimeBroadcast();
    }

    /**
     * 영상자동저장 브로드캐스트 전송
     */
    private void sendMovieAutoSaveBroadcast() {
        String movieAutoSave = ContentProviderManager.getMovieAutoSave(getActivity());

        Intent intent = new Intent(CommaxConstants.BROADCAST_MOVIE_AUTO_SAVE);
        intent.putExtra(CommaxConstants.KEY_MOVIE_AUTO_SAVE, movieAutoSave);
        getActivity().sendBroadcast(intent);
    }

    /**
     * 스마트폰 통화수신 브로드캐스트 전송
     */
    private void sendSmartphoneCallBroadcast() {
        String smartphoneCall = ContentProviderManager.getSmartphoneCall(getActivity());

        Log.d(LOG_TAG, "sendSmartphoneCallBroadcast");

        Intent intent = new Intent(CommaxConstants.BROADCAST_SMARTPHONE_CALL);
        intent.putExtra(CommaxConstants.KEY_SMARTPHONE_CALL, smartphoneCall);
        getActivity().sendBroadcast(intent);
    }

    /**
     * 연속통화시간 브로드캐스트 전송
     */
    private void sendCallTimeBroadcast() {
        String callTime = ContentProviderManager.getCallTime(getActivity());

        Intent intent = new Intent(CommaxConstants.BROADCAST_CALLTIME);
        intent.putExtra(CommaxConstants.KEY_CALLTIME, callTime);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onMovieRecordTimeConfirm() {
        showMovieRecordTime();
        sendMovieRecordTimeBroadcast();
    }

    /**
     * 영상녹화시간 브로드캐스트 전송
     */
    private void sendMovieRecordTimeBroadcast() {
        String movieRecordTime = ContentProviderManager.getMovieRecordTime(getActivity());

        Intent intent = new Intent(CommaxConstants.BROADCAST_MOVIE_RECORD_TIME);
        intent.putExtra(CommaxConstants.KEY_MOVIE_RECORD_TIME, movieRecordTime);
        getActivity().sendBroadcast(intent);
    }
}
