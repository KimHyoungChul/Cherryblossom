package com.commax.login.Common;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileEx {

    public String[] readFile(String fileName) throws FileNotFoundException, IOException {
        String[] returnValue = new String[]{};

        File f = new File(fileName);
        if (f.exists() == false) {
            return returnValue;
        }
        if (f.canRead() == false) {
            return returnValue;
        }

        FileInputStream fis = null;
        BufferedReader br = null;

        fis = new FileInputStream(f);
        br = new BufferedReader(new InputStreamReader(fis));
        String temp;
        ArrayList<String> list = new ArrayList<String>();
        while ((temp = br.readLine()) != null) {

            list.add(temp.trim());
        }
        returnValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnValue[i] = list.get(i);
        }

        if (br != null) {
            try {
                br.close();
            } catch (Exception e) {
                Log.e("FileEX", e.toString());
            }
        }
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                Log.e("FileEX", e.toString());
            }
        }

        return returnValue;
    }

    public String[] readFolder(String path) {
        String[] returnValue = new String[]{};

        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            return returnValue;
        }

        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                continue;
            }
            list.add(file.getName());
        }
        returnValue = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            returnValue[i] = list.get(i);
        }
        return returnValue;
    }
}
