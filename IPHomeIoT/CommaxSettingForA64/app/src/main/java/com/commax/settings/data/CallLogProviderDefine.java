package com.commax.settings.data;

import android.net.Uri;

public class CallLogProviderDefine {
    public static final String AUTHORITY = "com.commax.iphomeiot.call.data.provider.CallLogProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CallDBScheme.TABLENAME);
}