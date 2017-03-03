package com.commax.iphomiot.doorcall.helper;

import android.content.Context;

import com.commax.cmxua.Call;
import com.commax.iphomiot.doorcall.R;
import com.commax.settings.content_provider.ContentProviderManagerEx;

public class CallHelper {
    public static String getCallDisplayName(Context context, Call call) {
        switch (call.getCallSender()) {
            case Door:
                return ContentProviderManagerEx.getDoorCameraName(context, call.m_strIP);
            case Lobby:
                return context.getString(R.string.STR_LOBBY);
            case Guard:
                return context.getString(R.string.STR_GUARD);
            case House:
                return call.m_strName;
        }

        return "";
    }
}
