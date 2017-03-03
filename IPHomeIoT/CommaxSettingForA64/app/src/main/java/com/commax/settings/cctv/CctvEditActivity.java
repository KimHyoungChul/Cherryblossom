package com.commax.settings.cctv;

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
import com.commax.settings.onvif.OnvifDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * CCTV  편집 액티비티
 */
public class CctvEditActivity extends CommonActivity implements NameChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv_edit);
        setFullScreen();

    }

    /**
     * 카메라 삭제 액티비티에서 돌아왔을 때 리스트 갱신하기 위해 onResume에서 처리
     */
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
        List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<OnvifDevice> devices = getRegisteredDeviceTest();


        final ListView deviceList = (ListView) findViewById(R.id.registeredCctvList);


        EditCctvListAdapter adapter = new EditCctvListAdapter(this, R.layout.list_item_edit_cctv, devices);
        deviceList.setAdapter(adapter);
    }

    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllOnvifCctv(this);
    }

    private List<OnvifDevice> getRegisteredDeviceTest() {

        List<OnvifDevice> devices = new ArrayList<>();

        OnvifDevice device1 = new OnvifDevice();
        device1.setIpAddress("192.168.1.10");
        device1.setPort("80");
        device1.setName("CCTV 1");
        device1.setStreamUrl("rtsp://test/test");
        device1.setSipPhoneNo("201");
        device1.setIsOk(CommaxConstants.TRUE);
        device1.setId("admin");
        device1.setPassword("123456");

        OnvifDevice device2 = new OnvifDevice();
        device2.setIpAddress("192.168.1.20");
        device2.setPort("80");
        device2.setName("CCTV 2");
        device2.setStreamUrl("rtsp://test/test2");
        device2.setSipPhoneNo("202");
        device2.setIsOk(CommaxConstants.FALSE);
        device2.setId("admin");
        device2.setPassword("123456");

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
        final ListView deviceList = (ListView) findViewById(R.id.registeredCctvList);
        List<OnvifDevice> devices = ((EditCctvListAdapter) deviceList.getAdapter()).getDevices();

        if (devices.size() == 0) {
            closeActivity(deviceList);
        }
    }

    /**
     * CCTV 삭제 액티비티로 이동
     *
     * @param view
     */
    public void launchCctvDeleteActivity(View view) {
        Intent intent = new Intent(this, CctvDeleteActivity.class);
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

        final ListView deviceList = (ListView) findViewById(R.id.registeredCctvList);

        List<OnvifDevice> devices = ((EditCctvListAdapter) deviceList.getAdapter()).getAllData();

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
            //Log.d("yslee","saveEdit=> " + i + ":" + newDeviceName + " " + devices.get(i).getName());

            //이름이 변경된 경우 Content provider에 저장된 레코드 update
            if (!newDeviceName.equals(devices.get(i).getName())) {
                updateContentProvider(newDeviceName, devices.get(i).getIpAddress());
            }
        }

        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
    }

    /**
     * 변경된 카메라명 Content provider에 저장
     *
     * @param newDeviceName 새 카메라 명
     * @param ipAddress     IP 주소
     */
    private void updateContentProvider(String newDeviceName, String ipAddress) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.OnvifCctvEntry.COLUMN_NAME_DEVICE_NAME, newDeviceName);

        ContentProviderManager.updateOnvifCctvName(this, contentValues, ipAddress);

    }

}
