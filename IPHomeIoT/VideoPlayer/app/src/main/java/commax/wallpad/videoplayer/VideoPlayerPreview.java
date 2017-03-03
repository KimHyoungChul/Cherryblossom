package commax.wallpad.videoplayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.commax.iphomeiot.common.ui.dialog.PopUpDialog;
import com.commax.nubbyj.media.demuxer.FFMPEGDemuxer;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class VideoPlayerPreview extends Activity implements ITerminatedThread{

    private CustomVideoView videoPlayer_;
    private SeekBar previewMediaSeekBar_;
    private ImageView startVideo_;
    private ImageView deleteOneFile_;
    private TextView fileNameView_;
    private File file_ = null;
    private ImageView close_;
    private DisplayMetrics displayMetrics_;
    private String fileName_;
    private String storageRoot_;
    private ImageView videoThirdParty_;
    private ImageView absence_;
    private boolean isToday_;
    private PopUpDialog popUpDialog_;
    private FrameLayout videoViewBack_;
    private FrameLayout previewTitleBar_;
    private View decorView_;
    private int uiOptions_ = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private PreviewDrawSeekBar drawSeekBarThread_;
    private boolean isAbsence_;
    private boolean isTouch_ = false;
    private BroadcastReceiver receiver_;

    class PreviewDrawSeekBar extends Thread {
        private boolean isPlaying_;

        @Override
        public void run() {
            while (!isPlaying_) {
                previewMediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
            }
        }

        public void terminate() {
            isPlaying_ = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer_preview_activity);

        decorView_ = getWindow().getDecorView();
        decorView_.setSystemUiVisibility(uiOptions_);

        displayMetrics_ = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics_);

        storageRoot_ = MediaFileManager.getInstance().getStorageRoot();
        if (MediaFileManager.getInstance().size() == 0)
            MediaFileManager.getInstance().getFileList(storageRoot_, this);

        final Intent videoPlayIntent = getIntent();
        fileName_ = videoPlayIntent.getExtras().getString("fileName");
        isToday_ = videoPlayIntent.getExtras().getBoolean("isToday");

        startVideo_ = (ImageView) this.findViewById(R.id.previewStartVideo);
        fileNameView_ = (TextView) this.findViewById(R.id.previewFilenameView);
        videoPlayer_ = (CustomVideoView)this.findViewById(R.id.previewVideoPlayer);
        deleteOneFile_ = (ImageView) this.findViewById(R.id.previewDelete);
        videoThirdParty_ = (ImageView) this.findViewById(R.id.previewVideoThirdParty);
        close_ = (ImageView) this.findViewById(R.id.previewClose);
        absence_ = (ImageView) this.findViewById(R.id.previewAbsence);
        absence_.setVisibility(View.GONE);
        videoViewBack_ = (FrameLayout) findViewById(R.id.previewShadowBack);
        previewTitleBar_ = (FrameLayout) findViewById(R.id.previewTitleBar);
        previewMediaSeekBar_ = (SeekBar) findViewById(R.id.previewMediaSeekBar);
        previewMediaSeekBar_.setProgressTintList(ColorStateList.valueOf(Color.argb(225, 106, 114, 204)));
        displayMedia(fileName_);

        videoPlayer_.setStartPauseListener(new CustomVideoView.StartPauseListener() {
            @Override
            public void onStart() {
                if (drawSeekBarThread_ == null) {
                    drawSeekBarThread_ = new PreviewDrawSeekBar();
                    drawSeekBarThread_.start();
                    previewMediaSeekBar_.setMax(videoPlayer_.getDuration());
                }
                videoPlayer_.setBackground(null);
                setReadyStartView(true);
            }

            @Override
            public void onStop() {
                terminateThread();
                setReadyStartView(false);
                videoPlayer_.seekTo(0);
                previewMediaSeekBar_.setProgress(0);
            }
        });

        startVideo_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoPlayer_.isPlaying())
                    videoPlayer_.pause();
                else {
                    startVideo_.setImageDrawable(getDrawable(R.mipmap.btn_apps_thumbnail_video_pause_n));
                    videoPlayer_.start();
                    isTouch_ = false;
                }

            }
        });

        deleteOneFile_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoPlayer_.isPlaying())
                    videoPlayer_.pause();
                terminateThread();
                popUpDialog_ = new PopUpDialog(VideoPlayerPreview.this);
                popUpDialog_.setText(getString(R.string.deletePopTitle), getString(R.string.deletePopContents), getString(R.string.yes), getString(R.string.no));
                popUpDialog_.setListener(okClickListener, cancelClickListener);
                popUpDialog_.setFullScreen();
                popUpDialog_.show();
            }
        });

        close_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateThread();
                finish();
            }
        });

        previewMediaSeekBar_.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!videoPlayer_.isPlaying()) {
                    startVideo_.setImageDrawable(getDrawable(R.mipmap.btn_apps_thumbnail_video_n));
                }

                if (fromUser) {
                    if (drawSeekBarThread_ == null) {
                        drawSeekBarThread_ = new PreviewDrawSeekBar();
                        drawSeekBarThread_.start();
                    }

                    if (!videoPlayer_.isPlaying()) {
                        startVideo_.setImageDrawable(getDrawable(R.mipmap.btn_apps_thumbnail_video_pause_n));
                        videoPlayer_.start();
                    }

                    videoPlayer_.seekTo(progress);
                    previewMediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoPlayer_.seekTo(seekBar.getProgress());
                previewMediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        videoPlayer_.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isTouch_) {
                    setReadyStartView(false);
                    isTouch_ = true;
                }
                else {
                    setReadyStartView(true);
                    isTouch_ = false;
                }
                return false;
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver_ = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
                    finish();
            }
        }, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        MediaFileManager.getInstance().clearFile();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver_ != null) {
            unregisterReceiver(receiver_);
            receiver_ = null;
        }
    }

    private void terminateThread() {
        if (drawSeekBarThread_ != null) {
            drawSeekBarThread_.terminate();
            try {
                drawSeekBarThread_.join();
                drawSeekBarThread_ = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(file_.exists()) {
                file_.delete();
                MediaFileManager.getInstance().deleteFromeFilename(fileName_);
                finish();
            }
            popUpDialog_.dismiss();
            decorView_.setSystemUiVisibility(uiOptions_);
        }
    };

    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popUpDialog_.dismiss();
            decorView_.setSystemUiVisibility(uiOptions_);

            if (videoPlayer_.getCurrentPosition() == 0 )
                return;

            videoPlayer_.pause();
        }
    };

    private void setReadyStartView(boolean isReady) {
        if (isReady) {
            fileNameView_.setVisibility(View.GONE);
            startVideo_.setVisibility(View.GONE);
            if (isAbsence_)
                absence_.setVisibility(View.GONE);
            videoThirdParty_.setVisibility(View.GONE);
            videoViewBack_.setBackground(null);
            previewMediaSeekBar_.setMax(videoPlayer_.getDuration());
            previewTitleBar_.setVisibility(View.GONE);
            previewMediaSeekBar_.setVisibility(View.INVISIBLE);
        }
        else {
            fileNameView_.setVisibility(View.VISIBLE);
            startVideo_.setVisibility(View.VISIBLE);
            if (isAbsence_)
                absence_.setVisibility(View.VISIBLE);
            videoThirdParty_.setVisibility(View.VISIBLE);
            videoViewBack_.setBackground(getDrawable(R.mipmap.bg_thumbnail2_shadow));
            previewMediaSeekBar_.setMax(videoPlayer_.getDuration());
            previewTitleBar_.setVisibility(View.VISIBLE);
            previewMediaSeekBar_.setVisibility(View.VISIBLE);
        }
    }

    private void displayMedia(String fileName) {
        if (file_ != null)
            file_ = null;

        String filePath = storageRoot_ + "/" + fileName;
        fileNameView_.setVisibility(View.VISIBLE);
        videoViewBack_.setBackground(getDrawable(R.mipmap.bg_thumbnail2_shadow));

        FFMPEGDemuxer ffmpegDemuxer = new FFMPEGDemuxer();
        if (ffmpegDemuxer.openFile(filePath)) {
            String fromOtherParty = ffmpegDemuxer.readComment();
            StringTokenizer sectionTokenizer = new StringTokenizer(fromOtherParty, ",");
            String[] otherParty = new String[6];

            if (!fromOtherParty.isEmpty()) {
                int count = 0;
                while (sectionTokenizer.hasMoreTokens()) {
                    String data = sectionTokenizer.nextToken();
                    StringTokenizer partyTokenizer = new StringTokenizer(data, ":");

                    while (partyTokenizer.hasMoreTokens()){
                        String data1 = partyTokenizer.nextToken();
                        otherParty[count] = data1;
                        count++;
                    }
                }

                if (!otherParty[3].equals("2") && otherParty[5].equals("3")) {
                    absence_.setVisibility(View.VISIBLE);
                    isAbsence_ = true;
                }
                else {
                    absence_.setVisibility(View.GONE);
                    isAbsence_ = false;
                }

                if ( otherParty[1].equals("2")) {
                    videoThirdParty_.setImageDrawable(getDrawable(R.mipmap.ic_apps_thumbnail_common));
                } else {
                    videoThirdParty_.setImageDrawable(getDrawable(R.mipmap.ic_apps_thumbnail_my));
                }
            }
            ffmpegDemuxer.closeFile();
        }

        file_ = new File(filePath);

        if (videoPlayer_ == null) {
            videoPlayer_ = (CustomVideoView) this.findViewById(R.id.videoPlayer);
        }

        StringTokenizer stringTokenizer = new StringTokenizer(fileName, ".");
        String dateS = stringTokenizer.nextToken();

        long dateL = 0;
        try {
            dateL = Long.parseLong(dateS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if (dateL == 0)
            return;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(dateL);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");
        String strTime = sdfTime.format(dateL);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy.MM.dd");
        String strDate = sdfDate.format(dateL);

        if (isToday_) {
            int isAMorPM = cal1.get(Calendar.AM_PM);
            switch (isAMorPM) {

                case Calendar.AM:
                    fileNameView_.setText(getString(R.string.am) + " "+ strTime);
                    break;
                case Calendar.PM:
                    fileNameView_.setText(getString(R.string.pm) + " " + strTime);
                    break;
            }
        }
        else
            fileNameView_.setText(strDate);

        if (filePath.contains(".mp4")) {
            startVideo_.setVisibility(View.VISIBLE);
            videoPlayer_.setVisibility(View.VISIBLE);
            videoPlayer_.setVideoPath(filePath);
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
            videoPlayer_.setBackgroundDrawable(bitmapDrawable);
        }
    }

    @Override
    public void isTerminate() {
    }
}
