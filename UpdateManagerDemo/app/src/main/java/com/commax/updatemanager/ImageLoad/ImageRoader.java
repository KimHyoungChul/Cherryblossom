package com.commax.updatemanager.ImageLoad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.commax.updatemanager.MainActivity;
import com.commax.updatemanager.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by OWNER on 2016-05-31.
 */
public class ImageRoader {

    String TAG = ImageRoader.class.getSimpleName();
    String serverUrl = null;
    String LocalServer = null;

    public ImageRoader(String localserver) {
        LocalServer = localserver;
        new ThreadPolicy();
    }

    public Bitmap getBitmapImg(String imgStr) {

        Bitmap bitmapImg = null;
        serverUrl = "http://" + LocalServer + "/us/pad/icon/";
        Log.d(TAG , "serverUrl :  " + serverUrl);

        try {
            URL url = new URL(serverUrl +
                    URLEncoder.encode(imgStr, "utf-8"));
            // Character is converted to 'UTF-8' to prevent broken

            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            bitmapImg = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            Log.d("icon","no icon in server");
            //여기에 예외 처리
            BitmapDrawable d = (BitmapDrawable) MainActivity.getInstance().getResources().getDrawable(R.mipmap.ic_launcher);
            bitmapImg = d.getBitmap();
            Log.e("icon","Use default icon");
        }
        return bitmapImg;
    }
}
