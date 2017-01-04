package com.commax.login.Common;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by OWNER on 2016-07-27.
 */
public class GetMacaddress {
    private Context context = null;
    String TAG = GetMacaddress.class.getSimpleName();

    public GetMacaddress(Context mcontext) {
        super();
        this.context = mcontext;
    }

    /*
   * Load file content to String
   */
    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    /*
     * Get the STB MacAddress
     */
    public String getMacAddress() {
        WifiManager wm = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            Log.d(TAG, "wifi mac address");
            String wiftMac = wm.getConnectionInfo().getMacAddress();
            return wiftMac;
        } else {
            Log.d(TAG, "non wifi mac address");
            try {
                return loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
