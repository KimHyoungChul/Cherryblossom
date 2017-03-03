package com.commax.settings.setting_provider;


public class ValueType {
    private static final String KEY = "mysql";

    public boolean getValue() {
        if (Product.isWallpad()) {
            String ret = new SettingProperties().get(KEY);
            if (ret != null) {
                if ("true".equals(ret)) {
                    return true;
                }
            } else {
                //default
                return true;
            }
            return false;
        } else {
            return false;
        }


    }

    public Boolean setValue(boolean value) {
        return new SettingProperties().set(KEY, value);
    }
}
