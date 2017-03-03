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
import com.commax.settings.setting_provider.CenterPhoneNumber;
import com.commax.settings.setting_provider.GuardPhoneNumber;
import com.commax.settings.setting_provider.OnBooleanCallback;
import com.commax.settings.util.PlusClickGuard;

/**
 * 디바이스 서버 IP 입력 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class InputPhoneNumberPopup extends Dialog {

    public final static int GUARD_PHONE_MODE = 1;
    public final static int CENTER_PHONE_MODE = 2;

    private final Context mContext;
    private final InputPhoneNumberSetListener mListener;
    private int mPhoneType;


    public InputPhoneNumberPopup(Context context, InputPhoneNumberSetListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_phone_number);

        addButtonListener();
    }

    public void setPopupTitle(String title) {
        TextView popupTitle = (TextView) findViewById(R.id.progress_title);
        popupTitle.setText(title);
    }

    public void setPhoneNumberType(int phonetype) {
        mPhoneType = phonetype;
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

                switch(mPhoneType) {
                    case GUARD_PHONE_MODE:
                        saveGuardNumberContentProvider();
                        break;
                    case CENTER_PHONE_MODE:
                        saveCenterNumberContentProvider();
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
     * 경비실 번호 저장
     */
    private boolean saveGuardNumberContentProvider() {
        EditText phoneNumberInput = (EditText) findViewById(R.id.phoneNumberIpInput);
        String getNumber = phoneNumberInput.getText().toString();

        if (getNumber.equals("")) {
            Toast.makeText(mContext, R.string.put_in_phonenumber, Toast.LENGTH_SHORT).show();
            return false;
        }


        new GuardPhoneNumber(mContext).setValueInBackground(getNumber,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onGuardPhoneNumberSet();
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
     * 관리실 번호 저장
     */
    private boolean saveCenterNumberContentProvider() {
        EditText phoneNumberInput = (EditText) findViewById(R.id.phoneNumberIpInput);
        String getNumber = phoneNumberInput.getText().toString();

        if (getNumber.equals("")) {
            Toast.makeText(mContext, R.string.put_in_phonenumber, Toast.LENGTH_SHORT).show();
            return false;
        }


        new CenterPhoneNumber(mContext).setValueInBackground(getNumber,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onCenterPhoneNumberSet();
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
    public InputPhoneNumberPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
