package com.commax.settings.setting_provider;

import java.io.IOException;

public class SettingProperties {

    private static final String PATH = "/user/app/bin/settings.i";

    public boolean set(String key, String value) {
        try {
            new CMProperties().set(PATH, key, value);
            return true;
        } catch (IOException e) {

        }
        return false;
    }

    public boolean set(String key, boolean value) {
        try {
            new CMProperties().set(PATH, key, String.valueOf(value));
            return true;
        } catch (IOException e) {

        }
        return false;
    }

    public String get(String key) {
        try {
            return new CMProperties().get(PATH, key);

        } catch (IOException e) {

        }
        return null;
    }

    public String get(String key, String defValue) {
        String ret = get(key);
        if (ret == null) {
            return defValue;
        }
        return ret;
    }

}
