package com.commax.iphomeiot.calldial;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.Locale;

public class PhoneNumberFormattingTextWatcherEx extends PhoneNumberFormattingTextWatcher implements TextWatcher {
	// frameworks\base\telephony\java\android\telephony
	private static int sFormatType;
	private static Locale sCachedLocale;
	private boolean mFormatting;
	private boolean mDeletingHyphen;
	private int mHyphenStart;
	private boolean mDeletingBackward;

	public PhoneNumberFormattingTextWatcherEx() {
		if((sCachedLocale == null) || (sCachedLocale != Locale.getDefault())) {
			sCachedLocale = Locale.getDefault();
			sFormatType = PhoneNumberUtilsEx.getFormatTypeForLocale(sCachedLocale);
		}
	}

	public synchronized void afterTextChanged(Editable text) {
		// Make sure to ignore calls to afterTextChanged caused by the work done
		// below
		if (!mFormatting) {
			mFormatting = true;

			// If deleting the hyphen, also delete the char before or after that
			if (mDeletingHyphen && mHyphenStart > 0) {
				if (mDeletingBackward) {
					if (mHyphenStart - 1 < text.length()) {
						text.delete(mHyphenStart - 1, mHyphenStart);
					}
				} else if (mHyphenStart < text.length()) {
					text.delete(mHyphenStart, mHyphenStart + 1);
				}
			}

			PhoneNumberUtilsEx.formatNumber(text, sFormatType);

			mFormatting = false;
		}
	}
}
