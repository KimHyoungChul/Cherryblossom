package com.commax.settings.content_provider;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CMFile {

	public boolean canRead(final String fileName) {
		final File f = new File(fileName);
		if (f.exists() && f.canRead()) {
			return true;
		}else{
			return false;
		}		
	}
	
	
	public String read(final String filename) throws IOException {
		
		FileInputStream inputStream = new FileInputStream(filename);
		int size=inputStream.available();
		byte[] buffer = new byte[size];
		inputStream.read(buffer);
		inputStream.close();
		return new String(buffer).trim();
		
		// FileInputStream is need close() method, but BufferedReader is not
		// need to do.
		// https://developer.android.com/reference/java/io/FileInputStream.html
		// https://developer.android.com/reference/java/io/BufferedReader.html

		
	}
	
	public void write(final String filename,String str) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(filename);
		outputStream.write(str.getBytes());
		outputStream.flush();
		outputStream.getFD().sync();
		outputStream.close();
	}

}
