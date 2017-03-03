package com.commax.settings.ringtone;

import android.content.Context;

import com.commax.settings.setting_provider.SettingProvider;
import com.commax.settings.setting_provider.StringEx;

/**
 * 코맥스 벨소리 관리
 */
public class CommaxRingtoneManager {
    public static final int INDEX_DOOR = 13; //현관
    public static final int INDEX_EXTENSION = 16; //내선
    public static final int INDEX_GUARD = 14; //경비
    public static final int INDEX_PSTN = 17; //일반
    public static final int INDEX_PUBLIC = 15; //공동구역

    private Context context;

    public CommaxRingtoneManager(Context context) {
        this.context = context;
    }

    /**
     * 디폴트 현관 벨소리 가져옴
     *
     * @return
     */
    public String getEntranceRingtone() {

        return new SettingProvider(context).getValue(INDEX_DOOR);

    }

    /**
     * 디폴트 현관 벨소리 설정에 저장
     *
     * @param value
     * @return
     */
    public Boolean setEntranceRingtone(String value) {
        if (StringEx.isFault(value)) {
            return false;
        }

        return new SettingProvider(context).setValue(INDEX_DOOR, value);

    }

    /**
     * 디폴트 내선 벨소리 가져옴
     *
     * @return
     */
    public String getExtensionRingtone() {

        return new SettingProvider(context).getValue(INDEX_EXTENSION);

    }

    /**
     * 내선 벨소리 설정 저장
     *
     * @param value
     * @return
     */
    public Boolean setExtensionRingtone(String value) {
        if (StringEx.isFault(value)) {
            return false;
        }

        return new SettingProvider(context).setValue(INDEX_EXTENSION, value);

    }

    /**
     * 디폴트 경비실 벨소리 가져옴
     *
     * @return
     */
    public String getGuardHouseRingtone() {

        return new SettingProvider(context).getValue(INDEX_GUARD);

    }

    /**
     * 경비실 벨소리 설정 저장
     *
     * @param value
     * @return
     */
    public Boolean setGuardHouseRingtone(String value) {
        if (StringEx.isFault(value)) {
            return false;
        }

        return new SettingProvider(context).setValue(INDEX_GUARD, value);

    }

    /**
     * 디폴트 일반전화 벨소리 가져옴
     *
     * @return
     */
    public String getPstnRingtone() {

        return new SettingProvider(context).getValue(INDEX_PSTN);

    }

    /**
     * 일반전화 벨소리 설정 저장
     *
     * @param value
     * @return
     */
    public Boolean setPstnRingtone(String value) {
        if (StringEx.isFault(value)) {
            return false;
        }

        return new SettingProvider(context).setValue(INDEX_PSTN, value);

    }

    /**
     * 디폴트 공동구역 벨소리 가져옴
     *
     * @return
     */
    public String getPublicAreaRingtone() {

        return new SettingProvider(context).getValue(INDEX_PUBLIC);

    }

    /**
     * 공동구역 벨소리 설정 저장
     *
     * @param value
     * @return
     */
    public Boolean setPublicAreaRingtone(String value) {
        if (StringEx.isFault(value)) {
            return false;
        }

        return new SettingProvider(context).setValue(INDEX_PUBLIC, value);

    }
}
