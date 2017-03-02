package com.commax.login.Common;

import android.os.Environment;

import java.io.IOException;

/**
 * Created by OWNER on 2017-01-11.
 */
public class Network_state {

    public Network_state()
    {
    }

    public String getNetworkState(){
        FileEx file = new FileEx();
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = dirPath + "/networkState.i";
        String[] contents = null;
        try {
            contents = file.readFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String network_state = "";
        if (contents.length <= 0) {
            return network_state;
        }
        for (int i = 0; i < contents.length; i++) {
            String line = contents[i];
            if (line.startsWith("networkState=")) {
                String[] arr = line.split("=");
                try {
                    network_state = arr[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return network_state;
    }
}
