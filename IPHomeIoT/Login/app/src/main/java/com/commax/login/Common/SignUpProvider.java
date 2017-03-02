package com.commax.login.Common;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * Created by OWNER on 2017-01-19.
 * 회원가입 계정 및 관련 정보 ContentProvicer 로 적용 테스트 개발건
 * 사용하지 않음
 */
public class SignUpProvider extends ContentProvider{

    public static final String  AUTHORITY   = "com.commax.login";

    public static final Uri CONTENT_URI  =
            Uri.parse("content://" + AUTHORITY);

    private String Auth_key = "K333KAAARABIANUUUU";

    public String getAuthkey() {
        return Auth_key;
    }

    public void setAuthkey(String authkey) {
        Auth_key = authkey;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /**
     * ContentProvider 객체가 생성 되면 호출
     */
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Log.d("PROVIDERT", "A_insert()");

        List<String> reqValue = uri.getPathSegments();

        if(reqValue.size() > 0) {
            String serviceType = reqValue.get(0);
            Log.d("PROVIDERT", "A_serviceType = " + serviceType);

            if(serviceType.equals("AUTH_GET")){

                return Uri.parse(CONTENT_URI + "/" + serviceType + "/" + getAuthkey());
            }
        }

        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        Log.d("PROVIDERT", "A_update()");

        List<String> reqValue = uri.getPathSegments();

        if(reqValue.size() > 0) {
            String serviceType = reqValue.get(0);
            Log.d("PROVIDERT", "A_serviceType = " + serviceType);

            if(serviceType.equals("AUTH_UPDATE")){

                String new_authkey = values.getAsString("new_authkey");
                Log.i("PROVIDERT", "update() new_authkey = " + new_authkey);

                setAuthkey(new_authkey);
            }
        }

        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
