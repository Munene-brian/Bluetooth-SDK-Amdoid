package com.evolute.lilygendemoapp;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evolute.bluetooth.BluetoothComm;
import com.evolute.lilydemo.R;
import com.lillygen.api.Setup;

public class Act_SelectPeripherals extends Activity implements OnClickListener {

	private Context context = this;
	private Button btnFps, btnMag, btnSmartcard, btnSerial;
	private Button btnInformation, btnExit;
	private String sTo, sSubject, sMessage;
	private EditText edtTo, edtSubject, edtMessage;
	private Dialog dlgSupport;
	static ProgressDialog prgDialog;
	public static final String TAG = "Act_SelectPeripherals";
	TextView tvMag,tvFps,tvSc,tvSelectPheripheral;
	public static Setup setupGen;
	boolean blGen;
	//Boolean blEzy;
	protected void onCreate(Bundle savedInstanceState) { // TODO
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		Log.e(TAG, " Select Pheripheral");
		
		
			try {

				setupGen = new com.lillygen.api.Setup();
				boolean activate = setupGen.blActivateLibrary(context,R.raw.licence_lillyfull);
				if (activate == true) {
					Log.d(TAG, "Gen Library Activated......");
				} else if (activate == false) {
					Log.d(TAG, "Gen Library Not Activated...");
				}
			} catch (Exception e) {
			}
		

		// initialize the buttons
		btnFps = (Button) findViewById(R.id.fps_but);
		btnFps.setOnClickListener(this);

		btnMag = (Button) findViewById(R.id.mag_but);
		btnMag.setOnClickListener(this);

		btnSmartcard = (Button) findViewById(R.id.smart_but);
		btnSmartcard.setOnClickListener(this);

		btnInformation = (Button) findViewById(R.id.information_but);
		btnInformation.setOnClickListener(this);

		btnExit = (Button) findViewById(R.id.exit_but);
		btnExit.setOnClickListener(this);
		
		tvFps = (TextView) findViewById(R.id.tvFps);
		tvMag = (TextView) findViewById(R.id.tvMag);
		tvSc = (TextView) findViewById(R.id.tvSc);
		tvSelectPheripheral = (TextView) findViewById(R.id.tv_selectPheripheral);
	}
	
	@Override
	 protected void onResume() {
	  super.onResume();
	  Animation animset_right = AnimationUtils.loadAnimation(this, R.anim.set_right);
	  Animation animset_left= AnimationUtils.loadAnimation(this, R.anim.set);
	  Animation animset_bottom= AnimationUtils.loadAnimation(this, R.anim.set_bottom);
	  Animation animalpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
	  Animation animrotate= AnimationUtils.loadAnimation(this, R.anim.rotate);
	  btnMag.startAnimation(animset_right);
	  tvMag.startAnimation(animrotate);
	  btnFps.startAnimation(animset_left);
	  tvFps.startAnimation(animrotate);
	  btnSmartcard.startAnimation(animset_bottom);
	  tvSc.startAnimation(animrotate);
	  tvSelectPheripheral.startAnimation(animalpha);
	  btnExit.startAnimation(animrotate);
	  btnInformation.startAnimation(animrotate);
	 }

	// Button Events
	@Override
	public void onClick(View v) { // TODO
		switch (v.getId()) {
		case R.id.fps_but:
				Log.e(TAG, "Fps capture GEN");
				Intent fps = new Intent(getApplicationContext(), Act_FPS.class);
				startActivity(fps);
				break;
		
		case R.id.mag_but:
				Intent mag = new Intent(getApplicationContext(), Act_MagCard.class);
				startActivity(mag);
				break;
				
		case R.id.smart_but:
				Intent smart = new Intent(getApplicationContext(), Act_SmartCard.class);
				startActivity(smart);
				break;
		
		case R.id.information_but:
				dlgInformationBox_gen();
				break;
				
		case R.id.exit_but:
				dlgExit();
				break;
				
		default:
			break;
		}
	}
	
