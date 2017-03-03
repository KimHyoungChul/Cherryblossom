package com.commax.settings.doorphone_onvif;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.commax.settings.R;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 도어폰 카메라 편집 리스트 어댑터
 * Created by bagjeong-gyu on 2016. 8. 22..
 */
public class OnvifEditDooorphoneCameraListAdapter extends ArrayAdapter<OnvifDevice> {


    private OnvifNameChangeListener mListener;
    private String LOG_TAG = OnvifEditDooorphoneCameraListAdapter.class.getSimpleName();
    private final LayoutInflater mLayoutInflater;
    private final List<OnvifDevice> mDatas;
    private final Context mContext;


    public OnvifEditDooorphoneCameraListAdapter(Context context, int resource, List<OnvifDevice> devices) {
        super(context, resource, devices);
        mLayoutInflater = LayoutInflater.from(context);
        mDatas = devices;
        mContext = context;

        try {
            mListener = (OnvifNameChangeListener) context;
        } catch (ClassCastException e) {
            Log.d(LOG_TAG, "ClassCastException: " + e.getMessage());
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_edit_doorphone_camera,
                    parent, false);
        }


        //카메라 명
        final EditText deviceNameInput = PlusViewHolder.get(convertView, R.id.deviceNameInput);

        String deviceNameString = mDatas.get(position).getName();
        if (deviceNameString == null) {
            deviceNameString = "";
        }

        deviceNameInput.setText(deviceNameString);
        deviceNameInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS ); //2017-01-23,yslee::입력자판 변경

        //삭제 버튼
        //아래 코드로 하나만 삭제버튼이 표시(convertView라서 리사이클링 영향인 듯)
        //처음에는 안보이게 처리
        final Button deleteTextButton = PlusViewHolder.get(convertView, R.id.deleteText);
        deleteTextButton.setVisibility(View.GONE);


        //텍스트 변화 처리
        deviceNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (keyword.length() > 0) {
                    showDeleteButton(deleteTextButton, deviceNameInput);
                } else {
                    hideDeleteButton(deleteTextButton, deviceNameInput);
                }

            }
        });

        //EditText 포커스 처리
        deviceNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    mListener.onNameChanged();

                    if (deviceNameInput.getText().toString().length() > 0) {
                        showDeleteButton(deleteTextButton, deviceNameInput);
                    } else {
                        hideDeleteButton(deleteTextButton, deviceNameInput);
                    }
                }
            }
        });


        return convertView;
    }

    /**
     * 삭제 버튼 숨김
     *
     * @param deleteTextButton
     * @param deviceNameInput
     */
    private void hideDeleteButton(Button deleteTextButton, final EditText deviceNameInput) {
        if (!deleteTextButton.isShown()) {
            return;
        }

        deleteTextButton.setVisibility(View.GONE);
        deleteTextButton.setOnClickListener(null);

    }

    /**
     * 삭제 버튼 표시
     *
     * @param deleteTextButton
     * @param deviceNameInput
     */
    private void showDeleteButton(Button deleteTextButton, final EditText deviceNameInput) {
        if (deleteTextButton.isShown()) {
            return;
        }

        deleteTextButton.setVisibility(View.VISIBLE);
        deleteTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceNameInput.setText("");
            }
        });
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

    public List<OnvifDevice> getAllData() {
        return mDatas;
    }
}
