package com.commax.settings.doorphone_custom;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 등록된 도어폰 카메라 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class CustomRegisteredDooorphoneCameraListAdapter extends ArrayAdapter<CustomDevice> {


    private String LOG_TAG = CustomRegisteredDooorphoneCameraListAdapter.class.getSimpleName();
    private CustomPreviewRunListener mListener;
    private final LayoutInflater mLayoutInflater;
    private final List<CustomDevice> mDatas;
    private final Context mContext;


    public CustomRegisteredDooorphoneCameraListAdapter(Context context, int resource, List<CustomDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_registered_doorphone_camera,
                    parent, false);
        }


        //카메라 명
        TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        //String deviceNameString = mDatas.get(position).getName();
        String deviceNameString = mDatas.get(position).getModelName();
        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceName.setText(deviceNameString);


        //카메라 ip
        TextView deviceIp = PlusViewHolder.get(convertView, R.id.deviceIp);
        String ipString = mDatas.get(position).getIpv4();

        deviceIp.setText(ipString);

        //카메라 상태
        ImageView deviceStatus = PlusViewHolder.get(convertView, R.id.deviceStatus);
        deviceStatus.setImageResource((mDatas.get(position).getIsOk() != null && mDatas.get(position).getIsOk().equals(CommaxConstants.TRUE)) ? R.drawable.circle_status_ok : R.drawable.circle_status_not_ok);

        //미리보기/재설정
        Button previewReconfigure = PlusViewHolder.get(convertView, R.id.previewReconfigure);

        //카메라 상태가 ok
        mDatas.get(position).setIsOk(CommaxConstants.TRUE); //임시
        if (mDatas.get(position).getIsOk() != null && mDatas.get(position).getIsOk().equals(CommaxConstants.TRUE)) {
            deviceStatus.setImageResource(R.drawable.circle_status_ok);
            previewReconfigure.setText(R.string.preview);
            previewReconfigure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    if(TypeDef.OP_CUSTOM_DOORCAMERA_ENABLE) {
                        mListener.onrunPreview(mDatas.get(position));
                    } else {
                        preview(mDatas.get(position));
                    }
                }
            });


            //카메라 상태가 not ok
        } else {
            deviceStatus.setImageResource(R.drawable.circle_status_not_ok);
            previewReconfigure.setText(R.string.reconfigure);
            previewReconfigure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    reconfigure(mDatas.get(position));
                }
            });


        }


        return convertView;
    }

    /**
     * 재설정
     *
     * @param device
     */
    private void reconfigure(CustomDevice device) {
        //구현 필요!!
        //현관카메라 등록절차와 동일
    }

    /**
     * 미리보기
     *
     * @param device
     */
    private void preview(CustomDevice device) {

        //현관 모니터링 앱으로 브로드캐스트 전송
        Intent intent = new Intent(CommaxConstants.BROADCAST_DOOR_MONITOR);
        intent.putExtra(CommaxConstants.KEY_FROM, CommaxConstants.PREVIEW);
        intent.putExtra(CommaxConstants.KEY_IP, device.getIpv4());
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

    public List<CustomDevice> getAllData() {
        return mDatas;
    }

    public void setListener(CustomPreviewRunListener listener) {
        mListener = listener;
    }
}
