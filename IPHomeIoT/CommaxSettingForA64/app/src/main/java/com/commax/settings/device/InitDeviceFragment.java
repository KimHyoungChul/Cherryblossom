package com.commax.settings.device;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.commax.adapter.aidl.IAdapter;
import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.util.CustomProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 제어기기 연결 초기화
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class InitDeviceFragment extends Fragment implements InitDeviceListener {
    private final String LOG_TAG = InitDeviceFragment.class.getSimpleName();
    private final String PACKAGE_PAM_SERVICE = "com.commax.pam.service";
    private final String PACKAGE_PAM_SERVICE_NEW = "com.commax.services.adapter"; //new version
    public static RemoteServiceConnection mRemoteServiceConnection;

    private static final long DELAY_TIME = 15*1000; //15초 타임아웃
    private boolean mIsReceiveDeviceInitBroadcast = false;
    private boolean mIsReceiveCloudInitBroadcast = false;
    private boolean mIsDeviceAddCommandSent = false;

    public InitDeviceFragment() {

    }

    private final BroadcastReceiver mDeviceInitBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
             mIsReceiveDeviceInitBroadcast = true;
            Log.d(LOG_TAG, "mIsReceiveDeviceInitBroadcast = true");

            //클라우스 초기화 브로드캐스트를 받거나 월패드 디바이스 초기화 브로드캐스트를 받은지 15초가 지났는데 아무 브로드캐스트를 받지 못한 경우 재등록 명령 보냄.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!mIsDeviceAddCommandSent) {
                        sendDeviceAddCommand();
                    }
                }
            }, DELAY_TIME);
        }
    };

    private final BroadcastReceiver mCloudInitBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            mIsReceiveCloudInitBroadcast = true;
            Log.d(LOG_TAG, "mIsReceiveCloudInitBroadcast = true");

            //재등록 명령 보냄
            if(mIsReceiveDeviceInitBroadcast) {
                sendDeviceAddCommand();
            }
        }
    };

    /**
     * 재등록 명령 보냄
     */
    private void sendDeviceAddCommand() {
        Log.i(LOG_TAG, "재등록 명령 보냄");

        mIsDeviceAddCommandSent = true;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "add");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String addString = jsonObject.toString();


        if(mRemoteServiceConnection.iadpter != null){
            try {
                mRemoteServiceConnection.iadpter.sendToPAM(addString);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsReceiveDeviceInitBroadcast = false;
        mIsReceiveCloudInitBroadcast = false;
        mIsDeviceAddCommandSent = false;

        getActivity().registerReceiver(mDeviceInitBroadcastReceiver, new IntentFilter("DEVICE_INIT_BROADCAST_RECEIVE"));
        getActivity().registerReceiver(mCloudInitBroadcastReceiver, new IntentFilter("CLOUD_INIT_BROADCAST_RECEIVE"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_init_device, container, false);

    }

    private void addButtonListener() {
        Button init = (Button) getActivity().findViewById(R.id.init);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlusClickGuard.doIt(v);
                //init();

                v.setEnabled(false);
                showDeletePopup(); //2017-01-06,yslee::디바이스 초기화 확인팝업 추가
            }
        });
    }

    // 확인 팝업 표시
    private void showDeletePopup() {
        InitDevicePopup popup = new InitDevicePopup(getActivity(), this);
        popup.show();
    }

    /**
     * 디바이스 초기화 실행
     */
    public class InitDeviceProgress extends AsyncTask<Void, Void, Integer> {

        //private ProgressDialog mProgressDialog;
        private CustomProgressDialog mProgressDialog; //2017-01-18,yslee::CustomProgress로 변경

        public InitDeviceProgress() {

        }

        @Override
        protected void onPreExecute() {
//            mProgressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
//            mProgressDialog.setCancelable(false);

            //2017-01-18,yslee::CustomProgress로 변경
            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.dataInitWaiting));
            mProgressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String macAddress = null;
            int result = 0;

            init();

            for(int i=0;i< 5; i++) { //5초
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(mIsReceiveDeviceInitBroadcast) break;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();

            Toast.makeText(getActivity(), R.string.dataInitResult, Toast.LENGTH_SHORT).show();
            Button init = (Button) getActivity().findViewById(R.id.init);
            init.setEnabled(true);

            super.onPostExecute(result);
        }

    }

    //디바이스 초기화 콜백
    @Override
    public void onInitDevice(boolean bconfirm) {

        //init();
        //Toast.makeText(getActivity(), R.string.dataInitResult, Toast.LENGTH_SHORT).show();

        //2017-01-19,yslee::초기화 후 factoryReport 수신 후 버튼 활성화
        if(bconfirm) {
            InitDeviceFragment.InitDeviceProgress discovery = new InitDeviceFragment.InitDeviceProgress();
            discovery.execute();
        } else {
            Button init = (Button) getActivity().findViewById(R.id.init);
            init.setEnabled(true);
        }

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        if(mRemoteServiceConnection == null){
            mRemoteServiceConnection = new RemoteServiceConnection();
            Intent pamIntent = new Intent(IAdapter.class.getName());
            if(TypeDef.OP_NEW_AIDL_ADAPTOR_ENABLE) {
                pamIntent.setPackage(PACKAGE_PAM_SERVICE_NEW); //new version
            } else {
                pamIntent.setPackage(PACKAGE_PAM_SERVICE);
            }
            boolean is = getActivity().bindService(pamIntent, mRemoteServiceConnection , Activity.BIND_AUTO_CREATE);
            if(is == true) {
                Log.i(LOG_TAG, "[Ventilation Activity]bindService Successed");
            }
        }

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        //2017-01-03,yslee::exception handling
        try {
            if (mRemoteServiceConnection != null) {
                getActivity().unbindService(mRemoteServiceConnection);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        getActivity().unregisterReceiver(mDeviceInitBroadcastReceiver);
        getActivity().unregisterReceiver(mCloudInitBroadcastReceiver);

    }

    //제어기기 연결 초기화
    private void init() {
        handleInitDevice();
        //사용안함
        //sendInitDeviceBroadcast();
    }

    private void handleInitDevice() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("command", "factoryReset");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String resetString = jsonObject.toString();


        if(mRemoteServiceConnection.iadpter != null){
            try {
                mRemoteServiceConnection.iadpter.sendToPAM(resetString);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 제어기기 연결 초기화 브로드캐스트 전송
     */
    private void sendInitDeviceBroadcast() {
        Intent intent = new Intent(CommaxConstants.BROADCAST_INIT_DEVICE);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        addButtonListener();


    }


    /**
     * PAM과 연결
     */
    public class RemoteServiceConnection implements ServiceConnection {

        IAdapter iadpter;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            iadpter = IAdapter.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            iadpter = null;
        }

    }


}
