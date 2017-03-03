package com.commax.iphomeiot.calldial;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class SlaveID {
	private String[] entries;
	private String[] entryValues;

	public String[] getEntries() {
		if (entries == null) {
			name();
		}

		return (String[]) entries.clone();
	}

	public String[] getEntryValues() {
		if (entryValues == null) {
			name();
		}

		return (String[]) entryValues.clone();
	}

	private void name() {
		entries = new String[]{};
		entryValues = new String[]{};
		
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(NameSpace.SLAVE_ID_TABLE_FILE_NAME);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Set<Object> objects = prop.keySet();
		Object[] keys=objects.toArray();
		entries = new String[keys.length];
		entryValues = new String[keys.length];
		
		
		for (int i = 0; i < keys.length; i++) {
			
			String key=(String) keys[i];
			entries[i]=key;
			entryValues[i]=prop.getProperty(key);
			
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public String matchedEntry(String entryValue) {
		String[] entries = getEntries();
		String[] entryValues = getEntryValues();

		for (int i = 0; i < entryValues.length; i++) {
			if (entryValue.equals(entryValues[i])) {
				return entries[i];
			}
		}

		return null;
	}

	public String matchedEntryValue(String entry) {
		String[] entries = getEntries();
		String[] entryValues = getEntryValues();

		for (int i = 0; i < entryValues.length; i++) {
			if (entry.equals(entries[i])) {
				return entryValues[i];
			}
		}

		return null;
	}
}
