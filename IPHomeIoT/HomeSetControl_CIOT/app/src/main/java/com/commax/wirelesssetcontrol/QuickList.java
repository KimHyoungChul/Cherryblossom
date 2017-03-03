package com.commax.wirelesssetcontrol;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuickList extends LinearLayout {
    private final String SYM_AMPERSAND = "&";
    private Context context;
    private LayoutInflater inflater;
    private LinearLayout layout;
    static public ArrayList<ApplicationInfo> mApplications;
    static public ArrayList<Pair<String, String>> quicks;
    private int addedCount=0;

    public QuickList(Context context) {
        super(context);
        createView(context);
    }

    public QuickList(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    private void createView(Context context){
        this.context = context.getApplicationContext();
        inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.menu_malloc, null, false);
        layout = (LinearLayout) view.findViewById(R.id.quick_container);
        addView(view);
        updateQuickList();
    }

    public void updateQuickList() {
        layout.removeAllViews();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                loadApplications();
                readQuickFile();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                make(quicks);
                if(layout.getChildCount()>0){
                    layout.setVisibility(VISIBLE);
                }else {
                    layout.setVisibility(INVISIBLE);
                }
            }

            @Override
            protected void onPreExecute() {

            }

        }.execute();
    }

    private void make(ArrayList<Pair<String, String>> quick) {
        this.quicks=quick;
        layout.removeAllViews();

        String key;
        key = "1";
        createIcons(key);
    }

    private void createIcons(String key){
        ArrayList<String> entries = getMatched(key);
        setImage(entries);
    }

    private void setImage(ArrayList<String> entries){
        Log.d("entrySize : ", "" + entries.size());

        addedCount=0;

        for (int i=0;i<entries.size();i++){

            if(addedCount<5) {
                String clsName = entries.get(i);
                Log.d("clsName", "i : " + i + " / " + clsName);
                final ApplicationInfo info = AppMatched(clsName);
                Log.d("info", "" + info);
                if (info != null) {
                    QuickItem item = new QuickItem(context);
                    item.setItemImageDrawable(info.icon);
                    item.setItemText((String)info.title);
                    LayoutParams param = new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);
                    item.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                context.startActivity(info.intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    param.gravity = Gravity.LEFT;


                    param.topMargin = (int) getResources().getDimension(R.dimen.quick_margin);
                    param.bottomMargin = (int) getResources().getDimension(R.dimen.quick_margin);

                    layout.addView(item, param);
                    addedCount++;
                }
            }
        }
    }

    private void launchDoor() {
        try {
            Intent intent = new Intent(NameSpace.BROADCAST_DOOR_MONITOR);
            intent.putExtra(NameSpace.KEY_FROM, NameSpace.SET_CONTROL);
            String ip = getIpFromContentProvider();

            if(ip == null) {
                return;
            }

            intent.putExtra(NameSpace.KEY_IP, ip);
            context.sendBroadcast(intent);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Content Provider에서 도어폰 IP가져옴
     *
     * @return
     */
    private String getIpFromContentProvider() {
        //여러 개의 도어폰 카메라가 있는 경우 첫 번째 카메라의 ip 전송
        if(ContentProviderManager.getAllOnvifDoorCamera(context).size() > 0) {
            return ContentProviderManager.getAllOnvifDoorCamera(context).get(0).getIpAddress();
        } else {
            Toast.makeText(context, R.string.register_doorphone_camera_at_setting, Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private ArrayList<String> getMatched(String key) {
        ArrayList<String> ret = new ArrayList<>();
        for (Pair<String, String> quick : quicks) {
            String prefix = quick.first;
            String clsName = quick.second;
            if (prefix.equals(key)) {
                ret.add(clsName);
            }
        }
        return ret;
    }

    private ApplicationInfo AppMatched(String clsName) {
        for (int k = 0; k < mApplications.size(); k++) {
            ApplicationInfo application = mApplications.get(k);
            if (application.name.equals(clsName) == false) {
                continue;
            }
            return application;
        }
        return null;
    }

    private void loadApplications() {
        if (mApplications == null) {
            mApplications = new ArrayList<ApplicationInfo>();
        }
        mApplications.clear();

        PackageManager manager = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(
                mainIntent, 0);

        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            final int count = apps.size();
            ThemeLoader theme = new  ThemeLoader(context);
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                //icon

                Drawable icon = theme.getIcon(info.activityInfo.name, "quick_");
                if (icon==null) {
                    icon = info.activityInfo.loadIcon(manager);
                }

//                icon =resize(icon);

                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name), Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = icon;
                application.name = info.activityInfo.name;
                mApplications.add(application);
            }
        }
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, getResources()
                .getDimensionPixelSize(R.dimen.quick_size), getResources()
                .getDimensionPixelSize(R.dimen.quick_size), false);

        return new BitmapDrawable(getResources(), bitmapResized);
    }

    private void readQuickFile() {

        if (quicks == null) {
            quicks = new ArrayList<>();
        }
        quicks.clear();

        // quick.i �б�
        FileEx io = new FileEx();
        String[] files = null;
        try {
            files = io.readFile(NameSpace.FILE_NAME);
        } catch (FileNotFoundException e) {

            // e.printStackTrace();
        } catch (IOException e) {

            // e.printStackTrace();
        }

        if (files == null) {
            return;
        }

        if(files.length>0) {
            // ���� üũ
            if (files == null) {
                return;
            }
            if ("".equals(files[0])) {
                return;
            }
            if ("-1".equals(files[0])) {
                return;
            }
        }

        // ���� �� ����
        final int prefix = 0;
        final int clsName = 1;

        for (int i = 0; i < files.length; i++) {
            String line = files[i];
            if (line.startsWith("#")) {
                // �ּ� ����
                continue;
            }
            if (line.contains(SYM_AMPERSAND) == false) {
                // �ùٸ� �������� Ȯ��
                continue;
            }
            String arr[] = files[i].split(SYM_AMPERSAND);

            if (quicks.size()<(NameSpace.MAX_QUICK)) {
                quicks.add(new Pair<String, String>(arr[prefix], arr[clsName]));
            }
        }

    }

}
