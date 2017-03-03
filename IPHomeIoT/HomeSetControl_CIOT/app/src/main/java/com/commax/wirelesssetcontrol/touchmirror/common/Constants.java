package com.commax.wirelesssetcontrol.touchmirror.common;

/**
 * Created by Shin Sung on 2017-02-07.
 */
public class Constants {
    public static final int LONG_TOUCH_TIME = 500; //ms
    public static final int FADE_OUT_TIME = 500; //ms

    //TouchMirrorAct 관련 설정
    public static final int AREA_TOUCH_COL = 8;
    public static final int AREA_TOUCH_ROW = 4;

    public static final int AREA_TOUCH_BOTTOM_COL = 7;
    public static final int AREA_TOUCH_BOTTOM_ROW = 1;

    public static final int AREA_APPS_COL = 6;
    public static final int AREA_APPS_ROW = 3;

    public static final int AREA_WIDGET_COL = 2;
    public static final int AREA_WIDGET_ROW = 2;

    /* 넥서스 패드 테스트용 값
    public static final int AREA_TOUCH_COL = 13;
    public static final int AREA_TOUCH_ROW = 6;

    public static final int AREA_TOUCH_BOTTOM_COL = 9;
    public static final int AREA_TOUCH_BOTTOM_ROW = 1;

    public static final int AREA_APPS_COL = 12;
    public static final int AREA_APPS_ROW = 5;

    public static final int AREA_WIDGET_COL = 26;
    public static final int AREA_WIDGET_ROW = 12;*/


    //인텐트 키값 설정
    public static final String INTENT_KEY_ROOM_IDX = "room_idx"; //방 배경 번호
    public static final String INTENT_KEY_MODE = "mode";  //화면 너비, 높이 정보 공유
    public static final String INTENT_KEY_SCREEN_DATA_TOP = "screen_data_top";  //상단 화면 너비, 높이 정보 공유
    public static final String INTENT_KEY_SCREEN_DATA_BOTTOM = "screen_data_bottom";  //하단 화면 너비, 높이 정보 공유
    public static final String INTENT_KEY_UPDATE_TARGET = "update_target";  //추가 또는 업데이트가 일어난 영역표시
    public static final String INTENT_KEY_ADD_DATA = "add_icon_data";  //추가된 아이콘에 대한 정보 공유
    public static final String INTENT_KEY_UPDATE_DATA = "update_icon_data";  //업데이트 된 아이콘에 대한 정보 공유
    public static final String INTENT_KEY_ICON_DATA_TOP = "icon_data_top"; //기존 아이콘에 대한 정보 공유 상단
    public static final String INTENT_KEY_ICON_DATA_BTM = "icon_data_btm"; //기존 아이콘에 대한 정보 공유 하단

    //업데이트 영역
    public static final String TARGET_TOP = "top";
    public static final String TARGET_BOTTOM = "bottom";

    //메인 기본 아이템
    public static final String DEFAULT_ITEM_APPS = "Apps";
}
