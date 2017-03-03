package com.commax.settings.setting_provider;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class PropertiesEx {

    private String path;

    public PropertiesEx(final String path) {
        this.path = path;

    }

    private Properties getProperties() {
        Properties prop = new Properties();
        try {
            FileInputStream in = new FileInputStream(path);
            prop.load(in);
            in.close();
        } catch (FileNotFoundException e2) {
            Log.d("tag", "FileNotFoundException");
        } catch (IOException e) {
            Log.d("tag", "IOException");
        }
        return prop;
    }

    public boolean set(final String name, final String value) {
        boolean ret = false;

        Properties prop = getProperties();

        try {
            FileOutputStream out = new FileOutputStream(path);
            prop.setProperty(name, value);
            prop.store(out, null);

            // FileOutputStream.getFD().sync();
            out.getFD().sync();

            out.close();
            ret = true;
        } catch (FileNotFoundException e1) {
            Log.d("tag", "FileNotFoundException");
        } catch (IOException e) {
            Log.d("tag", "IOException");
        }

        return ret;


    }


    public String get(final String key) {
        String ret = getProperties().getProperty(key);
        if (ret != null) {
            if (ret.contains("\"")) {
                ret = ret.replace("\"", "");
            }
        }
        return ret;

    }

    public Set<Object> getAll() {
        return getProperties().keySet();

    }

}
