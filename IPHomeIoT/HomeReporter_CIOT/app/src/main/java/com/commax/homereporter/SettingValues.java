package com.commax.homereporter;

import android.content.Context;

public class SettingValues {
	public static final String SYMBOL_DASH = "-";
	private static final int LOCAL_PROVIDER_INDEX = 1;
	private static final int DONGHO_PROVIDER_INDEX = 9;
	private static final int DONG_INDEX = 0;
	private static final int HO_INDEX = 1;

	private String local;
	private String dong;
	private String ho;

	public SettingValues(Context context) {

		local = new SettingProvider(context).getValue(LOCAL_PROVIDER_INDEX);

		String dongho = new SettingProvider(context)
				.getValue(DONGHO_PROVIDER_INDEX);
		if (dongho != null) {
			if (dongho.length() > 0) {

				if (dongho.contains(SYMBOL_DASH)) {
					String[] array = dongho.split(SYMBOL_DASH);
					dong = array[DONG_INDEX];
					ho = array[HO_INDEX];
				}
			}
		}

	}

	public String getLocalServerIp() {

		return local;

	}

	public String getDong() {

		return dong;

	}

	public String getHo() {

		return ho;

	}
}
