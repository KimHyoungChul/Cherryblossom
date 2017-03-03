package com.commax.settings.ringtone;

import android.content.Context;
import android.widget.Toast;

import com.commax.settings.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 코맥스 벨소리 설정 관리
 * Created by bagjeong-gyu on 2016. 10. 4..
 */


public class CommaxRingtoneSettingManager {

    public static final String RINGTONE_FOLDER_PATH = "/user/app/bin/sound/";
    private final Context mContext;
    private String[] ringtoneNames; //벨소리 명
    private String[] ringtoneFiles; //mp3 파일명

    private static CommaxRingtoneSettingManager instance;

    private CommaxRingtoneSettingManager(Context context) {
        mContext = context;
        readSoundFile();

    }

    public static CommaxRingtoneSettingManager getInstance(Context context) {
        if (instance == null) {
            instance = new CommaxRingtoneSettingManager(context);
        }
        return instance;
    }


    /**
     * 폴더 읽음
     *
     * @param path
     * @return
     */
    private ArrayList<String> readFolder(final String path) {

        final File f = new File(path);
        final File[] files = f.listFiles();
        if (files == null) {
            return null;
        }

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            if (file.isDirectory()) {
                continue;
            }
            list.add(file.getName());
        }
        return list;
    }

    /**
     * 폴더내에 위치한 sound 파일 목록 생성
     */
    private void readSoundFile() {
        ArrayList<String> arr = readFolder(RINGTONE_FOLDER_PATH);
        if (arr != null) {
            if (arr.size() > 0) {
                ArrayList<String> list = new ArrayList<String>();

                for (int i = 0; i < arr.size(); i++) {
                    String value = arr.get(i);
                    if (value.startsWith("call") || value.startsWith("melody")) {
                        list.add(value);
                    }
                }

                Collections.sort(list);
                int max = list.size();
                ringtoneNames = new String[max];
                ringtoneFiles = new String[max];

                for (int i = 0; i < max; i++) {
                    final String format = "%1$s %2$d";
                    final int number = i + 1;

                    ringtoneNames[i] = String.format(format,
                            mContext.getString(R.string.ringtone), number);
                    ringtoneFiles[i] = list.get(i);

                }
            }
        }
    }

    /**
     * 벨소리명 가져옴
     *
     * @param value         mp3파일명
     * @param ringtoneNames 벨소리 명
     * @param ringtoneFiles 벨소리 파일명
     * @return
     */
    private String getMatchedName(String value, String[] ringtoneNames,
                                  String[] ringtoneFiles) {
        if (value != null) {
            if (ringtoneNames != null) {
                if (ringtoneFiles != null) {
                    for (int i = 0; i < ringtoneFiles.length; i++) {
                        if (ringtoneFiles[i].equals(value)) {
                            return ringtoneNames[i];
                        }
                    }
                }
            }
        }

        return null;
    }


    /**
     * 현관 벨소리 명 가져옴
     *
     * @return
     */
    public String getEntranceRingtoneName() {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        //value: mp3파일명
        final String value = ring.getEntranceRingtone();
        return getMatchedName(value, ringtoneNames,
                ringtoneFiles);
    }


    /**
     * 현관 벨소리 설정에 저장
     *
     * @param value
     */
    public void setEntranceRingtone(String value) {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        boolean result = ring.setEntranceRingtone(value);
        if (result) {
            //성공 처리!!

        } else {

            Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 벨소리 데이터 리스트 가져옴
     *
     * @return
     */
    public List<CommaxRingtone> getRingtoneList() {
        List<CommaxRingtone> datas = new ArrayList<>();

        //A64보드에서 코맥스 ringtone 파일이 없어 ringtoneFiles가 null임
        if (ringtoneFiles == null) {
            return datas;

        }

        int listSize = ringtoneFiles.length;
        CommaxRingtone ringtone = null;
        for (int i = 0; i < listSize; i++) {
            ringtone = new CommaxRingtone();
            ringtone.setName(ringtoneNames[i]);
            ringtone.setFile(ringtoneFiles[i]);

            datas.add(ringtone);
        }


        return datas;
    }

    /**
     * 공동구역 벨소리 명 가져옴
     *
     * @return
     */
    public String getPublicAreaRingtoneName() {
        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        final String value = ring.getPublicAreaRingtone();

        return getMatchedName(value, ringtoneNames,
                ringtoneFiles);
    }

    /**
     * 일반전화 벨소리 명 가져옴
     *
     * @return
     */
    public String getPstnRingtoneName() {
        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        final String value = ring.getPstnRingtone();

        return getMatchedName(value, ringtoneNames,
                ringtoneFiles);
    }

    /**
     * 경비실 벨소리 명 가져옴
     *
     * @return
     */
    public String getGuardHouseRingtoneName() {
        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        final String value = ring.getGuardHouseRingtone();

        return getMatchedName(value, ringtoneNames,
                ringtoneFiles);
    }

    /**
     * 내선 벨소리 명 가져옴
     *
     * @return
     */
    public String getExtensionRingtoneName() {
        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        final String value = ring.getExtensionRingtone();

        return getMatchedName(value, ringtoneNames,
                ringtoneFiles);
    }

    /**
     * 공동구역 벨소리 설정에 저장
     *
     * @param value
     */
    public void setPublicAreaRingtone(String value) {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        boolean result = ring.setPublicAreaRingtone(value);
        if (result) {
            //성공 처리!!

        } else {

            Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 일반전화 벨소리 설정에 저장
     *
     * @param value
     */
    public void setPstnRingtone(String value) {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        boolean result = ring.setPstnRingtone(value);
        if (result) {
            //성공 처리!!

        } else {

            Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 경비실 벨소리 설정에 저장
     *
     * @param value
     */
    public void setGuardHouseRingtone(String value) {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        boolean result = ring.setGuardHouseRingtone(value);
        if (result) {
            //성공 처리!!

        } else {

            Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 내선 벨소리 설정에 저장
     *
     * @param value
     */
    public void setExtensionRingtone(String value) {

        final CommaxRingtoneManager ring = new CommaxRingtoneManager(mContext);
        boolean result = ring.setExtensionRingtone(value);
        if (result) {
            //성공 처리!!

        } else {

            Toast.makeText(mContext, R.string.fail_change_ringtone, Toast.LENGTH_SHORT).show();
        }
    }
}
