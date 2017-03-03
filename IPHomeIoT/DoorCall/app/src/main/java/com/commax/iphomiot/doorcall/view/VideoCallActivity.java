package com.commax.iphomiot.doorcall.view;

import android.content.ContentValues;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commax.cmxua.Call;
import com.commax.cmxua.LobbyController;
import com.commax.iphomeiot.common.db.CallDBScheme;
import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.iphomiot.doorcall.R;
import com.commax.iphomiot.doorcall.database.CallLogProvider;
import com.commax.iphomiot.doorcall.helper.CallHelper;
import com.commax.nubbyj.base.TimeUtil;
import com.commax.nubbyj.network.rtsp.client.RTSPReceivable;
import com.commax.nubbyj.ui.activity.ActivityUtil;
import com.commax.settings.content_provider.ContentProviderManagerEx;
import com.commax.settings.ringtone.CommaxRingtoneManager;

import java.util.ArrayList;

public class VideoCallActivity extends BaseVideoActivity implements View.OnClickListener, VideoSubDisplay.VideoSubDisplayAction {
    private Call incomingCall_ = null;
    private String recordFilePath_ = "";
    private String callTime_ = String.format("%d", TimeUtil.getCurrentUnixtimeWithLocal());
    private int callType_ = CallLog.Calls.MISSED_TYPE;
    private int soundLoopCount_ = 1;
    private SoundPool bellSound_ = null;
    private int soundId_ = -1;
    private LobbyController lobbyController_ = null;
    private ArrayList<VideoSubDisplay> videoSubDisplayList_ = new ArrayList<>();

    private TextView tvRecordTime_;
    private Button btnDoorOpen_;
    private Button btnDoorAcept_;
    private TextView tvDoorState_;
    private ImageView imgRecordVideo_;
    private VolumeControlFragment volumeControlFragment_;

    public static final String INTENT_KEY_CALLOBJ = "incomingcall";
    public static final String INTENT_KEY_ENABLE_WAITINGCALL = "enable_waitingcall";

    private void startLiveVideo() {
        if (incomingCall_ == null)
            return;

        if (incomingCall_.getCallSender() == Call.CallSender.Door)
            startRtspLiveVideo(incomingCall_.m_strIP);
        else if (incomingCall_.getCallSender() == Call.CallSender.Lobby || incomingCall_.getCallSender() == Call.CallSender.Guard) {
            if (lobbyController_ == null)
                lobbyController_= new LobbyController();
            lobbyController_.setPushVList(incomingCall_);
            startRtpLiveVideo(Integer.parseInt(incomingCall_.m_strVideoLocalPort));
        }
    }

    private void initView() {
        if (incomingCall_.isSupportVideo()) {
            addCameraDisplayView((FrameLayout) findViewById(R.id.layoutVideo));
            ImageView imgLogo = (ImageView)findViewById(R.id.imgLogo);
            imgLogo.setVisibility(View.GONE);
        }
        tvRecordTime_ = (TextView)findViewById(R.id.tvRecordTime);
        tvRecordTime_.setVisibility(View.INVISIBLE);
        btnDoorOpen_ = (Button)findViewById(R.id.btnDoorOpenButton);
        btnDoorOpen_.setVisibility(View.GONE);
        btnDoorAcept_ = (Button)findViewById(R.id.btnCallAcceptButton);
        btnDoorAcept_.setVisibility(View.VISIBLE);
        tvDoorState_ = (TextView)findViewById(R.id.doorState);
        tvDoorState_.setText(R.string.STR_CALL_OFFERING);
        imgRecordVideo_ = (ImageView)findViewById(R.id.imgRecordVideo);
        if (!incomingCall_.isSupportOpenDoor())
            imgRecordVideo_.setVisibility(View.GONE);
        volumeControlFragment_ = (VolumeControlFragment)getFragmentManager().findFragmentById(R.id.fragmentVolumeControl);
        Button btnCaption = (Button)findViewById(R.id.btnCaption);
        btnCaption.setText(CallHelper.getCallDisplayName(this, incomingCall_));

        ActivityUtil.registerClickListener(this, R.id.btnDisconnectCall, R.id.btnCallAcceptButton, R.id.btnDoorOpenButton, R.id.imgRecordVideo);
    }

