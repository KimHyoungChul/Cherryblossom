package com.commax.iphomiot.doorcall.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.commax.iphomiot.doorcall.R;
import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.nubbyj.ui.activity.ActivityUtil;
import com.commax.settings.content_provider.ContentProviderManagerEx;

public class MonitoringActivity extends BaseVideoActivity implements View.OnClickListener {
    private String ip_ = "";
    private String recordFilePath_ = "";
    private String recordFileComment_ = null;

    private TextView tvRecordTime_;
    private ImageView imgRecordVideo_;

    public static final String INTENT_KEY_IP = "ip";

    private void initView() {
        addCameraDisplayView((FrameLayout)findViewById(R.id.layoutVideo));
        tvRecordTime_ = (TextView)findViewById(R.id.tvRecordTime);
        tvRecordTime_.setVisibility(View.INVISIBLE);
        imgRecordVideo_ = (ImageView)findViewById(R.id.imgRecordVideo);
        ActivityUtil.registerClickListener(this, R.id.btnCloseMonitoring, R.id.imgRecordVideo, R.id.imgGoDoorRegistration);

        String doorName = ContentProviderManagerEx.getDoorCameraName(this, ip_);
        if (doorName != null) {
            Button btnCaption = (Button)findViewById(R.id.btnCaption);
            btnCaption.setText(doorName);
        }
    }

    @Override
    protected void updatedRecordVideoTime(int mins, int seconds) {
        super.updatedRecordVideoTime(mins, seconds);
        tvRecordTime_.setText(String.format("%02d:%02d", mins, seconds));
    }

    @Override
    protected void stopRecord() {
        if (recordFileComment_ != null) {
            writeCommentToMuxer(recordFileComment_);
            recordFileComment_ = null;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r2);
                tvRecordTime_.setVisibility(View.GONE);
            }
        });
        super.stopRecord();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_monitoring);

        ip_ = getIntent().getStringExtra(INTENT_KEY_IP);
        initView();
        startRtspLiveVideo(ip_);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCloseMonitoring:
                finish();
                break;
            case R.id.imgRecordVideo:
                if (!isAliveMux()) {
                    if (startRecord() == null)
                        return;
                    imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r1);
                    updatedRecordVideoTime(0, 0);
                    tvRecordTime_.setVisibility(View.VISIBLE);
                    recordFileComment_ = String.format(String.format("from:%d, mode:%d, type:%d", VideoCallFrom.Door.ordinal(), VideoCallMode.Monitoring.ordinal(), CallLog.Calls.MISSED_TYPE));
                }
                else {
                    stopRecord();
                    imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r2);
                    tvRecordTime_.setVisibility(View.GONE);
                }
                break;
            case R.id.imgGoDoorRegistration:
                DoorCallApplication.getInstance().goToConfigurationActivity();
                break;
        }
    }
}
