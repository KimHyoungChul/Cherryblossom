package com.commax.ipdoorcamerasetting.content_provider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.commax.ipdoorcamerasetting.dongho.Dongho;


/**
 * Content Provider 관리
 * Created by bagjeong-gyu on 2016. 9. 26..
 */

public class ContentProviderManager {


    /**
     * 이전 동호 데이터 삭제
     *
     * @param context
     */
    public static void deletePreviousDongho(Context context) {
        int cursor =
                context.getContentResolver().delete(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        null, null);
    }


    /**
     * 동호 데이터 저장
     *
     * @param context
     * @param contentValues
     */
    public static void saveDongho(Context context, ContentValues contentValues) {


        Uri cursor =
                context.getContentResolver().insert(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        contentValues);


    }

    /**
     * 디바이스 IP가 Content Provider에 존재하는지 체크
     * @param context
     * @return
     */
    public static boolean isIpExistOnContentProvider(final Context context, String ip) {
        String[] args = { ip };
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.DeviceEntry.CONTENT_URI,
                        null,
                        ContentProviderConstants.DeviceEntry.COLUMN_NAME_IPV4 + "=?",
                        args,
                        null);

        if(cursor == null) {
            return false;
        }

        if(cursor.getCount() >0) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "이미 Content Provider에 저장되었습니다", Toast.LENGTH_SHORT).show();
                }
            });

            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }

    }

    /**
     * 동호 가져옴
     */
    public static Dongho getDongho(Context context) {
        Cursor cursor =
                context.getContentResolver().query(ContentProviderConstants.DonghoEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        Dongho dongho = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            dongho = new Dongho();

            dongho.setDong(cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.DonghoEntry.COLUMN_NAME_DONG)));

            dongho.setHo(cursor.getString(cursor
                    .getColumnIndex(ContentProviderConstants.DonghoEntry.COLUMN_NAME_HO)));

        }

        return dongho;


    }

}
