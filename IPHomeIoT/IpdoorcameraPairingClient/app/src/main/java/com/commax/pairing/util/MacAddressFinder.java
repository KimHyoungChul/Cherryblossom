package com.commax.pairing.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 맥어드레스 가져옴
 * Created by bagjeong-gyu on 2016. 9. 20..
 */
public class MacAddressFinder {

    public static String loadFileAsString(String filePath) throws IOException {
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            data.append(readData);
        }
        reader.close();
        return data.toString();
    }

    /**
     * wifi를 사용할 수 없고 lan을 사용하는 디바이스 경우
     *
     * @return
     */
    public static String getMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address")
                    .toUpperCase().substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

/**
 * 일반적으로 mac address를 가져오는 방법
 */
//    private String getMacAddress() {
//        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = manager.getConnectionInfo();
//       return info.getMacAddress();
//    }

/**
 * 안드로이드 6.0(마시멜로우)이상에서 mac address 가져오는 방법
 */
//    public static String getMacAddress() {
//        try {
//            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface nif : all) {
//                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
//
//                byte[] macBytes = nif.getHardwareAddress();
//                if (macBytes == null) {
//                    return "";
//                }
//
//                StringBuilder res1 = new StringBuilder();
//                for (byte b : macBytes) {
//                    res1.append(String.format("%02X:",b));
//                }
//
//                if (res1.length() > 0) {
//                    res1.deleteCharAt(res1.length() - 1);
//                }
//                return res1.toString();
//            }
//        } catch (Exception ex) {
//        }
//        return "02:00:00:00:00:00";
//    }
}
