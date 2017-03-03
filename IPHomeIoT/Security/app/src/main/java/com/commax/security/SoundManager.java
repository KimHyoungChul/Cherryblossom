package com.commax.security;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//singleton pattern 
public class SoundManager {
    
	public static SoundManager _instance;
    private static MediaPlayer m_MediaPlayer = null;
    private static AudioManager m_AudioManager = null;
    
    public SoundManager () {
        /* Media Player Object */
        m_MediaPlayer = null;
    }

    static synchronized public SoundManager getInstance() {
        if (_instance == null)
            _instance = new SoundManager();
        return _instance;
    }
       
    /* Set Effect Sound Play */
    public  void setEffectSoundPlay(int resID, boolean bSoundLoopFlag) {
        /* Event Sound Stop */
        eventSoundStop();

        try {
            Log.e("*++*+*+*+*+*+", "SoundPlay!!");
            
            if (0 == Config.SoundType) {
                m_MediaPlayer = MediaPlayer.create(SensorTestView.getInstance(), resID);
            }
            else {
                m_MediaPlayer = MediaPlayer.create(EmergencyView.getInstance(), resID);
            }
            m_MediaPlayer.setLooping(bSoundLoopFlag);
            m_MediaPlayer.start();
        }
        catch(Exception  e) {
            Log.e("*++*+*+*+*+*+", "error 1 : " + e.getMessage(), e);
        }
    }
    

    /* Event Sound Stop */
    public  void eventSoundStop() {
        if(m_MediaPlayer != null) {
            m_MediaPlayer.stop();
            m_MediaPlayer.release();
            m_MediaPlayer = null;
        }
        Log.e("*++*+*+*+*+*+", "SoundStop!!");
    }
    
    
    /* Send Event Message */
    public void sendEventMessage(int nMessageID) {
        try {
            eventSndManagerHandler.sendEmptyMessage(nMessageID);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    private Handler eventSndManagerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	
        	m_AudioManager = (AudioManager) EmergencyView.getInstance().getSystemService(Context.AUDIO_SERVICE);
            m_AudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, NameSpace.EMERGENCY_OCCUR_STREAM_MUSIC,
                    AudioManager.FLAG_ALLOW_RINGER_MODES);
            
            switch (msg.what) {
                /* Effect Sound Stop */
                case NameSpace.EmerEventNonType:
                    /* Event Sound Stop */
                    eventSoundStop();
                    break;
                /* 비상ㄹ Effect Sound */
                case NameSpace.EmerEventEmerType:
                    setEffectSoundPlay(R.raw.emer_occur, true);
                    break;
                /* 화재 Effect Sound */
                case NameSpace.EmerEventFireType:
                    setEffectSoundPlay(R.raw.fire_occur, true);
                    break;
                /* 가스 Effect Sound */
                case NameSpace.EmerEventGasType:
                    setEffectSoundPlay(R.raw.gas_occur, true);
                    break;
                /* 방범/피난사다리 Effect Sound */
                case NameSpace.EmerEventPrevType:
                case NameSpace.EmerEventLadderType:
                case NameSpace.EmerEventSafeType:
                    setEffectSoundPlay(R.raw.prev_occur, true);
                    break;
                default:
                	break;
            }
        }
    };
}

