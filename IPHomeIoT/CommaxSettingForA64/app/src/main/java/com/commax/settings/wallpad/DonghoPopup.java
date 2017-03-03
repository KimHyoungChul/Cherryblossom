package com.commax.settings.wallpad;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.setting_provider.DongHo;
import com.commax.settings.setting_provider.OnBooleanCallback;
import com.commax.settings.util.PlusClickGuard;

/**
 * 동호 입력 팝업
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class DonghoPopup extends Dialog {


    private final Context mContext;
    private final DonghoSetListener mListener;


    public DonghoPopup(Context context, DonghoSetListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_dongho);
        showDongho();
        addButtonListener();
    }

    private void showDongho() {
        String dongho = getDongho();

        if(dongho == null) {
            return;
        }

        String[] tokens = dongho.split("-");
        String dong = tokens[0];
        String ho = tokens[1];


        EditText dongInput = (EditText) findViewById(R.id.dongInput);

        EditText hoInput = (EditText) findViewById(R.id.hoInput);

        dongInput.setText(dong);
        hoInput.setText(ho);
    }

    /**
     * 기존의 setting content provider 사용
     */
    private String getDongho() {


        return new DongHo(mContext).getValue();

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
                saveDonghoToContentProvider();
//원래 코드
//                if(isDonghoSaved()) {
//                    dismiss();
//                    mListener.onDonghoSet();
//                }
            }
        });
    }

//    //원래 코드
//    /**
//     * 동호 저장
//     */
//    private boolean isDonghoSaved() {
//        EditText dongInput = (EditText) findViewById(R.id.dongInput);
//        EditText hoInput = (EditText) findViewById(R.id.hoInput);
//
//        String dong = dongInput.getText().toString();
//        String ho = hoInput.getText().toString();
//
//        if(dong.equals("")) {
//            Toast.makeText(mContext, R.string.put_in_dong, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//
//
//        if(ho.equals("")) {
//            Toast.makeText(mContext, R.string.put_in_ho, Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//
//
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG, dong);
//        contentValues.put(ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO, ho);
//
//        ContentProviderManager.deletePreviousDongho(mContext);
//        ContentProviderManager.saveDongho(mContext, contentValues);
//
//        return true;
//    }

    /**
     * 기존의 setting content provider 사용
     */
    /**
     * 동호 저장
     */
    private boolean saveDonghoToContentProvider() {
        EditText dongInput = (EditText) findViewById(R.id.dongInput);
        EditText hoInput = (EditText) findViewById(R.id.hoInput);

        String dong = dongInput.getText().toString();
        String ho = hoInput.getText().toString();

        if (dong.equals("")) {
            Toast.makeText(mContext, R.string.put_in_dong, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (ho.equals("")) {
            Toast.makeText(mContext, R.string.put_in_ho, Toast.LENGTH_SHORT).show();
            return false;
        }


        new DongHo(mContext).setValueInBackground(dong, ho,
                new OnBooleanCallback() {
                    @Override
                    public void onResult(boolean value) {
                        if (value) {
                            dismiss();
                            Toast.makeText(mContext, R.string.saved, Toast.LENGTH_SHORT).show();
                            mListener.onDonghoSet();
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
    public DonghoPopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
