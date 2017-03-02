package com.commax.control;

public class SubDevice_new {

    String sort;            //Commax defined sort name
    String subUuid;         //36Byte
    String type;            //read, readWrite
    String value;           //control value(ex:on/off,detected/undected, auto/manual, cool/warm, int/float
    String precision;       //precision      -> 추가구현필요
    String scale;           //scale unit     -> 추가구현필요
    String option1;         //option1        -> 추가구현필요
    String option2;         //option2        -> 추가구현필요
    String batteryLevel;   //batteryLevel
    String batteryEvent;    //batteryEvent : low
}
