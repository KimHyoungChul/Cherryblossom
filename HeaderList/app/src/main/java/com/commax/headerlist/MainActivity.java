package com.commax.headerlist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    BackgroundService backgroundService = new BackgroundService();
    JSONHelper jsonHelper = new JSONHelper();
    AboutFile aboutFile = new AboutFile();
    VersionCompare versionCompare = new VersionCompare();

    //activity 속성을 다른곳에서 사용가능하도록 함
    private static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }
    private Context context;
    //download progress dialog
    ProgressDialog progressDialog;
    private View mLoadingContainer;
    //application list view
    private ListView mListView = null;
    public ListAdapter3 mAdapter = null;
    //download directory in device
    private String downloaddir = Environment.getExternalStorageDirectory().getPath();
    //CMX manager app update dialog
    public boolean mDialog = true;
    ImageButton ip_setting;
    //preferences ip value
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    //for toast message to inform about error ip setting
    final int SEND_ERROR_IP = 1;
    SendMassgeHandler mMainHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        startService(new Intent(this, BackgroundService.class)); //background service start
        Log.d(TAG,"start Background Service");
        instance = this; //Activity 속성 다른곳에서 사용하도록
        initData();
        String kim = aboutFile.readFile("jiyoung");
        Log.d(TAG,"kim :"  + kim);
        try {
            aboutFile.makefile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void initData()
    {
        setting = getSharedPreferences("setting",0);
        editor = setting.edit();
        //초기ip 고정값 10.14.0.1
        if(TextUtils.isEmpty(setting.getString("ServerIP",""))){
            editor.putString("ServerIP","220.120.109.31");
            editor.commit();
            BackgroundService.CloudServerIP = "220.120.109.31";
            Log.d(TAG, "CloudeServerIP 1 :" + BackgroundService.CloudServerIP);
        }
        else {
            BackgroundService.CloudServerIP = setting.getString("ServerIP","");
            Log.e(TAG, "CloudeServerIP 2 = " + BackgroundService.CloudServerIP);
        }
        mLoadingContainer = findViewById(R.id.loading_container);
        mListView = (ListView) findViewById(R.id.app_list);
        ip_setting = (ImageButton) findViewById(R.id.ip_settingBtn);
        // delete directory
        try {
            DeleteDir(downloaddir + "/CMXdownload");
            Log.d(TAG, downloaddir + "delete download dirctroy");
        } catch (Exception e) {
            Log.e(TAG, "no exist download directory");
        }
        mAdapter = new ListAdapter3(this);
        mListView.setAdapter(mAdapter);
        Log.d(TAG, "mListView = set Adapter");
    // loading progress dialog
        progressDialog = new ProgressDialog(this);
    //handler for error information about ip server setting
        mMainHandler = new SendMassgeHandler();
    }
    //download directory delete
    void DeleteDir(String path)
    {
        File file = new File(path);
        File[] childFileList = file.listFiles();
        for (File childFile : childFileList)
        {
            if(childFile.isDirectory()){
                DeleteDir(childFile.getAbsolutePath());
                Log.d(TAG, "Delete Directory()");
            }
            else{
                childFile.delete();
            }
        }
        file.delete();
    }
    //ip setting dialog
    public void onIPsetting(View v)
    {
        All_dialg.ip_setting();
    }
    // for ip error message Handler 클래스
    class SendMassgeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_ERROR_IP:
                   Toast.makeText(MainActivity.getInstance(), "WIFI 연결 또는 서버 IP를 다시 확인하여주세요" ,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startTask();
        Log.d(TAG,"startTask");
    }
    //for use not to show up update dialog
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "=== onConfigurationChanged is called !!! ===");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // 슬라이드 닫힐때
            Log.i(TAG, "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 슬라이드 열릴때
            Log.i(TAG, "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
        try
        {
            int count =0;
            for(int i = 0; i < JSONHelper.list_cnt ; i++)
            {
                String category = versionCompare.Compare(JSONHelper.getPackageName[i], JSONHelper.getVersionName[i] , context);
                if(category.equals(getString(R.string.upgrade)))
                {
                    count++;
                }
            }
            Log.d(TAG ,"onPause count :" + count);
            //TODO test
            backgroundService.IconBadge(count , context);
            mDialog = false;
        }
        catch (Exception e)
        {
            Log.e(TAG, "icon badge error");
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    /**
     * 작업 시작
     */
    void startTask() {
        Log.d(TAG, "startTask");
        Log.d(TAG, "AppTask exe");
        new AppTask().execute();
    }
    /**로딩뷰 표시 설정
     @param isView
     표시 유무
     */
    public void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            Log.d(TAG, "Loading View display");
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            Log.d(TAG, "List View display");
            mAdapter.notifyDataSetChanged();
            mListView.setVisibility(View.VISIBLE);
            mLoadingContainer.setVisibility(View.GONE);
        }
    }
    /**
     * 작업 태스크
     * @author nohhs
     */
    private class AppTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            Log.d(TAG, "AppTask : onPreExecute");
            setLoadingView(true);
            mAdapter.notifyDataSetChanged();
            Log.d(TAG, "AppTask : notifyDataSetChanged exe");
        }
        @Override
        protected Void doInBackground(Void... params) {
            // 어플리스트 작업시작
            try {
                Log.d(TAG, "backgroundService.CloudServerIP" + BackgroundService.CloudServerIP);
                jsonHelper.restCall("127.0.0.1", BackgroundService.CloudServerIP ,BackgroundService.type, BackgroundService.param1);
                Log.d(TAG,"JSONHelper.restCall()");
                /*
                //TODO test // before_badge_count initialize 처음에 무조건적으로 노티가 되기때문에 초기화 값 넣어줌
                for(int i = 0; i < JSONHelper.list_cnt ; i++)
                {
                    String category = VersionCompare.Compare(JSONHelper.getPackageName[i], JSONHelper.getVersionName[i] , context);
                    if(category.equals(getString(R.string.upgrade)))
                    {
                        BackgroundService.before_badge_count++;
                    }
                }

//                BackgroundService.before_badge_count = backgroundService.BadgeCount();
                Log.d(TAG, "before_badge_count : " + BackgroundService.before_badge_count);
                */
            } catch (Exception e) {
                Log.e(TAG,"restCall error");
                JSONHelper.list_cnt = 0;
                e.printStackTrace();
            }
            Log.d(TAG, "AppTask : doInBackground");
            mAdapter.rebuild();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            Log.d(TAG, "AppTask : onPostExecute");
            // 로딩뷰 정지
            setLoadingView(false);
            Log.d(TAG, "AppTask : setLodingView finish Loading stop");
        }
    };
}
