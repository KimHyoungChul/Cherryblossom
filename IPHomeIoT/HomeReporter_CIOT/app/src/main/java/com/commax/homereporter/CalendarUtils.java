package com.commax.homereporter;

import java.util.Calendar;

public class CalendarUtils {

	
	public static String monthFormat(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		String str1 = year + ((month >= 10) ? "" : "0") + month;
		return str1;
	}

	public static String dateFormat(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DATE);
		String str1 = year + ((month >= 10) ? "" : "0") + month + ((date >= 10) ? "" : "0") + date;
		return str1;
	}

	public static String firstDateFormat(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = 1;
		String str1 = year + ((month >= 10) ? "" : "0") + month + "01";
		return str1;
	}

	public static String dotFormat(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = cal.get(Calendar.DATE);
		String str1 = year +"."+ ((month >= 10) ? "" : "0") + month +"."+ ((date >= 10) ? "" : "0") + date;
		return str1;
	}

	public static String firstDotFormat(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int date = 1;
		String str1 = year +"."+ ((month >= 10) ? "" : "0") + month + ".01";
		return str1;
	}
}
