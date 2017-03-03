package com.commax.settings.ringtone;

/**
 * 코맥스 내장 벨소리 모델
 * Created by bagjeong-gyu on 2016. 10. 5..
 */

public class CommaxRingtone {
    private String name; //벨소리 명
    private String file; //벨소리 파일명


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
