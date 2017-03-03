package com.commax.settings.doorphone_custom;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.CommonActivity;
import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.udp.UDPReceiveDataListener;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.wallpad.Dongho;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 커스텀 프로토콜로 도어폰 등록 => 테스트용
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class CustomDoorphoneCameraRegistrationActivity extends CommonActivity implements UDPReceiveDataListener,CustomPreviewRunListener {
    private static final String LOG_TAG = CustomDoorphoneCameraRegistrationActivity.class.getSimpleName();
    private DeviceManager mDeviceManager;
    ArrayList<String> mIps;
    private String mSync;
    private ObjectAnimator mtextColorAnim;

    private CustomDevice mSelectedCustomDevice;
    private static final int REQUEST_DOOR_PREVIEW = 567;
    private static final String DOOR_CAMERA_ID = "admin";
    private static final String DOOR_CAMERA_PASSWORD = "123456";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doorphone_camera_registration_bycustom);

        setFullScreen();
        initIps();
        initUDP();
        initListView();
        addButtonListener();
    }

    private void addButtonListener() {
        LinearLayout registration_layout = (LinearLayout) findViewById(R.id.registration_layout);
        registration_layout.setVisibility(View.GONE); //숨김

        Button registration = (Button) findViewById(R.id.registration);
        registration.setVisibility(View.GONE); //숨김

/*
        Button registration = (Button) findViewById(R.id.registration);
        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlusClickGuard.doIt(v);
                performSaveRegistration();
            }
        });
*/

    }

    private void initIps() {
        mIps = new ArrayList<>();
    }

    /**
     * UDP 서버 실행시 실행중 알림위함
     */
    private void blinkTextstart() {
        final TextView helpText = (TextView) findViewById(R.id.registerationHelpText);

        mtextColorAnim = ObjectAnimator.ofInt(helpText, "textColor", Color.BLACK, Color.TRANSPARENT);
        mtextColorAnim.setDuration(500);
        mtextColorAnim.setEvaluator(new ArgbEvaluator());
        mtextColorAnim.setRepeatCount(ValueAnimator.INFINITE);
        mtextColorAnim.setRepeatMode(ValueAnimator.REVERSE);
        mtextColorAnim.start();
    }

    private void blinkTextstop() {
        mtextColorAnim.cancel();
    }

    /**
     * UDP 서버 실행
     */
    private void initUDP() {
        mDeviceManager = new DeviceManager(this, this);
        mDeviceManager.initUdpServerAndClient();
        blinkTextstart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDeviceManager.closeUdpServer();
        mDeviceManager.closeUdpClient();
        blinkTextstop();
    }

    /**
     * ListView 초기화
     */
    private void initListView() {

        List<CustomDevice> devices = new ArrayList<CustomDevice>();

        //테스트용
        //테스트후 삭제하세요!!
        //devices = getRegisteredDeviceTest();

        final ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
        CustomUnregisteredDoorphoneCameraListAdapter adapter = new CustomUnregisteredDoorphoneCameraListAdapter(this, R.layout.list_item_scanned_doorphone_camera, devices);
        adapter.setListener(this);
        deviceList.setAdapter(adapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlusClickGuard.doIt(view);
                //multiChoice 모드일때에는 같은 아이템을 누르면 취소됨

//                if (getCheckedDevices().size() > 0) {
//                    toggleRegistrationButtonState(true);
//                } else {
//                    toggleRegistrationButtonState(false);
//                }

                if (getCheckedDevices().size() > 0) {
                    showSaveRegistrationButton();
                } else {
                    hideSaveRegistrationButton();
                }
            }
        });

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
        if(TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE) {
            Intent intent = new Intent();
            intent.setClassName(CommaxConstants.PACKAGE_DOOR, CommaxConstants.ACTIVITY_DOOR);
            intent.putExtra(CommaxConstants.KEY_MODE, CommaxConstants.PREVIEW);
            intent.putExtra(CommaxConstants.KEY_IP, device.getIpv4());

            Log.d(LOG_TAG, "runPreview=>" + CommaxConstants.PACKAGE_DOOR + "," + CommaxConstants.ACTIVITY_DOOR + "(IP:" + device.getIpv4() + ",SIPNO:" + device.getSipPhoneNo() + ")");
            if (isAvailable(intent)) {
                startActivityForResult(intent, REQUEST_DOOR_PREVIEW);
            } else {
                Toast.makeText(CustomDoorphoneCameraRegistrationActivity.this, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
            }

        } else {

            Log.d(LOG_TAG, "runPreview ip: " + device.getIpv4());
            Intent intent = new Intent(CommaxConstants.BROADCAST_DOOR_MONITOR);
            intent.putExtra(CommaxConstants.KEY_FROM, CommaxConstants.PREVIEW);
            intent.putExtra(CommaxConstants.KEY_IP, device.getIpv4());
            sendBroadcast(intent);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "onActivityResult DOOR_PREVIEW: " + data);

        //현관카메라 미리보기 등록 버튼을 클릭한 경우
        if(requestCode == REQUEST_DOOR_PREVIEW) {
            if(data == null) return; //2017-01-03,yslee::null exception handling
            if(data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED) != null && data.getStringExtra(CommaxConstants.KEY_IS_REGISTERED).equals(CommaxConstants.TRUE)) {

                saveDeviceInfoToContentProvider(mSelectedCustomDevice);
                final ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
                ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).deleteDevice(mSelectedCustomDevice);
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
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 선택한 카메라 가져오기
     *
     * @return
     */
    private ArrayList<CustomDevice> getCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
        ArrayList<CustomDevice> devices = ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();
        ArrayList<CustomDevice> selectedDevices = new ArrayList<>();

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
    private ArrayList<CustomDevice> doRemoveCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
        ArrayList<CustomDevice> devices = ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();
        ArrayList<CustomDevice> selectedDevices = new ArrayList<>();

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
                ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).delDevice(key);
            }
        }

        return selectedDevices;
    }


    /**
     * 등록버튼 상태 변경 -> 삭제
     *
     * @param isEnabled
     */
    /*
    private void toggleRegistrationButtonState(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.registration);
        button.setEnabled(isEnabled);
    }
    */


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
        final Button saveRegistration = (Button) findViewById(R.id.saveRegistration);
        saveRegistration.setVisibility(View.VISIBLE);
        saveRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSaveRegistration();
            }
        });
    }

    /**
     * 저장 완료 버튼 표시여부
     */
    public void selectSaveRegistrationButton() {

        //2017-01-09,yslee::add후 save버튼 활성 문제
        final ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
        ArrayList<CustomDevice> devices = ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).getDevices();


        if (devices.size() == 0) {
            hideSaveRegistrationButton();
        }
    }

    /**
     * 도어카메라에 월패드 IP 등록함
     * @return
     */

    /**
     * WS-Discovery 실행
     */
    public class HostConfigHTMLTask extends AsyncTask<Void, Void, Integer> {

        CustomDevice customDevice;
        public HostConfigHTMLTask(CustomDevice device) {
            customDevice = device;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int result = 0;
            try {
                MessageHandler.getInst().SendHostConfigResponse(customDevice.getIpv4());

            } catch (Exception e) {
            }
            return result;
        }


        @Override
        protected void onPostExecute(Integer result) {

            super.onPostExecute(result);
        }
    }

    public void SendHostConfigResponse(String doorIP) {
        Log.d(LOG_TAG, "SendHostConfigResponse() ");
        try {
            String hostIP = MessageHandler.getInst().getHostIPAddr();
            //String cmdInfo = "http://10.15.1.110/settings/platform/&enable=1&server=10.15.1.107&port=5060&user=admin&password=123456/";
            String cmdInfo = String.format("http://%s/settings/platform/&enable=1&server=%s&port=5060&user=admin&password=123456/" ,doorIP, hostIP);
            Log.d(LOG_TAG, "SendHostConfigResponse... " + cmdInfo);
            URL putcmd = new URL(cmdInfo);
            InputStream httpio = putcmd.openStream();
            httpio.close();

            Log.d(LOG_TAG, "SendHostConfigResponse...OK " + cmdInfo);

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
    }

    /**
     * 디바이스 등록(content provider에 저장, config 브로드캐스팅)
     */
    private void performSaveRegistration() {

        if (getCheckedDevices().size() == 0) {
            Toast.makeText(CustomDoorphoneCameraRegistrationActivity.this, R.string.choose_camera, Toast.LENGTH_SHORT).show();
            return;
        }

        //컨텐트 프로바이더에 저장
        ArrayList<CustomDevice> devices = getCheckedDevices();

        for (CustomDevice device : devices) {

            //도어카메라에 월패드 IP등록
            HostConfigHTMLTask sendtask = new HostConfigHTMLTask(device);
            sendtask.execute();

            //도어카메라 등록
            saveDeviceInfoToContentProvider(device);
        }

        //등록한 카메라 리스트에서 삭제
        doRemoveCheckedDevices();
        //toggleRegistrationButtonState(false);
        selectSaveRegistrationButton();

        Toast.makeText(CustomDoorphoneCameraRegistrationActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();
    }

    /**
     * 리스트에 Device 표시
     *
     * @param dataJson
     */
    private void showDevice(JSONObject dataJson) {
        String ip = null;
        ip = dataJson.optString(IpdoorcameraProtocolConstants.KEY_IPV4);

        if (isIpExistOnContentProvider(ip)) {
            return;
        }

        if (mIps.contains(ip)) {
            return;
        }

        mIps.add(ip);

        CustomDevice device = buildDevice(dataJson);

        showDeviceOnList(device);

    }

    /**
     * 리스트에 Device 표시 추가
     */
    private void showDeviceOnList(final CustomDevice device) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(DeviceSettingActivity.this, "리스트 표시", Toast.LENGTH_SHORT).show();
                ListView deviceList = (ListView) findViewById(R.id.scannedDeviceList);
                ((CustomUnregisteredDoorphoneCameraListAdapter) deviceList.getAdapter()).addDevice(device);
            }
        });
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
     * 2017-01-09,yslee::등록할 도어카메라 이름생성
     *
     * @return
     */
    private String getRegisterDoorCameraName() {

        //등록할 도어카메라 이름생성(카운터 증가)
        List<CustomDevice> regdevices = getRegisteredDevice();
        int devicecount = regdevices.size();
        int deviceindex = 1;
        String doorcamera_name = getString(R.string.found_doorphone_camera) + " " +  deviceindex;

        //이름 중복체크
        for (int i = 0; i < devicecount; i++) {
            CustomDevice deviceInfo = regdevices.get(i);
            String temp_name = deviceInfo.getModelName();
            if((temp_name!=null) && temp_name.equalsIgnoreCase(doorcamera_name)) {
                deviceindex++;
                doorcamera_name = getString(R.string.found_doorphone_camera) + " " +  deviceindex;
            }
        }

        return doorcamera_name;
    }

    /**
     * 2017-01-24,yslee::등록할 도어카메라 SIP NO생성
     *
     * @return
     */
    private String getRegisterDoorCameraSipNo() {

        //등록할 도어카메라 SipNo생성(카운터 증가)
        List<CustomDevice> regdevices = getRegisteredDevice();
        int devicecount = regdevices.size();
        int deviceindex = Integer.parseInt(CommaxConstants.DOOR_DEFAULT_SIPNO);
        String doorcamera_sipNo = Integer.toString(deviceindex);

        //이름 중복체크
        for (int i = 0; i < devicecount; i++) {
            CustomDevice deviceInfo = regdevices.get(i);
            String temp_sipNo = deviceInfo.getSipPhoneNo();
            if((temp_sipNo!=null) && temp_sipNo.equalsIgnoreCase(doorcamera_sipNo)) {
                deviceindex++;
                doorcamera_sipNo = Integer.toString(deviceindex);
            }
        }

        return doorcamera_sipNo;
    }

    /**
     * 도어폰 카메라의 IP가 content provider에 이미 저장되었는지 체크
     *
     * @param ip
     * @return
     */
    private boolean isCustomDoorCameraIpExistOnContentProvider(String ip) {

        return ContentProviderManager.isCustomDoorCameraIpExistOnContentProvider(CustomDoorphoneCameraRegistrationActivity.this, ip);

    }

    /**
     * 디바이스 정보를 Content Provider에 저장
     *
     * @param deviceInfo
     */
    private void saveDeviceInfoToContentProvider(CustomDevice deviceInfo) {

        //해당 디바이스가 이미 저장된 경우 pass
        if ( isCustomDoorCameraIpExistOnContentProvider(deviceInfo.getIpv4()) ) {
            return;
        }

        deviceInfo.setModelName(getRegisterDoorCameraName()); //2017-01-09,yslee::등록할 도어카메라 이름변경 지정
        deviceInfo.setSipPhoneNo(getRegisterDoorCameraSipNo()); //2017-01,24,yslee::SIP 번호 항목 추가함

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

        ContentProviderManager.saveCustomDoorCamera(this, contentValues);

    }

    /**
     * 도어폰 카메라의 IP가 content provider에 이미 저장되었는지 체크
     *
     * @param ip
     * @return
     */
    private boolean isIpExistOnContentProvider(String ip) {

        return ContentProviderManager.isIpExistOnContentProvider(this, ip);

    }

    /**
     * 액티비티 종료
     *
     * @param view
     */
    public void closeActivity(View view) {
        finish();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // ANJVISION MC200D 사양
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////
/*
<?xml version="1.0" encoding="GB2312" ?>
<XML_ANJVISION>
<MESSAGE_HEADER Msg_type="SYSTEM_NOTIFY_INFO_MESSAGE" />
<MESSAGE_BODY>
<DEVICE_TYPE DeviceType="NVS-IPCAM" DeviceModule="MC200D_KO_V0" />
<IPC_SERIALNUMBER
SerialNumber="06000000019CB6FF" />
<LANConfig
MacAddress="00:06:00:01:9C:B6"
IPAddress="10.15.1.110"
Netmask="255.0.0.0"
Gateway="10.1.1.254"
/>
</MESSAGE_BODY>
</XML_ANJVISION>

<?xml version="1.0" encoding="GB2312" ?><XML_TOPSEE>
<MESSAGE_HEADER
Msg_type="AUXPTZ_HEARTBEAT_MESSAGE"
Msg_code="CMD_HEARTBEAT"
Msg_flag="0"
/><MESSAGE_BODY/>
</XML_TOPSEE>
NetSDK: ipc[10.15.1.110:8091]: send heartbeat data ok!

<?xml version="1.0" encoding="GB2312" ?><XML_TOPSEE>
<MESSAGE_HEADER
Msg_type="SYSTEM_CONTROL_MESSAGE"
Msg_code="1012"
Msg_flag="0"
/><MESSAGE_BODY/>
</XML_TOPSEE>
NetSDK: add this action to list ok!

MainActivity: StatusEvent -lUser:-554442240-nStateCode:10
MainActivity: StatusEvent -pResponse:
NetSDK: receive msg:SYSTEM_CONTROL_MESSAGE, code:1012, flag:0

NetSDK: msgbody=
<RESPONSE_PARAM
KernelVersion="Linux 3.18.30 armv7l"
FileSystemVersion="MC200D_KO V2.3.3 build 2017-01-19 15:24:16 "
/>
MainActivity: Get System Version info. KernelVersion:Linux 3.18.30 armv7l FileSystemVersion:MC200D_KO V2.3.3 build 2017-01-19 15:24:16

*/

    @Override
    public void onReceive(final String data) {

        Log.d(LOG_TAG, "-> UDPReceiveDataListener onReceive():" + data);
        //handleBroadcast(data);

        handleReceivedData(data);

//        mDeviceManager.sendUdpBroadcast(deviceInfo);


    }

    /**
     * Received Data Parser
     *
     * @return
     */

    private void handleReceivedData(final String data) {

        MessageData msgData = new MessageData();
        MessageHandler.getInst().ReceivedDataParser(msgData, data);

        if(msgData.getMsgType().equalsIgnoreCase(MessageHandler.MSG_SYSTEM_NOTIFY)) {
            addDiscoveryDevice(msgData);
        }
    }

    /**
     * 검색된 카메라 리스트에 추가
     *
     * @return
     */
    private void addDiscoveryDevice(MessageData message) {

        //맥어드레스대신 ip로 구분하기로 됨.
        //해당 디바이스가 이미 저장된 경우 pass
        if (isCustomDoorCameraIpExistOnContentProvider(message.getIpv4())) {
            return;
        }

        //해당 디바이스가 이미 스캔된 경우 pass
        if (mIps.contains(message.getIpv4())) {
            return;
        }

        mIps.add(message.getIpv4());

        //디바이스 정보 가져옴
        CustomDevice device = new CustomDevice();
        device.setUsn(message.getUsn());
        device.setMac(message.getMac());
        device.setIpv4(message.getIpv4());
        device.setIpv4subnet(message.getIpv4subnet());
        device.setIpv4gateway(message.getIpv4gateway());
        device.setSipPhoneNo(CommaxConstants.DOOR_DEFAULT_SIPNO);
        device.setIsOk(CommaxConstants.TRUE);

        //device.setModelName(message.getDeviceName());
        device.setModelName(getString(R.string.found_doorphone_camera) + " " + mIps.size());
        device.setNickName(getString(R.string.unregistered_doorphone_camera) + " " + mIps.size()); //2017-01-06,yslee::닉네임 항목 추가

        showDeviceOnList(device);
    }


    /////////////////////////////////////////////////////////////////////////////
    // 불필요 -> 삭제
    /**
     * 브로드캐스트 처리
     *
     * @param data
     */
    private void handleBroadcast(final String data) {
        JSONObject dataJson = null;

        String action = null;


        try {
            dataJson = new JSONObject(data);
            action = dataJson.optString(IpdoorcameraProtocolConstants.KEY_ACTION);
            if (action == null || action.equals("")) {
                return;
            }

            //Discovery인 경우
            if (action.equals(IpdoorcameraProtocolConstants.ACTION_REQUEST_DISCOVERY)) {
                //테스트후 삭제하세요!!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomDoorphoneCameraRegistrationActivity.this, "브로드캐스트: " + data, Toast.LENGTH_SHORT).show();
                    }
                });

                broadcastDiscoveryResponse(dataJson);
            }

            //Config의 경우
            if (action.equals(IpdoorcameraProtocolConstants.ACTION_REQUEST_CONFIG)) {
                //테스트후 삭제하세요!!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CustomDoorphoneCameraRegistrationActivity.this, "브로드캐스트: " + data, Toast.LENGTH_SHORT).show();
                    }
                });


                broadcastConfigResponse(dataJson);
                showDevice(dataJson);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Config response 브로드캐스팅
     *
     * @param dataJson
     */
    private void broadcastConfigResponse(JSONObject dataJson) {


        String sync = dataJson.optString(IpdoorcameraProtocolConstants.KEY_SYNC);

        if (sync == null || sync.equals("")) {
            return;
        }

        if (!sync.equals(mSync)) {
            Log.d(LOG_TAG, getString(R.string.not_same_sync));
            return;
        }


        JSONObject responseJson = buildConfigResponseJson(dataJson);

        mDeviceManager.sendUdpBroadcast(responseJson.toString());

    }

    /**
     * Discovery response 브로드캐스팅
     *
     * @param dataJson
     */
    private void broadcastDiscoveryResponse(JSONObject dataJson) {


        String sync = dataJson.optString(IpdoorcameraProtocolConstants.KEY_SYNC);

        if (sync == null || sync.equals("")) {
            return;
        }

        mSync = sync;


        JSONObject responseJson = buildDiscoveryResponseJson(dataJson);

        mDeviceManager.sendUdpBroadcast(responseJson.toString());

    }



    private CustomDevice buildDevice(JSONObject dataJson) {

        CustomDevice device = new CustomDevice();


        String modelName = dataJson.optString(IpdoorcameraProtocolConstants.KEY_MODEL_NAME);
        String mac = dataJson.optString(IpdoorcameraProtocolConstants.KEY_MAC);
        String usn = dataJson.optString(IpdoorcameraProtocolConstants.KEY_USN);
        ; //unique serial number

        String ipv4 = dataJson.optString(IpdoorcameraProtocolConstants.KEY_IPV4);
        String ipv4gateway = dataJson.optString(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY);
        String ipv4subnet = dataJson.optString(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET);
        String dns = dataJson.optString(IpdoorcameraProtocolConstants.KEY_DNS);
        String wp = dataJson.optString(IpdoorcameraProtocolConstants.KEY_WP); //web port
        String rp = dataJson.optString(IpdoorcameraProtocolConstants.KEY_RP); //rtsp port
        String u0 = dataJson.optString(IpdoorcameraProtocolConstants.KEY_U0); //first streaming url
        String u1 = dataJson.optString(IpdoorcameraProtocolConstants.KEY_U1); //second streaming url

        device.setModelName(modelName);
        device.setMac(mac);
        device.setUsn(usn);
        device.setIpv4(ipv4);
        device.setIpv4gateway(ipv4gateway);
        device.setIpv4subnet(ipv4subnet);
        device.setDns(dns);
        device.setWebPort(wp);
        device.setRtspPort(rp);
        device.setFirstStreamUrl(u0);
        device.setSecondStreamUrl(u1);


        return device;
    }


    /**
     * Discovery response json 생성
     *
     * @return
     */
    private JSONObject buildConfigResponseJson(JSONObject requestJsonObject) {

        //모델명, ip 주소등은 어디서 가져오나??


        //나중에 수정하세요!!
        String action = IpdoorcameraProtocolConstants.ACTION_RESPONSE_CONFIG;
        String sync = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SYNC);
        String source = "wallpad";
        String dest = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SOURCE); //도어폰:"door", 로비폰:"lobby", 경비실:"guard"
        String packetrev = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_PACKET_REV);
        String modelName = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_MODEL_NAME);
        String mac = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_MAC);
        String usn = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_USN);
        ; //unique serial number


        String dong = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_DONG);
        String ho = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_HO);

        //IP 설정 모드
        String set = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SET); //Auto:"0","DHCP":"1", "STATIC":"2"

        String ipv4 = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4);
        String ipv4gateway = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY);
        String ipv4subnet = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET);
        String dns = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_DNS);
        String wp = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_WP); //web port
        String rp = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_RP); //rtsp port
        String u0 = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_U0); //first streaming url
        String u1 = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_U1); //second streaming url
        String time = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_TIME);
        String firmware = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_FIRMWARE);
        String result = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_RESULT);


        JSONObject responseJsonObject = new JSONObject();
        try {
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_ACTION, action);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SYNC, sync);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SOURCE, source);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DEST, dest);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_PACKET_REV, packetrev);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_MODEL_NAME, modelName);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_MAC, mac);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_USN, usn);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DONG, dong);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_HO, ho);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SET, set);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4, ipv4);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY, ipv4gateway);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET, ipv4subnet);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DNS, dns);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_WP, wp);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_RP, rp);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_U0, u0);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_U1, u1);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_TIME, time);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_FIRMWARE, firmware);
            //result에 넣을 값은 수정이 필요할 수 있음!!
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_RESULT, result);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseJsonObject;
    }

    /**
     * Discovery response json 생성
     *
     * @return
     */
    private JSONObject buildDiscoveryResponseJson(JSONObject requestJsonObject) {

        //모델명, ip 주소등은 어디서 가져오나??


        //나중에 수정하세요!!
        String action = IpdoorcameraProtocolConstants.ACTION_RESPONSE_DISCOVERY;
        String sync = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SYNC);
        String source = "wallpad";
        String dest = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SOURCE); //도어폰:"door", 로비폰:"lobby", 경비실:"guard"
        String packetrev = requestJsonObject.optString(IpdoorcameraProtocolConstants.KEY_PACKET_REV);
        String modelName = "CIOT-700M";
        String mac = "58:09:43:FF:9F:78";
        String usn = "48591351"; //unique serial number


        //동호
        Dongho dongho = ContentProviderManager.getDongho(this);

        String dong = dongho.getDong();
        String ho = dongho.getHo();

        //IP 설정 모드
        String set = "0"; //Auto:"0","DHCP":"1", "STATIC":"2"

        String ipv4 = "192.168.1.200";
        String ipv4gateway = "10.1.1.254";
        String ipv4subnet = "255.255.255.0";
        String dns = "168.126.63.1";
        String wp = "80"; //web port
        String rp = "554"; //rtsp port
        String u0 = "c0u0"; //first streaming url
        String u1 = "c0u1"; //second streaming url
        String time = "2014-08-20 14:11:30";
        String firmware = "0.9.11";


        JSONObject responseJsonObject = new JSONObject();
        try {
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_ACTION, action);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SYNC, sync);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SOURCE, source);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DEST, dest);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_PACKET_REV, packetrev);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_MODEL_NAME, modelName);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_MAC, mac);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_USN, usn);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DONG, dong);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_HO, ho);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_SET, set);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4, ipv4);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY, ipv4gateway);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET, ipv4subnet);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_DNS, dns);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_WP, wp);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_RP, rp);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_U0, u0);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_U1, u1);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_TIME, time);
            responseJsonObject.put(IpdoorcameraProtocolConstants.KEY_FIRMWARE, firmware);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return responseJsonObject;
    }
    /////////////////////////////////////////////////////////////////////////////


}
