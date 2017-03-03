package com.commax.pairing.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

/**
 * Property 관리
 */
public class PropertiesEx {

	private String path;

	public PropertiesEx(final String path) {
		this.path = path;

	}

	private Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(path);
			prop.load(in);
			in.close();
		} catch (FileNotFoundException e2) {
//			
		} catch (IOException e) {
//			
		}
		return prop;
	}

	public boolean set(final String name, final String value) {
		boolean ret = false;

		// 기존 값을 읽어야, 저장된 값 뒤에 수정된 것이 추가됨
		// 읽지 않을 경우 기존 값이 삭제됨
		Properties prop =getProperties();

		try {
			FileOutputStream	out = new FileOutputStream(path);
			prop.setProperty(name, value);
			prop.store(out, null);
			out.close();
			ret = true;
		} catch (FileNotFoundException e1) {
//			
		} catch (IOException e) {
//			
		}

		return ret;
	}



	public String get(final String key) {
		String ret=getProperties().getProperty(key);
		if (ret!=null) {
			if (ret.contains("\"")) {
				ret=ret.replace("\"", "");
			}
		}
		return ret;

	}

	public Set<Object> getAll() {
		return getProperties().keySet();

	}

}
