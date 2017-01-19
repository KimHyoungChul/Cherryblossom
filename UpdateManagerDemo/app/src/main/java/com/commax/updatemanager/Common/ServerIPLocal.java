package com.commax.updatemanager.Common;

import android.content.Context;

/**
 * Created by OWNER on 2016-11-28.
 */
public class ServerIPLocal {

    public static final int PROVIDER_INDEX = 1;
    private static final String GET_QUERY="CALL db_house.proc_ens_info()";
    private static final String SET_QUERY = "CALL db_house.proc_ens_update(\"";
    private static final String BROADCAST_ACTION = "commax.action.updated.LOCAL_SERVER_IP";

    private String mProviderValue;
    private String mMySqlValue;
    Context context;

    public ServerIPLocal(Context context) {
        this.context=context;

    }

    public String getValue() {

        ProviderSettings ps = new ProviderSettings(context);
        return ps.getValue(PROVIDER_INDEX);

    }

}
