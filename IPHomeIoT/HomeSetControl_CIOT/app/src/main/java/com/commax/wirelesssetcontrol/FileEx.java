package com.commax.wirelesssetcontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class FileEx {

	public String[] readFile(String fileName) throws FileNotFoundException,
			IOException {
		String[] returnValue = new String[]{};

		File f = new File(fileName);
		if (f.exists() == false) {
			return returnValue;
		}
		if (f.canRead() == false) {
			return returnValue;
		}

		FileInputStream fis = null;
		BufferedReader br = null;

		fis = new FileInputStream(f);
		br = new BufferedReader(new InputStreamReader(fis));
		String temp;
		ArrayList<String> list = new ArrayList<String>();
		while ((temp = br.readLine()) != null) {

			list.add(temp.trim());
		}
		returnValue = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			returnValue[i] = list.get(i);
		}

		if (br != null) {
			try {
				br.close();
			} catch (Exception e) {
				// Log.e(TAG, e.toString());
			}
		}
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				// Log.e(TAG, e.toString());
			}
		}

		return returnValue;
	}

	public String[] readFolder(String path) {
		String[] returnValue = new String[]{};

		File f = new File(path);
		File[] files = f.listFiles();
		if (files == null) {
			return returnValue;
		}

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				continue;
			}
			list.add(file.getName());
		}
		returnValue = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			returnValue[i] = list.get(i);
		}
		return returnValue;
	}

	public Bitmap findImage(String name, int dstWidth, int dstHeight){
		Bitmap bitmap = null;

		try{
			if ( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()+"/CMXBackground";

				File file = new File(path);
				String str;
				int num = 0;

				int imgCount = file.listFiles().length;	// 파일 총 갯수 얻어오기
				Bitmap[] map = new Bitmap[imgCount];

				if ( file.listFiles().length > 0 )
					for ( File f : file.listFiles() ) {
						try {
							str = f.getName();                // 파일 이름 얻어오기
							if (str.equalsIgnoreCase(name)) {

								final BitmapFactory.Options options = new BitmapFactory.Options();
								options.inJustDecodeBounds = true;

								Bitmap src = BitmapFactory.decodeFile(path + "/" + str);
								map[num] = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, true);
								bitmap = map[num];
							}
						}catch (Exception e){
							e.printStackTrace();
						}
						num++;
					}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}

	public String readAccountInfo(String name) {

		String value = "";

		try {
			FileInputStream fis = null;
			Properties mProperty = null;
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/CMXdata/CreateAccount.properties");

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
		}catch (Exception e){
			e.printStackTrace();
		}
		return value;
	}

	public ArrayList<Pair<String, String>> readHideFile() {

		ArrayList<Pair<String, String>> hide_apps = new ArrayList<>();

		try {
			// quick.i �б�
			FileEx io = new FileEx();
			String[] files = null;
			try {
				files = io.readFile(NameSpace.HIDE_FILE);
			} catch (FileNotFoundException e) {

				// e.printStackTrace();
			} catch (IOException e) {

				// e.printStackTrace();
			}

			if (files == null) {
				return null;
			}

			if (files.length > 0) {
				// ���� üũ
				if (files == null) {
					return null;
				}
				if ("".equals(files[0])) {
					return null;
				}
				if ("-1".equals(files[0])) {
					return null;
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
				if (line.contains(NameSpace.SYM_AMPERSAND) == false) {
					// �ùٸ� �������� Ȯ��
					continue;
				}
				String arr[] = files[i].split(NameSpace.SYM_AMPERSAND);
				hide_apps.add(new Pair<String, String>(arr[prefix], arr[clsName]));
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		return hide_apps;
	}
}
