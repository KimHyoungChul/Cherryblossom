package commax.wallpad.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

interface VideoListAdapterSelectState {
    public void selectItmeCount(int adapterId, int count);
}

public class VideoListAdapter extends BaseAdapter {

    private List<MediaFileManager.fileInfo> videoFileInfo_;
    private List<Boolean> selectedDeleteNum_;
    private String fileRoot_;
    private Context context_;
    private LayoutInflater layoutInflater_;
    private DisplayMetrics displayMetrics_;
    private boolean isDelete_;
    private boolean isToday_ = false;
    private int adapterId_;
    private VideoListAdapterSelectState notifier_;
    private ThreadPoolExecutor threadPoolExecutor_;
    private LinkedBlockingQueue<Runnable> runnableQueue_;

    public VideoListAdapter(int adapterId, VideoListAdapterSelectState notifier, Context context, String fileRoot, List<MediaFileManager.fileInfo> videoFileInfo, DisplayMetrics displayMetrics) {
        adapterId_ = adapterId;
        notifier_ = notifier;
        context_ = context;
        fileRoot_ = fileRoot;
        videoFileInfo_ = videoFileInfo;
        selectedDeleteNum_ = new ArrayList<Boolean>();
        layoutInflater_ = (LayoutInflater)context_.getSystemService(context_.LAYOUT_INFLATER_SERVICE);
        displayMetrics_ = displayMetrics;
        isDelete_ = false;
        runnableQueue_ = new LinkedBlockingQueue<Runnable>();
        threadPoolExecutor_ = new ThreadPoolExecutor(10, 10, 100, TimeUnit.MILLISECONDS,runnableQueue_);

        for (int i=0; i<videoFileInfo_.size(); i++)
            selectedDeleteNum_.add(false);
    }

    public void setDeleteState(boolean isDelete) {
        isDelete_ = isDelete;
        if (!isDelete_)
            initDeleteState();
    }

    private void initDeleteState() {
        for ( int num = 0; num <selectedDeleteNum_.size(); num++)
            selectedDeleteNum_.set(num, false);
    }

    public void deleteFile(int sectionId) {
        for (int i=0; i<selectedDeleteNum_.size(); i++) {
            if (selectedDeleteNum_.get(i) == true) {
                String fileName = videoFileInfo_.get(i).fileName_;
                File file = new File(fileRoot_ + "/" + fileName);
                file.delete();

                MediaFileManager.getInstance().deleteFromeFilename(fileName);
            }

        }
    }

    public void setAllDelete(boolean isAllDelete) {
        for (int i=0; i<videoFileInfo_.size(); i++)
            selectedDeleteNum_.set(i, isAllDelete);

        if (isAllDelete)
            notifier_.selectItmeCount(adapterId_, videoFileInfo_.size());
        else
            notifier_.selectItmeCount(adapterId_, 0);
    }

    public void updateData(List<MediaFileManager.fileInfo> videoFileInfo) {
        videoFileInfo_.clear();
        videoFileInfo_ = videoFileInfo;
        selectedDeleteNum_.clear();
        for (int i=0; i<videoFileInfo_.size(); i++) {
            selectedDeleteNum_.add(false);
        }
    }

    public void setIsToday(boolean isToday) {
        isToday_ = isToday;
    }

    @Override
    public int getCount() {
        return videoFileInfo_.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View view;
        if (convertView == null) {
            view = layoutInflater_.inflate(R.layout.main_griditem, viewGroup, false);
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        }
        else {
            view = convertView;
        }

        final ImageView thumnbnailImage = (ImageView) view.findViewById(R.id.thumnbnailImage);
        final ImageView thumnbnailIcon = (ImageView) view.findViewById(R.id.thumbnailIcon);
        final ImageView thumnbnailAbsence = (ImageView) view.findViewById(R.id.thumnbnailAbsence);
        final ImageView thumnbnailOtherParty = (ImageView) view.findViewById(R.id.thumbnailOtherParty);
        final ImageView deleteUnCheck = (ImageView) view.findViewById(R.id.deleteUnCheck);
        final ImageView deleteIsCheck = (ImageView) view.findViewById(R.id.deleteIsCheck);
        TextView thumnbnailText = (TextView) view.findViewById(R.id.thumbnailText);
        final FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.thumnbnailTitleBg);

