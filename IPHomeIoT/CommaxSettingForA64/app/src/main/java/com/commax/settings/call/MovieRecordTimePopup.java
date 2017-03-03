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
 * 영상녹화시간 설정 팝업(직접입력)
 * Created by bagjeong-gyu on 2016. 9. 28..
 */

public class MovieRecordTimePopup extends Dialog {


    private final Context mContext;
    private final MovieRecordTimeConfirmListener mListener;


    public MovieRecordTimePopup(Context context, MovieRecordTimeConfirmListener listener) {
        super(context, R.style.CustomDialog);
        mContext = context;
        mListener = listener;

        setOwnerActivity((Activity) context);
        setCancelable(true);
        setContentView(R.layout.popup_movie_record_time);


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
                if (isMovieRecordTimeSaved()) {
                    mListener.onMovieRecordTimeConfirm();
                    dismiss();
                }
            }
        });
    }

    /**
     * 영상녹화시간 저장
     */
    private boolean isMovieRecordTimeSaved() {
        EditText movieRecordTimeInput = (EditText) findViewById(R.id.movieRecordTimeInput);


        String moiveRecordTime = movieRecordTimeInput.getText().toString();
        int nmovieRecordTime = 0;

        if (moiveRecordTime.equals("")) {
            Toast.makeText(mContext, R.string.put_in_time, Toast.LENGTH_SHORT).show();
            return false;
        }

        //2017-01-12,yslee::movieRecordTime 범위체크
        try {
            nmovieRecordTime = Integer.parseInt(moiveRecordTime);
        }catch (Exception e) {

        }

        if (nmovieRecordTime < 10 || nmovieRecordTime > 60) {
            String message = ((Activity)mContext).getResources().getString(R.string.put_in_time) + "(" + ((Activity)mContext).getResources().getString(R.string.movieRecordTimeHint) +  ")";
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConstants.MovieRecordTimeEntry.COLUMN_NAME_MOVIE_RECORDTIME, moiveRecordTime);


        ContentProviderManager.deletePreviousMovieRecordTime(mContext);
        ContentProviderManager.saveMovieRecordTime(mContext, contentValues);

        return true;
    }


    /**
     * 팝업창 취소 여부 설정
     *
     * @param flag 팝업 취소 여부
     * @return 팝업창 객체
     */
    public MovieRecordTimePopup cancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}
