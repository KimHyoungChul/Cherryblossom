package com.commax.iphomiot.doorcall.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.commax.iphomiot.doorcall.application.DoorCallApplication;
import com.commax.nubbyj.base.PathUtil;
import com.commax.nubbyj.device.camera.display.CameraDisplayView;
import com.commax.nubbyj.media.muxer.FFMPEGMuxer;
import com.commax.nubbyj.media.parser.VideoStreamParser;
import com.commax.nubbyj.network.rtsp.client.RTSPReceivable;
import com.commax.nubbyj.ui.activity.ActivityUtil;
import com.commax.settings.content_provider.ContentProviderManager;
import com.commax.settings.content_provider.ContentProviderManagerEx;

import java.io.File;

public class BaseVideoActivity extends Activity implements CameraDisplayView.CameraDisplayViewAction {
    public enum VideoCallFrom {
        Unknown,
        Door,
        Lobby
    }

    public enum VideoCallMode {
        Unknown,
        DoorCall,
        Monitoring,
        Preview
    }

    private CameraDisplayView cameraDisplayView_ = null;
    private FFMPEGMuxer muxer_ = null;
    private String recordPath_ = "";
    private int videoWidth_ = 0;
    private int videoHeight_ = 0;
    private VideoStreamParser videoStreamParser_ = new VideoStreamParser();
    private int startRecordTime_ = 0;
    private int curRecordTime_ = 0;
    private int limitedRecordTime_ = 0;

    public static final String INTENT_KEY_AUTO_DESTROY_WHEN_SCREENOFF = "auto_destroy_when_screenoff";

    private BroadcastReceiver screenBroadcastRecver_ = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                finish();
        }
    };

    private String getRecordPath() {
        String recordPath = Environment.getExternalStorageDirectory() + "/cmxMediaGallery";
        File mediaGallery = new File(recordPath);
        if (!mediaGallery.exists()) {
            if (!mediaGallery.mkdirs())
                recordPath = "";
        }

        return recordPath;
    }

    private void removeOldRecordFile(File recordDirectory) {
        if (recordPath_.equals(""))
            return;

        // At least need 10 % of total size
        long totalSize = recordDirectory.getTotalSpace();
        long remainNeedSize = (totalSize * 10) / 100;
        long oldestFileName = 0;
        long curFileName;
        File deleteFile = null;
        if (recordDirectory.getUsableSpace() <= remainNeedSize) {
            File recordFolder = new File(recordPath_ + "/");
            File[] recordFiles = recordFolder.listFiles();
            for (File recordFile : recordFiles) {
                try {
                    curFileName = Long.parseLong(PathUtil.getFileNameWithoutExt(recordFile.getName()));
                }
                catch (Exception e) {
                    continue;
                }
                if (oldestFileName == 0) {
                    oldestFileName = curFileName;
                    deleteFile = recordFile;
                }
                if (curFileName < oldestFileName) {
                    oldestFileName = curFileName;
                    deleteFile = recordFile;
                }
            }
        }
        if (deleteFile != null)
            deleteFile.delete();
    }

    protected void addCameraDisplayView(FrameLayout frameLayout) {
        View view = frameLayout.findViewWithTag(cameraDisplayView_.getTag());
        if (view == null)
            frameLayout.addView(cameraDisplayView_, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    protected void startRtspLiveVideo(String ip) {
        cameraDisplayView_.startLiveVideo(ip, 554, "admin", "123456", "video1", 0, CameraDisplayView.MAIN_STREAMINDEX, true);
    }

    protected void startRtpLiveVideo(int port) {
        cameraDisplayView_.startRtpLiveVideo(0, port);
    }

    protected String startRecord() {
        stopRecord();
        String strRcordTime = ContentProviderManagerEx.getMovieRecordTime(this);
        limitedRecordTime_ = Integer.parseInt(strRcordTime);

        String recordFilePath = recordPath_ + "/" + String.format("%d", System.currentTimeMillis()) + ".mp4";
        muxer_ = new FFMPEGMuxer();
        if (!muxer_.openFile(recordFilePath)) {
            muxer_ = null;
            return null;
        }

        return recordFilePath;
    }

    protected void stopRecord() {
        if (muxer_ == null)
            return;

        muxer_.closeFile();
        muxer_ = null;
        startRecordTime_ = 0;
        curRecordTime_ = 0;
        limitedRecordTime_ = 0;
    }

    protected boolean isAliveMux() {
        return (muxer_ != null);
    }

    protected void updatedRecordVideoTime(int mins, int seconds) {
    }

    protected void writeCommentToMuxer(String comment) {
        if (muxer_ == null)
            return;

        muxer_.writeComment(comment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.activityToFullscreen(this);

        boolean autoDestroy = getIntent().getBooleanExtra(INTENT_KEY_AUTO_DESTROY_WHEN_SCREENOFF, false);
        if (autoDestroy)
            registerReceiver(screenBroadcastRecver_, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        else
            screenBroadcastRecver_ = null;

        cameraDisplayView_ = new CameraDisplayView(this, true);
        cameraDisplayView_.setActionTarget(this);
        cameraDisplayView_.setTag("CameraDisplayView");

        DoorCallApplication.getInstance().setRunningVideoActivity(this);
        recordPath_ = getRecordPath();
        removeOldRecordFile(Environment.getExternalStorageDirectory());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.activityToFullscreen(this);
    }

    @Override
    public void finish() {
        if (screenBroadcastRecver_ != null)
            unregisterReceiver(screenBroadcastRecver_);

        stopRecord();
        cameraDisplayView_.stopAllLiveVideo();
        DoorCallApplication.getInstance().setRunningVideoActivity(null);
        super.finish();
    }

    @Override
    public void onCommandResult(int cameraId, int streamNo, RTSPReceivable.RTSPClientCommand cmd, boolean cmdResult) {
    }

    @Override
    public void onRecvVideoEncodeStream(int cameraId, int streamNo, RTSPReceivable.MediaCodec mediaCodecId, byte[] mediaData, int mediaSize, int timesec) {
        if (muxer_ == null || mediaCodecId != RTSPReceivable.MediaCodec.H264)
            return;

        if (videoWidth_ == 0 || videoHeight_ == 0) {
            VideoStreamParser.VideoResolution resolution = videoStreamParser_.getResolution(VideoStreamParser.VideoParserCodec.H264, mediaData, mediaSize);
            if (resolution.videoWidth_ != 0 && resolution.videoHeight_ != 0) {
                videoWidth_ = resolution.videoWidth_;
                videoHeight_ = resolution.videoHeight_;
            }
            else
                return;
        }

        timesec = (int)(System.currentTimeMillis() / 1000);
        if (startRecordTime_ == 0)
            startRecordTime_ = timesec;
        if (curRecordTime_ == 0)
            curRecordTime_ = timesec;

        muxer_.writeFile(FFMPEGMuxer.mediaType.MEDIA_TYPE_VIDEO, FFMPEGMuxer.codecId.CODEC_ID_H264, mediaData, mediaSize, videoWidth_, videoHeight_, timesec);

        if (curRecordTime_ != timesec) {
            curRecordTime_ = timesec;

            int diffSeconds = curRecordTime_ - startRecordTime_;
            if ( diffSeconds > limitedRecordTime_) {
                stopRecord();
                return;
            }

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    int diffSeconds = curRecordTime_ - startRecordTime_;
                    int mins = diffSeconds / 60;
                    int seconds = diffSeconds % 60;
                    updatedRecordVideoTime(mins, seconds);
                }
            });
        }
    }
}
