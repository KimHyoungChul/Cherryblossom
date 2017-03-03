package com.commax.pairing.util;

import java.util.regex.Pattern;

/**
 * IP 주소 유효성 체크
 * Created by bagjeong-gyu on 2016. 9. 22..
 */

public class IPAdressChecker {

    static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static boolean isValidIPV4(final String s)
    {
        return IPV4_PATTERN.matcher(s).matches();
    }
}
