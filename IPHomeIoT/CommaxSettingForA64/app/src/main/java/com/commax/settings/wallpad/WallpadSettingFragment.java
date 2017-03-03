package com.commax.settings.wallpad;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.setting_provider.CenterPhoneNumber;
import com.commax.settings.setting_provider.DeviceServerIp;
import com.commax.settings.setting_provider.DongHo;
import com.commax.settings.setting_provider.GuardPhoneNumber;
import com.commax.settings.setting_provider.LocalServerIp;
import com.commax.settings.setting_provider.UpdateServerIp;
import com.commax.settings.util.PlusClickGuard;

/**
 * 월패드 설정 화면
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

//public class WallpadSettingFragment extends Fragment implements DonghoSetListener, WallpadPasswordChangeListener, DeviceServerIpSetListener, UpdateServerIpSetListener, LocalServerIpSetListener {
public class WallpadSettingFragment extends Fragment implements DonghoSetListener, WallpadPasswordChangeListener, InputServerIpSetListener,InputPhoneNumberSetListener {

    public WallpadSettingFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallpad_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDonghoValue();
        setGuardPhoneValue();
        setCenterPhoneValue();
        setDeviceServerIpValue();
        setUpdateServerIpValue();
        setLocalServerIpValue();
        addMenuItemListener();
    }

    //원래 코드
//    private void setDonghoValue() {
//
//        Dongho dongho = getDongho();
//
//
//        TextView dongHoValue = (TextView) getActivity().findViewById(R.id.dongHoValue);
//
//        if(dongho == null || dongHoValue == null) {
//            dongHoValue.setText(R.string.not_set);
//            return;
//        }
//
//        String dong = dongho.getDong();
//        String ho = dongho.getHo();
//
//
//
//        dongHoValue.setText(dong + "-" + ho);
//
//    }
//    //원래 코드
//    private Dongho getDongho() {
//        return ContentProviderManager.getDongho(getActivity());
//    }

    /**
     * 기존의 setting content provider 사용
     */
    private void setDeviceServerIpValue() {

        if(TypeDef.DISPLAY_DEVICE_SERVERIP_ENABLE) {
            String deviceServerIp = getDeviceServerIp();

            TextView deviceServerIpValue = (TextView) getActivity().findViewById(R.id.deviceServerIpValue);

            if (deviceServerIp == null) {
                deviceServerIpValue.setText(R.string.not_set);
                return;
            }

            deviceServerIpValue.setText(deviceServerIp);
        } else {
            RelativeLayout item_deviceServerIp = (RelativeLayout) getActivity().findViewById(R.id.deviceServerIp);
            item_deviceServerIp.removeAllViews();
            item_deviceServerIp.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.serverip_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }
    /**
     * 기존의 setting content provider 사용
     */
    private String getDeviceServerIp() {


        return new DeviceServerIp(getActivity()).getValue();

    }


    /**
     * 기존의 setting content provider 사용
     */
    private void setUpdateServerIpValue() {

        if(TypeDef.DISPLAY_UPDATE_SERVERIP_ENABLE) {
            String updateServerIp = getUpdateServerIp();

            TextView updateServerIpValue = (TextView) getActivity().findViewById(R.id.updateServerIpValue);

            if (updateServerIp == null) {
                updateServerIpValue.setText(R.string.not_set);
                return;
            }

            updateServerIpValue.setText(updateServerIp);
        } else {
            RelativeLayout item_updateServerIp = (RelativeLayout) getActivity().findViewById(R.id.updateServerIp);
            item_updateServerIp.removeAllViews();
            item_updateServerIp.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.updateip_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }
    /**
     * 기존의 setting content provider 사용
     */
    private String getUpdateServerIp() {


        return new UpdateServerIp(getActivity()).getValue();

    }

    /**
     * 기존의 setting content provider 사용
     */
    private void setLocalServerIpValue() {

        if(TypeDef.DISPLAY_LOCAL_SERVERIP_ENABLE) {
            String localServerIp = getLocalServerIp();

            TextView localServerIpValue = (TextView) getActivity().findViewById(R.id.localServerIpValue);

            if (localServerIp == null) {
                localServerIpValue.setText(R.string.not_set);
                return;
            }

            localServerIpValue.setText(localServerIp);
        } else {
            RelativeLayout item_localServerIp = (RelativeLayout) getActivity().findViewById(R.id.localServerIp);
            item_localServerIp.removeAllViews();
            item_localServerIp.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.localip_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }
    /**
     * 기존의 setting content provider 사용
     */
    private String getLocalServerIp() {


        return new LocalServerIp(getActivity()).getValue();

    }

    /**
     * 기존의 setting content provider 사용
     * getDongho();가 null이 반환됨
     */
    private void setDonghoValue() {

        if(TypeDef.DISPLAY_DONGHO_ENABLE) {

            String dongho = getDongho();
            TextView dongHoValue = (TextView) getActivity().findViewById(R.id.dongHoValue);

            if (dongho == null) {
                dongHoValue.setText(R.string.not_set);
                return;
            }
            dongHoValue.setText(dongho);
        } else {
            RelativeLayout item_dongho = (RelativeLayout) getActivity().findViewById(R.id.dongHo);
            item_dongho.removeAllViews();
            item_dongho.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.dongho_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }

    /**
     * 기존의 setting content provider 사용
     */
    private String getDongho() {

        return new DongHo(getActivity()).getValue();

    }

    /**
     * 기존의 setting content provider 사용
     */
    private void setGuardPhoneValue() {

        if(TypeDef.DISPLAY_GUARD_ENABLE) {

            String value = getGuardPhone();
            TextView viewValue = (TextView) getActivity().findViewById(R.id.guardPhoneNumberValue);

            if (value == null) {
                viewValue.setText(R.string.not_set);
                return;
            }
            viewValue.setText(value);
        } else {
            RelativeLayout item_view = (RelativeLayout) getActivity().findViewById(R.id.guardPhoneNumber);
            item_view.removeAllViews();
            item_view.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.guardPhoneNumber_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }

    /**
     * 기존의 setting content provider 사용
     */
    private String getGuardPhone() {

        return new GuardPhoneNumber(getActivity()).getValue();

    }

    /**
     * 기존의 setting content provider 사용
     */
    private void setCenterPhoneValue() {

        if(TypeDef.DISPLAY_CENTER_ENABLE) {

            String value = getCenterPhone();
            TextView viewValue = (TextView) getActivity().findViewById(R.id.centerPhoneNumberValue);

            if (value == null) {
                viewValue.setText(R.string.not_set);
                return;
            }
            viewValue.setText(value);
        } else {
            RelativeLayout item_view = (RelativeLayout) getActivity().findViewById(R.id.centerPhoneNumber);
            item_view.removeAllViews();
            item_view.setVisibility(View.GONE);

            View divider_bar = (View) getActivity().findViewById(R.id.centerPhoneNumber_divider_bar);
            divider_bar.setVisibility(View.GONE);
        }

    }

    /**
     * 기존의 setting content provider 사용
     */
    private String getCenterPhone() {

        return new CenterPhoneNumber(getActivity()).getValue();

    }


    /**
     * 메뉴 항목 클릭 리스너 추가
     */
    private void addMenuItemListener() {
        //비밀번호 변경
        RelativeLayout wallpadPassword = (RelativeLayout) getActivity().findViewById(R.id.wallpadPassword);
        wallpadPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                showWallpadPasswordPopup();
            }
        });

        //동호 입력
        if(TypeDef.DISPLAY_DONGHO_ENABLE) {
            RelativeLayout dongHo = (RelativeLayout) getActivity().findViewById(R.id.dongHo);
            dongHo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showDonghoPopup();
                }
            });
        }

        //경비실
        if(TypeDef.DISPLAY_GUARD_ENABLE) {
            RelativeLayout guardPhone = (RelativeLayout) getActivity().findViewById(R.id.guardPhoneNumber);
            guardPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showGuardNumberPopup();
                }
            });
        }

        //관리실
        if(TypeDef.DISPLAY_CENTER_ENABLE) {
            RelativeLayout centerPhone = (RelativeLayout) getActivity().findViewById(R.id.centerPhoneNumber);
            centerPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showCenterNumberPopup();
                }
            });
        }

        //디바이스 서버 IP 입력
        if(TypeDef.DISPLAY_DEVICE_SERVERIP_ENABLE) {
            RelativeLayout deviceServerIp = (RelativeLayout) getActivity().findViewById(R.id.deviceServerIp);
            deviceServerIp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showDeviceServerIpPopup();
                }
            });
        }

        //업데이트 서버 IP 입력
        if(TypeDef.DISPLAY_UPDATE_SERVERIP_ENABLE) {
            RelativeLayout updateServerIp = (RelativeLayout) getActivity().findViewById(R.id.updateServerIp);
            updateServerIp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showUpdateServerIpPopup();
                }
            });
        }

        //단지 서버 IP 입력
        if(TypeDef.DISPLAY_LOCAL_SERVERIP_ENABLE) {
            RelativeLayout localServerIp = (RelativeLayout) getActivity().findViewById(R.id.localServerIp);
            localServerIp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    showLocalServerIpPopup();
                }
            });
        }

    }

    /**
     * 디바이스 서버 IP 팝업창 표시
     */
    private void showDeviceServerIpPopup() {
        //DeviceServerIpPopup popup = new DeviceServerIpPopup(getActivity(), this);
        InputServerIpPopup popup = new InputServerIpPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.deviceServerIpInpuTitle));
        popup.setIpType(popup.DEVICE_IP_MODE);
        popup.show();
    }

    /**
     * 업데이트 서버 IP 팝업창 표시
     */
    private void showUpdateServerIpPopup() {
        //UpdateServerIpPopup popup = new UpdateServerIpPopup(getActivity(), this);
        InputServerIpPopup popup = new InputServerIpPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.updateServerIpInpuTitle));
        popup.setIpType(popup.UPDATE_IP_MODE);
        popup.show();
    }

    /**
     * 단지 서버 IP 팝업창 표시
     */
    private void showLocalServerIpPopup() {
        //LocalServerIpPopup popup = new LocalServerIpPopup(getActivity(), this);
        InputServerIpPopup popup = new InputServerIpPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.localServerIpInpuTitle));
        popup.setIpType(popup.LOCAL_IP_MODE);
        popup.show();
    }

    /**
     * 동호 입력 팝업창 표시
     */
    private void showDonghoPopup() {
        DonghoPopup popup = new DonghoPopup(getActivity(), this);
        popup.show();
    }

    /**
     * 비밀번호 변경 팝업창 표시
     */
    private void showWallpadPasswordPopup() {
        ChangePasswordPopup popup = new ChangePasswordPopup(getActivity(), this);
        popup.show();
    }


    /**
     * 경비실 번호 팝업창 표시
     */
    private void showGuardNumberPopup() {
        InputPhoneNumberPopup popup = new InputPhoneNumberPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.guardPhoneNumber));
        popup.setPhoneNumberType(popup.GUARD_PHONE_MODE);
        popup.show();
    }

    /**
     * 관리실 번호 팝업창 표시
     */
    private void showCenterNumberPopup() {
        InputPhoneNumberPopup popup = new InputPhoneNumberPopup(getActivity(), this);
        popup.setPopupTitle(getString(R.string.managementPhoneNumber));
        popup.setPhoneNumberType(popup.CENTER_PHONE_MODE);
        popup.show();
    }

    @Override
    public void onDonghoSet() {
        setDonghoValue();

        //기존의 setting provider를 사용하는 경우 DongHo.java 클래스 내에서 브로드캐스팅을 하므로 필요없음
        //sendDonghoBroadcast();

    }