        final int width = Math.round(displayMetrics_.widthPixels/3);
        final int height = Math.round(displayMetrics_.widthPixels*3/16);
        final String filePath = fileRoot_ + "/" + videoFileInfo_.get(i).fileName_;

        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(width, height));
        frameLayout.setBackgroundResource(R.mipmap.bg_thumbnail2_shadow);

        threadPoolExecutor_.execute(new Runnable() {

            @Override
            public void run() {
                Bitmap thumnbnail;
                final Bitmap outputThumnbnail;
                if (videoFileInfo_.size() == 0)
                    return;

                if (videoFileInfo_.get(i).fileName_.contains(".mp4")) {
                    thumnbnailIcon.setVisibility(View.VISIBLE);
                    thumnbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                }
                else {
                    thumnbnailIcon.setVisibility(View.INVISIBLE);
                    thumnbnail = BitmapFactory.decodeFile(filePath);
                }

                if (thumnbnail == null) {
                    thumnbnail = Bitmap.createBitmap(300,200, Bitmap.Config.ARGB_8888);
                    thumnbnail.eraseColor(Color.BLACK);
                }

                outputThumnbnail = Bitmap.createScaledBitmap(thumnbnail, width, height, true);

                    android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
                            thumnbnailImage.setLayoutParams(layoutParams);
                            thumnbnailImage.setImageBitmap(outputThumnbnail);

                            if (!videoFileInfo_.get(i).mode_.equals("2") && videoFileInfo_.get(i).type_.equals("3"))
                                thumnbnailAbsence.setVisibility(View.VISIBLE);
                            else
                                thumnbnailAbsence.setVisibility(View.GONE);

                            if ( videoFileInfo_.get(i).from_.equals("2")) {
                                thumnbnailOtherParty.setImageDrawable(context_.getDrawable(R.mipmap.ic_apps_thumbnail_common));
                            } else {
                                thumnbnailOtherParty.setImageDrawable(context_.getDrawable(R.mipmap.ic_apps_thumbnail_my));
                            }
                        }

                });
            }
        });

        StringTokenizer stringTokenizer = new StringTokenizer(videoFileInfo_.get(i).fileName_, ".");
        String dateS = stringTokenizer.nextToken();

        long dateL = Long.parseLong(dateS);
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
                    thumnbnailText.setText(context_.getString(R.string.am) + " " + strTime);
                    break;
                case Calendar.PM:
                    thumnbnailText.setText(context_.getString(R.string.pm) + " " + strTime);
                    break;
            }
        }
        else
            thumnbnailText.setText(strDate);

        if (isDelete_) {
            if (selectedDeleteNum_.get(i)) {
                deleteIsCheck.setVisibility(View.VISIBLE);
                deleteUnCheck.setVisibility(View.GONE);
            }
            else {
                deleteIsCheck.setVisibility(View.GONE);
                deleteUnCheck.setVisibility(View.VISIBLE);
            }
        }
        else {
            deleteUnCheck.setVisibility(View.GONE);
            deleteIsCheck.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            boolean selectedDelete = false;
            @Override
            public void onClick(View v) {
                selectedDelete = selectedDeleteNum_.get(i);
                if(isDelete_) {
                    if (!selectedDelete) {
                        deleteIsCheck.setVisibility(View.VISIBLE);
                        deleteUnCheck.setVisibility(View.GONE);
                        selectedDelete = true;
                        selectedDeleteNum_.set(i, selectedDelete);
                    }
                    else {
                        deleteIsCheck.setVisibility(View.GONE);
                        deleteUnCheck.setVisibility(View.VISIBLE);
                        selectedDelete = false;
                        selectedDeleteNum_.set(i, selectedDelete);
                    }
                    int count = 0;
                    for (int num = 0; num < selectedDeleteNum_.size(); num ++)
                    {
                        if (selectedDeleteNum_.get(num))
                            count++;
                    }
                    notifier_.selectItmeCount(adapterId_, count);
                }
                else {
                    selectedDelete = false;

                    for ( int num = 0; num <selectedDeleteNum_.size(); num++)
                        selectedDeleteNum_.set(num, selectedDelete);

                    setVideoPlayer(videoFileInfo_.get(i).fileName_);
                }
            }
        });

        return view;
    }

    private void setVideoPlayer(String fileName) {
        if (!isDelete_) {
            Intent videoPlay = new Intent(context_, VideoPlayer.class);
            videoPlay.putExtra("fileName", fileName);
            context_.startActivity(videoPlay);
            ((Activity) context_).finish();
        }
    }
}
