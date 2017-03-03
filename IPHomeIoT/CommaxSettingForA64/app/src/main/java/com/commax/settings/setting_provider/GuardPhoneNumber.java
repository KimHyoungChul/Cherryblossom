package com.commax.settings.setting_provider;

import android.content.Context;
import android.os.AsyncTask;

public class GuardPhoneNumber {

    private static final int GUARDNUM_PROVIDER_INDEX = 7;

    private Context context;

    public GuardPhoneNumber(Context context) {
        this.context = context;
    }

//	public EntryPair getValuePair() throws FileNotFoundException, IOException {
//
//		String value = getValue();
//		String[] arr = value.split(Symbol.SYMBOL_DASH);
//		if (arr.length != 2) {
//			return null;
//		}
//
//		return new EntryPair(arr[0], arr[1]);
//	}

    public String getValueProvider() {

        return new SettingProvider(context).getValue(GUARDNUM_PROVIDER_INDEX);
    }

    public String getValue() {


        return new SettingProvider(context).getValue(GUARDNUM_PROVIDER_INDEX);


//					if (isMysql) {
//						// mysql
//						String sql = new SoapHelper(context).getValue(MYSQL_GET);
//						if (sql != null) {
//							if (sql.contains("&amp;")) {
//								String[] array = sql.split("&amp;");
//								if (array.length >= 2) {
//									String first = array[0];
//									String second = array[1];
//									if (dong.equals(first) && ho.equals(second)) {
//										ret = dong + "-" + ho;
//									}
//								}
//
//							} else if (sql.contains(Symbol.SYMBOL_AMPERSAND)) {
//
//								String[] array = sql
//										.split(Symbol.SYMBOL_AMPERSAND);
//								if (array.length >= 3) {
//									String first = array[1];
//									String second = array[2];
//
//									if (dong.equals(first) && ho.equals(second)) {
//										ret = dong + "-" + ho;
//									}
//								}
//
//							}
//						}
//
//					}
//					 else {
//						// provider
//						ret = dong + "-" + ho;
//					}


        // provider

    }


    public void getValueInBackground(final OnStringCallback l) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {

                return getValue();
            }

            @Override
            protected void onPostExecute(String result) {
                if (l != null) {
                    l.onResult(result);
                }
            }
        }.execute();
    }


    public boolean setValue(String ip) {
//		if (isMysql) {
//			StringBuffer buf = new StringBuffer();
//			buf.append(MYSQL_SET);
//			buf.append(dong);
//			buf.append("\",\"");
//			buf.append(ho);
//			buf.append("\")");
//
//			boolean condition = new SoapHelper(context)
//					.setValue(buf.toString());
//			if (!condition) {
//				return false;
//			}
//		}

        // provider


        boolean ret = new SettingProvider(context).setValue(GUARDNUM_PROVIDER_INDEX,
                ip);
//        if (ret) {
//            sendBroadcast(ACTION);
//        }

        return ret;

    }

    public void setValueInBackground(final String ip,
                                     final OnBooleanCallback l) {
        new AsyncTask<Void, Void, Void>() {
            boolean value;

            @Override
            protected Void doInBackground(Void... params) {
                value = setValue(ip);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (l != null) {
                    l.onResult(value);
                }
            }

        }.execute();
    }

//    public void sendBroadcast(String action) {
//
//        Intent intent = new Intent(action);
//        context.sendBroadcast(intent);
//    }

}
