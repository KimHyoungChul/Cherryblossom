package com.commax.onvif_device_manager.device;

/**
 * Created by bagjeong-gyu on 2016. 9. 19..
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * W/P 정보 관리
 */
public class WallpadInfo {

    static final Uri HOME_ADDRESS_CONTEXT_URI = Uri.parse("content://com.commax.provider.settings/setting/9");

    static final String TAG = "WALLPADINFO";
    // String  strHouseAddress = getSettingsDatabaseQuery(HOME_ADDRESS_CONTEXT_URI);

    static public String getHouseAddress(Context activity)	{

        String result = getSettingsDatabaseQuery(activity, HOME_ADDRESS_CONTEXT_URI);

        return result;
    }
    /* Get Settings Database Query */
    static public String getSettingsDatabaseQuery(Context activity, Uri uiContextUri) {
        String strContext = "";
        Cursor InfoCursor = null;


        ContentResolver m_ContentResolver = activity.getContentResolver();

        try {
            if((InfoCursor = m_ContentResolver.query(uiContextUri, null, null, null, null)) != null) {
                while(InfoCursor.moveToNext()) {
                    strContext = InfoCursor.getString(InfoCursor.getColumnIndex("value"));
                }

                if(InfoCursor != null) InfoCursor.close();
                InfoCursor = null;
            }
        }
        catch(UnsupportedOperationException e) {
            Log.e(TAG, "Get Settings Database Query : Unsupported Operation Exception. !!!");
        }
        catch(NullPointerException e) {
            Log.e(TAG, "Get Settings Database Query : Null Pointer Exception. !!!");
        }

        return strContext;
    }


    static public String getSiteCode()	{

        File file = new File("/user/version/version.i");
        ArrayList<String> readed = readFile(file );


        if( readed.size() == 0)
        {
            return "empty file";
        }

        for( String line : readed)
        {
            int index = line.indexOf("SITE=");

            if( index < 0)
            {
                continue;
            }

            line = line.replace("SITE=","");
            line = line.replace("\t","");
            line = line.replace(" ","");

            return line;

        }

        return "can't find SITE=";
    }



    static public String getUUID()	{




        File file = new File("mnt/sdcard/CMXdata/CreateAccount.properties");
        ArrayList<String> readed = readFile(file );


        if( readed.size() == 0)
        {
            return "empty file";
        }

        for( String line : readed)
        {
            int index = line.indexOf("resourceNo=");

            if( index < 0)
            {
                continue;
            }

            line = line.replace("resourceNo=","");
            line = line.replace("\t","");
            line = line.replace(" ","");

            return line;
        }

        return "can't find resourceNo=";
    }



    static private ArrayList<String> readFile(File file){

        ArrayList<String> result = new ArrayList<String> ();


        try {
            // open the file for reading
            InputStream fis = new FileInputStream(file);

            // if file the available for reading
            if (fis != null) {

                // prepare the file for reading
                InputStreamReader chapterReader = new InputStreamReader(fis);
                BufferedReader buffreader = new BufferedReader(chapterReader);

                String line;

                // read every line of the file into the line-variable, on line at the time
                do {
                    line = buffreader.readLine();
                    // do something with the line
                    System.out.println(line);

                    result.add(line);
                } while (line != null);

            }


            fis.close();
        } catch (Exception e) {
            // print stack trace.
        } finally {

        }
        // close the file.
        // try {
        //      fis.close();
        //  } catch (IOException e) {
        //      e.printStackTrace();
        //  }
        // }


    	 /*
        int readcount=0;
        if(file!=null&&file.exists()){
            try {
                FileInputStream fis = new FileInputStream(file);
                readcount = (int)file.length();
                byte[] buffer = new byte[readcount];
                fis.read(buffer);
                for(int i=0 ; i<file.length();i++){
                    Log.d(TAG, ""+buffer[i]);
                }

                result.ad = new String(buffer, "UTF-8");
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();

                return "Fail To Read";
            }
        }
        */

        return result;
    }






}

