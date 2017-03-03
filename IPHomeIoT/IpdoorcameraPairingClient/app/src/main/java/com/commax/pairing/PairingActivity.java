package com.commax.pairing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.commax.pairing.ip_device_setting.IPDeviceManager;
import com.commax.pairing.udp.CommaxProtocolConstants;
import com.commax.pairing.udp.UDPReceiveDataListener;
import com.commax.pairing.util.ProgressDialogManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 하위 Device에서 Master W/P와 페어링
 */
public class PairingActivity extends Activity implements UDPReceiveDataListener {

    /* Setup Wizard Restart Broadcast Message */
    static final String SETUP_WIZARD_RESTART_ACTION = "com.commax.setupwizard.RESTART_ACTION";


    private boolean mIsDeviceRegistered;
    private Thread mThread;
    private IPDeviceManager mIpDeviceManager;
    private String mSync;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);
        init();

    }

    /**
     * 초기화
     */
    private void init() {
        initUDP();
        initDefaultValue();

    }

    /**
     * UDP 통신 초기화
     */
    private void initUDP() {
        mIpDeviceManager = new IPDeviceManager(this);
    }


    /**
     * default 값 설정
     */
    private void initDefaultValue() {
        mIsDeviceRegistered = false;
    }


    /**
     * UDP 서버와 클라이언트 close
     */
    private void closeUDP() {


        mIpDeviceManager.closeUdpServer();
        mIpDeviceManager.closeUdpClient();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeUDP();
    }

    /**
     * 자신의 Device 정보 브로드캐스팅
     *
     * @param view
     */
    public void sendDeviceInfo(View view) {

        if (mIsDeviceRegistered) {
            Toast.makeText(PairingActivity.this, "디바이스가 이미 등록되었습니다", Toast.LENGTH_SHORT).show();
            return;
        }


        Toast.makeText(PairingActivity.this, "디바이스 등록", Toast.LENGTH_SHORT).show();

        sendUdpBroadcast(getTestDiscoveryBroadcast());


    }

    private String getTestDiscoveryBroadcast() {

        JSONObject discoveryRequestJson = new JSONObject();
        try {
            discoveryRequestJson.put("action", "req_discovery");
            discoveryRequestJson.put("sync", "1234");
            discoveryRequestJson.put("source", "door");
            discoveryRequestJson.put("dest", "wallpad");
            discoveryRequestJson.put("packetrev", "1.0");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return discoveryRequestJson.toString();

        //아래를 보내니 브로드캐스트가 가지 않음
        // return "{ \"action\":\"req_discovery\", \"sync\":\"1234\", \"source\":\"door\", “dest”: “wallpad”,\"packetrev\" : \"1.0\" }";
    }

    /**
     * 로딩바 표시
     */
    private void showProgressDialog() {
        ProgressDialogManager.showProgessDialog(this, "새로운 IP 할당 중...");
        ProgressDialogManager.setCancelable(false);
    }


    /**
     * 브로드캐스팅
     *
     * @param deviceInfo
     */
    private void sendUdpBroadcast(final String deviceInfo) {

        mIpDeviceManager.sendUdpBroadcast(deviceInfo);
    }


    /**
     * 브로드캐스트를 받은 경우
     *
     * @param data 브로드캐스트 문자열
     */
    @Override
    public void onReceive(String data) {

        ///////////////////////
        //테스트후 삭제하세요!!
        //showToastTest(data);
        ///////////////////////

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
            action = dataJson.optString(CommaxProtocolConstants.KEY_ACTION);
            if (action == null || action.equals("")) {
                return;
            }

            //새로운 IP 할당하는 브로드캐스트 받은 경우
            if (action.equals(IpdoorcameraProtocolConstants.ACTION_RESPONSE_DISCOVERY)) {

                //테스트후 삭제하세요!!
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PairingActivity.this, "브로드캐스트: " + data, Toast.LENGTH_SHORT).show();
                    }
                });


                broadcastConfigRequest(dataJson);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Discovery response 브로드캐스팅
     *
     * @param dataJson
     */
    private void broadcastConfigRequest(JSONObject dataJson) {


        String sync = dataJson.optString(IpdoorcameraProtocolConstants.KEY_SYNC);

        if (sync == null || sync.equals("")) {
            return;
        }

        mSync = sync;


        JSONObject requestJson = buildConfigRequestJson(dataJson);

        mIpDeviceManager.sendUdpBroadcast(requestJson.toString());

    }

    private JSONObject buildConfigRequestJson(JSONObject responseJsonObject) {
        //모델명, ip 주소등은 어디서 가져오나??


        //나중에 수정하세요!!
        String action = IpdoorcameraProtocolConstants.ACTION_REQUEST_CONFIG;
        String sync = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SYNC);
        String source = "door";
        String dest = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SOURCE); //도어폰:"door", 로비폰:"lobby", 경비실:"guard"
        String packetrev = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_PACKET_REV);
        String modelName = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_MODEL_NAME);
        String mac = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_MAC);
        String usn = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_USN);
        ; //unique serial number


        String dong = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_DONG);
        String ho = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_HO);

        //IP 설정 모드
        String set = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_SET); //Auto:"0","DHCP":"1", "STATIC":"2"

        String ipv4 = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4);
        String ipv4gateway = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY);
        String ipv4subnet = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET);
        String dns = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_DNS);
        String wp = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_WP); //web port
        String rp = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_RP); //rtsp port
        String u0 = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_U0); //first streaming url
        String u1 = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_U1); //second streaming url
        String time = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_TIME);
        String firmware = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_FIRMWARE);
        String result = responseJsonObject.optString(IpdoorcameraProtocolConstants.KEY_RESULT);


        JSONObject requestJsonObject = new JSONObject();
        try {
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_ACTION, action);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_SYNC, sync);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_SOURCE, source);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_DEST, dest);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_PACKET_REV, packetrev);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_MODEL_NAME, modelName);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_MAC, mac);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_USN, usn);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_DONG, dong);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_HO, ho);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_SET, set);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4, ipv4);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_GATEWAY, ipv4gateway);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_IPV4_SUBNET, ipv4subnet);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_DNS, dns);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_WP, wp);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_RP, rp);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_U0, u0);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_U1, u1);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_TIME, time);
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_FIRMWARE, firmware);
            //result에 넣을 값은 수정이 필요할 수 있음!!
            requestJsonObject.put(IpdoorcameraProtocolConstants.KEY_RESULT, result);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return requestJsonObject;
    }


    /**
     * 로딩바 제거
     */
    private void removeProgressDialog() {

        ProgressDialogManager.removeProgressDialog();
        /////////////////////////////////////////////////////////////////////////////////////
        //테스트후 삭제하세요!!
        Toast.makeText(PairingActivity.this, "새로운 IP가 할당되었습니다", Toast.LENGTH_SHORT).show();
        ////////////////////////////////////////////////////////////////////////////////////


    }

}

