package com.commax.control.Common;

public class Log {
	static final boolean LOG = true;

	/*
	Log.isLoggable(TAG, Log.VERBOSE) == false
	Log.isLoggable(TAG, Log.DEBUG) == false
	Log.isLoggable(TAG, Log.INFO) == true
	Log.isLoggable(TAG, Log.WARN) == true
	Log.isLoggable(TAG, Log.ERROR) == true
	#setprop log.tag.<log_tag> <level>
	*/
	public static void i(String tag, String string) {
		if (LOG)
			android.util.Log.i(tag, string);
	}

	public static void e(String tag, String string) {
		if (LOG)
			android.util.Log.e(tag, string);
	}

	public static void d(String tag, String string) {
		if (LOG)
			android.util.Log.d(tag, string);
	}

	public static void v(String tag, String string) {
		if (LOG)
			android.util.Log.v(tag, string);
	}

	public static void w(String tag, String string) {
		if (LOG)
			android.util.Log.w(tag, string);
	}

	public static void d(String string) {
		if (LOG)
			android.util.Log.d("tag", string);
	}
}
