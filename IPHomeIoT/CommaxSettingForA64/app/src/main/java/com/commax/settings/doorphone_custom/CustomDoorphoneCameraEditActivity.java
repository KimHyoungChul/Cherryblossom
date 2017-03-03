package com.commax.settings.doorphone_custom;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 도어폰 카메라 편집 액티비티
 */
public class CustomDoorphoneCameraEditActivity extends CommonActivity implements CustomNameChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorphone_camera_edit);
        setFullScreen();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initListView();

        if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리
            hideDeleteButton();
        } else {
            checkAutocloseActivity();
        }

    }

    /**
     * ListView 초기화
     */
    private void initListView() {
        //원래 코드
        //테스트후 주석 푸세요!!
        List<CustomDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<CustomDevice> devices = getRegisteredDeviceTest();


        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);


        CustomEditDooorphoneCameraListAdapter adapter = new CustomEditDooorphoneCameraListAdapter(this, R.layout.list_item_edit_doorphone_camera, devices);
        deviceList.setAdapter(adapter);
    }

    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<CustomDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllCustomDoorCamera(this);
    }

    /**
     * 카메라 데이터 테스트용
     *
     * @return
     */
    private List<CustomDevice> getRegisteredDeviceTest() {

        List<CustomDevice> devices = new ArrayList<>();

        CustomDevice device1 = new CustomDevice();
        device1.setIpv4("192.168.1.10");
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
     * 액티비티 종료
     *
     * @param view
     */
    public void closeActivity(View view) {
        finish();
    }

    /**
     * 2017-01-09,yslee::자동 액티비티 종료
     */
    public void checkAutocloseActivity() {

        //2017-01-09,yslee::모두 delete 후 자동 액티비티 종료

        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        List<CustomDevice> devices = ((CustomEditDooorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();

        if (devices.size() == 0) {
            closeActivity(deviceList);
        }
    }

    /**
     * 카메라 삭제 화면으로 이동
     *
     * @param view
     */
    public void launchCameraDeleteActivity(View view) {
        Intent intent = new Intent(this, CustomDoorphoneCameraDeleteActivity.class);
        startActivity(intent);

    }

    /**
     * 카메라 명이 변경된 경우 콜백
     */
    @Override
    public void onNameChanged() {
        showSaveEditButton();
    }


    /**
     * 삭제 버튼 숨김
     */
    private void hideDeleteButton() {
        Button deleteCameraButton = (Button) findViewById(R.id.deleteCamera);
        deleteCameraButton.setVisibility(View.GONE);
    }


    /**
     * 완료 버튼 표시
     */
    private void showSaveEditButton() {
        Button saveEditButton = (Button) findViewById(R.id.saveEdit);

        //이미 버튼이 표시된 상태이면 아래 코드 패스
        if (saveEditButton.isShown()) {
            return;
        }

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdit();
            }
        });
        saveEditButton.setVisibility(View.VISIBLE);

        hideDeleteButton();
    }

    /**
     * 변경된 카메라명 저장
     */
    private void saveEdit() {

        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);

        List<CustomDevice> devices = ((CustomEditDooorphoneCameraListAdapter) deviceList.getAdapter()).getAllData();

        int listSize = deviceList.getAdapter().getCount();
        String newDeviceName = null;

        //2017-01-20,yslee::카메라명 유효성체크
        for (int i = 0; i < listSize; i++) {
            View view = deviceList.getChildAt(i);
            EditText deviceNameInput = (EditText) view.findViewById(R.id.deviceNameInput);
            newDeviceName = deviceNameInput.getText().toString();
            if (newDeviceName.equals("")) {
                Toast.makeText(this, R.string.put_in_name, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //카메라명 추출
        for (int i = 0; i < listSize; i++) {
            View view = deviceList.getChildAt(i);
            EditText deviceNameInput = (EditText) view.findViewById(R.id.deviceNameInput);
            newDeviceName = deviceNameInput.getText().toString();
            //Log.d("yslee","saveEdit=> " + i + ":" + newDeviceName + " " + devices.get(i).getModelName());

            //이름이 변경된 경우 Content provider에 저장된 레코드 update
            if (!newDeviceName.equals(devices.get(i).getModelName())) {
                updateContentProvider(newDeviceName, devices.get(i).getIpv4());
            }
        }

        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
    }

    /**
     * 변경된 카메라명 Content provider에 저장
     */
    private void updateContentProvider(String newDeviceName, String ipAddress) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.DoorCameraEntry.COLUMN_NAME_MODEL_NAME, newDeviceName);

        ContentProviderManager.updateCustomDoorCameraName(this, contentValues, ipAddress);

    }

}
