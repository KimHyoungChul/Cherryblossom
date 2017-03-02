package com.commax.headerlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
    private static final String TAG = UpdateButtonClick.class.getSimpleName();

    private String fileName; // 업데이트 시킬 파일 네임
    public static final int MESSAGE_DOWNLOAD_STARTING = 3;
    public static final int MESSAGE_DOWNLOAD_PROGRESS = 4;
    public static final int MESSAGE_DOWNLOAD_COMPLETE = 5;
    static String APPLICATION_DOWNLOAD_URL = "http://" + BackgroundService.CloudServerIP + "/us/pad/apk/";
    String Downloaddirectory = getDownloadDirectory();

    public void checkForUpdate(final String packagename ,final String NewversionName , final String appName , final String server_ip_address)
    {
        final String download_url = "http://" + server_ip_address + "/us/pad/apk/";
        APPLICATION_DOWNLOAD_URL = download_url;
        Log.d(TAG,"download_url :" +download_url);

        new Thread(new Runnable(){
            public void run(){
                Looper.prepare();
                try{
                    //TODO versionname 변경
                    fileName = packagename +"."+ NewversionName + ".apk";
                    Log.d(TAG,"fileName : " + fileName);
                    try
                    {
                        //다운로드 폴더 얻어오기
                        String localpath = getDownloadDirectory();
                        Log.d("Server version","Server app version: " + NewversionName);
                        Log.d("Download directory","Directory: " + localpath);
                        //TODO test
                        downloadUpdate(download_url + fileName, fileName,
                                localpath , appName);
                        Log.d(TAG,"update exe");
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
        } else {                       //내장메모리 위치
            Log.d("getDownloadDirectory","inner memory exe");
            File file = Environment.getRootDirectory();
            sdcardPath = file.getAbsolutePath();
            downloadpath = sdcardPath + "/CMXdownload";
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
            localpath , final String appName) {
        new Thread(new Runnable(){
            public void run(){
                try
                {
                    Log.e(TAG, "download Update exe");
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_DOWNLOAD_STARTING;
                    msg.obj = localpath + fileName ;

                    File apkFile = new File ( localpath + fileName );
                    Log.d("downloadUpdate","directory 1 :"+ localpath + fileName  );
                    ListAdapter3.viewUpdateHandler.sendMessage(msg);

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
                    Log.d("Test","Last");
                    try
                    {
                        Log.d(TAG, "AppName :" + appName);
                        //TODO 내꺼 업데이트면 프로세스 kill시켜야 한다.
                        if(appName.equals("headerlist"))
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
                }
            }
        }).start();
    }

    // 다운로드 받을 경로 없으면 다시 만들고 , 이전에 받은 apk있으면 지우는 과정 (다운로드 사전 작업)
    public boolean downloadUpdateFile(String downloadFileUrl, String destinationFilename, String localPath) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
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
        Log.d("downloadFile", "url :" + url);
        try {
            Log.d("downloadFile","try exe");
            HttpResponse response = client.execute(request);
            Log.e("downloadFile","after response");
            StatusLine status = response.getStatusLine();
            Log.d("downloadFile", "Request returned status " + status);

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
                    ListAdapter3.viewUpdateHandler.sendMessage(msg);

                    out.write(buf,0,len);
                }
                out.close();
                Log.d("downloadFile", "out.close()");
            } else
            {
                Log.e("downloadFile","error");
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
        ListAdapter3.viewUpdateHandler.sendMessage(msg);
        return filedownloaded;
    }


}
