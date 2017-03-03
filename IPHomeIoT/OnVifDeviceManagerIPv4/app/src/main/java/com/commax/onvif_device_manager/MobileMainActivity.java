/**
 * @file MobileMainActivity.java
 */
package com.commax.onvif_device_manager;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.commax.onvif_device_manager.content_provider.ContentProviderConstants;
import com.commax.onvif_device_manager.content_provider.ContentProviderManager;
import com.commax.onvif_device_manager.device.DeviceDeleteListener;
import com.commax.onvif_device_manager.device.DeviceInfoConstants;
import com.commax.onvif_device_manager.device.DeviceInfoPopup;
import com.commax.onvif_device_manager.device.OnvifDevice;
import com.commax.onvif_device_manager.device.OnvifDeviceListAdapter;
import com.commax.onvif_device_manager.device.NewIPAddressManager;
import com.commax.onvif_device_manager.network.NetworkOnvifRequester;
import com.commax.onvif_device_manager.network.OnvifProbe;
import com.commax.onvif_device_manager.device.DeviceInfoConfirmListener;

import org.onvif.ver10.schema.nativeParcel.ProbeMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * OnVif Discovery로 IP Camera와 CCTV 찾는 화면
 */
public class MobileMainActivity extends FragmentActivity implements DeviceInfoConfirmListener, DeviceDeleteListener, NetworkModeListener {


