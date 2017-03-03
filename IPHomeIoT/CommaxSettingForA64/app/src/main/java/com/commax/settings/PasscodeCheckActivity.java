package com.commax.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.settings.setting_provider.Passcode;
import com.commax.settings.setting_provider.Product;
import com.commax.settings.setting_provider.StringEx;
import com.commax.settings.setting_provider.Symbol;

/*

 * �ٸ� �ۿ����� ������ �� �����Ƿ� activity, manifest, package path �������� ������
 */

public class PasscodeCheckActivity extends Activity implements OnClickListener {

	private static final String ADMIN_CODE = "767767767";
	// private static final String ADMIN_CODE = "7";
	public static final int ADMIN = 9;
	public static final String KEY = "passcode";
	
	private static final int INPUT = 0;
	private static final int VALID = 1;
	private static final int INVALID = 2;

	private String inputedKeycode = "";//
	private String savedPasscode = null;
	private String inputedPasscode = "";
	private Integer tryLimitCount = 0;
	

	private TextView btn_1;
	private TextView btn_2;
	private TextView btn_3;
	private TextView btn_4;
	private TextView btn_5;
	private TextView btn_6;
	private TextView btn_7;
	private TextView btn_8;
	private TextView btn_9;
	private TextView btn_0;
	private TextView btn_del;
	
//	private Button btn_ok;

	private LinearLayout digitLayout;
	
	private ImageView dotImage1;
	private ImageView dotImage2;
	private ImageView dotImage3;
	private ImageView dotImage4;
	
	private ImageView underLine1;
	private ImageView underLine2;
	private ImageView underLine3;
	private ImageView underLine4;
	
	private TextView digitText1;
	private TextView digitText2;
	private TextView digitText3;
	private TextView digitText4;
	
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		context=this;
		
	    
	    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
	    layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
	    layoutParams.dimAmount = 0.5f;
	    getWindow().setAttributes(layoutParams);
	    

		setContentView(R.layout.activity_passcode);

		btn_1 = (TextView) findViewById(R.id.imageButton1);
		btn_2 = (TextView) findViewById(R.id.imageButton2);
		btn_3 = (TextView) findViewById(R.id.imageButton3);
		btn_4 = (TextView) findViewById(R.id.imageButton4);
		btn_5 = (TextView) findViewById(R.id.imageButton5);
		btn_6 = (TextView) findViewById(R.id.imageButton6);
		btn_7 = (TextView) findViewById(R.id.imageButton7);
		btn_8 = (TextView) findViewById(R.id.imageButton8);
		btn_9 = (TextView) findViewById(R.id.imageButton9);
		btn_0 = (TextView) findViewById(R.id.imageButton0);
		btn_del = (TextView) findViewById(R.id.imageButtonDelete);

//		btn_ok = (Button) findViewById(R.id.btn_ok);
		
		digitLayout = (LinearLayout) findViewById(R.id.digitLayout);
		dotImage1 = (ImageView) findViewById(R.id.dot1);
		dotImage2 = (ImageView) findViewById(R.id.dot2);
		dotImage3 = (ImageView) findViewById(R.id.dot3);
		dotImage4 = (ImageView) findViewById(R.id.dot4);
		
		underLine1 = (ImageView) findViewById(R.id.underLine1);
		underLine2 = (ImageView) findViewById(R.id.underLine2);
		underLine3 = (ImageView) findViewById(R.id.underLine3);
		underLine4 = (ImageView) findViewById(R.id.underLine4);
		
		digitText1 = (TextView) findViewById(R.id.digitText1);
		digitText2 = (TextView) findViewById(R.id.digitText2);
		digitText3 = (TextView) findViewById(R.id.digitText3);
		digitText4 = (TextView) findViewById(R.id.digitText4);

		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		btn_3.setOnClickListener(this);
		btn_4.setOnClickListener(this);
		btn_5.setOnClickListener(this);
		btn_6.setOnClickListener(this);
		btn_7.setOnClickListener(this);
		btn_8.setOnClickListener(this);
		btn_9.setOnClickListener(this);
		btn_0.setOnClickListener(this);
		btn_del.setOnClickListener(this);
		
//		btn_ok.setOnClickListener(this);
		
