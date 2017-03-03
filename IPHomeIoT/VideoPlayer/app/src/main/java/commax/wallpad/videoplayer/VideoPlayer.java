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
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.iphomeiot.common.ui.dialog.PopUpDialog;
import com.commax.nubbyj.media.demuxer.FFMPEGDemuxer;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

public class VideoPlayer extends Activity implements ITerminatedThread{

    private CustomVideoView videoPlayer_;
    private SeekBar mediaSeekBar_;
    private ImageView startVideo_;
    private ImageView deleteOneFile_;
    private TextView fileNameView_;
    private File file_ = null;
    private ImageView back_;
    private DisplayMetrics displayMetrics_;
    private String fileName_;
    private String storageRoot_;
    private int filePosition_;
    private ImageView previousButton_;
    private ImageView nextButton_;
    private ImageView videoThirdParty_;
    private ImageView absence_;
    private PopUpDialog popUpDialog_;
    private View decorView_;
    private FrameLayout videoViewBack_;
    private FrameLayout titleBar_;
    private int uiOptions_ = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    private DrawSeekBar drawSeekBarThread_;
    private boolean isAbsence_;
    private boolean isTouch_ = false;
    private BroadcastReceiver receiver_;

    class DrawSeekBar extends Thread {
        private boolean isPlaying_;

