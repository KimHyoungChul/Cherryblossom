package com.commax.applist;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;


/**
 * Created by user on 2016-08-19.
 */
public class V2_FavoriteData {

    public String mCategory = "";
    public String mPackageName = "";
    public String mActivityName = "";

    public String mName  = "";
    public Drawable mIcon  = null;

    private static final String FILE_NAME = "/user/app/bin/favorite.i";
    private static final String SYM_AMPERSAND = "&";

    static ArrayList<V2_FavoriteData> msFavoriteDatas;

    public static boolean hasApp(String packageName, String activityName ) {

        for( V2_FavoriteData favoriteData:  msFavoriteDatas){
            if( favoriteData.mPackageName.equals(packageName ) && favoriteData.mActivityName.equals( activityName ) ){
                return true;
            }
        }

        return false;
    }


    public static void writeFavoriteData(Activity activity){

        try {
            String jsonStr ="";

            for( V2_FavoriteData favoriteData :msFavoriteDatas ) {
                jsonStr += "0";
                jsonStr += SYM_AMPERSAND;
                jsonStr += favoriteData.mPackageName + "." + favoriteData.mActivityName;
                jsonStr += "\r\n";
            }

            writeFile(activity, jsonStr);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  String LOG_TAG = "Favorite";

    public static void shExecute(String[] commands) {

        Process shell = null;
        DataOutputStream out = null;
        BufferedReader in = null;

        try {
            // Acquire sh
            Log.i(LOG_TAG, "Starting exec of sh");
            shell = Runtime.getRuntime().exec("sh");//su if needed
            out = new DataOutputStream(shell.getOutputStream());

            in = new BufferedReader(new InputStreamReader(shell.getInputStream()));

            // Executing commands without root rights
            Log.i(LOG_TAG, "Executing commands...");
            for (String command : commands) {
                Log.i(LOG_TAG, "Executing: " + command);
                out.writeBytes(command + "\n");
                out.flush();
            }

            out.writeBytes("exit\n");
            out.flush();
            String line;
            StringBuilder sb = new StringBuilder();

            // shell.

            //pass result
            while (in.ready() && (line = in.readLine()) != null) {
                sb.append(line).append("\n");
            }
            Log.i(LOG_TAG, sb.toString());
            //shell.waitFor();
            shell.wait(1000 * 1);

        } catch (Exception e) {
            Log.e(LOG_TAG, "ShellRoot#shExecute() finished with error", e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if(in != null){
                    in.close();
                }
                // shell.destroy();
            } catch (Exception e) {
                // hopeless
            }
        }
    }

    static public void writeFile(Context context, String content ) throws FileNotFoundException,
            IOException {

        try {
            String[] commands = new String[4];
            commands[0] = "su";
            commands[1] = "touch "+ FILE_NAME;
            commands[2] = "chmod 777 "+ FILE_NAME;
            commands[3] = "exit 0";

            shExecute(commands);

        } catch ( Exception e) {
            // 루팅 안되있으면 Exception
            Log.d("test", "rooting X");
        }

        File f = new File(FILE_NAME);

        try {
            FileOutputStream fout = null;
            BufferedWriter br = null;

            fout = new FileOutputStream(f);
            br = new BufferedWriter(new OutputStreamWriter(fout));

            br.write(content);

            br.flush();

            if (fout != null) {
                fout.close();
            }
        } catch (IOException e) {
             Log.e("WritingFile", e.toString());

            Toast toast = Toast.makeText(context.getApplicationContext(),
                    "can't Write:" + FILE_NAME + " Please Check File Permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    static public void readFavoriteFile(Context context) {

        msFavoriteDatas = new ArrayList<V2_FavoriteData>();
        msFavoriteDatas.clear();

        // menu.i �б�
        FileEx io = new FileEx();
        String[] files = null;
        try {
            files = io.readFile(FILE_NAME);
        } catch (IOException e) {

            Toast toast = Toast.makeText(context.getApplicationContext(),
                    "can't Read:" + FILE_NAME + " Please Check File Permission", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
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

            String fullName  = arr[clsName];
            String activityName =  arr[clsName];
            String packageName =  arr[clsName];

            int lastIndex = activityName.lastIndexOf(".");

            if(lastIndex > 0 )  {
                packageName =  activityName.substring(0, lastIndex);//, activityName.length() );
                activityName =  activityName.substring( lastIndex+1, activityName.length());//, activityName.length() );
            }

            if( V2_AppData.checkHide( fullName) ) {
                continue;
            }

            msFavoriteDatas.add(makeData(context,  arr[prefix], packageName , activityName ));
        }

    }

    static public void removeNoTitle(Context context) {
        boolean bChanged = true;

        while (bChanged) {

            bChanged = false;

            for (int i = 0; i < msFavoriteDatas.size(); ++i) {

                V2_FavoriteData v2_appData = msFavoriteDatas.get(i);

                if (v2_appData.mPackageName.equals(V2_ActivityDictionary.EMPTY_PACKAGE)) {
                    bChanged = true;
                    msFavoriteDatas.remove(i);
                    break;
                }
            }
        }
    }

    static private V2_FavoriteData makeData(Context context , String category, String packageName, String activityName){

        String  packageAndActivityName = packageName + "." + activityName;
        V2_FavoriteData appData = new V2_FavoriteData();
        appData.mCategory = category;

        appData.mPackageName = V2_ActivityDictionary.getInstance(context).getPackageName(packageAndActivityName);
        appData.mActivityName = V2_ActivityDictionary.getInstance(context).getPackageActivityName(packageAndActivityName);

        appData.mName = V2_ActivityDictionary.getInstance(context).getTitle( packageAndActivityName);// applicationInfo.name;//packageName;
        appData.mIcon = V2_ActivityDictionary.getInstance(context).getIcon( packageAndActivityName );

        return appData;


    }
}
