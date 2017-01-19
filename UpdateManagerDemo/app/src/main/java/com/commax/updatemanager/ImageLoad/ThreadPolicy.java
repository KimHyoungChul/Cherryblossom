package com.commax.updatemanager.ImageLoad;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by OWNER on 2016-05-31.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ThreadPolicy {
    public ThreadPolicy() {

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }
}
