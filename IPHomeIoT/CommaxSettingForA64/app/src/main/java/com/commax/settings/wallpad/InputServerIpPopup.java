package com.commax.settings.wallpad;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.setting_provider.DeviceServerIp;
import com.commax.settings.setting_provider.LocalServerIp;
import com.commax.settings.setting_provider.OnBooleanCallback;
import com.commax.settings.setting_provider.UpdateServerIp;
import com.commax.settings.util.PlusClickGuard;

import static com.commax.settings.R.id.deviceServerIpInput;

/**
 * 디바이스 서버 IP 입력 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class InputServerIpPopup extends Dialog {

    public final static int DEVICE_IP_MODE = 1;
    public final static int UPDATE_IP_MODE = 2;
    public final static int LOCAL_IP_MODE = 3;

    private final Context mContext;
    private final InputServerIpSetListener mListener;
    private int mIPtype;


    public InputServerIpPopup(Context context, InputServerIpSetListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_device_server_ip);

        addButtonListener();
    }

    public void setPopupTitle(String title) {
        TextView popupTitle = (TextView) findViewById(R.id.progress_title);
        popupTitle.setText(title);
    }

    public void setIpType(int iptype) {
        mIPtype = iptype;
    }

    private void addButtonListener() {
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                dismiss();
            }
        });

        Button confirm = (Button) findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlusClickGuard.doIt(v);
                /**
                 * 기존의 setting content provider 사용
                 */

                switch(mIPtype) {
                    case DEVICE_IP_MODE:
                        saveDeviceServerIpToContentProvider();
                        break;
                    case UPDATE_IP_MODE:
                        saveUpdateServerIpToContentProvider();
                        break;
                    case LOCAL_IP_MODE:
                        saveLocalServerIpToContentProvider();
                        break;
                    default :
                        break;
                }



            }
        });
    }


    /**
     * 기존의 setting content provider 사용
     */
    /**
     * 디바이스 서버 IP 저장
     */
    private boolean saveDeviceServerIpToContentProvider() {
        EditText deviceServerIpInput = (EditText) findViewById(R.id.deviceServerIpInput);
        String deviceServerIp = deviceServerIpInput.getText().toString();

        if (deviceServerIp.equals("")) {
            Toast.makeText(mContext, R.string.put_in_device_server_ip, Toast.LENGTH_SHORT).show();
            return false;
        }


        new DeviceServerIp(mContext).setValueInBackground(deviceServerIp,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onDeviceServerIpSet();
                        } else {
                            Toast.makeText(mContext, R.string.not_saved, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        return true;
    }

    /**
     * 기존의 setting content provider 사용
     */
    /**
     * 업데이트 서버 IP 저장
     */
    private boolean saveUpdateServerIpToContentProvider() {
        EditText updateServerIpInput = (EditText) findViewById(R.id.deviceServerIpInput);
        String updateServerIp = updateServerIpInput.getText().toString();

        if (updateServerIp.equals("")) {
            Toast.makeText(mContext, R.string.put_in_device_server_ip, Toast.LENGTH_SHORT).show();
            return false;
        }


        new UpdateServerIp(mContext).setValueInBackground(updateServerIp,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onUpdateServerIpSet();
                        } else {
                            Toast.makeText(mContext, R.string.not_saved, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        return true;
    }


    /**
     * 기존의 setting content provider 사용
     */
    /**
     * 단지 서버 IP 저장
     */
    private boolean saveLocalServerIpToContentProvider() {
        EditText localServerIpInput = (EditText) findViewById(deviceServerIpInput);
        String localServerIp = localServerIpInput.getText().toString();

        if (localServerIp.equals("")) {
            Toast.makeText(mContext, R.string.put_in_device_server_ip, Toast.LENGTH_SHORT).show();
            return false;
        }


        new LocalServerIp(mContext).setValueInBackground(localServerIp,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onLocalServerIpSet();
                        } else {
                            Toast.makeText(mContext, R.string.not_saved, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        return true;
    }

    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public InputServerIpPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
