package com.commax.iphomeiot.calldial;

import android.content.Context;

public class PhoneNumberFormater {
	private static final int MIN_NEIGHBOR_LENGTH = 3;
	private static final int MAX_NEIGHBOR_UNIT_LENGTH = 4;

	public String convertEmergency(String str) {
		String msg = "";

		if(str.length() < MIN_NEIGHBOR_LENGTH) {
			return msg;
		}

		if(str.contains("-") == false) {
			return msg;
		}

		String arr[] = str.split("-");
		if((arr.length == 2) == false) {
			return msg;
		}

		for(int i = 0; i < arr.length; i++) {
			if ((arr[i].length() > 0) == false) {
				return msg;
			}

			if(arr[i].length() > MAX_NEIGHBOR_UNIT_LENGTH) {
				return msg;
			}
		}

		StringBuffer buf = new StringBuffer();
		for(int j = 0; j < (MAX_NEIGHBOR_UNIT_LENGTH - arr[0].length()); j++) {
			buf.append("0");
		}
		buf.append(arr[0]);

		for(int j = 0; j < (MAX_NEIGHBOR_UNIT_LENGTH - arr[1].length()); j++) {
			buf.append("0");
		}
		buf.append(arr[1]);

		return "#72" + buf.toString();
	}

	public String convertNeightbor(String str) {
		String msg = "";

		if(str.length() < MIN_NEIGHBOR_LENGTH) {
			return msg;
		}

		if(str.contains("-") == false) {
			return msg;
		}

		String arr[] = str.split("-");
		if((arr.length == 2) == false) {
			return msg;
		}

		for(int i = 0; i < arr.length; i++) {
			if((arr[i].length() > 0) == false) {
				return msg;
			}

			if(arr[i].length() > MAX_NEIGHBOR_UNIT_LENGTH) {
				return msg;
			}
		}

		StringBuffer buf = new StringBuffer();
		for(int j = 0; j < (MAX_NEIGHBOR_UNIT_LENGTH - arr[0].length()); j++) {
			buf.append("0");
		}

		buf.append(arr[0]);
		buf.append("*");
		for(int j = 0; j < (MAX_NEIGHBOR_UNIT_LENGTH - arr[1].length()); j++) {
			buf.append("0");
		}
		buf.append(arr[1]);

		return buf.toString();

	}

	public String convertExt(String str) {
		SlaveID slaveID = new SlaveID();

		if((str.length() > 0) == false) {
			return "";
		}

		if(str.length() < 3) {			
			String entryValue=slaveID.matchedEntryValue(str);
			if(entryValue!=null) {
				return  "#" +entryValue;
			}
		}

		if(str.length() == 3) {
			return  "#" + str;
		}

		return "";
	}

	public String getDoorNum() {
		return "#201";
	}

	public String getGuardNum(Context context) {
		return maker(new PhoneNumber(context).getGuard());
	}

	public String getMngOfficeNum(Context context) {
		return maker(new PhoneNumber(context).getOffice());
	}

	private String maker(String val) {
		if(val == null) {
			return "";
		}
		val = val.trim();
		if("".equals(val)) {
			return "";
		}
		if("-1".equals(val)) {
			return "";
		}

		return "#" + val;
	}
}