		updateDigitInit();

		final Button closeButton = (Button) findViewById(R.id.btn_close);
		closeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				finish();
				// returnResult(Symbol.INVALID);
			}
		});
		

		closeButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if ((event.getAction() == MotionEvent.ACTION_DOWN)) {
					Drawable alpha = closeButton.getBackground();
					alpha.setAlpha(0x99); // Opacity 60%
				}
				else {
					Drawable alpha = closeButton.getBackground();
					alpha.setAlpha(0xff); // Opacity 100%
				}

				return false;
			}
		});


	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		context=null;
	}

	/**
	 * FocusChanged 될때 NavigationBar 숨김(여기서 일괄처리,단 팝업 다이얼로그는 별도처리 요망)
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			try {
				// 액티비티 아래의 네비게이션 바가 안보이게
				final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
				getWindow().getDecorView().setSystemUiVisibility(flags);
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

	@Override
	public void onClick(View v) {

		
		if (btn_1 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("1");
		} else if (btn_2 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("2");
		} else if (btn_3 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("3");
		} else if (btn_4 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("4");
		} else if (btn_5 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("5");
		} else if (btn_6 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("6");
		} else if (btn_7 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("7");
		} else if (btn_8 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("8");
		} else if (btn_9 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_1;
			pressDigit("9");
		} else if (btn_0 == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_0;
			pressDigit("0");
		} else if (btn_del == v) {			
			inputedKeycode+= KeyEvent.KEYCODE_DEL;
			pressDel();
		} 
//		else if (btn_ok== v) {
//			// passcode
//			int ret = Symbol.ERROR;
//
//
//			if (savedPasscode == null) {
//				Passcode passcode = new Passcode(context);
//				String str = passcode.getValue();
//				if (StringEx.isFault(str)) {
//					ret = Symbol.ERROR;
//				}
//				savedPasscode = str;
//			}
//			if (savedPasscode != null) {
//				if (savedPasscode.equals(inputedPasscode)) {
//					ret = Symbol.VALID;
//				} else {
//					ret = Symbol.INVALID;
//				}
//			}
//
//			switch (ret) {
//			case Symbol.VALID: {
//				digitLayout.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						returnResult(Symbol.VALID);
//					}
//				}, 200);
//				
//
//				return;
//			}
//			case Symbol.INVALID: {
//
////				Animation shake = AnimationUtils.loadAnimation(context,
////						R.anim.phone_shake);
////				digitLayout.startAnimation(shake);
//				
//
//            	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                LayoutInflater inflater = getLayoutInflater();
//                View layout = inflater.inflate(R.layout.toast_popup,
//                        (ViewGroup) findViewById(R.id.custom_toast_layout));
//
//                Toast toast = new Toast(getApplicationContext());
//                toast.setDuration(Toast.LENGTH_LONG);
//                toast.setView(layout);
//                toast.show();
//
//				digitLayout.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						inputedPasscode = "";
//						tryLimitCount++;
//
//						updateDigitInit();
//
//						if (tryLimitCount > 2) {
//							returnResult(Symbol.INVALID);
//							return;
//						}
//					}
//				}, 200);
//
//
//				break;
//			}
//			default: {
//				ToastEx.show(context, R.string.failure);
//				returnResult(Symbol.INVALID);
//				return;
//			}
//			}
//		}
	}

	private void updateDigitInit() {
		dotImage1.setVisibility(View.GONE);
		dotImage2.setVisibility(View.GONE);
		dotImage3.setVisibility(View.GONE);
		dotImage4.setVisibility(View.GONE);

		digitText1.setVisibility(View.GONE);
		digitText2.setVisibility(View.GONE);
		digitText3.setVisibility(View.GONE);
		digitText4.setVisibility(View.GONE);

		underLine1.setBackgroundColor(Color.parseColor("#dfe8ee"));
		underLine2.setBackgroundColor(Color.parseColor("#dfe8ee"));
		underLine3.setBackgroundColor(Color.parseColor("#dfe8ee"));
		underLine4.setBackgroundColor(Color.parseColor("#dfe8ee"));
	}

	private void updateDigitIcon(int length) {
		switch (length) {
		case 0:
			updateDigitInit();
			break;

		case 1:
			digitText1.setVisibility(View.VISIBLE);
			digitText2.setVisibility(View.GONE);
			digitText3.setVisibility(View.GONE);
			digitText4.setVisibility(View.GONE);

			dotImage1.setVisibility(View.GONE);
			dotImage2.setVisibility(View.GONE);
			dotImage3.setVisibility(View.GONE);
			dotImage4.setVisibility(View.GONE);

			digitText1.setText(inputedPasscode);
			underLine1.setBackgroundColor(Color.parseColor("#6a71cc"));
			underLine2.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine3.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine4.setBackgroundColor(Color.parseColor("#dfe8ee"));
			break;

		case 2:
			digitText1.setVisibility(View.GONE);
			digitText2.setVisibility(View.VISIBLE);
			digitText3.setVisibility(View.GONE);
			digitText4.setVisibility(View.GONE);

			dotImage1.setVisibility(View.VISIBLE);
			dotImage2.setVisibility(View.GONE);
			dotImage3.setVisibility(View.GONE);
			dotImage4.setVisibility(View.GONE);
			dotImage1.setImageResource(R.drawable.img_password_dot_white);

			digitText2.setText(inputedPasscode.substring(1, 2));

			underLine1.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine2.setBackgroundColor(Color.parseColor("#6a71cc"));
			underLine3.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine4.setBackgroundColor(Color.parseColor("#dfe8ee"));
			break;

		case 3:
			digitText1.setVisibility(View.GONE);
			digitText2.setVisibility(View.GONE);
			digitText3.setVisibility(View.VISIBLE);
			digitText4.setVisibility(View.GONE);

			dotImage1.setVisibility(View.VISIBLE);
			dotImage2.setVisibility(View.VISIBLE);
			dotImage3.setVisibility(View.GONE);
			dotImage4.setVisibility(View.GONE);
			dotImage1.setImageResource(R.drawable.img_password_dot_white);
			dotImage2.setImageResource(R.drawable.img_password_dot_white);

			digitText3.setText(inputedPasscode.substring(2, 3));

			underLine1.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine2.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine3.setBackgroundColor(Color.parseColor("#6a71cc"));
			underLine4.setBackgroundColor(Color.parseColor("#dfe8ee"));
			break;

		case 4:
			digitText1.setVisibility(View.GONE);
			digitText2.setVisibility(View.GONE);
			digitText3.setVisibility(View.GONE);
			digitText4.setVisibility(View.VISIBLE);

			dotImage1.setVisibility(View.VISIBLE);
			dotImage2.setVisibility(View.VISIBLE);
			dotImage3.setVisibility(View.VISIBLE);
			dotImage4.setVisibility(View.GONE);
			dotImage1.setImageResource(R.drawable.img_password_dot_white);
			dotImage2.setImageResource(R.drawable.img_password_dot_white);
			dotImage3.setImageResource(R.drawable.img_password_dot_white);

			digitText4.setText(inputedPasscode.substring(3, 4));

			underLine1.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine2.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine3.setBackgroundColor(Color.parseColor("#dfe8ee"));
			underLine4.setBackgroundColor(Color.parseColor("#6a71cc"));
			break;

		default:
//			updateDigitInit();
			digitText4.setVisibility(View.GONE);

			dotImage4.setVisibility(View.VISIBLE);
			dotImage1.setImageResource(R.drawable.img_password_dot_white);
			dotImage2.setImageResource(R.drawable.img_password_dot_white);
			dotImage3.setImageResource(R.drawable.img_password_dot_white);
			dotImage4.setImageResource(R.drawable.img_password_dot_white);
			underLine4.setBackgroundColor(Color.parseColor("#dfe8ee"));
			break;
		}
	}

	private void returnResult(int value) {
		Bundle extra = new Bundle();
		Intent intent = new Intent();

		extra.putInt(KEY, value);
		intent.putExtras(extra);
		setResult(RESULT_OK, intent);

		finish();
	}

	private void pressDigit(String number) {


		if (inputedPasscode.length() < 5) {
			inputedPasscode = inputedPasscode + number;
			int length = inputedPasscode.length();

			updateDigitIcon(length);
			
			if (length < 4) {
				return;
			}

			// check validation

			// passcode
			int ret = Symbol.ERROR;


			if (savedPasscode == null) {
				Passcode passcode = new Passcode(context);
				String str = passcode.getValue();
				if (StringEx.isFault(str)) {
					ret = Symbol.ERROR;
				}
				savedPasscode = str;
			}
			if (savedPasscode != null) {
				if (savedPasscode.equals(inputedPasscode)) {
					ret = Symbol.VALID;
				}
				else {
					ret = Symbol.INVALID;
				}
			}

			switch (ret) {
				case Symbol.VALID: {
					digitLayout.postDelayed(new Runnable() {

						@Override
						public void run() {
							returnResult(Symbol.VALID);
						}
					}, 200);

					return;
				}
				case Symbol.INVALID: {
					Toast.makeText(this, R.string.password_is_not_same, Toast.LENGTH_SHORT).show(); //2017-01-13,yslee
//					Animation shake = AnimationUtils.loadAnimation(context, R.anim.phone_shake);
//					digitLayout.startAnimation(shake);
					digitLayout.postDelayed(new Runnable() {

						@Override
						public void run() {
							inputedPasscode = "";
							tryLimitCount++;
							updateDigitIcon(inputedPasscode.length());
							if (tryLimitCount > 2) {
								returnResult(Symbol.INVALID);
								return;
							}
						}
					}, 200);

					break;
				}
				default: {
					//Toast.makeText(this, R.string.password_is_not_same, Toast.LENGTH_SHORT).show();
					returnResult(Symbol.INVALID);
					return;
				}
			}
		}
		else {
			inputedPasscode = inputedPasscode.substring(0, 4);
		}
	}

	private void pressDel() {
		if (!(inputedPasscode.length()>0)) {
			updateDigitIcon(0);
			return;
		}
		
		// admin
		if (ADMIN_CODE.equals(inputedKeycode)) {
			if (Product.isWallpad()) {

				returnResult(ADMIN);
				return;
			}

		}
		
		if (inputedPasscode.length() < 5) {
			int length = inputedPasscode.length() - 1;
			inputedPasscode = inputedPasscode.substring(0, length);

			updateDigitIcon(length);
			
		}
		else {
			updateDigitIcon(5);
			
			int length = 3;
			inputedPasscode = inputedPasscode.substring(0, length);
			updateDigitIcon(length);
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {


		if (event.getAction() == KeyEvent.ACTION_UP) {
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case KeyEvent.KEYCODE_1:
				pressDigit("1");
				break;
			case KeyEvent.KEYCODE_2:
				pressDigit("2");
				break;
			case KeyEvent.KEYCODE_3:
				pressDigit("3");
				break;
			case KeyEvent.KEYCODE_4:
				pressDigit("4");
				break;
			case KeyEvent.KEYCODE_5:
				pressDigit("5");
				break;
			case KeyEvent.KEYCODE_6:
				pressDigit("6");
				break;
			case KeyEvent.KEYCODE_7:
				pressDigit("7");
				break;
			case KeyEvent.KEYCODE_8:
				pressDigit("8");
				break;
			case KeyEvent.KEYCODE_9:
				pressDigit("9");
				break;
			case KeyEvent.KEYCODE_0:
				pressDigit("0");
				break;
			case KeyEvent.KEYCODE_DEL:
				pressDel();
				break;
			case KeyEvent.KEYCODE_BACK:
				onBackPressed();
				break;
			default:
				break;
			}

		}


		return false;

	}

}
