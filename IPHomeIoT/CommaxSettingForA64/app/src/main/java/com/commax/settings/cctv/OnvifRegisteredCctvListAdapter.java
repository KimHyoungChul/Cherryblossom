package com.commax.settings.cctv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 등록된 CCTV 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class OnvifRegisteredCctvListAdapter extends ArrayAdapter<OnvifDevice> {


    private String LOG_TAG = OnvifRegisteredCctvListAdapter.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private final List<OnvifDevice> mDatas;
    private final Context mContext;


    public OnvifRegisteredCctvListAdapter(Context context, int resource, List<OnvifDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_registered_cctv,
                    parent, false);
        }


        //카메라 명
        TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        String deviceNameString = mDatas.get(position).getName();
        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceName.setText(deviceNameString);


        //카메라 ip
        TextView deviceIp = PlusViewHolder.get(convertView, R.id.deviceIp);
        String ipString = mDatas.get(position).getIpAddress();

        deviceIp.setText(ipString);


        //미리보기/재설정
        Button preview = PlusViewHolder.get(convertView, R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                preview(mDatas.get(position));
            }
        });


        return convertView;
    }


    /**
     * 미리보기
     *
     * @param device
     */
    private void preview(OnvifDevice device) {

        //CCTV 앱 실행
        Intent intent = new Intent();
        intent.setClassName(CommaxConstants.PACKAGE_CCTV, CommaxConstants.ACTIVITY_CCTV);
        intent.putExtra(CommaxConstants.KEY_IP, device.getIpAddress());
        intent.putExtra(CommaxConstants.KEY_PORT, device.getPort());
        intent.putExtra(CommaxConstants.KEY_ID, device.getId());
        intent.putExtra(CommaxConstants.KEY_PASSWORD, device.getPassword());
        intent.putExtra(CommaxConstants.KEY_RTSP_URL, device.getStreamUrl());

        if (isAvailable(intent)) {
            mContext.startActivity(intent);
        } else {
            Toast.makeText(mContext, R.string.app_is_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 앱이 실행 가능한지 체크
     *
     * @param intent
     * @return
     */
    public boolean isAvailable(Intent intent) {
        final PackageManager mgr = mContext.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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


    public ArrayList<OnvifDevice> getDevices() {
        return (ArrayList<OnvifDevice>) mDatas;
    }
}
