package com.commax.settings.cctv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 미등록된 CCTV 디바이스 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class OnvifUnregisteredCctvListAdapter extends ArrayAdapter<OnvifDevice> {


    private CctvIdPasswordConfirmListener mListener;
    private String LOG_TAG = OnvifUnregisteredCctvListAdapter.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private final List<OnvifDevice> mDatas;
    private final Context mContext;


    public OnvifUnregisteredCctvListAdapter(Context context, int resource, List<OnvifDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if(TypeDef.OP_NEW_CCTV_SCANMODE_ENABLE) { //2017-02-07,yslee::CCTV Scan방법을 현관과 동일하게 변경
                convertView = mLayoutInflater.inflate(R.layout.list_item_unregistered_cctv2, parent, false);
            } else {
                convertView = mLayoutInflater.inflate(R.layout.list_item_unregistered_cctv2, parent, false);
            }
        }


        //카메라 명
        TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        String deviceNameString = mDatas.get(position).getNickName(); //2017-01-06,yslee::닉네임 항목 추가
        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceName.setText(deviceNameString);


        //카메라 ip
        TextView deviceIp = PlusViewHolder.get(convertView, R.id.deviceIp);
        String ipString = mDatas.get(position).getIpAddress();

        deviceIp.setText(ipString);


        //미리보기 버튼
        Button preview = PlusViewHolder.get(convertView, R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                //CCTV 아이디와 비밀번호 입력 팝업창 표시
                showCctvIdPasswordPopup(mDatas.get(position));
                 //시나리오 변경으로 아래 코드 사용안함
                //runPreview(mDatas.get(position));
            }
        });

        return convertView;
    }

    /**
     * CCTV 아이디와 비밀번호 입력 팝업창 표시
     * @param onvifDevice
     */
    private void showCctvIdPasswordPopup(OnvifDevice onvifDevice) {
        CctvIdPasswordPopup popup = new CctvIdPasswordPopup(mContext, mListener);
        popup.setDevice(onvifDevice);
        popup.show();
    }

//    /**
//     * 미리보기
//     *
//     * @param device
//     */
//    private void runPreview(OnvifDevice device) {
//        //CCTV 앱 실행
//        Intent intent = new Intent();
//        intent.setClassName(CommaxConstants.PACKAGE_CCTV, CommaxConstants.ACTIVITY_CCTV);
//        intent.putExtra(CommaxConstants.KEY_IP, device.getIpAddress());
//        intent.putExtra(CommaxConstants.KEY_PORT, device.getPort());
//        intent.putExtra(CommaxConstants.KEY_ID, device.getId());
//        intent.putExtra(CommaxConstants.KEY_PASSWORD, device.getPassword());
//        intent.putExtra(CommaxConstants.KEY_RTSP_URL, device.getStreamUrl());
//
//        if (isAvailable(intent)) {
//            mContext.startActivity(intent);
//        } else {
//            Toast.makeText(mContext, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    /**
//     * 앱이 실행 가능한지 체크
//     *
//     * @param intent
//     * @return
//     */
//    public boolean isAvailable(Intent intent) {
//        final PackageManager mgr = mContext.getPackageManager();
//        List<ResolveInfo> list =
//                mgr.queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//        return list.size() > 0;
//    }

    /**
     * Device 추가
     *
     * @param device
     */
    public void addDevice(OnvifDevice device) {

        mDatas.add(device);
        notifyDataSetChanged();
    }


    public ArrayList<OnvifDevice> getDevices() {
        return (ArrayList<OnvifDevice>) mDatas;
    }


//    /**
//     * 미등록된 CCTV가 등록된 경우 리스트에서 제거
//     *
//     * @param devices
//     */
//    public void deleteDevices(ArrayList<OnvifDevice> devices) {
//
//        for (OnvifDevice device : devices) {
//            mDatas.remove(device);
//        }
//
//        notifyDataSetChanged();
//    }

    /**
     * 미등록된 CCTV가 등록된 경우 리스트에서 제거
     * @param device
     */
    public void deleteDevice(OnvifDevice device) {
        mDatas.remove(device);
        notifyDataSetChanged();
    }

    /**
     * Device 삭제
     *
     * @param position
     */
    public void delDevice(int position) {

        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void setListener(CctvIdPasswordConfirmListener listener) {
        mListener = listener;
    }

}
