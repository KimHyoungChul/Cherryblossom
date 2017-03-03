package com.commax.commaxwidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.commax.commaxwidget.notifycation.SettingsNotificationHelper;
import com.commax.commaxwidget.util.Constants;
import com.commax.commaxwidget.wallpad.InfoLoader;
import com.commax.commaxwidget.wallpad.constans.HandlerEvent;
import com.commax.commaxwidget.wallpad.data.DeviceInfo;
import com.commax.commaxwidget.wallpad.data.SubDevice;
import com.commax.commaxwidget.wallpad.data.SubSort;
import com.commax.commaxwidget.wallpad.data.WeatherInfo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by OWNER on 2017-02-10.
 */
public class MainService extends Service {
    private final static int LOTATION_DELAY_TIME = 5000;
    private final static int REFRESH_DELAY_TIME = 3600000;

    private InfoLoader mInfoLoader;
    private Thread mThread;

    private TimerTask mLotationTask = null;
    private Timer mLotationTimer = null;
    private TimerTask mRefreshTask = null;
    private Timer mRefreshTimer = null;

    private ArrayList<DeviceInfo> mSmartPlugArrayList = null;
    private ArrayList<DeviceInfo> mSensorList = null;
    private WeatherInfo mWeatherData = null;

    private int mCurrentDetectSensor = 0;
    private int mCurrentSmartPlug = 0;
    private String mCloudState = "false";

    private SettingsNotificationHelper mNotificationHelper = null;

