package com.commax.iphomiot.doorcall.service;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.commax.cmxua.NdkWrap;

public class AudioInThread extends Thread {
    private boolean stopped = false;
    private NdkWrap sipNdkWrap_ = null;

    public AudioInThread(NdkWrap sipNdkWrap) {
        super();
        sipNdkWrap_ = sipNdkWrap;
    }

    @Override
    public void run() {
        int frame_size = 160;
        int frame_rate = 75;
        short[] lin = new short[frame_size*(frame_rate+1)];
        stopped    = false;
        AudioRecord recorder = null;

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

        int min = AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        int readByte = 0;

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                min);

        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            recorder = null;
            return;
        }

        recorder.startRecording();

        while(!stopped) {
            int wantedRead = frame_size;
            int ret = 0;
            readByte = 0 ;

            while( readByte < wantedRead && !stopped )
            {
                ret = recorder.read(lin, readByte, wantedRead-readByte );
                if (ret == AudioRecord.ERROR_INVALID_OPERATION || ret == AudioRecord.ERROR_BAD_VALUE)
                    break;
                readByte += ret;
            }
            if( readByte != wantedRead )
                break;
            NdkWrap.nativeWrite(lin, readByte);
        }

        recorder.stop();
        recorder.release();
    }

    void close() {
        stopped = true;
    }
}
