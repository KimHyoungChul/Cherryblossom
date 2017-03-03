package com.commax.ipdoorcamerasetting.udp;

/**
 * UDP 브로드캐스트 리스너
 * Created by bagjeong-gyu on 2016. 9. 9..
 */
public interface UDPReceiveDataListener {
    public void onReceive(String data);
}