    private void saveCallLog() {
        if (incomingCall_ == null)
            return;

        ContentValues values = new ContentValues();
        values.put(CallDBScheme.COLUMN_NAME_TYPE, callType_);
        values.put(CallDBScheme.COLUMN_NAME_OTHERPARTY, incomingCall_.m_strID);
        values.put(CallDBScheme.COLUMN_NAME_OTHERPARTY_DISPLAYNAME, CallHelper.getCallDisplayName(this, incomingCall_));
        values.put(CallDBScheme.COLUMN_NAME_DATE, callTime_);
        values.put(CallDBScheme.COLUMN_NAME_RECORDFILEPATH, recordFilePath_);
        if (callType_ == CallLog.Calls.MISSED_TYPE)
            values.put(CallDBScheme.COLUMN_NAME_NEW, 1);
        else
            values.put(CallDBScheme.COLUMN_NAME_NEW, 0);
        getContentResolver().insert(CallLogProvider.CONTENT_URI, values);
    }

    private String getSoundFilePath() {
        String bellFilePath = "";
        String bellFolderPath = "/user/app/bin/sound/";
        CommaxRingtoneManager ringtoneManager = new CommaxRingtoneManager(this);
        switch (incomingCall_.getCallSender()) {
            case Door:
                bellFilePath = bellFolderPath + ringtoneManager.getEntranceRingtone();
                break;
            case Lobby:
                bellFilePath = bellFolderPath + ringtoneManager.getPublicAreaRingtone();
                break;
            case Guard:
            case House:
                bellFilePath = bellFolderPath + ringtoneManager.getGuardHouseRingtone();
                break;
        }

        return bellFilePath;
    }

