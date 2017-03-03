package com.commax.ipdoorcamerasetting.util;

import android.os.Handler;
import android.view.View;

/**
 * Created by OWNER on 2016-11-24.
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
