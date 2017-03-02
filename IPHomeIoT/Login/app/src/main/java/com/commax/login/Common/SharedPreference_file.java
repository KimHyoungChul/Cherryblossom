package com.commax.login.Common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by OWNER on 2017-01-16.
 */
public class SharedPreference_file {
    /* for Test
    * 회원 계정을 preference로 관리하면 App을 업데이트 진행시에 계정 정보 사라지게 되어 올바른 사용이 되지 않는다. 사용하지 않음
    * */

    public SharedPreferences account;
    public SharedPreferences.Editor editor;

    Context mContext;

    //singleton pattern
    private static SharedPreference_file instance;
    public static SharedPreference_file getInstance() {
        return instance;
    }


    public SharedPreference_file(Context context)
    {
        mContext = context;
        init();
    }


    public void init()
    {
        //TODO preference
        account = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = account.edit();
        instance = this;
    }

    public  void read_all_preference()
    {
        //키값없이 모든 저장값 가져오기

        Collection<?> col =  account.getAll().values();
        Iterator<?> it = col.iterator();

        while(it.hasNext())
        {
            String msg = (String)it.next();
            Log.d("Result", msg);
        }
    }
}
