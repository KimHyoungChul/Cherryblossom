package com.commax.login.UC;

import android.content.Context;

public class DongHo {

    private static final String ACTION = "commax.action.updated.RESIDENCE_NUMBER";
    private static final String MYSQL_GET = "CALL db_house.proc_dongho_info()";
    private static final String MYSQL_SET = "CALL db_house.proc_dong_ho_update(\"";
    public static final int PROVIDER_INDEX = 9;
    private String mProviderValue = "";
    private String mMySqlValue = "";

    Context context;

    public DongHo(Context context) {
        this.context = context;


    }

    public String getValue() {
        ProviderSettings ps = new ProviderSettings(context);
        return ps.getValue(PROVIDER_INDEX);
    }


}