	// display information dialog box
		public void dlgInformationBox_gen() { //TODO
			Dialog alert = new Dialog(context);
			alert.getWindow();
			alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// custom layout for information display
			alert.setContentView(R.layout.dlg_informationbox);
			TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
			String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a>";
			site_tv.setLinksClickable(true);
			site_tv.setMovementMethod(LinkMovementMethod.getInstance());
			site_tv.setText(Html.fromHtml(str_links));
			//site_tv.setText("www.evolute-sys.com");
			TextView supportteam_tv = (TextView) alert.findViewById(R.id.supportteam_tv);
			String supportteam_links = "<a href='http://supportteam'>sales@evolute-sys.com</a>";
			supportteam_tv.setText(Html.fromHtml(supportteam_links));
			//supportteam_tv.setText("supportteam");
			supportteam_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dlgSupportEmail("sales@evolute-sys.com");
				}
			});
			TextView feedbck_tv = (TextView) alert.findViewById(R.id.feedbck_tv);
			String feedback_links = "<a href='http://feedback'>support@evolute-sys.com</a>";
			feedbck_tv.setText(Html.fromHtml(feedback_links));
			//feedbck_tv.setText("feedback");
			feedbck_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dlgSupportEmail("support@evolute-sys.com");
				}
			});
			alert.show();
		}
	

	// display information dialog box
	public void dlgInformationBox_ezy() { // TODO
		Dialog alert = new Dialog(context);
		alert.getWindow();
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(R.layout.dlg_informationbox);
		TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
		String str_links = "<h2><a href='http://www.evolute-sys.com'>www.evolute-sys.com</a><br /></h2>";
		site_tv.setLinksClickable(true);
		site_tv.setMovementMethod(LinkMovementMethod.getInstance());
		site_tv.setText(Html.fromHtml(str_links));
		TextView supportteam_tv = (TextView) alert
				.findViewById(R.id.supportteam_tv);
		String supportteam_links = "<h2><a href='http://supportteam'>supportteam</a><br /></h2>";
		supportteam_tv.setText(Html.fromHtml(supportteam_links));
		supportteam_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgSupportEmail("support@evolute-sys.com");
			}
		});
		TextView feedbck_tv = (TextView) alert.findViewById(R.id.feedbck_tv);
		String feedback_links = "<h2><a href='http://feedback'>feedback</a><br /></h2>";
		feedbck_tv.setText(Html.fromHtml(feedback_links));
		feedbck_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlgSupportEmail("sales@evolute-sys.com");
			}
		});
		alert.show();
	}

	// displays a dialog box for composing a mail
	public void dlgSupportEmail(String stEmailId) { // TODO
		Button buttonSend;
		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		dlgSupport = new Dialog(context);
		dlgSupport.getWindow();
		dlgSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlgSupport.setContentView(R.layout.bdteamsupport);
		edtTo = (EditText) dlgSupport.findViewById(R.id.editTextTo);
		edtTo.setText(stEmailId);
		edtTo.setWidth(width);
		edtSubject = (EditText) dlgSupport.findViewById(R.id.editTextSubject);
		edtMessage = (EditText) dlgSupport.findViewById(R.id.editTextMessage);
		buttonSend = (Button) dlgSupport.findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sTo = edtTo.getText().toString();
				sSubject = edtSubject.getText().toString();
				sMessage = edtMessage.getText().toString();
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { sTo });
				email.putExtra(Intent.EXTRA_SUBJECT, sSubject);
				email.putExtra(Intent.EXTRA_TEXT, sMessage);
				email.setType("message/rfc822");
				startActivity(Intent.createChooser(email,
						"Choose an Email client :"));
				dlgSupport.cancel();
			}
		});
		dlgSupport.show();
	}

	// if back key is pressed prompts for a exit confirmation dialog box
	public boolean onKeyDown(int keyCode, KeyEvent event) { // TODO
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dlgExit();
		}
		return super.onKeyDown(keyCode, event);
	}

	// Exit confirmation dialog box
	public void dlgExit() {// TODO
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		//AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
		alertDialogBuilder.setTitle("Btn Bluetooth Application");
		alertDialogBuilder.setIcon(R.drawable.bluetooth);
		alertDialogBuilder.setMessage("Are you sure you want to Application");
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Exit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							BluetoothComm.mosOut = null;
							BluetoothComm.misIn = null;
						} catch (NullPointerException e) {
						}
						System.gc();
						Act_SelectPeripherals.this.finish();
					}
				});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		//alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_purple);
		alertDialog.show();
		
	}

	// displays a progress dialog with message
	public static void progressDialog(Context context, String msg) {
		prgDialog = new ProgressDialog(context);
		prgDialog.setMessage(msg);
		prgDialog.setIndeterminate(true);
		prgDialog.setCancelable(false);
		prgDialog.show();
	}

	// display information dialog box
	public void showdialog(String str) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setTitle("Lily Demo Application");
		alertDialogBuilder.setMessage(str).setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
