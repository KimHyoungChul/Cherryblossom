package com.commax.pairing.util;

import android.content.Context;

import com.commax.pairing.util.FileEx;
import com.commax.pairing.util.PropertiesEx;

import java.util.ArrayList;

/**
 * 설정값 관리
 */
public class SettingValues {
	private static final int DEVICE_PROVIDER_INDEX = 5;
	private static final int HOME_INDEX = 6;
	private static final int GUARD_PROVIDER_INDEX = 7;
	private static final int OFFICE_INDEX = 8;
	private static final int PROVIDER_INDEX_DONG_HO = 9;
	
	private static final String WIRED_PATH = "/user/etc/ifcfg-eth0";
	private static final String IP = "IPADDR";
	private static final String GATEWAY = "GATEWAY";
	private static final String NETMASK = "NETMASK";
	private static final String DNS = "NAMESERVER";
	private	Context context;
	public SettingValues(Context context){
		this.context=context;
	}


	public String getSubnetMask() {
		return name1(new PropertiesEx(WIRED_PATH).get(NETMASK));
	}

	public String getDefaultGateway() {
		return name1(new PropertiesEx(WIRED_PATH).get(GATEWAY));
	}

	public String getDnsServer() {
		return name1(new PropertiesEx(WIRED_PATH).get(DNS));
	}

	private String name1(String value) {
		if (value!=null) {
			if (value.contains("\"")) {
				value=value.replace("\"", "");
			}
		}
		return value;
	}


	public Boolean setEthernet(String ip, String gateway, String mask, String dns) {

		FileEx io = new FileEx(WIRED_PATH);
		ArrayList<String> file = io.read();
		if (file.size() > 0 == false) {
			// Utility.showToast(context, R.string.saving_failure);
			return false;
		}

		

		for (int i = 0; i < file.size(); i++) {
			String line = file.get(i);
			if (line.startsWith(IP)) {
				
				file.set(i, new StringBuilder().append(IP).append("=").append("\"").append(ip).append("\"").toString());

			} else if (line.startsWith(DNS)) {
				
				file.set(i, new StringBuilder().append(DNS).append("=").append("\"").append(dns).append("\"").toString());

			} else if (line.startsWith(GATEWAY)) {
				
				file.set(i, new StringBuilder().append(GATEWAY).append("=").append("\"").append(gateway).append("\"").toString());

			} else if (line.startsWith(NETMASK)) {
				
				file.set(i, new StringBuilder().append(NETMASK).append("=").append("\"").append(mask).append("\"").toString());

			}

		}

		return io.write(file);
		
		
	}

}
