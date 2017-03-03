package com.commax.settings.setting_provider;

import android.util.Pair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

public class CMProperties {


    public void set(final String filename, String name, final String value) throws IOException {


        // step 1
        FileInputStream inputStream = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(inputStream);
        inputStream.close();

        // step 2
        FileOutputStream outputStream = new FileOutputStream(filename);
        prop.setProperty(name, value);
        prop.store(outputStream, null);
        outputStream.getFD().sync();//for A20
        outputStream.close();
    }

    public void set(final String filename, ArrayList<Pair<String, String>> values) throws IOException {

        // step 1
        FileInputStream inputStream = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(inputStream);
        inputStream.close();

        // step 2
        FileOutputStream outputStream = new FileOutputStream(filename);
        for (Pair<String, String> pair : values) {
            prop.setProperty(pair.first, pair.second);
        }

        prop.store(outputStream, null);
        outputStream.getFD().sync();
        outputStream.close();
    }

    public String get(String filename, final String name) throws IOException {

        FileInputStream inputStream = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(inputStream);
        String ret = prop.getProperty(name);

        inputStream.close();
        return ret;

    }

    public ArrayList<String> get(String filename, final ArrayList<String> names) throws IOException {
        ArrayList<String> ret = new ArrayList<String>();
        FileInputStream inputStream = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(inputStream);
        for (String name : names) {
            String value = prop.getProperty(name);

            ret.add(value);
        }
        inputStream.close();
        return ret;

    }

    public ArrayList<Pair<String, String>> keySet(String filename) throws IOException {
        ArrayList<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
        FileInputStream inputStream = new FileInputStream(filename);
        Properties prop = new Properties();
        prop.load(inputStream);
        Set<Object> objects = prop.keySet();
        for (Object name : objects) {
            ret.add(new Pair<String, String>((String) name, prop.getProperty((String) name)));
        }
        inputStream.close();
        return ret;

    }


}
