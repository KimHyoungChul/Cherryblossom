package com.commax.iphomeiot.calldial;

import android.text.Editable;

public class PhoneNumberFormatterKorean {

	public static void format(Editable text) {
		int rootIndex = 1;
		int length = text.length();
		if (length > 3 && text.subSequence(0, 3).toString().equals("+82")) {
			rootIndex = 3;
		} else if (length < 1) {
			return;
		}

		// Strip the dashes first, as we're going to add them back
		int i = 0;
		while (i < text.length()) {
			if (text.charAt(i) == '-') {
				text.delete(i, i + 1);
			} else {
				i++;
			}
		}

		length = text.length();

		dddFormat(text, length);

		if ((length > 3) && (rootIndex == 3)) {
			text.insert(rootIndex, "-");
		}
	}

	private static void dddFormat(Editable text, int length) {
		if ((length >= 3) && (length < 6)) {
			if (text.charAt(0) == '0') {
				if (text.charAt(1) == '2') {
					text.insert(2, "-");
					return;
				}
			}
			if (length >= 4) {
				text.insert(3, "-");
			}
		} else if ((length >= 6) && (length < 10)) {
			if (text.charAt(0) == '0') {
				if (text.charAt(1) == '2') {
					text.insert(2, "-");
					text.insert(6, "-");
					return;
				}
			}

			if (length < 8) {
				text.insert(3, "-");
			} else if (length == 8) {
				text.insert(4, "-");
			} else if (length > 8) {
				text.insert(3, "-");
				text.insert(7, "-");
			}
		} else if (length >= 10) {
			if (text.charAt(0) == '0') {
				if (text.charAt(1) == '2') {
					text.insert(2, "-");
					text.insert(7, "-");
					return;
				}
			}
			text.insert(3, "-");
			if (length >= 11) {
				text.insert(8, "-");
			} else {
				text.insert(7, "-");
			}
		}
	}
}
