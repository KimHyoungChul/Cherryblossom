package com.commax.settings.doorphone_onvif;

import com.commax.settings.onvif.OnvifDevice;

/**
 * 카메라 미리보기 선택된 경우 리스너
 * Created by OWNER on 2016-12-15.
 */

public interface OnvifPreviewRunListener {
    public void onrunPreview(OnvifDevice device);
}
