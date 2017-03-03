package commax.wallpad.videoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.VideoView;

public class CustomVideoView extends VideoView {

    private StartPauseListener listener_;
    private SeekBar mediaSeekBar_;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int bottomNavigationHeight = getResources().getDimensionPixelSize(resourceId);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels; // - 32
        setMeasuredDimension(deviceWidth, deviceHeight + bottomNavigationHeight);
    }

    @Override
    public void start() {
        super.start();

        if (listener_ != null)
            listener_.onStart();
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();

        if (listener_ != null)
            listener_.onStop();
    }

    public void setStartPauseListener(StartPauseListener listener) {
        listener_ = listener;
    }

    public static interface StartPauseListener {
        void onStart();
        void onStop();
    }
}
