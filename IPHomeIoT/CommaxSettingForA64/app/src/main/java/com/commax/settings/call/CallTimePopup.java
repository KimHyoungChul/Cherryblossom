package com.commax.settings.call;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.commax.settings.R;
import com.commax.settings.content_provider.ContentProviderConstants;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.util.PlusClickGuard;

/**
 * 연속통화시간 설정 팝업(직접입력)
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class CallTimePopup extends Dialog {


    private final Context mContext;
    private final CallTimeConfirmListener mListener;


    public CallTimePopup(Context context, CallTimeConfirmListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_call_time);


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
                if (isCallTimeSaved()) {
                    mListener.onCallTimeConfirm();
                    dismiss();
                }
            }
        });
    }


    /**
     * 연속통화시간 저장
     */
    private boolean isCallTimeSaved() {
        EditText callTimeInput = (EditText) findViewById(R.id.callTimeInput);

        String callTime = callTimeInput.getText().toString();
        int ncallTime = 0;


        if (callTime.equals("")) {
            Toast.makeText(mContext, R.string.put_in_time, Toast.LENGTH_SHORT).show();
            return false;
        }

        //2017-01-12,yslee::callTime 범위체크
        try {
            ncallTime = Integer.parseInt(callTime);
        }catch (Exception e) {

        }

        if (ncallTime < 30 || ncallTime > 180) {
            String message = ((Activity)mContext).getResources().getString(R.string.put_in_time) + "(" + ((Activity)mContext).getResources().getString(R.string.callTimeHint) +  ")";
            Toast.makeText(mContext, message , Toast.LENGTH_SHORT).show();
            return false;
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.CallTimeEntry.COLUMN_NAME_CALLTIME, callTime);


        ContentProviderManager.deletePreviousCallTime(mContext);
        ContentProviderManager.saveCallTime(mContext, contentValues);

        return true;
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public CallTimePopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
