package com.commax.updatemanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.updatemanager.Common.AboutFile;
import com.commax.updatemanager.Common.ServerIPLocal;
import com.commax.updatemanager.GetAPPList_Download.ListHeaderAdapter;
import com.commax.updatemanager.GetAPPList_Download.StartService;
import com.commax.updatemanager.GetAPPList_Download.VersionCompare;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    BackgroundService backgroundService = new BackgroundService();
    JSONHelper jsonHelper = new JSONHelper();
    AboutFile aboutFile = new AboutFile();
    StartService startService = new StartService();
    VersionCompare versionCompare = new VersionCompare();

    Context mContext;
    public ToastMessageHandler toastHandler = null;

    //Progress dialog
    public ProgressDialog progressDialog;
    private View mLoadingContainer;

    // ListView and adapter
    private ListView mListView = null;
    public ListHeaderAdapter mAdapter = null;

    //app download directory in device (In Device)
    private String downloaddir = Environment.getExternalStorageDirectory().getPath() + "/CMXdownload";

    //activity 속성을 다른곳에서 사용가능하도록 함
    private static MainActivity instance;
    public static MainActivity getInstance(){
        return instance;
    }
    public boolean mDialog = true;
    public static boolean mStop = false;

    int ComparebadgeCount;
    public String LocalserverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.main);

        instance = this;
        mContext = this;

        //Local Server IP
        ServerIPLocal serverIPLocal = new ServerIPLocal(this);
        LocalserverIP = serverIPLocal.getValue();
        if(LocalserverIP.length() <= 0) {
            LocalserverIP = "10.1.0.2";
//            LocalserverIP = "220.120.109.31";
        }
        Log.d(TAG, "serverIP : " +LocalserverIP);

        //background service start
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
        Log.d(TAG,"start Background Service");
    }

    public void initData() {
        //Toast Handler
        toastHandler = new ToastMessageHandler();

        // Data initial
        mLoadingContainer = findViewById(R.id.loading_container);
        mListView = (ListView) findViewById(R.id.listView1);
        // delete download directory
        //기존 다운 로드 받은 파일들이 남아있기 때문에 앱이 실행될때마다 지워준다.
        try {
            aboutFile.DeleteDir(downloaddir);
            Log.d(TAG, downloaddir + "delete download dirctroy");
        } catch (Exception e) {
            Log.e(TAG, "no exist download directory");
        }
        //make directory
        try
        {
            aboutFile.makefile();
            aboutFile.writeFile("makedir","yes");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //List Adapter
        mAdapter = new ListHeaderAdapter(this , LocalserverIP , toastHandler);
        mListView.setAdapter(mAdapter);
        Log.d(TAG, "mListView set Adapter");

        //loading progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void onClick(View view)
    {
        //back button
        if(view.getId() == R.id.back_button)
        {
            Log.d(TAG, " back button clicked ");
            finish();
        }
        if(view.getId() == R.id.start_service)
        {
            startService.start_service(mContext, null);
            backgroundService.ShowNotification(3);
            Log.d(TAG, "start service");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initData();
        startTask();
        Log.d(TAG,"startTask");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG,"onPause");
        try
        {
            //badge count
            // 앱이 끝날때 마지막으로 벳지 카운트 해서 벳지 설정해준다.

            //badge count 갱신
            ComparebadgeCount = backgroundService.BadgeCount();
            aboutFile.writeFile("BadgeCount", String.valueOf(ComparebadgeCount));
            Log.i(TAG, "comparebadgecount : " + ComparebadgeCount);
            backgroundService.IconBadge(ComparebadgeCount);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "badge count error");
        }

        startService.start_service(mContext, null);

    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG,"onStop");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //화면 전환 되는 이벤트를 받는다.
        //CMX매니저 업데이트 다이얼로그 뜰때 다이얼로그가 꺼지지 않게 하기위해 사용된다.
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "=== onConfigurationChanged is called !!! ===");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // 슬라이드 닫힐때
            Log.i(TAG, "=== Configuration.ORIENTATION_PORTRAIT !!! ===");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // 슬라이드 열릴때
            Log.i(TAG, "=== Configuration.ORIENTATION_LANDSCAPE !!! ===");
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mDialog = false;
        Log.d(TAG, "onDestroy");
    }
    /**
     * 작업 시작
     */
    private void startTask() {
        Log.d(TAG, "startTask");
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



    // ID 중복 체크 에러 Toast message 핸들러
    class ToastMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext, String.valueOf(msg.obj), Toast.LENGTH_LONG).show();

                    break;
                case 1:
                    setLoadingView(false);
                default:
                    break;
            }
        }
    }

    /**
     * 작업 태스크
     * @author nohhs
     */
    private class AppTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "AppTask : onPreExecute");
            // 로딩뷰 시작
            setLoadingView(true);
            mAdapter.notifyDataSetChanged();
        }
        @Override
        protected Void doInBackground(String... params) {
            // 어플리스트 작업시작
            try {
                jsonHelper.restCall("127.0.0.1",LocalserverIP,"update", toastHandler , mContext);
            } catch (Exception e) {

                Log.e(TAG,"restCall error . Server can't connect");
                e.printStackTrace();
            }
            Log.d(TAG, "AppTask : doInBackground");
            mAdapter.rebuild();
//            backgroundService.CloudServerIP = params[1];
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            Log.d(TAG, "AppTask : onPostExecute");
            // 로딩뷰 정지
            setLoadingView(false);
            //badge count 갱신
            ComparebadgeCount = backgroundService.BadgeCount();
            aboutFile.writeFile("BadgeCount", String.valueOf(ComparebadgeCount));
            Log.i(TAG, "comparebadgecount : " + ComparebadgeCount);

        }
    };


}
