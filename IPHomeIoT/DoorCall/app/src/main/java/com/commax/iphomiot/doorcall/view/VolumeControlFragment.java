package com.commax.iphomiot.doorcall.view;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.commax.nubbyj.ui.activity.ActivityUtil;
import com.commax.iphomiot.doorcall.R;

import java.util.HashMap;
import java.util.Map;

public class VolumeControlFragment extends Fragment implements View.OnClickListener {
    private class VolumeInfo {
        public int currentVolume_ = 0;
        public int maxVolume_ = 0;
    }

    public class VolumeType {
        public static final int VOLUME_MUSIC = AudioManager.STREAM_MUSIC;
        public static final int VOLUME_VOICE = AudioManager.STREAM_VOICE_CALL;
    }

    private AudioManager audioManager_ = null;
    private Map<Integer, VolumeInfo> volumeInfoMap_ = new HashMap<>();
    private int curVolumeType_ = AudioManager.STREAM_VOICE_CALL;

    private ProgressBar prgbVolume_;
    private LinearLayout layoutVolumeProgress_;

    public void setVolume(int volumeType, int volume) {
        VolumeInfo volumeInfo = volumeInfoMap_.get(volumeType);
        if (volumeInfo == null)
            return;

        volumeInfo.currentVolume_ = volume;
        if (volumeType == curVolumeType_)
            prgbVolume_.setProgress(volume);
    }

    public void setVolumeType(int volumeType) {
        if (curVolumeType_ == volumeType)
            return;

        curVolumeType_ = volumeType;
        VolumeInfo volumeInfo = volumeInfoMap_.get(volumeType);
        if (volumeInfo == null)
            return;

        prgbVolume_.setMax(volumeInfo.maxVolume_);
        prgbVolume_.setProgress(volumeInfo.currentVolume_);
    }

    public int getVolume(int volumeType) {
        VolumeInfo volumeInfo = volumeInfoMap_.get(volumeType);
        if (volumeInfo == null)
            return 0;

        return volumeInfo.currentVolume_;
    }

    public void setImageViewSrc(int viewId, int resId) {
        ImageView view = (ImageView)getView().findViewById(viewId);
        view.setImageResource(resId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_volume, container, false);

        prgbVolume_ = (ProgressBar)view.findViewById(R.id.prgbVolume);
        layoutVolumeProgress_ = (LinearLayout)view.findViewById(R.id.layoutVolumeProgress);
        layoutVolumeProgress_.setVisibility(View.INVISIBLE);
        ActivityUtil.registerClickListener(this, view, R.id.imgControlVolume, R.id.imgIncreaseVolume, R.id.imgDecreaseVolume);

        audioManager_ = (AudioManager)view.getContext().getSystemService(Context.AUDIO_SERVICE);
        VolumeInfo volumeInfo = new VolumeInfo();
        volumeInfo.currentVolume_ = audioManager_.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeInfo.maxVolume_ = audioManager_.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeInfoMap_.put(AudioManager.STREAM_MUSIC, volumeInfo);

        volumeInfo = new VolumeInfo();
        volumeInfo.currentVolume_ = audioManager_.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        volumeInfo.maxVolume_ = audioManager_.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        volumeInfoMap_.put(AudioManager.STREAM_VOICE_CALL, volumeInfo);

        return view;
    }

    @Override
    public void onClick(View v) {
        VolumeInfo volumeInfo;
        switch (v.getId()) {
            case R.id.imgControlVolume:
                if (layoutVolumeProgress_.getVisibility() == View.INVISIBLE)
                    layoutVolumeProgress_.setVisibility(View.VISIBLE);
                else
                    layoutVolumeProgress_.setVisibility(View.INVISIBLE);
                break;
            case R.id.imgIncreaseVolume:
                volumeInfo = volumeInfoMap_.get(curVolumeType_);
                if (volumeInfo == null)
                    return;
                volumeInfo.currentVolume_ += 1;
                if (volumeInfo.currentVolume_ > volumeInfo.maxVolume_)
                    volumeInfo.currentVolume_ = volumeInfo.maxVolume_;
                audioManager_.setStreamVolume(curVolumeType_, (int)volumeInfo.currentVolume_, 0);
                prgbVolume_.setProgress((int)volumeInfo.currentVolume_);
                break;
            case R.id.imgDecreaseVolume:
                volumeInfo = volumeInfoMap_.get(curVolumeType_);
                if (volumeInfo == null)
                    return;
                volumeInfo.currentVolume_ -= 1;
                if (volumeInfo.currentVolume_ < 0)
                    volumeInfo.currentVolume_ = 0;
                audioManager_.setStreamVolume(curVolumeType_, (int)volumeInfo.currentVolume_, 0);
                prgbVolume_.setProgress((int)volumeInfo.currentVolume_);
                break;
        }
    }
}
