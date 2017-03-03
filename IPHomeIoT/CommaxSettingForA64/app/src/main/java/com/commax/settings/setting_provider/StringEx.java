package com.commax.settings.setting_provider;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;


public class StringEx {
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

    public String decodeHtml(String value) {
        return value.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'");
    }

    public String encodeHtml(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    public String convertToPosition(String arg0) {

        String pattern = ".########";
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(Double.parseDouble(arg0));
    }

    public static Date dateConverter(int year, int month, int day) {
        // passed
        // Date min = new Date();
        // min.setYear(year - 1900);
        // min.setMonth(month - 1);
        // min.setDate(date);
        // return min;

        // http://www.java2s.com/Tutorial/Java/0040__Data-Type/CreateajavautilDateObjectfromaYearMonthDayFormat.htm
        String date = year + "/" + month + "/" + day;
        Date utilDate = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            utilDate = formatter.parse(date);
        } catch (java.text.ParseException e) {

        }
        return utilDate;
    }

    public static String dateFormat(Context context, Date date) {

        return android.text.format.DateFormat.getDateFormat(context)
                .format(date);

    }

    public static String dateFormat(Context context, int year, int month,
                                    int day) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, day);

        return dateFormat(context, cal.getTime());
    }

    // private String name1(int year, int month, int date) {
    // //passed
    // Date min = new Date();
    // min.setYear(year);
    // min.setMonth(month);
    // min.setDate(date);
    // Locale locale = getResources().getConfiguration().locale;
    // Calendar cal = (Calendar) Calendar.getInstance().clone();
    // cal.setTime(min);
    // String str = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,
    // locale);
    //
    // DateFormat df = android.text.format.DateFormat.getLongDateFormat(this);
    // return (df.format(min) + " " + str);
    //
    // }

    public static String trim(final String value) {
        String result;
        if (isOK(value)) {
            result = value.trim();
        } else {
            result = value;

        }
        return result;
    }

    public static boolean isFault(final String value) {
        boolean result;
        if (value == null) {
            result = true;
        } else {
            if ("".equals(value.trim())) {
                result = true;
            } else if ("-1".equals(value.trim())) {
                result = true;
            } else {
                result = false;
            }
        }

        return result;
    }

    public static boolean isFault(final String[] values) {
        boolean result;
        if (values == null) {
            result = true;
        } else {
            result = isFault(values[0]);
        }
        return result;
    }

    public static boolean isOK(final String value) {
        return !isFault(value);
    }

    public static boolean isOK(final String[] values) {
        return !isFault(values);
    }

    public static boolean isDigit(final String string) {
        boolean result;
        try {
            Double.parseDouble(string);
            result = true;
        } catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }

    public static boolean isNumeric(final String string) {

        return isDigit(string);
    }

    public static boolean isValidIPAddressFormat(final String value) {
        boolean result;
        if (value == null) {
            result = false;
        } else {
            // http://www.java2s.com/Tutorial/Java/0130__Regular-Expressions/RegexforIPv4Address.htm
            final Pattern IPV4_PATTERN = Pattern
                    .compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
            result = IPV4_PATTERN.matcher(value).matches();
        }
        return result;
    }

    public static boolean isValidIP(String value) {
        return isValidIPAddressFormat(value);
    }

    public static Boolean isValidPhoneNumber(final String arg0) {
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

    public boolean isString(String str) {
        if (str == null) {
            return false;
        }
        if ("".equals(str)) {
            return false;
        }
        return true;
    }

    public boolean isInvalidString(String str) {
        return !isString(str);
    }

}
