package com.commax.updatemanager.GetAPPList_Download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.commax.updatemanager.MainActivity;
import com.commax.updatemanager.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by OWNER on 2016-04-22.
 */
public class UpdateButtonClick {
    private static final String TAG = UpdateButtonClick.class.getSimpleName(); //for log
    StartService startService = new StartService();

    Context mContext;
    Handler mHandler;
    private String fileName; // 업데이트 시킬 파일 네임
    String[] serversion; // "." 을 기준으로 파싱해서 값 비교하기 때문에 배열
    String oldversion[];
    public static final int MESSAGE_DOWNLOAD_STARTING = 3;
    public static final int MESSAGE_DOWNLOAD_PROGRESS = 4;
    public static final int MESSAGE_DOWNLOAD_COMPLETE = 5;

    public String IP_ADDRESS ;
    public String APPLICATION_DOWNLOAD_URL;// 서버의 apk가 있는 폴더 경로
    public String Downloaddirectory = getDownloadDirectory();

    public UpdateButtonClick(Context context)
    {
        mContext = context;
    }

    public UpdateButtonClick(Context context ,  String LoacalServer)
    {
        mContext = context;
        IP_ADDRESS = LoacalServer;
        APPLICATION_DOWNLOAD_URL = "http://" + IP_ADDRESS + "/us/pad/apk/";// 서버의 apk가 있는 폴더 경로
    }

    public UpdateButtonClick(Context context , String LoacalServer , Handler handler)
    {
        mContext = context;
        IP_ADDRESS = LoacalServer;
        APPLICATION_DOWNLOAD_URL = "http://" + IP_ADDRESS + "/us/pad/apk/";// 서버의 apk가 있는 폴더 경로
        mHandler = handler;
    }


