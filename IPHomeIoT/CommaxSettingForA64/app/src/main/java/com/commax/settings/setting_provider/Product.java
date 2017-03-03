package com.commax.settings.setting_provider;

import java.io.File;
import java.io.IOException;

public class Product {
    private static final String FOLDER_NAME = "/user/etc";
    private static final String FILE_NAME = "/user/etc/ifcfg-eth0";
    private static final String VERSION = "/user/version/version.i";
    private static final String LOADER = "/user/etc/uversion";

    public static boolean isWallpad() {
        File f = new File(FOLDER_NAME);
        return f.isDirectory();

    }

    public static boolean isWired() {
        File f = new File(FILE_NAME);
        return f.exists();

    }

    public static String getLoaderValue() {
        String str = null;

        try {
            str = new CMFile().read(LOADER);
        } catch (Exception e) {
            str = "1.0"; //파일 부재시 default값
        }

        return str;
    }


    /*
    root# cat # cat /user/version/version.i
    PROD=CAV-70GXAZ
    LANG=KO
    SITE=ZGX
    DATE=20161115105318
    VER=2.6
     */
    public static String getProdCode() {
        try {
            return new CMProperties().get(VERSION, "PROD");
        } catch (IOException e) {

        }
        return null;
    }

    public static String getSiteCode() {
        try {
            return new CMProperties().get(VERSION, "SITE");
        } catch (IOException e) {

        }
        return null;
    }

    public static String getVerCode() {
        try {
            return new CMProperties().get(VERSION, "VER");
        } catch (IOException e) {

        }
        return null;
    }
}
