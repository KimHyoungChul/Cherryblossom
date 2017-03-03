package com.commax.applist;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by OWNER on 2016-12-26.
 */
public class V2_CommonToast extends Toast {
    Context mContext;
    public V2_CommonToast(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(String body, int duration){
        // http://developer.android.com/guide/topics/ui/notifiers/toasts.html
        LayoutInflater inflater;
        View v;
        if(false){
            Activity act = (Activity)mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.v2_custom_toast, null);
        }else{	// same
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.v2_custom_toast, null);
        }
        TextView text = (TextView) v.findViewById(R.id.textViewMessage);
        text.setText(body);

        show(this,v,duration);
    }

    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}