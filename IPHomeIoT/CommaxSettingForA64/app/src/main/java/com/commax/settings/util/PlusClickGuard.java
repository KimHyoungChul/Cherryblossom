package com.commax.settings.util;

import android.os.Handler;
import android.view.View;

/**
 * 중복 클릭 방지 유틸
 * Created by OWNER on 2016-11-28.
 */

public class PlusClickGuard {
    public static void doIt(final View v) {
        v.setEnabled(false);
        Handler h = new Handler();
        h.postDelayed(new Runnable() {

            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 1000);
    }
}
