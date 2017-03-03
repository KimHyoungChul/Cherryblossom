package com.commax.settings.wallpad;

import android.content.Context;
import android.os.AsyncTask;

import com.commax.settings.setting_provider.OnBooleanCallback;
import com.commax.settings.setting_provider.OnStringCallback;
import com.commax.settings.setting_provider.SettingProvider;

/**
 * 비밀번호 SettingProvider에 저장 처리
 */
public class PasswordHandler {

	public static final int PROVIDER_INDEX = 12;
	private static final String DEFAULT_VALUE = "1234";

	//private static final String MYSQL_GET = "SELECT wc_pass FROM tbl_wp_configuration;";
	// private static final String SET_PASSWORD ="CALL db_house.db_house.proc_password_update(\"";
	//private static final String SET_PASSWORD = "UPDATE db_house.tbl_wp_configuration SET wc_pass = \"";

	private Context context;
	//private boolean isMysql;

	public PasswordHandler(Context context) {
		this.context = context;

		//isMysql = new ValueType().getValue();

	}

	/**
	 * SettingProvider에서 값을 가져옴
	 * @return
     */
	public String getValue() {
		String mProviderValue = new SettingProvider(context).getValue(PROVIDER_INDEX);
		return mProviderValue;

	}

	/**
	 * SettingProvider에서 모든 값을 가져옴
	 * @return
     */
	public String getAllValue() {

		String mProviderValue = new SettingProvider(context).getValue(PROVIDER_INDEX);

		if (mProviderValue != null) {

//			if (isMysql) {
//
//				String value = new SoapHelper(context).getValue(MYSQL_GET);
//				if (value != null) {
//					if (value.contains(Symbol.SYMBOL_AMPERSAND)) {
//
//						String[] arr = value.split(Symbol.SYMBOL_AMPERSAND);
//						if (arr.length >= 2) {
//							String mMySqlValue = arr[1];
//
//							if (mProviderValue.equals(mMySqlValue)) {
//								return mProviderValue;
//							} else {
//								// return default value
//
//							}
//						}
//
//					} else {
//						if (mProviderValue.equals(value)) {
//							return mProviderValue;
//						}
//					}
//				}
//
//			} else {
//				return mProviderValue;
//			}

			return mProviderValue;
		}
		return null;

	}

	/**
	 * SettingProvider에서 모든 값을 백그라운드로 가져옴
	 * @param l
     */
	public void getAllValueInBackground(final OnStringCallback l) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... arg0) {
				//SystemClock.sleep(200);

				return getAllValue();
			}

			@Override
			protected void onPostExecute(String result) {
				if (l != null) {
					l.onResult(result);
				}

			}

		}.execute();
	}

	//
	// public void getValueBackground(final OnStringCallback l) {
	//
	// new AsyncTask<Void, Void, Void>() {
	// String ret;
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	//
	// ret = getValue();
	//
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	//
	// if (l != null) {
	// l.onResult(ret);
	// }
	//
	// }
	//
	// }.execute();
	// }

	/**
	 * SettingProvider에 값을 저장
	 * @param value
	 * @return
     */
	public Boolean setValue(String value) {
		if (value == null) {
			return false;
		}

//		if (isMysql) {
//
//			StringBuffer buf = new StringBuffer();
//			buf.append(SET_PASSWORD);
//			buf.append(value);
//			buf.append("\";");
//
//			boolean condition = new SoapHelper(context)
//					.setValue(buf.toString());
//			if (condition == false) {
//				return false;
//			}
//		}

		return new SettingProvider(context).setValue(PROVIDER_INDEX, value);

	}

	/**
	 * SettingProvider에 값을 백그라운드로 저장
	 * @param value 값
	 * @param l 콜백
     */
	public void setValueInBackground(final String value, final OnBooleanCallback l) {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... arg0) {
				// SystemClock.sleep(200);

				return setValue(value);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (l != null) {
					l.onResult(result);
				}

			}

		}.execute();
	}

	/**
	 * SettingProvider에 값을 재설정
	 * @return
     */
	public boolean reset() {
		String value = DEFAULT_VALUE;
		boolean condition = setValue(value);
		if (!condition) {

			return new SettingProvider(context).setValue(PROVIDER_INDEX, value);
		}
		return condition;
	}

}
