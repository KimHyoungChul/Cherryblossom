package com.commax.settings.setting_provider;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class GetSetBase {

    protected Context context;

    protected boolean isMysql;

    public GetSetBase(Context context) {
        this.context = context;

        isMysql = new ValueType().getValue();
    }

    /*
     * null: failure.
     */
    protected String getValue(int providerIndex, String getQuery) {

        String provider = new SettingProvider(context).getValue(providerIndex);
        if (provider != null) {

            if (isMysql) {


                String value = new SoapHelper(context).getValue(getQuery);
                if (value != null) {
                    if (value.contains(Symbol.SYMBOL_AMPERSAND)) {
                        final int dong = 1;

                        String[] arr = value.split(Symbol.SYMBOL_AMPERSAND);
                        if (arr.length >= 2) {
                            String mMySqlValue = arr[dong];
                            if (mMySqlValue.equals(provider)) {
                                return provider;
                            }
                        }

                    } else {
                        if (value.equals(provider)) {
                            return provider;
                        }
                    }

                }

            } else {
                return provider;
            }

        }

        return null;

    }

    public void getValueInBackground(final int providerIndex,
                                     final String getQuery, final OnStringCallback l) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... arg0) {

                return getValue(providerIndex, getQuery);
            }

            @Override
            protected void onPostExecute(String result) {
                if (l != null) {
                    l.onResult(result);
                }
            }
        }.execute();
    }

    /*
     * input: ip or url
     */
    protected Boolean setValue(int providerIndex, String setQuert,
                               String value, String broadcastAction) {
        if (value == null) {
            return false;
        }


        if (isMysql) {

            boolean condition = new SoapHelper(context).setValue(String.format(setQuert, value));
            if (condition == false) {
                return false;
            }

        }

        boolean condition = new SettingProvider(context).setValue(providerIndex, value);

        if (condition) {

            sendBroadcast(broadcastAction);

        }
        return condition;
    }

    protected void setValueInBackground(final int providerIndex,
                                        final String setQuert, final String value,
                                        final String broadcastAction, final OnBooleanCallback l) {
        new AsyncTask<Void, Void, Void>() {

            Boolean ret = false;

            @Override
            protected Void doInBackground(Void... params) {

                ret = setValue(providerIndex, setQuert, value, broadcastAction);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {

                l.onResult(ret);

            }

        }.execute();
    }

    protected void sendBroadcast(String action) {

        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }
}
