package com.commax.settings.doorphone_onvif;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.doorphone_custom.IpConfigActivity;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 미등록된 도어폰 카메라 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class OnvifUnregisteredDoorphoneCameraListAdapter extends ArrayAdapter<OnvifDevice> {


    private String LOG_TAG = OnvifUnregisteredDoorphoneCameraListAdapter.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private final List<OnvifDevice> mDatas;
    private final Context mContext;


    public OnvifUnregisteredDoorphoneCameraListAdapter(Context context, int resource, List<OnvifDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_unregistered_doorphone_camera,
                    parent, false);
        }

        //체크박스
        //ImageView deviceCheckbox = PlusViewHolder.get(convertView, R.id.deviceCheckbox);
        //deviceCheckbox.setSelected(false);

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

        //IP 설정 버튼
        Button ipConfig = PlusViewHolder.get(convertView, R.id.ipConfig);
        ipConfig.setVisibility(View.GONE);
//        ipConfig.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlusClickGuard.doIt(v);
//                ipConfig(mDatas.get(position));
//            }
//        });


        //미리보기 버튼
        Button preview = PlusViewHolder.get(convertView, R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                runPreview(mDatas.get(position).getIpAddress());
            }
        });

        return convertView;
    }

    /**
     * IP 설정
     *
     * @param onvifDevice
     */
    private void ipConfig(OnvifDevice onvifDevice) {
        //수정 필요할 듯!!
        Intent intent = new Intent(mContext, IpConfigActivity.class);
        mContext.startActivity(intent);

    }

    /**
     * 미리보기
     *
     * @param ipAddress
     */
    private void runPreview(String ipAddress) {
        //현관 모니터링 앱으로 브로드캐스트 전송
        Log.d(LOG_TAG, "preview ip: " + ipAddress);
        Intent intent = new Intent(CommaxConstants.BROADCAST_DOOR_MONITOR);
        intent.putExtra(CommaxConstants.KEY_FROM, CommaxConstants.PREVIEW);
        intent.putExtra(CommaxConstants.KEY_IP, ipAddress);
        mContext.sendBroadcast(intent);


    }

    /**
     * Device 추가
     *
     * @param device
     */
    public void addDevice(OnvifDevice device) {

        mDatas.add(device);
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

    public ArrayList<OnvifDevice> getDevices() {
        return (ArrayList<OnvifDevice>) mDatas;
    }
}
