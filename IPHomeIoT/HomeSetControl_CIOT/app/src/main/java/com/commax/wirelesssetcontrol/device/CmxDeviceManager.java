package com.commax.wirelesssetcontrol.device;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.commax.pam.db.interfaces.MySQLConnection;
import com.commax.wirelesssetcontrol.Log;
import com.commax.wirelesssetcontrol.RootDevice;

import java.util.ArrayList;

/**
 * Created by shin on 2017-02-26.
 * 코맥스 디바이스를 관리하는 매니저
 */

public class CmxDeviceManager {
    private static CmxDeviceManager mManager;

    private final String TAG = "CMX_DB_MANAGER";
    public static final int DB_TRY_CNT = 5;

    private Context mContext;
    private CmxDeviceDataParser mCmxParser;

    public static void init(Context context){
        mManager = new CmxDeviceManager(context);
    }

    public static CmxDeviceManager getInst(){
         return mManager;
    }

    //생성자
    public CmxDeviceManager(Context context){
        mContext = context;
        init();
    }

    private void init(){
        mCmxParser = new CmxDeviceDataParser();
        connectDB(DB_TRY_CNT); //접속시도
    }

    /************* private methode *************/
    private void openDB(){
        try {
            if (MySQLConnection.getInstance().getConnectionVariable()!=null)
                MySQLConnection.getInstance().close();
            MySQLConnection.getInstance().getConnection(mContext.getContentResolver());
        }catch (Exception e){
            Log.d(TAG, "my sql open : " + e.getMessage());
        }
    }

    public boolean closeDB(){
        boolean isClose = false;
        try {
            if (MySQLConnection.getInstance() != null) {
                isClose = MySQLConnection.getInstance().close();
                MySQLConnection.getInstance().clearConnectionVariable();
            }
        }catch (Exception e){
            Log.d(TAG, "my sql close : " + e.getMessage());
            MySQLConnection.getInstance().clearConnectionVariable();
        }
        return isClose;
    }

    private boolean isDBConnected(){
        try {
            if (MySQLConnection.getInstance().getConnectionVariable() != null)
                return true;
            else
                return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /************* public method ****************/
    //데이터베이스 접속 시도
    public void connectDB(int cnt){
        if(!isDBConnected() && cnt > 0){
            Log.d(TAG, "Database connection");
            new Thread(new Runnable(){
                @Override
                public void run() {
                    openDB();
                }
            }).start();

            Message msg = new Message();
            msg.what = MSG_DB_CONNECTION;
            msg.arg1 = cnt; //retry cnt;
            mDeviceHandler.sendMessageDelayed(msg, 2000);
        }
        else if(!isDBConnected())
            Log.d(TAG, "Database connection error");
    }

    //컨트롤 가능한 장치 목록 반환
    private CmxDeviceDataReceiveInterface mCallBack = null;
    //제어 디바이스 정보 가져오기
    public void getDevice(final String[] list, final boolean alradyPlacedCheck, CmxDeviceDataReceiveInterface cb){
        mCallBack = cb;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<DeviceInfo> deviceList = new ArrayList<>();
                for(int i=0; i<list.length; i++)
                    deviceList.addAll(mCmxParser.getDeviceInfo(mContext, list[i], alradyPlacedCheck));
                if(mCallBack != null) {
                    mCallBack.getDeviceInfoCallback(deviceList);
                    mCallBack = null;
                }
            }
        }).start();
    }

    //raw데이터 검색에 대한 알고리즘이 개선 되어야 할듯
    public void getMatchDevice(final String raw, CmxDeviceDataReceiveInterface cb){
        mCallBack = cb;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<DeviceInfo> matchList = new ArrayList<>();
                DeviceInfo deviceInfo = mCmxParser.getSwitchStatusByRaw(raw);
                if(deviceInfo != null)
                    matchList.add(deviceInfo);

                deviceInfo = mCmxParser.getThermostatStatusByRaw(raw);
                if(deviceInfo != null)
                    matchList.add(deviceInfo);

//                deviceInfo = mCmxParser.getSensorStatusByRaw(raw);
//                if(deviceInfo != null)
//                    matchList.add(deviceInfo);

                deviceInfo = mCmxParser.getLockStatusByRaw(raw);
                if(deviceInfo != null)
                    matchList.add(deviceInfo);

                if(mCallBack != null) {
                    mCallBack.getDeviceInfoCallback(matchList);
                    mCallBack = null;
                }
            }
        }).start();
    }

    /*** 메시지 핸들링 ***/
    public static final int MSG_DB_CONNECTION = 1;

    private Handler mDeviceHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case MSG_DB_CONNECTION:
                    connectDB(msg.arg1 - 1);
                    break;
            }
        }
    };
}
