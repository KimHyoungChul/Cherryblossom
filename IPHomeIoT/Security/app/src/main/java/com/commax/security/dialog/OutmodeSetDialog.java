package com.commax.security.dialog;

import com.commax.security.NameSpace;
import com.commax.security.OutModeView;
import com.commax.security.PreferenceTool;
import com.commax.security.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OutmodeSetDialog extends Activity {
	
//	private static final int SYSTEM_UI_FLAG_IMMERSIVE_STICKY = 0x00001000;
	
	private static OutmodeSetDialog m_instance;
	
	@SuppressWarnings("unused")
	private static OutmodeSetDialog getInstance() {
        return m_instance;
    }  
	
	private static PreferenceTool mTool = null;
	
	private Button title_btn_close;
	
	/* Å¸ÀÌ¸Ó °ü·Ã */
	private TextView timer_min;
	private TextView timer_colon;
	private TextView timer_sec1;
	private TextView timer_sec2;
	private TextView text_setstate;
	
	private CountDown m_countDown = null;
	private SleepCountDown m_sleepCountDown = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_instance = this;
				
		mTool = new PreferenceTool(this);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.outmodeset_dialog);

        // 2017.01.06_yclee : ?????? ?????
        hideNavigationBar();
		
		title_btn_close = (Button) findViewById(R.id.btn_close);

        // IP HomeIoT 월패드에서는 버튼 안보이도록 함
        title_btn_close.setVisibility(View.GONE);
		
		/* Backkey ÅÍÄ¡½Ã Opacity °ª º¯°æ */
		title_btn_close.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = title_btn_close.getBackground();
					alpha.setAlpha(0x99); // Opacity 60%
				}
				else if ((event.getAction() == MotionEvent.ACTION_UP)) {
					Drawable alpha = title_btn_close.getBackground();
					alpha.setAlpha(0xff); // Opacity 100%
				}

				return false;
			}
		});
		
		title_btn_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		timer_min = (TextView) findViewById(R.id.timer_min);
		timer_colon = (TextView) findViewById(R.id.timer_colon);
		timer_sec1 = (TextView) findViewById(R.id.timer_sec1);
		timer_sec2 = (TextView) findViewById(R.id.timer_sec2);
		text_setstate = (TextView) findViewById(R.id.text_setstate);
		
		((LinearLayout) findViewById(R.id.ll_timer)).setVisibility(View.VISIBLE);                
        text_setstate.setText(getResources().getString(R.string.msg_setcounting));
		
		/* Å¸ÀÌ¸Ó ½ÃÀÛ */
        if (m_countDown == null) {
            m_countDown = new CountDown(NameSpace.nOutingDelayTime * 1000, 1000);
            m_countDown.start();
        }
	}
	
	public class CountDown extends CountDownTimer {
        
        public CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

		public void onFinish() {
			if (m_instance != null) {
				Intent intentBroad = new Intent(NameSpace.ACTION_GOOUT_START);
				intentBroad.putExtra("light_action", NameSpace.nOutingLightAllOff);
				intentBroad.putExtra("gasvalve_action", NameSpace.nOutingGasClose);
				OutModeView.getInstance().sendBroadcast(intentBroad);
			}

			/* Å¸ÀÌ¸Ó ÁßÁö */
			if (m_countDown != null) {
				m_countDown.cancel();
				m_countDown = null;
			}

			((LinearLayout) findViewById(R.id.ll_timer)).setVisibility(View.INVISIBLE);
			text_setstate.setText(getResources().getString(R.string.msg_setprevent));

			/* Å¸ÀÌ¸Ó ½ÃÀÛ */
			if (m_sleepCountDown == null) {
				m_sleepCountDown = new SleepCountDown(3000, 1000);
				m_sleepCountDown.start();
			}
        }

        public void onTick(long millisUntilFinished) {
            String strTime = formatTime(millisUntilFinished);            
            String [] strTimer = strTime.split(":");
            
            onDrawTimer(NameSpace.nOutingTimeMinute2, strTimer[1].charAt(1));
            onDrawTimer(NameSpace.nOutingTimeSecond1, strTimer[2].charAt(0));
            onDrawTimer(NameSpace.nOutingTimeSecond2, strTimer[2].charAt(1));
            
            // colon ±ôºýÀÓ
            if (Integer.parseInt(strTimer[2]) % 2 == 0) {
            	timer_colon.setVisibility(View.VISIBLE);
            }
            else {
            	timer_colon.setVisibility(View.INVISIBLE);
            }
        }
    }
	
	public class SleepCountDown extends CountDownTimer {
        
        public SleepCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
                
                /* Å¸ÀÌ¸Ó ÁßÁö */
                if (m_sleepCountDown != null) {
                	m_sleepCountDown.cancel();
                	m_sleepCountDown = null;
                }
                
                finish();                
        }

        public void onTick(long millisUntilFinished) {
        }
    }
	
	private void onDrawTimer(int nTimerWhich, char chNumber) {    
        switch (nTimerWhich) {
            case NameSpace.nOutingTimeMinute2:
                switch (chNumber) {
                    case '0':
                    	timer_min.setText("00");
                        break;
                    case '1':
                    	timer_min.setText("01");
                        break;
                    case '2':
                    	timer_min.setText("02");
                        break;   
                    default:
                    	break;
                }
                break;
            case NameSpace.nOutingTimeSecond1:
                switch (chNumber) {
                    case '0':
                    	timer_sec1.setText("0");
                        break;
                    case '1':
                    	timer_sec1.setText("1");
                        break;
                    case '2':
                    	timer_sec1.setText("2");
                        break;
                    case '3':
                    	timer_sec1.setText("3");
                        break;
                    case '4':
                    	timer_sec1.setText("4");
                        break;
                    case '5':
                    	timer_sec1.setText("5");
                        break; 
                    default:
                    	break;
                }
                break;
            case NameSpace.nOutingTimeSecond2:
                switch (chNumber) {
                    case '0':
                    	timer_sec2.setText("0");
                        break;
                    case '1':
                    	timer_sec2.setText("1");
                        break;
                    case '2':
                    	timer_sec2.setText("2");
                        break;
                    case '3':
                    	timer_sec2.setText("3");
                        break;
                    case '4':
                    	timer_sec2.setText("4");
                        break;
                    case '5':
                    	timer_sec2.setText("5");
                        break;
                    case '6':
                    	timer_sec2.setText("6");
                        break;
                    case '7':
                    	timer_sec2.setText("7");
                        break;
                    case '8':
                    	timer_sec2.setText("8");
                        break;
                    case '9':
                    	timer_sec2.setText("9");
                        break;
                    default:
                    	break;
                }
                break;
            default:
            	break;
        }
    }
    
    
    public String formatTime(long millis) {
        String output = "00:00";
        long seconds = millis / 1000;
        long m_minutes = seconds / 60;
        long hours = m_minutes / 60;

        seconds = seconds % 60;
        m_minutes = m_minutes % 60;
        hours = hours % 60;

        String secondsD = String.valueOf(seconds);
        String m_m_minutesD = String.valueOf(m_minutes);
        String hoursD = String.valueOf(hours);

        if (seconds < 10)
            secondsD = "0" + seconds;
        if (m_minutes < 10)
            m_m_minutesD = "0" + m_minutes;
        if (hours < 10)
            hoursD = "0" + hours;

        output = hoursD + ":" + m_m_minutesD + ":" + secondsD;
        return output;
    }
    
    /*@Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
	}*/

    private void hideNavigationBar(){

        try {
            // ???? ??? ????? ?? ????
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
// This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);
// Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
