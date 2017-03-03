package com.commax.settings.external_device;

/**
 * IP Door Camera 데이터 모델
 * Created by OWNER on 2016-10-12.
 */

public class IpDoorCamera {
    private String mainCodec; //메인스트림 코덱
    private String mainResolution; //메인스트림 해상도
    private String mainGop; //메인스트림 GOP
    private String mainBitrate; //메인스트림 비트레이트
    private String mainFramerate; //메인스트림 프레임레이트

    private String subCodec; //서브스트림 코덱
    private String subResolution; //서브스트림 해상도
    private String subGop; //서브스트림 GOP
    private String subBitrate; //서브스트림 비트레이트
    private String subFramerate; //서브스트림 프레임레이트


    public String getMainCodec() {
        return mainCodec;
    }

    public void setMainCodec(String mainCodec) {
        this.mainCodec = mainCodec;
    }

    public String getMainResolution() {
        return mainResolution;
    }

    public void setMainResolution(String mainResolution) {
        this.mainResolution = mainResolution;
    }

    public String getMainGop() {
        return mainGop;
    }

    public void setMainGop(String mainGop) {
        this.mainGop = mainGop;
    }

    public String getMainBitrate() {
        return mainBitrate;
    }

    public void setMainBitrate(String mainBitrate) {
        this.mainBitrate = mainBitrate;
    }

    public String getMainFramerate() {
        return mainFramerate;
    }

    public void setMainFramerate(String mainFramerate) {
        this.mainFramerate = mainFramerate;
    }

    public String getSubCodec() {
        return subCodec;
    }

    public void setSubCodec(String subCodec) {
        this.subCodec = subCodec;
    }

    public String getSubResolution() {
        return subResolution;
    }

    public void setSubResolution(String subResolution) {
        this.subResolution = subResolution;
    }

    public String getSubGop() {
        return subGop;
    }

    public void setSubGop(String subGop) {
        this.subGop = subGop;
    }

    public String getSubBitrate() {
        return subBitrate;
    }

    public void setSubBitrate(String subBitrate) {
        this.subBitrate = subBitrate;
    }

    public String getSubFramerate() {
        return subFramerate;
    }

    public void setSubFramerate(String subFramerate) {
        this.subFramerate = subFramerate;
    }
}