        @Override
        public void run() {
            while (!isPlaying_) {
                try {
                    mediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
                }
                catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        public void terminate() {
            isPlaying_ = true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoplayer_activity);

        decorView_ = getWindow().getDecorView();
        decorView_.setSystemUiVisibility(uiOptions_);

        displayMetrics_ = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics_);

        storageRoot_ = MediaFileManager.getInstance().getStorageRoot();
        if (MediaFileManager.getInstance().size() == 0)
            MediaFileManager.getInstance().getFileList(storageRoot_, this);


        startVideo_ = (ImageView) findViewById(R.id.startVideo);
        fileNameView_ = (TextView) findViewById(R.id.filenameView);
        videoPlayer_ = (CustomVideoView)findViewById(R.id.videoPlayer);
        mediaSeekBar_ = (SeekBar) findViewById(R.id.mediaSeekBar);
        mediaSeekBar_.setProgressTintList(ColorStateList.valueOf(Color.argb(225, 106, 114, 204)));
        deleteOneFile_ = (ImageView) findViewById(R.id.deleteOneFile);
        back_ = (ImageView) findViewById(R.id.backToVideoList);
        previousButton_ = (ImageView) findViewById(R.id.previousButton);
        nextButton_ = (ImageView) findViewById(R.id.nextButton);
        videoThirdParty_ = (ImageView) findViewById(R.id.videoThirdParty);
        absence_ = (ImageView) findViewById(R.id.absence);
        absence_.setVisibility(View.GONE);
        videoViewBack_ = (FrameLayout)findViewById(R.id.videoViewBack);
        titleBar_ = (FrameLayout) findViewById(R.id.titleBar);

        videoPlayer_.setStartPauseListener(new CustomVideoView.StartPauseListener() {
            @Override
            public void onStart() {
                if (drawSeekBarThread_ == null) {
                    drawSeekBarThread_ = new DrawSeekBar();
                    drawSeekBarThread_.start();
                    mediaSeekBar_.setMax(videoPlayer_.getDuration());
                }
                videoPlayer_.setBackground(null);
                setReadyStartView(true);
            }

            @Override
            public void onStop() {
                terminateThread();
                setReadyStartView(false);
                videoPlayer_.seekTo(0);
                mediaSeekBar_.setProgress(0);
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
                popUpDialog_ = new PopUpDialog(VideoPlayer.this);
                popUpDialog_.setText(getString(R.string.deletePopTitle), getString(R.string.deletePopContents), getString(R.string.yes), getString(R.string.no));
                popUpDialog_.setListener(okClickListener, cancelClickListener);
                popUpDialog_.setFullScreen();
                popUpDialog_.show();

            }
        });

        back_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateThread();
                Intent back = new Intent(VideoPlayer.this, MainActivity.class);
                startActivity(back);
                finish();
            }
        });

        previousButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateThread();
                videoPlayer_.stopPlayback();
                filePosition_ = filePosition_ -1;
                if (filePosition_ < 0)
                    filePosition_ = MediaFileManager.getInstance().size() -1;

                displayMedia(filePosition_);
            }
        });

        nextButton_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateThread();

                videoPlayer_.stopPlayback();

                filePosition_ = filePosition_ +1;
                if (filePosition_ == MediaFileManager.getInstance().size())
                    filePosition_ = 0;

                displayMedia(filePosition_);
            }
        });

        mediaSeekBar_.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!videoPlayer_.isPlaying()) {
                    startVideo_.setImageDrawable(getDrawable(R.mipmap.btn_apps_thumbnail_video_n));
                }

                if (fromUser) {
                    if (drawSeekBarThread_ == null) {
                        drawSeekBarThread_ = new DrawSeekBar();
                        drawSeekBarThread_.start();
                    }

                    if (!videoPlayer_.isPlaying()) {
                        startVideo_.setImageDrawable(getDrawable(R.mipmap.btn_apps_thumbnail_video_pause_n));
                        videoPlayer_.start();
                    }

                    videoPlayer_.seekTo(progress);
                    mediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                videoPlayer_.seekTo(seekBar.getProgress());
                mediaSeekBar_.setProgress(videoPlayer_.getCurrentPosition());
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
                Intent back = new Intent(VideoPlayer.this, MainActivity.class);
                startActivity(back);
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
            mediaSeekBar_.setMax(videoPlayer_.getDuration());
            titleBar_.setVisibility(View.GONE);
            previousButton_.setVisibility(View.INVISIBLE);
            nextButton_.setVisibility(View.INVISIBLE);
            mediaSeekBar_.setVisibility(View.INVISIBLE);

        }
        else {
            fileNameView_.setVisibility(View.VISIBLE);
            startVideo_.setVisibility(View.VISIBLE);
            if (isAbsence_)
                absence_.setVisibility(View.VISIBLE);
            videoThirdParty_.setVisibility(View.VISIBLE);
            videoViewBack_.setBackground(getDrawable(R.mipmap.bg_thumbnail2_shadow));
            mediaSeekBar_.setMax(videoPlayer_.getDuration());
            titleBar_.setVisibility(View.VISIBLE);
            previousButton_.setVisibility(View.VISIBLE);
            nextButton_.setVisibility(View.VISIBLE);
            mediaSeekBar_.setVisibility(View.VISIBLE);
        }
    }

    private void displayMedia(int filePosition) {
        if (file_ != null)
            file_ = null;

        int fileId = MediaFileManager.getInstance().getFileId(filePosition);

        boolean isToday = false;
        isToday = MediaFileManager.getInstance().isToday(fileId);
        videoViewBack_.setBackground(getDrawable(R.mipmap.bg_thumbnail2_shadow));
        String filePath = storageRoot_ + "/" + MediaFileManager.getInstance().getFileName(fileId);

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
                    videoThirdParty_.setVisibility(View.VISIBLE);
                } else {
                    videoThirdParty_.setImageDrawable(getDrawable(R.mipmap.ic_apps_thumbnail_my));
                    videoThirdParty_.setVisibility(View.VISIBLE);
                }
            }
            ffmpegDemuxer.closeFile();
        }

        file_ = new File(filePath);

        if (videoPlayer_ == null) {
            videoPlayer_ = (CustomVideoView) this.findViewById(R.id.videoPlayer);
        }

        StringTokenizer stringTokenizer = new StringTokenizer( MediaFileManager.getInstance().getFileName(fileId), ".");
        String dateS = stringTokenizer.nextToken();

        long dateL = Long.parseLong(dateS);
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(dateL);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");
        String strTime = sdfTime.format(dateL);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy.MM.dd");
        String strDate = sdfDate.format(dateL);

        if (isToday) {
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

        fileNameView_.setVisibility(View.VISIBLE);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent back = new Intent(VideoPlayer.this, MainActivity.class);
            startActivity(back);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void isTerminate() {
        final Intent videoPlayIntent = getIntent();
        fileName_ = videoPlayIntent.getExtras().getString("fileName");
        filePosition_ = MediaFileManager.getInstance().getPosition(fileName_);

        if (filePosition_ == -1)
        {
            return;
        }
        android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                displayMedia(filePosition_);
            }
        });
    }
}
