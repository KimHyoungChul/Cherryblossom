package com.commax.settings.ringtone;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.util.PlusClickGuard;

import java.util.List;

/**
 * 벨소리 설정화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class RingtoneFragment extends Fragment implements CommaxRingtoneSelectListener {


    public RingtoneFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ringtone, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addMenuItemListener();

        //디폴트 현관 벨소리명 표시
        showDefaultEntranceRingtoneName();

        if(TypeDef.DISPLAY_EXT_RINGTONE_ENABLE) { //2017-01-13,yslee::경비실,로비등 외부벨소리 설정 숨김
            //디폴트 내선 벨소리명 표시
            //showDefaultExtensionRingtoneName();
            //디폴트 경비실 벨소리명 표시
            showDefaultGuardHouseRingtoneName();
            //디폴트 일반전화 벨소리명 표시
            //showDefaultPstnRingtoneName();
            //디폴트 공동구역 벨소리명 표시
            showDefaultPublicAreaRingtoneName();
        }
    }

    /**
     * 디폴트 공동구역 벨소리명 표시
     */
    private void showDefaultPublicAreaRingtoneName() {
        TextView publicAreaRingtoneName = (TextView) getActivity().findViewById(R.id.publicAreaRingtoneName);
        String name = getDefaultPublicAreaRingtoneName();
        publicAreaRingtoneName.setText(name);
    }

    /**
     * 디폴트 공동구역 벨소리명 가져옴
     *
     * @return
     */
    private String getDefaultPublicAreaRingtoneName() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getPublicAreaRingtoneName();
    }

    /**
     * 디폴트 일반전화 벨소리명 표시
     */
    private void showDefaultPstnRingtoneName() {
        TextView pstnRingtoneName = (TextView) getActivity().findViewById(R.id.pstnRingtoneName);
        String name = getDefaultPstnRingtoneName();
        pstnRingtoneName.setText(name);
    }

    /**
     * 디폴트 일반전화 벨소리명 가져옴
     *
     * @return
     */
    private String getDefaultPstnRingtoneName() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getPstnRingtoneName();
    }

    /**
     * 디폴트 경비실 벨소리명 표시
     */
    private void showDefaultGuardHouseRingtoneName() {
        TextView guardHouseRingtoneName = (TextView) getActivity().findViewById(R.id.guardHouseRingtoneName);
        String name = getDefaultGuardHouseRingtoneName();
        guardHouseRingtoneName.setText(name);
    }

    /**
     * 디폴트 경비실 벨소리명 가져옴
     *
     * @return
     */
    private String getDefaultGuardHouseRingtoneName() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getGuardHouseRingtoneName();
    }

    /**
     * 디폴트 내선 벨소리명 표시
     */
    private void showDefaultExtensionRingtoneName() {
        TextView extensionRingtoneName = (TextView) getActivity().findViewById(R.id.extensionRingtoneName);
        String name = getDefaultExtensionRingtoneName();
        extensionRingtoneName.setText(name);
    }

    /**
     * 디폴트 내선 벨소리명 가져옴
     *
     * @return
     */
    private String getDefaultExtensionRingtoneName() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getExtensionRingtoneName();
    }

    /**
     * 디폴트 현관 벨소리명 표시
     */
    private void showDefaultEntranceRingtoneName() {
        TextView entranceRingtoneName = (TextView) getActivity().findViewById(R.id.entranceRingtoneName);
        String name = getDefaultEntranceRingtoneName();
        entranceRingtoneName.setText(name);

    }

    /**
     * 디폴트 현관 벨소리명 가져옴
     *
     * @return
     */
    private String getDefaultEntranceRingtoneName() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getEntranceRingtoneName();
    }

    /**
     * 설정 항목 클릭 리스너 추가
     */
    private void addMenuItemListener() {
        //현관 벨소리 설정
        RelativeLayout entranceRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.entranceRingtoneSetting);
        entranceRingtoneSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                showRingtoneSelectPopup(RingtoneConstants.ENTRANCE_RINGTONE, getDefaultEntranceRingtonePath());
            }
        });

//        //내선 벨소리 설정
//        RelativeLayout extensionRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.extensionRingtoneSetting);
//        extensionRingtoneSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                showRingtoneSelectPopup(RingtoneConstants.EXTENSION_RINGTONE, getDefaultExtensionRingtonePath());
//            }
//        });

        //경비실 벨소리 설정
        if(TypeDef.DISPLAY_EXT_RINGTONE_ENABLE){ //2017-01-13,yslee::경비실,로비등 외부벨소리 설정 숨김
            RelativeLayout guardHouseRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.guardHouseRingtoneSetting);
            guardHouseRingtoneSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showRingtoneSelectPopup(RingtoneConstants.GUARDHOUSE_RINGTONE, getDefaultGuardHouseRingtonePath());
                }
            });
        } else {
            RelativeLayout guardHouseRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.guardHouseRingtoneSetting);
            guardHouseRingtoneSetting.removeAllViews();
            guardHouseRingtoneSetting.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.guard_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

