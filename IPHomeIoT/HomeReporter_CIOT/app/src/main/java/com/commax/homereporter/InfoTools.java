package com.commax.homereporter;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

public class InfoTools {

    public InfoTools() {
    }

    public ArrayList<String> getUnSelectedInfo(){

        /* Getting unselected information items */
        Properties mProperty = new Properties();
        FileInputStream fis=null;
        ArrayList<String> result = new ArrayList<>();

        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/InfoFile/" + NameSpace.INFO_PROPERTIES);
            String proValue = null;

            if (file.exists()) {

                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);
                    for (int i = 1; i <= 17; i++) {
                        proValue = mProperty.getProperty(String.valueOf(i));
                        if (proValue != null) {
                            if (("not").equals(proValue) || ("false").equals(proValue)) {
                                result.add(String.valueOf(i));
                                Log.d("InfoTools", "getUnSelectedInfo : not : " + i);
                            }
                        }
                    }
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("InfoTools", "getUnSelectedInfo File not exists");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<String> getSupportInfo(){

        /* Getting unselected information items */
        Properties mProperty = new Properties();
        FileInputStream fis=null;
        ArrayList<String> result = new ArrayList<>();
        boolean contains = false;

        try {
            File file = new File(NameSpace.SUPPORT_PROPERTIES);
            String proValue = null;

            if (file.exists()) {

                mProperty = new Properties();
                try {
                    fis = new FileInputStream(file);
                    mProperty.load(fis);
                    for (int i = 1; i <= 17; i++) {
                        proValue = mProperty.getProperty(String.valueOf(i));
                        if (proValue != null) {
                            if (("use").equals(proValue)) {
                                result.add(String.valueOf(i));
                                Log.d("InfoTools", "getSupportInfo : use : " + i);
                                contains = true;
                            }
                        }
                    }
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("InfoTools", "getSupportInfo File not exists");
            }

            if (!contains){
                for (int i = 1; i <= 12; i++) {
                    result.add(String.valueOf(i));
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
