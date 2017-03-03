package com.commax.settings.wallpad;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.setting_provider.OnBooleanCallback;
import com.commax.settings.util.PlusClickGuard;

/**
 * 비밀번호 변경 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class ChangePasswordPopup extends Dialog {


    private final Context mContext;
    private final WallpadPasswordChangeListener mListener;


    public ChangePasswordPopup(Context context, WallpadPasswordChangeListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_change_password);

        addButtonListener();
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

                savePasswordToContentProvider();

//기존 코드
//                if (isPasswordSaved()) {
//                    mListener.onWallpadPasswordChanged();
//                    dismiss();
//                }
            }
        });
    }

//    /**
//       * 새로운 content provider 사용
//     * 비밀번호 저장
//     */
//    private boolean isPasswordSaved() {
//        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
//        EditText passwordConfirmInput = (EditText) findViewById(R.id.passwordConfirmInput);
//
//        String password = passwordInput.getText().toString();
//        String passwordConfirm = passwordConfirmInput.getText().toString();
//
//        if (password.equals("")) {
//            Toast.makeText(mContext, mContext.getString(R.string.put_in_password), Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (password.length() < 4) {
//            Toast.makeText(mContext, R.string.put_in_4_digit_password, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (passwordConfirm.equals("") || !password.equals(passwordConfirm)) {
//            Toast.makeText(mContext, R.string.password_is_not_same, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ContentProviderConstants.WallpadPasswordEntry.COLUMN_NAME_PASSWORD, password);
//
//
//        ContentProviderManager.deletePreviousWallpadPassword(mContext);
//        ContentProviderManager.saveWallpadPassword(mContext, contentValues);
//
//        return true;
//    }

    /**
     * 기존의 setting provider 사용
     * 비밀번호 저장
     */
    private boolean savePasswordToContentProvider() {
        EditText passwordInput = (EditText) findViewById(R.id.passwordInput);
        EditText passwordConfirmInput = (EditText) findViewById(R.id.passwordConfirmInput);

        String password = passwordInput.getText().toString();
        String passwordConfirm = passwordConfirmInput.getText().toString();

        if (password.equals("")) {
            Toast.makeText(mContext, mContext.getString(R.string.put_in_password), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 4) {
            Toast.makeText(mContext, R.string.put_in_4_digit_password, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (passwordConfirm.equals("") || !password.equals(passwordConfirm)) {
            Toast.makeText(mContext, R.string.password_is_not_same, Toast.LENGTH_SHORT).show();
            return false;
        }


        new PasswordHandler(mContext).setValueInBackground(password,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.password_saved, Toast.LENGTH_SHORT).show();
                            mListener.onWallpadPasswordChanged();

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
    public ChangePasswordPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
