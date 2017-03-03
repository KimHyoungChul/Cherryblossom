package com.commax.applist;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class ThemeLoader {

	private AssetManager assetManager = null;
	private Context context;
	public ThemeLoader(Context context) {
		this.context=context.getApplicationContext();
		// http://stackoverflow.com/questions/12716607/accessing-assets-of-other-android-app-on-jelly-bean
		// http://stackoverflow.com/questions/13524143/get-the-file-from-asset-folder-android

		PackageManager mPackageManager = context.getApplicationContext().getPackageManager();
		Resources res = null;
		try {
			res = mPackageManager
					//.getResourcesForApplication("com.commax.theme");
					.getResourcesForApplication("com.commax.pxdtheme");

		} catch (NameNotFoundException e) {

			e.printStackTrace();
		}

		if (res == null) {
			Log.d("tag", "not found theme apk");
			return;
		}

		assetManager = res.getAssets();
	}

	public Drawable getIcon(String fileName, String addedText) {
		if (assetManager == null) {
			return null;
		}

		InputStream istr = null;
		String folder_root = "";

		int screenSize = context.getResources().getConfiguration().screenLayout &
				Configuration.SCREENLAYOUT_SIZE_MASK;

		switch(screenSize) {
			case Configuration.SCREENLAYOUT_SIZE_LARGE:
//				Log.d("ThemeLoader", "screenSize : large");
				folder_root = "mdpi";
				break;

			case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//				Log.d("ThemeLoader", "screenSize : normal");
				folder_root = "mdpi";
				break;
		}

		try {
			// 파일 이름 주의! 확장자가 png여야 한다.
			if(!folder_root.equals("")) {
				istr = assetManager.open(folder_root + "/" + addedText + fileName + ".png");
			}else {
				istr = assetManager.open(addedText + fileName + ".png");
			}
		} catch (IOException e) {
//			Log.d("ThemeLoader", "maybe there is no "+addedText + fileName + ".png");
//			e.printStackTrace();
		}

		if (istr == null) {
//			Log.d("tag", "not found resource file");
			return null;
		}

//		Log.d("tag", "found resource file");

//		Bitmap bitmap = BitmapFactory.decodeStream(istr);
		Drawable drawable = new BitmapDrawable(context.getResources(), istr);
		return drawable;
	}
}
