package com.commax.homereporter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MissedItem extends LinearLayout {

    private static final String TAG = "MissedItem";
    ImageView iv_icon;
    TextView tv_type;
    TextView tv_badge;
    LinearLayout lay_badge;

    Context mContext;

    public MissedItem(Context context, String type, boolean not_dimmed, int count) {
        super(context);
        mContext = context;
        init(context, type, not_dimmed, count);
    }

    public void init(Context context, String type, boolean not_dimmed, int count){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rootView = inflater.inflate(R.layout.missed_item, this);

        tv_type = (TextView) rootView.findViewById(R.id.tv_type);
        FrameLayout lay_img = (FrameLayout) rootView.findViewById(R.id.lay_img);
        lay_badge = (LinearLayout) rootView.findViewById(R.id.lay_badge);
        tv_badge = (TextView) rootView.findViewById(R.id.tv_badge);
        iv_icon = (ImageView) rootView.findViewById(R.id.iv_icon);

        setView(type, not_dimmed, count);
    }

    private void setView(String type, boolean not_dimmed, int count){

        try {
            switch (type) {

                case NameSpace.MISSED_CALL:
                    setMissedCallView(not_dimmed, count);
                    break;

                case NameSpace.MISSED_VISITOR:
                    setMissedVisitorView(not_dimmed, count);
                    break;

                case NameSpace.MISSED_DELIVERY:
                    setMissedDeliveryView(not_dimmed, count);
                    break;

            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setMissedCallView(boolean not_dimmed, int count){

        try {
            iv_icon.setBackground(getResources().getDrawable(R.mipmap.ic_rp_call));
            tv_type.setText(getResources().getString(R.string.calls));

            if (count > 0) {
                lay_badge.setVisibility(VISIBLE);
                if (count<1000) {
                    tv_badge.setText("" + String.valueOf(count));
                }else {
                    tv_badge.setText("999+");
                }
            } else {
                lay_badge.setVisibility(INVISIBLE);
            }

            if (not_dimmed) {
                iv_icon.setAlpha(0.3f);
                tv_type.setAlpha(0.3f);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setMissedVisitorView(boolean not_dimmed, int count){

        try {
            iv_icon.setBackground(getResources().getDrawable(R.mipmap.ic_rp_visitor));
            tv_type.setText(getResources().getString(R.string.visitor));

            if (count > 0) {
                lay_badge.setVisibility(VISIBLE);
                updateVisitors();
            } else {
                lay_badge.setVisibility(INVISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setMissedDeliveryView(boolean not_dimmed, int count){

        try {
            iv_icon.setBackground(getResources().getDrawable(R.mipmap.ic_rp_box));
            tv_type.setText(getResources().getString(R.string.delivery));

            if (count > 0) {
                lay_badge.setVisibility(VISIBLE);
            } else {
                lay_badge.setVisibility(INVISIBLE);
            }

            if (not_dimmed) {
                iv_icon.setAlpha(0.3f);
                tv_type.setAlpha(0.3f);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateVisitors() {

        try {
            // 방문자 확인 관련 =========================
            Bitmap orgImage = null;
            Bitmap resizeImage = null;

            Cursor imageCursor = null;
            String[] strImageFileArray = new String[]{};
            String strImageFileName = "";
            int path_column_index;
            String imgName = "";
            int image_size = 60;

            try {
                image_size = (int) getResources().getDimension(R.dimen.missed_item_img_height);
            } catch (Exception e) {
                image_size = 60;
            }

      /* MediaStore Column */
            String strCmd[] = {MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DESCRIPTION,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            try {
                imageCursor = mContext.getContentResolver().query(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        strCmd,
                        null,
                        null,
                        MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            } catch (UnsupportedOperationException e) {
                Log.e(TAG, "Unsupported Operation Exception. !!!");
            } catch (NullPointerException e) {
                Log.e(TAG, "Null Pointer Exception. !!!");
            }

            // DB에서 최근날짜로 저장된 path 가져옴
            Log.i(TAG, "imageCursor.getCount() : " + imageCursor.getCount());

            if (imageCursor.getCount() > 0) {
                try {
                    imageCursor.moveToFirst();
                    path_column_index = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    imgName = imageCursor.getString(path_column_index);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "FileName imgName : " + imgName);

                // 동영상 저장파일에서는 첫 번째 이미지 파일의 path를 가져옴
                strImageFileArray = TextUtils.split(imgName, "[.]");
                if (strImageFileArray[1].equals("mp4") == true) {
                    strImageFileName = String.format("%s.jpg", strImageFileArray[0]);
                } else {
                    strImageFileName = imgName;

                }

                Log.i(TAG, "strImageFileName : " + strImageFileName);
                Log.i(TAG, "imgName : " + imgName);
                Log.d(TAG, "imgSize : " + image_size);

                // jpg 이미지파일을 Bitmap으로 변경하고 위젯 크기에 맞게 resize 함
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                orgImage = BitmapFactory.decodeFile(strImageFileName, options);
                resizeImage = Bitmap.createScaledBitmap(orgImage, image_size, image_size, true);

                // 이미지 적용
                iv_icon.setImageBitmap(cropCircle(resizeImage));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap cropCircle(Bitmap bitmap) {

        int raw_size = 60;

        try{
            raw_size = (int)getResources().getDimension(R.dimen.missed_item_img_height);
        }catch (Exception e){
            raw_size=60;
        }

        Bitmap output = Bitmap.createBitmap(raw_size,raw_size, Bitmap.Config.ARGB_8888);

        try {
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, raw_size, raw_size);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            int size = (bitmap.getWidth() / 2);
            canvas.drawCircle(size, size, size, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }catch (Exception e){
            e.printStackTrace();
        }

        return output;
    }
}