    private void playSound() {
        String bellFilePath = getSoundFilePath();
        if (bellFilePath.equals(""))
            return;

        soundLoopCount_ = 1;
        if (incomingCall_.getCallSender() != Call.CallSender.Door)
            soundLoopCount_ = -1;
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        bellSound_ = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
        soundId_ = bellSound_.load(bellFilePath, 1);
        bellSound_.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                bellSound_.play(soundId_, 0.25F, 0.25F, 1, soundLoopCount_, 1.0F);
            }
        });
    }

    private void stopSound() {
        if (soundId_ == -1)
            return;

        bellSound_.stop(soundId_);
        bellSound_.release();
        soundId_ = -1;
    }

    private int queryVolume(int volumeType) {
        Cursor cursor =  getContentResolver().query(CallLogProvider.SOUND_URI,
                new String[]{CallDBScheme.SoundDB.COLUMN_NAME_SOUNDVALUE},
                CallDBScheme.SoundDB.COLUMN_NAME_SOUNDKEY + " = ?",
                new String[]{Integer.toString(volumeType)},
                null);
        if (cursor == null)
            return -1;
        if (!cursor.moveToFirst())
            return -1;

        int ret = cursor.getInt(0);
        cursor.close();

        return ret;
    }

    private void initVolume() {
        volumeControlFragment_.setVolumeType(VolumeControlFragment.VolumeType.VOLUME_MUSIC);
        int bellSoundVolume = queryVolume(CallDBScheme.SoundDB.SoundType.BELL_SOUND);
        int voiceSoundVolume = queryVolume(CallDBScheme.SoundDB.SoundType.INCOMING_SOUND);
        if (bellSoundVolume == -1)
            bellSoundVolume = volumeControlFragment_.getVolume(VolumeControlFragment.VolumeType.VOLUME_MUSIC);
        if (voiceSoundVolume == -1)
            voiceSoundVolume = volumeControlFragment_.getVolume(VolumeControlFragment.VolumeType.VOLUME_VOICE);
        volumeControlFragment_.setVolume(VolumeControlFragment.VolumeType.VOLUME_MUSIC, bellSoundVolume);
        volumeControlFragment_.setVolume(VolumeControlFragment.VolumeType.VOLUME_VOICE, voiceSoundVolume);
    }

    private void saveVolumes() {
        int bellSoundVolume = volumeControlFragment_.getVolume(VolumeControlFragment.VolumeType.VOLUME_MUSIC);
        int voiceSoundVolume = volumeControlFragment_.getVolume(VolumeControlFragment.VolumeType.VOLUME_VOICE);
        ContentValues values = new ContentValues();
        values.put(CallDBScheme.SoundDB.COLUMN_NAME_SOUNDVALUE, bellSoundVolume);
        getContentResolver().update(CallLogProvider.SOUND_URI, values, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDKEY + "=?", new String[]{Integer.toString(CallDBScheme.SoundDB.SoundType.BELL_SOUND)});

        values.clear();
        values.put(CallDBScheme.SoundDB.COLUMN_NAME_SOUNDVALUE, voiceSoundVolume);
        getContentResolver().update(CallLogProvider.SOUND_URI, values, CallDBScheme.SoundDB.COLUMN_NAME_SOUNDKEY + "=?", new String[]{Integer.toString(CallDBScheme.SoundDB.SoundType.INCOMING_SOUND)});
    }

    private void processAutoRecord() {
        if (imgRecordVideo_.getVisibility() != View.VISIBLE)
            return;

        String isAutoRecord = ContentProviderManagerEx.getMovieAutoSave(this);
        if (isAutoRecord == null)
            return;

        if (isAutoRecord.equals("enabled"))
            imgRecordVideo_.performClick();
    }

    private void removeSubDisplay(VideoSubDisplay subDisplay) {
        if (videoSubDisplayList_ == null)
            return;

        VideoSubDisplay curSubDisplay;
        for (int i = 0; i < videoSubDisplayList_.size(); i++) {
            curSubDisplay = videoSubDisplayList_.get(i);
            if (curSubDisplay.equals(subDisplay)) {
                curSubDisplay.finish(true);
                videoSubDisplayList_.remove(i);
                break;
            }
        }
    }

    private void removeAllSubDisplays() {
        if (videoSubDisplayList_ == null)
            return;

        for (int i = 0; i < videoSubDisplayList_.size(); i++)
            videoSubDisplayList_.get(i).finish(true);
        videoSubDisplayList_.clear();
    }

    private boolean isEnableLiveVideoInSubDisplays() {
        if (videoSubDisplayList_ == null)
            return false;

        int liveVideoCount = 0;
        for (int i = 0; i < videoSubDisplayList_.size(); i++) {
            if (videoSubDisplayList_.get(i).getSubCall().isSupportVideo())
                liveVideoCount++;
        }

        return (liveVideoCount < 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_videocall);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        incomingCall_ = getIntent().getParcelableExtra(INTENT_KEY_CALLOBJ);

        initView();

        boolean isEnableWaitingCall = getIntent().getBooleanExtra(INTENT_KEY_ENABLE_WAITINGCALL, true);
        if (!isEnableWaitingCall)
            videoSubDisplayList_ = null;
        startLiveVideo();
        processAutoRecord();
        playSound();
        initVolume();
    }

    @Override
    protected void updatedRecordVideoTime(int mins, int seconds) {
        super.updatedRecordVideoTime(mins, seconds);
        tvRecordTime_.setText(String.format("%02d:%02d", mins, seconds));
    }

    @Override
    protected void stopRecord() {
        writeCommentToMuxer(String.format("from:%d, mode:%d, type:%d", incomingCall_.getCallSender().ordinal(), VideoCallMode.DoorCall.ordinal(), callType_));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r2);
                tvRecordTime_.setVisibility(View.GONE);
            }
        });

        super.stopRecord();
    }

    public boolean isEqualCall(Call call) {
        return incomingCall_.equals(call);
    }

    public boolean isIncludeWithSubcall(Call call) {
        if (videoSubDisplayList_ == null)
            return false;

        for (int i = 0; i < videoSubDisplayList_.size(); i++) {
            if (videoSubDisplayList_.get(i).getSubCall().equals(call))
                return true;
        }

        return false;
    }

    public void removeSubcall(Call call) {
        if (videoSubDisplayList_ == null)
            return;

        for (int i = 0; i < videoSubDisplayList_.size(); i++) {
            if (videoSubDisplayList_.get(i).getSubCall().equals(call)) {
                removeSubDisplay(videoSubDisplayList_.get(i));
                break;
            }
        }
    }

    public boolean isOffering() {
        return (btnDoorAcept_.getVisibility() == View.VISIBLE);
    }

    public void createSubDisplay(Call call) {
        if (videoSubDisplayList_ == null)
            return;

        VideoSubDisplay subDisplay = new VideoSubDisplay(this, call, this);
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(240, 180);
        layoutParam.bottomMargin = 15;
        subDisplay.setLayoutParams(layoutParam);
        subDisplay.requestLayout();

        LinearLayout layoutSubDisplay = (LinearLayout)findViewById(R.id.layoutSubDisplays);
        layoutSubDisplay.addView(subDisplay);
        if (isEnableLiveVideoInSubDisplays())
            subDisplay.startLiveVideo();
        else
            subDisplay.drawLogo();

        videoSubDisplayList_.add(subDisplay);
    }

    @Override
    public void finish() {
        if (isOffering())
            DoorCallApplication.getInstance().rejectCall(incomingCall_);
        else
            DoorCallApplication.getInstance().disconnectCall(incomingCall_);
        if (lobbyController_ != null)
            lobbyController_.setPopVList(incomingCall_);
        removeAllSubDisplays();

        stopSound();
        saveCallLog();
        saveVolumes();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDisconnectCall:
                if (videoSubDisplayList_  != null && videoSubDisplayList_.size() > 0) {
                    clickCameraDisplayInSubDisplay(videoSubDisplayList_.get(0));
                    return;
                }
                finish();
                break;
            case R.id.btnCallAcceptButton:
                DoorCallApplication.getInstance().acceptCall(incomingCall_);
                stopSound();
                callTime_ = String.format("%d", TimeUtil.getCurrentUnixtimeWithLocal());
                callType_ = CallLog.Calls.INCOMING_TYPE;
                btnDoorAcept_.setVisibility(View.GONE);
                if (incomingCall_.isSupportOpenDoor())
                    btnDoorOpen_.setVisibility(View.VISIBLE);
                volumeControlFragment_.setImageViewSrc(R.id.imgControlVolume, R.drawable.btn_apps_volume_n);
                volumeControlFragment_.setVolumeType(VolumeControlFragment.VolumeType.VOLUME_VOICE);
                tvDoorState_.setText(R.string.STR_CALL_ACCEPT);
                break;
            case R.id.btnDoorOpenButton:
                DoorCallApplication.getInstance().openDoor(incomingCall_, 80, "admin", "123456");
                break;
            case R.id.imgRecordVideo:
                if (!isAliveMux()) {
                    recordFilePath_ = startRecord();
                    if (recordFilePath_ == null) {
                        recordFilePath_ = "";
                        return;
                    }
                    imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r1);
                    updatedRecordVideoTime(0, 0);
                    tvRecordTime_.setVisibility(View.VISIBLE);
                }
                else {
                    stopRecord();
                    imgRecordVideo_.setImageResource(R.drawable.btn_apps_video_n_r2);
                    tvRecordTime_.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void clickCameraDisplayInSubDisplay(VideoSubDisplay subDisplay) {
        Call subCall = subDisplay.getSubCall();
        VideoSubDisplay curSubDisplay;
        for (int i = 0; i < videoSubDisplayList_.size(); i++) {
            curSubDisplay = videoSubDisplayList_.get(i);
            if (curSubDisplay.equals(subDisplay)) {
                // Prevent disconnecting the call from removeAllSubDisplays when Activity is finished
                curSubDisplay.finish(false);
                videoSubDisplayList_.remove(i);
            }
        }
        DoorCallApplication.getInstance().createVideoCallActivityWithSubCall(subCall);
    }

    @Override
    public void clickCloseButtonInSubDisplay(VideoSubDisplay subDisplay) {
        removeSubDisplay(subDisplay);
    }

    @Override
    public void commandResultInSubDisplay(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand cmd, boolean cmdRet) {
    }
}
