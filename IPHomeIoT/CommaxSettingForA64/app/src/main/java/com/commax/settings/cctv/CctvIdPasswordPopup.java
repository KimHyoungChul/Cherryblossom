package com.commax.settings.cctv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.commax.settings.R;
import com.commax.settings.content_provider.CommaxConstants;
import com.commax.settings.onvif.OnvifDevice;
import com.commax.settings.util.PlusClickGuard;

/**
 * CCTV 아이디 비밀번호 입력 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CctvIdPasswordPopup extends Dialog {


    private final Context mContext;
    private final CctvIdPasswordConfirmListener mListener;
    private OnvifDevice mDevice;


    public CctvIdPasswordPopup(Context context, CctvIdPasswordConfirmListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_cctv_id_password);
        showDefaultIdPassword();
        addButtonListener();
    }

    private void showDefaultIdPassword() {

        EditText idInput = (EditText) findViewById(R.id.idInput);

        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);

        //나중에 수정이 필요할 수 있음
        idInput.setText(CommaxConstants.CCTV_DEFAULT_ID); //CCTV_DEFAULT_ID
        passwordInput.setText(CommaxConstants.CCTV_DEFAULT_PW);  //CCTV_DEFAULT_PW
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
                dismiss();
                String id = getId();
                String password = getPassword();
                mDevice.setId(id);
                mDevice.setPassword(password);
                mListener.onCctvIdPasswordConfirm(mDevice);

            }
        });
    }

    private String getPassword() {
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        return passwordInput.getText().toString();
    }

    private String getId() {
        EditText idInput = (EditText) findViewById(R.id.idInput);
        return idInput.getText().toString();
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public CctvIdPasswordPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


    public void setDevice(OnvifDevice onvifDevice) {
        mDevice = onvifDevice;
    }
}
