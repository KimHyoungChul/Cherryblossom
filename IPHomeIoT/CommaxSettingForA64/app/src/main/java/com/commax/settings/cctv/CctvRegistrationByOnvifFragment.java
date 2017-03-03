package com.commax.settings.cctv;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
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
 * Onvif로 CCTV 등록
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class CctvRegistrationByOnvifFragment extends Fragment implements CctvIdPasswordConfirmListener{
    private static final String LOG_TAG = CctvRegistrationByOnvifFragment.class.getSimpleName();
    private static final int REQUEST_CCTV_PREVIEW = 234;
    private OnvifDevice mSelectedOnvifDevice;
    ArrayList<String> mIps;

    private static final String CCTV_ID = "admin";
    private static final String CCTV_PASSWORD = "123456";

    private static final String DOOR_CAMERA_MODEL_NAME = "ONVIF_CAMERA";
    private int mRegisteredCctvCount;
    private int mOldRegisteredCctvCount;

    public CctvRegistrationByOnvifFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cctv_registration, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initIps();
        initUnregisteredCctvListView();
        addButtonListener();
        findDevice();


        //초기 등록된 디바이스 수 저장
        List<OnvifDevice> devices = getRegisteredDevice();
        mOldRegisteredCctvCount = devices.size();
    }

    @Override
    public void onResume() {
        super.onResume();

        initRegisteredCctvListView();

        //2017-01-03,yslee::add후 edit버튼 비활성 문제
        List<OnvifDevice> devices = getRegisteredDevice();

        if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리
            Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
            Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
            if (devices == null || devices.size() == 0) {
                editBtn.setVisibility(View.INVISIBLE);
                deleteBtn.setVisibility(View.INVISIBLE);
            } else {
                editBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }

        } else {
            Button edit = (Button) getActivity().findViewById(R.id.edit);
            if (devices == null || devices.size() == 0) {
                edit.setEnabled(false);
            } else {
                edit.setEnabled(true);
            }
        }

        //2017-01-03,yslee::만약, 사용자에의해 등록 해지된 디바이스가 있으면 다시 스캔
        if(mOldRegisteredCctvCount > mRegisteredCctvCount) {
            mIps.clear();
            findDevice();
        }
        mOldRegisteredCctvCount = mRegisteredCctvCount;

    }

    /**
     * 등록된 CCTV ListView 초기화
     */
    private void initRegisteredCctvListView() {
        //원래 코드
        //테스트후 주석 푸세요!!
        List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<OnvifDevice> devices = getRegisteredDeviceTest();

        mRegisteredCctvCount = devices.size();


        final ListView deviceList = (ListView) getActivity().findViewById(R.id.registeredCctvList);
        OnvifRegisteredCctvListAdapter adapter = new OnvifRegisteredCctvListAdapter(getActivity(), R.layout.list_item_registered_cctv, devices);
        deviceList.setAdapter(adapter);
    }

    private List<OnvifDevice> getRegisteredDeviceTest() {

        List<OnvifDevice> devices = new ArrayList<>();

        OnvifDevice device1 = new OnvifDevice();
        device1.setIpAddress("192.168.1.11");
        device1.setPort("80");
        device1.setName("CCTV 1");
        device1.setStreamUrl("rtsp://test/test");
        device1.setId("admin");
        device1.setPassword("123456");

        OnvifDevice device2 = new OnvifDevice();
        device2.setIpAddress("192.168.1.20");
        device2.setPort("80");
        device2.setName("CCTV 2");
        device2.setStreamUrl("rtsp://test/test2");
        device2.setId("admin");
        device2.setPassword("123456");

        devices.add(device1);
        devices.add(device2);


        return devices;
    }


    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllOnvifCctv(getActivity());
    }

    /**
     * 디바이스 찾기
     */
    private void findDevice() {

        NetworkOnvifDiscovery discovery = new NetworkOnvifDiscovery();
        discovery.execute();
    }

    @Override
    public void onCctvIdPasswordConfirm(OnvifDevice device) {
        //스트리밍 url 가져옴
        String streamUrl = getStreamUrl(device);
        device.setStreamUrl(streamUrl);

        //2017-01-19,yslee::CCTV 카메라 미리보기 오류 처리
        if(streamUrl == null) {
            Toast.makeText(getActivity(), R.string.preview_cctv_error, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
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
               refreshLists();
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
        final PackageManager mgr = getActivity().getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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
//            mProgressDialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_HOLO_LIGHT);
//            mProgressDialog.setCancelable(false);
            //2017-01-18,yslee::CustomProgress로 변경
            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.scanning_camera));
            mProgressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String macAddress = null;
            int result = NetworkOnvifRequester.ERROR_UNKNOWN;
            //마지막 파라미터를 null로 주면 모든 IP에 대해 스캔
            OnvifProbe probe = new OnvifProbe(getActivity(), null); //mIPAddress);
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


                        OnvifDevice onvifDevice = new OnvifDevice();
                        onvifDevice.setIpAddress(probeMatch.get(i).mOnvifIPAddress);
                        //onvifDevice.setName(getString(R.string.unregistered_cctv) + " " + (mIps.size() + mRegisteredCctvCount));
                        onvifDevice.setName(getString(R.string.found_cctv) + " " + (mIps.size() + mRegisteredCctvCount)); //2017-02-03,yslee::등록할 CCTV 이름변경
                        onvifDevice.setNickName(getString(R.string.unregistered_cctv) + " " + (mIps.size() + mRegisteredCctvCount)); //2017-01-06,yslee::닉네임 항목 추가
                        onvifDevice.setPort(String.valueOf(probeMatch.get(i).mOnvifPort));

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

            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();

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


    private void addButtonListener() {
        //등록버튼은 뺌
//        Button registration = (Button) getActivity().findViewById(R.id.registration);
//        registration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                registerDevice();
//                refreshLists();
//
//
//            }
//        });

        if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리

            Button edit = (Button) getActivity().findViewById(R.id.edit);
            edit.setVisibility(View.INVISIBLE);

            Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchEditActivity();
                    //Toast.makeText(getActivity(), "현관 editBtn 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchDeleteActivity();
                    //Toast.makeText(getActivity(), "현관 deleteBtn 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Button edit = (Button) getActivity().findViewById(R.id.edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchEditActivity();
                }
            });
        }

        //원래 코드
        //테스트후 주석 푸세요!!
        List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<OnvifDevice> devices = getRegisteredDeviceTest();

        if (devices == null || devices.size() == 0) {
            return;
        } else {
            if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리
                Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
                editBtn.setVisibility(View.VISIBLE);
                Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
                deleteBtn.setVisibility(View.VISIBLE);

            } else {
                Button edit = (Button) getActivity().findViewById(R.id.edit);
                edit.setEnabled(true);
            }
        }

    }

    /**
     * 리스트 갱신
     */
    private void refreshLists() {

        //등록된 CCTV 리스트에 추가
        final ListView registeredDeviceList = (ListView) getActivity().findViewById(R.id.registeredCctvList);
        ((OnvifRegisteredCctvListAdapter) registeredDeviceList.getAdapter()).add(mSelectedOnvifDevice);

        //미등록된 CCTV 리스트에서 제거
        final ListView unregisteredDeviceList = (ListView) getActivity().findViewById(R.id.unregisteredCctvList);
        ((OnvifUnregisteredCctvListAdapter) unregisteredDeviceList.getAdapter()).deleteDevice(mSelectedOnvifDevice);


    }