    public void checkForUpdate(final String packagename ,final String NewversionName ,final String
            oldversionName , final String appName)
    {
        APPLICATION_DOWNLOAD_URL = "http://" + IP_ADDRESS + "/us/pad/apk/";// 서버의 apk가 있는 폴더 경로
        Log.d(TAG, "serverIP : " +IP_ADDRESS);
        Log.d(TAG, "Application URL : " +APPLICATION_DOWNLOAD_URL);
        new Thread(new Runnable(){
            public void run(){
                Looper.prepare();
                try{
                    serversion = NewversionName.split("[.]");
                    oldversion = new String[serversion.length];
                    fileName = packagename +"."+ NewversionName + ".apk";
                    Log.d(TAG,"fileName : " + fileName);

                    //server 버전 표시
                    for(int i=0; i<serversion.length; i++)
                    {
                        System.out.println("serversion["+i+"] : " + serversion[i]);
                    }
                    try
                    {
                        try{
                            if(TextUtils.isEmpty(oldversionName) || oldversionName.equals("null"))
                            {
                                Log.e(TAG,"oldversionName = null");
                            }
                            else
                            {
                                Log.d(TAG,"oldversionName try");
                                oldversion = oldversionName.split("[.]");
                                //현재 app 버전 표시
                                for(int i=0; i<oldversion.length; i++)
                                {
                                    Log.d(TAG, "oldversion["+i+"] : " + oldversion[i]);
                                }
                                Log.d(TAG, "oldversion : " +  String.valueOf(oldversionName));
                            }
                            //임시 널처리 version Name 을 0.0.0으로
                            if(TextUtils.isEmpty(oldversionName) || oldversionName.equals("null"))
                            {
                                for(int i = 0 ; i < serversion.length ; i++)
                                {
                                    oldversion[i] = "0";
                                }
                                Log.e(TAG,"null 처리");
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Log.e(TAG,"oldversionName == null");
                        }
                        //다운로드 폴더 얻어오기
                        String localpath = getDownloadDirectory();
                        Log.d("Server version","Server app version: " + NewversionName);
                        Log.d("Download directory","Directory: " + localpath);

                        //TODO version name 0.0.0 으료 비교 구문
                        int j = serversion.length <= oldversion.length ? oldversion
                                .length:serversion.length;
                        Log.d("long lenth", String.valueOf(j));
                        //배열 숫자 비교교
                        for(int i = 0; i< j ;i++)
                        {
                            if(Integer.parseInt(serversion[i]) > Integer.parseInt(oldversion[i]))
                            {
                                Log.d(TAG ,"download start : url = " + APPLICATION_DOWNLOAD_URL );
                                downloadUpdate(APPLICATION_DOWNLOAD_URL + fileName, fileName,
                                        localpath , appName , packagename);
                                Log.d(TAG,"update exe");
                                break;
                            }
                        }
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                        Log.e("Thread"," app version compare error");
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e("checkForUpdate"," Can't get properties!!");
                }
                Looper.loop();
            }
        }).start();
    }

    //다운로드 폴더 얻기
    private String getDownloadDirectory(){
        String sdcardPath = "";
        String downloadpath = "";
        if ( isUsableSDCard(true)){    //외장메모리 사용가능할 경우
            Log.d("getDownloadDirectory","External memory exe");
            sdcardPath = Environment.getExternalStorageDirectory().getPath();
            Log.d("getDownloadDirectory",sdcardPath);
            downloadpath = sdcardPath + "/CMXdownload/";
            Log.d("getDownloadDirectory",downloadpath);
        }
        else {                       //내장메모리 위치
            Log.d("getDownloadDirectory","inner memory exe");
            File file = Environment.getRootDirectory();
            sdcardPath = file.getAbsolutePath();
            downloadpath = sdcardPath + "/CMXdownload/";
        }
        return downloadpath;
    }

    //외장메모리 사용 가능여부 확인
    private boolean isUsableSDCard(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
        if ( Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if( !requireWriteAccess &&
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ) {
            return true;
        }
        return false;
    }

    //다운로드 받은 앱을 설치, 이전 실행 앱 종료
    public void downloadUpdate(final String downloadFileUrl, final String fileName,final String
            localpath , final String appName , String package_name) {
        new Thread(new Runnable(){
            public void run(){
                try
                {
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_DOWNLOAD_STARTING;
                    msg.obj = localpath + fileName ;

                    File apkFile = new File ( localpath + fileName );
                    Log.d("downloadUpdate","directory 1 :"+ localpath + fileName  );
                    ListHeaderAdapter.viewUpdateHandler.sendMessage(msg);

                    downloadUpdateFile(downloadFileUrl, fileName, localpath);
                    Log.d("downloadUpdate", "downloadUpdateFile exe ");

                    Log.d("downloadUpdate","Intent 전");
                    //다운로드 받은 패키지를 인스톨한다.
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    //apk파일 경로와 mime - type을 지정
                    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.getInstance().startActivity(intent); //TODO editted
                    Log.d("downloadUpdate", "Intent exe");
                  /*
                   * 안드로이드 프로세스는  단지 finish() 만 호출 하면 죽지 않는다.
                   * 만약 프로세스를 강제로 Kill 하기위해서는 화면에 떠있는 Activity를 BackGround로 보내고
                   * 강제로 Kill하면 프로세스가  완전히 종료가 된다.
                   * 종료 방법에 대한 Source는 아래 부분을 참조 하면 될것 같다.
                   */


                    //MainActivity.getInstance().moveTaskToBack(true); //TODO editted
                    Log.d("downloadUpdate", "moveTaskToBack exe ");
                    //MainActivity.getInstance().finish(); //TODO editted
                    Log.d("Test","Last");
                    try
                    {
                        //TODO 내꺼 업데이트면 프로세스 kill시켜야 한다.
                        if(appName.equals("UpdateManagerDemo"))
                        {
                            android.os.Process.sendSignal(android.os.Process.myPid(), android.os.Process.SIGNAL_KILL); //TODO editted not for finish
                        }
                        File apkfile = new File( localpath + fileName);
                        Log.d("App delete", "delete");
                    }
                    catch (Exception e)
                    {
                        Log.e("Delete Error"," Error");
                    }
                }
                catch (Exception e)
                {
                    Log.e("downloadUpdate","try error");
                    MainActivity.getInstance().progressDialog.dismiss();
                    //핸들러로 Toast 메시지 해야한다.
                    Message msg = mHandler.obtainMessage();
                    msg.what = 0;
                    msg.obj = String.valueOf(MainActivity.getInstance().getString(R.string.network_connect_error));
                    mHandler.sendMessage(msg);
                }
            }
        }).start();

    }

    // 다운로드 받을 경로 없으면 다시 만들고 , 이전에 받은 apk있으면 지우는 과정 (다운로드 사전 작업)
    public boolean downloadUpdateFile(String downloadFileUrl, String destinationFilename, String localPath) {
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) == false) {
            return false;
        }
        File downloadDir = new File( localPath );
        Log.d("DOWNLOAD","Downloading");
        if (downloadDir.exists() == false) {
            Log.d("downloadUpdateFile", "if moon exe");
            downloadDir.mkdirs();
        }
        else
        {
            File downloadFile = new File( localPath + destinationFilename );
            Log.d("downloadUpdateFile", "else moon exe");

            if (downloadFile.exists()) {
                try{
                    //downloadFile.delete(); //이전에 다운로드 받은 파일이 있으며 지우기
                    Log.d("downloadUpdateFile", "delete exe");
                }
                catch (Exception e)
                {
                    Log.e("downloadUpdatefile","downloadFile delete error");
                }
                Log.d("downloadUpdateFile", "LocalPath : " + localPath);
                Log.d("downloadUpdateFile", "destinationFilename : " + destinationFilename);
            }
        }
        return this.downloadFile(downloadFileUrl, localPath, destinationFilename );
    }

    //파일 다운로드 과정 표시
    public boolean downloadFile(String url, String destinationDirectory, String destinationFilename ) {
        boolean filedownloaded = true;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(String.format(url));
        Message msg = Message.obtain();

        try {
            HttpResponse response;
            StatusLine status;
            try
            {
                Log.d("downloadFile","try exe");
                response = client.execute(request);
                Log.d("downloadFile","after response");
                status = response.getStatusLine();
                Log.d("downloadFile", "Request returned status " + status);
            }catch (Exception e)
            {
                Log.d("downloadFile","Catch exe");
                response = client.execute(request);
                Log.d("downloadFile","after response");
                status = response.getStatusLine();
                Log.d("downloadFile", "Request returned status " + status);
            }

            if (status.getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream instream = entity.getContent();
                int fileSize = (int)entity.getContentLength();
                FileOutputStream out = new FileOutputStream(new File(destinationDirectory + destinationFilename));
                Log.d("downloadFile","destinationDiretory : " + destinationDirectory);
                Log.d("downloadFile","destinationFilename : " + destinationFilename);

                byte buf[] = new byte[8192];
                int len;
                int totalRead = 0;

                Log.d(TAG,"다운로드 시작");

                while((len = instream.read(buf)) > 0) {
                    //여기서 프로그레스 바 돌아가기?
                    msg = Message.obtain();
                    msg.what = MESSAGE_DOWNLOAD_PROGRESS;
                    totalRead += len;
                    msg.arg1 = totalRead / 1024;
                    msg.arg2 = fileSize / 1024;
                    ListHeaderAdapter.viewUpdateHandler.sendMessage(msg);

                    out.write(buf,0,len);
                }
                out.close();
                Log.d("downloadFile", "out.close()");
            } else
            {
                Log.e("downloadFile","status error");
                throw new IOException();
            }

        }
        catch (IOException e)
        {
            filedownloaded = false;
            Log.e("downloadFile","try error");
        }

        msg = Message.obtain();
        msg.what = MESSAGE_DOWNLOAD_COMPLETE;
        ListHeaderAdapter.viewUpdateHandler.sendMessage(msg);
        return filedownloaded;
    }


}
