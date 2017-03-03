package com.commax.login.Common;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by OWNER on 2016-06-30.
 */
public class AboutFile {

    private static final String TAG = AboutFile.class.getSimpleName();
    public static String downloaddir;
    public static String CMX_data_dir;
    public static String CMX_data_file_path;

    public AboutFile() {
        if(TypeDef.nexel_activity_70UX)
        {
            downloaddir = "/user/app";
            CMX_data_dir = downloaddir + "/bin/";
            CMX_data_file_path = CMX_data_dir + "CreateAccount.properties";
        }
        else
        {
           downloaddir = Environment.getExternalStorageDirectory().getPath();
           CMX_data_dir = downloaddir + "/CMXdata/";
           CMX_data_file_path = CMX_data_dir + "CreateAccount.properties";
        }
    }



    private static File mPropertyFile = null;
    //file inputstream
    private static FileInputStream mFis = null;
    //file outputstream
    private static FileOutputStream mFos = null;
    //proterties
    private static Properties mProperty = null;

    public void makefile() throws IOException {

        //쓰기시작할때 만들어도 된다.
        //폴더 생성
        File dir = makeDirectory(CMX_data_dir);
        Log.d(TAG, "make directory () : " + CMX_data_dir);
    }

    /**
     * 파일에 내용 쓰기
     */
    public void writeFile(String name, String value) {
        File file = null;
        try {
            file = new File(CMX_data_file_path);

            try {
                //Well it seems that you can't chmod because the storage is emulated (emulated storage systems normally use FAT file systems, which do not support chmod).
               //The only way to solve this issue is to change the installation path using a separate partition with different file system (ext).
                //TODO A64에서는 적용되는 것처럼 보이지 않음
                int i = changePermissons(file,0777);
            Log.d(TAG, "file permission : " + i);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProperty = new Properties();

            Log.d(TAG, "CMX_data_file_path : " + CMX_data_file_path);
            Log.d(TAG, "name : " + name + " , " + " value : " + value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
                Log.d(TAG, "file create");
            }

            mFis = new FileInputStream(file);
            mProperty.load(mFis);
            //TODO test clear 할필요 없지 않은가? clear 잇게끔 코드 수정해보기
//            mProperty.clear();

            mFos = new FileOutputStream(file);
            // properties 추가하기
            mProperty.setProperty(name, value);
            mProperty.store(mFos, "login inform");
            mFos.close();
            mFis.close();

            Log.d(TAG, "name :" + name + " value : " + value);

            try {
                Process process = Runtime.getRuntime().exec("sync");
                process.getErrorStream().close();
                process.getInputStream().close();
                process.getOutputStream().close();
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int changePermissons(File path, int mode) throws Exception {
        Class<?> fileUtils = Class.forName("android.os.FileUtils");
        Method setPermissions = fileUtils.getMethod("setPermissions",
                String.class, int.class, int.class, int.class);

        return (Integer) setPermissions.invoke(null, path.getAbsolutePath(),
                mode, -1, -1);
    }

    /**
     * 디렉토리 생성
     *
     * @return dir
     */
    private File makeDirectory(String dir_path) {
        File dir = new File(dir_path);
        if (!dir.exists()) {
            dir.mkdirs();
            Log.i(TAG, "!dir.exists");
        } else {
            Log.i(TAG, "dir.exists");
        }
        return dir;
    }

    /**
     * 파일 생성
     *
     * @param dir
     * @return file
     */
    private File makeFile(File dir, String file_path) {
        File file = null;
        boolean isSuccess = false;
        if (dir.isDirectory()) {
            file = new File(file_path);
            if (file != null && !file.exists()) {
                Log.i(TAG, "!file.exists");
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                }
            } else {
                Log.i(TAG, "file.exists");
            }
        }
        return file;
    }

    public String readFile(String name) {

        FileInputStream fis = null;
        Properties mProperty = null;
        File file = new File(CMX_data_file_path);
        String value = "";

        if (file.exists()) {
            mProperty = new Properties();
            try {
                fis = new FileInputStream(file);
                mProperty.load(fis);
                value = mProperty.getProperty(name);
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Property", "File not exists");
        }
//       if(name.equals("password"))
//       {
//           SubActivity.getInstance().setLoadingView(false);
//       }
        return value;
    }

}
