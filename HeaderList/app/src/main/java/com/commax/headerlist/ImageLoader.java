package com.commax.headerlist;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by OWNER on 2016-06-08.
 */
public class ImageLoader {
//    private final String serverUrl = "http://10.0.0.2/download/icon/";
    public static int TIMEOUT_VALUE = 1000;   // 1초
    static String TAG = ImageLoader.class.getSimpleName();
    private static PackageManager pm;

    public ImageLoader() {
        new ThreadPolicy();
    }

    public Bitmap getBitmapImg(String imgStr , String IP) {
        Bitmap bitmapImg = null;
        Log.d(TAG, "IP : " + IP);
        String ServerIP = "http://" + IP +"/us/pad/icon/";
        Log.d(TAG, "ServerIP :" + ServerIP);
        try {
            Log.d(TAG ,"test");
            URL url = new URL(ServerIP +
                    URLEncoder.encode(imgStr, "utf-8"));
            // Character is converted to 'UTF-8' to prevent broken
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
//            conn.connect();
            Log.d(TAG, "ServerIP :" + ServerIP);
            try{
                conn.setConnectTimeout(TIMEOUT_VALUE);// Timeout 5seconds
                Log.d(TAG , "Time out");
            }catch (Exception e)
            {
                Log.e(TAG, "Timeout error");
            }
            InputStream is = conn.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.d("icon","no icon in server");
            //여기에 예외 처리
            BitmapDrawable d = (BitmapDrawable) MainActivity.getInstance().getResources().getDrawable(R.drawable.icon_emergency);
            bitmapImg = d.getBitmap();
            Log.e("icon","Use default icon");
        }
        return bitmapImg;
    }

    // 설치 되어 있는 app의 아이콘 가져오기 , 설치 되어 있지 않은 app은 서버에서 가져오기
    static Drawable getIcon(String packageName)
    {
        pm = MainActivity.getInstance().getPackageManager();
        Drawable d = null;
        try {
            d = pm.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e)
        {
            Log.d(TAG, " backgroundService.CloudServerIP  ImageLoader:" +BackgroundService.CloudServerIP);
            Drawable b = new BitmapDrawable(new ImageLoader().getBitmapImg(packageName , BackgroundService.CloudServerIP ));
            d = b;
        }
        return d;
    }

}