//    /**
//     * 동호 브로드캐스트 전송
//     */
//    private void sendDonghoBroadcast() {
//        Dongho dongho = getDongho();
//
//        Intent intent = new Intent(CommaxConstants.BROADCAST_DONGHO);
//        intent.putExtra(CommaxConstants.KEY_DONG, dongho.getDong());
//        intent.putExtra(CommaxConstants.KEY_HO, dongho.getHo());
//        getActivity().sendBroadcast(intent);
//    }

    @Override
    public void onWallpadPasswordChanged() {
        sendWallpadPasswordChangeBroadcast();
    }

    /**
     * 월패드 비밀번호 변경 브로드캐스트 전송
     */
    private void sendWallpadPasswordChangeBroadcast() {
        //새로운 content provider 이용
        //String password = ContentProviderManager.getWallpadPassword(getActivity());

        //기존의 setting provider 이용
        String password = new PasswordHandler(getActivity()).getValue();

        Intent intent = new Intent(CommaxConstants.BROADCAST_WALLPAD_PASSWORD);
        intent.putExtra(CommaxConstants.KEY_WALLPAD_PASSWORD, password);
        getActivity().sendBroadcast(intent);

    }

    @Override
    public void onDeviceServerIpSet() {
        setDeviceServerIpValue();
    }

    @Override
    public void onUpdateServerIpSet() {
        setUpdateServerIpValue();
    }

    @Override
    public void onLocalServerIpSet() {
        setLocalServerIpValue();
    }

    @Override
    public void onGuardPhoneNumberSet() {
        setGuardPhoneValue();
    }

    @Override
    public void onCenterPhoneNumberSet() {
        setCenterPhoneValue();
    }

}
