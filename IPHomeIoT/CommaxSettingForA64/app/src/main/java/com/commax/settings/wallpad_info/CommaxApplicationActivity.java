package com.commax.settings.wallpad_info;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;
import com.commax.settings.util.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 코맥스 앱 설정 화면
 */
public class CommaxApplicationActivity extends CommonActivity {

    List<AppInfo> m_commaxLaunchables = null;
    List<AppInfo> m_systemLaunchables = null;
    List<AppInfo> m_otherLaunchables = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commax_application);

        setFullScreen();
        initAppInfo();
        findapplication();
    }

    /**
     * APP-Discovery 실행
     */
    public class ApplicationDiscovery extends AsyncTask<Void, Void, Integer> {

        private static final int MSG_SEND_ID_APPUPDATE = 1000;
        private Handler mhandler;
        private boolean bDiscoveryAppList;

        //private ProgressDialog mProgressDialog;
        private CustomProgressDialog mProgressDialog; //2017-01-18,yslee::CustomProgress로 변경

        public ApplicationDiscovery() {

            bDiscoveryAppList = false;

            //handler로 받아서 처리
            mhandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    if(msg.what == MSG_SEND_ID_APPUPDATE) {

                        //앱리스트 별로 표시
                        makeCommaxAppList();
                        makeSystemAppList();
                        makeOtherAppList();

                        bDiscoveryAppList = true;
                    };
                }
            };
        }

        @Override
        protected void onPreExecute() {
//            mProgressDialog = new ProgressDialog( CommaxApplicationActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
//            mProgressDialog.setCancelable(false);
            //2017-01-18,yslee::CustomProgress로 변경
            mProgressDialog = new CustomProgressDialog(CommaxApplicationActivity.this);
            mProgressDialog.setMessage(getString(R.string.loading_application));
            mProgressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            int result = 0;

            //앱리스트 검색 후 리스트 표시
            scanAppLaunchables();
            mhandler.sendEmptyMessage(MSG_SEND_ID_APPUPDATE);

            while(!bDiscoveryAppList) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                SystemClock.sleep(100);
                mProgressDialog.dismiss();
            }

            super.onPostExecute(result);
        }

    }


    /**
     * 코맥스 앱 정보 데이터 초기화
     */
    private void initAppInfo() {
        //CommaxAppMap.init(this);

        m_commaxLaunchables = new ArrayList<>();
        m_systemLaunchables = new ArrayList<>();
        m_otherLaunchables = new ArrayList<>();

    }

    /**
     * APP 찾기
     */
    private void findapplication() {

        //2017-01-05,yslee::앱리스트 Scan시 프로그래스바 추가
        CommaxApplicationActivity.ApplicationDiscovery discovery = new CommaxApplicationActivity.ApplicationDiscovery();
        discovery.execute();

    }

    /**
     * 앱 정보 Sort
     */
    private void scanAppLaunchables() {

        //if(m_allLaunchables == null) m_allLaunchables = new ArrayList<>();
        if(m_commaxLaunchables == null) m_commaxLaunchables = new ArrayList<>();
        if(m_systemLaunchables == null) m_systemLaunchables = new ArrayList<>();
        if(m_otherLaunchables == null) m_otherLaunchables = new ArrayList<>();

        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        for(PackageInfo pi : list) {
            ApplicationInfo ai = null;
            Drawable icon = null;
            String getpackageName = null;
            String applicationName = null;
            String version = null;

            try {
                ai = pm.getApplicationInfo(pi.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            getpackageName = pi.packageName.toLowerCase();
            version = pi.versionName;
            try {
                icon = pm.getApplicationIcon(pi.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            AppInfo appInfo = new AppInfo();
            appInfo.setLabel(applicationName);
            appInfo.setActivityName("");
            appInfo.setPackageName(pi.packageName);
            appInfo.setIcon(icon);
            appInfo.setVersion(version);

            //앱 정보 Sort
            if (getpackageName.contains("commax")) {
                m_commaxLaunchables.add(appInfo);
            } else if (getpackageName.contains("android")) {
                m_systemLaunchables.add(appInfo);
            } else {
                m_otherLaunchables.add(appInfo);
            }

        }
    }


    private void makeCommaxAppList() {

        PackageManager pm = getPackageManager();

        //2017-01-06,yslee::앱리스트 앱 검색루틴 및 표시방법 변경
        //List<AppInfo> launchables = getCommaxLaunchables();
        //List<AppInfo> launchables = getCommaxLaunchables2();
        List<AppInfo> launchables = m_commaxLaunchables;

        //리스트 타이틀
        TextView listTitle = (TextView) findViewById(R.id.commaxAppListTitle);
        String title = getString(R.string.commax) + "(" + launchables.size() + ")";
        listTitle.setText(title);
        listTitle.setVisibility(View.VISIBLE);

//        listTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                ListView commaxappList = (ListView) findViewById(R.id.commaxAppList);
//                ((AppAdapter) commaxappList.getAdapter()).setDynamicHeight(commaxappList);
//            }
//        });

        //리스트
        final AppAdapter adapter = new AppAdapter(this, pm, launchables);
        ListView appList = (ListView) findViewById(R.id.commaxAppList);
        appList.setVisibility(View.VISIBLE);
        appList.setAdapter(adapter);
        adapter.setFullExtendHeight(appList); //2017-01-05,yslee::페이지 단위 앱리스트로 수정

        //앱 실행
//        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PlusClickGuard.doIt(view);
//
//                AppInfo appInfo = adapter.getItem(position);
//
//                ComponentName name = new ComponentName(
//                        appInfo.getPackageName(), appInfo.getActivityName());
//                Intent i = new Intent();
//
//                // i.addCategory(Intent.CATEGORY_LAUNCHER);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                i.setComponent(name);
//
//                startActivity(i);
//            }
//        });

/*
        //앱 실행
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlusClickGuard.doIt(view);

                AppInfo appInfo = adapter.getItem(position);
                Intent startIntent = getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());

                if(startIntent != null){
                    startActivity(startIntent);
                }
            }
        });
*/

    }

/*
    private List<AppInfo> getCommaxLaunchables() {
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);

        main.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);
        List<AppInfo> commaxLaunchables = new ArrayList<>();

        //코맥스 앱만 필터링
        for (ResolveInfo resolveInfo : launchables) {
            //if (CommaxAppMap.containsActivity(resolveInfo.activityInfo.name)) {
            String getpackageName = resolveInfo.activityInfo.name.toLowerCase();
            //Log.d("1","--->" + resolveInfo.activityInfo.name);
            //if (resolveInfo.activityInfo.name.contains("commax")) {
            if (getpackageName.contains("commax")) {
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel((String) resolveInfo.loadLabel(pm));
                try {
                    appInfo.setVersion(pm.getPackageInfo(resolveInfo.activityInfo.packageName,0).versionName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                appInfo.setPackageName(resolveInfo.activityInfo.packageName);
                appInfo.setActivityName(resolveInfo.activityInfo.name);
                appInfo.setIcon(resolveInfo.loadIcon(pm));
                commaxLaunchables.add(appInfo);
            }
        }

        return commaxLaunchables;
    }

    //2017-01-06,yslee::앱리스트 앱 검색루틴 및 표시방법 변경
    private List<AppInfo> getCommaxLaunchables2() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        List<AppInfo> commaxLaunchables = new ArrayList<>();

        //코맥스 앱만 필터링
        for(PackageInfo pi : list) {
            ApplicationInfo ai = null;
            Drawable icon = null;
            String getpackageName = null;
            String applicationName = null;
            String version = null;

            try {
                ai = pm.getApplicationInfo(pi.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            getpackageName = pi.packageName.toLowerCase();
            if (getpackageName.contains("commax")) {
                version = pi.versionName;
                try {
                    icon = pm.getApplicationIcon(pi.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                AppInfo appInfo = new AppInfo();
                appInfo.setLabel(applicationName);
                appInfo.setActivityName("");
                appInfo.setPackageName(pi.packageName);
                appInfo.setIcon(icon);
                appInfo.setVersion(version);

                commaxLaunchables.add(appInfo);
            }
        }

        return commaxLaunchables;
    }
*/


    private void makeSystemAppList() {

        PackageManager pm = getPackageManager();
        //List<AppInfo> launchables = getSystemLaunchables();
        List<AppInfo> launchables = m_systemLaunchables;

        //리스트 타이틀
        TextView listTitle = (TextView) findViewById(R.id.systemAppListTitle);
        String title = getString(R.string.system) + "(" + launchables.size() + ")";
        listTitle.setText(title);
        listTitle.setVisibility(View.VISIBLE);

//        listTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                ListView systemappList = (ListView) findViewById(R.id.systemAppList);
//                ((AppAdapter) systemappList.getAdapter()).setDynamicHeight(systemappList);
//            }
//        });

        //리스트
        final AppAdapter adapter = new AppAdapter(this, pm, launchables);
        ListView appList = (ListView) findViewById(R.id.systemAppList);
        appList.setAdapter(adapter);
        appList.setVisibility(View.VISIBLE);
        adapter.setFullExtendHeight(appList); //2017-01-05,yslee::페이지 단위 앱리스트로 수정

/*
        //앱 실행
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlusClickGuard.doIt(view);

                AppInfo appInfo = adapter.getItem(position);
                Intent startIntent = getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());

                if(startIntent != null){
                    startActivity(startIntent);
                }
            }
        });
*/

    }

/*
    private List<AppInfo> getSystemLaunchables() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        List<AppInfo> systemLaunchables = new ArrayList<>();

        for(PackageInfo pi : list) {
            ApplicationInfo ai = null;
            Drawable icon = null;
            String applicationName = null;
            String version = null;

            try {
                ai = pm.getApplicationInfo(pi.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            //2017-01-06,yslee::앱리스트 앱 검색루틴 및 표시방법 변경
            version = pi.versionName;
            try {
                icon = pm.getApplicationIcon(pi.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
            //Log.d("2","--->" + pi.packageName);
            AppInfo appInfo = new AppInfo();
            appInfo.setLabel(applicationName);
            appInfo.setActivityName("");
            appInfo.setPackageName(pi.packageName);
            appInfo.setIcon(icon);
            appInfo.setVersion(version);

            systemLaunchables.add(appInfo);

        }
        return systemLaunchables;

    }
*/


    private void makeOtherAppList() {

        PackageManager pm = getPackageManager();
        //List<AppInfo> launchables = getOtherLaunchables();
        List<AppInfo> launchables = m_otherLaunchables;

        //리스트 타이틀
        TextView listTitle = (TextView) findViewById(R.id.otherAppListTitle);
        String title = getString(R.string.others) + "(" + launchables.size() + ")";
        listTitle.setText(title);
        listTitle.setVisibility(View.VISIBLE);

//        listTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                ListView otherappList = (ListView) findViewById(R.id.otherAppList);
//                ((AppAdapter) otherappList.getAdapter()).setDynamicHeight(otherappList);
//            }
//        });

        //리스트
        final AppAdapter adapter = new AppAdapter(this, pm, launchables);
        ListView appList = (ListView) findViewById(R.id.otherAppList);
        appList.setVisibility(View.VISIBLE);
        appList.setAdapter(adapter);
        adapter.setFullExtendHeight(appList); //2017-01-05,yslee::페이지 단위 앱리스트로 수정

/*
        //앱 실행
//        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PlusClickGuard.doIt(view);
//
//                AppInfo appInfo = adapter.getItem(position);
//
//                ComponentName name = new ComponentName(
//                        appInfo.getPackageName(), appInfo.getActivityName());
//                Intent i = new Intent();
//
//                // i.addCategory(Intent.CATEGORY_LAUNCHER);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                i.setComponent(name);
//
//                startActivity(i);
//            }
//        });

        //앱 실행
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlusClickGuard.doIt(view);

                AppInfo appInfo = adapter.getItem(position);
                Intent startIntent = getPackageManager().getLaunchIntentForPackage(appInfo.getPackageName());

                if(startIntent != null){
                    startActivity(startIntent);
                }
            }
        });
*/

    }

/*
    private List<AppInfo> getOtherLaunchables() {

        List<String> commaxAppPackages = getCommaxAppPackages();
        List<String> systemAppPackages = getSystemAppPackages();

        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);

        main.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> launchables = pm.queryIntentActivities(main, 0);
        List<AppInfo> otherLaunchables = new ArrayList<>();

        //코맥스,시스템앱들은 필터링
        for (ResolveInfo resolveInfo : launchables) {
            *//*
            if (CommaxAppMap.containsActivity(resolveInfo.activityInfo.name)) {
                continue;
            }
            *//*

            //2017-01-06,yslee::앱리스트 앱 검색루틴 및 표시방법 변경
            if(commaxAppPackages.contains(resolveInfo.activityInfo.packageName)) {
                continue;
            }

            if(systemAppPackages.contains(resolveInfo.activityInfo.packageName)) {
                continue;
            }

            AppInfo appInfo = new AppInfo();
            appInfo.setLabel((String) resolveInfo.loadLabel(pm));
            try {
                appInfo.setVersion(pm.getPackageInfo(resolveInfo.activityInfo.packageName,0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);
            appInfo.setActivityName(resolveInfo.activityInfo.name);
            appInfo.setIcon(resolveInfo.loadIcon(pm));
            otherLaunchables.add(appInfo);
        }

        return otherLaunchables;
    }
*/
/*

    private List<String> getSystemAppPackages() {
        List<String> packages = new ArrayList<>();

        List<AppInfo> systemLaunchables = getSystemLaunchables();

        for(AppInfo appInfo:systemLaunchables) {
            packages.add(appInfo.getPackageName());
        }

        return packages;
    }
*/


/*
    private List<String> getCommaxAppPackages() {
        List<String> packages = new ArrayList<>();

        List<AppInfo> commaxLaunchables = getSystemLaunchables();

        for(AppInfo appInfo:commaxLaunchables) {
            packages.add(appInfo.getPackageName());
        }

        return packages;
    }
*/

}
