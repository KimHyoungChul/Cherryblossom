package com.commax.iphomiot.doorcall.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.commax.cmxua.Call;
import com.commax.cmxua.LobbyController;
import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.iphomiot.doorcall.helper.CallHelper;
import com.commax.nubbyj.network.rtsp.client.RTSPReceivable;
import com.commax.iphomiot.doorcall.R;
import com.commax.nubbyj.device.camera.display.CameraDisplayView;

public class VideoSubDisplay extends LinearLayout implements View.OnClickListener, CameraDisplayView.CameraDisplayViewAction {
    public interface VideoSubDisplayAction {
        void clickCameraDisplayInSubDisplay(VideoSubDisplay subDisplay);
        void clickCloseButtonInSubDisplay(VideoSubDisplay subDisplay);
        void commandResultInSubDisplay(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand cmd, boolean cmdRet);
    }

    private CameraDisplayView cameraDisplay_ = null;
    private Button btnClose_ = null;
    private ImageView imgLogo_ = null;
    private Call subCall_ = null;
    private VideoSubDisplayAction actionTarget_ = null;
    private LobbyController lobbyController_ = null;

    private void initView()  {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View subView = inflater.inflate(R.layout.layout_subvideoview, this, false);
        addView(subView);

        TextView tvTitle = (TextView)findViewById(R.id.tvSubdisplayTitle);
        tvTitle.setText(CallHelper.getCallDisplayName(subView.getContext(), subCall_));
        imgLogo_ = (ImageView)findViewById(R.id.imgLogo);
        imgLogo_.setVisibility(View.GONE);
        imgLogo_.setOnClickListener(this);

        cameraDisplay_ = (CameraDisplayView)findViewById(R.id.cameraDisplay);
        cameraDisplay_.setZOrderMediaOverlay(true);
        cameraDisplay_.setOnClickListener(this);
        cameraDisplay_.setActionTarget(this);

        btnClose_ = (Button)findViewById(R.id.btnClose);
        btnClose_.setOnClickListener(this);
    }

    public VideoSubDisplay(Context context, Call subCall, VideoSubDisplayAction actionTarget) {
        super(context);
        subCall_ = subCall;
        actionTarget_ = actionTarget;
        initView();
    }

    public VideoSubDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoSubDisplay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public void startLiveVideo() {
        if (subCall_ == null)
            return;

        if (subCall_.getCallSender() == Call.CallSender.Door)
            cameraDisplay_.startLiveVideo(subCall_.m_strIP, 554, "admin", "123456", "video2", 0, 1, true);
        else if (subCall_.getCallSender() == Call.CallSender.Lobby || subCall_.getCallSender() == Call.CallSender.Guard) {
            if (lobbyController_ == null)
                lobbyController_ = new LobbyController();
            lobbyController_.setPushVList(subCall_);
            cameraDisplay_.startRtpLiveVideo(0, Integer.parseInt(subCall_.m_strVideoLocalPort));
        }
        else {
            drawLogo();
        }
    }

    public void drawLogo() {
        cameraDisplay_.setVisibility(View.GONE);
        imgLogo_.setVisibility(View.VISIBLE);
    }

    public void finish(boolean disconnectCall) {
        if (subCall_ != null) {
            if (lobbyController_ != null) {
                lobbyController_.setPopVList(subCall_);
                lobbyController_ = null;
            }
            if (disconnectCall)
                DoorCallApplication.getInstance().rejectCall(subCall_);
        }
        cameraDisplay_.stopAllLiveVideo();
        cameraDisplay_.setVisibility(View.GONE);
        cameraDisplay_.setZOrderMediaOverlay(false);
        cameraDisplay_ = null;
        setVisibility(View.GONE);
        invalidate();
    }

    public Call getSubCall() {
        return subCall_;
    }

    public boolean equals(VideoSubDisplay subDisplay) {
        if (subCall_ == null)
            return false;

        return (subCall_.equals(subDisplay.getSubCall()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                if (actionTarget_ != null)
                    actionTarget_.clickCloseButtonInSubDisplay(this);
                break;
            case R.id.cameraDisplay:
            case R.id.imgLogo:
                if (actionTarget_ != null)
                    actionTarget_.clickCameraDisplayInSubDisplay(this);
                break;
        }
    }

    @Override
    public void onCommandResult(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand cmd, boolean cmdRet) {
        if (actionTarget_ != null)
            actionTarget_.commandResultInSubDisplay(cameraId, streamNo, cmd, cmdRet);
    }

    @Override
    public void onRecvVideoEncodeStream(int cameraId, int streamNo, RTSPReceivable.MediaCodec mediaCodecId, byte[] mediaData, int mediaSize, int timesec) {
    }
}
