package com.commax.settings.cctv;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.commax.settings.R;
import com.commax.settings.common.TypeDef;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;

import java.util.ArrayList;
import java.util.List;

/**
 * Onvif로 CCTV 등록
 * Created by bagjeong-gyu on 2016. 9. 27..
 */

public class CctvRegistrationByOnVifFragment2 extends Fragment{
    private static final String LOG_TAG = CctvRegistrationByOnVifFragment2.class.getSimpleName();

    public CctvRegistrationByOnVifFragment2() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cctv_registration2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addButtonListener();

    }

    @Override
    public void onResume() {
        super.onResume();

        initRegisteredCctvListView();

        //2017-01-03,yslee::add후 edit버튼 비활성 문제
        List<OnvifDevice> devices = getRegisteredDevice();

        if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리
            Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
            Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
            if (devices == null || devices.size() == 0) {
                editBtn.setVisibility(View.INVISIBLE);
                deleteBtn.setVisibility(View.INVISIBLE);
            } else {
                editBtn.setVisibility(View.VISIBLE);
                deleteBtn.setVisibility(View.VISIBLE);
            }

        } else {
            Button edit = (Button) getActivity().findViewById(R.id.edit);
            if (devices == null || devices.size() == 0) {
                edit.setEnabled(false);
            } else {
                edit.setEnabled(true);
            }
        }

    }

    /**
     * 등록된 CCTV ListView 초기화
     */
    private void initRegisteredCctvListView() {
        //원래 코드
        //테스트후 주석 푸세요!!
        List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<OnvifDevice> devices = getRegisteredDeviceTest();

        final ListView deviceList = (ListView) getActivity().findViewById(R.id.registeredCctvList);
        OnvifRegisteredCctvListAdapter adapter = new OnvifRegisteredCctvListAdapter(getActivity(), R.layout.list_item_registered_cctv, devices);
        deviceList.setAdapter(adapter);
    }

    private List<OnvifDevice> getRegisteredDeviceTest() {

        List<OnvifDevice> devices = new ArrayList<>();

        OnvifDevice device1 = new OnvifDevice();
        device1.setIpAddress("192.168.1.11");
        device1.setPort("80");
        device1.setName("CCTV 1");
        device1.setStreamUrl("rtsp://test/test");
        device1.setId("admin");
        device1.setPassword("123456");

        OnvifDevice device2 = new OnvifDevice();
        device2.setIpAddress("192.168.1.20");
        device2.setPort("80");
        device2.setName("CCTV 2");
        device2.setStreamUrl("rtsp://test/test2");
        device2.setId("admin");
        device2.setPassword("123456");

        devices.add(device1);
        devices.add(device2);

        return devices;
    }


    /**
     * Content provider에 등록된 디바이스 가져옴
     *
     * @return
     */
    private List<OnvifDevice> getRegisteredDevice() {
        return ContentProviderManager.getAllOnvifCctv(getActivity());
    }

    private void addButtonListener() {

        LinearLayout register = (LinearLayout) getActivity().findViewById(R.id.registerCctv);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Onvif 프로토콜 사용하여 CCTV 카메라 등록
                launchRegisterActivity();
            }
        });

        if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리

            Button edit = (Button) getActivity().findViewById(R.id.edit);
            edit.setVisibility(View.INVISIBLE);

            Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchEditActivity();
                    //Toast.makeText(getActivity(), "현관 editBtn 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });

            Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchDeleteActivity();
                    //Toast.makeText(getActivity(), "현관 deleteBtn 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Button edit = (Button) getActivity().findViewById(R.id.edit);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlusClickGuard.doIt(v);
                    launchEditActivity();
                }
            });
        }

        //원래 코드
        //테스트후 주석 푸세요!!
        List<OnvifDevice> devices = getRegisteredDevice();

        //테스트용
        //테스트후 삭제하세요!!
        //List<OnvifDevice> devices = getRegisteredDeviceTest();

        if (devices == null || devices.size() == 0) {
            return;
        } else {
            if(TypeDef.OP_SAPERATE_EDIT_DEL_ENABLE) { //2017-02-06,yslee::현관,CCTV 편집 및 삭제 메뉴분리
                Button editBtn = (Button) getActivity().findViewById(R.id.editBtn);
                editBtn.setVisibility(View.VISIBLE);
                Button deleteBtn = (Button) getActivity().findViewById(R.id.deleteBtn);
                deleteBtn.setVisibility(View.VISIBLE);

            } else {
                Button edit = (Button) getActivity().findViewById(R.id.edit);
                edit.setEnabled(true);
            }
        }

    }

    /**
     * CCTV 자동등록 액티비티로 이동
     */
    private void launchRegisterActivity() {
        Intent intent = new Intent(getActivity(), CctvRegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * 등록된 CCTV 편집 액티비티로 이동
     */
    private void launchEditActivity() {
        Intent intent = new Intent(getActivity(), CctvEditActivity.class);
        startActivity(intent);
    }

    /**
     * 등록된 CCTV 삭제 액티비티로 이동
     */
    public void launchDeleteActivity() {
        Intent intent = new Intent(getActivity(), CctvDeleteActivity.class);
        startActivity(intent);

    }

}
