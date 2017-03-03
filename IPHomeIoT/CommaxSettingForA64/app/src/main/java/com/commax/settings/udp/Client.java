package com.commax.settings.udp;

import com.koushikdutta.async.AsyncDatagramSocket;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * UDP 클라이언트
 * Created by reweber on 12/20/14.
 */
public class Client {

    private final InetSocketAddress host;
    private AsyncDatagramSocket asyncDatagramSocket;


    public Client(String host, int port) {
        this.host = new InetSocketAddress(host, port);
        setup();
    }


    /**
     * 초기화
     */
    private void setup() {
        try {
            asyncDatagramSocket = AsyncServer.getDefault().connectDatagram(host);
            ((DatagramSocket) asyncDatagramSocket.getSocket()).setBroadcast(true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        asyncDatagramSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                System.out.println("[Client] Received Message " + new String(bb.getAllByteArray()));
            }
        });

        asyncDatagramSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully closed connection");
            }
        });

        asyncDatagramSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully end connection");
            }
        });
    }

    /**
     * 브로드캐스트 전송
     *
     * @param msg
     */
    public void send(String msg) {
        //Log.d("yslee", "-> send:" + msg);
        asyncDatagramSocket.send(host, ByteBuffer.wrap(msg.getBytes()));
    }

    /**
     * 닫기
     */
    public void close() {
        asyncDatagramSocket.close();
    }
}
