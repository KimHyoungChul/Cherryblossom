package com.commax.settings.doorphone_custom;

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
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 커스텀 도어폰 카메라 리스트 어댑터 => 테스트용
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class CustomUnregisteredDoorphoneCameraListAdapter extends ArrayAdapter<CustomDevice> {

    private String LOG_TAG = CustomUnregisteredDoorphoneCameraListAdapter.class.getSimpleName();
    private CustomPreviewRunListener mListener;
    private final LayoutInflater mLayoutInflater;
    private final List<CustomDevice> mDatas;
    private final Context mContext;


    public CustomUnregisteredDoorphoneCameraListAdapter(Context context, int resource, List<CustomDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_scanned_doorphone_camera,
                    parent, false);
        }

        //체크박스
        //ImageView deviceCheckbox = PlusViewHolder.get(convertView, R.id.deviceCheckbox);
        //deviceCheckbox.setSelected(false);
        //deviceCheckbox.setVisibility(View.GONE); //숨김

        //카메라 명
        TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        String deviceNameString = mDatas.get(position).getNickName();
        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceName.setText(deviceNameString);


        //카메라 ip
        TextView deviceIp = PlusViewHolder.get(convertView, R.id.deviceIp);
        String ipString = mDatas.get(position).getIpv4();

        deviceIp.setText(ipString);

        //IP 설정 버튼
        Button ipConfig = PlusViewHolder.get(convertView, R.id.ipConfig);
        ipConfig.setVisibility(View.GONE); //숨김
        ipConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                ipConfig(mDatas.get(position));
            }
        });


        //미리보기 버튼
        Button preview = PlusViewHolder.get(convertView, R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);

                //runPreview(mDatas.get(position).getIpv4());
                mListener.onrunPreview(mDatas.get(position));
            }
        });

        return convertView;
    }

    /**
     * IP 설정
     *
     * @param device
     */
    private void ipConfig(CustomDevice device) {
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
    public void addDevice(CustomDevice device) {

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

    public ArrayList<CustomDevice> getDevices() {
        return (ArrayList<CustomDevice>) mDatas;
    }

    /**
     * 리스트에서 제거
     * @param device
     */
    public void deleteDevice(CustomDevice device) {
        mDatas.remove(device);
        notifyDataSetChanged();
    }

    public void setListener(CustomPreviewRunListener listener) {
        mListener = listener;
    }
}
