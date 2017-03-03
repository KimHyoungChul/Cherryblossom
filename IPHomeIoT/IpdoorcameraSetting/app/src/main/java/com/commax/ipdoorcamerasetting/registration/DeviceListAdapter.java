package com.commax.ipdoorcamerasetting.registration;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.ipdoorcamerasetting.R;
import com.commax.ipdoorcamerasetting.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 디바이스 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class DeviceListAdapter extends ArrayAdapter<Device> {


    private final LayoutInflater mLayoutInflater;
    private final List<Device> mDatas;
    private final Context mContext;


    public DeviceListAdapter(Context context, int resource, List<Device> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;


    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_device,
                    parent, false);
        }


        TextView deviceName = PlusViewHolder.get(convertView, R.id.deviceName);

        String deviceNameString = mDatas.get(position).getModelName();
        deviceName.setText(deviceNameString);


        //프리뷰 버튼
        Button preview = PlusViewHolder.get(convertView, R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runPreview();
            }
        });

        return convertView;
    }

    /**
     * 미리보기
     */
    private void runPreview() {
        //버튼 클릭되는지 테스트!!
        Toast.makeText(mContext, "미리보기 버튼 클릭", Toast.LENGTH_SHORT).show();

    }

    /**
     * Device 추가
     * @param device
     */
    public void addDevice(Device device) {

        mDatas.add(device);
        notifyDataSetChanged();
    }


    public ArrayList<Device> getDevices() {
        return (ArrayList<Device>) mDatas;
    }
}
