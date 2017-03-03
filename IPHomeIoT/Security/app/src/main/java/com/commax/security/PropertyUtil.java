package com.commax.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.util.Log;

//////////////// ����
/*
 �̱��������� �ۼ��߱���.

 ������Ƽ�� �����ϴ� ����� setProperty(String key, String value) �޼���� Ű �� ���� �����Ͻø������Ͻø� �Ǳ���.
 �ݵ�� ���������� storeProperty �� ���ּž� ���Ͽ� ������ �˴ϴ�.
 ������Ƽ�� �ҷ����� ����� getProperty(Strng key) �޼���� Ű�� �Է��ϸ� �ش� ���� �����ɴϴ�.�����ɴϴ�. 

 �� ������~~

 ��� ��)
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
     * PropertyUtil ��ü
     */
    public static PropertyUtil m_propertyUtil;

    /**
     * ������Ƽ ���ϸ�
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

        m_profile = new File(PROPERTIY_FILE); // ������Ƽ ��ü ����
        m_pros = new Properties();

        try {

            // ������Ƽ ������ ���� ��� ������ ���� �����Ѵ�.
            if (!m_profile.exists())
                m_profile.createNewFile(); // ������Ƽ ���� �Է� ��Ʈ��
            // m_profile.delete(); //�־ ����� ���� ��������..
            // m_profile.createNewFile();
            m_fis = new FileInputStream(m_profile);

            // ������Ƽ ���� ��� ��Ʈ��
            m_fos = new FileOutputStream(m_profile, true); // ������Ƽ ������ �޸𸮷�
                                                           // �ε��Ѵ�.
            m_pros.load(m_fis);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Property File ���� �� �ε� ���� -> " + e.getMessage());
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
            Log.e(LOG_TAG, "Property File ���� ���� -> " + e.getMessage());
        }
    }
}