//        //일반전화 벨소리 설정
//        RelativeLayout pstnRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.pstnRingtoneSetting);
//        pstnRingtoneSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                showRingtoneSelectPopup(RingtoneConstants.PSTN_RINGTONE, getDefaultPstnRingtonePath());
//            }
//        });

        //공동구역 벨소리 설정
        if(TypeDef.DISPLAY_EXT_RINGTONE_ENABLE) { //2017-01-13,yslee::경비실,로비등 외부벨소리 설정 숨김
            RelativeLayout publicAreaRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.publicAreaRingtoneSetting);
            publicAreaRingtoneSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showRingtoneSelectPopup(RingtoneConstants.PUBLIC_AREA_RINGTONE, getDefaultPublicAreaRingtonePath());
                }
            });
        }else {
            RelativeLayout publicAreaRingtoneSetting = (RelativeLayout) getActivity().findViewById(R.id.publicAreaRingtoneSetting);
            publicAreaRingtoneSetting.removeAllViews();
            publicAreaRingtoneSetting.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.lobby_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }

    /**
     * 디폴트 공동구역 벨소리 path 가져옴
     */
    private String getDefaultPublicAreaRingtonePath() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getPublicAreaRingtoneName();
    }

    /**
     * 디폴트 내선 벨소리 path 가져옴
     */
    private String getDefaultExtensionRingtonePath() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getExtensionRingtoneName();
    }

    /**
     * 디폴트 경비실 벨소리 path 가져옴
     */
    private String getDefaultGuardHouseRingtonePath() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getGuardHouseRingtoneName();
    }

    /**
     * 디폴트 일반전화 벨소리 path 가져옴
     */
    private String getDefaultPstnRingtonePath() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getPstnRingtoneName();
    }

    /**
     * 디폴트 현관문 벨소리 path 가져옴
     */
    private String getDefaultEntranceRingtonePath() {
        return CommaxRingtoneSettingManager.getInstance(getActivity()).getEntranceRingtoneName();
    }

    /**
     * 벨소리 선택 팝업 표시
     *
     * @param soundType
     * @param fileName
     */
    private void showRingtoneSelectPopup(int soundType, String fileName) {

        CommaxRingtoneSelectPopup popup = new CommaxRingtoneSelectPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.title_ringtone_popup));
        popup.setSoundType(soundType);
        popup.setDefaultRingtoneFile(fileName);
        Log.d("ringtone", "ringtone: " + fileName);
        popup.setSoundData(getSoundData());
        popup.initList(getActivity());
        popup.show();

    }


    /**
     * 벨소리 데이터 가져옴
     *
     * @return
     */
    private List<CommaxRingtone> getSoundData() {

        return CommaxRingtoneSettingManager.getInstance(getActivity()).getRingtoneList();


    }


    /**
     * 코맥스 벨소리 선택된 경우 콜백
     *
     * @param soundType
     * @param ringtoneFile
     */
    @Override
    public void onCommaxRingtoneSelected(int soundType, String ringtoneFile) {
        switch (soundType) {
            //현관문
            case RingtoneConstants.ENTRANCE_RINGTONE:
                //현관문 벨소리 설정 저장
                setEntranceRingtone(ringtoneFile);
                //디폴트 현관문 벨소리명 표시
                showDefaultEntranceRingtoneName();

                break;
            //일반전화
            case RingtoneConstants.PSTN_RINGTONE:
                setPstnRingtone(ringtoneFile);
                showDefaultPstnRingtoneName();

                break;
            //경비실
            case RingtoneConstants.GUARDHOUSE_RINGTONE:
                setGuardHouseRingtone(ringtoneFile);
                showDefaultGuardHouseRingtoneName();


                break;
            //내선
            case RingtoneConstants.EXTENSION_RINGTONE:
                setExtensionRingtone(ringtoneFile);
                showDefaultExtensionRingtoneName();

                break;
            //공동구역
            case RingtoneConstants.PUBLIC_AREA_RINGTONE:
                setPublicAreaRingtone(ringtoneFile);
                showDefaultPublicAreaRingtoneName();

                break;
        }

    }

    /**
     * 공동구역 벨소리 설정 저장
     *
     * @param ringtoneFile
     */
    private void setPublicAreaRingtone(String ringtoneFile) {
        CommaxRingtoneSettingManager.getInstance(getActivity()).setPublicAreaRingtone(ringtoneFile);
    }

    /**
     * 내선 벨소리 설정 저장
     *
     * @param ringtoneFile
     */
    private void setExtensionRingtone(String ringtoneFile) {
        CommaxRingtoneSettingManager.getInstance(getActivity()).setExtensionRingtone(ringtoneFile);
    }

    /**
     * 경비실 벨소리 설정 저장
     *
     * @param ringtoneFile
     */
    private void setGuardHouseRingtone(String ringtoneFile) {
        CommaxRingtoneSettingManager.getInstance(getActivity()).setGuardHouseRingtone(ringtoneFile);
    }

    /**
     * 일반전화 벨소리 설정 저장
     *
     * @param ringtoneFile
     */
    private void setPstnRingtone(String ringtoneFile) {
        CommaxRingtoneSettingManager.getInstance(getActivity()).setPstnRingtone(ringtoneFile);

    }

    /**
     * 현관문 벨소리 설정 저장
     *
     * @param ringtoneFile
     */
    private void setEntranceRingtone(String ringtoneFile) {
        CommaxRingtoneSettingManager.getInstance(getActivity()).setEntranceRingtone(ringtoneFile);

    }


}
