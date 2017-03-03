package com.commax.settings.cctv;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.onvif.NetworkOnvifRequester;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.onvif.OnvifProbe;
import com.commax.settings.util.CustomProgressDialog;
import com.commax.settings.util.PlusClickGuard;

import org.onvif.ver10.schema.nativeParcel.ProbeMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * 도어폰 카메라 등록 액티비티
 */
public class CctvRegistrationActivity extends CommonActivity implements CctvIdPasswordConfirmListener {
    private static final String LOG_TAG = CctvRegistrationActivity.class.getSimpleName();
    private static final int REQUEST_CCTV_PREVIEW = 234;
    private OnvifDevice mSelectedOnvifDevice;
    ArrayList<String> mIps;

    private static final String CCTV_ID = "admin";
    private static final String CCTV_PASSWORD = "123456";

    private static final String DOOR_CAMERA_MODEL_NAME = "ONVIF_CAMERA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_registration);
        setFullScreen();
        initIps();
        initListView();
        findDevice();
    }

     private void initIps() {
        mIps = new ArrayList<>();
    }

    /**
     * ListView 초기화
     */
    private void initListView() {
        List<OnvifDevice> devices = new ArrayList<OnvifDevice>();
        final ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
        OnvifUnregisteredCctvListAdapter adapter = new OnvifUnregisteredCctvListAdapter(this, R.layout.list_item_unregistered_cctv2, devices);
        adapter.setListener(this);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlusClickGuard.doIt(view);
                //multiChoice 모드일때에는 같은 아이템을 누르면 취소됨

                if (getCheckedDevices().size() > 0) {
                    showSaveRegistrationButton();
                } else {
                    hideSaveRegistrationButton();
                }
            }
        });
    }

    /**
     * 디바이스 찾기
     */
    private void findDevice() {

        NetworkOnvifDiscovery discovery = new NetworkOnvifDiscovery();
        discovery.execute();
    }


    /**
     * WS-Discovery 실행
     */
    public class NetworkOnvifDiscovery extends AsyncTask<Void, Void, Integer> {

        //private ProgressDialog mProgressDialog;
        private CustomProgressDialog mProgressDialog; //2017-01-18,yslee::CustomProgress로 변경

        public NetworkOnvifDiscovery() {

        }

        @Override
        protected void onPreExecute() {
//            mProgressDialog = new ProgressDialog(DoorphoneCameraRegistrationActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
//            mProgressDialog.setCancelable(false);

            //2017-01-18,yslee::CustomProgress로 변경
            mProgressDialog = new CustomProgressDialog(CctvRegistrationActivity.this);
            mProgressDialog.setMessage(getString(R.string.scanning_camera));
            mProgressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String macAddress = null;
            int result = NetworkOnvifRequester.ERROR_UNKNOWN;
            //마지막 파라미터를 null로 주면 모든 IP에 대해 스캔
            OnvifProbe probe = new OnvifProbe(CctvRegistrationActivity.this, null); //mIPAddress);
            ArrayList<ProbeMatch> probeMatch = probe.sendProbeMessage();
            if (probeMatch != null && probeMatch.size() > 0) {

                result = NetworkOnvifRequester.SUCCESS;

                try {
                    for (int i = 0; i < probeMatch.size(); i++) {

                        //테스트용으로 모델명이 "ONVIF_CAMERA"인 것(도어폰 카메라) 제외
                        //CCTV의 모델명은 ""
                        //테스트후 주석 푸세요!!
                        if (probeMatch.get(i).mOnvifVendorModel != null && probeMatch.get(i).mOnvifVendorModel.equals(DOOR_CAMERA_MODEL_NAME)) {
                            continue;
                        }


                        //해당 디바이스가 이미 스캔된 경우 pass
                        if (isOnvifCctvIpExistOnContentProvider(probeMatch.get(i).mOnvifIPAddress)) {
                            continue;
                        }

                        //해당 디바이스가 이미 스캔된 경우 pass
                        if (mIps.contains(probeMatch.get(i).mOnvifIPAddress)) {
                            continue;
                        }


                        mIps.add(probeMatch.get(i).mOnvifIPAddress);

                        //스트리밍 url가져옴
                        OnvifDevice onvifDevice = new OnvifDevice();
                        onvifDevice.setIpAddress(probeMatch.get(i).mOnvifIPAddress);
                        onvifDevice.setName(getString(R.string.found_cctv) + " " + mIps.size());
                        onvifDevice.setNickName(getString(R.string.unregistered_cctv) + " " + mIps.size()); //2017-01-06,yslee::닉네임 항목 추가
                        onvifDevice.setPort(String.valueOf(probeMatch.get(i).mOnvifPort));
                        //id와 password 고정시킴

                        //id와 password를 입력하는 것으로 변경
//                   onvifDevice.setId(CCTV_ID);
//                   onvifDevice.setPassword(CCTV_PASSWORD);

                        //스트리밍 url을 아이디와 비밀번호 입력한 다음 가져오는 것으로 변경
//                    String streamUrl = getStreamUrl(onvifDevice);
//                    onvifDevice.setStreamUrl(streamUrl);

                        showDeviceOnList(onvifDevice);

                    }
                } catch (Exception e) {
                }
            }
            return result;
        }


        @Override
        protected void onPostExecute(Integer result) {


            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            //2017-01-09,yslee::검색 후 검색 디바이스가 없을경우 토스트 메세지
            final ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
            ArrayList<OnvifDevice> devices = ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).getDevices();
            if (devices.size() == 0) {
                Toast.makeText(CctvRegistrationActivity.this, R.string.cctv_camera_not_found, Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }
    }


    /**
     * 스트리밍 url  가져옴
     *
     * @param deviceInfo
     * @return
     */
    private String getStreamUrl(OnvifDevice deviceInfo) {

        NetworkOnvifRequester requester = new NetworkOnvifRequester(deviceInfo.getIpAddress(), Integer.parseInt(deviceInfo.getPort()), deviceInfo.getId(), deviceInfo.getPassword());
        int result = requester.createDeviceManagementAuthHeader();
        if (result == NetworkOnvifRequester.ERROR_SOCKET_TIMEOUT) {
            return null;
        }

        if ((result = requester.createMediaManagementAuthHeader()) > -1) {
            if ((result = requester.GetProfiles()) > -1) {
                int profileSize = requester.mGetProfilesResponse.mProfiles.size();
                if (profileSize > 0) {
                    for (int j = 0; j < profileSize; j++) {
                        if ((result = requester.GetStreamUri(requester.mGetProfilesResponse.mProfiles.get(j).mToken)) > -1) {
                            try {
                                requester.mGetStreamUriResponses.get(j).mMediaUri.mUri = requester.mGetStreamUriResponses.get(j).mMediaUri.mUri
                                        .replace("127.0.0.1", deviceInfo.getIpAddress());

                                return requester.mGetStreamUriResponses.get(j).mMediaUri.mUri;
                            } catch (Exception e) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * 리스트에 Device 추가
     *
     * @param device
     */
    private void showDeviceOnList(final OnvifDevice device) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
                ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).addDevice(device);

            }
        });
    }

    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllOnvifCctv(this);
    }

    /**
     * 2017-01-09,yslee::등록할 CCTV카메라 이름생성
     *
     * @return
     */
    private String getRegisterCctvName() {

        //등록할 CCTV카메라 이름생성(카운터 증가)
        List<OnvifDevice> regdevices = getRegisteredDevice();
        int devicecount = regdevices.size();
        int deviceindex = 1;
        String camera_name = getString(R.string.found_cctv) + " " +  deviceindex;

        //이름 중복체크
        for (int i = 0; i < devicecount; i++) {
            OnvifDevice deviceInfo = regdevices.get(i);
            String temp_name = deviceInfo.getName();
            if((temp_name!=null) && temp_name.equalsIgnoreCase(camera_name)) {
                deviceindex++;
                camera_name = getString(R.string.found_cctv) + " " +  deviceindex;
            }
        }

        return camera_name;
    }

    /**
     * CCTV 카메라의 IP가 content provider에 이미 저장되었는지 체크
     *
     * @param ip
     * @return
     */
    private boolean isOnvifCctvIpExistOnContentProvider(String ip) {


        return ContentProviderManager.isOnvifCctvIpExistOnContentProvider(CctvRegistrationActivity.this, ip);

    }

    @Override
    public void onCctvIdPasswordConfirm(OnvifDevice device) {
        //스트리밍 url 가져옴
        String streamUrl = getStreamUrl(device);
        device.setStreamUrl(streamUrl);

        //2017-01-19,yslee::CCTV 카메라 미리보기 오류 처리
        if(streamUrl == null) {
            Toast.makeText(CctvRegistrationActivity.this, R.string.preview_cctv_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //CCTV 미리보기 앱 실행
        runPreview(device);

        mSelectedOnvifDevice  = device;
    }

    /**
     * 미리보기
     *
     * @param device
     */
    private void runPreview(OnvifDevice device) {
        //CCTV 앱 실행
        Intent intent = new Intent();
        intent.setClassName(CommaxConstants.PACKAGE_CCTV, CommaxConstants.ACTIVITY_CCTV);
        intent.putExtra(CommaxConstants.KEY_IP, device.getIpAddress());
        intent.putExtra(CommaxConstants.KEY_PORT, device.getPort());
        intent.putExtra(CommaxConstants.KEY_ID, device.getId());
        intent.putExtra(CommaxConstants.KEY_PASSWORD, device.getPassword());
        intent.putExtra(CommaxConstants.KEY_RTSP_URL, device.getStreamUrl());
        //Log.d(LOG_TAG,"runPreview: " + device.getId() + "  " + device.getPassword() + "  " + device.getStreamUrl());

        if (isAvailable(intent)) {
            startActivityForResult(intent, REQUEST_CCTV_PREVIEW);
        } else {
            Toast.makeText(CctvRegistrationActivity.this, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 미리보기 테스트
     *
     * @param device
     */
    private void runPreviewTest(OnvifDevice device) {
        //CCTV 앱 실행
        Intent intent = new Intent();
        intent.setClassName("com.commax.fakecctvsetting", "com.commax.fakecctvsetting.MainActivity");
        intent.putExtra(CommaxConstants.KEY_IP, device.getIpAddress());
        intent.putExtra(CommaxConstants.KEY_PORT, device.getPort());
        intent.putExtra(CommaxConstants.KEY_ID, device.getId());
        intent.putExtra(CommaxConstants.KEY_PASSWORD, device.getPassword());
        intent.putExtra(CommaxConstants.KEY_RTSP_URL, device.getStreamUrl());

        if (isAvailable(intent)) {
            startActivityForResult(intent, REQUEST_CCTV_PREVIEW);
        } else {
            Toast.makeText(CctvRegistrationActivity.this, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //CCTV 미리보기 등록 버튼을 클릭한 경우
        if(requestCode == REQUEST_CCTV_PREVIEW) {
            if(data == null) return; //2017-01-03,yslee::null exception handling
            if(data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED) != null && data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED).equals(CommaxConstants.TRUE)) {

                saveDeviceInfoToContentProvider(mSelectedOnvifDevice);
                //미등록된 CCTV 리스트에서 제거
                final ListView unregisteredDeviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
                ((OnvifUnregisteredCctvListAdapter) unregisteredDeviceList.getAdapter()).deleteDevice(mSelectedOnvifDevice);
            }

        }
    }

    /**
     * 앱이 실행 가능한지 체크
     *
     * @param intent
     * @return
     */
    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 저장 완료 버튼 숨김
     */
    private void hideSaveRegistrationButton() {
        Button saveRegistration = (Button) findViewById(R.id.saveRegistration);
        saveRegistration.setVisibility(View.GONE);
        saveRegistration.setOnClickListener(null);
    }

    /**
     * 저장 완료 버튼 표시
     */
    private void showSaveRegistrationButton() {

        //CCTV에서는 미리보기후 등록 가능함
        /*
        final Button saveRegistration = (Button) findViewById(R.id.saveRegistration);
        saveRegistration.setVisibility(View.VISIBLE);
        saveRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSaveRegistration();
            }
        });
        */
    }

    /**
     * 저장 완료 버튼 표시여부
     */
    public void selectSaveRegistrationButton() {

        //2017-01-09,yslee::add후 save버튼 활성 문제
        final ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).getDevices();
        if (devices.size() == 0) {
            hideSaveRegistrationButton();
        }
    }

    /**
     * 카메라 등록 저장
     */
    private void performSaveRegistration() {
        if (getCheckedDevices().size() == 0) {
            Toast.makeText(CctvRegistrationActivity.this, R.string.choose_camera, Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(CctvRegistrationActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();

        //컨텐트 프로바이더에 저장
        ArrayList<OnvifDevice> devices = getCheckedDevices();

        for (OnvifDevice device : devices) {
            saveDeviceInfoToContentProvider(device);
        }

        //등록한 카메라 리스트에서 삭제
        doRemoveCheckedDevices();
        selectSaveRegistrationButton();
    }

    /**
     * 디바이스 정보를 Content Provider에 저장
     *
     * @param deviceInfo
     */
    private void saveDeviceInfoToContentProvider(OnvifDevice deviceInfo) {

        deviceInfo.setName(getRegisterCctvName()); //2017-01-09,yslee::등록할 도어카메라 이름변경 지정

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP, deviceInfo.getIpAddress());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_ID, deviceInfo.getId());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_PASSWORD, deviceInfo.getPassword());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_DEVICE_NAME, deviceInfo.getName());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_STREAM_URL, deviceInfo.getStreamUrl());


        ContentProviderManager.saveOnvifCctv(CctvRegistrationActivity.this, contentValues);

    }

    /**
     * 선택한 카메라 가져오기
     *
     * @return
     */
    private ArrayList<OnvifDevice> getCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).getDevices();

        ArrayList<OnvifDevice> selectedDevices = new ArrayList<>();

        SparseBooleanArray checked = deviceList.getCheckedItemPositions();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value) {
                selectedDevices.add(devices.get(key));
            }
        }

        return selectedDevices;
    }

    /**
     * 선택한 카메라 가져온 후 리스트에서 삭제
     *
     * @return
     */
    private ArrayList<OnvifDevice> doRemoveCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.unregisteredCctvCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).getDevices();

        ArrayList<OnvifDevice> selectedDevices = new ArrayList<>();
        SparseBooleanArray checked = deviceList.getCheckedItemPositions();
        ArrayList<Integer> regkeys= new ArrayList<Integer>();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            if (value) {
                regkeys.add(key);
            }
        }

        //2017-01-06,yslee::리스트에서 삭제함
        deviceList.clearChoices();
        size = regkeys.size();
        if(size > 0) {
            for (int i = size-1; i >= 0; i--) {
                int key = regkeys.get(i);
                ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).delDevice(key);
            }
        }

        return selectedDevices;
    }

    /**
     * 액티비티 종료
     *
     * @param view
     */
    public void closeActivity(View view) {
        finish();
    }


}
