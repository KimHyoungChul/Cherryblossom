package com.commax.ipdoorcamerasetting.registration;


import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.ipdoorcamerasetting.CommonActivity;
import com.commax.ipdoorcamerasetting.R;
import com.commax.ipdoorcamerasetting.content_provider.ContentProviderManager;
import com.commax.ipdoorcamerasetting.dongho.Dongho;
import com.commax.ipdoorcamerasetting.udp.UDPReceiveDataListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CameraRegistrationActivity extends CommonActivity implements UDPReceiveDataListener {

    private static final String LOG_TAG = CameraRegistrationActivity.class.getSimpleName();
    private DeviceManager mDeviceManager;
    ArrayList<String> mIps;
    private String mSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_registration);
        initIps();
        initUDP();
        initListView();

        //showTestDevices();

        //동호가 제대로 입력되었는지 확인
        //테스트후 삭제하세요!!
//        Dongho dongho = ContentProviderManager.getDongho(this);
//        Toast.makeText(this, "동: " + dongho.getDong() + " 호: " + dongho.getHo(), Toast.LENGTH_SHORT).show();
    }

    private void initIps() {
        mIps = new ArrayList<>();
    }


    /**
     * 테스트 디바이스 표시
     */
    private void showTestDevices() {

        ArrayList<Device> devices = new ArrayList<>();

        Device device1 = new Device();
        device1.setModelName("카메라 1");

        Device device2 = new Device();
        device2.setModelName("카메라 2");

        Device device3 = new Device();
        device3.setModelName("카메라 3");

        devices.add(device1);
        devices.add(device2);
        devices.add(device3);


        ListView deviceList = (ListView) findViewById(R.id.deviceList);
        ((DeviceListAdapter) deviceList.getAdapter()).addAll(devices);


    }


    /**
     * UDP 서버 실행
     */
    private void initUDP() {
        mDeviceManager = new DeviceManager(this);
        mDeviceManager.initUdpServerAndClient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //close를 하지 않으면 액티비티를 다시 실행했을 때 브로드캐스트가 하나가 아니라 여러 개가 중복되어 날아감
        //disconnect대신 close를 해야 하는 듯
        mDeviceManager.closeUdpClient();
        mDeviceManager.closeUdpServer();
    }

    /**
     * ListView 초기화
     */
    private void initListView() {
        List<Device> devices = new ArrayList<Device>();
        final ListView deviceList = (ListView) findViewById(R.id.deviceList);
        DeviceListAdapter adapter = new DeviceListAdapter(this, R.layout.list_item_device, devices);
        deviceList.setAdapter(adapter);
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //multiChoice 모드일때에는 같은 아이템을 누르면 취소됨

                if (getCheckedDevices().size() > 0) {
                    toggleRegistrationButtonState(true);
                } else {
                    toggleRegistrationButtonState(false);
                }
            }
        });

    }


    /**
     * 선택한 카메라 가져오기
     *
     * @return
     */
    private ArrayList<Device> getCheckedDevices() {
        final ListView deviceList = (ListView) findViewById(R.id.deviceList);
        ArrayList<Device> devices = ((DeviceListAdapter) deviceList.getAdapter()).getDevices();


        ArrayList<Device> selectedDevices = new ArrayList<>();

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
     * 등록버튼 상태 변경
     *
     * @param isEnabled
     */
    private void toggleRegistrationButtonState(boolean isEnabled) {
        Button button = (Button) findViewById(R.id.registration);
        button.setEnabled(isEnabled);
    }

    /**
     * 디바이스 등록(content provider에 저장, config 브로드캐스팅)
     *
     * @param view
     */
    public void registerDevice(View view) {

        if (getCheckedDevices().size() == 0) {
            Toast.makeText(CameraRegistrationActivity.this, R.string.choose_camera, Toast.LENGTH_SHORT).show();
            return;
        }

        //테스트후 삭제하세요!!
        Toast.makeText(CameraRegistrationActivity.this, R.string.registered, Toast.LENGTH_SHORT).show();

        //저장 처리 필요한가!!


    }

    @Override
    public void onReceive(final String data) {


        handleBroadcast(data);
    }

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

            Log.d(LOG_TAG, "브로드캐스트: " + data);

            //Discovery인 경우
            if (action.equals(IpdoorcameraProtocolConstants.ACTION_REQUEST_DISCOVERY)) {
                //테스트후 삭제하세요!!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraRegistrationActivity.this, "브로드캐스트: " + data, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CameraRegistrationActivity.this, "브로드캐스트: " + data, Toast.LENGTH_SHORT).show();
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


        Device device = buildDevice(dataJson);

        showDeviceOnList(device);

    }

    /**
     * 리스트에 Device 표시 추가
     */
    private void showDeviceOnList(final Device device) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(DeviceSettingActivity.this, "리스트 표시", Toast.LENGTH_SHORT).show();
                ListView deviceList = (ListView) findViewById(R.id.deviceList);
                ((DeviceListAdapter) deviceList.getAdapter()).addDevice(device);

            }
        });
    }

    private Device buildDevice(JSONObject dataJson) {

        Device device = new Device();


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

    private boolean isIpExistOnContentProvider(String ip) {


        return ContentProviderManager.isIpExistOnContentProvider(this, ip);

    }
}
