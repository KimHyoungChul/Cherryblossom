package com.commax.settings.screensaver;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.commax.settings.R;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * 가치정보 시간 선택 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class ScreensaverTimeSelectPopup extends Dialog {


    private final Context mContext;
    private final ScreensaverTimeChangeListener mListener;
    private CompoundButton mPreviousRadioButton;

    private String mSelectedMode;


    private String mSelectedTimeInterval;
    private String mSelectedStartTime;
    private String mSelectedFinishTime;

    public ScreensaverTimeSelectPopup(Context context, ScreensaverTimeChangeListener listener) {
        super(context, R.style.CustomDialog);

        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_screensaver_time_select);

        initValues();

        initSpinners(context);
        addButtonListener();

        initRadiobuttons();
    }

    private void initValues() {
        mSelectedMode = ScreensaverModeConstants.MODE_ALWAYS_ON;
        mSelectedStartTime = "";
        mSelectedFinishTime = "";
        mSelectedTimeInterval = "";
    }

    /**
     * 라디오버튼 초기화
     */
    private void initRadiobuttons() {
        //항상 꺼짐
        final CheckBox alwayOffSwitch = (CheckBox) findViewById(R.id.alwayOffSwitch);
        //항상 켜짐
        final CheckBox alwayOnSwitch = (CheckBox) findViewById(R.id.alwayOnSwitch);
        //파트 타임 켜짐
        final CheckBox fromTimeToTimeSwitch = (CheckBox) findViewById(R.id.fromTimeToTimeSwitch);
        //주기적으로 켜짐
        final CheckBox timeIntervalSwitch = (CheckBox) findViewById(R.id.timeIntervalSwitch);

        alwayOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //이전 선택한 라디오 버튼 해제
                uncheckPreviousRadioButton(buttonView);
                //설정한 모드를 저장
                mSelectedMode = ScreensaverModeConstants.MODE_ALWAYS_OFF;


            }
        });

        alwayOnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uncheckPreviousRadioButton(buttonView);
                mSelectedMode = ScreensaverModeConstants.MODE_ALWAYS_ON;
            }
        });


        fromTimeToTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uncheckPreviousRadioButton(buttonView);
                mSelectedMode = ScreensaverModeConstants.MODE_FROM_TIME_TO_TIME;
            }
        });

        timeIntervalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                uncheckPreviousRadioButton(buttonView);
                mSelectedMode = ScreensaverModeConstants.MODE_TIME_INTERVAL;
            }
        });

        //사용자가 이전에 선택한 옵션 표시

        mSelectedMode = getPreviousSelectedMode();


        switch (mSelectedMode) {
            case "0":
                alwayOffSwitch.setChecked(true);
                break;
            case "1":
                alwayOnSwitch.setChecked(true);
                break;
            case "2":
                fromTimeToTimeSwitch.setChecked(true);
                break;
            case "3":
                timeIntervalSwitch.setChecked(true);
                break;
        }
    }

    private String getPreviousSelectedMode() {


        return ContentProviderManager.getPreviousSelectedMode(mContext);
    }

    /**
     * 이전에 선택한 라디오버튼 체크 해제
     *
     * @param radioButton
     */
    private void uncheckPreviousRadioButton(CompoundButton radioButton) {
        if (mPreviousRadioButton != null) {
            mPreviousRadioButton.setChecked(false);
        }

        mPreviousRadioButton = radioButton;
    }


    /**
     * 스피너 초기화
     *
     * @param context
     */
    private void initSpinners(Context context) {
        final List<String> timeDatas = getTimeDatas();
        //시작 시간 스피너
        Spinner fromTimeSpinner = (Spinner) findViewById(R.id.fromTimeSpinner);

        SpinnerAdapter fromTimeSpinnerAdapter = new SpinnerAdapter(context, R.layout.spinner_header, timeDatas);
        fromTimeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);


        fromTimeSpinner.setAdapter(fromTimeSpinnerAdapter);
        fromTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedStartTime = timeDatas.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //종료 시간 스피너
        Spinner toTimeSpinner = (Spinner) findViewById(R.id.toTimeSpinner);

        SpinnerAdapter toTimeSpinnerAdapter = new SpinnerAdapter(context, R.layout.spinner_header, timeDatas);
        toTimeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);


        toTimeSpinner.setAdapter(toTimeSpinnerAdapter);
        toTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedFinishTime = timeDatas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final List<String> intervalDatas = getIntervalDatas();
        //주기적 스피너
        Spinner timeIntervalSpinner = (Spinner) findViewById(R.id.timeIntervalSpinner);

        SpinnerAdapter timeIntervalSpinnerAdapter = new SpinnerAdapter(context, R.layout.spinner_header, intervalDatas);
        timeIntervalSpinnerAdapter.setDropDownViewResource(R.layout.spinner_item);


        timeIntervalSpinner.setAdapter(timeIntervalSpinnerAdapter);
        timeIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSelectedTimeInterval = intervalDatas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 주기 데이터 가져옴
     *
     * @return
     */
    private List<String> getIntervalDatas() {
        List<String> datas = new ArrayList<>();
        datas.add("1 분");
        datas.add("2 분");
        datas.add("5 분");
        datas.add("10 분");
        datas.add("20 분");
        datas.add("30 분");
        datas.add("40 분");
        datas.add("50 분");
        return datas;
    }

    /**
     * 시간 데이터 가져옴
     *
     * @return
     */
    private List<String> getTimeDatas() {

        List<String> datas = new ArrayList<>();
        datas.add("00:00");
        datas.add("01:00");
        datas.add("02:00");
        datas.add("03:00");
        datas.add("04:00");
        datas.add("05:00");
        datas.add("06:00");
        datas.add("07:00");
        datas.add("08:00");
        datas.add("09:00");
        datas.add("10:00");
        datas.add("11:00");
        datas.add("12:00");
        datas.add("13:00");
        datas.add("14:00");
        datas.add("15:00");
        datas.add("16:00");
        datas.add("17:00");
        datas.add("18:00");
        datas.add("19:00");
        datas.add("20:00");
        datas.add("21:00");
        datas.add("22:00");
        datas.add("23:00");
        datas.add("24:00");
        return datas;
    }


    private void addButtonListener() {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
                deleteScreensaverTime();
                setScreensaverTime();
                mListener.onScreensaverTimeChanged();
            }
        });


    }

    private void deleteScreensaverTime() {
        ContentProviderManager.deletePreviousScreenSaverTime(mContext);
    }

    /**
     * 스크린세이버 시간 ContentProvider에 저장
     */
    private void setScreensaverTime() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_TIME_TYPE, mSelectedMode);
        contentValues.put(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_START_TIME, mSelectedStartTime);
        contentValues.put(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_FINISH_TIME, mSelectedFinishTime);
        contentValues.put(ContentProviderConstants.ScreenSaverEntry.COLUMN_NAME_INTERVAL_TIME, mSelectedTimeInterval);
        ContentProviderManager.saveScreenSaverTime(mContext, contentValues);
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public ScreensaverTimeSelectPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
