package com.commax.onvif_device_manager.device;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.util.List;

/**
 * Created by OWNER on 2016-10-28.
 */

public class SetNetworkInterfaceBodyManager {
    private static String bodyString;

    public static void createBodyString(String interfaceToken, String newIp) {

        int prefixlength = 32;
        try {
            java.net.NetworkInterface networkInterface = java.net.NetworkInterface.getByName(interfaceToken);
            List<InterfaceAddress> address = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : address) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    prefixlength = interfaceAddress.getNetworkPrefixLength();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<SOAP-ENV:Body xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        sb.append("<SetNetworkInterfaces xmlns=\"http://www.onvif.org/ver10/device/wsdl\">");
        sb.append("<InterfaceToken>");
        sb.append(interfaceToken);
        sb.append("</InterfaceToken>");
        sb.append("<NetworkInterface>");
        sb.append("<Enabled xmlns=\"http://www.onvif.org/ver10/schema\">true</Enabled>");
        sb.append("<IPv4 xmlns=\"http://www.onvif.org/ver10/schema\">");
        sb.append("<Enabled>true</Enabled>");
        sb.append("<Manual>");
        sb.append("<Address>");
        sb.append(newIp);
        sb.append("</Address>");
        sb.append("<PrefixLength>");
        sb.append(prefixlength);
        sb.append("</PrefixLength>");
        sb.append("</Manual>");
        sb.append("<DHCP>false</DHCP>");
        sb.append("</IPv4>");
        sb.append("</NetworkInterface>");
        sb.append("</SetNetworkInterfaces>");
        sb.append("</SOAP-ENV:Body>");

        bodyString = sb.toString();
    }

    public static String getBodyString() {
        return bodyString;
    }
}