//    /**
//     * 리스트 갱신
//     */
//    private void refreshLists() {
//
//        ArrayList<OnvifDevice> devices = getCheckedDevices();
//
//
//        //등록된 CCTV 리스트에 추가
//        final ListView registeredDeviceList = (ListView) getActivity().findViewById(R.id.registeredCctvList);
//        ((OnvifRegisteredCctvListAdapter) registeredDeviceList.getAdapter()).addAll(devices);
//
//        //미등록된 CCTV 리스트에서 제거
//        final ListView unregisteredDeviceList = (ListView) getActivity().findViewById(R.id.unregisteredCctvList);
//        ((OnvifUnregisteredCctvListAdapter) unregisteredDeviceList.getAdapter()).deleteDevices(devices);
//
//        //clearChoices 메소드를 호출하지 않으면 선택상태가 그냥 남아있는 것에 주의!!
//        unregisteredDeviceList.clearChoices();
//
//        //그냥 toggleRegistrationButtonState호출하면 작동안해서 post 메소드 사용
//        unregisteredDeviceList.post(new Runnable() {
//            @Override
//            public void run() {
//                Button button = (Button) getActivity().findViewById(R.id.registration);
//                //강제로 disable 이미지로 지정
//                button.setBackgroundResource(R.drawable.common_button_disabled);
//                //아래 코드로 등록버튼의 이미지가 disable로 변경되지 않음
//                //toggleRegistrationButtonState(false);
//            }
//        });
//
//    }

    /**
     * 등록된 CCTV 편집 액티비티로 이동
     */
    private void launchEditActivity() {
        Intent intent = new Intent(getActivity(), CctvEditActivity.class);
        startActivity(intent);
    }

    /**
     * 등록된 CCTV 삭제 액티비티로 이동
     */
    public void launchDeleteActivity() {
        Intent intent = new Intent(getActivity(), CctvDeleteActivity.class);
        startActivity(intent);

    }

    private void initIps() {
        mIps = new ArrayList<>();
    }


    /**
     * 디바이스 정보를 Content Provider에 저장
     *
     * @param deviceInfo
     */
    private void saveDeviceInfoToContentProvider(OnvifDevice deviceInfo) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_IP, deviceInfo.getIpAddress());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_ID, deviceInfo.getId());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_PASSWORD, deviceInfo.getPassword());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_DEVICE_NAME, deviceInfo.getName());
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_STREAM_URL, deviceInfo.getStreamUrl());


        ContentProviderManager.saveOnvifCctv(getActivity(), contentValues);

    }


    /**
     * 미등록된 CCTV ListView 초기화
     */
    private void initUnregisteredCctvListView() {
        List<OnvifDevice> devices = new ArrayList<OnvifDevice>();
        final ListView deviceList = (ListView) getActivity().findViewById(R.id.unregisteredCctvList);
        OnvifUnregisteredCctvListAdapter adapter = new OnvifUnregisteredCctvListAdapter(getActivity(), R.layout.list_item_unregistered_cctv2, devices);
        adapter.setListener(this);
        deviceList.setAdapter(adapter);

        //리스트 아이템을 클릭하는 기능은 뺌
//        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PlusClickGuard.doIt(view);
//                //multiChoice 모드일때에는 같은 아이템을 누르면 취소됨
//
//                if (getCheckedDevices().size() > 0) {
//                    toggleRegistrationButtonState(true);
//                } else {
//                    toggleRegistrationButtonState(false);
//                }
//            }
//        });

    }


    /**
     * 선택한 카메라 가져오기
     *
     * @return
     */
    private ArrayList<OnvifDevice> getCheckedDevices() {
        final ListView deviceList = (ListView) getActivity().findViewById(R.id.unregisteredCctvList);
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

//    /**
//     * 등록버튼 상태 변경
//     *
//     * @param isEnabled
//     */
//    private void toggleRegistrationButtonState(boolean isEnabled) {
//        Button button = (Button) getActivity().findViewById(R.id.registration);
//        button.setEnabled(isEnabled);
//    }
//
//    /**
//     * 디바이스 등록(content provider에 저장, config 브로드캐스팅)
//     */
//    private void registerDevice() {
//
//        if (getCheckedDevices().size() == 0) {
//            Toast.makeText(getActivity(), R.string.choose_camera, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        Toast.makeText(getActivity(), R.string.registered, Toast.LENGTH_SHORT).show();
//
//        //컨텐트 프로바이더에 저장
//        ArrayList<OnvifDevice> devices = getCheckedDevices();
//
//        for (OnvifDevice device : devices) {
//            saveDeviceInfoToContentProvider(device);
//        }
//
//    }


    /**
     * 리스트에 Device 표시 추가
     *
     * @param device
     */
    private void showDeviceOnList(final OnvifDevice device) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(DeviceSettingActivity.this, "리스트 표시", Toast.LENGTH_SHORT).show();
                ListView deviceList = (ListView) getActivity().findViewById(R.id.unregisteredCctvList);
                ((OnvifUnregisteredCctvListAdapter) deviceList.getAdapter()).addDevice(device);

            }
        });
    }


    /**
     * CCTV 카메라의 IP가 content provider에 이미 저장되었는지 체크
     *
     * @param ip
     * @return
     */
    private boolean isOnvifCctvIpExistOnContentProvider(String ip) {


        return ContentProviderManager.isOnvifCctvIpExistOnContentProvider(getActivity(), ip);

    }

}
