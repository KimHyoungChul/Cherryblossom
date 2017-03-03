package com.commax.settings.doorphone_custom;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom로 도어폰 등록 fragment
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class CustomDoorCameraRegistrationFragment extends Fragment implements CustomPreviewRunListener {
    private static final String LOG_TAG = CustomDoorCameraRegistrationFragment.class.getSimpleName();
    private static final int REQUEST_DOOR_PREVIEW = 567;
    private CustomDevice mSelectedCustomDevice;

    public CustomDoorCameraRegistrationFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_camera_registration, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addButtonListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        initRegisteredDeviceListView();

        //2017-01-03,yslee::add후 edit버튼 비활성 문제
        List<CustomDevice> devices = getRegisteredDevice();

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

    }

    /**
     * 등록된 디바이스 ListView 초기화
     */
    private void initRegisteredDeviceListView() {
        //원래 코드
        //테스트후 주석 푸세요!!
        List<CustomDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<CustomDevice> devices = getRegisteredDeviceTest();

        //테스트용(yslee)
        //checkRegisteredDeviceStatus(); //for test

        final ListView deviceList = (ListView) getActivity().findViewById(R.id.registeredDoorphoneCameraList);
        CustomRegisteredDooorphoneCameraListAdapter adapter = new CustomRegisteredDooorphoneCameraListAdapter(getActivity(), R.layout.list_item_registered_doorphone_camera, devices);
        adapter.setListener(this);
        deviceList.setAdapter(adapter);
    }


    /**
     * 등록된 디바이스 ListView 초기화
     */
    private void checkRegisteredDeviceStatus() {
        List<CustomDevice> devices = getRegisteredDevice();

        //for test
        //2017-01-03,yslee::등록된 디바이스 상태 업데이트
        if (devices != null) {

            for (int i = 0; i < devices.size(); i++) {
                CustomDevice deviceInfo = devices.get(i);
                if (deviceInfo.getIpv4() == null) deviceInfo.setRtspPort("80");
                if (getStreamUrl(deviceInfo) == null) {
                    deviceInfo.setIsOk(CommaxConstants.FALSE);
                } else {
                    deviceInfo.setIsOk(CommaxConstants.TRUE);
                }
            }
        }
        //for test
    }

    /**
     * 스트리밍 url 가져옴
     *
     * @param deviceInfo
     * @return
     */
    private String getStreamUrl(CustomDevice deviceInfo) {

        return null;
    }

    /**
     * 카메라 데이터 테스트용
     *
     * @return
     */
    private List<CustomDevice> getRegisteredDeviceTest() {

        List<CustomDevice> devices = new ArrayList<>();

        CustomDevice device1 = new CustomDevice();
        //device1.setIpv4("192.168.1.10");
        device1.setIpv4("10.15.1.110");
        device1.setIpv4subnet("255.0.0.0");
        device1.setRtspPort("80");
        device1.setModelName("Doorphone camera 1");
        device1.setNickName("Doorphone camera 1");
        device1.setIsOk(CommaxConstants.TRUE);

        CustomDevice device2 = new CustomDevice();
        device2.setIpv4("192.168.1.20");
        device2.setIpv4subnet("255.0.0.0");
        device2.setRtspPort("80");
        device2.setModelName("Doorphone camera 2");
        device2.setNickName("Doorphone camera 2");
        device2.setIsOk(CommaxConstants.TRUE);

        devices.add(device1);
        devices.add(device2);


        return devices;
    }


    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<CustomDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllCustomDoorCamera(getActivity());
    }


    private void addButtonListener() {

        LinearLayout register = (LinearLayout) getActivity().findViewById(R.id.registerDoorphoneCamera);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //커스텀 프로토콜 사용하여 도어폰 카메라 등록
                 launchRegisterByCustomActivity();
            }
        });

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
        List<CustomDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<CustomDevice> devices = getRegisteredDeviceTest();

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
     * 디바이스 정보를 Content Provider에 저장
     *
     * @param deviceInfo
     */
    private void saveDeviceInfoToContentProvider(CustomDevice deviceInfo) {

        //해당 디바이스가 이미 저장된 경우 pass
        if ( ContentProviderManager.isCustomDoorCameraIpExistOnContentProvider(getActivity(), deviceInfo.getIpv4()) ) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_MODEL_NAME, deviceInfo.getModelName());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_MAC, deviceInfo.getMac());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_USN, deviceInfo.getUsn());

        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4, deviceInfo.getIpv4());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4_GATEWAY, deviceInfo.getIpv4gateway());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IPV4_SUBNET, deviceInfo.getIpv4subnet());

        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_SIP_NO, deviceInfo.getSipPhoneNo()); //2017-01,24,yslee::SIP 번호 항목 추가함
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_DNS, deviceInfo.getDns());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_WEBPORT, deviceInfo.getWebPort());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_RTSPPORT, deviceInfo.getRtspPort());

        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_FIRST_STREAM_URL, deviceInfo.getFirstStreamUrl());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_SECOND_STREAM_URL, deviceInfo.getSecondStreamUrl());
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_IS_OK, deviceInfo.getIsOk());

        ContentProviderManager.saveCustomDoorCamera(getActivity(), contentValues);

    }

    @Override
    public void onrunPreview(CustomDevice device) {
        //현관카메라 미리보기 앱 실행
        runPreview(device);

        mSelectedCustomDevice  = device;
    }

    /**
     * 미리보기
     *
     * @param device
     */
    private void runPreview(CustomDevice device) {

        //현관 모니터링 앱 실행
        Intent intent = new Intent();
        intent.setClassName(CommaxConstants.PACKAGE_DOOR, CommaxConstants.ACTIVITY_DOOR);
        intent.putExtra(CommaxConstants.KEY_MODE, CommaxConstants.PREVIEW);
        intent.putExtra(CommaxConstants.KEY_IP, device.getIpv4());

        Log.d(LOG_TAG, "runPreview=>" + CommaxConstants.PACKAGE_DOOR + "," + CommaxConstants.ACTIVITY_DOOR + "(IP:" + device.getIpv4() + ",SIPNO:" + device.getSipPhoneNo() + ")");
        if (isAvailable(intent)) {
            startActivityForResult(intent, REQUEST_DOOR_PREVIEW);
        } else {
            Toast.makeText(getActivity(), R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "onActivityResult DOOR_PREVIEW: " + data);

        //현관카메라 미리보기 등록 버튼을 클릭한 경우
        if(requestCode == REQUEST_DOOR_PREVIEW) {
            if(data == null) return; //2017-01-03,yslee::null exception handling
            if(data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED) != null && data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED).equals(CommaxConstants.TRUE)) {

                //의미없음
                //saveDeviceInfoToContentProvider(mSelectedCustomDevice);

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
     * 현관카메라 수동등록 액티비티로 이동
     */
    private void launchRegisterByCustomActivity() {
        Intent intent = new Intent(getActivity(), CustomDoorphoneCameraRegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * 도어폰 카메라 편집 액티비티로 이동
     */
    private void launchEditActivity() {
        Intent intent = new Intent(getActivity(), CustomDoorphoneCameraEditActivity.class);
        startActivity(intent);
    }

    /**
     * 도어폰 카메라 삭제 액티비티로 이동
     */
    public void launchDeleteActivity() {
        Intent intent = new Intent(getActivity(), CustomDoorphoneCameraDeleteActivity.class);
        startActivity(intent);

    }
}
