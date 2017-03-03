package com.commax.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.util.Log;

//////////////// 사용법
/*
 싱글패턴으로 작성했구요.

 프로퍼티를 설정하는 방법은 setProperty(String key, String value) 메서드로 키 와 값을 저장하시면저장하시면 되구요.
 반드시 최종적으로 storeProperty 를 해주셔야 파일에 저장이 됩니다.
 프로퍼티를 불러오는 방법은 getProperty(Strng key) 메서드로 키만 입력하면 해당 값을 가져옵니다.가져옵니다. 

 참 쉽죠잉~~

 사용 예)
 PropertyUtil pu = PropertyUtil.getInstance();
 pu.setProperty("light_1", "room name1 is");
 pu.setProperty("light_2", "room name2 is");
 pu.setProperty("light_3", "room name3 is");
 pu.storeProperty(";");
 System.out.println("count : " + pu.getPropertyCount());
 */

public class PropertyUtil {

    private static String LOG_TAG = PropertyUtil.class.getSimpleName();

    /**
     * PropertyUtil 객체
     */
    public static PropertyUtil m_propertyUtil;

    /**
     * 프로퍼티 파일명
     */
    private static final String PROPERTIY_FILE = "/data/data/com.commax.security/sensor_name.properties";

    /**
     * file
     */
    private static File m_profile = null;

    /**
     * file input stream
     */
    private static FileInputStream m_fis = null;

    /**
     * file output stream
     */
    private static FileOutputStream m_fos = null;

    /**
     * properties
     */
    private static Properties m_pros = null;

    private PropertyUtil() {

        m_profile = new File(PROPERTIY_FILE); // 프로퍼티 객체 생성
        m_pros = new Properties();

        try {

            // 프로퍼티 파일이 없는 경우 파일을 새로 생성한다.
            if (!m_profile.exists())
                m_profile.createNewFile(); // 프로퍼티 파일 입력 스트림
            // m_profile.delete(); //있어도 지우고 새로 생성하자..
            // m_profile.createNewFile();
            m_fis = new FileInputStream(m_profile);

            // 프로퍼티 파일 출력 스트림
            m_fos = new FileOutputStream(m_profile, true); // 프로퍼티 파일을 메모리로
                                                           // 로드한다.
            m_pros.load(m_fis);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Property File 생성 및 로드 실패 -> " + e.getMessage());
        }
    }

    public synchronized static PropertyUtil getInstance() {
        if (m_propertyUtil == null) {
            m_propertyUtil = new PropertyUtil();
        }
        return m_propertyUtil;
    }

    public String getProperty(String key) {
        return m_pros.getProperty(key);
    }

    public void setProperty(String key, String value) {
        m_pros.setProperty(key, value);
    }

    public int getPropertyCount() {
        return m_pros.values().size();
    }

    public void storeProperty(String comment) {

        try {
            m_pros.store(m_fos, comment);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Property File 저장 실패 -> " + e.getMessage());
        }
    }
}