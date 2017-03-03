package com.commax.applist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by user on 2016-08-19.
 */
public class V2_AppData {

    private static final String FILE_NAME = "/user/app/bin/menu.i";
    private static final String HIDE_FILE_NAME = "/user/app/bin/hide_app.i";
    private static final String SYM_AMPERSAND = "&";
    private static final String DEBUG_TAG = "ViewApps";


    public static ArrayList<V2_AppData> msAppDatas;
    public static ArrayList<V2_AppData> msEtcAppDatas;
    private static ArrayList<Pair<String, String>> hide_apps;



    public String mCategory = "";
    public String mPackageName= "";
    public String mActivityName= "";
    public String mName= "Name";
    public Drawable mIcon= null;


    public static V2_AppData findApp(String packageName, String activityName)    {

        String packageAndActivityName =  packageName + "." + activityName;

        for( V2_AppData appData: msAppDatas){

            String packageAndActivityNameIn =  appData.mPackageName + "." + appData.mActivityName;

            if( packageAndActivityName.equals(packageAndActivityNameIn ) )   {
                return appData;
            }
        }

        return null;
    }


    static boolean reloadEtcAppData(Context context,boolean removeOldEtc )  {

        //remove oldEtc
        boolean bChanged = removeOldEtc;
        while(bChanged) {

            bChanged = false;
            for (V2_AppData v2_appData : msAppDatas) {
                if( v2_appData.mCategory.equals("5")) {
                    msAppDatas.remove(v2_appData);
                    bChanged = true;
                    break;
                }
            }
        }


        for( int i = 0; i < V2_ActivityDictionary.getInstance(context).mApplications.size(); ++i)  {
            String packageAndActivityName = V2_ActivityDictionary.getInstance(context).mApplications.get(i).name;

            if( checkHide( packageAndActivityName) ) {
                continue;
            }


            String activityName =  packageAndActivityName;
            String packageName =  packageAndActivityName;

            int lastIndex = activityName.lastIndexOf(".");

            if(lastIndex > 0 )  {
                packageName =  activityName.substring(0, lastIndex);//, activityName.length() );
                activityName =  activityName.substring( lastIndex+1, activityName.length());//, activityName.length() );
            }


            if( null == findApp(packageName, activityName )) {
                V2_AppData v2_appData =  makeData(context , "5", packageName, activityName );
                msAppDatas.add(v2_appData);
                //추가 하기
            }
        }



        return false;
    }

    static void reloadAppDatas(Context context )  {
       // boolean
        if( msAppDatas == null )
        {
            msAppDatas = new ArrayList<V2_AppData>();
        }else
        {

        }

        msAppDatas.clear();
        readMenuFile(context);

        //findApp()
    }


    static void removeNoTitle()    {
        boolean bChanged = true;

        while(bChanged)      {
            bChanged = false;

            for( int i = 0; i < msAppDatas.size(); ++i ){

                V2_AppData v2_appData= msAppDatas.get(i);
                if( v2_appData.mPackageName.equals(V2_ActivityDictionary.EMPTY_PACKAGE) ) {
                    bChanged = true;
                    msAppDatas.remove(i);
                    break;
                }
            }
        }
    }

    static private V2_AppData makeData(Context context , String category, String packageName, String activityName ){

        String packageAndActivityName =  packageName + "." + activityName;

        V2_AppData appData = new V2_AppData();
        appData.mCategory = category;

        appData.mPackageName = V2_ActivityDictionary.getInstance(context).getPackageName(packageAndActivityName);
        appData.mActivityName = V2_ActivityDictionary.getInstance(context).getPackageActivityName(packageAndActivityName);

        appData.mName = V2_ActivityDictionary.getInstance(context).getTitle( packageAndActivityName);// applicationInfo.name;//packageName;
        appData.mIcon = V2_ActivityDictionary.getInstance(context).getIcon( packageAndActivityName );

        return appData;


    }

    static private void readMenuFile(Context context) {

        readHideFile();

        // menu.i �б�
        FileEx io = new FileEx();
        String[] files = null;
        try {
            files = io.readFile(FILE_NAME);
        } catch (FileNotFoundException e) {

            // e.printStackTrace();
        } catch (IOException e) {

            Toast toast = Toast.makeText(context.getApplicationContext(),
                    "can't Read:" + FILE_NAME + " Please Check File Permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();


            // e.printStackTrace();
        }
        if (files == null) {
            return;
        }

        if(files.length>0) {
            // ���� üũ

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


            //menus.add(new Pair<String, String>(arr[prefix], arr[clsName]));

            V2_AppData appData = new V2_AppData();
            appData.mCategory = arr[prefix];
            //appData.mNameID = "main_app_03";
            //appData.mIconID = "ic_apps_main_app03_n";

           // mIcon

            appData.mPackageName = arr[clsName];

            String activityName =  arr[clsName];
            String packageName =  arr[clsName];

            int lastIndex = activityName.lastIndexOf(".");

            if(lastIndex > 0 )  {
                packageName =  activityName.substring(0, lastIndex);//, activityName.length() );
                activityName =  activityName.substring( lastIndex+1, activityName.length());//, activityName.length() );
            }

            boolean hide = false;
            hide = checkHide( arr[clsName]);

            if (hide) {

                continue;
            }


          	msAppDatas.add(makeData(context,  arr[prefix] , packageName , activityName ));
        }

    }



   // private static final String HIDE_FILE_NAME = "/user/app/bin/hide_app.i";

    private static void readHideFile() {

        try {
            if (hide_apps == null) {
                hide_apps = new ArrayList<>();
            }
            hide_apps.clear();

            // hide_app.i
            FileEx io = new FileEx();
            String[] files = null;
            try {
                files = io.readFile(HIDE_FILE_NAME);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (files == null) {
                return;
            }

            if (files.length > 0) {
                // ���� üũ

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
                    continue;
                }
                if (line.contains(SYM_AMPERSAND) == false) {
                    continue;
                }
                String arr[] = files[i].split(SYM_AMPERSAND);
                Log.d(DEBUG_TAG, "hide : " + arr[clsName]);
                hide_apps.add(new Pair<String, String>(arr[prefix], arr[clsName]));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    public static boolean checkHide(String app_name){

        if( app_name.equals("com.commax.applist.V2_MainActivity"))
        {
            return true;
        }

        boolean hide = false;

        try {
//			Log.d(DEBUG_TAG, "checkHide app_name "+app_name);
            for (int i = 0; i < hide_apps.size(); i++) {
                if (app_name.equalsIgnoreCase(hide_apps.get(i).second)) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return hide;

    }
}
