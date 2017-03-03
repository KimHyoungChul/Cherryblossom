package com.commax.settings.cctv;

import com.commax.settings.onvif.OnvifDevice;

/**
 * CCTV 아이디 비밀번호 팝업창에서 확인버튼을 누른 경우 리스너
 * Created by OWNER on 2016-12-21.
 */
public interface CctvIdPasswordConfirmListener {
    public void onCctvIdPasswordConfirm(OnvifDevice device);
}
