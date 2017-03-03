package com.commax.iphomiot.doorcall.service;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.commax.cmxua.NdkWrap;

public class AudioOutThread extends Thread {
    private boolean stopped_ = false;
    private NdkWrap sipNdkWrap_ = null;

    public AudioOutThread(NdkWrap sipNdkWrap) {
        super();
        sipNdkWrap_ = sipNdkWrap;
    }

    @Override
    public void run() {
        int frame_size = 160;
        int frame_rate = 75;
        short[] lin = new short[frame_size*(frame_rate+1)];
        stopped_ = false;
        AudioTrack track = null;

        int min = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        int readByte = 0;

        track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                160,
                AudioTrack.MODE_STREAM );

        if (track.getState() != AudioTrack.STATE_INITIALIZED) {
            track = null;
            return;
        }

        track.play();

        int writeByte;
        int ret;
        while (!stopped_) {
            readByte = sipNdkWrap_.nativeRead(lin);

            writeByte = 0;
            ret = 0;
            if (readByte > 0)
            {
                while (writeByte < readByte && !stopped_)
                {
                    ret = track.write(lin, writeByte, readByte-writeByte);
                    if (ret == AudioTrack.ERROR_INVALID_OPERATION || ret == AudioTrack.ERROR_BAD_VALUE)
                        break;
                    writeByte += ret;
                }
            }
            else
            {
                try {
                    sleep(10);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        track.stop();
        track.release();
    }

    void close() {
        stopped_ = true;
    }
}
