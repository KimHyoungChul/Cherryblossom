package com.commax.iphomiot.doorcall.view;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.commax.cmxua.Call;
import com.commax.iphomeiot.common.db.CallDBScheme;
import com.commax.iphomiot.doorcall.R;
import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.iphomiot.doorcall.database.CallLogProvider;
import com.commax.nubbyj.base.TimeUtil;
import com.commax.settings.ringtone.CommaxRingtoneManager;
import com.commax.settings.setting_provider.DongHo;

import java.util.StringTokenizer;


/**
 * Created by Woohj on 2017-02-08.
 */

public class OutgoingCallActivity extends BaseVideoActivity implements View.OnClickListener{

    private TextView tvReceiver_;
    private TextView tvCallState_;
    private Button btnEndConnecting_;
    private CallingAnimateView ivCallingAnimateView_;
    private FrameLayout layoutCallingIconBack_;

    private String recordFilePath_ = "";
    private String callTime_ = String.format("%d", TimeUtil.getCurrentUnixtimeWithLocal());
    private String callNumber_;
    private String callName_;
    private String displayCallName_;
    private String localNumber_;
    public int callType_ = CallLog.Calls.OUTGOING_TYPE;
    private SoundPool bellSound_ = null;
    private int soundId_ = -1;
    private Call outgoingCall_;
    private VolumeControlFragment volumeControlFragment_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_outgoingcall);

        tvReceiver_ = (TextView)findViewById(R.id.tvReceiver);
        tvCallState_ = (TextView)findViewById(R.id.tvCallState);
        tvCallState_.setText(getString(R.string.STR_CALL_CONNECTING));
        btnEndConnecting_ = (Button) findViewById(R.id.btnEndConnecting);
        btnEndConnecting_.setOnClickListener(this);
        ivCallingAnimateView_ = (CallingAnimateView) findViewById(R.id.callingAnimateView);
        layoutCallingIconBack_ = (FrameLayout) findViewById(R.id.layoutCallingIconBack);
        volumeControlFragment_ = (VolumeControlFragment)getFragmentManager().findFragmentById(R.id.fragmentOutgoingVolumeControl);
        ivCallingAnimateView_.startCallingAnimite();
        Intent intent = getIntent();
        callNumber_ = intent.getExtras().getString("callNumber");
        DongHo dongho = new DongHo(this);
        callName_ =  dongho.getValue();
        displayCallName_ = intent.getExtras().getString("callName");
        //intent.getExtras().getString("callName");

        StringTokenizer tokenizer = new StringTokenizer(callName_, "-");
        String dong = tokenizer.nextToken();
        String ho = tokenizer.nextToken();
        if (dong.length() < 4) {
            while (dong.length() != 4)
                dong = "0" + dong;
        }
        if (ho.length() < 4) {
            while (ho.length()!= 4)
                ho = "0" + ho;
        }

        localNumber_ = "70" + dong + ho;

        DoorCallApplication.getInstance().createCall(callNumber_, callName_, localNumber_);

       if (callName_.equals("Guard")) {
           displayCallName_ = getResources().getString(R.string.STR_GUARD);
        }
        else if (callName_.equals("Office")) {
           displayCallName_ = getResources().getString(R.string.STR_OFFICE);
        }

        tvReceiver_.setText(displayCallName_);
        saveCallLog();
    }

    public boolean isEqualCall(Call call) {
        if (outgoingCall_ != null && call != null)
            return outgoingCall_.equals(call);
        else
            return false;
    }

    public void ringingCall(Call call) {
        outgoingCall_ = call;
        playSound();
        initVolume();
    }

    public void connectedCall(Call call) {
        outgoingCall_ = call;
        tvCallState_.setText(getString(R.string.STR_CALL_ACCEPT));
        layoutCallingIconBack_.setBackgroundResource(R.drawable.bg_call_communication);
        stopSound();
        saveVolumes();
    }

    private void playSound() {
        String bellFilePath = getSoundFilePath();
        if (bellFilePath.equals(""))
            return;

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        bellSound_ = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(1).build();
        soundId_ = bellSound_.load(bellFilePath, 1);
        bellSound_.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                bellSound_.play(soundId_, 0.25F, 0.25F, 1, -1, 1.0F);
            }
        });
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

    private String getSoundFilePath() {
        String bellFilePath = "";
        String bellFolderPath = "/user/app/bin/sound/";
        CommaxRingtoneManager ringtoneManager = new CommaxRingtoneManager(this);
        bellFilePath = bellFolderPath + ringtoneManager.getPstnRingtone();
        return bellFilePath;
    }

    private void saveCallLog() {
        ContentValues values = new ContentValues();
        values.put(CallDBScheme.COLUMN_NAME_TYPE, callType_);
        values.put(CallDBScheme.COLUMN_NAME_OTHERPARTY, callNumber_);
        values.put(CallDBScheme.COLUMN_NAME_OTHERPARTY_DISPLAYNAME, displayCallName_);
        callTime_ = String.format("%d", TimeUtil.getCurrentUnixtimeWithLocal());
        values.put(CallDBScheme.COLUMN_NAME_DATE, callTime_);
        values.put(CallDBScheme.COLUMN_NAME_RECORDFILEPATH, recordFilePath_);
        if (callType_ == CallLog.Calls.MISSED_TYPE)
            values.put(CallDBScheme.COLUMN_NAME_NEW, 1);
        else
            values.put(CallDBScheme.COLUMN_NAME_NEW, 0);
        getContentResolver().insert(CallLogProvider.CONTENT_URI, values);

    }

    private void stopSound() {
        if (soundId_ == -1)
            return;

        bellSound_.stop(soundId_);
        bellSound_.release();
        soundId_ = -1;
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

    @Override
    public void finish() {
        if (outgoingCall_ != null) {
            DoorCallApplication.getInstance().disconnectCall(outgoingCall_);
        }

        outgoingCall_ = null;

        stopSound();
        saveVolumes();
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEndConnecting :
                ivCallingAnimateView_.stopCallingAnimite();
                finish();
                break;
        }
    }
}