    private OnvifDevice mSelectedOnvifDevice;
    private int mDeviceCount;
    ArrayList<String> ips;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////////////////////////////////////////////////////////////////////////////////////
        //A64보드 월패드는 루팅이 되어 있지 않아 아래 checkNetworkMode를 사용할 수 없음
        //checkNetworkMode();
        ////////////////////////////////////////////////////////////////////////////////////
        init();

    }

    private void init() {
        initIps();
        setDefaultValue();
        initListView();
    }

    private void checkNetworkMode() {
        NetworkModeFinder finder = new NetworkModeFinder(this);
        finder.run();
    }


    private void initIps() {
        ips = new ArrayList<>();
    }

    private void setDefaultValue() {

        mDeviceCount = 6;
    }


    /**
     * 스캔한 디바이스를 리스트에 표시
     *
     * @param device
     */
    public void addDeviceToList(final OnvifDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView deviceList = (ListView) findViewById(R.id.deviceList);
                ((OnvifDeviceListAdapter) deviceList.getAdapter()).addDevice(device);
            }
        });


    }


    /**
     * ListView 초기화
     */
    private void initListView() {
        List<OnvifDevice> devices = new ArrayList<OnvifDevice>();
        ListView deviceList = (ListView) findViewById(R.id.deviceList);
        OnvifDeviceListAdapter adapter = new OnvifDeviceListAdapter(this, R.layout.list_item_device, devices);
        deviceList.setAdapter(adapter);

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedOnvifDevice = (OnvifDevice) parent.getAdapter().getItem(position);

                showPopup(position);

            }
        });
    }

    /**
     * 고정 IP 사용하는지 여부
     *
     * @return
     */
    private boolean isUseStaticIp() {
        CheckBox useStaticIpCheckbox = (CheckBox) findViewById(R.id.useStaticIpCheckbox);

        return useStaticIpCheckbox.isChecked();
    }

    /**
     * 아이디, 비밀번호, 디바이스 별명 팝업창 띄움
     * @param position
     */
    private void showPopup(int position) {
        DeviceInfoPopup popup = new DeviceInfoPopup(this, this);
        popup.setPosition(position);
        popup.setSelectedIpDevice(mSelectedOnvifDevice);
        popup.show();
    }

    /**
     * 디바이스 찾기
     *
     * @param view
     */
    public void findDevice(View view) {

        NetworkOnvifDiscovery discovery = new NetworkOnvifDiscovery();
        discovery.execute();
    }

    /**
     * 사용자가 아이디, 비밀번호, 디바이스 별명을 입력하고 확인버튼을 눌렀을 때 콜백
     *  @param position
     * @param deviceInfo
     */
    @Override
    public void onDeviceInfoConfirmed(int position, OnvifDevice deviceInfo) {

        if (isUserIdAndPasswordValid(deviceInfo)) {
            if(saveMacaddressToDeviceInfo(deviceInfo)) {
                saveDeviceInfoToContentProvider(deviceInfo);
                changeIp(deviceInfo);
                refreshList(position, deviceInfo);
                Toast.makeText(this, "아이디와 비밀번호를 저장했습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "아이디와 비밀번호를 저장하지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "아이디와 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveMacaddressToDeviceInfo(OnvifDevice deviceInfo) {
        //디바이스의 맥어드레스 가져옴
        String macAddress = getMacAddress(deviceInfo);
        deviceInfo.setMac(macAddress);

        if(macAddress == null || macAddress.equals("")) {
            return false;
        } else {
            return true;
        }

    }

    private void refreshList(int position, OnvifDevice deviceInfo) {
        ListView deviceList = (ListView) findViewById(R.id.deviceList);
        OnvifDeviceListAdapter adapter = (OnvifDeviceListAdapter) deviceList.getAdapter();
        adapter.renewRowItem(position, deviceInfo);
    }

    private int changeIp(OnvifDevice deviceInfo) {


        NetworkOnvifRequester requester = new NetworkOnvifRequester(deviceInfo.getIpAddress(), Integer.parseInt(deviceInfo.getPort()), deviceInfo.getId(), deviceInfo.getPassword());
        int result = requester.createDeviceManagementAuthHeader();
        if (result == NetworkOnvifRequester.ERROR_SOCKET_TIMEOUT) {
            return result;
        }

        if ((result = requester.setNetworkInterface(deviceInfo.getNewIPAddress())) > -1) {
            Log.d(DeviceManagerConstants.LOG_TAG, "IP 변경 성공");
        } else {
            Log.d(DeviceManagerConstants.LOG_TAG, "IP 변경 실패");
        }

        return result;


    }

    /**
     * 디바이스 정보를 Content Provider에 저장
     *
     * @param deviceInfo
     */
    private void saveDeviceInfoToContentProvider(OnvifDevice deviceInfo) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_IP, deviceInfo.getNewIPAddress());
        contentValues.put(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_ID, deviceInfo.getId());
        contentValues.put(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_PASSWORD, deviceInfo.getPassword());
        contentValues.put(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_DEVICE_NAME, deviceInfo.getAlias());
        contentValues.put(ContentProviderConstants.OnvifDeviceEntry.COLUMN_NAME_MACADDRESS, deviceInfo.getMac());


        ContentProviderManager.saveOnvifDevice(this, contentValues);

    }

    /**
     * 사용자 아이디와 비밀번호가 유효한 지 체크
     *
     * @param deviceInfo
     * @return
     */
    private boolean isUserIdAndPasswordValid(OnvifDevice deviceInfo) {

        NetworkOnvifRequester requester = new NetworkOnvifRequester(deviceInfo.getIpAddress(), Integer.parseInt(deviceInfo.getPort()), deviceInfo.getId(), deviceInfo.getPassword());
        int result = requester.createDeviceManagementAuthHeader();
        if (result == NetworkOnvifRequester.ERROR_SOCKET_TIMEOUT) {
            return false;
        }

        if ((result = requester.createMediaManagementAuthHeader()) > -1) {
            if ((result = requester.GetProfiles()) > -1) {
                int profileSize = requester.mGetProfilesResponse.mProfiles.size();
                if (profileSize > 0) {
                    for (int j = 0; j < profileSize; j++) {
                        if ((result = requester.GetStreamUri(requester.mGetProfilesResponse.mProfiles.get(j).mToken)) > -1) {
                            try {
                                requester.mGetStreamUriResponses.get(j).mMediaUri.mUri = requester.mGetStreamUriResponses.get(j).mMediaUri.mUri
                                        .replace("127.0.0.1", mSelectedOnvifDevice.getIpAddress());

                                return true;
                            } catch (Exception e) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 리스트에서 OnVif 디바이스를 삭제한 경우
     */
    @Override
    public void onDelete() {
        //필요한 경우 추가 처리!!
        mDeviceCount--;
    }

    /**
     * 네트워크 모드 찾은 다음 콜백
     * @param dhcpDiscoverResult
     */
    @Override
    public void onFind(int dhcpDiscoverResult) {
        //고정 IP 사용 체크박스 초기값 표시
        CheckBox useStaticIpCheckbox = (CheckBox) findViewById(R.id.useStaticIpCheckbox);
        useStaticIpCheckbox.setChecked(dhcpDiscoverResult == NetworkModeFinder.DHCP_NOT_EXIST? true: false);
        useStaticIpCheckbox.setEnabled(false);


    }

    /**
     * WS-Discovery 실행
     */
    public class NetworkOnvifDiscovery extends AsyncTask<Void, Void, Integer> {

        private ProgressDialog mProgressDialog;

        public NetworkOnvifDiscovery() {

        }

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(MobileMainActivity.this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(MobileMainActivity.this.getString(R.string.loading));
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            String macAddress = null;
            int result = NetworkOnvifRequester.ERROR_UNKNOWN;
            //마지막 파라미터를 null로 주면 모든 IP에 대해 스캔
            OnvifProbe probe = new OnvifProbe(MobileMainActivity.this, null); //mIPAddress);
            ArrayList<ProbeMatch> probeMatch = probe.sendProbeMessage();
            if (probeMatch != null && probeMatch.size() > 0) {


                result = NetworkOnvifRequester.SUCCESS;

                String newIP = null;

                for (int i = 0; i < probeMatch.size(); i++) {


                    mDeviceCount++;
                    //ip 변경 처리
                    //ip camera와 cctv 디바이스 번호 수정!!
                    //ip가 유효하지 않으면(예를 들어 1234-5678같이 잘못된 동호수가 입력되어)
                    //ip 변경시 invalid ip라는 오류 생김

                    if(isUseStaticIp()) {
                        newIP = "";

                    } else {
                        newIP = NewIPAddressManager.getIP(MobileMainActivity.this, mDeviceCount);
                    }



                    //해당 디바이스가 이미 스캔된 경우 pass
                    if(isIpExistOnContentProvider(newIP)) {
                        continue;
                    }

                    //해당 디바이스가 이미 스캔된 경우 pass
                    if(ips.contains(probeMatch.get(i).mOnvifIPAddress)) {
                        continue;
                    }
                    ips.add(probeMatch.get(i).mOnvifIPAddress);


                    OnvifDevice onvifDevice = new OnvifDevice();
                    onvifDevice.setIpAddress(probeMatch.get(i).mOnvifIPAddress);
                    onvifDevice.setName(probeMatch.get(i).mOnvifVendorModel);
                    onvifDevice.setPort(String.valueOf(probeMatch.get(i).mOnvifPort));
                    onvifDevice.setIsUseStaticIp(isUseStaticIp()? DeviceInfoConstants.USE_STATIC_IP: DeviceInfoConstants.NOT_USE_STATIC_IP);
                    onvifDevice.setNewIPAddress(newIP);

                    onvifDevice.setId("");
                    onvifDevice.setPassword("");

                    addDeviceToList(onvifDevice);

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

    private String getMacAddress(OnvifDevice device) {
        NetworkOnvifRequester requester = new NetworkOnvifRequester(device.getIpAddress(), Integer.parseInt(device.getPort()), device.getId(), device.getPassword());
        int result = requester.createDeviceManagementAuthHeader();
        if (result == NetworkOnvifRequester.ERROR_SOCKET_TIMEOUT) {
            return "";
        }

        if ((result = requester.getNetworkInterface()) > -1) {

            Toast.makeText(this, "맥어드레스: " + requester.mGetNetworkResponse.mNetworkInterfacesInfoHwAddress, Toast.LENGTH_SHORT).show();
            return requester.mGetNetworkResponse.mNetworkInterfacesInfoHwAddress;

        } else {
            Toast.makeText(this, "GetNetworkInterface 사용 불가", Toast.LENGTH_SHORT).show();
        }

        return "";
    }

    private boolean isIpExistOnContentProvider(String ip) {
        return ContentProviderManager.isIpExistOnContentProvider(this, ip);

    }


}