    private Toast mToast = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onInit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setServiceIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLotationTimer.cancel();
        mRefreshTimer.cancel();
        unregisterReceiver(mBroadcastReceiver);
        mNotificationHelper.hide();
    }

    private void onInit() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_UPDATE_SMART_PLUG_SERVICE);
        filter.addAction(Constants.ACTION_UPDATE_SMART_PLUG_REFRESH);
        filter.addAction(Constants.ACTION_UPDATE_DETECT_SENSOR_REFRESH);
        filter.addAction(Constants.ACTION_UPDATE_WEATHER_REFRESH);
        filter.addAction(Constants.ACTION_UPDATE_DEVICE_PAM_ACTION);
        filter.addAction(Constants.ACTION_UPDATE_DEVICE_NICKNAME);
        filter.addAction(Constants.ACTION_UPDATE_DEVICE_CONTROL_NICKNAME);
        filter.addAction(Constants.ACTION_UPDATE_DETECT_SENSOR_SERVICE);
        filter.addAction(Constants.ACTION_UPDATE_WEATHER_SERVICE);
        filter.addAction(Constants.ACTION_LOTATION_WIDGET_SERVICE);
        filter.addAction(Constants.ACTION_CONNECTIVITY_CHANGE);
        filter.addAction(Constants.ACTION_WIFE_STATE_CHANGED);
        filter.addAction(Constants.ACTION_CLOUD_STATE_CHANGED);
        filter.addAction(Constants.ACTION_CHANGED_DOOR_CAMERA_STATE);
        filter.addAction(Constants.ACTION_CHANGED_CREATE_ACCOUNT);
        filter.addAction(Constants.ACTION_CLICK_EXTERNAL_NETWORK);
        registerReceiver(mBroadcastReceiver, filter);

        initData();
        initNotfication();
    }

    private void initData() {
        mInfoLoader = new InfoLoader(getApplicationContext(), mHandler);   //Draw view
        mThread = new Thread(mInfoLoader);
        mThread.start();

        mWeatherData = new WeatherInfo();

        mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        setLotationAlarm();
        setRefreshAlarm();
    }

    /***********************
     * Set Notification State
     ********************/
    private void initNotfication() {
        mNotificationHelper = new SettingsNotificationHelper(this);
        getConnectivityStatus();
    }

    public void getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                mNotificationHelper.setWifieStateNoty(Constants.TYPE_WIFI);
                return;
            }

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                mNotificationHelper.setWifieStateNoty(Constants.TYPE_NOT_CONNECTED);
                return;
            }
        }
        mNotificationHelper.setWifieStateNoty(Constants.TYPE_NOT_CONNECTED);
        mNotificationHelper.putCanShow();
    }

    private void setCloudState(String state) {
        mNotificationHelper.setCloudStateNoty(state);
        mNotificationHelper.putCanShow();
    }

    private void setDoorCameraState(String state) {
        mNotificationHelper.setDoorCameraStateNoty(state);
        mNotificationHelper.putCanShow();
    }

    private void setAccountState(String state) {
        mNotificationHelper.setAccountStateNoty(state);
        mNotificationHelper.putCanShow();
    }

    private void setLotationAlarm() {
        mLotationTask = new TimerTask() {
            @Override
            public void run() {
                drawNextDetectSensor();
                drawNextSmartPlug();
            }
        };
        mLotationTimer = new Timer();
        mLotationTimer.schedule(mLotationTask, LOTATION_DELAY_TIME, LOTATION_DELAY_TIME);
    }

    private void setRefreshAlarm() {
        mRefreshTask = new TimerTask() {
            @Override
            public void run() {
                redrawInfo();
            }
        };
        mRefreshTimer = new Timer();
        mRefreshTimer.schedule(mRefreshTask, REFRESH_DELAY_TIME, REFRESH_DELAY_TIME);
    }

    /***********************
     * Set WeatherData
     ********************/
    private void setWeatherData(WeatherInfo weatherData) {
        Intent intent = new Intent(Constants.ACTION_UPDATE_WEATHER_WIDGET);
        intent.putExtra(Constants.EXTRA_WEATHER_TEMP, weatherData.arg1);
        intent.putExtra(Constants.EXTRA_WEATHER_CODE, weatherData.arg2);
        intent.putExtra(Constants.EXTRA_WEATHER_HUMID, weatherData.arg3);
        intent.putExtra(Constants.EXTRA_WEATHER_LOCATION, weatherData.arg4);
        sendBroadcast(intent);
    }

    /***********************
     * Set SmartPlugData
     ********************/
    private void setSmartPlugData() {
        if (mSmartPlugArrayList == null || mSmartPlugArrayList.size() == 0) {
            Intent intent = new Intent(Constants.ACTION_UPDATE_NO_SMART_PLUG_WIDGET);
            sendBroadcast(intent);
        } else {
            DeviceInfo deviceInfo = mSmartPlugArrayList.get(mCurrentSmartPlug);
            Intent intent = new Intent(Constants.ACTION_UPDATE_SMART_PLUG_WIDGET);
            intent.putExtra(Constants.EXTRA_SMART_PLUG_DEVICES, deviceInfo);
            intent.putExtra(Constants.EXTRA_SMART_PLUG_DEVICES_ELECTRIC_METER, drawSmartPlugView(deviceInfo));
            intent.putExtra(Constants.EXTRA_SMART_PLUG_DEVICES_SWITCH_BINARY, drawSmartPlugOnOff(deviceInfo));
            sendBroadcast(intent);
        }
    }

    private SubDevice drawSmartPlugView(DeviceInfo deviceInfo) {
        SubDevice electric_meter = new SubDevice();
        try {
            for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.ELECTRIC_METER)) {
                    electric_meter = deviceInfo.subDevices.get(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return electric_meter;
    }

    private SubDevice drawSmartPlugOnOff(DeviceInfo deviceInfo) {
        SubDevice switchBinary = new SubDevice();
        try {
            for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.SWITCH_BINARY)) {
                    switchBinary = deviceInfo.subDevices.get(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return switchBinary;
    }

    private void drawNextSmartPlug() {
        Log.v("junhwe", "junhwe smarplug next  " + mCurrentSmartPlug);
        if (mSmartPlugArrayList == null || mSmartPlugArrayList.size() == 0) {
            return;
        }
        mCurrentSmartPlug = ((mCurrentSmartPlug + 1) >= mSmartPlugArrayList.size()) ? 0 : (mCurrentSmartPlug + 1);
        setSmartPlugData();
    }

    /************************
     * SetDetectSensor Data*
     ***********************/
    private void setDetectSensorData() {
        if (mSensorList == null || mSensorList.size() == 0) {
            Intent intent = new Intent(Constants.ACTION_UPDATE_NO_DETECT_SENSOR_WIDGET);
            sendBroadcast(intent);
        } else {
            DeviceInfo deviceInfo = mSensorList.get(mCurrentDetectSensor);
            Intent intent = new Intent(Constants.ACTION_UPDATE_DETECT_SENSOR_WIDGET);
            intent.putExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR, deviceInfo);
            intent.putExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR_TEMP, drawTempData(deviceInfo));
            intent.putExtra(Constants.EXTRA_CURRENT_DETECT_SENSOR_HUMIDITY, drawHumidityView(deviceInfo));
            sendBroadcast(intent);
        }
    }

    private SubDevice drawTempData(DeviceInfo deviceInfo) {
        SubDevice airTemperature = new SubDevice();
        try {
            for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.AIR_TEMPERATURE)) {
                    airTemperature = deviceInfo.subDevices.get(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return airTemperature;
    }

    private SubDevice drawHumidityView(DeviceInfo deviceInfo) {
        SubDevice humidity = new SubDevice();
        try {
            for (int i = 0; i < deviceInfo.subDevices.size(); i++) {
                if (deviceInfo.subDevices.get(i).sort.equalsIgnoreCase(SubSort.HUMIDITY)) {
                    humidity = deviceInfo.subDevices.get(i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return humidity;
    }

    private void drawNextDetectSensor() {
        if (mSensorList == null || mSensorList.size() == 0) {
            return;
        }

        Log.v("junhwe", "junhwe Receiver " + mCurrentDetectSensor);
        mCurrentDetectSensor = ((mCurrentDetectSensor + 1) >= mSensorList.size()) ? 0 : (mCurrentDetectSensor + 1);
        setDetectSensorData();
    }

    /***********************
     * RedrawInfo
     ********************/
    private void redrawInfo() {
        try {
            if (mInfoLoader.end_thread_running) {
                interruptThread();
                Log.v("junhwe", "junhwe Refreshtool ");
                mInfoLoader = new InfoLoader(getApplicationContext(), mHandler);   //Draw view
                mThread = new Thread(mInfoLoader);
                mThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void interruptThread() {
        try {
            if (mThread != null) {
                if (mThread.isAlive()) {
                    mThread.interrupt();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //External network state
    private void setServiceIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (action == null) {
            return;
        }

        if (action.equals(Constants.ACTION_CLICK_EXTERNAL_NETWORK)) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);
            setToastPopup(getResources().getString(R.string.external_network_error));
        }
    }

    private void setToastPopup(CharSequence text) {
        if (mToast != null) {
            mToast.setText(text);
            mToast.show();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v("junhwe", "junhwe Receiver " + action);
            if (action.equals(Constants.ACTION_UPDATE_SMART_PLUG_SERVICE)) {
                mCurrentSmartPlug = 0;
                setSmartPlugData();
            } else if (action.equals(Constants.ACTION_UPDATE_DETECT_SENSOR_SERVICE)) {
                mCurrentDetectSensor = 0;
                setDetectSensorData();
            } else if (action.equals(Constants.ACTION_UPDATE_DEVICE_PAM_ACTION) || action.equals(Constants.ACTION_UPDATE_SMART_PLUG_REFRESH) || action.equals(Constants.ACTION_UPDATE_DETECT_SENSOR_REFRESH) || action.equals(Constants.ACTION_UPDATE_WEATHER_REFRESH) || action.equals(Constants.ACTION_UPDATE_DEVICE_NICKNAME) || action.equals(Constants.ACTION_UPDATE_DEVICE_CONTROL_NICKNAME)) {
                redrawInfo();
            } else if (action.equals(Constants.ACTION_UPDATE_WEATHER_SERVICE)) {
                setWeatherData(mWeatherData);
            } else if (action.equals(Constants.ACTION_CONNECTIVITY_CHANGE) || action.equals(Constants.ACTION_WIFE_STATE_CHANGED)) {
                getConnectivityStatus();
            } else if (action.equals(Constants.ACTION_CLOUD_STATE_CHANGED)) {
                mCloudState = intent.getStringExtra(Constants.EXTRA_CLOUD_STAE);
                setCloudState(mCloudState);
            } else if (action.equals(Constants.ACTION_CHANGED_DOOR_CAMERA_STATE)) {
                String state = intent.getStringExtra(Constants.EXTRA_DOOR_CAMERA_STATE);
                setDoorCameraState(state);
            } else if (action.equals(Constants.ACTION_CHANGED_CREATE_ACCOUNT)) {
                String state = intent.getStringExtra(Constants.EXTRA_CREATE_ACCOUNT_STATE);
                setAccountState(state);
            }
        }
    };

    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case HandlerEvent.EVENT_HANDLE_ADD_INFO_VIEW:
                        WeatherInfo weatherData = (WeatherInfo) msg.obj;
                        Log.v("junhwe", "junhwe weather========== " + weatherData.arg1 + " " + weatherData.arg2);
                        mWeatherData.arg1 = weatherData.arg1;
                        mWeatherData.arg2 = weatherData.arg2;
                        mWeatherData.arg3 = weatherData.arg3;
                        mWeatherData.arg4 = weatherData.arg4;
                        setWeatherData(mWeatherData);
                        break;

                    case HandlerEvent.EVENT_HANDLE_FILL_VIEW:
                        break;

                    case HandlerEvent.EVENT_HANDLE_REMOVE_INFO_VIEW:
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_PROGRESS:
                        break;

                    case HandlerEvent.EVENT_HANDLE_HIDE_PROGRESS:
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_WEATHER:
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_AIR:
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_HEALTH_LIFE:
                        break;

                    case HandlerEvent.EVENT_HANDLE_SHOW_NETWORK_ERR:
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_DETECT_SENSOR:
                        mSensorList = (ArrayList<DeviceInfo>) msg.obj;

                        if (mSensorList == null) {
                            mSensorList = new ArrayList<DeviceInfo>();
                        }

                        Log.v("junhwe", "junhwe sencorcount " + mSensorList.size());
                        setDetectSensorData();
                        break;

                    case HandlerEvent.EVENT_HANDLE_UPDATE_SMART_PLUG:
                        mSmartPlugArrayList = (ArrayList<DeviceInfo>) msg.obj;

                        if (mSmartPlugArrayList == null) {
                            mSmartPlugArrayList = new ArrayList<DeviceInfo>();
                        }
                        mCurrentSmartPlug = 0;
                        setSmartPlugData();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
