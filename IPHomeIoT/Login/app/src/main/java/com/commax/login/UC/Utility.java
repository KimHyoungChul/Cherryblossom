package com.commax.login.UC;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.widget.Toast;

import com.commax.login.Common.FileEx;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {


    public static final String SYMBOL_DASH = "-";
    public static final String SYMBOL_DOLLAR = "$";
    public static final String SYMBOL_SHARP = "#";
    public static final String SYMBOL_AMPERSAND = "&";
    public static final String SYMBOL_EQUAL = "=";

    public static final String VISIBLE = "1";
    public static final String INVISIBLE = "0";
    public static final String CHECKED = "1";
    public static final String UNCHECKED = "0";
    public static final String ENABLE = "1";
    public static final String DISABLE = "0";
    public static final String FAULT = "";
    public static final int KEY_DOWN_ARROW = 4;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    public static final int VALID = 1;
    public static final int INVALID = 0;
    public static final int ERROR = -1;

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, Integer resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, Integer resId, Integer duration) {
        Toast.makeText(context, resId, duration).show();
    }

    public static String getStringResource(Context context, Integer resId) {
        return context.getString(resId);
    }

    public int countSubstring(String src, String sub) {
        int count = 0;
        int index = 0;

        while ((index = src.indexOf(sub, index)) > 0) {
            count++;
            index += sub.length();
        }

        return count;
    }

    public String extract_numeral(String str) {

        String numeral = "";
        if (str == null) {
            numeral = null;
        } else {
            String patternStr = "\\d"; // 占쏙옙占쌘몌옙 占쏙옙占쏙옙占쏙옙占쏙옙 占쏙옙占쏙옙
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(str);

            while (matcher.find()) {
                numeral += matcher.group(0); // 占쏙옙占쏙옙占쏙옙 占쏙옙占싹곤옙 占쏙옙칭占실몌옙 numeral 占쏙옙占쏙옙占쏙옙 占쌍는댐옙.
                // 占쏙옙占썩서占쏙옙 占쏙옙占쏙옙!!
            }
        }

        return numeral;
    }

    public Boolean isNumber(String number) {
//		try {
//			Integer.parseInt(newpw);
//		} catch (NumberFormatException nfe) {
//			showToast(R.string.password_new_wrong);
//			return;
//		}

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if (Character.isDigit(c) == false) {
                return false;
            }
        }
        return true;


    }

    public Boolean isValidIP(String ip) {
        boolean isIPv4;
        try {
            final InetAddress inet = InetAddress.getByName(ip);
            isIPv4 = inet.getHostAddress().equals(ip)
                    && inet instanceof Inet4Address;
        } catch (final UnknownHostException e) {
            isIPv4 = false;
        }
        return isIPv4;
    }


    public Boolean isValidPhoneNumber(String arg0) {
        if (arg0.length() != 10) {
            return false;
        }
        if (arg0.startsWith("7") == false) {
            return false;
        }

        char c;
        for (int j = 0; j < arg0.length(); j++) {
            c = arg0.charAt(j);
            if (c < 48 || c > 59) {
                return false;
            }
        }
        return true;

    }


    public String getEucKr(String str) throws UnsupportedEncodingException {
        int strLength = str.length();
        byte[] strOrg = new byte[strLength];
        for (int j = 0; j < strLength; j++) {
            str.getBytes(j, j + 1, strOrg, j);
        }

        return new String(strOrg, "euc-kr");

    }

    public String setEucKr(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes("EUC-KR"), "8859_1");

    }

    // public String toUnicode(String str)
    // throws UnsupportedEncodingException
    // {
    // return new String(str.getBytes("ISO-8859-1"));
    // }
    //
    // public String toLatin(String str)
    // throws UnsupportedEncodingException
    // {
    // return new String(str.getBytes(), "ISO-8859-1");
    // }


    public int getVersionCode(Context context, String pkgName) {

        int versionCode = -1;
        try {
            versionCode = context.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
        } catch (NameNotFoundException e) {

        }
        return versionCode;
    }


    public String getPkgName(String cls) {
        try {
            String[] arr = cls.split("\\.");
            String str = arr[arr.length - 1];
            return cls.substring(0, cls.length() - str.length() - 1);
        } catch (Exception e) {

            return "";
        }

    }

    public String getSiteCode() {
        FileEx file = new FileEx();
        String fileName = "/user/version/version.i";
        String[] contents = null;
        try {
            contents = file.readFile(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String code = "";
        if (contents.length > 0 == false) {
            return code;
        }

        for (int i = 0; i < contents.length; i++) {
            String line = contents[i];
            if (line.startsWith("SITE=")) {
                String[] arr = line.split("=");
                code = arr[1];
            }
        }
        return code;
    }

    public static boolean isFault(String value) {
        if (value == null) {
            return true;
        }
        value = value.trim();
        if ("".equals(value)) {
            return true;
        }
        if ("-1".equals(value)) {
            return true;
        }
        return false;
    }

    public static boolean isFault(String[] values) {
        if (values == null) {
            return true;
        }
        return isFault(values[0]);
    }

    public static boolean isOK(String value) {
        return !isFault(value);
    }

    public static boolean isOK(String[] values) {
        return !isFault(values);
    }


}
