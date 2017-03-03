package com.commax.settings.cctv;

import com.commax.settings.onvif.OnvifDevice;

/**
 * CCTV 삭제 리스너
 * Created by OWNER on 2016-12-16.
 */

public interface DeleteCctvListener {
    public void onDelete(OnvifDevice device);
}
