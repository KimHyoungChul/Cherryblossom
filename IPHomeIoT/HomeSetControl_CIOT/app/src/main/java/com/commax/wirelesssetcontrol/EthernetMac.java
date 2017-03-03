package com.commax.wirelesssetcontrol;

import android.content.Context;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class EthernetMac {

	private static final String URL = "http://127.0.0.1:29720";
	private static final String NAMESPACE = "urn:cmm";
	private static final String NAME = "getMAC";
	private Context context;
	public EthernetMac(Context context) {
		this.context=context;

	}

	
	public String getValue() {

		try {
			SoapObject object = new SoapObject(NAMESPACE, NAME);
			object.addProperty("in", 1);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.bodyOut = object;
			
			String value = "";
			Utility utility = new Utility();
			value = utility.getSiteCode();
			
			
			AndroidHttpTransport transport;
			if(value.equals("")){
				String local_value = "";
				
				//���� ���е尡 �����̺��� ��
				//local_value = DsConfig.getDsServerIp(EnergyMonitoringActivity.getInstance().getContentResolver());
				
				
				if(local_value.equals("")){
					local_value = "http://127.0.0.1:29720";
				}else{
					local_value = "http://" + local_value + ":29720";
				}
				
				transport = new AndroidHttpTransport(local_value);
				
			}else{
				transport = new AndroidHttpTransport(URL);
			}
			
			//AndroidHttpTransport transport = new AndroidHttpTransport(URL);
			transport.call("", envelope);
			SoapObject result = (SoapObject) envelope.bodyIn;
			return result.getProperty(1).toString();
		} catch (IOException e) {
			//err: -1
		} catch (XmlPullParserException e) {

		}

		return "";
	}

	
	
}
