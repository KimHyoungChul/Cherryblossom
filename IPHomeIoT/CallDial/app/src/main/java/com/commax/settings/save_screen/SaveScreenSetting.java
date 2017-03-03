package com.commax.settings.save_screen;

/**
 * 영상 저장 데이터 모델
 * Created by OWNER on 2016-10-12.
 */

public class SaveScreenSetting {
    private String autoOrManual; //자동 또는 수동
    private String pictureOrMovie; //정지영상 또는 동영상
    private String motionDetectEnabled; //모션감지 저장 여부

    public String getAutoOrManual() {
        return autoOrManual;
    }

    public void setAutoOrManual(String autoOrManual) {
        this.autoOrManual = autoOrManual;
    }

    public String getPictureOrMovie() {
        return pictureOrMovie;
    }

    public void setPictureOrMovie(String pictureOrMovie) {
        this.pictureOrMovie = pictureOrMovie;
    }

    public String getMotionDetectEnabled() {
        return motionDetectEnabled;
    }

    public void setMotionDetectEnabled(String motionDetectEnabled) {
        this.motionDetectEnabled = motionDetectEnabled;
    }
}
