package com.commax.updatemanager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.commax.updatemanager.GetAPPList_Download.GetAppList;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by OWNER on 2016-04-22.
 */

public class JSONHelper {
    public String TAG = JSONHelper.class.getSimpleName();
//    GetAppList getAppList = new GetAppList();

    private static JSONHelper instance;
    public JSONHelper()
    {

    }

    public static synchronized JSONHelper getInstance(){
        if(instance == null)
        {
            instance = new JSONHelper();
        }
        return instance;
    }
    //for Server array count number
    public int list_cnt;
    public int TIMEOUT_VALUE = 20000;   // 5초
    public String[] getPackageName;
    public String[] getVersionName;
    public String[] getAppName;

    public void restCall(String LocalIp, String cloud_svr, String type , Handler mHandler , Context context) throws IOException, IllegalArgumentException
    {
        URL url;
        instance = this;

        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type.isEmpty()) {
            throw new IllegalArgumentException();
        }
        HttpURLConnection urlConnection = null;

        try {
            if (type.equals("update")) {
                Log.d(TAG, "Update List");
               //박도욱 주임님 서버 url
                url = new URL("http://"+ cloud_svr +"/public/applicationinfo.php?method=getApplication");

                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException();
                }
                try {
                    urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                    urlConnection.setReadTimeout(TIMEOUT_VALUE);
                }catch (Exception e){
                    e.printStackTrace();
                }
                GetAppList.getInstance().get_app_list(urlConnection, mHandler , context);
            }
            else
            {
                url = new URL("");
            }
            Log.d(TAG, String.valueOf(url));
        } catch (MalformedURLException e) {
            Log.e(TAG,"url error");
            throw new IOException();
        }
    }
}
