package com.commax.homereporter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class NoticeView extends LinearLayout {

    TextView tv_when;
    TextView tv_title;
    Context mContext;
    Handler mHandler;

    String index = "";

    public NoticeView(Context context, Handler handler, NoticeItem noticeItem) {
        super(context);
        mContext = context;
        mHandler = handler;
        init(context, noticeItem);
    }

    public void init(Context context, NoticeItem noticeItem){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.notice, this);

        ImageButton ib_check = (ImageButton) rootView.findViewById(R.id.ib_check);
        FrameLayout lay_call_security = (FrameLayout) rootView.findViewById(R.id.lay_call_security);
        ImageButton ib_call_security = (ImageButton) rootView.findViewById(R.id.ib_call_security);
        tv_when = (TextView) rootView.findViewById(R.id.tv_when);
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        ImageView iv_type = (ImageView) rootView.findViewById(R.id.iv_type);

        ib_check.setOnClickListener(mClick);
        ib_call_security.setOnClickListener(mClick);

        try {
            index = noticeItem.index;
        }catch (Exception e){
            e.printStackTrace();
        }

        setNoticeData(noticeItem);

    }

    OnClickListener mClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_check:
                    doNotShowAgain();
                    removeThisView();
                    break;


                case R.id.ib_call_security:
                    try {
                        Log.d("NoticeView", "start notice app");
                        Intent intent = new Intent();
                        intent.setClassName("com.commax.webappbase", "com.commax.webappbase.MainActivityNotice");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void removeThisView(){
        try{
            Message addViewMsg = mHandler.obtainMessage();
            addViewMsg.what = HandlerEvent.EVENT_HANDLE_REMOVE_INFO_VIEW;
            addViewMsg.obj = this;
            mHandler.sendMessage(addViewMsg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doNotShowAgain(){

        try {
            if (!TextUtils.isEmpty(index)) {
                Properties mProperty = null;

                try {
                    File dir = new File(Environment.getExternalStorageDirectory() + "/InfoFile/");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    dir = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String filename = "notice.properties";
                File file = new File(Environment.getExternalStorageDirectory() + "/InfoFile/" + filename);
                mProperty = new Properties();

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                        Log.e("Property", " Create profile ");
                    }

                    FileInputStream fis = new FileInputStream(file);
                    mProperty.load(fis);
//                mProperty.clear();

                    FileOutputStream fos = new FileOutputStream(file);

                    mProperty.setProperty(index, "not");
                    mProperty.store(fos, "selected info");

                    fos.close();
                    fis.close();
                    fis = null;
                    fos = null;
                    Log.e("Property", " File Saved");

                    try {
                        Process process = Runtime.getRuntime().exec("sync");
                        process.getErrorStream().close();
                        process.getInputStream().close();
                        process.getOutputStream().close();
                        process.waitFor();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setNoticeData(NoticeItem noticeItem){

        try {
            tv_title.setText(noticeItem.title);
            tv_when.setText(noticeItem.date + " " + noticeItem.time);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
