package com.commax.settings.doorphone_onvif;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * 도어폰 카메라 삭제 액티비티
 */
public class OnvifDoorphoneCameraDeleteActivity extends CommonActivity implements OnvifDeleteDoorphoneCameraListener {

    private boolean mbSelectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorphone_camera_delete);
        setFullScreen();
        initListView();

        addButtonListener();
    }

    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllOnvifDoorCamera(this);
    }

    /**
     /* selectAll button에 대한 이벤트 처리
     */
    private void addButtonListener() {

        if (TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) { //2017-01-23,yslee:: 신규 Delete UX 적용
            ImageView selectAllButton = (ImageView) findViewById(R.id.selectAllCheckbox);
            selectAllButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    PlusClickGuard.doIt(v);
                    iselectAllbuttonToggle();
                }
            });

            TextView selectAllText = (TextView) findViewById(R.id.selectAllText);
            selectAllText.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {

                    PlusClickGuard.doIt(v);
                    iselectAllbuttonToggle();
                }
            });
        } else {
            ImageView selectAllButton = (ImageView) findViewById(R.id.selectAllCheckbox);
            TextView selectAllText = (TextView) findViewById(R.id.selectAllText);
            selectAllButton.setVisibility(View.GONE);
            selectAllText.setVisibility(View.GONE);
        }

    }

    /**
     /* selectAll button 처리
     */
    private void iselectAllbuttonToggle() {

        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        int count = ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).getCount();
        final ImageView selectAllButton = (ImageView) findViewById(R.id.selectAllCheckbox);

        mbSelectAll = !mbSelectAll;
        selectAllButton.setSelected(mbSelectAll);
        for (int i = 0; i < count; i++) {
            deviceList.setItemChecked(i, mbSelectAll); //todo
        }
        ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).notifyDataSetChanged();

        //확인버튼 표시
        if (getCheckedDevices().size() > 0) {
            showSaveDeleteButton();
            setScreenTitle(getCheckedDevices().size() + " " + getString(R.string.device_selected));
        } else {
            hideSaveDeleteButton();
            setScreenTitle(getString(R.string.delete_doorphone_camera));
        }

    }

    /**
     * ListView 초기화
     */
    private void initListView() {
        //원래 코드
        //테스트후 주석 푸세요!!
        final List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //final List<OnvifDevice> devices = getRegisteredDeviceTest();

        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);

        if (TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) { //2017-01-23,yslee:: 신규 Delete UX 적용
            OnvifDeleteDoorphoneCameraListAdapter adapter = new OnvifDeleteDoorphoneCameraListAdapter(this, R.layout.list_item_delete_doorphone_camera2, devices);
            deviceList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            deviceList.setAdapter(adapter);

            hideSaveDeleteButton();

        } else {
            OnvifDeleteDoorphoneCameraListAdapter adapter = new OnvifDeleteDoorphoneCameraListAdapter(this, R.layout.list_item_delete_doorphone_camera, devices);
            deviceList.setAdapter(adapter);
        }


        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) { //2017-01-23,yslee:: 신규 Delete UX 적용
                    PlusClickGuard.doIt(view);

                    if (getCheckedDevices().size() > 0) {
                        showSaveDeleteButton();
                        setScreenTitle(getCheckedDevices().size() + " " + getString(R.string.device_selected));
                    } else {
                        hideSaveDeleteButton();
                        setScreenTitle(getString(R.string.delete_doorphone_camera));

                        //체크해제
                        mbSelectAll = false;
                        final ImageView selectAllButton = (ImageView) findViewById(R.id.selectAllCheckbox);
                        selectAllButton.setSelected(mbSelectAll);
                    }

                } else {
                    showDeletePopup(devices.get(position));
                }
            }
        });
    }

    /**
     * 선택한 카메라 가져오기
     *
     * @return
     */
    private ArrayList<OnvifDevice> getCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();
        ArrayList<OnvifDevice> selectedDevices = new ArrayList<>();

        if (devices.size() > 0) {

            SparseBooleanArray checked = deviceList.getCheckedItemPositions();
            if(checked != null) {
                int size = checked.size(); // number of name-value pairs in the array
                for (int i = 0; i < size; i++) {
                    int key = checked.keyAt(i);
                    boolean value = checked.get(key);
                    //if(mbSelectAll) value = true;
                    if (value) {
                        selectedDevices.add(devices.get(key));
                    }
                }
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
        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();
        ArrayList<OnvifDevice> selectedDevices = new ArrayList<>();

        SparseBooleanArray checked = deviceList.getCheckedItemPositions();
        ArrayList<Integer> regkeys= new ArrayList<Integer>();
        int size = checked.size(); // number of name-value pairs in the array
        for (int i = 0; i < size; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.get(key);
            //if(mbSelectAll) value = true;
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
                ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).delDevice(key);
            }
        }

        return selectedDevices;
    }

    /**
     * 저장 완료 버튼 표시
     */
    private void showSaveDeleteButton() {
        //final Button saveRegistration = (Button) findViewById(R.id.saveDelete);
        final TextView saveRegistration = (TextView) findViewById(R.id.saveDelete);
        saveRegistration.setVisibility(View.VISIBLE);
        saveRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePopup(null);

            }
        });
    }

    /**
     * /2017-01-09,yslee::저장 완료 버튼 숨김
     */
    private void hideSaveDeleteButton() {
        //Button saveRegistration = (Button) findViewById(R.id.saveDelete);
        TextView saveRegistration = (TextView) findViewById(R.id.saveDelete);
        saveRegistration.setVisibility(View.GONE);
        saveRegistration.setOnClickListener(null);
    }

    /**
     * 저장 완료 버튼 표시여부
     */
    public void selectSaveDeleteButton() {

        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        ArrayList<OnvifDevice> devices = ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();

        if (devices.size() == 0) {
            hideSaveDeleteButton();
            if (TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {

                //체크해제 및 숨김
                mbSelectAll = false;
                ImageView selectAllButton = (ImageView) findViewById(R.id.selectAllCheckbox);
                selectAllButton.setSelected(mbSelectAll);
                selectAllButton.setVisibility(View.GONE);
                TextView selectAllText = (TextView) findViewById(R.id.selectAllText);
                selectAllText.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 삭제 팝업 표시
     *
     * @param onvifDevice
     */
    private void showDeletePopup(OnvifDevice onvifDevice) {
        OnvifDoorphoneCameraDeletePopup popup = new OnvifDoorphoneCameraDeletePopup(this, this);
        if (!TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
            popup.setDevice(onvifDevice);
        }
        popup.show();
    }

    /**
     * 디바이스 등록(content provider에 저장, config 브로드캐스팅)
     */
    private void performSaveDelete() {

        if (getCheckedDevices().size() == 0) {
            Toast.makeText(OnvifDoorphoneCameraDeleteActivity.this, R.string.choose_camera, Toast.LENGTH_SHORT).show();
            return;
        }

        //컨텐트 프로바이더에 저장
        ArrayList<OnvifDevice> devices = getCheckedDevices();

        for (OnvifDevice device : devices) {

            //도어카메라 삭제
            ContentProviderManager.deleteOnvifDoorCamera(this, device.getIpAddress());
        }

        //등록한 카메라 리스트에서 삭제
        doRemoveCheckedDevices();
        selectSaveDeleteButton();

        Toast.makeText(OnvifDoorphoneCameraDeleteActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
    }

    /**
     * 카메라 데이터 테스트용
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDeviceTest() {

        List<OnvifDevice> devices = new ArrayList<>();

        OnvifDevice device1 = new OnvifDevice();
        device1.setIpAddress("192.168.1.10");
        device1.setPort("80");
        device1.setName("Doorphone camera 1");
        device1.setStreamUrl("rtsp://test/test");
        device1.setSipPhoneNo("201");
        device1.setIsOk(CommaxConstants.TRUE);
        device1.setId("admin");
        device1.setPassword("123456");

        OnvifDevice device2 = new OnvifDevice();
        device2.setIpAddress("192.168.1.20");
        device2.setPort("80");
        device2.setName("Doorphone camera 2");
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
     * 저장하고 이전 화면으로 이동
     *
     * @param view
     */
    public void saveDelete(View view) {
        //구현 필요!! 도어폰 sdk와 연동 등이 필요할려나??
        finish();
    }

    /**
     * 카메라가 삭제된 경우 콜백
     *
     * @param device
     */
    @Override
    public void onDelete(OnvifDevice device) {
        if (TypeDef.OP_NEWDELETE_ADAPTOR_ENABLE) {
            performSaveDelete();
        } else {
            refreshList(device);
        }
    }

    /**
     * 리스트 갱신
     *
     * @param device
     */
    private void refreshList(OnvifDevice device) {
        final ListView deviceList = (ListView) findViewById(R.id.registeredDoorphoneCameraList);
        ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).deleteDevice(device);

        //2017-01-09,yslee::Delete 후 save버튼 활성 문제
        ArrayList<OnvifDevice> devices = ((OnvifDeleteDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();
        if (devices.size() == 0) {
            hideSaveDeleteButton();
        }
    }
}
