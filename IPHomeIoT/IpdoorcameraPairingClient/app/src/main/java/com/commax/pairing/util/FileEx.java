package com.commax.pairing.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 파일 관리
 */
public class FileEx {
	private String fileName;

	public FileEx(String fileName) {
		this.fileName = fileName;
	}

	public boolean isExistAndReadable() {
		File f = new File(fileName);
		if (f.exists() == false) {
			return false;
		}
		return f.canRead();
	}

	public boolean delete() {
		File f = new File(fileName);
		return f.delete();
	}

	// public boolean delete(final File file) {
	//
	// return file.delete();
	// }
	//
	public ArrayList<String> readFolder() {

		File f = new File(fileName);
		final File[] files = f.listFiles();
		if (files == null) {
			return null;
		}

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			final File file = files[i];
			if (file.isDirectory()) {
				continue;
			}
			list.add(file.getName());
		}
		return list;
	}

	public ArrayList<String> read() {

		File f = new File(fileName);
		return read(f);
	}

	public ArrayList<String> read(final File f) {

		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			Log.d("tag", "파일 찾을 수 없음");
			return null;
		}
		final BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		final ArrayList<String> list = new ArrayList<String>();
		String temp;
		try {
			while ((temp = br.readLine()) != null) {
				// 줄 단위로 읽고 리턴
				list.add(temp);
			}
		} catch (IOException e) {
			Log.d("tag", "파일 읽기 실패");
			return null;
		}
		try {
			br.close();
		} catch (IOException e) {
			Log.d("tag", "파일 닫기 실패");
			return null;
		}
		try {
			fis.close();
		} catch (IOException e) {
			Log.d("tag", "파일 닫기 실패");
			return null;
		}

		return list;
	}

	public boolean write(final ArrayList<String> contents) {
		File f = new File(fileName);
		return write(f, contents, false);
	}

	// public boolean write(final File f, final ArrayList<String> contents) {
	//
	//
	// return write(f, contents,false);
	//
	// }
	//
	// public boolean write( final ArrayList<String> contents,
	// final boolean append) {
	//
	//
	//
	// return write(f, contents,append);
	// }

	public boolean write(final File f, final ArrayList<String> contents,
						 final boolean append) {

		final StringBuffer buf = new StringBuffer();
		for (String s : contents) {
			buf.append(s);
			buf.append("\n");
		}

		return name(f, buf.toString(), append);

	}

	//
	// public boolean write( final String contents, final boolean append)
	// {
	//
	// // 내부 메모리에 저장할 경우 아래 함수를 호출하는 쪽에서 무조건 해야 한다. 그래야 files폴더가 생성된다
	// // getFilesDir().getAbsolutePath();
	// // final File f = new File(fileName);
	// final StringBuffer buf = new StringBuffer();
	// buf.append(contents);
	// buf.append("\n");
	//
	// return name(f, buf.toString(), append);
	//
	// }

	public boolean write(final String contents) {

		// 내부 메모리에 저장할 경우 아래 함수를 호출하는 쪽에서 무조건 해야 한다. 그래야 files폴더가 생성된다
		// getFilesDir().getAbsolutePath();
		// final File f = new File(fileName);
		final StringBuffer buf = new StringBuffer();
		buf.append(contents);
		buf.append("\n");
		File f = new File(fileName);
		return name(f, buf.toString(), false);

	}

	// public boolean write(final File f, final String contents) {
	//
	// // 내부 메모리에 저장할 경우 아래 함수를 호출하는 쪽에서 무조건 해야 한다. 그래야 files폴더가 생성된다
	// // getFilesDir().getAbsolutePath();
	//
	// final StringBuffer buf = new StringBuffer();
	// buf.append(contents);
	// buf.append("\n");
	//
	// return name(f, buf.toString(), false);
	// }

	private boolean name(final File f, final String contents,
						 final boolean append) {

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(f, append);
		} catch (FileNotFoundException e) {
			Log.d("tag", "파일 찾기 실패");
			return false;
		}
		try {
			fos.write(contents.getBytes());
			fos.flush();
			//추가됨
//			A20에서는 파일 저장 후 바로 제품을 끄면 파일 내용이 반영되지 않는다.
//			명시적으로 FileOutputStream.getFD().sync(); 을 사용해야 한다
			fos.getFD().sync();
			return true;
		} catch (IOException e) {
			Log.d("tag", "파일 쓰기 실패");
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				Log.d("tag", "파일 닫기 실패");

			}
		}

		return false;
	}
}
