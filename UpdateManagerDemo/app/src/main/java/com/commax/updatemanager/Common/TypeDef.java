package com.commax.updatemanager.Common;

import android.app.Application;

import com.commax.updatemanager.MainActivity;
import com.commax.updatemanager.R;

/**
 * Created by OWNER on 2016-12-05.
 */
public class TypeDef extends Application {
    public static final String Upgrade = MainActivity.getInstance().getString(R.string.update);
    public static final String Installation = MainActivity.getInstance().getString(R.string.install);
    public static final String Newest = MainActivity.getInstance().getString(R.string.newest);

}
