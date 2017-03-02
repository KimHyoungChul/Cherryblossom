package com.commax.headerlist;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by OWNER on 2016-06-27.
 */
public class IPisValid {
    //IP address valuable check
    public static boolean isValid(EditText editText) {
        //space 제거
        String first = editText.getText().toString();
        first.replace(" ","");
        if (!isValidIPAddressFormat(first)) {
            Animation shake = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.shake);
            editText.startAnimation(shake);
            return false;
        }
        return true;
    }

    private static boolean isValidIPAddressFormat(final String value) {
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

}
