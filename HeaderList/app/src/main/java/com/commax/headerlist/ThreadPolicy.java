package com.commax.headerlist;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

/**
 * Created by OWNER on 2016-06-08.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ThreadPolicy {
    public ThreadPolicy() {

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }
}
