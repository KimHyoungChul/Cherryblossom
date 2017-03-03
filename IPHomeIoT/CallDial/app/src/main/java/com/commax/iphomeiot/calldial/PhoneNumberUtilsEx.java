package com.commax.iphomeiot.calldial;

import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.SpannableStringBuilder;

import java.util.Locale;

public class PhoneNumberUtilsEx extends PhoneNumberUtils {
	public static final int FORMAT_KOREA = 82;

	private static final String[] NANP_COUNTRIES = new String[] { "US", // United States
			"CA", // Canada
			"AS", // American Samoa
			"AI", // Anguilla
			"AG", // Antigua and Barbuda
			"BS", // Bahamas
			"BB", // Barbados
			"BM", // Bermuda
			"VG", // British Virgin Islands
			"KY", // Cayman Islands
			"DM", // Dominica
			"DO", // Dominican Republic
			"GD", // Grenada
			"GU", // Guam
			"JM", // Jamaica
			"PR", // Puerto Rico
			"MS", // Montserrat
			"MP", // Northern Mariana Islands
			"KN", // Saint Kitts and Nevis
			"LC", // Saint Lucia
			"VC", // Saint Vincent and the Grenadines
			"TT", // Trinidad and Tobago
			"TC", // Turks and Caicos Islands
			"VI", // U.S. Virgin Islands
	};

	public static int getFormatTypeForLocale(Locale locale) {
		String country = locale.getCountry();

		return getFormatTypeFromCountryCode(country);
	}

	public static int getFormatTypeFromCountryCode(String country) {
		// Check for the NANP countries
		int length = NANP_COUNTRIES.length;
		for (int i = 0; i < length; i++) {
			if (NANP_COUNTRIES[i].compareToIgnoreCase(country) == 0) {
				return FORMAT_NANP;
			}
		}
		if ("jp".compareToIgnoreCase(country) == 0) {
			return FORMAT_JAPAN;
		} else if ("kr".compareToIgnoreCase(country) == 0) {
			return FORMAT_KOREA;
		}
		return FORMAT_UNKNOWN;
	}

	public static String formatNumber(String source) {
		SpannableStringBuilder text = new SpannableStringBuilder(source);
		formatNumber(text, getFormatTypeForLocale(Locale.getDefault()));
		return text.toString();
	}

	@SuppressWarnings("deprecation")
	public static void formatNumber(Editable text, int defaultFormattingType) {
		int formatType = defaultFormattingType;

		if ((text.length() > 2) && (text.charAt(0) == '+')) {
			if (text.charAt(1) == '1') {
				formatType = FORMAT_NANP;
			} else if ((text.length() >= 3) && (text.charAt(1) == '8')
					&& (text.charAt(2) == '1')) {
				formatType = FORMAT_JAPAN;
			} else if ((text.length() >= 3) && (text.charAt(1) == '8')
					&& (text.charAt(2) == '2')) {
				formatType = FORMAT_KOREA;
			} else {
				return;
			}
		}

		switch (formatType) {
		case FORMAT_NANP:
			formatNanpNumber(text);
			return;
		case FORMAT_JAPAN:
			formatJapaneseNumber(text);
			return;
		case FORMAT_KOREA:
			formatKoreanNumber(text);
			return;
		default:
			break;
		}
	}

	public static void formatKoreanNumber(Editable text) {
		PhoneNumberFormatterKorean.format(text);
	}
}
